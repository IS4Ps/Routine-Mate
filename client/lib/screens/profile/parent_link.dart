import 'package:flutter/material.dart';

class ParentLink extends StatelessWidget {
  const ParentLink({super.key});

  @override
  Widget build(BuildContext context) {
    return Dialog(
      backgroundColor: Colors.transparent,
      child: Container(
        width: 380,
        padding: const EdgeInsets.all(20),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(25),
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [

            /// 상단 제목 + 닫기
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text(
                  "보호자 연동하기",
                  style: TextStyle(
                    fontSize: 24,
                  ),
                ),
                IconButton(
                  icon: const Icon(Icons.close),
                  onPressed: () {
                    Navigator.pop(context);
                  },
                ),
              ],
            ),
            const Divider(color: Color(0xFFE0E0E0), thickness: 1),
            const SizedBox(height: 20),

            /// QR 영역
            Container(
              width: double.infinity,
              height: 120,
              decoration: BoxDecoration(
                color: const Color(0xFFF3F3F3),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Stack(
                children: [
                  const Positioned(
                    top: 18,
                    left: 18,
                    child: Text(
                      "연동 QR:",
                      style: TextStyle(fontSize: 18),
                    ),
                  ),
                  Positioned(
                    right: 80,
                    top: 10,
                    bottom: 10,
                    child: Container(
                      width: 80,
                      height: 80,
                      color: Colors.grey[300],
                      child: const Icon(Icons.qr_code, size: 70),
                    ),
                  ),
                ],
              ),
            ),

            const SizedBox(height: 16),

            /// 설명 텍스트
            const Text(
              "위의 QR코드를 보호자에게 공유해주세요",
              style: TextStyle(
                color: Color(0xFF7C7D7D),
                fontSize: 16,
              ),
            ),

            const SizedBox(height: 20),

            /// 링크 복사 버튼
            SizedBox(
              width: double.infinity,
              height: 55,
              child: ElevatedButton(
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFFDD00FF),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(15),
                  ),
                ),
                onPressed: () {},
                child: const Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(Icons.copy, color: Colors.white, size: 22),
                    SizedBox(width: 8),
                    Text("링크 복사하기", style: TextStyle(fontSize: 18, color: Colors.white, fontWeight: FontWeight.bold)),
                  ],
                ),
              ),
            ),

            const SizedBox(height: 12),

            /// 링크 공유 버튼
            SizedBox(
              width: double.infinity,
              height: 55,
              child: ElevatedButton(
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF1586E2),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(15),
                  ),
                ),
                onPressed: () {},
                child: const Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(Icons.share, color: Colors.white, size: 22),
                    SizedBox(width: 8),
                    Text("링크 공유하기", style: TextStyle(fontSize: 18, color: Colors.white, fontWeight: FontWeight.bold),),
                  ],
                ),
              ),
            ),

            const SizedBox(height: 20),

            /// 팁 박스
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(14),
              decoration: BoxDecoration(
                color: const Color(0xFFD8EDFF),
                borderRadius: BorderRadius.circular(15),
              ),
              child: const Text(
                "💡팁: 연동 코드는 24시간 동안 유효합니다. 상대방이 링크로 앱에 접속하면 자동으로 연결됩니다!",
                style: TextStyle(fontSize: 14, fontWeight: FontWeight.w600),
              ),
            ),
          ],
        ),
      ),
    );
  }
}