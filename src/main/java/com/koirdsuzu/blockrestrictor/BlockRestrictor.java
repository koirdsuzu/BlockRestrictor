package com.koirdsuzu.blockrestrictor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class BlockRestrictor extends JavaPlugin {
    public static BlockRestrictor plugin;
    public static FileConfiguration config;
    public static final List<Material> allowedBlocks = Lists.newArrayList();
    public static boolean restrictionEnabled;
    private String deathMessage, reloadMessage, helpMessage, enabledMessage, disabledMessage;
    public static final Map<Player, Material> map = Maps.newHashMap();

    @Override
    public void onEnable() {
        plugin = this;
        this.loadConfiguration();
        this.loadConfigValues();

        this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);

        final PluginCommand command = this.getCommand("blockrestrictor");
        if (command != null){
            command.setExecutor(new Commands());
        }
    }

    private void loadConfiguration(){
        final File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()){
            this.saveDefaultConfig();
        }

        config = this.getConfig();
    }

    public void loadConfigValues() {
        restrictionEnabled = config.getBoolean("enabled", true);

        allowedBlocks.clear();

        final List<String> blockList = config.getStringList("allowed-blocks");
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

    public void setRestrictionEnabled(boolean enabled) {
        restrictionEnabled = enabled;
        getConfig().set("enabled", enabled);
        saveConfig();
        reloadConfig();
        config = this.getConfig();
    }

    public String getDeathMessage(){
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