package imu.GS.ShopUtl;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import imu.GS.Main.Main;
import imu.GS.ShopUtl.Customer.CustomerMenuBaseInv;
import imu.GS.ShopUtl.Customer.ShopItemCustomer;
import imu.GS.ShopUtl.ItemPrice.ItemPrice;
import imu.GS.ShopUtl.ItemPrice.PriceMoney;
import imu.GS.ShopUtl.ItemPrice.PriceOwn;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.Metods;

public abstract class ShopItemBase 
{
	protected ItemStack _real_stack;
	ItemStack _display_stack;
	
	int _amount = 0;
	
	String lore_amount_str;
	String lore_price_str;
	
	String[] _lores;
	protected Metods _metods;

	HashMap<Inventory, SlotInfo> _slotPositions = new HashMap<>();
	ItemPrice _price;
	Main _main;
	protected ShopBase _shopBase;
	public ShopItemBase(Main main, ShopBase shopBase,ItemStack real, int amount) 
	{
		_metods = ImusAPI._metods;
		_main = main;
		_shopBase = shopBase;
		_real_stack = real.clone();
		_display_stack = real.clone();
		_display_stack.setAmount(1);		
		_amount = amount;
		LoadLores();
		SetItemPrice(new PriceMoney());
		
		
		//toolTip();
		
	}
	enum LoreSpot
	{
		AMOUNT;
	}
	
	class SlotInfo
	{
		Inventory _inv = null;
		CustomerMenuBaseInv _cmbi;
		int _page = -1;
		int _slot = -1;
		boolean _shopItem = false;
		public SlotInfo(Inventory _inv, CustomerMenuBaseInv cmbi,int _page, int _slot, boolean _shopItem) 
		{
			this._inv = _inv;
			this._page = _page;
			this._slot = _slot;
			this._shopItem = _shopItem;
			this._cmbi = cmbi;
		}
	}
	
	public void SetItemPrice(ItemPrice price)
	{
		if(price instanceof PriceMoney && !(price instanceof PriceOwn))
		{
			((PriceMoney)price).SetPrice(_main.get_shopManager().GetMaterialPrice(GetRealItem().getType()));
		}
		
		SetShowPrice(price);		
		_price = price;

		toolTip();
	}
	protected abstract void SetShowPrice(ItemPrice price);
	
	public ItemPrice GetItemPrice()
	{
		return _price;
	}
	
	public void RegisterSlot(Inventory inv, CustomerMenuBaseInv cmbi,int page, int slot, boolean shopItem)
	{
		_slotPositions.put(inv, new SlotInfo(inv, cmbi, page, slot, shopItem));
	}
	
	public void UnRegisterSlot(Inventory inv)
	{
		_slotPositions.remove(inv);
	}
	
	void LoadLores()
	{
		_lores = new String[5];
		_lores[0] =  _metods.msgC("&6Amount: &a");
		_lores[1] =  _metods.msgC("&9Price 1   : &5");
		_lores[2] =  _metods.msgC("&9Price 8   : &5");
		_lores[3] =  _metods.msgC("&9Price 64 : &5");
		_lores[4] =  _metods.msgC("&9Price All: &5");

	}
	
	protected void toolTip()
	{
		String[] lores = _lores.clone();
		lores[0] += _amount;
		lores[1] += _price.GetShowPriceOfAmountStr(1);
		lores[2] += _price.GetShowPriceOfAmountStr(8);
		lores[3] += _price.GetShowPriceOfAmountStr(64);
		lores[4] += _price.GetShowPriceOfAmountStr(_amount);
		//_metods.addLore(_display_stack, lores, false);
		_metods.SetLores(_display_stack, lores);
		//_metods.setlo
	}

	void SetLoreAtSpot(LoreSpot spot)
	{
		switch (spot) {
		case AMOUNT:
			_metods.reSetLore(_display_stack, _lores[0]+_amount, 0);
			_metods.reSetLore(_display_stack, _lores[4]+_price.GetShowPriceOfAmountStr(_amount), 4);
			break;

		default:
			break;
		}
	}
	public ItemStack GetDisplayItem()
	{
		return _display_stack;
	}
	public ItemStack GetRealItem()
	{
		return _real_stack;
	}
	
	public Boolean IsSameKind(ShopItemBase item)
	{
		return item.GetRealItem().isSimilar(_real_stack);
	}
	
	public Boolean IsSameKind(ItemStack item)
	{
		return item.isSimilar(_real_stack);
	}
	
	public void UpdateItem()
	{
		for(SlotInfo sInfo : _slotPositions.values())
		{
			if(!sInfo._shopItem)
			{
				sInfo._cmbi.UpdateCustomerSlot((ShopItemCustomer)this, sInfo._page, sInfo._slot);
				return;
			}else
			{
				sInfo._cmbi.UpdateShopSlot(sInfo._page, sInfo._slot);
			}
		}
	}
	
	public void AddAmount(int amount)
	{
		_amount+= amount;		
		if(_amount <= 0)
		{
			_amount = 0;
			GetDisplayItem().setType(Material.BLACK_STAINED_GLASS_PANE);
		}else
		{
			if(GetDisplayItem().getType() != GetRealItem().getType())
			{
				GetDisplayItem().setType(GetRealItem().getType());
			}
		}
		
		SetLoreAtSpot(LoreSpot.AMOUNT);
	}
	

	public int Get_amount() {
		return _amount;
	}


	public void Set_amount(int _amount) {
		this._amount = _amount;
	}
}
