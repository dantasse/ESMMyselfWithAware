package com.aware.plugin.esm_myself_with_aware;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.ESM;
import com.aware.providers.ESM_Provider.ESM_Data;
import com.aware.utils.Aware_Sensor;

public class Plugin extends Aware_Sensor {

    private ESMObserver observer = null;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ESM_MYSELF", "onCreate");
        Aware.setSetting(getContentResolver(), Aware_Preferences.STATUS_ESM, true);
        observer = new ESMObserver(new Handler());
        getContentResolver().registerContentObserver(ESM_Data.CONTENT_URI, true, observer);
        sendBroadcast(new Intent(Aware.ACTION_AWARE_REFRESH));
        
        String esmStringLikert = "[{'esm':{" +
                "'esm_type':" + ESM.TYPE_ESM_LIKERT + "," +
                "'esm_title':'How's your mood?'," +
                "'esm_instructions':'How is your mood now?'," +
                "'esm_submit':'OK'," +
                "'esm_likert_max':5," +
                "'esm_likert_max_label':'Great'," +
                "'esm_likert_min_label':'Bad'," +
                "'esm_likert_step':1," +
                "'esm_expiration_threashold':60," +
                "'esm_trigger':'ESMMyselfWithAware'}}]";
        String esmStringText = "[{'esm':{" +
                "'esm_type':" + ESM.TYPE_ESM_TEXT + "," +
                "'esm_title':'How is your mood?'," +
                "'esm_instructions':'How is your mood now?'," +
                "'esm_submit':'OK'," +
                "'esm_expiration_threashold':60," +
                "'esm_trigger':'ESMMyselfWithAware'}}]";
        String esmStringRadio = "[{'esm':{" + 
                "'esm_type':2," +
                "'esm_title':'ESM Radio'," +
                "'esm_instructions':'The user can only choose one option'," +
                "'esm_radios':['Option one','Option two','Other']," +
                "'esm_submit':'Next'," +
                "'esm_expiration_threashold':30," +
                "'esm_trigger':'esm trigger example'}}]";
        String esmQuick = "[{'esm': {" +
                "'esm_type': 5," +
                "'esm_title': 'How is your mood?'," +
                "'esm_instructions': 'How is your mood?'," +
                "'esm_quick_answers': ['1','2','3','4','5','6','7','8','9','10']," +
                "'esm_expiration_threashold': 60," +
                "'esm_trigger': 'ESMMyselfWithAware'" +
                "}}]";

        // Queue the ESM to be displayed when possible
        Intent esm = new Intent(ESM.ACTION_AWARE_QUEUE_ESM);
        esm.putExtra(ESM.EXTRA_ESM, esmQuick);
//        sendBroadcast(esm);
        
        Log.d("ESM_MYSELF", "setting alarm now");
        pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, esm, 0);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // ELAPSED_REALTIME (not _WAKEUP)
        // fire first at 1 second, then every hour
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 1000, 1000 * 60 * 60,
                pendingIntent);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ESM_MYSELF", "onDestroy");
        Aware.setSetting(getContentResolver(), Aware_Preferences.STATUS_ESM, false);
        getContentResolver().unregisterContentObserver(observer);

        alarmManager.cancel(pendingIntent);
        sendBroadcast(new Intent(Aware.ACTION_AWARE_REFRESH));
    }
    
    private class ESMObserver extends ContentObserver {
        public ESMObserver(Handler handler) {
            super(handler);
        }
        
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
        }
    }
}
