//package com.example.app;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Environment;
//import android.os.Parcelable;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.webkit.ValueCallback;
//import android.webkit.WebChromeClient;
//import android.webkit.WebView;
//
//import java.io.File;
//import java.io.IOException;
//
//public class ChromeClient extends WebChromeClient {
//
//	// For Android 5.0
//	public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
//		// Double check that we don't have any existing callbacks
//		if (mFilePathCallback != null) {
//			mFilePathCallback.onReceiveValue(null);
//		}`
//		mFilePathCallback = filePath;
//
//		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//			// Create the File where the photo should go
//			File photoFile = null;
//			try {
//				photoFile = createImageFile();
//				takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
//			} catch (IOException ex) {
//				// Error occurred while creating the File
//				Log.e(Common.TAG, "Unable to create Image File", ex);
//			}
//
//			// Continue only if the File was successfully created
//			if (photoFile != null) {
//				mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
//				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//						Uri.fromFile(photoFile));
//			} else {
//				takePictureIntent = null;
//			}
//		}
//
//		Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
//		contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
//		contentSelectionIntent.setType("image/*");
//
//		Intent[] intentArray;
//		if (takePictureIntent != null) {
//			intentArray = new Intent[]{takePictureIntent};
//		} else {
//			intentArray = new Intent[0];
//		}
//
//		Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
//		chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
//		chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
//		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
//
//		startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
//
//		return true;
//
//	}
//
//	// openFileChooser for Android 3.0+
//	public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
//
//		mUploadMessage = uploadMsg;
//		// Create AndroidExampleFolder at sdcard
//		// Create AndroidExampleFolder at sdcard
//
//		File imageStorageDir = new File(
//				Environment.getExternalStoragePublicDirectory(
//						Environment.DIRECTORY_PICTURES)
//				, "AndroidExampleFolder");
//
//		if (!imageStorageDir.exists()) {
//			// Create AndroidExampleFolder at sdcard
//			imageStorageDir.mkdirs();
//		}
//
//		// Create camera captured image file path and name
//		File file = new File(
//				imageStorageDir + File.separator + "IMG_"
//						+ String.valueOf(System.currentTimeMillis())
//						+ ".jpg");
//
//		mCapturedImageURI = Uri.fromFile(file);
//
//		// Camera capture image intent
//		final Intent captureIntent = new Intent(
//				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//
//		captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
//
//		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//		i.addCategory(Intent.CATEGORY_OPENABLE);
//		i.setType("image/*");
//
//		// Create file chooser intent
//		Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
//
//		// Set camera intent to file chooser
//		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
//				, new Parcelable[] { captureIntent });
//
//		// On select image call onActivityResult method of activity
//		startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
//
//
//	}
//
//	// openFileChooser for Android < 3.0
//	public void openFileChooser(ValueCallback<Uri> uploadMsg) {
//		openFileChooser(uploadMsg, "");
//	}
//
//	//openFileChooser for other Android versions
//	public void openFileChooser(ValueCallback<Uri> uploadMsg,
//								String acceptType,
//								String capture) {
//
//		openFileChooser(uploadMsg, acceptType);
//	}
//
//}
