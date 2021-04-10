package commands;

import com.minty.metrocore.MetroCore;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomTeleportCommand implements CommandExecutor {

    private final MetroCore plugin;
    private final List<Material> rtpblocks = Arrays.asList(
            Material.LAVA,
            Material.WATER,
            Material.BIRCH_LEAVES,
            Material.DARK_OAK_LEAVES,
            Material.JUNGLE_LEAVES,
            Material.OAK_LEAVES,
            Material.SPRUCE_LEAVES);

    public RandomTeleportCommand(MetroCore plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        Random rnd = new Random();
        Player player = (Player) sender;
        World world = player.getWorld();
        int min = plugin.getConfig().getInt("metrortp.minimum");
        int max = plugin.getConfig().getInt("metrortp.maximum");

        boolean isSafe = false;
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("metrortp.messages.looking_for_safe")));
        while (!isSafe) {

            int x = (int) (Math.random() * max * 2) - min;
            int z = (int) (Math.random() * max * 2) - min;
            int y = world.getHighestBlockYAt(x, z);
            Block b = world.getBlockAt(x, y, z);

            if (!rtpblocks.contains(b.getType())) {
                // check for leaves, tree etc (other annoying things)
                // If player moves or gets attacked, cancel
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("metrortp.messages.found_safe")));
                Location loc = new Location(player.getWorld(), x, y + 2, z);
                player.teleport(loc);
                for (String s : plugin.getConfig().getStringList("metrortp.commands")) {
                    String cmd = s.replaceAll("%x%", String.valueOf(x)).replaceAll("%y%", String.valueOf(y)).replaceAll("%z%", String.valueOf(z)).replaceAll("%player%", player.getName());
                    Bukkit.dispatchCommand(sender, cmd);
                }

                isSafe = true;
            }

        }
        return true;
    }
}
