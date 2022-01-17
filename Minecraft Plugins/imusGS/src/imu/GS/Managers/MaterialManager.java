package imu.GS.Managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import imu.GS.ENUMs.SQL_TABLES;
import imu.GS.Main.Main;
import imu.GS.Other.MaterialOverflow;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopItemBase;
import imu.GS.ShopUtl.ItemPrice.PriceMaterial;

public class MaterialManager extends Manager
{
	ShopManagerSQL _shopManagerSQL;
	ShopManager _shopManager;
	private HashMap<Material, PriceMaterial> _material_prices = new HashMap<>();
	
	public MaterialManager(Main main)
	{
		super(main);
		
		_shopManager = main.get_shopManager();
		_shopManagerSQL = main.GetShopManagerSQL();
	}
	
	@Override
	public void INIT() 
	{

		
	}
	public void CreateTables()
	{
		
		try(Connection con =_main.GetSQL().GetConnection()) 
		{
			PreparedStatement ps;
			ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+SQL_TABLES.price_materials.toString()+" ("
					+ "material VARCHAR(50), "
					+ "price FLOAT(20), "
					+ "PRIMARY KEY(material)"
					+ ");");
			ps.executeUpdate();
			
			ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+SQL_TABLES.material_overflow.toString()+" ("
					+ "material VARCHAR(50), "
					+ "softcap INT(50), "
					+ "batch_size INT(20), "
					+ "min_price FLOAT(20), "
					+ "drop_percent FLOAT(20), "
					+ "PRIMARY KEY(material)"
					+ ");");
			ps.executeUpdate();
			
			//_main.getLogger().info("====> material_prices");
			
