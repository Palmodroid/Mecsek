package digitalgarden.mecsek.diary;

import java.util.ArrayList;
import java.util.List;

import digitalgarden.mecsek.utils.Longtime;


public class ComplexDailyData
    {
    public class EntryData
        {
        private final Longtime date;
        private final String note;

        public EntryData(Longtime date, String note)
            {
            this.date = date;
            this.note = note;
            }

        public String getNote()
            {
            return note;
            }
        }

    // List of loading data
    private List<EntryData> entryDataListToLoad = new ArrayList<>();

    // List of data to show (loaded data is be moved here after loading finished)
    private List<EntryData> entryDataListToUse = null;

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
    public ComplexDailyData(Longtime longtime, int month, long today)
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

    public void addEntryData( Longtime date, String note )
        {
        entryDataListToLoad.add( new EntryData(date,note));
        }

    public void onLoadFinished()
        {
        entryDataListToUse = entryDataListToLoad;
        entryDataListToLoad = new ArrayList<>();
        }

    public List<EntryData> getEntryDataList()
        {
        return entryDataListToUse;
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
