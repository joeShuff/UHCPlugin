package me.JoeShuff.KoO.DataTracker;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;

import me.JoeShuff.KoO.KoOUHC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class DataTracker {

	private static boolean firstDamage = false;
	
	public static String tableName = "";
	
	public static HashMap<String, PlayerInfo> playerInfo = new HashMap<String, PlayerInfo>();
	
	public static void init(KoOUHC plugin)
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			playerInfo.put(p.getName(), new PlayerInfo(p.getName()));
		}
		
		tableName = plugin.getConfig().getString("table-name");
		
	}
	
	public static void uploadAllData()
	{
		getWalkStats();
		
		Connection conn = MySQL.getConnection();
		Statement s = null;
		
		try
		{
			s = conn.createStatement();
			
			s.executeUpdate("TRUNCATE `" + tableName + "`");
			
			for (PlayerInfo p : playerInfo.values())
			{
				String update = "INSERT INTO `" + tableName + "` (`playername`,`damage`,`kills`,`killer`,`gapples-eaten`,`blocks-placed`,`blocks-broken`,`pve-kills`,`distance`,`firstDamage`,`ironMan`) VALUES ('&name','&dam','&kills','&killer','&gapples','&placed','&broken','&pve','&distance','&first','&iron');";
				
				update.replace("&name", p.playername).replace("&dam", "" + p.damage).replace("&kills", "" + p.kills).replace("&killer", p.killer).replace("&gapples", "" + p.gapples).replace("&placed", "" + p.block_placed).replace("&broken", "" + p.block_broken).replace("&distance", "" + p.distance).replace("&first", "" + p.first_damage).replace("&iron", "" + p.ironMan);
				
				s.executeUpdate(update);
			}
		}
		catch (Exception e)
		{
			
		}
		finally
		{
			try {s.close();} catch (Exception e){}
		}
		
		Bukkit.broadcastMessage("" + ChatColor.GOLD + ChatColor.BOLD + "All the UHC data has been uploaded!");
	}
	
	private static void getWalkStats()
	{
		Objective obj = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getObjective("walking");
		
		for (String p : playerInfo.keySet())
		{
			Score s  = obj.getScore(p);
			playerInfo.get(p).distance = s.getScore() / 100;
		}
		
	}
	
	public static class PlayerInfo {
		
		private String playername = "";
		private int damage = 0;
		private int kills = 0;
		private String killer = "No-one";
		private int gapples = 0;
		private int block_placed = 0;
		private int block_broken = 0;
		private int distance = 0;
		private int pveKills = 0;
		
		private int coal_Mined = 0;
		private int iron_Mined = 0;
		private int gold_Mined = 0;
		private int diamond_Mined = 0;
		private int lapis_Mined = 0;
		private int emerald_Mined = 0;
		private int redstone_Mined = 0;
		
		
		private boolean first_damage = false;
		private boolean ironMan = false;
		
		public PlayerInfo(String playername)
		{
			this.playername = playername;
		}
		
		public void tookDamage(Double damage)
		{
			this.damage += damage.intValue();
			
			if (!DataTracker.firstDamage)
			{
				this.first_damage = true;
				DataTracker.firstDamage = true;
			}
			
			String lastPlayer = "";
			int noDamCount = 0;
			for (String p : DataTracker.playerInfo.keySet())
			{
				if (DataTracker.playerInfo.get(p).damage == 0)
				{
					noDamCount ++;
					lastPlayer = p;
				}
			}
			
			if (noDamCount == 1)
			{
				DataTracker.playerInfo.get(lastPlayer).ironMan = true;
			}
		}
		
		public void gotKill()
		{
			this.kills ++;
		}
		
		public void killed(String killer)
		{
			this.killer = killer;
		}
		
		public void gappleConsumed()
		{
			this.gapples ++;
		}
		
		public void blockPlaced()
		{
			this.block_placed ++;
		}
		
		public void blockBroken()
		{
			this.block_broken ++;
		}
		
		public void minedBlock(Material mat)
		{
			if (mat == Material.COAL_ORE)
			{
				coal_Mined ++;
			}
			else if (mat == Material.IRON_ORE)
			{
				iron_Mined ++;
			}
			else if (mat == Material.DIAMOND_ORE)
			{
				diamond_Mined ++;
			}
			else if (mat == Material.GOLD_ORE)
			{
				gold_Mined ++;
			}
			else if (mat == Material.LAPIS_ORE)
			{
				lapis_Mined ++;
			}
			else if (mat == Material.EMERALD_ORE)
			{
				emerald_Mined ++;
			}
			else if (mat == Material.REDSTONE_ORE)
			{
				redstone_Mined ++;
			}
		}
		
		public void entityKill()
		{
			pveKills ++;
		}
	}
	
}
