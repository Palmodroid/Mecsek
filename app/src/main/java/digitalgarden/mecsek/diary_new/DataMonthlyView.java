package digitalgarden.mecsek.diary;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import digitalgarden.mecsek.database.calendar.CalendarTable;
import digitalgarden.mecsek.scribe.Scribe;
import digitalgarden.mecsek.utils.Longtime;


/**
 * DataMonthlyView loads and stores all data for each day (As DataDay-s)
 */
public class DataMonthlyView implements LoaderManager.LoaderCallbacks<Cursor>
    {
    // Data for each daily view
    // There are maximum 31 days in one month
    private DataDay[] DataDayArray = new DataDay[31];

    // Months since epoch == index of the ViewPager
    private int monthsSinceEpoch;

    // "Name" of this month as string (Year, Month and Month as string)
    private String yearMonthString;

    // First day of this view - Longtime without timeinfo
    private Longtime longtimeStart;

    // Days since epoch - used for indexing complexDayilyDataArray
    private int daysSinceEpochForStart;

    // First day of next view - Longtime without timeinfo
    private Longtime longtimeEnd;


    /**
     * Constructor sets parameters for this month
     *
     * @param monthsSinceEpoch ViewPager index, which is months since epoch
     * @param today            Timestamp of today comes from DiaryActivity (as long because fragment
     *                         argiments are primitives)
     * @return year and month as string
     */
    public MonthlyViewerData(MonthlyViewerFragment monthlyViewerFragment,
                             MonthlyViewerLayout monthlyViewerLayout,
                             int monthsSinceEpoch,
                             long today)
        {
        this.monthlyViewerFragment = monthlyViewerFragment;
        this.monthsSinceEpoch = monthsSinceEpoch;

        // START DAY of this monthly view - FIRST DAY of the month is set first
        longtimeStart = new Longtime();
        longtimeStart.setYearMonth(monthsSinceEpoch);
        longtimeStart.setDayOfMonth(1);
        // YEAR MONTH and DAY (DAY_NAME) is set, but TIME is not

        // Data from FIRST DAY of this month
        yearMonthString = longtimeStart.toStringYearMonth(true);
        int month = longtimeStart.get(Longtime.MONTH);

        // Roll back to START DAY
        longtimeStart.addDays(-longtimeStart.getDayName());
        daysSinceEpochForStart = longtimeStart.daysSinceEpoch();

        // ENDING DAY - FIRST DAY OF THE NEXT MONTH
        longtimeEnd = longtimeStart.duplicate();
        longtimeEnd.addDays(42);

        Longtime longtime = longtimeStart.duplicate();
        for (int n = 0; n < 42; n++)
            {
            complexDailyDataArray[n] = new ComplexDailyData(longtime, month, today);
            ((ComplexDailyView) monthlyViewerLayout.getChildAt(n)).setData(complexDailyDataArray[n]);
            longtime.addDays(1); // 86350
            }
        }

    /**
     * "Name" of this month as string (Year, Month and Month as string)
     */
    public String getYearMonthString()
        {
        return yearMonthString;
        }

    public void createLoader()
        {
        Scribe.note("LOADER: CreateLoader - id: " + monthsSinceEpoch);
        LoaderManager.getInstance(monthlyViewerFragment.getActivity()).
                initLoader(monthsSinceEpoch, null, this);
        }

    /* !! Ez elfordításkor is mindent újratölt !! */
    public void destroyLoader()
        {
        Scribe.note("LOADER: DestroyLoader - id: " + monthsSinceEpoch);
        LoaderManager.getInstance(monthlyViewerFragment.getActivity()).
                destroyLoader(monthsSinceEpoch);
        }

    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle)
        {
        Scribe.note("LOADER: onCreateLoader (Query) started - id: " + monthsSinceEpoch);

        if (id != monthsSinceEpoch)
            {
            return null;
            }

        String[] projection = {
                column(CalendarTable.DATE),
                column(CalendarTable.NOTE)};

        String selection =
                column(CalendarTable.DATE) + " >=  ? AND " + column(CalendarTable.DATE) + " < ?";
        String[] selectionArgs = {
                Long.toString(longtimeStart.get()),
                Long.toString(longtimeEnd.get())};

        // http://code.google.com/p/android/issues/detail?id=3153
        CursorLoader cursorLoader = new CursorLoader(
                monthlyViewerFragment.getContext(),
                table(CALENDAR).contentUri(),
                projection,
                selection,
                selectionArgs,
                //new String[] { "%"+filterString+"%" },
                // ha nincs filterClause, akkor nem használja fel
                null);

        return cursorLoader;
        }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor)
        {
        Scribe.note("LOADER: onLoadFinished - id: " + monthsSinceEpoch);

        // !! VOLT EGY CURSOR.CLOSE() AZ EREDETI KÓDBAN. EZ SZTEM. MŰKÖDIK !!
        // https://stackoverflow.com/questions/49524029/support-library-27-1-0-onloaderfinished
        // -returns-a-closed-cursor-when-starting-a
        // if ( cursor.isClosed() )
        //    {
        //    LoaderManager.getInstance(monthlyViewerFragment.getActivity()).
        //            restartLoader(monthsSinceEpoch, null, this);
        //    return;
        //    }

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
            Longtime longtime = new Longtime
                    (cursor.getLong(cursor.getColumnIndexOrThrow(column(CalendarTable.DATE))));
            complexDailyDataArray[longtime.daysSinceEpoch() - daysSinceEpochForStart]
                    .addEntryData(
                            longtime,
                            cursor.getString(cursor.getColumnIndexOrThrow(column(CalendarTable.NOTE))));
            }

        for (ComplexDailyData data : complexDailyDataArray)
            {
            data.onLoadFinished();
            }
        }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader)
        {
        }
    }
