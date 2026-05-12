import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:frontend/models/reward/offline_reward_list_model.dart';
import '../../repository/reward/reward_repository.dart';
import 'package:frontend/models/reward/offline_reward_request_model.dart';

class RewardViewModel extends ChangeNotifier {
  final RewardRepository _repository = RewardRepository();

  bool _isLoading = false;
  bool _isWeekly = true;
  List<OfflineRewardListModel> _allOfflineRewards = [];

  // 현재 화면에서 편집/입력 중인 보상 단계 리스트
  List<OfflineRewardListModel> _editingRewards = [];

  bool get isLoading => _isLoading;
  bool get isWeekly => _isWeekly;
  List<OfflineRewardListModel> get editingRewards => _editingRewards;

  final String _testToken = dotenv.env['TEST_TOKEN'] ?? "";

  // 탭 전환 함수
  void togglePeriod(bool isWeekly) {
    _isWeekly = isWeekly;
    _updateEditingList();
    notifyListeners();
  }

  // 내부 로직: 서버 데이터 필터링 및 초기 카드 세팅
  void _updateEditingList() {
    String type = _isWeekly ? "WEEKLY" : "MONTHLY";

    // 서버에서 받아온 데이터 중 현재 타입에 맞는 것 필터링
    List<OfflineRewardListModel> filtered =
    _allOfflineRewards.where((r) => r.periodType == type).toList();

    _editingRewards = List.from(filtered);

    // 리스트 상단에 새 입력을 위한 빈 카드 추가
    _editingRewards.insert(
      0,
      OfflineRewardListModel(
        rewardId: 0,
        periodType: type,
        targetDays: 0, // 기본값 0일
        rewardPromiseText: "",
        status: "PENDING",
      ),
    );
  }

  // 단계 추가
  void addRewardStep() {
    _editingRewards.add(
      OfflineRewardListModel(
        rewardId: 0,
        periodType: _isWeekly ? "WEEKLY" : "MONTHLY",
        targetDays: 0,
        rewardPromiseText: "",
        status: "PENDING",
      ),
    );
    notifyListeners();
  }

  // 단계 삭제
  void removeRewardStep(int index) {
    if (_editingRewards.length > 1) {
      _editingRewards.removeAt(index);
    } else {
      _editingRewards[0] = OfflineRewardListModel(
        rewardId: 0,
        periodType: _isWeekly ? "WEEKLY" : "MONTHLY",
        targetDays: 0,
        rewardPromiseText: "",
        status: "PENDING",
      );
    }
    notifyListeners();
  }

  // 핵심 수정: 데이터 업데이트 (percent -> targetDays)
  void updateRewardEntry(int index, {int? targetDays, String? text}) {
    if (index >= 0 && index < _editingRewards.length) {
      final current = _editingRewards[index];
      _editingRewards[index] = OfflineRewardListModel(
        rewardId: current.rewardId,
        periodType: current.periodType,
        targetDays: targetDays ?? current.targetDays, // targetDays 사용
        rewardPromiseText: text ?? current.rewardPromiseText,
        status: current.status,
      );
      // TextField 입력 중에는 notifyListeners()를 호출하지 않거나
      // 필터링해서 호출해야 커서 튐 현상을 방지할 수 있습니다.
    }
  }

  // 토큰 디코딩 로직 (기존과 동일)
  int _getChildIdFromToken(String token) {
    try {
      final parts = token.split('.');
      if (parts.length != 3) return 1;
      String payload = parts[1];
      while (payload.length % 4 != 0) payload += '=';
      final String decoded = utf8.decode(base64Url.decode(payload));
      final Map<String, dynamic> json = jsonDecode(decoded);
      return int.parse(json['sub'].toString());
    } catch (e) {
      return 1;
    }
  }

  // 보상 목록 조회
  Future<void> fetchOfflineRewards() async {
    _isLoading = true;
    notifyListeners();

    try {
      final int dynamicChildId = _getChildIdFromToken(_testToken);
      final List<OfflineRewardListModel> rewards =
      await _repository.getOfflineRewards(dynamicChildId, _testToken);

      _allOfflineRewards = rewards;
      _updateEditingList();
    } catch (e) {
      print("[ViewModel 에러] 로드 실패: $e");
      _allOfflineRewards = [];
      _updateEditingList();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // 핵심 수정: 보상 등록 (targetPercent -> targetDays)
  Future<void> saveRewardStep(int index) async {
    if (index < 0 || index >= _editingRewards.length) return;

    final reward = _editingRewards[index];

    // 필수 입력값 체크 (targetDays가 0보다 커야 함)
    if (reward.rewardPromiseText.isEmpty || reward.targetDays <= 0) {
      print("[ViewModel] 보상 내용과 목표 일수를 정확히 입력해주세요.");
      return;
    }

    _isLoading = true;
    notifyListeners();

    try {
      final int dynamicChildId = _getChildIdFromToken(_testToken);

      // API 명세서 구조에 맞게 RequestModel 생성
      final request = OfflineRewardRequestModel(
        childId: dynamicChildId,
        periodType: _isWeekly ? "WEEKLY" : "MONTHLY",
        targetDays: reward.targetDays, // targetDays로 전달
        rewardPromiseText: reward.rewardPromiseText,
      );

      final bool isSuccess = await _repository.registerOfflineReward(_testToken, request);

      if (isSuccess) {
        await fetchOfflineRewards(); // 목록 새로고침
      }
    } catch (e) {
      print("[ViewModel 에러] 등록 중 오류 발생: $e");
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }
}