package com.ShibuiKaleb.deepProfessions.commands;

import com.ShibuiKaleb.deepProfessions.DeepProfessions;
import com.ShibuiKaleb.deepProfessions.data.DataManager;
import com.ShibuiKaleb.deepProfessions.data.PlayerData;
import com.ShibuiKaleb.deepProfessions.enums.Profession;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfessionCommand implements CommandExecutor {

    private final DeepProfessions plugin;
    private final DataManager dataManager;

    private final Map<UUID, Profession> pendingChanges = new HashMap<>();
    private final Map<UUID, Profession> pendingChoices = new HashMap<>();

    public ProfessionCommand(DeepProfessions plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "choose"  -> handleChoose(player, args);
            case "change"  -> handleChange(player, args);
            case "confirm" -> handleConfirm(player);
            default        -> sendHelp(player);
        }

        return true;
    }

    private void handleChoose(Player player, String[] args) {
        PlayerData data = dataManager.get(player.getUniqueId());

        if (data.getProfession() != null) {
            msg(player, "You already have a profession. Use /profession change <profession> to switch.");
            return;
        }

        if (args.length < 2) {
            msg(player, "Usage: /profession choose <profession>");
            return;
        }

        Profession chosen = parseProfession(player, args[1]);
        if (chosen == null) return;

        pendingChoices.put(player.getUniqueId(), chosen);
        long cooldownHours = plugin.getConfig().getLong("change-cooldown-hours", 24);
        msg(player, "You are about to choose " + formatProfession(chosen) + " as your profession.");
        msg(player, "You will not be able to change professions for " + cooldownHours + " hours.");
        msg(player, "Run /profession confirm to proceed, or do nothing to cancel.");
    }

    private void handleChange(Player player, String[] args) {
        PlayerData data = dataManager.get(player.getUniqueId());

        if (data.getProfession() == null) {
            msg(player, "You haven't chosen a profession yet. Use /profession choose <profession>.");
            return;
        }

        if (args.length < 2) {
            msg(player, "Usage: /profession change <profession>");
            return;
        }

        // Check cooldown
        long cooldownHours = plugin.getConfig().getLong("change-cooldown-hours", 24);
        long cooldownMillis = cooldownHours * 60 * 60 * 1000;
        long elapsed = System.currentTimeMillis() - data.getLastSwitchTimestamp();

        if (elapsed < cooldownMillis) {
            long hoursLeft = (cooldownMillis - elapsed) / (60 * 60 * 1000);
            long minutesLeft = ((cooldownMillis - elapsed) % (60 * 60 * 1000)) / (60 * 1000);
            msg(player, "You must wait " + hoursLeft + "h " + minutesLeft + "m before changing professions.");
            return;
        }

        Profession chosen = parseProfession(player, args[1]);
        if (chosen == null) return;

        if (chosen == data.getProfession()) {
            msg(player, "You are already a " + formatProfession(chosen) + ".");
            return;
        }

        pendingChanges.put(player.getUniqueId(), chosen);
        msg(player, "Warning: Changing to " + formatProfession(chosen) + " will wipe all your current profession data.");
        msg(player, "Run /profession confirm to proceed, or do nothing to cancel.");
    }

    private void handleConfirm(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData data = dataManager.get(uuid);

        if (pendingChoices.containsKey(uuid)) {
            Profession chosen = pendingChoices.remove(uuid);
            pendingChanges.remove(uuid);

            data.setProfession(chosen);
            data.setLastSwitchTimestamp(System.currentTimeMillis());
            dataManager.save(uuid);

            msg(player, "Welcome, " + formatProfession(chosen) + "! Your journey begins now.");
            return;
        }

        if (pendingChanges.containsKey(uuid)) {
            Profession chosen = pendingChanges.remove(uuid);

            data.getAllProficiency().clear();
            data.setSpecialization(null);
            data.setProfession(chosen);
            data.setLastSwitchTimestamp(System.currentTimeMillis());
            data.getCompletedQuests().clear();
            dataManager.save(uuid);

            msg(player, "You are now a " + formatProfession(chosen) + ". Your previous profession data has been cleared.");
            return;
        }

        msg(player, "You have no pending profession change to confirm.");
    }

    private Profession parseProfession(Player player, String input) {
        try {
            return Profession.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            msg(player, "Unknown profession: " + input + ".");
            return null;
        }
    }

    private String formatProfession(Profession profession) {
        String name = profession.name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }

    private void msg(Player player, String text) {
        player.sendMessage(
                Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                        .append(Component.text(text, NamedTextColor.YELLOW))
        );
    }

    private void sendHelp(Player player) {
        player.sendMessage(Component.text("--- DeepProfessions ---", NamedTextColor.GOLD));
        msg(player, "/profession choose <profession> - Choose your first profession");
        msg(player, "/profession change <profession> - Change your profession");
        msg(player, "/profession confirm - Confirm a pending profession change");
    }
}