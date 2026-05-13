// 현재 장착 아이템 조회

class EquippedItemModel {
  final int itemId;
  final String itemName;
  final String itemType;
  final String splineTriggerName; // 백엔드의 splineTriggerName 필드 매핑
  final bool isEquipped;

  EquippedItemModel({
    required this.itemId,
    required this.itemName,
    required this.itemType,
    required this.splineTriggerName,
    required this.isEquipped,
  });

  factory EquippedItemModel.fromJson(Map<String, dynamic> json) {
    return EquippedItemModel(
      itemId: json['itemId'] ?? 0,
      itemName: json['itemName'] ?? '',
      itemType: json['itemType'] ?? '',
      splineTriggerName: json['splineTriggerName'] ?? '', // 백엔드 가이드의 필드명 그대로 사용
      isEquipped: json['isEquipped'] ?? false,
    );
  }
}