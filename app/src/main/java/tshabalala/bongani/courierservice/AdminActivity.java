package tshabalala.bongani.courierservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import tshabalala.bongani.courierservice.adapters.DashboardAdapter;
import tshabalala.bongani.courierservice.model.Dashboard;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        GridView gridView = (GridView)findViewById(R.id.gridview);
        final DashboardAdapter dashboardAdapter = new DashboardAdapter(this, dashboards);

        gridView.setAdapter(dashboardAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Dashboard item = dashboards[i];
                switch(item.getName()){

                    case R.string.customers:
                        startActivity(new Intent(AdminActivity.this, ReportCustomerActivity.class));
                        break;
                    case R.string.shipper:
                        startActivity(new Intent(AdminActivity.this, ReportShipperActivity.class));
                        break;
                    case R.string.reports:
                        startActivity(new Intent(AdminActivity.this, ReportParcelActivity.class));
                        break;
                }
            }
        });

    }

    private Dashboard[] dashboards = {
            new Dashboard(R.string.customers, R.drawable.man),
            new Dashboard(R.string.shipper, R.drawable.man),
            new Dashboard(R.string.reports, R.drawable.man)
    };

}
