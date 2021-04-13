package com.minty.metrocore.commands.admincommands;

import com.minty.metrocore.commands.ChildCommand;
import com.minty.metrocore.methods.AdminMode;
import org.bukkit.entity.Player;

public class AdminOnCommand extends ChildCommand {

    private final AdminMode adminMode;

    public AdminOnCommand(String command, AdminMode adminMode) {
        super(command);
        this.adminMode = adminMode;
    }

    @Override
    public String getDescription() {
        return "Turn admin mode on!";
    }

    @Override
    public String getSyntax() {
        return "/admin on";
    }

    @Override
    public void perform(Player player, String[] args) {

        adminMode.enableAdminMode(player);
    }
}
