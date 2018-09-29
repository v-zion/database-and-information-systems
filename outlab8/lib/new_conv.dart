import 'package:flutter/material.dart';
import 'package:flutter_typeahead/flutter_typeahead.dart';
import 'session.dart';
import 'main.dart';
import 'dart:convert';
import 'chat_details.dart';

class NewConv extends StatefulWidget {
  @override
  NewConvState createState() => new NewConvState();
}

class NewConvState extends State<NewConv>{
  final session = new Session();
  bool _loaded = false;
  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return new Scaffold(
      appBar: new AppBar(
        title: const Text('Create Conversation')
      ),
      body:
        new TypeAheadField(
        suggestionsCallback: (pattern) async {
          var response = await session.get(urlRoot + 'AutoCompleteUser?term=' + pattern);
          print(response);
          List<dynamic> res = <dynamic>[];
          for (Map<String, dynamic> d in json.decode(response)){
            res.add(d);
          }
          return res;
        },
        itemBuilder: (context, suggestion) {
          return ListTile(
            title: Text(suggestion['label']),
          );
        },
        onSuggestionSelected: (suggestion) {
          print('selected');
          session.get(urlRoot + 'CreateConversation?other_id=' + suggestion['value']).then(
              (response) {
                print(response);
                Navigator.of(context).push(new MaterialPageRoute(
                    builder: (context) =>
                    new DetailsPage(otherId: suggestion['value'],
                      name: suggestion['name'],)));
              }
          );

        },
      )
    );
  }
}

