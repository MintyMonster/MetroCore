package uk.co.minty_studios.metrocore.listeners;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.event.LandClaimEvent;
import uk.co.minty_studios.metrocore.MetroCore;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LandClaimListener implements Listener {

    private final MetroCore plugin;
    private final static ConsoleCommandSender sender = Bukkit.getConsoleSender();
    private final static Random rnd = new Random();

    public LandClaimListener(MetroCore plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void getClaimKey(LandClaimEvent event) {

        Board boardinstance = Board.getInstance();
        FLocation location = event.getLocation();

        if(boardinstance.getFactionAt(location).isSafeZone()
                || boardinstance.getFactionAt(location).isWilderness()
                || boardinstance.getFactionAt(location).isWarZone())
            return;

        if(plugin.getConfig().getBoolean("metroclaimkeys.commands.multiple_commands")){
            for(String command : plugin.getConfig().getStringList("metroclaimkeys.commands.commands"))
                Bukkit.dispatchCommand(sender, command.replace("%player%", event.getfPlayer().getName()));

        }else{
            List<String> commands = new ArrayList<String>();
            for(String command : plugin.getConfig().getStringList("metroclaimkeys.commands.command"))
                commands.add(command);

            String command = commands.get(rnd.nextInt(commands.size())).replace("%player%", event.getfPlayer().getName());
            Bukkit.dispatchCommand(sender, command);
        }
    }
}
