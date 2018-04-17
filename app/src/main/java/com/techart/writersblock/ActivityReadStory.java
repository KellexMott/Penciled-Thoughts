package com.techart.writersblock;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.techart.writersblock.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ActivityReadStory extends AppCompatActivity {

    private List<String> contents;
    private List<String> pageNumbers;
    private int pageCount;
    private Spinner pages;
    private String postUrl;
    private int lastAccessedPage;
    private int setPage;
    private SharedPreferences mPref;
    private SharedPreferences.Editor editor;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        contents = getIntent().getStringArrayListExtra(Constants.POST_CONTENT);
        postUrl = getIntent().getStringExtra(Constants.POST_KEY);
        pageCount = contents.size();
        mPref = getSharedPreferences(String.format("%s",getString(R.string.app_name)),MODE_PRIVATE);
        setPage = mPref.getInt(postUrl,0);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setPageTransformer(true,new ZoomOutPageTransformer());
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //tvTitle.setText(chapterTitles.get(position));
                lastAccessedPage = position;
                pages.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Setup spinner
        pages = findViewById(R.id.pages);
        pageNumbers = new ArrayList<>();
        for(int i = 1; i <= pageCount; i++) {
            pageNumbers.add("Chapter " + i);//String.valueOf(i));//You should add items from db here (first spinner)
        }

        ArrayAdapter<String> pagesAdapter = new ArrayAdapter<>(ActivityReadStory.this, R.layout.chapter, pageNumbers);
        pagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pagesAdapter.notifyDataSetChanged();
        pages.setAdapter(pagesAdapter);
        pages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ((mViewPager.getCurrentItem() != position) ) {
                    lastAccessedPage = position;
                    mViewPager.setCurrentItem(position);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    @Override
    public void onStart(){
        super.onStart();
        mViewPager.setCurrentItem(setPage);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(String sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pagescroll, container, false);
            TextView textView = rootView.findViewById(R.id.section_label);

            textView.setText(getArguments().getString(ARG_SECTION_NUMBER));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a f
     * ragment corresponding to
     * one of the sections/tabs/categorySpinner.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(contents.get(position));
        }

        @Override
        public int getCount() {
            return pageCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        editor = mPref.edit();
        editor.putInt(postUrl,lastAccessedPage);
        editor.putInt(postUrl+1,pageCount);
        //editor.commit();
        editor.apply();
        finish();
    }

}
