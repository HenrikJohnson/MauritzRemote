package com.underscoreresearch.mauritzremote;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.underscoreresearch.mauritzremote.config.Settings;
import com.underscoreresearch.mauritzremote.rooms.Zone2Fragment;
import com.underscoreresearch.mauritzremote.rooms.LivingroomFragment;
import com.underscoreresearch.mauritzremote.rooms.MainFragment;
import com.underscoreresearch.mauritzremote.rooms.OfficeFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainFragment.OnFragmentListener {
    private static final String NANIT_PACKAGE = "com.nanit.baby";
    private static final String NEST_PACKAGE = "com.nest.android";
    private static final String LAUNCHER_PACKAGE = "com.teslacoilsw.launcher";
    private static final String BROWSER_PACKAGE = "com.android.chrome";
    private MainFragment mainFragment;
    private boolean fragmentLoaded;
    private TabLayout tabLayout;
    private HomeWatcher watcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RemoteService.initialize(this);
        watcher = new HomeWatcher(this);
        watcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (mainFragment != null) {
                    mainFragment.homePressed();
                }
            }

            @Override
            public void onHomeLongPressed() {
                if (mainFragment != null) {
                    mainFragment.homePressed();
                }
            }
        });

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
            case Zone2:
                menuId = R.id.nav_zone2;
                break;
            case Office:
                menuId = R.id.nav_office;
                break;
        }
        navigationView.setCheckedItem(menuId);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }
    }

    @Override
    protected void onPause() {
        watcher.stopWatch();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMainFragment(false);
        if (mainFragment != null) {
            mainFragment.homePressed();
        }
        watcher.startWatch();

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

        final Snackbar snackbar;
        if (!replace) {
            snackbar = Snackbar.make(findViewById(R.id.main_frame), "Network not available", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else {
            snackbar = null;
        }

        RemoteService.getRoomDevice(this, room, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int page = Integer.parseInt(response);
                if (mainFragment == null || !mainFragment.getMainTitle().equals(room) || page != Settings.getSelectedTab(MainActivity.this, room)) {
                    Settings.setSelectedTab(MainActivity.this, room, page);

                    if (mainFragment != null) {
                        mainFragment.lostFocus();
                    }
                    fragmentLoaded = false;
                    switch (Settings.getSelectedRoom(MainActivity.this)) {
                        case Zone2:
                            mainFragment = new Zone2Fragment();
                            break;
                        case Office:
                            mainFragment = new OfficeFragment();
                            break;
                        default:
                            mainFragment = new LivingroomFragment();
                            break;
                    }
                    RemoteService.setCurrentRoom(mainFragment.getMainTitle());
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, mainFragment).commit();
                    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                    toolbar.setTitle(mainFragment.getMainTitle());
                } else {
                    mainFragment.refresh();
                }

                if (snackbar != null) {
                    snackbar.dismiss();
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

        MenuItem item = menu.findItem(R.id.menu_nanit);
        try {
            item.setIcon(getPackageManager().getApplicationIcon(NANIT_PACKAGE));
        } catch (PackageManager.NameNotFoundException e) {
            item.setVisible(false);
        }
        item = menu.findItem(R.id.menu_browser);
        try {
            item.setIcon(getPackageManager().getApplicationIcon(BROWSER_PACKAGE));
        } catch (PackageManager.NameNotFoundException e) {
            item.setVisible(false);
        }
        item = menu.findItem(R.id.menu_launcher);
        try {
            item.setIcon(getPackageManager().getApplicationIcon(LAUNCHER_PACKAGE));
        } catch (PackageManager.NameNotFoundException e) {
            item.setVisible(false);
        }
        item = menu.findItem(R.id.menu_nest);
        try {
            item.setIcon(getPackageManager().getApplicationIcon(NEST_PACKAGE));
        } catch (PackageManager.NameNotFoundException e) {
            item.setVisible(false);
        }

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
                Intent intent = getPackageManager().getLaunchIntentForPackage(NANIT_PACKAGE);
                startActivity(intent);
                return true;
            }
            case R.id.menu_browser: {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://home.henrik.org"));
                startActivity(browserIntent);
                return true;
            }
            case R.id.menu_nest: {
                Intent intent = getPackageManager().getLaunchIntentForPackage(NEST_PACKAGE);
                startActivity(intent);
                return true;
            }
            case R.id.menu_launcher: {
                Intent intent = getPackageManager().getLaunchIntentForPackage(LAUNCHER_PACKAGE);
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
        if (id == R.id.nav_zone2) {
            newRoom = Settings.Room.Zone2;
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

    private static final Integer RecordAudioRequestCode = 1;

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }
}
