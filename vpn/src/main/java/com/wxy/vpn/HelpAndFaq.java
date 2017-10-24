package com.wxy.vpn;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HelpAndFaq extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.wxy.vpn.R.layout.activity_help_and_faq);

        List<FaqItem> list = new ArrayList<>();
        List<String> faq1 = Arrays.asList(getResources().getStringArray(com.wxy.vpn.R.array.faq1));
        List<String> faq2 = Arrays.asList(getResources().getStringArray(com.wxy.vpn.R.array.faq2));
        List<String> faq3 = Arrays.asList(getResources().getStringArray(com.wxy.vpn.R.array.faq3));
        List<String> faq4 = Arrays.asList(getResources().getStringArray(com.wxy.vpn.R.array.faq4));
        List<String> faq5 = Arrays.asList(getResources().getStringArray(com.wxy.vpn.R.array.faq5));
        list.add(new FaqItem(faq1.get(0), faq1.get(1)));
        list.add(new FaqItem(faq2.get(0), faq2.get(1)));
        list.add(new FaqItem(faq3.get(0), faq3.get(1)));
        list.add(new FaqItem(faq4.get(0), faq4.get(1)));
        list.add(new FaqItem(faq5.get(0), faq5.get(1)));

        ExpandableFaqListAdapter adapter = new ExpandableFaqListAdapter(list, this);

        ExpandableListView faqView = (ExpandableListView) findViewById(com.wxy.vpn.R.id.faq_expandable_list_view);
        faqView.setGroupIndicator(null);
        faqView.setAdapter(adapter);
    }

    private class FaqItem {
        private String header;
        private String content;

        public FaqItem(String header, String content) {

            this.header = header;
            this.content = content;
        }

        public String getHeader() {
            return header;
        }

        public String getContent() {
            return content;
        }
    }

    private class ExpandableFaqListAdapter extends BaseExpandableListAdapter {

        private final List<FaqItem> mFaqList;
        private final Context mContext;
        private final LayoutInflater mInflater;

        private ExpandableFaqListAdapter(List<FaqItem> faqList, Context context) {
            this.mFaqList = faqList;
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getGroupCount() {
            return mFaqList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public FaqItem getGroup(int groupPosition) {
            return mFaqList.get(groupPosition);
        }

        @Override
        public String getChild(int groupPosition, int childPosition) {
            return mFaqList.get(groupPosition).getContent();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View resultView = convertView;
            ViewHolder holder;

            if (convertView == null) {
                resultView = mInflater.inflate(com.wxy.vpn.R.layout.faq_header, null);

                holder = new ViewHolder();
                holder.textLabel = (TextView) resultView.findViewById(com.wxy.vpn.R.id.faq_header);
                holder.img = (ImageView) resultView.findViewById(com.wxy.vpn.R.id.faq_indicator_img);
                resultView.setTag(holder);
            } else {
                holder = (ViewHolder) resultView.getTag();
            }

            final FaqItem item = getGroup(groupPosition);

            holder.textLabel.setText(item.getHeader());
            if (isExpanded) {
                holder.img.setImageResource(com.wxy.vpn.R.drawable.minus);
            } else {
                holder.img.setImageResource(com.wxy.vpn.R.drawable.plus);
            }

            return resultView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View resultView = convertView;
            ViewHolder holder;

            if (convertView == null) {
                resultView = mInflater.inflate(com.wxy.vpn.R.layout.faq_content, null);

                holder = new ViewHolder();
                holder.textLabel = (TextView) resultView.findViewById(com.wxy.vpn.R.id.faq_content_body);

                resultView.setTag(holder);
            } else {
                holder = (ViewHolder) resultView.getTag();
            }

            final String item = getChild(groupPosition, childPosition);

            holder.textLabel.setText(item);

            return resultView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        private final class ViewHolder {
            ImageView img;
            TextView textLabel;
        }
    }
}
