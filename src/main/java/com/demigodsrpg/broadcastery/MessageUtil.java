package com.demigodsrpg.broadcastery;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class MessageUtil {
    private static Plugin PLUGIN;
    private static String PLUGIN_NAME;
    private static int LINE_SIZE, IN_GAME_LINE_SIZE;

    /**
     * Constructor for the Messages.
     *
     * @param plugin The current instance of the Plugin running this utility.
     */
    public MessageUtil(Plugin plugin) {
        PLUGIN = plugin;
        PLUGIN_NAME = plugin.getName();
        LINE_SIZE = 59 - PLUGIN_NAME.length();
        IN_GAME_LINE_SIZE = 54;
    }

    /**
     * Sends the message <code>msg</code> as a tagged message to the <code>sender</code>.
     *
     * @param sender The CommandSender to send the message to (allows console messages).
     */
    public static void tagged(CommandSender sender, String msg) {
        if (ChatColor.stripColor(msg).length() + PLUGIN_NAME.length() + 3 > IN_GAME_LINE_SIZE) {
            for (String line : wrapInGame(ChatColor.RED + "[" + PLUGIN_NAME + "] " + ChatColor.RESET + msg))
                sender.sendMessage(line);
            return;
        }
        sender.sendMessage(ChatColor.RED + "[" + PLUGIN_NAME + "] " + ChatColor.RESET + msg);
    }

    /**
     * Sends the console message <code>msg</code>.
     *
     * @param level The logger level.
     * @param msg   The message to be sent.
     */
    public static void log(Level level, String msg) {
        if (msg.length() > LINE_SIZE) {
            for (String line : wrapConsole(msg))
                PLUGIN.getLogger().log(level, line);
            return;
        }
        PLUGIN.getLogger().log(level, msg);
    }

    public static String[] wrapConsole(String msg) {
        return WordUtils.wrap(msg, LINE_SIZE, "/n", false).split("/n");
    }

    /**
     * Broadcast to the entire server (all players and the console) the message <code>msg</code>.
     *
     * @param msg The message to be sent.
     */
    public static void broadcast(String msg) {
        if (ChatColor.stripColor(msg).length() > IN_GAME_LINE_SIZE) {
            Server server = Bukkit.getServer();
            for (String line : wrapInGame(msg))
                server.broadcastMessage(line);
            return;
        }
        Bukkit.getServer().broadcastMessage(msg);
    }

    public static String[] wrapInGame(String msg) {
        return WordUtils.wrap(msg, IN_GAME_LINE_SIZE, "/n", false).split("/n");
    }
}
