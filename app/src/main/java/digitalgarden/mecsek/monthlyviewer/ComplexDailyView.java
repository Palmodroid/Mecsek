package digitalgarden.mecsek.monthlyviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import digitalgarden.mecsek.R;
import digitalgarden.mecsek.utils.Longtime;
import digitalgarden.mecsek.viewutils.BackgroundView;
import digitalgarden.mecsek.viewutils.TextPaint;

import static digitalgarden.mecsek.viewutils.TextPaint.millis;


/**
 * Draws a complex diary View on the top of a backgroundView
 * Parameters to set:
 * setRowPaint() - paint used for data rows
 * setDayPaint() - paint used for day of the month
 * setDayOfMonth() - sets only day of the month
 */
public class ComplexDailyView extends BackgroundView implements View.OnClickListener
    {
    // text paint to draw text with
    private TextPaint dayPaint;

    // text paint tod draw rows
    private TextPaint rowPaint;

    private int rowy;
    private int rowx;
    private int rowHeight;
    private int rowMaxWidth;

    private Bitmap bitmap;
    private Bitmap icon;

    private int bitmapX;
    private int bitmapY;

    // the day to show
    private String dayOfMonth = "";
    private int dayColor = 0;

    public ComplexDailyView(Context context)
        {
        super(context);
        }

    public ComplexDailyView(Context context, AttributeSet attrs)
        {
        super(context, attrs);
        }

    public ComplexDailyView(Context context, AttributeSet attrs, int defStyleAttr)
        {
        super(context, attrs, defStyleAttr);
        }


    public void setDayOfMonth(Longtime longtime )
        {
        this.dayOfMonth = longtime.toStringDayOfMonth();
        int dayName = longtime.getDayName();

        if ( dayName < 5 ) // Hétköznap
            dayColor = 0xFFE3D26F;
        else if ( dayName < 6 ) // Szombat
            dayColor = 0xFFCDA642;
        else // Vasárnap
            dayColor = 0xFFAD6519;
        }

    public void setDayPaint(TextPaint dayPaint)
        {
        this.dayPaint = dayPaint;
        }

    public void setRowPaint(TextPaint rowPaint)
        {
        this.rowPaint = rowPaint;
        }


    @Override
    protected void onAttachedToWindow()
        {
        super.onAttachedToWindow();

        Log.d("BOX", "ComplexDiaryView - onAttachedToWindow");

        if ( dayPaint == null )
            {
            dayPaint = new TextPaint();

            dayPaint.setColor(Color.BLUE);
            dayPaint.setTextBold(true);
            dayPaint.setTextItalics(false);
            }

        if ( dayPaint == null )
            {
            rowPaint = new TextPaint();

            rowPaint.setColor(Color.MAGENTA);
            rowPaint.setTextBold(false);
            rowPaint.setTextItalics(false);
            }
        dayPaint.setTextToMeasure("33");
        dayPaint.setTextAlign(TextPaint.ALIGN_LEFT + TextPaint.ALIGN_CENTER);
        // rowPaint.setTextToMeasure("M", "y");

        // bitmap = BitmapFactory.decodeResource( getContext().getResources(),
        //        R.drawable.smallballoons);

        setOnClickListener( this );
        }

    @Override
    public void onClick(View v)
        {
        Toast.makeText( getContext(), dayOfMonth, Toast.LENGTH_SHORT).show();
        }

    @Override
    protected void onSizeChangedInDraw(int width, int height)
        {
        super.onSizeChangedInDraw(width, height);

        Log.d("BOX", "ComplexDiaryView - onSizeChanged Width: " + width + " Height: " + height);

        // 5 - 43 - 4 - 43 - 5
        int imageSize = Math.min( millis( width, 430 ), millis( height, 430));

        // A hónap méretének el kell férnie egy kép méretű dobozban
        dayPaint.calculateTextSizeForBox( imageSize, imageSize );
        dayPaint.setTextXY( millis(width, 480),
                millis(width, 50) + (imageSize / 2));

        rowPaint.calculateTextSizeForBox( millis( imageSize, 300), millis( imageSize, 300) );

        rowy = millis(width, 100) + (imageSize);
        rowx = millis(width, 50);
        rowHeight = rowPaint.getMeasuredTextHeight() + millis(imageSize, 100);
        rowMaxWidth = millis( width, 900);

/*        rectF.set(
                millis(width, 520),
                millis(width, 50),
                millis(width, 520) + imageSize,
                millis(width, 50) + imageSize);
        canvas.drawRoundRect(rectF, 5f, 5f, paint);
*/
        // https://stackoverflow.com/questions/2895065/what-does-the-filter-parameter-to-createscaledbitmap-do
        // icon = Bitmap.createScaledBitmap( icon, imageSize - 6, imageSize - 6, true);

        // icon = ThumbnailUtils.extractThumbnail( bitmap, imageSize - 6, imageSize - 6);

        bitmapX = millis(width, 520) + 3;
        bitmapY = millis(width, 50) + 3;
        }

    int n = 0;

    @Override
    protected void onDraw(Canvas canvas)
        {
        getBackgroundPaint().setColor( dayColor );

        super.onDraw(canvas);

        dayPaint.drawText(canvas, dayOfMonth);

        int y = rowy;
        rowPaint.drawEllipsizedText(canvas,
                "Hello világ!", rowx, y, rowMaxWidth );

        y += rowHeight;
        rowPaint.drawEllipsizedText(canvas,
                "Abraka dabra darabka", rowx, y, rowMaxWidth );

        y += rowHeight;
        rowPaint.drawEllipsizedText(canvas,
                "Akármikor egy almafa alatt egyedem begyedem tengertánc", rowx, y, rowMaxWidth );

        if ( n % 2 == 1 )
            {
            // canvas.drawBitmap(icon, bitmapX, bitmapY, null);
            }
        n++;
        }

    }