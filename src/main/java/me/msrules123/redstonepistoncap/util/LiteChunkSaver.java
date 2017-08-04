package me.msrules123.redstonepistoncap.util;

import me.msrules123.redstonepistoncap.RedstonePistonCap;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class LiteChunkSaver extends BukkitRunnable {

    private final Logger logger;
    private final ChunkManager chunkManager;
    private final File file;

    public LiteChunkSaver(RedstonePistonCap plugin, ChunkManager chunkManager) {
        this.logger = plugin.getLogger();
        this.chunkManager = chunkManager;
        this.file = new File(plugin.getDataFolder(), "pistoncount.txt");
        run();
    }

    @Override
    public void run() {
        try {
            file.createNewFile();

            FileWriter fileWriter = new FileWriter(file);

            StringBuffer stringBuffer;
            for (Map.Entry<LiteChunk, Integer> entry : chunkManager.getAllEntries()) {
                stringBuffer = new StringBuffer();
                LiteChunk liteChunk = entry.getKey();

                stringBuffer.append(liteChunk.getWorld());
                stringBuffer.append(";" + liteChunk.getX());
                stringBuffer.append(";" + liteChunk.getZ());
                stringBuffer.append(";" + entry.getValue() + "\n");

                fileWriter.write(stringBuffer.toString());
            }

            fileWriter.close();
        } catch (IOException ex) {
            logger.warning("Could not save data to pistoncount.txt!");
            logger.warning("Data will be loaded for all chunks next start...");
            ex.printStackTrace();
        }

    }

}
