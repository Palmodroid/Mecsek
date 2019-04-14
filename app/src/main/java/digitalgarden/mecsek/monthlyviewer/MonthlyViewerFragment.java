package digitalgarden.mecsek.monthlyviewer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import digitalgarden.mecsek.R;
import digitalgarden.mecsek.utils.Longtime;


public class MonthlyViewerFragment extends Fragment
    {
    // Store instance variables
    private long today;
    private int monthsSinceEpoch;

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
        }

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

        return view;
        }

    }