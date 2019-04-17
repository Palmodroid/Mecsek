package digitalgarden.mecsek.monthlyviewer;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import digitalgarden.mecsek.R;
import digitalgarden.mecsek.database.books.BooksTable;
import digitalgarden.mecsek.database.calendar.CalendarTable;
import digitalgarden.mecsek.scribe.Scribe;
import digitalgarden.mecsek.utils.Longtime;

import static digitalgarden.mecsek.database.DatabaseMirror.column;
import static digitalgarden.mecsek.database.DatabaseMirror.table;
import static digitalgarden.mecsek.database.library.LibraryDatabase.BOOKS;
import static digitalgarden.mecsek.database.library.LibraryDatabase.CALENDAR;

public class MonthlyViewerFragment extends Fragment
    {
    // Store instance variables
    private long today;
    private int monthsSinceEpoch;

    private MonthlyViewerData monthlyViewerData;

    /**
     * newInstance constructor for creating fragment with arguments
     * @param monthsSinceEpoch
     * 
     * sets actual month as monthsSinceEpoch, which is equal the position in the MSEViewer
     * 
     * @param today
     * 
     * date of today (time part is deleted) - longtime as long
     * 
     * @return
     */
    public static MonthlyViewerFragment newInstance(int monthsSinceEpoch, long today)
        {
        MonthlyViewerFragment fragmentFirst = new MonthlyViewerFragment();
        Bundle args = new Bundle();
        args.putInt("MSE", monthsSinceEpoch);

        // Longtime lt = new Longtime();
        // lt.setYearMonth( monthsSinceEpoch );
        // Log.d("TODAY", "Fragments month: " + lt.toString());

        args.putLong("TODAY", today);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
        }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);

        monthsSinceEpoch = getArguments().getInt("MSE");
        today = getArguments().getLong("TODAY");

        monthlyViewerData = new MonthlyViewerData(this, monthsSinceEpoch );
        }


    private TextView testView = null;

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
        {
        View view = inflater.
                inflate(R.layout.fragment_monthly_viewer, container, false);

        // secondary parameters, which cannot be passed bay constructor (View constructor cannot
        // be changed)
        MonthlyViewerLayout monthlyViewerLayout =
                ((MonthlyViewerLayout)view.findViewById(R.id.diary_layout));
        String yearMonth = monthlyViewerLayout.setMonthsSinceEpoch( monthsSinceEpoch, today );

        TextView yearMonthTextView =
                (TextView) view.findViewById(R.id.year_month_text_view);
        yearMonthTextView.setText( yearMonth );

        testView = view.findViewById( R.id.test_view );


        // Azért került ide, hogy a View már biztosan kész legyen. Párja a másikban.
        monthlyViewerData.createLoader();

        return view;
        }


    @Override
    public void onDestroyView()
        {
        super.onDestroyView();

        // elfordításnál itt probléma lesz!! Vagyis kilövi, holott nem kellene.
        monthlyViewerData.destroyLoader();
        }

    }
