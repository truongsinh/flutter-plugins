// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.deviceinfo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.provider.Settings
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.util.Arrays
import java.util.HashMap

/** Substitute for missing values.  */
private val EMPTY_STRING_LIST = ArrayList<String>()

/** DeviceInfoPlugin  */
class DeviceInfoPlugin
/** Do not allow direct instantiation.  */
private constructor(private val context: Context) : MethodCallHandler {

    /**
     * Returns the Android hardware device ID that is unique between the device + user and app
     * signing. This key will change if the app is uninstalled or its data is cleared. Device factory
     * reset will also result in a value change.
     *
     * @return The android ID
     */
    private val androidId: String
        @SuppressLint("HardwareIds")
        get() = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    /**
     * A simple emulator-detection based on the flutter tools detection logic and a couple of legacy
     * detection systems
     */
    private val isEmulator: Boolean
        get() = (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator"))

    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method == "getAndroidDeviceInfo") {
            val build = HashMap<String, Any>()
            build["board"] = Build.BOARD
            build["bootloader"] = Build.BOOTLOADER
            build["brand"] = Build.BRAND
            build["device"] = Build.DEVICE
            build["display"] = Build.DISPLAY
            build["fingerprint"] = Build.FINGERPRINT
            build["hardware"] = Build.HARDWARE
            build["host"] = Build.HOST
            build["id"] = Build.ID
            build["manufacturer"] = Build.MANUFACTURER
            build["model"] = Build.MODEL
            build["product"] = Build.PRODUCT
            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                build["supported32BitAbis"] = Arrays.asList(*Build.SUPPORTED_32_BIT_ABIS)
                build["supported64BitAbis"] = Arrays.asList(*Build.SUPPORTED_64_BIT_ABIS)
                build["supportedAbis"] = Arrays.asList(*Build.SUPPORTED_ABIS)
            } else {
                build["supported32BitAbis"] = EMPTY_STRING_LIST
                build["supported64BitAbis"] = EMPTY_STRING_LIST
                build["supportedAbis"] = EMPTY_STRING_LIST
            }
            build["tags"] = Build.TAGS
            build["type"] = Build.TYPE
            build["isPhysicalDevice"] = !isEmulator
            build["androidId"] = androidId

            val version = HashMap<String, Any>()
            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                version["baseOS"] = VERSION.BASE_OS
                version["previewSdkInt"] = VERSION.PREVIEW_SDK_INT
                version["securityPatch"] = VERSION.SECURITY_PATCH
            }
            version["codename"] = VERSION.CODENAME
            version["incremental"] = VERSION.INCREMENTAL
            version["release"] = VERSION.RELEASE
            version["sdkInt"] = VERSION.SDK_INT
            build["version"] = version

            result.success(build)
        } else {
            result.notImplemented()
        }
    }

    companion object {
        /** Plugin registration.  */
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "plugins.flutter.io/device_info")
            channel.setMethodCallHandler(DeviceInfoPlugin(registrar.context()))
        }
    }
}
