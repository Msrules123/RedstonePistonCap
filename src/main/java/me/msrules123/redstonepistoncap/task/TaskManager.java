package me.msrules123.redstonepistoncap.task;

import me.msrules123.redstonepistoncap.ChunkManager;
import me.msrules123.redstonepistoncap.litechunk.LiteChunk;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Manages a Queue of runnables and acts as medium for callbacks to the {@link ChunkManager}
 */
public final class TaskManager {

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
    public TaskManager(ChunkManager chunkManager) {
        this.pistonCountTasks = new LinkedList<>();
        this.chunkManager = chunkManager;
    }

    /**
     * Adds task to the queue for the given chunk
     * @param chunk the chunk to be queued
     */
    public void addTask(LiteChunk chunk) {
        pistonCountTasks.add(new CountPistonTask(chunkManager, chunk));

        runNextTask();
    }

    /**
     * Runs a task immediately, skipping the queue for the given chunk
     * (Used when a chunk should be counted, but isn't, due to fault with {@link org.bukkit.event.world.ChunkLoadEvent})
     * @param chunk the chunk to be immediately counted
     */
    public void runTaskImmediately(LiteChunk chunk) {
        new CountPistonTask(chunkManager, chunk).startTask();
    }

    /**
     * Runs the next task in the queue
     */
    public void runNextTask() {
        if (pistonCountTasks.isEmpty()) {
            return;
        }

        pistonCountTasks.poll().startTask();
    }

}
