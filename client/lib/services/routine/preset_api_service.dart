import 'package:http/http.dart' as http;
import 'dart:convert';

class PresetApiService {
  static const String baseUrl = "http://100.27.204.252:8080";

  Future<http.Response> savePresetFromDate(Map<String, dynamic> body) async {
    final url = Uri.parse('$baseUrl/api/v1/presets/save-from-date');
    return await http.post(
      url,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(body),
    );
  }

  Future<http.Response> getPresets(int parentId) async {
    final url = Uri.parse('$baseUrl/api/v1/presets?parentId=$parentId');
    return await http.get(url);
  }

  Future<http.Response> updatePreset(int presetId, Map<String, dynamic> body) async {
    final url = Uri.parse('$baseUrl/api/v1/presets/$presetId');
    return await http.put(
      url,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(body),
    );
  }

  Future<http.Response> deletePreset(int presetId) async {
    final url = Uri.parse('$baseUrl/api/v1/presets/$presetId');
    return await http.delete(url);
  }

  Future<http.Response> loadPreset(int presetId, Map<String, dynamic> body) async {
    final url = Uri.parse('$baseUrl/api/v1/presets/$presetId/load');
    return await http.post(
      url,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(body),
    );
  }
}