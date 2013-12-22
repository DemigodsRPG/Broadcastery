package com.censoredsoftware.broadcastery;

import org.bukkit.Bukkit;
import org.mcstats.MetricsLite;

public class Broadcastery
{
	// Define variables
	public static final BroadcasteryPlugin plugin = (BroadcasteryPlugin) Bukkit.getServer().getPluginManager().getPlugin("Broadcastery");

	protected static void load()
	{
		// TODO

		// Initialize metrics
		loadMetrics();
	}

	private static void loadMetrics()
	{
		try
		{
			(new MetricsLite(plugin)).start();
		}
		catch(Exception ignored)
		{}
	}
}
