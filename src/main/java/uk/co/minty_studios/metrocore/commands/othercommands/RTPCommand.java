package uk.co.minty_studios.metrocore.commands.othercommands;

import org.bukkit.*;
import uk.co.minty_studios.metrocore.MetroCore;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RTPCommand implements CommandExecutor {

    private List<Material> rtpblocks = new ArrayList<>();
    private final MetroCore plugin;

    public RTPCommand(MetroCore plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;
        World world = player.getWorld();

        if(world.getEnvironment().equals(World.Environment.NETHER)){
            plugin.sendWithPrefix(player, plugin.getConfig().getString("RTP.messages.is_nether"));
            return true;
        }

        Boolean isSafe = false;
        for(String blocks : plugin.getConfig().getStringList("RTP.blocked"))
            rtpblocks.add(Material.getMaterial(blocks));
        int min = plugin.getConfig().getInt("RTP.minimum");
        int max = plugin.getConfig().getInt("RTP.maximum");


        plugin.sendWithPrefix(player, plugin.getConfig().getString("RTP.messages.looking_for_safe"));
        while(!isSafe){

            int x = (int) (Math.random() * max * 2) - min;
            int z = (int) (Math.random() * max * 2) - min;
            int y = world.getHighestBlockYAt(x, z);
            Block block = world.getBlockAt(x, y, z);

            if(!rtpblocks.contains(block.getType())){
                plugin.sendWithPrefix(player, plugin.getConfig().getString("RTP.messages.found_safe"));
                player.teleport(new Location(player.getWorld(), x, y + 2, z));
                for(String command : plugin.getConfig().getStringList("RTP.commands"))
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            command.replaceAll("%x%", String.valueOf(x)).replaceAll("%y%", String.valueOf(y)).replaceAll("%z%", String.valueOf(z)).replaceAll("%player%", player.getName()));
                isSafe = true;
            }
        }

        return true;
    }
}
