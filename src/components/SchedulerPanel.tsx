import React, { useState, useEffect } from "react";
import { Clock, CheckCircle2, AlertCircle, RefreshCw, Play, Settings, Save, ToggleLeft, ToggleRight, Calendar } from "lucide-react";

interface ScheduleConfig {
  enabled: boolean;
  time: string;
  timezone: string;
  command: string;
  lastRunDateStr: string;
}

interface HistoryItem {
  timestamp: string;
  command: string;
  status: "success" | "failed";
  details?: string;
}

export default function SchedulerPanel() {
  const [config, setConfig] = useState<ScheduleConfig>({
    enabled: true,
    time: "07:00",
    timezone: "Asia/Dhaka",
    command: "Adiamond",
    lastRunDateStr: ""
  });
  const [history, setHistory] = useState<HistoryItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [isTriggering, setIsTriggering] = useState(false);

  // Form states
  const [timeInput, setTimeInput] = useState("07:00");
  const [commandInput, setCommandInput] = useState("Adiamond");
  const [timezoneInput, setTimezoneInput] = useState("Asia/Dhaka");
  const [enabledState, setEnabledState] = useState(true);

  const fetchScheduleData = async () => {
    setIsLoading(true);
    try {
      const response = await fetch("/api/schedule");
      if (response.ok) {
        const data = await response.json();
        if (data.success) {
          setConfig(data.config);
          setHistory(data.history || []);
          setTimeInput(data.config.time);
          setCommandInput(data.config.command);
          setTimezoneInput(data.config.timezone);
          setEnabledState(data.config.enabled);
        }
      }
    } catch (err) {
      console.error("Error fetching schedule data:", err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchScheduleData();
    // Refresh history and details every 15 seconds
    const interval = setInterval(fetchScheduleData, 15000);
    return () => clearInterval(interval);
  }, []);

  const handleSaveConfig = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSaving(true);
    try {
      const response = await fetch("/api/schedule/update", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          enabled: enabledState,
          time: timeInput,
          timezone: timezoneInput,
          command: commandInput
        })
      });
      if (response.ok) {
        const data = await response.json();
        if (data.success) {
          setConfig(data.config);
          alert("অটোমেটিক সিডিউল সেটিংস সফলভাবে আপডেট হয়েছে!");
          fetchScheduleData();
        }
      }
    } catch (err) {
      console.error("Error saving schedule config:", err);
      alert("সেটিংস সংরক্ষণ করতে ব্যর্থ হয়েছে।");
    } finally {
      setIsSaving(false);
    }
  };

  const handleTriggerTest = async () => {
    if (!window.confirm("আপনি কি এখনই টেস্ট করার জন্য Telegram-এ কমান্ড পাঠাতে চান?")) {
      return;
    }
    setIsTriggering(true);
    try {
      const response = await fetch("/api/schedule/trigger", {
        method: "POST"
      });
      if (response.ok) {
        const data = await response.json();
        if (data.success) {
          alert(`সফলভাবে টেস্ট সম্পন্ন হয়েছে! Telegram-এ "${config.command}" কমান্ড পাঠানো হয়েছে।`);
          fetchScheduleData();
        } else {
          alert(`টেস্ট ব্যর্থ হয়েছে: ${data.result?.error || "Error"}`);
        }
      }
    } catch (err) {
      console.error("Error triggering scheduled task:", err);
      alert("টেস্ট রিকোয়েস্ট ব্যর্থ হয়েছে।");
    } finally {
      setIsTriggering(false);
    }
  };

  const formatTimestamp = (isoString: string) => {
    try {
      const date = new Date(isoString);
      return date.toLocaleString("en-US", {
        timeZone: "Asia/Dhaka",
        hour12: true,
        month: "short",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit"
      }) + " (BDT)";
    } catch (e) {
      return isoString;
    }
  };

  return (
    <div id="scheduler-panel" className="bg-slate-900/60 backdrop-blur-md border border-slate-800/80 rounded-2xl p-6 shadow-xl space-y-6">
      
      {/* Header section with active status */}
      <div className="flex items-center justify-between border-b border-slate-800/60 pb-4">
        <div className="flex items-center gap-3">
          <div className="p-2.5 rounded-xl bg-cyan-950 border border-cyan-800 text-cyan-400">
            <Clock className="w-5 h-5 animate-pulse" />
          </div>
          <div>
            <h3 className="font-display font-semibold text-slate-100 text-md tracking-wide uppercase">
              অটোমেটিক রেট চেকার সিডিউলার
            </h3>
            <p className="text-[11px] text-slate-400">
              প্রতিদিন নির্দিষ্ট সময়ে স্বয়ংক্রিয়ভাবে Telegram-এ কমান্ড পাঠায়
            </p>
          </div>
        </div>

        <button
          onClick={fetchScheduleData}
          disabled={isLoading}
          className="p-2 bg-slate-850 hover:bg-slate-800 text-slate-400 hover:text-cyan-400 rounded-lg border border-slate-800/80 transition-all cursor-pointer"
          title="রিফ্রেশ করুন"
        >
          <RefreshCw className={`w-4 h-4 ${isLoading ? "animate-spin text-cyan-400" : ""}`} />
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        
        {/* Left Side: Settings Form */}
        <form onSubmit={handleSaveConfig} className="space-y-4">
          <div className="flex items-center justify-between p-3.5 bg-slate-950/40 border border-slate-850 rounded-xl">
            <div>
              <span className="text-xs font-semibold text-slate-200 block uppercase tracking-wide">
                সিডিউল সার্ভিস অবস্থা
              </span>
              <span className="text-[11px] text-slate-400">
                {enabledState ? "সক্রিয় রয়েছে এবং ব্যাকগ্রাউন্ডে চলছে" : "সাময়িকভাবে বন্ধ করা রয়েছে"}
              </span>
            </div>
            <button
              type="button"
              onClick={() => setEnabledState(!enabledState)}
              className="text-slate-300 focus:outline-none cursor-pointer"
            >
              {enabledState ? (
                <ToggleRight className="w-10 h-10 text-emerald-400" />
              ) : (
                <ToggleLeft className="w-10 h-10 text-slate-500" />
              )}
            </button>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <label className="text-[11px] font-mono uppercase text-slate-400 tracking-wider">
                সময় (Hour & Minute)
              </label>
              <input
                type="text"
                value={timeInput}
                onChange={(e) => setTimeInput(e.target.value)}
                placeholder="e.g., 07:00"
                className="w-full bg-slate-950 border border-slate-800 focus:border-cyan-500/80 focus:ring-1 focus:ring-cyan-500 rounded-xl px-3.5 py-2.5 text-xs font-mono text-slate-200 outline-none"
                required
              />
            </div>

            <div className="space-y-1.5">
              <label className="text-[11px] font-mono uppercase text-slate-400 tracking-wider">
                টাইমজোন (Timezone)
              </label>
              <select
                value={timezoneInput}
                onChange={(e) => setTimezoneInput(e.target.value)}
                className="w-full bg-slate-950 border border-slate-800 focus:border-cyan-500/80 focus:ring-1 focus:ring-cyan-500 rounded-xl px-3.5 py-2.5 text-xs font-mono text-slate-200 outline-none"
              >
                <option value="Asia/Dhaka">Asia/Dhaka (BDT)</option>
                <option value="UTC">UTC (Greenwich)</option>
              </select>
            </div>
          </div>

          <div className="space-y-1.5">
            <label className="text-[11px] font-mono uppercase text-slate-400 tracking-wider">
              Telegram কমান্ড
            </label>
            <input
              type="text"
              value={commandInput}
              onChange={(e) => setCommandInput(e.target.value)}
              placeholder="e.g., Adiamond"
              className="w-full bg-slate-950 border border-slate-800 focus:border-cyan-500/80 focus:ring-1 focus:ring-cyan-500 rounded-xl px-3.5 py-2.5 text-xs font-mono text-slate-200 outline-none"
              required
            />
          </div>

          <div className="flex gap-2 pt-2">
            <button
              type="submit"
              disabled={isSaving}
              className="flex-1 py-2.5 px-4 bg-gradient-to-r from-cyan-500/20 to-emerald-500/20 hover:from-cyan-500/30 hover:to-emerald-500/30 text-cyan-400 font-semibold border border-cyan-500/30 hover:border-cyan-500/50 text-xs rounded-xl transition-all cursor-pointer flex items-center justify-center gap-2"
            >
              <Save className="w-3.5 h-3.5" />
              {isSaving ? "সংরক্ষণ করা হচ্ছে..." : "সেটিংস সংরক্ষণ করুন"}
            </button>

            <button
              type="button"
              onClick={handleTriggerTest}
              disabled={isTriggering}
              className="py-2.5 px-4 bg-slate-850 hover:bg-slate-800 text-slate-200 border border-slate-800 text-xs font-medium rounded-xl transition-all cursor-pointer flex items-center justify-center gap-2"
              title="টেস্ট করার জন্য এখনই কমান্ড পাঠান"
            >
              <Play className="w-3.5 h-3.5 text-emerald-400" />
              {isTriggering ? "পাঠানো হচ্ছে..." : "টেস্ট রান"}
            </button>
          </div>
        </form>

        {/* Right Side: Running Logs / History */}
        <div className="space-y-3 flex flex-col justify-between">
          <div className="space-y-2">
            <span className="text-[10px] font-mono uppercase text-slate-500 block tracking-wider">
              🕒 সিডিউল এক্সিকিউশন লগ হিস্টোরি (সর্বশেষ ১৫টি)
            </span>

            <div className="bg-slate-950 border border-slate-850 rounded-xl p-3 max-h-[190px] overflow-y-auto space-y-2 font-mono scrollbar-thin scrollbar-thumb-slate-800 scrollbar-track-transparent">
              {history.length === 0 ? (
                <div className="text-center py-8 text-slate-600 text-[11px]">
                  এখনও কোনো স্বয়ংক্রিয় লগ রেকর্ড তৈরি হয়নি।
                </div>
              ) : (
                history.map((item, idx) => (
                  <div key={idx} className="text-[11px] p-2 bg-slate-900/60 rounded border border-slate-850/40 flex items-start gap-2.5">
                    {item.status === "success" ? (
                      <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
                    ) : (
                      <AlertCircle className="w-4 h-4 text-rose-400 shrink-0 mt-0.5" />
                    )}
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center justify-between text-slate-400">
                        <span>কমান্ড: <strong className="text-slate-200">"{item.command}"</strong></span>
                        <span className={`text-[9px] px-1.5 py-0.2 rounded border uppercase font-semibold ${
                          item.status === "success" 
                            ? "bg-emerald-950/40 border-emerald-800/40 text-emerald-400" 
                            : "bg-rose-950/40 border-rose-800/40 text-rose-400"
                        }`}>
                          {item.status}
                        </span>
                      </div>
                      <div className="text-[9px] text-slate-500 mt-1 flex justify-between">
                        <span>{formatTimestamp(item.timestamp)}</span>
                        {item.details && <span className="text-slate-600 italic truncate max-w-[120px]">{item.details}</span>}
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>

          {/* Next execution helper indicator */}
          <div className="bg-slate-950/40 border border-slate-850 rounded-xl p-3.5 flex items-center justify-between text-xs font-mono text-slate-400">
            <div className="flex items-center gap-2">
              <Calendar className="w-4 h-4 text-cyan-400" />
              <span>পরবর্তী কমান্ড সময়:</span>
            </div>
            <span className="text-cyan-400 font-semibold">{config.time} (Asia/Dhaka)</span>
          </div>
        </div>

      </div>

    </div>
  );
}
