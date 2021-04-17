package uk.co.minty_studios.metrocore.listeners;

import uk.co.minty_studios.metrocore.filehandling.LogCommands;
import uk.co.minty_studios.metrocore.MetroCore;
import uk.co.minty_studios.metrocore.methods.PlayerStates;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandUseListener implements Listener {

    private final MetroCore plugin;
    private LogCommands logCommands;
    private final PlayerStates playerStates;

    public CommandUseListener(MetroCore plugin, LogCommands logCommands, PlayerStates playerStates){
        this.logCommands = logCommands;
        this.plugin = plugin;
        this.playerStates = playerStates;
    }

    @EventHandler
    public void onCommandUse(PlayerCommandPreprocessEvent event){
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
