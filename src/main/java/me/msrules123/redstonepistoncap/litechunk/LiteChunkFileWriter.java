package me.msrules123.redstonepistoncap.litechunk;

import me.msrules123.redstonepistoncap.ChunkManager;
import me.msrules123.redstonepistoncap.RedstonePistonCap;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class LiteChunkFileWriter extends BukkitRunnable {

    public static final char COORDINATE_FILE_DELIMITER = ';';
    public static final char CHUNK_FILE_DELIMITER = '\n';

    private final Logger logger;
    private final ChunkManager chunkManager;
    private final File file;

    public LiteChunkFileWriter(RedstonePistonCap plugin, ChunkManager chunkManager) {
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
                stringBuffer.append(COORDINATE_FILE_DELIMITER);

                stringBuffer.append(liteChunk.getX());
                stringBuffer.append(COORDINATE_FILE_DELIMITER);

                stringBuffer.append(liteChunk.getZ());
                stringBuffer.append(COORDINATE_FILE_DELIMITER);

                stringBuffer.append(entry.getValue());

                stringBuffer.append(CHUNK_FILE_DELIMITER);

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
