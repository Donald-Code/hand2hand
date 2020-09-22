package tshabalala.bongani.courierservice.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import tshabalala.bongani.courierservice.R;
import tshabalala.bongani.courierservice.helper.Common;
import tshabalala.bongani.courierservice.presenters.ProfileEditPresenter;


public class ProfileEditFragment extends Fragment {

    private ProfileEditPresenter mPresenter;
    private EditText mNameEdit;
    private EditText mPhoneEdit;
    private EditText mMailEdit;
    private EditText mDobEdit;
    private EditText mGenderEdit;

    private Button mAcceptButton;
    private ProgressDialog mProgressDialog;
    SharedPreferences sharedPreferences;
    String ref;

    public ProfileEditFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        ref = sharedPreferences.getString(Common.ROLE,"");
        return inflater.inflate(R.layout.fragment_profile_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mNameEdit = (EditText) view.findViewById(R.id.profileedit_name_edit_text);
        mPhoneEdit = (EditText) view.findViewById(R.id.profileedit_telephone_edit_text);
        mMailEdit = (EditText) view.findViewById(R.id.profileedit_mail_edit_text);

        mDobEdit = (EditText) view.findViewById(R.id.profileedit_dob_edit_text);
        mGenderEdit = (EditText) view.findViewById(R.id.profileedit_gender_edit_text);
        mAcceptButton = (Button) view.findViewById(R.id.profileedit_accept_button);


        mPresenter = new ProfileEditPresenter(this);
        mPresenter.initialize(ref);
    }

    public void setListeners() {
        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.setListenerForAccept(mNameEdit.getText().toString(),
                        mPhoneEdit.getText().toString(),
                        mMailEdit.getText().toString(),ref);
            }
        });

        mNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.checkIfEditTextValid(getString(R.string.profile_error_name), mNameEdit.getText().toString(), 1);
            }
        });
        mPhoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.checkIfEditTextValid(getString(R.string.profile_error_phone), mPhoneEdit.getText().toString(), 2);
            }
        });
        mMailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.checkIfEditTextValid(getString(R.string.profile_error_mail), mMailEdit.getText().toString(), 3);
            }
        });
    }

    /**
     * Method for producing toasts when event of a numberOfMessage type happened.
     * @param numberOfMessage: 1 = error in submission, 2 = successful submission
     */
    public void makeToast(int numberOfMessage) {
        String text;
        if(numberOfMessage == 1) {
            text = getString(R.string.profile_error_submit);
        } else {
            text = getString(R.string.profile_toast_update);
        }
        Toast toast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    public void setTexts(String name, String phone, String mail, String dob, String gender, String age) {
        mNameEdit.setText(name);
        mPhoneEdit.setText(phone);
        mMailEdit.setText(mail);
        mDobEdit.setText(dob);
        mGenderEdit.setText(gender);

    }

    public void setEditTextErrors(String errorMessage, int dataNumber) {
        if(dataNumber == 1) {
            mNameEdit.setError(errorMessage);
        } else if(dataNumber == 2) {
            mPhoneEdit.setError(errorMessage);
        } else if(dataNumber == 3) {
            mMailEdit.setError(errorMessage);
        }
    }

    /**
     * Prepares and shows ProgressDialog upon call (while the data is loading).
     */
    public void showProgressDialog() {
        if(mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(getString(R.string.progress_dialog_loading));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }
    }

    public void hideProgressDialog() {
        mProgressDialog.hide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
