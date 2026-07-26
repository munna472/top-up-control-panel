package com.telegramautotopup.gateway

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GatewayRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("atg_gateway_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        val DEFAULT_PACKAGES = listOf(
            TopUpPackage("1", "25 Diamond", "25"),
            TopUpPackage("2", "50 Diamond", "50"),
            TopUpPackage("3", "115 Diamond", "115"),
            TopUpPackage("4", "240 Diamond", "240"),
            TopUpPackage("5", "610 Diamond", "610"),
            TopUpPackage("6", "1240 Diamond", "1240"),
            TopUpPackage("7", "2530 Diamond", "2530"),
            TopUpPackage("8", "Weekly Membership", "161"),
            TopUpPackage("9", "Monthly Membership", "800"),
            TopUpPackage("10", "Weekly Lite", "lite"),
            TopUpPackage("11", "Evo Access 3 Days", "evo3"),
            TopUpPackage("12", "Evo Access 7 Days", "evo7"),
            TopUpPackage("13", "Evo Access 30 Days", "evo30"),
            TopUpPackage("14", "Level 6 Pass", "lvl6"),
            TopUpPackage("15", "Level 10 Pass", "lvl10"),
            TopUpPackage("16", "Level 15 Pass", "lvl15"),
            TopUpPackage("17", "Level 20 Pass", "lvl20"),
            TopUpPackage("18", "Level 25 Pass", "lvl25"),
            TopUpPackage("19", "Level 30 Pass", "lvl30"),
            TopUpPackage("20", "Full Level Up Pass", "lvlall")
        )
    }

    fun getPackages(): List<TopUpPackage> {
        val json = prefs.getString("atg_packages", null) ?: return DEFAULT_PACKAGES
        return try {
            val type = object : TypeToken<List<TopUpPackage>>() {}.type
            gson.fromJson(json, type) ?: DEFAULT_PACKAGES
        } catch (e: Exception) {
            DEFAULT_PACKAGES
        }
    }

    fun savePackages(packages: List<TopUpPackage>) {
        val json = gson.toJson(packages)
        prefs.edit().putString("atg_packages", json).apply()
    }

    fun resetPackagesToDefault() {
        prefs.edit().remove("atg_packages").apply()
    }

    fun getScheduleConfig(): ScheduleConfig {
        val json = prefs.getString("schedule_config", null) ?: return ScheduleConfig()
        return try {
            gson.fromJson(json, ScheduleConfig::class.java) ?: ScheduleConfig()
        } catch (e: Exception) {
            ScheduleConfig()
        }
    }

    fun saveScheduleConfig(config: ScheduleConfig) {
        val json = gson.toJson(config)
        prefs.edit().putString("schedule_config", json).apply()
    }

    fun getHistoryLogs(): List<HistoryLog> {
        val json = prefs.getString("history_logs", null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<HistoryLog>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addHistoryLog(log: HistoryLog) {
        val logs = getHistoryLogs().toMutableList()
        logs.add(0, log)
        if (logs.size > 15) {
            logs.removeAt(logs.size - 1)
        }
        val json = gson.toJson(logs)
        prefs.edit().putString("history_logs", json).apply()
    }

    fun clearHistoryLogs() {
        prefs.edit().remove("history_logs").apply()
    }

    fun getBotToken(): String {
        return prefs.getString("bot_token", "") ?: ""
    }

    fun saveBotToken(token: String) {
        prefs.edit().putString("bot_token", token).apply()
    }

    fun getChatId(): String {
        return prefs.getString("chat_id", "") ?: ""
    }

    fun saveChatId(id: String) {
        prefs.edit().putString("chat_id", id).apply()
    }
}
