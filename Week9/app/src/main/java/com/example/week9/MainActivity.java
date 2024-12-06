package com.example.week9;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.net.URL;
import java.net.URLConnection;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    EditText txtURL;
    Button btnDownload;
    ImageView imgView;
    //Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Bitmap bitmap;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        txtURL = findViewById(R.id.txtURL);
        btnDownload = findViewById(R.id.btnDownload);
        imgView = (ImageView) findViewById(R.id.imgView);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permission = ActivityCompat.checkSelfPermission(
                        MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE
                    );
                }
                String fileName = "temp.jpg";
                String imagePath = (Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOWNLOADS)).toString()
                        + "/" + fileName;
                downloadFile(txtURL.getText().toString(), imagePath);
                preview(imagePath);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 2 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            String fileName = "temp.jpg";
            String imagePath = (Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS)).toString()
                    + "/" + fileName;
            downloadFile(txtURL.getText().toString(), imagePath);
            preview(imagePath);
        } else {
            Toast.makeText(this, "External Storage permission not granted",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void preview(String imagePath) {
        Bitmap image = BitmapFactory.decodeFile(imagePath);
        float imageWidth = image.getWidth();
        float imageHeight = image.getHeight();
        int rescaledWidth = 400;
        //int rescaledHeight;
        int rescaledHeight = (int) ((imageHeight * rescaledWidth) / imageWidth);
        Bitmap bitmap = Bitmap.createScaledBitmap(image, rescaledWidth, rescaledHeight, false);

        imgView.setImageBitmap(bitmap);


    }

    private Bitmap downloadFile(String url, String imagePath) {
        try {
            URL strURL = new URL(url);
            URLConnection connection = strURL.openConnection();
            connection.connect();
            InputStream inputStream = new BufferedInputStream(strURL.openStream(), 8192);
            OutputStream outputStream = new FileOutputStream(imagePath);
            byte data[] = new byte[1024];
            int count;
            while ((count = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, count);
            }
            outputStream.flush();
            outputStream.close();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        class DownloadTask extends AsyncTask<String, Integer, Bitmap> {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMax(100);
                progressDialog.setIndeterminate(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle("Downloading");
                progressDialog.setMessage("Please wait..");
                progressDialog.show();
            }

            private Bitmap rescaleBitmap(String imagePath) {
                Bitmap image = BitmapFactory.decodeFile(imagePath);
                return image;
            }

            protected void ProgressUpdate(Integer... values) {
                progressDialog.setProgress(values[0]);
            }


            @Override
            protected Bitmap doInBackground(String... urls) {
                String fileName = "temp.jpg";
                String imagePath =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                downloadFile(urls[0], imagePath + "/" + fileName);

                return rescalBitmap(imagePath + "/" + fileName);

                @Override
                protected void onPostExecute(Bitmap bitmap){
                    imgView.setImageBitmap(bitmap);
                    progressDialog.dismiss();
                }

                class DownloadRunnable implements Runnable {
                    String url;

                    public DownloadRunnable(String url) {
                        this.url = url;
                    }

                    @Override
                    public void run() {
                        String fileName = "temp.jpg";
                        String imagePath =
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                        Bitmap bitmap1 = downloadFile(urls[0], imagePath + "/" + fileName);
                        Bitmap bitmap = rescaleBitmap(imagePath + "/" + fileName);
                        runOnUiThread(new UpdateBitmap(bitmap));
                    }

                    class UpdateBitmap implements Runnable {
                        Bitmap bitmap;

                        public UpdateBitmap(Bitmap bitmap) {
                            this.bitmap = bitmap;
                        }

                        @Override
                        public void run() {
                            imgView.setImageBitmap(bitmap);

                        }
                    }
                }
            }
        }}

