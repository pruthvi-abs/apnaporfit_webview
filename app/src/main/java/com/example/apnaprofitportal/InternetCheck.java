package com.example.apnaprofitportal;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.view.View;
import android.widget.Button;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class InternetCheck extends BroadcastReceiver {
    private SwipeRefreshLayout swipeContainer;
    private Button tryagain;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (Global.isNetworkAvailable(context)) {
            Dialog dialog1 = new Dialog(context, R.style.df_dialog);
            dialog1.setContentView(R.layout.internetdialog);
            dialog1.setCancelable(true);
            dialog1.setCanceledOnTouchOutside(true);
            dialog1.show();
            dialog1.findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent1 = new Intent();
                    intent1.setClassName(context.getPackageName(), MainActivity.class.getName());
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent1);
//                    dialog1.dismiss();
//                    onReceive(context, intent);
                }
            });
        }
    }

   public static boolean checkInternetConnection(MainActivity context) {
        ConnectivityManager con_manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (con_manager.getActiveNetworkInfo() != null
                && con_manager.getActiveNetworkInfo().isAvailable()
                && con_manager.getActiveNetworkInfo().isConnected());
    }
}





