package com.ShibuiKaleb.deepProfessions.util;

import com.ShibuiKaleb.deepProfessions.DeepProfessions;

public class DebugLogger {

    private final DeepProfessions plugin;

    public DebugLogger(DeepProfessions plugin) {
        this.plugin = plugin;
    }

    public void log(String message) {
        if (plugin.getConfig().getBoolean("debug", false)) {
            plugin.getLogger().info("[DEBUG] " + message);
        }
    }
}