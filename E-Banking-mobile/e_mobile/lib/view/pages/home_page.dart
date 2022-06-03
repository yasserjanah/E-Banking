// ignore_for_file: unused_element, unnecessary_this

import 'dart:convert';

import 'package:e_mobile/GlobalVariables.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  // ignore: library_private_types_in_public_api
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  String userId = "";
  String username = "";
  String userType = "";
  int tabId = 0;
  // ignore: unnecessary_question_mark, unused_field
  List<dynamic?> _accounts = [];
  // ignore: unnecessary_question_mark
  List<dynamic?> operations = [];

  // get user accounts
  Future<List<dynamic>?> getUserAccounts(String userId) async {
    try {
      _getFromSharedPreferences("accessToken").then((accessToken) async {
        print("userId: $userId");
        final response = await http.get(
            Uri.parse("${GlobalVariables.APP_CUSTOMERS_URL}/$userId/accounts"),
            headers: {
              "Authorization": "Bearer $accessToken",
            });
        print(response.body);
        if (response.statusCode == 200) {
          List<dynamic> accounts = json.decode(response.body);
          setState(() {
            this._accounts = accounts;
          });
          getUserOperations(this._accounts[0]["id"])
              .then((operations) async {});
        } else {
          print("Error: ${response.statusCode}");
        }
      });
    } catch (e) {
      print("retrieveUserAccounts: $e");
      print(e);
      return null;
    }
    return null;
  }

  // get user operations
  Future<List<dynamic>?> getUserOperations(String userId) async {
    try {
      _getFromSharedPreferences("accessToken").then((accessToken) async {
        print("accountId: $userId");
        final response = await http.get(
            Uri.parse("${GlobalVariables.APP_ACCOUNTS_URL}/$userId/operations"),
            headers: {
              "Authorization": "Bearer $accessToken",
            });
        // ignore: avoid_print
        print("Response: ${response}");
        print(response.body);
        if (response.statusCode == 200) {
          List<dynamic> operations = json.decode(response.body);
          setState(() {
            this.operations = operations;
          });
        } else {
          print("Error: ${response.statusCode}");
        }
      });
    } catch (e) {
      print("retrieveUserOperations: $e");
      print(e);
      return null;
    }
    return null;
  }

  // construtor
  _HomePageState() {
    _getFromSharedPreferences('userId').then((value) {
      setState(() {
        userId = value!;
        getUserAccounts(userId).then((value) {
          setState(() {
            _accounts = value!;
          });
        });
      });
    });
    _getFromSharedPreferences('username').then((value) {
      setState(() {
        username = value!;
      });
    });
    _getFromSharedPreferences('userType').then((value) {
      setState(() {
        userType = value!;
      });
    });
  }

  @override
  void initState() {
    super.initState();
  }

  Future<String?> _getFromSharedPreferences(String? keyName) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    return prefs.getString(keyName!);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      bottomNavigationBar: BottomNavigationBar(
        backgroundColor: Colors.red,
        selectedIconTheme: const IconThemeData(color: Colors.white, size: 22),
        items: const <BottomNavigationBarItem>[
          BottomNavigationBarItem(
            icon: Icon(Icons.home),
            label: "Home",
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.account_balance),
            label: "Accounts",
          ),
          BottomNavigationBarItem(
              icon: Icon(Icons.pending_actions), label: "Operations"),
        ],
        currentIndex: tabId,
        onTap: (index) {
          // print(index);
          // set the tabId
          setState(() {
            tabId = index;
          });
        },
      ),
      body: getPage(),
    );
  }

  Widget getPage() {
    switch (tabId) {
      case 0:
        return _buildHomePage(
            userId: this.userId,
            username: this.username,
            userType: this.userType);
      case 1:
        return _buildAccountPage(accounts: this._accounts);
      case 2:
        return _buildOperationsPage(operations: this.operations);
      default:
        return _buildHomePage(
            userId: this.userId,
            username: this.username,
            userType: this.userType);
    }
  }
}

// ignore: camel_case_types, must_be_immutable
class _buildHomePage extends StatelessWidget {
  // ignore: prefer_typing_uninitialized_variables
  var userId;

  // ignore: prefer_typing_uninitialized_variables
  var username;

  // ignore: prefer_typing_uninitialized_variables
  var userType;

