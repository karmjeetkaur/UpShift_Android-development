package com.upshft.upshiftapp.signup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.upshft.upshiftapp.R;

public class AccountDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    TextView profile,security,billing,plan,card,paypal,payments,invoices;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        profile = (TextView) findViewById(R.id.profile);
        security = (TextView) findViewById(R.id.security);
        billing = (TextView) findViewById(R.id.billing);
        plan = (TextView) findViewById(R.id.plan);
        card = (TextView) findViewById(R.id.card);
        paypal = (TextView) findViewById(R.id.paypal);
        payments = (TextView) findViewById(R.id.payments);
        invoices = (TextView) findViewById(R.id.invoices);

        profile.setOnClickListener(this);
        security.setOnClickListener(this);
        billing.setOnClickListener(this);
        plan.setOnClickListener(this);
        card.setOnClickListener(this);
        paypal.setOnClickListener(this);
        payments.setOnClickListener(this);
        invoices.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.profile:
                Intent profile_intent = new Intent(AccountDetailsActivity.this,SecurityActivity.class);
                startActivity(profile_intent);
                break;

            case R.id.security:
                Intent security_intent = new Intent(AccountDetailsActivity.this,SecurityActivity.class);
                startActivity(security_intent);
                break;

            case R.id.billing:
                Intent billing_intent = new Intent(AccountDetailsActivity.this,SecurityActivity.class);
                startActivity(billing_intent);
                break;

            case R.id.plan:
                Intent plan_intent = new Intent(AccountDetailsActivity.this,PlanActivity.class);
                startActivity(plan_intent);
                break;

            case R.id.card:
                break;

            case R.id.paypal:
                break;

            case R.id.payments:
                break;

            case R.id.invoices:
                Intent invoices_intent = new Intent(AccountDetailsActivity.this,SecurityActivity.class);
                startActivity(invoices_intent);
                break;
        }
    }
}
