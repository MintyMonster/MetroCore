package com.minty.metrocore.commands.modcommands;

import com.minty.metrocore.commands.ChildCommand;
import com.minty.metrocore.methods.ModMode;
import org.bukkit.entity.Player;

public class ModOnCommand extends ChildCommand {

    private final ModMode modMode;

    public ModOnCommand(String command, ModMode modMode) {
        super(command);
        this.modMode = modMode;
    }

    @Override
    public String getDescription() {
        return "Turn mod mode on!";
    }

    @Override
    public String getSyntax() {
        return "/mod on";
    }

    @Override
    public void perform(Player player, String[] args) {
        modMode.enableModMode(player);
    }
}
