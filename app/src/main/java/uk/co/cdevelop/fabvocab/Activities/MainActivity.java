package uk.co.cdevelop.fabvocab.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import uk.co.cdevelop.fabvocab.Fragments.AddWordsFragment;
import uk.co.cdevelop.fabvocab.Fragments.Dialog.DatabaseSelectDialog;
import uk.co.cdevelop.fabvocab.Fragments.HomePageFragment;
import uk.co.cdevelop.fabvocab.Fragments.IFragmentWithCleanUp;
import uk.co.cdevelop.fabvocab.Fragments.MyDictionaryFragment;
import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.SQL.FabVocabContract;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton btnFloatingAdd = null;

    // Allow clearing of focus when clicking outside of an EditText view! Elegant!
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if(v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if(!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // START: Stetho Debug
        Stetho.initializeWithDefaults(this);
        // END: Stetho Debug

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btnFloatingAdd = (FloatingActionButton) findViewById(R.id.fab);
        btnFloatingAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                onNavigationItemSelected(R.id.nav_addwords);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.flcontent, new HomePageFragment(), null).commit();
        }



        // START: SQL SETUP
        // Populate with dummy data
        /*int wordId;
        String nextWord;
        for (int i = 0; i < 52; ++i) {
            nextWord = ((char) (97 + (Math.floor(i/2)) )) + "DUMMY";
            wordId = FabVocabSQLHelper.getInstance(this).addWord(nextWord, null);
            FabVocabSQLHelper.getInstance(this).addDefinition(wordId, "Dummy definition 1");
            FabVocabSQLHelper.getInstance(this).addDefinition(wordId, "Dummy definition 2");
            FabVocabSQLHelper.getInstance(this).addDefinition(wordId, "Dummy definition 3");
        }*/
        // END: SQL SETUP

    }

    public void hideFloatingAddButton() {
        if(btnFloatingAdd != null) {
            btnFloatingAdd.setVisibility(View.GONE);
        }
    }
    public void showFloatingAddButton() {
        if(btnFloatingAdd != null) {
            btnFloatingAdd.setVisibility(View.VISIBLE);
        }
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
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_purgedatabase) {
            FabVocabSQLHelper.getInstance(this).purge();
        } else if (id == R.id.action_selectdatabase) {
            (new DatabaseSelectDialog()).show(getSupportFragmentManager(), "databaseSelect");
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return onNavigationItemSelected(item.getItemId());
    }
    private boolean onNavigationItemSelected(int id) {


        IFragmentWithCleanUp newFragment = null;

        if(id == R.id.nav_home) {
            newFragment = new HomePageFragment();
        } else if (id == R.id.nav_addwords) {
            newFragment = new AddWordsFragment();

        } else if (id == R.id.nav_mydictionary) {
            newFragment = new MyDictionaryFragment();

        } else if (id == R.id.nav_practice) {
            Intent intent = new Intent(this, PracticeActivity.class);
            intent.putExtra("mode", "normal");
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        // Clean up CURRENT FRAGMENT - i.e. remove children, close db



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if(newFragment != null){

            FragmentManager fm = getSupportFragmentManager();

            // Erase any backstack when a new page is selected from the AppDrawer navigation
            for(int i = 0; i < fm.getBackStackEntryCount(); i++) {
                fm.popBackStack();
            }

            // Clear all active fragments
            for(Fragment f : fm.getFragments()) {
                if(f != null) { // TODO: Why would there be null entries in the fragment manager?!
                    fm.beginTransaction().remove(f).commit();
                }
            }

            fm.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.flcontent, (Fragment) newFragment, null)
                    .commit();

        } else {
            //TODO: This should become a redundant scope once the menu is fully implemented.

            // For debug purposes
            Toast.makeText(this, "Attempt to load a menu item that hasn't yet been implemented...", Toast.LENGTH_LONG).show();

        }

        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        FabVocabSQLHelper.getInstance(this).close();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
