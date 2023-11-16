package imu.imusEnchants.Events;



import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import imu.iAPI.Utilities.ItemUtils;
import imu.imusEnchants.Enchants.EnchantedItem;
import imu.imusEnchants.Managers.ManagerEnchants;
import imu.imusEnchants.main.ImusEnchants;

public class Events implements Listener
{
	Plugin _plugin;
	
	public Events()
	{
		_plugin = ImusEnchants.Instance;
		
	}
	
	@EventHandler
    public void OnCraftItem(CraftItemEvent event) 
	{
		System.out.println("crafting");
        ItemStack result = event.getCurrentItem();
        if (result == null) return; 
        
        System.out.println("crafting: "+ result);
        if(!ItemUtils.IsTool(result)) return;
        
        System.out.println("enchant item: ");
        EnchantedItem eItem = new EnchantedItem(result);
        eItem.SetTooltip();
    }
	
	@EventHandler
    public void OnPlayerInteract(PlayerInteractEvent event)
    {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            Block clickedBlock = event.getClickedBlock();

            if (clickedBlock != null && clickedBlock.getType() == Material.ENCHANTING_TABLE)
            {
                Player player = event.getPlayer();
                event.setCancelled(true);
                ManagerEnchants.Instance.OpenEnchantingInventory(player);
            }
        }
    }
	

}