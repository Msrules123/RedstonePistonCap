package me.msrules123.redstonepistoncap.util;

import me.msrules123.redstonepistoncap.RedstonePistonCap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class LiteChunkLoader extends BukkitRunnable {

    private final Logger logger;
    private final ChunkManager chunkManager;
    private final File file;

    public LiteChunkLoader(RedstonePistonCap plugin, ChunkManager chunkManager) {
        this.logger = plugin.getLogger();
        this.chunkManager = chunkManager;
        this.file = new File(plugin.getDataFolder(), "pistoncount.txt");
        runTaskAsynchronously(plugin);
    }

    @Override
    public void run() {
        if (!(file.exists())) {
            return;
        }

        try {
            LineIterator iterator = FileUtils.lineIterator(file);

            String[] components;
            String line, world;
            int x, z, pistonCount;

            while (iterator.hasNext()) {
                line = iterator.nextLine();
                components = line.split(";");

                world = components[0];
                x = parseInt(components[1]);
                z = parseInt(components[2]);
                pistonCount = parseInt(components[3]);

                chunkManager.addEntry(new LiteChunk(world, x, z), pistonCount);
            }

            iterator.close();

            file.delete();
        } catch (IOException | NumberFormatException e) {
            logger.warning("Could not load data from pistoncount.txt!");
            e.printStackTrace();
        }
    }

    private int parseInt(String component) throws NumberFormatException {
        return Integer.parseInt(component);
    }
}


