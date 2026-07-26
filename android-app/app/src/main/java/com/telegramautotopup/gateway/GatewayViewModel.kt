package com.telegramautotopup.gateway

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class GatewayViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GatewayRepository(application)
    private val client = OkHttpClient()
    private val gson = Gson()

    // Secure dynamic Telegram credentials
    var botToken by mutableStateOf("")
        private set
    var chatId by mutableStateOf("")
        private set

    // --- State Variables ---
    var packages by mutableStateOf<List<TopUpPackage>>(emptyList())
        private set

    var selectedPackage by mutableStateOf<TopUpPackage?>(null)
    var uid by mutableStateOf("")
    var verifiedName by mutableStateOf<String?>(null)
    var isCheckingUid by mutableStateOf(false)
    var uidError by mutableStateOf<String?>(null)
    var isVerificationEnabled by mutableStateOf(true)

    var topUpQty by mutableStateOf(1)
    var isSubmitting by mutableStateOf(false)
    var cooldown by mutableStateOf(0)
    var toasts by mutableStateOf<List<ToastMessage>>(emptyList())
        private set

    var rawTerminalCommand by mutableStateOf("")

    // Command Hub State
    var trxId by mutableStateOf("")
    var ucValue by mutableStateOf("161")
    var ucQty by mutableStateOf(1)
    var customUc by mutableStateOf("")
    var useCustomUc by mutableStateOf(false)
    var shellAmount by mutableStateOf("50")
    var shellQty by mutableStateOf(1)

    // Calculator State
    var calcExpr by mutableStateOf("")
    var calcResult by mutableStateOf<Double?>(null)
    var queryUid by mutableStateOf("")

    // Scheduler State
    var schedulerConfig by mutableStateOf(ScheduleConfig())
        private set
    var schedulerEnabled by mutableStateOf(true)
    var schedulerTime by mutableStateOf("07:00")
    var schedulerTimezone by mutableStateOf("Asia/Dhaka")
    var schedulerCommand by mutableStateOf("Adiamond")
    var schedulerLogs by mutableStateOf<List<HistoryLog>>(emptyList())
        private set

    private var cooldownJob: Job? = null
    private var schedulerCheckJob: Job? = null

    init {
        packages = repository.getPackages()
        schedulerConfig = repository.getScheduleConfig()

        schedulerEnabled = schedulerConfig.enabled
        schedulerTime = schedulerConfig.time
        schedulerTimezone = schedulerConfig.timezone
        schedulerCommand = schedulerConfig.command
        schedulerLogs = repository.getHistoryLogs()

        // Initialize secure credentials
        val savedToken = repository.getBotToken()
        botToken = if (savedToken.isNotEmpty()) savedToken else if (BuildConfig.TELEGRAM_BOT_TOKEN != "PLACEHOLDER_BOT_TOKEN") BuildConfig.TELEGRAM_BOT_TOKEN else "8908339374:AAGDZJtaRLQpF5lYgRkK2TKNtGztCEfU8AI"

        val savedChatId = repository.getChatId()
        chatId = if (savedChatId.isNotEmpty()) savedChatId else if (BuildConfig.TELEGRAM_CHAT_ID != "PLACEHOLDER_CHAT_ID") BuildConfig.TELEGRAM_CHAT_ID else "-1004413191032"

        // Start scheduler loop simulation
        startSchedulerSimulationLoop()
    }

    fun saveTelegramCredentials(token: String, id: String) {
        val cleanToken = token.trim()
        val cleanId = id.trim()
        if (cleanToken.isNotEmpty()) {
            botToken = cleanToken
            repository.saveBotToken(cleanToken)
        }
        if (cleanId.isNotEmpty()) {
            chatId = cleanId
            repository.saveChatId(cleanId)
        }
        addToast(ToastType.SUCCESS, "Credentials Updated", "Telegram bot credentials saved successfully!")
    }

    // --- Toast Notifications Logic ---
    fun addToast(type: ToastType, message: String, description: String? = null) {
        val toast = ToastMessage(type = type, message = message, description = description)
        toasts = toasts + toast
        viewModelScope.launch {
            delay(5000)
            removeToast(toast.id)
        }
    }

    fun removeToast(id: String) {
        toasts = toasts.filter { it.id != id }
    }

    // --- Cooldown Timer ---
    private fun startCooldownTimer() {
        cooldownJob?.cancel()
        cooldown = 10
        cooldownJob = viewModelScope.launch {
            while (cooldown > 0) {
                delay(1000)
                cooldown--
            }
        }
    }

    // --- Package Management Logic ---
    fun addPackage(name: String, command: String) {
        val cleanName = name.trim()
        val cleanCommand = command.trim()
        if (cleanName.isEmpty() || cleanCommand.isEmpty()) return

        val newPkg = TopUpPackage(
            id = System.currentTimeMillis().toString(),
            name = cleanName,
            commandValue = cleanCommand,
            isCustom = true
        )
        packages = packages + newPkg
        repository.savePackages(packages)
        addToast(ToastType.SUCCESS, "Package configuration saved", "The active package commands have been updated.")
    }

    fun editPackage(id: String, name: String, command: String) {
        val cleanName = name.trim()
        val cleanCommand = command.trim()
        if (cleanName.isEmpty() || cleanCommand.isEmpty()) return

        packages = packages.map {
            if (it.id == id) {
                it.copy(name = cleanName, commandValue = cleanCommand)
            } else {
                it
            }
        }
        repository.savePackages(packages)
        addToast(ToastType.SUCCESS, "Package updated", "Package has been edited successfully.")
    }

    fun deletePackage(id: String) {
        packages = packages.filter { it.id != id }
        repository.savePackages(packages)
        if (selectedPackage?.id == id) {
            selectedPackage = null
        }
        addToast(ToastType.INFO, "Package deleted", "Package was successfully removed.")
    }

    fun resetPackagesToDefault() {
        repository.resetPackagesToDefault()
        packages = GatewayRepository.DEFAULT_PACKAGES
        selectedPackage = null
        addToast(ToastType.INFO, "Default packages restored", "Successfully reverted database to primary default settings.")
    }

    // --- Player UID Verification (API integration) ---
    fun verifyUid() {
        val cleanUid = uid.trim()
        if (cleanUid.isEmpty()) {
            uidError = "Please key in a valid Player UID"
            verifiedName = null
            return
        }

        isCheckingUid = true
        uidError = null
        verifiedName = null

        viewModelScope.launch {
            val apiEndpoint = "https://test1.mraipay.top/mraiprimetop1/player?uid=${cleanUid}"
            val request = Request.Builder().url(apiEndpoint).get().build()

            try {
                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }

                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    val map = gson.fromJson(bodyString, Map::class.java)
                    val accountName = map?.get("account") as? String

                    if (!accountName.isNullOrEmpty()) {
                        verifiedName = accountName
                        uidError = null
                    } else {
                        verifiedName = "Verified Account"
                        uidError = "API returned success, but account name was empty. Proceed with caution."
                    }
                } else {
                    throw Exception("Server returned status: ${response.code}")
                }
            } catch (e: Exception) {
                verifiedName = null
                uidError = e.message ?: "Unable to reach verification database. Server might be offline."
            } finally {
                isCheckingUid = false
            }
        }
    }

    fun onVerificationModeChanged(enabled: Boolean) {
        isVerificationEnabled = enabled
        uidError = null
        if (!enabled) {
            verifiedName = if (uid.trim().isNotEmpty()) "Skipped (Optional Mode)" else null
        } else {
            verifiedName = null
        }
    }

    fun onUidChanged(newUid: String) {
        uid = newUid
        uidError = null
        if (isVerificationEnabled) {
            verifiedName = null
        } else {
            verifiedName = if (newUid.trim().isNotEmpty()) "Skipped (Optional Mode)" else null
        }
    }

    // --- Command Dispatch to Telegram ---
    fun dispatchCommandToTelegram(commandText: String, actionLabel: String) {
        if (cooldown > 0) {
            addToast(ToastType.ERROR, "Anti-Flood Lock", "Please allow $cooldown seconds of cooldown buffer before sending next transmission.")
            return
        }

        isSubmitting = true
        viewModelScope.launch {
            val telegramApi = "https://api.telegram.org/bot$botToken/sendMessage"
            val jsonPayload = gson.toJson(mapOf("chat_id" to chatId, "text" to commandText))
            val body = jsonPayload.toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder().url(telegramApi).post(body).build()

            try {
                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }

                if (response.isSuccessful) {
                    addToast(ToastType.SUCCESS, "Command Dispatched", "Sent [$commandText] successfully for: $actionLabel")
                    startCooldownTimer()
                } else {
                    throw Exception("Telegram API responded status: ${response.code}")
                }
            } catch (e: Exception) {
                addToast(ToastType.ERROR, "Link Interrupted", e.message ?: "Failed to route control packet to Telegram servers. Kindly try again.")
            } finally {
                isSubmitting = false
            }
        }
    }

    fun handleSubmitTopUp() {
        val cleanUid = uid.trim()
        val pkg = selectedPackage
        if (cleanUid.isEmpty()) {
            addToast(ToastType.ERROR, "Execution Failed", "Please verify and insert a valid Player UID first.")
            return
        }
        if (pkg == null) {
            addToast(ToastType.ERROR, "Execution Failed", "Please highlight and select a top-up bundle.")
            return
        }

        val formattedCmd = if (topUpQty > 1) {
            "Atp $cleanUid ${pkg.commandValue} $topUpQty"
        } else {
            "Atp $cleanUid ${pkg.commandValue}"
        }

        dispatchCommandToTelegram(formattedCmd, "Atp Premium Diamonds (${pkg.name})")
    }

    fun handleManualConsoleSubmit() {
        val cleanCmd = rawTerminalCommand.trim()
        if (cleanCmd.isEmpty()) return
        dispatchCommandToTelegram(cleanCmd, "Manual Terminal Input")
        rawTerminalCommand = ""
    }

    // --- Command Hub Handlers ---
    fun handleVerifyTrx() {
        val cleanTrx = trxId.trim()
        if (cleanTrx.isEmpty()) return
        dispatchCommandToTelegram("Averify $cleanTrx", "Verify Payment (TrxID: $cleanTrx)")
        trxId = ""
    }

    fun handleUcSubmit() {
        val activeUc = if (useCustomUc) customUc.trim() else ucValue
        if (activeUc.isEmpty()) return
        val cmdStr = if (ucQty > 1) "Auc $activeUc $ucQty" else "Auc $activeUc"
        dispatchCommandToTelegram(cmdStr, "Unipin UC Code ($activeUc UC)")
    }

    fun handleShellSubmit() {
        if (shellAmount.isEmpty()) return
        val cmdStr = if (shellQty > 1) "Ashell $shellAmount $shellQty" else "Ashell $shellAmount"
        dispatchCommandToTelegram(cmdStr, "Garena Shell ($shellAmount Shells)")
    }

    fun handleCalculatorEvaluate() {
        val expr = calcExpr.trim()
        if (expr.isEmpty()) return
        val res = Calculator.evaluateExpression(expr)
        if (res != null) {
            calcResult = res
            dispatchCommandToTelegram(expr, "Calculator [$expr]")
        } else {
            calcResult = null
            addToast(ToastType.ERROR, "Calculator Error", "Expression is invalid or cannot be parsed.")
        }
    }

    fun handleQueryUidSubmit() {
        val qUid = queryUid.trim()
        if (qUid.isEmpty()) return
        dispatchCommandToTelegram(qUid, "ID Details Check [$qUid]")
    }

    // --- Scheduler Configuration and History Log Logic ---
    fun saveSchedulerConfig() {
        val newConfig = ScheduleConfig(
            enabled = schedulerEnabled,
            time = schedulerTime,
            timezone = schedulerTimezone,
            command = schedulerCommand,
            lastRunDateStr = schedulerConfig.lastRunDateStr
        )
        schedulerConfig = newConfig
        repository.saveScheduleConfig(newConfig)
        addToast(ToastType.SUCCESS, "সেটিংস সংরক্ষিত", "অটোমেটিক সিডিউল সেটিংস সফলভাবে আপডেট হয়েছে!")
    }

    fun triggerSchedulerTestRun() {
        viewModelScope.launch {
            val cmd = schedulerCommand
            val timestamp = getFormattedTimestampInTimeZone(schedulerTimezone)
            val telegramApi = "https://api.telegram.org/bot$botToken/sendMessage"
            val jsonPayload = gson.toJson(mapOf("chat_id" to chatId, "text" to cmd))
            val body = jsonPayload.toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder().url(telegramApi).post(body).build()

            try {
                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }

                val log = if (response.isSuccessful) {
                    HistoryLog(timestamp, cmd, "success", "Dispatched successfully at scheduled time")
                } else {
                    HistoryLog(timestamp, cmd, "failed", "Telegram status: ${response.code}")
                }
                repository.addHistoryLog(log)
                schedulerLogs = repository.getHistoryLogs()

                if (response.isSuccessful) {
                    addToast(ToastType.SUCCESS, "সফল টেস্ট সম্পন্ন", "Telegram-এ \"$cmd\" কমান্ড পাঠানো হয়েছে।")
                } else {
                    addToast(ToastType.ERROR, "টেস্ট ব্যর্থ হয়েছে", "কমান্ড পাঠাতে ব্যর্থ হয়েছে।")
                }
            } catch (e: Exception) {
                val log = HistoryLog(timestamp, cmd, "failed", e.message ?: "Unknown communication failure")
                repository.addHistoryLog(log)
                schedulerLogs = repository.getHistoryLogs()
                addToast(ToastType.ERROR, "টেস্ট ব্যর্থ হয়েছে", e.message ?: "কানেকশন সমস্যা")
            }
        }
    }

    private fun getFormattedTimestampInTimeZone(timezoneId: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }

    // Scheduler background polling task simulation inside native Android app
    private fun startSchedulerSimulationLoop() {
        schedulerCheckJob?.cancel()
        schedulerCheckJob = viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                delay(10000) // check every 10 seconds
                if (!schedulerEnabled) continue

                try {
                    val tz = TimeZone.getTimeZone(schedulerTimezone)
                    val cal = Calendar.getInstance(tz)
                    val currentHour = String.format("%02d", cal.get(Calendar.HOUR_OF_DAY))
                    val currentMinute = String.format("%02d", cal.get(Calendar.MINUTE))
                    val currentTime = "$currentHour:$currentMinute"

                    val currentYear = cal.get(Calendar.YEAR)
                    val currentMonth = String.format("%02d", cal.get(Calendar.MONTH) + 1)
                    val currentDay = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH))
                    val currentDateStr = "$currentYear-$currentMonth-$currentDay"

                    if (currentTime == schedulerTime) {
                        if (schedulerConfig.lastRunDateStr != currentDateStr) {
                            // Update last run date state & persist
                            val updatedConfig = schedulerConfig.copy(lastRunDateStr = currentDateStr)
                            schedulerConfig = updatedConfig
                            repository.saveScheduleConfig(updatedConfig)

                            // Execute scheduled task
                            executeScheduledTaskBackground()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun executeScheduledTaskBackground() {
        viewModelScope.launch {
            val cmd = schedulerCommand
            val timestamp = getFormattedTimestampInTimeZone(schedulerTimezone)
            val telegramApi = "https://api.telegram.org/bot$botToken/sendMessage"
            val jsonPayload = gson.toJson(mapOf("chat_id" to chatId, "text" to cmd))
            val body = jsonPayload.toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder().url(telegramApi).post(body).build()

            try {
                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }

                val log = if (response.isSuccessful) {
                    HistoryLog(timestamp, cmd, "success", "Dispatched successfully at scheduled time")
                } else {
                    HistoryLog(timestamp, cmd, "failed", "Telegram status: ${response.code}")
                }
                repository.addHistoryLog(log)
                schedulerLogs = repository.getHistoryLogs()
            } catch (e: Exception) {
                val log = HistoryLog(timestamp, cmd, "failed", e.message ?: "Unknown communication failure")
                repository.addHistoryLog(log)
                schedulerLogs = repository.getHistoryLogs()
            }
        }
    }
}
