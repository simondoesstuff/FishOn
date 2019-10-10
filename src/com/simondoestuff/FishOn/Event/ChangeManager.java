package com.simondoestuff.FishOn.Event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

public class ChangeManager implements Listener {
    public enum State {SWIMMING, WALKING, SUFFOCATING}

    public enum CheckState {WATER, LAND}

    private static HashMap<UUID, State> log = new HashMap<UUID, State>();

    @EventHandler   // automatic updates
    public static void onSwimChange(SwimChangeEvent e) {
        if (e.isSwimming()) pushState(e.getPlayer().getUniqueId(), State.SWIMMING);
        else pushState(e.getPlayer().getUniqueId(), State.WALKING);
    }

    private static void callSwimChangeEvent(Player player, boolean swimming) {
        Bukkit.getServer().getPluginManager().callEvent(new SwimChangeEvent(player, swimming));
    }

    private static void pushState(UUID player, State state) {
        if (state == State.SWIMMING) log.remove(player);
        else log.put(player, state);
    }

    public static State getState(UUID player) {
        return log.getOrDefault(player, State.SWIMMING);
    }

    // updates the log and returns if the update was sound. this is a silent push and does not call SwimChangeEvent

    public static boolean forceChange(Player player, State state) {
        return forceChange(player.getUniqueId(), state);
    }

    public static boolean forceChange(UUID player, State state) {
        boolean ret = false;

        switch (state) {
            case SWIMMING: {
                State pState = getState(player);
                if (pState == State.WALKING || pState == State.SUFFOCATING) ret = true;
                break;
            }
            case WALKING: {
                State pState = getState(player);
                if (pState == State.SWIMMING) ret = true;
                break;
            }
            case SUFFOCATING: {
                State pState = getState(player);
                if (pState == State.WALKING) ret = true;
                break;
            }
        }

        pushState(player, state);
        return ret;
    }

    public static boolean inform(Player player, CheckState state) {
        return inform(player.getUniqueId(), state);
    }

    public static boolean inform(UUID player, CheckState state) {
        switch (state) {
            case WATER: {
                State pState = getState(player);
                if (pState == State.WALKING || pState == State.SUFFOCATING) {
//                    pushState(player, State.SWIMMING);
                    callSwimChangeEvent(Bukkit.getPlayer(player), true);
                    return true;
                }
                break;
            }
            case LAND: {
                State pState = getState(player);
                if (pState == State.SWIMMING) {
//                    pushState(player, State.WALKING);
                    callSwimChangeEvent(Bukkit.getPlayer(player), false);
                    return true;
                }
                break;
            }
        }

        return false;
    }
}
