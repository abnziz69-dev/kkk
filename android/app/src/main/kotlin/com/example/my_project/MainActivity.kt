package com.example.my_project

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import kotlin.concurrent.thread

class MainActivity : FlutterActivity() {
    private val CHANNEL = "rfid_channel"
    private val PERMISSION_REQUEST_CODE = 1001
    private lateinit var rfidHandler: RFIDHandler

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        rfidHandler = RFIDHandler(this)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
            .setMethodCallHandler { call, result ->
                when (call.method) {
                    "connectRFID" -> {
                        if (!hasBluetoothPermissions()) {
                            requestBluetoothPermissions()
                            result.success(false)
                            return@setMethodCallHandler
                        }

                        thread {
                            val success = rfidHandler.connectReader { tagId ->
                                runOnUiThread {
                                    MethodChannel(
                                        flutterEngine.dartExecutor.binaryMessenger,
                                        CHANNEL
                                    ).invokeMethod("onTagRead", tagId)
                                }
                            }
                            runOnUiThread {
                                result.success(success)
                            }
                        }
                    }

                    "startScan" -> {
                        val success = rfidHandler.startInventory()
                        result.success(success)
                    }

                    "stopScan" -> {
                        val success = rfidHandler.stopInventory()
                        result.success(success)
                    }

                    "disableRFID" -> {
                        val success = rfidHandler.disableReader()
                        result.success(success)
                    }
                    "disconnectRFID" -> {
                        val success = rfidHandler.disconnectReader()
                        result.success(success)
                    }

                    "saveCsvToDownloads" -> {
                        try {
                            val fileName = call.argument<String>("fileName")
                            val content = call.argument<String>("content")

                            if (fileName.isNullOrBlank() || content == null) {
                                result.error("INVALID_ARGS", "fileName or content missing", null)
                                return@setMethodCallHandler
                            }

                            val savedPath = saveCsvToDownloads(fileName, content)
                            if (savedPath != null) {
                                result.success(savedPath)
                            } else {
                                result.error("SAVE_FAILED", "Failed to save CSV file", null)
                            }
                        } catch (e: Exception) {
                            result.error("SAVE_ERROR", e.message, null)
                        }
                    }

                    else -> result.notImplemented()
                }
            }
    }

    private fun hasBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                ),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun saveCsvToDownloads(fileName: String, content: String): String? {
        val resolver = applicationContext.contentResolver
        val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI

        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val itemUri = resolver.insert(collection, values) ?: return null

        resolver.openOutputStream(itemUri)?.use { outputStream ->
            outputStream.write(content.toByteArray())
            outputStream.flush()
        } ?: return null

        values.clear()
        values.put(MediaStore.Downloads.IS_PENDING, 0)
        resolver.update(itemUri, values, null, null)

        return "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath}/$fileName"
    }
}