package com.ShibuiKaleb.deepProfessions.util;

public class LevelUtil {

    public static final int MAX_LEVEL = 100;
    private static final double EXPONENT = 1.5;
    private static final double BASE = 100.0;

    // XP required to reach a specific level from the previous one
    public static double xpForLevel(int level) {
        return BASE * Math.pow(level, EXPONENT);
    }

    // Total XP required to reach a level from zero
    public static double totalXpForLevel(int level) {
        double total = 0;
        for (int i = 1; i <= level; i++) {
            total += xpForLevel(i);
        }
        return total;
    }

    // Convert raw proficiency XP into a level (1-100)
    public static int getLevel(double proficiency) {
        int level = 0;
        double remaining = proficiency;
        while (level < MAX_LEVEL) {
            double cost = xpForLevel(level + 1);
            if (remaining < cost) break;
            remaining -= cost;
            level++;
        }
        return Math.max(1, level);
    }

    // XP progress into the current level (for display purposes)
    public static double getProgressIntoLevel(double proficiency) {
        int level = getLevel(proficiency);
        double spent = totalXpForLevel(level - 1);
        return proficiency - spent;
    }

    // XP required to complete the current level
    public static double getXpRequiredForCurrentLevel(double proficiency) {
        int level = getLevel(proficiency);
        return xpForLevel(level);
    }

    // Which major tier is this level in
    public static Tier getTier(int level) {
        if (level <= 20) return Tier.APPRENTICE;
        if (level <= 40) return Tier.JOURNEYMAN;
        if (level <= 60) return Tier.EXPERT;
        if (level <= 80) return Tier.MASTER;
        return Tier.LEGENDARY;
    }

    // Whether this level is a minor milestone (every 5 levels)
    public static boolean isMinorMilestone(int level) {
        return level % 5 == 0;
    }

    // Whether this level is a major milestone (tier transition — every 20 levels)
    public static boolean isMajorMilestone(int level) {
        return level % 20 == 0;
    }

    // Whether a player has unlocked specialization (Expert tier — level 40+)
    public static boolean hasUnlockedSpecialization(int level) {
        return level >= 40;
    }

    public enum Tier {
        APPRENTICE,
        JOURNEYMAN,
        EXPERT,
        MASTER,
        LEGENDARY
    }
}