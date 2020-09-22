package tshabalala.bongani.courierservice.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tshabalala.bongani.courierservice.R;
import tshabalala.bongani.courierservice.helper.Common;
import tshabalala.bongani.courierservice.presenters.ProfilePresenter;


public class ProfileFragment extends Fragment {

    private TextView mName;
    private TextView mPhone;
    private TextView mMail;
    private TextView mDob;
    private TextView mGender;
    private TextView mAge;
    private ProgressDialog mProgressDialog;
    private ProfilePresenter mPresenter;
    SharedPreferences sharedPreferences;
    String ref;

    public ProfileFragment() {
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mName = (TextView) view.findViewById(R.id.name_text_view);
        mPhone = (TextView) view.findViewById(R.id.telephone_text_view);
        mMail = (TextView) view.findViewById(R.id.mail_text_view);

        mDob = (TextView) view.findViewById(R.id.dob_text_view);
        mGender = (TextView) view.findViewById(R.id.gender_text_view);
        mAge = (TextView) view.findViewById(R.id.age_text_view);

        mPresenter = new ProfilePresenter(this);
        mPresenter.onUserDetailsRequired(ref);
    }

    public void setTexts(String name, String phone, String mail, String dob, String gender, String age) {
        mName.setText(name);
        mPhone.setText(phone);
        mMail.setText(mail);

        mDob.setText(dob);
        mGender.setText(gender);
        mAge.setText(age);
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
