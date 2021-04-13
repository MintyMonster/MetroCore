package com.minty.metrocore.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MasterCommand {

    private final Map<String, ChildCommand> childCommands = new HashMap<>();

    public void addCommand(ChildCommand command) {
        childCommands.put(command.getName(), command);
    }

    public ChildCommand getCommand(String command) {
        return childCommands.get(command.trim());
    }

    public Map<String, ChildCommand> getChildCommands() {
        return Collections.unmodifiableMap(childCommands);
    }
}