package com.umangmathur.breachvulnerableapp

import de.robv.android.xposed.XposedBridge
import java.io.*

class Utils {

    companion object {
        /**
         * Appends a url to the log file
         *
         * @param packageName:   package name string of the application
         * @param urlStr: string to be written to the results file
         */
        fun saveStringToResultsFile(urlStr: String) {
            try {
                val logFile = File("$DATA_DATA_DIRECTORY/$TARGET_APP_PACKAGE_NAME/$LOG_FILENAME")
                //Create log file if not present
                if (!logFile.exists()) {
                    val created: Boolean = logFile.createNewFile()
                    if (!created) {
                        XposedBridge.log("Failed to create results file. Path: " + logFile.absolutePath)
                        return
                    }
                }
                val fw = FileWriter(logFile, true)
                val bw = BufferedWriter(fw)
                bw.write(urlStr + "\n")
                bw.close()
            } catch (e: IOException) {
                XposedBridge.log(e)
            }
        }
    }

}