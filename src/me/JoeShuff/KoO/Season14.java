package me.JoeShuff.KoO;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.inventivetalent.glow.GlowAPI;

public class Season14 {

	/*
	 * This class is to create the gamemode for Season 14 of KoO UHC
	 */
	
	private static List<String> captains = new ArrayList<String>();
	
	private static KoOUHC plugin;
	
	private static int teams = 0;
	private static int team_size = 0;
	
	public static void start(KoOUHC plugin)
	{
		Season14.plugin = plugin;
		
		team_size = plugin.getConfig().getInt("team-size");
		
		double d_teams = Math.ceil(plugin.getServer().getOnlinePlayers().size() / team_size);
		teams = Double.valueOf(d_teams).intValue();
		
		Bukkit.broadcastMessage(ChatColor.GOLD + "Randomly Assigning " + teams + " captains...");
		
		List<String> players = new ArrayList<String>();
		
		for (Player p : plugin.getServer().getOnlinePlayers())
		{
			if (p.getGameMode() == GameMode.SURVIVAL)
			{
				players.add(p.getName());
			}
		}
		
		for (int i = 0; i < teams; i ++)
		{
			int selector = new Random().nextInt(players.size());
			
			captains.add(players.get(selector));
			players.remove(selector);
		}
		
		int teamID = 0;
		for (String captain : captains)
		{
			plugin.getServer().getScoreboardManager().getMainScoreboard().registerNewTeam("Team" + teamID);
			Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam("Team" + teamID);
			
			team.addEntry(captain);
			team.setPrefix(plugin.getTeamColor(false));
			
			Bukkit.broadcastMessage(team.getPrefix() + captain + " has been chosen a captain!");
			
			Bukkit.getPlayer(captain).sendMessage(ChatColor.YELLOW + "Use /teamname to add a team name");
			
			teamID ++;
		}
	}
	
	/*
	 * This method is called whenever a player dies
	 */
	public static void death(Player p)
	{
		if (!KoOUHC.season14)
		{
			return;
		}
		
		if (captains.contains(p.getName()))
		{
			Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().getEntryTeam(p.getName());
			
			if (team.getEntries().size() == 1)
			{
				List<String> players = new ArrayList<String>();
				
				for (Player player : plugin.getServer().getOnlinePlayers())
				{
					if (player.getGameMode() == GameMode.SURVIVAL)
					{
						players.add(player.getName());
					}
				}
				
				for (String captain : captains)
				{
					players.remove(captain);
				}
				
				captains.remove(p.getName());
				
				List<String> toremove = new ArrayList<String>();
				for (String s : players)
				{
					if (plugin.getServer().getScoreboardManager().getMainScoreboard().getEntryTeam(s) != null)
					{
						toremove.add(s);
					}
				}
				
				for (String s : toremove)
				{
					players.remove(s);
				}
				
				int selector = new Random().nextInt(players.size());
				
				captains.add(players.get(selector));
				Bukkit.broadcastMessage(team.getPrefix() + players.get(selector) + " has been chosen a replacement captain!");
				
				Bukkit.getPlayer(players.get(selector)).sendMessage(ChatColor.YELLOW + "Use /teamname to add a team name");
				
				team.addEntry(players.get(selector));
			}
		}
	}
	
	public static void tick()
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			if (p.getGameMode() != GameMode.SURVIVAL)
			{
				continue;
			}
			
			if (Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(p.getName()) != null)
			{
				continue;
			}
			
			for (String captain : captains)
			{
				if (captain.equals(p.getName()))
				{
					continue;
				}
				
				if (Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(captain).getEntries().size() >= team_size)
				{
					continue;
				}
				
				if (Bukkit.getPlayer(captain) != null)
				{
					if (Bukkit.getPlayer(captain).getLocation().distance(p.getLocation()) < plugin.getConfig().getInt("season14-radius"))
					{
						Team t = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(captain);
						
						t.addEntry(p.getName());
						
						p.sendMessage(t.getPrefix() + "You have joined " + captain + "'s team");
						Bukkit.broadcastMessage(ChatColor.YELLOW + p.getName() + ChatColor.WHITE + " has joined " + t.getPrefix() + captain + "'s team");
					}
				}
			}
		}
	}
}
