package digitalgarden.mecsek.diary;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import digitalgarden.mecsek.database.calendar.CalendarTable;
import digitalgarden.mecsek.scribe.Scribe;
import digitalgarden.mecsek.utils.Longtime;

import static digitalgarden.mecsek.database.DatabaseMirror.column;
import static digitalgarden.mecsek.database.DatabaseMirror.table;
import static digitalgarden.mecsek.database.library.LibraryDatabase.CALENDAR;

/**
 * All data for this monthlyview is stored in this class
 * Instead of Loaders:
 * https://stackoverflow.com/questions/51408098/what-is-the-appropriate-replacer-of-deprecated-getsupportloadermanager
 */
public class OLDMonthlyViewerData implements LoaderManager.LoaderCallbacks<Cursor>
    {
    //Contains caller: fragment of the whole month
    private MonthlyFragment monthlyFragment;

    // Data for each daily view
    private OLDComplexDailyData[] OLDComplexDailyDataArray = new OLDComplexDailyData[42];

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
     * @param monthsSinceEpoch ViewPager index, which is months since epoch
     * @param today Timestamp of today comes from OLDDiaryActivity (as long because fragment
     *              argiments are primitives)
     * @return year and month as string
     */
    public OLDMonthlyViewerData(MonthlyFragment monthlyFragment,
                                MonthlyLayout monthlyLayout,
                                int monthsSinceEpoch,
                                long today )
        {
        this.monthlyFragment = monthlyFragment;
        this.monthsSinceEpoch = monthsSinceEpoch;

        // START DAY of this monthly view - FIRST DAY of the month is set first
        longtimeStart = new Longtime();
        longtimeStart.setMonthIndex( monthsSinceEpoch );
        longtimeStart.setDayOfMonth(1);
        // YEAR MONTH and DAY (DAY_NAME) is set, but TIME is not

        // Data from FIRST DAY of this month
        yearMonthString = longtimeStart.toStringYearMonth( true );
        int month = longtimeStart.get(Longtime.MONTH);

        // Roll back to START DAY
        longtimeStart.addDays( -longtimeStart.getDayName() );
        daysSinceEpochForStart = longtimeStart.getDayIndex();

        // ENDING DAY - FIRST DAY OF THE NEXT MONTH
        longtimeEnd = longtimeStart.duplicate();
        longtimeEnd.addDays( 42 );

        Longtime longtime = longtimeStart.duplicate();
        for (int n= 0; n < 42; n++)
            {
            OLDComplexDailyDataArray[n] = new OLDComplexDailyData( longtime, month, today );
            //((ComplexDailyView) monthlyLayout.getChildAt(n)).setMonthlyData(
            // OLDComplexDailyDataArray[n]);
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
        LoaderManager.getInstance(monthlyFragment.getActivity()).
                initLoader( monthsSinceEpoch, null, this);
        }

    /* !! Ez elfordításkor is mindent újratölt !! */
    public void destroyLoader()
        {
        Scribe.note("LOADER: DestroyLoader - id: " + monthsSinceEpoch);
        LoaderManager.getInstance(monthlyFragment.getActivity()).
              destroyLoader( monthsSinceEpoch );
        }

    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle)
        {
        Scribe.note("LOADER: onCreateLoader (Query) started - id: " + monthsSinceEpoch);

        if ( id != monthsSinceEpoch )
            return null;

        String[] projection = {
                column(CalendarTable.DATE),
                column(CalendarTable.NOTE)};

        String selection =
                column(CalendarTable.DATE) + " >=  ? AND " + column(CalendarTable.DATE) + " < ?";
        String[] selectionArgs = {
                Long.toString( longtimeStart.get()),
                Long.toString( longtimeEnd.get()) };

        // http://code.google.com/p/android/issues/detail?id=3153
        CursorLoader cursorLoader = new CursorLoader(
                monthlyFragment.getContext(),
                table( CALENDAR ).contentUri(),
                projection,
                selection,
                selectionArgs,
                //new String[] { "%"+filterString+"%" },
                // ha nincs filterClause, akkor nem használja fel
                null );

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
        //    LoaderManager.getInstance(monthlyFragment.getActivity()).
        //            restartLoader(getMonthIndex, null, this);
        //    return;
        //    }

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
            Longtime longtime = new Longtime
                    (cursor.getLong(cursor.getColumnIndexOrThrow( column( CalendarTable.DATE ))));
            OLDComplexDailyDataArray[longtime.getDayIndex() - daysSinceEpochForStart]
                    .addEntryData(
                            longtime,
                            cursor.getString(cursor.getColumnIndexOrThrow( column( CalendarTable.NOTE ))));
            }

        for ( OLDComplexDailyData data : OLDComplexDailyDataArray)
            {
            data.onLoadFinished();
            }
        }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader)
        {
        }
    }