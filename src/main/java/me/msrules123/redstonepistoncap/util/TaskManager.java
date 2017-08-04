package me.msrules123.redstonepistoncap.util;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Manages a Queue of runnables and acts as medium for callbacks to the {@link ChunkManager}
 */
final class TaskManager {

    /**
     * A queue of tasks to run, avoid stalling server by running many at once
     */
    private final Queue<CountPistonTask> pistonCountTasks;

    /**
     * Reference to the ChunkManager, methods are used as callbacks
     * to supply the manager with loaded data from the tasks
     */
    private final ChunkManager chunkManager;

    /**
     * The default constructor
     * @param chunkManager the manager to give data to
     */
    TaskManager(ChunkManager chunkManager) {
        this.pistonCountTasks = new LinkedList<>();
        this.chunkManager = chunkManager;
    }

    /**
     * Adds task to the queue for the given chunk
     * @param chunk the chunk to be queued
     */
    void addTask(LiteChunk chunk) {
        pistonCountTasks.add(new CountPistonTask(chunkManager, chunk));
        runNextTask();
    }

    /**
     * Runs a task immediately, skipping the queue for the given chunk
     * (Used when a chunk should be counted, but isn't, due to fault with {@link org.bukkit.event.world.ChunkLoadEvent})
     * @param chunk the chunk to be immediately counted
     */
    void runTaskImmediately(LiteChunk chunk) {
        new CountPistonTask(chunkManager, chunk).startTask();
    }

    /**
     * Runs the next task in the queue
     */
    void runNextTask() {
        if (!(pistonCountTasks.isEmpty())) {
            pistonCountTasks.poll().startTask();
        }
    }

}
