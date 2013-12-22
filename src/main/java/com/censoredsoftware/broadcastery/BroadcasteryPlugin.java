package com.censoredsoftware.broadcastery;

import org.bukkit.plugin.java.JavaPlugin;

import com.censoredsoftware.broadcastery.util.Messages;

public class BroadcasteryPlugin extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		// Load Broadcastery
		Broadcastery.load();

		// Print success!
		Messages.info("Successfully enabled.");
	}

	@Override
	public void onDisable()
	{
		// Print success!
		Messages.info("Successfully disabled.");
	}
}
