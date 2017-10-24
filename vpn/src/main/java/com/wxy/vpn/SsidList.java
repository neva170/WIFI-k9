package com.wxy.vpn;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wxy.vpn.utils.SettingsStorage;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.wxy.vpn.utils.SettingsStorage.Ssid.LIST_TYPE_HISTORY;
import static com.wxy.vpn.utils.SettingsStorage.Ssid.LIST_TYPE_SAFE_LIST;
import static com.wxy.vpn.utils.SettingsStorage.Ssid.LIST_TYPE_TAG;


public class SsidList extends AppCompatActivity {

    private AlertDialog.Builder mDialogBuilder;
    private String mListType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListType = getIntent().getStringExtra(LIST_TYPE_TAG);

        if (LIST_TYPE_HISTORY.equals(mListType)) {
            getSupportActionBar().setTitle("History");
        } else if (LIST_TYPE_SAFE_LIST.equals(mListType)) {
            getSupportActionBar().setTitle("Safe List");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final boolean hasItems = !SettingsStorage.Ssid.getList(
                getApplicationContext(), mListType
        ).isEmpty();

        if (hasItems) {
            setContentView(com.wxy.vpn.R.layout.activity_history);

            mDialogBuilder = new AlertDialog.Builder(this);

            setupListView(true);
        } else {
            setContentView(com.wxy.vpn.R.layout.list_empty);
        }
    }

    private void setupListView(boolean hasItems) {
        RecyclerView ssidList = (RecyclerView) findViewById(com.wxy.vpn.R.id.history_ssid_list);

        ssidList.setLayoutManager(new LinearLayoutManager(this));
        ssidList.setHasFixedSize(true);

        if (mListType.contentEquals(LIST_TYPE_HISTORY) && hasItems) {
            Snackbar.make(
                    findViewById(android.R.id.content),
                    "Click on an item to add hotspot to the Safe List",
                    Snackbar.LENGTH_LONG
            ).show();
        }

        ssidList.setAdapter(new Adapter(mListType));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mSsidName;
        final TextView mLastConnected;
        final Adapter mAdapter;

        public ViewHolder(View itemView, Adapter adapter) {
            super(itemView);

            mSsidName = (TextView) itemView.findViewById(com.wxy.vpn.R.id.ssid_list_name);
            mLastConnected = (TextView) itemView.findViewById(com.wxy.vpn.R.id.ssid_list_last_connected);
            mAdapter = adapter;

            if (LIST_TYPE_HISTORY.equals(mAdapter.getType())) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialogBuilder
                                .setMessage("Do you want to add '" + mSsidName.getText() + "' to the Safe List?")
                                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SettingsStorage.Ssid.Item item =
                                                SettingsStorage.Ssid.getList(
                                                        getApplicationContext(),
                                                        mAdapter.getType()
                                                ).get(getAdapterPosition());

                                        SettingsStorage.Ssid.add(
                                                getApplicationContext(),
                                                SettingsStorage.Ssid.LIST_TYPE_SAFE_LIST,
                                                item
                                        );
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                });
            }

            itemView.findViewById(com.wxy.vpn.R.id.ssid_list_item_delete_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogBuilder
                            .setMessage("Do you want to delete '" + mSsidName.getText() + "' from the list?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SettingsStorage.Ssid.Item item =
                                            SettingsStorage.Ssid.getList(
                                                    getApplicationContext(),
                                                    mAdapter.getType()
                                            ).get(getAdapterPosition());

                                    SettingsStorage.Ssid.remove(
                                            getApplicationContext(),
                                            mAdapter.getType(),
                                            item.getSsidName()
                                    );

                                    mAdapter.notifyItemRemoved(getAdapterPosition());
                                }
                            })
                            .setNegativeButton("Keep", null)
                            .show();
                }
            });
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        final private String mType;

        private Adapter(String listType) {
            this.mType = listType;
        }

        public String getType() {
            return mType;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(com.wxy.vpn.R.layout.ssid_list_item, parent, false);

            return new ViewHolder(v, this);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final ArrayList<SettingsStorage.Ssid.Item> list =
                    SettingsStorage.Ssid.getList(
                            getApplicationContext(), mType
                    );

            holder.mSsidName.setText(list.get(position).getSsidName());

            DateFormat f = DateFormat.getDateTimeInstance(
                    DateFormat.SHORT,
                    DateFormat.SHORT,
                    Locale.getDefault()
            );
            holder.mLastConnected.setText(f.format(list.get(position).getLastConnected()));
        }

        @Override
        public int getItemCount() {
            return SettingsStorage.Ssid.getList(
                    getApplicationContext(), mType
            ).size();
        }
    }
}
