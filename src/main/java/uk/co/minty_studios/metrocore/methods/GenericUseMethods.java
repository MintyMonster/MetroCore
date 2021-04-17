package uk.co.minty_studios.metrocore.methods;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import uk.co.minty_studios.metrocore.MetroCore;

import java.util.UUID;

public class GenericUseMethods {

    private final MetroCore plugin;
    private static ConsoleCommandSender sender = Bukkit.getConsoleSender();

    public GenericUseMethods(MetroCore plugin) {
        this.plugin = plugin;
    }

    public Player getPlayerByUuid(UUID uuid){
        for(Player p : Bukkit.getOnlinePlayers())
            if(p.getUniqueId().equals(uuid))
                return p;

        return null;
    }

    public UUID getOnlineUuid(String name){
        for(Player p : Bukkit.getOnlinePlayers())
            if(p.getName().equalsIgnoreCase(name))
                return p.getUniqueId();

        return null;
    }

    public void executeSingleCommand(Player player, String node){
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.dispatchCommand(sender, plugin.getConfig().getString(node).replace("%player%", player.getName()));
        });
    }

    public void executeMultipleCommands(Player player, String node){
        Bukkit.getScheduler().runTask(plugin, () -> {
            plugin.getConfig().getStringList(node).forEach(c -> {
                Bukkit.dispatchCommand(sender, c.replace("%player%", player.getName()));
            });
        });
    }
}
