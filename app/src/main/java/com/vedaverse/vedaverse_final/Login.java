package com.vedaverse.vedaverse_final;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Spinner countryCodeSpinner = findViewById(R.id.countryCodeSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.country_code, R.layout.spinner_item);

        adapter.setDropDownViewResource(R.layout.spinner_item);

        countryCodeSpinner.setAdapter(adapter);

        TextView signTextView = findViewById(R.id.sign);

        String text = "Already have an account?";
        SpannableString spannableString = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(Login.this, Account.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
            }

            @Override
            public void updateDrawState(@NonNull android.text.TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLACK);
                ds.setUnderlineText(true);
            }
        };

        spannableString.setSpan(clickableSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signTextView.setText(spannableString);
        signTextView.setMovementMethod(LinkMovementMethod.getInstance());


        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(v -> {
            Spinner countryCodeSpinnertosend = findViewById(R.id.countryCodeSpinner);
            EditText mobileNumberEditText = findViewById(R.id.editText);

            String countryCode = countryCodeSpinnertosend.getSelectedItem().toString();
            String mobileNumber = mobileNumberEditText.getText().toString();

            Intent intent = new Intent(Login.this, otpVerification.class);
            intent.putExtra("COUNTRY_CODE", countryCode);
            intent.putExtra("MOBILE_NUMBER", mobileNumber);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            finish();
        });
    }
}
