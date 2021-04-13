package com.minty.metrocore.listeners;

import com.minty.metrocore.filehandling.LogCommands;
import com.minty.metrocore.MetroCore;
import com.minty.metrocore.methods.PlayerStates;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final MetroCore plugin;
    private final LogCommands logCommands;
    private final PlayerStates playerStates;

    public ChatListener(MetroCore plugin, LogCommands logCommands, PlayerStates playerStates){
        this.logCommands = logCommands;
        this.plugin = plugin;
        this.playerStates = playerStates;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        if(event.getPlayer().hasPermission("metrocore.admin")){
            if(playerStates.getPlayerState(event.getPlayer()).equals(PlayerStates.PlayerState.ADMIN)){
                logCommands.logAdminCommands(event.getPlayer(), event.getMessage());
            }
        }else if(event.getPlayer().hasPermission("metrocore.mod")){
            if(playerStates.getPlayerState(event.getPlayer()).equals(PlayerStates.PlayerState.MOD)){
                logCommands.logModCommands(event.getPlayer(), event.getMessage());
            }
        }
    }
}
