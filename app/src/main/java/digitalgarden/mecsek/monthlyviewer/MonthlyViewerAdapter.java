package digitalgarden.mecsek.monthlyviewer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import digitalgarden.mecsek.utils.Longtime;

/**
 * 1601 - 2999 évek között számol, ez kb 16788 hónap (legalább nem végtelen. Egyébként vehetjük
 * kisebbre, ha más epoch-ot állítunk be.
 */
public class MonthlyViewerAdapter extends FragmentStatePagerAdapter
    {
    private static int NUM_MONTHS = 16788;
    private Longtime today;

    public MonthlyViewerAdapter(FragmentManager fragmentManager, Longtime today)
        {
        super(fragmentManager);
        this.today = today;

        today.clearDate();
        Log.d("TODAY", today.toString());
        }

    // Returns total number of pages
    @Override
    public int getCount()
        {
        return NUM_MONTHS;
        }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position)
        {
        // position == montsSinceEpoch
        return MonthlyViewerFragment.newInstance( position, today.get()); //null;
        }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position)
        {
        return (1601 + (position / 12)) + "." + (position % 12 + 1);
        }

    }
