package imu.GS.ShopUtl;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import imu.GS.Invs.ShopUI.PLAYER_INV_STATE;
import imu.iAPI.Interfaces.IButton;
import imu.iAPI.Other.CustomInvLayout;

class CustomerMenuBaseInv extends CustomInvLayout
{

	ShopItemCustomer[][] _shopItemCustomer;
	
	//ShopItemCustomer[] _playerItemsOthers;
	ItemStack[] p_state_display = new ItemStack[2];
	
	int _player_slots_start = 36;
	int _shop_slot_start = 0;
	
	PLAYER_INV_STATE p_state = PLAYER_INV_STATE.NORMAL;
	
	ItemStack empty_display;

	
	int _playerInvPage = 0;
	int _shopInvPage = 0;
	ShopBase _shopBase;
	
	HashMap<Material, ArrayList<ShopItemCustomer>> players_materialCompares= new HashMap<>();
	int materialCompares_counter = 0;
	public CustomerMenuBaseInv(Plugin main, Player player, ShopBase shopBase) {
		super(main, player, shopBase.GetNameWithColor(), 6*9);
		_shopBase = shopBase;
		setupButtons();
		_shopItemCustomer = new ShopItemCustomer[4][18];
		
		LoadShopInv();
		LoadPlayerInv();
	}
	
	class ClickInfo
	{
		public BUTTON _button;
		public int _click_amount;
		public ClickType _clickType;
		public ShopItemBase _shopItemBase;
		public int _slot;
		public ClickInfo(BUTTON button,int slot ,int amount, ClickType cType, ShopItemBase shopItemBase) 
		{
			_button = button;
			_click_amount = amount;
			_clickType = cType;
			_shopItemBase = shopItemBase;
			_slot = slot;
		}
	}

