package com.umangmathur.breachvulnerableapp

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers.*
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.text.SimpleDateFormat
import java.util.*


class XposedClass : IXposedHookLoadPackage {

    companion object {
        private const val classToHook : String = "okhttp3.RealCall"
        private const val methodExecute : String = "execute"
        private const val methodEnqueue : String = "enqueue"
        private const val methodEnqueueCallback : String = "okhttp3.Callback"
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName.equals(TARGET_APP_PACKAGE_NAME, false)) {
            XposedBridge.log("Launched App: " + lpparam.packageName)
            try {
                //Hook into okhttp3.RealCall.execute() method of OkHTTP SDK
                findAndHookMethod(classToHook, lpparam.classLoader, methodExecute, object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                            val urlString = getUrlStringFromRealCallObject(param)
                            val completeString = "${getTimeStamp()} --- $urlString"
                            Utils.saveStringToResultsFile(completeString)
                            XposedBridge.log(completeString)
                        }
                    })
                //Hook into okhttp3.RealCall.enqueue(callback) method of OkHTTP SDK
                findAndHookMethod(classToHook, lpparam.classLoader, methodEnqueue, methodEnqueueCallback, object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                            val urlString = getUrlStringFromRealCallObject(param)
                            val completeString = "${getTimeStamp()} --- $urlString"
                            Utils.saveStringToResultsFile(completeString)
                            XposedBridge.log(completeString)
                        }
                    })
            } catch (ex: Exception) {
                XposedBridge.log("something went wrong: $ex")
            }
        }
    }

    /**
     * Extract the url string value from OkHTTP RealCall object
     *
     * @param param: parameters passed to a hooked method
     * @return url string value extracted from OkHTTP RealCall object
     */
    private fun getUrlStringFromRealCallObject(param: MethodHookParam): String {
        //Get RealCall.this.originalRequest.url object
        val request = getObjectField(param.thisObject, "originalRequest")
        val httpUrl = getObjectField(request, "url")
        //Invoke HttpUrl.toString() method
        return callMethod(httpUrl, "toString") as String
    }

    private fun getTimeStamp(): String {
        val currTime = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ")
        return sdf.format(currTime.time)
    }

}