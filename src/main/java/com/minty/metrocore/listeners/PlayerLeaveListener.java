package com.minty.metrocore.listeners;

import com.minty.metrocore.MetroCore;
import com.minty.metrocore.methods.AdminMode;
import com.minty.metrocore.methods.ModMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    private final MetroCore plugin;
    private final ModMode modMode;
    private final AdminMode adminMode;

    public PlayerLeaveListener(MetroCore plugin, ModMode modMode, AdminMode adminMode){
        this.plugin = plugin;
        this.modMode = modMode;
        this.adminMode = adminMode;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        if(event.getPlayer().hasPermission("metrocore.admin")){
            if(plugin.Admin.get(event.getPlayer())){
                modMode.disableModMode(event.getPlayer());
                plugin.Admin.replace(event.getPlayer(), false);
            }
        }else if(event.getPlayer().hasPermission("metrocore.mod")){
            if(plugin.Mod.get(event.getPlayer())){
                adminMode.disableAdminMode(event.getPlayer());
                plugin.Mod.replace(event.getPlayer(), false);
            }
        }
    }
}
