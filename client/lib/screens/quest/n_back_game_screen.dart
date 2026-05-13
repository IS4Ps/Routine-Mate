import 'dart:async';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../view_model/quest/quest_view_model.dart';
import '../../models/quest/n_back_submit_request_model.dart';


class NBackGameScreen extends StatefulWidget {
  const NBackGameScreen({super.key});

  @override
  State<NBackGameScreen> createState() => _NBackGameScreenState();
}

class _NBackGameScreenState extends State<NBackGameScreen> {
  bool isGameFinished = false;
  int currentIndex = 0;
  bool isShowingStimulus = false;

  // 사용자의 실시간 응답 기록용 (자극 index : 클릭여부)
  Map<int, bool> userResponses = {};

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) => _startSequence());
  }

  Future<void> _startSequence() async {
    final viewModel = Provider.of<QuestViewModel>(context, listen: false);
    final data = viewModel.nBackData;

    if (data == null) return;

    userResponses.clear();

    for (int i = 0; i < data.stimuli.length; i++) {
      if (!mounted) return;

      setState(() {
        currentIndex = i;
        isShowingStimulus = true;
      });

      await Future.delayed(Duration(milliseconds: data.displayTime));

      if (!mounted) return;
      setState(() => isShowingStimulus = false);

      await Future.delayed(Duration(milliseconds: data.intervalTime));
    }

    // --- 게임 종료 후 API 제출 로직 ---
    if (mounted) {
      List<NBackAnswerModel> finalAnswers = [];
      for (var stimulus in data.stimuli) {
        // N 이하 인덱스는 제외 (이미지 명세 규칙)
        if (stimulus.index <= data.nLevel) continue;

        finalAnswers.add(NBackAnswerModel(
          index: stimulus.index,
          matched: userResponses[stimulus.index] ?? false,
        ));
      }

      // 제출 API 호출
      bool success = await viewModel.submitNBackGame(finalAnswers);

      if (success) {
        setState(() => isGameFinished = true);
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("결과 저장에 실패했습니다.")),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final viewModel = Provider.of<QuestViewModel>(context);
    final data = viewModel.nBackData;

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
      body: viewModel.isLoading
          ? const Center(child: CircularProgressIndicator())
          : isGameFinished
          ? _buildResultUI(viewModel)
          : _buildGameUI(data),
    );
  }

  Widget _buildGameUI(dynamic data) {
    if (data == null) return const Center(child: Text("데이터를 불러오지 못했습니다."));
    final currentStimulusIndex = data.stimuli[currentIndex].index;

    return Center(
      child: Column(
        children: [
          const Text("도형 순서 기억하기", style: TextStyle(fontSize: 26, fontWeight: FontWeight.bold)),
          const SizedBox(height: 20),
          Container(
            width: 340,
            padding: const EdgeInsets.symmetric(vertical: 30, horizontal: 20),
            decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(25),
                boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.05), blurRadius: 10, offset: const Offset(0, 5))]
            ),
            child: Column(
              children: [
                Text("Level - ${data.nLevel}", style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                Text("(${data.nLevel}단계 전과 같으면 클릭!)", style: const TextStyle(color: Colors.grey, fontSize: 13)),
                const SizedBox(height: 15),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    TextButton(
                      onPressed: () => Navigator.pop(context),
                      child: const Text("그만하기", style: TextStyle(color: Colors.grey, fontWeight: FontWeight.w600)),
                    ),
                    Text("진행도 : ${currentIndex + 1} / ${data.totalCount}", style: const TextStyle(fontWeight: FontWeight.w500)),
                  ],
                ),
                const SizedBox(height: 20),
                Container(
                  width: 180, height: 180,
                  decoration: BoxDecoration(color: const Color(0xFFE9ECEF), borderRadius: BorderRadius.circular(15)),
                  child: isShowingStimulus
                      ? Center(child: _getStimulusWidget(data.stimuli[currentIndex].value))
                      : null,
                ),
                const SizedBox(height: 40),
                SizedBox(
                  width: double.infinity, height: 60,
                  child: ElevatedButton(
                    onPressed: !isShowingStimulus ? null : () {
                      setState(() {
                        userResponses[currentStimulusIndex] = true;
                      });
                    },
                    style: ElevatedButton.styleFrom(
                      backgroundColor: (userResponses[currentStimulusIndex] ?? false)
                          ? const Color(0xFF4A76B8)
                          : const Color(0xFF5C92E1),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
                    ),
                    child: const Text("일치!", style: TextStyle(fontSize: 20, color: Colors.white, fontWeight: FontWeight.bold)),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  // 3. 결과 UI (처음 보내주신 명세 디자인 반영)
  Widget _buildResultUI(QuestViewModel viewModel) {
    final result = viewModel.nBackResult; // 서버에서 받은 실제 결과 데이터
    if (result == null) return const Center(child: Text("결과 데이터를 불러올 수 없습니다."));

    return Center(
      child: SingleChildScrollView(
        child: Column(
          children: [
            const Text("도형 순서 기억하기", style: TextStyle(fontSize: 26, fontWeight: FontWeight.bold)),
            const SizedBox(height: 12),
            Container(
              width: 340,
              padding: const EdgeInsets.all(25),
              decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(30),
                  boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.1), blurRadius: 15, offset: const Offset(0, 10))]
              ),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  const Text("미니게임 성공!", style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 12),
                  Container(
                    width: double.infinity,
                    padding: const EdgeInsets.symmetric(vertical: 20, horizontal: 15),
                    decoration: BoxDecoration(
                        color: const Color(0xFFECF2F8),
                        borderRadius: BorderRadius.circular(20),
                        border: Border.all(color: Colors.black.withOpacity(0.05))
                    ),
                    child: Column(
                      children: [
                        const Text("게임 결과", style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                        Text("수고하셨습니다. 총 ${result.totalCount}문항에 대한 결과입니다.", style: const TextStyle(fontSize: 11, color: Colors.black54)),
                        const SizedBox(height: 15),
                        // 실제 서버 데이터(correctCount) 바인딩
                        _buildDetailResult("정답", "${result.correctCount}개", const Color(0xFF8CD85A), "일치할 때 누름 + 불일치일 때 안 누름"),
                        const SizedBox(height: 12),
                        // 실제 서버 데이터(wrongCount) 바인딩
                        _buildDetailResult("오답", "${result.wrongCount}개", Colors.redAccent, "불일치인데 누름 + 일치인데 안 누름"),
                      ],
                    ),
                  ),
                  const SizedBox(height: 12),
                  Container(
                    width: double.infinity,
                    padding: const EdgeInsets.all(15),
                    decoration: BoxDecoration(color: const Color(0xFFF1F3F7), borderRadius: BorderRadius.circular(15)),
                    child: Column(
                      children: [
                        const Text("획득 보상", style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold)),
                        const SizedBox(height: 8),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            // 실제 서버 보상 데이터(rewardGold, statIntelligenceGain) 바인딩
                            Text("+${result.rewardGold} gold", style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.orange)),
                            Text("+${result.statIntelligenceGain} exp", style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.blueAccent)),
                          ],
                        )
                      ],
                    ),
                  ),
                  const SizedBox(height: 16),
                  // 버튼 액션들은 필요에 따라 수정 가능합니다.
                  _buildActionBtn("난이도 올려서 다시하기", Colors.black, () {
                    Navigator.pop(context); // 미니게임 선택창으로 돌아가서 레벨 선택
                  }),
                  const SizedBox(height: 12),
                  _buildActionBtn("지금 난이도로 다시하기", const Color(0xFF5C92E1), () {
                    setState(() {
                      isGameFinished = false;
                      currentIndex = 0;
                    });
                    _startSequence(); // 게임 시퀀스 재실행
                  }),
                  const SizedBox(height: 6),
                  TextButton(
                    onPressed: () => Navigator.pop(context),
                    child: const Text("홈으로 돌아가기", style: TextStyle(color: Colors.grey, fontSize: 13)),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  // 기존 헬퍼 메서드 유지
  Widget _buildDetailResult(String title, String value, Color color, String description) {
    return Column(
      children: [
        Text(title, style: const TextStyle(fontSize: 13, color: Colors.black54)),
        Text(value, style: TextStyle(fontSize: 26, fontWeight: FontWeight.w900, color: color)),
        Text(description, style: const TextStyle(fontSize: 10, color: Colors.black38)),
      ],
    );
  }

  Widget _buildActionBtn(String text, Color color, VoidCallback onTap) {
    return SizedBox(
      width: double.infinity,
      height: 55,
      child: ElevatedButton(
        onPressed: onTap,
        style: ElevatedButton.styleFrom(
            backgroundColor: color,
            elevation: 0,
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15))
        ),
        child: Text(text, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 15)),
      ),
    );
  }

  Widget _getStimulusWidget(String value) {
    switch (value) {
      case 'apple': return const Icon(Icons.favorite, size: 100, color: Color(0xFFE17D7D));
      case 'banana': return const Icon(Icons.circle, size: 100, color: Color(0xFFE17D7D));
      case 'clover': return const Icon(Icons.eco, size: 100, color: Color(0xFFE17D7D));
      default: return const Icon(Icons.help_outline, size: 100, color: Color(0xFFE17D7D));
    }
  }
}