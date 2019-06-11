package digitalgarden.mecsek.diary;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import digitalgarden.mecsek.scribe.Scribe;
import digitalgarden.mecsek.utils.Longtime;

/**
 * 1601 - 2999 évek között számol, ez kb 16788 hónap (legalább nem végtelen. Egyébként vehetjük
 * kisebbre, ha más epoch-ot állítunk be.
 */
public class DiaryAdapter extends FragmentStatePagerAdapter // FragmentStatePagerAdapter
    {
    private static int NUM_MONTHS = 16788;
    private long today;

FragmentManager fm;


    public DiaryAdapter(FragmentManager fragmentManager, long today)
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