  // construtor to accept arguments
  _buildHomePage({this.userId, this.username, this.userType});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          Container(
            margin: const EdgeInsets.only(top: 7),
            height: MediaQuery.of(context).size.height * 0.2,
            width: MediaQuery.of(context).size.width,
            decoration: const BoxDecoration(
              color: Colors.red,
              borderRadius: BorderRadius.only(
                bottomLeft: Radius.circular(20),
                bottomRight: Radius.circular(20),
              ),
            ),
            child: const Center(
                child: Text("E-Mobile",
                    style: TextStyle(
                        fontSize: 40,
                        fontWeight: FontWeight.bold,
                        color: Colors.white))),
          ),
          Container(
            width: MediaQuery.of(context).size.width,
            height: MediaQuery.of(context).size.height * 0.70,
            decoration: const BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.only(
                topLeft: Radius.circular(20),
                topRight: Radius.circular(20),
              ),
            ),
            child: Column(
              children: <Widget>[
                const SizedBox(height: 10),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: <Widget>[
                    Container(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: <Widget>[
                          const Text(
                            "Profile",
                            style: TextStyle(
                                fontSize: 30, fontWeight: FontWeight.bold),
                          ),
                          Padding(
                            padding: const EdgeInsets.all(15.0),
                            child: Container(
                              margin: const EdgeInsets.only(top: 10),
                              child: Text("Hello $username,",
                                  style: const TextStyle(
                                      fontWeight: FontWeight.bold,
                                      color: Colors.black,
                                      fontSize: 16)),
                            ),
                          ),
                          Container(
                            margin: const EdgeInsets.only(top: 10),
                            child: Text("Type: $userType",
                                style: const TextStyle(
                                    fontWeight: FontWeight.bold,
                                    color: Colors.black,
                                    fontSize: 16)),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

// ignore: camel_case_types, must_be_immutable
class _buildAccountPage extends StatelessWidget {
  // ignore: prefer_typing_uninitialized_variables
  var accounts;

  _buildAccountPage({this.accounts});

  @override
  Widget build(BuildContext context) {
    // list of accounts
    return Scaffold(
      body: Column(
        children: <Widget>[
          Container(
            margin: const EdgeInsets.only(top: 7),
            height: MediaQuery.of(context).size.height * 0.2,
            width: MediaQuery.of(context).size.width,
            decoration: const BoxDecoration(
              color: Colors.red,
              borderRadius: BorderRadius.only(
                bottomLeft: Radius.circular(20),
                bottomRight: Radius.circular(20),
              ),
            ),
            child: const Center(
                child: Text("E-Mobile",
                    style: TextStyle(
                        fontSize: 40,
                        fontWeight: FontWeight.bold,
                        color: Colors.white))),
          ),
          SizedBox(height: 10),
          Container(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                const Text(
                  "Accounts",
                  style: TextStyle(fontSize: 30, fontWeight: FontWeight.bold),
                ),
                Container(
                  margin: const EdgeInsets.only(top: 10),
                  child: Text("${accounts.length} accounts",
                      style: const TextStyle(
                          fontWeight: FontWeight.bold,
                          color: Colors.black,
                          fontSize: 16)),
                ),
              ],
            ),
          ),
          Expanded(
            child: ListView.builder(
              itemCount: accounts.length,
              itemBuilder: (context, index) {
                return Card(
                  child: ListTile(
                    title: // bank Icon
                        Row(
                      children: <Widget>[
                        const Icon(
                          Icons.account_balance,
                          color: Colors.red,
                        ),
                        Text("  ${accounts[index]["balance"]}" "\$")
                      ],
                    ),
                    subtitle: Text(
                        accounts[index]["accountType"] == 'CurrentAccount'
                            ? 'CA'
                            : 'SA',
                        style: const TextStyle(
                            fontWeight: FontWeight.bold,
                            color: Colors.red,
                            fontSize: 16)),
                    trailing: Text(
                        accounts[index]["accountType"] == 'CurrentAccount'
                            ? accounts[index]['overDraft'].toString()
                            : accounts[index]['interestRate'].toString(),
                        style: const TextStyle(
                            fontWeight: FontWeight.bold,
                            color: Colors.red,
                            fontSize: 16)),
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}

// ignore: camel_case_types
class _buildOperationsPage extends StatelessWidget {
  var operations;

  _buildOperationsPage({this.operations});

  @override
  Widget build(BuildContext context) {
    return // table of bank accounts operations
        Scaffold(
      body: Column(
        children: <Widget>[
          Container(
            margin: const EdgeInsets.only(top: 7),
            height: MediaQuery.of(context).size.height * 0.2,
            width: MediaQuery.of(context).size.width,
            decoration: const BoxDecoration(
              color: Colors.red,
              borderRadius: BorderRadius.only(
                bottomLeft: Radius.circular(20),
                bottomRight: Radius.circular(20),
              ),
            ),
            child: const Center(
                child: Text("E-Mobile",
                    style: TextStyle(
                        fontSize: 40,
                        fontWeight: FontWeight.bold,
                        color: Colors.white))),
          ),
          SizedBox(height: 10),
          Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              const Text(
                "Operations",
                style: TextStyle(fontSize: 30, fontWeight: FontWeight.bold),
              ),
              Container(
                margin: const EdgeInsets.only(top: 10),
                child: Text("${operations.length} operations",
                    style: const TextStyle(
                        fontWeight: FontWeight.bold,
                        color: Colors.black,
                        fontSize: 16)),
              ),
            ],
          ),
          Expanded(
            child: ListView.builder(
              itemCount: operations.length,
              itemBuilder: (context, index) {
                return Card(
                  child: ListTile(
                    title: // bank Icon
                        Row(
                      children: <Widget>[
                        const Icon(
                          Icons.comment_bank,
                          color: Colors.red,
                        ),
                        Text("  ${operations[index]["type"]}")
                      ],
                    ),
                    subtitle: Text(operations[index]["amount"].toString(),
                        style: const TextStyle(
                            fontWeight: FontWeight.bold,
                            color: Colors.red,
                            fontSize: 16)),
                    trailing: Text(operations[index]["description"],
                        style: const TextStyle(
                            fontWeight: FontWeight.bold,
                            color: Colors.red,
                            fontSize: 16)),
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
