package com.minty.metrocore.listeners;

import com.minty.metrocore.filehandling.LogCommands;
import com.minty.metrocore.MetroCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandUseListener implements Listener {

    private MetroCore plugin;
    private LogCommands logCommands;

    public CommandUseListener(MetroCore plugin, LogCommands logCommands){
        this.logCommands = logCommands;
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommandUse(PlayerCommandPreprocessEvent event){
        if(event.getPlayer().hasPermission("metrocore.admin")){
            if(!plugin.Admin.containsKey(event.getPlayer())){
                plugin.Admin.put(event.getPlayer(), false);
            }
            if(plugin.Admin.get(event.getPlayer())){
                logCommands.logAdminCommands(event.getPlayer(), event.getMessage());
            }

        }else if(event.getPlayer().hasPermission("metrocore.mod")){
            if(!plugin.Mod.containsKey(event.getPlayer())){
                plugin.Mod.put(event.getPlayer(), false);
            }
            if(plugin.Mod.get(event.getPlayer())){
                logCommands.logModCommands(event.getPlayer(), event.getMessage());
            }
        }

    }
}
