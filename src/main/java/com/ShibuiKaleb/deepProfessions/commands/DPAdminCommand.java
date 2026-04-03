package com.ShibuiKaleb.deepProfessions.commands;

import com.ShibuiKaleb.deepProfessions.DeepProfessions;
import com.ShibuiKaleb.deepProfessions.data.DataManager;
import com.ShibuiKaleb.deepProfessions.data.PlayerData;
import com.ShibuiKaleb.deepProfessions.enums.Profession;
import com.ShibuiKaleb.deepProfessions.util.LevelUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DPAdminCommand implements CommandExecutor {

    private final DeepProfessions plugin;
    private final DataManager dataManager;

    public DPAdminCommand(DeepProfessions plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                    .append(Component.text("You don't have permission to use this command.", NamedTextColor.RED)));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "setlevel" -> handleSetLevel(sender, args);
            case "info" -> handleInfo(sender, args);
            default -> sendHelp(sender);
        }

        return true;
    }

    // /dpadmin info <player>
    private void handleInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                    .append(Component.text("Usage: /dpadmin info <player>", NamedTextColor.YELLOW)));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                    .append(Component.text("Player not found: " + args[1], NamedTextColor.RED)));
            return;
        }

        PlayerData data = dataManager.get(target.getUniqueId());
        if (data == null || data.getProfession() == null) {
            sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                    .append(Component.text(target.getName() + " has no profession yet.", NamedTextColor.RED)));
            return;
        }

        Profession profession = data.getProfession();
        double xp = data.getProficiency(profession);
        int level = LevelUtil.getLevel(xp);
        LevelUtil.Tier tier = LevelUtil.getTier(level);
        double progressIntoLevel = LevelUtil.getProgressIntoLevel(xp);
        double xpRequired = LevelUtil.getXpRequiredForCurrentLevel(xp);
        String spec = data.getSpecialization() != null ? data.getSpecialization().name() : "None";

        // Active buffs — check attribute modifiers
        org.bukkit.attribute.AttributeInstance blockBreak = target.getAttribute(
                org.bukkit.Registry.ATTRIBUTE.get(org.bukkit.NamespacedKey.minecraft("block_break_speed"))
        );
        org.bukkit.attribute.AttributeInstance moveSpeed = target.getAttribute(
                org.bukkit.Registry.ATTRIBUTE.get(org.bukkit.NamespacedKey.minecraft("movement_speed"))
        );
        org.bukkit.attribute.AttributeInstance attackDamage = target.getAttribute(
                org.bukkit.Registry.ATTRIBUTE.get(org.bukkit.NamespacedKey.minecraft("attack_damage"))
        );

        String blockBreakVal = blockBreak != null ? String.format("%.2f", blockBreak.getValue()) : "N/A";
        String moveSpeedVal = moveSpeed != null ? String.format("%.4f", moveSpeed.getValue()) : "N/A";
        String attackDamageVal = attackDamage != null ? String.format("%.2f", attackDamage.getValue()) : "N/A";

        sender.sendMessage(Component.text("--- " + target.getName() + " ---", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                .append(Component.text("Profession:      ", NamedTextColor.YELLOW))
                .append(Component.text(profession.name(), NamedTextColor.WHITE)));
        sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                .append(Component.text("Specialization:  ", NamedTextColor.YELLOW))
                .append(Component.text(spec, NamedTextColor.WHITE)));
        sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                .append(Component.text("Tier:            ", NamedTextColor.YELLOW))
                .append(Component.text(tier.name(), NamedTextColor.WHITE)));
        sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                .append(Component.text("Level:           ", NamedTextColor.YELLOW))
                .append(Component.text(String.valueOf(level), NamedTextColor.WHITE)));
        sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                .append(Component.text("XP:              ", NamedTextColor.YELLOW))
                .append(Component.text(String.format("%.0f / %.0f (%.0f total)", progressIntoLevel, xpRequired, xp), NamedTextColor.WHITE)));
        sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                .append(Component.text("Buffs:", NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                .append(Component.text("  Block Break Speed:  ", NamedTextColor.YELLOW))
                .append(Component.text(blockBreakVal, NamedTextColor.WHITE)));
        sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                .append(Component.text("  Movement Speed:     ", NamedTextColor.YELLOW))
                .append(Component.text(moveSpeedVal, NamedTextColor.WHITE)));
        sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                .append(Component.text("  Attack Damage:      ", NamedTextColor.YELLOW))
                .append(Component.text(attackDamageVal, NamedTextColor.WHITE)));
        sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                .append(Component.text("  Night Vision:       ", NamedTextColor.YELLOW))
                .append(Component.text(target.hasPotionEffect(org.bukkit.potion.PotionEffectType.NIGHT_VISION) ? "Active" : "Inactive", NamedTextColor.WHITE)));
    }

    // /dpadmin setlevel <player> <level>
    private void handleSetLevel(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                    .append(Component.text("Usage: /dpadmin setlevel <player> <level>", NamedTextColor.YELLOW)));
            return;
        }

        // Find target player
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                    .append(Component.text("Player not found: " + args[1], NamedTextColor.RED)));
            return;
        }

        // Parse level
        int level;
        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                    .append(Component.text("Invalid level: " + args[2], NamedTextColor.RED)));
            return;
        }

        if (level < 1 || level > LevelUtil.MAX_LEVEL) {
            sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                    .append(Component.text("Level must be between 1 and " + LevelUtil.MAX_LEVEL + ".", NamedTextColor.RED)));
            return;
        }

        // Get player data
        PlayerData data = dataManager.get(target.getUniqueId());
        if (data == null || data.getProfession() == null) {
            sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                    .append(Component.text(target.getName() + " has no profession.", NamedTextColor.RED)));
            return;
        }

        // Set XP to exactly the total needed for this level
        Profession profession = data.getProfession();
        double xpNeeded = LevelUtil.totalXpForLevel(level);
        data.setProficiency(profession, xpNeeded);
        dataManager.save(target.getUniqueId());

        // Reapply buffs for new level
        if (profession == Profession.LUMBERJACK) {
            plugin.getLumberjackBuffs().applyBuffs(target, data);
        }

        sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                .append(Component.text("Set " + target.getName() + " to level " + level
                        + " (" + LevelUtil.getTier(level).name() + ")"
                        + " — " + xpNeeded + " XP.", NamedTextColor.YELLOW)));

        target.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                .append(Component.text("An admin set your level to " + level
                        + " (" + LevelUtil.getTier(level).name() + ").", NamedTextColor.YELLOW)));
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text("--- DeepProfessions Admin ---", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                .append(Component.text("/dpadmin setlevel <player> <level> - Set a player's profession level", NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                .append(Component.text("/dpadmin info <player> - Show all profession info for a player", NamedTextColor.YELLOW)));
    }
}