	protected enum BUTTON implements IButton
	{
		NONE,
		SHOP_ITEM,
		PLAYER_ITEM,
		GO_LEFT_SHOP,
		GO_RIGHT_SHOP,
		GO_LEFT_PLAYER,
		GO_RIGHT_PLAYER,
		STATE_PLAYER_INV;
	}
	@Override
	public void invClosed(InventoryCloseEvent arg0) 
	{
		UnRegisterItems();
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void onClickInsideInv(InventoryClickEvent e) 
	{
		e.setCancelled(true);
		ItemStack stack = e.getCurrentItem();
		if(stack == null)
			return;
		
		String buttonName = getButtonName(e.getCurrentItem());
		if(buttonName == null)
			return;
		
		BUTTON button = BUTTON.valueOf(buttonName);
		
		ClickType c_type = e.getClick();
		int amount = 0;
		ShopItemBase si = GetItem(e.getSlot());
		
		switch(c_type)
		{
			case LEFT:
				amount = 1;
				break;
			case RIGHT:
				amount = 8;
				break;
			case SHIFT_LEFT:
				amount = 64;
				break;
			case SHIFT_RIGHT:
				amount = si.Get_amount(); //all
				break;
		}
		
		if(amount > si.Get_amount())
			amount = si.Get_amount();
		
		ClickSorter(new ClickInfo(button,e.getSlot(),amount ,c_type, si));
	}

	@Override
	public void setupButtons() 
	{
		setupButton(BUTTON.GO_LEFT_SHOP, Material.DARK_OAK_SIGN, _metods.msgC("&9<< Shop"), 27);
		setupButton(BUTTON.GO_RIGHT_SHOP, Material.DARK_OAK_SIGN, _metods.msgC("&9Shop >>"), 35);
		setupButton(BUTTON.GO_RIGHT_PLAYER, Material.BIRCH_SIGN, _metods.msgC("&9<< Inv"), 30);
		setupButton(BUTTON.GO_RIGHT_PLAYER, Material.BIRCH_SIGN, _metods.msgC("&9Inv >>"), 32);
		
		p_state_display[1] = setupButton(BUTTON.STATE_PLAYER_INV, Material.STONE, _metods.msgC("&9Blocks, Ores..."), 31);
		p_state_display[0] = _metods.hideAttributes(setupButton(BUTTON.STATE_PLAYER_INV, Material.NETHERITE_CHESTPLATE, _metods.msgC("&9Tools, Armor..."), 31));
		
		
		ItemStack redLine = _metods.setDisplayName(new ItemStack(Material.RED_STAINED_GLASS_PANE), " ");
		_inv.setItem(28, redLine);_inv.setItem(29, redLine);_inv.setItem(35-1, redLine);_inv.setItem(35-2, redLine);
	}
	
	
	public void ClickSorter(ClickInfo cInfo)
	{
		switch (cInfo._button) 
		{
		case GO_LEFT_PLAYER:
			System.out.println("p go left");


			break;			
		case GO_RIGHT_PLAYER:
			System.out.println("p go right");

			break;
		case GO_LEFT_SHOP:
			System.out.println("s go left");
			break;
		case GO_RIGHT_SHOP:
			System.out.println("s go right");
			break;

		case PLAYER_ITEM:
			System.out.println("player item: "+cInfo._click_amount);
			ShopItemSeller sis = new ShopItemSeller(cInfo._shopItemBase.GetRealItem(), cInfo._click_amount);
			((ShopItemCustomer)cInfo._shopItemBase).AddAmountToPlayer(cInfo._click_amount * -1);
			_shopBase.AddNewItem(sis);	
			RefreshSlot(cInfo._slot);
			break;
		case SHOP_ITEM:
			_shopBase.RemoveItem(_shopInvPage, cInfo._slot, cInfo._click_amount);
			
			break;
		case STATE_PLAYER_INV:
			System.out.println("p state");
			break;
		default:
			break;				
		}

	
	}
	
	public void UpdateCustomerSlot(ShopItemCustomer sic,int page, int slot)
	{
		_shopItemCustomer[page][slot] = sic;
		if(page != _playerInvPage)
			return;
		
		RefreshSlot(slot+_player_slots_start);
	}
	
	public void UpdateShopSlot(int page, int slot)
	{
		if(page != _shopInvPage)
			return;
		
		RefreshSlot(slot);
		
	}
	
	void RefreshSlot(int slot)
	{
		if(slot >= 0 &&  slot <= 27)
		{
			//System.out.println("Refresh Shopslot");
			_inv.setItem(slot, SetButton(_shopBase._items.get(_shopInvPage)[slot].GetDisplayItem(), BUTTON.SHOP_ITEM));
		}
		if(slot >= _player_slots_start && slot < _size)
		{
			//System.out.println("Refresh playerSlot");
			//_inv.setItem(slot, _shopItemCustomer[slot-_player_slots_start].GetDisplayItem());
			if(_shopItemCustomer[_playerInvPage][slot-_player_slots_start] == null)
			{
				_inv.setItem(slot, null);
				return;
			}				
			_inv.setItem(slot, SetButton(_shopItemCustomer[_playerInvPage][slot-_player_slots_start].GetDisplayItem(), BUTTON.PLAYER_ITEM));
		}
	}
	void AddItemToCustomer(ShopItemBase shopItemBase, int amount)
	{
		if(shopItemBase.Get_amount() <= amount)
			amount = shopItemBase.Get_amount();
		
		
	}
	
	public void LoadShopInv()
	{
		
		for(int i = 0; i < _shopBase._items.get(_shopInvPage).length; ++i)
		{
			ShopItemSeller sis = _shopBase._items.get(_shopInvPage)[i];
			if(sis == null)
				continue;
			sis.RegisterSlot(_inv, this, _shopInvPage, i, true);
			UpdateShopSlot(_shopInvPage, i);
		}
		
	}
	
	void UnRegisterItems()
	{		
		System.out.println("Unregister items!");
		for(int i = 0; i < _shopItemCustomer.length; ++i)
		{
			for(int l = 0; l < _shopItemCustomer[i].length; ++l)
			{
				if(_shopItemCustomer[i][l] == null)
					continue;
				_shopItemCustomer[i][l].UnRegisterSlot(_inv);
			}
		}
		
		_shopBase.UnRegisterItems(_inv);
		
	}
	
	void RefreshPlayerDisplayPageSlots()
	{
		for(int i = 0; i < _shopItemCustomer[_playerInvPage].length; ++i) //page
		{
			RefreshSlot(i+_player_slots_start);
		}
	}
	
	void LoadPlayerInv()
	{
		players_materialCompares = new HashMap<>();
		for (ItemStack itemStack : _player.getInventory().getContents()) 
		{
			if(itemStack == null)
				continue;
			//AddItemStackMaterialCompares(itemStack);
			for(int i = 0; i < _shopItemCustomer.length; ++i) //page
			{
				for(int l = 0; l < _shopItemCustomer[i].length; ++l) //slot
				{
					ShopItemCustomer sic =  _shopItemCustomer[i][l];
					if(sic == null)
					{					
						_shopItemCustomer[i][l] = new ShopItemCustomer(l, itemStack, itemStack.getAmount());
						_shopItemCustomer[i][l].RegisterSlot(_inv, this,i, l, false);
						_shopItemCustomer[i][l].AddPlayerItemStackRef(itemStack);
						break;
					}
					
					if(sic.IsSameKind(itemStack))
					{
						sic.AddAmount(itemStack.getAmount());
						sic.AddPlayerItemStackRef(itemStack);
						break;
					}
				}
			}
			
		}
		
		RefreshPlayerDisplayPageSlots();
		
		System.out.println("PlayerInvLoaded");
		
	}
	
	protected ShopItemBase GetItem(int slot)
	{
		if(slot >= 0 && slot < 27) //shopside
		{
			return _shopBase.GetItem(_shopInvPage, slot);
		}
		
		if(slot >= _player_slots_start && slot <_size)
		{
			if(p_state == PLAYER_INV_STATE.NORMAL)
			{
				return _shopItemCustomer[_playerInvPage][slot-_player_slots_start];
			}
			
//			if(p_state == PLAYER_INV_STATE.OTHER_STUFF)
//			{
//				return _shopItemCustomer[_playerInvPage][slot-_player_slots_start];
//			}
			
		}
		return null;
	}
	
//	void AddItemStackMaterialCompares(ItemStack itemStack)
//	{
//		if(players_materialCompares.containsKey(itemStack.getType()))
//		{			
//			for(ShopItemCustomer sic : players_materialCompares.get(itemStack.getType()))
//			{
//				if(sic.IsSameKind(itemStack))
//				{
//					sic.AddAmount(itemStack.getAmount());
//					sic.AddPlayerItemStackRef(itemStack);
//					return;
//				}
//			}			
//		}
//		
//		ShopItemCustomer sic = new ShopItemCustomer(materialCompares_counter++,itemStack,itemStack.getAmount());
//		sic.AddPlayerItemStackRef(itemStack);
//		if(players_materialCompares.containsKey(itemStack.getType()))
//		{
//			players_materialCompares.get(itemStack.getType()).add(sic);
//			
//		}else
//		{
//			ArrayList<ShopItemCustomer> ar = new ArrayList<ShopItemCustomer>();
//			ar.add(sic);
//			players_materialCompares.put(itemStack.getType(), ar);
//		}	
//	}
	

	
	
//	final void loadPlayerInv() 
//	{
//		HashMap<Material, ShopItem> mats_i = new HashMap<>();
//		//HashMap<Integer, Material> i_mats = new HashMap<>();
//		_player_inv_refs = new HashMap<>();
//		
//		ItemStack[] content = _player.getInventory().getContents();
//		for(int i = 0; i < content.length ;++i)
//		{
//			ItemStack s = content[i];
//			
//			if(s == null)
//				continue;
//			
//			if(mats_i.containsKey(s.getType()) )
//			{
//				//System.out.println("found same type: "+s.getType());
//				if(mats_i.get(s.getType()).getRealItem().isSimilar(s))
//				{
//					//System.out.println("found same type2: "+s.getType());
//					mats_i.get(s.getType()).addAmount(s.getAmount());
//					_player_inv_refs.get(s.getType()).put(i,s);
//					continue;
//				}
//				
//				
//			}
//			ShopItem shopitem = new ShopItem(_mainn,i ,s,s.getAmount());
//			mats_i.put(s.getType(), shopitem);
//			HashMap<Integer,ItemStack> ar = new HashMap<>();
//			ar.put(i,s);
//			_player_inv_refs.put(s.getType(), ar);
//			//i_mats.put(i,s.getType());
//			
//		}
//		
////		List<Material> test = new ArrayList<>(i_mats.values());
////		Collections.sort(test);
//		int count = 0;
//		int count2 = 0;
//		for(ShopItem si : mats_i.values())
//		{
//			ItemStack displayItem = si.getDisplayItem();
//			setButton(displayItem, BUTTON.PLAYER_ITEM);
//			if(_metods.isArmor(si.getRealItem()) || _metods.isTool(si.getRealItem()))
//			{
//				 _playerItemsOthers[count2++] = si;
//				continue;
//			}
//						
//			_playerItemsNormal[count] = si;		
//			if(count < 18)
//			{
//				_inv.setItem(count+_player_slots_start, displayItem);
//			}
//			count++;
////			if(++count > _playerItemsNormal.length)
////				break;
//		}		
//	}
//	ShopItemBase GetItem(int slot)
//	{
//		if(slot >= 0 && slot < 27) //shopside
//		{
//			//return _shopItems.get(getShopSlot(slot));
//		}
//		
//		if(slot >= _player_slots_start && slot <_size)
//		{
//			if(p_state == PLAYER_INV_STATE.NORMAL)
//			{
//				return _playerItemsNormal[slot-_player_slots_start];
//			}
//			
//			if(p_state == PLAYER_INV_STATE.OTHER_STUFF)
//			{
//				return _playerItemsOthers[slot-_player_slots_start];
//			}
//			
//		}
//		return null;
//	}
}
