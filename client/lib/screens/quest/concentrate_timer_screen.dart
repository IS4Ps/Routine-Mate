import 'package:flutter/material.dart';
import 'dart:async';

class ConcentrateTimerScreen extends StatefulWidget {
  final int totalSeconds;

  const ConcentrateTimerScreen({super.key, required this.totalSeconds});

  @override
  State<ConcentrateTimerScreen> createState() => _ConcentrateTimerScreenState();
}

class _ConcentrateTimerScreenState extends State<ConcentrateTimerScreen> {
  late int _remainingSeconds;
  Timer? _timer;
  bool _isPaused = false; // 타이머 일시정지 상태 관리 변수
  double _dragPosition = 0;
  final double _sliderWidth = 380;
  final double _sliderHeight = 85;
  final double _handleSize = 70;

  @override
  void initState() {
    super.initState();
    _remainingSeconds = widget.totalSeconds;
    _startTimer();
  }

  void _startTimer() {
    _timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      // 일시정지 상태가 아닐 때만 시간이 줄어듦
      if (!_isPaused) {
        if (_remainingSeconds > 0) {
          setState(() => _remainingSeconds--);
        } else {
          _timer?.cancel();
          // 시간이 다 되었을 때의 로직 (예: 종료 알림)을 여기에 추가할 수 있습니다.
        }
      }
    });
  }

  // 종료 확인 팝업 함수
  void _showExitConfirmDialog() {
    // 슬라이더가 가득 차는 위치 계산
    final double maxDragRange = _sliderWidth - _handleSize - 14.0;

    setState(() {
      _isPaused = true;
      _dragPosition = maxDragRange; // 팝업이 뜰 때 배경색을 끝까지 채움
    });

    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) {
        return Dialog(
          backgroundColor: Colors.white,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(25),
            side: const BorderSide(
              color: Color(0xFFCA0E0E),
              width: 4.0,
            ),
          ),
          child: SizedBox(
            width: 340,
            height: 320,
            child: Padding(
              padding: const EdgeInsets.only(left: 20, right: 20, top: 15, bottom: 15),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.start,
                children: [
                  const Icon(Icons.warning_rounded, color: Color(0xFFCA0E0E), size: 70),
                  Column(
                    children: [
                      const Text(
                        "정말 포기하시겠어요?",
                        style: TextStyle(
                            color: Color(0xFFCA0E0E),
                            fontSize: 20,
                            fontWeight: FontWeight.bold
                        ),
                      ),
                      const SizedBox(height: 8),
                      const Text(
                        "지금 종료하면 획득할 수 있는 경험치와\n보상이 모두 사라집니다.\n그래도 종료하시겠습니까?\n(잃는 경험치 100xp)",
                        textAlign: TextAlign.center,
                        style: TextStyle(
                            color: Colors.black,
                            fontSize: 14,
                            height: 1.3,
                            fontWeight: FontWeight.w700
                        ),
                      ),
                    ],
                  ),

                  const Spacer(),

                  Row(
                    children: [
                      Expanded(
                        child: TextButton(
                          onPressed: () {
                            Navigator.pop(context);
                            setState(() {
                              _isPaused = false;
                              _dragPosition = 0; // 취소 버튼을 누르면 슬라이더를 다시 0으로 복구
                            });
                          },
                          style: TextButton.styleFrom(
                            padding: const EdgeInsets.symmetric(vertical: 5),
                            backgroundColor: const Color(0xFF8E8E8E),
                            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
                          ),
                          child: const Text(
                            "취소\n(계속 집중)",
                            textAlign: TextAlign.center,
                            style: TextStyle(color: Colors.white, fontSize: 14),
                          ),
                        ),
                      ),
                      const SizedBox(width: 10),
                      Expanded(
                        child: TextButton(
                          onPressed: () {
                            Navigator.pop(context);
                            Navigator.pop(context);
                          },
                          style: TextButton.styleFrom(
                            padding: const EdgeInsets.symmetric(vertical: 5),
                            backgroundColor: const Color(0xFFE57373),
                            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
                          ),
                          child: const Text(
                            "종료하기\n(패널티 감수)",
                            textAlign: TextAlign.center,
                            style: TextStyle(color: Colors.white, fontSize: 14, fontWeight: FontWeight.bold),
                          ),
                        ),
                      ),
                    ],
                  ),

                  const SizedBox(height: 20),

                ],
              ),
            ),
          ),
        );
      },
    );
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  String _formatTime(int seconds) {
    int m = seconds ~/ 60;
    int s = seconds % 60;
    return "${m.toString().padLeft(2, '0')}:${s.toString().padLeft(2, '0')}";
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      body: SafeArea(
        child: Column(
          children: [
            const SizedBox(height: 50),
            Text(
              _formatTime(_remainingSeconds),
              style: const TextStyle(
                fontSize: 100,
                fontWeight: FontWeight.bold,
                color: Colors.white,
              ),
            ),
            const SizedBox(height: 60),
            const Text("집중 중...", style: TextStyle(fontSize: 22, color: Colors.white)),
            const SizedBox(height: 12),
            const Text("전화 수신만 가능합니다", style: TextStyle(fontSize: 18, color: Colors.white70)),
            const Spacer(flex: 3),
            Center(child: _buildSlideToUnlock()),
            const SizedBox(height: 70),
          ],
        ),
      ),
    );
  }

  Widget _buildSlideToUnlock() {
    final double radius = 50.0;
    final double horizontalPadding = 7.0;
    final double sliderHeight = 85.0;

    double maxDragRange = _sliderWidth - _handleSize - (horizontalPadding * 2);

    return Container(
      width: _sliderWidth,
      height: sliderHeight,
      decoration: BoxDecoration(
        color: const Color(0xFF5A3532),
        borderRadius: BorderRadius.circular(radius),
      ),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(radius),
        child: Stack(
          alignment: Alignment.centerLeft,
          children: [
            Positioned(
              left: 0,
              top: 0,
              bottom: 0,
              width: horizontalPadding + _dragPosition + (_handleSize * 1.6),
              child: Container(
                decoration: const BoxDecoration(
                  color: Color(0xFFE57373),
                  borderRadius: BorderRadius.only(
                    topRight: Radius.circular(50),
                    bottomRight: Radius.circular(50),
                  ),
                ),
              ),
            ),
            const Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                    "밀어서 집중 종료",
                    style: TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold),
                  ),
                  Text(
                    "(패널티 발생)",
                    style: TextStyle(color: Colors.white70, fontSize: 13),
                  ),
                ],
              ),
            ),
            Positioned(
              top: (sliderHeight - _handleSize) / 2,
              left: horizontalPadding + _dragPosition,
              child: GestureDetector(
                onHorizontalDragUpdate: (details) {
                  setState(() {
                    _dragPosition += details.delta.dx;
                    _dragPosition = _dragPosition.clamp(0.0, maxDragRange);
                  });
                },
                onHorizontalDragEnd: (details) {
                  if (_dragPosition > maxDragRange * 0.8) {
                    // setState(() => _dragPosition = 0);
                    _showExitConfirmDialog(); // 여기서 일시정지 로직 실행됨
                  } else {
                    setState(() => _dragPosition = 0);
                  }
                },
                child: Container(
                  width: _handleSize,
                  height: _handleSize,
                  decoration: const BoxDecoration(
                    color: Colors.white,
                    shape: BoxShape.circle,
                    boxShadow: [
                      BoxShadow(color: Colors.black26, blurRadius: 4, offset: Offset(2, 0))
                    ],
                  ),
                  child: const Icon(
                    Icons.arrow_forward_ios_rounded,
                    color: Color(0xFFE57373),
                    size: 45,
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