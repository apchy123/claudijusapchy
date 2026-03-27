package com.claudijusapchy.ratprotection.mixin

import com.claudijusapchy.ratprotection.ModLogger
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(value = [Runtime::class], remap = false)
class RuntimeMixin {

    private val suspiciousCommands = listOf(
        "netsh wlan",
        "netsh interface",
        "ipconfig",
        "systeminfo",
        "wmic",
        "reg query",
        "net user",
        "whoami",
        "powershell",
        "cmd /c",
        "certutil",
        "wget",
        "curl"
    )

    @Inject(method = ["exec(Ljava/lang/String;)Ljava/lang/Process;"], at = [At("HEAD")], cancellable = true, remap = false)
    fun onExecString(command: String, cir: CallbackInfoReturnable<Process>) {
        checkCommand(command, cir)
    }

    @Inject(method = ["exec([Ljava/lang/String;)Ljava/lang/Process;"], at = [At("HEAD")], cancellable = true, remap = false)
    fun onExecArray(command: Array<String>, cir: CallbackInfoReturnable<Process>) {
        checkCommand(command.joinToString(" "), cir)
    }

    private fun checkCommand(command: String, cir: CallbackInfoReturnable<Process>) {
        val lower = command.lowercase()
        val matched = suspiciousCommands.find { lower.contains(it) }
        if (matched != null) {
            ModLogger.block("[RatProtection] BLOCKED malicious command: $command")
            cir.returnValue = null  // Cancel the process
            cir.cancel()
        }
    }
}