package com.minty.metrocore.commands;

import com.minty.metrocore.MetroCore;
import com.minty.metrocore.methods.ModMode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModCommand implements CommandExecutor {

    private final MetroCore plugin;
    private final ModMode modMode;

    public ModCommand(MetroCore plugin, ModMode modMode){
        this.plugin = plugin;
        this.modMode = modMode;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){

        if(plugin.Mod.containsKey((Player) sender)){
            if(args.length == 0)
                modMode.toggleModMode((Player) sender);
            else if(args[0].equalsIgnoreCase("on"))
                modMode.enableModMode((Player) sender);
            else if(args[0].equalsIgnoreCase("off"))
                modMode.disableModMode((Player) sender);
        }else{
            sender.sendMessage(ChatColor.RED + "Error! <ModMode> Please try and relog!");
        }
        return true;
    }
}
