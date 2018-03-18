package com.techart.writersblock;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.techart.writersblock.models.Users;
import com.techart.writersblock.tabs.Tab1Poems;
import com.techart.writersblock.tabs.Tab2Stories;
import com.techart.writersblock.tabs.Tab3Devotion;
import com.techart.writersblock.utils.FireBaseUtils;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Landing page.
 * The {@link android.support.v4.view.PagerAdapter} that will provide
 * fragments for each of the sections. We use a
 * loaded fragment in memory. If this becomes too memory intensive, it
 * may be best to switch to a
 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
 * The {@link ViewPager} that will host the section contents.
 */
public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener,ViewPager.OnPageChangeListener
{
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private BottomNavigationView bottomNavigationView;

    private ViewPager mViewPager;

    private ViewPager vp;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()==null)
                {
                    Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
                FireBaseUtils.subscribeToNewPostNotification();
            }
        };
        haveNetworkConnection();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //VIEWPAGER
        vp= findViewById(R.id.container);
        this.addPages();
        //TABLAYOUT
        tabLayout= findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(vp);
        tabLayout.addOnTabSelectedListener(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        //Refresh View
                        //createNotification("Story", "Chapter added");
                        break;
                    case R.id.navigation_create:
                        Intent dialogIntent = new Intent(MainActivity.this,  PostTypeDialog.class);
                        startActivity(dialogIntent);
                        break;
                    case R.id.navigation_profile:
                        startLibraryActivity();
                        break;
                }
                return true;
                }
            });
        //End bottom naviagtion
    }

    private void startLibraryActivity(){
        FireBaseUtils.mDatabaseUsers.child(FireBaseUtils.getUiD()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                if (user != null){
                    if (user.getSignedAs().trim().equals("Writer")){
                        Intent accountIntent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(accountIntent);
                    } else if (user.getSignedAs().trim().equals("Reader")) {
                        Intent accountIntent = new Intent(MainActivity.this, LibraryActivity.class);
                        startActivity(accountIntent);
                    } else {
                        Toast.makeText(MainActivity.this,"Could not open library "+ user.getSignedAs(),LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(MainActivity.this,"Error...! Try later",LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void addPages()
    {
        MyPageAdapter pagerAdapter=new MyPageAdapter(this.getSupportFragmentManager());
        pagerAdapter.addFragment(new Tab2Stories());
        pagerAdapter.addFragment(new Tab1Poems());
        pagerAdapter.addFragment(new Tab3Devotion());
        vp.setOffscreenPageLimit(1);
        //SET ADAPTER TO VP
        vp.setAdapter(pagerAdapter);
    }
    public void onTabSelected(TabLayout.Tab tab) {
        vp.setCurrentItem(tab.getPosition());
    }
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }
    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.getTabAt(position).select();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
       /* SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_help:
                Intent help = new Intent(MainActivity.this,InformationActivity.class);
                startActivity(help);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void haveNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null)
        {
            NetworkInfo netWorkInfo = cm.getActiveNetworkInfo();
            if (netWorkInfo != null && netWorkInfo.getState() == NetworkInfo.State.CONNECTED)
            {
                Toast.makeText(MainActivity.this,"Connected", LENGTH_LONG).show();
            }
            else {
                Toast.makeText(MainActivity.this,"No internet Connection", LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }
}
