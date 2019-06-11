package digitalgarden.mecsek.diary_new;

import java.util.ArrayList;
import java.util.List;

import digitalgarden.mecsek.utils.Longtime;

/**
 * DataDay stores all data for one day.
 * The list of the entries is filled during load, but when loaded, will be ready for showing
 */
public class DataDay
    {
    // List for loading data
    private List<DataEntry> dataEntryListToLoad = new ArrayList<>();

    // List for data to show (loaded data is be moved here after loading finished)
    private List<DataEntry> dataEntryListToUse = null;

    // Day of the month as string (to show as header)
    private String dayOfMonth;

    // Basic background color of the day - can be altered by loaded entries
    private int dayColor;


    /**
     * These parameters are needed first to set up a day.
     * Second, loader fill up data for the whole monthly view, and fills data for the days
     * @param longtime time of this day (no timeinfo)
     * @param month month of this monthly view
     * @param today datestamp of today, comes from DiaryActivity without timeinfo
     */
    public DataDay(Longtime longtime, int month, long today)
        {
        dayOfMonth = longtime.toStringDayOfMonth();

        if ( today == longtime.get() )
            dayColor = 0xFFCD3925;
        else
            dayColor = getColorForDayName( longtime.getDayName() );

        if ( month != longtime.get(Longtime.MONTH) )
            dayColor &= 0x40FFFFFF;
        }

    /**
     * Returns background color for day-name
     * Static, to be called by MonthlyHeaderLayout
     */
    static int getColorForDayName( int dayName )
        {
        if (dayName < 5) // Hétköznap
            return 0xFFE3D26F;
        if (dayName < 6) // Szombat
            return 0xFFCDA642;
        // Vasárnap
        return 0xFFAD6519;
        }

    public void addEntryData(long id, Longtime date, String note )
        {
        dataEntryListToLoad.add( new DataEntry(id, note, date));
        }

    public void onLoadFinished()
        {
        dataEntryListToUse = dataEntryListToLoad;
        dataEntryListToLoad = new ArrayList<>();
        }

    public List<DataEntry> getEntryDataList()
        {
        return dataEntryListToUse;
        }

    public String getDayOfMonth()
        {
        return dayOfMonth;
        }

    public int getDayColor()
        {
        return dayColor;
        }

    }
