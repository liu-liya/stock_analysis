package analysis.stock.com;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    /**
     * 得到某年某周的第一天
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
     * 得到某年某周的最后一天
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
     * 取得当前日期所在周的第一天
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
     * 取得当前日期所在周的周五日期
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
     * 取得当前日期所在周的前一周最后一天
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
     * 返回指定日期的月的第一天
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
     * 返回指定年月的月的第一天
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
     * 返回指定日期的月的最后一天
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
     * 返回指定年月的月的最后一天
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
     * 返回指定日期的上个月的最后一天
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
     * 返回指定日期的季的第一天
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
     * 返回指定年季的季的第一天
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
     * 返回指定日期的季的最后一天
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
     * 返回指定年季的季的最后一天
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
     * 返回指定日期的上一季的最后一天
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
     * 返回指定年季的上一季的最后一天
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
     * 返回指定日期的季度
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
