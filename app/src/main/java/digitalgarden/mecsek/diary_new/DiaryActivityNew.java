package digitalgarden.mecsek.diary_new;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import digitalgarden.mecsek.R;
import digitalgarden.mecsek.diary.ComplexDailyData;
import digitalgarden.mecsek.diary.DiaryAdapter;
import digitalgarden.mecsek.diary.MonthlyViewerFragment;
import digitalgarden.mecsek.scribe.Scribe;
import digitalgarden.mecsek.utils.Longtime;

/**
 * DiaryActivity shows the ViewPager, which gets its data from the DiaryAdapter.
 * MonthlyViewerFragment shows the days of one month, as ComplexDailyView-s of MonthlyViewerLayout.
 * MonthlyViewerData stores all data on a daily basis.
 */
public class DiaryActivityNew extends AppCompatActivity
        implements MonthlyViewerFragment.OnInputReadyListener
    {
    FragmentStatePagerAdapter monthlyAdapter;
    FragmentStatePagerAdapter dailyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        Longtime today = new Longtime();
        today.set();
        today.clearDate();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_new);
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

        ViewPager viewPagerMonthly = (ViewPager) findViewById(R.id.view_pager_monthly);
        monthlyAdapter = new MonthlyViewAdapter( getSupportFragmentManager(), today.get());
        viewPagerMonthly.setAdapter(monthlyAdapter);
        viewPagerMonthly.setCurrentItem( today.getMonthIndex() );
        viewPagerMonthly.setOffscreenPageLimit(1);

        ViewPager viewPagerDaily = (ViewPager) findViewById(R.id.view_pager_daily);
        dailyAdapter = new DayAdapter( getSupportFragmentManager(), today.get());
        viewPagerDaily.setAdapter(dailyAdapter);
        viewPagerDaily.setCurrentItem( today.getDayIndex() );
        viewPagerDaily.setOffscreenPageLimit(1);

        Log.d("TODAY", "Today: " + today.toString());

        // Attach the page change listener inside the activity
        viewPagerMonthly.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
            {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position)
                {
                Toast.makeText(DiaryActivityNew.this,
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

        }
    }
