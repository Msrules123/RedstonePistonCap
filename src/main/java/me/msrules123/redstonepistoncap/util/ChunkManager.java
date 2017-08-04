package me.msrules123.redstonepistoncap.util;

import org.bukkit.Chunk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Designed to hold information about Chunks, including piston count
 * and whether or not the chunk is being scanned in an async thread
 */
public final class ChunkManager {

    /**
     * Holds a count of the number of pistons in a certain chunk, represented by a {@link LiteChunk}
     */
    private final Map<LiteChunk, Integer> pistonsPerChunk;

    /**
     * Holds references to chunks that are currently being scanned sync.
     */
    private final Set<LiteChunk> syncLockedChunks;

    private final TaskManager taskManager;

    private int pistonCap;

    public ChunkManager(int pistonCap) {
        this.pistonsPerChunk = new HashMap<>();
        this.syncLockedChunks = new HashSet<>();
        this.taskManager = new TaskManager(this);
        this.pistonCap = pistonCap;
    }

    public int getPistonCap() {
        return this.pistonCap;
    }

    public void setPistonCap(int pistonCap) {
        this.pistonCap = pistonCap;
    }

    Set<Map.Entry<LiteChunk, Integer>> getAllEntries() {
        return this.pistonsPerChunk.entrySet();
    }

    public void loadChunk(Chunk chunk) {
        LiteChunk liteChunk = LiteChunk.fromBukkitChunk(chunk);
        if (isChunkLoaded(chunk)) {
            return;
        }

        syncLockedChunks.add(liteChunk);

        taskManager.addTask(liteChunk);
    }

    public void loadChunkImmediately(Chunk chunk) {
        LiteChunk liteChunk = LiteChunk.fromBukkitChunk(chunk);
        if (isChunkLoaded(chunk)) {
            return;
        }
        syncLockedChunks.add(liteChunk);
        taskManager.runTaskImmediately(liteChunk);
    }


    void addEntry(LiteChunk chunk, int pistons) {
        pistonsPerChunk.put(chunk, getPistonsInChunk(chunk) + pistons);
        syncLockedChunks.remove(chunk);

        taskManager.runNextTask();
    }

    public int getPistonsInChunk(Chunk chunk) {
        LiteChunk liteChunk = LiteChunk.fromBukkitChunk(chunk);
        return getPistonsInChunk(liteChunk);
    }

    private int getPistonsInChunk(LiteChunk liteChunk) {
        return pistonsPerChunk.getOrDefault(liteChunk, 0);
    }

    public boolean isChunkLoaded(Chunk chunk) {
        return pistonsPerChunk.containsKey(LiteChunk.fromBukkitChunk(chunk));
    }

    public boolean isChunkLocked(Chunk chunk) {
        return syncLockedChunks.contains(LiteChunk.fromBukkitChunk(chunk));
    }

    public boolean isPistonCountCapped(Chunk chunk) {
        return (getPistonsInChunk(chunk) > pistonCap);
    }

    public void removePistonFromChunk(Chunk chunk) {
        LiteChunk liteChunk = LiteChunk.fromBukkitChunk(chunk);
        pistonsPerChunk.put(liteChunk, getPistonsInChunk(liteChunk) - 1);
    }

    public void addPistonToChunk(Chunk chunk) {
        LiteChunk liteChunk = LiteChunk.fromBukkitChunk(chunk);
        pistonsPerChunk.put(liteChunk, getPistonsInChunk(liteChunk) + 1);
    }

}
