package com.minty.metrocore.commands;

import com.minty.metrocore.MetroCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MetroCoreCommand implements CommandExecutor {

    private final MetroCore plugin;

    public MetroCoreCommand(MetroCore plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){

        if (args.length == 0)
            sender.sendMessage(ChatColor.RED + "Usage: /metrocore [command]");

        else if (args[0].equalsIgnoreCase("reload")) {
            try {
                plugin.reloadConfig();
                sender.sendMessage(ChatColor.GOLD + "[MetroCore] " + ChatColor.GREEN + "Reload complete.");
            } catch (Exception e) {
                sender.sendMessage(ChatColor.GOLD + "[MetroCore] " + ChatColor.RED + "Something went wrong whilst reloading config.\nCheck logs for error.");
                e.printStackTrace();
            }
            return true;
        }
        return true;
    }
}
