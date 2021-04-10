package methods;

import com.minty.metrocore.MetroCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class ModMode {

    private final MetroCore plugin;
    private static ConsoleCommandSender sender = Bukkit.getConsoleSender();

    public ModMode(MetroCore plugin){
        this.plugin = plugin;
    }

    public void toggleModMode(Player player) {
        if (plugin.Mod.get(player)) {
            disableModMode(player);
        } else {
            enableModMode(player);
        }
    }

    public void enableModMode(Player player) {
        if (plugin.Mod.get(player).equals(true)) {
            for (String s : plugin.getConfig().getStringList("metromoderation.already_active")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
        } else {
            plugin.Mod.replace(player, true);
            File folder = new File(plugin.getDataFolder() + File.separator + "Logs" + File.separator + "Mod" + File.separator);
            File file = new File(folder, player.getName() + ".txt");
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                bw.write("[" + LocalDateTime.now() + "] " + player.getName() + ": **SESSION STARTED**");
                bw.newLine();
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("metrocore.mod")) {
                    for (String s : plugin.getConfig().getStringList("metromoderation.turn_on.messages")) {
                        String message = ChatColor.translateAlternateColorCodes('&', s).replaceAll("%player%", player.getName());
                        p.sendMessage(message);
                    }
                }
            }
            for (String s : plugin.getConfig().getStringList("metromoderation.turn_on.commands")) {
                String cmd = s.replaceAll("%player%", player.getName());
                Bukkit.dispatchCommand(sender, cmd);
            }
        }
    }

    public void disableModMode(Player player) {
        if (plugin.Mod.get(player).equals(false)) {
            for (String s : plugin.getConfig().getStringList("metromoderation.already_inactive")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
        } else {
            plugin.Mod.replace(player, false);
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
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("metrocore.mod")) {
                    for (String s : plugin.getConfig().getStringList("metromoderation.turn_off.messages")) {
                        String message = ChatColor.translateAlternateColorCodes('&', s).replaceAll("%player%", player.getName());
                        p.sendMessage(message);
                    }
                }
            }
            for (String s : plugin.getConfig().getStringList("metromoderation.turn_off.commands")) {
                String cmd = s.replaceAll("%player%", player.getName());
                Bukkit.dispatchCommand(sender, cmd);
            }

        }
    }
}
