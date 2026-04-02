package com.ShibuiKaleb.deepProfessions.data;

import com.ShibuiKaleb.deepProfessions.DeepProfessions;
import com.ShibuiKaleb.deepProfessions.enums.Profession;
import com.ShibuiKaleb.deepProfessions.enums.Specialization;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager {

    private final DeepProfessions plugin;
    private final File playersFolder;
    private final Map<UUID, PlayerData> cache;

    public DataManager(DeepProfessions plugin) {
        this.plugin = plugin;
        this.cache = new HashMap<>();
        this.playersFolder = new File(plugin.getDataFolder(), "players");
        if (!playersFolder.exists()) {
            playersFolder.mkdirs();
        }
    }

    // Called when a player joins
    public void load(UUID uuid) {
        File file = getFile(uuid);

        if (!file.exists()) {
            // First time player — just create blank data in memory, no file yet
            cache.put(uuid, new PlayerData(uuid));
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        PlayerData data = new PlayerData(uuid);

        // Load profession (may be null if never chosen)
        String professionStr = config.getString("profession");
        if (professionStr != null) {
            data.setProfession(Profession.valueOf(professionStr));
        }

        // Load specialization (may be null if never chosen)
        String specStr = config.getString("specialization");
        if (specStr != null) {
            data.setSpecialization(Specialization.valueOf(specStr));
        }

        // Load proficiency map
        if (config.isConfigurationSection("proficiency")) {
            for (String key : config.getConfigurationSection("proficiency").getKeys(false)) {
                Profession prof = Profession.valueOf(key);
                double value = config.getDouble("proficiency." + key);
                data.setProficiency(prof, value);
            }
        }

        // Load last switch timestamp
        data.setLastSwitchTimestamp(config.getLong("last_switch", 0));

        cache.put(uuid, data);
    }

    // Called whenever data changes
    public void save(UUID uuid) {
        PlayerData data = cache.get(uuid);
        if (data == null) return;

        // Option B — don't write file if they haven't chosen a profession yet
        if (data.getProfession() == null) return;

        File file = getFile(uuid);
        YamlConfiguration config = new YamlConfiguration();

        config.set("profession", data.getProfession().name());

        if (data.getSpecialization() != null) {
            config.set("specialization", data.getSpecialization().name());
        }

        for (Map.Entry<Profession, Double> entry : data.getAllProficiency().entrySet()) {
            config.set("proficiency." + entry.getKey().name(), entry.getValue());
        }

        config.set("last_switch", data.getLastSwitchTimestamp());

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save data for " + uuid + ": " + e.getMessage());
        }
    }

    // Called when a player quits
    public void unload(UUID uuid) {
        save(uuid);
        cache.remove(uuid);
    }

    // Get a player's data from the cache
    public PlayerData get(UUID uuid) {
        return cache.get(uuid);
    }

    private File getFile(UUID uuid) {
        return new File(playersFolder, uuid.toString() + ".yml");
    }
}