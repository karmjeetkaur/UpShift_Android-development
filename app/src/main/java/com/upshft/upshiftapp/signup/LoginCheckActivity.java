package com.upshft.upshiftapp.signup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.upshft.upshiftapp.R;

public class LoginCheckActivity extends AppCompatActivity {

    Button bt_login,bt_signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_check);

        bt_login = (Button) findViewById(R.id.bt_login);
        bt_signUp = (Button) findViewById(R.id.bt_signUp);


        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signIn_intent = new Intent(LoginCheckActivity.this,SignInActivity.class);
                startActivity(signIn_intent);
            }
        });

        bt_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signUp_intent = new Intent(LoginCheckActivity.this,SignUpActivity.class);
                startActivity(signUp_intent);
            }
        });
    }
}
