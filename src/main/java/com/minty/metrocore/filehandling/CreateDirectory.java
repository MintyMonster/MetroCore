package com.minty.metrocore.filehandling;

import com.minty.metrocore.MetroCore;

import java.io.File;

public class CreateDirectory {

    private final MetroCore plugin;

    public CreateDirectory(MetroCore plugin){
        this.plugin = plugin;
    }

    public void createDir(String name){
        File dir = plugin.getDataFolder();
        File folder = new File(dir + File.separator + name + File.separator);
        if(!folder.exists()) folder.mkdir();
    }

    public File getDirectory(String name){
        File dir = plugin.getDataFolder();
        File folder = new File(dir + File.separator + name + File.separator);
        return folder;
    }
}
