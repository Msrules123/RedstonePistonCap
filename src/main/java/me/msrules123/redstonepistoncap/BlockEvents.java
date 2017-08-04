package me.msrules123.redstonepistoncap;

import me.msrules123.redstonepistoncap.util.ChunkManager;
import me.msrules123.redstonepistoncap.util.Messenger;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class BlockEvents implements Listener {

    private final Messenger messenger;
    private final Plugin plugin;
    private final ChunkManager chunkManager;

    public BlockEvents(RedstonePistonCap plugin, ChunkManager chunkManager, Messenger messenger) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
        this.chunkManager = chunkManager;
        this.messenger = messenger;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().stream()
                .filter(block ->
                        (block.getType() == Material.PISTON_BASE || block.getType() == Material.PISTON_STICKY_BASE))
                .forEach(block -> chunkManager.removePistonFromChunk(block.getChunk()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material blockType = block.getType();

        Chunk chunk = block.getLocation().getChunk();

        if (blockType != Material.PISTON_BASE && blockType != Material.PISTON_STICKY_BASE) {
            return;
        }

        if (loadChunk(chunk)) {
            event.setCancelled(true);
            messenger.sendChunkIsLoadingMessage(event.getPlayer());
            return;
        }

        if (chunkManager.isChunkLocked(chunk)) {
            messenger.sendChunkIsLoadingMessage(event.getPlayer());
            event.setCancelled(true);
            return;
        }

        chunkManager.removePistonFromChunk(chunk);

        sendMessages(event.getPlayer(), chunk);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Material blockType = block.getType();

        Chunk chunk = block.getLocation().getChunk();

        if (blockType != Material.PISTON_BASE && blockType != Material.PISTON_STICKY_BASE) {
            return;
        }

        if (loadChunk(chunk)) {
            event.setCancelled(true);
            messenger.sendChunkIsLoadingMessage(event.getPlayer());
            return;
        }

        if (chunkManager.isChunkLocked(chunk)) {
            event.setCancelled(true);
            messenger.sendChunkIsLoadingMessage(event.getPlayer());
            return;
        }

        chunkManager.addPistonToChunk(chunk);

        sendMessages(event.getPlayer(), chunk);
    }

    private void sendMessages(Player player, Chunk chunk) {
        if (chunkManager.isPistonCountCapped(chunk)) {
            messenger.sendRedstoneDisabledMessage(player, chunkManager.getPistonCap());
            messenger.sendPistonsOverMessage(player, chunkManager.getPistonCap(),
                    chunkManager.getPistonsInChunk(chunk));
        } else {
            messenger.sendPistonsRemainingMessage(player, chunkManager.getPistonCap(),
                    chunkManager.getPistonsInChunk(chunk));
        }
    }

    /**
     * Loads the chunk immediately if it doesn't already exist
     * @param chunk the chunk to be loaded
     * @return whether or not the chunk needed to be loaded
     */
    private boolean loadChunk(Chunk chunk) {
        if (!(chunkManager.isChunkLoaded(chunk))) {
            chunkManager.loadChunkImmediately(chunk);
            return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkLoadEvent(ChunkLoadEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                chunkManager.loadChunk(event.getChunk());
            }

        }.runTaskLater(plugin, 10);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRedstoneEvent(BlockRedstoneEvent event) {
        Chunk chunk = event.getBlock().getChunk();
        loadChunk(chunk);

       if (chunkManager.isPistonCountCapped(chunk)) {
           event.setNewCurrent(event.getOldCurrent());
           return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        Chunk chunk = event.getBlock().getChunk();
        loadChunk(chunk);

        if (chunkManager.isPistonCountCapped(chunk)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPistonExtend(BlockPistonRetractEvent event) {
        Chunk chunk = event.getBlock().getChunk();
        loadChunk(chunk);

        if (chunkManager.isPistonCountCapped(chunk)) {
            event.setCancelled(true);
            return;
        }
    }

}