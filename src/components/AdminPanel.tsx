import React, { useState } from "react";
import { TopUpPackage } from "../types";
import { Settings, Plus, Trash2, Edit2, Check, RotateCcw, X } from "lucide-react";
import { motion, AnimatePresence } from "motion/react";

interface AdminPanelProps {
  packages: TopUpPackage[];
  onUpdatePackages: (packages: TopUpPackage[]) => void;
  onResetDefaults: () => void;
}

export default function AdminPanel({
  packages,
  onUpdatePackages,
  onResetDefaults,
}: AdminPanelProps) {
  const [isOpen, setIsOpen] = useState(false);
  
  // Form states for creating a new package
  const [newName, setNewName] = useState("");
  const [newCommand, setNewCommand] = useState("");
  
  // State for tracking which package is being edited
  const [editingId, setEditingId] = useState<string | null>(null);
  const [editName, setEditName] = useState("");
  const [editCommand, setEditCommand] = useState("");

  const handleAddPackage = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newName.trim() || !newCommand.trim()) return;

    const newPkg: TopUpPackage = {
      id: Date.now().toString(),
      name: newName.trim(),
      commandValue: newCommand.trim(),
      isCustom: true,
    };

    onUpdatePackages([...packages, newPkg]);
    setNewName("");
    setNewCommand("");
  };

  const handleDeletePackage = (id: string) => {
    onUpdatePackages(packages.filter((pkg) => pkg.id !== id));
    if (editingId === id) {
      setEditingId(null);
    }
  };

  const startEditing = (pkg: TopUpPackage) => {
    setEditingId(pkg.id);
    setEditName(pkg.name);
    setEditCommand(pkg.commandValue);
  };

  const handleSaveEdit = (id: string) => {
    if (!editName.trim() || !editCommand.trim()) return;
    
    const updated = packages.map((pkg) =>
      pkg.id === id
        ? { ...pkg, name: editName.trim(), commandValue: editCommand.trim() }
        : pkg
    );
    onUpdatePackages(updated);
    setEditingId(null);
  };

  const cancelEditing = () => {
    setEditingId(null);
  };

  return (
    <div className="w-full bg-slate-900/80 backdrop-blur-md border border-slate-800 rounded-2xl overflow-hidden shadow-2xl transition-all duration-300">
      {/* Header / Toggle Button */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="w-full px-6 py-4 flex items-center justify-between text-left cursor-pointer hover:bg-slate-850 transition-colors"
      >
        <div className="flex items-center gap-3">
          <div className="p-2 rounded-xl bg-cyan-500/10 border border-cyan-500/30 text-cyan-400">
            <Settings className={`w-5 h-5 ${isOpen ? "rotate-45" : ""} transition-transform duration-500`} />
          </div>
          <div>
            <h3 className="font-display font-medium text-lg text-slate-100 flex items-center gap-2">
              Admin Gateway Settings
              <span className="text-xs font-mono font-normal bg-cyan-950 text-cyan-400 border border-cyan-800 px-2 py-0.5 rounded-full">
                {packages.length} active commands
              </span>
            </h3>
            <p className="text-xs text-slate-400 mt-0.5">Configure top-up packages, credentials, and custom commands</p>
          </div>
        </div>
        <div className={`w-6 h-6 flex items-center justify-center rounded-full bg-slate-800 text-slate-400 hover:text-white transition-colors`}>
          <span className="text-xl leading-none">{isOpen ? "−" : "+"}</span>
        </div>
      </button>

      {/* Accordion Content */}
      <AnimatePresence initial={false}>
        {isOpen && (
          <motion.div
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: "auto", opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.25 }}
            className="overflow-hidden"
          >
            <div className="p-6 border-t border-slate-800 space-y-6">
              {/* Add New Package Form */}
              <div className="bg-slate-950/50 p-4 border border-slate-800/80 rounded-xl">
                <h4 className="text-sm font-display font-medium text-slate-200 mb-3 flex items-center gap-2">
                  <span className="w-1.5 h-1.5 rounded-full bg-cyan-400"></span>
                  Create Custom Package
                </h4>
                <form onSubmit={handleAddPackage} className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                  <div>
                    <label className="block text-[11px] font-mono uppercase tracking-wider text-slate-400 mb-1">Package Name</label>
                    <input
                      type="text"
                      placeholder="e.g., 500 Diamonds"
                      value={newName}
                      onChange={(e) => setNewName(e.target.value)}
                      className="w-full bg-slate-900 border border-slate-800 focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500 rounded-lg px-3 py-2 text-sm text-slate-200 placeholder-slate-500 outline-none transition-all"
                    />
                  </div>
                  <div>
                    <label className="block text-[11px] font-mono uppercase tracking-wider text-slate-400 mb-1">Command Code</label>
                    <input
                      type="text"
                      placeholder="e.g., 500"
                      value={newCommand}
                      onChange={(e) => setNewCommand(e.target.value)}
                      className="w-full bg-slate-900 border border-slate-800 focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500 rounded-lg px-3 py-2 text-sm text-slate-200 placeholder-slate-500 outline-none transition-all"
                    />
                  </div>
                  <div className="flex items-end">
                    <button
                      type="submit"
                      disabled={!newName.trim() || !newCommand.trim()}
                      className="w-full bg-cyan-600 hover:bg-cyan-500 disabled:opacity-40 disabled:cursor-not-allowed text-white font-medium text-sm py-2 px-4 rounded-lg flex items-center justify-center gap-2 transition-all shadow-lg hover:shadow-cyan-500/20 active:scale-[0.98] cursor-pointer"
                    >
                      <Plus className="w-4 h-4" />
                      Add Package
                    </button>
                  </div>
                </form>
              </div>

              {/* Package List Grid */}
              <div className="space-y-3">
                <div className="flex justify-between items-center">
                  <h4 className="text-sm font-display font-medium text-slate-200 flex items-center gap-2">
                    <span className="w-1.5 h-1.5 rounded-full bg-cyan-400"></span>
                    Manage Package Database
                  </h4>
                  <button
                    onClick={onResetDefaults}
                    className="text-xs text-rose-400 hover:text-rose-300 flex items-center gap-1.5 border border-rose-500/10 hover:border-rose-500/30 px-2.5 py-1 rounded-lg bg-rose-500/5 transition-all cursor-pointer"
                  >
                    <RotateCcw className="w-3.5 h-3.5" />
                    Reset to Default List
                  </button>
                </div>

                <div className="max-h-[300px] overflow-y-auto pr-1 space-y-2 border border-slate-800/80 rounded-xl p-3 bg-slate-950/20 custom-scrollbar">
                  {packages.map((pkg) => (
                    <div
                      key={pkg.id}
                      className="flex items-center justify-between p-2 rounded-lg bg-slate-900/60 border border-slate-800/50 hover:border-slate-800 transition-all gap-3"
                    >
                      {editingId === pkg.id ? (
                        /* Edit mode */
                        <div className="flex-1 grid grid-cols-1 sm:grid-cols-2 gap-2">
                          <input
                            type="text"
                            value={editName}
                            onChange={(e) => setEditName(e.target.value)}
                            className="bg-slate-950 border border-slate-800 focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500 rounded px-2 py-1 text-xs text-slate-200 outline-none"
                            placeholder="Name"
                          />
                          <input
                            type="text"
                            value={editCommand}
                            onChange={(e) => setEditCommand(e.target.value)}
                            className="bg-slate-950 border border-slate-800 focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500 rounded px-2 py-1 text-xs text-slate-200 outline-none"
                            placeholder="Command"
                          />
                        </div>
                      ) : (
                        /* Normal display mode */
                        <div className="flex-1 min-w-0 flex items-center justify-between">
                          <span className="text-sm text-slate-200 font-medium truncate">{pkg.name}</span>
                          <span className="text-xs font-mono bg-slate-800 border border-slate-700/50 text-slate-400 px-2 py-0.5 rounded ml-2">
                            Cmd: {pkg.commandValue}
                          </span>
                        </div>
                      )}

                      <div className="flex items-center gap-1.5">
                        {editingId === pkg.id ? (
                          <>
                            <button
                              onClick={() => handleSaveEdit(pkg.id)}
                              className="p-1.5 rounded bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 hover:bg-emerald-500/20 hover:text-emerald-300 transition-all cursor-pointer"
                              title="Save Package"
                            >
                              <Check className="w-3.5 h-3.5" />
                            </button>
                            <button
                              onClick={cancelEditing}
                              className="p-1.5 rounded bg-slate-800 border border-slate-700 text-slate-400 hover:bg-slate-700 hover:text-slate-200 transition-all cursor-pointer"
                              title="Cancel"
                            >
                              <X className="w-3.5 h-3.5" />
                            </button>
                          </>
                        ) : (
                          <>
                            <button
                              onClick={() => startEditing(pkg)}
                              className="p-1.5 rounded bg-slate-850 border border-slate-800/80 text-slate-400 hover:text-cyan-400 hover:bg-cyan-500/5 hover:border-cyan-500/20 transition-all cursor-pointer"
                              title="Edit"
                            >
                              <Edit2 className="w-3.5 h-3.5" />
                            </button>
                            <button
                              onClick={() => handleDeletePackage(pkg.id)}
                              className="p-1.5 rounded bg-slate-850 border border-slate-800/80 text-slate-400 hover:text-rose-400 hover:bg-rose-500/5 hover:border-rose-500/20 transition-all cursor-pointer"
                              title="Delete"
                            >
                              <Trash2 className="w-3.5 h-3.5" />
                            </button>
                          </>
                        )}
                      </div>
                    </div>
                  ))}
                  {packages.length === 0 && (
                    <div className="py-8 text-center text-slate-500 text-sm">
                      No packages registered. Click "Reset to Default List" to load presets.
                    </div>
                  )}
                </div>
              </div>

              {/* Bot Credentials Summary */}
              <div className="bg-slate-950/40 p-4 border border-slate-800/40 rounded-xl text-xs space-y-1">
                <span className="font-mono text-[10px] uppercase tracking-wider text-slate-500 block">Telegram Channel Node</span>
                <p className="font-mono text-slate-300 truncate">Bot Token: <span className="text-cyan-500">8908339374:AAGDZJ...8AI</span></p>
                <p className="font-mono text-slate-300">Target Chat / Channel ID: <span className="text-emerald-500">-1004413191032</span></p>
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
