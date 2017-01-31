package com.underscoreresearch.mauritzremote;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;

import com.android.volley.Response;
import com.underscoreresearch.mauritzremote.config.Settings;
import com.underscoreresearch.mauritzremote.rooms.BedroomFragment;
import com.underscoreresearch.mauritzremote.rooms.LivingroomFragment;
import com.underscoreresearch.mauritzremote.rooms.MainFragment;
import com.underscoreresearch.mauritzremote.rooms.OfficeFragment;

import java.lang.reflect.Method;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainFragment.OnFragmentListener {
    private MainFragment mainFragment;
    private boolean fragmentLoaded;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RemoteService.initialize(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainFragment.turnOff();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (fragmentLoaded) {
                    mainFragment.selectPage(tab.getPosition(), true);
                    RemoteService.setRoomDevice(Settings.getSelectedRoom(MainActivity.this).name(), tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (fragmentLoaded) {
                    mainFragment.selectPage(tab.getPosition(), false);
                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        int menuId = R.id.nav_livingroom;
        switch(Settings.getSelectedRoom(this)) {
            case Bedroom:
                menuId = R.id.nav_bedroom;
                break;
            case Office:
                menuId = R.id.nav_office;
                break;
        }
        navigationView.setCheckedItem(menuId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMainFragment(false);

        if (Settings.getPassword(this) == null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Remote Service Password");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RemoteService.setPassword(MainActivity.this, input.getText().toString());
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }

    private void loadMainFragment(boolean replace) {
        final String room = Settings.getSelectedRoom(this).name();

        RemoteService.getRoomDevice(this, room, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int page = Integer.parseInt(response);
                if (mainFragment == null || !mainFragment.getMainTitle().equals(room) || page != Settings.getSelectedTab(MainActivity.this, room)) {
                    Settings.setSelectedTab(MainActivity.this, room, page);

                    fragmentLoaded = false;
                    switch (Settings.getSelectedRoom(MainActivity.this)) {
                        case Bedroom:
                            mainFragment = new BedroomFragment();
                            break;
                        case Office:
                            mainFragment = new OfficeFragment();
                            break;
                        default:
                            mainFragment = new LivingroomFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, mainFragment).commit();
                    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                    toolbar.setTitle(mainFragment.getMainTitle());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.menu_turn_on:
                mainFragment.turnOn();
                return true;
            case R.id.menu_nanit: {
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.nanit.baby");
                startActivity(intent);
                return true;
            }
            case R.id.menu_browser: {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://home.henrik.org"));
                startActivity(browserIntent);
                return true;
            }
            case R.id.menu_nest: {
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.nest.android");
                startActivity(intent);
                return true;
            }
            case R.id.menu_sense: {
                Intent intent = getPackageManager().getLaunchIntentForPackage("is.hello.sense");
                startActivity(intent);
                return true;
            }
            case R.id.menu_launcher: {
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.teslacoilsw.launcher");
                startActivity(intent);
                return true;
            }
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Settings.Room currentRoom = Settings.getSelectedRoom(this);
        Settings.Room newRoom = currentRoom;
        if (id == R.id.nav_bedroom) {
            newRoom = Settings.Room.Bedroom;
        } else if (id == R.id.nav_office) {
            newRoom = Settings.Room.Office;
        } else if (id == R.id.nav_livingroom) {
            newRoom = Settings.Room.Livingroom;
        }

        if (newRoom != currentRoom) {
            Settings.setSelectedRoom(this, newRoom);
            loadMainFragment(true);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onViewCreated(Fragment fragment, View view) {
        if ((Object)fragment == (Object)mainFragment) {
            mainFragment.applyPager(tabLayout, mainFragment.getViewPager(view));
            mainFragment.getViewPager(view).addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    RemoteService.cancelButton();
                }

                @Override
                public void onPageSelected(int position) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            fragmentLoaded = true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            mainFragment.volumeUp(true);
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            mainFragment.volumeDown(true);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            mainFragment.volumeUp(false);
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            mainFragment.volumeDown(false);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
