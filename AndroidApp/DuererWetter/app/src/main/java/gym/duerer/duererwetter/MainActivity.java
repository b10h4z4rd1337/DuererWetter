package gym.duerer.duererwetter;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main, new NowFragment());
        fragmentTransaction.commit();

        View someView = findViewById(R.id.main);
        View root = someView.getRootView();
        root.setBackgroundColor(getResources().getColor(android.R.color.white));

        ListView listView = (ListView) findViewById(R.id.navigationDrawer);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.drawerItems)));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Resources res = MainActivity.this.getResources();
                String selected = ((TextView) view).getText().toString();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment destination = null;
                if (selected.equals(res.getString(R.string.drawerNow))) {
                    destination = new NowFragment();
                } else if (selected.equals(res.getString(R.string.drawer24))) {
                    PastFragment temp = new PastFragment();
                    temp.setTime(1);
                    destination = temp;
                } else if (selected.equals(res.getString(R.string.drawerWeek))) {
                    PastFragment temp = new PastFragment();
                    temp.setTime(7);
                    destination = temp;
                } else if (selected.equals(res.getString(R.string.drawerWeek2))) {
                    PastFragment temp = new PastFragment();
                    temp.setTime(14);
                    destination = temp;
                } else if (selected.equals(res.getString(R.string.drawerMonth))) {
                    PastFragment temp = new PastFragment();
                    temp.setTime(30);
                    destination = temp;
                }

                if (destination != null) {
                    fragmentTransaction.replace(R.id.main, destination);
                    fragmentTransaction.disallowAddToBackStack();
                    fragmentTransaction.commit();
                    drawerLayout.closeDrawers();
                }
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle("Dürer-Wetter");
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle("Menü");
                    actionBar.setSubtitle("");
                }
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
}
