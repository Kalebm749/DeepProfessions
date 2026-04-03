package com.ShibuiKaleb.deepProfessions;

import com.ShibuiKaleb.deepProfessions.data.DataManager;
import com.ShibuiKaleb.deepProfessions.listeners.PlayerListener;
import com.ShibuiKaleb.deepProfessions.commands.ProfessionCommand;
import com.ShibuiKaleb.deepProfessions.commands.ProfessionTabCompleter;
import com.ShibuiKaleb.deepProfessions.listeners.ProficiencyListener;
import com.ShibuiKaleb.deepProfessions.buffs.BuffManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class DeepProfessions extends JavaPlugin {

    private DataManager dataManager;
    private BuffManager buffManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.dataManager = new DataManager(this);
        this.buffManager = new BuffManager(this);
        getServer().getPluginManager().registerEvents(new PlayerListener(dataManager, buffManager), this);
        getServer().getPluginManager().registerEvents(new ProficiencyListener(this, dataManager), this);
        getCommand("profession").setExecutor(new ProfessionCommand(this, dataManager));
        getCommand("profession").setTabCompleter(new ProfessionTabCompleter());
        getLogger().info("DeepProfessions is online!");
    }

    @Override
    public void onDisable() {
        getLogger().info("DeepProfessions is offline.");
    }

    public DataManager getDataManager() {
        return dataManager;
    }
    public BuffManager getBuffManager() {
        return buffManager;
    }
}
