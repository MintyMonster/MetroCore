package uk.co.minty_studios.metrocore.methods;

import uk.co.minty_studios.metrocore.MetroCore;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class ModMode {

    private final MetroCore plugin;
    private final PlayerStates playerStates;
    private static ConsoleCommandSender sender = Bukkit.getConsoleSender();

    public ModMode(MetroCore plugin, PlayerStates playerStates){
        this.plugin = plugin;
        this.playerStates = playerStates;
    }

    public void enableModMode(Player player){
        if(playerStates.getPlayerState(player).equals(PlayerStates.PlayerState.MOD)){
            for (String message : plugin.getConfig().getStringList("metromoderation.already_active"))
                plugin.sendWithPrefix(player, message);
        }else{
            playerStates.addPlayers(player, PlayerStates.PlayerState.MOD);
            File folder = new File(plugin.getDataFolder() + File.separator + "Logs" + File.separator + "Mod" + File.separator);
            File file = new File(folder, player.getName() + ".txt");
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                bw.write("[" + LocalDateTime.now() + "] " + player.getName() + ": **SESSION STARTED**");
                bw.newLine();
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for(Player p : Bukkit.getOnlinePlayers()){
                if((p.hasPermission("metrocore.mod")) || (p.hasPermission("metrocore.admin")))
                    for (String message : plugin.getConfig().getStringList("metromoderation.turn_on.messages"))
                        plugin.sendWithPrefix(player, message.replace("%player%", player.getName()));
            }
            for(String command : plugin.getConfig().getStringList("metromoderation.turn_on.commands"))
                Bukkit.dispatchCommand(sender, command.replace("%player%", player.getName()));
        }
    }

    public void disableModMode(Player player){
        if(!(playerStates.getPlayerState(player).equals(PlayerStates.PlayerState.MOD)))
            for(String message : plugin.getConfig().getStringList("metromoderation.already_inactive"))
                plugin.sendWithPrefix(player, message);
        else{
            playerStates.setPlayerState(player, PlayerStates.PlayerState.NORMAL);
            File folder = new File(plugin.getDataFolder() + File.separator + "Logs" + File.separator + "Mod" + File.separator);
            File file = new File(folder, player.getName() + ".txt");
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                bw.write("[" + LocalDateTime.now() + "] " + player.getName() + ": **SESSION ENDED**");
                bw.newLine();
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            for(Player p : Bukkit.getOnlinePlayers()){
                if((p.hasPermission("metrocore.mod")) || (p.hasPermission("metrocore.admin")))
                    for(String message : plugin.getConfig().getStringList("metromoderation.turn_off.messages"))
                        plugin.sendWithPrefix(player, message.replace("%player%", player.getName()));
            }
            for(String command : plugin.getConfig().getStringList("metromoderation.turn_off.commands"))
                Bukkit.dispatchCommand(sender, command.replace("%player%", player.getName()));
        }
    }
}
