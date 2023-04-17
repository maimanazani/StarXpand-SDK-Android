<p align="center">
  <img
    src="docs/logo.png"
    width="600"
    style="margin-top: 20px; margin-bottom: 20px;"
  />
</p>

- [日本語はこちら](docs/README_JP.md)

# StarXpand SDK for Android

`StarXpand SDK for Android` is a software development kit for supporting application development for Star Micronics devices.

This software development kit provides the StarIO10 library (StarIO10.aar) as a library to control the Star Micronics devices.

## Documentation

Please refer [here](https://www.star-m.jp/starxpandsdk-oml.html) for StarXpand SDK documentation.

Documentation includes an overview of the SDK, how to build a sample application, how to use the API, and a API reference.

## Requirements

| Platform | Version | Arch |
| --- | --- | --- |
| Android | Android 6.0 or later | arm64-v8a, armeabi-v7a, x86, x86_64 |

## Installation

### 1. Add the StarIO10 library to your project

In order to integrate the StarIO10 library into your Android application, Use Maven repository. Add the following dependencies to the `dependencies` block in `app/build.gradle`.

The `VERSION_NUMBER` part is the version of the library. Please refer to [app/build.gradle](app/build.gradle) for the latest version of the library.

```gradle
dependencies {
    implementation 'com.starmicronics:stario10:VERSION_NUMBER'
    ...
}
```

For more information on how to integrate a library into your application, please refer to the following URL.

https://developer.android.com/studio/build/dependencies

### 2. Add settings to your project
#### 2.1. When using a Bluetooth printer 

Refer to [sample code](app/src/main/java/com/starmicronics/starxpandsdk) and obtain BLUETOOTH_CONNECT permission before starting to communicate with or discover printers.

#### 2.2. To prevent the connection permission dialog from being displayed every time the USB cable is plugged in or unplugged

When communicating with a USB printer, a dialog box will appear asking for connection permission. This permission is reset when the USB cable is plugged in or unplugged (including when the printer is turned on or off).

If you do not want to display the connection permission dialog every time the USB cable is plugged in or unplugged, configure the following settings. This setting will also allow the application to start automatically when the USB cable is plugged.

##### 2.2.1. Add settings to AndroidManifest.xml
Add the following `<intent-filter>` and `<meta-data>` elements to AndroidManifest.xml.

```xml
<intent-filter>
    <action android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" />
    <action android:name="android.hardware.usb.action.USB_ACCESSORY_ATTACHED" />
</intent-filter>

<meta-data android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" android:resource="@xml/device_filter" />
<meta-data android:name="android.hardware.usb.action.USB_ACCESSORY_ATTACHED" android:resource="@xml/accessory_filter" />
```

##### 2.2.2. Add a resource file
Store the following resource files under `res/xml` with the names `device_filter.xml` and `accessory_filter.xml`.

- device_filter.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <usb-device class="255" subclass="66" protocol="1" />
    <usb-device vendor-id="1305" product-id="0003" />   <!--TSP100IIU+/IIIU/IV  - printerClass-->
    <usb-device vendor-id="1305" product-id="0071" />   <!--mC-Print3           - printerClass-->
    <usb-device vendor-id="1305" product-id="0073" />   <!--mC-Print2           - printerClass-->
    <usb-device vendor-id="1305" product-id="0023" />   <!--mPOP                - printerClass-->
</resources>
```

- accessory_filter.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <usb-accessory model="Star TSP143IV-UE" manufacturer="STAR"/>
</resources>
```

## Examples

StarXpand SDK includes an example application that can be used in combination with the printer to check its operation. Please use it in conjunction with the explanations of each function in the linked pages.

#### 1. [Discover printers](https://star-m.jp/products/s_print/sdk/starxpand/manual/en/searchPrinter.html)

#### 2. [Create printing data](https://star-m.jp/products/s_print/sdk/starxpand/manual/en/basic-step1.html)

Printing samples of labels (sample code and print results) for each type of business that you can use for your print layouts are [also available](app/src/main/java/com/starmicronics/starxpandsdk/printingsamples/PrintingSamples.md).

> :warning: Some printer models may not be able to print some samples. Please adjust the layout accordingly when using this samples.

#### 3. [Send printing data](https://star-m.jp/products/s_print/sdk/starxpand/manual/en/basic-step2.html)

#### 4. [Send printing data using spooler function](https://star-m.jp/products/s_print/sdk/starxpand/manual/en/spooler.html)

#### 5. [Get printer status](#GetPrinterStatus)

#### 6. [Monitor printer](#MonitorPrinter)

<a id="GetPrinterStatus"></a>
### Get printer status

```kotlin
fun getStatus() {
    // Specify your printer connection settings.
    val settings = StarConnectionSettings(interfaceType.Lan, "00:11:62:00:00:00")
    val printer = StarPrinter(settings, applicationContext)

    val job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.Default + job)

    scope.launch {
        try {
            // Connect to the printer.
            printer.openAsync().await()

            // Get printer status.
            val status = printer.getStatusAsync().await()
            Log.d("Status", "${status}")
        } catch (e: Exception) {
            // Exception.
            Log.d("Status", "${e.message}")
        } finally {
            // Disconnect from the printer.
            printer.closeAsync().await()
        }
    }
}
```

<a id="MonitorPrinter"></a>
### Monitor printer

```kotlin
fun monitor() {
    val job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.Default + job)

    scope.launch {
        // Specify your printer connection settings.
        val settings = StarConnectionSettings(interfaceType.Lan, "00:11:62:00:00:00")
        printer = StarPrinter(settings, applicationContext)

        // Callback for printer state changed.
        printer?.printerDelegate = object : PrinterDelegate() {
            override fun onReady() {
                super.onReady()
                Log.d("Monitor", "Printer: Ready")
            }

            // ...
            // Please refer to document for other callback.
        }

        printer?.drawerDelegate = object : DrawerDelegate() {
            override fun onOpenCloseSignalSwitched(openCloseSignal: Boolean) {
                super.onOpenCloseSignalSwitched(openCloseSignal)
                Log.d("Monitor", "Drawer: Open Close Signal Switched: ${openCloseSignal}")
            }

            // ...
            // Please refer to document for other callback.
        }

        printer?.inputDeviceDelegate = object : InputDeviceDelegate() {
            override fun onDataReceived(data: List<Byte>) {
                super.onDataReceived(data)
                Log.d("Monitor", "Input Device: DataReceived ${data}")
            }

            // ...
            // Please refer to document for other callback.
        }

        printer?.displayDelegate = object : DisplayDelegate() {
            override fun onConnected() {
                super.onConnected()
                Log.d("Monitor", "Display: Connected")
            }

            // ...
            // Please refer to document for other callback.
        }

        try {
            // Connect to the printer.
            printer?.openAsync()?.await()
        } catch (e: Exception) {
            // Exception.
            Log.d("Monitor", "${e.message}")
        }
    }
}
```

## Copyright

Copyright 2022 Star Micronics Co., Ltd. All rights reserved.
