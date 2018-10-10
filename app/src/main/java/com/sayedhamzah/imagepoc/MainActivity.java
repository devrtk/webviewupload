package com.sayedhamzah.imagepoc;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST_CODE = 1;
    private static final int RESULT_GALLERY = 0;
    // your authority, must be the same as in your manifest file
    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.sayedhamzah.imagepoc.fileprovider";
    private Button imgButton;
    private Button galleryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgButton = (Button)findViewById(R.id.button_photo);
        imgButton.setOnClickListener(internal_storage_photo);
        galleryButton = (Button)findViewById(R.id.button_gallery);
        galleryButton.setOnClickListener(photo_gallery);


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {


        if (requestCode == IMAGE_REQUEST_CODE) {

            // further processing with the stored image within the application's internal storage done here

            if (resultCode == Activity.RESULT_OK) {
                File path = new File(getApplicationContext().getFilesDir(), "tempimage/");
                if (!path.exists()) path.mkdirs();
                File imageFile = new File(path, "image.jpg");
            }
        }

        else if (requestCode == RESULT_GALLERY)
        {
            if (null != intent) {
                Uri imageUri = intent.getData();
                Log.d("photo-gallery",imageUri.toString());

                //further processing with the selected image from gallery done here

            }
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }


    private View.OnClickListener internal_storage_photo = new View.OnClickListener() {
        public void onClick(View v) {

            File path = new File(getApplicationContext().getFilesDir(), "tempimage/");
            if (!path.exists())
            {
                path.mkdirs();
            }

            File image = new File(path, "image.jpg");
            Uri imageUri = FileProvider.getUriForFile(getApplicationContext(), CAPTURE_IMAGE_FILE_PROVIDER, image);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);


            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                intent.setClipData(ClipData.newRawUri("", imageUri));
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            startActivityForResult(intent, IMAGE_REQUEST_CODE);

        }
    };

    private View.OnClickListener photo_gallery = new View.OnClickListener() {
        public void onClick(View v) {

            Intent galleryIntent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent , RESULT_GALLERY );
        }
    };




}
