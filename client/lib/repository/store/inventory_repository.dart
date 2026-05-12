import 'dart:convert';
import 'package:frontend/models/store/inventory_model.dart';
import '../../services/store/inventory_api_service.dart';

class InventoryRepository {
  final InventoryApiService _apiService = InventoryApiService();

  Future<List<InventoryModel>?> getInventory(int childId, String token) async {
    try {
      print("[API 호출] 인벤토리 조회 시작 (childId: $childId)");

      final response = await _apiService.fetchInventory(childId, token);

      if (response.statusCode == 200) {
        print("[API 성공] 상태 코드: ${response.statusCode}");
        print("[응답 데이터] ${response.body}");

        final body = jsonDecode(response.body);

        if (body['data'] != null) {
          return (body['data'] as List)
              .map((item) => InventoryModel.fromJson(item))
              .toList();
        } else {
          return [];
        }
      } else {
        print("[API 실패] 상태 코드: ${response.statusCode}");
        return null;
      }
    } catch (e) {
      print("[Repository 에러] 네트워크/런타임 문제 발생: $e");
      return null;
    }
  }

  Future<bool> equipItem(int inventoryId, String token) async {
    try {
      print("[API 호출] 아이템 장착/해제 시작 (inventoryId: $inventoryId)");

      final response = await _apiService.equipItem(inventoryId, token);

      if (response.statusCode == 200) {
        print("[API 성공] 아이템 장착/해제 완료");
        print("[응답 데이터] ${response.body}");
        return true;
      } else {
        print("[API 실패] 상태 코드: ${response.statusCode}");
        return false;
      }
    } catch (e) {
      print("[Repository 에러] 네트워크/런타임 문제 발생: $e");
      return false;
    }
  }
}