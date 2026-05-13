import 'dart:convert';
import 'package:frontend/models/store/item_model.dart';
import '../../services/store/store_api_service.dart';

class StoreRepository {
  final StoreApiService _apiService = StoreApiService();

  Future<List<ItemModel>?> getStoreItems(int childLevel, int childJobId, String token) async {
    try {
      print("[API 호출] 상점 아이템 조회 시작 (childLevel: $childLevel, childJobId: $childJobId)");

      final response = await _apiService.fetchStoreItems(childLevel, childJobId, token);

      if (response.statusCode == 200) {
        print("[API 성공] 상태 코드: ${response.statusCode}");
        print("[응답 데이터] ${response.body}");

        final body = jsonDecode(response.body);

        if (body['data'] != null) {
          return (body['data'] as List)
              .map((item) => ItemModel.fromJson(item))
              .toList();
        } else {
          print("[데이터 분석] data가 비어있습니다.");
          return [];
        }
      } else {
        print("[API 실패] 상태 코드: ${response.statusCode}");
        print("[에러 내용] ${response.body}");
        return null;
      }
    } catch (e) {
      print("[Repository 에러] 네트워크/런타임 문제 발생: $e");
      return null;
    }
  }

  Future<bool> purchaseItem(int itemId, int childId, String token) async {
    try {
      print("[API 호출] 아이템 구매 시작 (itemId: $itemId, childId: $childId)");

      final response = await _apiService.purchaseItem(itemId, childId, token);

      if (response.statusCode == 200 || response.statusCode == 201) {
        print("[API 성공] 아이템 구매 완료");
        return true;
      } else {
        print("[API 실패] 상태 코드: ${response.statusCode}");
        print("[에러 내용] ${response.body}");
        return false;
      }
    } catch (e) {
      print("[Repository 에러] 네트워크/런타임 문제 발생: $e");
      return false;
    }
  }
}