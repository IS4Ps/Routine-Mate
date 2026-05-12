import 'package:flutter/material.dart';
import 'concentrate_screen.dart';
import 'n_back_game_screen.dart';
import 'mini_game_select_screen.dart';

class GrowScreen extends StatelessWidget {
  final VoidCallback? onBack;
  final VoidCallback? onQuizTap;

  const GrowScreen({
    super.key,
    this.onBack,
    this.onQuizTap,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SingleChildScrollView(
        padding: const EdgeInsets.fromLTRB(0, 50, 0, 40),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 10),
              child: IconButton(
                icon: const Icon(Icons.arrow_back_ios_new_rounded, color: Colors.black, size: 24),
                onPressed: onBack,
              ),
            ),
            const SizedBox(height: 10),
            Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                _buildHeader(),
                const SizedBox(height: 30),
                _buildChildGrowCard(
                  title: "미니게임 하러가기",
                  subtitle: "재미있는 미니게임으로 골드 모으기!",
                  icon: Icons.sports_esports_rounded,
                  gradient: [const Color(0xFF63AFFF), const Color(0xFF8FD8FF)],
                  onTap: () {
                    // 미니게임 선택하는 화면으로 이동
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => const MiniGameSelectScreen(),
                      ),
                    );
                  },
                ),
                const SizedBox(height: 20),
                _buildChildGrowCard(
                  title: "집중하기",
                  subtitle: "집중력 쑥쑥! 타이머로 시간 관리하기",
                  icon: Icons.timer_rounded,
                  gradient: [const Color(0xFF8CD85A), const Color(0xFFB5E385)],
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => const ConcentrateScreen(),
                      ),
                    );
                  },
                ),
                const SizedBox(height: 20),
                _buildChildGrowCard(
                  title: "퀴즈 풀러가기",
                  subtitle: "매일매일 똑똑해지는 AI 퀴즈 도전!",
                  icon: Icons.auto_awesome_rounded,
                  gradient: [const Color(0xFFD644FC), const Color(0xFFF17AC8)],
                  onTap: () {
                    if (onQuizTap != null) {
                      onQuizTap!();
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
          Column(
            children: [
              const Text(
                "성장하러 가볼까요?",
                style: TextStyle(
                  fontSize: 32,
                  fontWeight: FontWeight.w700,
                  color: Color(0xFF1F1F1F),
                  letterSpacing: -1.0,
                ),
              ),
              const SizedBox(height: 12),
              Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  _buildDot(const Color(0xFFFFB7B2)),
                  _buildDot(const Color(0xFFFFDAC1)),
                  _buildDot(const Color(0xFFE2F0CB)),
                  _buildDot(const Color(0xFFB5EAD7)),
                  _buildDot(const Color(0xFFC7CEEA)),
                ],
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildDot(Color color) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 4),
      width: 10,
      height: 10,
      decoration: BoxDecoration(color: color, shape: BoxShape.circle),
    );
  }

  Widget _buildChildGrowCard({
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