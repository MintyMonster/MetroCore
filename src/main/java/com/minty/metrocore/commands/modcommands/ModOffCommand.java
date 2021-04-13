package com.minty.metrocore.commands.modcommands;

import com.minty.metrocore.commands.ChildCommand;
import com.minty.metrocore.methods.ModMode;
import org.bukkit.entity.Player;

public class ModOffCommand extends ChildCommand {

    private final ModMode modMode;

    public ModOffCommand(String command, ModMode modMode) {
        super(command);
        this.modMode = modMode;
    }

    @Override
    public String getDescription() {
        return "Turn mod mode off!";
    }

    @Override
    public String getSyntax() {
        return "/mod off";
    }

    @Override
    public void perform(Player player, String[] args) {
        modMode.disableModMode(player);
    }
}
