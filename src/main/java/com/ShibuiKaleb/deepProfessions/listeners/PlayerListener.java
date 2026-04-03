package com.ShibuiKaleb.deepProfessions.listeners;

import com.ShibuiKaleb.deepProfessions.data.DataManager;
import com.ShibuiKaleb.deepProfessions.buffs.BuffManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final DataManager dataManager;
    private final BuffManager buffManager;

    public PlayerListener(DataManager dataManager, BuffManager buffManager) {
        this.dataManager = dataManager;
        this.buffManager = buffManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        dataManager.load(event.getPlayer().getUniqueId());
        // Buffs will be reapplied by LumberjackBuffs once we build it
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        buffManager.clearAllModifiers(event.getPlayer());
        dataManager.unload(event.getPlayer().getUniqueId());
    }
}