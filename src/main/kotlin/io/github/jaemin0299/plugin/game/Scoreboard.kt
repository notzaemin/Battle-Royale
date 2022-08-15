package io.github.jaemin0299.plugin.game

import org.bukkit.ChatColor
import org.bukkit.Server
import org.bukkit.scoreboard.DisplaySlot

class Scoreboard(
    private val server: Server,
    private val game: Game
): Runnable {
    override fun run() {
        server.onlinePlayers.forEach {
            val scoreboard = server.scoreboardManager.newScoreboard
            val objective = scoreboard.registerNewObjective("BATTLE ROYALE", "dummy", "${ChatColor.AQUA}BATTLE ROYALE")
            objective.displaySlot = DisplaySlot.SIDEBAR
            if (game.worldBorderReduction) {
                if (!game.worldBorderTimer) objective.getScore("${ChatColor.RED}월드 보더 축소 중...").score = 6
                else objective.getScore("월드 보더 축소까지: ${ChatColor.GREEN}${game.worldBorderReductionMinutes}분 ${game.worldBorderReductionSeconds}초").score = 6
                objective.getScore("   ").score = 5
            }
            objective.getScore("남은 생존자: ${ChatColor.GREEN}${game.getPlayerCount()}명").score = 4
            objective.getScore("  ").score = 3
            objective.getScore("처치 횟수: ${ChatColor.GREEN}${game.getKills(it)}회".replace("null", "0")).score = 2
            objective.getScore(" ").score = 1
            objective.getScore("좌표: ${ChatColor.GREEN}${it.location.x.toInt()}, ${it.location.y.toInt()}, ${it.location.z.toInt()}").score = 0
            it.scoreboard = scoreboard
        }
    }
}