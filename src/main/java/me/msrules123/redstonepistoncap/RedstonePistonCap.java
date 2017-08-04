package me.msrules123.redstonepistoncap;

import me.msrules123.redstonepistoncap.util.ChunkManager;
import me.msrules123.redstonepistoncap.util.LiteChunkLoader;
import me.msrules123.redstonepistoncap.util.LiteChunkSaver;
import me.msrules123.redstonepistoncap.util.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class RedstonePistonCap extends JavaPlugin {

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
        messenger = new Messenger(new File(getDataFolder(), "messages.yml"));
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));;

        chunkManager = new ChunkManager(config.getInt("piston-cap"));
        new LiteChunkLoader(this, chunkManager);
        new BlockEvents(this, chunkManager, messenger);
    }

    @Override
    public void onDisable() {
        new LiteChunkSaver(this, chunkManager);
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

    private void generateConfigFiles() {
        generateConfigFile("config.yml");
        generateConfigFile("messages.yml");
    }

    private void generateConfigFile(String fileName) {
        File file = new File(getDataFolder(), fileName);
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
