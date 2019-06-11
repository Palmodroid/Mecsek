package digitalgarden.mecsek.diary;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import digitalgarden.mecsek.R;

public class MonthlyViewerFragment extends Fragment
        implements View.OnClickListener, View.OnLongClickListener
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
        new Fragment();

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


    OnInputReadyListener onInputReadyListener;

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnInputReadyListener
        {
        public void onReady(ComplexDailyData data);
        }

    @Override
    public void onAttach(Context context)
        {
        super.onAttach(context);

        try
            {
            onInputReadyListener = (OnInputReadyListener) context;
            }
        catch (ClassCastException e)
            {
            throw new ClassCastException(context.toString() + " must implement " +
                    "OnInputReadyListener");
            }
        }

    @Override
    public void onDetach()
        {
        super.onDetach();


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
        monthlyViewerData = new MonthlyViewerData(this, monthlyViewerLayout,
                monthsSinceEpoch, today );

        monthlyViewerLayout.setOnClickListener( this );
        monthlyViewerLayout.setOnLongClickListener( this );

        TextView yearMonthTextView =
                (TextView) view.findViewById(R.id.year_month_text_view);
        yearMonthTextView.setText( monthlyViewerData.getYearMonthString() );

        // Azért került ide, hogy a View már biztosan kész legyen. Párja a másikban.
        monthlyViewerData.createLoader();

        return view;
        }

    @Override
    public void onClick(View v)
        {
        onInputReadyListener.onReady( ((ComplexDailyView)v).getData() );
        }

    @Override
    public boolean onLongClick(View v)
        {
        onInputReadyListener.onReady( ((ComplexDailyView)v).getData() );
        return true;
        }

    @Override
    public void onDestroyView()
        {
        super.onDestroyView();

        // elfordításnál itt probléma lesz!! Vagyis kilövi, holott nem kellene.
        monthlyViewerData.destroyLoader();
        }

    }
