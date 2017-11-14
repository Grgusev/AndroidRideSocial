package com.pjtech.android.ridesocial.ui.activities;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.DataCollector;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.exceptions.BraintreeError;
import com.braintreepayments.api.exceptions.ErrorWithResponse;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.BraintreeResponseListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.model.PaymentType;
import com.pjtech.android.ridesocial.model.RequestType;

public class PaymentSetupActivity extends BaseActivity {

    //US
    LinearLayout mUSPaymentPart = null;
    RadioGroup mUSPaymentLocation   = null;

    //india
    RadioGroup mIndiaPaymentLocation   = null;
    ImageView mPaytmCheck  = null;

    Button mNextBtn     = null;
    TextView mSkipBtn   = null;

    //location
    boolean location_us     = true;

    String checkPayment     = "";

    BraintreeFragment mBraintreeFragment;
    String mAuthorization;
    String nonce ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_setup);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

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

        mSkipBtn    = (TextView) this.findViewById(R.id.btn_skip);

        mUSPaymentPart = (LinearLayout)this.findViewById(R.id.us_payment_section);
        mUSPaymentLocation  = (RadioGroup)this.findViewById(R.id.payment_us_location);

        mIndiaPaymentLocation   = (RadioGroup)this.findViewById(R.id.payment_india_location);
        mPaytmCheck     = (ImageView)this.findViewById(R.id.paytm_chk_img);

        mNextBtn    = (Button)this.findViewById(R.id.btn_next);

        setPaymentVisible();

        mSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PaymentSetupActivity.this, CabServiceActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedPayment = getPaymentSelect();

                Intent intent = new Intent();

                if (selectedPayment.equals(PaymentType.PAYPAL))
                {
//                    intent.setClass(PaymentSetupActivity.this, RegisterPaypalActivity.class);
                    PayPal.authorizeAccount(mBraintreeFragment);
                }
                else if (selectedPayment.equals(PaymentType.VEMEO))
                {
                    intent.setClass(PaymentSetupActivity.this, RegisterVemeoActivity.class);
                }
                else if (selectedPayment.equals(PaymentType.CREDIT))
                {
                    intent.setClass(PaymentSetupActivity.this, RegisterCreditActivity.class);
                }
                else
                {
                    intent.setClass(PaymentSetupActivity.this, RegisterPaytmActivity.class);
                }
//                startActivityForResult(intent, RequestType.REGISTER_PAYMENT_REQUEST);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestType.REGISTER_PAYMENT_REQUEST)
        {

        }
    }

    private String getPaymentSelect()
    {
        if (location_us)
        {
            int selected = mUSPaymentLocation.getCheckedRadioButtonId();

            if (selected == R.id.paypal) return PaymentType.PAYPAL;
            else if (selected == R.id.venmo) return PaymentType.VEMEO;
            else if (selected == R.id.credit) return PaymentType.CREDIT;
        }
        else
        {
            return PaymentType.PAYTM;
        }
        return PaymentType.PAYPAL;
    }

    private void setPaymentVisible()
    {
        if (location_us)
        {
            mUSPaymentPart.setVisibility(View.VISIBLE);
            mIndiaPaymentLocation.setVisibility(View.GONE);
        }
        else
        {
            mUSPaymentPart.setVisibility(View.GONE);
            mIndiaPaymentLocation.setVisibility(View.INVISIBLE);
        }
    }
}
