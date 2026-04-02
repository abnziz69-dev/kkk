import 'package:flutter/services.dart';

class RFIDService {
  static const MethodChannel _channel = MethodChannel('rfid_channel');

  static Future<bool> connectRFID() async {
    final bool result = await _channel.invokeMethod('connectRFID');
    return result;
  }

  static Future<bool> startScan() async {
    final bool result = await _channel.invokeMethod('startScan');
    return result;
  }

  static Future<bool> stopScan() async {
    final bool result = await _channel.invokeMethod('stopScan');
    return result;
  }

  static Future<bool> disableRFID() async {
    final result = await _channel.invokeMethod('disableRFID');
    return result == true;
  }

  static Future<bool> disconnectRFID() async {
    final result = await _channel.invokeMethod('disconnectRFID');
    return result == true;
  }

  static void setTagReadListener(Function(String tagId) onTagRead) {
    _channel.setMethodCallHandler((call) async {
      if (call.method == 'onTagRead') {
        final String tagId = call.arguments;
        onTagRead(tagId);
      }
    });
  }
}