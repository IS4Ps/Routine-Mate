import 'package:http/http.dart' as http;
import 'package:frontend/auth/token_manager.dart' as my_auth;
class SettingApiService {
  static const String baseUrl = "http://100.27.204.252:8080";

  Future<http.Response> fetchParentInfo() async {
    final url = Uri.parse('$baseUrl/parents/me');
    final String? token = my_auth.TokenManager().token;

    return await http.get(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );
  }
}