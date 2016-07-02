package me.JoeShuff.KoO;

import java.lang.reflect.Field;

import net.minecraft.server.v1_10_R1.EnumParticle;
import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;
import net.minecraft.server.v1_10_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_10_R1.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_10_R1.PlayerConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class VisualEffects {

	/*
	 * THIS METHOD TAKES 2 PARAMETERS
	 * player - THE TARGET PLAYER
	 * message - THE STRING TO DISPLAY - USE ChatColor.COLOR TO ADD COLOR
	 * TO THE STRING
	 * 
	 * THIS SENDS THE message TO THE SPECIFIED PLAYER ALONG THE ACTIONBAR
	 * 
	 */
	public static void sendActionBar(Player player, String message)
	{
		PlayerConnection conn = ((CraftPlayer) player).getHandle().playerConnection;
		
		IChatBaseComponent chat = ChatSerializer.a("{\"text\": \"" + message + "\"}");
		PacketPlayOutChat packet = new PacketPlayOutChat(chat, (byte) 2);
		
		conn.sendPacket(packet);
	}
	
	/*
	 * THIS METHOD TAKES 3 PARAMETERS
	 * player - THE TARGET PLAYER
	 * messageTop - THE STRING TO DISPLAY ON THE TOP - USE ChatColor.COLOR TO ADD COLOR
	 * TO THE STRING
	 * messageBottom - THE STRING TO DISPLAY ON THE TOP - USE ChatColor.COLOR TO ADD COLOR
	 * TO THE STRING
	 * 
	 * THIS SENDS THE messageTop AND messageBotom TO THE PLAYER IN THE TAB
	 * 
	 */
	public static void sendTabList(Player player, String messageTop, String messageBottom)
	{
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		
		IChatBaseComponent top = ChatSerializer.a("{\"text\": \"" + messageTop + "\"}");
		IChatBaseComponent bottom = ChatSerializer.a("{\"text\": \"" + messageBottom + "\"}");
		
		PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
		
		try
		{
			Field headerField = packet.getClass().getDeclaredField("a");
			headerField.setAccessible(true);
			headerField.set(packet, top);
			headerField.setAccessible(!headerField.isAccessible());
			
			Field footerField = packet.getClass().getDeclaredField("b");
			footerField.setAccessible(true);
			footerField.set(packet, bottom);
			footerField.setAccessible(!footerField.isAccessible());
		} catch (Exception e)
		{
			
		}
		
		connection.sendPacket(packet);
	}
	
	public static void sendJSONMessage(Player player, String JSON)
	{
		IChatBaseComponent msg = ChatSerializer.a(JSON);
		PacketPlayOutChat packet = new PacketPlayOutChat(msg);
		 
		PlayerConnection conn = ((CraftPlayer) player).getHandle().playerConnection;
		conn.sendPacket(packet);
	}
	
	public static void sendTitle(Player player, String msgTitle, String msgSubTitle)
	{
		sendTitle(player, msgTitle, msgSubTitle, 20, 60, 20);
	}
	
	public static void sendTitle(Player player, String msgTitle, String msgSubTitle, int in, int show, int out)
	{
        IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + msgTitle + "\"}");
        IChatBaseComponent chatSubTitle = ChatSerializer.a("{\"text\": \"" + msgSubTitle + "\"}");
        PacketPlayOutTitle p = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
        PacketPlayOutTitle p2 = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, chatSubTitle);
        PacketPlayOutTitle pt = new PacketPlayOutTitle(EnumTitleAction.TIMES, null, in, show, out);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(p);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(p2);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(pt);
	}
	
	public static void setPlayerName(Player player, String prefix)
	{
		setPlayerName(player, prefix, null);
	}
	
	public static void setPlayerName(Player player, String prefix,String teamColor)
	{
		String name = player.getName();
		
		String newName;
		
		if (prefix == null)
		{
			if (teamColor == null)
			{
				newName = name;
			}
			else
			{
				newName = teamColor + name;
			}
		}
		else
		{
			newName = prefix + " " + name + ChatColor.WHITE;
		}
		
		player.setDisplayName(newName);
		player.setPlayerListName(newName);
		player.setCustomName(newName);
	}
	
	public static void sendParticle(Boolean allPlayers, Player player, EnumParticle particle, float XOffset, float YOffset, float ZOffset, float speed, int numberofParticles)
	{
		if (particle == null)
		{
			System.out.println("Particle type cannot be null");
			return;
		}
		
		//type
		//true
		//X coord
		//Y coord
		//Z coord
		//x offset
		//y offset
		//z offset
		//speed
		//number of particles
		//null
		
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle,
				true,
				(float) player.getLocation().getX(), 
				(float) player.getLocation().getY(),
				(float) player.getLocation().getZ(),
				XOffset,
				YOffset,
				ZOffset,
				speed,
				numberofParticles,
				null);
		
		if (allPlayers)
		{
			for (Player pl : Bukkit.getServer().getOnlinePlayers())
			{
				CraftPlayer playr = (CraftPlayer) pl;
				sendParticle(playr, packet);
			}
		}
		else
		{
			CraftPlayer playr = (CraftPlayer) player;
			sendParticle(playr, packet);
		}
	}
	
	private static void sendParticle(CraftPlayer player, PacketPlayOutWorldParticles packet)
	{
		player.getHandle().playerConnection.sendPacket(packet);
	}
}
