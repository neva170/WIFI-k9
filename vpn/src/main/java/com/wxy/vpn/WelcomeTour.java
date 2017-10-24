package com.wxy.vpn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


public class WelcomeTour extends AppCompatActivity {

    final private int PAGES_COUNT = 4;

    private ScrollingTogglableViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!shouldShowTour()) {
            startActivity(new Intent(this, MasterActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_welcome_tour);

        mViewPager = (ScrollingTogglableViewPager) findViewById(com.wxy.vpn.R.id.tour_page_container);
        mViewPager.setAdapter(new TourPagerAdapter(getSupportFragmentManager()));

        mTabLayout = (TabLayout) findViewById(com.wxy.vpn.R.id.tour_tabs_indicator);
        mTabLayout.setupWithViewPager(mViewPager, true);
        mTabLayout.setVisibility(View.GONE);
    }

    private boolean shouldShowTour() {
        // TODO move to SettingsStorage
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("basic_on_startup_show_tour", true);
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_LAYOUT = "layout_resource";

        static public PlaceholderFragment newInstance(int layout) {
            PlaceholderFragment fragment = new PlaceholderFragment();

            Bundle args = new Bundle();
            args.putInt(ARG_LAYOUT, layout);
            fragment.setArguments(args);

            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
            final int layout = getArguments().getInt(ARG_LAYOUT);
            final View view = inflater.inflate(layout, container, false);

            final View.OnClickListener onStopTour = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CheckBox dontShowView = (CheckBox) view.findViewById(com.wxy.vpn.R.id.tour_dont_show_at_startup);

                    // TODO move to SettingsStorage
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                    editor.putBoolean("basic_on_startup_show_tour", dontShowView != null && !dontShowView.isChecked());
                    editor.apply();

                    startActivity(new Intent(getActivity(), MasterActivity.class));
                    getActivity().finish();
                }
            };

            switch (layout) {
                case com.wxy.vpn.R.layout.fragment_tour_1:
                    setupPage1(view, onStopTour);
                    break;
                case com.wxy.vpn.R.layout.fragment_tour_4:
                    setupPage4(view, onStopTour);
                    break;
            }

            return view;
        }

        private void setupPage1(View view, View.OnClickListener onStopTour) {
            final Button skipBtn = (Button) view.findViewById(com.wxy.vpn.R.id.tour_btn_skip);
            skipBtn.setOnClickListener(onStopTour);

            final View startButton = view.findViewById(com.wxy.vpn.R.id.tour_btn_start);
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TabLayout dots = ((WelcomeTour) getActivity()).mTabLayout;
                    dots.setVisibility(View.VISIBLE);

                    final ScrollingTogglableViewPager pager = ((WelcomeTour) getActivity()).mViewPager;
                    pager.setPagingEnabled(true);
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);

                    startButton.setEnabled(false);
                }
            });
        }

        private void setupPage4(View view, View.OnClickListener onStopTour) {
            final Button finishBtn = (Button) view.findViewById(com.wxy.vpn.R.id.tour_btn_finish);
            finishBtn.setOnClickListener(onStopTour);

            view.findViewById(com.wxy.vpn.R.id.tour_btn_invite).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), InviteFriends.class));
                }
            });

            urlSetOnClick((TextView) view.findViewById(com.wxy.vpn.R.id.tour_text_faq), com.wxy.vpn.R.string.tour4_text2, new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    startActivity(new Intent(getActivity(), HelpAndFaq.class));
                }
            });

            urlSetOnClick((TextView) view.findViewById(com.wxy.vpn.R.id.tour_text_let_us_know),com.wxy.vpn.R.string.tour4_text4, new ClickableSpan() {
                @Override
                public void onClick(View widget) {

                }
            });

            ((CheckBox) view.findViewById(com.wxy.vpn.R.id.tour_dont_show_at_startup))
                    .setOnCheckedChangeListener(
                            new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    // TODO move to SettingsStorage
                                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                                    editor.putBoolean("basic_on_startup_show_tour", isChecked);
                                    editor.apply();
                                }
                            }
                    );
        }

        private void urlSetOnClick(TextView text, int stringRes, ClickableSpan clickable) {
            CharSequence sequence = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                sequence = Html.fromHtml(getString(stringRes), Html.FROM_HTML_MODE_LEGACY);
            } else {
                sequence = Html.fromHtml(getString(stringRes));
            }

            SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
            URLSpan[] urls = strBuilder.getSpans(0, strBuilder.length(), URLSpan.class);

            final int spanStart = strBuilder.getSpanStart(urls[0]);
            final int spanEnd = strBuilder.getSpanEnd(urls[0]);
            final int spanFlags = strBuilder.getSpanFlags(urls[0]);

            strBuilder.removeSpan(urls[0]);
            strBuilder.setSpan(clickable, spanStart, spanEnd, spanFlags);

            text.setText(strBuilder);
            text.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }


    static public class ScrollingTogglableViewPager extends ViewPager {

        private boolean enabled;


        public ScrollingTogglableViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
            enabled = false;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return enabled && super.onInterceptTouchEvent(ev);
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            return enabled && super.onTouchEvent(ev);
        }

        public void setPagingEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    private class TourPagerAdapter extends FragmentPagerAdapter {

        public TourPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            int layout;

            switch (position) {
                case 0:
                    layout = com.wxy.vpn.R.layout.fragment_tour_1;
                    break;
                case 1:
                    layout = com.wxy.vpn.R.layout.fragment_tour_2;
                    break;
                case 2:
                    layout = com.wxy.vpn.R.layout.fragment_tour_3;
                    break;
                case 3:
                    layout = com.wxy.vpn.R.layout.fragment_tour_4;
                    break;
                default:
                    throw new UnsupportedOperationException();
            }

            return PlaceholderFragment.newInstance(layout);
        }

        @Override
        public int getCount() {
            return PAGES_COUNT;
        }
    }
}
