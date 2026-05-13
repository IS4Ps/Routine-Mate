import 'package:flutter/material.dart';
import 'package:frontend/screens/profile/parent_link.dart';

class ProfileScreen extends StatelessWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final topPadding = MediaQuery.of(context).padding.top;

    return Scaffold(
      backgroundColor: Colors.white,
      body: SingleChildScrollView(
        physics: const ClampingScrollPhysics(),
        child: Column(
          children: [
            // 상단 파란 영역 + 상태바 높이 포함
            Container(
              width: double.infinity,
              padding: EdgeInsets.only(top: topPadding),
              color: const Color(0xFF1586E2),
              child: Column(
                children: [
                  Padding(
                    padding: const EdgeInsets.fromLTRB(16, 16, 16, 12),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text(
                          '프로필',
                          style: TextStyle(
                            color: Colors.white,
                            fontSize: 32,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                        Container(
                          width: 115,
                          height: 30,
                          decoration: BoxDecoration(
                            color: const Color(0xFFF75E5E),
                            borderRadius: BorderRadius.circular(50),
                          ),
                          alignment: Alignment.center,
                          child: const Text(
                            '로그아웃',
                            style: TextStyle(
                              color: Colors.black,
                              fontSize: 18,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),

                  // 프로필 카드
                  Container(
                    margin: const EdgeInsets.fromLTRB(16, 0, 16, 18),
                    padding: const EdgeInsets.all(20),
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(15),
                    ),
                    child: Column(
                      children: [
                        Row(
                          children: [
                            Container(
                              width: 60,
                              height: 60,
                              decoration: const BoxDecoration(
                                color: Color(0xFFD1D1D1),
                                shape: BoxShape.circle,
                              ),
                            ),
                            const SizedBox(width: 12),
                            const Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  '준수',
                                  style: TextStyle(
                                    color: Colors.black,
                                    fontSize: 16,
                                  ),
                                ),
                                SizedBox(height: 4),
                                Text(
                                  '레벨 1 - 모험가',
                                  style: TextStyle(
                                    color: Colors.black,
                                    fontSize: 16,
                                  ),
                                ),
                              ],
                            ),
                          ],
                        ),
                        const SizedBox(height: 18),

                        LayoutBuilder(
                          builder: (context, constraints) {
                            return Stack(
                              children: [
                                Container(
                                  width: double.infinity,
                                  height: 18,
                                  decoration: BoxDecoration(
                                    color: const Color(0xFFE2E2E2),
                                    borderRadius: BorderRadius.circular(30),
                                  ),
                                ),
                                Container(
                                  width: constraints.maxWidth * 0.7, // 70%
                                  height: 18,
                                  decoration: BoxDecoration(
                                    color: const Color(0xFFA1FF6F),
                                    borderRadius: BorderRadius.circular(30),
                                  ),
                                ),
                              ],
                            );
                          },
                        ),

                        const SizedBox(height: 10),
                        const Text(
                          '다음 레벨까지 30%',
                          style: TextStyle(
                            color: Colors.black,
                            fontSize: 16,
                          ),
                        ),
                        const SizedBox(height: 18),
                        const Text(
                          '예상 직업 마법사',
                          style: TextStyle(
                            color: Colors.black,
                            fontSize: 16,
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),

            // 통계 카드 3개
            Padding(
              padding: const EdgeInsets.fromLTRB(16, 16, 16, 0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: const [
                  _StatCard(
                    value: '47',
                    label: '완료한 퀘스트',
                  ),
                  SizedBox(width: 12),
                  _StatCard(
                    value: '1250',
                    label: '획득한 별',
                  ),
                  SizedBox(width: 12),
                  _StatCard(
                    value: '5일',
                    label: '연속 달성',
                  ),
                ],
              ),
            ),

            // 업적 제목
            const Padding(
              padding: EdgeInsets.fromLTRB(16, 20, 16, 0),
              child: Align(
                alignment: Alignment.centerLeft,
                child: Text(
                  '업적',
                  style: TextStyle(
                    color: Colors.black,
                    fontSize: 26,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ),

            // 업적 카드 그리드
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 8, 20, 24),
              child: MediaQuery.removePadding(
                context: context,
                removeTop: true,
                child: GridView.count(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  crossAxisCount: 3,
                  crossAxisSpacing: 25,
                  mainAxisSpacing: 25,
                  childAspectRatio: 100 / 110,
                  children: const [
                    _AchievementCard(title: '첫 퀘스트 완료'),
                    _AchievementCard(title: '일주일 연속\n달성'),
                    _AchievementCard(title: '별 100개 획득'),
                    _AchievementCard(title: '전설의 시작'),
                    _AchievementCard(
                      title: '완벽한 한 달',
                      disabled: true,
                    ),
                    _AchievementCard(
                      title: '위대한 모험가',
                      disabled: true,
                    ),
                  ],
                ),
              ),
            ),

            Padding(
              padding: const EdgeInsets.fromLTRB(16, 0, 16, 24),
              child: Center(
                child: SizedBox(
                  width: 290,
                  height: 55,
                  child: ElevatedButton(
                    onPressed: () {
                      showDialog(
                        context: context,
                        builder: (context) {
                          return const ParentLink();
                        },
                      );
                    },
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color(0xFF1586E2),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(50),
                      ),
                      elevation: 0,
                    ),
                    child: const Text(
                      '보호자 연동하기',
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 22,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _StatCard extends StatelessWidget {
  final String value;
  final String label;

  const _StatCard({
    required this.value,
    required this.label,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 100,
      height: 140,
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(15),
        boxShadow: const [
          BoxShadow(
            color: Color(0x3F000000),
            blurRadius: 4,
            offset: Offset(0, 4),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const SizedBox(height: 12),
          Padding(
            padding: const EdgeInsets.only(left: 12),
            child: Container(
              width: 42,
              height: 42,
              decoration: const BoxDecoration(
                color: Color(0xFFD1D1D1),
                shape: BoxShape.circle,
              ),
            ),
          ),
          const SizedBox(height: 16),
          Padding(
            padding: const EdgeInsets.only(left: 12),
            child: Text(
              value,
              style: const TextStyle(
                color: Colors.black,
                fontSize: 22,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
          const SizedBox(height: 6),
          Padding(
            padding: const EdgeInsets.only(left: 12),
            child: Text(
              label,
              textAlign: TextAlign.left,
              style: const TextStyle(
                color: Color(0xFF7C7D7D),
                fontSize: 12,
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _AchievementCard extends StatelessWidget {
  final String title;
  final bool disabled;

  const _AchievementCard({
    required this.title,
    this.disabled = false,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: disabled ? const Color(0xFFE2E2E2) : Colors.white,
        borderRadius: BorderRadius.circular(15),
        boxShadow: const [
          BoxShadow(
            color: Color(0x3F000000),
            blurRadius: 4,
            offset: Offset(0, 4),
          ),
        ],
      ),
      child: Column(
        children: [
          const SizedBox(height: 12),
          Container(
            width: 48,
            height: 48,
            decoration: const BoxDecoration(
              color: Color(0xFFD9D9D9),
              shape: BoxShape.circle,
            ),
          ),
          const Spacer(),
          Padding(
            padding: const EdgeInsets.only(bottom: 12),
            child: Text(
              title,
              textAlign: TextAlign.center,
              style: const TextStyle(
                color: Color(0xFF7C7D7D),
                fontSize: 12,
              ),
            ),
          ),
        ],
      ),
    );
  }
}