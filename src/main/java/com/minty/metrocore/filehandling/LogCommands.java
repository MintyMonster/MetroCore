package com.minty.metrocore.filehandling;

import com.minty.metrocore.MetroCore;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class LogCommands {

    private final CreatePluginFiles createPluginFiles;
    private final MetroCore plugin;

    public LogCommands(MetroCore plugin, CreatePluginFiles createPluginFiles){
        this.plugin = plugin;
        this.createPluginFiles = createPluginFiles;
    }

    public void logAdminCommands(Player player, String cmd){
        File folder = new File(plugin.getDataFolder() + File.separator + "Logs" + File.separator + "Admin" + File.separator);
        File file = new File(folder, player.getName() + ".txt");
        if(!file.exists()){
            createPluginFiles.createStaffFile(player, "Admin");
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write("[" + LocalDateTime.now() + "] " + player.getName() + ": " + cmd);
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void logModCommands(Player player, String cmd){
        File folder = new File(plugin.getDataFolder() + File.separator + "Logs" + File.separator + "Mod" + File.separator);
        File file = new File(folder, player.getName() + ".txt");
        if(!file.exists()) {
            createPluginFiles.createStaffFile(player, "Mod");
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write("[" + LocalDateTime.now() + "] " + player.getName() + ": " + cmd);
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
