package tshabalala.bongani.courierservice.adapters;



import java.util.List;

import tshabalala.bongani.courierservice.model.PriceItem;


public class BasePriceAdapterByGenre extends BasePriceAdapter {

    public BasePriceAdapterByGenre(List<PriceItem> itemList) {
        super(itemList);
    }


    public boolean onPlaceSubheaderBetweenItems(int itemPosition, int nextItemPosition) {
        final PriceItem priceItem1 = priceList.get(itemPosition);
        final PriceItem priceItem2 = priceList.get(nextItemPosition);

        return !priceItem1.getGenre().equals(priceItem2.getGenre());
    }


    public void onBindItemViewHolder(final PriceItemViewHolder holder, final int position) {
        final PriceItem priceItem = priceList.get(position);

        holder.mDescription.setText(priceItem.getDescription());
        holder.mPrice.setText(priceItem.getPrice());

    }


    public void onBindSubheaderViewHolder(SubheaderViewHolder subheaderViewHolder, int nextItemPosition) {
        final PriceItem nextPriceItem = priceList.get(nextItemPosition);
        String genre = String.valueOf(nextPriceItem.getGenre());
        subheaderViewHolder.subheaderText.setText(genre);
    }
}