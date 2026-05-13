import 'package:flutter/material.dart';
import 'dart:async';

class OxQuizScreen extends StatefulWidget {
  final VoidCallback? onBack;
  final List<Map<String, dynamic>> quizzes;

  const OxQuizScreen({super.key, this.onBack, required this.quizzes});

  @override
  State<OxQuizScreen> createState() => _OxQuizScreenState();
}

class _OxQuizScreenState extends State<OxQuizScreen> {
  int _seconds = 60;
  Timer? _timer;
  String? _selected;
  int _currentIndex = 0;

  @override
  void initState() {
    super.initState();
    _startTimer();
  }

  void _startTimer() {
    _timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (_seconds == 0) {
        timer.cancel();
      } else {
        setState(() {
          _seconds--;
        });
      }
    });
  }

  void _nextQuestion() {
    _timer?.cancel();
    setState(() {
      _seconds = 60;
      _selected = null;
      if (_currentIndex < widget.quizzes.length - 1) {
        _currentIndex++;
      }
    });
    _startTimer();
  }

  String get _timerText {
    final m = (_seconds ~/ 60).toString().padLeft(2, '0');
    final s = (_seconds % 60).toString().padLeft(2, '0');
    return '$m:$s';
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final currentQuiz = widget.quizzes.isNotEmpty ? widget.quizzes[_currentIndex] : null;

    return Scaffold(
      backgroundColor: const Color(0xFF1586E2),
      body: SafeArea(
        child: Column(
          children: [
            _buildTopBar(),
            Expanded(
              child: Container(
                width: double.infinity,
                padding: const EdgeInsets.all(20),
                decoration: const BoxDecoration(
                  color: Color(0xFF1586E2),
                  borderRadius: BorderRadius.vertical(
                    top: Radius.circular(24),
                  ),
                ),
                child: Column(
                  children: [
                    _buildQuestionCard(currentQuiz),
                    const SizedBox(height: 35),
                    _buildOxButtons(),
                    const SizedBox(height: 25),
                    _buildSelectButton(),
                    const SizedBox(height: 20),
                    _buildExplanationBox(currentQuiz),
                    const SizedBox(height: 25),
                    _buildNextButton(),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTopBar() {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 12, 24, 8),
      child: Row(
        children: [
          AnimatedContainer(
            duration: const Duration(milliseconds: 300),
            width: 110,
            height: 30,
            decoration: BoxDecoration(
              color: const Color(0xFF8AC5F5),
              border: Border.all(color: const Color(0xFF0320FF)),
              borderRadius: BorderRadius.circular(50),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Icon(Icons.timer_outlined, size: 18, color: Colors.black),
                const SizedBox(width: 6),
                Text(
                  _timerText,
                  style: const TextStyle(
                    color: Colors.black,
                    fontSize: 16,
                    fontFamily: 'JejuGothic',
                  ),
                ),
              ],
            ),
          ),
          const Expanded(
            child: Center(
              child: Text(
                'OX 퀴즈!',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 24,
                  fontFamily: 'JejuGothic',
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ),
          GestureDetector(
            onTap: () => Navigator.pop(context),
            child: Container(
              width: 100,
              height: 40,
              alignment: Alignment.centerRight,
              child: const Text(
                '←',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 25,
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildQuestionCard(Map<String, dynamic>? quiz) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(16),
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
          Text(
            quiz?['questionText'] ?? '문제',
            style: const TextStyle(
              color: Colors.black,
              fontSize: 20,
              fontFamily: 'JejuGothic',
            ),
          ),
          const SizedBox(height: 80),
        ],
      ),
    );
  }

  Widget _buildOxButtons() {
    return Row(
      children: [
        Expanded(
          child: _buildChoiceCard(
            label: 'O',
            bgColor: const Color(0xFFA1FF6F),
            borderColor: const Color(0xFF53C516),
            isSelected: _selected == 'O',
          ),
        ),
        const SizedBox(width: 50),
        Expanded(
          child: _buildChoiceCard(
            label: 'X',
            bgColor: const Color(0xFFFF6F6F),
            borderColor: const Color(0xFFFF0000),
            isSelected: _selected == 'X',
          ),
        ),
      ],
    );
  }

  Widget _buildChoiceCard({
    required String label,
    required Color bgColor,
    required Color borderColor,
    required bool isSelected,
  }) {
    return GestureDetector(
      onTap: () {
        setState(() {
          _selected = label;
        });
      },
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 150),
        height: 130,
        decoration: BoxDecoration(
          color: bgColor,
          border: Border.all(
            color: isSelected ? Colors.white : borderColor,
            width: isSelected ? 2.5 : 2,
          ),
          borderRadius: BorderRadius.circular(30),
          boxShadow: [
            BoxShadow(
              color: const Color(0x3F000000),
              blurRadius: isSelected ? 10 : 4,
              offset: const Offset(0, 4),
            ),
          ],
        ),
        child: Center(
          child: label == 'O'
              ? Container(
            width: 90,
            height: 90,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              border: Border.all(color: Colors.white, width: 14),
            ),
          )
              : SizedBox(
            width: 90,
            height: 90,
            child: Stack(
              alignment: Alignment.center,
              children: [
                Transform.rotate(
                  angle: 0.785398,
                  child: Container(
                    width: 16,
                    height: 100,
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(20),
                    ),
                  ),
                ),
                Transform.rotate(
                  angle: -0.785398,
                  child: Container(
                    width: 16,
                    height: 100,
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(20),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildSelectButton() {
    return Container(
      width: 200,
      height: 44,
      decoration: BoxDecoration(
        color: const Color(0xFF8AC5F5),
        border: Border.all(color: const Color(0xFF2F44E1), width: 2),
        borderRadius: BorderRadius.circular(25),
      ),
      child: const Center(
        child: Text(
          '선택 완료',
          style: TextStyle(
            color: Colors.black,
            fontSize: 20,
            fontFamily: 'JejuGothic',
            fontWeight: FontWeight.w600,
          ),
        ),
      ),
    );
  }

  Widget _buildExplanationBox(Map<String, dynamic>? quiz) {
    return Expanded(
      child: Container(
        width: double.infinity,
        padding: const EdgeInsets.all(16),
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
            Text(
              quiz?['explanation'] ?? '해설',
              style: const TextStyle(
                color: Colors.black,
                fontSize: 20,
                fontFamily: 'JejuGothic',
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildNextButton() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 30),
      child: GestureDetector(
        onTap: _nextQuestion,
        child: Container(
          width: double.infinity,
          height: 45,
          decoration: BoxDecoration(
            color: const Color(0xFF8AC5F5),
            border: Border.all(color: const Color(0xFF2F44E1), width: 2),
            borderRadius: BorderRadius.circular(25),
          ),
          child: const Center(
            child: Text(
              '다음 문제',
              style: TextStyle(
                color: Colors.black,
                fontSize: 20,
                fontFamily: 'JejuGothic',
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ),
      ),
    );
  }
}