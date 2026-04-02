package com.example.my_project

import android.content.Context
import android.util.Log
import com.zebra.rfid.api3.*

class RFIDHandler(private val context: Context) {

    private var readers: Readers? = null
    private var readerDevice: ReaderDevice? = null
    private var reader: RFIDReader? = null
    private var listenerRegistered = false
    private var currentCallback: ((String) -> Unit)? = null
    private var isReaderEnabled = false
    private var inventoryRunning = false

    private val rfidListener = object : RfidEventsListener {

        override fun eventReadNotify(e: RfidReadEvents?) {
            val currentReader = reader ?: return
            if (!isReaderEnabled) return

            try {
                val tags = currentReader.Actions.getReadTags(100)
                if (tags != null) {
                    for (tag in tags) {
                        val epc = tag.tagID
                        if (!epc.isNullOrEmpty()) {
                            Log.d("RFID", "Tag read: $epc")
                            currentCallback?.invoke(epc)
                        }
                    }
                }
            } catch (ex: Exception) {
                Log.e("RFID", "Read event error: ${ex.message}", ex)
            }
        }

        override fun eventStatusNotify(e: RfidStatusEvents?) {
            try {
                val statusData = e?.StatusEventData
                val status = statusData?.statusEventType
                Log.d("RFID", "Status event received: $status")

                when (status) {
                    STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT -> {
                        val triggerData = statusData.HandheldTriggerEventData
                        val pressed = getTriggerPressedState(triggerData)

                        Log.d("RFID", "Handheld trigger pressed state = $pressed")

                        if (pressed == true) {
                            try {
                                if (inventoryRunning) {
                                    stopInventoryInternal()
                                    Thread.sleep(100)
                                }
                                startInventoryInternal()
                            } catch (e: Exception) {
                                Log.e("RFID", "Safe start error: ${e.message}", e)
                            }
                        } else if (pressed == false) {
                            stopInventoryInternal()
                        } else {
                            logTriggerDebug(triggerData)
                        }
                    }

                    STATUS_EVENT_TYPE.INVENTORY_START_EVENT -> {
                        inventoryRunning = true
                        Log.d("RFID", "Inventory started")
                    }

                    STATUS_EVENT_TYPE.INVENTORY_STOP_EVENT -> {
                        inventoryRunning = false
                        Log.d("RFID", "Inventory stopped")
                        purgeTagsInternal()
                    }

                    else -> {}
                }
            } catch (ex: Exception) {
                Log.e("RFID", "Status event error: ${ex.message}", ex)
            }
        }
    }

    fun connectReader(callback: (String) -> Unit): Boolean {
        return try {
            Log.d("RFID", "Creating Readers with ALL transport")

            try {
                if (reader?.isConnected == true) {
                    try {
                        reader?.Actions?.Inventory?.stop()
                    } catch (_: Exception) {
                    }
                    Thread.sleep(200)
                }
            } catch (_: Exception) {
            }

            readers = Readers(context, ENUM_TRANSPORT.ALL)
            Thread.sleep(500)

            val availableReaders = readers?.GetAvailableRFIDReaderList()
            Log.d("RFID", "ALL transport readers found: ${availableReaders?.size ?: 0}")

            if (availableReaders == null || availableReaders.isEmpty()) {
                Log.d("RFID", "No readers found")
                return false
            }

            readerDevice = availableReaders[0]
            reader = readerDevice?.rfidReader

            val currentReader = reader ?: return false

            if (!currentReader.isConnected) {
                Log.d("RFID", "Connecting to reader...")
                currentReader.connect()
            }

            if (currentReader.isConnected) {
                Log.d("RFID", "Reader connected successfully")
                currentCallback = callback
                isReaderEnabled = true
                configureReader()
                registerListenerIfNeeded()
                Log.d("RFID", "Reader auto-enabled after connect")
                true
            } else {
                Log.d("RFID", "Reader connect call returned but isConnected = false")
                false
            }
        } catch (e: Exception) {
            Log.e("RFID", "Connection error: ${e.message}", e)
            false
        }
    }

    private fun configureReader() {
        val currentReader = reader ?: return
        val config = currentReader.Config

        try {
            config.setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE, true)
        } catch (e: Exception) {
            Log.e("RFID", "Trigger mode config error: ${e.message}", e)
        }

        try {
            val triggerInfo = TriggerInfo()

            triggerInfo.StartTrigger.triggerType =
                START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE

            triggerInfo.StopTrigger.triggerType =
                STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE

            triggerInfo.setTagReportTrigger(1)

            currentReader.Config.startTrigger = triggerInfo.StartTrigger
            currentReader.Config.stopTrigger = triggerInfo.StopTrigger

            Log.d("RFID", "Immediate trigger mode configured")
        } catch (e: Exception) {
            Log.e("RFID", "Trigger config error: ${e.message}", e)
        }

        try {
            val singulationControl = currentReader.Config.Antennas.getSingulationControl(1)
            singulationControl.setSession(SESSION.SESSION_S0)
            currentReader.Config.Antennas.setSingulationControl(1, singulationControl)
            Log.d("RFID", "Singulation set to SESSION_S0")
        } catch (e: Exception) {
            Log.e("RFID", "Singulation config error: ${e.message}", e)
        }

        try {
            currentReader.Config.setUniqueTagReport(true)
            Log.d("RFID", "Unique Tag Report enabled")
        } catch (e: Exception) {
            Log.e("RFID", "Unique Tag Report config error: ${e.message}", e)
        }

