package com.censoredsoftware.broadcastery;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcstats.MetricsLite;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Broadcastery
{
	// Define variables
	public static final BroadcasteryPlugin PLUGIN = (BroadcasteryPlugin) Bukkit.getServer().getPluginManager().getPlugin("Broadcastery");
	public static final Configuration CONFIG = PLUGIN.getConfig();
	public static final Logger LOGGER = PLUGIN.getLogger();
	public static final Random RAND = new Random();
	public static List<String> messages;
	public static int lastIndex = 0;

	protected static void load()
	{
		// Load config
		loadConfig();

		// Load messages to memory
		messages = CONFIG.getStringList("messages");

		// Load commands
		loadCommands();

		// Finally, initialize metrics
		loadMetrics();

		// Start broadcasting
		startBroadcasting();
	}

	private static void startBroadcasting()
	{
		int delay = CONFIG.getInt("start_delay");
		int frequency = CONFIG.getInt("frequency");

		Bukkit.getScheduler().scheduleAsyncRepeatingTask(PLUGIN, new BukkitRunnable()
		{
			@Override
			public void run()
			{
				// Grab a random message
				String message = messages.get(RAND.nextInt(messages.size()));

				// Handle ordered messages
				if(!CONFIG.getBoolean("random_order"))
				{
					message = messages.get(lastIndex);
					lastIndex++;
					if(lastIndex >= messages.size()) lastIndex = 0;
				}

				// Send the message
				Messages.broadcast(ChatColor.translateAlternateColorCodes('&', message));
			}
		}, delay * 1200, frequency * 1200);

        Messages.log(Level.INFO, "Delay: " + delay + " minute(s), Frequency: " + frequency + " minute(s).");
	}

	private static void loadConfig()
	{
		Configuration config = PLUGIN.getConfig();
		config.options().copyDefaults(true);
		PLUGIN.saveConfig();
	}

	private static void loadCommands()
	{
		Commands executor = new Commands();

		PLUGIN.getCommand("broadcastery").setExecutor(executor);
	}

	private static void loadMetrics()
	{
		try
		{
			(new MetricsLite(PLUGIN)).start();
			Messages.log(Level.INFO, "Metrics loaded.");
		}
		catch(Exception ignored)
		{
			Messages.log(Level.WARNING, "Metrics could not load.");
		}
	}

	static class Commands implements CommandExecutor
	{
 @Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
		{
			if("broadcastery".equals(command.getName()))
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
					if("reload".equalsIgnoreCase(args[0]))
					{
  // Send messages and disable
                        sender.sendMessage(ChatColor.GREEN + "Reloading Broadcastery...");
						Bukkit.getPluginManager().disablePlugin(PLUGIN);

						// Enable and send message
						Bukkit.getPluginManager().enablePlugin(PLUGIN);
						sender.sendMessage(ChatColor.GREEN + "Broadcastery reloaded!");

						return true;
					}
					else if("add".equals(args[0]))
					{
						if(args.length == 1)
             {
                            // Not enough arguments
							sender.sendMessage(ChatColor.RED + "Not enough arguments: /bc add [message]");
							return true;
						}

						// Create the message from the args
						List<String> messageArr = Lists.newArrayList(args);
                        messageArr.remove(0);
						String message = StringUtils.join(messageArr, " ");

						// Save the message
						messages.add(message);
						CONFIG.set("messages", messages);
						PLUGIN.saveConfig();

						// Success!
						sender.sendMessage(ChatColor.GREEN + "Message added!");
					}
				}
			}

			return true;
		}
    }

    static class Messages
    {
        private static final String PLUGIN_NAME;
        private static final int LINE_SIZE, IN_GAME_LINE_SIZE;

        /**
		 * Constructor for the Messages.
		 * 
		 * @param instance The current instance of the Plugin running this module.
		 */
		static
		{
			PLUGIN_NAME = PLUGIN.getName();
			LINE_SIZE = 59 - PLUGIN_NAME.length();
       IN_GAME_LINE_SIZE = 54;
        }

        /**
		 * Sends the message <code>msg</code> as a tagged message to the <code>sender</code>.
		 * 
		 * @param sender The CommandSender to send the message to (allows console messages).
		 */
		public static void tagged(CommandSender sender, String msg)
		{
			if(ChatColor.stripColor(msg).length() + PLUGIN_NAME.length() + 3 > IN_GAME_LINE_SIZE)
     {
				for(String line : wrapInGame(ChatColor.RED + "[" + PLUGIN_NAME + "] " + ChatColor.RESET + msg))
					sender.sendMessage(line);
				return;
			}
			sender.sendMessage(ChatColor.RED + "[" + PLUGIN_NAME + "] " + ChatColor.RESET + msg);
		}

		/**
		 * Sends the console message <code>msg</code>.
		 * 
		 * @param level The logger level.
		 * @param msg The message to be sent.
		 */
		public static void log(Level level, String msg)
		{
			if(msg.length() > LINE_SIZE)
			{
				for(String line : wrapConsole(msg))
					LOGGER.log(level, line);
				return;
			}
			LOGGER.log(level, msg);
		}

		public static String[] wrapConsole(String msg)
		{
			return WordUtils.wrap(msg, LINE_SIZE, "/n", false).split("/n");
		}

		/**
		 * Broadcast to the entire server (all players and the console) the message <code>msg</code>.
		 * 
		 * @param msg The message to be sent.
		 */
		public static void broadcast(String msg)
		{
			if(ChatColor.stripColor(msg).length() > IN_GAME_LINE_SIZE)
			{
				Server server = Broadcastery.PLUGIN.getServer();
				for(String line : wrapInGame(msg))
					server.broadcastMessage(line);
				return;
			}
			Broadcastery.PLUGIN.getServer().broadcastMessage(msg);
		}

		public static String[] wrapInGame(String msg)
		{
			return WordUtils.wrap(msg, IN_GAME_LINE_SIZE, "/n", false).split("/n");
		}
	}
}
