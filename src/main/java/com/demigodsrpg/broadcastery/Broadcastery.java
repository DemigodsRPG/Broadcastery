package com.demigodsrpg.broadcastery;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class Broadcastery extends JavaPlugin {

    // Define variables
    public Random RAND = new Random();
    public List<String> messages;
    public int lastIndex = 0;

    @Override
    public void onEnable() {
        // Handle message util
        new MessageUtil(this);

        // Load config
        loadConfig();

        // Load messages to memory
        messages = getConfig().getStringList("messages");

        // Load commands
        loadCommands();

        // Finally, initialize metrics
        loadMetrics();

        // Start broadcasting
        startBroadcasting();
    }

    @Override
    public void onDisable() {
        // Cancel tasks
        Bukkit.getScheduler().cancelTasks(this);

        // Print success!
        getLogger().info("Successfully disabled.");
    }

    private void startBroadcasting() {
        int delay = getConfig().getInt("start_delay");
        int frequency = getConfig().getInt("frequency");

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () -> {
            // Grab a random message
            String message = messages.get(RAND.nextInt(messages.size()));

            // Handle ordered messages
            if (!getConfig().getBoolean("random_order")) {
                message = messages.get(lastIndex);
                lastIndex++;
                if (lastIndex >= messages.size()) lastIndex = 0;
            }

            // Send the message
            MessageUtil.broadcast(ChatColor.translateAlternateColorCodes('&', message));
        }, delay * 1200, frequency * 1200);

        MessageUtil.log(Level.INFO, "Delay: " + delay + " minute(s), Frequency: " + frequency + " minute(s).");
    }

    private void loadConfig() {
        Configuration config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();
    }

    private void loadCommands() {
        getCommand("broadcastery").setExecutor(this);
    }

    private void loadMetrics() {
        try {
            (new MetricsLite(this)).start();
            MessageUtil.log(Level.INFO, "Metrics loaded.");
        } catch (Exception ignored) {
            MessageUtil.log(Level.WARNING, "Metrics could not load.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ("broadcastery".equals(command.getName())) {
            if (args.length == 0) {
                MessageUtil.tagged(sender, "Broadcastery");
                sender.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/bc reload");
                sender.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/bc add [message]");

                return true;
            } else if (args.length > 0) {
                if ("reload".equalsIgnoreCase(args[0])) {
                    // Send messages and disable
                    sender.sendMessage(ChatColor.GREEN + "Reloading Broadcastery...");
                    Bukkit.getPluginManager().disablePlugin(this);

                    // Enable and send message
                    Bukkit.getPluginManager().enablePlugin(this);
                    sender.sendMessage(ChatColor.GREEN + "Broadcastery reloaded!");

                    return true;
                } else if ("add".equals(args[0])) {
                    if (args.length == 1) {
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
                    getConfig().set("messages", messages);
                    saveConfig();

                    // Success!
                    sender.sendMessage(ChatColor.GREEN + "Message added!");
                }
            }
        }

        return true;
    }
}
