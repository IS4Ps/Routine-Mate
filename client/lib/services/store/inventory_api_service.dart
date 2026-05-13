import 'package:http/http.dart' as http;

class InventoryApiService {
  static const String baseUrl = "http://100.27.204.252:8080";

  Future<http.Response> fetchInventory(int childId, String token) async {
    final url = Uri.parse('$baseUrl/api/v1/inventory/$childId');
    return await http.get(
      url,
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );
  }

  Future<http.Response> equipItem(int inventoryId, String token) async {
    final url = Uri.parse('$baseUrl/api/v1/inventory/$inventoryId/equip');
    return await http.patch(
      url,
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );
  }
}