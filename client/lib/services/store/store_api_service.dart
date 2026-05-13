import 'package:http/http.dart' as http;

class StoreApiService {
  static const String baseUrl = "http://100.27.204.252:8080";

  // 상점 아이템 조회
  Future<http.Response> fetchStoreItems(int childLevel, int childJobId, String token) async {
    final url = Uri.parse('$baseUrl/api/v1/items?childLevel=$childLevel&childJobId=$childJobId');
    return await http.get(
      url,
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );
  }

  // 아이템 구매
  Future<http.Response> purchaseItem(int itemId, int childId, String token) async {
    final url = Uri.parse('$baseUrl/api/v1/inventory/$itemId/purchase?childId=$childId');
    return await http.post(
      url,
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );
  }
}