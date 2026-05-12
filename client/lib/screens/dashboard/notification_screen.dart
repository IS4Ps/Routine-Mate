import 'package:flutter/material.dart';

class NotificationScreen extends StatelessWidget {
  final VoidCallback? onBack;

  const NotificationScreen({super.key, this.onBack});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFEBF1F7),
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        leading: GestureDetector(
          onTap: () => onBack?.call(),
          child: const Icon(Icons.arrow_back, color: Colors.black),
        ),
        title: const Text(
          '알림 및 승인',
          style: TextStyle(
            color: Colors.black,
            fontSize: 24,
            fontFamily: 'Imprima',
            fontWeight: FontWeight.w400,
          ),
        ),
        centerTitle: true,
      ),
      body: Padding(
        padding: const EdgeInsets.fromLTRB(30, 20, 30, 20), // 3. 좌우 줄이기
        child: Container(
          width: double.infinity,
          height: double.infinity, // 3. 아래로 늘리기
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(20),
          ),
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [ // mainAxisSize: min 제거
              const Text(
                '미션 & 보상 요청',
                style: TextStyle(
                  color: Colors.black,
                  fontSize: 16,
                  fontFamily: 'JejuGothic',
                  fontWeight: FontWeight.w600, // 1. 진하게
                ),
              ),
              const SizedBox(height: 12),
              _buildNotificationItem(
                message: '준수님이 등교 준비하기 미션을 완료 했습니다.',
                time: '방금 전',
              ),
              const SizedBox(height: 8),
              _buildNotificationItem(
                message: '준수님이 게임 30분하기 보상을 사용 요청했습니다.',
                time: '5분 전',
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildNotificationItem({required String message, required String time}) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.fromLTRB(12, 6, 12, 6), // 2. 위아래 패딩 줄이기
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: const Color(0xFFE2E2E2)),
        boxShadow: const [
          BoxShadow(
            color: Color(0x3F000000),
            blurRadius: 4,
            offset: Offset(0, 4),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Align(
            alignment: Alignment.centerRight,
            child: Text(
              time,
              style: const TextStyle(
                color: Colors.black,
                fontSize: 10,
                fontFamily: 'JejuGothic',
                fontWeight: FontWeight.w600, // 1. 진하게
              ),
            ),
          ),
          Text(
            message,
            style: const TextStyle(
              color: Colors.black,
              fontSize: 14,
              fontFamily: 'JejuGothic',
              fontWeight: FontWeight.w600, // 1. 진하게
            ),
          ),
          const SizedBox(height: 3), // 2. 간격 줄이기
          Row(
            mainAxisAlignment: MainAxisAlignment.end,
            children: [
              const Icon(Icons.check_circle, color: Colors.green, size: 24),
              const SizedBox(width: 8),
              const Icon(Icons.cancel, color: Colors.red, size: 24),
            ],
          ),
        ],
      ),
    );
  }
}