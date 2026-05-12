import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:frontend/view_model/routine/big_task_view_model.dart';
import 'package:frontend/view_model/routine/mission_view_model.dart';
import 'package:frontend/view_model/routine/preset_view_model.dart';
import 'package:frontend/view_model/setting/setting_view_model.dart';
import 'package:provider/provider.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:webview_flutter/webview_flutter.dart';
import 'package:webview_flutter_android/webview_flutter_android.dart';
import 'package:kakao_flutter_sdk_user/kakao_flutter_sdk_user.dart';
import 'view_model/dashboard/dashboard_view_model.dart';
import 'view_model/quest/quest_view_model.dart';
import 'view_model/reward/reward_view_model.dart';
import 'start_screen.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // 카카오 SDK 초기화
  KakaoSdk.init(
    nativeAppKey: 'b83532b20b25865de4745d49050ecc79',
  );

  // Android에서는 키 해시, iOS에서는 origin 확인용으로 출력됨
  print("카카오 origin: ${await KakaoSdk.origin}");

  // Android에서만 Android WebView 설정
  if (defaultTargetPlatform == TargetPlatform.android &&
      WebViewPlatform.instance == null) {
    WebViewPlatform.instance = AndroidWebViewPlatform();
  }

  await dotenv.load(fileName: ".env");

  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => QuestViewModel()),
        ChangeNotifierProvider(create: (_) => DashboardViewModel()),
        ChangeNotifierProvider(create: (_) => RewardViewModel()),
        ChangeNotifierProvider(create: (_) => BigTaskViewModel()),
        ChangeNotifierProvider(create: (_) => PresetViewModel()),
        ChangeNotifierProvider(create: (_) => SettingViewModel()),
        ChangeNotifierProvider(create: (_) => MissionViewModel()),
      ],
      child: const MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'IS4Ps',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF1687E3),
        ),
        useMaterial3: true,
      ),
      home: const StartScreen(),
    );
  }
}