package imu.DontLoseItems.other;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import imu.DontLoseItems.CustomItems.Arrow_ReflectController;
import imu.DontLoseItems.CustomItems.Manager_HellTools;
import imu.DontLoseItems.Enums.ITEM_RARITY;
import imu.DontLoseItems.main.DontLoseItems;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.Cooldowns;
import imu.iAPI.Other.Metods;
import imu.iAPI.Utilities.ImusUtilities;

public class Manager_HellArmor implements Listener
{
	public static Manager_HellArmor Instance;
	
	private HashMap<UUID, Location> _lastLocations;
	

	private final String _PD_HELL_BOOTS = "HELL_BOOTS";
	private final String _PD_HELL_LEGGINS = "HELL_LEGGINS";
	private final String _PD_HELL_CHESTPLATE = "HELL_CHESTPLATE";
	private final String _PD_HELL_HELMET = "HELL_HELMET";
	private final String _PD_HELL_TIER = "HELL_TIER";
	private final String _PD_HELL_FEAR_REDUCE = "HELL_FEAR_REDUCE";
	private final String _PD_HELL_ARROW = "HELL_ARROW";
	private final String _PD_HELL_TORCH = "HELL_TORCH";
	//private final String _PD_HELL_STONE_SHIELD = "HELL_STONE_SHIELD";
	private final String _PD_HELL_REFLECT_SHIELD = "HELL_REFLECT_SHIELD";
	
	private final String _PD_BLOCK_DEBRIS = "DebrisBlock";	
	//private final String _META_HELL_BOOTS_STONE = "HB_Stone";

	private final String _CD_ON_LAVA = "onLava";
	private final String _CD_IN_LAVA = "inLava";
	private final String _CD_CREATED_SAFE_PLAT = "safe_plat";
	
	private Cooldowns _cds;
	//private Random _rand;
	private World _nether;
	
	private final int _hellBootsRadius = 3;
	public final int ReflectShieldDurabilityLost = 3;
	

	@SuppressWarnings("unused")
	private BukkitTask _asyncTask;
	private int _asyncTaskTicks = 1;
	
	@SuppressWarnings("unused")
	private BukkitTask _task;
	private int _taskTicks = 1;
	
	//public final Arrow_Reflect ReflectArrow = new Arrow_Reflect(0.5,1.2,2,10,false);
	public final Arrow_ReflectController ReflectArrowController;
	
	public Manager_HellArmor()
	{
		Instance = this;
		_lastLocations = new HashMap<>();

		_cds = new Cooldowns();
		//_rand = new Random();
		_nether = Bukkit.getWorld("world_nether");
		//RunnableAsync();
		
		ReflectArrowController = new Arrow_ReflectController();
		Runnable();
	}
	

	
	private void Runnable()
	{
		_task = new BukkitRunnable() 
		{				
			@Override
			public void run() 
			{
				
				ReflectArrowController.OnReflectArrowLoop();
				Manager_HellTools.Instance.HellSword_Controller.OnThrowLoop();
				Manager_HellTools.Instance.HellAxe_Controller.OnThrowLoop();
			}

			
		}.runTaskTimer(DontLoseItems.Instance, 20 * 3, _taskTicks);	
	}
	
	
	
	private RarityItem[] _hellBoots = 
		{
			new RarityItem(new ItemStack(Material.LEATHER_BOOTS), ChatColor.DARK_RED+"Hell Boots", ITEM_RARITY.Common, 		new double[] {0.0, -14,  1, 0, 0, 2}),	
			new RarityItem(new ItemStack(Material.GOLDEN_BOOTS), ChatColor.DARK_RED+"Hell Boots", ITEM_RARITY.Uncommon, 	new double[] {0.00, -12, 1, 0, 0, 2}),	
			new RarityItem(new ItemStack(Material.CHAINMAIL_BOOTS), ChatColor.DARK_RED+"Hell Boots", ITEM_RARITY.Rare, 		new double[] {0.01, -10, 1, 1, 0, 2}),	
			new RarityItem(new ItemStack(Material.IRON_BOOTS), ChatColor.DARK_RED+"Hell Boots", ITEM_RARITY.Epic, 			new double[] {0.01, -8,  1, 1, 0, 2}),	
			new RarityItem(new ItemStack(Material.DIAMOND_BOOTS), ChatColor.DARK_RED+"Hell Boots", ITEM_RARITY.Mythic, 		new double[] {0.02, -6,  1, 1, 0, 2.2}),	
			new RarityItem(new ItemStack(Material.NETHERITE_BOOTS), ChatColor.DARK_RED+"Hell Boots", ITEM_RARITY.Legendary, new double[] {0.05, -4,  3, 3, 0, 3}),	
		};
	
