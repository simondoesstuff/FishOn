package com.simondoestuff.FishOn;

import com.simondoestuff.FishOn.Commands.CommandCheckNow;
import com.simondoestuff.FishOn.Event.SwimTrigger;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public final static String prefix = "§f§l[§2Fish§a§lOn!§f§l]§e";
    public static Main instance;

    public void registerEvents(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void onEnable() {
        instance = this;

        registerEvents(new SwimTrigger());
        registerEvents(new PrimaryBreathingOperations());
        registerEvents(new AntiWaterbucketSurf());
        this.getCommand("checknow").setExecutor(new CommandCheckNow());

        Bukkit.getLogger().info(prefix + " §aFish: §lON!");
    }

    @Override
    public void onDisable() {
        getLogger().info(prefix + " §4Fish: §lOFF!");
    }
}
