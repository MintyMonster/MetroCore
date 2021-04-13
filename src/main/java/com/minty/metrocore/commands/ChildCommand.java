package com.minty.metrocore.commands;

import org.bukkit.entity.Player;

public abstract class ChildCommand {

    private final String command;

    protected ChildCommand(String command) {
        this.command = command;
    }

    public String getName() {
        return command;
    }

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract void perform(Player player, String[] args);
}