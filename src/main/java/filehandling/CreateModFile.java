package filehandling;

import com.minty.metrocore.MetroCore;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class CreateModFile {

    private final MetroCore plugin;

    public CreateModFile(MetroCore plugin){
        this.plugin = plugin;
    }

    public void createModFile(Player player){
        File dir = plugin.getDataFolder();
        File Folder = new File(dir + File.separator + "Logs" + File.separator + "Mod" + File.separator);
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
