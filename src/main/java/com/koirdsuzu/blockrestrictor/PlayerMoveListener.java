package com.koirdsuzu.blockrestrictor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!BlockRestrictor.restrictionEnabled){
            return;// 無効化されている場合は処理しない
        }

        if (event.getPlayer().hasPermission("blockrestrictor.bypass")){
            return; // バイパス権限がある場合は処理しない
        }

        Player player = event.getPlayer();
        Material blockUnder = player.getLocation().subtract(0, 0.3, 0).getBlock().getType(); // 真下のブロックを取得

        if (blockUnder == Material.AIR || blockUnder == Material.CAVE_AIR){
            return; // 真下が空気なら何もしない
        }

        if (!BlockRestrictor.allowedBlocks.contains(blockUnder)) {
            BlockRestrictor.map.put(player, blockUnder);
            player.setHealth(0.0);
            player.sendMessage(BlockRestrictor.plugin.getDeathMessage());
        }
    }
}
