package com.smiligenceUAT1.metrozdeliveryappnew;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smiligenceUAT1.metrozdeliveryappnew.Common.Constant;
import com.smiligenceUAT1.metrozdeliveryappnew.Common.SplashActivity;
import com.smiligenceUAT1.metrozdeliveryappnew.Common.TextUtils;
import com.smiligenceUAT1.metrozdeliveryappnew.bean.OrderDetails;
import com.smiligenceUAT1.metrozdeliveryappnew.bean.PaymentDetails;
import com.smiligenceUAT1.metrozdeliveryappnew.bean.UserDetails;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smiligenceUAT1.metrozdeliveryappnew.Common.CommonMethods.*;


public class DeliveryBoyProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    String drivingLicenseProof;
    String aadharIdProof;
    String approvalStatus;
    StorageReference storageReference;
    StorageTask deliveryBoyTask, licenseTask, aadharTask;

    DatabaseReference deliveryBoyLoginDetails, orderDetailsRef;

    String startDateMon,endDateSunday;
    final ArrayList<String> list = new ArrayList<String>();
    OrderDetails orderDetails;
    int resultTotalAmount=0;
    PaymentDetails paymentDetails=new PaymentDetails();
    boolean checking=true;
    EditText firstNameEdt, lastNameEdt, email_Id_Edt, mobile_Number_Edt, zipCodeEdt, bankNameEdt, bankBranchNameEdt, bankifscCodeEdt,
            accountNumberEdt, aadharnumberEdt, bikeNumberEdt, bikeLicenseNumberEdt;

    Button deliveryBoyButton, aadharIdProofButton, approvalStatusButton, uploadKyc;

    ImageView deliveryImageView, aadharIdProofImageView, bikeLicenseImageView;

    Uri deliveryBoyImageUri, aadharIdProofUri, bikeLicenseUri;
    Uri deliveryBoyImageUrires, aadharIdProofUrires, bikeLicenseUrires;

    TextView statusTxt, suggestion;
    Ringtone r;
    SweetAlertDialog pDialog;

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 100;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 100;
    Intent intent = new Intent();
    NavigationView navigationView;
    public static Menu menuNav;
    public static View mHeaderView;
    public static String approvedStatus;


    public static TextView textViewUsername;
    public static ImageView imageView;

    public static String saved_customerPhonenumber, saved_id;

    Drawable drawable;

    Uri downloadUrl1, downloadUrl2, downloadUrl3;

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    boolean check = true;
    boolean notify=false;
    OrderDetails billDetails;

    int count = 0;
    private Handler handler = new Handler();

    public static String username, email, image;


    final ArrayList<String> orderListSize = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_boy_profile);


        disableAutofill();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        androidx.appcompat.widget.Toolbar toolbar1 = findViewById(R.id.toolbar);
        toolbar1.setTitle(Constant.MY_PROFILE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar1, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.White));
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(DeliveryBoyProfileActivity.this);
        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);
        navigationView.setCheckedItem(R.id.View_Profile_id);

        final SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_customerPhonenumber = loginSharedPreferences.getString("customerPhoneNumber", "");
        saved_id = loginSharedPreferences.getString("customerId", "");

        textViewUsername = (TextView) mHeaderView.findViewById(R.id.name123);
        imageView = (ImageView) mHeaderView.findViewById(R.id.imageViewheader123);
        menuNav = navigationView.getMenu();

        firstNameEdt = findViewById(R.id.Edittextfirstnamedelivery);
        lastNameEdt = findViewById(R.id.Edittextlastnamedelivery);
        email_Id_Edt = findViewById(R.id.Edittextemailiddelivery);
        mobile_Number_Edt = findViewById(R.id.Edittextphonenumberdelivery);
        zipCodeEdt = findViewById(R.id.EdittextPincode);
        bankNameEdt = findViewById(R.id.Edittextbanknamedelivery);
        bankBranchNameEdt = findViewById(R.id.Edittextbranchnamedelivery);
        bankifscCodeEdt = findViewById(R.id.Edittextifsccodedelivery);
        accountNumberEdt = findViewById(R.id.Edittextaccountnumberdelivery);
        aadharnumberEdt = findViewById(R.id.Edittextaadharnumberdelivery);
        bikeNumberEdt = findViewById(R.id.Edittextbikenumberdelivery);
        bikeLicenseNumberEdt = findViewById(R.id.Edittextbikelicensenumberdelivery);

        statusTxt = findViewById(R.id.statusTxt);
        suggestion = findViewById(R.id.Suggestion);


        deliveryBoyButton = findViewById(R.id.choosedeliveryboyprofile);
        aadharIdProofButton = findViewById(R.id.aadhariDProof);
        approvalStatusButton = findViewById(R.id.bikeLicensenumber);
        uploadKyc = findViewById(R.id.uploaddeliveryboydetails);


        deliveryImageView = findViewById(R.id.deliverBoyImageview);
        aadharIdProofImageView = findViewById(R.id.aadhariDProofImageview);
        bikeLicenseImageView = findViewById(R.id.bikeLicensenumberImageview);

        mobile_Number_Edt.setText(saved_customerPhonenumber);

        deliveryBoyLoginDetails = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("DeliveryBoyLoginDetails");
        orderDetailsRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("OrderDetails");



        Query query = deliveryBoyLoginDetails.orderByChild("mobileNumber").equalTo(saved_customerPhonenumber);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {

                    for (DataSnapshot approvedStatusSnap : dataSnapshot.getChildren()) {
                        UserDetails userDetails = approvedStatusSnap.getValue(UserDetails.class);
                        approvedStatus = userDetails.getDeliveryboyApprovalStatus();

                        username = userDetails.getFirstName();
                        email = userDetails.getEmailId();
                        image = userDetails.getDeliveryBoyProfile();
                        screenPermissions();


                        if (DeliveryBoyProfileActivity.username != null && !"".equals(DeliveryBoyProfileActivity.username)) {
                            textViewUsername.setText(DeliveryBoyProfileActivity.username);
                        }
                        if (DeliveryBoyProfileActivity.image != null && !"".equals(DeliveryBoyProfileActivity.image)) {
                            Picasso.get().load(String.valueOf(Uri.parse(DeliveryBoyProfileActivity.image))).into(imageView);
                        }

                        if (approvedStatus.equals("Approved") || approvedStatus.equals("Waiting for approval")) {
                            firstNameEdt.setEnabled(false);
                            lastNameEdt.setEnabled(false);
                            email_Id_Edt.setEnabled(false);
                            mobile_Number_Edt.setEnabled(false);
                            zipCodeEdt.setEnabled(false);
                            bankNameEdt.setEnabled(false);
                            bankBranchNameEdt.setEnabled(false);
                            accountNumberEdt.setEnabled(false);
                            bankifscCodeEdt.setEnabled(false);
                            aadharnumberEdt.setEnabled(false);
                            deliveryBoyButton.setEnabled(false);
                            aadharIdProofButton.setEnabled(false);
                            approvalStatusButton.setEnabled(false);
                            uploadKyc.setEnabled(false);
                            uploadKyc.setVisibility(View.INVISIBLE);
                            bikeLicenseNumberEdt.setEnabled(false);
                            bikeNumberEdt.setEnabled(false);

                            if (userDetails.getAadharIdProof() != null && userDetails.getDeliveryBoyProfile() != null && userDetails.getBikeLicenseNumber() != null) {
                                preLoadFunction(userDetails.getFirstName(), userDetails.getLastName()
                                        , userDetails.getEmailId(), userDetails.getMobileNumber(), userDetails.getZipcode(),
                                        userDetails.getBankName(), userDetails.getBankBranchName(), userDetails.getAccountNumber(),
                                        userDetails.getBankIfscCode(), userDetails.getAadharNumber(), userDetails.getDeliveryBoyProfile()
                                        , userDetails.getAadharIdProof(), userDetails.getDrivingLicenseProof(), userDetails.getBikeNumber(), userDetails.getBikeLicenseNumber(), userDetails.getDeliveryboyApprovalStatus(), userDetails.getCommentsIfAny());
                            }
                        } else if (approvedStatus.equals("Rejected")) {
                            firstNameEdt.setEnabled(true);
                            lastNameEdt.setEnabled(true);
                            email_Id_Edt.setEnabled(true);
                            mobile_Number_Edt.setEnabled(true);
                            zipCodeEdt.setEnabled(true);
                            bankNameEdt.setEnabled(true);
                            bankBranchNameEdt.setEnabled(true);
                            accountNumberEdt.setEnabled(true);
                            bankifscCodeEdt.setEnabled(true);
                            aadharnumberEdt.setEnabled(true);
                            deliveryBoyButton.setEnabled(true);
                            aadharIdProofButton.setEnabled(true);
                            approvalStatusButton.setEnabled(true);
                            uploadKyc.setEnabled(true);
                            bikeLicenseNumberEdt.setEnabled(true);
                            bikeNumberEdt.setEnabled(true);
                            uploadKyc.setVisibility(View.VISIBLE);

                            if (userDetails.getAadharIdProof() != null && userDetails.getDeliveryBoyProfile() != null && userDetails.getBikeLicenseNumber() != null) {
                                preLoadFunction(userDetails.getFirstName(), userDetails.getLastName()
                                        , userDetails.getEmailId(), userDetails.getMobileNumber(), userDetails.getZipcode(),
                                        userDetails.getBankName(), userDetails.getBankBranchName(), userDetails.getAccountNumber(),
                                        userDetails.getBankIfscCode(), userDetails.getAadharNumber(), userDetails.getDeliveryBoyProfile()
                                        , userDetails.getAadharIdProof(), userDetails.getDrivingLicenseProof(), userDetails.getBikeNumber(), userDetails.getBikeLicenseNumber(), userDetails.getDeliveryboyApprovalStatus(), userDetails.getCommentsIfAny());
                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        loadFunction();
        storageReference = FirebaseStorage.getInstance().getReference("DeliveryBoyLoginDetails");


        uploadKyc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(approvedStatus!=null && !approvedStatus.equals("")) {
                    if (approvedStatus.equals("Rejected")) {
                        if ("".equals(firstNameEdt.getText().toString().trim())) {
                            firstNameEdt.setError("Required");
                            return;
                        } else if ("".equals(lastNameEdt.getText().toString().trim())) {
                            lastNameEdt.setError("Required");
                            return;
                        } else if ("".equals(email_Id_Edt.getText().toString().trim())) {
                            email_Id_Edt.setError("Required");
                        } else if (!"".equalsIgnoreCase(email_Id_Edt.getText().toString()) && !TextUtils.isValidEmail(email_Id_Edt.getText().toString())) {
                            email_Id_Edt.setError("Enter Valid Email");
                            return;
                        } else if ("".equals(mobile_Number_Edt.getText().toString().trim())) {
                            mobile_Number_Edt.setError("Required");
                        } else if ("".equals(zipCodeEdt.getText().toString().trim())) {
                            zipCodeEdt.setError("Required");
                        } else if (!(zipCodeEdt.getText().toString().trim().length() == 6)) {
                            zipCodeEdt.setError("Enter valid Pincode");

                        } else if ("".equals(bankNameEdt.getText().toString().trim())) {
                            bankNameEdt.setError("Required");
                        } else if (!"".equalsIgnoreCase(bankNameEdt.getText().toString().trim()) && !(bankNameEdt.getText().toString().trim().length() >= 3)) {
                            bankNameEdt.setError("Bank name  requires minimum 3 characters");
                            return;
                        } else if ("".equals(bankBranchNameEdt.getText().toString().trim())) {
                            bankBranchNameEdt.setError("Required");
                        } else if (!"".equalsIgnoreCase(bankBranchNameEdt.getText().toString().trim()) && !(bankBranchNameEdt.getText().toString().trim().length() >= 3)) {
                            bankBranchNameEdt.setError("Branch Name  requires minimum 3 characters");
                            return;
                        } else if ("".equals(accountNumberEdt.getText().toString().trim())) {
                            accountNumberEdt.setError("Required");
                            return;
                        } else if (!"".equalsIgnoreCase(accountNumberEdt.getText().toString().trim()) &&
                                !(accountNumberEdt.getText().toString().length() >= 12) || !(accountNumberEdt.getText().toString().length() <= 15)) {
                            accountNumberEdt.setError("Enter Valid Account Number");
                            return;
                        } else if ("".equals(bankifscCodeEdt.getText().toString().trim())) {
                            bankifscCodeEdt.setError("Required");
                        } else if (!"".equalsIgnoreCase(bankifscCodeEdt.getText().toString().trim()) && !TextUtils.validIFSCCode(bankifscCodeEdt.getText().toString().trim())) {
                            bankifscCodeEdt.setError("Enter Valid IFSC Code");
                            return;
                        } else if ("".equals(aadharnumberEdt.getText().toString().trim())) {
                            aadharnumberEdt.setError("Required");
                        } else if (!(aadharnumberEdt.getText().toString().length() == 12)) {
                            aadharnumberEdt.setError("Invalid Aadhar Number");
                            return;
                        } else if ("".equals(bikeNumberEdt.getText().toString().trim())) {
                            bikeNumberEdt.setError("Required");
                        } else if ("".equals(bikeLicenseNumberEdt.getText().toString().trim())) {
                            bikeLicenseNumberEdt.setError("Required");
                            return;

                        } else if (deliveryBoyImageUri != null && !deliveryBoyImageUri.equals("") && aadharIdProofUri != null && !aadharIdProofUri.equals("") &&
                                bikeLicenseUri != null && !bikeLicenseUri.equals("")) {

                            pDialog = new SweetAlertDialog(DeliveryBoyProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#3F0300"));
                            pDialog.setTitleText("Loading ...");
                            pDialog.setCancelable(false);
                            pDialog.show();

                            final DatabaseReference deliveryBoyDetailsDataRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                    .getReference("DeliveryBoyLoginDetails").child(saved_id);

                            StorageReference fileRef = storageReference.child("Uploads/" + System.currentTimeMillis());

                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), deliveryBoyImageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                            byte[] data = baos.toByteArray();

                            deliveryBoyTask = fileRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful()) ;
                                    downloadUrl1 = urlTask.getResult();


                                    Bitmap bmp = null;
                                    try {
                                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), aadharIdProofUri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                    byte[] dataAadharProof = baos.toByteArray();
                                    StorageReference fileRef1 = storageReference.child("Uploads/" + System.currentTimeMillis());
                                    aadharTask = fileRef1.putBytes(dataAadharProof).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                            while (!urlTask.isSuccessful()) ;
                                            downloadUrl2 = urlTask.getResult();

                                            Bitmap bmp = null;
                                            try {
                                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), bikeLicenseUri);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                            byte[] dataLicenseProof = baos.toByteArray();

                                            StorageReference fileRef2 = storageReference.child("Uploads/" + System.currentTimeMillis());
                                            licenseTask = fileRef2.putBytes(dataLicenseProof).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                                    while (!urlTask.isSuccessful()) ;
                                                    downloadUrl3 = urlTask.getResult();
                                                    deliveryBoyDetailsDataRef.child("deliveryBoyProfile").setValue(downloadUrl1.toString());
                                                    deliveryBoyDetailsDataRef.child("drivingLicenseProof").setValue(downloadUrl3.toString());
                                                    deliveryBoyDetailsDataRef.child("aadharIdProof").setValue(downloadUrl2.toString());
                                                    if (downloadUrl3 != null) {
                                                        deliveryBoyDetailsDataRef.child("firstName").setValue(firstNameEdt.getText().toString());
                                                        deliveryBoyDetailsDataRef.child("lastName").setValue(lastNameEdt.getText().toString());
                                                        deliveryBoyDetailsDataRef.child("emailId").setValue(email_Id_Edt.getText().toString());
                                                        deliveryBoyDetailsDataRef.child("mobileNumber").setValue(mobile_Number_Edt.getText().toString());
                                                        deliveryBoyDetailsDataRef.child("zipcode").setValue(zipCodeEdt.getText().toString());
                                                        deliveryBoyDetailsDataRef.child("bankName").setValue(bankNameEdt.getText().toString());
                                                        deliveryBoyDetailsDataRef.child("bankBranchName").setValue(bankBranchNameEdt.getText().toString());
                                                        deliveryBoyDetailsDataRef.child("bankIfscCode").setValue(bankifscCodeEdt.getText().toString());
                                                        deliveryBoyDetailsDataRef.child("accountNumber").setValue(accountNumberEdt.getText().toString());
                                                        deliveryBoyDetailsDataRef.child("aadharNumber").setValue(aadharnumberEdt.getText().toString());
                                                        deliveryBoyDetailsDataRef.child("bikeNumber").setValue(bikeNumberEdt.getText().toString());
                                                        deliveryBoyDetailsDataRef.child("bikeLicenseNumber").setValue(bikeLicenseNumberEdt.getText().toString());
                                                        deliveryBoyDetailsDataRef.child("deliveryboyApprovalStatus").setValue("Waiting for approval");
                                                        deliveryBoyDetailsDataRef.child("commentsIfAny").setValue("");
                                                        pDialog.dismiss();
                                                        downloadUrl1 = null;
                                                        downloadUrl3 = null;
                                                        downloadUrl2 = null;

                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });

                        } else if (deliveryBoyImageUri != null && !deliveryBoyImageUri.equals("") && aadharIdProofUrires != null && !aadharIdProofUrires.equals("") &&
                                bikeLicenseUrires != null && !bikeLicenseUrires.equals("")) {

                            pDialog = new SweetAlertDialog(DeliveryBoyProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#3F0300"));
                            pDialog.setTitleText("Loading ...");
                            pDialog.setCancelable(false);
                            pDialog.show();

                            final DatabaseReference deliveryBoyDetailsDataRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                    .getReference("DeliveryBoyLoginDetails").child(saved_id);

                            StorageReference fileRef = storageReference.child("Uploads/" + System.currentTimeMillis());

                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), deliveryBoyImageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                            byte[] data = baos.toByteArray();

                            deliveryBoyTask = fileRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful()) ;
                                    downloadUrl1 = urlTask.getResult();

                                    if (!((Activity) DeliveryBoyProfileActivity.this).isFinishing()) {
                                        deliveryBoyDetailsDataRef.child("deliveryBoyProfile").setValue(downloadUrl1.toString());
                                        deliveryBoyDetailsDataRef.child("drivingLicenseProof").setValue(bikeLicenseUrires.toString());
                                        deliveryBoyDetailsDataRef.child("aadharIdProof").setValue(aadharIdProofUrires.toString());
                                    }
                                    if (downloadUrl1 != null) {
                                        deliveryBoyDetailsDataRef.child("firstName").setValue(firstNameEdt.getText().toString());
                                        deliveryBoyDetailsDataRef.child("lastName").setValue(lastNameEdt.getText().toString());
                                        deliveryBoyDetailsDataRef.child("emailId").setValue(email_Id_Edt.getText().toString());
                                        deliveryBoyDetailsDataRef.child("mobileNumber").setValue(mobile_Number_Edt.getText().toString());
                                        deliveryBoyDetailsDataRef.child("zipcode").setValue(zipCodeEdt.getText().toString());
                                        deliveryBoyDetailsDataRef.child("bankName").setValue(bankNameEdt.getText().toString());
                                        deliveryBoyDetailsDataRef.child("bankBranchName").setValue(bankBranchNameEdt.getText().toString());
                                        deliveryBoyDetailsDataRef.child("bankIfscCode").setValue(bankifscCodeEdt.getText().toString());
                                        deliveryBoyDetailsDataRef.child("accountNumber").setValue(accountNumberEdt.getText().toString());
                                        deliveryBoyDetailsDataRef.child("aadharNumber").setValue(aadharnumberEdt.getText().toString());
                                        deliveryBoyDetailsDataRef.child("bikeNumber").setValue(bikeNumberEdt.getText().toString());
                                        deliveryBoyDetailsDataRef.child("bikeLicenseNumber").setValue(bikeLicenseNumberEdt.getText().toString());
                                        deliveryBoyDetailsDataRef.child("deliveryboyApprovalStatus").setValue("Waiting for approval");
                                        deliveryBoyDetailsDataRef.child("commentsIfAny").setValue("");
                                        pDialog.dismiss();
                                        downloadUrl1 = null;
                                        bikeLicenseUrires = null;
                                        aadharIdProofUrires = null;

                                    }

                                }
                            });

                        } else if (deliveryBoyImageUrires != null && !deliveryBoyImageUrires.equals("") && aadharIdProofUri != null && !aadharIdProofUri.equals("") &&
                                bikeLicenseUrires != null && !bikeLicenseUrires.equals("")) {


                            pDialog = new SweetAlertDialog(DeliveryBoyProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#3F0300"));
                            pDialog.setTitleText("Loading ...");
                            pDialog.setCancelable(false);
                            pDialog.show();

                            final DatabaseReference deliveryBoyDetailsDataRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                    .getReference("DeliveryBoyLoginDetails").child(saved_id);
                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), aadharIdProofUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                            byte[] dataAadharProof = baos.toByteArray();
                            StorageReference fileRef1 = storageReference.child("Uploads/" + System.currentTimeMillis());
                            aadharTask = fileRef1.putBytes(dataAadharProof).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful()) ;
                                    downloadUrl2 = urlTask.getResult();

                                    Bitmap bmp = null;
                                    try {
                                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), bikeLicenseUri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    if (!((Activity) DeliveryBoyProfileActivity.this).isFinishing()) {
                                        deliveryBoyDetailsDataRef.child("deliveryBoyProfile").setValue(deliveryBoyImageUrires.toString());
                                        deliveryBoyDetailsDataRef.child("drivingLicenseProof").setValue(bikeLicenseUrires.toString());
                                        deliveryBoyDetailsDataRef.child("aadharIdProof").setValue(downloadUrl2.toString());
                                    }
                                    if (downloadUrl2 != null) {
                                        if (!((Activity) DeliveryBoyProfileActivity.this).isFinishing()) {
                                            deliveryBoyDetailsDataRef.child("firstName").setValue(firstNameEdt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("lastName").setValue(lastNameEdt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("emailId").setValue(email_Id_Edt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("mobileNumber").setValue(mobile_Number_Edt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("zipcode").setValue(zipCodeEdt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("bankName").setValue(bankNameEdt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("bankBranchName").setValue(bankBranchNameEdt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("bankIfscCode").setValue(bankifscCodeEdt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("accountNumber").setValue(accountNumberEdt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("aadharNumber").setValue(aadharnumberEdt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("bikeNumber").setValue(bikeNumberEdt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("bikeLicenseNumber").setValue(bikeLicenseNumberEdt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("deliveryboyApprovalStatus").setValue("Waiting for approval");
                                            deliveryBoyDetailsDataRef.child("commentsIfAny").setValue("");
                                            pDialog.dismiss();
                                            deliveryBoyImageUrires = null;
                                            bikeLicenseUrires = null;
                                            downloadUrl2 = null;
                                        }

                                    }
                                }
                            });

                        } else if (deliveryBoyImageUrires != null && !deliveryBoyImageUrires.equals("") && aadharIdProofUrires != null && !aadharIdProofUrires.equals("") &&
                                bikeLicenseUri != null && !bikeLicenseUri.equals("")) {


                            pDialog = new SweetAlertDialog(DeliveryBoyProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#3F0300"));
                            pDialog.setTitleText("Loading ...");
                            pDialog.setCancelable(false);
                            pDialog.show();

                            final DatabaseReference deliveryBoyDetailsDataRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                    .getReference("DeliveryBoyLoginDetails").child(saved_id);


                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), bikeLicenseUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                            byte[] dataLicenseProof = baos.toByteArray();

                            StorageReference fileRef2 = storageReference.child("Uploads/" + System.currentTimeMillis());
                            licenseTask = fileRef2.putBytes(dataLicenseProof).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful()) ;
                                    downloadUrl3 = urlTask.getResult();
                                    if (!((Activity) DeliveryBoyProfileActivity.this).isFinishing()) {
                                        deliveryBoyDetailsDataRef.child("deliveryBoyProfile").setValue(deliveryBoyImageUrires.toString());
                                        deliveryBoyDetailsDataRef.child("drivingLicenseProof").setValue(downloadUrl3.toString());
                                        deliveryBoyDetailsDataRef.child("aadharIdProof").setValue(aadharIdProofUrires.toString());
                                    }
                                    if (downloadUrl3 != null) {
                                        if (!((Activity) DeliveryBoyProfileActivity.this).isFinishing()) {
                                            deliveryBoyDetailsDataRef.child("firstName").setValue(firstNameEdt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("lastName").setValue(lastNameEdt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("emailId").setValue(email_Id_Edt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("mobileNumber").setValue(mobile_Number_Edt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("zipcode").setValue(zipCodeEdt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("bankName").setValue(bankNameEdt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("bankBranchName").setValue(bankBranchNameEdt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("bankIfscCode").setValue(bankifscCodeEdt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("accountNumber").setValue(accountNumberEdt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("aadharNumber").setValue(aadharnumberEdt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("bikeNumber").setValue(bikeNumberEdt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("bikeLicenseNumber").setValue(bikeLicenseNumberEdt.getText().toString());
                                            deliveryBoyDetailsDataRef.child("deliveryboyApprovalStatus").setValue("Waiting for approval");
                                            deliveryBoyDetailsDataRef.child("commentsIfAny").setValue("");
                                            pDialog.dismiss();
                                            deliveryBoyImageUrires = null;
                                            downloadUrl3 = null;
                                            aadharIdProofUrires = null;
                                        }

                                    }
                                }
                            });

                        } else if (deliveryBoyImageUrires != null && !deliveryBoyImageUrires.equals("") && aadharIdProofUri != null && !aadharIdProofUri.equals("") &&
                                bikeLicenseUri != null && !bikeLicenseUri.equals("")) {





                            pDialog = new SweetAlertDialog(DeliveryBoyProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#3F0300"));
                            pDialog.setTitleText("Loading ...");
                            pDialog.setCancelable(false);
                            pDialog.show();

                            final DatabaseReference deliveryBoyDetailsDataRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                    .getReference("DeliveryBoyLoginDetails").child(saved_id);
                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), aadharIdProofUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                            byte[] dataAadharProof = baos.toByteArray();
                            StorageReference fileRef1 = storageReference.child("Uploads/" + System.currentTimeMillis());
                            aadharTask = fileRef1.putBytes(dataAadharProof).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful()) ;
                                    downloadUrl2 = urlTask.getResult();

                                    Bitmap bmp = null;
                                    try {
                                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), bikeLicenseUri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                    byte[] dataLicenseProof = baos.toByteArray();

                                    StorageReference fileRef2 = storageReference.child("Uploads/" + System.currentTimeMillis());
                                    licenseTask = fileRef2.putBytes(dataLicenseProof).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                            while (!urlTask.isSuccessful()) ;
                                            downloadUrl3 = urlTask.getResult();
                                            deliveryBoyDetailsDataRef.child("deliveryBoyProfile").setValue(deliveryBoyImageUrires.toString());
                                            deliveryBoyDetailsDataRef.child("drivingLicenseProof").setValue(downloadUrl3.toString());
                                            deliveryBoyDetailsDataRef.child("aadharIdProof").setValue(downloadUrl2.toString());
                                            if (downloadUrl3 != null) {
                                                deliveryBoyDetailsDataRef.child("firstName").setValue(firstNameEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("lastName").setValue(lastNameEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("emailId").setValue(email_Id_Edt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("mobileNumber").setValue(mobile_Number_Edt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("zipcode").setValue(zipCodeEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("bankName").setValue(bankNameEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("bankBranchName").setValue(bankBranchNameEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("bankIfscCode").setValue(bankifscCodeEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("accountNumber").setValue(accountNumberEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("aadharNumber").setValue(aadharnumberEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("bikeNumber").setValue(bikeNumberEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("bikeLicenseNumber").setValue(bikeLicenseNumberEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("deliveryboyApprovalStatus").setValue("Waiting for approval");
                                                deliveryBoyDetailsDataRef.child("commentsIfAny").setValue("");
                                                pDialog.dismiss();
                                                deliveryBoyImageUrires = null;
                                                downloadUrl3 = null;
                                                downloadUrl2 = null;
                                            }
                                        }
                                    });
                                }
                            });


                        } else if (deliveryBoyImageUri != null && !deliveryBoyImageUri.equals("") && aadharIdProofUrires != null && !aadharIdProofUrires.equals("") &&
                                bikeLicenseUri != null && !bikeLicenseUri.equals("")) {





                            pDialog = new SweetAlertDialog(DeliveryBoyProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#3F0300"));
                            pDialog.setTitleText("Loading ...");
                            pDialog.setCancelable(false);
                            pDialog.show();

                            final DatabaseReference deliveryBoyDetailsDataRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                    .getReference("DeliveryBoyLoginDetails").child(saved_id);

                            StorageReference fileRef = storageReference.child("Uploads/" + System.currentTimeMillis());

                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), deliveryBoyImageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                            byte[] data = baos.toByteArray();

                            deliveryBoyTask = fileRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful()) ;
                                    downloadUrl1 = urlTask.getResult();
                                    Bitmap bmp = null;
                                    try {
                                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), bikeLicenseUri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                    byte[] dataLicenseProof = baos.toByteArray();

                                    StorageReference fileRef2 = storageReference.child("Uploads/" + System.currentTimeMillis());
                                    licenseTask = fileRef2.putBytes(dataLicenseProof).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                            while (!urlTask.isSuccessful()) ;
                                            downloadUrl3 = urlTask.getResult();
                                            deliveryBoyDetailsDataRef.child("deliveryBoyProfile").setValue(downloadUrl1.toString());
                                            deliveryBoyDetailsDataRef.child("drivingLicenseProof").setValue(downloadUrl3.toString());
                                            deliveryBoyDetailsDataRef.child("aadharIdProof").setValue(aadharIdProofUrires.toString());
                                            if (downloadUrl3 != null) {
                                                deliveryBoyDetailsDataRef.child("firstName").setValue(firstNameEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("lastName").setValue(lastNameEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("emailId").setValue(email_Id_Edt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("mobileNumber").setValue(mobile_Number_Edt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("zipcode").setValue(zipCodeEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("bankName").setValue(bankNameEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("bankBranchName").setValue(bankBranchNameEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("bankIfscCode").setValue(bankifscCodeEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("accountNumber").setValue(accountNumberEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("aadharNumber").setValue(aadharnumberEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("bikeNumber").setValue(bikeNumberEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("bikeLicenseNumber").setValue(bikeLicenseNumberEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("deliveryboyApprovalStatus").setValue("Waiting for approval");
                                                deliveryBoyDetailsDataRef.child("commentsIfAny").setValue("");
                                                pDialog.dismiss();
                                                downloadUrl1 = null;
                                                downloadUrl3 = null;
                                                aadharIdProofUrires = null;
                                            }
                                        }
                                    });

                                }
                            });


                        } else if (deliveryBoyImageUri != null && !deliveryBoyImageUri.equals("") && aadharIdProofUri != null && !aadharIdProofUri.equals("") &&
                                bikeLicenseUrires != null && !bikeLicenseUrires.equals("")) {

                            pDialog = new SweetAlertDialog(DeliveryBoyProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#3F0300"));
                            pDialog.setTitleText("Loading ...");
                            pDialog.setCancelable(false);
                            pDialog.show();

                            final DatabaseReference deliveryBoyDetailsDataRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                    .getReference("DeliveryBoyLoginDetails").child(saved_id);

                            StorageReference fileRef = storageReference.child("Uploads/" + System.currentTimeMillis());

                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), deliveryBoyImageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                            byte[] data = baos.toByteArray();

                            deliveryBoyTask = fileRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful()) ;
                                    downloadUrl1 = urlTask.getResult();


                                    Bitmap bmp = null;
                                    try {
                                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), aadharIdProofUri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                    byte[] dataAadharProof = baos.toByteArray();
                                    StorageReference fileRef1 = storageReference.child("Uploads/" + System.currentTimeMillis());
                                    aadharTask = fileRef1.putBytes(dataAadharProof).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                            while (!urlTask.isSuccessful()) ;
                                            downloadUrl2 = urlTask.getResult();

                                            deliveryBoyDetailsDataRef.child("deliveryBoyProfile").setValue(downloadUrl1.toString());
                                            deliveryBoyDetailsDataRef.child("drivingLicenseProof").setValue(bikeLicenseUrires.toString());
                                            deliveryBoyDetailsDataRef.child("aadharIdProof").setValue(downloadUrl2.toString());
                                            if (downloadUrl2 != null) {
                                                deliveryBoyDetailsDataRef.child("firstName").setValue(firstNameEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("lastName").setValue(lastNameEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("emailId").setValue(email_Id_Edt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("mobileNumber").setValue(mobile_Number_Edt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("zipcode").setValue(zipCodeEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("bankName").setValue(bankNameEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("bankBranchName").setValue(bankBranchNameEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("bankIfscCode").setValue(bankifscCodeEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("accountNumber").setValue(accountNumberEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("aadharNumber").setValue(aadharnumberEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("bikeNumber").setValue(bikeNumberEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("bikeLicenseNumber").setValue(bikeLicenseNumberEdt.getText().toString());
                                                deliveryBoyDetailsDataRef.child("deliveryboyApprovalStatus").setValue("Waiting for approval");
                                                deliveryBoyDetailsDataRef.child("commentsIfAny").setValue("");
                                                pDialog.dismiss();
                                                downloadUrl1 = null;
                                                bikeLicenseUrires = null;
                                                downloadUrl2 = null;

                                            }

                                        }
                                    });
                                }
                            });


                        } else {

                            pDialog = new SweetAlertDialog(DeliveryBoyProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#3F0300"));
                            pDialog.setTitleText("Loading ...");
                            pDialog.setCancelable(false);
                            pDialog.show();

                            final DatabaseReference deliveryBoyDetailsDataRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                    .getReference("DeliveryBoyLoginDetails").child(saved_id);


                            if (!((Activity) DeliveryBoyProfileActivity.this).isFinishing()) {
                                deliveryBoyDetailsDataRef.child("deliveryBoyProfile").setValue(deliveryBoyImageUrires.toString());
                                deliveryBoyDetailsDataRef.child("drivingLicenseProof").setValue(bikeLicenseUrires.toString());
                                deliveryBoyDetailsDataRef.child("aadharIdProof").setValue(aadharIdProofUrires.toString());

                                deliveryBoyDetailsDataRef.child("firstName").setValue(firstNameEdt.getText().toString());
                                deliveryBoyDetailsDataRef.child("lastName").setValue(lastNameEdt.getText().toString());
                                deliveryBoyDetailsDataRef.child("emailId").setValue(email_Id_Edt.getText().toString());
                                deliveryBoyDetailsDataRef.child("mobileNumber").setValue(mobile_Number_Edt.getText().toString());
                                deliveryBoyDetailsDataRef.child("zipcode").setValue(zipCodeEdt.getText().toString());
                                deliveryBoyDetailsDataRef.child("bankName").setValue(bankNameEdt.getText().toString());
                                deliveryBoyDetailsDataRef.child("bankBranchName").setValue(bankBranchNameEdt.getText().toString());
                                deliveryBoyDetailsDataRef.child("bankIfscCode").setValue(bankifscCodeEdt.getText().toString());
                                deliveryBoyDetailsDataRef.child("accountNumber").setValue(accountNumberEdt.getText().toString());
                                deliveryBoyDetailsDataRef.child("aadharNumber").setValue(aadharnumberEdt.getText().toString());
                                deliveryBoyDetailsDataRef.child("bikeNumber").setValue(bikeNumberEdt.getText().toString());
                                deliveryBoyDetailsDataRef.child("bikeLicenseNumber").setValue(bikeLicenseNumberEdt.getText().toString());
                                deliveryBoyDetailsDataRef.child("deliveryboyApprovalStatus").setValue("Waiting for approval");
                                deliveryBoyDetailsDataRef.child("commentsIfAny").setValue("");
                                deliveryBoyImageUrires = null;
                                bikeLicenseUrires = null;
                                aadharIdProofUrires = null;
                                pDialog.dismiss();

                            }
                        }
                    }
                }else {
                    if ("".equals(firstNameEdt.getText().toString().trim())) {
                        firstNameEdt.setError("Required");
                        return;
                    } else if ("".equals(lastNameEdt.getText().toString().trim())) {
                        lastNameEdt.setError("Required");
                        return;
                    } else if ("".equals(email_Id_Edt.getText().toString().trim())) {
                        email_Id_Edt.setError("Required");
                    } else if (!"".equalsIgnoreCase(email_Id_Edt.getText().toString()) && !TextUtils.isValidEmail(email_Id_Edt.getText().toString())) {
                        email_Id_Edt.setError("Enter Valid Email");
                        return;
                    } else if ("".equals(mobile_Number_Edt.getText().toString().trim())) {
                        mobile_Number_Edt.setError("Required");
                        return;
                    } else if ("".equals(zipCodeEdt.getText().toString().trim())) {
                        zipCodeEdt.setError("Required");
                        return;
                    } else if (!(zipCodeEdt.getText().toString().trim().length() == 6)) {
                        zipCodeEdt.setError("Enter valid Pincode");
                        return;
                    } else if ("".equals(bankNameEdt.getText().toString().trim())) {
                        bankNameEdt.setError("Required");
                        return;
                    } else if (!"".equalsIgnoreCase(bankNameEdt.getText().toString().trim()) && !(bankNameEdt.getText().toString().trim().length() >= 3)) {
                        bankNameEdt.setError("Bank name  requires minimum 3 characters");
                        return;
                    } else if ("".equals(bankBranchNameEdt.getText().toString().trim())) {
                        bankBranchNameEdt.setError("Required");
                    } else if (!"".equalsIgnoreCase(bankBranchNameEdt.getText().toString().trim()) && !(bankBranchNameEdt.getText().toString().trim().length() >= 3)) {
                        bankBranchNameEdt.setError("Branch Name  requires minimum 3 characters");
                        return;
                    } else if ("".equals(accountNumberEdt.getText().toString().trim())) {
                        accountNumberEdt.setError("Required");
                        return;
                    } else if (!"".equalsIgnoreCase(accountNumberEdt.getText().toString().trim()) &&
                            !(accountNumberEdt.getText().toString().length() >= 12) || !(accountNumberEdt.getText().toString().length() <= 15)) {
                        accountNumberEdt.setError("Enter Valid Account Number");
                        return;
                    } else if ("".equals(bankifscCodeEdt.getText().toString().trim())) {
                        bankifscCodeEdt.setError("Required");
                    } else if (!"".equalsIgnoreCase(bankifscCodeEdt.getText().toString().trim()) && !TextUtils.validIFSCCode(bankifscCodeEdt.getText().toString().trim())) {
                        bankifscCodeEdt.setError("Enter Valid IFSC Code");
                        return;
                    } else if ("".equals(aadharnumberEdt.getText().toString().trim())) {
                        aadharnumberEdt.setError("Required");
                        return;
                    }else if (!(aadharnumberEdt.getText().toString().length() == 12)) {
                        aadharnumberEdt.setError("Invalid Aadhar Number");
                        return;
                    }  else if ("".equals(bikeNumberEdt.getText().toString().trim())) {
                        bikeNumberEdt.setError("Required");
                    } else if ("".equals(bikeLicenseNumberEdt.getText().toString().trim())) {
                        bikeLicenseNumberEdt.setError("Required");
                        return;
                    } else if (bikeLicenseUri == null || bikeLicenseUri.equals("")) {
                        Toast.makeText(DeliveryBoyProfileActivity.this, "Please upload license Proof", Toast.LENGTH_SHORT).show();
                    } else if (aadharIdProofUri == null || aadharIdProofUri.equals("")) {
                        Toast.makeText(DeliveryBoyProfileActivity.this, "Please upload aadhar Proof", Toast.LENGTH_SHORT).show();
                    } else if (deliveryBoyImageUri == null || deliveryBoyImageUri.equals("")) {
                        Toast.makeText(DeliveryBoyProfileActivity.this, "Please upload profile image", Toast.LENGTH_SHORT).show();
                    } else {
                        pDialog = new SweetAlertDialog(DeliveryBoyProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#3F0300"));
                        pDialog.setTitleText("Loading ...");
                        pDialog.setCancelable(false);
                        pDialog.show();

                        final DatabaseReference deliveryBoyDetailsDataRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("DeliveryBoyLoginDetails").child(saved_id);

                        StorageReference fileRef = storageReference.child("Uploads/" + System.currentTimeMillis());

                        Bitmap bmp = null;
                        try {
                            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), deliveryBoyImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                        byte[] data = baos.toByteArray();

                        deliveryBoyTask = fileRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!urlTask.isSuccessful()) ;
                                downloadUrl1 = urlTask.getResult();


                                Bitmap bmp = null;
                                try {
                                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), aadharIdProofUri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                byte[] dataAadharProof = baos.toByteArray();
                                StorageReference fileRef1 = storageReference.child("Uploads/" + System.currentTimeMillis());
                                aadharTask = fileRef1.putBytes(dataAadharProof).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while (!urlTask.isSuccessful()) ;
                                        downloadUrl2 = urlTask.getResult();

                                        Bitmap bmp = null;
                                        try {
                                            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), bikeLicenseUri);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                        byte[] dataLicenseProof = baos.toByteArray();

                                        StorageReference fileRef2 = storageReference.child("Uploads/" + System.currentTimeMillis());
                                        licenseTask = fileRef2.putBytes(dataLicenseProof).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                                while (!urlTask.isSuccessful()) ;
                                                downloadUrl3 = urlTask.getResult();
                                                deliveryBoyDetailsDataRef.child("deliveryBoyProfile").setValue(downloadUrl1.toString());
                                                deliveryBoyDetailsDataRef.child("drivingLicenseProof").setValue(downloadUrl3.toString());
                                                deliveryBoyDetailsDataRef.child("aadharIdProof").setValue(downloadUrl2.toString());
                                                if (downloadUrl3 != null) {
                                                    deliveryBoyDetailsDataRef.child("firstName").setValue(firstNameEdt.getText().toString());
                                                    deliveryBoyDetailsDataRef.child("lastName").setValue(lastNameEdt.getText().toString());
                                                    deliveryBoyDetailsDataRef.child("emailId").setValue(email_Id_Edt.getText().toString());
                                                    deliveryBoyDetailsDataRef.child("mobileNumber").setValue(mobile_Number_Edt.getText().toString());
                                                    deliveryBoyDetailsDataRef.child("zipcode").setValue(zipCodeEdt.getText().toString());
                                                    deliveryBoyDetailsDataRef.child("bankName").setValue(bankNameEdt.getText().toString());
                                                    deliveryBoyDetailsDataRef.child("bankBranchName").setValue(bankBranchNameEdt.getText().toString());
                                                    deliveryBoyDetailsDataRef.child("bankIfscCode").setValue(bankifscCodeEdt.getText().toString());
                                                    deliveryBoyDetailsDataRef.child("accountNumber").setValue(accountNumberEdt.getText().toString());
                                                    deliveryBoyDetailsDataRef.child("aadharNumber").setValue(aadharnumberEdt.getText().toString());
                                                    deliveryBoyDetailsDataRef.child("bikeNumber").setValue(bikeNumberEdt.getText().toString());
                                                    deliveryBoyDetailsDataRef.child("bikeLicenseNumber").setValue(bikeLicenseNumberEdt.getText().toString());
                                                    deliveryBoyDetailsDataRef.child("deliveryboyApprovalStatus").setValue("Waiting for approval");
                                                    deliveryBoyDetailsDataRef.child("commentsIfAny").setValue("");
                                                    pDialog.dismiss();
                                                    downloadUrl1=null;
                                                    downloadUrl3=null;
                                                    downloadUrl2=null;
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });


                    }
                }
            }


        });
        Calendar c = Calendar.getInstance();
        // Set the calendar to monday of the current week
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        System.out.println();
        // Print dates of the current week starting on Monday
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        startDateMon=df.format(c.getTime());
        list.add(df.format(c.getTime()));
        for (int i = 0; i <6; i++) {
            c.add(Calendar.DATE, 1);
            list.add(df.format(c.getTime()));

        }
        endDateSunday=df.format(c.getTime());



            orderDetailsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (checking) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            orderListSize.clear();
                            for (DataSnapshot itemSnap : dataSnapshot.getChildren()) {
                                orderDetails = itemSnap.getValue(OrderDetails.class);

                                if (orderDetails.getDeliverboyId() != null && !orderDetails.getDeliverboyId().equals("")) {
                                    if (orderDetails.getDeliverboyId().equals(DeliveryBoyProfileActivity.saved_id)) {
                                        for (int i = 0; i < list.size(); i++) {
                                            if (orderDetails.getFormattedDate()!=null && !orderDetails.getFormattedDate().equals("")) {
                                                if (orderDetails.getFormattedDate().equals(list.get(i))) {
                                                    resultTotalAmount = resultTotalAmount + orderDetails.getTotalFeeForDeliveryBoy()+orderDetails.getTipAmount();
                                                    orderListSize.add(orderDetails.getOrderId());
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            DatabaseReference startTimeDataRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                    .getReference("DeliveryBoyPayments").child(DeliveryBoyProfileActivity.saved_id).child(startDateMon);
                            startTimeDataRef.child("startDate").setValue(startDateMon);
                            startTimeDataRef.child("endDate").setValue(endDateSunday);
                            startTimeDataRef.child("totalAmount").setValue(resultTotalAmount);
                            startTimeDataRef.child("orderCount").setValue(String.valueOf(orderListSize.size()));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        deliveryBoyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
                startActivityForResult(intent, 0);
            }
        });
        aadharIdProofButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
                startActivityForResult(intent, 1);
            }
        });
        approvalStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
                startActivityForResult(intent, 2);
            }
        });


    }

    private void openFileChooser() {
        intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
                if (requestCode == 0 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    deliveryBoyImageUrires=null;
                    deliveryBoyImageUri = data.getData();
                    Picasso.get().load(deliveryBoyImageUri).into(deliveryImageView);
                }
                break;
            case 1:
                if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    aadharIdProofUrires=null;
                    aadharIdProofUri = data.getData();
                    Picasso.get().load(aadharIdProofUri).into(aadharIdProofImageView);
                }
                break;
            case 2:
                if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    bikeLicenseUrires=null;
                    bikeLicenseUri = data.getData();
                    Picasso.get().load(bikeLicenseUri).into(bikeLicenseImageView);
                }
                break;


        }
    }

    private String getExtenstion(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.View_Profile_id) {
            Intent intent = new Intent(getApplicationContext(), DeliveryBoyProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            item.setChecked(true);
            item.setCheckable(true);
            item.setEnabled(true);
        } else if (id == R.id.checkin_checkout_id) {
            Intent intent = new Intent(getApplicationContext(), DeliveryBoyAttendanceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else if (id == R.id.View_Orders_id) {
            Intent intent = new Intent(getApplicationContext(), ViewOrdersActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.logout) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DeliveryBoyProfileActivity.this);
            bottomSheetDialog.setContentView(R.layout.logout_confirmation);
            Button logout = bottomSheetDialog.findViewById(R.id.logout);
            Button stayinapp = bottomSheetDialog.findViewById(R.id.stayinapp);

            bottomSheetDialog.show();
            bottomSheetDialog.setCancelable(false);

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.commit();

                    SharedPreferences billSharedPref = getSharedPreferences("BILL", MODE_PRIVATE);
                    SharedPreferences.Editor billEditor = billSharedPref.edit();
                    billEditor.clear();
                    billEditor.commit();

                    Intent intent = new Intent(DeliveryBoyProfileActivity.this, RegisterOtp.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    bottomSheetDialog.dismiss();
                }
            });
            stayinapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                }
            });

        } else if (id == R.id.attendanceHistory) {
            Intent intent = new Intent(getApplicationContext(), AttendanceHistory.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.payments) {
            Intent intent = new Intent(getApplicationContext(), DeliveryBoyPaymentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else if (id == R.id.weeklypayments) {
            Intent intent = new Intent ( getApplicationContext (), DeliveryBoyWeeklySettlements.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        }
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void preLoadFunction(String firstName, String lastName, String email_id, String mobileNumber,
                                 String zipCode, String bankName, String bankBranchName, String accountNumber,
                                 String bankIfscCode, String aadharNumber, String deliveryBoyProfile, String aadharProof, String drivingLicenseProof, String bikeNumber, String bikeLicenseNumber, String status, String comments) {

        firstNameEdt.setText(firstName);
        lastNameEdt.setText(lastName);
        email_Id_Edt.setText(email_id);
        mobile_Number_Edt.setText(mobileNumber);
        zipCodeEdt.setText(zipCode);
        bankNameEdt.setText(bankName);
        bankBranchNameEdt.setText(bankBranchName);
        accountNumberEdt.setText(accountNumber);
        bankifscCodeEdt.setText(bankIfscCode);
        aadharnumberEdt.setText(aadharNumber);
        bikeNumberEdt.setText(bikeNumber);
        bikeLicenseNumberEdt.setText(bikeLicenseNumber);
        if (status.equals("Approved")) {
            statusTxt.setTextColor(getResources().getColor(R.color.Green));
        } else if (status.equals("Rejected")) {
            statusTxt.setTextColor(getResources().getColor(R.color.Red));
        } else if (status.equals("Waiting for approval")) {
            statusTxt.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        statusTxt.setText(status);
        suggestion.setText(comments);
        if (deliveryBoyProfile != null || !deliveryBoyProfile.equals("")) {
            String deliveryBoyProfileUri = String.valueOf(Uri.parse(deliveryBoyProfile));
            Picasso.get().load(deliveryBoyProfileUri).into(deliveryImageView);
            deliveryBoyImageUrires = Uri.parse(deliveryBoyProfileUri);
        }
        if (aadharProof != null || !aadharProof.equals("")) {
            String aadharProofeUri = String.valueOf(Uri.parse(aadharProof));
            Picasso.get().load(aadharProofeUri).into(aadharIdProofImageView);
            aadharIdProofUrires = Uri.parse(aadharProofeUri);
        }
        if (drivingLicenseProof != null || !drivingLicenseProof.equals("")) {
            String drivingLicenseProofUri = String.valueOf(Uri.parse(drivingLicenseProof));
            Picasso.get().load(drivingLicenseProofUri).into(bikeLicenseImageView);
            bikeLicenseUrires = Uri.parse(drivingLicenseProofUri);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void createNotification(String res,String orderid) {
        int count=0;

        if (count==0) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(DeliveryBoyProfileActivity.this, default_notification_channel_id)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("New Delivery Order")
                    .setContentText(res)
                    .setAutoCancel(true);

            Intent secondActivityIntent = new Intent(this, SplashActivity.class);
            PendingIntent secondActivityPendingIntent = PendingIntent.getActivity(this, 0, secondActivityIntent, PendingIntent.FLAG_ONE_SHOT);
            mBuilder.addAction(R.mipmap.ic_launcher, "View", secondActivityPendingIntent);


            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                try {
                    Uri path = Uri.parse("android.resource://com.smiligenceUAT1.metrozdeliveryappnew/" + R.raw.old_telephone_tone);
                    r = RingtoneManager.getRingtone(getApplicationContext(), path);
                    r.play();
                } catch (Exception e) {
                }
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                assert mNotificationManager != null;
                mNotificationManager.createNotificationChannel(notificationChannel);

            }
            assert mNotificationManager != null;


                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notification = mBuilder.build();
                    notification.flags = Notification.FLAG_AUTO_CANCEL;
                    Random random = new Random();
                    int m = random.nextInt(9999 - 1000) + 1000;
                    notificationManager.notify(Integer.parseInt(orderid), notification);
                }
            },3000);
            count = count + 1;
        }
    }

    public void loadFunction()
    {
        final Query deliveryBoyNotificationQuery = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("OrderDetails").orderByChild("deliverboyId").equalTo(DeliveryBoyProfileActivity.saved_id);
        deliveryBoyNotificationQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notify=true;
                if (check)
                {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                    billDetails = dataSnapshot1.getValue(OrderDetails.class);

                                    if (!billDetails.getAssignedTo().equals("")) {
                                                if (!billDetails.getOrderStatus().equals("Delivery is on the way")) {
                                                    if (!billDetails.getOrderStatus().equals("Delivered")) {
                                                        if ("false".equals(billDetails.getNotificationStatus())) {
                                                            if (notify) {
                                                                if (!((Activity) DeliveryBoyProfileActivity.this).isFinishing()) {
                                                                    DatabaseReference orderDetailsRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                                            .getReference("OrderDetails").child(billDetails.getOrderId());
                                                                    orderDetailsRef.child("notificationStatus").setValue("true");

                                                                        createNotification("Order #" + billDetails.getOrderId() + " Assigned for delivery ", billDetails.getOrderId());
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                    }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DeliveryBoyProfileActivity.this);
        bottomSheetDialog.setContentView(R.layout.application_exiting_dialog);
        Button quit = bottomSheetDialog.findViewById(R.id.quit_dialog);
        Button cancel = bottomSheetDialog.findViewById(R.id.cancel_dialog);

        bottomSheetDialog.show();
        bottomSheetDialog.setCancelable(false);

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
                bottomSheetDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutofill() {
        getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
    }

}