package tshabalala.bongani.courierservice.interactors;

import android.content.Context;


import java.util.ArrayList;

import tshabalala.bongani.courierservice.adapters.OrdersAdapter;
import tshabalala.bongani.courierservice.helper.FirebaseOpsHelper;
import tshabalala.bongani.courierservice.model.OrderReceived;
import tshabalala.bongani.courierservice.presenters.PreviousOrderPresenter;

public class PreviousOrderInteractor {

    private PreviousOrderPresenter mPresenter;
    private Context context;
    private OrdersAdapter mOrdersAdapter;

    public PreviousOrderInteractor(PreviousOrderPresenter presenter) {
        this.mPresenter = presenter;
    }

    public void fetchPreviousOrdersData() {
        FirebaseOpsHelper fbHelper = new FirebaseOpsHelper();
        fbHelper.onPreviousOrdersCall(this);
    }

    public void onReceivedPreviousOrdersData(ArrayList<OrderReceived> orderList) {
        mPresenter.sendContext();
        mOrdersAdapter = new OrdersAdapter(orderList, context);
        mPresenter.onAdapterReady();
    }

    public void setInteractorContext(Context context) {
        this.context = context;
    }

    public OrdersAdapter sendOrdersAdapter() {
        return mOrdersAdapter;
    }

}
