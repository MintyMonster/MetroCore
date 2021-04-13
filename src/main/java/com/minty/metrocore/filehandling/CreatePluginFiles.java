package com.minty.metrocore.filehandling;

import com.minty.metrocore.MetroCore;
import com.minty.metrocore.filehandling.CreateDirectory;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class CreatePluginFiles {

    private final MetroCore plugin;
    private final CreateDirectory createDirectory;

    public CreatePluginFiles(MetroCore plugin, CreateDirectory createDirectory){
        this.plugin = plugin;
        this.createDirectory = createDirectory;
    }

    public void createFile(String name, String directory){
        File dir = createDirectory.getDirectory(directory);
        File file = new File(dir, name);
        if(!file.exists()){
            try{
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createStaffFile(Player player, String folder){
        File dir = plugin.getDataFolder();
        File Folder = new File(dir + File.separator + "Logs" + File.separator + folder + File.separator);
        if(!Folder.exists()) Folder.mkdir();
        File file = new File(Folder, player.getName() + ".txt");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
