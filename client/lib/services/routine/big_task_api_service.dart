import 'package:http/http.dart' as http;
import 'dart:convert';

class BigTaskApiService {
  static const String baseUrl = "http://100.27.204.252:8080";

  Future<http.Response> fetchBigTasks(int parentId, String token) async {
    final url = Uri.parse('$baseUrl/api/v1/big-tasks?parentId=$parentId');
    return await http.get(
      url,
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );
  }

  Future<http.Response> createBigTask(Map<String, dynamic> body, String token) async {
    final url = Uri.parse('$baseUrl/api/v1/big-tasks');
    return await http.post(
      url,
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
      body: jsonEncode(body),
    );
  }
}