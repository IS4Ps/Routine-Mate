import 'dart:convert';
import 'package:flutter/material.dart';
import '../../repository/routine/preset_repository.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class PresetViewModel extends ChangeNotifier {
  final PresetRepository _repository = PresetRepository();

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  List<dynamic> _presets = [];
  List<dynamic> get presets => _presets;

  final String _testToken = dotenv.env['TEST_TOKEN'] ?? "";

  int _getParentIdFromToken(String token) {
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

  Future<bool> savePresetFromDate({
    required String title,
    required String date,
  }) async {
    _isLoading = true;
    notifyListeners();

    final body = {
      "parentId": 5,
      "childId": 5,
      "date": date,
      "title": title,
      "description": "",
    };

    final bool isSuccess = await _repository.savePresetFromDate(body);

    _isLoading = false;
    notifyListeners();

    return isSuccess;
  }

  Future<void> getPresets() async {
    _isLoading = true;
    notifyListeners();

    final int parentId = _getParentIdFromToken(_testToken);
    _presets = await _repository.getPresets(parentId);

    _isLoading = false;
    notifyListeners();
  }

  Future<bool> updatePreset({
    required int presetId,
    required String title,
  }) async {
    _isLoading = true;
    notifyListeners();

    final body = {
      "title": title,
      "description": "",
      "icon": "default",
      "durationDays": 1,
    };

    final bool isSuccess = await _repository.updatePreset(presetId, body);

    if (isSuccess) await getPresets();

    _isLoading = false;
    notifyListeners();

    return isSuccess;
  }

  Future<bool> deletePreset(int presetId) async {
    _isLoading = true;
    notifyListeners();

    final bool isSuccess = await _repository.deletePreset(presetId);

    if (isSuccess) await getPresets();

    _isLoading = false;
    notifyListeners();

    return isSuccess;
  }

  Future<bool> loadPreset({
    required int presetId,
    required String startDate,
  }) async {
    _isLoading = true;
    notifyListeners();

    final body = {
      "childId": 5,
      "startDate": startDate,
      "assignedExpPerMission": 20,
    };

    final bool isSuccess = await _repository.loadPreset(presetId, body);

    _isLoading = false;
    notifyListeners();

    return isSuccess;
  }
}