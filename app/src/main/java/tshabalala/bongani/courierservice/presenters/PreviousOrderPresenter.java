package tshabalala.bongani.courierservice.presenters;


import tshabalala.bongani.courierservice.fragments.PreviousOrdersFragment;
import tshabalala.bongani.courierservice.interactors.PreviousOrderInteractor;

public class PreviousOrderPresenter {

    private PreviousOrdersFragment mView;
    private PreviousOrderInteractor mInteractor;

    public PreviousOrderPresenter(PreviousOrdersFragment fragment) {
        this.mView = fragment;
        this.mInteractor = new PreviousOrderInteractor(this);
    }

    public void initialize() {
        mInteractor.fetchPreviousOrdersData();
    }

    public void sendContext() {
        mInteractor.setInteractorContext(mView.getFragmentContext());
    }

    public void onAdapterReady() {
        mView.setListViewWithAdapter(mInteractor.sendOrdersAdapter());
    }

    public void detachView() {
        mView = null;
    }

}
