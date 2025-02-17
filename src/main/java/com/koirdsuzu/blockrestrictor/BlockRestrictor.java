package com.koirdsuzu.blockrestrictor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockRestrictor extends JavaPlugin implements Listener {
    private Set<Material> allowedBlocks = new HashSet<>();
    private boolean restrictionEnabled;
    private String deathMessage, reloadMessage, helpMessage, enabledMessage, disabledMessage;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("blockrestrictor").setExecutor(new BlockRestrictorCommand(this));
    }

    public void loadConfigValues() {
        FileConfiguration config = getConfig();
        restrictionEnabled = config.getBoolean("enabled", true);

        allowedBlocks.clear();
        List<String> blockList = config.getStringList("allowed-blocks");
        for (String blockName : blockList) {
            try {
                allowedBlocks.add(Material.valueOf(blockName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("Invalid block in config: " + blockName);
            }
        }

        deathMessage = ChatColor.translateAlternateColorCodes('&', config.getString("messages.death", "&cYou stepped on a restricted block!"));
        reloadMessage = ChatColor.translateAlternateColorCodes('&', config.getString("messages.reload", "&aConfig reloaded!"));
        helpMessage = ChatColor.translateAlternateColorCodes('&', config.getString("messages.help", "&bUse /blockrestrictor reload to reload the config."));
        enabledMessage = ChatColor.translateAlternateColorCodes('&', config.getString("messages.enabled", "&aBlock restriction is now enabled."));
        disabledMessage = ChatColor.translateAlternateColorCodes('&', config.getString("messages.disabled", "&cBlock restriction is now disabled."));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!restrictionEnabled) return; // 無効化されている場合は処理しない

        Player player = event.getPlayer();
        Material blockUnder = player.getLocation().subtract(0, 1, 0).getBlock().getType(); // 真下のブロックを取得

        if (blockUnder == Material.AIR) return; // 真下が空気なら何もしない

        if (!allowedBlocks.contains(blockUnder)) {
            player.setHealth(0.0);
            player.sendMessage(deathMessage);
        }
    }

    public boolean isRestrictionEnabled() {
        return restrictionEnabled;
    }

    public void setRestrictionEnabled(boolean enabled) {
        this.restrictionEnabled = enabled;
        getConfig().set("enabled", enabled);
        saveConfig();
    }

    public String getDeathMessage() {
        return deathMessage;
    }

    public String getReloadMessage() {
        return reloadMessage;
    }

    public String getHelpMessage() {
        return helpMessage;
    }

    public String getEnabledMessage() {
        return enabledMessage;
    }

    public String getDisabledMessage() {
        return disabledMessage;
    }
}


class BlockRestrictorCommand implements CommandExecutor {
    private final BlockRestrictor plugin;

    public BlockRestrictorCommand(BlockRestrictor plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getHelpMessage());
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.loadConfigValues();
            sender.sendMessage(plugin.getReloadMessage());
            return true;
        } else if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(plugin.getHelpMessage());
            return true;
        } else if (args[0].equalsIgnoreCase("on")) {
            plugin.setRestrictionEnabled(true);
            sender.sendMessage(plugin.getEnabledMessage());
            return true;
        } else if (args[0].equalsIgnoreCase("off")) {
            plugin.setRestrictionEnabled(false);
            sender.sendMessage(plugin.getDisabledMessage());
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Invalid command. Use /blockrestrictor help.");
        return true;
    }
}