package com.simondoestuff.fishon;

import com.simondoestuff.fishon.commands.CommandCheckNow;
import com.simondoestuff.fishon.customitems.Waterbottle;
import com.simondoestuff.fishon.event.SwimTrigger;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    public final static String prefix = "§f§l[§2Fish§a§lOn!§f§l]§e";
    private static Plugin instance;
    private static PrimaryBreathingOperations breathingOp;

    public static PrimaryBreathingOperations getBreathingOp() {
        return breathingOp;
    }

    public static Plugin getInstance() {
        return instance;
    }

    public static void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, getInstance());
    }

    private void registerCustomItems() {
//        registerEvents(new Tank());
        registerEvents(new Waterbottle());
    }

    @Override
    public void onEnable() {
        instance = this;
        breathingOp = new PrimaryBreathingOperations();

        registerEvents(new SwimTrigger());
        registerEvents(breathingOp);
        registerEvents(new AntiWaterbucketSurf());
        registerCustomItems();
        this.getCommand("checknow").setExecutor(new CommandCheckNow());

        CommandCheckNow.checkAll();
        Bukkit.getLogger().info(prefix + " §aFish: §lON!");
    }

    @Override
    public void onDisable() {
        getLogger().info(prefix + " §4Fish: §lOFF!");
    }
}