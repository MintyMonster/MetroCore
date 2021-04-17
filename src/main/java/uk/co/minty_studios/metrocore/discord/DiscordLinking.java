package uk.co.minty_studios.metrocore.discord;


import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DiscordLinking {

    private static final Map<UUID, Integer> linkedPlayers = new HashMap<>();

    public void linkPlayer(Player player, int code){
        linkedPlayers.put(player.getUniqueId(), code);
    }

    public int getCode(UUID uuid){
        int code = linkedPlayers.get(uuid);
        return code;
    }

    public void removePlayer(UUID uuid){
        linkedPlayers.remove(uuid);
    }

    public UUID lookForCode(int code){
        UUID uuid = null;
        for(Map.Entry<UUID, Integer> entry : linkedPlayers.entrySet()){
            if(entry.getValue().equals(code))
                uuid = entry.getKey();
        }

        return uuid;
    }

    public Boolean codeExists(int code){
        for(Map.Entry<UUID, Integer> entry : linkedPlayers.entrySet())
            if(entry.getValue().equals(code))
                return true;

        return false;
    }

    public Boolean mapExists(UUID uuid){
        for(Map.Entry<UUID, Integer> entry : linkedPlayers.entrySet())
            if(entry.getKey().equals(uuid))
                return true;

        return false;
    }
}
