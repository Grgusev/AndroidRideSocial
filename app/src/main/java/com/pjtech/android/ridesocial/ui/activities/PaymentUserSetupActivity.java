package com.pjtech.android.ridesocial.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.DataCollector;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.exceptions.ErrorWithResponse;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.BraintreeResponseListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.model.PaymentType;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentUserSetupActivity extends BaseActivity {

    @BindView(R.id.btn_next)
    Button next;

    @BindView(R.id.payment_us_location)
    RadioGroup paymentSection;

    BraintreeFragment mBraintreeFragment;
    String mAuthorization;
    String nonce ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_user_setup);
        ButterKnife.bind(this);

        try {
            mBraintreeFragment = BraintreeFragment.newInstance(this, mAuthorization);

            DataCollector.collectDeviceData(mBraintreeFragment, new BraintreeResponseListener<String>() {
                @Override
                public void onResponse(String deviceData) {

                }
            });

            mBraintreeFragment.addListener(new PaymentMethodNonceCreatedListener() {
                @Override
                public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
                    nonce = paymentMethodNonce.getNonce();
                }
            });

            mBraintreeFragment.addListener(new BraintreeCancelListener() {

                @Override
                public void onCancel(int requestCode) {

                }
            });

            mBraintreeFragment.addListener(new BraintreeErrorListener() {
                @Override
                public void onError(Exception error) {
                    if (error instanceof ErrorWithResponse) {
                    }
                }
            });
        } catch (InvalidArgumentException e) {
        }

        this.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBack();
            }
        });
    }

    private String getPaymentSelect()
    {
        int selected = paymentSection.getCheckedRadioButtonId();

        if (selected == R.id.paypal) return PaymentType.PAYPAL;
        else if (selected == R.id.venmo) return PaymentType.VEMEO;
        else if (selected == R.id.credit) return PaymentType.CREDIT;

        return  PaymentType.PAYPAL;
    }

    @OnClick(R.id.btn_next)
    public void nextClick()
    {
        String selectedPayment = getPaymentSelect();

        if (selectedPayment.equals(PaymentType.PAYPAL))
        {
            PayPal.authorizeAccount(mBraintreeFragment);
        }
        else if (selectedPayment.equals(PaymentType.VEMEO))
        {
        }
        else if (selectedPayment.equals(PaymentType.CREDIT))
        {
        }
    }
}
