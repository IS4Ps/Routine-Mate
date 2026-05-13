import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../view_model/reward/reward_view_model.dart';

class RewardScreen extends StatefulWidget {
  const RewardScreen({super.key});

  @override
  State<RewardScreen> createState() => _RewardScreenState();
}

class _RewardScreenState extends State<RewardScreen> {
  @override
  void initState() {
    super.initState();
    Future.microtask(() => context.read<RewardViewModel>().fetchOfflineRewards());
  }

  @override
  Widget build(BuildContext context) {
    final viewModel = context.watch<RewardViewModel>();

    final editingItems = viewModel.editingRewards.where((r) => r.rewardId == 0).toList();
    final registeredItems = viewModel.editingRewards.where((r) => r.rewardId != 0).toList();

    return Scaffold(
      backgroundColor: const Color(0xFFECF2F8),
      body: Column(
        children: [
          // 1. SettingScreen과 100% 동일한 크기의 상단바
          _buildCustomAppBar(),

          Expanded(
            child: SingleChildScrollView(
              padding: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 24.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('보상 기간', style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold, color: Colors.black)),
                  const SizedBox(height: 12),
                  Row(
                    children: [
                      _buildPeriodTab('주간', Icons.calendar_today_rounded, viewModel.isWeekly, () {
                        viewModel.togglePeriod(true);
                        viewModel.fetchOfflineRewards();
                      }),
                      const SizedBox(width: 12),
                      _buildPeriodTab('월간', Icons.calendar_month_rounded, !viewModel.isWeekly, () {
                        viewModel.togglePeriod(false);
                        viewModel.fetchOfflineRewards();
                      }),
                    ],
                  ),
                  const SizedBox(height: 12),
                  const Text('선택한 기간 동안의 목표 달성 일수에 따라 보상이 주어집니다',
                      style: TextStyle(color: Color(0XFF7C7D7D), fontSize: 14)),

                  const SizedBox(height: 32),

                  // --- 보상 설정 섹션 (기존의 시원한 UI 스타일 복구) ---
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text('보상 설정 추가', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                      GestureDetector(
                        onTap: () => viewModel.addRewardStep(),
                        child: const Text('+ 단계추가', style: TextStyle(color: Color(0xFFDD00FF), fontSize: 14, fontWeight: FontWeight.bold)),
                      ),
                    ],
                  ),
                  const SizedBox(height: 16),

                  ...editingItems.map((reward) {
                    int idx = viewModel.editingRewards.indexOf(reward);
                    return _buildRewardCard(idx, reward.targetDays.toString(), reward.rewardPromiseText, viewModel, isNew: true);
                  }).toList(),

                  if (registeredItems.isNotEmpty) ...[
                    const SizedBox(height: 32),
                    const Text('등록된 보상 목록', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                    const SizedBox(height: 16),
                    ...registeredItems.map((reward) {
                      int idx = viewModel.editingRewards.indexOf(reward);
                      return _buildRewardCard(idx, reward.targetDays.toString(), reward.rewardPromiseText, viewModel, isNew: false);
                    }).toList(),
                  ],

                  if (viewModel.isLoading)
                    const Center(child: Padding(padding: EdgeInsets.all(20), child: CircularProgressIndicator())),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  // SettingScreen의 AppBar 크기를 한 픽셀도 안 틀리고 동일하게 적용
  Widget _buildCustomAppBar() {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.only(top: 50, left: 20, right: 20, bottom: 15),
      color: Colors.white,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: const [
          Text('보상 설정', style: TextStyle(color: Colors.black, fontWeight: FontWeight.bold, fontSize: 20)),
          Text('루틴 달성 일수에 따른 보상 관리', style: TextStyle(color: Color(0XFF7C7D7D), fontSize: 12)),
        ],
      ),
    );
  }

  Widget _buildPeriodTab(String title, IconData icon, bool isSelected, VoidCallback onTap) {
    return Expanded(
      child: GestureDetector(
        onTap: onTap,
        child: Container(
          height: 90, // 기존의 넉넉한 높이 복구
          decoration: BoxDecoration(
            color: isSelected ? const Color(0xFFA6C8FF) : Colors.white,
            borderRadius: BorderRadius.circular(15),
            border: Border.all(color: isSelected ? Colors.transparent : Colors.grey.shade200),
          ),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(icon, color: isSelected ? Colors.black87 : Colors.grey, size: 28),
              const SizedBox(height: 6),
              Text(title, style: TextStyle(fontWeight: FontWeight.bold, color: isSelected ? Colors.black87 : Colors.grey)),
            ],
          ),
        ),
      ),
    );
  }

  // --- 기존의 보상 카드 스타일 복구 ---
  Widget _buildRewardCard(int index, String days, String rewardContent, RewardViewModel viewModel, {required bool isNew}) {
    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 18),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: const Color(0xFF7C7D7D), width: 0.55),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.02), blurRadius: 8, offset: const Offset(0, 2))],
      ),
      child: Column(
        children: [
          Row(
            children: [
              Container(
                width: 54, height: 24,
                decoration: BoxDecoration(
                  border: Border.all(color: const Color(0xFF7C7D7D), width: 0.55),
                  borderRadius: BorderRadius.circular(6),
                  color: isNew ? Colors.white : const Color(0xFFF5F5F5),
                ),
                alignment: Alignment.center,
                child: TextField(
                  enabled: isNew,
                  textAlign: TextAlign.center,
                  keyboardType: TextInputType.number,
                  style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                  decoration: const InputDecoration(border: InputBorder.none, isDense: true, contentPadding: EdgeInsets.zero),
                  onChanged: (val) => viewModel.updateRewardEntry(index, targetDays: int.tryParse(val)),
                  controller: TextEditingController.fromValue(TextEditingValue(
                    text: days == "0" ? "" : days,
                    selection: TextSelection.collapsed(offset: days == "0" ? 0 : days.length),
                  )),
                ),
              ),
              const SizedBox(width: 8),
              const Text('일 달성 시', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
              const Spacer(),
              if (isNew) ...[
                GestureDetector(
                  onTap: () => viewModel.saveRewardStep(index),
                  child: const Icon(Icons.check, color: Color(0xFF00FF00), size: 22),
                ),
                const SizedBox(width: 12),
              ],
              GestureDetector(
                onTap: () => viewModel.removeRewardStep(index),
                child: Icon(isNew ? Icons.close : Icons.delete_outline, color: const Color(0xFFFF0000), size: 20),
              ),
            ],
          ),
          const SizedBox(height: 16),
          Row(
            children: [
              Expanded(
                flex: 3,
                child: _buildEditableInputBox(
                  rewardContent,
                  '보상 종류(예: 게임 시간, 용돈)',
                      (val) => viewModel.updateRewardEntry(index, text: val),
                  enabled: isNew,
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                flex: 1,
                child: _buildEditableInputBox(
                  '', '수량',
                      (val) {},
                  textAlign: TextAlign.center,
                  enabled: isNew,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildEditableInputBox(String value, String hint, Function(String) onChanged, {TextAlign textAlign = TextAlign.start, bool enabled = true}) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 14),
      decoration: BoxDecoration(
        color: enabled ? Colors.white : const Color(0xFFF5F5F5),
        borderRadius: BorderRadius.circular(6),
        border: Border.all(color: const Color(0xFF7C7D7D), width: 0.55),
      ),
      child: TextField(
        enabled: enabled,
        onChanged: onChanged,
        textAlign: textAlign,
        style: const TextStyle(color: Colors.black, fontSize: 12),
        decoration: InputDecoration(
          hintText: hint,
          hintStyle: const TextStyle(color: Color(0xFF7C7D7D), fontSize: 12),
          border: InputBorder.none,
          isDense: true,
          contentPadding: const EdgeInsets.symmetric(vertical: 10),
        ),
        controller: TextEditingController.fromValue(TextEditingValue(
          text: value,
          selection: TextSelection.collapsed(offset: value.length),
        )),
      ),
    );
  }
}