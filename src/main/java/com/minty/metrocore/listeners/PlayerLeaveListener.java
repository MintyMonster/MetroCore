package com.minty.metrocore.listeners;

import com.minty.metrocore.MetroCore;
import com.minty.metrocore.methods.AdminMode;
import com.minty.metrocore.methods.ModMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    private final ModMode modMode;
    private final AdminMode adminMode;

    public PlayerLeaveListener(ModMode modMode, AdminMode adminMode){
        this.modMode = modMode;
        this.adminMode = adminMode;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        if(event.getPlayer().hasPermission("metrocore.admin")){
            adminMode.disableAdminMode(event.getPlayer());
        }else if(event.getPlayer().hasPermission("metrocore.mod")){
            modMode.disableModMode(event.getPlayer());
        }
    }
}
