package com.bdb.webview;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

	private WebView webView;
	private ValueCallback<Uri[]> filePathCallback;
	public static final int FILE_REQUEST_CODE = 1;

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
		// Google login fix inside wv
		webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");

		webSettings.setGeolocationDatabasePath(this.getFilesDir().getPath());
		webSettings.setAllowFileAccess(true);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			webSettings.setAllowFileAccessFromFileURLs(true);
			webSettings.setAllowUniversalAccessFromFileURLs(true);
		}

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
				callback.invoke(origin, true, false);
			}

			@Override
			public void onPermissionRequest(final PermissionRequest request) {
				// I'll just grant myself the permissions I need...
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					request.grant(request.getResources());
				}
			}

			public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
											 WebChromeClient.FileChooserParams fileChooserParams) {

				if (MainActivity.this.filePathCallback != null) {
					MainActivity.this.filePathCallback.onReceiveValue(null);
				}
				MainActivity.this.filePathCallback = filePathCallback;

				Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
				contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
				contentSelectionIntent.setType("image/*");

				Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
				chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
				chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
				startActivityForResult(chooserIntent, FILE_REQUEST_CODE);
				return true;
			}
		});

		// Use remote resource
		webView.loadUrl("https://www.bikedeboa.com.br");
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != FILE_REQUEST_CODE || filePathCallback == null) {
			super.onActivityResult(requestCode, resultCode, data);
			return;
		}

		Uri[] results = null;
		if (resultCode == Activity.RESULT_OK) {
			if (data != null) {
				String dataString = data.getDataString();
				if (dataString != null) {
					results = new Uri[]{Uri.parse(dataString)};
				}
			}
		}
		filePathCallback.onReceiveValue(results);
		filePathCallback = null;
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
}