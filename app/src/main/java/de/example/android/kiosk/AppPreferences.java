package de.example.android.kiosk;


import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.DisplayMetrics;
import android.widget.GridView;
import android.widget.Toast;

import java.io.IOException;

public class AppPreferences extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        }
    @SuppressLint("ValidFragment")
    public class MyPreferenceFragment extends PreferenceFragment {



        @Override
        public void onCreate(final Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);


            addPreferencesFromResource(R.xml.preferences);


            Preference ExitButton = findPreference("pref_Exit");

            ExitButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    PrefUtils.setKioskModeActive(false, getActivity());
                    getPackageManager().clearPackagePreferredActivities(getPackageName());
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Exit me", true);
                    startActivity(intent);
                    finish();
                    System.exit(0);
                    return true;
                }
            });

            Preference AllowedApps = findPreference("pref_AllowedApps");

            AllowedApps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), ChooseApplications.class);
                    startActivity(intent);
                    return true;
                }
            });

        }

    }
}

