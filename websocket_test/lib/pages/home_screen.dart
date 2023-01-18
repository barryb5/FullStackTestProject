import 'dart:collection';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:web_socket_channel/web_socket_channel.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({Key? key}) : super(key: key);

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final Map<int, dynamic> chats = HashMap();
  final TextEditingController _controller = TextEditingController();
  final _channel = WebSocketChannel.connect(
    Uri.parse('ws://localhost:4000/getChats'),
  );

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Home Screen"),
        actions: <Widget>[
          Padding(
          padding: EdgeInsets.only(right: 20.0),
            child: GestureDetector(
              onTap: () {
              },
              child: Icon(
                Icons.plus_one,
                size: 26.0,
              ),
            )
          ),
        ]
      ),
      body: Column(
        children: [
          Text("Chats"),
          StreamBuilder(
              stream: _channel.stream,
              builder: (context, snapshot) {
                if (snapshot.hasData) {
                  dynamic data = jsonDecode(snapshot.data);
                  chats.addAll({chats.length: data});
                }

                if (chats.length > 0) {
                  return Expanded(
                    child: ListView.builder(
                      itemCount: chats.length,
                      padding: EdgeInsets.all(8),
                      scrollDirection: Axis.vertical,
                      itemBuilder: (context, index) => TextButton(
                          onPressed: () {
                            Navigator.pushNamed(context, 'chat');
                          },
                        child: Text("Chat name here"),
                      ),
                    ),
                  );
                } else {
                  return Text("No chats joined");
                }
              }
          ),
        ],
      ),
    );
  }
}
