import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../view_model/quest/quest_view_model.dart';

class SpeechBubble extends StatefulWidget {
  const SpeechBubble({super.key});

  @override
  State<SpeechBubble> createState() => _SpeechBubbleState();
}

class _SpeechBubbleState extends State<SpeechBubble> {
  int? selectedIndex;
  int? selectedDetailIndex;
  bool isDetailView = false;

  // 1단계: 메인 기분 데이터
  final List<Map<String, dynamic>> moods = [
    {'emoji': '😆', 'label': '매우 좋음', 'sub': '신나요', 'color': const Color(0xFFFFE169)},
    {'emoji': '😊', 'label': '좋음', 'sub': '뿌듯해요', 'color': const Color(0xFFAFDAA6)},
    {'emoji': '😐', 'label': '보통', 'sub': '그저 그래요', 'color': const Color(0xFFC9E6F5)},
    {'emoji': '😟', 'label': '나쁨', 'sub': '속상해요', 'color': const Color(0xFFFFCBA4)},
    {'emoji': '😭', 'label': '매우 나쁨', 'sub': '화나요', 'color': const Color(0xFFFFB0B0)},
  ];

  // 2단계: 상세 감정 데이터 (Map 구조로 변경됨)
  final Map<int, List<Map<String, dynamic>>> detailMoodMap = {
    0: [ // 😆 매우 좋음
      {'label': '신나요', 'color': const Color(0xFFFFF4B7), 'textColor': const Color(0xFF8B7300)},
      {'label': '최고예요', 'color': const Color(0xFFFFF4B7), 'textColor': const Color(0xFF8B7300)},
      {'label': '활기차요', 'color': const Color(0xFFFFF4B7), 'textColor': const Color(0xFF8B7300)},
      {'label': '짜릿해요', 'color': const Color(0xFFFFF4B7), 'textColor': const Color(0xFF8B7300)},
      {'label': '즐거워요', 'color': const Color(0xFFFFF4B7), 'textColor': const Color(0xFF8B7300)},
      {'label': '환상적예요', 'color': const Color(0xFFFFF4B7), 'textColor': const Color(0xFF8B7300)},
    ],
    1: [ // 😊 좋음
      {'label': '행복해요', 'color': const Color(0xFFC6FF8C), 'textColor': const Color(0xFF4E7A1E)},
      {'label': '편안해요', 'color': const Color(0xFFC6FF8C), 'textColor': const Color(0xFF4E7A1E)},
      {'label': '감사해요', 'color': const Color(0xFFC6FF8C), 'textColor': const Color(0xFF4E7A1E)},
      {'label': '뿌듯해요', 'color': const Color(0xFFC6FF8C), 'textColor': const Color(0xFF4E7A1E)},
      {'label': '화사해요', 'color': const Color(0xFFC6FF8C), 'textColor': const Color(0xFF4E7A1E)},
      {'label': '쾌적해요', 'color': const Color(0xFFC6FF8C), 'textColor': const Color(0xFF4E7A1E)},
    ],
    2: [ // 😐 보통
      {'label': '그저그래요', 'color': const Color(0xFFE1EFFF), 'textColor': const Color(0xFF4A608A)},
      {'label': '차분해요', 'color': const Color(0xFFE1EFFF), 'textColor': const Color(0xFF4A608A)},
      {'label': '평온해요', 'color': const Color(0xFFE1EFFF), 'textColor': const Color(0xFF4A608A)},
      {'label': '멍해요', 'color': const Color(0xFFE1EFFF), 'textColor': const Color(0xFF4A608A)},
      {'label': '심심해요', 'color': const Color(0xFFE1EFFF), 'textColor': const Color(0xFF4A608A)},
      {'label': '졸려요', 'color': const Color(0xFFE1EFFF), 'textColor': const Color(0xFF4A608A)},
    ],
    3: [ // 😟 나쁨
      {'label': '속상해요', 'color': const Color(0xFFFFE3D1), 'textColor': const Color(0xFF9E5C32)},
      {'label': '우울해요', 'color': const Color(0xFFFFE3D1), 'textColor': const Color(0xFF9E5C32)},
      {'label': '지쳐요', 'color': const Color(0xFFFFE3D1), 'textColor': const Color(0xFF9E5C32)},
      {'label': '불안해요', 'color': const Color(0xFFFFE3D1), 'textColor': const Color(0xFF9E5C32)},
      {'label': '답답해요', 'color': const Color(0xFFFFE3D1), 'textColor': const Color(0xFF9E5C32)},
      {'label': '걱정돼요', 'color': const Color(0xFFFFE3D1), 'textColor': const Color(0xFF9E5C32)},
    ],
    4: [ // 😭 매우 나쁨
      {'label': '화나요', 'color': const Color(0xFFFFD1D1), 'textColor': const Color(0xFF9E3232)},
      {'label': '짜증나요', 'color': const Color(0xFFFFD1D1), 'textColor': const Color(0xFF9E3232)},
      {'label': '슬퍼요', 'color': const Color(0xFFFFD1D1), 'textColor': const Color(0xFF9E3232)},
      {'label': '서운해요', 'color': const Color(0xFFFFD1D1), 'textColor': const Color(0xFF9E3232)},
      {'label': '억울해요', 'color': const Color(0xFFFFD1D1), 'textColor': const Color(0xFF9E3232)},
      {'label': '힘들어요', 'color': const Color(0xFFFFD1D1), 'textColor': const Color(0xFF9E3232)},
    ],
  };

