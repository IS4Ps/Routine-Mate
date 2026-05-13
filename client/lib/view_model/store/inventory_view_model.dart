import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:frontend/models/store/inventory_model.dart';
import '../../repository/store/inventory_repository.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class InventoryViewModel extends ChangeNotifier {
  final InventoryRepository _repository = InventoryRepository();

  bool _isLoading = false;
  List<InventoryModel> _items = [];

  bool get isLoading => _isLoading;
  List<InventoryModel> get items => _items;

  final String _testToken = dotenv.env['TEST_TOKEN'] ?? "";

  int _getChildIdFromToken(String token) {
    try {
      final parts = token.split('.');
      if (parts.length != 3) return 1;

      String payload = parts[1];
      while (payload.length % 4 != 0) {
        payload += '=';
      }

      final String decoded = utf8.decode(base64Url.decode(payload));
      final Map<String, dynamic> json = jsonDecode(decoded);

      return int.parse(json['sub'].toString());
    } catch (e) {
      print("토큰 디코딩 에러: $e");
      return 1;
    }
  }

  Future<void> loadInventory() async {
    _isLoading = true;
    notifyListeners();

    final int childId = _getChildIdFromToken(_testToken);
    _items = await _repository.getInventory(childId, _testToken) ?? [];

    _isLoading = false;
    notifyListeners();
  }

  Future<void> equipItem(int inventoryId) async {
    _isLoading = true;
    notifyListeners();

    final bool isSuccess = await _repository.equipItem(inventoryId, _testToken);

    if (isSuccess) {
      print("[장착/해제 성공]");
      await loadInventory(); // 장비 목록 새로고침
    } else {
      print("[장착/해제 실패]");
    }

    _isLoading = false;
    notifyListeners();
  }
}