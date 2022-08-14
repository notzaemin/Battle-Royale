package io.github.jaemin0299.plugin.utils

import org.bukkit.configuration.file.*
import java.io.File
import java.io.IOException

class Data(path: String, name: String) {
    private var file: File = File(path, name)
    private var data: FileConfiguration? = null

    init {
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (_: IOException) {}
        }
        data = YamlConfiguration.loadConfiguration(file)
    }

    @JvmName("getData1")
    fun getData(): FileConfiguration? {
        return data
    }

    fun saveData() {
        try {
            getData()?.save(file)
        } catch (_: IOException) {}
    }
}