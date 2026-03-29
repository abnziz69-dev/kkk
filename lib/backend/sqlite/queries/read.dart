import '/backend/sqlite/queries/sqlite_row.dart';
import 'package:sqflite/sqflite.dart';

Future<List<T>> _readQuery<T>(
  Database database,
  String query,
  T Function(Map<String, dynamic>) create,
) =>
    database.rawQuery(query).then((r) => r.map((e) => create(e)).toList());

/// BEGIN GETALLTAGS
Future<List<GetAllTagsRow>> performGetAllTags(
  Database database,
) {
  final query = '''
SELECT * FROM saved_tags;
''';
  return _readQuery(database, query, (d) => GetAllTagsRow(d));
}

class GetAllTagsRow extends SqliteRow {
  GetAllTagsRow(Map<String, dynamic> data) : super(data);

  String? get nameDescription => data['name_description'] as String?;
  String? get serialNumber => data['serial_number'] as String?;
  String? get tagId => data['tag_id'] as String?;
}

/// END GETALLTAGS

/// BEGIN SEARCHTAGS
Future<List<SearchTagsRow>> performSearchTags(
  Database database, {
  String? searchText,
}) {
  final query = '''
SELECT
  name_description,
  serial_number,
  tag_id
FROM saved_tags
WHERE
  '${searchText}' = ''
  OR name_description LIKE '%' || '${searchText}' || '%'
  OR serial_number LIKE '%' || '${searchText}' || '%'
  OR tag_id LIKE '%' || '${searchText}' || '%'
ORDER BY rowid DESC;
''';
  return _readQuery(database, query, (d) => SearchTagsRow(d));
}

class SearchTagsRow extends SqliteRow {
  SearchTagsRow(Map<String, dynamic> data) : super(data);

  String? get nameDescription => data['name_description'] as String?;
  String? get serialNumber => data['serial_number'] as String?;
  String? get tagId => data['tag_id'] as String?;
}

/// END SEARCHTAGS

/// BEGIN CHECKTAGEXISTS
Future<List<CheckTagExistsRow>> performCheckTagExists(
  Database database, {
  String? tagId,
}) {
  final query = '''
SELECT tag_id
FROM saved_tags
WHERE tag_id = '${tagId}';
LIMIT 1;
''';
  return _readQuery(database, query, (d) => CheckTagExistsRow(d));
}

class CheckTagExistsRow extends SqliteRow {
  CheckTagExistsRow(Map<String, dynamic> data) : super(data);

  String? get tagId => data['tag_Id'] as String?;
}

/// END CHECKTAGEXISTS
