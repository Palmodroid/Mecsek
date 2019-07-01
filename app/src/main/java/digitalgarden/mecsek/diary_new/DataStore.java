package digitalgarden.mecsek.diary_new;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import digitalgarden.mecsek.utils.Longtime;

/**
 * DataStore organizes data for diary.
 * DataMonthlyViewer contains(6x7 = 42 days), and it loads calendar data for its each day.
 * DataDay contains data for only one day, and this data originates from its DataMonthlyViewer.
 * DataMonthlyViewers overlap each other, but - because views start from monthly view - it is
 * easier to load the whole view, not only one month.
 * Touching one day on monthly view gets data from this DataMonthlyView (and not from its month).
 * Scrolling to an other day gets data from this month's DataMonthlyView
 */
public class DataStore
    {
    Map<Integer, DataMonthlyView> DataMonthlyViewList = new HashMap<>();

    /**
     * Gets data for monthly view
     * @param indexMonth
     * @return
     */
    public DataMonthlyView getDataMonthlyView( int indexMonth )
        {
        DataMonthlyView dataMonthlyView = DataMonthlyViewList.get( indexMonth );

        if ( dataMonthlyView == null )
            {
            dataMonthlyView = new DataMonthlyView( indexMonth );

            DataMonthlyViewList.put( indexMonth, dataMonthlyView );
            }

        return dataMonthlyView;
        }

    /**
     * Gets data for one day
     * @param indexDay
     * @return
     */
    public DataDay getDataDay( int indexDay )
        {
        Longtime longtime = new Longtime();

        longtime.setDayIndex( indexDay );

        DataMonthlyView dataMonthlyView = getDataMonthlyView( longtime.getMonthIndex() );


        return dataMonthlyView.getDay....
        }

    }
