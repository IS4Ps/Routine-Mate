import 'dart:async';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../view_model/quest/quest_view_model.dart';
import 'package:frontend/models/quest/stroop_submit_request_model.dart';

class StroopGameScreen extends StatefulWidget {
  const StroopGameScreen({super.key});

  @override
  State<StroopGameScreen> createState() => _StroopGameScreenState();
}

class _StroopGameScreenState extends State<StroopGameScreen> {
  bool isGameFinished = false;
  int _remainingTime = 50;
  final int _totalStartTime = 50;
  Timer? _countdownTimer;

  final List<StroopAnswerModel> _userAnswers = [];
  DateTime? _questionStartTime;
  int _currentQuestionIndex = 1;

  // 백엔드 기준 문항 수 설정
  final int _maxQuestions = 20;

  final Map<String, Color> gameColors = {
    '빨강': const Color(0xFFEF5350),
    '파랑': const Color(0xFF42A5F5),
    '초록': const Color(0xFF8BC34A),
    '노랑': const Color(0xFFFFEB3B),
  };

  late String currentText = "";
  late Color currentTextColor = Colors.black;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _loadQuestionFromServer();
      _startTimer();
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
      if (_remainingTime > 0) {
        setState(() => _remainingTime--);
      } else {
        // 시간 초과 시 종료
        _finishGame();
      }
    });
  }

  void _loadQuestionFromServer() {
    final viewModel = Provider.of<QuestViewModel>(context, listen: false);
    final stimuli = viewModel.stroopData?.stimuli;

    // 현재 인덱스가 최대 문항 수를 넘지 않았을 때만 로드
    if (stimuli != null && stimuli.isNotEmpty && _currentQuestionIndex <= _maxQuestions) {
      final indexToLoad = (_currentQuestionIndex - 1) % stimuli.length;
      final currentStimulus = stimuli[indexToLoad];

      setState(() {
        currentText = currentStimulus.word;
        currentTextColor = gameColors[currentStimulus.inkColor] ?? Colors.black;
        _questionStartTime = DateTime.now();
      });
    }
  }

  void _handleAnswer(String selectedLabel) {
    if (_questionStartTime == null || isGameFinished) return;

    final responseTime = DateTime.now().difference(_questionStartTime!).inMilliseconds;

    _userAnswers.add(StroopAnswerModel(
      index: _currentQuestionIndex,
      selectedColor: selectedLabel,
      responseTime: responseTime,
    ));

    // 20번째 문제를 풀었으면 즉시 종료
    if (_currentQuestionIndex >= _maxQuestions) {
      _finishGame();
    } else {
      _currentQuestionIndex++;
      _loadQuestionFromServer();
    }
  }

  void _finishGame() async {
    if (isGameFinished) return; // 중복 실행 방지

    _countdownTimer?.cancel();
    final viewModel = Provider.of<QuestViewModel>(context, listen: false);

    bool success = await viewModel.submitStroopGame(_userAnswers);

    if (mounted) {
      if (success) {
        setState(() => isGameFinished = true);
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(viewModel.message)),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final viewModel = Provider.of<QuestViewModel>(context);

    return Scaffold(
      backgroundColor: const Color(0xFFECF2F8),
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios, color: Colors.black),
          onPressed: () => Navigator.pop(context),
        ),
      ),
      body: SafeArea(
        child: viewModel.isLoading
            ? const Center(child: CircularProgressIndicator(color: Colors.black))
            : AnimatedSwitcher(
          duration: const Duration(milliseconds: 300),
          child: isGameFinished
              ? _buildResultUI(viewModel)
              : _buildGameUI(),
        ),
      ),
    );
  }

  Widget _buildGameUI() {
    return Column(
      children: [
        const SizedBox(height: 10),
        const Text('단어 색깔 구별하기', style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold)),
        const SizedBox(height: 15),

        SizedBox(
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
        ),
        const SizedBox(height: 30),

        Center(
          child: Container(
            width: 340,
            padding: const EdgeInsets.all(24),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(25),
              boxShadow: [
                BoxShadow(color: Colors.black.withOpacity(0.05), blurRadius: 15, offset: const Offset(0, 5))
              ],
            ),
            child: Column(
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    GestureDetector(
                      onTap: () => Navigator.pop(context),
                      child: const Text('그만하기', style: TextStyle(color: Colors.grey, fontSize: 14)),
                    ),
                    const Text('Level - 1', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                    Text('문항 : $_currentQuestionIndex / $_maxQuestions', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                  ],
                ),
                const SizedBox(height: 20),
                Container(
                  width: double.infinity,
                  height: 160,
                  decoration: BoxDecoration(
                    color: const Color(0xFFF1F3F5),
                    borderRadius: BorderRadius.circular(15),
                    border: Border.all(color: const Color(0xFFDEE2E6)),
                  ),
                  child: Center(
                    child: Text(
                      currentText,
                      style: TextStyle(fontSize: 48, fontWeight: FontWeight.bold, color: currentTextColor),
                    ),
                  ),
                ),
                const SizedBox(height: 35),
                RichText(
                  text: TextSpan(
                    style: const TextStyle(fontSize: 12, color: Colors.black54),
                    children: [
                      const TextSpan(text: '아래에서 '),
                      TextSpan(
                        text: '글자 색',
                        style: TextStyle(fontWeight: FontWeight.w900, color: Colors.black.withOpacity(0.85)),
                      ),
                      const TextSpan(text: '에 해당하는 칸을 누르세요'),
                    ],
                  ),
                ),
                const SizedBox(height: 20),
                GridView.count(
                  shrinkWrap: true,
                  crossAxisCount: 2,
                  childAspectRatio: 2.2,
                  mainAxisSpacing: 12,
                  crossAxisSpacing: 15,
                  physics: const NeverScrollableScrollPhysics(),
                  children: gameColors.entries.map((e) => _buildColorSelectionBtn(e.key, e.value)).toList(),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildResultUI(QuestViewModel viewModel) {
    final result = viewModel.stroopResult;
    if (result == null) return const Center(child: Text("결과 데이터 로드 중..."));

    return Center(
      child: SingleChildScrollView(
        child: Column(
          children: [
            const Text('단어 색깔 구별하기', style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold)),
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
                        // 서버 응답값 totalCount를 신뢰하여 출력
                        Text(
                          '수고하셨습니다. 총 ${result.totalCount}문항에 대한 결과입니다.',
                          style: const TextStyle(fontSize: 11, color: Colors.black54),
                        ),
                        const SizedBox(height: 15),
                        _resultRow('맞은 개수', '${result.correctCount}개', color: Colors.green),
                        _resultRow('틀린 개수', '${result.wrongCount}개', color: Colors.red),
                        _resultRow('정확도', '${result.accuracy.toStringAsFixed(1)}%'),
                        _resultRow('점수', '${result.score}점'),
                      ],
                    ),
                  ),
                  const SizedBox(height: 15),
                  Row(
                    children: [
                      Expanded(child: _buildGrayBox(child: _buildInfoItem('평균 반응속도', '${result.avgResponseTime.toInt()}ms'))),
                      const SizedBox(width: 15),
                      Expanded(child: _buildGrayBox(child: _buildInfoItem('획득 보상', '+${result.rewardGold} gold\n+${result.statCreativityGain} creativity'))),
                    ],
                  ),
                  const SizedBox(height: 25),
                  _actionButton('난이도 올려서 다시하기', Colors.black, () {}),
                  const SizedBox(height: 12),
                  _actionButton('지금 난이도로 다시하기', const Color(0xFF5C92E1), () {
                    setState(() {
                      isGameFinished = false;
                      _remainingTime = _totalStartTime;
                      _userAnswers.clear();
                      _currentQuestionIndex = 1;
                    });
                    _startTimer();
                    _loadQuestionFromServer();
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

  // 버튼 및 기타 헬퍼 함수는 동일
  Widget _buildColorSelectionBtn(String label, Color color) {
    return ElevatedButton(
      onPressed: () => _handleAnswer(label),
      style: ElevatedButton.styleFrom(
        backgroundColor: Colors.white,
        foregroundColor: Colors.black,
        elevation: 0,
        side: const BorderSide(color: Color(0xFFDEE2E6)),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        padding: const EdgeInsets.symmetric(horizontal: 10),
      ),
      child: Row(
        children: [
          Container(width: 30, height: 30, decoration: BoxDecoration(color: color, borderRadius: BorderRadius.circular(6))),
          const SizedBox(width: 12),
          Text(label, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
        ],
      ),
    );
  }

  Widget _buildGrayBox({required Widget child}) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.symmetric(vertical: 18, horizontal: 15),
      decoration: BoxDecoration(color: const Color(0xFFECF2F8), borderRadius: BorderRadius.circular(20)),
      child: child,
    );
  }

  Widget _buildInfoItem(String label, String value) {
    return Column(
      children: [
        Text(label, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
        const SizedBox(height: 8),
        Text(value, textAlign: TextAlign.center, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w900)),
      ],
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