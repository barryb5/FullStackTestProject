import 'dart:collection';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:web_socket_channel/web_socket_channel.dart';

import '../services/chat.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({Key? key}) : super(key: key);

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final Map<int, Chat> chats = HashMap();
  final TextEditingController _controller = TextEditingController();
  final _channel = WebSocketChannel.connect(
    Uri.parse('ws://localhost:4000/getChats'),
  );

  @override
  Widget build(BuildContext context) {
    _channel.stream.listen(
          (dynamic message) {
        debugPrint('message $message');
      },
      onDone: () {
        debugPrint('ws channel closed');
      },
      onError: (error) {
        debugPrint('ws error $error');
      },
    );

    return Scaffold(
      appBar: AppBar(
        title: Text("Home Screen"),
        actions: <Widget>[
          Padding(
          padding: EdgeInsets.only(right: 20.0),
            child: GestureDetector(
              onTap: () {
                chats.addAll({chats.length: Chat(0, "name")});
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
                            _channel.sink.close(1000, 'CLOSE_NORMAL');
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
