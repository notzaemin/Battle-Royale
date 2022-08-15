package io.github.jaemin0299.plugin.game

import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.event.player.PlayerQuitEvent

class Event(
    private val game: Game
): Listener {
    @EventHandler
    private fun playerJoinEvent(event: PlayerJoinEvent) {
        if (game.progress == Progress.Waiting) {
            game.playerList.add(event.player)
            event.player.gameMode = GameMode.ADVENTURE
            event.joinMessage(Component.text(
                "${ChatColor.YELLOW}${event.player.name}님이 게임에 참여하셨어요! (${ChatColor.AQUA}${game.getPlayerCount()}${ChatColor.YELLOW}/${ChatColor.AQUA}${game.getMaxPlayerCount()}${ChatColor.YELLOW})"
            ))
        }
        if (game.progress == Progress.Ongoing) {
            event.player.gameMode = GameMode.SPECTATOR
            event.joinMessage(Component.text("${ChatColor.GRAY}${event.player.name}님이 게임에 참여하셨어요!"))
        }
    }
    @EventHandler
    private fun playerQuitEvent(event: PlayerQuitEvent) {
        if (game.progress == Progress.Waiting) {
            game.playerList.remove(event.player)
            event.quitMessage(Component.text(
                "${ChatColor.YELLOW}${event.player.name}님이 게임을 떠나셨어요! (${ChatColor.AQUA}${game.getPlayerCount()}${ChatColor.YELLOW}/${ChatColor.AQUA}${game.getMaxPlayerCount()}${ChatColor.YELLOW})"
            ))
        }
        if (game.progress == Progress.Ongoing) {
            event.quitMessage(Component.text("${ChatColor.GRAY}${event.player.name}님이 게임을 떠나셨어요!"))
            if (game.playerList.contains(event.player)) game.setFail(event.player)
        }
    }
    @EventHandler
    private fun playerDeathEvent(event: PlayerDeathEvent) {
        if (game.progress == Progress.Ongoing && game.playerList.contains(event.player)) {
            event.isCancelled = true
            game.setFail(event.player)
            if (event.player.killer != null) game.addKill(event.player.killer!!)
        }
    }
    @EventHandler
    private fun playerAttemptPickupItemEvent(event: PlayerAttemptPickupItemEvent) {
        event.isCancelled = (game.progress == Progress.Waiting)
    }
    @EventHandler
    private fun entityDamageEvent(event: EntityDamageEvent) {
        event.isCancelled = (game.progress == Progress.Waiting)
    }
    @EventHandler
    private fun playerPortalEvent(event: PlayerPortalEvent) {
        event.isCancelled = true
        event.player.showTitle(Title.title(
            Component.text(""),
            Component.text("${ChatColor.RED}이 게임에서는 포탈을 이용할 수 없어요!")
        ))
    }
}