package digitalgarden.mecsek.monthlyviewer;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

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


    public void setMonthsSinceEpoch( int monthsSinceEpoch ) // Longtime longtime)
        {
        longtime = new Longtime();
        longtime.setYearMonth( monthsSinceEpoch );
        longtime.setDayOfMonth(1);

        // nem kéne másolni átállítás előtt??
        int dayName = longtime.getDayName();

        longtime.addDays( -dayName );

        for (int n= 0; n < getChildCount(); n++)
            {
            ((ComplexDailyView) getChildAt(n)).setDayOfMonth(longtime);
            longtime.addDays(1);
            }

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
