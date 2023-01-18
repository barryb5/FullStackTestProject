import 'package:flutter/material.dart';
import 'package:websocket_test/pages/chat_screen.dart';
import 'package:websocket_test/pages/home_screen.dart';

void main() {
  runApp(MaterialApp(
    initialRoute: "/home",
    routes: {
      '/home': (context) => const HomeScreen(),
      '/chat': (context) => const ChatScreen(),
    },
  ));
}
