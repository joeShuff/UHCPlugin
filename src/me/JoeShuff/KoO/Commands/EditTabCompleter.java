package me.JoeShuff.KoO.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.JoeShuff.KoO.KoOUHC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class EditTabCompleter implements TabCompleter 
{

	private KoOUHC plugin;
	
	public EditTabCompleter(KoOUHC plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) 
	{
		if (cmd.getName().equalsIgnoreCase("edituhc") && args.length >= 1)
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				
				List<String> list = Arrays.asList("episode-length","perma-day","perma-day-ep","should-shrink","shrink-size","shrink-ep","shrink-time","apple-rate"
						,"pearl-rate","death-lightning","fall-damage","pearl-damage");
				
				List<String> newList = new ArrayList<String>();
				
				for (String name : list)
				{
					if (name.contains(args[0]))
					{
						newList.add(name);
					}
				}
				
				return newList;
			}
		}
		return null;
	}

}
