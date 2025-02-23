package com.koirdsuzu.blockrestrictor;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import javax.annotation.Nonnull;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] strings) {
        if (strings.length == 0){
            commandSender.sendMessage(BlockRestrictor.plugin.getHelpMessage());
            return true;
        }

        switch (strings[0]){
            case "reload" -> {
                BlockRestrictor.plugin.reloadConfig();
                BlockRestrictor.plugin.loadConfigValues();
                commandSender.sendMessage(BlockRestrictor.plugin.getReloadMessage());
            }
            case "help" -> commandSender.sendMessage(BlockRestrictor.plugin.getHelpMessage());
            case "on" -> {
                BlockRestrictor.plugin.setRestrictionEnabled(true);
                commandSender.sendMessage(BlockRestrictor.plugin.getEnabledMessage());
            }
            case "off" -> {
                BlockRestrictor.plugin.setRestrictionEnabled(false);
                commandSender.sendMessage(BlockRestrictor.plugin.getDisabledMessage());
            }
        }

        commandSender.sendMessage(ChatColor.RED + "Invalid command. Use /blockrestrictor help.");
        return true;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] strings) {
        final List<String> complete = Lists.newArrayList();
        if (strings.length == 0){
            complete.add("reload");
            complete.add("help");
            complete.add("on");
            complete.add("off");
        }else if (strings.length == 1){
            if (strings[0].isEmpty()){
                complete.add("reload");
                complete.add("help");
                complete.add("on");
                complete.add("off");
            }else if ("reload".startsWith(strings[0])){
                complete.add("reload");
            }else if ("help".startsWith(strings[0])){
                complete.add("help");
            }else if ("on".startsWith(strings[0])){
                complete.add("on");
            }else if ("off".startsWith(strings[0])){
                complete.add("off");
            }
        }
        return complete;
    }
}
