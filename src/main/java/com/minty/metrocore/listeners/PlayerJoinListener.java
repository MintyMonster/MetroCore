package com.minty.metrocore.listeners;

import com.minty.metrocore.filehandling.CreateAdminFile;
import com.minty.metrocore.filehandling.CreateModFile;
import com.minty.metrocore.filehandling.PlayerJoinDatabase;
import com.minty.metrocore.MetroCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final MetroCore plugin;
    private final PlayerJoinDatabase playerDatabase;
    private final CreateAdminFile createFile;
    private final CreateModFile modFile;

    public PlayerJoinListener(MetroCore plugin, PlayerJoinDatabase database, CreateAdminFile createFile, CreateModFile modFile){
        this.plugin = plugin;
        this.playerDatabase = database;
        this.createFile = createFile;
        this.modFile = modFile;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        if(event.getPlayer().hasPermission("metrocore.admin")){
            if(!plugin.Admin.containsKey(event.getPlayer())){
                plugin.Admin.put(event.getPlayer(), false);
                createFile.createAdminFile(event.getPlayer());
                playerDatabase.addToDatabase(event.getPlayer());
            }
        }else if(event.getPlayer().hasPermission("metrocore.mod")){
            if(!plugin.Mod.containsKey(event.getPlayer())){
                plugin.Mod.put(event.getPlayer(), false);
                modFile.createModFile(event.getPlayer());
                playerDatabase.addToDatabase(event.getPlayer());
            }
        }else{
            playerDatabase.addToDatabase(event.getPlayer());
        }
    }
}
