// Automatic FlutterFlow imports
import '/backend/sqlite/sqlite_manager.dart';
import '/flutter_flow/flutter_flow_theme.dart';
import '/flutter_flow/flutter_flow_util.dart';
import 'index.dart'; // Imports other custom actions
import 'package:flutter/material.dart';
// Begin custom action code
// DO NOT REMOVE OR MODIFY THE CODE ABOVE!

Future<bool> autoConnectRFID() async {
  while (true) {
    try {
      await Future.delayed(const Duration(seconds: 1));

      bool connected = true;

      if (connected) {
        return true;
      }
    } catch (e) {}

    await Future.delayed(const Duration(seconds: 2));
  }
}
// Set your action name, define your arguments and return parameter,
// and then add the boilerplate code using the green button on the right!