	private RarityItem[] _hellLeggins = 
		{
			new RarityItem(new ItemStack(Material.LEATHER_LEGGINGS), ChatColor.DARK_RED+"Hell Leggins", ITEM_RARITY.Common, 		new double[] {0.0, 	-8,  1, 0, 0, 2}),	
			new RarityItem(new ItemStack(Material.GOLDEN_LEGGINGS), ChatColor.DARK_RED+"Hell Leggins", ITEM_RARITY.Uncommon, 		new double[] {0.00, -7, 2, 0 , 0, 2}),	
			new RarityItem(new ItemStack(Material.CHAINMAIL_LEGGINGS), ChatColor.DARK_RED+"Hell Leggins", ITEM_RARITY.Rare, 		new double[] {0.00, -6, 2, 1 , 0, 2}),	
			new RarityItem(new ItemStack(Material.IRON_LEGGINGS), ChatColor.DARK_RED+"Hell Leggins", ITEM_RARITY.Epic, 				new double[] {0.00, -5,  3, 1, 0, 2}),	
			new RarityItem(new ItemStack(Material.DIAMOND_LEGGINGS), ChatColor.DARK_RED+"Hell Leggins", ITEM_RARITY.Mythic, 		new double[] {0.01, -2,  5, 1, 0, 3}),	
			new RarityItem(new ItemStack(Material.NETHERITE_LEGGINGS), ChatColor.DARK_RED+"Hell Leggins", ITEM_RARITY.Legendary, 	new double[] {0.01,  0,  6, 3, 0, 3}),	
		};
	
