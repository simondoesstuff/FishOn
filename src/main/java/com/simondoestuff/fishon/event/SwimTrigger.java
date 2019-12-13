package com.simondoestuff.fishon.event;

import com.simondoestuff.fishon.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class SwimTrigger implements Listener {
    Plugin plugin;

    public SwimTrigger() {
        plugin = Plugin.getInstance();
    }

    public static boolean isSafeBlock(Block block) {
        Material material = block.getType();
        BlockData data = block.getBlockData();

        switch (material) {
            case WATER:
            case BUBBLE_COLUMN:
            case KELP:
            case KELP_PLANT:
            case SEAGRASS:
            case TALL_SEAGRASS:
            case TUBE_CORAL:
            case BRAIN_CORAL:
            case BUBBLE_CORAL:
            case FIRE_CORAL:
            case CONDUIT:
                return true;
        }   // exceptions

        return (data instanceof Waterlogged && ((Waterlogged) data).isWaterlogged());
    }

    private void callSwimChange(Player player, boolean swimming) {
        Bukkit.getServer().getPluginManager().callEvent(new SwimChangeEvent(player, swimming));
    }

    private void stateFromTo(Player player, Block from, Block to) {
        boolean toSafeState = isSafeBlock(Objects.requireNonNull(to));
        boolean fromSafeState = isSafeBlock(from);

        //https://hub.spigotmc.org/jira/browse/SPIGOT-4139

        if (fromSafeState != toSafeState) {        // only trigger on change
            callSwimChange(player, toSafeState);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        stateFromTo(event.getPlayer(), event.getFrom().getBlock(), Objects.requireNonNull(event.getTo()).getBlock());
    }   // this is good

    @EventHandler
    public void onPlayerWaterPlace(PlayerBucketEmptyEvent e) {
        if (e.getBucket() != Material.WATER_BUCKET) return;

        // Location location = new Location(e.getPlayer().getWorld(), 0, 0, 0);
        // new Location( world, x, y, z )

        // East = + x
        // South = + z

        int x, y, z;
        BlockFace blockFace = e.getBlockFace();
        Location blockClickedLoc = e.getBlockClicked().getLocation();

//        Bukkit.broadcastMessage(String.format("§3Block Clicked: X: %d, Y: %d, Z: %d", blockClickedLoc.getBlockX(), blockClickedLoc.getBlockY(), blockClickedLoc.getBlockZ()));

        x = blockClickedLoc.getBlockX();
        y = blockClickedLoc.getBlockY();
        z = blockClickedLoc.getBlockZ();

        if (blockFace.equals(BlockFace.NORTH)) z--;
        else if (blockFace.equals(BlockFace.EAST)) x++;
        else if (blockFace.equals(BlockFace.SOUTH)) z++;
        else if (blockFace.equals(BlockFace.WEST)) x--;
        else if (blockFace.equals(BlockFace.UP)) y++;
        else if (blockFace.equals(BlockFace.DOWN)) y--;

        Location waterLoc = new Location(e.getPlayer().getWorld(), x, y, z);

//        Bukkit.broadcastMessage(String.format("§3Water placed at: X: %d, Y: %d, Z: %d", x, y, z));

        for (Player p : Bukkit.getOnlinePlayers()) {
            Location ploc = p.getLocation();
            ploc = new Location(p.getWorld(), ploc.getBlockX(), ploc.getBlockY(), ploc.getBlockZ());

            if (ploc.equals(waterLoc)) {
                callSwimChange(e.getPlayer(), true);
            }
        }
    }

    @EventHandler
    public void onPlayerWaterBreak(PlayerBucketFillEvent e) {
        if (e.getBucket() != Material.WATER_BUCKET) return;

        // Location location = new Location(e.getPlayer().getWorld(), 0, 0, 0);
        // new Location( world, x, y, z )

        // East = + x
        // South = + z

        int x, y, z;
        BlockFace blockFace = e.getBlockFace();
        Location blockClickedLoc = e.getBlockClicked().getLocation();

//        Bukkit.broadcastMessage(String.format("§3Block Clicked: X: %d, Y: %d, Z: %d", blockClickedLoc.getBlockX(), blockClickedLoc.getBlockY(), blockClickedLoc.getBlockZ()));

        x = blockClickedLoc.getBlockX();
        y = blockClickedLoc.getBlockY();
        z = blockClickedLoc.getBlockZ();

        if (blockFace.equals(BlockFace.NORTH)) z--;
        else if (blockFace.equals(BlockFace.EAST)) x++;
        else if (blockFace.equals(BlockFace.SOUTH)) z++;
        else if (blockFace.equals(BlockFace.WEST)) x--;
        else if (blockFace.equals(BlockFace.UP)) y++;
        else if (blockFace.equals(BlockFace.DOWN)) y--;

        Location waterLoc = new Location(e.getPlayer().getWorld(), x, y, z);

//        Bukkit.broadcastMessage(String.format("§3Water placed at: X: %d, Y: %d, Z: %d", x, y, z));

        for (Player p : Bukkit.getOnlinePlayers()) {
            Location ploc = p.getLocation();
            ploc = new Location(p.getWorld(), ploc.getBlockX(), ploc.getBlockY(), ploc.getBlockZ());

            if (ploc.equals(waterLoc)) {
                callSwimChange(e.getPlayer(), false);
            }
        }
    }

    @EventHandler
    public void onNaturalBlockChange(BlockFromToEvent event) {
        boolean toSafeState = isSafeBlock(event.getToBlock()),
                fromSafeState = isSafeBlock(event.getBlock());

        if (toSafeState != fromSafeState) {
            for (Player player : Bukkit.getOnlinePlayers()) {   // check all players, if they were in the change -> call event
                Location ploc = player.getLocation();
                ploc = new Location(ploc.getWorld(), ploc.getBlockX(), ploc.getBlockY(), ploc.getBlockZ());

                Location eloc = event.getBlock().getLocation();
                eloc = eloc.subtract(0, 1, 0);

//                Bukkit.broadcastMessage("§5§lplayer: " + player.getName());
//                Bukkit.broadcastMessage("§5player location §d-> " + ploc);
//                Bukkit.broadcastMessage("§5event location §d-> " + eloc);
//                Bukkit.broadcastMessage("§5condition §d-> " + (ploc.equals(eloc)));
//                Bukkit.broadcastMessage("§5---");
//                Bukkit.broadcastMessage("");

                if (ploc.equals(eloc) && isSafeBlock(event.getToBlock())) {
//                    Bukkit.broadcastMessage(String.valueOf(event.getBlock()));
                    callSwimChange(player, isSafeBlock(event.getBlock()));
//                    Bukkit.broadcastMessage("§5event.getBlock() -> §f" + event.getBlock());               // i think the to and from are flipped
//                    Bukkit.broadcastMessage("§5event.getToBlock() -> §f" + event.getToBlock());
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        boolean safe = isSafeBlock(block);

        for (Player player : Bukkit.getOnlinePlayers()) {
            Location ploc = player.getLocation();
            ploc = new Location(ploc.getWorld(), ploc.getBlockX(), ploc.getBlockY(), ploc.getBlockZ());

            if (block.getLocation().equals(ploc)) {
                ChangeManager.inform(player.getUniqueId(), (safe) ? ChangeManager.CheckState.WATER : ChangeManager.CheckState.LAND);
            }
        }
    }

//    @EventHandler(priority = EventPriority.LOWEST);
    @EventHandler  // the respawn can overlap the effect grant, which acts as a block.
    public void onRespawn(PlayerRespawnEvent e) {
        new BukkitRunnable() {

            @Override
            public void run() {
                callSwimChange(e.getPlayer(), isSafeBlock(e.getRespawnLocation().getBlock()));
            }
        }.runTaskLater(plugin, 1);
    }   // later you should use playerDeath to add the player to a HashMap and then pull from the map on respawn to treat it like a move event

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        ChangeManager.inform(e.getPlayer().getUniqueId(), (isSafeBlock(e.getPlayer().getLocation().getBlock())) ? ChangeManager.CheckState.WATER : ChangeManager.CheckState.LAND);
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent e) {
        if (!(e.getEntered() instanceof Player)) return;

        ChangeManager.inform(e.getEntered().getUniqueId(), (isSafeBlock(e.getEntered().getLocation().getBlock())) ? ChangeManager.CheckState.WATER : ChangeManager.CheckState.LAND);
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent e) {
            Player player = null;
            for (Entity entity : e.getVehicle().getPassengers()) {
                if (entity instanceof Player) player = (Player) entity;
            }

            if (player == null) return;
            stateFromTo(player, e.getFrom().getBlock(), e.getTo().getBlock());
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent e) {
        if (!(e.getExited() instanceof Player)) return;
        ChangeManager.CheckState state;
        Block topBlock = e.getVehicle().getLocation().add(0, 1, 0).getBlock();

        if (!isSafeBlock(topBlock)) state = ChangeManager.CheckState.LAND;
        else state = (isSafeBlock(e.getExited().getLocation().getBlock())) ? ChangeManager.CheckState.WATER : ChangeManager.CheckState.LAND;

        ChangeManager.inform(e.getExited().getUniqueId(), state);
    }

    @EventHandler
    public void onBedExit(PlayerBedLeaveEvent e) {
        ChangeManager.inform(e.getPlayer().getUniqueId(), (isSafeBlock(e.getPlayer().getLocation().getBlock())) ? ChangeManager.CheckState.WATER : ChangeManager.CheckState.LAND);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        stateFromTo(e.getPlayer(), e.getFrom().getBlock(), e.getTo().getBlock());
    }  // this is good
}