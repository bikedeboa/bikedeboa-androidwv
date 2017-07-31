package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

	private WebView webView;
	private ValueCallback<Uri[]> mFilePathCallback;
	private String mCameraPhotoPath;
	public static final int INPUT_FILE_REQUEST_CODE = 1;
	public static final String EXTRA_FROM_NOTIFICATION = "EXTRA_FROM_NOTIFICATION";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Put status bar back on screen
		Window window = this.getWindow();
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
		}

		webView = (WebView) findViewById(R.id.activity_main_webview);
		// Enable Javascript
		WebSettings webSettings = webView.getSettings();
		// webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setGeolocationEnabled(true);
		webSettings.setPluginState(WebSettings.PluginState.ON);

		webSettings.setGeolocationDatabasePath(this.getFilesDir().getPath());
		webSettings.setAllowFileAccess(true);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			webSettings.setAllowFileAccessFromFileURLs(true);
			webSettings.setAllowUniversalAccessFromFileURLs(true);
		}

		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});


		webView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onGeolocationPermissionsShowPrompt(String origin,
														   GeolocationPermissions.Callback callback) {
				callback.invoke(origin, true, false);
			}



			@Override
			public void onPermissionRequest(final PermissionRequest request) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					request.grant(request.getResources());
				}
			}

			public boolean onShowFileChooser(
					WebView webView, ValueCallback<Uri[]> filePathCallback,
					WebChromeClient.FileChooserParams fileChooserParams) {

				if(mFilePathCallback != null) {
					mFilePathCallback.onReceiveValue(null);
				}
				mFilePathCallback = filePathCallback;

				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
					// Create the File where the photo should go
					File photoFile = null;
					try {
						photoFile = createImageFile();
						takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
					} catch (IOException ex) {
						// Error occurred while creating the File
						Log.e(TAG, "Unable to create Image File", ex);
					}

					// Continue only if the File was successfully created
					if (photoFile != null) {
						mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
						takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(photoFile));
					} else {
						takePictureIntent = null;
					}
				}

				Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
				contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
				contentSelectionIntent.setType("image/*");

				Intent[] intentArray;
				if(takePictureIntent != null) {
					intentArray = new Intent[]{takePictureIntent};
				} else {
					intentArray = new Intent[0];
				}

				Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
				chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
				chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

				startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

				return true;
			}
		});

		// Use remote resource
		webView.loadUrl("https://bikedeboa-dev.herokuapp.com");

		// Stop local links and redirects from opening in browser instead of WebView
		webView.setWebViewClient(new BdbWebViewClient());
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		File imageFile = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
		);
		return imageFile;
	}

	@Override
	public void onActivityResult (int requestCode, int resultCode, Intent data) {
		if(requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
			super.onActivityResult(requestCode, resultCode, data);
			return;
		}

		Uri[] results = null;

		// Check that the response is a good one
		if(resultCode == Activity.RESULT_OK) {
			if(data == null) {
				// If there is not data, then we may have taken a photo
				if(mCameraPhotoPath != null) {
					results = new Uri[]{Uri.parse(mCameraPhotoPath)};
				}
			} else {
				String dataString = data.getDataString();
				if (dataString != null) {
					results = new Uri[]{Uri.parse(dataString)};
				}
			}
		}

		mFilePathCallback.onReceiveValue(results);
		mFilePathCallback = null;
		return;
	}


	// Prevent the back-button from closing the app
	@Override
	public void onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}