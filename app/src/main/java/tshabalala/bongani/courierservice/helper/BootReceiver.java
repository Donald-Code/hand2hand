package tshabalala.bongani.courierservice.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    static final String ACTION1 = "android.intent.action.BOOT_COMPLETED";


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent main = new Intent();
        main.setClassName("tshabalala.bongani.courierservice", "tshabalala.bongani.courierservice.MainActivity");
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(main);
    }
}
