package com.ShibuiKaleb.deepProfessions;

import com.ShibuiKaleb.deepProfessions.commands.DPAdminCommand;
import com.ShibuiKaleb.deepProfessions.data.DataManager;
import com.ShibuiKaleb.deepProfessions.listeners.PlayerListener;
import com.ShibuiKaleb.deepProfessions.commands.ProfessionCommand;
import com.ShibuiKaleb.deepProfessions.commands.ProfessionTabCompleter;
import com.ShibuiKaleb.deepProfessions.listeners.ProficiencyListener;
import com.ShibuiKaleb.deepProfessions.buffs.BuffManager;
import com.ShibuiKaleb.deepProfessions.buffs.LumberjackBuffs;
import com.ShibuiKaleb.deepProfessions.util.DebugLogger;
import org.bukkit.plugin.java.JavaPlugin;

public final class DeepProfessions extends JavaPlugin {

    private DataManager dataManager;
    private BuffManager buffManager;
    private LumberjackBuffs lumberjackBuffs;
    private DebugLogger debugLogger;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.dataManager = new DataManager(this);
        this.buffManager = new BuffManager(this);
        this.lumberjackBuffs = new LumberjackBuffs(this, buffManager);
        this.debugLogger = new DebugLogger(this);
        getServer().getPluginManager().registerEvents(new PlayerListener(dataManager, buffManager, lumberjackBuffs), this);
        getServer().getPluginManager().registerEvents(new ProficiencyListener(this, dataManager, buffManager, lumberjackBuffs), this);
        getCommand("profession").setExecutor(new ProfessionCommand(this, dataManager));
        getCommand("profession").setTabCompleter(new ProfessionTabCompleter());
        getCommand("dpadmin").setExecutor(new DPAdminCommand(this, dataManager));
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
    public LumberjackBuffs getLumberjackBuffs(){
        return lumberjackBuffs;
    }
    public DebugLogger getDebugLogger() { return debugLogger; }
}
