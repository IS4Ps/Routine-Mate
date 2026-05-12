class InventoryModel {
  final int inventoryId;
  final int itemId;
  final String itemName;
  final String itemType;
  final String? splineTriggerName;
  final bool isEquipped;

  InventoryModel({
    required this.inventoryId,
    required this.itemId,
    required this.itemName,
    required this.itemType,
    this.splineTriggerName,
    required this.isEquipped,
  });

  factory InventoryModel.fromJson(Map<String, dynamic> json) {
    return InventoryModel(
      inventoryId: json['inventoryId'],
      itemId: json['itemId'],
      itemName: json['itemName'],
      itemType: json['itemType'],
      splineTriggerName: json['splineTriggerName'],
      isEquipped: json['isEquipped'] ?? false,
    );
  }
}