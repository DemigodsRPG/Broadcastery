package com.censoredsoftware.broadcastery.util;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.censoredsoftware.broadcastery.Broadcastery;

/**
 * Module to handle all common messages sent to players or the console.
 */
public class Messages
{
	private static final Logger log = Logger.getLogger("Minecraft");
	private static String pluginName;

	/**
	 * Constructor for the Messages.
	 * 
	 * @param instance The current instance of the Plugin running this module.
	 */
	static
	{
		pluginName = Broadcastery.plugin.getName();
	}

	/**
	 * Sends the message <code>msg</code> as a tagged message to the <code>sender</code>.
	 * 
	 * @param sender The CommandSender to send the message to (allows console messages).
	 */
	public static void tagged(CommandSender sender, String msg)
	{
		sender.sendMessage(ChatColor.RED + "[" + pluginName + "] " + ChatColor.RESET + msg);
	}

	/**
	 * Sends the console message <code>msg</code> with "info" tag.
	 * 
	 * @param msg The message to be sent.
	 */
	public static void info(String msg)
	{
		log.info("[" + pluginName + "] " + msg);
	}

	/**
	 * Sends the console message <code>msg</code> with "warning" tag.
	 * 
	 * @param msg The message to be sent.
	 */
	public static void warning(String msg)
	{
		log.warning("[" + pluginName + "] " + msg);
	}

	/**
	 * Sends the console message <code>msg</code> with "severe" tag.
	 * 
	 * @param msg The message to be sent.
	 */
	public static void severe(String msg)
	{
		log.severe("[" + pluginName + "] " + msg);
	}

	/**
	 * Broadcast to the entire server (all players and the console) the message <code>msg</code>.
	 * 
	 * @param msg The message to be sent.
	 */
	public static void broadcast(String msg)
	{
		Broadcastery.plugin.getServer().broadcastMessage(msg);
	}

	/**
	 * Let the <code>sender</code> know it does not have permission.
	 * 
	 * @param sender The CommandSender being notified.
	 * @return True.
	 */
	public static boolean noPermission(CommandSender sender)
	{
		sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");
		return true;
	}

	/**
	 * Let the <code>console</code> know it cannot continue.
	 * 
	 * @param console The console.
	 * @return True.
	 */
	public static boolean noConsole(ConsoleCommandSender console)
	{
		console.sendMessage("That can only be executed by a player.");
		return true;
	}

	/**
	 * Let the <code>player</code> know it cannot continue.
	 * 
	 * @param player The Player being notified.
	 * @return True.
	 */
	public static boolean noPlayer(Player player)
	{
		player.sendMessage("That can only be executed by the console.");
		return true;
	}

	/**
	 * Clears the chat for <code>player</code> using .sendMessage().
	 * 
	 * @param player the player whose chat to clear.
	 */
	public static void clearChat(Player player)
	{
		for(int x = 0; x < 120; x++)
			player.sendMessage(" ");
	}

	/**
	 * Clears the chat for <code>player</code> using .sendRawMessage().
	 * 
	 * @param player the player whose chat to clear.
	 */
	public static void clearRawChat(Player player)
	{
		for(int x = 0; x < 120; x++)
			player.sendRawMessage(" ");
	}
}
