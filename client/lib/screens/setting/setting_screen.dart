import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:kakao_flutter_sdk_user/kakao_flutter_sdk_user.dart';
import '../../start_screen.dart';
import '../../view_model/setting/setting_view_model.dart';
import 'child_selection_dialog.dart';


class SettingScreen extends StatefulWidget {
  const SettingScreen({super.key});

  @override
  State<SettingScreen> createState() => _SettingScreenState();
}

class _SettingScreenState extends State<SettingScreen> {
  String currentChildName = '준수';
  String? profileImageUrl;
  String parentNickname = '사용자';

  @override
  void initState() {
    super.initState();
    // 1. 화면 로드 후 실제 카카오 토큰을 꺼내서 ViewModel에 전달
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      await _loadUserInfo();
      _loadKakaoProfile();
    });
  }

  // --- [수정] 실제 토큰을 꺼내서 서버 데이터를 불러오는 함수 ---
  Future<void> _loadUserInfo() async {
    try {
      if (mounted) {
        // 복잡하게 토큰 꺼낼 필요 없이 그냥 '가져와!'라고만 하면 됨
        await context.read<SettingViewModel>().fetchParentInfo();
      }
    } catch (e) {
      debugPrint('로드 에러: $e');
    }
  }

  // 카카오 SDK 실시간 프로필 정보 로드 (이름, 사진)
  Future<void> _loadKakaoProfile() async {
    try {
      User user = await UserApi.instance.me();
      setState(() {
        parentNickname = user.kakaoAccount?.profile?.nickname ?? '사용자';
        profileImageUrl = user.kakaoAccount?.profile?.thumbnailImageUrl;
      });
    } catch (e) {
      debugPrint('카카오 프로필 로드 실패: $e');
    }
  }

  void _openChildSelection() {
    showDialog(
      context: context,
      barrierDismissible: true,
      builder: (context) => const ChildSelectionDialog(),
    ).then((value) {
      if (value != null) setState(() => currentChildName = value);
    });
  }

  @override
  Widget build(BuildContext context) {
    final viewModel = context.watch<SettingViewModel>();

    return Scaffold(
      backgroundColor: const Color(0xFFECF2F8),
      body: viewModel.isLoading
          ? const Center(child: CircularProgressIndicator())
          : Column(
        children: [
          _buildCustomAppBar(),
          Expanded(
            child: SingleChildScrollView(
              padding: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 20.0),
              child: Column(
                children: [
                  _buildProfileSection(viewModel), // 프로필 정보 표시
                  _buildChildAccountSection(),
                  _buildMenuSection(),
                  const SizedBox(height: 10),
                  _buildLogoutButton(),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  // --- 프로필 섹션 (수정 버튼 제거 버전) ---
  Widget _buildProfileSection(SettingViewModel viewModel) {
    final parentData = viewModel.parentData;

    return _buildSectionCard(
      title: '프로필',
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 5.0),
        child: Row(
          children: [
            // 카카오 프로필 이미지
            CircleAvatar(
              radius: 28,
              backgroundColor: const Color(0XFFD9D9D9),
              backgroundImage: profileImageUrl != null ? NetworkImage(profileImageUrl!) : null,
              child: profileImageUrl == null ? const Icon(Icons.person, color: Colors.white, size: 30) : null,
            ),
            const SizedBox(width: 15),
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // 카카오 닉네임
                Text(
                    parentNickname,
                    style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 17, color: Colors.black87)
                ),
                const SizedBox(height: 4),
                // 서버에서 가져온 실제 이메일 연동
                Text(
                    parentData?.email ?? '이메일 정보 없음',
                    style: const TextStyle(fontSize: 13, color: Color(0XFF7C7D7D))
                ),
              ],
            )
          ],
        ),
      ),
    );
  }

  // 자녀 계정 섹션
  Widget _buildChildAccountSection() {
    return _buildSectionCard(
      title: '자녀 계정',
      trailing: TextButton(
        onPressed: _openChildSelection,
        style: TextButton.styleFrom(padding: EdgeInsets.zero, minimumSize: const Size(40, 20)),
        child: const Text('선택하기', style: TextStyle(color: Color(0XFF1687E3), fontSize: 13, fontWeight: FontWeight.bold)),
      ),
      child: Column(
        children: [
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(color: const Color(0xFFF8F8F8), borderRadius: BorderRadius.circular(12)),
            child: Row(
              children: [
                const CircleAvatar(radius: 24, backgroundColor: Color(0XFFD9D9D9)),
                const SizedBox(width: 12),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(currentChildName, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                    const SizedBox(height: 2),
                    const Text('레벨 1 - 모험가', style: TextStyle(fontSize: 12, color: Color(0XFF7C7D7D))),
                  ],
                )
              ],
            ),
          ),
          const SizedBox(height: 12),
          SizedBox(
            width: double.infinity,
            height: 48,
            child: ElevatedButton.icon(
              onPressed: () {},
              icon: const Icon(Icons.link, size: 20),
              label: const Text('자녀 계정 연동하기', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF1687E3),
                foregroundColor: Colors.white,
                elevation: 0,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              ),
            ),
          )
        ],
      ),
    );
  }

  // 메뉴 섹션
  Widget _buildMenuSection() {
    return _buildSectionCard(
      padding: EdgeInsets.zero,
      child: Column(
        children: [
          _buildListMenu('도움말 및 지원'),
          const Divider(height: 1, indent: 15, endIndent: 15),
          _buildListMenu('서비스 이용약관'),
          const Divider(height: 1, indent: 15, endIndent: 15),
          _buildListMenu('개인정보 처리방침'),
        ],
      ),
    );
  }

  // 로그아웃 버튼 (토큰 매니저 clear 포함)
  Widget _buildLogoutButton() {
    return SizedBox(
      width: double.infinity,
      height: 46,
      child: TextButton(
        onPressed: () {
          showDialog(
            context: context,
            builder: (context) => AlertDialog(
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
              title: const Text('로그아웃', style: TextStyle(fontWeight: FontWeight.bold)),
              content: const Text('정말 로그아웃하시겠습니까?\n로그인 정보가 완전히 삭제됩니다.'),
              actions: [
                TextButton(onPressed: () => Navigator.pop(context), child: const Text('취소', style: TextStyle(color: Colors.grey))),
                TextButton(
                  onPressed: () async {
                    try {
                      await UserApi.instance.logout();
                      await TokenManagerProvider.instance.manager.clear();
                      if (mounted) {
                        Navigator.pushAndRemoveUntil(
                          context,
                          MaterialPageRoute(builder: (context) => const StartScreen()),
                              (route) => false,
                        );
                      }
                    } catch (e) {
                      await TokenManagerProvider.instance.manager.clear();
                      Navigator.pushAndRemoveUntil(
                        context,
                        MaterialPageRoute(builder: (context) => const StartScreen()),
                            (route) => false,
                      );
                    }
                  },
                  child: const Text('로그아웃', style: TextStyle(color: Colors.redAccent, fontWeight: FontWeight.bold)),
                ),
              ],
            ),
          );
        },
        style: TextButton.styleFrom(
          backgroundColor: const Color(0xFFF5E6E6),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: const [
            Icon(Icons.logout, color: Colors.redAccent, size: 16),
            SizedBox(width: 8),
            Text('로그아웃', style: TextStyle(color: Colors.redAccent, fontWeight: FontWeight.bold, fontSize: 14)),
          ],
        ),
      ),
    );
  }

  // 공통 레이아웃 위젯
  Widget _buildCustomAppBar() { return Container(width: double.infinity, padding: const EdgeInsets.only(top: 50, left: 20, right: 20, bottom: 15), color: Colors.white, child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: const [Text('설정', style: TextStyle(color: Colors.black, fontWeight: FontWeight.bold, fontSize: 20)), Text('부모님 계정 관리', style: TextStyle(color: Color(0XFF7C7D7D), fontSize: 12))])); }
  Widget _buildSectionCard({String? title, Widget? trailing, required Widget child, EdgeInsets? padding}) { return Container(margin: const EdgeInsets.only(bottom: 15), padding: padding ?? const EdgeInsets.all(15), decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(15), boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.02), blurRadius: 8, offset: const Offset(0, 2))]), child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [if (title != null) Padding(padding: const EdgeInsets.only(bottom: 10), child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [Text(title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15)), if (trailing != null) trailing])), child])); }
  Widget _buildListMenu(String title) { return ListTile(dense: true, contentPadding: const EdgeInsets.symmetric(horizontal: 20), title: Text(title, style: const TextStyle(fontSize: 14, fontWeight: FontWeight.w500)), trailing: const Icon(Icons.chevron_right, color: Colors.grey, size: 20), onTap: () {}); }
}