package uk.co.minty_studios.metrocore.listeners;

import uk.co.minty_studios.metrocore.discord.DiscordLinking;
import uk.co.minty_studios.metrocore.methods.AdminMode;
import uk.co.minty_studios.metrocore.methods.ModMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    private final ModMode modMode;
    private final AdminMode adminMode;
    private final DiscordLinking discordLinking;

    public PlayerLeaveListener(ModMode modMode, AdminMode adminMode, DiscordLinking discordLinking){
        this.modMode = modMode;
        this.adminMode = adminMode;
        this.discordLinking = discordLinking;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        if(discordLinking.mapExists(event.getPlayer().getUniqueId()))
            discordLinking.removePlayer(event.getPlayer().getUniqueId());

        if(event.getPlayer().hasPermission("metrocore.admin")){
            adminMode.disableAdminMode(event.getPlayer());
        }else if(event.getPlayer().hasPermission("metrocore.mod")){
            modMode.disableModMode(event.getPlayer());
        }
    }
}
