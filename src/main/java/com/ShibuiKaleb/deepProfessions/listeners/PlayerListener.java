package com.ShibuiKaleb.deepProfessions.listeners;

import com.ShibuiKaleb.deepProfessions.data.DataManager;
import com.ShibuiKaleb.deepProfessions.buffs.BuffManager;
import com.ShibuiKaleb.deepProfessions.buffs.LumberjackBuffs;
import com.ShibuiKaleb.deepProfessions.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final DataManager dataManager;
    private final BuffManager buffManager;
    private final LumberjackBuffs lumberjackBuffs;

    public PlayerListener(DataManager dataManager, BuffManager buffManager, LumberjackBuffs lumberjackBuffs) {
        this.dataManager = dataManager;
        this.buffManager = buffManager;
        this.lumberjackBuffs = lumberjackBuffs;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        dataManager.load(player.getUniqueId());
        PlayerData data = dataManager.get(player.getUniqueId());
        if (data == null || data.getProfession() == null) return;

        // Reapply buffs on login
        if (data.getProfession() == com.ShibuiKaleb.deepProfessions.enums.Profession.LUMBERJACK) {
            lumberjackBuffs.applyBuffs(player, data);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        buffManager.clearAllModifiers(event.getPlayer());
        dataManager.unload(event.getPlayer().getUniqueId());
    }
}