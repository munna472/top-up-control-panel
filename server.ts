import express from "express";
import path from "path";
import fs from "fs";
import { createServer as createViteServer } from "vite";

async function startServer() {
  const app = express();
  const PORT = 3000;

  // Setup standard CORS headers to support any cross-origin web/script integrations
  app.use((req, res, next) => {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
    res.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PATCH, PUT, DELETE");
    if (req.method === "OPTIONS") {
      return res.sendStatus(200);
    }
    next();
  });

  // Setup express json parsing middleware and urlencoded parsing (crucial for normal PHP cURL / standard form submits)
  app.use(express.json());
  app.use(express.urlencoded({ extended: true }));

  // Helper top-up handler function to serve both /api/trigger-topup and /trigger-topup paths
  const handleTriggerTopup = async (req: express.Request, res: express.Response) => {
    try {
      // In urlencoded requests, values might be in req.body. Let's print or log it
      const { uid, command, raw_command, qty } = req.body;

      // Determine final message payload
      let finalCommandText = "";
      
      if (raw_command) {
        // High priority raw custom command direct forwarder
        finalCommandText = raw_command.trim();
      } else if (uid && command) {
        // Build the automated command: "Atp [UID] [Command] [Qty]"
        const cleanUid = uid.toString().trim();
        const cleanCmd = command.toString().trim();
        const cleanQty = qty ? parseInt(qty.toString()) : 1;

        if (cleanQty > 1 && cleanQty <= 5) {
          finalCommandText = `Atp ${cleanUid} ${cleanCmd} ${cleanQty}`;
        } else {
          finalCommandText = `Atp ${cleanUid} ${cleanCmd}`;
        }
      }

      if (!finalCommandText) {
        return res.status(400).json({
          success: false,
          error: "Invalid payload parameters. Ensure you provide 'raw_command' OR ('uid' and 'command').",
          received_body: req.body
        });
      }

      // Dispatch to Telegram target Bot Client
      const BOT_TOKEN = "8908339374:AAGDZJtaRLQpF5lYgRkK2TKNtGztCEfU8AI";
      const CHAT_ID = "-1004413191032";

      const telegramApi = `https://api.telegram.org/bot${BOT_TOKEN}/sendMessage`;
      const response = await fetch(telegramApi, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          chat_id: CHAT_ID,
          text: finalCommandText,
        }),
      });

      if (!response.ok) {
        throw new Error(`Telegram API responded with status: ${response.status}`);
      }

      const data = await response.json();

      return res.status(200).json({
        success: true,
        message: "Order automation triggered & command dispatched to Telegram!",
        dispatched_command: finalCommandText,
        telegram_response: data
      });

    } catch (error: any) {
      console.error("Webhook processing error:", error);
      return res.status(500).json({
        success: false,
        error: "Internal loop failed. Cannot bridge transaction.",
        details: error?.message || error
      });
    }
  };

  // Define GET handler for validation so developers can test if URL matches
  const handleGetValidation = (req: express.Request, res: express.Response) => {
    res.json({
      success: true,
      status: "ONLINE",
      port: 3000,
      protocol: "ATG Gateway Automation API Protocol",
      allowed_methods: ["POST"],
      instructions: "To trigger an automated order top-up, send a POST request with JSON or urlencoded data consisting of: 'raw_command' OR ('uid' and 'command', optional 'qty')."
    });
  };

  // --- Scheduling Engine for daily Diamond Rate Check ---
  const configPath = path.join(process.cwd(), "schedule-config.json");
  
  interface ScheduleConfig {
    enabled: boolean;
    time: string; // "HH:MM" e.g., "07:00"
    timezone: string; // e.g., "Asia/Dhaka"
    command: string; // e.g., "Adiamond"
    lastRunDateStr: string; // "YYYY-MM-DD" in target timezone to prevent duplicate runs on the same day
    history: Array<{
      timestamp: string;
      command: string;
      status: "success" | "failed";
      details?: string;
    }>;
  }

  let scheduleConfig: ScheduleConfig = {
    enabled: true,
    time: "07:00",
    timezone: "Asia/Dhaka",
    command: "Adiamond",
    lastRunDateStr: "",
    history: []
  };

  // Load existing schedule config if it exists
  try {
    if (fs.existsSync(configPath)) {
      const data = fs.readFileSync(configPath, "utf-8");
      scheduleConfig = { ...scheduleConfig, ...JSON.parse(data) };
    }
  } catch (e) {
    console.error("Failed to load schedule-config.json, using defaults", e);
  }

  const saveScheduleConfig = () => {
    try {
      fs.writeFileSync(configPath, JSON.stringify(scheduleConfig, null, 2), "utf-8");
    } catch (e) {
      console.error("Failed to save schedule-config.json", e);
    }
  };

  const executeScheduledTask = async () => {
    const BOT_TOKEN = "8908339374:AAGDZJtaRLQpF5lYgRkK2TKNtGztCEfU8AI";
    const CHAT_ID = "-1004413191032";
    const commandText = scheduleConfig.command;
    const timestamp = new Date().toISOString();

    try {
      const telegramApi = `https://api.telegram.org/bot${BOT_TOKEN}/sendMessage`;
      const response = await fetch(telegramApi, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          chat_id: CHAT_ID,
          text: commandText,
        }),
      });

      if (!response.ok) {
        throw new Error(`Telegram API responded with status: ${response.status}`);
      }

      // Record success in history
      scheduleConfig.history.unshift({
        timestamp,
        command: commandText,
        status: "success",
        details: "Dispatched successfully at scheduled time"
      });
      // Keep history max 15 items
      if (scheduleConfig.history.length > 15) {
        scheduleConfig.history = scheduleConfig.history.slice(0, 15);
      }
      saveScheduleConfig();
      console.log(`[Scheduler] Successfully ran scheduled rate check command: "${commandText}"`);
      return { success: true, command: commandText };
    } catch (error: any) {
      console.error("[Scheduler] Error executing scheduled task:", error);
      scheduleConfig.history.unshift({
        timestamp,
        command: commandText,
        status: "failed",
        details: error?.message || "Unknown communication failure"
      });
      if (scheduleConfig.history.length > 15) {
        scheduleConfig.history = scheduleConfig.history.slice(0, 15);
      }
      saveScheduleConfig();
      return { success: false, error: error?.message || error };
    }
  };

  // Run scheduler check loop every 10 seconds
  setInterval(() => {
    if (!scheduleConfig.enabled) return;

    try {
      const now = new Date();
      // Get time in target timezone
      const targetTimeStr = now.toLocaleString("en-US", { timeZone: scheduleConfig.timezone });
      const targetDate = new Date(targetTimeStr);

      const hours = targetDate.getHours().toString().padStart(2, "0");
      const minutes = targetDate.getMinutes().toString().padStart(2, "0");
      const currentTimeStr = `${hours}:${minutes}`; // "HH:MM" format

      // Format date string for duplicate checking (e.g. "2026-06-23")
      const year = targetDate.getFullYear();
      const month = (targetDate.getMonth() + 1).toString().padStart(2, "0");
      const day = targetDate.getDate().toString().padStart(2, "0");
      const currentDateStr = `${year}-${month}-${day}`;

      if (currentTimeStr === scheduleConfig.time) {
        if (scheduleConfig.lastRunDateStr !== currentDateStr) {
          scheduleConfig.lastRunDateStr = currentDateStr;
          saveScheduleConfig();
          executeScheduledTask();
        }
      }
    } catch (err) {
      console.error("[Scheduler] Error in check loop:", err);
    }
  }, 10000);

  // 1. API routes: Mount handlers under both /api/ and root namespace to eliminate 404 URL typos
  app.post("/api/trigger-topup", handleTriggerTopup);
  app.post("/trigger-topup", handleTriggerTopup);

  app.get("/api/trigger-topup", handleGetValidation);
  app.get("/trigger-topup", handleGetValidation);

  // 1.5. Schedule API endpoints
  app.get("/api/schedule", (req, res) => {
    res.json({
      success: true,
      config: {
        enabled: scheduleConfig.enabled,
        time: scheduleConfig.time,
        timezone: scheduleConfig.timezone,
        command: scheduleConfig.command,
        lastRunDateStr: scheduleConfig.lastRunDateStr
      },
      history: scheduleConfig.history
    });
  });

  app.post("/api/schedule/update", (req, res) => {
    try {
      const { enabled, time, timezone, command } = req.body;

      if (enabled !== undefined) scheduleConfig.enabled = !!enabled;
      if (time && /^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/.test(time)) scheduleConfig.time = time;
      if (timezone) scheduleConfig.timezone = timezone;
      if (command) scheduleConfig.command = command;

      saveScheduleConfig();
      res.json({
        success: true,
        message: "Scheduler configuration updated successfully!",
        config: {
          enabled: scheduleConfig.enabled,
          time: scheduleConfig.time,
          timezone: scheduleConfig.timezone,
          command: scheduleConfig.command
        }
      });
    } catch (err: any) {
      res.status(500).json({ success: false, error: err.message });
    }
  });

  app.post("/api/schedule/trigger", async (req, res) => {
    try {
      const result = await executeScheduledTask();
      res.json({
        success: true,
        message: "Test run executed manually!",
        result
      });
    } catch (err: any) {
      res.status(500).json({ success: false, error: err.message });
    }
  });

  // 2. Health check route
  app.get("/api/health", (req, res) => {
    res.json({ status: "healthy", active: true });
  });

  // Vite middleware for development vs React static asset serving for production
  if (process.env.NODE_ENV !== "production") {
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: "spa",
    });
    app.use(vite.middlewares);
  } else {
    const distPath = path.join(process.cwd(), "dist");
    app.use(express.static(distPath));
    app.get("*", (req, res) => {
      res.sendFile(path.join(distPath, "index.html"));
    });
  }

  app.listen(PORT, "0.0.0.0", () => {
    console.log(`[ATG Gateway Daemon] Server running on http://0.0.0.0:${PORT}`);
  });
}

startServer();
