package de.example.android.kiosk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


import static android.app.Dialog.*;


public class MainActivity extends Activity {


    List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    private int count = 0;
    private long startMillis = 0;
    private WebView browser;
    public class AppDetail {
        CharSequence label;
        CharSequence name;
        Drawable icon;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }

    }

    public PackageManager PM;
    public List <AppDetail> AlowedApps;

    public ListView AlowedApplist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);



    if( getIntent().getBooleanExtra("Exit me", false)){
         finish();
         return; // add this to prevent from doing unnecessary stuffs

     }
       setContentView(R.layout.activity_main);
       getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
       getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
       preventStatusBarExpansion(getApplicationContext());
       PrefUtils.setKioskModeActive(true, getApplicationContext());

        loadApps();
        loadListView();
        addClickListener();
   }

    private void loadApps() {
        PM = getPackageManager();
        AlowedApps = new ArrayList<>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<String> myAps = prefs.getStringSet("AllowedApps", null);

        if(myAps !=null){
        for (String s : myAps) {
            AppDetail app = new AppDetail();


            try {
                ApplicationInfo ai = this.getPackageManager().getApplicationInfo(s, 0);
                app.label = PM.getApplicationLabel(ai);
                app.icon = PM.getApplicationIcon(ai);
                app.name = ai.packageName;
                AlowedApps.add(app);

            } catch (final PackageManager.NameNotFoundException e) {

            }

        }
    }}

    private void loadListView(){
        AlowedApplist = (ListView)findViewById(R.id.apps_list);

        ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(this,
                R.layout.show_allowed_apps, AlowedApps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.show_allowed_apps, null);
                }

                ImageView appIcon = (ImageView)convertView.findViewById(R.id.item_app_icon);
                appIcon.getLayoutParams().width = 60;
                appIcon.getLayoutParams().height = 60;
                appIcon.setImageDrawable(AlowedApps.get(position).icon);


                TextView appLabel = (TextView)convertView.findViewById(R.id.item_app_label);
                appLabel.setText(AlowedApps.get(position).label);


                return convertView;
            }
        };
        AlowedApplist.setAdapter(adapter);
    }

    private void addClickListener() {
        AlowedApplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                Intent i = PM.getLaunchIntentForPackage(AlowedApps.get(pos).name.toString());
                startActivity(i);

            }
        });
    }

 private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String WebViewURL = preferences.getString("pref_ChangeWebviewURL", "");
        browser = (WebView) findViewById(R.id.webview);
        browser.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        browser.getSettings().setAllowFileAccess(true);
        browser.getSettings().setAppCacheEnabled(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default

        loadApps();
        loadListView();
        addClickListener();

        if ( !isNetworkAvailable() ) { // loading offline
            browser.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        }

        browser.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }
        });
        browser.loadUrl(WebViewURL);

        if (!hasFocus) {
            // Close every kind of system dialog
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }

    }

    @Override
    public void onBackPressed() {
        // nothing to do here
        // … really
    }

  public static void preventStatusBarExpansion(Context context) {

        WindowManager manager = ((WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |

        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (50 * context.getResources()
                .getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        customViewGroup view = new customViewGroup(context);
        manager.addView(view, localLayoutParams);

    }

    public static class customViewGroup extends ViewGroup {

        public customViewGroup(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            Log.v("customViewGroup", "**********Intercepted");
            return true;
        }
    }


//detect any touch event in the screen (instead of an specific view)


        @Override

        public boolean onTouchEvent (MotionEvent event) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            final String DeffUnlockCode = preferences.getString("pref_UnlockCode", "7777");
            final String GetTouches = preferences.getString("pref_ClickAttempts", "5");
            final String DeffTimeToUnlock = preferences.getString("pref_TimeToUnlock","3");


            Integer TouchCount = Integer.parseInt(GetTouches);
            final Integer TouchTime = Integer.parseInt(DeffTimeToUnlock);

            int eventaction = event.getAction();
            if (eventaction == MotionEvent.ACTION_UP) {


                //get system current milliseconds
                long time = System.currentTimeMillis();


                //if it is the first time, or if it has been more than 3 seconds since the first tap ( so it is like a new try), we reset everything
                if (startMillis == 0 || (time - startMillis > (TouchTime * 1000))) {
                    startMillis = time;
                    count = 1;
                }
                //it is not the first, and it has been  less than 3 seconds since the first
                else { //  time-startMillis< PeriodToUnlock/1000
                    count++;

                }

                if (count == TouchCount) {
                    AlertDialog.Builder Builder = new AlertDialog.Builder(this);
                    Builder.setTitle("Unlock Device");
                    Builder.setMessage("Please enter Unlock Code:");
                    final EditText input = new EditText(this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);


                    if (DeffUnlockCode.equals("7777")){
                        startActivity(new Intent(MainActivity.this, AppPreferences.class));
                        Toast.makeText(getApplicationContext(), "Don't forget do define your Unlock code for future usage!", Toast.LENGTH_SHORT).show();

                    } else {

                        AlertDialog.Builder ok = Builder.setPositiveButton("OK", new OnClickListener() {


                            public void onClick(final DialogInterface dialog, int whichButton) {

                                String SetUnlockCode = input.getText().toString();

                                if (DeffUnlockCode.equals(SetUnlockCode)) {
                                    startActivity(new Intent(MainActivity.this, AppPreferences.class));
                                    input.getText().clear();

                                } else {

                                    Toast.makeText(getApplicationContext(), "Wrong Unlock Code!", Toast.LENGTH_SHORT).show();

                                }

                            }

                        });

                        final AlertDialog dlg = Builder.create();

                        dlg.setView(input);
                        dlg.show();


                        dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                                if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                                    onBackPressed();
                                    return true;
                                } else {

                                    Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                                    sendBroadcast(closeDialog);
                                }


                                return false;
                            }
                        });

               final Timer t = new Timer();
                        t.schedule(new TimerTask() {
                            public void run() {
                                dlg.dismiss(); // when the task active then close the dialog
                                t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                            }
                        }, 3000); // after 5 second (or 5000 miliseconds), the task will be active
                    }
                    return true;


                }

            }
            return false;
        }


}






