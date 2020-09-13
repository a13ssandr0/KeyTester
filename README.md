# KeyTester

Test the behaviour of quite any key on your Android device.

KeyTester on Android 10

<img src="https://github.com/a13ssandr0/KeyTester/blob/master/KeyTester%20Android10.jpg" width="250">

Some buttons such as Home, Recent Apps (Multitasking) and Power won't be detected because the system needs them to be always available and working (e.g., when an app freezes and the user wants to terminate it), so it won't let any app intercept them (at least without some special privileges).

Each time your press a button, it will be registered and displayed in a list that you can save and share easily.

The core of the app is the method `Activity.dispatchKeyEvent`, from it we get a `KeyEvent` and then a `KeyCode` from one of these [constants](https://developer.android.com/reference/android/view/KeyEvent#constants).
Note that the app will output only a numeric constant when run on APIs prior to version 12 (Android 3.1 Honeycomb), because the method [keyCodeToString](https://developer.android.com/reference/android/view/KeyEvent#keyCodeToString(int)) was implemented in that API version.

All the constants work with:
- [Activity.dispatchKeyEvent](https://developer.android.com/reference/android/app/Activity#dispatchKeyEvent(android.view.KeyEvent))
- [Activity.dispatchKeyShortcutEvent](https://developer.android.com/reference/android/app/Activity#dispatchKeyShortcutEvent(android.view.KeyEvent))
- [Activity.onKeyDown](https://developer.android.com/reference/android/app/Activity#onKeyDown(int,%20android.view.KeyEvent))
- [Activity.onKeyLongPress](https://developer.android.com/reference/android/app/Activity#onKeyLongPress(int,%20android.view.KeyEvent))
- [Activity.onKeyMultiple](https://developer.android.com/reference/android/app/Activity#onKeyMultiple(int,%20int,%20android.view.KeyEvent))
- [Activity.onKeyShortcut](https://developer.android.com/reference/android/app/Activity#onKeyShortcut(int,%20android.view.KeyEvent))
- [Activity.onKeyUp](https://developer.android.com/reference/android/app/Activity#onKeyUp(int,%20android.view.KeyEvent))
- any other method that deals with KeyEvents

[![Get it on Google Play](https://github.com/a13ssandr0/KeyTester/blob/master/google-play-badge.png)](https://play.google.com/store/apps/details?id=a13ssandr0.keytester)
