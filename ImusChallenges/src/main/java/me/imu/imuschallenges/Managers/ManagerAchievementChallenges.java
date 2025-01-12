package me.imu.imuschallenges.Managers;

import imu.iAPI.Other.Metods;
import me.imu.imuschallenges.CONSTANTS;
import me.imu.imuschallenges.ImusChallenges;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ManagerAchievementChallenges implements Listener
{
    private ImusChallenges _main = ImusChallenges.getInstance();
    private Map<UUID, Set<String>> _playerCompletedAdvancements;
    private Set<String> _globalCompletedAdvancements;

    private final ConcurrentLinkedQueue<PlayerAdvancementPair> buffer = new ConcurrentLinkedQueue<>();
    private final BukkitScheduler scheduler = Bukkit.getScheduler();

    private static class PlayerAdvancementPair
    {
        final Player player;
        final String advancementKey;

        PlayerAdvancementPair(Player player, String advancementKey)
        {
            this.player = player;
            this.advancementKey = advancementKey;
        }
    }

    public ManagerAchievementChallenges()
    {
        _playerCompletedAdvancements = new HashMap<>();
        _globalCompletedAdvancements = new HashSet<>();
        createTables();
        startBufferProcessor();
    }


    private void createTables()
    {
        try (Connection con = _main.GetSQL().GetConnection())
        {
            PreparedStatement ps = con.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS player_achievements (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "player_name CHAR(36) NOT NULL," +
                            "player_uuid CHAR(36) NOT NULL," +
                            "advancement_key VARCHAR(255) NOT NULL," +
                            "completion_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")"
            );
            ps.executeUpdate();
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            Bukkit.getLogger().info("[ImusChallenges] Tables created!");
            loadGlobalCompletedAdvancements();
        }
    }

    private void loadGlobalCompletedAdvancements()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try (Connection con = _main.GetSQL().GetConnection();
                     PreparedStatement ps = con.prepareStatement("SELECT DISTINCT advancement_key FROM player_achievements"))
                {
                    try (ResultSet rs = ps.executeQuery())
                    {
                        while (rs.next())
                        {
                            _globalCompletedAdvancements.add(rs.getString("advancement_key"));
                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(_main);
    }

    // In ManagerAchievementChallenges class

    private void loadPlayerAchievements(UUID playerUuid)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                Set<String> playerAchievements = new HashSet<>();
                String query = "SELECT advancement_key FROM player_achievements WHERE player_uuid = ?";
                try (Connection con = _main.GetSQL().GetConnection();
                     PreparedStatement ps = con.prepareStatement(query))
                {

                    ps.setString(1, playerUuid.toString());
                    try (ResultSet rs = ps.executeQuery())
                    {
                        while (rs.next())
                        {
                            playerAchievements.add(rs.getString("advancement_key"));
                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                } finally
                {
                    Bukkit.getScheduler().runTask(_main, () -> _playerCompletedAdvancements.put(playerUuid, playerAchievements));
                }
            }
        }.runTaskAsynchronously(_main);
    }

    private void startBufferProcessor()
    {
        scheduler.runTaskTimerAsynchronously(_main, () ->
        {
            while (!buffer.isEmpty())
            {
                PlayerAdvancementPair pair = buffer.poll();
                if (pair != null && !_globalCompletedAdvancements.contains(pair.advancementKey))
                {
                    _globalCompletedAdvancements.add(pair.advancementKey);
                    savePlayerAdvancement(pair.player, pair.advancementKey);
                    informPlayer(pair.player, pair.advancementKey);  // Notify player
                }
            }
        }, 20L, 100L); // Runs every 5 seconds, adjustable
    }


    /*@EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event)
    {
        Player player = event.getPlayer();
        Advancement advancement = event.getAdvancement();
        String advancementKey = advancement.toString();
        System.out.println("Player " + player.getName() + " completed advancement " + advancementKey);
        if (!_globalCompletedAdvancements.contains(advancementKey))
        {
            markAdvancementAsCompleted(player, advancementKey);
            savePlayerAdvancement(player, advancementKey);
            _globalCompletedAdvancements.add(advancementKey);  // Mark globally as completed
            // Notify player or perform other actions
        }
    }*/
    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event)
    {
        Player player = event.getPlayer();
        Advancement advancement = event.getAdvancement();

        if(advancement.getDisplay() == null)
        {
            return;
        }

        String advancementKey = advancement.getDisplay().getTitle();

        if (!_globalCompletedAdvancements.contains(advancementKey))
        {
            buffer.offer(new PlayerAdvancementPair(player, advancementKey));
        }
    }


    private void markAdvancementAsCompleted(Player player, String advancementKey)
    {
        _playerCompletedAdvancements.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(advancementKey);
    }

    private boolean hasPlayerCompletedAdvancement(Player player, String advancementKey)
    {
        Set<String> completedAdvancements = _playerCompletedAdvancements.get(player.getUniqueId());
        return completedAdvancements != null && completedAdvancements.contains(advancementKey);
    }

    private void savePlayerAdvancement(Player player, String advancementKey)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try (Connection con = _main.GetSQL().GetConnection();
                     PreparedStatement ps = con.prepareStatement(
                             "INSERT INTO player_achievements (player_name, player_uuid, advancement_key) VALUES (?, ?, ?)"
                     ))
                {
                    ps.setString(1, player.getName());
                    ps.setString(2, player.getUniqueId().toString());
                    ps.setString(3, advancementKey);
                    ps.executeUpdate();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(_main);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        UUID playerUuid = event.getPlayer().getUniqueId();
        loadPlayerAchievements(playerUuid);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        UUID playerUuid = event.getPlayer().getUniqueId();
        _playerCompletedAdvancements.remove(playerUuid);
    }

    private void informPlayer(Player player, String achievement)
    {
        if (player.hasPermission(CONSTANTS.PERM_SERVER_WIDE_COLLECTION_CHALLENGE_BROADCAST))
        {
            Bukkit.getServer().broadcast(Metods.msgC("&9" + player.getName() + " &9is first to complete the &6" + achievement), CONSTANTS.PERM_SERVER_WIDE_COLLECTION_CHALLENGE_BROADCAST);
            return;
        }

       /* String message;
        message = Metods.msgC("&9New &6Achievement &9found! The &3" + achievement);

        player.sendMessage(message);*/
    }
}

