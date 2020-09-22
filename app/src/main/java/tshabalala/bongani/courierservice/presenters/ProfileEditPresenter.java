package tshabalala.bongani.courierservice.presenters;


import tshabalala.bongani.courierservice.fragments.ProfileEditFragment;
import tshabalala.bongani.courierservice.interactors.ProfileEditInteractor;
import tshabalala.bongani.courierservice.model.User;

public class ProfileEditPresenter {

    private ProfileEditFragment mView;
    private ProfileEditInteractor mModel;



    public ProfileEditPresenter(ProfileEditFragment fragment) {
        this.mModel = new ProfileEditInteractor(this);
        this.mView = fragment;
    }

    public void initialize(String ref) {
        mView.showProgressDialog();
        getUserData(ref);
        mView.setListeners();
    }

    /**
     * Called by fragment instantiation {ProfileEditFragment}
     * Sends a request for user data from db based on his UID
     */
    private void getUserData(String ref) {
        mModel.getUserDataFromDb(ref);
    }


    /**
     * Receives call from the model when User data is acquired from db
     * Makes a call to the method that is responsible for showing user data
     * in the text fields for the user.
     */
    public void onReceivedUserDataFromDb(User user) {
        onShowUserDataInViews(user);
    }

    /**
     * Receives a call from onReceivedUserDataFromDb
     * Sets text fields with user data and starts edittexts
     * listeners for errors.
     */
    private void onShowUserDataInViews(User user) {
        mView.setTexts(user.getName(), user.getPhone(), user.getEmail(), user.getDateofbirth(), user.getGender(), user.getAge());
        mView.hideProgressDialog();
    }


    /**
     * Sets a lstener for the accept button. Upon clicking
     * it checks whether all text fields are correct and sends
     * the user data to updateUserDb method in interactor.
     */
    public void setListenerForAccept(String name, String phone, String mail,String ref) {
        if (phone.length() < 9 ||
                        name.length() <= 1 ||
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(
                                mail).matches())  {
            sendToastRequestToView(1);
        } else {
            mModel.updateUserDb(ref);
        }
    }

    /**
     * Called from this presenter while setting text fields with user data.
     * Starts listeners for edittexts and sets error msgs.
     * @param  dataNumber: 1 = mNameEdit, 2 = mPhoneEdit, 3 = mMailEdit
     */
    public void checkIfEditTextValid(String errorMessage, String text, int dataNumber) {
        if(dataNumber == 1) {
            if(text.length() <= 1) {
                mView.setEditTextErrors(errorMessage, dataNumber);
            } else {
                // Send new to db
                mModel.setUserDetail(dataNumber, text);
            }
        } else if(dataNumber == 2) {
            if(text.length() < 9) {
                mView.setEditTextErrors(errorMessage, dataNumber);
            } else {
                // Send new to db
                mModel.setUserDetail(dataNumber, text);
            }
        } else if(dataNumber == 3) {
            if(!ProfileEditInteractor.isValidEmailAddress(text)) {
                mView.setEditTextErrors(errorMessage, dataNumber);
            } else {
                // Send new to db
                mModel.setUserDetail(dataNumber, text);
            }
        }
    }

    public void sendToastRequestToView(int dataNumber) {
        mView.makeToast(dataNumber);
    }

    public void hideProgressDialog() {
        mView.hideProgressDialog();
    }

    public void detachView() {
        mView = null;
    }


}
