import 'package:flutter/material.dart';

class MinigameScoreCard extends StatelessWidget {
  const MinigameScoreCard({super.key});

  @override
  Widget build(BuildContext context) {
    final dates = ['7/1', '7/2', '7/3', '7/4', '7/5', '7/6', '7/7'];

    final nBackScores =   [250, 300, 450, 520, 680, 750, 880];
    final goNoGoScores =  [400, 380, 550, 420, 610, 530, 720];
    final stromScores =   [180, 260, 310, 390, 440, 500, 600];

    return Container(
      width: double.infinity,
      margin: const EdgeInsets.symmetric(horizontal: 20),
      padding: const EdgeInsets.fromLTRB(18, 18, 18, 18),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Padding(
            padding: EdgeInsets.only(left: 10),
            child: Text(
              '미니게임 점수 추세',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w700,
                color: Colors.black,
              ),
            ),
          ),
          const SizedBox(height: 16),
          SizedBox(
            height: 240,
            child: CustomPaint(
              size: const Size(double.infinity, 240),
              painter: LineChartPainter(
                dates: dates,
                nBackScores: nBackScores,
                goNoGoScores: goNoGoScores,
                stromScores: stromScores,
              ),
            ),
          ),
          const SizedBox(height: 12),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: const [
              _LegendItem(color: Color(0xFF4A90D9), label: 'N-Back'),
              SizedBox(width: 16),
              _LegendItem(color: Color(0xFF7BC67E), label: 'Go/No-Go'),
              SizedBox(width: 16),
              _LegendItem(color: Color(0xFFF0A500), label: '스트룹'),
            ],
          ),
        ],
      ),
    );
  }
}

class _LegendItem extends StatelessWidget {
  final Color color;
  final String label;

  const _LegendItem({required this.color, required this.label});

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Container(
          width: 10,
          height: 10,
          decoration: BoxDecoration(
            color: color,
            shape: BoxShape.circle,
          ),
        ),
        const SizedBox(width: 4),
        Text(
          label,
          style: const TextStyle(fontSize: 12, color: Color(0xFF3D3D3D), fontWeight: FontWeight.w500),
        ),
      ],
    );
  }
}

class LineChartPainter extends CustomPainter {
  final List<String> dates;
  final List<int> nBackScores;
  final List<int> goNoGoScores;
  final List<int> stromScores;

  const LineChartPainter({
    required this.dates,
    required this.nBackScores,
    required this.goNoGoScores,
    required this.stromScores,
  });

  @override
  void paint(Canvas canvas, Size size) {
    const double leftPadding = 30;
    const double rightPadding = 20;
    const double topPadding = 30;
    const double bottomPadding = 35;

    final chartWidth = size.width - leftPadding - rightPadding;
    final chartHeight = size.height - topPadding - bottomPadding;

    const int maxVal = 1000;

    final gridPaint = Paint()
      ..color = const Color(0xFFE0E0E0)
      ..strokeWidth = 1;

    const textStyle = TextStyle(fontSize: 10, color: Colors.black);

    // Y축 가로 그리드 선 & 라벨
    for (int i = 0; i <= 5; i++) {
      final y = topPadding + chartHeight * (1 - i / 5);
      canvas.drawLine(
        Offset(leftPadding, y),
        Offset(leftPadding + chartWidth, y),
        gridPaint,
      );

      final label = (200 * i).toString();
      final tp = TextPainter(
        text: TextSpan(text: label, style: textStyle),
        textDirection: TextDirection.ltr,
      )..layout();
      tp.paint(canvas, Offset(leftPadding - tp.width - 8, y - tp.height / 2));
    }

    // Y축 제목
    final yTitleTp = TextPainter(
      text: const TextSpan(text: '점수 (Scores)', style: textStyle),
      textDirection: TextDirection.ltr,
    )..layout();
    yTitleTp.paint(canvas, Offset(-2, topPadding - 25));

    // 세로 그리드 선 (날짜 앞뒤로)
    for (int i = 0; i <= dates.length; i++) {
      final x = leftPadding + chartWidth * i / dates.length;
      canvas.drawLine(
        Offset(x, topPadding),
        Offset(x, topPadding + chartHeight),
        gridPaint,
      );
    }

    // X축 라벨 (각 구간 중간에)
    for (int i = 0; i < dates.length; i++) {
      final x = leftPadding + chartWidth * (i + 0.5) / dates.length;
      final tp = TextPainter(
        text: TextSpan(text: dates[i], style: textStyle),
        textDirection: TextDirection.ltr,
      )..layout();
      tp.paint(canvas, Offset(x - tp.width / 2, size.height - bottomPadding + 6));
    }

    // 날짜 (Date) 라벨
    final xTitleTp = TextPainter(
      text: const TextSpan(text: '날짜\n(Date)', style: textStyle),
      textDirection: TextDirection.ltr,
    )..layout();
    xTitleTp.paint(canvas, Offset(
      leftPadding + chartWidth,
      size.height - bottomPadding + 6,
    ));

    // 선 그리기
    void drawLine(List<int> scores, Color color) {
      final paint = Paint()
        ..color = color
        ..strokeWidth = 2
        ..style = PaintingStyle.stroke
        ..strokeCap = StrokeCap.round;

      final path = Path();
      for (int i = 0; i < scores.length; i++) {
        final x = leftPadding + chartWidth * (i + 0.5) / scores.length;
        final y = topPadding + chartHeight * (1 - scores[i] / maxVal);
        if (i == 0) path.moveTo(x, y);
        else path.lineTo(x, y);
      }
      canvas.drawPath(path, paint);

      final dotPaint = Paint()..color = color;
      final labelStyle = TextStyle(fontSize: 9, color: Colors.black, fontWeight: FontWeight.w300);

      for (int i = 0; i < scores.length; i++) {
        final x = leftPadding + chartWidth * (i + 0.5) / scores.length;
        final y = topPadding + chartHeight * (1 - scores[i] / maxVal);
        canvas.drawCircle(Offset(x, y), 3, dotPaint);

        final tp = TextPainter(
          text: TextSpan(text: '${scores[i]}', style: labelStyle),
          textDirection: TextDirection.ltr,
        )..layout();

        final yOffset = color == const Color(0xFF4A90D9)
            ? y - tp.height - 3
            : color == const Color(0xFF7BC67E)
            ? (i == 4 ? y + 3 : y - tp.height - 3)
            : y + 3;

        tp.paint(canvas, Offset(x - tp.width / 2, yOffset));
      }
    }

    drawLine(nBackScores, const Color(0xFF4A90D9));
    drawLine(goNoGoScores, const Color(0xFF7BC67E));
    drawLine(stromScores, const Color(0xFFF0A500));
  }

  @override
  bool shouldRepaint(covariant LineChartPainter oldDelegate) => false;
}