	private RarityItem[] _hellChest = 
		{
			new RarityItem(new ItemStack(Material.LEATHER_CHESTPLATE), ChatColor.DARK_RED+"Hell Chestplate", ITEM_RARITY.Common, 		new double[] {0.0, 	-5,  3, 0, 0,   3}),	
			new RarityItem(new ItemStack(Material.GOLDEN_CHESTPLATE), ChatColor.DARK_RED+"Hell Chestplate", ITEM_RARITY.Uncommon, 		new double[] {0.00, -4,  5, 0, 0,   3}),	
			new RarityItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE), ChatColor.DARK_RED+"Hell Chestplate", ITEM_RARITY.Rare, 		new double[] {0.00, -3,  5, 1, 0,   3}),	
			new RarityItem(new ItemStack(Material.IRON_CHESTPLATE), ChatColor.DARK_RED+"Hell Chestplate", ITEM_RARITY.Epic, 			new double[] {0.00, -2,  5, 1, 0,   3}),	
			new RarityItem(new ItemStack(Material.DIAMOND_CHESTPLATE), ChatColor.DARK_RED+"Hell Chestplate", ITEM_RARITY.Mythic, 		new double[] {0.00, -1,  7, 1, 0.1, 4}),	
			new RarityItem(new ItemStack(Material.NETHERITE_CHESTPLATE), ChatColor.DARK_RED+"Hell Chestplate", ITEM_RARITY.Legendary, 	new double[] {0.00, 0,  8, 2, 0.6,   5}),	
		};
	
	private RarityItem[] _hellHelmet = 
		{
			new RarityItem(new ItemStack(Material.LEATHER_HELMET), ChatColor.DARK_RED+"Hell Helmet", ITEM_RARITY.Common, 		new double[] {0.0, 	-4,  1, 0, 0, 2}),	
			new RarityItem(new ItemStack(Material.GOLDEN_HELMET), ChatColor.DARK_RED+"Hell Helmet", ITEM_RARITY.Uncommon, 		new double[] {0.00, -4,  1, 0, 0, 2}),	
			new RarityItem(new ItemStack(Material.CHAINMAIL_HELMET), ChatColor.DARK_RED+"Hell Helmet", ITEM_RARITY.Rare, 		new double[] {0.00, -3,  1, 0, 0, 2  }),	
			new RarityItem(new ItemStack(Material.IRON_HELMET), ChatColor.DARK_RED+"Hell Helmet", ITEM_RARITY.Epic, 			new double[] {0.00, -3,  1, 0, 0, 2}),	
			new RarityItem(new ItemStack(Material.DIAMOND_HELMET), ChatColor.DARK_RED+"Hell Helmet", ITEM_RARITY.Mythic, 		new double[] {0.00, -1,  2, 1, 0, 2}),	
			new RarityItem(new ItemStack(Material.NETHERITE_HELMET), ChatColor.DARK_RED+"Hell Helmet", ITEM_RARITY.Legendary, 	new double[] {0.01,  0,  2, 2, 0, 3}),	
		};
	
	private RarityItem[] _hellArrows = 
		{
			new RarityItem(new ItemStack(Material.ARROW), ChatColor.DARK_RED+"Hell Arrow", ITEM_RARITY.Common, 		new double[] {1}),	
			new RarityItem(new ItemStack(Material.ARROW), ChatColor.DARK_RED+"Hell Arrow", ITEM_RARITY.Uncommon, 	new double[] {2}),	
			new RarityItem(new ItemStack(Material.ARROW), ChatColor.DARK_RED+"Hell Arrow", ITEM_RARITY.Rare, 		new double[] {3}),	
			new RarityItem(new ItemStack(Material.ARROW), ChatColor.DARK_RED+"Hell Arrow", ITEM_RARITY.Epic, 		new double[] {5}),	
			new RarityItem(new ItemStack(Material.ARROW), ChatColor.DARK_RED+"Hell Arrow", ITEM_RARITY.Mythic, 		new double[] {9}),	
			new RarityItem(new ItemStack(Material.ARROW), ChatColor.DARK_RED+"Hell Arrow", ITEM_RARITY.Legendary, 	new double[] {20}),	
		};
	
	
	private RarityItem[] _hellReflectShields = 
		{
			new RarityItem(new ItemStack(Material.SHIELD), ChatColor.DARK_RED+"Hell Reflect Shield", ITEM_RARITY.Common, 		new double[] {0}),	//not used
			new RarityItem(new ItemStack(Material.SHIELD), ChatColor.DARK_RED+"Hell Reflect Shield", ITEM_RARITY.Uncommon, 		new double[] {0}),	//not used
			new RarityItem(new ItemStack(Material.SHIELD), ChatColor.DARK_RED+"Hell Reflect Shield", ITEM_RARITY.Rare, 			new double[] {0}),	//not used
			new RarityItem(new ItemStack(Material.SHIELD), ChatColor.DARK_RED+"Hell Reflect Shield", ITEM_RARITY.Epic, 			new double[] {0}),	
			new RarityItem(new ItemStack(Material.SHIELD), ChatColor.DARK_RED+"Hell Reflect Shield", ITEM_RARITY.Mythic, 		new double[] {0}),	
			new RarityItem(new ItemStack(Material.SHIELD), ChatColor.DARK_RED+"Hell Reflect Shield", ITEM_RARITY.Legendary, 	new double[] {0}),	
		};
	
	
	public ItemStack RemoveArmorData(ItemStack stack)
	{
		String[] hellEquipment = {
			    _PD_HELL_BOOTS,
			    _PD_HELL_LEGGINS,
			    _PD_HELL_CHESTPLATE,
			    _PD_HELL_HELMET,
			    _PD_HELL_TIER,
			    _PD_HELL_REFLECT_SHIELD
			};
		
		for(String pd : hellEquipment)
		{
			stack = Metods._ins.removePersistenData(stack, pd);
		}
		
		return stack;
	}
	public ItemStack CreateHellBoots(ITEM_RARITY rarity)
	{
		
		RarityItem rarityItem = null;
		
		for(RarityItem boots : _hellBoots)
		{
			if(boots.Rarity == rarity) {rarityItem = boots;}
		}
		
		ItemStack stack = rarityItem.GetItemStack();
		
		String[] lores = 
			{ 
				" ",
				"&9Able to walk on &cLava",
				" ",
				"&9Reduce &5Fear &9Build up by &2"+rarityItem.Values[5],
				" ",
				"&7'Some say these boots were created by",
				"&7a mad wizard in an attempt to control the power",
				"&7of the volcanoes. Others say he just wanted",
				"&7a comfy pair of slippers'",
				
				};

		Metods._ins.SetLores(stack, lores, false);

		ItemMeta meta = stack.getItemMeta();
 
	
		if(rarityItem.Values[0] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "generic.movementSpeed", rarityItem.Values[0],AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
		if(rarityItem.Values[1] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, new AttributeModifier(UUID.randomUUID(), "generic.health", rarityItem.Values[1], AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
		if(rarityItem.Values[2] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "generic.armor", rarityItem.Values[2], AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
		if(rarityItem.Values[3] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "generic.armor_toughness", rarityItem.Values[3], AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));

		stack.setItemMeta(meta);

		Metods._ins.setPersistenData(stack, _PD_HELL_BOOTS, PersistentDataType.INTEGER, 1);
		Metods._ins.setPersistenData(stack, _PD_HELL_TIER, PersistentDataType.STRING, rarity.toString());
		Metods._ins.setPersistenData(stack, _PD_HELL_FEAR_REDUCE, PersistentDataType.DOUBLE, rarityItem.Values[5]);

		return stack;
	}
	
	public ItemStack CreateHellLeggins(ITEM_RARITY rarity)
	{
		
		RarityItem rarityItem = null;
		
		for(RarityItem leggins : _hellLeggins)
		{
			if(leggins.Rarity == rarity) {rarityItem = leggins;}
		}
		
		ItemStack stack = rarityItem.GetItemStack();
		
		String[] lores = 
			{ 
				" ",
				"&9Half &cFire &9Damage Taken",
				" ",
				"&9Reduce &5Fear &9Build up by &2"+rarityItem.Values[5],
				" ",
				"&7'Looking for leggings that can withstand",
				"&7the heat?. Look no further, the Hell",
				"&7Leggings have got you covered...",
				"&7half-covered, that is'",
				
				};

		Metods._ins.SetLores(stack, lores, false);

		ItemMeta meta = stack.getItemMeta();
 
	
		if(rarityItem.Values[0] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "generic.movementSpeed", rarityItem.Values[0],AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));
		if(rarityItem.Values[1] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, new AttributeModifier(UUID.randomUUID(), "generic.health", rarityItem.Values[1], AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));
		if(rarityItem.Values[2] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "generic.armor", rarityItem.Values[2], AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));
		if(rarityItem.Values[3] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "generic.armor_toughness", rarityItem.Values[3], AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));

		stack.setItemMeta(meta);

		Metods._ins.setPersistenData(stack, _PD_HELL_LEGGINS, PersistentDataType.INTEGER, 1);
		Metods._ins.setPersistenData(stack, _PD_HELL_TIER, PersistentDataType.STRING, rarity.toString());
		Metods._ins.setPersistenData(stack, _PD_HELL_FEAR_REDUCE, PersistentDataType.DOUBLE, rarityItem.Values[5]);

		return stack;
	}
	
	public ItemStack CreateHellChestplate(ITEM_RARITY rarity)
	{
		
		RarityItem rarityItem = null;
		
		for(RarityItem chest : _hellChest)
		{
			if(chest.Rarity == rarity) {rarityItem = chest;}
		}
		
		ItemStack stack = rarityItem.GetItemStack();
		
		String[] lores = 
			{ 
				" ",
				"&9Half &cFire &9Damage Taken.",
				" ",
				"&9If &4Hell Leggins &9Equip too",
				"&9then &5immunity &cFire &9damage",
				" ",
				"&9Reduce &5Fear &9Build up by &2"+rarityItem.Values[5],
				" ",
				"&7'When it comes to fire protection,",
				"&7the Hell Chestplate is worth its weight in,",
				"&7well, not gold, but it will save you ",
				"&7from getting burned!'",
				
				};

		Metods._ins.SetLores(stack, lores, false);

		ItemMeta meta = stack.getItemMeta();
 
	
		if(rarityItem.Values[0] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "generic.movementSpeed", rarityItem.Values[0],AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
		if(rarityItem.Values[1] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, new AttributeModifier(UUID.randomUUID(), "generic.health", rarityItem.Values[1], AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
		if(rarityItem.Values[2] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "generic.armor", rarityItem.Values[2], AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
		if(rarityItem.Values[3] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "generic.armor_toughness", rarityItem.Values[3], AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
		if(rarityItem.Values[4] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "generic.armor_knocback", rarityItem.Values[4], AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));

		stack.setItemMeta(meta);

		Metods._ins.setPersistenData(stack, _PD_HELL_CHESTPLATE, PersistentDataType.INTEGER, 1);
		Metods._ins.setPersistenData(stack, _PD_HELL_TIER, PersistentDataType.STRING, rarity.toString());
		Metods._ins.setPersistenData(stack, _PD_HELL_FEAR_REDUCE, PersistentDataType.DOUBLE, rarityItem.Values[5]);

		return stack;
	}
	
	
	public ItemStack CreateHellHelmet(ITEM_RARITY rarity)
	{
		
		RarityItem rarityItem = null;
		
		for(RarityItem helmet : _hellHelmet)
		{
			if(helmet.Rarity == rarity) {rarityItem = helmet;}
		}
		
		ItemStack stack = rarityItem.GetItemStack();
		
		String[] lores = 
			{ 
				" ",
				"&9Gives &aNight Vision",
				//" ",
				//"&4Hells &8Darkness &9Doesn't effect on you",
				" ",
				"&9Reduce &5Fear &9Build up by &2"+rarityItem.Values[5],
				" ",
				"&7'No more fumbling around in",
				"&7the dark with the Hell Helmet.",
				"&7It's like a built-in night light!",
				
				};

		Metods._ins.SetLores(stack, lores, false);

		ItemMeta meta = stack.getItemMeta();
 
	
		if(rarityItem.Values[0] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "generic.movementSpeed", rarityItem.Values[0],AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD));
		if(rarityItem.Values[1] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, new AttributeModifier(UUID.randomUUID(), "generic.health", rarityItem.Values[1], AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD));
		if(rarityItem.Values[2] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "generic.armor", rarityItem.Values[2], AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD));
		if(rarityItem.Values[3] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "generic.armor_toughness", rarityItem.Values[3], AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD));
		if(rarityItem.Values[4] != 0.00) meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "generic.armor_toughness", rarityItem.Values[4], AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD));

		stack.setItemMeta(meta);

		Metods._ins.setPersistenData(stack, _PD_HELL_HELMET, PersistentDataType.INTEGER, 1);
		Metods._ins.setPersistenData(stack, _PD_HELL_TIER, PersistentDataType.STRING, rarity.toString());
		Metods._ins.setPersistenData(stack, _PD_HELL_FEAR_REDUCE, PersistentDataType.DOUBLE, rarityItem.Values[5]);

		return stack;
	}
	
	
	public ItemStack CreateHellArrow(ITEM_RARITY rarity)
	{
		
		RarityItem rarityItem = null;
		
		for(RarityItem arrow : _hellArrows)
		{
			if(arrow.Rarity == rarity) {rarityItem = arrow;}
		}
		
		ItemStack stack = rarityItem.GetItemStack();
		
		String[] lores = 
			{ 
				" ",
				"&9Explodes Ores in Blast area of &2"+rarityItem.Values[0],
				"&9Explode Ores Rolls with Fortune IV",
				"&9Ores comes to your inv",
				" ",
				"&9Infinity enchant doesn't work for this",
				" ",
				"&7'Blast your way to riches with",
				"&7the Hell Arrow. It's like a miniature",
				"&7mining explosion in every shot.",
				
				};
		
		if(rarity == ITEM_RARITY.Common) lores[2] 		= "&9Explode Oress";
		if(rarity == ITEM_RARITY.Uncommon) lores[2] 	= "&9Explode Ores Rolls with Fortune I";
		if(rarity == ITEM_RARITY.Epic) lores[2] 		= "&9Explode Ores Rolls with Fortune II";
		if(rarity == ITEM_RARITY.Mythic) lores[2] 		= "&9Explode Ores Rolls with Fortune III";
		if(rarity == ITEM_RARITY.Legendary) lores[2]	= "&9Explode Ores Rolls with Fortune V";
		
		Metods._ins.SetLores(stack, lores, false);

		ItemMeta meta = stack.getItemMeta();
 
		stack.setItemMeta(meta);

		Metods._ins.setPersistenData(stack, _PD_HELL_ARROW, PersistentDataType.INTEGER, (int)rarityItem.Values[0]);
		Metods._ins.setPersistenData(stack, _PD_HELL_TIER, PersistentDataType.STRING, rarity.toString());

		return stack;
	}
	
	public ItemStack CreateHellTorch()
	{
		
		ItemStack stack = new ItemStack(Material.TORCH);
		
		Metods.setDisplayName(stack, ChatColor.DARK_RED+"Hell Torch");
		
		String[] lores = 
			{ 
				" ",
				"&9Doesn't get knocked",
				"&9off by &8Darkness,",
				"&9but can be used only once",
				" ",
				"&7'Light up the darkness with the",
				"&7Hell Torch. It's like a little piece",
				"&7of Hell, in a good way.",
				
				};

		Metods._ins.SetLores(stack, lores, false);

		ItemMeta meta = stack.getItemMeta();
 
		stack.setItemMeta(meta);

		Metods._ins.setPersistenData(stack, _PD_HELL_TORCH, PersistentDataType.INTEGER, 1);
		return stack;
	}
	
	public ItemStack CreateHellStoneShield()
	{
		
		ItemStack stack = new ItemStack(Material.TORCH);
		
		Metods.setDisplayName(stack, ChatColor.DARK_RED+"Hell Stone Shield");
		
		String[] lores = 
			{ 
				" ",
				"&9Creates Stone Wall Front of You",
				" ",
				"&7'Light up the darkness with the",
				"&7Hell Torch. It's like a little piece",
				"&7of Hell, in a good way.",
				
				};

		Metods._ins.SetLores(stack, lores, false);

		ItemMeta meta = stack.getItemMeta();
 
		stack.setItemMeta(meta);

		Metods._ins.setPersistenData(stack, _PD_HELL_TORCH, PersistentDataType.INTEGER, 1);
		return stack;
	}
	
	public ItemStack CreateHellReflectShield(ITEM_RARITY rarity)
	{
		RarityItem rarityItem = null;
		
		for(RarityItem arrow : _hellReflectShields)
		{
			if(arrow.Rarity == rarity) {rarityItem = arrow;}
		}
		
		ItemStack stack = rarityItem.GetItemStack();
		
		ArrayList<String> lores = new ArrayList<>();
		lores.add(" ");
		lores.add("&9Blocks arrows &3any &9direction");
		lores.add(" ");
		if(rarity != ITEM_RARITY.Epic)lores.add("&eReflects &7arrow &9back to");
		if(rarity == ITEM_RARITY.Epic)lores.add("&eReflects &7arrow &9back to attacker");

		
		if(rarity == ITEM_RARITY.Mythic) 	lores.add("&9attacker and &5doubles");
		if(rarity == ITEM_RARITY.Legendary) lores.add("&9attacker and &5triples");
		if(rarity != ITEM_RARITY.Epic) lores.add("&9it damage");
		
		if(rarity != ITEM_RARITY.Epic) lores.add(" ");
//		if(rarity == ITEM_RARITY.Epic) lores.add("&9Arrow leaves a small trail");
//		if(rarity == ITEM_RARITY.Epic) lores.add("&9of flames behind it");
		
		if(rarity == ITEM_RARITY.Mythic) lores.add("&9Arrow leaves a substantial");
		if(rarity == ITEM_RARITY.Mythic) lores.add("&9trail of &4flames &9behind it");
	
		
		if(rarity == ITEM_RARITY.Legendary) lores.add("&9Arrow leaves a massive");
		if(rarity == ITEM_RARITY.Legendary) lores.add("&9trail of &4flames &9behind it");
		lores.add(" ");
		if(rarity == ITEM_RARITY.Legendary) lores.add("&9Causes &4fiery &9destruction upon impact");
		if(rarity == ITEM_RARITY.Legendary) lores.add(" ");
//		lores.add("&9If Arrow is Players, " + durabilityLost + " durability will be lost");
//		lores.add(" ");
		lores.add("&7'Legend has it that this Shield was");
		lores.add("&7once owned by a mischievous archer");
		lores.add("&7who loved to pull pranks on his");
		lores.add("&7enemies. The Shield would always send");
		lores.add("&7their arrows right back at them, much");
		lores.add("&7to their surprise (and sometimes, horror!)'");
		

		Metods._ins.SetLores(stack, lores.toArray(new String[lores.size()]), false);

		ItemMeta meta = stack.getItemMeta();
 
		stack.setItemMeta(meta);

		Metods._ins.setPersistenData(stack, _PD_HELL_REFLECT_SHIELD, PersistentDataType.INTEGER, 1);
		Metods._ins.setPersistenData(stack, _PD_HELL_TIER, PersistentDataType.STRING, rarity.toString());
		return stack;
	}
	
	
	
	
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		RemoveLastLocation(e.getPlayer());

		_cds.removeCooldown(e.getPlayer().getUniqueId().toString() + _CD_IN_LAVA);
		_cds.removeCooldown(e.getPlayer().getUniqueId().toString() + _CD_ON_LAVA);
	}

	private void RemoveLastLocation(Player player)
	{
		if (_lastLocations.containsKey(player.getUniqueId()))
			_lastLocations.remove(player.getUniqueId());
	}
	

	@EventHandler(priority = EventPriority.HIGH)
	public void OnEntityDamage(EntityDamageEvent e)
	{
		Entity entity = e.getEntity();
		
		if(!(entity instanceof Player)) return;
		
		Player player = (Player) entity;
		
		DamageCause cause = e.getCause();
		
		
		if(cause != DamageCause.FIRE  && cause != DamageCause.FIRE_TICK && cause != DamageCause.LAVA) return;
		
		if (IsHellLeggins(player.getInventory().getLeggings()))
		{
			if(IsHellChestplate(player.getInventory().getChestplate())) 
			{
				player.setFireTicks(0);
				e.setCancelled(true); 
				return;
			}
			
			e.setDamage(e.getDamage() * 0.5);
			return;
		}
		
		if (IsHellChestplate(player.getInventory().getChestplate()))
		{

			e.setDamage(e.getDamage() * 0.5);
			
			return;
		}
		

	}
	
	@EventHandler
	public void OnProtectileHit(ProjectileHitEvent e)
	{
		Projectile projectile = e.getEntity();
		
		if(projectile instanceof Arrow)
		{
			if(e.getHitBlock() == null) return;
			
			Arrow arrow = (Arrow)projectile;
			
			if(!(arrow.getShooter() instanceof Player)) return;
			
			Integer radius = GetHellArrow(arrow);
			
			if(radius <= 0) return;
			
			ExplodeHellArrow((Player)arrow.getShooter(),GetHellArrowTier(arrow),e.getHitBlock(), radius);
			arrow.remove();
				
		}
	}
	
	@EventHandler
    public void OnPlayerShoot(EntityShootBowEvent e) 
	{
		//System.out.println("Player shoots");
        Entity entity = e.getEntity();
        
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player)e.getEntity();
        Arrow arrow = (Arrow) e.getProjectile();
        
        ItemStack arrowItemStack = null;
        for(ItemStack stack : player.getInventory().getContents())
        {
        	if(stack == null || stack.getType() == Material.AIR) continue;
        	

        	if(stack.getType() != Material.ARROW) continue;
        	
        	arrowItemStack = stack;
        	break;
        	
        }

        if(arrowItemStack == null) return;
        
        SetHellArrow(arrowItemStack,arrow, player.getGameMode() != GameMode.CREATIVE);
        //e.getProjectile().remove();

    }

	@EventHandler
	public void OnPlayerMove(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();

		Location lastLocation = _lastLocations.get(player.getUniqueId());
		Location currentLocation = player.getLocation();
		
		if (IsHellHelmet(player.getInventory().getHelmet())) // night vision effect
		{ 
		     PotionEffect existingEffect = player.getPotionEffect(PotionEffectType.NIGHT_VISION);

		     if (existingEffect != null && existingEffect.getDuration() > 300) 
		     {
		    	 
		     }
		     else
		     {
		    	 PotionEffect nightVision = new PotionEffect(PotionEffectType.NIGHT_VISION, 600, 0, false);
			     player.addPotionEffect(nightVision);
		     }
		       
		     
		}
		

		if (!(lastLocation == null || lastLocation.getWorld() != currentLocation.getWorld()
				|| currentLocation.distance(lastLocation) >= 1))
		{
			return;
		}
			

		if (!IsHellBoots(player.getInventory().getBoots()))
		{
			RemoveLastLocation(player);
			return;
		}

		Block block = currentLocation.getBlock().getRelative(BlockFace.DOWN);
		
		if(block.getType() != Material.LAVA && block.getType() != Material.STONE && block.getType() != Material.AIR)
		{
			//System.out.println("noni");
			_cds.removeCooldown(player.getUniqueId().toString() + _CD_ON_LAVA);
		}
		
		if (currentLocation.getBlock().getType() == Material.LAVA)
		{
			return;
		}
		
		//CHECKING FALLING 
		if(player.getVelocity().getY() < -0.5 && _cds.isCooldownReady(player.getUniqueId().toString()+_CD_CREATED_SAFE_PLAT))
		{

			for(int i = 0; i < 6; i++)
			{
				Block b = currentLocation.clone().add(0,-1*i,0).getBlock();
				
				if(b.getType() != Material.LAVA) continue;
				
				if(!CreateStonePlatform(player, b,_hellBootsRadius)) continue;
				
				_cds.setCooldownInSeconds(player.getUniqueId().toString()+_CD_CREATED_SAFE_PLAT, 2);
				return;
			}
		}
		
		if(CreateStonePlatform(player, block,_hellBootsRadius))
		{
			_lastLocations.put(player.getUniqueId(), currentLocation);
		}
	}
	
	private Block SetBlockType(Player player,Block block, Material mat)
	{
	    BlockPlaceEvent event = new BlockPlaceEvent(block, block.getState(), block.getRelative(BlockFace.DOWN), new ItemStack(mat), player, true, EquipmentSlot.HAND);
	    //new BlockPl
	    Bukkit.getServer().getPluginManager().callEvent(event);
	    
	    if(event.isCancelled()) return block;
	    
	    block.setType(mat);
	    
		return block;
	}
	
	private boolean CreateStonePlatform(Player player,Block block, int radius)
	{
		if (block.getType() == Material.LAVA || !_cds.isCooldownReady(player.getUniqueId().toString() + _CD_ON_LAVA))
		{
			//System.out.println("set lava");
			
			Block center = block.getRelative(player.getFacing(), 1);

			_cds.setCooldownInSeconds(player.getUniqueId().toString() + _CD_ON_LAVA, 3);
			int x = center.getX();
			int y = center.getY();
			int z = center.getZ();
			int r = radius;
			
			for (int i = x - r; i <= x + r; i++)
			{
				for (int k = z - r; k <= z + r; k++)
				{
					Block b = center.getWorld().getBlockAt(i, y, k);
					
					if (b.getType() != Material.LAVA) continue;
	
					if(!IsInCircle(i, y, k, x, y, z, r)) continue;
					
					SetBlockType(player, b, Material.STONE);
				}

			}
			return true;
		}
		
		return false;
	}
	private boolean IsInCircle(int x, int y, int z, int cx, int cy, int cz, int r)
	{
		return (x - cx) * (x - cx) + (y - cy) * (y - cy) + (z - cz) * (z - cz) <= r * r;
	}
	
	public void ExplodeHellArrow(Player player, ITEM_RARITY rarity,Block block, int radius)
	{
		//if(rarity == null) rarity = ITEM_RARITY.Uncommon;
		
		
		new BukkitRunnable() {
			
			@Override
			public void run()
			{
				LinkedList<Location> locs = ImusUtilities.CreateSphere(block.getLocation(), radius,null, ImusAPI.Ores);
				
				new BukkitRunnable() {
					
					ITEM_RARITY rary = rarity;
					@Override
					public void run()
					{
						if(rary == null) rary = ITEM_RARITY.Uncommon;
						
						int FortuneLevel = rarity.GetIndex();
						
						for(Location loc : locs)
						{
							Block b = loc.getBlock();
							
							if(b.getType() == Material.OBSIDIAN) continue;
//							
//							if(!_ores.contains(b.getType())) continue;
							//if(IsPlayerDebris(b)) continue;
							
							BlockBreakEvent bBreakEvent = new BlockBreakEvent(b, player);
							Bukkit.getServer().getPluginManager().callEvent(bBreakEvent);
							
							if(bBreakEvent.isCancelled()) continue;
							
							Collection<ItemStack> stacks = b.getDrops();
							
							if(b.getType() == Material.ANCIENT_DEBRIS)
							{
								stacks = new ArrayList<ItemStack>();
								stacks.add(new ItemStack(Material.ANCIENT_DEBRIS));
							}
							
							for(ItemStack stack : stacks)
							{
								Metods._ins.InventoryAddItemOrDrop(Metods._ins.FortuneSimulation(stack, FortuneLevel), player);
							}
							
							
							
							b.setType(Material.OBSIDIAN);
						}
					}
				}.runTask(DontLoseItems.Instance);
			}
		}.runTaskAsynchronously(DontLoseItems.Instance);
		
		
		
		
	}
	
	public void OnHellShieldReflect(LivingEntity reflecterEntity, Entity target,Arrow arrow, ITEM_RARITY rarity )
	{
		ReflectArrowController.OnArrowInit(reflecterEntity, target, arrow, rarity);
	}
	
	
	
	public boolean IsHellBoots(ItemStack stack)
	{
		return Metods._ins.getPersistenData(stack, _PD_HELL_BOOTS, PersistentDataType.INTEGER) != null;
	}
	
	public boolean IsHellLeggins(ItemStack stack)
	{
		return Metods._ins.getPersistenData(stack, _PD_HELL_LEGGINS, PersistentDataType.INTEGER) != null;
	}
	
	public boolean IsHellChestplate(ItemStack stack)
	{
		return Metods._ins.getPersistenData(stack, _PD_HELL_CHESTPLATE, PersistentDataType.INTEGER) != null;
	}
	
	public boolean IsHellHelmet(ItemStack stack)
	{
		return Metods._ins.getPersistenData(stack, _PD_HELL_HELMET, PersistentDataType.INTEGER) != null;
	}
	
	public boolean IsHellTorch(ItemStack stack)
	{
		return Metods._ins.getPersistenData(stack, _PD_HELL_TORCH, PersistentDataType.INTEGER) != null;
	}
	
	public ITEM_RARITY GetRarity(ItemStack stack)
	{
		return RarityItem.GetRarity(stack);
	}
	
	public double GetFearReduceAmount(ItemStack stack)
	{
		Double x = Metods._ins.getPersistenData(stack, _PD_HELL_FEAR_REDUCE, PersistentDataType.DOUBLE);
		
		if(x != null) return x;
		
		return 0;
	}
	
	public boolean IsPlayerDebris(Block block)
	{	
		return Metods._ins.HasMetaData(block, _PD_BLOCK_DEBRIS);
	}
	
	public void SetPlayerDebrisBlock(Block block)
	{
		Metods._ins.SetMetaData(block, _PD_BLOCK_DEBRIS, 1);
	}
	
	public void RemovePlayerDebris(Block block)
	{
		Metods._ins.RemoveMetaData(block, _PD_BLOCK_DEBRIS);
	}
	
	public boolean IsHellArrow(ItemStack stack)
	{
		return (GetHellArrow(stack) == 0 ? false : true);
	}
	
	public boolean IsHellReflectShield(ItemStack stack)
	{
		return Metods._ins.getPersistenData(stack, _PD_HELL_REFLECT_SHIELD, PersistentDataType.INTEGER) != null;
	}
	
	public int GetHellArrow(ItemStack stack)
	{
		Integer x = Metods._ins.getPersistenData(stack, _PD_HELL_ARROW, PersistentDataType.INTEGER);
		
		if(x != null) return x;
		
		return 0;
	}
	
	public void SetHellArrow(ItemStack arrow, Entity entity, boolean reduceStackAmount)
	{
		Integer radius = Manager_HellArmor.Instance.GetHellArrow(arrow);

        if(radius <= 0) return;
        
        if(reduceStackAmount) arrow.setAmount(arrow.getAmount()-1);
        
        Metods._ins.setPersistenData(entity, _PD_HELL_ARROW, PersistentDataType.INTEGER, radius);
        Metods._ins.setPersistenData(entity, _PD_HELL_TIER, PersistentDataType.STRING, GetRarity(arrow).toString());
	}
	
	public Integer GetHellArrow(Entity entity)
	{
		Integer x = Metods._ins.getPersistenData(entity, _PD_HELL_ARROW, PersistentDataType.INTEGER);
		
		if(x == null || x <= 0) return 0;
		
		return x;
		
	}
	
	public ITEM_RARITY GetHellArrowTier(Entity entity)
	{
		String x = Metods._ins.getPersistenData(entity, _PD_HELL_TIER, PersistentDataType.STRING);
		
		if(x == null ) return null;
		
		return ITEM_RARITY.valueOf(x);
		
	}
	
	@SuppressWarnings("unused")
	private boolean IsNether(World world)
	{
		if(world == null) return false;
		
		return world == _nether;
	}

	@SuppressWarnings("unused")
	private boolean IsNether(Entity entity)
	{
		if(entity == null) return false;
		
		return entity.getWorld() == _nether;
	}

	@SuppressWarnings("unused")
	private boolean IsNether(Block block)
	{
		if(block == null) return false;
		
		return block.getWorld() == _nether;
	}

	@SuppressWarnings("unused")
	private boolean IsNether(Location loc)
	{
		if(loc == null) return false;
		
		return loc.getWorld() == _nether;
	}
	
	
}