package com.ShibuiKaleb.deepProfessions;

import org.bukkit.plugin.java.JavaPlugin;

public final class DeepProfessions extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("DeepProfessions is online!");
    }

    @Override
    public void onDisable() {
        getLogger().info("DeepProfessions is offline.");
    }
}