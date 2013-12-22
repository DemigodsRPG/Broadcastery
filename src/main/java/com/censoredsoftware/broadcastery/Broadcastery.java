package com.censoredsoftware.broadcastery;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

		// Initialize metrics
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
