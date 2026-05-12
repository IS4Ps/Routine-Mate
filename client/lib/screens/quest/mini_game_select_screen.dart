import 'package:flutter/material.dart';
import 'n_back_game_screen.dart';
import 'package:frontend/view_model/quest/quest_view_model.dart';
import 'package:provider/provider.dart';
import 'stroop_game_screen.dart';
import 'go_nogo_game_screen.dart';


class MiniGameSelectScreen extends StatelessWidget {
  const MiniGameSelectScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white, // GrowScreen과 동일한 배경색
      body: SingleChildScrollView(
        padding: const EdgeInsets.fromLTRB(0, 50, 0, 40),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 뒤로가기 버튼 (GrowScreen 스타일 유지)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 10),
              child: IconButton(
                icon: const Icon(Icons.arrow_back_ios_new_rounded, color: Colors.black, size: 24),
                onPressed: () => Navigator.pop(context),
              ),
            ),
            const SizedBox(height: 10),
            Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                _buildHeader(),
                const SizedBox(height: 30),

                // 1. 도형 순서 기억하기 (N-Back)
                _buildGameCard(
                  title: "도형 순서 기억하기",
                  subtitle: "전에 나온 도형들을 기억하세요!",
                  icon: Icons.psychology_outlined,
                  gradient: [const Color(0xFF63AFFF), const Color(0xFF8FD8FF)],
                  onTap: () async {
                    final viewModel = Provider.of<QuestViewModel>(context, listen: false);

                    // API 호출을 먼저 수행합니다. (nLevel: 2)
                    await viewModel.startNBackGame(2);

                    if (viewModel.nBackData != null) {
                      // 데이터 로드 성공 시 게임 화면으로 이동
                      Navigator.push(
                        context,
                        MaterialPageRoute(builder: (context) => const NBackGameScreen()),
                      );
                    } else {
                      // 실패 시 에러 메시지 표시
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(content: Text(viewModel.message)),
                      );
                    }
                  },
                ),
                const SizedBox(height: 20),

                // 2. Go/No-Go
                _buildGameCard(
                  title: "Go/No-Go",
                  subtitle: "알맞은 도형이 나오면 클릭해요!",
                  icon: Icons.traffic_outlined,
                  gradient: [const Color(0xFF8CD85A), const Color(0xFFB5E385)], // 초록색 계열
                  onTap: () async {
                    final viewModel = Provider.of<QuestViewModel>(context, listen: false);

                    // Go/No-Go 게임 시작 API 호출 (난이도 1, 총 20문항 예시)
                    await viewModel.startGoNoGoGame(difficulty: 1, totalCount: 20);

                    // 데이터 로드 성공 여부 확인
                    if (viewModel.goNoGoData != null) {
                      // 성공 시 게임 화면으로 이동
                      Navigator.push(
                        context,
                        MaterialPageRoute(builder: (context) => const GoNogoGameScreen()),
                      );
                    } else {
                      // 실패 시 스낵바 등으로 에러 메시지 표시
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(content: Text(viewModel.message)),
                      );
                    }
                  },
                ),
                const SizedBox(height: 20),

                // 3. 단어 색깔 구별하기 (Stroop)
                _buildGameCard(
                  title: "단어 색깔 구별하기",
                  subtitle: "단어의 색을 골라주세요!",
                  icon: Icons.visibility_outlined,
                  gradient: [const Color(0xFFD644FC), const Color(0xFFF17AC8)],
                  onTap: () async {
                    final viewModel = Provider.of<QuestViewModel>(context, listen: false);

                    // N-Back처럼 API 호출을 먼저 수행합니다. (총 20문제, 난이도 1)
                    await viewModel.startStroopGame(totalCount: 20, difficulty: 1);

                    if (viewModel.stroopData != null) {
                      // 데이터 로드 성공 시 스트룹 게임 화면으로 이동
                      Navigator.push(
                        context,
                        MaterialPageRoute(builder: (context) => const StroopGameScreen()),
                      );
                    } else {
                      // 실패 시 에러 메시지 표시
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(content: Text(viewModel.message)),
                      );
                    }
                  },
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  // 헤더 영역 (GrowScreen의 헤더 스타일 재사용)
  Widget _buildHeader() {
    return Center(
      child: Stack(
        alignment: Alignment.center,
        children: [
          Container(
            width: 350,
            height: 100,
            decoration: BoxDecoration(
              color: const Color(0xFFF1F3F5),
              borderRadius: BorderRadius.circular(50),
            ),
          ),
          const Text(
            "미니게임을 선택해주세요!",
            style: TextStyle(
              fontSize: 28, // GrowScreen(32)보다 약간 작게 조절
              fontWeight: FontWeight.w700,
              color: Color(0xFF1F1F1F),
              letterSpacing: -1.0,
            ),
          ),
        ],
      ),
    );
  }

  // 게임 선택 카드 (GrowScreen의 _buildChildGrowCard 로직과 동일하게 구현)
  Widget _buildGameCard({
    required String title,
    required String subtitle,
    required IconData icon,
    required List<Color> gradient,
    required VoidCallback onTap,
  }) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 22),
      child: GestureDetector(
        onTap: onTap,
        child: Container(
          width: double.infinity,
          height: 145,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(30),
            gradient: LinearGradient(
              colors: gradient,
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
            ),
            boxShadow: [
              BoxShadow(
                color: gradient[0].withOpacity(0.3),
                blurRadius: 15,
                offset: const Offset(0, 8),
              ),
            ],
          ),
          child: Stack(
            children: [
              // 배경에 깔리는 큰 아이콘 효과
              Positioned(
                right: -10,
                top: -5,
                child: Icon(
                  icon,
                  size: 110,
                  color: Colors.white.withOpacity(0.12),
                ),
              ),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 25),
                child: Row(
                  children: [
                    // 아이콘 동그라미 배경
                    Container(
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: Colors.white.withOpacity(0.25),
                        shape: BoxShape.circle,
                      ),
                      child: Icon(icon, size: 42, color: Colors.white),
                    ),
                    const SizedBox(width: 20),
                    Expanded(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            title,
                            style: const TextStyle(
                              fontSize: 22,
                              fontWeight: FontWeight.w900,
                              color: Colors.white,
                            ),
                          ),
                          const SizedBox(height: 4),
                          Text(
                            subtitle,
                            style: TextStyle(
                              fontSize: 14,
                              color: Colors.white.withOpacity(0.9),
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}