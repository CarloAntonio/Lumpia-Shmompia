package com.riskitbiskit.lumpiashmompia;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.riskitbiskit.lumpiashmompia.data.MenuContract;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.riskitbiskit.lumpiashmompia.MenuActivity.CHECKOUT_LIST;
import static com.riskitbiskit.lumpiashmompia.MenuActivity.EMPTY;
import static com.riskitbiskit.lumpiashmompia.data.MenuContract.MenuEntry.CONTENT_URI;

public class AboutActivity extends AppCompatActivity {

    //Variables
    private String[] mMenuTitles;
    private ActionBarDrawerToggle mDrawerToggle;
    private SharedPreferences sharedPreferences;

    //Views
    @BindView(R.id.about_toolbar)
    Toolbar aboutToolbar;

    @BindView(R.id.about_drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.about_left_drawer)
    ListView mDrawerList;

    @BindView(R.id.to_map_bt)
    Button directionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        //Setup custom toolbar
        setSupportActionBar(aboutToolbar);

        //Get reference of Shared Preference
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Setup Hamburger Menu
        mMenuTitles = getResources().getStringArray(R.array.menu_options);

        mDrawerLayout.setScrimColor(ContextCompat.getColor(this, android.R.color.transparent));

        mDrawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mMenuTitles));
        mDrawerList.setOnItemClickListener(new AboutActivity.AdapterClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);

        //Performs drawer <-> back button animation
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawerToggle.syncState();

        //Setup directions button
        directionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/?api=1&query=554+Ygnacio+Valley+Rd,+Walnut Creek"));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //if any items in the menu is clicked...return true
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class AdapterClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            switch (position) {
                case 0:
                    Intent reorderIntent = new Intent(getBaseContext(), OrderActivity.class);
                    startActivity(reorderIntent);
                    return;
                case 1:
                    Intent menuIntent = new Intent(getBaseContext(), MenuActivity.class);
                    startActivity(menuIntent);
                    finish();
                    return;
                case 2:
                    if (sharedPreferences.getString(CHECKOUT_LIST, EMPTY).contentEquals(EMPTY)) {
                        Toast.makeText(getBaseContext(), "Nothing In Cart", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent checkoutIntent = new Intent(getBaseContext(), OrderActivity.class);
                        startActivity(checkoutIntent);
                    }
                    return;
                case 3:
                    //Delete all items in Shared Preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(CHECKOUT_LIST, EMPTY);
                    editor.apply();

                    //Reset all item totals in database
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MenuContract.MenuEntry.COLUMN_ITEM_COUNT, "1");
                    getContentResolver().update(CONTENT_URI, contentValues, null, null);

                    //Restart activity
                    Intent clearCartIntent = new Intent(getBaseContext(), MenuActivity.class);
                    startActivity(clearCartIntent);
                    finish();
                    return;
                case 4:
                    Intent aboutIntent = new Intent(getBaseContext(), AboutActivity.class);
                    startActivity(aboutIntent);
                    finish();
                    return;
                default:
                    return;
            }
        }
    }
}
