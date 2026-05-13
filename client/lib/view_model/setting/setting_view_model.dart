import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:frontend/repository/setting/setting_repository.dart';
import 'package:frontend/models/setting/parent_model.dart';


class SettingViewModel extends ChangeNotifier {
  final SettingRepository _repository = SettingRepository();

  ParentModel? _parentData;
  bool _isLoading = false;

  ParentModel? get parentData => _parentData;
  bool get isLoading => _isLoading;

  Future<void> fetchParentInfo() async {
    _isLoading = true;
    notifyListeners();

    try {
      final ParentModel? result = await _repository.getParentInfo();

      if (result != null) {
        _parentData = result;

        debugPrint('[SettingViewModel] 내 정보 조회 성공!');
        debugPrint('🆔 Parent ID : ${_parentData?.parentId}');
        debugPrint('📧 Email     : ${_parentData?.email}');
      } else {
        debugPrint('[SettingViewModel] 결과 데이터가 null입니다.');
      }
    } catch (e) {
      debugPrint('[SettingViewModel] 에러 발생: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }
}