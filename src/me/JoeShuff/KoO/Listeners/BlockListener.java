package me.JoeShuff.KoO.Listeners;

import java.util.Random;

import me.JoeShuff.KoO.KoOUHC;
import me.JoeShuff.KoO.VisualEffects;
import me.JoeShuff.KoO.DataTracker.DataTracker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.conversations.PlayerNamePrompt;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {
	
	public BlockListener(KoOUHC plugin)
	{
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
	
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(16.0D);
		
		VisualEffects.sendTitle(player,ChatColor.GREEN + "WELCOME", "" + ChatColor.YELLOW + ChatColor.ITALIC + player.getName());
		VisualEffects.sendTabList(player, ChatColor.BLUE + "Kingdom " + ChatColor.WHITE + "of " + ChatColor.GOLD + "Ores " + ChatColor.RED + "UHC", ChatColor.GOLD + "by " + ChatColor.GREEN + "Dractus");
		
		if (!KoOUHC.UHCLive && !KoOUHC.UHCprepped && Bukkit.getWorld(KoOUHC.hubWorldName) != null)
		{
			Location loc = new Location(Bukkit.getWorld(KoOUHC.hubWorldName), KoOUHC.hubCentreX, KoOUHC.hubCentreY, KoOUHC.hubCentreZ);
			
			player.teleport(loc);
		}
	}
	
	@EventHandler
	public void breakBlock(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		
		if (player.getWorld().getName().equals(KoOUHC.hubWorldName))
		{
			event.setCancelled(true);
			return;
		}
		
		DataTracker.playerInfo.get(player.getName()).minedBlock(event.getBlock().getType());
		
		if (!player.hasPermission("blockBefore.allowed"))
		{
			player.sendMessage(ChatColor.RED + "Unable to destroy block before UHC started!");
			event.setCancelled(true);
			return;
		}
		
		if (event.getBlock().getType() == Material.LEAVES || event.getBlock().getType() == Material.LEAVES_2)
		{
			if (new Random().nextInt(100) <= KoOUHC.APPLE_RATE - 1)
			{
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE, 1));
			}
		}
	}
	
	@EventHandler
	public void blockChange(LeavesDecayEvent event)
	{
		if (event.getBlock().getType() == Material.LEAVES || event.getBlock().getType() == Material.LEAVES_2)
		{
			if (new Random().nextInt(100) <= KoOUHC.APPLE_RATE - 1)
			{
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE, 1));
			}
		}
	}
	
	@EventHandler
	public void entDeath(EntityDeathEvent event)
	{
		if (event.getEntityType() == EntityType.ENDERMAN)
		{
			if (new Random().nextInt(100) <= KoOUHC.PEARL_RATE - 1)
			{
				event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.ENDER_PEARL, 1));
			}
		}
	}
	
	@EventHandler
	public void placeBlock(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		
		if (player.isOp())
		{
			return;
		}
		
		if (player.getWorld().getName().equals(KoOUHC.hubWorldName))
		{
			if (event.getBlock().getType() != Material.WOOD_BUTTON)
			{
				event.setCancelled(true);
				return;
			}
		}
		else
		{
			if (!player.hasPermission("blockBefore.allowed"))
			{
				player.sendMessage(ChatColor.RED + "Unable to place block before UHC started!");
				event.setCancelled(true);
			}
		}	
	}

	@EventHandler
	public void brewingStand(BrewEvent event)
	{
		boolean glowstone = false;
		boolean strength1 = false;
		
		boolean ghasttear = false;
		
		for (ItemStack items : event.getContents().getContents())
		{
			if (items.getType() == Material.POTION && (items.getDurability() == 8265 || items.getDurability() == 8201 || items.getDurability() == 16393 || items.getDurability() == 16457))
			{
				strength1 = true;
			}
			
			if (items.getType() == Material.GLOWSTONE_DUST)
			{
				glowstone = true;
			}
			
			if (items.getType() == Material.GHAST_TEAR)
			{
				ghasttear = true;
			}
		}
		
		if ((strength1 && glowstone) || ghasttear)
		{
			event.setCancelled(true);
		}
	}
	
	
	@EventHandler
	public void damage(EntityDamageEvent event)
	{
		if (!(event.getEntity() instanceof Player))
		{
			return;
		}
		
		Player player = (Player) event.getEntity();
		
		if (player.getWorld().getName().equals(KoOUHC.hubWorldName))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void foodChange(FoodLevelChangeEvent event)
	{
		if (!(event.getEntity() instanceof Player))
		{
			return;
		}
		
		Player player = (Player) event.getEntity();
		player.setCustomName("");
		//player.setCustomNameVisible(false);
		
		if (player.getWorld().getName().equals(KoOUHC.hubWorldName))
		{
			event.setCancelled(true);
		}
	}
}
