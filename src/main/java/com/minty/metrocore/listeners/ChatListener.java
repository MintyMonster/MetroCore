package com.minty.metrocore.listeners;

import com.minty.metrocore.filehandling.LogCommands;
import com.minty.metrocore.MetroCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final MetroCore plugin;
    private LogCommands logCommands;

    public ChatListener(MetroCore plugin, LogCommands logCommands){
        this.logCommands = logCommands;
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        if(event.getPlayer().hasPermission("metrocore.admin")){
            if(plugin.Admin.get(event.getPlayer())){
                logCommands.logAdminCommands(event.getPlayer(), event.getMessage());
            }
        }else if(event.getPlayer().hasPermission("metrocore.mod")){
            if(plugin.Mod.get(event.getPlayer())){
                logCommands.logModCommands(event.getPlayer(), event.getMessage());
            }
        }
    }
}
