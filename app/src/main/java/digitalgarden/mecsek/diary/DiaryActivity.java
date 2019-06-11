package digitalgarden.mecsek.diary;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import digitalgarden.mecsek.R;
import digitalgarden.mecsek.scribe.Scribe;
import digitalgarden.mecsek.utils.Keyboard;
import digitalgarden.mecsek.utils.Longtime;

/**
 * DiaryActivity shows the ViewPager, which gets its data from the DiaryAdapter.
 * MonthlyViewerFragment shows the days of one month, as ComplexDailyView-s of MonthlyViewerLayout.
 * MonthlyViewerData stores all data on a daily basis.
 */
public class DiaryActivity extends AppCompatActivity
        implements MonthlyViewerFragment.OnInputReadyListener
    {
    FragmentStatePagerAdapter diaryAdapter;

    FrameLayout dailyListFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        Longtime today = new Longtime();
        today.set();
        today.clearDate();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
            {
            @Override
            public void onClick(View view)
                {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                }
            });

        dailyListFrame = findViewById( R.id.daily_list_frame );

        ViewPager vpPager = (ViewPager) findViewById(R.id.view_pager_diary);
        diaryAdapter = new DiaryAdapter( getSupportFragmentManager(), today.get());
        vpPager.setAdapter(diaryAdapter);
        vpPager.setCurrentItem( today.monthsSinceEpoch() );
        vpPager.setOffscreenPageLimit(1);

        Log.d("TODAY", "Today: " + today.toString());

        // Attach the page change listener inside the activity
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
            {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position)
                {
                Toast.makeText(DiaryActivity.this,
                        "Selected page position: " + position, Toast.LENGTH_SHORT).show();
                }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
                {
                // Code goes here
                }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state)
                {
                // Code goes here
                }
            });
        }

    @Override
    protected void onResumeFragments()
        {
        Scribe.locus();
        super.onResumeFragments();

        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment dailyListFragment = fragmentManager.findFragmentByTag("LIST");
        if (dailyListFragment == null)
            {
/*            dailyListFragment = DailyListFragment.newInstance( );

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add( R.id.daily_list_frame, dailyListFragment, "LIST" );
            fragmentTransaction.commit();

            // New fragment always starts without keyboard
            Keyboard.hide( this );
*/
            Scribe.debug("New LIST Fragment was created, added");
            }
        else
            {
            dailyListFrame.setVisibility( View.VISIBLE );
            Scribe.debug("Old LIST Fragment was found");
            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
        {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_monthly_viewer, menu);
        return true;
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
        {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
            {
            return true;
            }

        return super.onOptionsItemSelected(item);
        }

    @Override
    public void onReady(ComplexDailyData data)
        {
        dailyListFrame.setVisibility( View.VISIBLE );

        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment dailyListFragment = fragmentManager.findFragmentByTag("LIST");
        if (dailyListFragment == null)
            {
            dailyListFragment = DailyListFragment.newInstance( );

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add( R.id.daily_list_frame, dailyListFragment, "LIST" );
            fragmentTransaction.commit();

            // New fragment always starts without keyboard
            Keyboard.hide( this );

            Scribe.debug("New LIST Fragment was created, added");
            }
        else
            {
            Scribe.debug("Old LIST Fragment was found");
            }

        Toast.makeText( this, "SHORT CLICK: " + data.getDayOfMonth(), Toast.LENGTH_SHORT).show();
        }

    @Override
    public void onBackPressed()
        {
        dailyListFrame.setVisibility( View.GONE );
        Fragment dailyListFragment = getSupportFragmentManager().findFragmentByTag("LIST");
        if (dailyListFragment != null)
            {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove( dailyListFragment );
            fragmentTransaction.commit();

            // New fragment always starts without keyboard
            Keyboard.hide( this );

            Scribe.debug("New LIST Fragment was created, added");
            }
        else
            {
            super.onBackPressed();
            Scribe.debug("Old LIST Fragment was found");
            }

        }
    }
