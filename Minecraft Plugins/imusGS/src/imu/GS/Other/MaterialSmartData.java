package imu.GS.Other;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import imu.GS.Managers.MaterialManager;
import imu.iAPI.Other.Metods;
import imu.iAPI.Other.Tuple;

public class MaterialSmartData 
{
	private HashMap<Material, Double> _mat_multies = new HashMap<>();
	private Material _mat;
	private double _multiplier;
	public MaterialSmartData(Material mat, double multiplier)
	{
		_mat = mat;
		_multiplier = multiplier;
	}
	
	public boolean Calculate()
	{
		_mat_multies.clear();
		
		for(Tuple<ItemStack,Double> info : Metods._ins.GetRecipe(new ItemStack(_mat)))
		{
			//System.out.println(info);
			if(!_mat_multies.containsKey(info.GetKey().getType())) _mat_multies.put(info.GetKey().getType(), 0.0);
			
			double multi = _mat_multies.get(info.GetKey().getType())+info.GetValue();
			_mat_multies.put(info.GetKey().getType(), multi);

		}
		
		if(_mat_multies.isEmpty()) return false;
		
		return true;
	}
	
	public double GetPrice()
	{
		double price = 0.0;
		
		for(Map.Entry<Material, Double> ing : _mat_multies.entrySet())
		{
			price += MaterialManager._ins.GetPriceMaterial(ing.getKey()).GetPrice() * ing.getValue();
		}
		return price * _multiplier;
	}
	
	public HashMap<Material, Double> MatAndRatio()
	{
		return _mat_multies;
	}
	
	public double GetMultiplier()
	{
		return _multiplier;
	}
}
