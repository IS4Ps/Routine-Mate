import 'dart:math';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../view_model/dashboard/dashboard_view_model.dart';
import '../../models/dashboard/monthly_mood_model.dart';
import 'heatmap_card.dart';
import 'minigame_score_card.dart';

class DashboardScreen extends StatefulWidget {
  final VoidCallback? onNotificationTap;
  const DashboardScreen({super.key, this.onNotificationTap});

  @override
  State<DashboardScreen> createState() => _DashboardScreenState();
}

class _DashboardScreenState extends State<DashboardScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<DashboardViewModel>().fetchMonthlyMood();
      context.read<DashboardViewModel>().fetchWeeklyStats();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFEBF1F7),
      body: Consumer<DashboardViewModel>(
        builder: (context, viewModel, child) {
          if (viewModel.isLoading) {
            return const Center(child: CircularProgressIndicator());
          }

          return Column(
            children: [
              Container(
                color: Colors.white,
                child: SafeArea(
                  bottom: false,
                  child: _buildHeader(),
                ),
              ),
              const SizedBox(height: 24),
              Expanded(
                child: SingleChildScrollView(
                  padding: const EdgeInsets.fromLTRB(16, 0, 16, 24),
                  child: Column(
                    children: [
                      _buildProgressCard(viewModel),
                      const SizedBox(height: 24),
                      _buildActivityCard(),
                      const SizedBox(height: 24),
                      _buildEmotionCard(viewModel.monthlyMoodData),
                      const SizedBox(height: 24),
                      const HeatmapCard(
                        title: '루틴 습관 히트맵',
                        subtitle: '3개월 일관성',
                        data: [
                          2,1,0,2,3,3,0,1,1,0,2,3,2,
                          2,2,2,0,3,2,1,2,1,0,0,2,3,
                          1,3,2,0,0,3,1,2,2,2,2,3,1,
                          2,1,0,2,2,0,0,2,2,0,3,2,2,
                          2,3,0,3,2,3,0,2,3,1,3,2,3,
                          3,2,2,0,1,3,3,1,0,1,3,0,0,
                          3,2,2,3,0,3,0,1,0,1,2,2,2,
                        ],
                      ),
                      const SizedBox(height: 24),
                      const HeatmapCard(
                        title: '미니게임 히트맵',
                        subtitle: '3개월 일관성',
                        data: [
                          2,1,0,2,3,3,0,1,1,0,2,3,2,
                          2,2,2,0,3,2,1,2,1,0,0,2,3,
                          1,3,2,0,0,3,1,2,2,2,2,3,1,
                          2,1,0,2,2,0,0,2,2,0,3,2,2,
                          2,3,0,3,2,3,0,2,3,1,3,2,3,
                          3,2,2,0,1,3,3,1,0,1,3,0,0,
                          3,2,2,3,0,3,0,1,0,1,2,2,2,
                        ],
                      ),
                      const SizedBox(height: 24),
                      const MinigameScoreCard(),
                    ],
                  ),
                ),
              ),
            ],
          );
        },
      ),
    );
  }

  Widget _buildHeader() {
    return Container(
      padding: const EdgeInsets.fromLTRB(16, 8, 16, 16),
      decoration: const BoxDecoration(
        color: Colors.white,
        border: Border(
          bottom: BorderSide(color: Color(0xFFD7D7D7)),
        ),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          const Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                '준수의 진행 상황',
                style: TextStyle(
                  fontSize: 28,
                  fontWeight: FontWeight.w500,
                ),
              ),
              SizedBox(height: 4),
              Text(
                '진행 상황을 한 눈에 볼 수 있는 대시보드',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w300,
                ),
              ),
            ],
          ),
          Padding(
            padding: const EdgeInsets.only(bottom: 8),
            child: GestureDetector(
              onTap: () {
                widget.onNotificationTap?.call();
              },
              child: Stack(
                clipBehavior: Clip.none,
                children: const [
                  Icon(Icons.notifications_none, size: 34),
                  Positioned(
                    right: -2,
                    top: 18,
                    child: Text(
                      '④',
                      style: TextStyle(
                        color: Colors.red,
                        fontSize: 12,
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildProgressCard(DashboardViewModel viewModel) {
    return Container(
      width: double.infinity,
      margin: const EdgeInsets.symmetric(horizontal: 20),
      padding: const EdgeInsets.fromLTRB(18, 14, 18, 18),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Padding(
            padding: EdgeInsets.only(left: 6),
            child: Text(
              '활동 & 성과',
              style: TextStyle(
                fontSize: 12,
                fontWeight: FontWeight.w300,
              ),
            ),
          ),
          const SizedBox(height: 4),
          const Padding(
            padding: EdgeInsets.only(left: 6),
            child: Text(
              '주간 루틴 완료율',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w700,
              ),
            ),
          ),
          const SizedBox(height: 18),
          Center(
            child: SizedBox(
              width: 190,
              height: 180,
              child: CustomPaint(
                painter: DonutChartPainter(progress: viewModel.avgCompletionRate / 100),
                child: Center(
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Text(
                        '${viewModel.avgCompletionRate.toStringAsFixed(1)}%',
                        style: const TextStyle(
                          fontSize: 28,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 2),
                      const Text(
                        '평균',
                        style: TextStyle(
                          fontSize: 17,
                          fontWeight: FontWeight.w400,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildActivityCard() {
    return Container(
      width: double.infinity,
      margin: const EdgeInsets.symmetric(horizontal: 20),
      padding: const EdgeInsets.fromLTRB(18, 14, 18, 20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Padding(
            padding: EdgeInsets.only(left: 6),
            child: Text(
              '활동 태그 비율',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w700,
              ),
            ),
          ),
          const SizedBox(height: 10),
          Center(
            child: SizedBox(
              width: 180,
              height: 170,
              child: CustomPaint(
                painter: PieChartPainter(
                  sections: const [
                    PieSection(value: 0.53, color: Color(0xFFE05555), label: '운동'),
                    PieSection(value: 0.13, color: Color(0xFF1586E2), label: '예술'),
                    PieSection(value: 0.34, color: Color(0xFF4CAF50), label: '공부'),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildEmotionCard(MonthlyMoodModel? moodData) {
    return Container(
      width: double.infinity,
      margin: const EdgeInsets.symmetric(horizontal: 20),
      padding: const EdgeInsets.fromLTRB(18, 12, 18, 0),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Padding(
            padding: EdgeInsets.only(left: 6),
            child: Text(
              '감정 캘린더',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w700,
              ),
            ),
          ),
          const SizedBox(height: 8),
          _buildCalendarSection(moodData),
          const SizedBox(height: 4),
          const Padding(
            padding: EdgeInsets.only(left: 6),
            child: Text(
              '상위 3개 감정',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w700,
              ),
            ),
          ),
          const SizedBox(height: 5),
          const Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              _TopEmotionItem(emoji: '😆', label: '행복'),
              _TopEmotionItem(emoji: '😊', label: '신남'),
              _TopEmotionItem(emoji: '😐', label: '평온'),
            ],
          ),
          const SizedBox(height: 10),
          const Padding(
            padding: EdgeInsets.only(left: 6),
            child: Text(
              '감정 변동성',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w700,
              ),
            ),
          ),
          const SizedBox(height: 8),
          SizedBox(
            height: 100,
            child: Row(
              children: [
                const SizedBox(
                  width: 42,
                  child: Padding(
                    padding: EdgeInsets.only(left: 10),
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text('높음', style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold)),
                        Padding(
                          padding: EdgeInsets.only(bottom: 2),
                          child: Text('중간', style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold)),
                        ),
                        Padding(
                          padding: EdgeInsets.only(bottom: 15),
                          child: Text('낮음', style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold)),
                        ),
                      ],
                    ),
                  ),
                ),
                const SizedBox(width: 10),
                Expanded(
                  child: CustomPaint(
                    size: const Size(double.infinity, 120),
                    painter: EmotionLineChartPainter(),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildCalendarSection(MonthlyMoodModel? moodData) {
    final now = DateTime.now();
    final int year = moodData?.year ?? now.year;
    final int month = moodData?.month ?? now.month;
    final int daysInMonth = DateTime(year, month + 1, 0).day;

    return Container(
      padding: const EdgeInsets.fromLTRB(10, 0, 10, 10),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(12),
        color: Colors.white,
      ),
      child: GridView.builder(
        padding: EdgeInsets.zero,
        itemCount: daysInMonth,
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 7,
          childAspectRatio: 0.55,
          crossAxisSpacing: 4,
          mainAxisSpacing: 4,
        ),
        itemBuilder: (context, index) {
          final int day = index + 1;
          final String dateKey = "$year-${month.toString().padLeft(2, '0')}-${day.toString().padLeft(2, '0')}";
          final log = moodData?.moodMap[dateKey];
          final bool isEmpty = log == null;

          return Container(
            decoration: BoxDecoration(
              color: isEmpty ? const Color(0xFFF0F0F0) : Colors.white,
              border: Border.all(color: const Color(0xFFE6E6E6)),
              borderRadius: BorderRadius.circular(6),
            ),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text(
                  '$day',
                  style: TextStyle(
                    fontSize: 12,
                    color: isEmpty ? const Color(0xFFB8B8B8) : Colors.black87,
                    fontWeight: FontWeight.w500,
                  ),
                ),
                const SizedBox(height: 2),
                Text(
                  log?.emotionEmoji ?? '',
                  style: const TextStyle(fontSize: 22),
                ),
              ],
            ),
          );
        },
      ),
    );
  }
}

class _TopEmotionItem extends StatelessWidget {
  final String emoji;
  final String label;

  const _TopEmotionItem({required this.emoji, required this.label});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text(emoji, style: const TextStyle(fontSize: 44)),
        Text(
          label,
          style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
        ),
      ],
    );
  }
}

class DonutChartPainter extends CustomPainter {
  final double progress;

  const DonutChartPainter({required this.progress});

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final radius = size.width / 2 - 10;
    const strokeWidth = 16.0;

    final trackPaint = Paint()
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth
      ..color = const Color(0xFFD7D7D7);

    final progressPaint = Paint()
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth
      ..strokeCap = StrokeCap.round
      ..color = const Color(0xFF1586E2);

    final rect = Rect.fromCircle(center: center, radius: radius);
    canvas.drawCircle(center, radius, trackPaint);
    canvas.drawArc(rect, -pi / 2, 2 * pi * progress, false, progressPaint);
  }

  @override
  bool shouldRepaint(covariant DonutChartPainter oldDelegate) {
    return oldDelegate.progress != progress;
  }
}

class PieSection {
  final double value;
  final Color color;
  final String label;

  const PieSection({required this.value, required this.color, required this.label});
}

class PieChartPainter extends CustomPainter {
  final List<PieSection> sections;

  const PieChartPainter({required this.sections});

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final radius = size.width / 2;
    double startAngle = pi / 2;

    for (final section in sections) {
      final sweepAngle = 2 * pi * section.value;
      final paint = Paint()
        ..style = PaintingStyle.fill
        ..color = section.color;

      canvas.drawArc(
        Rect.fromCircle(center: center, radius: radius),
        startAngle,
        sweepAngle,
        true,
        paint,
      );

      final labelAngle = startAngle + sweepAngle / 2;
      final labelRadius = radius * 0.55;
      final labelOffset = Offset(
        center.dx + labelRadius * cos(labelAngle),
        center.dy + labelRadius * sin(labelAngle),
      );

      final text = '${section.label}\n${(section.value * 100).round()}%';
      final tp = TextPainter(
        text: TextSpan(
          text: text,
          style: const TextStyle(
            color: Colors.black,
            fontSize: 11,
            fontWeight: FontWeight.w600,
            height: 1.3,
          ),
        ),
        textAlign: TextAlign.center,
        textDirection: TextDirection.ltr,
      )..layout();

      Offset adjustedOffset = labelOffset - Offset(tp.width / 2, tp.height / 2);
      if (section.label == '예술') adjustedOffset += const Offset(5, -10);

      tp.paint(canvas, adjustedOffset);
      startAngle += sweepAngle;
    }
  }

  @override
  bool shouldRepaint(covariant PieChartPainter oldDelegate) {
    return oldDelegate.sections != sections;
  }
}

class EmotionLineChartPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final gridPaint = Paint()
      ..color = const Color(0xFFD1D1D1)
      ..strokeWidth = 1;

    final linePaint = Paint()
      ..color = const Color(0xFF1586E2)
      ..strokeWidth = 2
      ..style = PaintingStyle.stroke;

    for (int i = 0; i < 4; i++) {
      final y = size.height * (i / 4) + 8;
      canvas.drawLine(Offset(0, y), Offset(size.width, y), gridPaint);
    }

    final path = Path();
    path.moveTo(0, size.height * 0.82);
    path.cubicTo(
      size.width * 0.12, size.height * 0.68,
      size.width * 0.22, size.height * 0.70,
      size.width * 0.34, size.height * 0.58,
    );
    path.cubicTo(
      size.width * 0.42, size.height * 0.32,
      size.width * 0.50, size.height * 0.62,
      size.width * 0.62, size.height * 0.45,
    );
    path.cubicTo(
      size.width * 0.72, size.height * 0.16,
      size.width * 0.82, size.height * 0.18,
      size.width, size.height * 0.42,
    );
    canvas.drawPath(path, linePaint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}