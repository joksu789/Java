package imu.imusEnchants.Enchants;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import imu.iAPI.Utilities.ItemUtils;
import imu.imusEnchants.main.CONSTANTS;

public class NodeBooster extends Node
{
	private int _power = 1;
	
	private final static String PD_BOOSTER = "PD_BOOSTER";
	
	public NodeBooster()
	{
		SetLock(false);
	}
	
	public void SetPower(int power)
	{
		_power = power;
	}
	
	public int GetPower()
	{
		return _power;
	}
	
	@Override
	public boolean IsValidGUIitem(EnchantedItem enchantedItem, ItemStack stack)
	{
		System.out.println("checking stack: "+stack);
		if(stack.getType() != CONSTANTS.BOOSTER_MATERIAL) return false;
		
		if(!IsBooster(stack)) return false;
		
		return true;
	}
    
	@Override
	public ItemStack GetGUIitemLoad(EnchantedItem enchantedItem)
	{
		return GetBoosterStack(_power);
	}
	
    @Override
    public ItemStack GetItemStack()
    {
    	return GetBoosterStack(_power);
    }
    
    public static ItemStack GetBoosterStack(int power)
    {
    	ItemStack itemStack = new ItemStack(CONSTANTS.BOOSTER_MATERIAL);        
    	ItemUtils.SetDisplayName(itemStack, "&eBOOSTER");
    	ItemUtils.AddLore(itemStack, "&5Power: &6"+power, false);
    	ItemUtils.HideFlag(itemStack, ItemFlag.HIDE_POTION_EFFECTS);
    	ItemUtils.SetPersistenData(itemStack, PD_BOOSTER, PersistentDataType.INTEGER, power);
    	
    	
    	return itemStack;
    }
    
    public static int GetPower(ItemStack stack)
    {
    	Integer p = ItemUtils.GetPersistenData(stack, PD_BOOSTER, PersistentDataType.INTEGER);
    	return p != null ? p : 0;
    }
    
    public static boolean IsBooster(ItemStack stack)
    {
    	return ItemUtils.GetPersistenData(stack, PD_BOOSTER, PersistentDataType.INTEGER) != null;
    }
    
    @Override
    public String Serialize() 
 	{
		return this.getClass().getSimpleName() + 
				":" + GetX() + 
				":" + GetY() + 
				":" + _power;
    }

    @Override
    public void Deserialize(String data) 
    {
        String[] parts = data.split(":");
        _x = Integer.parseInt(parts[1]);
        _y = Integer.parseInt(parts[2]);
        _power = Integer.parseInt(parts[3]);
    }
}
