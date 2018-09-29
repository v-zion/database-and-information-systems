import 'package:flutter/material.dart';
import 'login.dart';
import 'session.dart';
import 'chats.dart';

void main() => runApp(new MyApp());

String urlRoot = 'http://192.168.1.103:8080/lab8servlets/';

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      title: 'WhatASap',
      theme: new ThemeData(
        // This is the theme of your application.
        //
        // Try running your application with "flutter run". You'll see the
        // application has a blue toolbar. Then, without quitting the app, try
        // changing the primarySwatch below to Colors.green and then invoke
        // "hot reload" (press "r" in the console where you ran "flutter run",
        // or press Run > Flutter Hot Reload in IntelliJ). Notice that the
        // counter didn't reset back to zero; the application is not restarted.
        primarySwatch: Colors.blue,
      ),
      home: new HomePage(),
    );
  }
}

class HomePage extends StatefulWidget {
  @override
  HomePageState createState() => new HomePageState();
}

class HomePageState extends State<HomePage> {
  @override
  Widget build(BuildContext context) {
    if (sessionExists()) {
//      return new Scaffold(
//        appBar: new AppBar(
//          title: new Text('Session headers'),
//        ),
//        body: new Text(new Session().headers.toString()),
//      );
    return new ChatPage();
    }
    return new LoginForm();
  }
  bool sessionExists(){
    Session session = new Session();
    if (session.headers.isEmpty){
      return false;
    }
    return true;
  }
}


