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
  bool typing = false;
  final _channel = WebSocketChannel.connect(
    Uri.parse('ws://localhost:4000/getChats'),
  );

  void _sendMessage() {
    if (_controller.text.isNotEmpty) {
      _channel.sink.add(_controller.text);
    }
  }

  @override
  Widget build(BuildContext context) {
    // _channel.stream.listen(
    //       (dynamic message) {
    //     debugPrint('message $message');
    //     setState(() {
    //       typing = false;
    //     });
    //   },
    //   onDone: () {
    //     debugPrint('ws channel closed');
    //   },
    //   onError: (error) {
    //     debugPrint('ws error $error');
    //   },
    // );

    return Scaffold(
      appBar: AppBar(
        title: Text("Home Screen"),
          leading: IconButton(
            icon: Icon(typing ? Icons.done : Icons.search),
            onPressed: () {
              _sendMessage();
              setState(() {
                typing = true;
              });
            },
          ),
          actions: <Widget>[

        ]
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: TextField(
              controller: _controller,
              decoration: InputDecoration(
                  enabledBorder: const OutlineInputBorder(
                    // width: 0.0 produces a thin "hairline" border
                    borderSide: const BorderSide(color: Colors.grey, width: 0.0),
                  ),
                  hintText: 'Search with chatId'
              ),
            ),
          ),
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
