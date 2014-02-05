package com.censoredsoftware.broadcastery;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BroadcasteryPlugin extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		// Load Broadcastery
		Broadcastery.load();

		// Print success!
		getLogger().info("Successfully enabled.");
	}

	@Override
	public void onDisable()
	{
		// Cancel tasks
		Bukkit.getScheduler().cancelTasks(this);

		// Print success!
		getLogger().info("Successfully disabled.");
	}
}