        Log.d("RFID", "Reader configured successfully")
    }

    private fun registerListenerIfNeeded() {
        val currentReader = reader ?: return

        if (!listenerRegistered) {
            currentReader.Events.addEventsListener(rfidListener)
            listenerRegistered = true
            Log.d("RFID", "RFID listener registered")
        }

        try {
            currentReader.Events.setInventoryStartEvent(true)
            currentReader.Events.setInventoryStopEvent(true)
            currentReader.Events.setTagReadEvent(true)
            currentReader.Events.setHandheldEvent(true)
            currentReader.Events.setAttachTagDataWithReadEvent(false)
        } catch (e: Exception) {
            Log.e("RFID", "Event registration error: ${e.message}", e)
        }
    }

    fun enableReader(callback: (String) -> Unit): Boolean {
        return try {
            currentCallback = callback
            isReaderEnabled = true
            registerListenerIfNeeded()
            Log.d("RFID", "Reader enabled")
            true
        } catch (e: Exception) {
            Log.e("RFID", "Enable reader error: ${e.message}", e)
            false
        }
    }

    fun disableReader(): Boolean {
        return try {
            isReaderEnabled = false
            currentCallback = null
            stopInventoryInternal()
            purgeTagsInternal()
            Log.d("RFID", "Reader disabled")
            true
        } catch (e: Exception) {
            Log.e("RFID", "Disable reader error: ${e.message}", e)
            false
        }
    }

    fun startInventory(): Boolean {
        return try {
            isReaderEnabled = true
            registerListenerIfNeeded()
            startInventoryInternal()
            Log.d("RFID", "Inventory started from app button")
            true
        } catch (e: Exception) {
            Log.e("RFID", "startInventory error: ${e.message}", e)
            false
        }
    }

    fun stopInventory(): Boolean {
        return try {
            stopInventoryInternal()
            purgeTagsInternal()
            Log.d("RFID", "Inventory stopped from app button")
            true
        } catch (e: Exception) {
            Log.e("RFID", "stopInventory error: ${e.message}", e)
            false
        }
    }

    private fun startInventoryInternal() {
        val currentReader = reader ?: return
        if (!isReaderEnabled) return
        if (inventoryRunning) return

        try {
            currentReader.Actions.Inventory.perform()
            Log.d("RFID", "Inventory perform() called")
        } catch (e: OperationFailureException) {
            Log.e("RFID", "Inventory start operation failure: ${e.message}", e)
        } catch (e: Exception) {
            Log.e("RFID", "Inventory start error: ${e.message}", e)
        }
    }

    private fun stopInventoryInternal() {
        val currentReader = reader ?: return
        if (!inventoryRunning) return

        try {
            currentReader.Actions.Inventory.stop()
            Log.d("RFID", "Inventory stop() called")
        } catch (e: OperationFailureException) {
            Log.e("RFID", "Inventory stop operation failure: ${e.message}", e)
        } catch (e: Exception) {
            Log.e("RFID", "Inventory stop error: ${e.message}", e)
        }
    }

    private fun purgeTagsInternal() {
        try {
            reader?.Actions?.purgeTags()
            Log.d("RFID", "Reader tag cache purged")
        } catch (e: Exception) {
            Log.e("RFID", "Purge tags error: ${e.message}", e)
        }
    }

    private fun getTriggerPressedState(triggerData: Any?): Boolean? {
        if (triggerData == null) return null

        val candidateMethodNames = listOf(
            "getTriggerEvent",
            "getHandheldEvent",
            "getHandheldTriggerEvent",
            "getEvent"
        )

        for (methodName in candidateMethodNames) {
            try {
                val method = triggerData.javaClass.methods.firstOrNull {
                    it.name == methodName && it.parameterCount == 0
                } ?: continue

                val value = method.invoke(triggerData)?.toString()?.uppercase() ?: continue

                if (value.contains("PRESSED")) return true
                if (value.contains("RELEASED")) return false
            } catch (_: Exception) {
            }
        }

        return null
    }

    private fun logTriggerDebug(triggerData: Any?) {
        if (triggerData == null) {
            Log.d("RFID", "Trigger debug: triggerData is null")
            return
        }

        try {
            val methods = triggerData.javaClass.methods
                .filter { it.parameterCount == 0 && it.name.startsWith("get") }
                .sortedBy { it.name }

            for (method in methods) {
                try {
                    val value = method.invoke(triggerData)
                    Log.d("RFID", "Trigger debug ${method.name} = $value")
                } catch (_: Exception) {
                }
            }
        } catch (e: Exception) {
            Log.e("RFID", "Trigger debug error: ${e.message}", e)
        }
    }

    fun disconnectReader(): Boolean {
        return try {
            stopInventoryInternal()
            Thread.sleep(200)
            purgeTagsInternal()

            if (listenerRegistered) {
                try {
                    reader?.Events?.removeEventsListener(rfidListener)
                } catch (_: Exception) {
                }
                listenerRegistered = false
            }

            isReaderEnabled = false
            inventoryRunning = false
            currentCallback = null

            try {
                if (reader?.isConnected == true) {
                    reader?.disconnect()
                }
            } catch (_: Exception) {
            }

            try {
                readers?.Dispose()
            } catch (_: Exception) {
            }

            reader = null
            readerDevice = null
            readers = null

            Log.d("RFID", "Reader disconnected and cleaned up")
            true
        } catch (e: Exception) {
            Log.e("RFID", "disconnectReader error: ${e.message}", e)
            false
        }
    }
}