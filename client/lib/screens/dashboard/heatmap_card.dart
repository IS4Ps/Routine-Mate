import 'package:flutter/material.dart';

class HeatmapCard extends StatelessWidget {
  final String title;
  final String subtitle;
  final List<int> data;

  const HeatmapCard({
    super.key,
    required this.title,
    required this.subtitle,
    required this.data,
  });

  @override
  Widget build(BuildContext context) {
    final colors = [
      const Color(0xFFF1F3F5),
      const Color(0xFFBDECC7),
      const Color(0xFF2ED573),
      const Color(0xFF138A36),
    ];

    return Container(
      width: double.infinity,
      margin: const EdgeInsets.symmetric(horizontal: 20),
      padding: const EdgeInsets.fromLTRB(18, 18, 18, 18),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.only(left: 10),
            child: Text(
              title,
              style: const TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w700,
                color: Colors.black,
              ),
            ),
          ),
          const SizedBox(height: 4),
          Padding(
            padding: const EdgeInsets.only(left: 10),
            child: Text(
              subtitle,
              style: const TextStyle(
                fontSize: 12,
                color: Color(0xFF3D3D3D),
              ),
            ),
          ),
          const SizedBox(height: 25),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20),
            child: GridView.builder(
              padding: EdgeInsets.zero,
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: data.length,
              gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 13,
                crossAxisSpacing: 3,
                mainAxisSpacing: 3,
                childAspectRatio: 1,
              ),
              itemBuilder: (context, index) {
                return Container(
                  decoration: BoxDecoration(
                    color: colors[data[index]],
                    borderRadius: BorderRadius.circular(5),
                  ),
                );
              },
            ),
          ),
          const SizedBox(height: 10),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20),
            child: const Row(
              mainAxisAlignment: MainAxisAlignment.start,
              children: [
                Text('1월', style: TextStyle(fontSize: 12, color: Color(0xFF3D3D3D))),
                SizedBox(width: 76),
                Text('2월', style: TextStyle(fontSize: 12, color: Color(0xFF3D3D3D))),
                SizedBox(width: 60),
                Text('3월', style: TextStyle(fontSize: 12, color: Color(0xFF3D3D3D))),
              ],
            ),
          ),
        ],
      ),
    );
  }
}