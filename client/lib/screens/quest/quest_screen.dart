import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:model_viewer_plus/model_viewer_plus.dart'; // 3D 뷰어 패키지 추가
import '../../view_model/quest/quest_view_model.dart';
import 'grow_screen.dart';
import 'speech_bubble.dart';
import 'routine_screen.dart';
import 'ox_screen.dart';

class QuestScreen extends StatefulWidget {
  const QuestScreen({super.key});

  @override
  State<QuestScreen> createState() => _QuestScreenState();
}

class _QuestScreenState extends State<QuestScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (mounted) {
        final viewModel = context.read<QuestViewModel>();
        viewModel.loadTodayMood();
        viewModel.fetchTodayMissions();
        viewModel.fetchEquippedItems();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<QuestViewModel>(
      builder: (context, viewModel, child) {
        if (viewModel.currentStep == 3) {
          return OxScreen(onBack: () => viewModel.changeStep(2));
        }
        if (viewModel.currentStep == 2) {
          return GrowScreen(
            onBack: () => viewModel.changeStep(1),
            onQuizTap: () => viewModel.changeStep(3),
          );
        }
        if (viewModel.currentStep == 1) {
          return RoutineScreen(
            onBack: () => viewModel.changeStep(0),
            onGrowTap: () => viewModel.changeStep(2),
          );
        }

        return Scaffold(
          backgroundColor: const Color(0xFFFDFDFD),
          body: SingleChildScrollView(
            child: Column(
              children: [
                const SizedBox(height: 50),
                _buildLevelSection(),
                const SizedBox(height: 20),
                _buildCharacterSection(context, viewModel),
                const SizedBox(height: 30),
                _buildQuestCard(context, viewModel),
                const SizedBox(height: 30),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildLevelSection() {
    return Padding(
      padding: const EdgeInsets.all(20.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text("Level 1 모험가", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 24)),
          const SizedBox(height: 8),
          ClipRRect(
            borderRadius: BorderRadius.circular(30),
            child: const LinearProgressIndicator(
              value: 0.7,
              backgroundColor: Color(0xFFE2E2E2),
              valueColor: AlwaysStoppedAnimation<Color>(Color(0xFFA1FF6F)),
              minHeight: 18,
            ),
          ),
          const SizedBox(height: 10),
          const Center(child: Text("70%", style: TextStyle(color: Colors.black, fontSize: 22))),
        ],
      ),
    );
  }

  // 1. 함수 찾기
  Widget _buildCharacterSection(BuildContext context, QuestViewModel viewModel) {
    return Stack(
      children: [
        Padding(
          padding: const EdgeInsets.only(top: 80),
          child: Center(
            child: Container(
              width: 250,
              height: 310,
              decoration: BoxDecoration(
                color: const Color(0xFFFDFDFD),
                borderRadius: BorderRadius.circular(20),
              ),
              child: ClipRRect(
                borderRadius: BorderRadius.circular(20),
                child: Stack( // 모델들을 겹치기 위해 내부에서 다시 Stack 사용
                  children: [
                    // 1. 기본 캐릭터 모델
                    const ModelViewer(
                      src: 'assets/models/character/Rogue.glb',
                      alt: "Base Character",
                      autoRotate: false,
                      cameraControls: true,
                      disableZoom: true,
                      autoPlay: true,
                      backgroundColor: Color(0xFFFDFDFD),
                      loading: Loading.eager,
                    ),

                    // 2. 장착된 아이템 리스트를 순회하며 위에 얹기
                    ...viewModel.equippedItems.map((item) {
                      return ModelViewer(
                        src: 'assets/models/item/${item.splineTriggerName}.glb',
                        alt: item.itemName,
                        autoRotate: false,
                        cameraControls: false, // 아이템 모델은 컨트롤 비활성화 (캐릭터에 고정된 느낌)
                        disableZoom: true,
                        autoPlay: true,
                        backgroundColor: Colors.transparent, // 배경을 투명하게 해서 캐릭터가 보이게 함
                        loading: Loading.eager,
                      );
                    }).toList(),
                  ],
                ),
              ),
            ),
          ),
        ),

        // 말풍선 및 스태스 박스 (기존 코드 유지)
        Positioned(
          top: 0,
          left: 30,
          child: GestureDetector(
            onTap: () async {
              await viewModel.loadTodayMood();
              if (mounted) {
                _showMoodPopup(context, viewModel);
              }
            },
            child: CustomPaint(
              painter: BubblePainter(),
              child: Container(
                padding: const EdgeInsets.fromLTRB(20, 10, 20, 22),
                child: viewModel.isLoading
                    ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2, color: Colors.grey))
                    : Text(
                  viewModel.message,
                  style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w500),
                ),
              ),
            ),
          ),
        ),

        Positioned(
          top: 0, // 말풍선 아래 적절한 높이
          right: 40, // 오른쪽 여백
          child: GestureDetector(
            onTap: () => _showJobSelectionPopup(context),
            child: Container(
              width: 50,
              height: 50,
              decoration: BoxDecoration(
                color: Colors.white,
                shape: BoxShape.circle,
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.1),
                    blurRadius: 8,
                    offset: const Offset(0, 4),
                  ),
                ],
                border: Border.all(color: Colors.grey.withOpacity(0.2), width: 1),
              ),
              child: const Center(
                child: Icon(
                  Icons.face,
                  size: 30,
                  color: Colors.black,
                ),
              ),
            ),
          ),
        ),

        Positioned(right: 30, bottom: 0, child: _buildStatBox()),
      ],
    );
  }

  // 동그라미 사람 아이콘 선택 시
  void _showJobSelectionPopup(BuildContext context) {
    int selectedIndex = -1;

    showDialog(
      context: context,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setState) {
            bool isSelected = selectedIndex != -1;

            return Dialog(
              insetPadding: const EdgeInsets.symmetric(horizontal: 20, vertical: 24),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
              backgroundColor: Colors.white,
              child: Container(
                width: MediaQuery.of(context).size.width * 0.9,
                padding: const EdgeInsets.symmetric(vertical: 30, horizontal: 20),
                child: Column(
                  mainAxisSize: MainAxisSize.min, // 내용물에 맞게 높이 조절
                  children: [
                    _buildJobItem(0, "전사", "강력한 힘을 자랑하는 전사\n운동을 통해 체력을 기르세요!", selectedIndex == 0, () => setState(() => selectedIndex = 0)),
                    const SizedBox(height: 16),
                    _buildJobItem(1, "마법사", "지혜를 자랑하는 마법사\n공부, 독서를 통해 지혜를 기르세요!", selectedIndex == 1, () => setState(() => selectedIndex = 1)),
                    const SizedBox(height: 16),
                    _buildJobItem(2, "예술가", "자유로운 영혼의 예술가\n예술을 통해 창의력을 기르세요!", selectedIndex == 2, () => setState(() => selectedIndex = 2)),

                    const SizedBox(height: 35),

                    GestureDetector(
                      onTap: isSelected ? () => Navigator.pop(context) : null,
                      child: Opacity(
                        opacity: isSelected ? 1.0 : 0.5,
                        child: Container(
                          width: double.infinity, // 버튼도 팝업 너비에 맞춰 넓게
                          margin: const EdgeInsets.symmetric(horizontal: 80),
                          height: 55,
                          decoration: BoxDecoration(
                            color: const Color(0xFF4388E2),
                            borderRadius: BorderRadius.circular(15),
                            boxShadow: isSelected ? [
                              BoxShadow(color: Colors.black.withOpacity(0.1), blurRadius: 4, offset: const Offset(0, 2))
                            ] : [],
                          ),
                          child: const Center(
                            child: Text(
                                "선택 완료",
                                style: TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold)
                            ),
                          ),
                        ),
                      ),
                    ),

                    const SizedBox(height: 20),
                    const Text(
                      "직업 선택의 기회는 한 번밖에 없습니다!\n신중하게 선택하세요!",
                      textAlign: TextAlign.center,
                      style: TextStyle(color: Colors.black87, fontSize: 13, fontWeight: FontWeight.bold),
                    ),
                  ],
                ),
              ),
            );
          },
        );
      },
    );
  }

  Widget _buildJobItem(int index, String title, String desc, bool isSelected, VoidCallback onTap) {
    List<String> descLines = desc.split('\n');
    String mainDesc = descLines[0];
    String subDesc = descLines.length > 1 ? descLines[1] : "";

    String getImagePath() {
      switch (index) {
        case 0: return 'assets/models/character/Knight.png';
        case 1: return 'assets/models/character/Mage.png';
        case 2: return 'assets/models/character/Ranger.png';
        default: return 'assets/models/character/Rogue.png';
      }
    }

    Color getBorderColor() {
      switch (index) {
        case 0: return const Color(0xFFFF7070);
        case 1: return const Color(0xFF54C517);
        case 2: return const Color(0xFF2194FF);
        default: return const Color(0xFFEEEEEE);
      }
    }

    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(15),
        decoration: BoxDecoration(
          color: isSelected ? Colors.white : const Color(0xFFFDFDFD),
          borderRadius: BorderRadius.circular(12),
          border: Border.all(
            color: isSelected ? const Color(0xFF75F94C) : const Color(0xFFEEEEEE),
            width: 2,
          ),
          boxShadow: isSelected ? [
            BoxShadow(color: Colors.black.withOpacity(0.05), blurRadius: 10, offset: const Offset(0, 4))
          ] : [],
        ),
        child: Row(
          children: [
            Container(
              width: 90,
              height: 90,
              decoration: BoxDecoration(
                color: const Color(0xFFFFFFFF),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(
                  color: getBorderColor(),
                  width: 1.0,
                ),
              ),
              child: ClipRRect(
                borderRadius: BorderRadius.circular(12),
                child: Image.asset(
                  getImagePath(),
                  fit: BoxFit.contain,
                  // 사진 파일이 폴더에 없거나 경로가 틀리면 아래 에러 텍스트가 뜹니다.
                  errorBuilder: (context, error, stackTrace) => const Center(
                    child: Text(
                      "사진 없음",
                      textAlign: TextAlign.center,
                      style: TextStyle(fontSize: 10, color: Colors.black54),
                    ),
                  ),
                ),
              ),
            ),
            const SizedBox(width: 15),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(title, style: const TextStyle(fontSize: 20, fontWeight: FontWeight.w700, color: Colors.black)),
                  const SizedBox(height: 6),
                  Text(
                    mainDesc,
                    style: const TextStyle(
                      fontSize: 15,
                      color: Colors.black87,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                  const SizedBox(height: 2),
                  Text(
                    subDesc,
                    style: const TextStyle(
                      fontSize: 12,
                      color: Colors.black87,
                      fontWeight: FontWeight.normal,
                      height: 1.4,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _showMoodPopup(BuildContext context, QuestViewModel viewModel) {
    showDialog(
      context: context,
      barrierDismissible: true,
      builder: (BuildContext dialogContext) {
        return ChangeNotifierProvider.value(
          value: viewModel,
          child: const Dialog(
            backgroundColor: Colors.transparent,
            insetPadding: EdgeInsets.symmetric(horizontal: 20),
            child: SpeechBubble(),
          ),
        );
      },
    );
  }

  Widget _buildQuestCard(BuildContext context, QuestViewModel viewModel) {
    String displayTitle = "진행 중인 퀘스트가 없어요";
    if (viewModel.todayMissions.isNotEmpty) {
      displayTitle = viewModel.todayMissions.first.bigTaskTitle;
    }

    return GestureDetector(
      onTap: () => viewModel.changeStep(1),
      child: Container(
        margin: const EdgeInsets.symmetric(horizontal: 20),
        padding: const EdgeInsets.all(20),
        decoration: BoxDecoration(
          color: const Color(0xFFFDFDFD),
          borderRadius: BorderRadius.circular(20),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.15),
              blurRadius: 10,
              offset: const Offset(0, 4),
            )
          ],
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text("다음 퀘스트", style: TextStyle(color: Colors.grey, fontSize: 18)),
                const SizedBox(height: 5),
                Text(
                  displayTitle,
                  style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
                ),
              ],
            ),
            const Icon(Icons.arrow_forward_ios, color: Colors.grey, size: 18),
          ],
        ),
      ),
    );
  }

  Widget _buildStatBox() {
    return Container(
      width: 104, height: 80,
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 12),
      decoration: BoxDecoration(
        color: const Color(0xFFFDFDFD),
        borderRadius: BorderRadius.circular(10),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.05), blurRadius: 10, offset: const Offset(0, 4))],
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          _buildMiniBar(const Color(0xFFFF7070), 0.8),
          _buildMiniBar(const Color(0xFF54C517), 0.5),
          _buildMiniBar(const Color(0xFF2194FF), 0.3),
        ],
      ),
    );
  }

  Widget _buildMiniBar(Color color, double value) {
    return Column(
      children: [
        Stack(
          children: [
            Container(width: double.infinity, height: 8, decoration: BoxDecoration(color: const Color(0xFFEEEEEE), borderRadius: BorderRadius.circular(4))),
            FractionallySizedBox(widthFactor: value, child: Container(height: 8, decoration: BoxDecoration(color: color, borderRadius: BorderRadius.circular(4)))),
          ],
        ),
      ],
    );
  }
}

class BubblePainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paintFill = Paint()..color = const Color(0xFFFDFDFD)..style = PaintingStyle.fill;
    final path = Path();
    const double radius = 15.0;
    const double tailWidth = 10.0;
    const double tailHeight = 10.0;
    double tailPosition = size.width * 0.8;
    path.addRRect(RRect.fromLTRBR(0, 0, size.width, size.height - tailHeight, const Radius.circular(radius)));
    path.moveTo(tailPosition - tailWidth, size.height - tailHeight);
    path.lineTo(tailPosition, size.height);
    path.lineTo(tailPosition + tailWidth, size.height - tailHeight);
    path.close();
    canvas.drawShadow(path, Colors.black.withOpacity(0.2), 6.0, true);
    canvas.drawPath(path, paintFill);
  }
  @override
  bool shouldRepaint(CustomPainter oldDelegate) => false;
}