// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'dart:async';

import 'package:async/async.dart';
import 'package:flutter/services.dart';
import 'package:test/test.dart';
import 'package:battery/battery.dart';
import 'package:mockito/mockito.dart';

void main() {
  const MethodChannel methodChannel  = MethodChannel('plugins.flutter.io/battery');
  MockEventChannel eventChannel;
  Battery battery;

  setUp(() {
    eventChannel = MockEventChannel();
    battery = Battery.private(methodChannel, eventChannel);
  });

  tearDown(() {
    methodChannel.setMockMethodCallHandler(null);
  });

  test('batteryLevel', () async {
    methodChannel.setMockMethodCallHandler((MethodCall _) async => 42);
    expect(await battery.batteryLevel, 42);
  });

  group('battery state', () {
    StreamController<String> controller;

    setUp(() {
      controller = StreamController<String>();
      when(eventChannel.receiveBroadcastStream())
          .thenAnswer((Invocation invoke) => controller.stream);
    });

    tearDown(() {
      controller.close();
    });

    test('calls receiveBroadcastStream once', () {
      battery.onBatteryStateChanged;
      battery.onBatteryStateChanged;
      battery.onBatteryStateChanged;
      verify(eventChannel.receiveBroadcastStream()).called(1);
    });

    test('receive values', () async {
      final StreamQueue<BatteryState> queue =
          StreamQueue<BatteryState>(battery.onBatteryStateChanged);

      controller.add("full");
      expect(await queue.next, BatteryState.full);

      controller.add("discharging");
      expect(await queue.next, BatteryState.discharging);

      controller.add("charging");
      expect(await queue.next, BatteryState.charging);

      controller.add("illegal");
      expect(queue.next, throwsArgumentError);
    });
  });
}

class MockEventChannel extends Mock implements EventChannel {}
