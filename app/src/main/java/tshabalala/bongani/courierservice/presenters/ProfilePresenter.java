package tshabalala.bongani.courierservice.presenters;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import tshabalala.bongani.courierservice.fragments.ProfileFragment;
import tshabalala.bongani.courierservice.interactors.ProfileInteractor;
import tshabalala.bongani.courierservice.model.User;

public class ProfilePresenter {

    private ProfileInteractor mModel;
    private ProfileFragment mView;
    SharedPreferences sharedPreferences;

    public ProfilePresenter(ProfileFragment view) {
        this.mModel = new ProfileInteractor(this);
        this.mView = view;

    }

    /**
     * Called by fragment instantiation {ProfileFragment}
     * Sends a request for user data from db based on his UID
     * @param ref
     */
    public void onUserDetailsRequired(String ref) {
        mView.showProgressDialog();
        mModel.getUserDataFromDb(ref);
    }

    // RETURNING FROM MODEL

    /**
     * Receives call from the model when User data is acquired from db
     */
    public void onReceivedUserDataFromDb(User user) {
        showUserDataInViews(user);
    }

    /**
     * Sends user details to the view's textfields.
     * @param user
     */
    private void showUserDataInViews(User user) {
        mView.setTexts(user.getName(), user.getPhone(), user.getEmail(), user.getDateofbirth(), user.getGender(), user.getAge());
        hideProgressDialog();
    }

    private void hideProgressDialog() {
        mView.hideProgressDialog();
    }

    public void detachView() {
        mView = null;
    }


}
