package me.ezar.yetanothermacspoofer

import android.annotation.SuppressLint
import android.net.MacAddress
import dalvik.system.PathClassLoader
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import io.github.libxposed.api.annotations.AfterInvocation
import io.github.libxposed.api.annotations.BeforeInvocation
import io.github.libxposed.api.annotations.XposedHooker

lateinit var module: ModuleMain

class ModuleMain(base: XposedInterface, param: XposedModuleInterface.ModuleLoadedParam) : XposedModule(base, param) {

    @XposedHooker
    class WifiNativeHooker : XposedInterface.Hooker {

        companion object {

            @SuppressLint("PrivateApi")
            @BeforeInvocation
            @JvmStatic
            fun before(callback: XposedInterface.BeforeHookCallback) {
                val mac = "40:24:B2:F4:CB:F7"
                module.log("setStaMacAddress called! hijacking it to output $mac instead")
                callback.args[1] = MacAddress.fromString(mac)
            }

        }

    }

    @XposedHooker
    class SSSHooker : XposedInterface.Hooker {

        companion object {

            @SuppressLint("PrivateApi")
            @AfterInvocation
            @JvmStatic
            fun after(callback: XposedInterface.AfterHookCallback) {
                if (callback.args[0] == "com.android.server.wifi.WifiService") {
                    val classLoader = callback.args[1] as PathClassLoader

                    val setMacMethod = classLoader.loadClass("com.android.server.wifi.WifiNative").methods.first { it.name == "setStaMacAddress" }
                    module.hook(setMacMethod, WifiNativeHooker::class.java)

                }
            }

        }

    }

    @SuppressLint("PrivateApi")
    override fun onSystemServerLoaded(param: XposedModuleInterface.SystemServerLoadedParam) {
        super.onSystemServerLoaded(param)
        module = this
        val sss = param.classLoader.loadClass("com.android.server.SystemServiceManager")
        log("Loaded class ${sss.name}")

        val classLoadMethod = sss.methods.first { it.name == "loadClassFromLoader" }
        hook(classLoadMethod, SSSHooker::class.java)


    }

}