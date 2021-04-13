package com.minty.metrocore.commands.admincommands;

import com.minty.metrocore.commands.ChildCommand;
import com.minty.metrocore.methods.AdminMode;
import org.bukkit.entity.Player;

public class AdminOffCommand extends ChildCommand {

    private final AdminMode adminMode;

    public AdminOffCommand(String command, AdminMode adminMode) {
        super(command);
        this.adminMode = adminMode;
    }

    @Override
    public String getDescription() {
        return "Turn admin mode off!";
    }

    @Override
    public String getSyntax() {
        return "/admin off";
    }

    @Override
    public void perform(Player player, String[] args) {
        adminMode.disableAdminMode(player);
    }
}
