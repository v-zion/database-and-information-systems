import 'package:flutter/material.dart';
import 'session.dart';
import 'main.dart';
import 'dart:convert';

class DetailsPage extends StatefulWidget {

  const DetailsPage({
    Key key,
    this.otherId
}) : super(key : key);

  final String otherId;

  @override
  DetailsPageState createState() => new DetailsPageState();
}

class DetailsPageState extends State<DetailsPage> {

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
    Map<String, String> jsonData = new Map<String, String>();
    jsonData['other_id'] = widget.otherId;
    var postResponse = session.post(urlRoot + 'ConversationDetail', jsonData);
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

class ChatMessage extends StatelessWidget {
  final String text;
  final String name;
  ChatMessage({this.text, this.name});
  @override
  Widget build(BuildContext context) {
    return new Container(
      margin: const EdgeInsets.symmetric(vertical: 10.0),
      child: new Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          new Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              new Text(name, style: Theme.of(context).textTheme.subhead),
              new Container(
                margin: const EdgeInsets.only(top: 5.0),
                child: new Text(text),
              ),
            ],
          ),
        ],
      ),
    );
  }
}