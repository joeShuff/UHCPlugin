package me.JoeShuff.KoO.Timers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import me.JoeShuff.KoO.KoOUHC;
import me.JoeShuff.KoO.VisualEffects;

import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class timerClass extends BukkitRunnable {

	private KoOUHC plugin;
	//Declares your plugin variable
	
	private int startSecs = 10;
	
	private int episodeLength;
	
	private int seconds = 0;
	private int minutes = 0;
	
	private int episode = 1;
	
	private boolean permaDay = false;
	private int permaDayEp = 1;
	private boolean shrink = true;
	private int shrinkSize = 0;
	private int shrinkEp = 6;
	private int shrinkTime = 20;
	
	public timerClass(JavaPlugin plugin, int episodeLength,boolean permaDayS,int permaDayEpS,boolean shrinkS,int shrinkSizeS,int shrinkEpS, int shrinkTime) 
	{
		this.plugin = (KoOUHC) plugin;
		this.episodeLength = episodeLength;
		this.permaDay = permaDayS;
		this.permaDayEp = permaDayEpS;
		this.shrink = shrinkS;
		this.shrinkSize = shrinkSizeS;
		this.shrinkEp = shrinkEpS;
		this.shrinkTime = shrinkTime;
	}
	//This is called from your main class and sets your plugin variable
	
	public timerClass(KoOUHC plugin, Boolean force, int episode, int mins, int seconds, int epL)
	{
		startSecs = -1;
		minutes = mins;
		this.seconds = seconds;
		this.episode = episode;
		this.plugin = plugin;
		this.episodeLength = epL;
		
		this.runTaskTimer(plugin, 20, 20);
	}
	
	List<PotionEffect> effects = Arrays.asList(new PotionEffect(PotionEffectType.SPEED, 1000000, 1, false, false), new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 1, false, false), new PotionEffect(PotionEffectType.JUMP, 1000000, 2, false, false), new PotionEffect(PotionEffectType.HEALTH_BOOST, 1000000, 4, false, false), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 0, false, false));
	
	int prev = 0;
	
	@Override
	public void run() {
		
		KoOUHC.updatePlayerHealth();
		
		if (KoOUHC.UHCLive == false)
		{
			Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "===============================\n \n" + ChatColor.RED + ChatColor.BOLD + "THE UHC HAS BEEN STOPPED!" + ChatColor.RESET + ChatColor.DARK_RED + "\n \n===============================");

			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "====== " + ChatColor.BLUE + "KINGDOM " + ChatColor.WHITE + "OF" + ChatColor.GOLD + " ORES" + ChatColor.GOLD + " UHC" + ChatColor.GOLD + " ======");
			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "    1st Place : " + KoOUHC.FirstPlace);
			Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "    2nd Place : " + KoOUHC.SecondPlace);
			Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "    3rd Place : " + KoOUHC.ThirdPlace);
			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "===============================");
			
			KoOUHC.FirstPlace = "";
			KoOUHC.SecondPlace = "";
			KoOUHC.ThirdPlace = "";
			
			for (Player player : Bukkit.getServer().getOnlinePlayers())
			{
				VisualEffects.sendTabList(player, ChatColor.BLUE + "Kingdom " + ChatColor.WHITE + "of " + ChatColor.GOLD + "Ores " + ChatColor.RED + "UHC", ChatColor.GOLD + "by " + ChatColor.GREEN + "Dractus");
				player.getInventory().clear();
				player.getInventory().setBoots(null);
				player.getInventory().setLeggings(null);
				player.getInventory().setHelmet(null);
				player.getInventory().setChestplate(null);
			}
			
			for (Player player1 : Bukkit.getServer().getOnlinePlayers())
			{
				for (Player player2 : Bukkit.getServer().getOnlinePlayers())
				{
					player2.showPlayer(player1);
				}
			}
			
			this.cancel();
			return;
		}
		
		if (startSecs > -1)
		{
			if (startSecs >= 6)
			{
				for (Player player : this.plugin.getServer().getOnlinePlayers())
				{
					VisualEffects.sendTitle(player,"", "" + ChatColor.DARK_RED + startSecs);
					player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
				}
			}
			else if (startSecs >= 4)
			{
				for (Player player : this.plugin.getServer().getOnlinePlayers())
				{
					VisualEffects.sendTitle(player,"", "" + ChatColor.RED + startSecs);
					player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
				}
			}
			else if (startSecs >=2)
			{
				for (Player player : this.plugin.getServer().getOnlinePlayers())
				{
					VisualEffects.sendTitle(player,"", "" + ChatColor.GOLD + startSecs);
					player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
				}
			}
			else if (startSecs > 0)
			{
				for (Player player : this.plugin.getServer().getOnlinePlayers())
				{
					VisualEffects.sendTitle(player,"", "" + ChatColor.YELLOW + startSecs);
					player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
				}
			}
			
			startSecs --;
			if (startSecs == -1)
			{
				this.plugin = this.plugin.getPlugin();
				
				for (Player player : this.plugin.getServer().getOnlinePlayers())
				{
					KoOUHC.showRules(player);
					
					VisualEffects.sendTitle(player,ChatColor.BLUE + "EPISODE " + ChatColor.GREEN + episode + ChatColor.BLUE + " MARKER", "");
					prev = (int) (Bukkit.getWorld(KoOUHC.UHCWorldName).getWorldBorder().getSize() / 2);
					player.setOp(false);
					player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 0.2F, 1);
					player.setHealth(20);
					player.setFoodLevel(20);
					
					 for (PotionEffect effect : player.getActivePotionEffects())
					 {
			            player.removePotionEffect(effect.getType());
			         }
					 
					 Random rnd = new Random();
					 
					 if (KoOUHC.season13)
					 {
						 player.addPotionEffect(effects.get(rnd.nextInt(effects.size())));
					 }
					 
					 player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 10));
					
					 this.plugin.getServer().getPluginManager().getPermission("blockBefore.allowed").setDefault(PermissionDefault.TRUE);
					 
					 for (World w : Bukkit.getWorlds())
					 {
						 w.setDifficulty(Difficulty.HARD);
						 
						 w.setTime(0);
						 
						 w.setGameRuleValue("doDaylightCycle", "true");
						 
						 w.setPVP(false);
					 }
					 					
					 player.removeAchievement(Achievement.OPEN_INVENTORY);
				}
			}
		}
		else
		{
			this.plugin = this.plugin.getPlugin();
			
			seconds ++;
			
			if (seconds % 10 == 0)
			{
				int size = (int) (Bukkit.getWorld(KoOUHC.UHCWorldName).getWorldBorder().getSize() / 2);
				
				if (size != prev)
				{
					for (Player player : Bukkit.getServer().getOnlinePlayers())
					{
						VisualEffects.sendActionBar(player, ChatColor.GOLD + "The world border is at +-" + ChatColor.BOLD + ChatColor.RED + size);
					}
					
					prev = size;
				}		
			}
			
			if (seconds == 60)
			{
				minutes ++;
				seconds = 0;
				
				if (minutes == episodeLength)
				{
					minutes = 0;
					episode ++;
					
					if (episode == 2 && plugin.getConfig().getBoolean("moles") == true)
					{
						Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "genmoles");
					}
					
					if (episode == 2 && KoOUHC.season13)
					{
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "teams " + plugin.getConfig().getInt("team-size"));
					}
					
					if (shrink == true && shrinkEp == episode)
					{
						this.plugin.getServer().broadcastMessage(ChatColor.GOLD + "=============================================================================");
						this.plugin.getServer().broadcastMessage(ChatColor.RED + "WORLD BORDER SHRINKING TO " + ChatColor.GREEN + shrinkSize + "x" + shrinkSize + " over " + shrinkTime + " minute(s)!");
						this.plugin.getServer().broadcastMessage(ChatColor.GOLD + "=============================================================================");
					}
					
					for (Player player : this.plugin.getServer().getOnlinePlayers())
					{
						
						player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
						
						if (episode == 2)
						{
							VisualEffects.sendTitle(player,ChatColor.BLUE + "EPISODE " + ChatColor.GREEN + episode + ChatColor.BLUE + " MARKER", ChatColor.GREEN + "PVP ENABLED");
							player.getWorld().setPVP(true);
						}
						else if (permaDay == true && episode == permaDayEp)
						{
							VisualEffects.sendTitle(player,ChatColor.BLUE + "EPISODE " + ChatColor.GREEN + episode + ChatColor.BLUE + " MARKER", ChatColor.GREEN + "PERMA-DAY ENABLED");

							for (World world : Bukkit.getWorlds())
							{
								world.setTime(0);
								world.setGameRuleValue("doDaylightCycle", "false");
							}
						}
						else
						{
							VisualEffects.sendTitle(player,ChatColor.BLUE + "EPISODE " + ChatColor.GREEN + episode + ChatColor.BLUE + " MARKER", "");
						}
						
						if (shrink == true)
						{
							if (episode == shrinkEp)
							{
								player.getWorld().getWorldBorder().setSize(shrinkSize, shrinkTime * 60);
							}
						}
					}
				}
			}
			
			for (Player player : this.plugin.getServer().getOnlinePlayers())
			{
				VisualEffects.sendTabList(player, ChatColor.BLUE + "Kingdom " + ChatColor.WHITE + "of " + ChatColor.GOLD + "Ores " + ChatColor.RED + "UHC", ChatColor.GOLD + "Episode " + ChatColor.RED + episode + ChatColor.GOLD + " | " + ChatColor.YELLOW + minutes + ChatColor.BLUE + "m " + ChatColor.YELLOW + seconds + ChatColor.AQUA + "s");
			}
		}
	}
	
}
