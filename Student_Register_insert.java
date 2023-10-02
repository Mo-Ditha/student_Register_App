package com.example.studentregisterwithfirbase;



import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudentRegister extends AppCompatActivity {

    private EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDoB, editTextRegisterMobile, editTextRegisterPwd,editTextRegisterConfirmPwd;

    private ProgressBar progressBar;

    private RadioGroup radioGroupRegisterGender;

    private RadioButton radioButtonRegisterGenderSelected;

    private DatePickerDialog picker;
    private static final String TAG ="RegisterActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

        // Check if the ActionBar exists before trying to modify it
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Register"); // Set the title of the ActionBar
        }


        Toast.makeText(StudentRegister.this, "You can register now!", Toast.LENGTH_LONG).show();

        editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterDoB = findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirm_password);


        progressBar = findViewById(R.id.progressbar);
        //radio button for gender

        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        //Setting up datePicker on EditText
        editTextRegisterDoB.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //date picker
                picker = new DatePickerDialog(StudentRegister.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextRegisterDoB.setText(dayOfMonth+"/"+ (month +1 ) +"/"+ year);
                    }

                },year, month, day);
                picker.show();
            }
        });
        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

                //Obtain the entered data
                String textFullName = editTextRegisterFullName.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textDoB = editTextRegisterDoB.getText().toString();
                String textMobile = editTextRegisterMobile.getText().toString();
                String textPwd = editTextRegisterPwd.getText().toString();
                String textConfirmPwd = editTextRegisterConfirmPwd.toString();

                //can't obtain the value before verifying if any button was selected or not
                String textGender;

                //validation Mobile Number
                String mobileRegex = "[0-9]";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobile);

                if(TextUtils.isEmpty(textFullName)){
                    Toast.makeText(StudentRegister.this,"Please enter your full name",Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full name is required");
                    editTextRegisterFullName.requestFocus();
                }else if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(StudentRegister.this,"Please enter your email",Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Email is required");
                    editTextRegisterEmail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(StudentRegister.this,"Please check your email address again",Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Valid email is required");
                    editTextRegisterEmail.requestFocus();
                }else if(TextUtils.isEmpty(textDoB)){
                    Toast.makeText(StudentRegister.this,"Please enter the DoB",Toast.LENGTH_LONG).show();
                    editTextRegisterDoB.setError("Date of Birth is required");
                    editTextRegisterDoB.requestFocus();
                }else if(radioGroupRegisterGender.getCheckedRadioButtonId() == -1){
                    Toast.makeText(StudentRegister.this,"Please Select Your Gender",Toast.LENGTH_LONG).show();
                    radioButtonRegisterGenderSelected.requestFocus();
                }
                else if(TextUtils.isEmpty(textMobile)){
                    Toast.makeText(StudentRegister.this,"Please enter your mobile number",Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile number is required");
                    editTextRegisterMobile.requestFocus();
                }else if(textMobile.length() != 10){
                    Toast.makeText(StudentRegister.this,"Please enter valid mobile number",Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile No. Should be 10 digits");
                    editTextRegisterMobile.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(StudentRegister.this,"Please enter Your Password",Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password is Required");
                    editTextRegisterPwd.requestFocus();
                } else if (textPwd.length() < 6 ) {
                    Toast.makeText(StudentRegister.this,"Password should be at least 6 digits",Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password To Week");
                    editTextRegisterPwd.requestFocus();
                }else if(TextUtils.isEmpty(textConfirmPwd)){
                    Toast.makeText(StudentRegister.this,"Please Confirm your Password",Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("Password Confirmation is Required ");
                    editTextRegisterConfirmPwd.requestFocus();
                }else if (textPwd.equals(textConfirmPwd)){
                    Toast.makeText(StudentRegister.this,"Please same same password",Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("Password Confirmation is required");
                    editTextRegisterConfirmPwd.requestFocus();
                    //clear the enter password

                    editTextRegisterPwd.clearComposingText();
                    editTextRegisterConfirmPwd.clearComposingText();
                }else {
                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName,textEmail,textDoB,textGender,textMobile,textPwd);
                }

            }
        });
    }

    //Register user using the credentials given
    private void registerUser(String textFullName, String textEmail, String textDoB, String textGender, String textMobile, String textPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //create user profile
        auth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(StudentRegister.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser firebaseuser = auth.getCurrentUser();

                    //Update Display Name of user
                    UserProfileChangeRequest profileChangeRequest =new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaseuser.updateProfile(profileChangeRequest);

                    //enter user data into the firebase realtime Database
                    ReadWriteDetails writeUserDetails = new ReadWriteDetails(textDoB, textGender, textMobile);

                    //Extracting user Data into the firebase realtime database for registered use
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered User");
                    referenceProfile.child(firebaseuser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if( task.isSuccessful()){

                                //send Verification Email
                                firebaseuser.sendEmailVerification();

                                Toast.makeText(StudentRegister.this, "User registered Successfully! *Please verify your email*",Toast.LENGTH_LONG).show();

                   /* //Open User Profile after successfully
                    Intent intent =new Intent(StudentRegister.this,UserProfileActivity.class);
                    //To prevent user from returning back to back Register Activity on passing back button after registration
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                    //to close Register Activity
                    finish();*/

                            }
                            else
                            {
                                Toast.makeText(StudentRegister.this, "User registered Failed. Please try again",Toast.LENGTH_LONG).show();
                            }
                            //Hide progress Bar whether user creation is successful or failed
                            progressBar.setVisibility(View.GONE);

                        }
                    });


                }

                else {
                    try{
                        throw task.getException();

                    }
                    catch (FirebaseAuthWeakPasswordException e){
                        editTextRegisterPwd.setError("Your Password is too weak. Kindly use a mix of alphabets, number and special characters");
                        editTextRegisterPwd.requestFocus();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        editTextRegisterPwd.setError("Your Email is invalid or already re-enter.");
                        editTextRegisterPwd.requestFocus();
                    }
                    catch (FirebaseAuthUserCollisionException e){
                        editTextRegisterPwd.setError("User is already registered with this email. use another email.");
                        editTextRegisterPwd.requestFocus();
                    }
                    catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(StudentRegister.this, e.getMessage(),Toast.LENGTH_LONG).show();

                    }
                    //Hide progress Bar whether user creation is successful or failed
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
