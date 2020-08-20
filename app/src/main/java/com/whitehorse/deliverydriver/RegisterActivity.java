package com.whitehorse.deliverydriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements Dialog_Get_Image.onPhotoSelectedListener ,Dialog_Get_Image.MyDialogCloseListener{
    String vehicle_type;
    RadioButton scooter,van;
    ImageView profile_iv,fnumber_iv,bnumber_iv;
    Uri imageuri=null;
    Bitmap imagebitmap=null;
    StorageReference storageReference;
    Uri profile_uri,dlback_uri,dlfront_uri;
    Bitmap profile_bit,dlback_bit,dlfront_bit;
    EditText name,address,email,vehicle_no,pass,conf_pass;
    TextInputEditText phone;
    CountryCodePicker countryCodePicker;
    String mobile_no, naam, ema,add, plate_no,password,confirm_password;
    FirebaseFirestore firebaseFirestore;
    String image_name = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_register);

        vehicle_type = "";
        firebaseFirestore = FirebaseFirestore.getInstance();
        name = findViewById(R.id.name_et);
        email = findViewById(R.id.email_et);
        address = findViewById(R.id.address_et);
        phone = findViewById(R.id.phone_et);
        countryCodePicker = findViewById(R.id.ccp);
        vehicle_no = findViewById(R.id.vehiclenumber_et);
        pass = findViewById(R.id.password_et);
        conf_pass = findViewById(R.id.confirm_et);
        storageReference = FirebaseStorage.getInstance().getReference().child("DRIVERS");
        profile_iv = findViewById(R.id.profile_iv);
        fnumber_iv = findViewById(R.id.frontl_iv);
        bnumber_iv = findViewById(R.id.backl_iv);
        scooter = findViewById(R.id.scooter);
        van = findViewById(R.id.van);
        profile_iv.setOnClickListener(v -> {
            Bundle args=new Bundle();
            args.putInt("curr",1);
            Dialog_Get_Image dgi=new Dialog_Get_Image();
            dgi.setArguments(args);
            dgi.show(getSupportFragmentManager(),"DialogGetImage");
        });

        fnumber_iv.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("curr",2);
            Dialog_Get_Image dgi = new Dialog_Get_Image();
            dgi.setArguments(args);
            dgi.show(getSupportFragmentManager(),"DialogGetImage");
        });

        bnumber_iv.setOnClickListener(v -> {
            Bundle args=new Bundle();
            args.putInt("curr",3);
            Dialog_Get_Image dgi=new Dialog_Get_Image();
            dgi.setArguments(args);
            dgi.show(getSupportFragmentManager(),"DialogGetImage");
        });

        scooter.setOnClickListener(v -> {
            vehicle_type = scooter.getText().toString();
            Toast.makeText(RegisterActivity.this,vehicle_type,Toast.LENGTH_SHORT).show();
        });

        van.setOnClickListener(v -> {
            vehicle_type = van.getText().toString();
            Toast.makeText(RegisterActivity.this,vehicle_type,Toast.LENGTH_SHORT).show();
        });
    }
    public void submit_open(View view) {

        naam = name.getText().toString().trim();
        ema = email.getText().toString().trim();
        add = address.getText().toString().trim();
        mobile_no = countryCodePicker.getDefaultCountryCodeWithPlus() + phone.getText().toString().trim();
        plate_no = vehicle_no.getText().toString().trim();
        password = pass.getText().toString().trim();
        confirm_password = conf_pass.getText().toString().trim();

        if(naam.isEmpty()){
            name.setError("Cannot be empty");
            name.requestFocus();
            return;
        }else if(!isEmailValid(ema)){
            email.setError("Invalid Email Format");
            email.requestFocus();
            return;
        }else if(add.isEmpty()){
            address.setError("Cannot be empty");
            address.requestFocus();
            return;
        }else if(phone.getText().toString().length() < 10){
            phone.setError("Invalid number");
            phone.requestFocus();
            return;
        }else if(plate_no.isEmpty()){
            vehicle_no.setError("Cannot be empty");
            vehicle_no.requestFocus();
            return;
        }else if(password.isEmpty()){
            pass.setError("Cannot be Empty");
            pass.requestFocus();
            return;
        }else if(!confirm_password.equals(password)){
            conf_pass.setError("Does not match");
            conf_pass.requestFocus();
            return;
        }else if(profile_uri == null && profile_bit == null){
            Toast.makeText(this,"Please upload Profile picture",Toast.LENGTH_SHORT).show();
            return;
        }else if(dlback_uri == null && dlback_bit == null){
            Toast.makeText(this,"Please upload Driving License back image",Toast.LENGTH_SHORT).show();
            return;
        }else if(dlfront_uri == null && dlfront_bit == null){
            Toast.makeText(this,"Please upload Driving License front image",Toast.LENGTH_SHORT).show();
            return;
        }else if(vehicle_type.isEmpty()){
            Toast.makeText(this,"Please select your vehicle type",Toast.LENGTH_SHORT).show();
            return;
        }

        if(profile_uri != null){
            uploadDataUri(profile_uri);
        }else if(profile_bit != null){
            uploadDataBitmap(profile_bit);
        }
        if(dlfront_uri != null){
            uploadFile(dlfront_uri,2);
        }else if(dlfront_bit != null){
            uploadBitmap(dlfront_bit,2);
        }

        if(dlback_uri != null){
            uploadFile(dlback_uri,3);
        }else if(dlback_bit != null){
            uploadBitmap(dlback_bit,3);
        }

        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
            }
        },2500);*/
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
    }

    @Override
    public void getImagePath(Uri imagePath) {
        imageuri=imagePath;
        imagebitmap=null;
    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        imagebitmap=bitmap;
        imageuri=null;
    }

    @Override
    public void handleDialogClose(int num) {
        if(num == 1){
            if(imagebitmap != null){
                profile_iv.setImageDrawable(null);
                profile_iv.setImageBitmap(imagebitmap);
                profile_bit = imagebitmap;
            }
            else if(imageuri != null){
                profile_iv.setImageDrawable(null);
                profile_iv.setImageURI(imageuri);
                profile_uri = imageuri;
            }
            else{
                Toast.makeText(this,"No image was selected",Toast.LENGTH_SHORT).show();
            }
        }
        else if(num==2){
            if(imagebitmap!=null){
                fnumber_iv.setImageDrawable(null);
                fnumber_iv.setImageBitmap(imagebitmap);
                dlfront_bit = imagebitmap;
            }
            else if(imageuri!=null){
                fnumber_iv.setImageDrawable(null);
                fnumber_iv.setImageURI(imageuri);
                dlfront_uri = imageuri;
            }
            else{
                Toast.makeText(this,"No image was selected",Toast.LENGTH_SHORT).show();
            }
        }
        else if(num==3){
            if(imagebitmap != null){
                bnumber_iv.setImageDrawable(null);
                bnumber_iv.setImageBitmap(imagebitmap);
                dlback_bit = imagebitmap;
            }
            else if(imageuri != null){
                bnumber_iv.setImageDrawable(null);
                bnumber_iv.setImageURI(imageuri);
                dlback_uri = imageuri;
            }
            else{
                Toast.makeText(this,"No image was selected",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadFile(Uri uri, int num){
        final String time = String.valueOf(System.currentTimeMillis());
        final String ext = getFileExtension(uri);
        String imagename1 = "";
        if(num == 2){
            imagename1 = mobile_no+"dl_front";
        }else if(num == 3){
            imagename1 = mobile_no+"dl_back";
        }
        StorageReference storageReference1 = storageReference.child(imagename1);
        UploadTask uploadTask = storageReference1.putFile(uri);

        uploadTask.addOnSuccessListener(taskSnapshot -> Toast.makeText(RegisterActivity.this,"Uploaded",Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(RegisterActivity.this,"ERROR IN UPLOAD: "+e.getMessage(),Toast.LENGTH_SHORT).show()).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double p = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                //progressBar.setProgress((int)p);
            }
        });
    }

    public void uploadBitmap(Bitmap bitmap, int num){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] bytes = baos.toByteArray();
        final String time = String.valueOf(System.currentTimeMillis());
        final String ext = "jpeg";
        String name = "";
        if(num == 2){
            name = mobile_no+"dl_front";
        }else if(num == 3){
            name = mobile_no+"dl_back";
        }
        StorageReference storageReference1 = storageReference.child(name);
        UploadTask uploadTask = storageReference1.putBytes(bytes);

        uploadTask.addOnSuccessListener(taskSnapshot -> Toast.makeText(RegisterActivity.this,"Uploaded",Toast.LENGTH_SHORT).show()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this,"ERROR IN UPLOAD: "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(taskSnapshot -> {
            double p = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
            //progressBar.setProgress((int)p);
        });
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeInfo = MimeTypeMap.getSingleton();
        return  mimeTypeInfo.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void uploadDataBitmap(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] bytes = baos.toByteArray();
        final String time = String.valueOf(System.currentTimeMillis());
        final String ext = "jpeg";
        String name = "";
        name = mobile_no+"profile";

        StorageReference storageReference1 = storageReference.child(name);
        UploadTask uploadTask = storageReference1.putBytes(bytes);

        String finalName = name;
        uploadTask.addOnSuccessListener(taskSnapshot ->
                storageReference.child(finalName).getDownloadUrl()
                        .addOnCompleteListener(task -> {

                    String url = task.getResult().toString();
                        DriverData driverData = new DriverData();
                        driverData.setName(naam);
                        driverData.setAddress(add);
                        driverData.setEmail(ema);
                        driverData.setPhone(mobile_no);
                        driverData.setPassword(password);
                        driverData.setProfile_url(url);
                        driverData.setVehicle_type(vehicle_type);
                        driverData.setNumber_plate(plate_no);
                        firebaseFirestore.collection("DRIVERS").document(mobile_no).set(driverData)
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(RegisterActivity.this,"Registration Successful",Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                Toast.makeText(RegisterActivity.this,"Registration Failed: "+e.getMessage(),Toast.LENGTH_SHORT).show());

                })).addOnFailureListener(e ->
                Toast.makeText(RegisterActivity.this,"ERROR IN UPLOAD: "+e.getMessage(),Toast.LENGTH_SHORT).show())
                .addOnProgressListener(taskSnapshot -> {
                    double p = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    //progressBar.setProgress((int)p);
                });
    }

    public void uploadDataUri(Uri uri){
        final String time = String.valueOf(System.currentTimeMillis());
        final String ext = getFileExtension(uri);
        String name = "";
        name = mobile_no+"profile";

        StorageReference storageReference1 = storageReference.child(name);
        UploadTask uploadTask = storageReference1.putFile(uri);

        String finalName = name;
        uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.child(finalName).getDownloadUrl()
                .addOnCompleteListener(task -> {
                        String url = task.getResult().toString();
                        DriverData driverData = new DriverData();
                        driverData.setName(naam);
                        driverData.setAddress(add);
                        driverData.setEmail(ema);
                        driverData.setPhone(mobile_no);
                        driverData.setPassword(password);
                        driverData.setProfile_url(url);
                        driverData.setVehicle_type(vehicle_type);
                        driverData.setNumber_plate(plate_no);
                        firebaseFirestore.collection("DRIVERS").document(mobile_no).set(driverData)
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(RegisterActivity.this,"Registration Successful",Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(RegisterActivity.this,"Registration Failed: "+e.getMessage(),Toast.LENGTH_SHORT).show());

                })).addOnFailureListener(e ->
                Toast.makeText(RegisterActivity.this,"ERROR IN UPLOAD: "+e.getMessage(),Toast.LENGTH_SHORT).show())
                .addOnProgressListener(taskSnapshot -> {
                    double p = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    //progressBar.setProgress((int)p);
                });
    }
}
