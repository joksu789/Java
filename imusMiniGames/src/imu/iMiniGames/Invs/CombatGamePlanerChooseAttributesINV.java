package imu.iMiniGames.Invs;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import imu.iMiniGames.Enums.COMBAT_ATTRIBUTE;
import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Managers.CombatManager;
import imu.iMiniGames.Other.CombatDataCard;
import imu.iMiniGames.Other.CustomInvLayout;
import net.md_5.bungee.api.ChatColor;

public class CombatGamePlanerChooseAttributesINV extends CustomInvLayout implements Listener
{
	CombatManager _combatManager;
	
	String pd_buttonType = "img.GPCAIbt";

	int _tooltip_starts = 0;
	int _current_page = 0;
	CombatDataCard _card;
	
	ItemStack[] _displays = new ItemStack[2];
	public CombatGamePlanerChooseAttributesINV(Main main, Player player, CombatDataCard card) 
	{
		super(main, player, ChatColor.DARK_AQUA + "====== Available Attributes =====", 3*9);
		
		_main.getServer().getPluginManager().registerEvents(this,_main);
		_combatManager = main.get_combatManager();
		_tooltip_starts = _size-9;
		_card = card;
		setAtts();
		openThis();
		refresh();
	}
	
	public enum BUTTON
	{
		NONE,
		ATTRIBUTE,
		GO_LEFT,
		GO_RIGHT,
		BACK;
	}
	void setAtts()
	{		
		ItemStack att = new ItemStack(Material.ARROW);
		_itemM.setDisplayName(att, ChatColor.YELLOW+"Arrow Spread");
		_itemM.addLore(att, ChatColor.translateAlternateColorCodes('&', "&5Should be enabled to fair play!"), true);
		_itemM.addLore(att, ChatColor.translateAlternateColorCodes('&', "&5No Random Spread!"), true);
		setButton(att, BUTTON.ATTRIBUTE);
		_itemM.setPersistenData(att, "type", PersistentDataType.STRING, COMBAT_ATTRIBUTE.NO_ARROW_SPREAD.toString());
		_itemM.setPersistenData(att, "slot", PersistentDataType.INTEGER, 0);
		_itemM.setPersistenData(att, COMBAT_ATTRIBUTE.NO_ARROW_SPREAD.toString(), PersistentDataType.INTEGER, _card.getAttribute(COMBAT_ATTRIBUTE.NO_ARROW_SPREAD));
		_displays[0] = att;
		
		att = new ItemStack(Material.RED_DYE);
		_itemM.setDisplayName(att, ChatColor.YELLOW+"Show Damage");
		_itemM.addLore(att, ChatColor.translateAlternateColorCodes('&', "&5Display dmg to you and opponent!"), true);
		setButton(att, BUTTON.ATTRIBUTE);
		_itemM.setPersistenData(att, "type", PersistentDataType.STRING, COMBAT_ATTRIBUTE.SHOW_DMG.toString());
		_itemM.setPersistenData(att, COMBAT_ATTRIBUTE.SHOW_DMG.toString(), PersistentDataType.INTEGER, _card.getAttribute(COMBAT_ATTRIBUTE.SHOW_DMG));
		_itemM.setPersistenData(att, "slot", PersistentDataType.INTEGER, 1);
		_displays[1] = att;

	}
	void refresh()
	{
		ItemStack optionLine = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
		
		_itemM.setDisplayName(optionLine, " ");
		
		for(int i = _size-1; i > _tooltip_starts-1; --i)
		{
			_inv.setItem(i, optionLine);
		}
		
		int start = 0 + _current_page * (_tooltip_starts);
		
		
		for(int i = 0; i < 9 ; ++i)
		{
			int idx = i +start;
			
			if(idx < _displays.length)
			{
				ItemStack s =_displays[idx];
				String type =  _itemM.getPersistenData(s, "type", PersistentDataType.STRING);
				int value = _itemM.getPersistenData(s,type, PersistentDataType.INTEGER);
				_card.setAttribute(COMBAT_ATTRIBUTE.valueOf(type), value);
				_inv.setItem(i, s);
				ItemStack clone = s.clone();
				if(value > 0)
				{
					clone.setType(Material.GREEN_STAINED_GLASS_PANE);
					_inv.setItem(i+9, _itemM.setDisplayName(clone, ChatColor.GREEN + "Enabled"));
				}
				else
				{
					clone.setType(Material.RED_STAINED_GLASS_PANE);
					_inv.setItem(i+9, _itemM.setDisplayName(clone, ChatColor.RED +"Disabled"));
				}
				
				
			}
			else
			{
				_inv.setItem(i, _itemM.setDisplayName(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " "));
				_inv.setItem(i+9, _itemM.setDisplayName(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " "));
			}
			
			
		}
		
		setupButton(BUTTON.GO_LEFT, Material.BIRCH_SIGN, ChatColor.AQUA + "<<", _tooltip_starts+3);
		setupButton(BUTTON.GO_RIGHT, Material.BIRCH_SIGN, ChatColor.AQUA + ">>", _size-4);
		setupButton(BUTTON.BACK, Material.RED_STAINED_GLASS_PANE, ChatColor.AQUA + "GO BACK", _tooltip_starts);
		
		
		
	}
	
