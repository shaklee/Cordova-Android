<!--
 # README.md
 #
 #   PSPDFKit
 #
 #   Copyright (c) 2015 PSPDFKit GmbH. All rights reserved.
 #
 #   THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 #   AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE PSPDFKIT LICENSE AGREEMENT.
 #   UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 #   This notice may not be removed from this file.
 # -->
# Cordova Wrapper for PSPDFKit 2 for Android
============================================

This plugin defines a global `PSPDFKit` object, which provides an API for viewing PDF documents with PSPDFKit for Android.

## Installation

	cordova plugin add https://github.com/PSPDFKit/Cordova-Android.git

## Example

```javascript
function showMyDocument() {
	PSPDFKit.showDocumentFromAssets('www/documents/myFile.pdf', {
			title: 'My PDF Document',
			page: 4,
			scrollDirection: PSPDFKit.PageScrollDirection.VERTICAL,
			useImmersiveMode: true
	});
}
```

## PSPDFKit.showDocument

Opens a document from the local device storage.

```javascript
PSPDFKit.showDocument(uri, options, success, fail)
```

## PSPDFKit.showDocumentFromAssets

Opens a document from the app's asset directory. To package a file within your app's assets, put it into the `www/` directory of your project.

```javascript
PSPDFKit.showDocumentFromAssets(assetPath, options, success, fail)
```

## Supported Platforms

* Android SDK API level 16+ / Android 4.1+ (Jelly Bean)

## Options

You can use the `options` parameter to configure PSPDFKit. Here is a list of valid configuration options.

```javascript
var options {
	backgroundColor: '#EFEFEF', // hex-color of the page background
	disableOutline: true, // hide the outline menu (default: false)
	hidePageLabels: true, // hide page labels (if available in PDF) in page overlay and thumbnail grid (default: false)  
	hidePageNumberOverlay: false, // hide the overlay showing the current page (default: false)
	hideThumbnailBar: true, // hide the thumbnail bar (default: false)
	hideThumbnailGrid: false, // hide the thumbnail grid menu (default: false)
	pageFitMode: PSPDFKit.PageFitMode.FIT_TO_WIDTH, // also valid: PSPDFKit.PageFitMode.FIT_TO_SCREEN
	scrollDirection: PSPDFKit.PageScrollDirection.VERTICAL, // also valid: PSPDFKit.PageScrollDirection.SEARCH_INLINE
	invertColors: false, // invert rendered colors (default: false)
	toGrayscale: true, // render document in grayscale only (default: false)
	loggingEnabled: false, // let PSPDFKit generate warning or debugging logs (default: true)
	title: "My PSPDFKit app", // title displayed in the viewer action bar
	startZoomScale: 2.0, // initial zoom value (default: 1.0)
	maxZoomScale: 10.0, // maximum zoom factor when zooming into a page (default: 15.0)
	zoomOutBounce: false, // "bounce" animation when pinch-zooming out (default: true)
	page: 2, // initial page number (default: 0, i.e. the first page)
	useImmersiveMode: true, // activate Android's immersive app mode (default: false)
	disableSearch: false, // completely deactivate document search (default: false)
	searchType: PSPDFKit.SearchType.SEARCH_MODULAR, // also valid: PSPDFKit.SearchType.SEARCH_INLINE
	autosaveEnabled: true, // automatically save document changes on exit (default: true)
	annotationEditing: {
		enabled: true, // activate annotation editing (default: true)
		creatorName: 'John Doe' // author name written into new annotations (default: null)
	}
};

PSPDFKit.showDocumentFromAssets('www/documents/myFile.pdf', options);
```

## Quickstart Guide

Create a new Apache Cordova project from your command line using the [Apache Cordova Command-Line Interface (CLI)](https://cordova.apache.org/docs/en/5.1.1/index.html).

	$ cordova create pdfapp com.example.pdfapp PDF-App
	$ cd pdfapp

> Important: Your app's package name (in the above example `com.example.pdfapp`) has to match your PSPDFKit license name or PSPDFKit will throw an exception.

Add Android platform support to your project:

	$ cordova platform add android

Install the PSPDFKit plugin:

	$ cordova plugin add https://github.com/PSPDFKit/Cordova-Android.git

Copy the PSPDFKit library file (usually `pspdfkit-<version>.aar`) into your project. This example uses version `2.x.x` of the PSPDFKit library. The current working directory has to be your project directory:

	$ cp /path/to/pspdfkit-2.x.x.aar platforms/android/libs/

Set the minimum SDK version of your Android application to 15. To do so, add the `android-minSdkVersion` preference to the android platform configuration of your `config.xml`. It should now look like this:

	<platform name="android">
        <preference name="android-minSdkVersion" value="16" />
        <!-- more Android platform settings -->
    </platform>

Configure your PSPDFKit license key inside the `platforms/android/AndroidManifest`:

	<manifest>
		<application>
			<meta-data android:name="PSPDFKIT_LICENSE_KEY" android:value="..." />
		</application>
	</manifest>

You are now ready to build your app!

	$ cordova build
