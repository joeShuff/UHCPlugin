package me.JoeShuff.KoO.Timers;

import java.util.ArrayList;
import java.util.List;

import me.JoeShuff.KoO.KoOUHC;
import me.JoeShuff.KoO.Listeners.PlayerListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

public class tpTimer extends BukkitRunnable {

	private final JavaPlugin plugin;
	//Declares your plugin variable
	
	private boolean teams = false;
	
	List<Location> Locs;
	
	List<Team> Teams = new ArrayList<Team>();
	
	List<Player> onPlayers = new ArrayList<Player>();
	
	int Seconds = 0;
	
	int slot = 0;
	
	public tpTimer(JavaPlugin plugin, boolean teams, List<Location> Locations,List<Player> online) 
	{
		this.plugin = plugin;
		this.teams = teams;

		Locs = Locations;
		
		Teams.addAll(plugin.getServer().getScoreboardManager().getMainScoreboard().getTeams());
		
		onPlayers = online;
		
		for (Player p : online)
		{
			PlayerListener.playingList.add(p.getName());
		}
		
		for (World world : Bukkit.getWorlds())
		{
			world.setDifficulty(Difficulty.PEACEFUL);
		}
	}
	
	@Override
	public void run() {
		
		if (Seconds == 1)
		{
			if (teams == true)
			{
				plugin.getServer().broadcastMessage(ChatColor.YELLOW + "Prepping Teleport for " + ChatColor.RED + Teams.get(slot).getDisplayName());
			}
			else
			{
				plugin.getServer().broadcastMessage(ChatColor.YELLOW + "Prepping Teleport for " + ChatColor.RED + onPlayers.get(slot).getName());
			}
		}
		if (Seconds == 3)
		{
			plugin.getServer().broadcastMessage(ChatColor.GRAY + "Loading Chunks...");
			Bukkit.getWorld(KoOUHC.UHCWorldName).setSpawnLocation((int)Locs.get(slot).getX(),(int)Locs.get(slot).getY(),(int)Locs.get(slot).getZ());
		}
		
		if (Seconds == 10)
		{
			if (teams == true)
			{
				for (Player player : plugin.getServer().getOnlinePlayers())
				{
					if (plugin.getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(player) != null)
					{
						if (plugin.getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(player).getName().equals(Teams.get(slot).getName()))
						{
							player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,1000000,100));
							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,1000000,100));
							player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,1000000,-100));
							player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,1000000,256));
							player.teleport(Locs.get(slot));
							plugin.getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.WHITE + " has been teleported");
						}
					}		
				}
			}
			else
			{
				onPlayers.get(slot).teleport(Locs.get(slot));
				onPlayers.get(slot).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,1000000,100));
				onPlayers.get(slot).addPotionEffect(new PotionEffect(PotionEffectType.SLOW,1000000,100));
				onPlayers.get(slot).addPotionEffect(new PotionEffect(PotionEffectType.JUMP,1000000,-100));
				onPlayers.get(slot).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,1000000,256));
				plugin.getServer().broadcastMessage(ChatColor.YELLOW + onPlayers.get(slot).getName() + ChatColor.WHITE + " has been teleported");
			}
			slot ++;
			Seconds = 0;
		}
		
		if (Locs.size() == slot)
		{
			plugin.getServer().broadcastMessage(ChatColor.GREEN + "All players have been teleported");
			Bukkit.getWorld(KoOUHC.UHCWorldName).setSpawnLocation(0, 0, 0);
			this.cancel();
		}
		
		Seconds ++;
	}

}
