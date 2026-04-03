package com.ShibuiKaleb.deepProfessions.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class TreeFeller {

    private static final int MAX_LOGS = 64;

    private static final Set<Material> LOGS = Set.of(
            Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG,
            Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG,
            Material.MANGROVE_LOG, Material.CHERRY_LOG, Material.CRIMSON_STEM,
            Material.WARPED_STEM
    );

    private static final Set<Material> LEAVES = Set.of(
            Material.OAK_LEAVES, Material.SPRUCE_LEAVES, Material.BIRCH_LEAVES,
            Material.JUNGLE_LEAVES, Material.ACACIA_LEAVES, Material.DARK_OAK_LEAVES,
            Material.MANGROVE_LEAVES, Material.CHERRY_LEAVES, Material.AZALEA_LEAVES,
            Material.FLOWERING_AZALEA_LEAVES
    );

    // Returns true if the felling was triggered, false if conditions not met
    public static boolean tryFell(Player player, Block brokenBlock, double fellChance) {
        // Must be holding an axe
        ItemStack held = player.getInventory().getItemInMainHand();
        if (!isAxe(held.getType())) return false;

        // Must be a log
        if (!LOGS.contains(brokenBlock.getType())) return false;

        // Roll the chance
        if (Math.random() >= fellChance) return false;

        // Detect the tree
        List<Block> treeLogs = detectTree(brokenBlock);
        if (treeLogs.isEmpty()) return false;

        // Break all logs and drop items naturally
        for (Block log : treeLogs) {
            log.breakNaturally(held);
        }

        return true;
    }

    // BFS flood fill to find all connected logs — smart detection included
    private static List<Block> detectTree(Block origin) {
        List<Block> found = new ArrayList<>();
        Set<Block> visited = new HashSet<>();
        Queue<Block> queue = new LinkedList<>();

        queue.add(origin);
        visited.add(origin);

        Material logType = origin.getType();

        while (!queue.isEmpty() && found.size() < MAX_LOGS) {
            Block current = queue.poll();
            found.add(current);

            // Check all 6 direct neighbors + diagonals above
            for (Block neighbor : getNeighbors(current)) {
                if (visited.contains(neighbor)) continue;
                visited.add(neighbor);

                // Only follow same log type — prevents eating unrelated trees
                if (neighbor.getType() != logType) continue;

                // Smart check — ignore logs that are floating (no log or ground below)
                if (!hasSupport(neighbor, logType)) continue;

                queue.add(neighbor);
            }
        }

        // Sanity check — if we didn't find any leaves nearby it's probably
        // not a real tree (e.g. a player-built log structure)
        if (!hasNearbyLeaves(origin, found)) return new ArrayList<>();

        return found;
    }

    // Gets all neighbors — sides, above, and diagonals above (trees branch out)
    private static List<Block> getNeighbors(Block block) {
        List<Block> neighbors = new ArrayList<>();
        int[][] offsets = {
                {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}, // sides
                {0, 1, 0},                                        // directly above
                {1, 1, 0}, {-1, 1, 0}, {0, 1, 1}, {0, 1, -1},  // diagonals above
                {1, 1, 1}, {-1, 1, 1}, {1, 1, -1}, {-1, 1, -1} // corners above
        };
        for (int[] offset : offsets) {
            neighbors.add(block.getRelative(offset[0], offset[1], offset[2]));
        }
        return neighbors;
    }

    // A log has support if there is another log or a solid ground block below it
    private static boolean hasSupport(Block block, Material logType) {
        Block below = block.getRelative(0, -1, 0);
        return below.getType() == logType || below.getType().isSolid();
    }

    // Checks if there are any leaves within 4 blocks of the tree
    // This is the key smart detection — player-built log structures won't have leaves
    private static boolean hasNearbyLeaves(Block origin, List<Block> logs) {
        for (Block log : logs) {
            for (int x = -2; x <= 2; x++) {
                for (int y = -1; y <= 3; y++) {
                    for (int z = -2; z <= 2; z++) {
                        if (LEAVES.contains(log.getRelative(x, y, z).getType())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean isAxe(Material material) {
        return switch (material) {
            case WOODEN_AXE, STONE_AXE, IRON_AXE,
                 GOLDEN_AXE, DIAMOND_AXE, NETHERITE_AXE -> true;
            default -> false;
        };
    }
}