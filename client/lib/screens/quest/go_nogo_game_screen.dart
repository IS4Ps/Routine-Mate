import 'dart:async';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../view_model/quest/quest_view_model.dart';
import 'package:frontend/models/quest/go_nogo_submit_request_model.dart';

class GoNogoGameScreen extends StatefulWidget {
  const GoNogoGameScreen({super.key});

  @override
  State<GoNogoGameScreen> createState() => _GoNogoGameScreenState();
}

class _GoNogoGameScreenState extends State<GoNogoGameScreen> {
  bool isGameFinished = false;
  int currentIndex = 0;
  bool isShowingStimulus = false;

  Timer? _countdownTimer;
  int _remainingTime = 50;
  final int _totalStartTime = 50;

  List<GoNoGoAnswerModel> userAnswers = [];
  DateTime? stimulusStartTime;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (mounted) {
        _startTimer();
        _startSequence();
      }
    });
  }

  @override
  void dispose() {
    _countdownTimer?.cancel();
    super.dispose();
  }

  void _startTimer() {
    _countdownTimer?.cancel();
    _countdownTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (!mounted) {
        timer.cancel();
        return;
      }
      if (_remainingTime > 0) {
        setState(() => _remainingTime--);
      } else {
        _handleGameEnd();
      }
    });
  }

  Future<void> _startSequence() async {
    final viewModel = Provider.of<QuestViewModel>(context, listen: false);
    final data = viewModel.goNoGoData;

    if (data == null) return;

    for (int i = 0; i < data.stimuli.length; i++) {
      if (!mounted || isGameFinished) break;

      setState(() {
        currentIndex = i;
        isShowingStimulus = true;
        stimulusStartTime = DateTime.now();
      });

      await Future.delayed(Duration(milliseconds: data.timeLimit));

      if (userAnswers.length <= i) {
        userAnswers.add(GoNoGoAnswerModel(
          index: data.stimuli[i].index,
          tapped: false,
          responseTime: 0,
        ));
      }

      if (!mounted || isGameFinished) break;
      setState(() => isShowingStimulus = false);

      await Future.delayed(const Duration(milliseconds: 500));
    }

    if (mounted && !isGameFinished) _handleGameEnd();
  }

  Future<void> _handleGameEnd() async {
    _countdownTimer?.cancel();
    if (isGameFinished) return;

    final viewModel = Provider.of<QuestViewModel>(context, listen: false);
    await viewModel.submitGoNoGoGame(userAnswers);

    if (mounted) {
      setState(() => isGameFinished = true);
    }
  }

  @override
  Widget build(BuildContext context) {
    final viewModel = Provider.of<QuestViewModel>(context);
    final data = viewModel.goNoGoData;
    final result = viewModel.goNoGoResult;

    return Scaffold(
      backgroundColor: const Color(0xFFECF2F8), // 배경색을 Stroop과 통일
      body: SafeArea(
        child: data == null
            ? const Center(child: CircularProgressIndicator(color: Colors.black))
            : AnimatedSwitcher(
          duration: const Duration(milliseconds: 300),
          child: isGameFinished
              ? _buildResultUI(result)
              : _buildGameUI(data),
        ),
      ),
    );
  }

  // --- 게임 중 UI ---
  Widget _buildGameUI(dynamic data) {
    final currentStimulus = data.stimuli[currentIndex];

    return Column(
      children: [
        const SizedBox(height: 40),
        const Text('Go/No-Go', style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold)),
        const SizedBox(height: 20),
        _buildTimerBar(),
        const SizedBox(height: 30),
        Center(
          child: Container(
            width: 340,
            padding: const EdgeInsets.all(24),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(25),
              boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.05), blurRadius: 15, offset: const Offset(0, 5))],
            ),
            child: Column(
              children: [
                _buildGameHeader(),
                const SizedBox(height: 20),
                Container(
                  width: double.infinity,
                  height: 240,
                  decoration: BoxDecoration(
                    color: const Color(0xFFF1F3F5),
                    borderRadius: BorderRadius.circular(15),
                    border: Border.all(color: const Color(0xFFDEE2E6)),
                  ),
                  child: Center(
                    child: isShowingStimulus
                        ? _getStimulusWidget(currentStimulus.image)
                        : const SizedBox.shrink(),
                  ),
                ),
                const SizedBox(height: 30),
                ElevatedButton(
                  onPressed: () {
                    if (isShowingStimulus && userAnswers.length <= currentIndex && stimulusStartTime != null) {
                      final responseTime = DateTime.now().difference(stimulusStartTime!).inMilliseconds;
                      userAnswers.add(GoNoGoAnswerModel(
                        index: currentStimulus.index,
                        tapped: true,
                        responseTime: responseTime,
                      ));
                    }
                  },
                  style: _buttonStyle(),
                  child: const Text('터치!', style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }

  // --- 결과 UI: 버튼 추가 및 레이아웃 수정 ---
  Widget _buildResultUI(dynamic result) {
    if (result == null) return const Center(child: CircularProgressIndicator());

    return Center(
      child: SingleChildScrollView(
        child: Column(
          children: [
            const Text('Go/No-Go', style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold)),
            const SizedBox(height: 25),
            Container(
              width: 340,
              padding: const EdgeInsets.all(24),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(30),
                boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.1), blurRadius: 20, offset: const Offset(0, 10))],
              ),
              child: Column(
                children: [
                  const Text('미니게임 성공!', style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 20),
                  _buildGrayBox(
                    child: Column(
                      children: [
                        const Text('게임 결과', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
                        Text('수고하셨습니다. 총 ${result.totalCount}문항에 대한 결과입니다.', style: const TextStyle(fontSize: 11, color: Colors.black54)),
                        const SizedBox(height: 15),
                        _resultRow('맞은 개수', '${result.correctCount}개', color: Colors.green),
                        _resultRow('틀린 개수', '${result.wrongCount}개', color: Colors.red),
                        _resultRow('정확도', '${result.accuracy}%'),
                        _resultRow('점수', '${result.score}점'),
                      ],
                    ),
                  ),
                  const SizedBox(height: 15),
                  Row(
                    children: [
                      Expanded(
                        child: _buildGrayBox(
                          child: Column(
                            children: [
                              const Text('평균 반응속도', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
                              const SizedBox(height: 8),
                              Text('${result.avgResponseTime.toInt()}ms', style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w900)),
                            ],
                          ),
                        ),
                      ),
                      const SizedBox(width: 15),
                      Expanded(
                        child: _buildGrayBox(
                          child: Column(
                            children: [
                              const Text('획득 보상', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
                              const SizedBox(height: 8),
                              Text('+${result.rewardGold} gold', style: const TextStyle(fontSize: 13, fontWeight: FontWeight.bold)),
                              Text('+${result.statStrengthGain} strength', style: const TextStyle(fontSize: 13, fontWeight: FontWeight.bold)),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 25),

                  // ★ 버튼 2개(검은색/파란색)로 수정
                  _actionButton('난이도 올려서 다시하기', Colors.black, () {
                    // 난이도 상승 로직 (필요시 구현)
                  }),
                  const SizedBox(height: 12),
                  _actionButton('지금 난이도로 다시하기', const Color(0xFF5C92E1), () {
                    if (mounted) {
                      setState(() {
                        isGameFinished = false;
                        currentIndex = 0;
                        userAnswers = [];
                        _remainingTime = _totalStartTime;
                      });
                      _startTimer();
                      _startSequence();
                    }
                  }),
                  const SizedBox(height: 8),
                  TextButton(
                    onPressed: () => Navigator.pop(context),
                    child: const Text('홈 화면으로 돌아가기', style: TextStyle(color: Colors.grey, fontSize: 14, fontWeight: FontWeight.w500)),
                  )
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  // --- 헬퍼 위젯들 ---
  Widget _buildTimerBar() {
    return SizedBox(
      width: 320,
      child: Column(
        children: [
          Text('남은 시간 : $_remainingTime초', style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w500)),
          const SizedBox(height: 8),
          ClipRRect(
            borderRadius: BorderRadius.circular(10),
            child: LinearProgressIndicator(
              value: _remainingTime / _totalStartTime,
              backgroundColor: const Color(0xFFD9D9D9),
              valueColor: const AlwaysStoppedAnimation<Color>(Colors.black),
              minHeight: 10,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildGameHeader() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        GestureDetector(
          onTap: () => Navigator.pop(context),
          child: const Text('그만하기', style: TextStyle(color: Colors.grey, fontSize: 14)),
        ),
        const Column(
          children: [
            Text('Level - 1', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
            Text(
                '(초록 원이 나오면 터치!)',
                style: TextStyle(fontSize: 10, color: Colors.grey, fontWeight: FontWeight.w700)
            ),
          ],
        ),
        const Text('점수 : 0', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
      ],
    );
  }

  ButtonStyle _buttonStyle() {
    return ElevatedButton.styleFrom(
      backgroundColor: const Color(0xFF5C92E1),
      foregroundColor: Colors.white,
      minimumSize: const Size(double.infinity, 60),
      elevation: 0,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
    );
  }

  Widget _getStimulusWidget(String imageName) {
    switch (imageName) {
      case 'monster_green':
        return const Icon(Icons.sentiment_very_satisfied, size: 140, color: Colors.green);
      case 'bomb_red':
        return const Icon(Icons.dangerous, size: 140, color: Colors.red);
      default:
        return const Icon(Icons.help_outline, size: 140, color: Colors.grey);
    }
  }

  Widget _buildGrayBox({required Widget child}) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.symmetric(vertical: 18, horizontal: 15),
      decoration: BoxDecoration(color: const Color(0xFFECF2F8), borderRadius: BorderRadius.circular(20)),
      child: child,
    );
  }

  Widget _resultRow(String label, String value, {Color? color}) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 2),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text('$label : ', style: const TextStyle(fontSize: 13, color: Colors.black54)),
          Text(value, style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: color)),
        ],
      ),
    );
  }

  Widget _actionButton(String text, Color bgColor, VoidCallback onTap) {
    return SizedBox(
      width: double.infinity,
      height: 58,
      child: ElevatedButton(
        onPressed: onTap,
        style: ElevatedButton.styleFrom(
          backgroundColor: bgColor,
          foregroundColor: Colors.white,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
          elevation: 0,
        ),
        child: Text(text, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
      ),
    );
  }
}