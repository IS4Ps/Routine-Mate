import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'dart:io';
import 'package:frontend/services/quest/quiz_api_service.dart';
import 'ox_quiz_screen.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class OxScreen extends StatefulWidget {
  final VoidCallback? onBack;

  const OxScreen({super.key, this.onBack});

  @override
  State<OxScreen> createState() => _OxScreenState();
}

class _OxScreenState extends State<OxScreen> {
  final ImagePicker _picker = ImagePicker();
  List<XFile> _selectedImages = [];
  bool _isLoading = false;

  Future<void> _pickImages() async {
    final List<XFile> images = await _picker.pickMultiImage();
    if (images.isNotEmpty) {
      setState(() {
        _selectedImages = images.take(4).toList();
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        backgroundColor: const Color(0xFF1586E2),
        elevation: 0,
        leadingWidth: 100,
        leading: GestureDetector(
          onTap: () {
            if (widget.onBack != null) {
              widget.onBack!();
            } else {
              Navigator.pop(context);
            }
          },
          child: Container(
            margin: const EdgeInsets.only(left: 16, top: 15, bottom: 15),
            padding: const EdgeInsets.symmetric(horizontal: 20),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(50),
              boxShadow: const [
                BoxShadow(
                  color: Colors.black26,
                  blurRadius: 4,
                  offset: Offset(0, 2),
                ),
              ],
            ),
            child: const Padding(
              padding: EdgeInsets.only(top: 3),
              child: Text(
                '← Back',
                style: TextStyle(color: Colors.black, fontSize: 12),
              ),
            ),
          ),
        ),
        title: const Text(
          'OX 퀴즈 이미지 선택',
          style: TextStyle(
            color: Colors.white,
            fontSize: 20,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
      ),
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            GestureDetector(
              onTap: _pickImages,
              child: Container(
                width: double.infinity,
                height: 90,
                decoration: BoxDecoration(
                  color: const Color(0xFF1586E2),
                  borderRadius: BorderRadius.circular(15),
                  boxShadow: const [
                    BoxShadow(
                      color: Colors.black26,
                      blurRadius: 4,
                      offset: Offset(0, 4),
                    ),
                  ],
                ),
                child: const Center(
                  child: Text(
                    '앨범에서 이미지 선택하기',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 20,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
              ),
            ),
            const SizedBox(height: 24),
            const Text(
              '선택된 이미지',
              style: TextStyle(color: Color(0xFF7C7D7D), fontSize: 15),
            ),
            const SizedBox(height: 12),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: List.generate(4, (index) {
                if (index < _selectedImages.length) {
                  return ClipRRect(
                    borderRadius: BorderRadius.circular(8),
                    child: Image.file(
                      File(_selectedImages[index].path),
                      width: 75,
                      height: 75,
                      fit: BoxFit.cover,
                    ),
                  );
                }
                return _buildImageSlot();
              }),
            ),
            const Spacer(),
            if (_isLoading)
              const Center(
                child: Column(
                  children: [
                    CircularProgressIndicator(
                      color: Color(0xFF1586E2),
                      strokeWidth: 4,
                    ),
                    SizedBox(height: 16),
                    Text(
                      'AI가 이미지를 분석 중이에요!',
                      style: TextStyle(fontSize: 16, color: Colors.black),
                    ),
                  ],
                ),
              ),
            const Spacer(),
            Center(
              child: GestureDetector(
                onTap: () async {
                  if (_selectedImages.isEmpty) return;

                  setState(() {
                    _isLoading = true;
                  });

                  try {
                    final token = dotenv.env['TEST_TOKEN'] ?? '';
                    final service = QuizApiService();
                    final quizzes = await service.generateQuiz(_selectedImages, 5, token);

                    if (mounted) {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => OxQuizScreen(quizzes: quizzes),
                        ),
                      );
                    }
                  } catch (e) {
                    print('[에러] $e');
                  } finally {
                    setState(() {
                      _isLoading = false;
                    });
                  }
                },
                child: Container(
                  width: 150,
                  height: 50,
                  decoration: BoxDecoration(
                    color: const Color(0xFF1586E2),
                    borderRadius: BorderRadius.circular(25),
                  ),
                  child: const Center(
                    child: Text(
                      '선택 완료',
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 20,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildImageSlot() {
    return Container(
      width: 75,
      height: 75,
      decoration: const BoxDecoration(
        color: Color(0xFFD9D9D9),
        boxShadow: [
          BoxShadow(
            color: Colors.black26,
            blurRadius: 4,
            offset: Offset(0, 4),
          ),
        ],
      ),
    );
  }
}