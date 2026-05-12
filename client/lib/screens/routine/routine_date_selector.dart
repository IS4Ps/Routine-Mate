import 'package:flutter/material.dart';

class WeekCalendarWidget extends StatelessWidget {
  final int selectedDay;
  final ValueChanged<int> onDaySelected;
  final bool isFullCalendarOpen;
  final VoidCallback? onToggleCalendar;

  const WeekCalendarWidget({
    super.key,
    required this.selectedDay,
    required this.onDaySelected,
    this.isFullCalendarOpen = false,
    this.onToggleCalendar,
  });

  @override
  Widget build(BuildContext context) {
    final now = DateTime.now();
    final int currentMonth = now.month;
    final int daysInMonth = DateUtils.getDaysInMonth(now.year, now.month);
    // 이번 달 1일의 요일 (0=일, 1=월, ..., 6=토)
    final int firstWeekday = DateTime(now.year, now.month, 1).weekday % 7;

    return Container(
      width: double.infinity,
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(15),
        boxShadow: const [
          BoxShadow(
            color: Color(0x14000000),
            blurRadius: 8,
            offset: Offset(0, 4),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                '$currentMonth월 $selectedDay일',
                style: const TextStyle(
                  color: Colors.black,
                  fontSize: 16,
                  fontFamily: 'JejuGothic',
                  fontWeight: FontWeight.w500,
                ),
              ),
              if (onToggleCalendar != null)
                GestureDetector(
                  onTap: onToggleCalendar,
                  child: Icon(
                    isFullCalendarOpen
                        ? Icons.keyboard_arrow_up
                        : Icons.keyboard_arrow_down,
                    color: const Color(0xFFB0B0B0),
                    size: 22,
                  ),
                ),
            ],
          ),
          const SizedBox(height: 12),
          Container(
            width: double.infinity,
            padding: const EdgeInsets.fromLTRB(8, 8, 8, 1),
            decoration: BoxDecoration(
              color: const Color(0xFFD9D9D9),
              borderRadius: BorderRadius.circular(28),
            ),
            child: isFullCalendarOpen
                ? _buildFullCalendar(daysInMonth, firstWeekday)
                : _buildWeekRow(daysInMonth, firstWeekday),
          ),
        ],
      ),
    );
  }

  // 선택된 날짜가 속한 주의 날짜 7개 반환
  List<int?> _getWeekDays(int daysInMonth, int firstWeekday) {
    // 전체 달력 그리드 생성 (앞에 빈칸 포함)
    final List<int?> grid = [
      ...List.filled(firstWeekday, null),
      ...List.generate(daysInMonth, (i) => i + 1),
    ];

    // selectedDay가 몇 번째 인덱스인지 찾기
    final int selectedIndex = grid.indexOf(selectedDay);
    // 해당 주의 시작 인덱스
    final int weekStart = (selectedIndex ~/ 7) * 7;

    return List.generate(7, (i) {
      final idx = weekStart + i;
      return idx < grid.length ? grid[idx] : null;
    });
  }

  Widget _buildWeekRow(int daysInMonth, int firstWeekday) {
    const days = ['일', '월', '화', '수', '목', '금', '토'];
    final weekDays = _getWeekDays(daysInMonth, firstWeekday);

    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      children: List.generate(7, (i) {
        final date = weekDays[i];
        return Expanded(
          child: _CalendarCell(
            dayLabel: days[i],
            date: date,
            isSelected: date != null && selectedDay == date,
            onTap: date != null ? onDaySelected : null,
          ),
        );
      }),
    );
  }

  Widget _buildFullCalendar(int daysInMonth, int firstWeekday) {
    const days = ['일', '월', '화', '수', '목', '금', '토'];

    // 전체 그리드 (빈칸 + 날짜)
    final List<int?> grid = [
      ...List.filled(firstWeekday, null),
      ...List.generate(daysInMonth, (i) => i + 1),
    ];

    // 7개씩 주 단위로 나누기
    final List<List<int?>> weeks = [];
    for (int i = 0; i < grid.length; i += 7) {
      final week = grid.sublist(i, i + 7 > grid.length ? grid.length : i + 7);
      while (week.length < 7) week.add(null);
      weeks.add(week);
    }

    return Column(
      children: [
        // 요일 헤더
        Row(
          children: List.generate(7, (i) => Expanded(
            child: Padding(
              padding: const EdgeInsets.only(bottom: 4),
              child: Text(
                days[i],
                textAlign: TextAlign.center,
                style: const TextStyle(
                  color: Colors.black,
                  fontSize: 14,
                  fontFamily: 'JejuGothic',
                  fontWeight: FontWeight.w700,
                ),
              ),
            ),
          )),
        ),
        // 날짜 행들
        ...weeks.map((week) => Padding(
          padding: const EdgeInsets.only(top: 4),
          child: Row(
            children: List.generate(7, (i) {
              final date = week[i];
              return Expanded(
                child: _NumberCell(
                  date: date,
                  isSelected: date != null && selectedDay == date,
                  onTap: date != null ? onDaySelected : null,
                ),
              );
            }),
          ),
        )),
      ],
    );
  }
}

class _CalendarCell extends StatelessWidget {
  final String dayLabel;
  final int? date;
  final bool isSelected;
  final ValueChanged<int>? onTap;

  const _CalendarCell({
    required this.dayLabel,
    required this.date,
    required this.isSelected,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Text(
          dayLabel,
          textAlign: TextAlign.center,
          style: const TextStyle(
            color: Colors.black,
            fontSize: 14,
            fontFamily: 'JejuGothic',
            fontWeight: FontWeight.w700,
            height: 1.0,
          ),
        ),
        _NumberCell(date: date, isSelected: isSelected, onTap: onTap),
      ],
    );
  }
}

class _NumberCell extends StatelessWidget {
  final int? date;
  final bool isSelected;
  final ValueChanged<int>? onTap;

  const _NumberCell({
    required this.date,
    required this.isSelected,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: date != null && onTap != null ? () => onTap!(date!) : null,
      child: AspectRatio(
        aspectRatio: 1,
        child: Container(
          constraints: const BoxConstraints(maxWidth: 34, maxHeight: 34),
          child: Stack(
            alignment: Alignment.center,
            children: [
              if (isSelected)
                FractionallySizedBox(
                  widthFactor: 0.88,
                  heightFactor: 0.88,
                  child: Container(
                    decoration: const BoxDecoration(
                      color: Color(0xFF8EBBE4),
                      shape: BoxShape.circle,
                    ),
                  ),
                ),
              Text(
                date != null ? '$date' : '',
                style: const TextStyle(
                  color: Colors.black,
                  fontSize: 14,
                  fontFamily: 'JejuGothic',
                  fontWeight: FontWeight.w700,
                  height: 1.0,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}