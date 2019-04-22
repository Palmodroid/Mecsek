package digitalgarden.mecsek.diary;

import java.util.ArrayList;
import java.util.List;

import digitalgarden.mecsek.utils.Longtime;


public class ComplexDailyData
    {
    public class EntryData
        {
        private final Longtime date;
        private final String note;

        public EntryData(Longtime date, String note)
            {
            this.date = date;
            this.note = note;
            }

        public String getNote()
            {
            return note;
            }
        }

    private List<EntryData> entryDataList = new ArrayList<>();

    public void addEntryData( Longtime date, String note )
        {
        entryDataList.add( new EntryData(date,note));
        }

    public List<EntryData> getEntryDataList()
        {
        return entryDataList;
        }

    private String dayOfMonth;

    private int dayColor;

    public void setDayOfMonth( String dayOfMonth, int dayColor)
        {
        this.dayOfMonth = dayOfMonth;
        this.dayColor = dayColor;
        }

    public String getDayOfMonth()
        {
        return dayOfMonth;
        }

    public int getDayColor()
        {
        return dayColor;
        }
    }
