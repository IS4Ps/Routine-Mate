import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:frontend/models/routine/big_task_model.dart';
import '../../repository/routine/big_task_repository.dart';
import '../../repository/routine/mission_repository.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class BigTaskViewModel extends ChangeNotifier {
  final BigTaskRepository _repository = BigTaskRepository();
  final MissionRepository _missionRepository = MissionRepository();

  bool _isLoading = false;
  List<BigTaskModel> _bigTasks = [];

  bool get isLoading => _isLoading;
  List<BigTaskModel> get bigTasks => _bigTasks;

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

  Future<void> loadBigTasks() async {
    _isLoading = true;
    notifyListeners();

    final int parentId = _getParentIdFromToken(_testToken);
    _bigTasks = await _repository.getBigTasks(parentId, _testToken) ?? [];

    _isLoading = false;
    notifyListeners();
  }

  Future<bool> createBigTask({
    required String title,
    required String startTime,
    required String endTime,
    required List<Map<String, dynamic>> smallTasks,
    required String tag,
  }) async {
    _isLoading = true;
    notifyListeners();

    final int parentId = _getParentIdFromToken(_testToken);
    final body = {
      "parentId": parentId,
      "title": title,
      "icon": "default",
      "orderIndex": _bigTasks.length + 1,
      "startTime": startTime,
      "endTime": endTime,
      "smallTasks": smallTasks,
    };

    final bool isSuccess = await _repository.createBigTask(body, _testToken);

    if (isSuccess) {
      await loadBigTasks();
    }

    _isLoading = false;
    notifyListeners();

    return isSuccess;
  }

  Future<bool> createMission({
    required int bigTaskId,
    required int childId,
    required String date,
    required String startTime,
    required String endTime,
  }) async {
    final body = {
      "childId": childId,
      "originBigTaskId": bigTaskId,
      "assignedExp": 20,
      "date": date,
      "startTime": startTime,
      "endTime": endTime,
    };

    return await _missionRepository.createMission(body);
  }
}