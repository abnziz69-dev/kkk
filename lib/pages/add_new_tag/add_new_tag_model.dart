import '/backend/sqlite/sqlite_manager.dart';
import '/flutter_flow/flutter_flow_util.dart';
import '/flutter_flow/form_field_controller.dart';
import '/index.dart';
import 'add_new_tag_widget.dart' show AddNewTagWidget;
import 'package:flutter/material.dart';

class AddNewTagModel extends FlutterFlowModel<AddNewTagWidget> {
  ///  Local state fields for this page.

  String scannedTagId = ' ';

  bool canSave = false;

  bool saveSuccess = false;

  ///  State fields for stateful widgets in this page.

  // State field(s) for DescTextField widget.
  FocusNode? descTextFieldFocusNode;
  TextEditingController? descTextFieldTextController;
  String? Function(BuildContext, String?)? descTextFieldTextControllerValidator;
  // State field(s) for DropDown widget.
  String? dropDownValue;
  FormFieldController<String>? dropDownValueController;
  // State field(s) for SerialTextField widget.
  FocusNode? serialTextFieldFocusNode;
  TextEditingController? serialTextFieldTextController;
  String? Function(BuildContext, String?)?
      serialTextFieldTextControllerValidator;
  // State field(s) for TagIdTextField widget.
  FocusNode? tagIdTextFieldFocusNode;
  TextEditingController? tagIdTextFieldTextController;
  String? Function(BuildContext, String?)?
      tagIdTextFieldTextControllerValidator;
  // Stores action output result for [Backend Call - SQLite (CheckTagExists)] action in Container widget.
  List<CheckTagExistsRow>? checkTagExistsResult;

  @override
  void initState(BuildContext context) {}

  @override
  void dispose() {
    descTextFieldFocusNode?.dispose();
    descTextFieldTextController?.dispose();

    serialTextFieldFocusNode?.dispose();
    serialTextFieldTextController?.dispose();

    tagIdTextFieldFocusNode?.dispose();
    tagIdTextFieldTextController?.dispose();
  }
}
