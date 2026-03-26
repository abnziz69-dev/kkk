import 'package:flutter/foundation.dart';

import '/backend/sqlite/init.dart';
import 'queries/read.dart';
import 'queries/update.dart';

import 'package:sqflite/sqflite.dart';
export 'queries/read.dart';
export 'queries/update.dart';

class SQLiteManager {
  SQLiteManager._();

  static SQLiteManager? _instance;
  static SQLiteManager get instance => _instance ??= SQLiteManager._();

  static late Database _database;
  Database get database => _database;

  static Future initialize() async {
    if (kIsWeb) {
      return;
    }
    _database = await initializeDatabaseFromDbFile(
      'savedtags',
      'saved_tags.db',
    );
  }

  /// START READ QUERY CALLS

  Future<List<GetAllTagsRow>> getAllTags() => performGetAllTags(
        _database,
      );

  Future<List<SearchTagsRow>> searchTags({
    String? searchText,
  }) =>
      performSearchTags(
        _database,
        searchText: searchText,
      );

  /// END READ QUERY CALLS

  /// START UPDATE QUERY CALLS

  Future insertTag({
    String? nameDesc,
    String? serialNumber,
    String? tagId,
  }) =>
      performInsertTag(
        _database,
        nameDesc: nameDesc,
        serialNumber: serialNumber,
        tagId: tagId,
      );

  Future updateTag({
    String? nameDesc,
    String? serialNumber,
    String? tagId,
  }) =>
      performUpdateTag(
        _database,
        nameDesc: nameDesc,
        serialNumber: serialNumber,
        tagId: tagId,
      );

  Future deleteTag({
    String? tagId,
  }) =>
      performDeleteTag(
        _database,
        tagId: tagId,
      );

  /// END UPDATE QUERY CALLS
}
