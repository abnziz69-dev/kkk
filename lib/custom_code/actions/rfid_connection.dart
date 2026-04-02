// Automatic FlutterFlow imports
import '/flutter_flow/flutter_flow_theme.dart';
import '/flutter_flow/flutter_flow_util.dart';
import 'index.dart'; // Imports other custom actions
import 'package:flutter/material.dart';
import '/services/rfid_service.dart';
// Begin custom action code
// DO NOT REMOVE OR MODIFY THE CODE ABOVE!

Future<bool> rfidConnection() async {
  try {
    final connected = await RFIDService.connectRFID();
    return connected;
  } catch (e) {
    return false;
  }
}