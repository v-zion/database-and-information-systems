import 'package:flutter/material.dart';
import 'session.dart';
import 'main.dart';
import 'dart:convert';

class DetailsPage extends StatefulWidget {

  const DetailsPage({
    Key key,
    this.otherId,
    this.name
}) : super(key : key);

  final String otherId;
  final String name;

  @override
  DetailsPageState createState() => new DetailsPageState();
}

class DetailsPageState extends State<DetailsPage> {

  final session = new Session();
  bool _loaded = false;
  final List<ChatMessage> _messages = <ChatMessage>[];
  final TextEditingController _textController = new TextEditingController();


  @override
  Widget build(BuildContext context){
    print('herebuild');
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
          title: new Text(widget.name)
        ),
        body: new Column(
          children: <Widget>[
            new Expanded(
              child: new ListView.builder(
                padding: new EdgeInsets.all(8.0),
                reverse: true,
                itemBuilder: (_, int index) => _messages[index],
                itemCount: _messages.length,
              ),
            ),
            new Divider(height: 1.0),                                 //new
            new Container(                                            //new
              decoration: new BoxDecoration(
                  color: Theme.of(context).cardColor),                  //new
              child: _buildTextComposer(),
            )
          ],
        ),
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
      print('here');
      Map<String, dynamic> jsonResponse = json.decode(response);
      print(jsonResponse);
      if (jsonResponse['status']) {
        for (Map<String, dynamic> d in jsonResponse['data']){
          _messages.insert(0, new ChatMessage(text: d['text'], name: d['uid'] == widget.otherId ? widget.name : 'You',));
        }
        print(_messages.toString());
        setState(() {
          _loaded = true;
        });
      }
      else{

      }
    });
  }

  Widget _buildTextComposer(){
    return new IconTheme(                                            //new
      data: new IconThemeData(color: Theme.of(context).accentColor), //new
      child: new Container(                                     //modified
        margin: const EdgeInsets.symmetric(horizontal: 8.0),
        child: new Row(
          children: <Widget>[
            new Flexible(
              child: new TextField(
                controller: _textController,
                onSubmitted: _handleSubmitted,
                decoration: new InputDecoration.collapsed(
                    hintText: "Send a message"),
              ),
            ),
            new Container(
              margin: new EdgeInsets.symmetric(horizontal: 4.0),
              child: new IconButton(
                  icon: new Icon(Icons.send, color: Colors.green,),
                  onPressed: () => _handleSubmitted(_textController.text)),
            ),
          ],
        ),
      ),                                                             //new
    );
  }

  void _handleSubmitted(String text){
    Session session = new Session();
    session.get(urlRoot + 'NewMessage?other_id=' + widget.otherId + '&msg=' + text).then((response) {
        Map<String, dynamic> jsonResponse = json.decode(response);
        if (jsonResponse['status']){
          setState(() {
            _messages.insert(0, new ChatMessage(text: text, name: 'You',));
          });
        }
    });
    _textController.clear();
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