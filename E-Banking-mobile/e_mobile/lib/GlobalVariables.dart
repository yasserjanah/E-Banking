
class GlobalVariables {
  // ignore: constant_identifier_names
  static const HOSTNAME = "192.168.1.2:8080";
  // ignore: constant_identifier_names
  static const String APP_LOGIN_URL = "http://$HOSTNAME/login"; // login url
  // ignore: constant_identifier_names
  static const String APP_REFRESH_TOKEN = "http://$HOSTNAME/users/refresh-token"; // refresh token url
  // ignore: constant_identifier_names
  static const String APP_CURRENT_USER_URL = "http://$HOSTNAME/users/current"; // get current user url
  // ignore: constant_identifier_names
  static const String APP_CUSTOMERS_URL = "http://$HOSTNAME/customers"; // get customers url
  // ignore: constant_identifier_names
  static const String APP_ACCOUNTS_URL = "http://$HOSTNAME/accounts"; // get accounts url
}