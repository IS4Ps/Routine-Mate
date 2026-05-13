import 'package:flutter/material.dart';

class ChildSelectionDialog extends StatefulWidget {
  const ChildSelectionDialog({super.key});

  @override
  State<ChildSelectionDialog> createState() => _ChildSelectionDialogState();
}

class _ChildSelectionDialogState extends State<ChildSelectionDialog> {
  // 처음에는 아무도 선택되지 않은 상태로 시작하려면 null,
  // 기본 선택값을 두려면 '준수' 등으로 설정하세요.
  String? selectedChild;

  @override
  Widget build(BuildContext context) {
    // 자녀가 선택되었는지 여부 확인
    bool isChildSelected = selectedChild != null;

    return Dialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      backgroundColor: Colors.white,
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 30, horizontal: 20),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Text(
              '자녀 계정 선택',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.black),
            ),
            const SizedBox(height: 30),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                _buildChildItem('준수'),
                _buildChildItem('철수'),
              ],
            ),
            const SizedBox(height: 35),
            const Divider(height: 1, thickness: 1, color: Color(0xFFF0F0F0)),
            const SizedBox(height: 25),

            // 선택 여부에 따라 색상이 변하는 버튼
            SizedBox(
              width: double.infinity,
              height: 52,
              child: ElevatedButton(
                // 아무도 선택 안 됐으면 버튼 클릭 안 되게 하려면 null,
                // 그냥 닫히게 하려면 Navigator.pop
                onPressed: isChildSelected
                    ? () => Navigator.pop(context, selectedChild)
                    : null,
                style: ElevatedButton.styleFrom(
                  // 선택됐을 때: 파란색, 안 됐을 때: 연회색
                  backgroundColor: isChildSelected
                      ? const Color(0xFF1687E3)
                      : const Color(0xFFE0E0E0),
                  foregroundColor: isChildSelected ? Colors.white : Colors.black38,
                  elevation: 0,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
                  // 버튼 비활성화 시 색상 정의 (중요)
                  disabledBackgroundColor: const Color(0xFFE0E0E0),
                  disabledForegroundColor: Colors.black38,
                ),
                child: Text(
                    '선택 완료',
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 16,
                      color: isChildSelected ? Colors.white : Colors.black38,
                    )
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildChildItem(String name) {
    bool isSelected = selectedChild == name;

    return GestureDetector(
      onTap: () {
        setState(() {
          selectedChild = name;
        });
      },
      child: Column(
        children: [
          AnimatedContainer(
            duration: const Duration(milliseconds: 200),
            padding: const EdgeInsets.all(3),
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              border: Border.all(
                color: isSelected ? const Color(0xFF1687E3) : Colors.transparent,
                width: 2,
              ),
            ),
            child: CircleAvatar(
              radius: 35,
              backgroundColor: isSelected
                  ? const Color(0xFF1687E3).withOpacity(0.1)
                  : const Color(0xFFD9D9D9),
              child: Icon(
                Icons.person,
                size: 40,
                color: isSelected ? const Color(0xFF1687E3) : Colors.white,
              ),
            ),
          ),
          const SizedBox(height: 10),
          Text(
            name,
            style: TextStyle(
              fontSize: 15,
              fontWeight: isSelected ? FontWeight.bold : FontWeight.w500,
              color: isSelected ? const Color(0xFF1687E3) : Colors.black54,
            ),
          ),
        ],
      ),
    );
  }
}