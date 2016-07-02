package me.JoeShuff.KoO;


import java.io.File;
import java.util.*;

import me.JoeShuff.KoO.Commands.EditTabCompleter;
import me.JoeShuff.KoO.DataTracker.DataTracker;
import me.JoeShuff.KoO.DataTracker.MySQL;
import me.JoeShuff.KoO.Listeners.BlockListener;
import me.JoeShuff.KoO.Listeners.PlayerListener;
import me.JoeShuff.KoO.Timers.timerClass;
import me.JoeShuff.KoO.Timers.tpTimer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class KoOUHC extends JavaPlugin {
	
	public static KoOUHC plugin;
	
	public Permission startedPermission = new Permission("blockBefore.allowed");
	
	//STORE ALL OF THE INFORMAION FOR THE HUB AND UHC WORLD
	public static String UHCWorldName = "";
	public static String hubWorldName = "";
	
	public static int hubCentreX = 0;
	public static int hubCentreY = 0;
	public static int hubCentreZ = 0;
	
	//DATA FOR THE PLAYTHROUGH OF THE UHC
	public static boolean UHCprepped = false;
	public static boolean UHCLive = false;
	
	//STORE THE TEAMNAMES OF THE TEAMS IN FIRST,SECOND AND THIRD
	public static String ThirdPlace = "ThirdPlace";
	public static String SecondPlace = "SecondPlace";
	public static String FirstPlace = "FirstPlace";
	
	//WHETHER OR NOT THE GAME IS BEING PLAYED IN TEAMS OR FFA
	public static Boolean teams;
	
	//WHETHER THE SEASON 13 GAMEMODE IS TO BE USED OR NOT
	public static Boolean season13 = false;
	
	//WHETHER THE SEASON 14 GAMEMODE IS TO BE USED OR NOT
	public static Boolean season14 = false;
	
	//====GAME DATA=====
	public static boolean FALL_DAMAGE = true;
	public static boolean PEARL_DAMAGE = true;
	
	public static double APPLE_RATE = 0.5;
	public static double PEARL_RATE = 0.5;
	
	public static boolean DEATH_LIGHTNING = false;
	//=================
	
	@Override
	public void onEnable()
	{
		this.plugin = this;
		
		File configFile = new File(this.getDataFolder(),"config.yml");
		if (!configFile.exists()) 
		{
		     try
		     {
		       this.saveResource("config.yml", true);
		       configFile.createNewFile();
		     }
		     catch (Exception e)
		     {
		       System.out.println("Couldn't create file config.yml");
		     }
		}
		
		PluginManager manager = getServer().getPluginManager();
		manager.addPermission(startedPermission);
		
		File rulesFile = new File(this.getDataFolder(),"rules.yml");
		if (!rulesFile.exists()) 
		{
		     try
		     {
		       this.saveResource("rules.yml", true);
		       rulesFile.createNewFile();
		     }
		     catch (Exception e)
		     {
		       System.out.println("Couldn't create file rules.yml");
		     }
		}
		
		getWorlds();
		
		//Stop the daylight cycle
		for (World world : Bukkit.getWorlds())
		{
			world.setGameRuleValue("doDaylightCycle", "false");
		}
		
		//INITALISE LISTENERS
		new BlockListener(this);
		
		teams = Boolean.valueOf(getConfig().getBoolean("teams"));
		
		season13 = Boolean.valueOf(getConfig().getBoolean("season13"));
		season14 = Boolean.valueOf(getConfig().getBoolean("season14"));
		
		FALL_DAMAGE = Boolean.valueOf(getConfig().getBoolean("fall-damage"));
		PEARL_DAMAGE = Boolean.valueOf(getConfig().getBoolean("pearl-damage"));
		DEATH_LIGHTNING = Boolean.valueOf(getConfig().getBoolean("death-lightning"));
		
		APPLE_RATE = getConfig().getDouble("apple-rate");
		PEARL_RATE = getConfig().getDouble("pearl-rate");
		
		getCommand("edituhc").setTabCompleter(new EditTabCompleter(this));
		
		for (Player player : Bukkit.getServer().getOnlinePlayers())
		{
			VisualEffects.sendTabList(player, ChatColor.BLUE + "Kingdom " + ChatColor.WHITE + "of " + ChatColor.GOLD + "Ores " + ChatColor.RED + "UHC", ChatColor.GOLD + "by " + ChatColor.GREEN + "Dractus");
		}
		
		DataTracker.init(this); 
		
		new MySQL("104.218.96.10", "MCS_10213", "MCS_10213", "9115d0fc06", this);
				
		getLogger().info("==================================");
		getLogger().info("Kingdom of Ores UHC Plugin Enabled");
		getLogger().info("==================================");
	}
	
	@Override
	public void onDisable()
	{
		
	}
	
	public KoOUHC getPlugin()
	{
		return this;
	}
	
	public static Location getHubSpawn()
	{
		return new Location(Bukkit.getWorld(hubWorldName), hubCentreX, hubCentreY, hubCentreZ);
	}
	
	private void getWorlds()
	{
		UHCWorldName = this.getConfig().getString("UHCWorld");
		
		if (UHCWorldName == null || UHCWorldName.equals(""))
		{
			getLogger().info("Can't find a world name for UHCWorld in the config");
		}
		
		hubWorldName = this.getConfig().getString("hubWorld");
		
		if (hubWorldName == null || hubWorldName.equals(""))
		{
			getLogger().info("Can't find a world name for hubWorld in the config");
		}
		
		try {
			hubCentreX = this.getConfig().getInt("hubCentreX");
			hubCentreY = this.getConfig().getInt("hubCentreY");
			hubCentreZ = this.getConfig().getInt("hubCentreZ");
		} catch (Exception e){
			getLogger().info("Can't find the hub centre from the config!");
		}
		
	}
	
	boolean[] alreadySelected = new boolean[48];
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("edituhc") && sender instanceof Player)
		{
			Player player = (Player) sender;
			
			if (!player.isOp())
			{
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!   ~Skrub");
				return true;
			}
			
			if (args.length == 0)
			{
				sender.sendMessage(ChatColor.RED + "Insufficient Arguments - /edituhc <rule> <value>");
				return true;
			}
			
			if (args.length == 1)
			{
				if (args[0].equalsIgnoreCase("episode-length") || args[0].equalsIgnoreCase("perma-day-ep") || args[0].equalsIgnoreCase("shrink-size") || args[0].equalsIgnoreCase("shrink-time") || args[0].equalsIgnoreCase("shrink-ep")|| args[0].equalsIgnoreCase("season14-radius"))
				{
					sender.sendMessage(ChatColor.RED + args[0] + ": " + ChatColor.GREEN + getConfig().getInt(args[0]));
				}
				else if (args[0].equalsIgnoreCase("perma-day") || args[0].equalsIgnoreCase("should-shrink") || args[0].equalsIgnoreCase("teams") || args[0].equalsIgnoreCase("moles") || args[0].equalsIgnoreCase("death-lightning") || args[0].equalsIgnoreCase("fall-damage") || args[0].equalsIgnoreCase("pearl-damage") || args[0].equalsIgnoreCase("season13") || args[0].equalsIgnoreCase("season14"))
				{
					sender.sendMessage(ChatColor.RED + args[0] + ": " + ChatColor.GREEN + getConfig().getBoolean(args[0]));
				}
				else if (args[0].equalsIgnoreCase("apple-rate") || args[0].equalsIgnoreCase("pearl-rate"))
				{
					sender.sendMessage(ChatColor.RED + args[0] + ": " + ChatColor.GREEN + getConfig().getDouble(args[0]));
				}
				else
				{
					sender.sendMessage(ChatColor.RED + args[0] + ": " + ChatColor.GREEN + getConfig().getString(args[0]));
				}		
			}
			
			if (args.length == 2)
			{	
				if (args[0].equalsIgnoreCase("episode-length") || args[0].equalsIgnoreCase("perma-day-ep") || args[0].equalsIgnoreCase("shrink-size") || args[0].equalsIgnoreCase("shrink-ep") || args[0].equalsIgnoreCase("shrink-time") || args[0].equalsIgnoreCase("season14-radius"))
				{
					getConfig().set(args[0], Integer.valueOf(args[1]));
				}
				else if (args[0].equalsIgnoreCase("perma-day") || args[0].equalsIgnoreCase("should-shrink") || args[0].equalsIgnoreCase("teams") || args[0].equalsIgnoreCase("moles") || args[0].equalsIgnoreCase("death-lightning") || args[0].equalsIgnoreCase("fall-damage") || args[0].equalsIgnoreCase("pearl-damage") || args[0].equalsIgnoreCase("season13") || args[0].equalsIgnoreCase("season14"))
				{
					if (args[0].equalsIgnoreCase("death-lightning"))
					{
						DEATH_LIGHTNING = Boolean.valueOf(args[1]);
					}
					else if (args[0].equalsIgnoreCase("fall-damage"))
					{
						FALL_DAMAGE = Boolean.valueOf(args[1]);
						
						if (FALL_DAMAGE == false)
						{
							PEARL_DAMAGE = false;
							getConfig().set("pearl-damage", false);
						}
						
					}
					else if (args[0].equalsIgnoreCase("pearl-damage"))
					{
						PEARL_DAMAGE = Boolean.valueOf(args[1]);
					}
					
					getConfig().set(args[0], Boolean.valueOf(args[1]));
				}
				else if (args[0].equalsIgnoreCase("apple-rate") || args[0].equalsIgnoreCase("pearl-rate"))
				{
					if (args[0].equalsIgnoreCase("apple-rate"))
					{
						APPLE_RATE = Double.valueOf(args[1]);
					}
					else
					{
						PEARL_RATE = Double.valueOf(args[1]);
					}
					
					getConfig().set(args[0], Double.valueOf(args[1]));
				}
				else
				{
					if (getConfig().get(args[0]) == null)
					{
						sender.sendMessage(ChatColor.RED + "That rule doesn't exist");
						return true;
					}
					getConfig().set(args[0], args[1]);
				}
				String rule = args[0];
				String[] rules = rule.split("-");
				rule = "";
				for (String s : rules)
				{
					rule = rule + s.substring(0, 1).toUpperCase() + s.substring(1) + " ";
				}
				rule = rule.trim();
				
				Bukkit.broadcastMessage("" + ChatColor.BLUE + ChatColor.BOLD + "Rule " + rule + " updated to : " + ChatColor.AQUA + args[1]);
				saveConfig();
			}
			
			getWorlds();
			
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("start-uhc") && sender instanceof Player)
		{
			Player player = (Player) sender;
			
			if (!player.isOp())
			{
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!   ~Skrub");
				return true;
			}
			
			if (UHCLive == true)
			{
				sender.sendMessage("UHC already in progress");
				return true;
			}
				
			boolean permaDay = getConfig().getBoolean("perma-day");
			int permaEp = getConfig().getInt("perma-day-ep");
				
			boolean shrink = getConfig().getBoolean("should-shrink");
			int shrinkSize = getConfig().getInt("shrink-size");
			int shrinkEp = getConfig().getInt("shrink-ep");
			int shrinkLength = getConfig().getInt("shrink-time");

			int episodeTime = getConfig().getInt("episode-length");
				
			UHCLive = true;
			
			new PlayerListener(this);	
			
			new timerClass(this,episodeTime,permaDay,permaEp,shrink,shrinkSize,shrinkEp,shrinkLength).runTaskTimer(this, 20, 20);
			getServer().broadcastMessage("UHC Started with Episode length of " + episodeTime + " minute(s)");
			getServer().broadcastMessage("Shrink at Episode : " + String.valueOf(shrinkEp) + " to " + String.valueOf(shrinkSize) + "x" + String.valueOf(shrinkSize) + " and will last " + String.valueOf(shrinkLength));
			for (Player p : Bukkit.getOnlinePlayers())
			{
				p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 10);
			}
				
			Objective obj = getServer().getScoreboardManager().getMainScoreboard().getObjective("walking");
			
			if (obj == null)
			{
				getServer().getScoreboardManager().getMainScoreboard().registerNewObjective("walking", "stat.walkOneCm");
			}
			else
			{
				obj.unregister();
				getServer().getScoreboardManager().getMainScoreboard().registerNewObjective("walking", "stat.walkOneCm");
			}
			
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("clearteams") && sender instanceof Player)
		{
			if (!(sender.isOp()))
			{
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!   ~Skrub");
				return true;
			}
			
			Set<Team> amountofteams = getServer().getScoreboardManager().getMainScoreboard().getTeams();
				
			int teams = amountofteams.size();
				
			for (int i = 1 ; i <= teams ; i ++)
			{
				Team findTeam = getServer().getScoreboardManager().getMainScoreboard().getTeam("team" + i);
					
				if (findTeam != null)
				{
					getServer().getScoreboardManager().getMainScoreboard().getTeam("team" + i).unregister();
				}
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("team") && sender instanceof Player)
		{
			if (args.length == 0)
			{
				sender.sendMessage(ChatColor.RED + "Insufficient Arguments - /team <player> <player> ...");
				return true;
			}
			
			List<Player> players = new ArrayList<Player>();
			
			players.addAll(getServer().getOnlinePlayers());
			
			int slot = 0;
			
			int playerAmount = args.length;
			
			boolean found = false;
			
			Player sentPlayer = (Player) sender;
			
			Team sentTeam = getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(sentPlayer);
			
			if (sentTeam == null)
			{
				for (int i = 1 ; i < i + 1; i++)
				{
					Team testTeam = getServer().getScoreboardManager().getMainScoreboard().getTeam("team" + i);
					
					if (testTeam == null)
					{
						Team addTeam = getServer().getScoreboardManager().getMainScoreboard().registerNewTeam("team" + i);
						addTeam.setPrefix(getTeamColor(false));
						addTeam.setSuffix("§r");
						addTeam.setDisplayName("Team" + i);
						addTeam.setCanSeeFriendlyInvisibles(true);
						addTeam.setAllowFriendlyFire(true);
						
						addTeam.addPlayer(sentPlayer);
						
						break;
					}
				}
			}
			
			do
			{
				found = false;
				
				for (Player player : players)
				{
					if (player.getName().equalsIgnoreCase(args[slot]))
					{
						found = true;
						
						Team getTeam = getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(sentPlayer);
						
						getTeam.addPlayer(player);
						
						sentPlayer.sendMessage(ChatColor.GREEN + player.getName() + " has joined your team!");
						player.sendMessage(ChatColor.GREEN + "You have joined " + sentPlayer.getName() + "'s Team");
						
						break;
					}	
				}
				
				if(found == false)
				{
					sentPlayer.sendMessage(ChatColor.RED + "Could not find player " + args[slot]);
				}
				
				slot ++;
				
			}while (slot < playerAmount);
			
			sentPlayer.sendMessage(ChatColor.GREEN + "Team created!");
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("teams"))
		{
			if (sender instanceof Player)
			{
				if (!(sender.isOp()))
				{
					sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!   ~Skrub");
					return true;
				}
			}
			
			if (teams(sender, args))
			{
				return true;
			}
			
			sender.sendMessage(ChatColor.RED + "Insufficient arguments - /teams <players per team>");
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("tchat") && sender instanceof Player)
		{
			Player player = (Player) sender;
			Team senderTeam = getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(player);
			
			if (senderTeam == null)
			{
				player.sendMessage(ChatColor.RED + "You are not on a team");
				return true;
			}
			
			if (args.length == 0)
			{
				player.sendMessage(ChatColor.RED + "Insufficient arguments - /tchat <message>");
				return true;
			}
			
			String message = args[0];
			
			if (args.length > 1)
			{
				for (int i = 1 ; i < args.length ; i ++)
				{
					message = message + " " + args[i];
				}
			}
			
			for (Player players : getServer().getOnlinePlayers())
			{
				if (getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(players) != null)
				{
					if (senderTeam.getName().equals(getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(players).getName()))
					{	
						if (players != player)
						{
							players.sendMessage(ChatColor.GREEN + "[TEAM] " + player.getName() + ChatColor.GRAY + " > " + ChatColor.WHITE + message );
						}
					}
				}
			}
			
			player.sendMessage(ChatColor.GRAY + "You said to teammates > " + ChatColor.WHITE + message);
			return true;
		}
		
		if ((cmd.getName().equalsIgnoreCase("tloc") || cmd.getName().equalsIgnoreCase("tl")) && sender instanceof Player)
		{
			Player player = (Player) sender;
			Team senderTeam = getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(player);
			
			if (senderTeam == null)
			{
				player.sendMessage(ChatColor.RED + "You are not on a team");
				return true;
			}
			
			String message = "X: " + (int) player.getLocation().getX() + " Y: " + (int) player.getLocation().getY() + " Z: " + (int) player.getLocation().getZ();
			
			for (Player players : getServer().getOnlinePlayers())
			{
				if (getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(players) != null)
				{
					if (senderTeam.getName().equals(getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(players).getName()))
					{
						if (players != player)
						{
							if (players.getWorld().getName().equals(player.getWorld().getName()))
							{
								players.sendMessage(ChatColor.GREEN + "[TEAM] " + player.getName() + ChatColor.GRAY + " > " + ChatColor.WHITE + message + " " + ChatColor.GREEN + "(" + ChatColor.WHITE + (int) player.getLocation().distance(players.getLocation()) + " blocks" + ChatColor.GREEN + ")");
							}		
							else
							{
								players.sendMessage(ChatColor.GREEN + "[TEAM] " + player.getName() + ChatColor.GRAY + " > " + ChatColor.WHITE + message + " " + ChatColor.GREEN + "(Different World)");
							}
						}
					}
				}
			}
			
			player.sendMessage(ChatColor.GRAY + "You said to teammates > " + ChatColor.WHITE + message);
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("prepuhc") && sender instanceof Player)
		{
			if (!sender.isOp())
			{
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
				return true;
			}
			
			Player Splayer = (Player) sender;
			
			World world =  Bukkit.getWorld(UHCWorldName); 
			
			if (world == null)
			{
				sender.sendMessage(ChatColor.RED + "Unable to find UHC world : " + UHCWorldName);
				return true;
			}
			
			if (args.length != 1)
			{
				sender.sendMessage(ChatColor.RED + "Insufficient Arguments - /prepuhc <worldborder diameter>");
				return true;
			}
			
			Scoreboard board = getServer().getScoreboardManager().getMainScoreboard();
			
			Objective healthTest = board.getObjective("health");
			
			if (healthTest != null)
			{
				sender.sendMessage(ChatColor.RED + "World already prepped!");
				return true;
			}
			
			UHCprepped = true;
			
			Player playerSent = (Player) sender;
			
			Integer worldBorderRadius = Integer.valueOf(args[0]);
			
			Location worldBorderCenter = new Location(world,0,0,0);
			
			for (Player player : getServer().getOnlinePlayers())
			{
				player.teleport(new Location(world,0,(playerSent.getWorld().getHighestBlockYAt(worldBorderCenter) + 1),0));
			}
			
			world.getWorldBorder().setCenter(worldBorderCenter);
			world.getWorldBorder().setSize(worldBorderRadius);
			world.getWorldBorder().setWarningDistance(25);
			
			getServer().broadcastMessage(ChatColor.GREEN + "World border set to " + worldBorderRadius + " blocks diameter.");
			
			for (World worldS : Bukkit.getServer().getWorlds())
			{
				worldS.setGameRuleValue("naturalRegeneration", "false");
				worldS.setPVP(false);
			}
			
			if (healthTest == null)
			{
				board.registerNewObjective("health", "dummy");
				board.getObjective("health").setDisplaySlot(DisplaySlot.PLAYER_LIST);
			}
			
			Objective healthTest2 = board.getObjective("health2");
			if (healthTest2 == null)
			{
				board.registerNewObjective("health2", "health").setDisplayName(ChatColor.RED + "♥");
				board.getObjective("health2").setDisplaySlot(DisplaySlot.BELOW_NAME);
			}
			
			
			for (Player player : getServer().getOnlinePlayers())
			{
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,200,100), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,10,10), true);
				
				Bukkit.getServer().getScoreboardManager().getMainScoreboard().getObjective("health").getScore(player.getName()).setScore(20);
				Bukkit.getServer().getScoreboardManager().getMainScoreboard().getObjective("health2").getScore(player.getName()).setScore(20);
				
				player.getInventory().clear();
				
				player.setHealth(20);
				
				player.setFoodLevel(20);
				
				player.getEnderChest().clear();
				
				player.getWorld().setTime(0);
				
				player.setExp(0F);
				
				player.setLevel(0);
				
				player.setGameMode(GameMode.SURVIVAL);
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,1000000,100));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,1000000,100));
				player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,1000000,-100));
				player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,1000000,256));
			}
			
			Objective killsTest = board.getObjective("kills");
			
			if (killsTest == null)
			{
				board.registerNewObjective("kills", "stat.playerKills").setDisplayName(ChatColor.RED + "--Kills--");	
			}
			
			boolean teams = Boolean.valueOf(getConfig().getBoolean("teams"));
			
			if (getConfig().getBoolean("season13") == true)
			{
				teams = false;
			}
			
			if (teams == true)
			{
				getServer().dispatchCommand(getServer().getConsoleSender(), "loc true");
			}
			else
			{
				getServer().dispatchCommand(getServer().getConsoleSender(), "loc false");
			}
			
			getServer().dispatchCommand(getServer().getConsoleSender(), "showkills");
			
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("showkills"))
		{
			if (sender instanceof Player)
			{
				if (!sender.isOp())
				{
					sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
					return true;
				}
			}
			
			Objective killsTest = getServer().getScoreboardManager().getMainScoreboard().getObjective("kills");
			
			if (killsTest == null)
			{
				sender.sendMessage(ChatColor.RED + "You need to prep the world for UHC first. Use /prepuhc");
			}
			else
			{
				Scoreboard board = getServer().getScoreboardManager().getMainScoreboard();
			
				board.getObjective("kills").setDisplaySlot(DisplaySlot.SIDEBAR);
				
				sender.sendMessage(ChatColor.GREEN + "Successfully set the kills to sidebar");
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("pvp") && sender instanceof Player)
		{
			if (!sender.isOp())
			{
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
				return true;
			}
			
			if (args.length == 1)
			{
				Player player = (Player) sender;
				
				String argument = args[0];
				
				if (argument.equalsIgnoreCase("on"))
				{
					for (World world : Bukkit.getServer().getWorlds())
					{
						world.setPVP(true);
					}
					sender.sendMessage(ChatColor.GREEN + "PVP Activated");
				}
				else if (argument.equalsIgnoreCase("off"))
				{
					for (World world : Bukkit.getServer().getWorlds())
					{
						world.setPVP(false);
					}
					sender.sendMessage(ChatColor.RED + "PVP Deactivated");
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "Invalid arguments - /pvp <on|off>");
				}
				
				return true;
			}
			else if (args.length == 0)
			{
				sender.sendMessage(ChatColor.RED + "Invalid arguments - /pvp <on|off>");
				return true;
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("loc"))
		{
			if (sender instanceof Player)
			{
				if (!sender.isOp())
				{
					sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!   ~Skrub");
					return true;
				}
			}

			if (Bukkit.getWorld(UHCWorldName) == null)
			{
				sender.sendMessage(ChatColor.RED + "UHC World called '" + UHCWorldName + "' does not exist");
				return true;
			}
			
			if (args.length == 1)
			{
				List<Location> Locs = new ArrayList<Location>();
			
				for (World world : Bukkit.getWorlds())
				{
					world.setDifficulty(Difficulty.PEACEFUL);
				}
				
				boolean teams;
				
				if (!(args[0].equals("true") || args[0].equals("false")))
				{
					sender.sendMessage(ChatColor.RED + "Invalid arguments - /loc <respect teams true|false>");
					return true;
				}
				
				teams = Boolean.valueOf(args[0]);
				
				int maxSpread = (int) Bukkit.getWorld(UHCWorldName).getWorldBorder().getSize() / 2;
				
				int minSpread = (int) maxSpread / 6;
				
				int noOfLoc;
				
				List<Player> online = new ArrayList<Player>();
				
				List<Team> onlineTeams = getOnlineTeams();
				
				if (teams == true)
				{
					noOfLoc = onlineTeams.size();
				}
				else
				{
					for (Player player : Bukkit.getServer().getOnlinePlayers())
					{
						if (player.getGameMode() != GameMode.SPECTATOR)
						{
							online.add(player);
						}
					}
					
					
					noOfLoc = online.size();
				}
				
				if (noOfLoc == 0)
				{
					getServer().broadcastMessage(ChatColor.RED + "0 locations attempting to generate, use /loc to retry");
					return true;
				}
				
				getServer().broadcastMessage(ChatColor.GOLD + "Generating " + ChatColor.RED + noOfLoc + ChatColor.GOLD + " locations.");
				for (int i = 0; i < noOfLoc; i ++)
				{
					Locs.add(checkLocs(minSpread,maxSpread,Locs));
				}
				
				getServer().broadcastMessage(ChatColor.GOLD + "Generated Locations");
				
				for (int i = 0; i < Locs.size();i ++)
				{
					Location loc = Locs.get(i);
					getLogger().info(ChatColor.GREEN + "Location Generated : " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
				}
				
				if (getConfig().getBoolean("season13") == true)
				{
					teams = false;
				}
				
				getServer().broadcastMessage(ChatColor.GREEN + "Beginning teleportations...");
				new tpTimer(this,teams,Locs,online).runTaskTimer(this, 20, 20);
				
				return true;
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "Invalid arguments - /loc <respect teams true|false>");
				return true;
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("rules") && sender instanceof Player)
		{
			showRules((Player) sender);
			return true;
		}
		
		
		if (cmd.getName().equalsIgnoreCase("recolor"))
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				
				if (!sender.isOp())
				{
					sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
					return true;
				}
			}
			
			for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams())
			{
				team.setPrefix(getTeamColor(true));
			}
			
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("stop-uhc"))
		{
			if (sender instanceof Player)
			{
				if (!sender.isOp())
				{
					sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
					return true;
				}
			}
			
			UHCLive = false;
			UHCprepped = false;
			
			if (getServer().getScoreboardManager().getMainScoreboard().getObjective("health") != null)
			{
				getServer().getScoreboardManager().getMainScoreboard().getObjective("health").unregister();
			}
			
			if (getServer().getScoreboardManager().getMainScoreboard().getObjective("health2") != null)
			{
				getServer().getScoreboardManager().getMainScoreboard().getObjective("health2").unregister();
			}
			
			if (getServer().getScoreboardManager().getMainScoreboard().getObjective("kills") != null)
			{
				getServer().getScoreboardManager().getMainScoreboard().getObjective("kills").unregister();
			}
			
			for (Player player : Bukkit.getServer().getOnlinePlayers())
			{
				VisualEffects.setPlayerName(player, null);
				
				player.getInventory().clear();
				
				VisualEffects.sendTabList(player, ChatColor.BLUE + "Kingdom " + ChatColor.WHITE + "of " + ChatColor.GOLD + "Ores " + ChatColor.RED + "UHC", ChatColor.GOLD + "by " + ChatColor.GREEN + "Dractus");
				for (PotionEffect effect : player.getActivePotionEffects())
				{
					player.removePotionEffect(effect.getType());
				}
				
				player.setGameMode(GameMode.SURVIVAL);
				
			}
			
			PlayerListener.deadList = new ArrayList<String>();
			
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("removeteam") && sender instanceof Player)
		{
			if (!sender.isOp())
			{
				sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command!");
				return true;
			}
			
			if (args.length != 1)
			{
				sender.sendMessage(ChatColor.RED + "Insufficient arguments - /removeteam <teamname> OR /removeteam <playername>");
				return true;
			}
			
			Team fetchTeam = getServer().getScoreboardManager().getMainScoreboard().getTeam(args[0]);
			
			if (fetchTeam == null)
			{
				OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(args[0]);
				
				if (player == null)
				{
					sender.sendMessage(ChatColor.RED + "Unable to find team or player on a team by name " + args[0]);
					return true;
				}
				else
				{
					Team playersTeam = getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(player);
					
					if (playersTeam == null)
					{
						sender.sendMessage(ChatColor.RED + "Unable to find team or player on a team by name " + args[0]);
						return true;
					}
					else
					{
						fetchTeam = playersTeam;
					}
				}
			}
			
			sender.sendMessage(ChatColor.GREEN + "Successfully removed team " + fetchTeam.getName());
			fetchTeam.unregister();
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("gmc") && sender instanceof Player)
		{
			Player player = (Player) sender;
			
			if (!sender.isOp())
			{
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
				return true;
			}
			
			((Player) sender).setGameMode(GameMode.CREATIVE);
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("gms") && sender instanceof Player)
		{
			Player player = (Player) sender;
			
			if (!sender.isOp())
			{
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
				return true;
			}
			
			((Player) sender).setGameMode(GameMode.SURVIVAL);
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("worldtest"))
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				
				if (player.isOp())
				{
					World UHCtest = Bukkit.getWorld(UHCWorldName);
					
					if (UHCtest == null)
					{
						player.sendMessage(ChatColor.RED + "Unable to find the UHC world : " + UHCWorldName);
					}
					else
					{
						player.sendMessage(ChatColor.GREEN + "Successfully found the UHC world : " + UHCWorldName);
					}
					
					World hubtest = Bukkit.getWorld(hubWorldName);
					
					if (hubtest == null)
					{
						player.sendMessage(ChatColor.RED + "Unable to find the hub world : " + hubWorldName);
					}
					else
					{
						player.sendMessage(ChatColor.GREEN + "Successfully found the hub world : " + hubWorldName);
					}
					
					return true;
				}
				else
				{
					player.sendMessage(ChatColor.RED + "You do not have permission to run this command!");
				}
			}
			else
			{
				World UHCtest = Bukkit.getWorld(UHCWorldName);
				
				if (UHCtest == null)
				{
					sender.sendMessage(ChatColor.RED + "Unable to find the UHC world : " + UHCWorldName);
				}
				else
				{
					sender.sendMessage(ChatColor.GREEN + "Successfully found the UHC world : " + UHCWorldName);
				}
				
				World hubtest = Bukkit.getWorld(hubWorldName);
				
				if (hubtest == null)
				{
					sender.sendMessage(ChatColor.RED + "Unable to find the hub world : " + hubWorldName);
				}
				else
				{
					sender.sendMessage(ChatColor.GREEN + "Successfully found the hub world : " + hubWorldName);
				}
				
				
				return true;
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("teamname") && sender instanceof Player)
		{
			Player player = (Player) sender;
			
			if (PlayerListener.deadList.contains(player.getName()))
			{
				sender.sendMessage(ChatColor.RED + "Unfortunately you died, therefor you cannot use this command!");
				return true;
			}
			
			if (args.length != 1)
			{
				sender.sendMessage(ChatColor.RED + "Insufficent arguments - /teamname <name>");
				return true;
			}
			
			String teamname = args[0];
			
			if (teamname.length() > 16)
			{
				sender.sendMessage(ChatColor.RED + "Team names cannot exceed 16 characters");
				return true;
			}
			
			Team team = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
			
			if (team == null)
			{
				sender.sendMessage(ChatColor.RED + "Cannot find your team");
				return true;
			}
			
			team.setDisplayName(teamname);
			
			for (String p : team.getEntries())
			{
				if (Bukkit.getPlayer(p) != null)
				{
					Bukkit.getPlayer(p).sendMessage(ChatColor.GREEN + "Successfully updated name to " + ChatColor.GOLD + teamname);
				}
			}

			return true;
		}
		
		return false;
	}
	
	public static List<Team> getOnlineTeams()
	{
		/*
		 * This method returns a list of teams that have at least one player online
		 */
		
		List<Team> Teams = new ArrayList<Team>();
		
		for (Team team : Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeams())
		{
			Boolean playersOnline = false;
			
			for (OfflinePlayer player : team.getPlayers())
			{
				if (Bukkit.getServer().getPlayer(player.getName()) != null)
				{
					playersOnline = true;
				}
			}
			
			if (playersOnline == true)
			{
				Teams.add(team);
			}
		}
		
		return Teams;
	}
	
	public boolean teams(CommandSender sender, String[] args)
	{
		/*
		 * This method assigns random teams based on some parameters
		 */
		
		if (args.length == 1)
		{
			Scoreboard board = getServer().getScoreboardManager().getMainScoreboard();
			
			Team findTeam = board.getTeam("team1");
			
			if (findTeam != null)
			{
				sender.sendMessage(ChatColor.RED + "Unable to add teams. Teams already exist. Do /clearteams then retry this command");
				return true;
			}
			
			alreadySelected = new boolean[48];
			
			int PlayersPerTeam = Integer.valueOf(args[0]);
			
			List<Player> players = new ArrayList<Player>();
			
			for (Player player : getServer().getOnlinePlayers())
			{
				if (player.getGameMode() != GameMode.SPECTATOR)
				{
					players.add(player);
				}
			}
			
			int AmountofTeams;
			
			int AmountofPlayers = players.size();
			
			if (PlayersPerTeam > AmountofPlayers)
			{
				PlayersPerTeam = AmountofPlayers;
				getServer().broadcastMessage(ChatColor.RED + "Players per team was greater than players - reduced players per team to " + PlayersPerTeam);
			}
			
			getServer().broadcastMessage("Randomizing players into teams of " + PlayersPerTeam + " players per team");
			
			if (AmountofPlayers % PlayersPerTeam == 0)
			{
				AmountofTeams = AmountofPlayers / PlayersPerTeam;
			}
			else
			{
				AmountofTeams = (AmountofPlayers / PlayersPerTeam) + 1;
			}
			
			if (AmountofTeams > 48)
			{
				sender.sendMessage(ChatColor.RED + "There are a maximum of 48 teams! You chose " + AmountofTeams);
				return true;
			}
			
			for (int i = 1 ; i <= AmountofTeams ; i ++ )
			{
				Team team = board.registerNewTeam("team" + i);
				team.setPrefix(getTeamColor(false));
				team.setSuffix("§r");
				team.setDisplayName("Team" + i);
				team.setCanSeeFriendlyInvisibles(true);
				team.setAllowFriendlyFire(true);
				
				getLogger().info("Added team : team" + i);
			}
			
			int iterations = 0;
			
			for (Player player : players)
			{
				boolean found = false;
				
				Random rand = new Random();
				
				do
				{
					iterations ++;
					
					int selected = rand.nextInt(AmountofTeams);
					
					selected ++;
					
					if (selected > AmountofTeams)
					{
						selected --;
					}
					
					Team team = board.getTeam("team" + selected);
					
					if (team.getSize() < PlayersPerTeam)
					{
						OfflinePlayer offlinePlayer;
					
						offlinePlayer = player;
					
						team.addPlayer(offlinePlayer);
						
						found = true;
						
						player.sendMessage(ChatColor.GREEN + "You have been added to " + ChatColor.DARK_GREEN + ChatColor.BOLD + "team" + selected);
						player.sendMessage("" + ChatColor.BOLD + ChatColor.GOLD + "Use '/teamname <name>' to change your teams name!");
					}
				} while (!found);
			}
			getServer().broadcastMessage(ChatColor.GREEN + "Successfully randomized " + AmountofPlayers + " players onto " + AmountofTeams + " teams after " + iterations + " iterations.");
			
			Positions.initialise(true);
			return true;
		}
		
		return false;
	}
	
	public static void showRules(Player player)
	{
		/*
		 * This method is going to show the player that is passed as a parameter
		 * the game rules and game settings.
		 */
		
		File rules = new File(plugin.getDataFolder(),"rules.yml");
		FileConfiguration rulesConfig = YamlConfiguration.loadConfiguration(rules);
		
		String rulesList = rulesConfig.getString("rules");
		
		List<String> allRules = new ArrayList<String>();
		
		String message = ChatColor.RED + "=======- RULES -=======\n";
		
		String currentMessage = "";
		
		for (int i = 0 ; i <= rulesList.length() ; i ++)
		{
			if (i == rulesList.length())
			{
				allRules.add(currentMessage);
				break;
			}
			if (String.valueOf(rulesList.charAt(i)).equals(","))
			{
				allRules.add(currentMessage);
				currentMessage = "";
			}
			else
			{
				currentMessage = currentMessage + String.valueOf(rulesList.charAt(i));
			}
		}
		
		for (String rule : allRules)
		{
			rule = rule.trim();
			message = message + ChatColor.GOLD + "• " + ChatColor.YELLOW + rule + "\n";
		}
		
		message = message + ChatColor.RED + "=======================";
		
		message = message + "\n \n" + ChatColor.BLUE + "====- GAME DATA -====\n";
		
		message = message + ChatColor.YELLOW + "Apple Rates: " + ChatColor.AQUA + KoOUHC.APPLE_RATE + "%\n";
		message = message + ChatColor.YELLOW + "Pearl Rates: " + ChatColor.AQUA + KoOUHC.PEARL_RATE + "%\n"; 
		message = message + ChatColor.YELLOW + "Fall Damage: " + ChatColor.AQUA + KoOUHC.FALL_DAMAGE + "\n";
		message = message + ChatColor.YELLOW + "Pearl Damage: " + ChatColor.AQUA + KoOUHC.PEARL_DAMAGE + "\n";
		message = message + ChatColor.YELLOW + "Death Lightning: " + ChatColor.AQUA + KoOUHC.DEATH_LIGHTNING + "\n";
				
		message = message + ChatColor.BLUE + "=====================\n";
		
		player.sendMessage(message);
	}
	
	private Location checkLocs(Integer minDist, Integer maxSpread,List<Location> locs)
	{
		boolean validLoc = false;
		
		Random rnd = new Random();
		
		Location genLoc;
		
		int iterations = 0;
		
		do
		{
			iterations ++;
			
			validLoc = true;
			
			int X;
			int Y;
			int Z;
			
			X = rnd.nextInt(maxSpread - 5) + 1;
			Z = rnd.nextInt(maxSpread - 5) + 1;
			
			if (rnd.nextInt(100) < 50)
			{
				X = X * -1;
			}
			
			if (rnd.nextInt(100) < 50)
			{
				Z = Z * -1;
			}
			
			Y = Bukkit.getWorld(UHCWorldName).getHighestBlockYAt(X, Z) + 1;
			
			genLoc = new Location(Bukkit.getWorld(UHCWorldName),X,Y + 4,Z);
			
			if (iterations > 20 && (!String.valueOf(Bukkit.getWorld(UHCWorldName).getBlockAt(X, Y - 1,Z).getType()).contains("LAVA")))
			{
				getLogger().info("Forced generation of location");
				return genLoc;
			}
			
			for (int i = 0;i < locs.size();i ++)
			{
				if ((int) genLoc.distance(locs.get(i)) < minDist)
				{
					validLoc = false;
				}
			}
			
			if (String.valueOf(Bukkit.getWorld(UHCWorldName).getBlockAt(X, Y - 2, Z).getType()).contains("WATER"))
			{
				Bukkit.getWorld(UHCWorldName).getBlockAt(X,Y-1,Z).setType(Material.WATER_LILY);
				validLoc = true;
			}
			
			if (String.valueOf(Bukkit.getWorld(UHCWorldName).getBlockAt(X, Y - 1, Z).getType()).contains("LAVA"))
			{
				validLoc = false;
			}
			
		} while (!validLoc);
		
		
		return genLoc;
	}
	
	public static void updatePlayerHealth()
	{
		/*
		 * This method takes the players health from the auto updating objective
		 * and sets that to the dummy objective which shows their health as a number.
		 */
		
		for (Player player : Bukkit.getOnlinePlayers())
		{
			//LIST OBJECTIVE
			Objective health1 = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getObjective("health");
			
			if (health1 == null)
			{
				return;
			}
			
			//NAME OBJECTIVE
			Objective health2 = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getObjective("health2");
			
			if (health2 == null)
			{
				return;
			}
			
			int health = health2.getScore(player.getName()).getScore();
			
			health1.getScore(player.getName()).setScore(health);
		}
	}
	
	public String getTeamColor(Boolean forced)
	{
		
		/*
		 * This method generates a random number and then returns a random 
		 * color for the teams, based on that number
		 * 
		 * It will only return the color if it hasnt already been generated
		 * 
		 * If forced is true, it won't check to see if the color has been done
		 */
		
		Random generator = new Random();
		
		boolean found = false;
		
		do
		{
			int selector = generator.nextInt(480);
			
			if (selector <= 10)
			{
				if (alreadySelected[0] == false || forced)
				{
					found = true;
					alreadySelected[0] = true;
					return "" + ChatColor.AQUA;
				}
			}
//			else if (selector <=20)
//			{
//				if (alreadySelected[1] == false)
//				{
//					found = true;
//					alreadySelected[1] = true;
//					return "" + ChatColor.BLACK;
//				}
//			}
			else if (selector <=30)
			{
				if (alreadySelected[2] == false || forced)
				{
					found = true;
					alreadySelected[2] = true;
					return "" + ChatColor.BLUE;
				}
			}
			else if (selector <=40)
			{
				if (alreadySelected[3] == false || forced)
				{
					found = true;
					alreadySelected[3] = true;
					return "" + ChatColor.DARK_AQUA;
				}
			}
			else if (selector <=50)
			{
				if (alreadySelected[4] == false || forced)
				{
					found = true;
					alreadySelected[4] = true;
					return "" + ChatColor.DARK_BLUE;
				}
			}
			else if (selector <=60)
			{
				if (alreadySelected[5] == false || forced)
				{
					found = true;
					alreadySelected[5] = true;
					return "" + ChatColor.DARK_GRAY;
				}
			}
			else if (selector <=70)
			{
				if (alreadySelected[6] == false || forced)
				{
					found = true;
					alreadySelected[6] = true;
					return "" + ChatColor.DARK_GREEN;
				}
			}
			else if (selector <=80)
			{
				if (alreadySelected[7] == false || forced)
				{
					found = true;
					alreadySelected[7] = true;
					return "" + ChatColor.DARK_PURPLE;
				}
			}
			else if (selector <=90)
			{
				if (alreadySelected[8] == false || forced)
				{
					found = true;
					alreadySelected[8] = true;
					return "" + ChatColor.DARK_RED;
				}
			}
			else if (selector <=100)
			{
				if (alreadySelected[9] == false || forced)
				{
					found = true;
					alreadySelected[9] = true;
					return "" + ChatColor.GOLD;
				}
			}
			else if (selector <=110)
			{
				if (alreadySelected[10] == false || forced)
				{
					found = true;
					alreadySelected[10] = true;
					return "" + ChatColor.GRAY;
				}
			}
			else if (selector <=120)
			{
				if (alreadySelected[11] == false || forced)
				{
					found = true;
					alreadySelected[11] = true;
					return "" + ChatColor.GREEN;
				}
			}
			else if (selector <=130)
			{
				if (alreadySelected[12] == false || forced)
				{
					found = true;
					alreadySelected[12] = true;
					return "" + ChatColor.RED;
				}
			}
			else if (selector <=140)
			{
				if (alreadySelected[13] == false || forced)
				{
					found = true;
					alreadySelected[13] = true;
					return "" + ChatColor.WHITE;
				}
			}
			else if (selector <=150)
			{
				if (alreadySelected[14] == false || forced)
				{
					found = true;
					alreadySelected[14] = true;
					return "" + ChatColor.YELLOW;
				}
			}
			else if (selector <=160)
			{
				if (alreadySelected[15] == false || forced)
				{
					found = true;
					alreadySelected[15] = true;
					return "" + ChatColor.AQUA + ChatColor.BOLD;
				}
			}
//			else if (selector <=170)
//			{
//				if (alreadySelected[16] == false)
//				{
//					found = true;
//					alreadySelected[16] = true;
//					return "" + ChatColor.BLACK + ChatColor.BOLD;
//				}
//			}
			else if (selector <=180)
			{
				if (alreadySelected[17] == false || forced)
				{
					found = true;
					alreadySelected[17] = true;
					return "" + ChatColor.BLUE + ChatColor.BOLD;
				}
			}
			else if (selector <=190)
			{
				if (alreadySelected[18] == false || forced)
				{
					found = true;
					alreadySelected[18] = true;
					return "" + ChatColor.DARK_AQUA + ChatColor.BOLD;
				}
			}
			else if (selector <=200)
			{
				if (alreadySelected[19] == false || forced)
				{
					found = true;
					alreadySelected[19] = true;
					return "" + ChatColor.DARK_BLUE + ChatColor.BOLD;
				}
			}
			else if (selector <=210)
			{
				if (alreadySelected[20] == false || forced)
				{
					found = true;
					alreadySelected[20] = true;
					return "" + ChatColor.DARK_GRAY + ChatColor.BOLD;
				}
			}
			else if (selector <=220)
			{
				if (alreadySelected[21] == false || forced)
				{
					found = true;
					alreadySelected[21] = true;
					return "" + ChatColor.DARK_GREEN + ChatColor.BOLD;
				}
			}
			else if (selector <=230)
			{
				if (alreadySelected[22] == false || forced)
				{
					found = true;
					alreadySelected[22] = true;
					return "" + ChatColor.DARK_PURPLE + ChatColor.BOLD;
				}
			}
			else if (selector <=240)
			{
				if (alreadySelected[23] == false || forced)
				{
					found = true;
					alreadySelected[23] = true;
					return "" + ChatColor.DARK_RED + ChatColor.BOLD;
				}
			}
			else if (selector <=250)
			{
				if (alreadySelected[24] == false || forced)
				{
					found = true;
					alreadySelected[24] = true;
					return "" + ChatColor.GOLD + ChatColor.BOLD;
				}
			}
			else if (selector <=260)
			{
				if (alreadySelected[25] == false || forced)
				{
					found = true;
					alreadySelected[25] = true;
					return "" + ChatColor.GRAY + ChatColor.BOLD;
				}
			}
			else if (selector <=270)
			{
				if (alreadySelected[26] == false || forced)
				{
					found = true;
					alreadySelected[26] = true;
					return "" + ChatColor.GREEN + ChatColor.BOLD;
				}
			}
			else if (selector <=280)
			{
				if (alreadySelected[27] == false || forced)
				{
					found = true;
					alreadySelected[27] = true;
					return "" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD;
				}
			}
			else if (selector <=290)
			{
				if (alreadySelected[28] == false || forced)
				{
					found = true;
					alreadySelected[28] = true;
					return "" + ChatColor.LIGHT_PURPLE;
				}
			}
			else if (selector <=300)
			{
				if (alreadySelected[29] == false || forced)
				{
					found = true;
					alreadySelected[29] = true;
					return "" + ChatColor.RED + ChatColor.BOLD;
				}
			}
			else if (selector <=310)
			{
				if (alreadySelected[30] == false || forced)
				{
					found = true;
					alreadySelected[30] = true;
					return "" + ChatColor.WHITE + ChatColor.BOLD;
				}
			}
			else if (selector <=320)
			{
				if (alreadySelected[31] == false || forced)
				{
					found = true;
					alreadySelected[31] = true;
					return "" + ChatColor.YELLOW + ChatColor.BOLD;
				}
			}
			else if (selector <=330)
			{
				if (alreadySelected[32] == false || forced)
				{
					found = true;
					alreadySelected[32] = true;
					return "" + ChatColor.AQUA + ChatColor.ITALIC;
				}
			}
//			else if (selector <=340)
//			{
//				if (alreadySelected[33] == false)
//				{
//					found = true;
//					alreadySelected[33] = true;
//					return "" + ChatColor.BLACK + ChatColor.ITALIC;
//				}
//			}
			else if (selector <=350)
			{
				if (alreadySelected[34] == false || forced)
				{
					found = true;
					alreadySelected[34] = true;
					return "" + ChatColor.BLUE + ChatColor.ITALIC;
				}
			}
			else if (selector <=360)
			{
				if (alreadySelected[35] == false || forced)
				{
					found = true;
					alreadySelected[35] = true;
					return "" + ChatColor.DARK_AQUA + ChatColor.ITALIC;
				}
			}
			else if (selector <=370)
			{
				if (alreadySelected[36] == false || forced)
				{
					found = true;
					alreadySelected[36] = true;
					return "" + ChatColor.DARK_BLUE + ChatColor.ITALIC;
				}
			}
			else if (selector <=380)
			{
				if (alreadySelected[37] == false || forced)
				{
					found = true;
					alreadySelected[37] = true;
					return "" + ChatColor.DARK_GRAY + ChatColor.ITALIC;
				}
			}
			else if (selector <=390)
			{
				if (alreadySelected[38] == false || forced)
				{
					found = true;
					alreadySelected[38] = true;
					return "" + ChatColor.DARK_GREEN + ChatColor.ITALIC;
				}
			}
			else if (selector <=400)
			{
				if (alreadySelected[39] == false || forced)
				{
					found = true;
					alreadySelected[39] = true;
					return "" + ChatColor.DARK_PURPLE + ChatColor.ITALIC;
				}
			}
			else if (selector <=410)
			{
				if (alreadySelected[40] == false || forced)
				{
					found = true;
					alreadySelected[40] = true;
					return "" + ChatColor.DARK_RED + ChatColor.ITALIC;
				}
			}
			else if (selector <=420)
			{
				if (alreadySelected[41] == false || forced)
				{
					found = true;
					alreadySelected[41] = true;
					return "" + ChatColor.GOLD + ChatColor.ITALIC;
				}
			}
			else if (selector <=430)
			{
				if (alreadySelected[42] == false || forced)
				{
					found = true;
					alreadySelected[42] = true;
					return "" + ChatColor.GRAY + ChatColor.ITALIC;
				}
			}
			else if (selector <=440)
			{
				if (alreadySelected[43] == false || forced)
				{
					found = true;
					alreadySelected[43] = true;
					return "" + ChatColor.GREEN + ChatColor.ITALIC;
				}
			}
			else if (selector <=450)
			{
				if (alreadySelected[44] == false || forced)
				{
					found = true;
					alreadySelected[44] = true;
					return "" + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC;
				}
			}
			else if (selector <=460)
			{
				if (alreadySelected[45] == false || forced)
				{
					found = true;
					alreadySelected[45] = true;
					return "" + ChatColor.RED + ChatColor.ITALIC;
				}
			}
			else if (selector <=470)
			{
				if (alreadySelected[46] == false || forced)
				{
					found = true;
					alreadySelected[46] = true;
					return "" + ChatColor.WHITE + ChatColor.ITALIC;
				}
			}
			else if (selector <=480)
			{
				if (alreadySelected[47] == false || forced)
				{
					found = true;
					alreadySelected[47] = true;
					return "" + ChatColor.YELLOW + ChatColor.ITALIC;
				}
			}
		} while (!found);
		
		return "" + ChatColor.AQUA;
	}
}
