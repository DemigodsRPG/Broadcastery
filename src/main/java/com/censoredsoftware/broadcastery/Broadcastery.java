package com.censoredsoftware.broadcastery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcstats.MetricsLite;

import com.censoredsoftware.broadcastery.util.Configs;
import com.censoredsoftware.broadcastery.util.Messages;

public class Broadcastery
{
	// Define variables
	public static final BroadcasteryPlugin plugin = (BroadcasteryPlugin) Bukkit.getServer().getPluginManager().getPlugin("Broadcastery");
	public static List<String> messages;

	protected static void load()
	{
		// Load messages to memory
		messages = Configs.getSettingList("messages");

		// Load commands
		loadCommands();

		// Finally, initialize metrics
		loadMetrics();

		// Start broadcasting
		startBroadcasting();
	}

	private static void startBroadcasting()
	{
		int delay = Configs.getSettingInt("start_delay");
		int frequency = Configs.getSettingInt("frequency");

		Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, new BukkitRunnable()
		{
			@Override
			public void run()
			{
				// Create a random and grab a random message
				Random rand = new Random();
				String message = messages.get(rand.nextInt(messages.size()));

				// Send the message
				Messages.broadcast(ChatColor.translateAlternateColorCodes('&', message));
			}
		}, delay * 1200, frequency * 1200);

		Messages.info("Delay: " + delay + " minute(s), Frequency: " + frequency + " minute(s).");
	}

	private static void loadCommands()
	{
		Commands executor = new Commands();

		plugin.getCommand("broadcastery").setExecutor(executor);
	}

	private static void loadMetrics()
	{
		try
		{
			(new MetricsLite(plugin)).start();
            Messages.info("Metrics loaded.");
		}
		catch(Exception ignored)
		{
            Messages.info("Metrics not loaded.");
        }
	}
}

class Commands implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(command.getName().equals("broadcastery"))
		{
			if(args.length == 0)
			{
				Messages.tagged(sender, "Broadcastery");
				sender.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/bc reload");
				sender.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/bc add [message]");

				return true;
			}
			else if(args.length > 0)
			{
				if(args[0].equalsIgnoreCase("reload"))
				{
					// Send messages and disable
					sender.sendMessage(ChatColor.GREEN + "Reloading Broadcastery...");
					Bukkit.getPluginManager().disablePlugin(Broadcastery.plugin);

					// Enable and send message
					Bukkit.getPluginManager().enablePlugin(Broadcastery.plugin);
					sender.sendMessage(ChatColor.GREEN + "Broadcastery reloaded!");

					return true;
				}
				else if(args[0].equals("add"))
				{
					if(args.length == 1)
					{
						// Not enough arguments
						sender.sendMessage(ChatColor.RED + "Not enough arguments: /bc add [message]");
						return true;
					}

					// Create the message from the args
					ArrayList<String> messageArr = new ArrayList<>(Arrays.asList(args));
					messageArr.remove(0);
					String message = StringUtils.join(messageArr, " ");

					// Save the message
					Configs.addToSettingList("messages", message);
					Broadcastery.messages.add(message);

					// Success!
					sender.sendMessage(ChatColor.GREEN + "Message added!");
				}
			}
		}

		return true;
	}
}
