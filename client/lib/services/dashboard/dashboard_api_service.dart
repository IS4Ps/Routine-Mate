import 'package:http/http.dart' as http;

class DashboardApiService {
  static const String baseUrl = "http://100.27.204.252:8080";

  // 월간 캘린더 감정 조회
  Future<http.Response> fetchMonthlyMood(int childId, String token, int year, int month) async {
    final url = Uri.parse('$baseUrl/api/v1/mood-logs/$childId/monthly?year=$year&month=$month');

    return await http.get(
      url,
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );
  }

  Future<http.Response> fetchWeeklyStats(int childId) async {
    final url = Uri.parse('$baseUrl/api/v1/missions/stats/$childId');
    return await http.get(url);
  }
}