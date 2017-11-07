package ch.ethz.vs.se.team7.carbonfootprintmonitor;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage.DbHandler;

import static ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage.Contract.ActivityRecordedEntry.TABLE_NAME;
import static ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage.SQLQueries.CREATE_TABLE_QUERY;

/**
 * Created by Prashanth on 11/6/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_visualizer);
        Preference button = findPreference("dropTable");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DbHandler dbHandler = new DbHandler(getActivity());
                SQLiteDatabase sqLiteDatabase = dbHandler.getWritableDatabase();
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                Toast.makeText(getActivity(), "Dropped table!", Toast.LENGTH_SHORT).show();
                sqLiteDatabase.execSQL(CREATE_TABLE_QUERY);
                return true;
            }
        });

    }
}
