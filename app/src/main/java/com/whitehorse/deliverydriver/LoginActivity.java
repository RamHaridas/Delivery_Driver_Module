package com.whitehorse.deliverydriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hbb20.CountryCodePicker;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {


    String[] permissions={Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference;
    DocumentReference documentReference;
    List<String> contactList;
    CountryCodePicker ccp;
    Button log_in;
    TextInputEditText phone,pass;
    String phone_no, password,check;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    SharedPreferences isLogin;
    SharedPreferences.Editor edit;
    private static final int REQUEST_CODE=11;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("phone",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isLogin = getSharedPreferences("login",MODE_PRIVATE);
        edit = isLogin.edit();
        if(isLogin.getBoolean("login",false)){
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//Change Here
            startActivity(intent);
            finish();
        }
        phone_no = "";
        password = "";
        check = "";
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("DRIVERS");
        contactList = new ArrayList<>();
        phone = findViewById(R.id.phone_no);
        pass = findViewById(R.id.password);
        ccp = findViewById(R.id.ccp);
        log_in=findViewById(R.id.login);
        verifyPermissions();
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                phone_no = ccp.getDefaultCountryCodeWithPlus()+s.toString();
                getAllUsers();
            }
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                getAllUsers();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                password = s.toString();
                checkPass();
            }
        });
        getAllUsers();
    }
    public void openMain(View view) {
        if(contactList == null || contactList.isEmpty()){
            Toast.makeText(this,"Your Internet connection is weak",Toast.LENGTH_SHORT).show();
        }
        if(!phone_no.isEmpty()){
            checkPass();
        }
        if(!contactList.contains(phone_no)){
            phone.setError("User Not Registered");
            phone.requestFocus();
            return;
        }else if (phone_no.isEmpty()){
            phone.setError("Cannot be empty");
            phone.requestFocus();
            return;
        }else if (password.isEmpty()){
            pass.setError("Cannot be empty");
            pass.requestFocus();
            return;
        }else if(!check.equals(password)){
            pass.setError("Invalid Password");
            pass.requestFocus();
            return;
        }
        editor.putString("phone",phone_no);
        editor.apply();
        edit.putBoolean("login",true);
        edit.apply();
        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//Change Here
        startActivity(intent);
        finish();
    }

    public void forgotPassword(View view) {
        startActivity(new Intent(this, ForgetPasswordActivity.class));
    }

    public void register(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    void verifyPermissions(){
        if((ContextCompat.checkSelfPermission(this,permissions[0])== PackageManager.PERMISSION_GRANTED) &&  (ContextCompat.checkSelfPermission(this,
                permissions[1])== PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(this,
                permissions[2])== PackageManager.PERMISSION_GRANTED){


        }else{
            ActivityCompat.requestPermissions(this,permissions,REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void getAllUsers(){
        collectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            contactList.clear();
            for(QueryDocumentSnapshot post : queryDocumentSnapshots){
                contactList.add(post.getId());
            }
        });
    }

    public void checkPass(){
    if(phone_no==null){
        return;
    }
        documentReference = firebaseFirestore.collection("DRIVERS").document(phone_no);

        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            DriverData u = documentSnapshot.toObject(DriverData.class);
            if(u != null && u.getName() != null) {
                check = u.getPassword();
            }
        }).addOnFailureListener(e -> {

        });

    }
}
