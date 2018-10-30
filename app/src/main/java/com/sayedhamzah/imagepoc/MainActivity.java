package com.sayedhamzah.imagepoc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST_CODE = 1;
    private static final int RESULT_GALLERY = 0;

    // your authority, must be the same as in your manifest file
    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.sayedhamzah.imagepoc.fileprovider";

   // private Button imgButton;
    private Button galleryButton;
   // private Button uploadButton;
    private Uri uploadedImage;
    private WebView webViewContent;
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;



    private static final String TAG = "Webview";
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

     //   imgButton = (Button)findViewById(R.id.button_photo);
      //  imgButton.setOnClickListener(internal_storage_photo);
      //  galleryButton = (Button)findViewById(R.id.button_gallery);
      //  galleryButton.setOnClickListener(photo_gallery);
     //   uploadButton = (Button)findViewById(R.id.button_upload);
      //  uploadButton.setOnClickListener(uploadimage);

        webViewContent = (WebView) findViewById(R.id.webview);
        webViewContent.getSettings().setDefaultFontSize(14);
        webViewContent.getSettings().setUseWideViewPort(false);
        webViewContent.getSettings().setJavaScriptEnabled(true);
        WebSettings webSettings = webViewContent.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);


        webViewContent.loadUrl("https://en.imgbb.com/");


        webViewContent.setWebViewClient(new WebViewClient() {
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final SslErrorHandler sslHandler = handler;
                builder.setMessage("Invalid Certificate");
                builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sslHandler.proceed();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sslHandler.cancel();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                try {
                    webViewContent.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                  //  MSFLog.error(Log.getStackTraceString(e));
                }

            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && url.toLowerCase().contains(".pdf") && !url.toLowerCase().contains("docs.google.com")) {
                    url = "https://docs.google.com/gview?embedded=true&url=" + url;
                }
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url != null && url.toLowerCase().contains(".pdf") && !url.toLowerCase().contains("docs.google.com")) {
                    url = "https://docs.google.com/gview?embedded=true&url=" + url;
                }
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                try {
                    webViewContent.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                 //   MSFLog.error(Log.getStackTraceString(e));
                }
            }
        });

        //update 22/03/2018 Account Opening upload image, Bunna
        if(Build.VERSION.SDK_INT >= 21){
            webSettings.setMixedContentMode(0);
            webViewContent.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }else if(Build.VERSION.SDK_INT >= 19){
            webViewContent.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }else if(Build.VERSION.SDK_INT < 19){
            webViewContent.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webViewContent.setWebChromeClient(new WebChromeClient(){
            //For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg){
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i,"File Chooser"), RESULT_GALLERY);
            }
            // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
            public void openFileChooser(ValueCallback uploadMsg, String acceptType){
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        RESULT_GALLERY);
            }
            //For Android 4.1+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
               startActivityForResult(Intent.createChooser(i, "File Chooser"), RESULT_GALLERY);
            }
            //For Android 5.0+
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams){

                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_CALLBACK_CONSTANT);
                    return false;
                } else {
                    if(mUMA != null){
                        mUMA.onReceiveValue(null);
                    }
                    mUMA = filePathCallback;
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(takePictureIntent.resolveActivity(getPackageManager()) != null){
                        File photoFile = null;
                        try{
                            //TODO step 2
                            photoFile = createImageFile();
                            takePictureIntent.putExtra("PhotoPath", mCM);
                        }catch(IOException ex){
                            Log.e("rtk", "Image file creation failed", ex);
                        }
                        if(photoFile != null){
                         //   mCM = "file:" + photoFile.getAbsolutePath();

                            //TODO step 3
                            Uri imageUri = FileProvider.getUriForFile(getApplicationContext(), CAPTURE_IMAGE_FILE_PROVIDER, photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                                takePictureIntent.setClipData(ClipData.newRawUri("", imageUri));
                                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            }



                        }else{
                            takePictureIntent = null;
                        }
                    }

                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.setType("*/*");

//                    Intent[] intentArray;
//                    if(takePictureIntent != null){
//                        intentArray = new Intent[]{takePictureIntent};
//                    }else{
//                        intentArray = new Intent[0];
//                    }

                    Intent fileIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
                    fileIntent.putExtra("CONTENT_TYPE", "*/*");
                    fileIntent.addCategory(Intent.CATEGORY_DEFAULT);


                    List<Intent> list=new ArrayList<Intent>();
                    list.add(takePictureIntent);
                    list.add(fileIntent);

                    Intent[] intentArray0  = new Intent[list.size()];
                    intentArray0 = list.toArray(intentArray0);



//
//                    Intent galleryIntent = new Intent(
//                            Intent.ACTION_PICK,
//                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);


                    //TODO It cause this intent not working without Write Permission

                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "File Chooser");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray0);
                    startActivityForResult(chooserIntent, RESULT_GALLERY);


                    //TODO Testing
//                    Intent galleryIntent = new Intent(
//                            Intent.ACTION_PICK,
//                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(galleryIntent , RESULT_GALLERY );


                    return true;
                }

            }
        });


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {


        if (requestCode == IMAGE_REQUEST_CODE) {

            // further processing with the stored image within the application's internal storage done here

            if (resultCode == Activity.RESULT_OK) {
                File path = new File(getApplicationContext().getFilesDir(), "tempimage/");
                if (!path.exists()) path.mkdirs();
                File imageFile = new File(path, "temp.jpg");
                uploadedImage = Uri.fromFile(imageFile);
            }
        }

        else if (requestCode == RESULT_GALLERY)
        {
            if (null != intent) {
                uploadedImage = intent.getData();

            }
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }



    private View.OnClickListener photo_gallery = new View.OnClickListener() {

        public void onClick(View v) {

            Intent galleryIntent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent , RESULT_GALLERY );
        }
    };

    // Create an image file
    private File createImageFile() throws IOException {
//        Log.e("rtk", "Hello World");
//        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "img_"+timeStamp+"_";
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        return File.createTempFile(imageFileName,".jpg",storageDir);

        //TODO step 1
        File path = new File(getApplicationContext().getFilesDir(), "tempimage/");
        if (!path.exists())
        {
            path.mkdirs();
        }

        File image = new File(path, "temp.jpg");
        return image;
    }


}

