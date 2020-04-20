package imu.GeneralStore.Other;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ConfigMaker {

	private Plugin _plugin;
	private File _file;
	private String _fileName;
	private FileConfiguration _config;
	
	public ConfigMaker(Plugin plugin, String fileName)
	{
		_plugin=plugin;
		_file = new File(_plugin.getDataFolder() + "/" + fileName);
		_fileName = fileName;
		_config = YamlConfiguration.loadConfiguration(_file);
	}
	
	public String getFileName()
	{
		return _fileName;
	}
	public void saveConfig()
	{
		try
		{
			_config.save(_file);
		}catch(IOException e)
		{
			System.out.println("File named "+ _fileName +"didn't found");
			e.printStackTrace();
		}
	}
	
	public void removeConfig()
	{
		File f= new File(_plugin.getDataFolder() + "/" + _fileName);
		f.delete();
	}
	
	public FileConfiguration getConfig()
	{
		return _config;
	}
	
	public boolean isExists()
	{
		File b = new File(_plugin.getDataFolder() + "/" + _fileName);
		return b.exists();
	}
	
	void saveInvTOconfig(Player player)
	{
		ConfigMaker cm = new ConfigMaker(_plugin, _fileName);
		FileConfiguration config = cm.getConfig();
		
		config.set(player.getUniqueId().toString(), player.getInventory().getContents());
		cm.saveConfig();
	}
	
	ItemStack[] getSavedInvFromConfig(Player player)
	{
		ConfigMaker cm = new ConfigMaker(_plugin, _fileName);
		FileConfiguration config = cm.getConfig();
		if(config.contains(player.getUniqueId().toString()))
		{
			System.out.println("Player exist in invs");
			@SuppressWarnings("unchecked")
			List<ItemStack> stacks = (List<ItemStack>) config.getList(player.getUniqueId().toString());
			ItemStack[] content = new ItemStack[stacks.size()];
			stacks.toArray(content);
			return content;
			
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T addDefault(String name, T value)
	{
		name = name+"("+value.getClass().getSimpleName()+")";
		if(!isExists() || !_config.contains(name))
		{
			_config.set(name,value);
		}
		saveConfig();

		return (T)_config.get(name);
		
	}
}
