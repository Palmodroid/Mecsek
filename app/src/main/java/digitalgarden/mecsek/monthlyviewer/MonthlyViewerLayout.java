package digitalgarden.mecsek.monthlyviewer;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import digitalgarden.mecsek.R;
import digitalgarden.mecsek.utils.Longtime;
import digitalgarden.mecsek.viewutils.CheckedLayout;
import digitalgarden.mecsek.viewutils.TextPaint;

/**
 * Forces 7x5 Grid for the children
 */
public class MonthlyViewerLayout extends CheckedLayout
    {
    public MonthlyViewerLayout(Context context)
        {
        super(context);
        }

    public MonthlyViewerLayout(Context context, AttributeSet attrs)
        {
        super(context, attrs);
        }

    public MonthlyViewerLayout(Context context, AttributeSet attrs, int defStyleAttr)
        {
        super(context, attrs, defStyleAttr);
        }

    Longtime longtime;
    TextPaint dayPaint;
    TextPaint rowPaint;

    @Override
    public void init()
        {
        // Columns and Rows are expicitly set for MonthlyViewer
        columns = 7;
        rows = 6; // 2019.09 - 6 rows are needed

        dayPaint = new TextPaint();
        rowPaint = new TextPaint();
        }

    @Override
    public View getChildView( int row, int col )
        {
        ComplexDailyView childView = new ComplexDailyView( getContext() );
        childView.setDayPaint( dayPaint );
        childView.setRowPaint( rowPaint );

        return childView;
        }


    /**
     * Secondary parameters for this view
     * @param monthsSinceEpoch
     * @param today
     * @return year and month as string
     */
    public String setMonthsSinceEpoch( int monthsSinceEpoch, long today ) // Longtime longtime)
        {
        longtime = new Longtime();
        longtime.setYearMonth( monthsSinceEpoch );
        longtime.setDayOfMonth(1);
        // YEAR MONTH and DAY (DAY_NAME) is set, but TIME is not

        String yearMonth = longtime.toStringYearMonth( true );
        int month = longtime.get(Longtime.MONTH);

        int dayName = longtime.getDayName();
        longtime.addDays( -dayName );

        int dayColor;
        for (int n= 0; n < getChildCount(); n++)
            {
            dayName = longtime.getDayName();

            Longtime longtoday = new Longtime( today );
            Log.d("TODAY",
                    "Today (" + longtoday.toString() + ") as long: " + today + " This day (" + longtime.toString() +
                            ") as " +
                            "long: " + longtime.get());

            if ( today == longtime.get() )
                dayColor = 0xFFCD3925;
            else if ( dayName < 5 ) // Hétköznap
                dayColor = 0xFFE3D26F;
            else if ( dayName < 6 ) // Szombat
                dayColor = 0xFFCDA642;
            else // Vasárnap
                dayColor = 0xFFAD6519;

            if ( month != longtime.get(Longtime.MONTH) )
                dayColor &= 0x40FFFFFF;

            ((ComplexDailyView) getChildAt(n)).setDayOfMonth(longtime.toStringDayOfMonth(), dayColor);
            longtime.addDays(1); // 86350
            }

        return yearMonth;
        }


    // https://medium.com/square-corner-blog/android-leak-pattern-subscriptions-in-views-18f0860aa74c
    @Override
    protected void onAttachedToWindow()
        {
        super.onAttachedToWindow();

        // start timing of repeats
        repeatHandler.postDelayed( repeatRunnable, 1000 );
        }

    @Override
    protected void onDetachedFromWindow()
        {
        super.onDetachedFromWindow();

        // clear previous repeats
        repeatHandler.removeCallbacks( repeatRunnable );
        }

    private Handler repeatHandler = new Handler();

    private Runnable repeatRunnable = new Runnable()
        {
        @Override
        public void run()
            {
            for ( int row = 0; row < rows; row++ )
                {
                for (int col = 0; col < columns; col++)
                    {
                    getChildAt( row, col).invalidate();
                    }
                }

            repeatHandler.postDelayed( repeatRunnable, 1000);
            }
        };
    }
