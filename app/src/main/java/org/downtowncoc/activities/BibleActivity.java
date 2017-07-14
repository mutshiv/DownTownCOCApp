package org.downtowncoc.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.downtowncoc.R;

public class BibleActivity extends AppCompatActivity
{
    private static final String LOG_TAG = BibleActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bible);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Bible");

        WebView myWebView = (WebView) findViewById(R.id.bible_view);
        String bible_url = "https://www.bible.com/bible";

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.loadUrl(bible_url);

        myWebView.canGoBack();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class MyWebViewClient extends WebViewClient
    {
        ProgressDialog progressDialog;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            Log.d(LOG_TAG, "should override url loading method called with url " + url);
            view.loadUrl(url);
            return true;
        }

        //Show loader on url load
        public void onLoadResource(WebView view, String url)
        {
            if (progressDialog == null)
            {
                Log.d(LOG_TAG, "onLoadResource");
                progressDialog = new ProgressDialog(view.getContext());
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }
        }

        public void onPageFinished(WebView view, String url) {
            try
            {
                if (progressDialog != null)
                {
                    Log.d(LOG_TAG, "onPageFinished");
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }catch(Exception exception){
                exception.printStackTrace();
            }
        }
    }
}
