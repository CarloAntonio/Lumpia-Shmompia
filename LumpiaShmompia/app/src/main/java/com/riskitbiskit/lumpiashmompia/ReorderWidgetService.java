package com.riskitbiskit.lumpiashmompia;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;


public class ReorderWidgetService extends IntentService {
    //Actions
    public static final String ACTION_NO_PREVIOUS_ORDER_TOAST = "com.riskitbiskit.lumpiashmompia.action.noPreviousOrder";
    public static final String ACTION_UPDATE_BASKET = "com.riskitbiskit.lumpiashmompia.action.updateBasket";

    public ReorderWidgetService() {
        super("ReorderWidgetService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final  String action = intent.getAction();
            if (ACTION_NO_PREVIOUS_ORDER_TOAST.equals(action)) {
                handleActionToast();
            }
            if (ACTION_UPDATE_BASKET.equals(action)){
                handleActionBasket();
            }
        }

    }

    private void handleActionToast() {
        //TODO: open app at OrderActivity
        Toast.makeText(getBaseContext(), "No Previous Order", Toast.LENGTH_SHORT).show();
    }

    private void handleActionBasket() {
        //Update shared preference with old order
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String previousOrder = sharedPreferences.getString(OrderActivity.PREVIOUS_ORDER, MenuActivity.EMPTY);
        editor.putString(MenuActivity.CHECKOUT_LIST, previousOrder);
        editor.apply();
    }

    public static void startActionToast(Context context) {
        Intent intent = new Intent(context, ReorderWidgetService.class);
        intent.setAction(ACTION_NO_PREVIOUS_ORDER_TOAST);
        context.startService(intent);
    }
}
