import 'dart:convert';
import '../../services/routine/preset_api_service.dart';

class PresetRepository {
  final PresetApiService _apiService = PresetApiService();

  Future<bool> savePresetFromDate(Map<String, dynamic> body) async {
    try {
      print("[API 호출] 날짜 기반 프리셋 생성 시작");

      final response = await _apiService.savePresetFromDate(body);

      if (response.statusCode == 200 || response.statusCode == 201) {
        print("[API 성공] 프리셋 생성 완료");
        print("[응답 데이터] ${response.body}");
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

  Future<List<dynamic>> getPresets(int parentId) async {
    try {
      print("[API 호출] 프리셋 목록 조회 시작");

      final response = await _apiService.getPresets(parentId);

      if (response.statusCode == 200) {
        print("[API 성공] 프리셋 목록 조회 완료");
        final data = jsonDecode(response.body);
        print("[목록 데이터] ${data['data']}");
        return data['data'] ?? [];
      } else {
        print("[API 실패] 상태 코드: ${response.statusCode}");
        return [];
      }
    } catch (e) {
      print("[Repository 에러] 네트워크/런타임 문제 발생: $e");
      return [];
    }
  }

  Future<bool> updatePreset(int presetId, Map<String, dynamic> body) async {
    try {
      print("[API 호출] 프리셋 수정 시작");

      final response = await _apiService.updatePreset(presetId, body);

      if (response.statusCode == 200 || response.statusCode == 201) {
        print("[API 성공] 프리셋 수정 완료");
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

  Future<bool> deletePreset(int presetId) async {
    try {
      print("[API 호출] 프리셋 삭제 시작 (presetId: $presetId)");

      final response = await _apiService.deletePreset(presetId);

      if (response.statusCode == 200 || response.statusCode == 204) {
        print("[API 성공] 프리셋 삭제 완료");
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

  Future<bool> loadPreset(int presetId, Map<String, dynamic> body) async {
    try {
      print("[API 호출] 프리셋 불러오기 시작 (presetId: $presetId)");

      final response = await _apiService.loadPreset(presetId, body);

      if (response.statusCode == 200 || response.statusCode == 201) {
        print("[API 성공] 프리셋 불러오기 완료");
        print("[응답 데이터] ${response.body}");
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