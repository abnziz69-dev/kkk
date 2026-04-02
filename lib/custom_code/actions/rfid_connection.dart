// Automatic FlutterFlow imports
import '/backend/sqlite/sqlite_manager.dart';
import '/flutter_flow/flutter_flow_theme.dart';
import '/flutter_flow/flutter_flow_util.dart';
import 'index.dart'; // Imports other custom actions
import 'package:flutter/material.dart';
// Begin custom action code
// DO NOT REMOVE OR MODIFY THE CODE ABOVE!

import 'index.dart'; // Imports other custom actions
import '/services/rfid_service.dart';

Future<bool> rfidConnection() async {
  try {
    final connected = await RFIDService.connectRFID();
    return connected;
  } catch (e) {
    return false;
  }
}
