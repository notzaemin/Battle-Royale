package io.github.jaemin0299.plugin.utils

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun Player.giveItem(item: ItemStack) = run {
    val map = inventory.addItem(item)
    if (map.isNotEmpty()) {
        map.forEach { (_, item) ->
            try {
                location.world.dropItem(location, item)
            } catch (_: IllegalArgumentException) {}
        }
    }
}