import React, { useState } from "react";
import { 
  User, 
  Wallet, 
  Clock, 
  Info, 
  DollarSign, 
  CheckSquare, 
  Star, 
  Layers, 
  Sparkles, 
  UserPlus, 
  Calculator, 
  BookOpen, 
  HelpCircle,
  Hash,
  Activity,
  Award,
  BookmarkCheck,
  Send,
  Coins
} from "lucide-react";
import { evaluateExpression } from "../utils/calculator";

interface CommandHubProps {
  uid: string;
  onUidPreset: (uid: string) => void;
  onSubmitCommand: (commandText: string, label: string) => void;
  isSubmitting: boolean;
  cooldown: number;
}

export default function CommandHub({
  uid,
  onUidPreset,
  onSubmitCommand,
  isSubmitting,
  cooldown
}: CommandHubProps) {
  const [activeTab, setActiveTab] = useState<"account" | "vouchers" | "rates" | "utilities">("account");
  
  // States for interactive inputs
  // 1. Verify TrxID
  const [trxId, setTrxId] = useState("");
  
  // 2. UC Purchase
  const [ucValue, setUcValue] = useState("161");
  const [ucQty, setUcQty] = useState<number>(1);
  const [customUc, setCustomUc] = useState("");
  const [useCustomUc, setUseCustomUc] = useState(false);

  // 3. Garena Shells
  const [shellAmount, setShellAmount] = useState("50");
  const [shellQty, setShellQty] = useState<number>(1);
  
  // 4. Calculator State
  const [calcExpr, setCalcExpr] = useState("");
  const [calcResult, setCalcResult] = useState<number | null>(null);

  // 5. Raw UID Input details query
  const [queryUid, setQueryUid] = useState("");

  const handleCalcSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!calcExpr.trim()) return;
    const res = evaluateExpression(calcExpr);
    if (res !== null) {
      setCalcResult(res);
      // Trigger evaluation command to telegram
      onSubmitCommand(calcExpr.trim(), `Calculator [${calcExpr.trim()}]`);
    } else {
      setCalcResult(null);
    }
  };

  const handleQueryUidSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!queryUid.trim()) return;
    onSubmitCommand(queryUid.trim(), `ID Details Check [${queryUid.trim()}]`);
  };

  const handleUcSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const activeUc = useCustomUc ? customUc.trim() : ucValue;
    if (!activeUc) return;
    
    // Command format: "Auc [UC] [qty]" (quantity omitted if is 1)
    const cmdStr = ucQty > 1 ? `Auc ${activeUc} ${ucQty}` : `Auc ${activeUc}`;
    onSubmitCommand(cmdStr, `Unipin UC Code (${activeUc} UC)`);
  };

  const handleShellSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!shellAmount) return;
    
    // Command format: "Ashell [amount] [qty]" (quantity omitted if is 1)
    const cmdStr = shellQty > 1 ? `Ashell ${shellAmount} ${shellQty}` : `Ashell ${shellAmount}`;
    onSubmitCommand(cmdStr, `Garena Shell (${shellAmount} Shells)`);
  };

  return (
    <div className="space-y-6">
      
      {/* Tab Navigation buttons */}
      <div className="flex border-b border-slate-800 bg-slate-900/40 p-1 rounded-xl overflow-x-auto w-full gap-1 custom-scrollbar">
        <button
          onClick={() => setActiveTab("account")}
          className={`flex items-center gap-2 px-4 py-2.5 rounded-lg text-xs font-semibold uppercase tracking-wider transition-all whitespace-nowrap cursor-pointer shrink-0 ${
            activeTab === "account"
              ? "bg-gradient-to-r from-cyan-600 to-cyan-500 text-slate-100 shadow-md"
              : "text-slate-400 hover:text-slate-200 hover:bg-slate-800/40"
          }`}
        >
          <User className="w-3.5 h-3.5" />
          👤 Account & Billing
        </button>

        <button
          onClick={() => setActiveTab("vouchers")}
          className={`flex items-center gap-2 px-4 py-2.5 rounded-lg text-xs font-semibold uppercase tracking-wider transition-all whitespace-nowrap cursor-pointer shrink-0 ${
            activeTab === "vouchers"
              ? "bg-gradient-to-r from-cyan-600 to-cyan-500 text-slate-100 shadow-md"
              : "text-slate-400 hover:text-slate-200 hover:bg-slate-800/40"
          }`}
        >
          <Coins className="w-3.5 h-3.5" />
          🎮 Vouchers (UC/Shells)
        </button>

        <button
          onClick={() => setActiveTab("rates")}
          className={`flex items-center gap-2 px-4 py-2.5 rounded-lg text-xs font-semibold uppercase tracking-wider transition-all whitespace-nowrap cursor-pointer shrink-0 ${
            activeTab === "rates"
              ? "bg-gradient-to-r from-cyan-600 to-cyan-500 text-slate-100 shadow-md"
              : "text-slate-400 hover:text-slate-200 hover:bg-slate-800/40"
          }`}
        >
          <Layers className="w-3.5 h-3.5" />
          📊 Rate Sheets
        </button>

        <button
          onClick={() => setActiveTab("utilities")}
          className={`flex items-center gap-2 px-4 py-2.5 rounded-lg text-xs font-semibold uppercase tracking-wider transition-all whitespace-nowrap cursor-pointer shrink-0 ${
            activeTab === "utilities"
              ? "bg-gradient-to-r from-cyan-600 to-cyan-500 text-slate-100 shadow-md"
              : "text-slate-400 hover:text-slate-200 hover:bg-slate-800/40"
          }`}
        >
          <Calculator className="w-3.5 h-3.5" />
          🛠️ Terminal Tools
        </button>
      </div>

      {/* Account & Billing Tab Content */}
      {activeTab === "account" && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          
          {/* Quick Info & Balance Queries */}
          <div className="bg-slate-900/80 backdrop-blur-md border border-slate-800 rounded-2xl p-6 shadow-xl space-y-4">
            <h4 className="font-display font-medium text-slate-200 text-sm flex items-center gap-2">
              <span className="w-1.5 h-3 bg-cyan-400 rounded-sm"></span>
              Client Account Actions
            </h4>
            <p className="text-xs text-slate-400 leading-normal">
              Click any profile button below to retrieve live statistics directly from your synced account backend.
            </p>

            <div className="grid grid-cols-2 gap-3 pt-2">
              <button
                onClick={() => onSubmitCommand("Aprofile", "View Profile")}
                disabled={isSubmitting || cooldown > 0}
                className="p-3.5 bg-slate-950 hover:bg-slate-850 border border-slate-800 hover:border-slate-700 rounded-xl text-left transition-all relative group cursor-pointer"
              >
                <User className="w-5 h-5 text-cyan-400 mb-2 group-hover:scale-110 transition-transform" />
                <span className="text-[11px] font-mono text-slate-500 block uppercase">Aprofile</span>
                <span className="text-xs font-semibold text-slate-200">View Profile Info</span>
              </button>

              <button
                onClick={() => onSubmitCommand("Abalance", "Check Balance")}
                disabled={isSubmitting || cooldown > 0}
                className="p-3.5 bg-slate-950 hover:bg-slate-850 border border-slate-800 hover:border-slate-700 rounded-xl text-left transition-all relative group cursor-pointer"
              >
                <Wallet className="w-5 h-5 text-emerald-400 mb-2 group-hover:scale-110 transition-transform" />
                <span className="text-[11px] font-mono text-slate-500 block uppercase">Abalance</span>
                <span className="text-xs font-semibold text-slate-200">Check Balance</span>
              </button>

              <button
                onClick={() => onSubmitCommand("Adue", "Check Due")}
                disabled={isSubmitting || cooldown > 0}
                className="p-3.5 bg-slate-950 hover:bg-slate-850 border border-slate-800 hover:border-slate-700 rounded-xl text-left transition-all relative group cursor-pointer"
              >
                <Clock className="w-5 h-5 text-rose-400 mb-2 group-hover:scale-110 transition-transform" />
                <span className="text-[11px] font-mono text-slate-500 block uppercase">Adue</span>
                <span className="text-xs font-semibold text-slate-200">Outstanding Due</span>
              </button>

              <button
                onClick={() => onSubmitCommand("Amyinfo", "My Info Details")}
                disabled={isSubmitting || cooldown > 0}
                className="p-3.5 bg-slate-950 hover:bg-slate-850 border border-slate-800 hover:border-slate-700 rounded-xl text-left transition-all relative group cursor-pointer"
              >
                <Info className="w-5 h-5 text-purple-400 mb-2 group-hover:scale-110 transition-transform" />
                <span className="text-[11px] font-mono text-slate-500 block uppercase">Amyinfo</span>
                <span className="text-xs font-semibold text-slate-200">Telegram Details</span>
              </button>
            </div>

            <div className="pt-2">
              <button
                onClick={() => onSubmitCommand("Aresetbaki", "Reset/Clear Due baki")}
                disabled={isSubmitting || cooldown > 0}
                className="w-full py-2.5 bg-rose-500/10 hover:bg-rose-500/15 border border-rose-500/20 hover:border-rose-500/30 text-rose-400 rounded-xl text-xs font-mono font-bold tracking-wider uppercase transition-all flex items-center justify-center gap-2 cursor-pointer"
              >
                🔄 Aresetbaki (Clear Outstanding Due)
              </button>
            </div>
          </div>

          {/* Add Money & Payment Verification */}
          <div className="bg-slate-900/80 backdrop-blur-md border border-slate-800 rounded-2xl p-6 shadow-xl space-y-4">
            <h4 className="font-display font-medium text-slate-200 text-sm flex items-center gap-2">
              <span className="w-1.5 h-3 bg-cyan-400 rounded-sm"></span>
              Add Cash & verify Payment
            </h4>
            <p className="text-xs text-slate-400 leading-normal">
              Check the operator's active number to cash-in, and then input your TrxID below for dynamic activation.
            </p>

            {/* Check Cash-In Numbers Button */}
            <button
              onClick={() => onSubmitCommand("Anumber", "Check Merchant Numbers")}
              disabled={isSubmitting || cooldown > 0}
              className="w-full p-4 bg-slate-950 hover:bg-slate-850 border border-slate-800 hover:border-slate-700 rounded-xl transition-all flex items-center justify-between text-left group cursor-pointer"
            >
              <div className="flex items-center gap-3">
                <div className="p-2 bg-cyan-950 border border-cyan-800 text-cyan-400 rounded-lg">
                  <DollarSign className="w-4 h-4" />
                </div>
                <div>
                  <span className="text-xs font-semibold text-slate-200 block">📱 View Merchant Numbers</span>
                  <span className="text-[10px] text-slate-400">Trigger standard cash-out / send money list commands</span>
                </div>
              </div>
              <span className="text-[10px] font-mono text-cyan-400 group-hover:translate-x-1 transition-transform">Anumber →</span>
            </button>

            {/* TrxID Verification Form */}
            <div className="bg-slate-950/40 p-4 border border-slate-800/80 rounded-xl space-y-3">
              <span className="text-[10px] font-mono uppercase tracking-wider text-slate-500 block">Verify with TrxID (Averify)</span>
              <div className="flex gap-2">
                <input
                  type="text"
                  placeholder="Enter TrxID (e.g. BG6JS8JD)"
                  value={trxId}
                  onChange={(e) => setTrxId(e.target.value)}
                  className="flex-1 bg-slate-900 border border-slate-800 focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500 rounded-lg px-3 py-2 text-xs font-mono text-slate-200 placeholder-slate-700 outline-none"
                />
                
                <button
                  onClick={() => {
                    const cleanTrx = trxId.trim();
                    if (!cleanTrx) return;
                    onSubmitCommand(`Averify ${cleanTrx}`, `Verify Payment (TrxID: ${cleanTrx})`);
                    setTrxId("");
                  }}
                  disabled={isSubmitting || !trxId.trim() || cooldown > 0}
                  className="bg-emerald-600 hover:bg-emerald-500 disabled:opacity-40 text-slate-100 text-xs font-semibold px-4 py-2 rounded-lg transition-all active:scale-[0.98] cursor-pointer"
                >
                  Verify TrxID
                </button>
              </div>
              <p className="text-[10px] text-slate-500 italic block leading-relaxed">
                Sends the live string: `Averify [trxID]`. E.g., `Averify BG6JS8JD`
              </p>
            </div>
          </div>
        </div>
      )}

      {/* UC & Garena Shell Vouchers Tab Content */}
      {activeTab === "vouchers" && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          
          {/* UC Purchase Widget */}
          <div className="bg-slate-900/80 backdrop-blur-md border border-slate-800 rounded-2xl p-6 shadow-xl space-y-4">
            <h4 className="font-display font-medium text-slate-200 text-sm flex items-center gap-2">
              <span className="w-1.5 h-3 bg-cyan-400 rounded-sm"></span>
              🎮 UniPin UC Purchase Hub
            </h4>
            <p className="text-xs text-slate-400">
              Select or type specific UC parameters options to dispatch commands. E.g. `Auc 161 4`
            </p>

            <form onSubmit={handleUcSubmit} className="space-y-4">
              <div>
                <label className="block text-[11px] font-mono text-slate-400 uppercase tracking-wider mb-2">Select UC Preset Value</label>
                <div className="grid grid-cols-3 gap-2">
                  {[
                    { label: "161 UC", value: "161" },
                    { label: "320 UC", value: "320" },
                    { label: "610 UC", value: "610" }
                  ].map((preset) => (
                    <button
                      key={preset.value}
                      type="button"
                      onClick={() => {
                        setUcValue(preset.value);
                        setUseCustomUc(false);
                      }}
                      className={`py-2 px-3 rounded-lg border text-xs font-mono font-medium transition-all cursor-pointer ${
                        ucValue === preset.value && !useCustomUc
                          ? "bg-cyan-500/10 border-cyan-400 text-cyan-400"
                          : "bg-slate-950 border-slate-850 text-slate-400 hover:border-slate-800"
                      }`}
                    >
                      {preset.label}
                    </button>
                  ))}
                </div>
              </div>

              {/* Custom Input override */}
              <div className="bg-slate-950/40 p-3.5 border border-slate-850 rounded-xl space-y-2">
                <div className="flex items-center justify-between">
                  <span className="text-[10px] font-mono uppercase tracking-wider text-slate-500">Custom UC Value</span>
                  <label className="flex items-center gap-1.5 text-xs text-slate-300 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={useCustomUc}
                      onChange={(e) => setUseCustomUc(e.target.checked)}
                      className="rounded border-slate-800 text-cyan-500 focus:ring-cyan-500"
                    />
                    Use custom value
                  </label>
                </div>
                {useCustomUc && (
                  <input
                    type="text"
                    placeholder="Enter custom UC code value"
                    value={customUc}
                    onChange={(e) => setCustomUc(e.target.value)}
                    className="w-full bg-slate-900 border border-slate-800 focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500 rounded-lg px-3 py-2 text-xs text-slate-200 outline-none"
                  />
                )}
              </div>

              {/* Multi topup package quantity */}
              <div>
                <div className="flex items-center justify-between text-[11px] font-mono text-slate-400 mb-1">
                  <span>PACK QUANTITY</span>
                  <span className="text-cyan-400 font-bold">{ucQty} Pack(s)</span>
                </div>
                <input
                  type="range"
                  min="1"
                  max="5"
                  value={ucQty}
                  onChange={(e) => setUcQty(Number(e.target.value))}
                  className="w-full accent-cyan-500"
                />
                <span className="text-[9px] text-slate-500 block mt-0.5 font-mono">⚠️ Max 5 Top-Ups simultaneously</span>
              </div>

              <button
                type="submit"
                disabled={isSubmitting || cooldown > 0 || (useCustomUc && !customUc.trim())}
                className="w-full py-3 bg-gradient-to-r from-cyan-600 to-cyan-500 hover:from-cyan-500 hover:to-cyan-400 disabled:opacity-40 text-slate-100 rounded-xl text-xs font-semibold uppercase tracking-wider transition-all cursor-pointer flex items-center justify-center gap-2"
              >
                <Send className="w-3.5 h-3.5" />
                Dispatch UC Order: {useCustomUc ? customUc || "?" : ucValue} UC {ucQty > 1 && `(${ucQty} Packs)`}
              </button>
            </form>
          </div>

          {/* Garena Shell Purchase Widget */}
          <div className="bg-slate-900/80 backdrop-blur-md border border-slate-800 rounded-2xl p-6 shadow-xl space-y-4">
            <h4 className="font-display font-medium text-slate-200 text-sm flex items-center gap-2">
              <span className="w-1.5 h-3 bg-cyan-400 rounded-sm"></span>
              🎯 Garena Shell Store
            </h4>
            <p className="text-xs text-slate-400">
              Pick amount and quantity for instant Garena Shell activations, formatting `Ashell [amount] [qty]`.
            </p>

            <form onSubmit={handleShellSubmit} className="space-y-4">
              <div>
                <label className="block text-[11px] font-mono text-slate-400 uppercase tracking-wider mb-2">Select Shell Amount</label>
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
                  {["50", "100", "330", "500"].map((val) => (
                    <button
                      key={val}
                      type="button"
                      onClick={() => setShellAmount(val)}
                      className={`py-2 px-3 rounded-lg border text-xs font-mono font-medium transition-all cursor-pointer ${
                        shellAmount === val
                          ? "bg-cyan-500/10 border-cyan-400 text-cyan-400"
                          : "bg-slate-950 border-slate-850 text-slate-400 hover:border-slate-800"
                      }`}
                    >
                      {val} Shells
                    </button>
                  ))}
                </div>
              </div>

              {/* Shell quantity */}
              <div>
                <div className="flex items-center justify-between text-[11px] font-mono text-slate-400 mb-1">
                  <span>PACK QUANTITY</span>
                  <span className="text-cyan-400 font-bold">{shellQty} Pack(s)</span>
                </div>
                <input
                  type="range"
                  min="1"
                  max="5"
                  value={shellQty}
                  onChange={(e) => setShellQty(Number(e.target.value))}
                  className="w-full accent-cyan-500"
                />
                <span className="text-[9px] text-slate-500 block mt-0.5 font-mono">⚠️ Max 5 Top-Ups simultaneously</span>
              </div>

              <div className="bg-slate-950/20 p-3 rounded-lg border border-slate-850/50 text-[11px] font-mono text-slate-500">
                Command payload outbed: <span className="text-cyan-400 font-bold">Ashell {shellAmount} {shellQty > 1 ? shellQty : ""}</span>
              </div>

              <button
                type="submit"
                disabled={isSubmitting || cooldown > 0}
                className="w-full py-3 bg-gradient-to-r from-emerald-600 to-emerald-500 hover:from-emerald-500 hover:to-emerald-400 disabled:opacity-40 text-slate-100 rounded-xl text-xs font-semibold uppercase tracking-wider transition-all cursor-pointer flex items-center justify-center gap-2"
              >
                <Send className="w-3.5 h-3.5" />
                Dispatch Garena shell order
              </button>
            </form>
          </div>
        </div>
      )}

      {/* Rate sheets inquiry */}
      {activeTab === "rates" && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          
          <button
            onClick={() => onSubmitCommand("Arate", "Check General Rates")}
            disabled={isSubmitting || cooldown > 0}
            className="p-5 bg-slate-900 hover:bg-slate-850 border border-slate-800 hover:border-slate-700 rounded-2xl flex flex-col justify-between text-left transition-all relative overflow-hidden group cursor-pointer h-40"
          >
            <div className="p-2.5 rounded-xl bg-cyan-950 border border-cyan-800/40 text-cyan-400 w-fit group-hover:scale-105 transition-transform">
              <BookOpen className="w-5 h-5" />
            </div>
            <div>
              <span className="font-mono text-[9px] uppercase text-slate-500 block mb-1">Inquiry Command • Arate</span>
              <h5 className="font-display font-semibold text-xs text-slate-100 group-hover:text-cyan-400 transition-colors">
                General Product Rates
              </h5>
              <p className="text-[10px] text-slate-400 mt-1">Get overall retail and reseller rate tables.</p>
            </div>
          </button>

          <button
            onClick={() => onSubmitCommand("Apacks", "Check Top-up Pack Commands")}
            disabled={isSubmitting || cooldown > 0}
            className="p-5 bg-slate-900 hover:bg-slate-850 border border-slate-800 hover:border-slate-700 rounded-2xl flex flex-col justify-between text-left transition-all relative overflow-hidden group cursor-pointer h-40"
          >
            <div className="p-2.5 rounded-xl bg-emerald-950 border border-emerald-800/40 text-emerald-400 w-fit group-hover:scale-105 transition-transform">
              <Layers className="w-5 h-5" />
            </div>
            <div>
              <span className="font-mono text-[9px] uppercase text-slate-500 block mb-1">Inquiry Command • Apacks</span>
              <h5 className="font-display font-semibold text-xs text-slate-100 group-hover:text-emerald-400 transition-colors">
                Diamond Packs Rates
              </h5>
              <p className="text-[10px] text-slate-400 mt-1">Query precise commands to use for direct diamonds.</p>
            </div>
          </button>

          <button
            onClick={() => onSubmitCommand("Adiamond", "Check BD & Indo Diamond Rates")}
            disabled={isSubmitting || cooldown > 0}
            className="p-5 bg-slate-900 hover:bg-slate-850 border border-slate-800 hover:border-slate-700 rounded-2xl flex flex-col justify-between text-left transition-all relative overflow-hidden group cursor-pointer h-40"
          >
            <div className="p-2.5 rounded-xl bg-purple-950 border border-purple-800/40 text-purple-400 w-fit group-hover:scale-105 transition-transform">
              <Star className="w-5 h-5" />
            </div>
            <div>
              <span className="font-mono text-[9px] uppercase text-slate-500 block mb-1">Inquiry Command • Adiamond</span>
              <h5 className="font-display font-semibold text-xs text-slate-100 group-hover:text-purple-400 transition-colors">
                BD & Indo Diamonds
              </h5>
              <p className="text-[10px] text-slate-400 mt-1">Compare regional FreeFire server diamond pricing.</p>
            </div>
          </button>

          <button
            onClick={() => onSubmitCommand("Alist", "Check UC / Diamond equivalence value list")}
            disabled={isSubmitting || cooldown > 0}
            className="p-5 bg-slate-900 hover:bg-slate-850 border border-slate-800 hover:border-slate-700 rounded-2xl flex flex-col justify-between text-left transition-all relative overflow-hidden group cursor-pointer h-40"
          >
            <div className="p-2.5 rounded-xl bg-amber-950 border border-amber-800/40 text-amber-400 w-fit group-hover:scale-105 transition-transform">
              <Activity className="w-5 h-5" />
            </div>
            <div>
              <span className="font-mono text-[9px] uppercase text-slate-500 block mb-1">Inquiry Command • Alist</span>
              <h5 className="font-display font-semibold text-xs text-slate-100 group-hover:text-amber-400 transition-colors">
                UC vs Diamond Values
              </h5>
              <p className="text-[10px] text-slate-400 mt-1">View comprehensive unit comparison listing.</p>
            </div>
          </button>
        </div>
      )}

      {/* Utilities: Calculator, custom raw query UID details */}
      {activeTab === "utilities" && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          
          {/* Live Telegram Calculator Widget */}
          <div className="bg-slate-900/80 backdrop-blur-md border border-slate-800 rounded-2xl p-6 shadow-xl space-y-4">
            <h4 className="font-display font-medium text-slate-200 text-sm flex items-center gap-2">
              <span className="w-1.5 h-3 bg-cyan-400 rounded-sm"></span>
              🧮 Interactive Terminal Calculator
            </h4>
            <p className="text-xs text-slate-400">
              Type equations directly (e.g., `100+100`, `100-90`, `100*6`, `100/10`). Triggers results instantly and logs math command packets.
            </p>

            <form onSubmit={handleCalcSubmit} className="space-y-4">
              <div>
                <label className="block text-[11px] font-mono text-slate-400 uppercase tracking-wider mb-2">Equation expression</label>
                <input
                  type="text"
                  placeholder="e.g. 100+100-90"
                  value={calcExpr}
                  onChange={(e) => {
                    setCalcExpr(e.target.value);
                    setCalcResult(null);
                  }}
                  className="w-full bg-slate-950 border border-slate-800 focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500 rounded-xl px-4 py-3 text-sm font-mono text-slate-200 outline-none"
                />
              </div>

              {calcResult !== null && (
                <div className="p-3.5 bg-emerald-500/10 border border-emerald-500/20 rounded-xl flex items-center justify-between font-mono">
                  <span className="text-xs text-slate-400">Result:</span>
                  <span className="text-sm font-bold text-emerald-400">{calcResult}</span>
                </div>
              )}

              <button
                type="submit"
                disabled={isSubmitting || !calcExpr.trim() || cooldown > 0}
                className="w-full py-3 bg-cyan-600 hover:bg-cyan-500 disabled:opacity-40 text-slate-100 rounded-xl text-xs font-semibold uppercase tracking-wider transition-all cursor-pointer flex items-center justify-center gap-2"
              >
                <Calculator className="w-4 h-4" />
                Evaluate & Dispatch Command
              </button>
            </form>
          </div>

          {/* Raw UID Enquiry */}
          <div className="bg-slate-900/80 backdrop-blur-md border border-slate-800 rounded-2xl p-6 shadow-xl space-y-4">
            <h4 className="font-display font-medium text-slate-200 text-sm flex items-center gap-2">
              <span className="w-1.5 h-3 bg-cyan-400 rounded-sm"></span>
              🔍 Direct UID Details Search
            </h4>
            <p className="text-xs text-slate-400">
              Trigger plain ID validation details from server queue. Writing simply raw digits is passed directly.
            </p>

            <form onSubmit={handleQueryUidSubmit} className="space-y-4">
              <div>
                <label className="block text-[11px] font-mono text-slate-400 uppercase tracking-wider mb-2">Target UID Code</label>
                <input
                  type="text"
                  placeholder="e.g. 2232962333"
                  value={queryUid}
                  onChange={(e) => setQueryUid(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500 rounded-xl px-4 py-3 text-sm font-mono text-slate-200 outline-none"
                />
              </div>

              <div className="bg-slate-950/20 p-3 rounded-lg border border-slate-850/50 text-[11px] font-mono text-slate-500 text-center">
                Command payload matched: <span className="text-cyan-400 font-bold">{queryUid || "..."}</span>
              </div>

              <div className="flex gap-2">
                <button
                  type="submit"
                  disabled={isSubmitting || !queryUid.trim() || cooldown > 0}
                  className="flex-1 py-3 bg-cyan-600 hover:bg-cyan-500 disabled:opacity-40 text-slate-100 rounded-xl text-xs font-semibold uppercase tracking-wider transition-all cursor-pointer flex items-center justify-center gap-2"
                >
                  <Send className="w-3.5 h-3.5" />
                  Request Details
                </button>
                
                {queryUid.trim() && (
                  <button
                    type="button"
                    onClick={() => {
                      onUidPreset(queryUid.trim());
                      setQueryUid("");
                      setActiveTab("account");
                    }}
                    className="p-3 bg-slate-800 hover:bg-slate-700 border border-slate-700 rounded-xl text-cyan-400 transition-all cursor-pointer"
                    title="Load into Main Gateway Panel"
                  >
                    <UserPlus className="w-4 h-4" />
                  </button>
                )}
              </div>
            </form>
          </div>
        </div>
      )}

    </div>
  );
}
