package com.ShibuiKaleb.deepProfessions;

import com.ShibuiKaleb.deepProfessions.data.DataManager;
import com.ShibuiKaleb.deepProfessions.listeners.PlayerListener;
import com.ShibuiKaleb.deepProfessions.commands.ProfessionCommand;
import com.ShibuiKaleb.deepProfessions.commands.ProfessionTabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class DeepProfessions extends JavaPlugin {

    private DataManager dataManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.dataManager = new DataManager(this);
        getServer().getPluginManager().registerEvents(new PlayerListener(dataManager), this);
        getLogger().info("DeepProfessions is online!");
        getCommand("profession").setExecutor(new ProfessionCommand(this, dataManager));
        getCommand("profession").setTabCompleter(new ProfessionTabCompleter());
    }

    @Override
    public void onDisable() {
        getLogger().info("DeepProfessions is offline.");
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}