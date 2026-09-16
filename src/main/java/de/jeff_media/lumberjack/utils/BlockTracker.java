package de.jeff_media.lumberjack.utils;

import com.jeff_media.customblockdata.CustomBlockData;
import de.jeff_media.lumberjack.LumberJack;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * Keeps track of player placed blocks by storing a marker in the chunk's PersistentDataContainer.
 * Replaces JeffLib's BlockTracker, which is no longer available.
 * <p>
 * All listeners run on MONITOR priority and must be registered after LumberJack's other listeners,
 * so that those can still check whether a block was placed by a player before the marker is removed.
 */
public class BlockTracker implements Listener {

    private static final Set<Material> TRACKED_TYPES = EnumSet.noneOf(Material.class);
    private static NamespacedKey key;
    private static LumberJack plugin;

    public static void init(LumberJack lumberJack) {
        plugin = lumberJack;
        key = new NamespacedKey(lumberJack, "player_placed");
        lumberJack.getServer().getPluginManager().registerEvents(new BlockTracker(), lumberJack);
    }

    public static void addTrackedBlockTypes(Collection<Material> materials) {
        TRACKED_TYPES.addAll(materials);
    }

    public static boolean isTrackedBlockType(Material material) {
        return TRACKED_TYPES.contains(material);
    }

    public static boolean isPlayerPlacedBlock(Block block) {
        if (!CustomBlockData.hasCustomBlockData(block, plugin)) return false;
        return new CustomBlockData(block, plugin).has(key, PersistentDataType.BYTE);
    }

    public static void setPlayerPlacedBlock(Block block, boolean playerPlaced) {
        CustomBlockData data = new CustomBlockData(block, plugin);
        if (playerPlaced) {
            data.set(key, PersistentDataType.BYTE, (byte) 1);
        } else {
            data.remove(key);
        }
    }

    private static void clear(Block block) {
        if (CustomBlockData.hasCustomBlockData(block, plugin)) {
            setPlayerPlacedBlock(block, false);
        }
    }

    private static void clear(Collection<Block> blocks) {
        blocks.forEach(BlockTracker::clear);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        setPlayerPlacedBlock(block, isTrackedBlockType(block.getType()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        clear(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBurn(BlockBurnEvent event) {
        clear(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExplode(BlockExplodeEvent event) {
        clear(event.blockList());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        clear(event.blockList());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        // Stripping a log keeps the marker, anything else (falling blocks landing, endermen, ...) removes it
        if (TreeUtils.matchesTrunkType(event.getBlock().getType(), event.getTo())) return;
        clear(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        movePistonBlocks(event.getBlocks(), event.getDirection());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        movePistonBlocks(event.getBlocks(), event.getDirection());
    }

    private void movePistonBlocks(List<Block> blocks, BlockFace direction) {
        List<Block> placed = new ArrayList<>();
        for (Block block : blocks) {
            if (isPlayerPlacedBlock(block)) {
                placed.add(block);
                setPlayerPlacedBlock(block, false);
            }
        }
        for (Block block : placed) {
            setPlayerPlacedBlock(block.getRelative(direction), true);
        }
    }
}
