# android-imagecapture-poc
Written by Sayed Hamzah (Twitter handle: @xxxbaemaxxx)

Proof of concept for an Android application to perform an image capture without requiring write permissions to the external storage.

This application has been tested on API Level 19.

## Added files

- res/xml/image_path.xml (for FileProvider)
- For AndroidManifest.xml, additional permissions added:

<uses-feature android:name="android.hardware.camera" android:required="false" />
<uses-feature
    android:name="android.hardware.camera.any"
    android:required="true" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.CAMERA" />

## Application Functions

- Take Photo - takes a photo using the camera and stores the file temporarily within the internal storage (tempimage/temp.jpg).
- Select from Gallery - select a photo from the phone media gallery.
- Upload Photo - encodes the photo data in Base64 and saves within the application internal storage. At the same time, tempimage/temp.jpg will be deleted from the internal storage.

## Setup

- Clone the repository and load it as an Android Studio project. You should be able to launch it using an Android Emulator such as AVD and Genymotion.