			ps.close();
			con.close();
			
		} 
		catch (Exception e) 
		{
			PrintERROR("CreateTables", "Couldnt Create tables");
			e.printStackTrace();
		}
	}
	
	public PriceMaterial GetPriceMaterialAndCheck(ItemStack stack)
	{
		//System.err.println("Checking mat price: "+stack.getType());
		PriceMaterial priceMaterial = new PriceMaterial();
		priceMaterial.SetPrice(0);
		if(stack == null || stack.getType() == Material.AIR) return priceMaterial;
		//if((ImusAPI._metods.isArmor(stack) || ImusAPI._metods.isTool(stack)) &&  ImusAPI._metods.getDurabilityProsent(stack) != 1.0) return priceMaterial; // if material has eny durability lost it will be 0
		
		//double price = _material_prices.get(stack.getType()).GetPrice();
		priceMaterial = (PriceMaterial)_material_prices.get(stack.getType()).clone();
		
		double addedEnchantPrice = _main.GetShopEnchantManager().CalculateEnchantPrice(stack);

		double newPrice = priceMaterial.GetPrice() + addedEnchantPrice;

		if(newPrice < 0.0) newPrice = 0.0;
		
		priceMaterial.SetPrice(newPrice * _shopManager.GetDurabilityReduction(stack));
	
		
		return priceMaterial;
	}
	
	void SetMaterialOverflow(MaterialOverflow mOverflow)
	{
		_material_prices.get(mOverflow.get_mat()).SetOverflow(mOverflow);
	}
	
	void RemoveMaterialOverflow(Material mat)
	{
		_material_prices.get(mat).SetOverflow(null);
	}
	
	void PutMaterialPrice(Material mat, double price)
	{		
		PriceMaterial pm = new PriceMaterial();
		pm.SetPrice(price);
		_material_prices.put(mat, pm);
	}
	
	public PriceMaterial GetPriceMaterial(Material mat)
	{
		return _material_prices.get(mat);
	}
	
	public void SaveMaterialPrice(List<Material> mats, double prices)
	{
		new BukkitRunnable() 
    	{
			@Override
			public void run() 
			{				
				_shopManagerSQL.SaveMaterialPrice(mats, prices);
			}
		}.runTaskAsynchronously(_main);	
		
		new BukkitRunnable() {
			
			double price = prices;
			@Override
			public void run() 
			{
				if(price < 0) 
				{
					price = 0;
				}else
				{
					price = Math.round(price * 100.00) / 100.00;
				}
				for(Material mat : mats)
				{
					PutMaterialPrice(mat, price);

					for(ShopBase shop : _shopManager.GetShops())
					{
						boolean removeCustomers = false;
						for(ShopItemBase[] pages : shop.get_items())
						{
							for(ShopItemBase sib : pages)
							{
								if(sib == null) continue;
								
								if(sib.GetItemPrice() instanceof PriceMaterial && sib.GetRealItem().getType() == mat)
								{
									sib.SetItemPrice(GetPriceMaterial(mat));
									removeCustomers = true;
								}
							}
						}
						if(removeCustomers) shop.RemoveCustomerALL();
					}	
				}	
			}
		}.runTask(_main);
		
	}
	
	public void SaveMaterialOverflowAsync(Iterable<MaterialOverflow> overflows)
	{
		LinkedList<String> statements = new LinkedList<>();
		
		for(MaterialOverflow of : overflows)
		{
			statements.add("REPLACE INTO "+SQL_TABLES.material_overflow+" (material, softcap, batch_size, min_price, drop_percent) VALUES("
					+ "\""+of.get_mat()+"\","
					+ of.get_softCap()+","
					+ of.Get_batchSize()+","
					+ of.Get_minPrice()+","
					+ of.get_dropProsent()
					+ ");");
			
			SetMaterialOverflow(of);
		}
		
		_main.GetSQL().ExecuteStatementsAsync(statements);
	}
	public void LoadMaterialOverflows()
	{
		try 
		{
			Connection con = _main.GetSQL().GetConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM "+SQL_TABLES.material_overflow.toString()+";");
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				int i = 1;
				Material mat = Material.getMaterial(rs.getString(i++));
				int softCap = rs.getInt(i++);
				int batch_size = rs.getInt(i++);
				double min_price = rs.getFloat(i++);
				double drop_percent = rs.getFloat(i++);
				
				SetMaterialOverflow(new MaterialOverflow(mat, softCap, drop_percent, batch_size, min_price));
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}
	
	public void RemoveMaterialOverflow(Iterable<Material> mats)
	{
		LinkedList<String> statements = new LinkedList<>();
		
		for(Material mat : mats)
		{
			statements.add("DELETE FROM " + SQL_TABLES.material_overflow+" WHERE material=\""+mat.toString()+"\";");
			RemoveMaterialOverflow(mat);
		}
		_main.GetSQL().ExecuteStatementsAsync(statements);
	}
	void LoadMaterialPrices()
	{	
		try 
		{
			Connection con = _main.GetSQL().GetConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM "+SQL_TABLES.price_materials.toString()+";");
			ResultSet rs = ps.executeQuery();
			int i = 1;
			if(!rs.isBeforeFirst())
			{
				//empty!
				PreparedStatement ps2 = con.prepareStatement("INSERT INTO "+SQL_TABLES.price_materials.toString()+" (material, price) VALUES(?,?);");
				for(Material mat : Material.values())
				{
					i = 1;

					ps2.setString(i++, mat.name());
					ps2.setFloat(i++, 0.0f);
					ps2.addBatch();
					PutMaterialPrice(mat, 0.0);
				}
				
				ps2.executeBatch();
				ps2.close();
			}
			else
			{
				while(rs.next())
				{
					i = 1;
					String mat_name = rs.getString(i++);
					PutMaterialPrice(Material.getMaterial(mat_name), (double)rs.getFloat(i++));
				}

				PrintINFO("LoadMaterialPrices", "Material prices loaded");
			}
			
			
			rs.close();
			ps.close();
			con.close();
		}
		catch (SQLException e) 
		{
		
			PrintERROR("LoadMaterialPrices", "ERROR");
			e.printStackTrace();

		}
		
		
	}

	
	
}
