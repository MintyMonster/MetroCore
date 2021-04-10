package commands;

import com.minty.metrocore.MetroCore;
import methods.AdminMode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor {

    private final MetroCore plugin;
    private final AdminMode adminMode;

    public AdminCommand(MetroCore plugin, AdminMode adminMode){
        this.plugin = plugin;
        this.adminMode = adminMode;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(plugin.Admin.containsKey((Player) sender)){
            if(args.length == 0)
                adminMode.toggleAdminMode((Player) sender);
            if(args[0].equalsIgnoreCase("on"))
                adminMode.enableAdminMode((Player) sender);
            if(args[0].equalsIgnoreCase("off"))
                adminMode.disableAdminMode((Player) sender);
        }else{
            sender.sendMessage(ChatColor.RED + "Error! <AdminMode> Please try and relog!");
        }
        return true;
    }
}
