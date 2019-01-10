import 'package:flutter_test/flutter_test.dart';

import 'package:android_intent/android_intent.dart';

void main() {
  test('dummy', () async {
    final AndroidIntent _ =  const AndroidIntent(
      action: 'action_view',
      data: 'https://play.google.com/store/apps/details?'
          'id=com.google.android.apps.myapp',
      arguments: <String, dynamic>{},
  );
  });
}
