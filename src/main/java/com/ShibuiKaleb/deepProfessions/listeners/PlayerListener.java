package com.ShibuiKaleb.deepProfessions.listeners;

import com.ShibuiKaleb.deepProfessions.data.DataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final DataManager dataManager;

    public PlayerListener(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        dataManager.load(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        dataManager.unload(event.getPlayer().getUniqueId());
    }
}