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
  final Map<int, dynamic> messages = HashMap();
  final TextEditingController _controller = TextEditingController();
  final _channel = WebSocketChannel.connect(
    Uri.parse('ws://localhost:4000/chat'),
  );

  void _sendMessage() {
    if (_controller.text.isNotEmpty) {
      _channel.sink.add(_controller.text);
    }
  }

  @override
  void dispose() {
    _channel.sink.close();
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Test Chat"),
      ),
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            StreamBuilder(
              stream: _channel.stream,
              builder: (context, snapshot) {
                if (snapshot.hasData) {
                  dynamic data = jsonDecode(snapshot.data);
                  messages.addAll({messages.length: data});
                }

                if (messages.length > 0) {
                  return Expanded(
                    child: ListView.builder(
                      itemCount: messages.length,
                      padding: EdgeInsets.all(8),
                      scrollDirection: Axis.vertical,
                      itemBuilder: (context, index) => ListTile(
                          leading: const Icon(Icons.person),
                          title: Text('${messages[index]["username"]}: ${messages[index]["message"]}')
                      ),
                    ),
                  );
                } else {
                  return Text("No messages");
                }
              }
            ),
            SizedBox(height: 24,),
            Container(
              child: Row(
                mainAxisAlignment: MainAxisAlignment.start,
                children: [
                  Expanded(child: Form(
                    child: TextFormField(
                      controller: _controller,
                      decoration: const InputDecoration(labelText: 'Send a message'),
                    ),
                  )),
                  FloatingActionButton(
                    onPressed: _sendMessage,
                    tooltip: 'Send Message',
                    child: const Icon(Icons.send),
                  ),
                ],
              ),
            ),
          ],
        )
      ),
    );
  }
}
