package imu.UIC.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import imu.UIC.main.Main;

public class Event1 implements Listener
{
	Main _main = null;

	public Event1(Main main)
	{
		_main = main;		
	}
	
	@EventHandler
	public void onPlayerLeave(org.bukkit.event.player.PlayerQuitEvent e)
	{
		
	}
	
	
	
}
