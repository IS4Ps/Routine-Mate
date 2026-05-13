import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import '../auth/token_manager.dart';

class FcmTokenService {
  static final String _baseUrl = dotenv.env['BASE_URL'] ?? '';

  /// 앱 시작 시 FCM 토큰을 받아 서버에 등록
  static Future<void> registerToken() async {
    try {
      final messaging = FirebaseMessaging.instance;

      // 알림 권한 요청
      final settings = await messaging.requestPermission(
        alert: true,
        badge: true,
        sound: true,
      );

      if (settings.authorizationStatus != AuthorizationStatus.authorized) {
        return;
      }

      final fcmToken = await messaging.getToken();
      if (fcmToken == null) return;

      final accessToken = await TokenManager.getAccessToken();
      if (accessToken == null) return;

      await http.post(
        Uri.parse('$_baseUrl/auth/fcm-token?fcmToken=$fcmToken'),
        headers: {'Authorization': 'Bearer $accessToken'},
      );

      // 토큰 갱신 시 자동 재등록
      messaging.onTokenRefresh.listen((newToken) async {
        final token = await TokenManager.getAccessToken();
        if (token == null) return;
        await http.post(
          Uri.parse('$_baseUrl/auth/fcm-token?fcmToken=$newToken'),
          headers: {'Authorization': 'Bearer $token'},
        );
      });
    } catch (_) {}
  }

  /// 포그라운드 메시지 수신 처리
  static void handleForegroundMessages() {
    FirebaseMessaging.onMessage.listen((RemoteMessage message) {
      print('[FCM] 포그라운드 메시지: ${message.notification?.title} - ${message.notification?.body}');
    });
  }
}
