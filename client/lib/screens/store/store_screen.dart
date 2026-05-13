import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:frontend/view_model/store/store_view_model.dart';
import 'package:frontend/view_model/store/inventory_view_model.dart';

import '../../models/store/item_model.dart';

class StoreScreen extends StatefulWidget {
  const StoreScreen({super.key});

  @override
  State<StoreScreen> createState() => _StoreScreenState();
}

class _StoreScreenState extends State<StoreScreen> {
  int _selectedIndex = 0;
  final List<String> _categories = ["상점", "장비", "보상"];

  int? _activeIndex;
  Set<int> _purchasedItemIds = {};

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<StoreViewModel>(context, listen: false).loadStoreItems(1, 1);

      Provider.of<InventoryViewModel>(context, listen: false)
          .loadInventory()
          .then((_) {
        final inventoryItems = Provider.of<InventoryViewModel>(context, listen: false).items;
        setState(() {
          _purchasedItemIds = inventoryItems.map((e) => e.itemId).toSet();
        });
      });
    });
  }

  void _onTabTapped(int index) {
    setState(() => _selectedIndex = index);
    if (index == 1) {
      Provider.of<InventoryViewModel>(context, listen: false).loadInventory();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        automaticallyImplyLeading: false,
        title: const Text(
          "상점",
          style: TextStyle(
            color: Colors.white,
            fontSize: 28,
            fontWeight: FontWeight.bold,
          ),
        ),
        centerTitle: false,
        backgroundColor: const Color(0xFF1586E2),
        elevation: 0,
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(12),
            child: Row(
              children: List.generate(_categories.length, (index) {
                return Padding(
                  padding: EdgeInsets.only(right: index < _categories.length - 1 ? 10 : 0),
                  child: GestureDetector(
                    onTap: () => _onTabTapped(index),
                    child: _categoryButton(_categories[index], _selectedIndex == index),
                  ),
                );
              }),
            ),
          ),
          Expanded(
            child: IndexedStack(
              index: _selectedIndex,
              children: [
                _buildShopGrid(),
                _buildEquipmentGrid(),
                _buildRewardGrid(),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildShopGrid() {
    return Consumer<StoreViewModel>(
      builder: (context, viewModel, child) {
        if (viewModel.isLoading) {
          return const Center(child: CircularProgressIndicator());
        }
        return GestureDetector(
          onTap: () => setState(() => _activeIndex = null),
          child: GridView.builder(
            padding: const EdgeInsets.all(16),
            itemCount: viewModel.items.length,
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 3,
              crossAxisSpacing: 30,
              mainAxisSpacing: 30,
              childAspectRatio: 0.7,
            ),
            itemBuilder: (context, index) {
              final item = viewModel.items[index];
              return _itemCard(item, _activeIndex == index, () => setState(() => _activeIndex = index));
            },
          ),
        );
      },
    );
  }

  Widget _buildEquipmentGrid() {
    return Consumer<InventoryViewModel>(
      builder: (context, viewModel, child) {
        if (viewModel.isLoading) {
          return const Center(child: CircularProgressIndicator());
        }
        return GestureDetector(
          onTap: () => setState(() => _activeIndex = null),
          child: GridView.builder(
            padding: const EdgeInsets.all(16),
            itemCount: viewModel.items.length,
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 3,
              crossAxisSpacing: 30,
              mainAxisSpacing: 30,
              childAspectRatio: 0.7,
            ),
            itemBuilder: (context, index) {
              final item = viewModel.items[index];
              return _equipmentCard(
                item.itemName,
                item.isEquipped,
                _activeIndex == index,
                    () => setState(() => _activeIndex = index),
                    () async {
                  await Provider.of<InventoryViewModel>(context, listen: false)
                      .equipItem(item.inventoryId);
                  setState(() => _activeIndex = null);
                },
              );
            },
          ),
        );
      },
    );
  }

  Widget _equipmentCard(String label, bool isEquipped, bool isActive, VoidCallback onTap, VoidCallback onUnequip) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(15),
          border: isActive
              ? Border.all(color: const Color(0xFF1586E2), width: 2)
              : Border.all(color: Colors.transparent, width: 2),
          boxShadow: const [
            BoxShadow(color: Colors.black12, blurRadius: 4, offset: Offset(0, 4)),
          ],
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Expanded(
              child: Stack(
                children: [
                  Center(
                    child: Container(
                      width: 75,
                      height: 75,
                      decoration: const BoxDecoration(
                        color: Color(0xFFD9D9D9),
                        shape: BoxShape.circle,
                      ),
                    ),
                  ),
                  if (isEquipped)
                    Positioned(
                      top: 5,
                      left: 4,
                      child: Container(
                        padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 1),
                        decoration: BoxDecoration(
                          color: Colors.grey[400],
                          borderRadius: BorderRadius.circular(10),
                        ),
                        child: const Text(
                          "장착 중",
                          style: TextStyle(fontSize: 11, color: Colors.black87, fontWeight: FontWeight.bold),
                        ),
                      ),
                    ),
                ],
              ),
            ),
            if (isActive)
              GestureDetector(
                onTap: onUnequip,
                child: Container(
                  margin: const EdgeInsets.only(bottom: 10, left: 10, right: 10),
                  padding: const EdgeInsets.symmetric(vertical: 2),
                  width: double.infinity,
                  decoration: BoxDecoration(
                    color: isEquipped ? Colors.redAccent : const Color(0xFF1586E2), // 장착 중이면 빨강, 아니면 파랑
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: Text(
                    isEquipped ? "장착 해제" : "장착", // 장착 중이면 해제, 아니면 장착
                    textAlign: TextAlign.center,
                    style: const TextStyle(color: Colors.white, fontSize: 12, fontWeight: FontWeight.bold),
                  ),
                ),
              )
            else
              Padding(
                padding: const EdgeInsets.only(bottom: 10),
                child: Text(
                  label,
                  textAlign: TextAlign.center,
                  style: const TextStyle(fontSize: 14),
                ),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildRewardGrid() {
    final List<Map<String, String>> rewards = [
      {"title": "용돈 5000원", "subtitle": "현금으로 지급"},
      {"title": "놀이공원", "subtitle": "롯데월드 가기"},
      {"title": "게임 쿠폰", "subtitle": "한 시간 쿠폰"},
    ];

    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: rewards.length,
      itemBuilder: (context, index) {
        return Container(
          margin: const EdgeInsets.only(bottom: 12),
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 16),
          decoration: BoxDecoration(
            color: const Color(0xFFF8F8F8),
            borderRadius: BorderRadius.circular(15),
            boxShadow: const [
              BoxShadow(
                color: Color(0x3F000000),
                blurRadius: 4,
                offset: Offset(0, 4),
              ),
            ],
          ),
          child: Row(
            children: [
              Container(
                width: 46,
                height: 46,
                decoration: BoxDecoration(
                  color: Colors.grey[300],
                  shape: BoxShape.circle,
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      rewards[index]["title"]!,
                      style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      rewards[index]["subtitle"]!,
                      style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold),
                    ),
                  ],
                ),
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
                decoration: BoxDecoration(
                  color: const Color(0xFF1586E2),
                  borderRadius: BorderRadius.circular(25),
                ),
                child: const Text(
                  "사용",
                  style: TextStyle(color: Colors.white, fontSize: 12, fontWeight: FontWeight.bold),
                ),
              ),
            ],
          ),
        );
      },
    );
  }

  Widget _itemCard(ItemModel item, bool isActive, VoidCallback onTap) {
    return Consumer<StoreViewModel>(
      builder: (context, viewModel, child) {
        final isPurchased = _purchasedItemIds.contains(item.itemId);
        return GestureDetector(
          onTap: onTap,
          child: Container(
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(15),
              border: isActive
                  ? Border.all(color: const Color(0xFF1586E2), width: 2)
                  : Border.all(color: Colors.transparent, width: 2),
              boxShadow: const [
                BoxShadow(color: Colors.black12, blurRadius: 4, offset: Offset(0, 4)),
              ],
            ),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Expanded(
                  child: Center(
                    child: Container(
                      width: 75,
                      height: 75,
                      decoration: const BoxDecoration(
                        color: Color(0xFFD9D9D9),
                        shape: BoxShape.circle,
                      ),
                    ),
                  ),
                ),
                if (isActive && !isPurchased)
                  GestureDetector(
                    onTap: () async {
                      setState(() {
                        _purchasedItemIds.add(item.itemId);
                        _activeIndex = null;
                      });
                      final childId = viewModel.getChildId();
                      await viewModel.purchaseItem(item.itemId, childId);
                      if (context.mounted) {
                        await Provider.of<InventoryViewModel>(context, listen: false).loadInventory();
                      }
                    },
                    child: Container(
                      margin: const EdgeInsets.only(bottom: 10, left: 10, right: 10),
                      padding: const EdgeInsets.symmetric(vertical: 2),
                      width: double.infinity,
                      decoration: BoxDecoration(
                        color: const Color(0xFF1586E2),
                        borderRadius: BorderRadius.circular(10),
                      ),
                      child: const Text(
                        "구매",
                        textAlign: TextAlign.center,
                        style: TextStyle(color: Colors.white, fontSize: 12, fontWeight: FontWeight.bold),
                      ),
                    ),
                  )
                else
                  Padding(
                    padding: const EdgeInsets.only(bottom: 10),
                    child: Text(
                      isPurchased ? "보유 중" : "${item.price}gold",
                      textAlign: TextAlign.center,
                      style: const TextStyle(fontSize: 14),
                    ),
                  ),
              ],
            ),
          ),
        );
      },
    );
  }
}

Widget _categoryButton(String text, bool selected) {
  return Container(
    padding: const EdgeInsets.symmetric(horizontal: 23, vertical: 5),
    decoration: BoxDecoration(
      color: selected
          ? const Color(0xFF1586E2).withOpacity(0.4)
          : Colors.grey[300],
      borderRadius: BorderRadius.circular(20),
    ),
    child: Text(
      text,
      style: const TextStyle(fontWeight: FontWeight.bold),
    ),
  );
}