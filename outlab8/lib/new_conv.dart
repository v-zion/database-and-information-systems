import 'package:flutter/material.dart';
import 'package:flutter_typeahead/flutter_typeahead.dart';
import 'session.dart';
import 'main.dart';
import 'dart:convert';
import 'chat_details.dart';
import 'chats.dart';

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
        textFieldConfiguration: TextFieldConfiguration(
            autofocus: true,
            style: DefaultTextStyle.of(context).style.copyWith(
                fontStyle: FontStyle.italic
            ),
            decoration: InputDecoration(
                border: OutlineInputBorder()
            )
        ),
        suggestionsCallback: (pattern) async {
          session.get(urlRoot + 'AutoCompleteUser?term=' + pattern).then(print);
//          return await session.get(urlRoot + 'AutoCompleteUser?term=' + pattern);
        return [];
        },
        itemBuilder: (context, suggestion) {
          return ListTile(
            leading: Icon(Icons.shopping_cart),
            title: Text(suggestion['name']),
            subtitle: Text('\$${suggestion['price']}'),
          );
        },
        onSuggestionSelected: (suggestion) {
//          Navigator.of(context).push(MaterialPageRoute(
//              builder: (context) => ProductPage(product: suggestion)
//          ));
        },
      )

    );
  }
}

