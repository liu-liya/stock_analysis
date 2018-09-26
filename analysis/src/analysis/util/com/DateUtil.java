package analysis.util.com;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    /**
     * �õ�ĳ��ĳ�ܵĵ�һ��
     *
     * @param year
     * @param week
     * @return
     */
    public Date getFirstDayOfWeek(int year, int week) {
        week = week - 1;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DATE, 1);

        Calendar cal = (Calendar) calendar.clone();
        cal.add(Calendar.DATE, week * 7);

        return getFirstDayOfWeek(cal.getTime());
    }

    /**
     * �õ�ĳ��ĳ�ܵ����һ��
     *
     * @param year
     * @param week
     * @return
     */
    public Date getLastDayOfWeek(int year, int week) {
        week = week - 1;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DATE, 1);
        Calendar cal = (Calendar) calendar.clone();
        cal.add(Calendar.DATE, week * 7);

        return getLastDayOfWeek(cal.getTime());
    }

    /**
     * ȡ�õ�ǰ���������ܵĵ�һ��
     *
     * @param date
     * @return
     */
    public Date getFirstDayOfWeek(Date pDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.setTime(pDate);
        calendar.set(Calendar.DAY_OF_WEEK,
                      calendar.getFirstDayOfWeek()); // Sunday
        return calendar.getTime();
    }

    /**
     * ȡ�õ�ǰ���������ܵ���������
     *
     * @param date
     * @return
     */
    public Date getLastDayOfWeek(Date pDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.setTime(pDate);
        calendar.set(Calendar.DAY_OF_WEEK,
                     calendar.getFirstDayOfWeek() + 5); // Saturday
        return calendar.getTime();
    }

    public Date getLastDayOfWeek(String pDate) {
        Date dayTime = new Date();
        DateFormat dateFormat = new SimpleDateFormat ("yyyyMMdd" );
        try {
            dayTime = dateFormat.parse( pDate );
        } 
        catch ( Exception e ) {
            e.printStackTrace();
        }
        
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.setTime(dayTime);
        calendar.set(Calendar.DAY_OF_WEEK,
                     calendar.getFirstDayOfWeek() + 5); // Saturday
        return calendar.getTime();
    }
    /**
     * ȡ�õ�ǰ���������ܵ�ǰһ�����һ��
     *
     * @param date
     * @return
     */
    public Date getLastDayOfLastWeek(Date pDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pDate);
        return getLastDayOfWeek(calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.WEEK_OF_YEAR) - 1);
    }

    /**
     * ����ָ�����ڵ��µĵ�һ��
     *
     * @param year
     * @param month
     * @return
     */
    public Date getFirstDayOfMonth(Date pDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pDate);
        calendar.set(calendar.get(Calendar.YEAR),
                     calendar.get(Calendar.MONTH), 1);
        return calendar.getTime();
    }

    /**
     * ����ָ�����µ��µĵ�һ��
     *
     * @param year
     * @param month
     * @return
     */
    public Date getFirstDayOfMonth(Integer year, Integer month) {
        Calendar calendar = Calendar.getInstance();
        if (year == null) {
            year = calendar.get(Calendar.YEAR);
        }
        if (month == null) {
            month = calendar.get(Calendar.MONTH);
        }
        calendar.set(year, month, 1);
        return calendar.getTime();
    }

    /**
     * ����ָ�����ڵ��µ����һ��
     *
     * @param year
     * @param month
     * @return
     */
    public Date getLastDayOfMonth(Date pDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pDate);
        calendar.set(calendar.get(Calendar.YEAR),
                     calendar.get(Calendar.MONTH), 1);
        calendar.roll(Calendar.DATE, -1);
        return calendar.getTime();
    }

    /**
     * ����ָ�����µ��µ����һ��
     *
     * @param year
     * @param month
     * @return
     */
    public Date getLastDayOfMonth(Integer year, Integer month) {
        Calendar calendar = Calendar.getInstance();
        if (year == null) {
            year = calendar.get(Calendar.YEAR);
        }
        if (month == null) {
            month = calendar.get(Calendar.MONTH);
        }
        calendar.set(year, month, 1);
        calendar.roll(Calendar.DATE, -1);
        return calendar.getTime();
    }

    /**
     * ����ָ�����ڵ��ϸ��µ����һ��
     *
     * @param year
     * @param month
     * @return
     */
    public Date getLastDayOfLastMonth(Date pDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pDate);
        calendar.set(calendar.get(Calendar.YEAR),
                     calendar.get(Calendar.MONTH) - 1, 1);
        calendar.roll(Calendar.DATE, -1);
        return calendar.getTime();
    }

    /**
     * ����ָ�����ڵļ��ĵ�һ��
     *
     * @param year
     * @param quarter
     * @return
     */
    public Date getFirstDayOfQuarter(Date pDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pDate);
        return getFirstDayOfQuarter(calendar.get(Calendar.YEAR),
                                    getQuarterOfYear(pDate));
    }

    /**
     * ����ָ���꼾�ļ��ĵ�һ��
     *
     * @param year
     * @param quarter
     * @return
     */
    public Date getFirstDayOfQuarter(Integer year, Integer quarter) {
        Calendar calendar = Calendar.getInstance();
        Integer month = new Integer(0);
        if (quarter == 1) {
            month = 1 - 1;
        } else if (quarter == 2) {
            month = 4 - 1;
        } else if (quarter == 3) {
            month = 7 - 1;
        } else if (quarter == 4) {
            month = 10 - 1;
        } else {
            month = calendar.get(Calendar.MONTH);
        }
        return getFirstDayOfMonth(year, month);
    }

    /**
     * ����ָ�����ڵļ������һ��
     *
     * @param year
     * @param quarter
     * @return
     */
    public Date getLastDayOfQuarter(Date pDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pDate);
        return getLastDayOfQuarter(calendar.get(Calendar.YEAR),
                                   getQuarterOfYear(pDate));
    }

    /**
     * ����ָ���꼾�ļ������һ��
     *
     * @param year
     * @param quarter
     * @return
     */
    public Date getLastDayOfQuarter(Integer year, Integer quarter) {
        Calendar calendar = Calendar.getInstance();
        Integer month = new Integer(0);
        if (quarter == 1) {
            month = 3 - 1;
        } else if (quarter == 2) {
            month = 6 - 1;
        } else if (quarter == 3) {
            month = 9 - 1;
        } else if (quarter == 4) {
            month = 12 - 1;
        } else {
            month = calendar.get(Calendar.MONTH);
        }
        return getLastDayOfMonth(year, month);
    }

    /**
     * ����ָ�����ڵ���һ�������һ��
     *
     * @param year
     * @param quarter
     * @return
     */
    public Date getLastDayOfLastQuarter(Date pDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pDate);
        return getLastDayOfLastQuarter(calendar.get(Calendar.YEAR),
                                       getQuarterOfYear(pDate));
    }

    /**
     * ����ָ���꼾����һ�������һ��
     *
     * @param year
     * @param quarter
     * @return
     */
    public Date getLastDayOfLastQuarter(Integer year, Integer quarter) {
        Calendar calendar = Calendar.getInstance();
        Integer month = new Integer(0);
        if (quarter == 1) {
            month = 12 - 1;
        } else if (quarter == 2) {
            month = 3 - 1;
        } else if (quarter == 3) {
            month = 6 - 1;
        } else if (quarter == 4) {
            month = 9 - 1;
        } else {
            month = calendar.get(Calendar.MONTH);
        }
        return getLastDayOfMonth(year, month);
    }

    /**
     * ����ָ�����ڵļ���
     *
     * @param date
     * @return
     */
    public int getQuarterOfYear(Date pDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pDate);
        return calendar.get(Calendar.MONTH) / 3 + 1;
    }
}
