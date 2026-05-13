import 'package:flutter/material.dart';
import 'dart:async';

class RoutineDetailScreen extends StatefulWidget {
  final String taskTitle;
  final String mainQuestTitle;

  const RoutineDetailScreen({
    super.key,
    required this.taskTitle,
    required this.mainQuestTitle,
  });

  @override
  State<RoutineDetailScreen> createState() => _RoutineDetailScreenState();
}

class _RoutineDetailScreenState extends State<RoutineDetailScreen> {
  double _progress = 1.0;
  int _remainingSeconds = 180;
  Timer? _timer;

  @override
  void initState() {
    super.initState();
    _startTimer();
  }

  void _startTimer() {
    _timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (_remainingSeconds == 0) {
        setState(() => timer.cancel());
      } else {
        setState(() {
          _remainingSeconds--;
          _progress = _remainingSeconds / 180;
        });
      }
    });
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            // 1. 뒤로가기(X) 버튼 섹션
            Align(
              alignment: Alignment.centerLeft,
              child: Padding(
                padding: const EdgeInsets.only(left: 10, top: 10),
                child: IconButton(
                  icon: const Icon(Icons.close, color: Colors.black, size: 28),
                  onPressed: () => Navigator.pop(context),
                ),
              ),
            ),

            // 2. 상단 퀘스트 제목
            Text(
              widget.mainQuestTitle,
              style: const TextStyle(color: Colors.grey, fontSize: 16),
            ),

            const SizedBox(height: 30), // 전체를 위로 올리기 위해 간격을 좁게 조절

            // 3. 메인 제목 섹션
            Column(
              children: [
                Text(
                  widget.taskTitle,
                  style: const TextStyle(
                    fontSize: 36,
                    fontWeight: FontWeight.bold,
                    letterSpacing: -1.0,
                  ),
                ),
                const SizedBox(height: 8),
                const Text(
                  "얼른 진행하세요!",
                  style: TextStyle(
                    fontSize: 22,
                    fontWeight: FontWeight.w500,
                    color: Color(0xFF4A4A4A),
                  ),
                ),
              ],
            ),

            const SizedBox(height: 70), // 타이머와의 간격

            // 4. 메인 타이머 섹션
            Stack(
              alignment: Alignment.center,
              children: [
                SizedBox(
                  width: 260, // 약간 크기 조절
                  height: 260,
                  child: CircularProgressIndicator(
                    value: _progress,
                    strokeWidth: 15,
                    backgroundColor: const Color(0xFFE3E3E3),
                    valueColor: const AlwaysStoppedAnimation<Color>(Color(0xFF6389E9)),
                    strokeCap: StrokeCap.round,
                  ),
                ),
                Column(
                  children: [
                    const Icon(Icons.timer_outlined, size: 45, color: Color(0xFF6389E9)),
                    const SizedBox(height: 5),
                    Text(
                      "${(_remainingSeconds ~/ 60).toString().padLeft(2, '0')}:${(_remainingSeconds % 60).toString().padLeft(2, '0')}",
                      style: const TextStyle(
                        fontSize: 48,
                        fontWeight: FontWeight.bold,
                        fontFamily: 'monospace',
                      ),
                    ),
                  ],
                ),
              ],
            ),

            // 5. 하단 버튼을 '나머지 공간'의 하단에 고정
            const Spacer(),

            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 40),
              child: SizedBox(
                width: double.infinity,
                height: 70,
                child: ElevatedButton(
                  onPressed: () => Navigator.pop(context),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF6389E9),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(50),
                    ),
                    elevation: 5,
                    shadowColor: const Color(0xFF6389E9).withOpacity(0.4),
                  ),
                  child: const Text(
                    "다음 퀘스트로!",
                    style: TextStyle(
                      fontSize: 30,
                      fontWeight: FontWeight.bold,
                      color: Colors.white,
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