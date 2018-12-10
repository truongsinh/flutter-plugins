import 'dart:async';

// Imports the Flutter Driver API
import 'package:flutter_driver/flutter_driver.dart';
import 'package:test/test.dart';

void main() {
  group('Device info', () {
    FlutterDriver driver;

    setUpAll(() async {
      // Connects to the app
      driver = await FlutterDriver.connect();
    });

    tearDownAll(() async {
      if (driver != null) {
        // Closes the connection
        driver.close();
      }
    });

    group('iOS', () {
      var a = {
        'name': 'iPhone XR',
        "systemName": "iOS",
        "systemVersion": "12.1",
        "model": "iPhone",
        "localizedModel": "iPhone",
        "identifierForVendor": '12EE650B-3A0B-4A25-AB82-3516677A48D5',
        "isPhysicalDevice": "false",
        "utsname.sysname": 'Darwin',
        'utsname.nodename': 'Wolfie-MBP.local',
        'utsname.release': '18.2.0',
        'utsname.version':
            'Darwin Kernel Version 18.2.0: Fri Oct  5 19:41:49 PDT 2018; root:xnu-4903.221.2~2/RELEASE_X86_64',
        'utsname.machine': 'x86_64',
      };
      a.forEach((k, v) => test(k, () async {
            expect(await driver.getText(find.byValueKey(k)), v);
          }));
    }, tags: "ios");


    group('Android', () {
      var a = {
    'version':'',
    'board':'',
    'bootloader':'',
    'brand':'',
    'device':'',
      };
      a.forEach((k, v) => test(k, () async {
            expect(await driver.getText(find.byValueKey(k)), v);
          }));
    }, tags: "android");
  });
}
