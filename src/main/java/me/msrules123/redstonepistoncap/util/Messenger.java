package me.msrules123.redstonepistoncap.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Messenger {

    private final String prefix;

    private final String noPermission;
    private final String pistonCapChanged;
    private final String redstoneDisabled;
    private final String chunkIsLoading;
    private final String pistonsRemaining;
    private final String pistonsOver;

    public Messenger(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        this.prefix = config.getString("prefix");

        this.noPermission = config.getString("no-permission");
        this.pistonCapChanged = config.getString("piston-cap-changed");
        this.redstoneDisabled = config.getString("redstone-disabled");
        this.chunkIsLoading = config.getString("chunk-is-loading");
        this.pistonsRemaining = config.getString("pistons-remaining");
        this.pistonsOver = config.getString("pistons-over");
    }

    private String getAndColorMessageWithPrefix(String message) {
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }

    private String insertReplacementIntoMessageWithPrefix(String message, String toReplace, int cap) {
        return ChatColor.translateAlternateColorCodes('&', prefix +
            message.replace(toReplace, Integer.toString(cap)));
    }

    public void sendNoPermissionMessage(CommandSender sender) {
        sender.sendMessage(getAndColorMessageWithPrefix(noPermission));
    }

    public void sendPistonCapChangedMessage(CommandSender sender, int pistonCap) {
        sender.sendMessage(insertReplacementIntoMessageWithPrefix(pistonCapChanged, "{cap}", pistonCap));
    }

    public void sendRedstoneDisabledMessage(CommandSender sender, int pistonCap) {
        sender.sendMessage(insertReplacementIntoMessageWithPrefix(redstoneDisabled, "{cap}", pistonCap));
    }

    public void sendChunkIsLoadingMessage(CommandSender sender) {
        sender.sendMessage(getAndColorMessageWithPrefix(chunkIsLoading));
    }

    public void sendPistonsOverMessage(CommandSender sender, int pistonCap, int amount) {
        sender.sendMessage(insertReplacementIntoMessageWithPrefix(pistonsOver, "{amount}", amount - pistonCap));
    }

    public void sendPistonsRemainingMessage(CommandSender sender, int pistonCap, int amount) {
        sender.sendMessage(insertReplacementIntoMessageWithPrefix(pistonsRemaining, "{amount}", pistonCap - amount));
    }

}
