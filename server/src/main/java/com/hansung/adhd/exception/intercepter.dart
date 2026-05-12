import 'package:dio/dio.dio';
// 보안 스토리지 패키지 (flutter_secure_storage 등을 쓴다고 가정)
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class DioClient {
  static final Dio _dio = Dio(BaseOptions(
    baseUrl: 'http://100.27.204.252:8080', // 백엔드 서버 주소
  ));
  static final FlutterSecureStorage _storage = FlutterSecureStorage();

  static Dio get dio {
    // 💡 여기에 인터셉터를 장착!
    _dio.interceptors.add(
      InterceptorsWrapper(
        // 1. 요청을 보낼 때 (토큰 자동 삽입)
        onRequest: (options, handler) async {
          // 스토리지에서 실제 토큰(parent_token or child_token) 꺼내오기
          final accessToken = await _storage.read(key: 'accessToken');

          if (accessToken != null) {
            // 헤더에 자동으로 Bearer 토큰 꽂아버리기!
            options.headers['Authorization'] = 'Bearer $accessToken';
          }
          print('[요청] ${options.method} ${options.path}');
          return handler.next(options); // 하던 길 가라고 보내줌
        },

        // 2. 응답이 왔을 때
        onResponse: (response, handler) {
          print('[응답] ${response.statusCode} ${response.requestOptions.path}');
          return handler.next(response);
        },

        // 3. 에러가 났을 때 (401, 403 처리)
        onError: (DioException e, handler) async {
          if (e.response?.statusCode == 401 || e.response?.statusCode == 403) {
            print('[에러] 권한 없음! 캐시된 예전 토큰 삭제!!');

            // 💡 이 줄을 추가해서 꼬인 토큰을 앱에서 완전히 날려버리세요!
            await _storage.deleteAll();

            // TODO: (선택) 여기서 로그인 화면으로 강제 이동시켜주면 완벽합니다!
          }
          return handler.next(e);
        },
      ),
    );
    return _dio;
  }
}