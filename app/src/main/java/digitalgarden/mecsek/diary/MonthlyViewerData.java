package digitalgarden.mecsek.diary;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import digitalgarden.mecsek.database.calendar.CalendarTable;
import digitalgarden.mecsek.scribe.Scribe;
import digitalgarden.mecsek.utils.Longtime;

import static digitalgarden.mecsek.database.DatabaseMirror.column;
import static digitalgarden.mecsek.database.DatabaseMirror.table;
import static digitalgarden.mecsek.database.library.LibraryDatabase.CALENDAR;

/**
 * Instead of Loaders:
 * https://stackoverflow.com/questions/51408098/what-is-the-appropriate-replacer-of-deprecated-getsupportloadermanager
 */
public class MonthlyViewerData implements LoaderManager.LoaderCallbacks<Cursor>
    {
    private MonthlyViewerFragment monthlyViewerFragment;
    private int monthsSinceEpoch;

    // First day of this view - without timeinfo
    private Longtime longtimeStart;

    private int daysSinceEpochForStart;

    // First day of next view - without timeinfo
    private Longtime longtimeEnd;

    private String yearMonth;

    private ComplexDailyData[] complexDailyDataArray = new ComplexDailyData[42];


    public String getYearMonth()
        {
        return yearMonth;
        }

    /**
     * Date parameters for this month
     * @param monthsSinceEpoch
     * @param today
     * @return year and month as string
     */
    public MonthlyViewerData( MonthlyViewerFragment monthlyViewerFragment,
                              MonthlyViewerLayout monthlyViewerLayout, int monthsSinceEpoch,
                              long today )
        {
        this.monthlyViewerFragment = monthlyViewerFragment;
        this.monthsSinceEpoch = monthsSinceEpoch;

        longtimeStart = new Longtime();
        longtimeStart.setYearMonth( monthsSinceEpoch );
        longtimeStart.setDayOfMonth(1);
        // YEAR MONTH and DAY (DAY_NAME) is set, but TIME is not

        yearMonth = longtimeStart.toStringYearMonth( true );
        int month = longtimeStart.get(Longtime.MONTH);

        longtimeStart.addDays( -longtimeStart.getDayName() );

        daysSinceEpochForStart = longtimeStart.daysSinceEpoch();

        longtimeEnd = longtimeStart.duplicate();
        longtimeEnd.addDays( 42 );

        Longtime longtime = longtimeStart.duplicate();
        for (int n= 0; n < monthlyViewerLayout.getChildCount(); n++)
            {
            complexDailyDataArray[n] = new ComplexDailyData();

            int dayColor;

            if ( today == longtime.get() )
                dayColor = 0xFFCD3925;
            else
                dayColor = getColorForDayName( longtime.getDayName() );

            if ( month != longtime.get(Longtime.MONTH) )
                dayColor &= 0x40FFFFFF;

            complexDailyDataArray[n].setDayOfMonth(longtime.toStringDayOfMonth(), dayColor);

            ((ComplexDailyView) monthlyViewerLayout.getChildAt(n)).
                    setData( complexDailyDataArray[n]);

            longtime.addDays(1); // 86350
            }

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


    public void createLoader()
        {
        monthlyViewerFragment.getActivity().getSupportLoaderManager().
                initLoader( monthsSinceEpoch, null, this);
        }


    public void destroyLoader()
        {
        monthlyViewerFragment.getActivity().getSupportLoaderManager().
                destroyLoader( monthsSinceEpoch );
        }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle)
        {
        Scribe.note("onCreateLoader (Query) started");

        if ( id != monthsSinceEpoch )
            return null;

        String[] projection = {
                column(CalendarTable.DATE),
                column(CalendarTable.NOTE)};

        String selection =
                column(CalendarTable.DATE) + " >=  ? AND " + column(CalendarTable.DATE) + " <= ?";
        String[] selectionArgs = {
                Long.toString( longtimeStart.get()),
                Long.toString( longtimeEnd.get()) };

        Log.d("NAPTAR", "MSE: " + monthsSinceEpoch +
                " First day: " + longtimeStart.toString() +
                " Last day: " + longtimeEnd.toString());

        // http://code.google.com/p/android/issues/detail?id=3153
        CursorLoader cursorLoader = new CursorLoader(
                monthlyViewerFragment.getContext(),
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
        StringBuilder sb = new StringBuilder("Adatok: * ");

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
            Longtime longtime = new Longtime
                    (cursor.getLong(cursor.getColumnIndexOrThrow( column( CalendarTable.DATE ))));

            sb.append(longtime.toString());
            sb.append(" ");
            sb.append(cursor.getString(cursor.getColumnIndexOrThrow( column( CalendarTable.NOTE ))));
            sb.append(" * ");

            int index = longtime.daysSinceEpoch() - daysSinceEpochForStart;
            complexDailyDataArray[index].addEntryData(
                            longtime,
                            cursor.getString(cursor.getColumnIndexOrThrow( column( CalendarTable.NOTE ))));
            }

        Log.d("TEST", sb.toString());

        cursor.close();
        }


    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader)
        {

        }

    }
