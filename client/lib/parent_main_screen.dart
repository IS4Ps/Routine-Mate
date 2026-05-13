import 'package:provider/provider.dart';
import 'package:frontend/view_model/routine/big_task_view_model.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:frontend/screens/dashboard/notification_screen.dart';
import 'screens/dashboard/dashboard_screen.dart';
import 'screens/routine/routine_setting_screen.dart';
import 'screens/routine/mission_management_screen.dart';
import 'screens/setting/setting_screen.dart';
import 'screens/reward/reward_screen.dart';

class ParentMainScreen extends StatefulWidget {
  const ParentMainScreen({super.key});

  @override
  State<ParentMainScreen> createState() => _ParentMainScreenState();
}

class _ParentMainScreenState extends State<ParentMainScreen> {
  int _selectedIndex = 0;
  bool _showNotification = false;
  bool _showMissionManagement = false;

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
      _showNotification = false;
      _showMissionManagement = false;
    });
    if (index == 1) {
      Provider.of<BigTaskViewModel>(context, listen: false).loadBigTasks();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          IndexedStack(
            index: _selectedIndex,
            children: [
              DashboardScreen(
                onNotificationTap: () {
                  setState(() {
                    _showNotification = true;
                  });
                },
              ),
              RoutineSettingScreen(
                onMissionManagementTap: () {
                  setState(() {
                    _showMissionManagement = true;
                  });
                },
              ),
              const RewardScreen(),
              const SettingScreen(),
            ],
          ),
          if (_showNotification)
            NotificationScreen(
              onBack: () {
                setState(() {
                  _showNotification = false;
                });
              },
            ),
          if (_showMissionManagement)
            MissionManagementScreen(
              onBack: () {
                setState(() {
                  _showMissionManagement = false;
                });
              },
            ),
        ],
      ),
      bottomNavigationBar: Container(
        decoration: const BoxDecoration(
          color: Color(0xFFFFFFFF),
          border: Border(
            top: BorderSide(color: Color(0xFFE3E3E3), width: 1.0),
          ),
        ),
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
            selectedFontSize: 16,
            unselectedFontSize: 16,
            items: [
              _buildNavItem('assets/icons/dashboard.svg', '대시보드'),
              _buildNavItem('assets/icons/routine.svg', '루틴 설정'),
              _buildNavItem('assets/icons/reward.svg', '보상 설정'),
              _buildNavItem('assets/icons/setting.svg', '설정'),
            ],
          ),
        ),
      ),
    );
  }

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