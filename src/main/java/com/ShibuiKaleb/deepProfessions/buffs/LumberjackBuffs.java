package com.ShibuiKaleb.deepProfessions.buffs;

import com.ShibuiKaleb.deepProfessions.DeepProfessions;
import com.ShibuiKaleb.deepProfessions.data.PlayerData;
import com.ShibuiKaleb.deepProfessions.enums.Specialization;
import com.ShibuiKaleb.deepProfessions.util.LevelUtil;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LumberjackBuffs {

    private final BuffManager buffManager;
    private final DeepProfessions plugin;

    // Modifier IDs — used to apply and replace cleanly
    private static final String CHOP_SPEED = "lumberjack_chop_speed";
    private static final String MOVE_SPEED = "lumberjack_move_speed";
    private static final String FOREST_STRENGTH = "lumberjack_forest_strength";

    public LumberjackBuffs(BuffManager buffManager) {
        this.plugin = plugin;
        this.buffManager = buffManager;
    }

    // Called whenever a player's level changes — reapplies all buffs for their current level
    public void applyBuffs(Player player, PlayerData data) {
        int level = LevelUtil.getLevel(data.getProficiency(
                com.ShibuiKaleb.deepProfessions.enums.Profession.LUMBERJACK
        ));
        Specialization spec = data.getSpecialization();

        plugin.getDebugLogger().log(player.getName() + " | Applying Lumberjack buffs"
                + " | Level: " + level
                + " | Spec: " + (spec != null ? spec.name() : "none"));

        applySharedBuffs(player, level);

        if (level >= 40 && spec != null) {
            if (spec == Specialization.CARPENTER) {
                applyCarpenterBuffs(player, level);
            } else if (spec == Specialization.ARBORIST) {
                applyArboristBuffs(player, level);
            }
        }
    }

    // Remove all lumberjack buffs from a player
    public void clearBuffs(Player player) {
        Attribute blockBreakSpeed = Registry.ATTRIBUTE.get(
                org.bukkit.NamespacedKey.minecraft("player.block_break_speed")
        );
        Attribute moveSpeed = Registry.ATTRIBUTE.get(
                org.bukkit.NamespacedKey.minecraft("generic.movement_speed")
        );
        Attribute attackDamage = Registry.ATTRIBUTE.get(
                org.bukkit.NamespacedKey.minecraft("generic.attack_damage")
        );

        if (blockBreakSpeed != null) buffManager.removeModifier(player, blockBreakSpeed, CHOP_SPEED);
        if (moveSpeed != null) buffManager.removeModifier(player, moveSpeed, MOVE_SPEED);
        if (attackDamage != null) buffManager.removeModifier(player, attackDamage, FOREST_STRENGTH);

        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }

    private void applySharedBuffs(Player player, int level) {
        Attribute blockBreakSpeed = Registry.ATTRIBUTE.get(
                org.bukkit.NamespacedKey.minecraft("player.block_break_speed")
        );
        Attribute moveSpeed = Registry.ATTRIBUTE.get(
                org.bukkit.NamespacedKey.minecraft("generic.movement_speed")
        );
        Attribute attackDamage = Registry.ATTRIBUTE.get(
                org.bukkit.NamespacedKey.minecraft("generic.attack_damage")
        );

        // Chop speed — replaces previous value at each milestone
        if (blockBreakSpeed != null) {
            if (level >= 35) {
                buffManager.applyModifier(player, blockBreakSpeed, CHOP_SPEED, 0.12);
            } else if (level >= 15) {
                buffManager.applyModifier(player, blockBreakSpeed, CHOP_SPEED, 0.08);
            } else if (level >= 5) {
                buffManager.applyModifier(player, blockBreakSpeed, CHOP_SPEED, 0.04);
            }
        }

        // Movement speed in forests — permanent from level 20
        if (moveSpeed != null && level >= 20) {
            buffManager.applyModifier(player, moveSpeed, MOVE_SPEED, 0.05);
        }

        // Night vision in forests — permanent from level 40
        if (level >= 40) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.NIGHT_VISION,
                    Integer.MAX_VALUE,
                    0,
                    false,
                    false,
                    false
            ));
        }
    }

    private void applyCarpenterBuffs(Player player, int level) {
        Attribute blockBreakSpeed = Registry.ATTRIBUTE.get(
                org.bukkit.NamespacedKey.minecraft("player.block_break_speed")
        );

        // Chop speed Carpenter track — replaces shared chop speed
        if (blockBreakSpeed != null) {
            if (level >= 90) {
                buffManager.applyModifier(player, blockBreakSpeed, CHOP_SPEED, 0.25);
            } else if (level >= 70) {
                buffManager.applyModifier(player, blockBreakSpeed, CHOP_SPEED, 0.18);
            } else if (level >= 50) {
                buffManager.applyModifier(player, blockBreakSpeed, CHOP_SPEED, 0.15);
            }
        }
        // Note: rare wood drops, bonus log chance, plank crafting, and charcoal smelting
        // are event-driven and handled in ProficiencyListener — not attribute modifiers
    }

    private void applyArboristBuffs(Player player, int level) {
        Attribute blockBreakSpeed = Registry.ATTRIBUTE.get(
                org.bukkit.NamespacedKey.minecraft("player.block_break_speed")
        );

        // Chop speed Arborist track — replaces shared chop speed
        if (blockBreakSpeed != null) {
            if (level >= 90) {
                buffManager.applyModifier(player, blockBreakSpeed, CHOP_SPEED, 0.25);
            } else if (level >= 70) {
                buffManager.applyModifier(player, blockBreakSpeed, CHOP_SPEED, 0.18);
            } else if (level >= 50) {
                buffManager.applyModifier(player, blockBreakSpeed, CHOP_SPEED, 0.15);
            }
        }
        // Note: tree felling, 2-for-1 drops, haste scaling, honey drops, and ender bundle
        // are event-driven and handled in ProficiencyListener — not attribute modifiers
    }
}