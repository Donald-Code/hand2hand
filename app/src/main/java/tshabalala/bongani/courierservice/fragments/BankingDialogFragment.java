package tshabalala.bongani.courierservice.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;

import tshabalala.bongani.courierservice.MainActivity;
import tshabalala.bongani.courierservice.ParcelActivity;
import tshabalala.bongani.courierservice.R;
import tshabalala.bongani.courierservice.model.User;

public class BankingDialogFragment extends DialogFragment {

    private Spinner spinBanks;
    private Button btnBack,btnAdd;
    private EditText etAccNumber;
    private EditText etAccHolder;
    private EditText etCardCCV;

    private String banks,accNumber,accHolder,accCCV;
    static User user;

    private ProgressDialog pDialog;
    //  private static String url = "http://ec2-52-71-253-250.compute-1.amazonaws.com/php/log.php";
    private static String urlCon = "http://funtym.000webhostapp.com/registerBank.php";
    String [] province = {"Select Bank","FNB","Nedbank","Standard Bank","ABSA","Capitec","Wes-Bank"};

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 10000;

    public static BankingDialogFragment getInstance(User emaill){

        user = emaill;

        return new BankingDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState == null){
            View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.bank_details, null);

            spinBanks = (Spinner)dialogView. findViewById(R.id.spinnerBanks);

            etAccNumber = (EditText)dialogView.findViewById(R.id.accountNumber);
            etAccHolder = (EditText)dialogView.findViewById(R.id.accountHolder);
            etCardCCV = (EditText)dialogView.findViewById(R.id.accountCCV);

            btnBack = (Button)dialogView.findViewById(R.id.button_back);
            btnAdd = (Button)dialogView.findViewById(R.id.button_add);

            ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getActivity(),
                    R.layout.spinner_layout, Arrays.asList(province));
            dataAdapter2.setDropDownViewResource(R.layout.spinner_layout);
            spinBanks.setAdapter(dataAdapter2);

            spinBanks.setOnItemSelectedListener(new CustomOnItemSelectedListeners());


            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("user",user);
                    getActivity().startActivity(intent);
//                    BankingDialogFragment logOptionsDialogFragment = BankingDialogFragment.getInstance(email);
//                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                    ft.add(logOptionsDialogFragment,"newFragment");
//                    ft.commit();
                }
            });

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (spinBanks.getSelectedItem().toString().equals("Select Bank")) {
                        Toast.makeText(getActivity(), "Please select Bank", Toast.LENGTH_SHORT).show();


                    }else {
                        attemptRegistration();
                    }
                }
            });






            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyWhiteAlertDialogStyle)
                    .setView(dialogView)
                    .setNegativeButton("", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            return builder.show();
        } else {
            return super.onCreateDialog(savedInstanceState);
        }
    }

    private void attemptRegistration() {

        // Store values at the time of the login attempt.
        accNumber = etAccNumber.getText().toString();
        accHolder = etAccHolder.getText().toString();
        accCCV = etCardCCV.getText().toString();


        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(accNumber)) {
            etAccNumber.setError("Error - Account number can't be empty");
            focusView = etAccNumber;
            cancel = true;

        }

        if (TextUtils.isEmpty(accHolder)) {
            etAccHolder.setError("Error - Account holder can't be empty");
            focusView = etAccHolder;
            cancel = true;

        }

        if (TextUtils.isEmpty(accCCV)) {
            etCardCCV.setError("Error - ccv can't be empty");
            focusView = etCardCCV;
            cancel = true;

        }




        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            if (!isOnline(getActivity())) {
                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_LONG).show();
            } else {

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);

            }
        }
    }



    private boolean isOnline(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public class CustomOnItemSelectedListeners implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            banks = spinBanks.getSelectedItem().toString();
            if (banks.equals("Select Bank")) {
                // etCity.setVisibility(View.GONE);
                // Toast.makeText(getActivity(), "Please select Bank", Toast.LENGTH_SHORT).show();
            }
            //           else if (strCity.equals("Other")) {
//
//                etCity.setVisibility(View.VISIBLE);
//                strCity = etCity.getText().toString();
//
//            }else
//            {
//                etCity.setVisibility(View.GONE);
//            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}
