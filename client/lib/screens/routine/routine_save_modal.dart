import 'package:flutter/material.dart';
import 'routine_date_selector.dart';
import 'package:provider/provider.dart';
import '../../view_model/routine/preset_view_model.dart';

Future<void> showRoutineSaveModal(
    BuildContext context, {
      int initialDay = 1,
    }) {
  int selectedDay = initialDay;
  bool isFullCalendarOpen = false;
  final TextEditingController controller = TextEditingController();

  return showDialog(
    context: context,
    barrierDismissible: true,
    builder: (context) {
      return StatefulBuilder(
        builder: (context, setState) {
          return Dialog(
            backgroundColor: Colors.white,
            insetPadding: const EdgeInsets.symmetric(horizontal: 24),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(15),
            ),
            child: Container(
              padding: const EdgeInsets.fromLTRB(16, 10, 16, 16),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(15),
                border: Border.all(color: const Color(0xFF1586E2)),
              ),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  const Text(
                    '반복루틴 저장',
                    style: TextStyle(
                      color: Colors.black,
                      fontSize: 16,
                      fontFamily: 'JejuGothic',
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(height: 10),

                  WeekCalendarWidget(
                    selectedDay: selectedDay,
                    isFullCalendarOpen: isFullCalendarOpen,
                    onDaySelected: (day) {
                      setState(() {
                        selectedDay = day;
                      });
                    },
                    onToggleCalendar: () {
                      setState(() {
                        isFullCalendarOpen = !isFullCalendarOpen;
                      });
                    },
                  ),

                  const SizedBox(height: 14),

                  const Align(
                    alignment: Alignment.centerLeft,
                    child: Text(
                      '반복루틴 이름',
                      style: TextStyle(
                        color: Colors.black,
                        fontSize: 14,
                        fontFamily: 'JejuGothic',
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                  const SizedBox(height: 6),

                  TextField(
                    controller: controller,
                    style: const TextStyle(
                      fontSize: 13,
                    ),
                    decoration: InputDecoration(
                      hintText: '반복루틴 이름을 입력하세요. (예: 아침 등교 루틴)',
                      hintStyle: const TextStyle(
                        fontSize: 13,
                        color: Color(0xFF555555),
                      ),
                      contentPadding: const EdgeInsets.symmetric(
                        horizontal: 10,
                        vertical: 10,
                      ),

                      enabledBorder: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(6),
                        borderSide: const BorderSide(
                          color: Color(0xFFCCCCCC),
                          width: 1,
                        ),
                      ),

                      focusedBorder: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(6),
                        borderSide: const BorderSide(
                          color: Color(0xFFCCCCCC), // 👈 동일하게!
                          width: 1,
                        ),
                      ),
                    ),
                  ),

                  const SizedBox(height: 16),

                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      SizedBox(
                        width: 110,
                        child: GestureDetector(
                          onTap: () => Navigator.pop(context),
                          child: Container(
                            height: 28,
                            decoration: BoxDecoration(
                              color: const Color(0xFFD9D9D9),
                              borderRadius: BorderRadius.circular(10),
                            ),
                            alignment: Alignment.center,
                            child: const Text(
                              '취소',
                              style: TextStyle(
                                fontSize: 14,
                                fontFamily: 'JejuGothic',
                                fontWeight: FontWeight.w700,
                              ),
                            ),
                          ),
                        ),
                      ),
                      const SizedBox(width: 10),
                      SizedBox(
                        width: 110,
                        child: GestureDetector(
                          onTap: () async {
                            if (controller.text.isEmpty) return;

                            final presetViewModel = Provider.of<PresetViewModel>(context, listen: false);

                            final now = DateTime.now();
                            final date = '${now.year}-${now.month.toString().padLeft(2, '0')}-${selectedDay.toString().padLeft(2, '0')}';

                            final success = await presetViewModel.savePresetFromDate(
                              title: controller.text,
                              date: date,
                            );

                            if (success && context.mounted) {
                              Navigator.pop(context);
                            }
                          },
                          child: Container(
                            height: 28,
                            decoration: BoxDecoration(
                              color: const Color(0xFF1586E2),
                              borderRadius: BorderRadius.circular(10),
                            ),
                            alignment: Alignment.center,
                            child: const Text(
                              '저장',
                              style: TextStyle(
                                color: Colors.white,
                                fontSize: 14,
                                fontFamily: 'JejuGothic',
                                fontWeight: FontWeight.w700,
                              ),
                            ),
                          ),
                        ),
                      ),
                    ],
                  )
                ],
              ),
            ),
          );
        },
      );
    },
  );
}