	int totalPages()
	{
		int pages =(int) Math.round(((_main.getServer().getOnlinePlayers().size()-1)/(_tooltip_starts))+0.5);
		return pages-1;
	}
	
	void chanceCurrentPage(int i)
	{
		_current_page = _current_page + i;
		if(_current_page < 0)
		{
			_current_page = 0;
		}
		if(_current_page > totalPages())
		{
			_current_page = totalPages();
		}
	}
	
	void setButton(ItemStack stack, BUTTON b)
	{
		_itemM.setPersistenData(stack, pd_buttonType, PersistentDataType.STRING, b.toString());
	}
	
	BUTTON getButton(ItemStack stack)
	{
		String button = _itemM.getPersistenData(stack, pd_buttonType, PersistentDataType.STRING);
		if(button != null)
			return BUTTON.valueOf(button);
		
		return BUTTON.NONE;
	}
	
	public ItemStack setupButton(BUTTON b, Material material, String displayName, int itemSlot)
	{
		ItemStack sbutton = new ItemStack(material);
		_itemM.setDisplayName(sbutton, displayName);
		setButton(sbutton, b);
		_inv.setItem(itemSlot, sbutton);
		return _inv.getItem(itemSlot);
	}
	
	
	@EventHandler
	public void onInvClickEvent(InventoryClickEvent e) 
	{
		int rawSlot = e.getRawSlot();
		int slot = e.getSlot();
		
		if(isThisInv(e) && (rawSlot == slot))
		{			
			e.setCancelled(true);
			ItemStack stack = e.getCurrentItem();
			
			BUTTON button = getButton(stack);
			//int item_id = (_current_page * _tooltip_starts)+slot;
			switch (button) 
			{
			case NONE:
				
				break;
			case GO_LEFT:
				chanceCurrentPage(-1);
				refresh();
				break;
			case GO_RIGHT:
				chanceCurrentPage(1);
				refresh();
				break;
				
			case BACK:
				new CombatGamePlaner(_main, _player, _card);
				break;
			case ATTRIBUTE:
				//otetaan v��r�st�, pit�s ottaa se sielt� arraysta
				String type = _itemM.getPersistenData(stack, "type", PersistentDataType.STRING);
				int value = _itemM.getPersistenData(stack, type, PersistentDataType.INTEGER);
				if(value > 0)
				{
					_itemM.setPersistenData(_displays[_itemM.getPersistenData(stack, "slot", PersistentDataType.INTEGER)], type, PersistentDataType.INTEGER, 0);
				}else
				{
					_itemM.setPersistenData(_displays[_itemM.getPersistenData(stack, "slot", PersistentDataType.INTEGER)], type, PersistentDataType.INTEGER, 1);
				}				
				refresh();
				break;
			default:
				break;
			}
			
		}	
	}
	
	@EventHandler
	public void onInvClose(InventoryCloseEvent e)
	{
		if(isThisInv(e))
		{
			HandlerList.unregisterAll(this);
		}
	}

}
