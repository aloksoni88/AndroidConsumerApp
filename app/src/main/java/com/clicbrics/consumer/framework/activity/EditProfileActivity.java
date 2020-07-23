package com.clicbrics.consumer.framework.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.buy.housing.backend.userEndPoint.UserEndPoint;
import com.buy.housing.backend.userEndPoint.model.BlobEntity;
import com.buy.housing.backend.userEndPoint.model.CommonResponse;
import com.buy.housing.backend.userEndPoint.model.SendOTPToPhoneNumberResponse;
import com.buy.housing.backend.userEndPoint.model.UserLoginResponse;
import com.clicbrics.consumer.EndPointBuilder;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.adapter.CountryListAdapter;
import com.clicbrics.consumer.customview.CircularImageView;
import com.clicbrics.consumer.framework.customview.CustomDialog;
import com.clicbrics.consumer.framework.util.TrackAnalytics;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.CountryDto;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.ImageCaptureActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@SuppressWarnings("deprecation")
public class EditProfileActivity extends BaseActivity {
    private static String TAG = EditProfileActivity.class.getName();
    ImageView imageView;
    CircularImageView circularImageView;
    EditText nameTxt;
    EditText emailTxt;

    TextView countryStdCodeTxt;
    EditText phoneTxt;

    String name = "";
    String email = "";
    String mobile = "";
    String countryStdCode = "";
    String password = "";

    private static int REQUEST_IMAGE = 5001;
    private final static int REQUEST_PERMISSION = 5002;
    private static int REQUEST_OPT_ACTIVITY = 5003;
    UserEndPoint mLoginWebService = null;
    Handler mHandler;
    TextView nameLabel;
    TextView mobileLabel;
    TextView emailLabel;

    TextView addContactsTxt;

    String otp = "";
    int initialContactCount = 0;
    boolean isProfileImageEdited = false;
    BlobEntity blobEntity = new BlobEntity();
    String editedImageStr;
    //byte[] editImageByteArr;
    Bitmap imageBitmap = null;
    private int trustedContactIndex = -1;
    //boolean isInValidProfile = false;
    CustomDialog mCustomDialog;

