package io.github.jaemin0299.plugin.game

import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.ChatColor
import org.bukkit.Difficulty
import org.bukkit.GameMode
import org.bukkit.GameRule
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.UUID
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.random.Random

enum class Progress {
    Waiting,
    Ongoing,
    Ended
}

class Game(
    private val plugin: Plugin
) {
    private fun broadcast(message: String) {
        plugin.server.broadcast(Component.text(message))
    }
    private fun broadcastTitle(titleText: String, subtitleText: String) {
        plugin.server.onlinePlayers.forEach {
            it.showTitle(Title.title(Component.text(titleText), Component.text(subtitleText)))
        }
    }
    var progress = Progress.Waiting
    val playerList = ArrayList<Player>()
    private val kills = LinkedHashMap<UUID, Int?>()
    var worldBorderTimer = true
    var worldBorderReduction = true
    private val defaultWorldBorderReductionMinutes = 8
    private val defaultWorldBorderReductionSeconds = 0
    var worldBorderReductionMinutes = defaultWorldBorderReductionMinutes
    var worldBorderReductionSeconds = defaultWorldBorderReductionSeconds
    fun getPlayerCount(): Int {
        return playerList.size
    }
    fun getMaxPlayerCount(): Int {
        return plugin.server.maxPlayers
    }
    fun getKills(player: Player): Int? {
        return kills[player.uniqueId]
    }
    fun addKill(player: Player) {
        kills[player.uniqueId] = kills[player.uniqueId]?.plus(1)
    }
    fun setFail(player: Player) {
        val playerRanking = playerList.size
        playerList.remove(player)
        player.gameMode = GameMode.SPECTATOR
        player.world.strikeLightningEffect(player.location)
        broadcast("${ChatColor.RED}${player.name}님이 탈락하셨어요! (${ChatColor.WHITE}남은 생존자: ${getPlayerCount()}명${ChatColor.RED})")
        player.showTitle(Title.title(
            Component.text("${ChatColor.RED}탈락하셨어요!"),
            Component.text("${player.name}님은 ${playerRanking}위에요!")
        ))
        if (getPlayerCount() == 1) exit()
    }
    fun worldSetup() {
        val world = plugin.server.getWorld("world")
        val worldBorder = world?.worldBorder
        worldBorder?.setCenter(0.5 , 0.5)
        worldBorder?.size = 1000.0
        world?.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        world?.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        world?.time = 1000
        if (progress == Progress.Ongoing) world?.difficulty = Difficulty.EASY
        else world?.difficulty = Difficulty.PEACEFUL
    }
    fun start() {
        progress = Progress.Ongoing
        val worldBorder = plugin.server.getWorld("world")?.worldBorder
        worldSetup()
        var delay = 5L
        playerList.forEach {
            kills[it.uniqueId] = 0
            it.gameMode = GameMode.SURVIVAL
            plugin.server.scheduler.runTaskLater(plugin, Runnable {
                val randomX = Random.nextInt(-480, 480)
                val randomZ = Random.nextInt(-480, 480)
                it.teleport(it.location.set(
                    randomX.toDouble(),
                    it.world.getHighestBlockYAt(randomX, randomZ).toDouble() + 1,
                    randomZ.toDouble()
                ))
            }, delay)
            delay += 5L
        }
        broadcastTitle("${ChatColor.GREEN}GAME START!", "다른 생존자들보다 오래 생존하세요!")
        plugin.server.scheduler.runTaskTimer(plugin, Runnable {
            if (worldBorderTimer) {
                fun sendTitle(time: String) {
                    val text = "${ChatColor.YELLOW}월드 보더 축소까지 ${ChatColor.GOLD}${time} ${ChatColor.YELLOW}남았어요!"
                    broadcast(text)
                    broadcastTitle("", text)
                }
                if (worldBorderReductionSeconds == 0) {
                    worldBorderReductionMinutes -= 1
                    worldBorderReductionSeconds = 59
                }
                else worldBorderReductionSeconds -= 1
                if (worldBorderReductionSeconds == 0) {
                    if (worldBorderReductionMinutes == 7) sendTitle("7분")
                    if (worldBorderReductionMinutes == 5) sendTitle("5분")
                    if (worldBorderReductionMinutes == 3) sendTitle("3분")
                    if (worldBorderReductionMinutes == 1) sendTitle("1분")
                }
                if (worldBorderReductionMinutes == 0) {
                    if (worldBorderReductionSeconds == 30) sendTitle("30초")
                    if (worldBorderReductionSeconds == 15) sendTitle("15초")
                    if (worldBorderReductionSeconds == 10) sendTitle("10초")
                    if (worldBorderReductionSeconds == 9) sendTitle("9초")
                    if (worldBorderReductionSeconds == 8) sendTitle("8초")
                    if (worldBorderReductionSeconds == 7) sendTitle("7초")
                    if (worldBorderReductionSeconds == 6) sendTitle("6초")
                    if (worldBorderReductionSeconds == 5) sendTitle("5초")
                    if (worldBorderReductionSeconds == 4) sendTitle("4초")
                    if (worldBorderReductionSeconds == 3) sendTitle("3초")
                    if (worldBorderReductionSeconds == 2) sendTitle("2초")
                    if (worldBorderReductionSeconds == 1) sendTitle("1초")
                    if (worldBorderReductionSeconds == 0) {
                        worldBorderTimer = false
                        val text = "${ChatColor.RED}월드 보더 축소 중..."
                        broadcast(text)
                        broadcastTitle("", text)
                        worldBorder?.setSize(worldBorder.size / 2, 2 * 60)
                        plugin.server.scheduler.runTaskLater(plugin, Runnable {
                            if ((worldBorder?.size!! / 2) > 0) {
                                worldBorderReductionMinutes = defaultWorldBorderReductionMinutes
                                worldBorderReductionSeconds = defaultWorldBorderReductionSeconds
                                worldBorderTimer = true
                            }
                            else worldBorderReduction = false
                        }, 2 * 60 * 20L)
                    }
                }
            }
        }, 0, 1 * 20L)
    }
    fun exit() {
        progress = Progress.Ended
        worldBorderTimer = false
        worldBorderReduction = false
        playerList[0].gameMode = GameMode.SPECTATOR
        broadcastTitle("${ChatColor.RED}GAME OVER!", "${playerList[0].name}님이 승리하셨어요!")
    }
}