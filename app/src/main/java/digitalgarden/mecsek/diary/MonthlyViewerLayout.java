package digitalgarden.mecsek.diary;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

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
