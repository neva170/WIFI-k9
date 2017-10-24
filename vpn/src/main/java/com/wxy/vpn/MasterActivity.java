package com.wxy.vpn;

import android.*;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.wxy.vpn.api.ApiK9Server;
import com.wxy.vpn.fragments.Local;
import com.wxy.vpn.fragments.Protect;
import com.wxy.vpn.utils.Connectivity;
import com.wxy.vpn.utils.SettingsStorage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VpnStatus;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.wxy.vpn.utils.SettingsStorage.Ssid.LIST_TYPE_HISTORY;
import static com.wxy.vpn.utils.SettingsStorage.Ssid.LIST_TYPE_TAG;


public class MasterActivity extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks,
        Protect.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener,DialogInterface.OnClickListener, DialogInterface.OnCancelListener {

    private final static int MINIMIZE_DELAY_SECONDS = 5;
    private final String TAG = getClass().getSimpleName();

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private NavigationView mNavView;
    private ViewPager mViewPager;
    private CountDownTimer mMinimizeTimer;
    private ApiK9Server.ApiInterface api;
    private ProgressBar mMinimizerProgressBar;
    private TextView mMinimizerCaption;
    private boolean mMinimizerShouldShow;
    private DrawerLayout mDrawer;
    protected OpenVPNService mService;
    private static final int MEDIA_SCREEN_PERM = 124;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            OpenVPNService.LocalBinder binder = (OpenVPNService.LocalBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.wxy.vpn.R.layout.activity_master);

        setupAds();

        Toolbar toolbar = (Toolbar) findViewById(com.wxy.vpn.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mDrawer = (DrawerLayout) findViewById(com.wxy.vpn.R.id.nav_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                com.wxy.vpn.R.string.open_nav_menu, com.wxy.vpn.R.string.close_nav_menu);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(com.wxy.vpn.R.id.main_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(com.wxy.vpn.R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mNavView = (NavigationView) findViewById(com.wxy.vpn.R.id.nav_view);
        mNavView.setNavigationItemSelectedListener(this);

        api = ApiK9Server.getApiInterface(getApplicationContext());

        navHeaderInit();

       /* if (ProfileManager.getInstance(this).getProfiles().isEmpty()) {
            startGettingVpnConfigsChain();
        }*/

        setupMinimizer();

    }

    private void setupAds() {
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-7746521259214064~8432708432");

        AdView mAdView = (AdView) findViewById(com.wxy.vpn.R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("02BE24DA98979D90C06B4B41AF84EDE1")
                .build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onPause() {
        minimizerStop();
        mMinimizerShouldShow = !VpnStatus.isVPNActive();
        super.onPause();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        minimizerStop();
        return super.dispatchTouchEvent(ev);
    }

    private void setupMinimizer() {
        final int delayMillisec = MINIMIZE_DELAY_SECONDS * 1000;

        mMinimizerProgressBar = (ProgressBar) findViewById(com.wxy.vpn.R.id.minimizer_progress_bar);
        mMinimizerProgressBar.setMax(delayMillisec);

        mMinimizerCaption = (TextView) findViewById(com.wxy.vpn.R.id.minimizer_progress_caption);

        mMinimizeTimer = new CountDownTimer(delayMillisec, (1000 / 60) * 3) {
            @Override
            public void onTick(long millisUntilFinished) {
                mMinimizerProgressBar.setProgress((int) millisUntilFinished);
                mMinimizerCaption.setText(getString(com.wxy.vpn.R.string.minimizer_caption, (millisUntilFinished / 1000) + 1));
            }

            @Override
            public void onFinish() {
                // minimize
                moveTaskToBack(true);
            }
        };
    }

    private void minimizerStop() {
        findViewById(com.wxy.vpn.R.id.minimizer).setVisibility(View.GONE);
        mMinimizeTimer.cancel();
    }

    private void minimizerStart() {
        findViewById(com.wxy.vpn.R.id.minimizer).setVisibility(View.VISIBLE);
        mMinimizeTimer.start();
    }

    private void navHeaderInit() {
        Call<ApiK9Server.UserCred> getCredentials = api.getUserCredentials(
                new ApiK9Server.BasicCred(
                        SettingsStorage.User.getToken(this),
                        SettingsStorage.User.getEmail(this)
                )
        );
        getCredentials.enqueue(new Callback<ApiK9Server.UserCred>() {
            @Override
            public void onResponse(Call<ApiK9Server.UserCred> call, Response<ApiK9Server.UserCred> response) {
                if (!response.isSuccessful() && response.errorBody() != null) {
                    ApiK9Server.ApiError error = ApiK9Server.parseError(response);
                    for (String errorMessage : error.getErrors().getMessages()) {
                        Log.e(TAG + ":getUserCredentials", errorMessage);
                    }
                }

                ApiK9Server.UserCred decodedResponse = response.body();
                if (decodedResponse != null) {
                    SettingsStorage.User.setCredentials(MasterActivity.this, decodedResponse.data);
                    navHeaderRefresh();
                }
            }

            @Override
            public void onFailure(Call<ApiK9Server.UserCred> call, Throwable t) {
                Log.e(TAG + ":getUserCredentials", "onFailure: ", t);
            }
        });

        navHeaderRefresh();
    }

    private void navHeaderRefresh() {
        View headerView = mNavView.getHeaderView(0);

        TextView username = (TextView) headerView.findViewById(com.wxy.vpn.R.id.nav_header_user_name);
        username.setText(SettingsStorage.User.getCredentials(this).getFullName());

        TextView city = (TextView) headerView.findViewById(com.wxy.vpn.R.id.nav_header_user_city);
        city.setText(SettingsStorage.User.getCredentials(this).getCity());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

/*    private void startGettingVpnConfigsChain() {
        Call<ApiK9Server.ListWrapper<ApiK9Server.VpnInfo>> vpnList = api.getVpnList(
                new ApiK9Server.BasicCred(
                        SettingsStorage.User.getToken(this),
                        SettingsStorage.User.getEmail(this)
                )
        );
        vpnList.enqueue(new Callback<ApiK9Server.ListWrapper<ApiK9Server.VpnInfo>>() {
            @Override
            public void onResponse(Call<ApiK9Server.ListWrapper<ApiK9Server.VpnInfo>> call,
                                   Response<ApiK9Server.ListWrapper<ApiK9Server.VpnInfo>> response) {
                if (!response.isSuccessful() && response.errorBody() != null) {
                    ApiK9Server.ApiError error = ApiK9Server.parseError(response);
                    for (String errorMessage : error.getErrors().getMessages()) {
                        Log.e(TAG + ":getVpnList", errorMessage);
                    }
                }

                ApiK9Server.ListWrapper<ApiK9Server.VpnInfo> decodedResponse = response.body();
                if (decodedResponse != null) {
                    SettingsStorage.Vpn.setId(MasterActivity.this, decodedResponse.getList().get(0).getIdentificator());
                    fetchConfig(MasterActivity.this);
                }
            }

            @Override
            public void onFailure(Call<ApiK9Server.ListWrapper<ApiK9Server.VpnInfo>> call, Throwable t) {
                Log.e(TAG + ":getVpnList", "onFailure: ", t);
            }
        });
    }*/

/*    private void fetchConfig(final Context context) {
        Call<ResponseBody> fetch = api.getVpnConfig(
                new ApiK9Server.VpnConfigCred(
                        SettingsStorage.User.getToken(context),
                        SettingsStorage.Vpn.getId(context),
                        SettingsStorage.User.getEmail(context),
                        SettingsStorage.Device.getId(context)
                )
        );
        fetch.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful() && response.errorBody() != null) {
                    ApiK9Server.ApiError error = ApiK9Server.parseError(response);
                    for (String errorMessage : error.getErrors().getMessages()) {
                        Log.e(TAG + ":getVpnConfig", errorMessage);
                    }
                    if (response.code() == 404)
                        createCertificateOnServer(context);
                }

                ResponseBody decodedResponse = response.body();
                if (decodedResponse != null) {
                    addVpnConfig(decodedResponse.charStream());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG + ":getVpnConfig", "onFailure: ", t);
            }
        });
    }*/

  /*  private void addVpnConfig(Reader reader) {
        ConfigParser cp = new ConfigParser();
        try {
            cp.parseConfig(reader);
            VpnProfile profile = cp.convertProfile();

            ProfileManager vpl = ProfileManager.getInstance(this);
            vpl.addProfile(profile);
            vpl.saveProfile(this, profile);
            vpl.saveProfileList(this);

            ProfileManager.setAlwaysOnVPN(this, profile.getUUIDString());
            SettingsStorage.Vpn.setCertVpnUuid(this, profile.getUUIDString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
/*

    private void createCertificateOnServer(final Context context) {
        Call<ApiK9Server.ApiResponse> create = api.createAndroidCertificate(
                new ApiK9Server.AndroidCreateCertificateCred(
                        SettingsStorage.User.getToken(context),
                        SettingsStorage.Device.getId(context),
                        SettingsStorage.User.getEmail(context),
                        UUID.randomUUID().toString().replaceAll("-", "")
                )
        );
        create.enqueue(new Callback<ApiK9Server.ApiResponse>() {
            @Override
            public void onResponse(Call<ApiK9Server.ApiResponse> call, Response<ApiK9Server.ApiResponse> response) {
                if (!response.isSuccessful() && response.errorBody() != null) {
                    ApiK9Server.ApiError error = ApiK9Server.parseError(response);
                    for (String errorMessage : error.getErrors().getMessages()) {
                        Log.e(TAG + ":createAndroidCertifica", errorMessage);
                    }
                }

                ApiK9Server.ApiResponse decodedResponse = response.body();
                if (decodedResponse != null) {
                    fetchConfig(context);
                }
            }

            @Override
            public void onFailure(Call<ApiK9Server.ApiResponse> call, Throwable t) {
                Log.e(TAG + ":createAndroidCertifica", "onFailure: ", t);
            }
        });
    }
*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(com.wxy.vpn.R.id.nav_drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.wxy.vpn.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.wxy.vpn.R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @AfterPermissionGranted(MEDIA_SCREEN_PERM)
    private void requestPermissions() {
        String[] perms = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            startVpn();
        }
        else {
            EasyPermissions.requestPermissions(this, "Permission Required", MEDIA_SCREEN_PERM, perms);
        }
        }
   @Override
    public void onProtectClicked(boolean start) {
        if (start) {
            //requestPermissions();
            startVpn();
        }else if (VpnStatus.isVPNActive()) {
                Intent intent = new Intent(this, OpenVPNService.class);
                intent.setAction(OpenVPNService.START_SERVICE);
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                showDisconnectDialog();
            }

            }


    @Override
    public void onVpnConnectedChanged(boolean connected) {
        if (!connected) return;

        // TODO move to SettingsStorage
        final boolean prefAutoMinimize = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("basic_on_connection_minimize", false);
        if (mMinimizerShouldShow && prefAutoMinimize) {
            minimizerStart();
        }
    }

    @Override
    public void onWifiDisconnect(boolean connected) {
         if(!connected) {
             Toast.makeText(this,"No Network!",Toast.LENGTH_LONG).show();
             Intent intent = new Intent(this, OpenVPNService.class);
             intent.setAction(OpenVPNService.START_SERVICE);
             bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
             ProfileManager.setConntectedVpnProfileDisconnected(this);
             if (mService != null && mService.getManagement() != null) {
                 mService.getManagement().stopVPN(false);
             }

        }
    }

    private void startVpn() {
      try {
          /***********************************************************************************************************
           * Attention!
           * You should prepare your own client.ovpn file in the assets folder.
           * *********************************************************************************************************
           */
          InputStream conf = getAssets().open("wifik9.ovpn");// your own file in /assets/client.ovpn
          InputStreamReader isr = new InputStreamReader(conf);
          BufferedReader br = new BufferedReader(isr);
          String config = "";
          String line;
          while (true) {
              line = br.readLine();
              if (line == null) break;
              config += line + "\n";
          }
          br.readLine();
          OpenVpnApi.startVpn(this, config, null, null);
      } catch (IOException | RemoteException e) {
          e.printStackTrace();
      }
  }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case com.wxy.vpn.R.id.nav_menu_logout:
                SettingsStorage.User.logoutDialog(this);
                break;
            case com.wxy.vpn.R.id.nav_menu_account:
                startActivity(new Intent(this, UserProfile.class));
                break;
            case com.wxy.vpn.R.id.nav_menu_invite:
                startActivity(new Intent(this, InviteFriends.class));
                break;
            case com.wxy.vpn.R.id.nav_menu_help_faq:
                startActivity(new Intent(this, HelpAndFaq.class));
                break;
            case com.wxy.vpn.R.id.nav_menu_history:
                final Intent intent = new Intent(this, SsidList.class);
                intent.putExtra(LIST_TYPE_TAG, LIST_TYPE_HISTORY);
                startActivity(intent);
                break;
            /*
            case R.id.nav_menu_safe_list:
                final Intent intentSafeList = new Intent(this, SsidList.class);
                intentSafeList.putExtra(LIST_TYPE_TAG, LIST_TYPE_SAFE_LIST);
                startActivity(intentSafeList);
                break;
             */
            case com.wxy.vpn.R.id.nav_menu_settings:
                startActivity(new Intent(this, Settings.class));
                break;
            case com.wxy.vpn.R.id.nav_menu_terms:
                final Intent intentTerms = new Intent(this, TermsOfService.class);
                startActivity(intentTerms);
                break;
            case com.wxy.vpn.R.id.nav_menu_privacy:
                final Intent intentPrivacy = new Intent(this, PrivacyPolicy.class);
                startActivity(intentPrivacy);
                break;
            case com.wxy.vpn.R.id.nav_menu_about:
                final Intent mapIntent = new Intent(this, MapsActivity.class);
                startActivity(mapIntent);
                break;
        }
        return false;
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {

    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == DialogInterface.BUTTON_POSITIVE) {
            ProfileManager.setConntectedVpnProfileDisconnected(this);
            if (mService != null && mService.getManagement() != null) {
                mService.getManagement().stopVPN(false);
            }
        }
      //  finish();
    }
    private void showDisconnectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(com.wxy.vpn.R.string.title_cancel);
        builder.setMessage(com.wxy.vpn.R.string.cancel_connection_query);
        builder.setNegativeButton(android.R.string.no, this);
        builder.setPositiveButton(android.R.string.yes, this);
        builder.setOnCancelListener(this);
        builder.show();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        startVpn();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private String[] mFragTitleDic = {"Protect", "Local"};

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return Protect.newInstance("");

                case 1:
                    return Local.newInstance("", "");
            }
            return null;
        }

        @Override
        public int getCount() {
//            return mFragTitleDic.length;
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragTitleDic[position];
        }
    }
}
