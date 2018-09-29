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
      return new Scaffold(
        appBar: new AppBar(
          title: const Text('Chats'),
          actions: <Widget>[
            IconButton(
              icon: Icon(Icons.home),
              onPressed: (){
                Navigator.of(context).pushReplacement(new MaterialPageRoute<void>(builder: (BuildContext context) => new ChatPage()));
              }
            ),
            IconButton(
              icon: Icon(Icons.create),
              onPressed: (){
                Navigator.of(context).pushReplacement(new MaterialPageRoute<void>(builder: (BuildContext context) => new ChatPage()));
              }
            ),
            IconButton(
              icon: Icon(Icons.exit_to_app),
              onPressed: (){
                session.headers = {};
                Navigator.of(context).pushReplacement(new MaterialPageRoute<void>(builder: (BuildContext context) => new HomePage()));
              }
            )
          ],
          child: new Container(
            child: new Column(
              children: <Widget>[
                new TextField(
                  decoration: new InputDecoration(
                      labelText: 'Search'
                  ),
                  onChanged: (text){
                    setState(() {

                    });
                  },
                )
              ],
            )
          )
        ),
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