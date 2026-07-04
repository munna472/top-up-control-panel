import React, { useState } from "react";
import { Search, CheckCircle, AlertCircle, RefreshCw, UserCheck } from "lucide-react";

interface VerificationPanelProps {
  uid: string;
  onUidChange: (val: string) => void;
  onVerifiedName: (name: string | null) => void;
  verifiedName: string | null;
}

export default function VerificationPanel({
  uid,
  onUidChange,
  onVerifiedName,
  verifiedName,
}: VerificationPanelProps) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isVerificationEnabled, setIsVerificationEnabled] = useState(true);

  const handleVerify = async () => {
    const cleanUid = uid.trim();
    if (!cleanUid) {
      setError("Please key in a valid Player UID");
      onVerifiedName(null);
      return;
    }

    setLoading(true);
    setError(null);
    onVerifiedName(null);

    const apiEndpoint = `https://test1.mraipay.top/mraiprimetop1/player?uid=${encodeURIComponent(cleanUid)}`;

    try {
      const controller = new AbortController();
      const id = setTimeout(() => controller.abort(), 7000); // 7-second timeout fallback

      const res = await fetch(apiEndpoint, {
        method: "GET",
        signal: controller.signal,
      });
      clearTimeout(id);

      if (!res.ok) {
        throw new Error(`Server returned status: ${res.status}`);
      }

      const data = await res.json();
      
      if (data && typeof data === "object") {
        if (data.account) {
          onVerifiedName(data.account);
          setError(null);
        } else {
          // Fallback if data doesn't contain account
          onVerifiedName("Verified Account");
          setError("API returned success, but account name was empty. Proceed with caution.");
        }
      } else {
        throw new Error("Invalid response format received from server");
      }
    } catch (err: any) {
      console.error("Verification error:", err);
      let errMsg = "Unable to reach verification database. Server might be offline.";
      if (err.name === 'AbortError') {
        errMsg = "Verification request timed out. Please try again or type manually.";
      } else if (err.message) {
        errMsg = `Error: ${err.message}. Double check UID format.`;
      }
      setError(errMsg);
      onVerifiedName(null);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter" && isVerificationEnabled) {
      e.preventDefault();
      handleVerify();
    }
  };

  return (
    <div className="bg-slate-900/80 backdrop-blur-md border border-slate-800 rounded-2xl p-6 shadow-xl space-y-4">
      <div className="flex items-center justify-between flex-wrap gap-2">
        <label className="block font-display font-medium text-slate-100 flex items-center gap-2">
          <span className="w-1.5 h-3 bg-cyan-400 rounded-sm"></span>
          Step 1: Player UID
        </label>
        
        <div className="flex items-center gap-3 bg-slate-950/60 p-1.5 px-3 rounded-lg border border-slate-850">
          <span className="text-[11px] font-mono text-slate-400">Live Check:</span>
          <button
            type="button"
            onClick={() => {
              const nextVal = !isVerificationEnabled;
              setIsVerificationEnabled(nextVal);
              setError(null);
              if (!nextVal) {
                onVerifiedName("Skipped (Optional Mode)");
              } else {
                onVerifiedName(null);
              }
            }}
            className={`px-2.5 py-1 rounded-md text-[10px] font-mono tracking-wider transition-all uppercase font-bold cursor-pointer ${
              isVerificationEnabled 
                ? "bg-cyan-500/10 border border-cyan-500/30 text-cyan-400" 
                : "bg-slate-800 border border-slate-700 text-slate-500"
            }`}
          >
            {isVerificationEnabled ? "Active (Recommended)" : "Optional (Skipped)"}
          </button>
        </div>
      </div>

      <div className="flex gap-2 flex-col sm:flex-row">
        <div className="relative flex-1">
          <input
            type="text"
            placeholder="Enter Player UID (e.g. 2777402998)"
            value={uid}
            onKeyDown={handleKeyDown}
            onChange={(e) => {
              onUidChange(e.target.value);
              if (error) setError(null);
              if (isVerificationEnabled) {
                if (verifiedName) onVerifiedName(null);
              } else {
                if (e.target.value.trim()) {
                  onVerifiedName("Skipped (Optional Mode)");
                } else {
                  onVerifiedName(null);
                }
              }
            }}
            className="w-full bg-slate-950 border border-slate-800 focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500 rounded-xl pl-10 pr-4 py-3 text-slate-200 placeholder-slate-600 outline-none transition-all font-mono tracking-wide text-sm"
          />
          <Search className="w-4 h-4 text-slate-600 absolute left-3.5 top-3.5" />
        </div>
        
        {isVerificationEnabled && (
          <button
            onClick={handleVerify}
            type="button"
            disabled={loading || !uid.trim()}
            className="bg-cyan-600 hover:bg-cyan-500 disabled:opacity-40 disabled:hover:bg-cyan-600 disabled:cursor-not-allowed font-medium text-slate-100 text-sm py-3 px-6 rounded-xl transition-all shadow-lg hover:shadow-cyan-500/20 active:scale-[0.98] outline-none flex items-center justify-center gap-2 cursor-pointer min-w-[130px]"
          >
            {loading ? (
              <>
                <RefreshCw className="w-4 h-4 animate-spin-slow" />
                Verifying...
              </>
            ) : (
              <>
                <UserCheck className="w-4 h-4" />
                Verify UID
              </>
            )}
          </button>
        )}
      </div>

      {/* Skipping message when toggled off */}
      {!isVerificationEnabled && uid.trim() && (
        <div className="p-3 bg-slate-950/60 rounded-xl border border-slate-850/60 flex items-center gap-2 text-xs text-slate-400 font-mono">
          <span className="w-1.5 h-1.5 rounded-full bg-cyan-500"></span>
          Direct routing enabled. Verified player profile lookup is skipped.
        </div>
      )}

      {/* Loading indicator */}
      {loading && isVerificationEnabled && (
        <div className="p-3 bg-slate-950/40 rounded-xl border border-slate-880/40 flex items-center gap-2 text-xs text-slate-400 font-mono animate-pulse">
          <div className="w-1.5 h-1.5 rounded-full bg-cyan-400 animate-ping"></div>
          Contacting FreeFire UID Gateway system...
        </div>
      )}

      {/* Verification Results Status */}
      {verifiedName && isVerificationEnabled && verifiedName !== "Skipped (Optional Mode)" && (
        <div className="p-4 bg-emerald-500/10 border border-emerald-500/20 rounded-xl flex items-center gap-3 shadow-[0_0_15px_-3px_rgba(16,185,129,0.1)]">
          <CheckCircle className="w-5 h-5 text-emerald-400 shrink-0" />
          <div className="min-w-0">
            <span className="text-[10px] uppercase font-mono tracking-wider text-emerald-500 block leading-none">Database Identity Match</span>
            <span className="text-sm font-semibold text-emerald-400 tracking-wider font-mono">
              Player Name: {verifiedName}
            </span>
          </div>
        </div>
      )}

      {/* Error state */}
      {error && isVerificationEnabled && (
        <div className="p-4 bg-rose-500/10 border border-rose-500/20 rounded-xl flex flex-col gap-2 shadow-[0_0_15px_-3px_rgba(239,68,68,0.1)]">
          <div className="flex items-center gap-3">
            <AlertCircle className="w-5 h-5 text-rose-400 shrink-0" />
            <div className="text-xs text-rose-400 font-mono">
              {error}
            </div>
          </div>
          <p className="text-[11px] text-slate-500 pl-8 leading-normal">
            If you are sure the UID is correct, you can still select a package below and submit the transaction.
          </p>
        </div>
      )}
    </div>
  );
}
