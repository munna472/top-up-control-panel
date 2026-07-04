import { useState } from "react";
import { TopUpPackage } from "../types";
import { ShoppingBag, Search, Sparkles, Star } from "lucide-react";

interface PackageCardProps {
  packages: TopUpPackage[];
  selectedPackage: TopUpPackage | null;
  onSelectPackage: (pkg: TopUpPackage) => void;
}

export default function PackageCard({
  packages,
  selectedPackage,
  onSelectPackage,
}: PackageCardProps) {
  const [searchTerm, setSearchTerm] = useState("");

  const filteredPackages = packages.filter((pkg) =>
    pkg.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    pkg.commandValue.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="bg-slate-900/80 backdrop-blur-md border border-slate-800 rounded-2xl p-6 shadow-xl space-y-4">
      {/* Header and Search filter */}
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <label className="block font-display font-medium text-slate-100 flex items-center gap-2">
            <span className="w-1.5 h-3 bg-cyan-400 rounded-sm"></span>
            Step 2: Select Top-Up Bundle
          </label>
          <p className="text-xs text-slate-400 mt-1">Pick your desired premium game package or voucher code</p>
        </div>

        {/* Quick Search */}
        <div className="relative w-full sm:w-64">
          <input
            type="text"
            placeholder="Search packages..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full bg-slate-950 border border-slate-800 focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500 rounded-xl pl-8 pr-3 py-1.5 text-xs text-slate-200 placeholder-slate-600 outline-none transition-all font-mono"
          />
          <Search className="w-3.5 h-3.5 text-slate-600 absolute left-2.5 top-2.5" />
        </div>
      </div>

      {/* Grid List */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3 max-h-[360px] overflow-y-auto pr-1 p-1 bg-slate-950/20 border border-slate-800/40 rounded-xl custom-scrollbar">
        {filteredPackages.map((pkg) => {
          const isSelected = selectedPackage?.id === pkg.id;
          const isDiamond = pkg.name.toLowerCase().includes("diamond");
          const isMember = pkg.name.toLowerCase().includes("membership");
          const isPass = pkg.name.toLowerCase().includes("pass") || pkg.name.toLowerCase().includes("lite") || pkg.name.toLowerCase().includes("evo");

          return (
            <button
              key={pkg.id}
              onClick={() => onSelectPackage(pkg)}
              type="button"
              className={`text-left p-4 rounded-xl border relative overflow-hidden transition-all duration-300 flex flex-col justify-between cursor-pointer outline-none group min-h-[90px] ${
                isSelected
                  ? "bg-cyan-500/10 border-cyan-400/80 shadow-[0_0_20px_-3px_rgba(6,182,212,0.25)]"
                  : "bg-slate-900 border-slate-800/80 hover:border-slate-700 hover:bg-slate-850/80 hover:shadow-lg"
              }`}
            >
              {/* Subtle visual icons indicating category */}
              <div className="absolute right-3 top-3 opacity-20 group-hover:opacity-30 transition-all text-cyan-400 pointer-events-none">
                {isDiamond && <Star className="w-5 h-5 fill-cyan-400" />}
                {isMember && <ShoppingBag className="w-5 h-5" />}
                {isPass && <Sparkles className="w-5 h-5" />}
              </div>

              {/* Selection Status dot */}
              <div className="flex items-start justify-between gap-2">
                <span className={`text-sm font-semibold truncate flex-1 ${
                  isSelected ? "text-cyan-400" : "text-slate-200"
                }`}>
                  {pkg.name}
                </span>
                
                {/* Custom radio button design */}
                <div className={`w-4 h-4 rounded-full border flex items-center justify-center shrink-0 mt-0.5 ${
                  isSelected ? "border-cyan-400" : "border-slate-700"
                }`}>
                  {isSelected && <div className="w-2 h-2 rounded-full bg-cyan-400"></div>}
                </div>
              </div>

              {/* Command Code Display Footer */}
              <div className="mt-3 flex items-center justify-between text-[11px] font-mono">
                <span className="text-slate-500">Command</span>
                <span className={`px-2 py-0.5 rounded border ${
                  isSelected
                    ? "bg-cyan-950 text-cyan-400 border-cyan-800/50"
                    : "bg-slate-950 text-slate-400 border-slate-800/50"
                }`}>
                  {pkg.commandValue}
                </span>
              </div>
            </button>
          );
        })}

        {filteredPackages.length === 0 && (
          <div className="col-span-full py-12 text-center text-slate-500 text-sm">
            No active bundles matching search filters found.
          </div>
        )}
      </div>
    </div>
  );
}
