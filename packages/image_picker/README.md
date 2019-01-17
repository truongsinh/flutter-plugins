# Fork of official companion plugins
This `image_picker_modern` is a fork of official companion plugin `image_picker` with several changes/enhancements, while the maintainer commit to keep the API compatible and code up-to-date with the upstream. Just replace `image_picker` with `image_picker_modern` in the official instruction to use.

Vision:
- Visible unit test coverage [![](https://shields-staging-pr-2473.herokuapp.com/badge/endpoint.svg?url=https://flutter-plugins-coverage.netlify.com/image_picker.json)](https://codecov.io/gh/truongsinh/flutter-plugins/tree/master/packages/image_picker)
- Unit test for all parts (dart, ios, android)
- Convert all plugins to Swift/Kotlin for better engagement with the community using modern languages
- Fix cash with unit test [flutter/flutter#21863](https://github.com/flutter/flutter/issues/21863) `image_picker: FATAL EXCEPTION when choosing an image from downloads`
- Ultimately merged back to upstream

# Image Picker plugin for Flutter

[![pub package](https://img.shields.io/pub/v/image_picker_modern.svg)](https://pub.dartlang.org/packages/image_picker_modern)

A Flutter plugin for iOS and Android for picking images from the image library,
and taking new pictures with the camera.

*Note*: This plugin is still under development, and some APIs might not be available yet. [Feedback welcome](https://github.com/flutter/flutter/issues) and [Pull Requests](https://github.com/truongsinh/flutter-plugins/pulls) are most welcome!

## Installation

First, add `image_picker_modern` as a [dependency in your pubspec.yaml file](https://flutter.io/platform-plugins/).

### iOS

Add the following keys to your _Info.plist_ file, located in `<project root>/ios/Runner/Info.plist`:

* `NSPhotoLibraryUsageDescription` - describe why your app needs permission for the photo library. This is called _Privacy - Photo Library Usage Description_ in the visual editor.
* `NSCameraUsageDescription` - describe why your app needs access to the camera. This is called _Privacy - Camera Usage Description_ in the visual editor.
* `NSMicrophoneUsageDescription` - describe why your app needs access to the microphone, if you intend to record videos. This is called _Privacy - Microphone Usage Description_ in the visual editor.

### Android

No configuration required - the plugin should work out of the box.

### Example

``` dart
import 'package:image_picker_modern/image_picker_modern.dart';

class MyHomePage extends StatefulWidget {
  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  File _image;

  Future getImage() async {
    var image = await ImagePicker.pickImage(source: ImageSource.camera);

    setState(() {
      _image = image;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Image Picker Example'),
      ),
      body: Center(
        child: _image == null
            ? Text('No image selected.')
            : Image.file(_image),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: getImage,
        tooltip: 'Pick Image',
        child: Icon(Icons.add_a_photo),
      ),
    );
  }
}
```
