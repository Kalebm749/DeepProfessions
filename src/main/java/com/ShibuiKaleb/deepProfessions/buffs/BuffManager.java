package com.ShibuiKaleb.deepProfessions.buffs;

import com.ShibuiKaleb.deepProfessions.DeepProfessions;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class BuffManager {

    private final DeepProfessions plugin;

    // Tracks which modifier keys are currently applied to each player
    // so we can cleanly remove them later
    private final Map<String, NamespacedKey> activeModifiers = new HashMap<>();

    public BuffManager(DeepProfessions plugin) {
        this.plugin = plugin;
    }

    // Apply a percentage attribute modifier to a player
    // id = unique string key e.g. "lumberjack_chop_speed"
    // percentage = 0.04 for 4%, 0.15 for 15% etc
    public void applyModifier(Player player, Attribute attribute, String id, double percentage) {
        removeModifier(player, attribute, id);

        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;

        NamespacedKey key = new NamespacedKey(plugin, id + "_" + player.getUniqueId());
        AttributeModifier modifier = new AttributeModifier(
                key,
                percentage,
                AttributeModifier.Operation.MULTIPLY_SCALAR_1,
                org.bukkit.inventory.EquipmentSlotGroup.ANY
        );

        instance.addModifier(modifier);
        activeModifiers.put(player.getUniqueId() + "_" + id, key);
    }

    // Remove a specific modifier from a player
    public void removeModifier(Player player, Attribute attribute, String id) {
        String cacheKey = player.getUniqueId() + "_" + id;
        NamespacedKey key = activeModifiers.remove(cacheKey);
        if (key == null) return;

        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;

        instance.getModifiers().stream()
                .filter(m -> m.getKey().equals(key))
                .findFirst()
                .ifPresent(instance::removeModifier);
    }

    // Remove ALL profession modifiers from a player (called on logout or profession change)
    public void clearAllModifiers(Player player) {
        String uuidPrefix = player.getUniqueId().toString();
        activeModifiers.entrySet().removeIf(entry -> {
            if (!entry.getKey().startsWith(uuidPrefix)) return false;

            // Extract the attribute from the key and remove the modifier
            for (Attribute attr : org.bukkit.Registry.ATTRIBUTE) {
                AttributeInstance instance = player.getAttribute(attr);
                if (instance == null) continue;
                instance.getModifiers().stream()
                        .filter(m -> m.getKey().equals(entry.getValue()))
                        .findFirst()
                        .ifPresent(instance::removeModifier);
            }
            return true;
        });
    }

    // Check if a modifier is currently active on a player
    public boolean hasModifier(Player player, String id) {
        return activeModifiers.containsKey(player.getUniqueId() + "_" + id);
    }
}