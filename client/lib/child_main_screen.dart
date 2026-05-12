import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart'; // SVG 사용을 위해 꼭 필요
import 'package:provider/provider.dart';
import 'package:frontend/view_model/store/store_view_model.dart';
import 'package:frontend/view_model/store/inventory_view_model.dart';
import 'screens/quest/quest_screen.dart';
import 'screens/home/home_screen.dart';
import 'screens/profile/profile_screen.dart';
import 'screens/store/store_screen.dart';

class ChildMainScreen extends StatefulWidget {
  const ChildMainScreen({super.key});

  @override
  State<ChildMainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<ChildMainScreen> {
  int _selectedIndex = 0; // 현재 선택된 탭의 인덱스

  final List<Widget> _screens = [
    const Center(child: Text("아동")),   // 0번: 홈
    const QuestScreen(),
    const ProfileScreen(),
    const StoreScreen(),
  ];

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index; // 탭을 누를 때마다 화면을 다시 그려줘
    });
  }

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => StoreViewModel()),
        ChangeNotifierProvider(create: (_) => InventoryViewModel()),
      ],
      child: Scaffold(
        body: _screens[_selectedIndex],
        bottomNavigationBar: Container(
          decoration: const BoxDecoration(
            color: Color(0xFFFFFFFF),
            border: Border(
              top: BorderSide(color: Color(0xFFE3E3E3), width: 1.0),
            ),
          ),
          // 1. Theme 위젯으로 감싸서 스플래시 효과 제거
          child: Theme(
            data: ThemeData(
              splashColor: Colors.transparent,
              highlightColor: Colors.transparent,
            ),
            child: BottomNavigationBar(
              backgroundColor: const Color(0xFFFFFFFF),
              elevation: 0,
              type: BottomNavigationBarType.fixed,
              currentIndex: _selectedIndex,
              onTap: _onItemTapped,
              selectedItemColor: const Color(0xFF4A90E2),
              unselectedItemColor: const Color(0xFF7C7D7D),
      
              // 2. 폰트 크기를 동일하게 설정해서 확대 현상 방지
              selectedFontSize: 16,   // 원하는 크기로 설정
              unselectedFontSize: 16, // 선택되지 않았을 때도 동일하게
      
              items: [
                _buildNavItem('assets/icons/home.svg', '홈'),
                _buildNavItem('assets/icons/quest.svg', '퀘스트'),
                _buildNavItem('assets/icons/profile.svg', '프로필'),
                _buildNavItem('assets/icons/store.svg', '상점'),
              ],
            ),
          ),
        ),
      ),
    );
  }

  // 중복 코드를 줄이기 위한 아이템 생성 함수
  BottomNavigationBarItem _buildNavItem(String assetPath, String label) {
    return BottomNavigationBarItem(
      icon: SvgPicture.asset(
        assetPath,
        width: 48,
        height: 48,
        colorFilter: const ColorFilter.mode(Color(0xFF7C7D7D), BlendMode.srcIn),
      ),
      activeIcon: SvgPicture.asset(
        assetPath,
        width: 48,
        height: 48,
        colorFilter: const ColorFilter.mode(Color(0xFF4A90E2), BlendMode.srcIn),
      ),
      label: label,
    );
  }
}