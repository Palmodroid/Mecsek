package digitalgarden.mecsek.diary_new;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import digitalgarden.mecsek.diary.MonthlyViewerFragment;
import digitalgarden.mecsek.scribe.Scribe;

public class MonthlyViewAdapter extends FragmentStatePagerAdapter
    {
    private static int NUM_MONTHS = 16788;
    private long today;

    FragmentManager fm;


    public MonthlyViewAdapter(FragmentManager fragmentManager, long today)
        {
        super(fragmentManager);
        this.today = today;
        fm = fragmentManager;
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
        return MonthlyViewerFragment.newInstance( position, today); //null;
        }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position)
        {
        return (1601 + (position / 12)) + "." + (position % 12 + 1);
        }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
        {
        super.destroyItem(container, position, object);

        for ( Fragment f : fm.getFragments())
            {
            Scribe.debug("Fragment: " + f.getTag());
            }

        Scribe.debug("Item destroyed: " + position);
        }
    }
