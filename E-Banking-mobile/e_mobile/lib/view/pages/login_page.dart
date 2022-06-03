// ignore_for_file: deprecated_member_use

import 'dart:convert';

import 'package:e_mobile/GlobalVariables.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({Key? key}) : super(key: key);

  @override
  // ignore: library_private_types_in_public_api
  _LoginPageState createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final _formKey = GlobalKey<FormState>();
  final _scaffoldKey = GlobalKey<ScaffoldState>();
  final _usernameController = TextEditingController();
  final _passwordController = TextEditingController();
  final _usernameFocus = FocusNode();
  final _passwordFocus = FocusNode();
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _usernameController.addListener(_onUsernameChanged);
    _passwordController.addListener(_onPasswordChanged);
    // check if user is already logged in
    _checkIfLoggedIn();
  }

  Future<String?> _getFromSharedPreferences(String? keyName) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    return prefs.getString(keyName!);
  }

  Future<void> _checkIfLoggedIn() async {
    _verifyAccessToken(await _getFromSharedPreferences('accessToken'));
  }

  @override
  void dispose() {
    _usernameController.dispose();
    _passwordController.dispose();
    _usernameFocus.dispose();
    _passwordFocus.dispose();
    super.dispose();
  }

  void _onUsernameChanged() {
    if (_usernameController.text.isEmpty) {
      _usernameFocus.unfocus();
    }
  }

  void _onPasswordChanged() {
    if (_passwordController.text.isEmpty) {
      _passwordFocus.unfocus();
    }
  }

  void _onLoginPressed() {
    if (_formKey.currentState!.validate()) {
      _formKey.currentState!.save();
      setState(() {
        _isLoading = true;
      });
      _login();
    }
  }

  Future<void> _login() async {
    try {
      final response = await http.post(
        Uri.parse(GlobalVariables.APP_LOGIN_URL),
        body: json.encode({
          'username': _usernameController.text,
          'password': _passwordController.text,
        }),
      );
      if (response.statusCode == 200) {
        Map<String, dynamic> responseData = json.decode(response.body);
        print("saving login access tokens");
        _saveToSharedPreferences("accessToken", responseData['accessToken']);
        _saveToSharedPreferences("refreshToken", responseData['refreshToken']);
        // ignore: use_build_context_synchronously
        Navigator.of(context).pushReplacementNamed('/home');
      } else {
        throw Exception("Failed to login");
      }
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  _showSnackBar(String message) {
    _scaffoldKey.currentState!.showSnackBar(SnackBar(
      content: Text(message),
      duration: const Duration(seconds: 3),
    ));
  }

  _saveToSharedPreferences(String keyName, String accessToken) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.setString(keyName, accessToken);
  }
  

  Future<void> _verifyAccessToken(String? token) async {
    final response = await http.get(
      Uri.parse(GlobalVariables.APP_CURRENT_USER_URL),
      headers: {
        'Authorization': 'Bearer $token',
      },
    );
    if (response.statusCode == 200) {
      Map<String, dynamic> responseJson = json.decode(response.body);
      print(responseJson);
      _saveToSharedPreferences('userId', responseJson['userId']);
      _saveToSharedPreferences('username', responseJson['username']);
      _saveToSharedPreferences('userType', responseJson['userType']);
        // ignore: use_build_context_synchronously
      Navigator.of(context).pushReplacementNamed('/home');
    } else {
      // refresh token
      _refreshToken(await _getFromSharedPreferences("refreshToken"));
      _showSnackBar('Login failed');
    }
  }

  Future<void> _refreshToken(String? refToken) async {
    try {
      final response = await http.get(
        Uri.parse(GlobalVariables.APP_REFRESH_TOKEN),
        headers: {
          'Authorization': 'Bearer $refToken',
        },
      );
      if (response.statusCode == 200) {
        Map<String, dynamic> responseData = json.decode(response.body);
        print("Saving new access tokens");
        _saveToSharedPreferences("accessToken", responseData['accessToken']);
        _saveToSharedPreferences("refreshToken", responseData['refreshToken']);
        // ignore: use_build_context_synchronously
        Navigator.of(context).pushReplacementNamed('/home');
      } else {
        throw Exception("Failed to refresh token");
      }
    } catch (e) {
      _showSnackBar('Login failed');
    }
  }

  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      key: _scaffoldKey,
      body: SafeArea(
        child: Form(
          key: _formKey,
          child: ListView(
            padding: const EdgeInsets.symmetric(horizontal: 24.0),
            children: <Widget>[
              const SizedBox(height: 80.0),
              const Text(
                'E-Mobile',
                style: TextStyle(
                  fontSize: 50.0,
                  fontWeight: FontWeight.bold,
                  color: Colors.red,
                ),
              ),
              const SizedBox(height: 120.0),
              TextFormField(
                controller: _usernameController,
                focusNode: _usernameFocus,
                decoration: const InputDecoration(
                  labelText: 'Username',
                ),
                textInputAction: TextInputAction.next,
                onFieldSubmitted: (_) {
                  FocusScope.of(context).requestFocus(_passwordFocus);
                },
                validator: (value) {
                  if (value!.isEmpty) {
                    return 'Please enter your username';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 12.0),
              TextFormField(
                controller: _passwordController,
                focusNode: _passwordFocus,
                decoration: const InputDecoration(
                  labelText: 'Password',
                ),
                obscureText: true,
                textInputAction: TextInputAction.done,
                onFieldSubmitted: (_) {
                  _onLoginPressed();
                },
                validator: (value) {
                  if (value!.isEmpty) {
                    return 'Please enter your password';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 24.0),
              RaisedButton(
                color: Colors.red,
                padding: const EdgeInsets.symmetric(vertical: 16.0),
                // ignore: sort_child_properties_last
                child: _isLoading ? const CircularProgressIndicator(color: Colors.white,) : const Text('Login', style: TextStyle(color: Colors.white, fontSize: 18.0)),
                onPressed: _onLoginPressed,
              ),
            ],
          ),
        ),
      ),
    );
  }
}