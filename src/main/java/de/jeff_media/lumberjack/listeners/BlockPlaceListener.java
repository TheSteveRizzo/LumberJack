package de.jeff_media.lumberjack.listeners;

import de.jeff_media.lumberjack.LumberJack;
import de.jeff_media.lumberjack.NBTKeys;
import de.jeff_media.lumberjack.utils.BlockTracker;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.persistence.PersistentDataType;

public class BlockPlaceListener implements Listener {

    private final LumberJack plugin;

    public BlockPlaceListener(LumberJack plugin) {
        this.plugin = plugin;
    }

    // Stripping logs with an axe fires an EntityChangeBlockEvent
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLogStrip(EntityChangeBlockEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Block block = event.getBlock();
        if(!BlockTracker.isTrackedBlockType(block.getType())) return;
        if(block.getType().name().startsWith("STRIPPED_") || !event.getTo().name().startsWith("STRIPPED_")) return;
        if(BlockTracker.isPlayerPlacedBlock(block)) return;
        plugin.getCustomDropManager().doCustomDrops(block.getLocation(), event.getTo());
    }


    // Prevent torches and stuff being placed inside a falling log
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {

        if (!plugin.getConfig().getBoolean("prevent-torch-exploit")) {
            return;
        }

        //System.out.println("possible conflicting block has been placed");

        for (Entity entity : e.getBlock().getLocation().getWorld().getNearbyEntities(e.getBlock().getLocation(), 1, 256, 1, entity -> entity instanceof FallingBlock)) {

            FallingBlock fallingBlock = (FallingBlock) entity;

            if (!fallingBlock.getPersistentDataContainer().has(new NamespacedKey(plugin, NBTKeys.IS_FALLING_LOG), PersistentDataType.STRING)) {
                continue;
            }

            if (fallingBlock.getLocation().getBlockX() != e.getBlockPlaced().getLocation().getBlockX()) {
                continue;
            }
            if (fallingBlock.getLocation().getBlockZ() != e.getBlockPlaced().getLocation().getBlockZ()) {
                continue;
            }
            if (fallingBlock.getLocation().getBlockY() < e.getBlockPlaced().getLocation().getBlockY()) {
                continue;
            }

            //if(plugin.treeUtils.isPartOfTree(fallingBlock.getBlockData().getMaterial())) {
            e.setCancelled(true);
            e.getPlayer().updateInventory();
            //}

        }
    }

}
