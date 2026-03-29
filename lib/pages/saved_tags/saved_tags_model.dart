import '/backend/sqlite/sqlite_manager.dart';
import '/flutter_flow/flutter_flow_util.dart';
import 'saved_tags_widget.dart' show SavedTagsWidget;
import 'package:flutter/material.dart';

class SavedTagsModel extends FlutterFlowModel<SavedTagsWidget> {
  ///  Local state fields for this page.

  String searchText = ' ';

  List<String> tags = [];
  void addToTags(String item) => tags.add(item);
  void removeFromTags(String item) => tags.remove(item);
  void removeAtIndexFromTags(int index) => tags.removeAt(index);
  void insertAtIndexInTags(int index, String item) => tags.insert(index, item);
  void updateTagsAtIndex(int index, Function(String) updateFn) =>
      tags[index] = updateFn(tags[index]);

  ///  State fields for stateful widgets in this page.

  // Stores action output result for [Backend Call - SQLite (GetAllTags)] action in SavedTags widget.
  List<GetAllTagsRow>? kk123;
  // State field(s) for TextField widget.
  FocusNode? textFieldFocusNode;
  TextEditingController? textController;
  String? Function(BuildContext, String?)? textControllerValidator;

  @override
  void initState(BuildContext context) {}

  @override
  void dispose() {
    textFieldFocusNode?.dispose();
    textController?.dispose();
  }
}
