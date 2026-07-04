import React, { useState, useEffect } from "react";
import { TopUpPackage } from "./types";
import { DEFAULT_PACKAGES } from "./data/defaultPackages";
import AdminPanel from "./components/AdminPanel";
import VerificationPanel from "./components/VerificationPanel";
import PackageCard from "./components/PackageCard";
import CommandHub from "./components/CommandHub";
import SchedulerPanel from "./components/SchedulerPanel";
import { 
  Zap, 
  Send, 
  Clock, 
  ShieldCheck, 
  X, 
  AlertTriangle,
  HelpCircle,
  Activity,
  Layers,
  Award,
  Terminal,
  RefreshCw,
  ShoppingBag,
  BellRing
} from "lucide-react";
import { motion, AnimatePresence } from "motion/react";

interface Toast {
  id: string;
  type: "success" | "error" | "info";
  message: string;
  description?: string;
}

export default function App() {
  // Load packages from localStorage or fallback to defaults
  const [packages, setPackages] = useState<TopUpPackage[]>(() => {
    const stored = localStorage.getItem("atg_packages");
    if (stored) {
      try {
        return JSON.parse(stored);
      } catch (e) {
        console.error("Failed to parse stored packages", e);
      }
    }
    return DEFAULT_PACKAGES;
  });

  const [selectedPackage, setSelectedPackage] = useState<TopUpPackage | null>(null);
  const [uid, setUid] = useState("");
  const [verifiedName, setVerifiedName] = useState<string | null>(null);
  
  // Custom multi top-up quantity (1 to 5)
  const [topUpQty, setTopUpQty] = useState<number>(1);

  // Submit actions states
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [cooldown, setCooldown] = useState(0);
  const [toasts, setToasts] = useState<Toast[]>([]);

  // Raw manual command input console
  const [rawTerminalCommand, setRawTerminalCommand] = useState("");

  // Telegram credentials
  const BOT_TOKEN = "8908339374:AAGDZJtaRLQpF5lYgRkK2TKNtGztCEfU8AI";
  const CHAT_ID = "-1004413191032";

  // Persist packages inside localStorage whenever they change
  useEffect(() => {
    localStorage.setItem("atg_packages", JSON.stringify(packages));
  }, [packages]);

  // Cooldown countdown timer
  useEffect(() => {
    if (cooldown > 0) {
      const timer = setTimeout(() => {
        setCooldown(cooldown - 1);
      }, 1000);
      return () => clearTimeout(timer);
    }
  }, [cooldown]);

  const addToast = (type: "success" | "error" | "info", message: string, description?: string) => {
    const toast: Toast = {
      id: Date.now().toString(),
      type,
      message,
      description,
    };
    setToasts((prev) => [...prev, toast]);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== toast.id));
    }, 5000);
  };

  const removeToast = (id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  };

  const handleUpdatePackages = (updated: TopUpPackage[]) => {
    setPackages(updated);
    addToast("success", "Package configuration saved", "The active package commands have been updated.");
    
    // If selected package was deleted, clear selection
    if (selectedPackage && !updated.some((pkg) => pkg.id === selectedPackage.id)) {
      setSelectedPackage(null);
    }
  };

  const handleResetDefaults = () => {
    if (window.confirm("Are you sure you want to restore the default package list? This will erase all custom configurations.")) {
      setPackages(DEFAULT_PACKAGES);
      setSelectedPackage(null);
      addToast("info", "Default packages restored", "Successfully reverted database to primary default settings.");
    }
  };

  // General Dispatch handler for any command triggered by interactive panels!
  const dispatchCommandToTelegram = async (commandText: string, actionLabel: string) => {
    if (cooldown > 0) {
      addToast("error", "Anti-Flood Lock", `Please allow ${cooldown} seconds of cooldown buffer before sending next transmission.`);
      return false;
    }

    setIsSubmitting(true);
    const telegramApi = `https://api.telegram.org/bot${BOT_TOKEN}/sendMessage`;

    try {
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
        throw new Error(`Telegram API responded status: ${response.status}`);
      }

      addToast(
        "success",
        "Command Dispatched",
        `Sent [${commandText}] successfully for: ${actionLabel}`
      );
      
      // Lock for 10 seconds to safeguard rate thresholds
      setCooldown(10);
      return true;
    } catch (err: any) {
      console.error("Failed executing action:", err);
      addToast(
        "error",
        "Link Interrupted",
        err.message || "Failed to route control packet to Telegram servers. Kindly try again."
      );
      return false;
    } finally {
      setIsSubmitting(false);
    }
  };

  // Dedicated ATP top-up dispatcher (Step 1 & 2 integration)
  const handleSubmitTopUp = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!uid.trim()) {
      addToast("error", "Execution Failed", "Please verify and insert a valid Player UID first.");
      return;
    }

    if (!selectedPackage) {
      addToast("error", "Execution Failed", "Please highlight and select a top-up bundle.");
      return;
    }

    // Format rule command: "Atp [UID] [Command_Value] [Qty]"
    // If quantity is 1, omit quantity suffix: "Atp [UID] [Command_Value]"
    const formattedCmd = topUpQty > 1 
      ? `Atp ${uid.trim()} ${selectedPackage.commandValue} ${topUpQty}`
      : `Atp ${uid.trim()} ${selectedPackage.commandValue}`;

    const ok = await dispatchCommandToTelegram(formattedCmd, `Atp Premium Diamonds (${selectedPackage.name})`);
    if (ok) {
      // Clear configuration upon successful queue dispatch
      setSelectedPackage(null);
    }
  };

  const handleManualConsoleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const cmdClean = rawTerminalCommand.trim();
    if (!cmdClean) return;

    dispatchCommandToTelegram(cmdClean, "Manual Terminal Input");
    setRawTerminalCommand("");
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans selection:bg-cyan-500/30 selection:text-cyan-200">
      
      {/* Glow ambient background graphics */}
      <div className="fixed inset-0 overflow-hidden pointer-events-none z-0">
        <div className="absolute top-[-10%] left-[-20%] w-[60%] h-[50%] rounded-full bg-cyan-900/10 blur-[120px]" />
        <div className="absolute bottom-[10%] right-[-20%] w-[50%] h-[60%] rounded-full bg-emerald-900/10 blur-[120px]" />
        <div className="absolute top-[40%] right-[30%] w-[40%] h-[40%] rounded-full bg-indigo-950/20 blur-[130px]" />
      </div>

      {/* Main Grid Margin Container */}
      <div className="flex-1 w-full max-w-5xl mx-auto px-4 py-8 relative z-10 flex flex-col gap-6">
        
        {/* Navigation & Brand Header */}
        <header className="flex items-center justify-between border-b border-slate-900 pb-6">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-cyan-500 to-emerald-500 p-[1px] shadow-[0_0_15px_-3px_rgba(6,182,212,0.4)]">
              <div className="w-full h-full bg-slate-950 rounded-[11px] flex items-center justify-center text-cyan-400">
                <Zap className="w-5.5 h-5.5 fill-cyan-400/10" />
              </div>
            </div>
            <div>
              <h1 className="font-display font-medium text-lg leading-tight tracking-tight text-slate-100 uppercase">
                Telegram Auto Top-Up Gateway
              </h1>
              <p className="text-[11px] font-mono tracking-widest text-emerald-400 uppercase">
                Instant Automatic Processing Node
              </p>
            </div>
          </div>

          <div className="hidden sm:flex items-center gap-4 text-xs font-mono">
            <div className="flex items-center gap-2 px-3 py-1.5 rounded-full bg-slate-900 border border-slate-800">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
              <span className="text-slate-400">Gateway Pipeline: Active</span>
            </div>
            <div className="text-slate-500">
              Chat Node ID: <span className="text-slate-300">-100441...</span>
            </div>
          </div>
        </header>

        {/* Informational Widget Badges */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="bg-slate-900/50 border border-slate-800/40 p-4 rounded-xl flex items-start gap-3">
            <ShieldCheck className="w-5 h-5 text-cyan-400 shrink-0 mt-0.5" />
            <div>
              <h5 className="text-xs font-semibold text-slate-200 uppercase tracking-wide">SECURE VERIFICATION</h5>
              <p className="text-[11px] text-slate-400 mt-1 leading-relaxed">Direct synchronization ensures you check players live in-game database before triggering credit dispatches.</p>
            </div>
          </div>
          <div className="bg-slate-900/50 border border-slate-800/40 p-4 rounded-xl flex items-start gap-3">
            <Clock className="w-5 h-5 text-emerald-400 shrink-0 mt-0.5" />
            <div>
              <h5 className="text-xs font-semibold text-slate-200 uppercase tracking-wide">INSTANT EXECUTION</h5>
              <p className="text-[11px] text-slate-400 mt-1 leading-relaxed">Automated trigger relays directly message your dedicated Telegram bot queue within millisecond cycles.</p>
            </div>
          </div>
          <div className="bg-slate-900/50 border border-slate-800/40 p-4 rounded-xl flex items-start gap-3">
            <Activity className="w-5 h-5 text-amber-400 shrink-0 mt-0.5" />
            <div>
              <h5 className="text-xs font-semibold text-slate-200 uppercase tracking-wide">INTERACTIVE COMMAND PANEL</h5>
              <p className="text-[11px] text-slate-400 mt-1 leading-relaxed">Quick actions for balance updates, UC codes, Garena shell vouchers, and live terminal calculation sheets.</p>
            </div>
          </div>
        </div>

        {/* Scheduler Automation Panel */}
        <SchedulerPanel />

        {/* Main Interface Layout Split */}
        <main className="grid grid-cols-1 lg:grid-cols-3 gap-6 items-start">
          
          {/* Main Workspace (Left Column) - Spans 2 columns */}
          <div className="lg:col-span-2 space-y-6">
            
            {/* Stage Title for Diamond Top-up */}
            <div className="border-b border-slate-800 pb-2">
              <h2 className="text-md uppercase font-semibold text-cyan-400 tracking-wider flex items-center gap-2">
                <ShoppingBag className="w-4 h-4" />
                💎 Direct FreeFire & Game Packages (Atp)
              </h2>
            </div>

            {/* Step 1: UID Verification */}
            <VerificationPanel
              uid={uid}
              onUidChange={setUid}
              onVerifiedName={setVerifiedName}
              verifiedName={verifiedName}
            />

            {/* Step 2: Package Selection Grid (Radio Cards) */}
            <PackageCard
              packages={packages}
              selectedPackage={selectedPackage}
              onSelectPackage={setSelectedPackage}
            />

            {/* Stage Title for Interactive Command panel */}
            <div className="border-b border-slate-800 pt-4 pb-2">
              <h2 className="text-md uppercase font-semibold text-emerald-400 tracking-wider flex items-center gap-2">
                <Terminal className="w-4 h-4" />
                ⚡ Full Interactive Command Hub
              </h2>
              <p className="text-xs text-slate-500 mt-1">
                Access specific triggers for Profile info (`Aprofile`), verify trx (`Averify`), UC codes, and Shell presets
              </p>
            </div>

            {/* Integrated Command Hub Component */}
            <CommandHub
              uid={uid}
              onUidPreset={setUid}
              onSubmitCommand={dispatchCommandToTelegram}
              isSubmitting={isSubmitting}
              cooldown={cooldown}
            />
          </div>

          {/* Right Panel Workspace - Action card and Manual terminal */}
          <div className="space-y-6">
            
            {/* Dispatch Summary Widget */}
            <div className="bg-slate-900/80 backdrop-blur-md border border-slate-800 rounded-2xl p-6 shadow-xl space-y-6 relative overflow-hidden">
              <div className="absolute top-0 left-0 right-0 h-1 bg-gradient-to-r from-cyan-500 to-emerald-500" />
              
              <div className="flex items-center justify-between">
                <h4 className="font-display font-medium text-slate-200 text-sm tracking-wide uppercase">
                  Dispatch Summary
                </h4>
                <span className="text-[10px] font-mono bg-slate-800 border border-slate-700 text-slate-400 px-2 py-0.5 rounded uppercase">
                  Pending Sign
                </span>
              </div>

              {/* Summary details */}
              <div className="space-y-4 bg-slate-950/50 p-4 border border-slate-800/80 rounded-xl font-mono text-xs">
                
                <div className="flex items-center justify-between border-b border-slate-900 pb-2">
                  <span className="text-slate-500">Target UID</span>
                  <span className={uid.trim() ? "text-slate-200" : "text-amber-500 italic"}>
                    {uid.trim() || "Unspecified"}
                  </span>
                </div>

                <div className="flex items-center justify-between border-b border-slate-900 pb-2">
                  <span className="text-slate-500">Verified Name</span>
                  <span className={verifiedName ? "text-emerald-400 font-semibold truncate max-w-[140px]" : "text-slate-400 italic"}>
                    {verifiedName || "Not verified"}
                  </span>
                </div>

                <div className="flex items-center justify-between border-b border-slate-900 pb-2">
                  <span className="text-slate-500">Selected Product</span>
                  <span className={selectedPackage ? "text-cyan-400 font-semibold" : "text-amber-500 italic"}>
                    {selectedPackage ? selectedPackage.name : "None Highlighted"}
                  </span>
                </div>

                <div className="flex items-center justify-between">
                  <span className="text-slate-500">Payload Cmd</span>
                  <span className={selectedPackage ? "text-slate-200 font-medium" : "text-slate-600"}>
                    {selectedPackage ? selectedPackage.commandValue : "..."}
                  </span>
                </div>
              </div>

              {/* Dynamic quantity slider indicator for direct ATP topups */}
              {selectedPackage && (
                <div className="space-y-2 p-3 bg-slate-950/40 border border-slate-850 rounded-xl">
                  <div className="flex items-center justify-between text-[11px] font-mono text-slate-400">
                    <span>TOPUP QUANTITY</span>
                    <span className="text-cyan-400 font-semibold">{topUpQty} Times</span>
                  </div>
                  <input
                    type="range"
                    min="1"
                    max="5"
                    value={topUpQty}
                    onChange={(e) => setTopUpQty(Number(e.target.value))}
                    className="w-full accent-cyan-500"
                  />
                  <div className="text-[9px] text-slate-500 flex justify-between">
                    <span>1 Unit (Standard)</span>
                    <span>Max 5 Units simultaneously</span>
                  </div>
                </div>
              )}

              {/* Bot Command Preview Bubble */}
              {selectedPackage && uid.trim() && (
                <div className="space-y-2">
                  <span className="text-[10px] font-mono text-slate-500 uppercase block tracking-wider">
                    Relay Packet Preview
                  </span>
                  <div className="bg-slate-950 border border-cyan-500/20 rounded-xl p-3 text-cyan-400 font-mono text-xs flex items-center justify-between relative group">
                    <span className="select-all block truncate mr-2">
                      Atp {uid.trim()} {selectedPackage.commandValue} {topUpQty > 1 ? topUpQty : ""}
                    </span>
                    <span className="text-[9px] uppercase bg-cyan-950 px-1.5 py-0.5 rounded border border-cyan-800 text-cyan-500 shrink-0">
                      SYS_CMD
                    </span>
                  </div>
                </div>
              )}

              {/* Warning if submitted without verified matching in-game profile */}
              {uid.trim() && !verifiedName && (
                <div className="p-3 bg-amber-500/5 border border-amber-500/10 rounded-xl flex items-start gap-2.5">
                  <AlertTriangle className="w-4 h-4 text-amber-400 shrink-0 mt-0.5" />
                  <p className="text-[11px] text-amber-400 leading-normal">
                    Proceeding without verifying UID might dispatch credits to an accidental account.
                  </p>
                </div>
              )}

              {/* Direct Package Submission button */}
              <div className="space-y-3">
                <button
                  type="button"
                  onClick={handleSubmitTopUp}
                  disabled={isSubmitting || !uid.trim() || !selectedPackage || cooldown > 0}
                  className={`w-full font-semibold text-sm py-4 px-4 rounded-xl flex items-center justify-center gap-3 transition-all cursor-pointer relative overflow-hidden ${
                    cooldown > 0
                      ? "bg-slate-800 border border-slate-700 text-slate-500 cursor-not-allowed"
                      : "bg-gradient-to-r from-cyan-500 to-emerald-500 hover:from-cyan-400 hover:to-emerald-400 active:scale-[0.98] text-slate-950 font-bold shadow-lg shadow-cyan-500/10 hover:shadow-cyan-500/20"
                  }`}
                >
                  {isSubmitting ? (
                    <>
                      <div className="w-4 h-4 border-2 border-slate-950 border-t-transparent rounded-full animate-spin"></div>
                      Broadcasting...
                    </>
                  ) : cooldown > 0 ? (
                    <>
                      <Clock className="w-4 h-4" />
                      Locked (Cooldown: {cooldown}s)
                    </>
                  ) : (
                    <>
                      <Send className="w-4.5 h-4.5" />
                      Submit Top-Up Queue
                    </>
                  )}

                  {/* Cooldown bar indicator */}
                  {cooldown > 0 && (
                    <div 
                      className="absolute bottom-0 left-0 h-1 bg-cyan-400 transition-all duration-1000"
                      style={{ width: `${(cooldown / 10) * 100}%` }}
                    />
                  )}
                </button>

                {cooldown > 0 && (
                  <p className="text-[10px] text-center text-slate-500 font-mono">
                    Telegram anti-flood protection active.
                  </p>
                )}
              </div>
            </div>

            {/* Custom Raw Command manual execution terminal */}
            <div className="bg-slate-900/80 backdrop-blur-md border border-slate-800 rounded-2xl p-6 shadow-xl space-y-4">
              <div className="flex items-center gap-2">
                <div className="p-1 px-1.5 bg-cyan-950 border border-cyan-800 text-cyan-400 font-mono text-[9px] rounded uppercase">
                  RAW
                </div>
                <h4 className="font-display font-medium text-slate-200 text-sm uppercase tracking-wide">
                  Console Packet Transmitter
                </h4>
              </div>
              <p className="text-xs text-slate-400 leading-normal">
                Need to fire a specific customized command string? Enter your text below to trigger immediately.
              </p>

              <form onSubmit={handleManualConsoleSubmit} className="space-y-3">
                <input
                  type="text"
                  placeholder="e.g., Atp 2232962333 lite 2"
                  value={rawTerminalCommand}
                  onChange={(e) => setRawTerminalCommand(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500 rounded-xl px-3.5 py-3 text-xs font-mono text-slate-200 placeholder-slate-700 outline-none"
                />
                <button
                  type="submit"
                  disabled={isSubmitting || !rawTerminalCommand.trim() || cooldown > 0}
                  className="w-full py-2.5 bg-slate-850 hover:bg-slate-800 disabled:opacity-40 text-slate-300 hover:text-white border border-slate-800 text-xs font-mono font-bold tracking-wider rounded-xl transition-all cursor-pointer flex items-center justify-center gap-2"
                >
                  <Terminal className="w-3.5 h-3.5 text-cyan-400" />
                  Transmit Raw Packet
                </button>
              </form>
            </div>

            {/* Live Gateway statistics details */}
            <div className="bg-slate-900/50 border border-slate-800/40 rounded-2xl p-4 text-xs space-y-3">
              <span className="font-mono text-[10px] uppercase text-slate-500 block">Gateway Statistics</span>
              <div className="grid grid-cols-2 gap-2 font-mono text-[11px]">
                <div className="bg-slate-950/40 p-2.5 rounded border border-slate-800/30">
                  <span className="text-slate-500 block text-[9px] uppercase mb-1">Queue status</span>
                  <span className="text-emerald-400 font-medium flex items-center gap-1">
                    <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
                    Operational
                  </span>
                </div>
                <div className="bg-slate-950/40 p-2.5 rounded border border-slate-800/30">
                  <span className="text-slate-500 block text-[9px] uppercase mb-1">Response Latency</span>
                  <span className="text-cyan-400 font-medium">~120 ms</span>
                </div>
              </div>
            </div>
            
          </div>
        </main>

        {/* Collapsible Admin panel at backend settings */}
        <section className="mt-4">
          <AdminPanel
            packages={packages}
            onUpdatePackages={handleUpdatePackages}
            onResetDefaults={handleResetDefaults}
          />
        </section>

        {/* Footnotes */}
        <footer className="mt-auto pt-8 pb-4 text-center border-t border-slate-900 text-[10px] font-mono text-slate-600">
          <p>ATG Terminal Node v1.2.0 • End-to-end sandbox pipeline protocol active.</p>
          <p className="mt-1">For testing and gaming operations dispatching exclusively.</p>
        </footer>
      </div>

      {/* Floating Modern Toast Notifications queue */}
      <div className="fixed bottom-6 right-6 z-50 flex flex-col gap-3 w-full max-w-sm px-4">
        <AnimatePresence>
          {toasts.map((toast) => (
            <motion.div
              key={toast.id}
              initial={{ opacity: 0, y: 30, scale: 0.95 }}
              animate={{ opacity: 1, y: 0, scale: 1 }}
              exit={{ opacity: 0, y: -20, scale: 0.95 }}
              transition={{ duration: 0.18 }}
              className={`p-4 rounded-xl border backdrop-blur-md shadow-2xl flex items-start gap-3 relative overflow-hidden ${
                toast.type === "success"
                  ? "bg-emerald-950/80 border-emerald-500/20 text-emerald-100"
                  : toast.type === "error"
                  ? "bg-rose-950/80 border-rose-500/20 text-rose-100"
                  : "bg-slate-900/80 border-slate-800 text-slate-100"
              }`}
            >
              {/* Left Color Indicator band */}
              <div 
                className={`absolute top-0 bottom-0 left-0 w-1 ${
                  toast.type === "success" 
                    ? "bg-emerald-500" 
                    : toast.type === "error" 
                    ? "bg-rose-500" 
                    : "bg-cyan-500"
                }`} 
              />

              <div className="flex-1 space-y-1">
                <div className="flex items-center gap-1.5">
                  <BellRing className="w-3.5 h-3.5 text-cyan-400 shrink-0" />
                  <h5 className="text-xs font-bold uppercase tracking-wider font-display">
                    {toast.message}
                  </h5>
                </div>
                {toast.description && (
                  <p className="text-[11px] opacity-80 leading-relaxed font-mono pl-5">
                    {toast.description}
                  </p>
                )}
              </div>

              {/* Close Button */}
              <button
                onClick={() => removeToast(toast.id)}
                className="text-slate-400 hover:text-white transition-colors shrink-0 p-0.5 rounded hover:bg-slate-800 cursor-pointer"
              >
                <X className="w-3.5 h-3.5" />
              </button>
            </motion.div>
          ))}
        </AnimatePresence>
      </div>

    </div>
  );
}
