package com.wxy.vpn.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.wxy.vpn.utils.Connectivity;
import com.wxy.vpn.utils.SettingsStorage;

import de.blinkt.openvpn.R;
import de.blinkt.openvpn.core.VpnStatus;


public class Protect extends Fragment implements VpnStatus.StateListener {

    public static final String ARG_TITLE = "tab_title";

//    private final String TAG = getClass().getSimpleName();

    // TODO: Rename and change types of parameters
    private String mTabTitle;

    private OnFragmentInteractionListener mListener;
    private ToggleButton mBtnDoProtect;
    private ProgressDialog mProgress;

    public Protect() {
        // Required empty public constructor
    }


    public static Protect newInstance(String tabTitle) {
        Protect fragment = new Protect();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, tabTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTabTitle = getArguments().getString(ARG_TITLE);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(com.wxy.vpn.R.layout.fragment_protect, container, false);

        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Your connection is being protected…");

        setupOnClickListeners(inflater, view);

        return view;
    }

    private void setupOnClickListeners(final LayoutInflater inflater, final View view) {
        mBtnDoProtect = (ToggleButton) view.findViewById(com.wxy.vpn.R.id.btn_do_protect);
        mBtnDoProtect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (VpnStatus.isVPNActive() || Connectivity.isConnectedWifi(getActivity())) {

                mListener.onProtectClicked(mBtnDoProtect.isChecked());
                setProtectButtonChecked(VpnStatus.isVPNActive());

                if (!VpnStatus.isVPNActive())
                    mProgress.show();
             } else {
                    final ViewGroup cont = (ViewGroup) view;
                    cont.removeAllViews();
                    final View noWifiView = inflater.inflate(com.wxy.vpn.R.layout.fragment_no_wifi, cont);

                    noWifiView.findViewById(com.wxy.vpn.R.id.btn_no_wifi_back).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final ViewGroup cont = (ViewGroup) view;
                            cont.removeAllViews();
                            final View protectView = inflater.inflate(com.wxy.vpn.R.layout.fragment_protect, cont);
                            setupOnClickListeners(inflater, protectView);
                        }
                    });
                }
            }
        });
    }

    private void setProtectButtonChecked(boolean checked) {
        mBtnDoProtect.setChecked(checked);
        if (checked)
            mBtnDoProtect.setTextSize(22);
        else mBtnDoProtect.setTextSize(32);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChangePasswordDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        VpnStatus.removeStateListener(this);
        super.onPause();
    }



    @Override
    public void onResume() {
        super.onResume();
        VpnStatus.addStateListener(this);
    }


    @Override
    public void updateState(final String state, String logmessage, int localizedResId, final VpnStatus.ConnectionStatus level) {
        Log.i("VpnState", state + " " + logmessage + " " + level.toString());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (level == VpnStatus.ConnectionStatus.LEVEL_CONNECTED) {
                    setProtectButtonChecked(true);
                    mListener.onVpnConnectedChanged(true);

                    if (mProgress.isShowing())
                        mProgress.dismiss();

                    if (Connectivity.isConnectedWifi(getActivity())) {
                        NotificationManagerCompat.from(getActivity()).cancelAll();
                        SettingsStorage.Ssid.update(
                                getActivity(),
                                SettingsStorage.Ssid.LIST_TYPE_HISTORY,
                                Connectivity.getNetworkName(getActivity())
                        );
                    }
                } else if(level == VpnStatus.ConnectionStatus.LEVEL_NONETWORK && state.equals("NONETWORK")) {
                    setProtectButtonChecked(false);
                    mListener.onVpnConnectedChanged(false);
                    mListener.onWifiDisconnect(false);
                    if (mProgress.isShowing() && state.contains("PERMISSION"))
                        mProgress.dismiss();
                }
            }
        });

    }

    public interface OnFragmentInteractionListener {
        void onProtectClicked(boolean start);

        void onVpnConnectedChanged(boolean connected);

        void  onWifiDisconnect(boolean connected);
    }
}
