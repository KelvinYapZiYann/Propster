package com.propster.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.github.chrisbanes.photoview.PhotoView;
import com.propster.R;

import java.io.File;
import java.io.FileOutputStream;

public class PreviewImageActivity extends AppCompatActivity {

    private PhotoView previewImage;
    private Button previewImageSaveButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    private String imageUrl;
    private String imageName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);

        this.previewImage = findViewById(R.id.previewImageImage);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            this.imageUrl = null;
            this.imageName = null;
        } else {
            this.imageUrl = extras.getString(Constants.INTENT_EXTRA_IMAGE_URL, null);
            this.imageName = extras.getString(Constants.INTENT_EXTRA_IMAGE_NAME, null);
        }

        this.backgroundView = findViewById(R.id.previewImageBackground);
        this.loadingSpinner = findViewById(R.id.previewImageLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        this.previewImageSaveButton = findViewById(R.id.previewImageSaveButton);
        this.previewImageSaveButton.setOnClickListener(v -> {
            AlertDialog.Builder uploadedFileSavingDialog = new AlertDialog.Builder(this);
            uploadedFileSavingDialog.setCancelable(false);
            uploadedFileSavingDialog.setTitle("Do you want to save this file?");
            uploadedFileSavingDialog.setPositiveButton("OK", (dialog, which) -> {
                File path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS + "/propster");
                path.mkdirs();
                File imageFile = new File(path, this.imageName + ".png");
                try {
                    FileOutputStream out = new FileOutputStream(imageFile);
                    ((BitmapDrawable) this.previewImage.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
//                    MediaScannerConnection.scanFile(this, new String[] { imageFile.getAbsolutePath() }, null, (path1, uri) -> {
//                        System.out.println("ExternalStorage scan = " + path1);
//                        System.out.println("ExternalStorage uri = " + uri.toString());
//                    });
                } catch (Exception e) {
                    getImageFailed(Constants.ERROR_IMAGE_DOCUMENT_FILE_NOT_SAVED);
                }
                dialog.cancel();
            });
            uploadedFileSavingDialog.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.cancel();
            });
            uploadedFileSavingDialog.create().show();
        });

        Toolbar mainToolbar = findViewById(R.id.previewImageToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        mainToolbar.setNavigationOnClickListener(v -> finish());

        this.refreshImageFromUrl();
    }

    private void refreshImageFromUrl() {
        if (this.imageUrl == null) {
            return;
        }
        this.startLoadingSpinner();
        ImageRequest imageRequest = new ImageRequest(this.imageUrl, response -> {
            this.previewImage.setImageBitmap(response);
            this.stopLoadingSpinner();
        }, 0, 0, null, null, error -> {
            getImageFailed(Constants.ERROR_IMAGE_DOCUMENT_FILE_NOT_LOADED);
        });
        imageRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.requestQueue.add(imageRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_empty_toolbar, menu);
        return true;
    }

    private void getImageFailed(String imageFailed) {
        this.stopLoadingSpinner();
        AlertDialog.Builder imageFailedDialog = new AlertDialog.Builder(this);
        imageFailedDialog.setCancelable(false);
        imageFailedDialog.setTitle("Get Image Failed");
        imageFailedDialog.setMessage(imageFailed);
        imageFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        imageFailedDialog.create().show();
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.previewImageSaveButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.previewImageSaveButton.setEnabled(true);
    }
}
