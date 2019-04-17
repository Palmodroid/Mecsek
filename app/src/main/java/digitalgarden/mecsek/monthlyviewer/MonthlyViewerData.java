package digitalgarden.mecsek.monthlyviewer;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import digitalgarden.mecsek.R;
import digitalgarden.mecsek.database.calendar.CalendarTable;
import digitalgarden.mecsek.scribe.Scribe;
import digitalgarden.mecsek.utils.Longtime;

import static digitalgarden.mecsek.database.DatabaseMirror.column;
import static digitalgarden.mecsek.database.DatabaseMirror.table;
import static digitalgarden.mecsek.database.library.LibraryDatabase.CALENDAR;

public class MonthlyViewerData implements LoaderManager.LoaderCallbacks<Cursor>
    {
    private MonthlyViewerFragment monthlyViewerFragment;
    private int monthsSinceEpoch;

    public MonthlyViewerData( MonthlyViewerFragment monthlyViewerFragment, int monthsSinceEpoch )
        {
        this.monthlyViewerFragment = monthlyViewerFragment;
        this.monthsSinceEpoch = monthsSinceEpoch;
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

        Longtime longtimeStart = new Longtime();
        longtimeStart.setYearMonth( monthsSinceEpoch );
        longtimeStart.setDayOfMonth(1);
        longtimeStart.addDays( -longtimeStart.getDayName() );

        Longtime longtimeEnd = longtimeStart.duplicate();
        longtimeEnd.addDays( 42 );

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

        long time;
        Longtime longtime = new Longtime();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
            longtime.set(cursor.getLong(cursor.getColumnIndexOrThrow( column( CalendarTable.DATE ))));
            sb.append(longtime.toString());
            sb.append(" ");
            sb.append(cursor.getString(cursor.getColumnIndexOrThrow( column( CalendarTable.NOTE ))));
            sb.append(" * ");
            }

        Log.d("TEST", sb.toString());

        cursor.close();
        }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader)
        {

        }

    }
