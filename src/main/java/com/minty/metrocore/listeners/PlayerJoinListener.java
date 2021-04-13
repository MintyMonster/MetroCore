package com.minty.metrocore.listeners;

import com.minty.metrocore.filehandling.CreatePluginFiles;
import com.minty.metrocore.filehandling.PlayerJoinDatabase;
import com.minty.metrocore.methods.PlayerStates;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final PlayerJoinDatabase playerDatabase;
    private final PlayerStates playerStates;
    private final CreatePluginFiles createPluginFiles;

    public PlayerJoinListener(PlayerJoinDatabase database, PlayerStates playerStates, CreatePluginFiles createPluginFiles){
        this.playerDatabase = database;
        this.playerStates = playerStates;
        this.createPluginFiles = createPluginFiles;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){

        playerStates.setPlayerState(event.getPlayer(), PlayerStates.PlayerState.NORMAL);
        playerDatabase.addToDatabase(event.getPlayer());

        if(event.getPlayer().hasPermission("metrocore.admin"))
            createPluginFiles.createStaffFile(event.getPlayer(), "Admin");
        else if(event.getPlayer().hasPermission("metrocore.mod"))
            createPluginFiles.createStaffFile(event.getPlayer(), "Mod");
    }
}
