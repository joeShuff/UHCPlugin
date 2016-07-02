package me.JoeShuff.KoO;

import java.util.ArrayList;
import java.util.List;

import me.JoeShuff.KoO.Listeners.PlayerListener;
import me.JoeShuff.KoO.Timers.VictoryTimer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class Positions {
	
	/*
	 * This class controls the teams and finishing positions of the teams.
	 * Everytime a player dies, it checks to see if that was the final member
	 * of a team alive and organises the teams to who comes, first, second, third etc...
	 */
	
	public static List<String> aliveTeams = new ArrayList<String>();
	
	private static Boolean teams = null;
	
	public static void initialise(Boolean teams)
	{
		Positions.teams = teams;
		
		aliveTeams = new ArrayList<String>();
		
		if (teams)
		{
			for (Team team : KoOUHC.getOnlineTeams())
			{
				aliveTeams.add(team.getName());
			}
		}
		else
		{
			for (Player player : Bukkit.getServer().getOnlinePlayers())
			{
				aliveTeams.add(player.getName());
			}
		}
		
	}
	
	/*
	 * This method is called everytime a player dies. It checks to see if a team has now been eliminated.
	 * If the amount of teams left is less that 3, then the first, second and third positions will start filling up
	 */
	public static void checkTeams(KoOUHC plugin)
	{
		if (teams == null)
		{
			return;
		}
		
		if (teams)
		{
			for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams())
			{
				if (allDead(team))
				{
					if (aliveTeams.contains(team.getName()))
					{
						aliveTeams.remove(team.getName());
						Bukkit.getServer().broadcastMessage("" + ChatColor.RED + ChatColor.BOLD + "TEAM " + team.getPrefix() + team.getDisplayName() + ChatColor.RED + " HAS BEEN ELIMINATED!");
						
						if (aliveTeams.size() == 2)
						{
							KoOUHC.ThirdPlace = team.getPrefix() + team.getDisplayName();
							Bukkit.getServer().broadcastMessage("" + team.getPrefix() + team.getDisplayName() + ChatColor.GREEN + " have finished third!");
						}
						else if (aliveTeams.size() == 1)
						{
							KoOUHC.SecondPlace = team.getPrefix() + team.getDisplayName();
							Bukkit.getServer().broadcastMessage("" + team.getPrefix() + team.getDisplayName() + ChatColor.GREEN + " have finished second!");
							try
							{
								Team t = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam(aliveTeams.get(0));
								KoOUHC.FirstPlace = t.getPrefix() + t.getDisplayName();
								Bukkit.getServer().broadcastMessage("" + t.getPrefix() + t.getDisplayName() + ChatColor.GREEN + " have finished first!");
							} catch (Exception ex)
							{
								
							}	
							
							new VictoryTimer(plugin, true, aliveTeams.get(0));
						}
					}
				}
			}
		}
		else
		{
			for (Player player : Bukkit.getOnlinePlayers())
			{
				if (PlayerListener.deadList.contains(player.getName()) && PlayerListener.playingList.contains(player.getName()))
				{
					if (aliveTeams.contains(player.getName()))
					{
						aliveTeams.remove(player.getName());
						
						Team playerTeam = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
						String prefix = "";
						
						if (playerTeam != null)
						{
							prefix = playerTeam.getPrefix();
						}
						
						Bukkit.getServer().broadcastMessage("" + ChatColor.RED + ChatColor.BOLD + "PLAYER " + prefix + player.getName()+ ChatColor.RED + ChatColor.BOLD + " HAS BEEN ELIMINATED!");
					
						if (aliveTeams.size() == 2)
						{
							KoOUHC.ThirdPlace = prefix + player.getName();
							Bukkit.getServer().broadcastMessage("" + prefix + player.getName() + ChatColor.GREEN + " has finished third!");
						}
						else if (aliveTeams.size() == 1)
						{
							KoOUHC.SecondPlace = prefix + player.getName();
							Bukkit.getServer().broadcastMessage("" + prefix + player.getName() + ChatColor.GREEN + " has finished second!");
							try
							{
								String firstPrefix = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getEntryTeam(aliveTeams.get(0)).getPrefix();
								KoOUHC.FirstPlace = firstPrefix + aliveTeams.get(0);
								Bukkit.getServer().broadcastMessage("" + prefix + player.getName() + ChatColor.GREEN + " has finished first!");
							} catch (Exception ex)
							{
								Bukkit.getServer().broadcastMessage(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " has finished first!");
							}	
							
							new VictoryTimer(plugin, false, aliveTeams.get(0));
						}
					}
				}
			}
		}
	}
	
	/*
	 * This method takes a team as a parameter and checks if all the players on that team have died in the UHC.
	 * 
	 * It returns true if the whole team died.
	 */
	private static boolean allDead(Team team)
	{
		for (OfflinePlayer player : team.getPlayers())
		{
			Boolean allDead = true;
			
			if (!PlayerListener.deadList.contains(player.getName()) && PlayerListener.playingList.contains(player.getName()))
			{
				return false;
			}
		}

		return true;
	}
}