  @override
  Widget build(BuildContext context) {
    bool isButtonEnabled = isDetailView
        ? selectedDetailIndex != null
        : selectedIndex != null;

    return Container(
      width: MediaQuery.of(context).size.width * 0.85,
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(25),
        border: Border.all(color: const Color(0xFF7297E3), width: 2.0),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            isDetailView ? "구체적으로 어떤 기분인가요?" : "오늘의 기분은 어때?",
            style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold, color: Color(0xFF4A90E2)),
          ),
          const SizedBox(height: 20),
          SizedBox(
            height: 140,
            child: isDetailView ? _buildDetailView() : _buildMainView(),
          ),
          const SizedBox(height: 20),
          ElevatedButton(
            onPressed: isButtonEnabled
                ? () async {
              if (!isDetailView) {
                setState(() {
                  isDetailView = true;
                  selectedDetailIndex = null; // 상세 인덱스 초기화
                });
              } else {
                final viewModel = context.read<QuestViewModel>();

                // detailMoodMap에서 선택된 리스트를 가져와서 라벨 추출
                final currentDetails = detailMoodMap[selectedIndex!]!;
                String finalEmotion = currentDetails[selectedDetailIndex!]['label'];

                await viewModel.saveFeeling(finalEmotion);

                if (context.mounted) {
                  Navigator.pop(context);
                }
              }
            }
                : null,
            style: ElevatedButton.styleFrom(
              backgroundColor: isButtonEnabled ? const Color(0xFF5C85E5) : const Color(0xFFD1D1D1),
              minimumSize: const Size(double.infinity, 60),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(50)),
              elevation: 0,
            ),
            child: Text(
              "선택 완료",
              style: TextStyle(
                  color: isButtonEnabled ? Colors.white : Colors.white70,
                  fontSize: 20,
                  fontWeight: FontWeight.bold
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildMainView() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: List.generate(moods.length, (index) {
        bool isSelected = selectedIndex == index;
        return GestureDetector(
          onTap: () => setState(() => selectedIndex = index),
          child: Opacity(
            opacity: selectedIndex == null || isSelected ? 1.0 : 0.4,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                CircleAvatar(
                  radius: 22,
                  backgroundColor: moods[index]['color'],
                  child: Text(moods[index]['emoji'], style: const TextStyle(fontSize: 20)),
                ),
                const SizedBox(height: 6),
                Text(moods[index]['label'], style: const TextStyle(fontSize: 9, fontWeight: FontWeight.bold)),
                const SizedBox(height: 4),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 5, vertical: 2),
                  decoration: BoxDecoration(
                    color: moods[index]['color'],
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Text(
                    moods[index]['sub'],
                    style: const TextStyle(fontSize: 9, color: Colors.black, fontWeight: FontWeight.bold),
                  ),
                ),
              ],
            ),
          ),
        );
      }),
    );
  }

  Widget _buildDetailView() {
    // 선택된 메인 기분에 맞는 상세 리스트 가져오기
    final currentDetails = detailMoodMap[selectedIndex!] ?? [];

    return Center(
      child: Wrap(
        spacing: 8,
        runSpacing: 10,
        alignment: WrapAlignment.center,
        children: List.generate(currentDetails.length, (index) {
          bool isSelected = selectedDetailIndex == index;
          return GestureDetector(
            onTap: () => setState(() => selectedDetailIndex = index),
            child: Container(
              width: 85,
              padding: const EdgeInsets.symmetric(vertical: 10),
              decoration: BoxDecoration(
                color: currentDetails[index]['color'],
                borderRadius: BorderRadius.circular(50),
                border: isSelected ? Border.all(color: const Color(0xFF5C85E5), width: 2) : null,
              ),
              child: Center(
                child: Text(
                  currentDetails[index]['label'],
                  style: TextStyle(
                    color: currentDetails[index]['textColor'],
                    fontWeight: FontWeight.bold,
                    fontSize: 12,
                  ),
                ),
              ),
            ),
          );
        }),
      ),
    );
  }
}