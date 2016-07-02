package me.JoeShuff.KoO.Listeners;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.JoeShuff.KoO.KoOUHC;
import me.JoeShuff.KoO.Positions;
import me.JoeShuff.KoO.Season14;
import me.JoeShuff.KoO.DataTracker.DataTracker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class PlayerListener implements Listener {
	
	public static List<String> deadList = new ArrayList<String>();
	public static List<String> playingList = new ArrayList<String>();
	
	private KoOUHC plugin;
	
	public PlayerListener(KoOUHC plugin)
	{
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent event)
	{	
		Player player = event.getEntity();
		
		Location spawnLoc = player.getLocation();
		
		if (spawnLoc.getY() < 0)
		{
			spawnLoc.setY(20);
		}
		
		Season14.death(player);
		
		player.setBedSpawnLocation(spawnLoc, true);
		
		player.setGameMode(GameMode.SPECTATOR);
		
		if (KoOUHC.DEATH_LIGHTNING)
		{
			player.getWorld().strikeLightningEffect(player.getLocation());
		}
		
		if (Boolean.valueOf(plugin.getConfig().getBoolean("can-spectate")) == false)
		{
			if (plugin.getServer().getWorld(KoOUHC.hubWorldName) != null)
			{
				player.setGameMode(GameMode.ADVENTURE);
				player.teleport(KoOUHC.getHubSpawn());
			}
		}
		
		String killer = "Entity";
		
		if (event.getEntity().getKiller() == null)
		{			
			Objective objective = player.getServer().getScoreboardManager().getMainScoreboard().getObjective("kills");
			
			Score score = objective.getScore("" + ChatColor.AQUA + ChatColor.BOLD + "PvE:");
		
			int newscore = score.getScore() + 1;
			
			score.setScore(newscore);
		}
		else
		{
			killer = event.getEntity().getKiller().getName();
			
			try
			{
				DataTracker.playerInfo.get(event.getEntity().getKiller().getName()).gotKill();
			} catch(Exception e){}
		}
		
		DataTracker.playerInfo.get(player.getName()).killed(killer);
		
		for (Player p : Bukkit.getOnlinePlayers())
		{
			if (deadList.contains(p.getName()))
			{
				player.showPlayer(p);
			}
			else
			{
				p.hidePlayer(player);
			}
		}
		
		deadList.add(player.getName());
		try
		{
			Positions.checkTeams(plugin);
		} catch (Exception e){}
		
	}
	
	@EventHandler
	public void changeWorld(PlayerChangedWorldEvent event)
	{
		for (World w : Bukkit.getServer().getWorlds())
		{
			w.setGameRuleValue("naturalRegeneration", "false");
			w.setDifficulty(Difficulty.HARD);
		}
	}
	
	@EventHandler
	public void tpEvent(PlayerTeleportEvent event)
	{
		if (event.getCause() == TeleportCause.ENDER_PEARL)
		{
			if (KoOUHC.PEARL_DAMAGE == false)
			{
				event.setCancelled(true);
				event.getPlayer().teleport(event.getTo());
			}
		}
	}
	
	@EventHandler
	public void damage(EntityDamageEvent event)
	{	
		if (!(event.getEntity() instanceof Player))
		{
			return;
		}
		
		if (event.getCause() == DamageCause.FALL)
		{
			if (!KoOUHC.FALL_DAMAGE)
			{
				event.setCancelled(true);
				return;
			}
		}
		
		Player player = (Player) event.getEntity();
		
		try
		{
			DataTracker.playerInfo.get(player.getName()).tookDamage(event.getDamage());
		} catch(Exception e){}
	}
	
	@EventHandler
	public void eat(PlayerItemConsumeEvent event)
	{
		if (event.getItem().getType() == Material.GOLDEN_APPLE)
		{
			try 
			{
				DataTracker.playerInfo.get(event.getPlayer().getName()).gappleConsumed();
			} catch(Exception e){}
		}
	}
	
	@EventHandler
	public void placeBlock(BlockPlaceEvent event)
	{
		try
		{
			DataTracker.playerInfo.get(event.getPlayer().getName()).blockPlaced();
		} catch(Exception e){}
	}
	
	@EventHandler
	public void blockBreak(BlockBreakEvent event)
	{
		try
		{
			DataTracker.playerInfo.get(event.getPlayer().getName()).blockBroken();
		} catch(Exception e){}
	}
	
	@EventHandler
	public void entityDeath(EntityDeathEvent event)
	{
		if (!(event.getEntity() instanceof Player))
		{
			if (event.getEntity().getKiller() != null)
			{
				try
				{
					DataTracker.playerInfo.get(event.getEntity().getKiller().getName()).entityKill();
					
				} catch(Exception e){}
			}
		}
	}
	
	HashMap<Integer, Location> arrowShot = new HashMap<Integer, Location>();
	
	@EventHandler
	public void shoot(EntityShootBowEvent event)
	{
		if (!(event.getEntity() instanceof Player))
		{
			return;
		}
		
		arrowShot.put(event.getProjectile().getEntityId(), event.getEntity().getLocation());
	}
	
	@EventHandler
	public void arrowLand(ProjectileHitEvent event)
	{
		if (event.getEntity() instanceof Arrow)
		{
			arrowShot.remove(event.getEntity().getEntityId());
		}
	}
	
	@EventHandler
	public void entByEnt(EntityDamageByEntityEvent event)
	{		
		if (event.getDamager() instanceof Arrow)
		{
			if (event.getEntity() instanceof Player)
			{
				Arrow arrow = (Arrow) event.getDamager();
				
				if (arrow.getShooter() instanceof Player)
				{
					Player shooter = (Player) arrow.getShooter();
					double dist = event.getEntity().getLocation().distance(arrowShot.get(arrow.getEntityId()));
					
					DecimalFormat format = new DecimalFormat("####0.00");
					String distance = format.format(dist);
					
					if (dist > 25)
					{
						Bukkit.broadcastMessage(ChatColor.RED + shooter.getName() + " shot " + event.getEntity().getName() + " from " + distance + " blocks away!");
					}
					else
					{
						shooter.sendMessage(ChatColor.RED + "You shot " + event.getEntity().getName() + " from " + distance + " blocks away!");
					}
				}
				
			}
			
			arrowShot.remove(event.getDamager().getEntityId());
		}		
	}
}
