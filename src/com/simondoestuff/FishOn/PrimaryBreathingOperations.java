package com.simondoestuff.FishOn;

import com.simondoestuff.FishOn.Event.ChangeManager;
import com.simondoestuff.FishOn.Event.SwimChangeEvent;
import com.simondoestuff.FishOn.Event.SwimTrigger;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class PrimaryBreathingOperations implements Listener {           // I DID IT!!! completely instantiable!
    private final Main main = Main.instance;
    private final int airtime = 10;

    private class BreathTimer extends BukkitRunnable {      // this is the breath system --- creates a separate thread which means when running this method again it wouldnt have access anymore
        private long endTime;
        private UUID initialPlayer;

        private BreathTimer(Player player) {
            this(player.getUniqueId());
        }

        private BreathTimer(UUID player) {
            this.endTime = System.currentTimeMillis() + airtime * 1000;
            this.initialPlayer = player;

            sendActionBar(initialPlayer, createBreathBar(0, airtime).toString());
            bars.add(this);
            this.runTaskTimer(main, 20, 20);   // the delay in here is timed to be totally flush
        }   // I think this automatically requires a non null player

        private void close() {
            sendActionBar(initialPlayer, "");
            this.cancel();
            bars.remove(this);
        }

        public UUID getPlayer() {
            return initialPlayer;
        }

        @Override
        public void run() {
            int remaining = (int) ((endTime - System.currentTimeMillis()) / 1000);
            int elapsed = airtime - remaining;

            sendActionBar(initialPlayer, createBreathBar(elapsed, remaining).toString());

            if (remaining < 1) {
                close();
                sendActionBar(initialPlayer, "");

                effects.setState(initialPlayer, ChangeManager.State.SUFFOCATING);
            }
        }
    }

    public class StateManager {
        private final PotionEffect[] waterEffects = {
                new PotionEffect(PotionEffectType.CONDUIT_POWER, 90000, 8, false, false),
                new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 90000, 3, false, false),
        };

        private final PotionEffect[] landEffects = {
                new PotionEffect(PotionEffectType.SLOW_DIGGING, 90000, 0, false, false),
                new PotionEffect(PotionEffectType.WEAKNESS, 90000, 0, false, false),
                new PotionEffect(PotionEffectType.SLOW, 90000, 1, false, false),
        };

        private final PotionEffect[] suffocatingEffects = {
                new PotionEffect(PotionEffectType.SLOW_DIGGING, 90000, 0, false, false),
                new PotionEffect(PotionEffectType.WITHER, 90000, 1, false, false),
                new PotionEffect(PotionEffectType.WEAKNESS, 90000, 1, false, false),
                new PotionEffect(PotionEffectType.SLOW, 90000, 2, false, false),
                new PotionEffect(PotionEffectType.CONFUSION, 90000, 0, false, false),
        };

        public void grantEffects(Player player, ChangeManager.State state) {
            PotionEffect[] effects;

            switch (state) {
                case SWIMMING:
                    effects = waterEffects;
                    break;
                case WALKING:
                    effects = landEffects;
                    break;
                default:
                    effects = suffocatingEffects;
                    break;
            }

            for (PotionEffect effect : effects) {
                player.addPotionEffect(effect);
//                Bukkit.broadcastMessage("§3Adding Effect: " + effect.getType().getName());
//
//                if (player != null) {
//                    new BukkitRunnable() {          // combining low respawn priority with delay 0 makes the respawning grant work
//                        @Override
//                        public void run() {
//                            player.addPotionEffect(effect);
//                        }
//                    }.runTaskLater(main, 1);            // nvm it seems like the delay of 1 is necessary again, keeping this for safety.
//                }
            }
        }

        public void takeEffects(Player player, ChangeManager.State state) {
            PotionEffect[] effects;

            switch (state) {
                case SWIMMING:
                    effects = waterEffects;
                    break;
                case WALKING:
                    effects = landEffects;
                    break;
                default:
                    effects = suffocatingEffects;
                    break;
            }

            for (PotionEffect effect : effects) if (player != null) player.removePotionEffect(effect.getType());
        }

        public void setState(UUID player, ChangeManager.State state) {
            Player playerObj = Bukkit.getPlayer(player);

            if (playerObj != null) {
                for (ChangeManager.State s : ChangeManager.State.values()) {
                    if (s != state) takeEffects(playerObj, s);
                }

                grantEffects(playerObj, state);
            }

            ChangeManager.forceChange(player, state);
        }

        public void setState(Player player, ChangeManager.State state) {
            setState(player.getUniqueId(), state);
        }

    }

    private StateManager effects = new StateManager();
    private ArrayList<BreathTimer> bars = new ArrayList<>();

    // ------------

    public BreathTimer getBar(Player player) {
        for (BreathTimer bar : bars) {
            if (bar.getPlayer().equals(player.getUniqueId())) return bar;
        }

        return null;
    }

    public void sendActionBar(UUID player, String message) {
        sendActionBar(Bukkit.getPlayer(player), message);
    }

    public void sendActionBar(Player player, String message) {
        if (player == null) return;

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    public StringBuilder createBreathBar(int elapsed, int remaining) {
        StringBuilder breathBar = new StringBuilder();
        String elapsedColor = "§0";
        String remainingColor = "§f";

        breathBar.append(elapsedColor);
        for (int i = 0; i < elapsed; i++) {
            breathBar.append("⬤");
        }

        breathBar.append(remainingColor);
        for (int i = 0; i < remaining; i++) {
            breathBar.append("⬤");
        }

        return breathBar;
    }

    @EventHandler
    public void milkResume(PlayerItemConsumeEvent e) {
        if (!e.getItem().getType().equals(Material.MILK_BUCKET)) return;

        Player p = e.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (BreathTimer bar : bars) {
                    if (bar.toString().equals(p.toString())) {
                        effects.setState(p, ChangeManager.State.WALKING);
                        return;
                    }
                }

                if (SwimTrigger.isSafeBlock(p.getLocation().getBlock())) {
                    effects.setState(p, ChangeManager.State.SWIMMING);
                } else effects.setState(p, ChangeManager.State.SUFFOCATING);
            }
        }.runTaskLater(main, 1);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        effects.setState(event.getPlayer(), ChangeManager.getState(event.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void swimStateChangeOverhead(SwimChangeEvent event) {
        Player player = event.getPlayer();
        BreathTimer bar = getBar(player);

        if (bar != null) bar.close();

        if (event.isSwimming()) effects.setState(player, ChangeManager.State.SWIMMING);
        else {
            effects.setState(player, ChangeManager.State.WALKING);
            new BreathTimer(event.getPlayer());
        }
    }

//    @EventHandler
//    private void swimStateChangeOverhead(SwimChangeEvent event) {
//        Player player = event.getPlayer();
//        boolean swimming = event.isSwimming();
//        BreathTimer bar = getBar(player);
//
//        Bukkit.broadcastMessage(String.format("§3(swimming): %b, (getBar(player) != null): %s, (a && b): %b", swimming, (getBar(player) != null), (swimming && getBar(player) != null)));
//
//        if (swimming && getBar(player) != null) bar.close();
//
//        if (swimming) {
//            if(bar != null) bar.close();
//
//            updateEffects(player, EffectOptions.land, false);
//            updateEffects(player, EffectOptions.suffocate, false);
//            updateEffects(player, EffectOptions.water, true);
//        } else {
//            updateEffects(player, EffectOptions.suffocate, false);
//            updateEffects(player, EffectOptions.water, false);
//            updateEffects(player, EffectOptions.land, true);
//
//            if (bar == null) new BreathTimer(event);
//        }
//    }
}
