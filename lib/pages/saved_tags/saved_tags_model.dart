import '/backend/sqlite/sqlite_manager.dart';
import '/flutter_flow/flutter_flow_util.dart';
import 'saved_tags_widget.dart' show SavedTagsWidget;
import 'package:flutter/material.dart';

class SavedTagsModel extends FlutterFlowModel<SavedTagsWidget> {
  ///  Local state fields for this page.

  String searchText = ' ';

  ///  State fields for stateful widgets in this page.

  // Stores action output result for [Backend Call - SQLite (GetAllTags)] action in SavedTags widget.
  List<GetAllTagsRow>? getAllTagsResult;

  @override
  void initState(BuildContext context) {}

  @override
  void dispose() {}
}
