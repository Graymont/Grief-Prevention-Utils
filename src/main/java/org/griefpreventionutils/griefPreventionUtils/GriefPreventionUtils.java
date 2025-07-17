package org.griefpreventionutils.griefPreventionUtils;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static org.griefpreventionutils.griefPreventionUtils.GPManager.TrackerTrigger;

public final class GriefPreventionUtils extends JavaPlugin {

    public GPManager gpManager = new GPManager();
    public Commands commands = new Commands();

    public GPItem gpItem = new GPItem();

    public static GriefPreventionUtils getMainPlugin(){
        return GriefPreventionUtils.getPlugin(GriefPreventionUtils.class);
    }

    @Override
    public void onEnable() {
        RegisterEvents();
        RegisterCommands();

        TrackerTrigger();
    }

    @Override
    public void onDisable() {

    }

    void RegisterEvents(){
        getServer().getPluginManager().registerEvents(gpManager, this);
        getServer().getPluginManager().registerEvents(gpItem, this);
    }

    void RegisterCommands(){
        Objects.requireNonNull(getCommand("griefpreventionutils")).setExecutor(this.commands);
        Objects.requireNonNull(getCommand("claimtracker")).setExecutor(this.commands);

        // tab
        Objects.requireNonNull(getCommand("griefpreventionutils")).setExecutor(this.commands);
    }
}
