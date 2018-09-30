import 'package:flutter/material.dart';
import 'session.dart';
import 'main.dart';
import 'dart:convert';
import 'chat_details.dart';
import 'new_conv.dart';

class ChatPage extends StatefulWidget {
  @override
  ChatPageState createState() => new ChatPageState();
}

class ChatPageState extends State<ChatPage> {

  final session = new Session();
  bool _loaded = false;
  final List<ConvDetail> _messages = <ConvDetail>[];
  List<ConvDetail> _displayMessages = <ConvDetail>[];

  @override
  Widget build(BuildContext context){
    if (!_loaded){
      return new Scaffold(
          appBar: new AppBar(
            title: const Text('Loading')
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
                Navigator.of(context).push(new MaterialPageRoute<void>(builder: (BuildContext context) => new NewConv())).then(
                    (value) => Navigator.of(context).pushReplacement(new MaterialPageRoute(builder: (BuildContext context) => new ChatPage()))
                );
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
        ),
        body: new Column(
          children: <Widget>[
            new TextField(
              decoration: new InputDecoration(
                labelText: 'Search'
              ),
              onChanged: (text){
                setState(() {
                  _displayMessages = <ConvDetail>[];
                  for (var i = 0; i < _messages.length; i++){
                    if (_messages[i].name.contains(text) || _messages[i].id.contains(text)){
                      _displayMessages.add(_messages[i]);
                    }
                  }
                  print(text);
                });
              },
            ),
            new Flexible(
              child: new ListView.builder(
                padding: new EdgeInsets.all(8.0),
                //                  reverse: true,
                itemBuilder: (_, int index) => _displayMessages[index],
                itemCount: _displayMessages.length,
            //                shrinkWrap: true,
              )
            )
          ],
        )
      );
    }
  }

  @override
  void initState(){
    super.initState();
    var postResponse = session.post(urlRoot + 'AllConversations', {});
    postResponse.then((response) {
      Map<String, dynamic> jsonResponse = json.decode(response);
      print(jsonResponse);

        setState(() {
          _loaded = true;
          if (jsonResponse['status']) {
            for (Map<String, dynamic> d in jsonResponse['data']) {
              _messages.add(new ConvDetail(
                  name: d['name'],
                  lastTime: d['last_timestamp'] == null ? "" : d['last_timestamp'],
                  id: d['uid']));
            }
            print(_messages.length);
            _displayMessages = _messages;
//        _messages.sort((a, b) => a == "" ? -1 : b == "" ? 1 : DateTime.parse(a.lastTime).isAfter(DateTime.parse(b.lastTime)) ? 1 : -1);
          }
          else{

          }
        });

    });
  }
}

class ConvDetail extends StatelessWidget {
  final String lastTime;
  final String name;
  final String id;
  ConvDetail({this.lastTime, this.name, this.id});
  @override
  Widget build(BuildContext context) {
    return new InkWell(
        child: new Container(
          margin: const EdgeInsets.symmetric(vertical: 10.0),
          child: new Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              new Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  new Text(name, style: Theme.of(context).textTheme.subhead),
                  new Text(id, style: Theme.of(context).textTheme.subhead),
                  new Container(
                    margin: const EdgeInsets.only(top: 5.0),
                    child: new Text(lastTime),
                  ),
                ],
              ),
            ],

          ),
        ),
      onTap: (){
          Navigator.of(context).push(new MaterialPageRoute<void>(
              builder: (BuildContext context) => new DetailsPage(otherId: id, name: name,)
          ))
          .then((value) {
            print('complted push');
              Navigator.of(context).pushReplacement(new MaterialPageRoute<void>(
                  builder: (BuildContext context) => new ChatPage()));
            print('hereafter');
          });
      },
    );
  }
}