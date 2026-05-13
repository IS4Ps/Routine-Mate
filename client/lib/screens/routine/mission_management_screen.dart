import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../routine/routine_date_selector.dart';
import '../../view_model/routine/mission_view_model.dart';

class MissionManagementScreen extends StatefulWidget {
  final VoidCallback? onBack;
  const MissionManagementScreen({super.key, this.onBack});

  @override
  State<MissionManagementScreen> createState() => _MissionManagementScreenState();
}

class _MissionManagementScreenState extends State<MissionManagementScreen> {
  int selectedDay = DateTime.now().day;
  bool isFullCalendarOpen = false;
  final Map<int, bool> _expandedState = {};

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _fetchMissions();
    });
  }

  void _fetchMissions() {
    final now = DateTime.now();
    final date = '${now.year}-${now.month.toString().padLeft(2, '0')}-${selectedDay.toString().padLeft(2, '0')}';
    context.read<MissionViewModel>().getMissionsByDate(5, date);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF6F6F6),
      body: SafeArea(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 16, 20, 8),
              child: Row(
                children: [
                  GestureDetector(
                    onTap: () => widget.onBack?.call(),
                    child: const Icon(Icons.arrow_back_ios, size: 20),
                  ),
                  const SizedBox(width: 8),
                  const Text(
                    '미션 관리',
                    style: TextStyle(
                      fontSize: 20,
                      fontFamily: 'JejuGothic',
                      fontWeight: FontWeight.w700,
                    ),
                  ),
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 40),
              child: WeekCalendarWidget(
                selectedDay: selectedDay,
                isFullCalendarOpen: isFullCalendarOpen,
                onDaySelected: (day) {
                  setState(() => selectedDay = day);
                  _fetchMissions();
                },
                onToggleCalendar: () => setState(() => isFullCalendarOpen = !isFullCalendarOpen),
              ),
            ),
            const SizedBox(height: 16),
            const Padding(
              padding: EdgeInsets.only(left: 20),
              child: Text(
                '오늘의 퀘스트!',
                style: TextStyle(
                  fontSize: 24,
                  fontFamily: 'JejuGothic',
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
            const SizedBox(height: 12),
            Expanded(
              child: Consumer<MissionViewModel>(
                builder: (context, viewModel, _) {
                  if (viewModel.isLoading) {
                    return const Center(child: CircularProgressIndicator());
                  }

                  return ListView.builder(
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    itemCount: viewModel.missions.length,
                    itemBuilder: (context, index) {
                      final mission = viewModel.missions[index];
                      final missionId = mission['missionId'];
                      final isExpanded = _expandedState[missionId] ?? false;
                      final smallTasks = mission['smallTasks'] as List<dynamic>? ?? [];

                      return Container(
                        margin: const EdgeInsets.only(bottom: 12),
                        decoration: BoxDecoration(
                          color: Colors.white,
                          borderRadius: BorderRadius.circular(15),
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Padding(
                              padding: const EdgeInsets.fromLTRB(16, 12, 8, 12),
                              child: Row(
                                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                children: [
                                  Text(
                                    mission['bigTaskTitle'] ?? '',
                                    style: const TextStyle(
                                      fontSize: 18,
                                      fontFamily: 'JejuGothic',
                                      fontWeight: FontWeight.w700,
                                    ),
                                  ),
                                  GestureDetector(
                                    onTap: () {
                                      // 삭제 기능은 다음 이슈에서
                                    },
                                    child: Container(
                                      width: 14,
                                      height: 14,
                                      decoration: BoxDecoration(
                                        color: Colors.red,
                                        borderRadius: BorderRadius.circular(4),
                                      ),
                                      child: const Icon(Icons.close, size: 14, color: Colors.white),
                                    ),
                                  ),
                                ],
                              ),
                            ),
                            if (isExpanded && smallTasks.isNotEmpty)
                              ...smallTasks.map((task) =>
                                  Container(
                                    margin: const EdgeInsets.fromLTRB(16, 0, 16, 8),
                                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                                    decoration: BoxDecoration(
                                      color: const Color(0xFFF6F6F6),
                                      borderRadius: BorderRadius.circular(15),
                                    ),
                                    child: Row(
                                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                      children: [
                                        Text(
                                          task['title'] ?? '',
                                          style: const TextStyle(
                                            fontSize: 14,
                                            fontFamily: 'JejuGothic',
                                            fontWeight: FontWeight.w600,
                                          ),
                                        ),
                                        Container(
                                          width: 14,
                                          height: 14,
                                          decoration: BoxDecoration(
                                            color: Colors.red,
                                            borderRadius: BorderRadius.circular(4),
                                          ),
                                          child: const Icon(Icons.close, size: 12, color: Colors.white),
                                        ),
                                      ],
                                    ),
                                  ),
                              ).toList(),
                            if (smallTasks.isNotEmpty)
                              GestureDetector(
                                onTap: () {
                                  setState(() {
                                    _expandedState[missionId] = !isExpanded;
                                  });
                                },
                                child: Padding(
                                  padding: const EdgeInsets.symmetric(vertical: 4),
                                  child: Center(
                                    child: Icon(
                                      isExpanded
                                          ? Icons.keyboard_arrow_up
                                          : Icons.keyboard_arrow_down,
                                      color: const Color(0xFFBCBCBC),
                                    ),
                                  ),
                                ),
                              ),
                            const SizedBox(height: 4),
                          ],
                        ),
                      );
                    },
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}