import 'package:flutter/material.dart';
import '../../repository/routine/mission_repository.dart';

class MissionViewModel extends ChangeNotifier {
  final MissionRepository _repository = MissionRepository();

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  List<dynamic> _missions = [];
  List<dynamic> get missions => _missions;

  Future<void> getMissionsByDate(int childId, String date) async {
    _isLoading = true;
    notifyListeners();

    _missions = await _repository.getMissionsByDate(childId, date);

    _isLoading = false;
    notifyListeners();
  }
}