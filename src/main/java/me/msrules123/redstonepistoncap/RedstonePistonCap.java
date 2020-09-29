package me.msrules123.redstonepistoncap;

import me.msrules123.redstonepistoncap.litechunk.LiteChunkFileReader;
import me.msrules123.redstonepistoncap.litechunk.LiteChunkFileWriter;
import me.msrules123.redstonepistoncap.util.Messenger;
import me.msrules123.redstonepistoncap.listener.BlockListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class RedstonePistonCap extends JavaPlugin {

    private static final String CONFIG_FILE_NAME = "config.yml";
    private static final String MESSAGES_FILE_NAME = "messages.yml";

    private ChunkManager chunkManager;

    private Messenger messenger;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        File dataFolder = getDataFolder();
        if (!(dataFolder.exists())) {
            dataFolder.mkdir();
        }

        generateConfigFiles();

        this.messenger = new Messenger(getResourceFile(MESSAGES_FILE_NAME));
        this.config = YamlConfiguration.loadConfiguration(getResourceFile(CONFIG_FILE_NAME));;

        this.chunkManager = new ChunkManager(config.getInt("piston-cap"));

        new LiteChunkFileReader(this, chunkManager);
        new BlockListener(this, chunkManager, messenger);
    }

    @Override
    public void onDisable() {
        new LiteChunkFileWriter(this, chunkManager);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender.hasPermission("rpc.changecap"))) {
            messenger.sendNoPermissionMessage(sender);
            return true;
        }

        int newCap;
        try {
            newCap = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            return false;
        }

        chunkManager.setPistonCap(newCap);
        messenger.sendPistonCapChangedMessage(sender, newCap);
        config.set("piston-cap", newCap);

        return true;
    }

    private File getResourceFile(String fileName) {
        return new File(getDataFolder(), fileName);
    }

    private void generateConfigFiles() {
        generateConfigFile(CONFIG_FILE_NAME);
        generateConfigFile(MESSAGES_FILE_NAME);
    }

    private void generateConfigFile(String fileName) {
        File file = getResourceFile(fileName);
        if (!(file.exists())) {
            try {
                file.createNewFile();
                FileConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(
                        getResource(fileName)));
                config.save(file);
            } catch (IOException ex) {
                getLogger().severe("Could not load default file for file " + fileName);
                ex.printStackTrace();
            }
        }
    }

}
