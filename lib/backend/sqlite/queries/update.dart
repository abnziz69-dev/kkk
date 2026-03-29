import 'package:sqflite/sqflite.dart';

/// BEGIN INSERTTAG
Future performInsertTag(
  Database database, {
  String? nameDesc,
  String? serialNumber,
  String? tagId,
}) {
  final query = '''
INSERT INTO saved_tags (name_description,serial_number,tag_id)
 VALUES ('${nameDesc}','${serialNumber}','${tagId}');
''';
  return database.rawQuery(query);
}

/// END INSERTTAG

/// BEGIN UPDATETAG
Future performUpdateTag(
  Database database, {
  String? nameDesc,
  String? serialNumber,
  String? tagId,
}) {
  final query = '''
UPDATE saved_tags
SET
  name_description = '${nameDesc}',
  serial_number = '${serialNumber}'
WHERE tag_id = '${tagId}';
''';
  return database.rawQuery(query);
}

/// END UPDATETAG

/// BEGIN DELETETAG
Future performDeleteTag(
  Database database, {
  String? tagId,
}) {
  final query = '''
DELETE FROM saved_tags;

''';
  return database.rawQuery(query);
}

/// END DELETETAG
