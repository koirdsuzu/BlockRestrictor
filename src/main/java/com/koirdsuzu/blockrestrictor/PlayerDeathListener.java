package com.koirdsuzu.blockrestrictor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        final Player player = event.getEntity();
        final Material material = BlockRestrictor.map.get(player);
        if (material != null){
            final Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
            Team team = scoreboard.getEntryTeam(player.getName());
            final String prefix = team != null ? team.getPrefix() : "";
            event.setDeathMessage(prefix + player.getName() + "は死んだ (死因: " + material + " )");
            BlockRestrictor.map.remove(player);
        }
    }
}
