package uk.co.minty_studios.metrocore.methods;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStates {

    private static final Map<UUID, PlayerState> registeredPlayers = new HashMap<>();

    public void addPlayers(Player player, PlayerState state){
        registeredPlayers.put(player.getUniqueId(), state);
    }

    public PlayerState getPlayerState(Player player){
        PlayerState state = registeredPlayers.get((player.getUniqueId()));

        return state == null ? PlayerState.NORMAL : state;
    }

    public PlayerState setPlayerState(Player player, PlayerState state){
        UUID uuid = player.getUniqueId();

        if(state == PlayerState.NORMAL){
            registeredPlayers.remove(uuid);
            return PlayerState.NORMAL;
        }

        return registeredPlayers.put(uuid, state);
    }

    public enum PlayerState {
        NORMAL, MOD, ADMIN
    }
}
