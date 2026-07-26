package com.telegramautotopup.gateway

import java.util.UUID

data class TopUpPackage(
    val id: String,
    val name: String,
    val commandValue: String,
    val isCustom: Boolean = false
)

data class ScheduleConfig(
    val enabled: Boolean = true,
    val time: String = "07:00",
    val timezone: String = "Asia/Dhaka",
    val command: String = "Adiamond",
    val lastRunDateStr: String = ""
)

data class HistoryLog(
    val timestamp: String,
    val command: String,
    val status: String, // "success" or "failed"
    val details: String = ""
)

data class ToastMessage(
    val id: String = UUID.randomUUID().toString(),
    val type: ToastType,
    val message: String,
    val description: String? = null
)

enum class ToastType {
    SUCCESS, ERROR, INFO
}
