package com.ShibuiKaleb.deepProfessions.listeners;

import com.ShibuiKaleb.deepProfessions.DeepProfessions;
import com.ShibuiKaleb.deepProfessions.buffs.BuffManager;
import com.ShibuiKaleb.deepProfessions.buffs.LumberjackBuffs;
import com.ShibuiKaleb.deepProfessions.data.DataManager;
import com.ShibuiKaleb.deepProfessions.data.PlayerData;
import com.ShibuiKaleb.deepProfessions.enums.Profession;
import com.ShibuiKaleb.deepProfessions.util.LevelUtil;
import com.ShibuiKaleb.deepProfessions.enums.Specialization;
import com.ShibuiKaleb.deepProfessions.util.TreeFeller;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class ProficiencyListener implements Listener {

    private final DeepProfessions plugin;
    private final DataManager dataManager;
    private final BuffManager buffManager;
    private final LumberjackBuffs lumberjackBuffs;

    private static final Set<Material> ORES = Set.of(
            Material.STONE, Material.COAL_ORE, Material.IRON_ORE, Material.COPPER_ORE,
            Material.GOLD_ORE, Material.LAPIS_ORE, Material.REDSTONE_ORE,
            Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.NETHER_GOLD_ORE,
            Material.NETHER_QUARTZ_ORE, Material.ANCIENT_DEBRIS,
            Material.DEEPSLATE_COAL_ORE, Material.DEEPSLATE_IRON_ORE,
            Material.DEEPSLATE_COPPER_ORE, Material.DEEPSLATE_GOLD_ORE,
            Material.DEEPSLATE_LAPIS_ORE, Material.DEEPSLATE_REDSTONE_ORE,
            Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_EMERALD_ORE
    );

    private static final Set<Material> LOGS = Set.of(
            Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG,
            Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG,
            Material.MANGROVE_LOG, Material.CHERRY_LOG, Material.CRIMSON_STEM,
            Material.WARPED_STEM
    );

    private static final Set<Class<? extends Entity>> PASSIVE_MOBS = Set.of(
            Cow.class, Sheep.class, Pig.class, Chicken.class, Rabbit.class,
            Horse.class, Llama.class, Fox.class, Wolf.class, Bee.class
    );

    private static final Set<Material> METAL_TOOLS_AND_WEAPONS = Set.of(
            Material.IRON_SWORD, Material.IRON_AXE, Material.IRON_PICKAXE,
            Material.IRON_SHOVEL, Material.IRON_HOE,
            Material.GOLDEN_SWORD, Material.GOLDEN_AXE, Material.GOLDEN_PICKAXE,
            Material.GOLDEN_SHOVEL, Material.GOLDEN_HOE,
            Material.DIAMOND_SWORD, Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE,
            Material.DIAMOND_SHOVEL, Material.DIAMOND_HOE,
            Material.NETHERITE_SWORD, Material.NETHERITE_AXE, Material.NETHERITE_PICKAXE,
            Material.NETHERITE_SHOVEL, Material.NETHERITE_HOE
    );

    private static final Set<Material> METAL_ARMOR = Set.of(
            Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
            Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS,
            Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
            Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS
    );

    private static final Set<Material> LEATHER_ARMOR = Set.of(
            Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS
    );

    private static final Set<Material> FOODS = Set.of(
            Material.BREAD, Material.COOKED_BEEF, Material.COOKED_PORKCHOP,
            Material.COOKED_CHICKEN, Material.COOKED_MUTTON, Material.COOKED_RABBIT,
            Material.COOKED_COD, Material.COOKED_SALMON, Material.BAKED_POTATO,
            Material.PUMPKIN_PIE, Material.CAKE, Material.COOKIE,
            Material.MUSHROOM_STEW, Material.RABBIT_STEW, Material.BEETROOT_SOUP
    );

    public ProficiencyListener(DeepProfessions plugin, DataManager dataManager, BuffManager buffManager, LumberjackBuffs lumberjackBuffs) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.buffManager = buffManager;
        this.lumberjackBuffs = lumberjackBuffs;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerData data = dataManager.get(player.getUniqueId());
        if (data == null || data.getProfession() == null) return;

        Material block = event.getBlock().getType();
        Profession profession = data.getProfession();

        if (profession == Profession.MINER && ORES.contains(block)) {
            double xp = plugin.getConfig().getDouble("xp-values.miner." + block.name(), 0);
            awardXP(player, data, xp);
        } else if (profession == Profession.LUMBERJACK && LOGS.contains(block)) {
            double xp = plugin.getConfig().getDouble("xp-values.lumberjack." + block.name(), 0);
            awardXP(player, data, xp);

            // Arborist tree felling
            Specialization spec = data.getSpecialization();
            if (spec == Specialization.ARBORIST) {
                int level = LevelUtil.getLevel(data.getProficiency(Profession.LUMBERJACK));
                double fellChance = 0;
                if (level >= 80) fellChance = 0.20;
                else if (level >= 45) fellChance = 0.10;

                plugin.getDebugLogger().log(player.getName() + " | Arborist tree fell roll"
                        + " | Level: " + level
                        + " | Chance: " + (fellChance * 100) + "%");

                if (fellChance > 0) {
                    TreeFeller.tryFell(player, event.getBlock(), fellChance);
                }
            }
        } else if (profession == Profession.FARMER) {
            if (isMatureCrop(event.getBlock())) {
                double xp = plugin.getConfig().getDouble("xp-values.farmer." + block.name(), 0);
                if (xp > 0) awardXP(player, data, xp);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player player)) return;
        PlayerData data = dataManager.get(player.getUniqueId());
        if (data == null || data.getProfession() != Profession.HUNTER) return;

        Entity entity = event.getEntity();
        boolean isPassive = PASSIVE_MOBS.stream().anyMatch(cls -> cls.isInstance(entity));
        if (!isPassive) return;

        double xp = plugin.getConfig().getDouble("xp-values.hunter." + entity.getType().name(), 0);
        if (xp > 0) awardXP(player, data, xp);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        Player player = event.getPlayer();
        PlayerData data = dataManager.get(player.getUniqueId());
        if (data == null || data.getProfession() != Profession.FISHER) return;

        double xp = plugin.getConfig().getDouble("xp-values.fisher.FISH", 8);
        awardXP(player, data, xp);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        PlayerData data = dataManager.get(player.getUniqueId());
        if (data == null || data.getProfession() == null) return;

        ItemStack result = event.getRecipe().getResult();
        Material mat = result.getType();
        Profession profession = data.getProfession();

        if (profession == Profession.BLACKSMITH) {
            if (METAL_TOOLS_AND_WEAPONS.contains(mat)) {
                double xp = plugin.getConfig().getDouble("xp-values.blacksmith.WEAPONS", 12);
                awardXP(player, data, xp);
            } else if (METAL_ARMOR.contains(mat)) {
                double xp = plugin.getConfig().getDouble("xp-values.blacksmith.ARMOR", 11);
                awardXP(player, data, xp);
            }
        } else if (profession == Profession.TAILOR && LEATHER_ARMOR.contains(mat)) {
            double xp = plugin.getConfig().getDouble("xp-values.tailor.LEATHER_ARMOR", 9);
            awardXP(player, data, xp);
        } else if (profession == Profession.CHEF && FOODS.contains(mat)) {
            double xp = plugin.getConfig().getDouble("xp-values.chef.FOOD", 7);
            awardXP(player, data, xp);
        }
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        for (Player player : event.getBlock().getWorld().getPlayers()) {
            if (event.getBlock().getLocation().distanceSquared(player.getLocation()) > 25) continue;
            PlayerData data = dataManager.get(player.getUniqueId());
            if (data == null || data.getProfession() != Profession.ALCHEMIST) continue;
            double xp = plugin.getConfig().getDouble("xp-values.alchemist.POTION", 10);
            awardXP(player, data, xp);
            break;
        }
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        PlayerData data = dataManager.get(player.getUniqueId());
        if (data == null || data.getProfession() != Profession.ARCANIST) return;

        double xp = plugin.getConfig().getDouble("xp-values.arcanist.ENCHANT", 12);
        awardXP(player, data, xp);
    }

    private void awardXP(Player player, PlayerData data, double amount) {
        if (amount <= 0) return;

        Profession profession = data.getProfession();
        double currentXP = data.getProficiency(profession);
        int levelBefore = LevelUtil.getLevel(currentXP);

        double newXP = currentXP + amount;
        data.setProficiency(profession, newXP);
        dataManager.save(player.getUniqueId());

        //Debug
        plugin.getDebugLogger().log(player.getName() + " | Profession: " + profession
                + " | +" + amount + " XP | Total: " + newXP
                + " | Level: " + LevelUtil.getLevel(newXP));

        int levelAfter = LevelUtil.getLevel(newXP);

        if (levelAfter > levelBefore) {
            onLevelUp(player, data, levelBefore, levelAfter);
        }
    }

    private void onLevelUp(Player player, PlayerData data, int oldLevel, int newLevel) {
        LevelUtil.Tier tier = LevelUtil.getTier(newLevel);

        player.sendMessage(
                Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                        .append(Component.text("Level up! You are now level " + newLevel + " (" + tier.name() + ")", NamedTextColor.YELLOW))
        );

        if (data.getProfession() == Profession.LUMBERJACK) {
            lumberjackBuffs.applyBuffs(player, data);
        }

        if (LevelUtil.isMinorMilestone(newLevel)) {
            player.sendMessage(
                    Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                            .append(Component.text("Minor milestone reached at level " + newLevel + "!", NamedTextColor.GREEN))
            );
        }

        if (LevelUtil.isMajorMilestone(newLevel)) {
            player.sendMessage(
                    Component.text("[DeepProfessions] ", NamedTextColor.GOLD)
                            .append(Component.text("You have reached level " + newLevel + "! Complete your " + tier.name() + " quest to advance.", NamedTextColor.AQUA))
            );
        }
    }

    private boolean isMatureCrop(org.bukkit.block.Block block) {
        if (block.getBlockData() instanceof org.bukkit.block.data.Ageable ageable) {
            return ageable.getAge() == ageable.getMaximumAge();
        }
        return false;
    }
}