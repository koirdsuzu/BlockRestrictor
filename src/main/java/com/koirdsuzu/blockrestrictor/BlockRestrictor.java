package com.koirdsuzu.blockrestrictor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockRestrictor extends JavaPlugin implements Listener {

    private Set<Material> allowedBlocks = new HashSet<>();
    private String deathMessage;
    private String reloadMessage;
    private String helpMessage;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("blockrestrictor").setExecutor(new BlockRestrictorCommand(this));
    }

    public void loadConfigValues() {
        FileConfiguration config = getConfig();

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
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Material blockUnder = player.getLocation().getBlock().getType();

        if (!allowedBlocks.contains(blockUnder)) {
            player.setHealth(0.0);
            player.sendMessage(deathMessage);
        }
    }

    public String getReloadMessage() {
        return reloadMessage;
    }

    public String getHelpMessage() {
        return helpMessage;
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
        }

        sender.sendMessage(ChatColor.RED + "Invalid command. Use /blockrestrictor help.");
        return true;
    }
}
