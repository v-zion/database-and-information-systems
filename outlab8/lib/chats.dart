import 'package:flutter/material.dart';
import 'session.dart';
import 'main.dart';
import 'dart:convert';

class ChatPage extends StatefulWidget {
  @override
  ChatPageState createState() => new ChatPageState();
}

class ChatPageState extends State<ChatPage> {

  final session = new Session();
  bool _loaded = false;

  @override
  Widget build(BuildContext context){
    if (!_loaded){
      return new Scaffold(
          appBar: new AppBar(
            title: Text('Loading')
          ),
          body: new Center(
            child: new CircularProgressIndicator(),
          )
      );
    }
    else{
      return new Column(
          children: <Widget>[
            new CircularProgressIndicator(),
            new Text('Loading...')
          ]
      );
    }
  }

  @override
  void initState(){
    super.initState();
    var postResponse = session.post(url_root + 'AllConversations', {});
    postResponse.then((response) {
      Map<String, dynamic> jsonResponse = json.decode(response);
      if (!jsonResponse['status']) {
        setState(() {
          _loaded = true;
        });
      }
      else{

      }
    });
  }
}