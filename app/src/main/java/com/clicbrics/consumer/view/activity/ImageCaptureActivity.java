package com.clicbrics.consumer.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.clicbrics.consumer.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageCaptureActivity extends Activity {

    private static final String TAG = "ImageCaptureActivity";
    private Uri fileUri,tempFileUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDataFromIntent();
    }

    private void getDataFromIntent() {
        if(getIntent().hasExtra(Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE)){
            if(getIntent().getIntExtra(Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE, 0)==
                Constants.ImageCaptureConstants.OPEN_CAMERA){
                openCamera();
            }else {
                openMediaContent();
            }

        }
    }


    public void openMediaContent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, Constants.ImageCaptureConstants.PICKFILE_REQUEST_CODE);
    }

    public void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File file = createImageFile();
        boolean isDirectoryCreated = file.getParentFile().mkdirs();
        Log.d("", "openCamera: isDirectoryCreated: " + isDirectoryCreated);
        String authority = getPackageName() + ".fileprovider001";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tempFileUri = FileProvider.getUriForFile(this,
                authority, // As defined in Manifest
                file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri);
        } else {
            tempFileUri = Uri.fromFile(file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri);
        }
        startActivityForResult(cameraIntent, Constants.ImageCaptureConstants.START_CAMERA_REQUEST_CODE);
    }

    private File createImageFile() {
        clearTempImages();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new
            Date());
        File file = new File(Constants.ImageCaptureConstants.IMAGE_PATH, "IMG_" + timeStamp +
            ".jpg");
        fileUri = Uri.fromFile(file);
        return file;
    }

    private void clearTempImages() {
        try {
            File tempFolder = new File(Constants.ImageCaptureConstants.IMAGE_PATH);
            for (File f : tempFolder.listFiles())
                f.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            Intent intent=new Intent();
            if(requestCode==Constants.ImageCaptureConstants.PICKFILE_REQUEST_CODE){
                if(data!=null) {
                    /*try {
                        Bitmap bitmap = getRotatedBitmap(getRealPathFromURI(fileUri),getBitmap(fileUri));
                        fileUri = getUri(this,bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                    intent.putExtra(Constants.ImageCaptureConstants.SCANNED_RESULT,data.getData());
                }
                intent.putExtra(Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE, Constants.ImageCaptureConstants.OPEN_MEDIA);
                setResult(RESULT_OK,intent);
                finish();

            }else if(requestCode==Constants.ImageCaptureConstants.START_CAMERA_REQUEST_CODE){
                intent.putExtra(Constants.ImageCaptureConstants.SCANNED_RESULT,fileUri);
                intent.putExtra(Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE, Constants.ImageCaptureConstants.OPEN_CAMERA);
                setResult(RESULT_OK,intent);
                finish();
            }
            else {
                finish();
            }
        }else{
            finish();
        }
    }


    private String getTempFilename(Context context) throws IOException {
        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile("image_", "_tmp", outputDir);
        return outputFile.getAbsolutePath();
    }

    public Bitmap getRotatedBitmap(String photoPath, Bitmap bitmap) {
        Bitmap rotatedBitmap = null;
        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);


            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (rotatedBitmap == null) {
            return bitmap;
        } else {
            return rotatedBitmap;
        }

    }

    //By Avinash
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
            matrix, true);
    }

    public String getRealPathFromURI(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor =this.managedQuery(uri, proj, null, null, null);
        if (cursor == null) {

            return uri.getPath();  // Getting path from url itself

        } else {

            cursor.moveToFirst();
            int id = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(id);
        }
    }

    private Bitmap getBitmap(Uri selectedimg) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        AssetFileDescriptor fileDescriptor = null;
        fileDescriptor =
                getContentResolver().openAssetFileDescriptor(selectedimg, "r");
        Bitmap original = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);
        return original;
    }

    private Uri getUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        Log.i("Utils", "Media store path getUri: " + path);
        return Uri.parse(path);
    }
}
