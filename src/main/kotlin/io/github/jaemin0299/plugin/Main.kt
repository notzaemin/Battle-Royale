package io.github.jaemin0299.plugin

import io.github.jaemin0299.plugin.game.Event
import io.github.jaemin0299.plugin.game.Game
import io.github.jaemin0299.plugin.game.Scoreboard
import io.github.monun.kommand.kommand
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {
    override fun onEnable() {
        val game = Game(this)
        server.pluginManager.registerEvents(Event(game), this)
        server.scheduler.runTaskTimer(this, Scoreboard(server, game), 0, 1)
        game.worldSetup()
        kommand {
            register("game") {
                then("start") {
                    executes {
                        game.start()
                    }
                }
                then("exit") {
                    executes {
                        game.exit()
                    }
                }
            }
        }
    }
}