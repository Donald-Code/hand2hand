package tshabalala.bongani.courierservice.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import tshabalala.bongani.courierservice.R;
import tshabalala.bongani.courierservice.model.Dashboard;

public class DashboardAdapter extends BaseAdapter {

    private final Context mContext;
    private final Dashboard[] dashboards;

    // Your "view holder" that holds references to each subview
    private class ViewHolder {
        private final TextView nameTextView;
        private final TextView authorTextView;
        private final ImageView imageViewCoverArt;
        private final ImageView imageViewFavorite;

        public ViewHolder(TextView nameTextView, TextView authorTextView, ImageView imageViewCoverArt, ImageView imageViewFavorite) {
            this.nameTextView = nameTextView;
            this.authorTextView = authorTextView;
            this.imageViewCoverArt = imageViewCoverArt;
            this.imageViewFavorite = imageViewFavorite;
        }
    }
    // 1
    public DashboardAdapter(Context context, Dashboard [] dashboards) {
        this.mContext = context;
        this.dashboards = dashboards;
    }

    // 2
    @Override
    public int getCount() {
        return dashboards.length;
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }


    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1
        final Dashboard dashboard = dashboards[position];

        // 2
        // view holder pattern
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.linearlayout_dashboard, null);

            final ImageView imageViewCoverArt = (ImageView)convertView.findViewById(R.id.imageview_cover_art);
            final TextView nameTextView = (TextView)convertView.findViewById(R.id.textview_book_name);
            final TextView authorTextView = (TextView)convertView.findViewById(R.id.textview_book_author);
            final ImageView imageViewFavorite = (ImageView)convertView.findViewById(R.id.imageview_favorite);

            final ViewHolder viewHolder = new ViewHolder(nameTextView, authorTextView, imageViewCoverArt, imageViewFavorite);
            convertView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder)convertView.getTag();
        // viewHolder.imageViewCoverArt.setImageResource(dashboard.getImageResource());
        Picasso.with(mContext).load(dashboard.getImageResource()).into(viewHolder.imageViewCoverArt);
        viewHolder.nameTextView.setText(mContext.getString(dashboard.getName()));
        // viewHolder.authorTextView.setText(mContext.getString(dashboard.getAuthor()));
       // viewHolder.imageViewFavorite.setImageResource(dashboard.getIsFavorite() ? R.drawable.star_enabled : R.drawable.star_disabled);

        return convertView;
    }

}

