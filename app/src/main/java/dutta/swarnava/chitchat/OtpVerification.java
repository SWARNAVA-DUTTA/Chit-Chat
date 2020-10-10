package dutta.swarnava.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.iid.FirebaseInstanceId;
import com.poovam.pinedittextfield.PinField;


import java.util.concurrent.TimeUnit;

public class OtpVerification extends AppCompatActivity {
    String verificationId;
    FirebaseAuth mAuth;
    TextView countdownText,msg,wrngno,resendsms;
    PinField verify_code;
    Button btn_verify;
    ProgressDialog loading;
    ImageView icon;
    CountDownTimer countDownTimer;
    private DatabaseReference UsersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String str = intent.getStringExtra("message_key");
        getSupportActionBar().setTitle("Verify "+str);
        countdownText = (TextView) findViewById(R.id.countdownText);
        msg = (TextView) findViewById(R.id.msg);
        msg.setText("We have sent an SMS with a code to "+str+". ");
        wrngno = (TextView) findViewById(R.id.wrngno);
        resendsms = (TextView) findViewById(R.id.resendsms);
        verify_code=(PinField)findViewById(R.id.verify_code);
        btn_verify=(Button)findViewById(R.id.btn_verify);
        icon=(ImageView) findViewById(R.id.icon);
        loading=new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        verificationSend(str);
        start_Timer(1*60*1000);
        verify_code.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete (@NotNull String enteredText) {
                String code = verify_code.getText().toString().trim();
                verifyCode(code);
                return true; // Return false to keep the keyboard open else return true to close the keyboard
            }
        });
    }

    public void user_execute(View view) {
        String code = verify_code.getText().toString().trim();
        if (code.isEmpty() || code.length() < 6) {
            verify_code.setError("Enter code...");
            verify_code.requestFocus();
            return;
        }
        loading.setMessage("Verifying...");
        loading.setCanceledOnTouchOutside(false);
        loading.show();
        verifyCode(code);
    }
    public void wrongno(View view)
    {
        super.onBackPressed();
    }
    private void   verificationSend(String phone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,//Phone number provided by User
                60,//Max time Time limited within which Home need to be forwarded
                TimeUnit.SECONDS,//Time is calculated in Unit of Seconds
                TaskExecutors.MAIN_THREAD,//The execution is taking place in MAIN_THREAD, so until the
                //whole process is completed, we wont move to next operation
                mCallBack//Starting the process of Generating and Receiving Home
        );
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        //Responsible for Sending Verification Home
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        //Responsible for Reading Home automatically
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verify_code.setText(code);
                verifyCode(code);
            }
        }

        //Responsible for Error checking during Home send
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OtpVerification.this,e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential  credential = PhoneAuthProvider.getCredential(verificationId,code);
        signInWithCredential(credential);
    }

    //When verification is complete what next action to be done-

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String currentUserId = mAuth.getCurrentUser().getUid();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            UsersRef.child(currentUserId).child("device_token")
                                    .setValue(deviceToken)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            Intent intent = getIntent();
                                            String str = intent.getStringExtra("message_key");
                                            SharedPreferences.Editor editor = getSharedPreferences("Reg",MODE_PRIVATE).edit();
                                            editor.putString("Phone_Number", str);
                                            editor.commit();
                                            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Phone").setValue(str);
                                            Intent i=new Intent(getApplicationContext(),UserCredential.class);
                                            loading.dismiss();
                                            i.putExtra("message_key", str);
                                            startActivity(i);
                                        }
                                    });



                            //  Toast.makeText(HomeActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(OtpVerification.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            loading.dismiss();
                        }
                    }
                });

    }

    public void startTimer(View view) {
//        countdownText.setVisibility(View.VISIBLE);
//        resendsms.setClickable(false);
//        icon.setImageResource(R.drawable.sms_gray);
//        resendsms.setTextColor(Color.parseColor("#9F9796"));
        Intent intent = getIntent();
        String str = intent.getStringExtra("message_key");
        verificationSend(str);
        if (countDownTimer == null) {
                int noOfMinutes = 1 * 60 * 1000;
                start_Timer(noOfMinutes);
              }
        else {
            stopCountdown();
        }
    }
    private void start_Timer(int noOfMinutes) {
        countdownText.setVisibility(View.VISIBLE);
        resendsms.setClickable(false);
        icon.setImageResource(R.drawable.sms_gray);
        countDownTimer = new CountDownTimer(noOfMinutes, 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                //Convert milliseconds into hour,minute and seconds
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                countdownText.setText(hms);//set text
            }

            public void onFinish() {
                resendsms.setClickable(true);
                icon.setImageResource(R.drawable.sms_black);
                resendsms.setTextColor(Color.parseColor("#000000"));
                countdownText.setVisibility(View.GONE); //On finish change timer text
                countDownTimer = null;//set CountDownTimer to null
            }
        }.start();

    }
    private void stopCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}