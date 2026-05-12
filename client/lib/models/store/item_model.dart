class ItemModel {
  final int itemId;
  final String name;
  final String type;
  final int price;
  final String splineTriggerName;
  final int requiredLevel;
  final int? requiredJobId;
  final bool isOwned;

  ItemModel({
    required this.itemId,
    required this.name,
    required this.type,
    required this.price,
    required this.splineTriggerName,
    required this.requiredLevel,
    this.requiredJobId,
    required this.isOwned,
  });

  factory ItemModel.fromJson(Map<String, dynamic> json) {
    return ItemModel(
      itemId: json['itemId'],
      name: json['name'],
      type: json['type'],
      price: json['price'],
      splineTriggerName: json['splineTriggerName'],
      requiredLevel: json['requiredLevel'],
      requiredJobId: json['requiredJobId'],
      isOwned: json['isOwned'] ?? false,
    );
  }
}