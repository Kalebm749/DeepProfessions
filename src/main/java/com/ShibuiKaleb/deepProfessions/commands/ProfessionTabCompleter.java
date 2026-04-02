package com.ShibuiKaleb.deepProfessions.commands;

import com.ShibuiKaleb.deepProfessions.enums.Profession;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfessionTabCompleter implements TabCompleter {

    private static final List<String> SUBCOMMANDS = Arrays.asList("choose", "change", "confirm");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return List.of();

        // First argument — suggest subcommands
        if (args.length == 1) {
            return filter(SUBCOMMANDS, args[0]);
        }

        // Second argument — suggest professions for choose and change
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("choose") || sub.equals("change")) {
                List<String> professions = new ArrayList<>();
                for (Profession p : Profession.values()) {
                    professions.add(p.name().charAt(0) + p.name().substring(1).toLowerCase());
                }
                return filter(professions, args[1]);
            }
        }

        return List.of();
    }

    // Filters suggestions to only those starting with what the player has typed so far
    private List<String> filter(List<String> options, String typed) {
        List<String> result = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase().startsWith(typed.toLowerCase())) {
                result.add(option);
            }
        }
        return result;
    }
}