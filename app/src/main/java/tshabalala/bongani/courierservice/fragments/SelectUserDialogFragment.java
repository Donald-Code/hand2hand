package tshabalala.bongani.courierservice.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import tshabalala.bongani.courierservice.R;
import tshabalala.bongani.courierservice.helper.Common;


public class SelectUserDialogFragment  extends DialogFragment
{

    private RadioGroup radioUser;
    private RadioButton radioShipper;
    private RadioButton radioCustomer;
    private String selectedChoose;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public static SelectUserDialogFragment newInstance(){
        Bundle args = new Bundle();
        SelectUserDialogFragment frag = new SelectUserDialogFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    TextView textViewMessage;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState == null){
            View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.selection_user_fragment, null);
           // Toolbar toolbar = (Toolbar) dialogView.findViewById(R.id.dialogTitleToolbar);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            editor = sharedPreferences.edit();
            textViewMessage = (TextView) dialogView.findViewById(R.id.dialogMessageTV);

            radioUser = dialogView.findViewById(R.id.radioUser);
            radioCustomer = dialogView.findViewById(R.id.radioCustomer);
            radioShipper = dialogView.findViewById(R.id.radioShipper);

            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
                    .setView(dialogView)
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false)
                    .setPositiveButton("Next", null)
                    .create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(radioShipper.isChecked()){
                                goToAddressFragment("Shipper");
                            }else{
                                goToCustomerAddressFragment("Customer");
                            }

                           // goToAddressFragment(selectedChoose);

                        }
                    });
                }
            });
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);

            return alertDialog;
        } else {
            return super.onCreateDialog(savedInstanceState);
        }
    }

    private void goToAddressFragment(String selectedChoose) {

        Log.e("ROLE ","role selected "+selectedChoose);
//
//        PersonalDetailDialogFragment newClientDialogFragment = PersonalDetailDialogFragment.newInstance(selectedChoose);
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        Fragment previous = getFragmentManager().findFragmentByTag("newEventDialog");
//        if (previous != null){
//            transaction.remove(previous);
//        }
//        transaction.add(newClientDialogFragment, "newEventDialog");
//        transaction.addToBackStack(null);
//        transaction.commit();
    }

    private void goToCustomerAddressFragment(String selectedChoose) {

        Log.e("ROLE ","role selected "+selectedChoose);
//
//        PersonalCustomerDetailDialogFragment newClientDialogFragment = PersonalCustomerDetailDialogFragment.newInstance(selectedChoose);
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        Fragment previous = getFragmentManager().findFragmentByTag("newEventDialog");
//        if (previous != null){
//            transaction.remove(previous);
//        }
//        transaction.add(newClientDialogFragment, "newEventDialog");
//        transaction.addToBackStack(null);
//        transaction.commit();
    }


}
