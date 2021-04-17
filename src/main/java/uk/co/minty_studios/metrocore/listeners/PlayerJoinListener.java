package uk.co.minty_studios.metrocore.listeners;

import uk.co.minty_studios.metrocore.filehandling.CreatePluginFiles;
import uk.co.minty_studios.metrocore.filehandling.PlayerJoinDatabase;
import uk.co.minty_studios.metrocore.methods.PlayerStates;
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
