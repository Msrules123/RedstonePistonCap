package me.msrules123.redstonepistoncap.task;

import me.msrules123.redstonepistoncap.ChunkManager;
import me.msrules123.redstonepistoncap.RedstonePistonCap;
import me.msrules123.redstonepistoncap.litechunk.LiteChunk;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Counts the amount of pistons in a chunk, delayed for no server latency
 */
public final class CountPistonTask extends BukkitRunnable {

    private static final RedstonePistonCap PLUGIN = RedstonePistonCap.getPlugin(RedstonePistonCap.class);

    private static final int Y_BLOCK_INTERVAL = 32;
    private static final int MAX_Y_BLOCK = 256;

    private final ChunkManager chunkManager;
    private final LiteChunk chunk;

    private int pistonCount;
    private int currentYBlock;

    public CountPistonTask(ChunkManager chunkManager, LiteChunk chunk) {
        this.chunkManager = chunkManager;
        this.chunk = chunk;

        this.currentYBlock = MAX_Y_BLOCK;
        this.pistonCount = 0;
    }

    public void startTask() {
        runTaskTimer(PLUGIN, 0, 3);
    }

    @Override
    public void run() {
        Chunk bukkitChunk = chunk.getBukkitChunk();
        ChunkSnapshot chunkSnapshot;

        if (!(bukkitChunk.isLoaded())) {
            bukkitChunk.load();

            chunkSnapshot = bukkitChunk.getChunkSnapshot();
            bukkitChunk.unload();
        } else {
            chunkSnapshot = bukkitChunk.getChunkSnapshot();
        }

        Material currentMaterial;

        // Loop through all the blocks, find piston bases
        for (int y = currentYBlock; y > currentYBlock - Y_BLOCK_INTERVAL; y--) {
            for (int x =  0; x <= 15; x++) {
                for (int z = 0; z <= 15; z++) {
                    currentMaterial = Material.getMaterial(chunkSnapshot.getBlockTypeId(x, y, z));
                    if (currentMaterial == Material.PISTON_BASE || currentMaterial == Material.PISTON_STICKY_BASE) {
                        pistonCount++;
                    }
                }
            }
        }

        currentYBlock -= Y_BLOCK_INTERVAL;

        // If our loop is finished, add the piston count to the ChunkManager and cancel the runnable
        if (currentYBlock <= 0) {
            chunkManager.addEntry(chunk, pistonCount);
            cancel();
        }
    }

}