    RelativeLayout parentLayout;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        UtilityMethods.setStatusBarColor(this,R.color.light_white);
        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);

        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        nameTxt = (EditText) findViewById(R.id.id_name);
        emailTxt = (EditText) findViewById(R.id.id_email);
        phoneTxt = (EditText) findViewById(R.id.phone_number);
        imageView = (ImageView) findViewById(R.id.driveEditImage);
        circularImageView = (CircularImageView) findViewById(R.id.editCircularImage);
        countryStdCodeTxt = ((TextView) findViewById(R.id.countryStdCode));

        nameLabel = (TextView) findViewById(R.id.id_name_lable);
        emailLabel = (TextView) findViewById(R.id.id_email_lable);
        mobileLabel = (TextView) findViewById(R.id.id_phone_label);

        //hideSoftKeyboard();

        //password = UtilityMethods.getStringInPref(EditProfileActivity.this,Constants.AppConstants.PASS_PREF_KEY,"");

        if (getIntent().hasExtra("Password")) {
            password = getIntent().getStringExtra("Password");
        }
        if (getIntent().hasExtra("Name")) {
            name = getIntent().getStringExtra("Name");
        }
        if (getIntent().hasExtra("Mobile")) {
            mobile = getIntent().getStringExtra("Mobile");
            phoneTxt.setText(getIntent().getStringExtra("Mobile"));
        }
        if (getIntent().hasExtra("Email")) {
            email = getIntent().getStringExtra("Email");
            emailTxt.setText(getIntent().getStringExtra("Email"));
        }
        if (getIntent().hasExtra("CountryStdCode")) {
            countryStdCode = getIntent().getStringExtra("CountryStdCode");
        }
        UtilityMethods.saveStringInPref(EditProfileActivity.this, Constants.AppConstants.SELECTED_COUNTRY_STD_CODE, countryStdCode);
        nameTxt.setText(name);
        phoneTxt.setText(mobile);
        emailTxt.setText(email);
        countryStdCodeTxt.setText(countryStdCode);
        selectedCountryStdCode = countryStdCode;
        nameTxt.setSelection(nameTxt.getText().length());
        addBackButton();


        UtilityMethods.loadCircularImageFromPicasso(this, circularImageView);
        UtilityMethods.loadBlurImage(this, imageView);


        //editImageByteArr = getBytesFromBitmap(imageBitmap);
        editedImageStr = getStringFromBitmap(imageBitmap);


        nameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(nameTxt.getText().toString().trim())) {
                    UtilityMethods.setTextViewColor(EditProfileActivity.this, nameLabel, R.color.red_dark);
                } else {
                    UtilityMethods.setTextViewColor(EditProfileActivity.this, nameLabel, R.color.gray_600);
                    UtilityMethods.setTextViewColor(EditProfileActivity.this, nameTxt, R.color.text_color_login_reg);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        emailTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(emailTxt.getText().toString().trim())) {
                    UtilityMethods.setTextViewColor(EditProfileActivity.this, emailLabel, R.color.red_dark);
                } else {
                    UtilityMethods.setTextViewColor(EditProfileActivity.this, emailTxt, R.color.text_color_login_reg);
                    UtilityMethods.setTextViewColor(EditProfileActivity.this, emailLabel, R.color.gray_600);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phoneTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(phoneTxt.getText().toString().trim())) {
                    UtilityMethods.setTextViewColor(EditProfileActivity.this, mobileLabel, R.color.red_dark);
                } else {
                    UtilityMethods.setTextViewColor(EditProfileActivity.this, phoneTxt, R.color.text_color_login_reg);
                    UtilityMethods.setTextViewColor(EditProfileActivity.this, mobileLabel, R.color.gray_600);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mHandler = new Handler();
        buildRegService();
        //hideSoftKeyboard();
        loadCountryList();


        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.light_white)));
        } else {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.light_white)));
        }*/
    }

    private void loadCountryList() {
        countryList = new ArrayList<CountryDto>();
        countryFlagBitmap = new ArrayList<Bitmap>();
        final List<String> countryNames = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.country_names)));
        countryFlagBitmap = getBitmapListFromAsset(countryNames);
        final List<String> countryStdCodes = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.country_std_code)));
        for (int i = 0; i < countryNames.size(); i++) {
            CountryDto countryDto = new CountryDto();
            countryDto.setCountryName(countryNames.get(i));
            countryDto.setCountryCode(countryStdCodes.get(i));
            countryDto.setFlagName(countryNames.get(i) + ".png");
            countryDto.setFlagImage(countryFlagBitmap.get(i));
            countryList.add(countryDto);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_profile_menu, menu);
        return true;
    }


    List<CountryDto> countryList;
    ArrayList<Bitmap> countryFlagBitmap;
    Dialog customDialog;
    String selectedCountryFlagName = "";
    String selectedCountryStdCode = "";
    String selectedCountryName = "";

    public void showCountryList(View v) {
        if (customDialog == null) {
            customDialog = new Dialog(this);
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            View view = getLayoutInflater().inflate(R.layout.dialog_country_list, null);
            ListView listView = (ListView) view.findViewById(R.id.countryList);
            EditText countrySearch = (EditText) view.findViewById(R.id.inputCountrySearch);

            if (countryList == null) {
                countryList = new ArrayList<>();
                countryFlagBitmap = new ArrayList<Bitmap>();
                final List<String> countryNames = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.country_names)));
                countryFlagBitmap = getBitmapListFromAsset(countryNames);
                final List<String> countryStdCodes = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.country_std_code)));
                for (int i = 0; i < countryNames.size(); i++) {
                    CountryDto countryDto = new CountryDto();
                    countryDto.setCountryName(countryNames.get(i));
                    countryDto.setCountryCode(countryStdCodes.get(i));
                    countryDto.setFlagName(countryNames.get(i) + ".png");
                    countryDto.setFlagImage(countryFlagBitmap.get(i));
                    countryList.add(countryDto);
                }
            }
            final CountryListAdapter adapter = new CountryListAdapter(this, countryList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CountryDto countryDto = adapter.getItem(position);
                    try {
                        countryStdCodeTxt.setText(countryDto.getCountryCode());
                        selectedCountryFlagName = countryDto.getFlagName();
                        selectedCountryStdCode = countryDto.getCountryCode();
                        selectedCountryName = countryDto.getCountryName();
                        customDialog.dismiss();
                        //hideSoftKeyboard();
                        customDialog = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            countrySearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    Log.d(TAG, "beforeTextChanged : " + s);
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.d(TAG, "onTextChanged : " + s);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d(TAG, "afterTextChanged : " + s);
                    adapter.getFilter().filter(s);
                }
            });
            customDialog.setContentView(view);
            final Typeface mFont = Typeface.createFromAsset(getAssets(),
                    "fonts/FiraSans-Light.ttf");
            final ViewGroup mContainer = (ViewGroup) customDialog.findViewById(
                    android.R.id.content).getRootView();
            UtilityMethods.setAppFont(mContainer, mFont, false);
            customDialog.setCancelable(true);
            TextView cancelButton = (TextView) view.findViewById(R.id.cancel);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialog.dismiss();
                    //hideSoftKeyboard();
                    customDialog = null;
                }
            });
            customDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    customDialog = null;
                }
            });

            if (!customDialog.isShowing()) {
                customDialog.show();
            }
        }
    }

    private ArrayList<Bitmap> getBitmapListFromAsset(List<String> listFlagNames) {
        AssetManager assetManager = getAssets();
        for (int i = 0; i < listFlagNames.size(); i++) {
            try {
                InputStream inputStream = assetManager.open(Constants.AppConstants.COUNTRY_FLAG_DIR + File.separator + listFlagNames.get(i).trim() + ".png");
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                countryFlagBitmap.add(bitmap);
                inputStream.close();
            } catch (IOException e) {
                countryFlagBitmap.add(BitmapFactory.decodeResource(getResources(), R.drawable.no_flag_icon));
                e.printStackTrace();
            }
        }
        return countryFlagBitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        selectImage();
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        //Toast.makeText(this, getResources().getString(R.string.permision), Toast.LENGTH_SHORT).show();
                        UtilityMethods.showErrorSnackBar(parentLayout, getResources().getString(R.string.permision), Snackbar.LENGTH_SHORT);
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }


    public void editImage(View v) {
        selectImage();
    }

    private void buildRegService() {
        mLoginWebService = EndPointBuilder.getUserEndPoint();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_image: {
                selectImage();
            }
            break;
            case R.id.done: {
                doneButtonSaveChanges();
            }
            break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int errorMSG = -1;
        if (requestCode == REQUEST_OPT_ACTIVITY && resultCode == RESULT_OK) {
            Log.d(TAG, "save on server! " + phoneTxt.getText().toString().trim() + "\nOTP->" + data.getStringExtra("OTP"));
            if (!TextUtils.isEmpty(data.getStringExtra("OTP"))) {
                otp = data.getStringExtra("OTP");
            }
            doSaveOnServer(nameTxt.getText().toString().trim(), phoneTxt.getText().toString().trim(), emailTxt.getText().toString());
        }
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            isProfileImageEdited = true;
            try {
                //imageBitmap = (Bitmap) data.getExtras().get("data");
                Uri uri = data.getExtras().getParcelable(Constants.ImageCaptureConstants.SCANNED_RESULT);
                int captureType = data.getIntExtra(Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE,-1);
                if(captureType == Constants.ImageCaptureConstants.OPEN_MEDIA){
                    imageBitmap = getBitmapFromUri(uri);
                }else{
                    imageBitmap = getRotatedBitmap(getRealPathFromURI(uri),getBitmap(uri));
                }
                circularImageView.setImageBitmap(imageBitmap);
                Bitmap blur = UtilityMethods.fastblur(imageBitmap);
                imageView.setImageBitmap(blur);
                imageBitmap = getResizedBitmap(imageBitmap,300);
                editedImageStr = getStringFromBitmap(imageBitmap);
                //editImageByteArr = getBytesFromBitmap(imageBitmap);
                getContentResolver().delete(uri, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getStringFromBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, Constants.AppConstants.RIDER_IMAGE_COMPRESSION_RATIO, stream);
            return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
        } else {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap defaultImage = UtilityMethods.getBitmap(imageView);
            defaultImage.compress(Bitmap.CompressFormat.PNG, Constants.AppConstants.RIDER_IMAGE_COMPRESSION_RATIO, stream);
            return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
        }
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, Constants.AppConstants.RIDER_IMAGE_COMPRESSION_RATIO, stream);
            return stream.toByteArray();
        } else {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap defaultImage = UtilityMethods.getBitmap(imageView);
            defaultImage.compress(Bitmap.CompressFormat.PNG, Constants.AppConstants.RIDER_IMAGE_COMPRESSION_RATIO, stream);
            return stream.toByteArray();
        }
    }


    @Override
    public void onBackPressed() {
        final boolean isValidProfile = validateFields();
        if (isProfileEdited()) {
            View.OnClickListener positiveListener = new View.OnClickListener() {

                @Override
                public void onClick(View dialog) {
                    if (isValidProfile) {
                        doneButtonSaveChanges();
                    }
                    if (mCustomDialog != null)
                        mCustomDialog.dismiss();
                }
            };
            View.OnClickListener negativeListener = new View.OnClickListener() {

                @Override
                public void onClick(View dialog) {
                    if (mCustomDialog != null)
                        mCustomDialog.dismiss();
                    if (isValidProfile) {
                        finish();
                    }
                }
            };
            if (isValidProfile) {
                mCustomDialog = UtilityMethods.showAlert(EditProfileActivity.this, R.string.confirm, this.getString(R.string.save_change_confirmation), true, R.string.yes, positiveListener, R.string.no, negativeListener, null);
            } else {
                mCustomDialog = UtilityMethods.showAlert(EditProfileActivity.this, R.string.confirm, this.getString(R.string.invalid_fields), true, R.string.ok, positiveListener, R.string.cancel, negativeListener, null);
            }

        } else {
            finish();
        }
        //super.onBackPressed();
    }

    private void doneButtonSaveChanges() {
        TrackAnalytics.trackEvent(EditProfileActivity.this.getResources().getString(R.string.ga_button_category), getString(R.string.edit_profile), this);

        if (!UtilityMethods.isInternetConnected(EditProfileActivity.this)) {
            showNetworkErrorSnackBar();
            //UtilityMethods.showSnackBar(parentLayout,getResources().getString(R.string.network_error_msg),Snackbar.LENGTH_SHORT);
            return;
        }
        if (!validateFields()) {
            Log.d(TAG, "validateFields");
            //showInvalidFieldPopup();
            return;
        } else {
            if ((!phoneTxt.getText().toString().trim().equals(mobile)) || (!selectedCountryStdCode
                    .equals(countryStdCode))) {
                sendOTP();
                Log.d(TAG, "sendOTP");
            } else {
                Log.d(TAG, "saved on server");
                doSaveOnServer(nameTxt.getText().toString().trim(), phoneTxt.getText().toString().trim(), emailTxt.getText().toString().trim());
            }
        }
    }

    public void showNetworkErrorSnackBar() {
        final Snackbar snackbar = Snackbar.make(parentLayout, getResources().getString(R.string.network_error_msg),
                Snackbar.LENGTH_INDEFINITE);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(EditProfileActivity.this, R.color.uber_red));
        snackbar.setActionTextColor(ContextCompat.getColor(EditProfileActivity.this, R.color.white));
        snackbar.setAction("TRY AGAIN", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                doneButtonSaveChanges();
            }
        });
        snackbar.show();
    }


    private boolean validateFields() {
        boolean isValid = true;
        if (TextUtils.isEmpty(nameTxt.getText().toString().trim())) {
            nameLabel.setTextColor(Color.RED);
            isValid = false;
            UtilityMethods.showSnackBar(parentLayout, getResources().getString(R.string.name_error), Snackbar.LENGTH_LONG);
        }
        if (TextUtils.isEmpty(emailTxt.getText().toString().trim())) {
            emailTxt.setTextColor(Color.RED);
            isValid = false;
            UtilityMethods.showSnackBar(parentLayout, getResources().getString(R.string.email_error), Snackbar.LENGTH_LONG);
        } else if (!UtilityMethods.isValidEmail(emailTxt.getText().toString().trim())) {
            emailTxt.setTextColor(Color.RED);
            isValid = false;
            UtilityMethods.showSnackBar(parentLayout, getResources().getString(R.string.email_error), Snackbar.LENGTH_LONG);
        }
        String Regex = "[^\\d]";
        if (TextUtils.isEmpty(phoneTxt.getText().toString().trim())) {
            mobileLabel.setTextColor(Color.RED);
            isValid = false;
            UtilityMethods.showSnackBar(parentLayout, getResources().getString(R.string.mobile_error), Snackbar.LENGTH_LONG);
        } else if ((phoneTxt.getText().toString().trim().replaceAll(Regex, "")).length() < 10) {
            mobileLabel.setTextColor(Color.RED);
            isValid = false;
            UtilityMethods.showSnackBar(parentLayout, getResources().getString(R.string.mobile_error), Snackbar.LENGTH_LONG);
        } else if (!UtilityMethods.isValidPhoneNumber(phoneTxt.getText().toString())) {
            mobileLabel.setTextColor(Color.RED);
            isValid = false;
            UtilityMethods.showSnackBar(parentLayout, getResources().getString(R.string.mobile_error), Snackbar.LENGTH_LONG);
        }
        return isValid;
    }


    private boolean checkForInternet() {
        if (!UtilityMethods.isInternetConnected(getApplicationContext())) {
            View.OnClickListener positiveListener = new View.OnClickListener() {

                @Override
                public void onClick(View dialog) {
                    if (mCustomDialog != null)
                        mCustomDialog.dismiss();
                }
            };
            mCustomDialog = UtilityMethods.showAlert(EditProfileActivity.this, R.string.network_error_title
                    , this.getString(R.string.network_error_msg), true, R.string.close, positiveListener, -1, null, null);
            return false;
        } else {


            return true;
        }
    }

    private void selectImage() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if(currentapiVersion >= Build.VERSION_CODES.M){
            if (!UtilityMethods.selfPermissionGranted(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},this)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION);
                return;
            }
        }
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.select_image)};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle(R.string.edit_image);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                Intent intent = new Intent(mActivity, ImageCaptureActivity.class);
                if (items[item].equals(getString(R.string.take_photo))) {
                    intent.putExtra(Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE, Constants.ImageCaptureConstants.OPEN_CAMERA);
                } else if (items[item].equals(getString(R.string.select_image))) {
                    /*Intent intent = new Intent(
                            Intent.ACTION_GET_CONTENT,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    int requestCode = -1;
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), REQUEST_IMAGE_GALLERY);*/
                    intent.putExtra(Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE, Constants.ImageCaptureConstants.OPEN_MEDIA);
                }
                startActivityForResult(intent,REQUEST_IMAGE);
            }
        });
        builder.show();
    }

    private String pictureImagePath = "";
    private void openCamera(Intent cameraIntent) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(pictureImagePath);
        Uri outputFileUri = Uri.fromFile(file);
        //Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider",fileImagePath);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, REQUEST_IMAGE);
    }

    public boolean numberChanged = false;

    private boolean isProfileEdited() {
        String editedName = nameTxt != null ? nameTxt.getText().toString().trim() : "";
        String editedEamil = emailTxt != null ? emailTxt.getText().toString().trim() : "";
        String editedMobile = phoneTxt != null ? phoneTxt.getText().toString().trim() : "";
        String editedCountryCode = countryStdCodeTxt != null ? countryStdCodeTxt.getText().toString().trim() : "";

        Log.d(TAG, "name->" + name + "   " + editedName + "\nemail->" + email + "   " + editedEamil + "\nmobile->" +
                mobile + "    " + editedMobile + "\nISD->" + UtilityMethods.getStringInPref(this,
                Constants.AppConstants.SELECTED_COUNTRY_STD_CODE, "") + "   " + editedCountryCode.replaceAll("[\\s\\-]", "")
                + "imageEdited->" + isProfileImageEdited);

        if (!name.equals(editedName)) {
            return true;
        }
        if (!email.equals(editedEamil)) {
            return true;
        }
        if (!mobile.equals(editedMobile)) {
            numberChanged = true;
            return true;
        }

        if (!countryStdCode.equals(editedCountryCode)) {
            numberChanged = true;
            return true;
        }

        if (!UtilityMethods.getStringInPref(this, Constants.AppConstants.SELECTED_COUNTRY_STD_CODE, "")
                .equals(editedCountryCode.replaceAll("[\\s\\-]", ""))) {
            numberChanged = true;
            return true;
        }

        if (isProfileImageEdited) {
            return true;
        }
        return false;
    }

    private void sendOTP() {
        //showProgressBar(getString(R.string.please_wait));
        showProgressBar();
        final long rUID = UtilityMethods.getLongInPref(EditProfileActivity.this, Constants.ServerConstants.USER_ID, -1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMSG = "";
                try {

                    final UserEndPoint.VerifyPhoneNumber loginResponse = mLoginWebService.verifyPhoneNumber(rUID);
                    loginResponse.setPhoneNumber(phoneTxt.getText().toString());
                    loginResponse.setCountrySTDCode(selectedCountryStdCode.replaceAll("[\\s\\-]", ""));
                    loginResponse.setCountryName(selectedCountryName);
                    final SendOTPToPhoneNumberResponse response = loginResponse.setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();
                    if (response.getStatus()) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //progressBar.setVisibility(View.GONE);
                                dismissProgressBar();
                                UtilityMethods.saveStringInPref(EditProfileActivity.this,
                                        Constants.AppConstants.SELECTED_COUNTRY_NAME, selectedCountryStdCode.replaceAll("[\\s\\-]", ""));
                                UtilityMethods.saveStringInPref(EditProfileActivity.this,
                                        Constants.AppConstants.SELECTED_COUNTRY_NAME, selectedCountryName);
                                Intent intent = new Intent(EditProfileActivity.this, OTPActivity.class);
                                intent.putExtra("oTP", response.getOtp());
                                intent.putExtra("isFromEditProfile", true);
                                intent.putExtra("COUNTRY_CODE", selectedCountryStdCode);
                                intent.putExtra("MOBILENO", phoneTxt.getText().toString().trim());
                                startActivityForResult(intent, REQUEST_OPT_ACTIVITY);
                                //                                    finish();
                            }
                        });
                    } else {
                        errorMSG = !TextUtils.isEmpty(response.getErrorMessage()) ? response.getErrorMessage() : EditProfileActivity.this.getResources().getString(R.string.network_error_msg);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                dismissProgressBar();
                            }
                        });
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    errorMSG = EditProfileActivity.this.getResources().getString(R.string.network_error_msg);
                    TrackAnalytics.trackException(TAG, e.getMessage(), e);
                }catch (Exception e) {
                    e.printStackTrace();
                    errorMSG = EditProfileActivity.this.getResources().getString(R.string.something_went_wrong);
                    TrackAnalytics.trackException(TAG, e.getMessage(), e);
                }
                if (!TextUtils.isEmpty(errorMSG)) {
                    final String errorMsg = errorMSG;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //progressBar.setVisibility(View.GONE);
                            dismissProgressBar();
                            View.OnClickListener positiveListener = new View.OnClickListener() {

                                @Override
                                public void onClick(View dialog) {
                                    if (mCustomDialog != null)
                                        mCustomDialog.dismiss();
                                }
                            };
                            mCustomDialog = UtilityMethods.showAlert(EditProfileActivity.this, R.string.alert, errorMsg, true, R.string.ok, positiveListener, -1, null, null);
                        }
                    });
                }


            }
        }).start();
    }


    private void doSaveOnServer(final String name, final String mobileNO, final String email) {

        if (isProfileEdited()) {
            UtilityMethods.saveStringInPref(EditProfileActivity.this, Constants.AppConstants.USER_NAME_PREF_KEY, name);
            UtilityMethods.saveStringInPref(EditProfileActivity.this, Constants.AppConstants.MOBILE_PREF_KEY, mobileNO);
            UtilityMethods.saveStringInPref(EditProfileActivity.this, Constants.AppConstants.EMAIL_PREF_KEY, email);
            UtilityMethods.saveStringInPref(EditProfileActivity.this,
                    Constants.AppConstants.SELECTED_COUNTRY_STD_CODE, selectedCountryStdCode);
            UtilityMethods.saveStringInPref(EditProfileActivity.this,
                    Constants.AppConstants.SELECTED_COUNTRY_NAME, selectedCountryName);
            UtilityMethods.saveStringInPref(EditProfileActivity.this,
                    Constants.AppConstants.SELECTED_COUNTRY_FLAG_NAME, selectedCountryFlagName);
            /*if (isProfileImageEdited) {
                try {
                    OutputStream output = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                    output.flush();
                    output.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/

            showProgressBar();
            final long rUID = UtilityMethods.getLongInPref(getApplicationContext(), Constants.ServerConstants.USER_ID, -1);
            Log.d(TAG, "UID->" + rUID);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String errorMSG = "";
                    try {

                        if (!isProfileImageEdited) {
                            UserEndPoint.UpdateUser updateProfileRequest = mLoginWebService.updateUser(rUID, password);
                            updateProfileRequest.setName(name);
                            if(!TextUtils.isEmpty(selectedCountryName)) {
                                updateProfileRequest.setCountryName(selectedCountryName);
                            }
                            if (numberChanged) {
                                updateProfileRequest.setCountrySTDCode(selectedCountryStdCode.replaceAll("[\\s\\-]", ""));
                                updateProfileRequest.setPhoneNumber(phoneTxt.getText().toString().trim());
                            }
                            updateProfileRequest.setEmail(emailTxt.getText().toString().trim());

                            Log.d(TAG, "OTP->" + otp);
                            if (!TextUtils.isEmpty(otp)) {
                                updateProfileRequest.setOTP(otp);
                            }

                            final CommonResponse loginResponse = updateProfileRequest
                                    .setRequestHeaders(UtilityMethods.getHttpHeaders())
                                    .execute();

                            Log.d(TAG, "loginResponse->" + loginResponse);
                            if (loginResponse.getStatus()) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //progressBar.setVisibility(View.GONE);
                                        dismissProgressBar();
                                        Intent intent = new Intent();
                                        intent.putExtra("Name", name);
                                        //intent.putExtra("Image", editedImageStr);
                                        intent.putExtra("MOBILE", mobileNO);
                                        intent.putExtra("Email", email);

                                        UtilityMethods.saveStringInPref(EditProfileActivity.this,
                                                Constants.AppConstants.SELECTED_COUNTRY_FLAG_NAME, selectedCountryFlagName);
                                        UtilityMethods.saveStringInPref(EditProfileActivity.this,
                                                Constants.AppConstants.SELECTED_COUNTRY_STD_CODE, selectedCountryStdCode.replaceAll("[\\s\\-]", ""));
                                        UtilityMethods.saveStringInPref(EditProfileActivity.this,
                                                Constants.AppConstants.SELECTED_COUNTRY_NAME, selectedCountryName);
                                        UtilityMethods.saveStringInPref(EditProfileActivity.this,
                                                Constants.AppConstants.EMAIL_PREF_KEY, emailTxt.getText().toString().trim());
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                });
                            } else {
                                errorMSG = loginResponse.getErrorMessage();
                            }
                        } else {
                            UserEndPoint.UpdateUser updateProfileRequest = mLoginWebService.updateUser(rUID, password);
                            updateProfileRequest.setName(name);
                            updateProfileRequest.setCountryName(selectedCountryName);
                            if (numberChanged) {
                                updateProfileRequest.setCountrySTDCode(selectedCountryStdCode.replaceAll("[\\s\\-]", ""));
                                updateProfileRequest.setPhoneNumber(phoneTxt.getText().toString().trim());
                            }
                            updateProfileRequest.setEmail(emailTxt.getText().toString().trim());

                            CommonResponse loginResponse
                                    = updateProfileRequest.setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();

                            blobEntity.setBlob(editedImageStr);
                            UserEndPoint.UpdateUserImage updateImage = mLoginWebService.updateUserImage(rUID, blobEntity);

                            final UserLoginResponse imageUpdateResponse = updateImage
                                    .setRequestHeaders(UtilityMethods.getHttpHeaders()).execute();

                            if (imageUpdateResponse.getStatus() && loginResponse.getStatus()) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //progressBar.setVisibility(View.GONE);
                                        dismissProgressBar();

                                        Intent intent = new Intent();
                                        intent.putExtra("Name", name);
                                        //intent.putExtra("Image", editedImageStr);
                                        intent.putExtra("Email", email);
                                        UtilityMethods.saveStringInPref(EditProfileActivity.this,
                                                Constants.AppConstants.SELECTED_COUNTRY_FLAG_NAME, selectedCountryFlagName);
                                        UtilityMethods.saveStringInPref(EditProfileActivity.this,
                                                Constants.AppConstants.SELECTED_COUNTRY_STD_CODE,
                                                selectedCountryStdCode.replaceAll("[\\s\\-]", ""));
                                        UtilityMethods.saveStringInPref(EditProfileActivity.this,
                                                Constants.AppConstants.SELECTED_COUNTRY_NAME, selectedCountryName);
                                        UtilityMethods.saveStringInPref(EditProfileActivity.this,
                                                Constants.AppConstants.IMAGE_URL,
                                                imageUpdateResponse.getUser().getImageURL());
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                });
                            }else if(imageUpdateResponse.getStatus() && !loginResponse.getStatus()){
                                dismissProgressBar();
                                UtilityMethods.saveStringInPref(EditProfileActivity.this,
                                        Constants.AppConstants.SELECTED_COUNTRY_FLAG_NAME, selectedCountryFlagName);
                                UtilityMethods.saveStringInPref(EditProfileActivity.this,
                                        Constants.AppConstants.SELECTED_COUNTRY_STD_CODE,
                                        selectedCountryStdCode.replaceAll("[\\s\\-]", ""));
                                UtilityMethods.saveStringInPref(EditProfileActivity.this,
                                        Constants.AppConstants.SELECTED_COUNTRY_NAME, selectedCountryName);
                                UtilityMethods.saveStringInPref(EditProfileActivity.this,
                                        Constants.AppConstants.IMAGE_URL,
                                        imageUpdateResponse.getUser().getImageURL());
                                if(!TextUtils.isEmpty(loginResponse.getErrorMessage())){
                                    errorMSG = loginResponse.getErrorMessage();
                                }else {
                                    errorMSG = "Could not update profile details. \n Please try again!";
                                }
                            }else if(!imageUpdateResponse.getStatus() && loginResponse.getStatus()){
                                if(!TextUtils.isEmpty(imageUpdateResponse.getErrorMessage())){
                                    errorMSG = imageUpdateResponse.getErrorMessage();
                                }else {
                                    errorMSG = "Could not update profile details. \n Please try again!";
                                }
                            }
                            else {
                                Log.d(TAG, "err->" + loginResponse.getErrorMessage());
                                errorMSG = TextUtils.isEmpty(imageUpdateResponse.getErrorMessage()) ? loginResponse.getErrorMessage() : imageUpdateResponse.getErrorMessage();
                            }
                        }

                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        errorMSG = EditProfileActivity.this.getResources().getString(R.string.network_error_msg);
                        TrackAnalytics.trackException(TAG, e.getMessage(), e);
                    }catch (Exception e) {
                        e.printStackTrace();
                        errorMSG = EditProfileActivity.this.getResources().getString(R.string.something_went_wrong);
                        TrackAnalytics.trackException(TAG, e.getMessage(), e);
                    }
                    if (!TextUtils.isEmpty(errorMSG)) {
                        final String errorMsg = errorMSG;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //progressBar.setVisibility(View.GONE);
                                dismissProgressBar();
                                View.OnClickListener positiveListener = new View.OnClickListener() {

                                    @Override
                                    public void onClick(View dialog) {
                                        if (mCustomDialog != null)
                                            mCustomDialog.dismiss();
                                    }
                                };
                                mCustomDialog = UtilityMethods.showAlert(EditProfileActivity.this, R.string.alert, errorMsg, true, R.string.ok, positiveListener, -1, null, null);
                            }
                        });
                    }
                }
            }).start();
        } else {
            /*Intent intent = new Intent();
            intent.putExtra("Name", name);
            intent.putExtra("Image", editImageByteArr);
            UtilityMethods.saveStringInPref(EditProfileActivity.this,
                    Constants.AppConstants.SELECTED_COUNTRY_FLAG_NAME, selectedCountryFlagName);
            UtilityMethods.saveStringInPref(EditProfileActivity.this,
                    Constants.AppConstants.SELECTED_COUNTRY_STD_CODE, selectedCountryStdCode.replaceAll("[\\s\\-]", ""));
            UtilityMethods.saveStringInPref(EditProfileActivity.this,
                    Constants.AppConstants.SELECTED_COUNTRY_NAME, selectedCountryName);

            setResult(Constants.AppConstants.EDIT_ACTIVITY_RESULT, intent);
            finish();*/
            finish();
        }
    }

    private void showInvalidFieldPopup() {
        View.OnClickListener positiveListener = new View.OnClickListener() {

            @Override
            public void onClick(View dialog) {
                if (mCustomDialog != null)
                    mCustomDialog.dismiss();
            }
        };
        mCustomDialog = UtilityMethods.showAlert(EditProfileActivity.this, R.string.error
                , this.getString(R.string.invalid_name), true, R.string.ok, positiveListener, -1, null, null);
    }

    @Override
    protected void onDestroy() {
        if (imageView != null) {
            imageView = null;
        }
        super.onDestroy();
    }

    long onStartTime;
    @Override
    protected void onStart() {
        super.onStart();
        onStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();
        TrackAnalytics.trackEvent("EditProfilePage", Constants.AppConstants.HOLD_TIME ,
                UtilityMethods.getTimeDiffInSeconds(System.currentTimeMillis(),onStartTime), this);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        try {
            int width = image.getWidth();
            int height = image.getHeight();

            float bitmapRatio = (float) width / (float) height;
            if (bitmapRatio > 1) {
                width = maxSize;
                height = (int) (width / bitmapRatio);
            } else {
                height = maxSize;
                width = (int) (height * bitmapRatio);
            }
            return Bitmap.createScaledBitmap(image, width, height, true);
        } catch (Exception e) {
            e.printStackTrace();
            return image;
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);

        // to handle rotated/orientation_locked images
        /*ExifInterface exif = new ExifInterface(uri.getPath());
        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int rotationInDegrees = -exifToDegrees(rotation);

Log.d(TAG,"Rotation in degrees->"+rotationInDegrees);

        Matrix matrix = new Matrix();
        if (rotation != 0f) {
            matrix.preRotate(rotationInDegrees);
        }

        Bitmap adjustedBitmap = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);

        parcelFileDescriptor.close();
        return adjustedBitmap;*/
        return image;
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

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


    private Bitmap getBitmap(Uri selectedimg) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        AssetFileDescriptor fileDescriptor = null;
        fileDescriptor =
                getContentResolver().openAssetFileDescriptor(selectedimg, "r");
        Bitmap original = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);
        return original;
    }


}
