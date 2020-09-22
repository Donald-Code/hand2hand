package tshabalala.bongani.courierservice.presenters;


import tshabalala.bongani.courierservice.fragments.PricelistFragment;
import tshabalala.bongani.courierservice.interactors.PricelistInteractor;

public class PricelistPresenter {

    private PricelistInteractor mModel;
    private PricelistFragment mView;

    public PricelistPresenter(PricelistFragment pricelistFragment) {
        this.mModel = new PricelistInteractor(this);
        this.mView = pricelistFragment;
    }

    /**
     * Called from PricelistFragment
     * Sets adapter for @param.
     * @param
     */
    public void initialize() {
        mModel.setContext(mView.getContextFromFragment());
        mModel.onPrepareRecyclerView(mView.getRecyclerView());
    }

    public void detachView() {
        mView = null;
    }

}
