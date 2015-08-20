package de.example.android.kiosk;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChooseApplications extends Activity implements OnItemClickListener{

        ListView apps;
        PackageManager packageManager;
    ArrayList <String> checkedValue = new ArrayList<>();
    Button bt1;
        @Override
        protected void onCreate(Bundle savedInstanceState) {



            super.onCreate(savedInstanceState);
            setContentView(R.layout.choose_apps);
            bt1 = (Button) findViewById(R.id.button1);
            apps = (ListView) findViewById(R.id.listView1);
            packageManager = getPackageManager();
            final List <PackageInfo> packageList = packageManager
                    .getInstalledPackages(PackageManager.GET_META_DATA); // all apps in the phone
            final List <PackageInfo> packageList1 = packageManager
                    .getInstalledPackages(0);

            try {
                packageList1.clear();
                for (int n = 0; n < packageList.size(); n++)
                {

                    PackageInfo PackInfo = packageList.get(n);
                    if ((PackInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && !PackInfo.packageName.equals("de.example.android.kiosk"))
                    //check weather it is system app or user installed app
                    {
                        try
                        {

                            packageList1.add(packageList.get(n)); // add in 2nd list if it is user installed app
                            Collections.sort(packageList1,new Comparator <PackageInfo>()
                                    // this will sort App list on the basis of app name
                            {
                                public int compare(PackageInfo o1,PackageInfo o2)
                                {
                                    return o1.applicationInfo.packageName
                                            .compareToIgnoreCase(o2.applicationInfo.packageName);
                                              // compare and return sorted packagelist.
                                }
                            });


                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ListAdapter Adapter = new ListAdapter(this,packageList1, packageManager);
            apps.setAdapter(Adapter);
            apps.setOnItemClickListener(this);
            bt1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    Set<String> set = new HashSet<>();

                    set.addAll(checkedValue);
                    editor.putStringSet("AllowedApps", set);
                    editor.apply();


                    ///////

                    Set<String> AllowedAps = prefs.getStringSet("AllowedApps", null);

                    Toast.makeText(ChooseApplications.this, "" + AllowedAps, Toast.LENGTH_LONG).show();
                }
            });

        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }


        @Override
        public void onItemClick(AdapterView arg0, View v, int position, long arg3) {
            CheckBox cb = (CheckBox) v.findViewById(R.id.checkBox1);
            TextView pn = (TextView) v.findViewById(R.id.textView0);
            //TextView tv = (TextView) v.findViewById(R.id.textView1);
            String Chk = pn.getText().toString();
            cb.performClick();
            if (cb.isChecked()) {
                checkedValue.add(Chk);}
                if (!cb.isChecked()) {
                    checkedValue.remove(Chk);
                }

        }

    }



