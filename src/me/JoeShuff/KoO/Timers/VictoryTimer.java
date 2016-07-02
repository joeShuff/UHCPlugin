package me.JoeShuff.KoO.Timers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.JoeShuff.KoO.KoOUHC;
import me.JoeShuff.KoO.VisualEffects;
import me.JoeShuff.KoO.DataTracker.DataTracker;
import net.minecraft.server.v1_10_R1.EnumParticle;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

public class VictoryTimer extends BukkitRunnable
{

	private List<String> players = new ArrayList<String>();
	
	private boolean teams;
	
	public VictoryTimer(KoOUHC plugin, Boolean teams, String winner)
	{
		players = new ArrayList<String>();
		this.teams = teams;
		
		if (teams)
		{
			Team team = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam(winner);
			
			if (team != null)
			{
				for (String player : team.getEntries())
				{
					players.add(player);
				}
			}
		}
		else
		{
			players.add(winner);
		}
		
		this.runTaskTimer(plugin, 10, 10);
	}
	
	private int seconds = 0;
	
	@Override
	public void run() {
		seconds ++;
		
		if (seconds % 5 == 0)
		{
			String cong = ChatColor.GOLD + "CONGRATULATIONS TO " + ChatColor.YELLOW;
			
			for (String p : players)
			{
				Player player = Bukkit.getServer().getPlayer(p);

				for (Player p1 : Bukkit.getOnlinePlayers())
				{
					if (p1.getName().equals(p))
					{
						player = p1;
					}
				}
				
				cong = cong + p + " ";
			}
			
			Bukkit.getServer().broadcastMessage(cong);
		}
		
		if (seconds < 40)
		{
			for (String p : players)
			{
				Player player = Bukkit.getServer().getPlayer(p);

				for (Player p1 : Bukkit.getOnlinePlayers())
				{
					if (p1.getName().equals(p))
					{
						player = p1;
					}
				}
				
				if (player != null)
				{
					VisualEffects.sendParticle(true, player, EnumParticle.NOTE, 0.5f, 1f, 0.5f, 2f, 100);
					
					playFirework(player, player.getLocation());
					//TODO : PARTICLES
				}
			}
		}
		else
		{
			for (Player player : Bukkit.getOnlinePlayers())
			{
				World world = Bukkit.getWorld(KoOUHC.hubWorldName);
				
				Location loc;
				
				if (world != null)
				{
					loc = new Location(world, KoOUHC.hubCentreX, KoOUHC.hubCentreY, KoOUHC.hubCentreZ);
					player.teleport(loc);
					player.getInventory().clear();
					player.getInventory().setBoots(null);
					player.getInventory().setLeggings(null);
					player.getInventory().setHelmet(null);
					player.getInventory().setChestplate(null);
					player.setGameMode(GameMode.SURVIVAL);
				}	
			}
			
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop-uhc");
			DataTracker.uploadAllData();
			this.cancel();
		}
		
	}
	
	public static void playFirework(Player player, Location loc)
	{
		//Spawn the Firework, get the FireworkMeta.
        Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
       
        //Our random generator
        Random r = new Random();  

        //Get the type
        int rt = r.nextInt(5) + 1;
        Type type = Type.BALL;      
        if (rt == 1) type = Type.BALL;
        if (rt == 2) type = Type.BALL_LARGE;
        if (rt == 3) type = Type.BURST;
        if (rt == 4) type = Type.CREEPER;
        if (rt == 5) type = Type.STAR;
       
        //Get our random colours  
        int r1i = r.nextInt(17) + 1;
        int r2i = r.nextInt(17) + 1;
        Color c1 = Color.fromRGB(r.nextInt(255),r.nextInt(255), r.nextInt(255));
        Color c2 = Color.fromRGB(r.nextInt(255),r.nextInt(255), r.nextInt(255));
       
        //Create our effect with this
        FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();
        
        //Then apply the effect to the meta
        fwm.addEffect(effect);
       
        //Generate some random power and set it
        int rp = r.nextInt(1) + 1;
        fwm.setPower(rp);
        
        //Then apply this to our rocket
        fw.setFireworkMeta(fwm);
	}
	
}
