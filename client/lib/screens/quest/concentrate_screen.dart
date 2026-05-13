import 'package:flutter/material.dart';
import 'concentrate_timer_screen.dart';

class ConcentrateScreen extends StatefulWidget {
  final VoidCallback? onBack;
  const ConcentrateScreen({super.key, this.onBack});

  @override
  State<ConcentrateScreen> createState() => _ConcentrateScreenState();
}

class _ConcentrateScreenState extends State<ConcentrateScreen> {
  int hours = 0;
  int minutes = 0;

  String selectedTag = "공부";
  final List<String> tags = ["공부", "운동", "음악", "미술", "독서"];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFECF2F8),
      body: SingleChildScrollView(
        padding: const EdgeInsets.fromLTRB(0, 50, 0, 40),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            // 1. 뒤로가기 버튼
            Align(
              alignment: Alignment.centerLeft,
              child: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 10),
                child: IconButton(
                  icon: const Icon(Icons.arrow_back_ios_new_rounded, color: Colors.black, size: 24),
                  onPressed: () {
                    if (widget.onBack != null) {
                      widget.onBack!();
                    } else {
                      Navigator.pop(context);
                    }
                  },
                ),
              ),
            ),

            const SizedBox(height: 13),

            // 2. 상단 타이틀
            const Text(
              "집중하기",
              style: TextStyle(
                fontSize: 40,
                fontWeight: FontWeight.w700,
                color: Colors.black,
              ),
            ),
            const SizedBox(height: 40),

            // 3. 시간 선택 카드
            Center(
              child: Container(
                width: 320,
                padding: const EdgeInsets.symmetric(vertical: 25, horizontal: 20),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(20),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.05),
                      blurRadius: 15,
                      offset: const Offset(0, 5),
                    ),
                  ],
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    _buildTimePickerColumn("시간", hours, (val) {
                      setState(() => hours = (hours + val).clamp(0, 23));
                    }),
                    _buildTimePickerColumn("분", minutes, (val) {
                      setState(() => minutes = (minutes + val).clamp(0, 59));
                    }),
                  ],
                ),
              ),
            ),

            const SizedBox(height: 40),

            // 4. 태그 선택 섹션
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Padding(
                  padding: EdgeInsets.only(left: 30, bottom: 25),
                  child: Text(
                    "태그 선택",
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                SizedBox(
                  height: 30,
                  child: ListView.separated(
                    scrollDirection: Axis.horizontal,
                    padding: const EdgeInsets.symmetric(horizontal: 30),
                    itemCount: tags.length,
                    separatorBuilder: (context, index) => const SizedBox(width: 8),
                    itemBuilder: (context, index) {
                      return _buildTagButton(tags[index]);
                    },
                  ),
                ),
              ],
            ),

            const SizedBox(height: 80),

            // 5. 집중 시작 버튼
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 40),
              child: SizedBox(
                width: 260,
                height: 74,
                child: ElevatedButton(
                  onPressed: () {
                    // 1. 설정한 총 시간을 초 단위로 계산
                    int totalSeconds = (hours * 3600) + (minutes * 60);

                    // 2. 시간이 0초일 경우 예외 처리
                    if (totalSeconds == 0) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(
                          content: Text("집중할 시간을 설정해주세요!"),
                          duration: Duration(seconds: 1),
                        ),
                      );
                      return;
                    }

                    // 3. 타이머 화면으로 이동하며 시간 데이터 전달
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => ConcentrateTimerScreen(
                          totalSeconds: totalSeconds,
                        ),
                      ),
                    );
                  },
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF1687E3),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(15),
                    ),
                    elevation: 0,
                  ),
                  child: const Text(
                    "집중 시작",
                    style: TextStyle(
                      fontSize: 22,
                      fontWeight: FontWeight.bold,
                      color: Colors.white,
                    ),
                  ),
                ),
              ),
            ),
            const SizedBox(height: 40),
          ],
        ),
      ),
    );
  }

  Widget _buildTimePickerColumn(String unit, int value, Function(int) onUpdate) {
    return Column(
      children: [
        GestureDetector(
          onTap: () => onUpdate(1),
          child: const Icon(Icons.keyboard_arrow_up_rounded, size: 45, color: Color(0xFFD1D1D1)),
        ),
        Row(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.end,
          children: [
            Text(
              value.toString().padLeft(2, '0'),
              style: const TextStyle(fontSize: 36, fontWeight: FontWeight.bold),
            ),
            Padding(
              padding: const EdgeInsets.only(bottom: 6, left: 2),
              child: Text(
                unit,
                style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
            ),
          ],
        ),
        GestureDetector(
          onTap: () => onUpdate(-1),
          child: const Icon(Icons.keyboard_arrow_down_rounded, size: 45, color: Color(0xFFD1D1D1)),
        ),
      ],
    );
  }

  Widget _buildTagButton(String tag) {
    bool isSelected = selectedTag == tag;
    return GestureDetector(
      onTap: () => setState(() => selectedTag = tag),
      child: Container(
        width: 64,
        height: 30,
        alignment: Alignment.center,
        decoration: BoxDecoration(
          color: isSelected ? const Color(0xFF8AC5F5) : Colors.white,
          borderRadius: BorderRadius.circular(25),
          border: Border.all(
            color: isSelected ? const Color(0xFF4388E2) : const Color(0xFFE0E0E0),
            width: 1.0,
          ),
        ),
        child: Text(
          tag,
          style: TextStyle(
            fontSize: 16,
            fontWeight: isSelected ? FontWeight.bold : FontWeight.w600,
            color: isSelected ? Colors.black : Colors.black54,
          ),
        ),
      ),
    );
  }
}