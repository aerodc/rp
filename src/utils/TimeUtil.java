package utils;

import java.sql.Timestamp;
import java.text.*;
import java.util.*;

/**
 * <p>Cette classe est un utilitaire de m�thodes statiques servant � manipuler et
 * formater les dates. Elle sont sutout utilis�e pour mettre en forme les dates
 * contenues dans la base de donn�es qui sont en format 'YYYYMMDDHHMMSS'.</p>
 *
 * @version 1.0
 */
public class TimeUtil {

    /**
     * M�thode renvoyant la date courrante dans un format standart.<br/>
     * Exemple: '23, Juin 2003'
     *
     * @return la date courrante format�e
     */
    public static String currentTextDate() {

        Calendar cal = GregorianCalendar.getInstance();
        DateFormat df = DateFormat.getDateInstance();

        return df.format(cal.getTime());
    }

    /**
     * Méthode renvoyant la date courrante dans un format standart.<br/>
     * Exemple: '23, Juin 2003'
     *
     * @return la date courrante format�e
     */
    public static String currentTextDate(String format) {

        Calendar cal = GregorianCalendar.getInstance();

        return new SimpleDateFormat(format).format(cal.getTime());
    }

    /**
     * Méthode qui renvoie la date courante dans le format utilis� par la
     * base de donn�es, i.e. YYYYMMDDHHMMSS
     *
     * @return la date courante au format de la base de donn�es.
     */
    public static String currentDBDate() {
        Calendar calendar = GregorianCalendar.getInstance();
        DecimalFormat df = new DecimalFormat("00");

        return df.format(calendar.get(Calendar.YEAR)) +
                df.format(calendar.get(Calendar.MONTH) + 1) +
                df.format(calendar.get(Calendar.DAY_OF_MONTH)) +
                df.format(calendar.get(Calendar.HOUR_OF_DAY)) +
                df.format(calendar.get(Calendar.MINUTE)) +
                df.format(calendar.get(Calendar.SECOND));
    }

    /**
     * M�thode qui renvoie la date courante en ms
     *
     * @return la date courante en ms
     */
    public static long currentDateInMillis() {
        Calendar calendar = GregorianCalendar.getInstance();
        return calendar.getTimeInMillis();
    }

    /**
     * M�thode qui renvoie la date courante en ms
     *
     * @return la date courante en ms
     */
    public static Date currentDate() {
        Calendar calendar = GregorianCalendar.getInstance();
        return calendar.getTime();
    }

    public static Date currentDateTime(String format) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        //System.out.println(dateFormat.format(date)); //2013/10/15 16:16:39
        return stringToDate(dateFormat.format(date), format);
    }

    /**
     * M�thode qui renvoie la date courante en ms
     *
     * @return la date courante en ms
     */
    public static String currentDate(String oFormat) {
        Calendar calendar = GregorianCalendar.getInstance();
        return convert(calendar.getTime(), oFormat);
    }

    /**
     * M�thode qui renvoie la date entr�e en ms
     *
     * @return la date en ms.
     */
    public static long thisDateInMillis(String sDate, String sFormat) {
        if (sDate == null || "".equals(sDate) || " ".equals(sDate)) { return 0; }
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat(sFormat);
        Date inputDate = null;
        try {
            inputDate = sdf.parse(sDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
            return 0;
        }
        calendar.setTime(inputDate);
        return calendar.getTimeInMillis();
    }

    /**
     * M�thode qui convertie ms en date
     *
     * @return la date en ms.
     */
    public static String thisMillisInDate(long ms, String sFormat) {
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat(sFormat);
        calendar.setTimeInMillis(ms);
        return sdf.format(calendar.getTime());
    }

    public static String formatCalendarToDBDate(Calendar calendar) {
        DecimalFormat df = new DecimalFormat("00");

        return df.format(calendar.get(Calendar.YEAR)) +
                df.format(calendar.get(Calendar.MONTH) + 1) +
                df.format(calendar.get(Calendar.DAY_OF_MONTH)) +
                df.format(calendar.get(Calendar.HOUR_OF_DAY)) +
                df.format(calendar.get(Calendar.MINUTE)) +
                df.format(calendar.get(Calendar.SECOND));
    }

    public static String formatCalendarToStdDate(Calendar calendar) {
        DateFormat df = DateFormat.getDateInstance();

        return df.format(calendar.getTime());
    }

    /**
     * M�thode qui permet de formatter une date au format YYYYMMDDHHMMSS pass�e en param�tre,
     * en une date au format 'DD/MM/YYYY'
     *
     * @param date date � formater
     * @return renvoie la date format�e
     */
    public static String formatDBtoStdDate(String date) {
        String ret = "";
        if (date != null) {
            if (date.length() >= 8) {
                ret = date.substring(6, 8) + "/" + date.substring(4, 6) + "/" + date.substring(0, 4);
            }
        }
        return ret;
    }

    /**
     * M�thode qui permet de formatter une date au format YYYYMMDDHHMMSS pass�e en param�tre,
     * en une date au format 'DD/MM/YYYY HH:MM:SS'
     *
     * @param date date � formater
     * @return renvoie la date format�e
     */
    public static String formatDBtoStdTime(String date) {
        String ret = "";
        if (date != null) {
            if (date.length() >= 14) {
                ret = date.substring(6, 8) + "/" + date.substring(4, 6) + "/" + date.substring(0, 4) + " " + date
                        .substring(8, 10) + ":" + date.substring(10, 12) + ":" + date.substring(12, 14);
            } else if (date.length() >= 8) {
                ret = date.substring(6, 8) + "/" + date.substring(4, 6) + "/" + date.substring(0, 4);
            }
        }
        return ret;
    }

    /**
     * Transforms a date in the 'DD/MM/YYYY' format in the database format:
     * YYYYMMDDHHMMSS.
     *
     * @param date String
     * @return String
     */
    public static String formatStdtoDBDate(String date) {
        if (date == null || "".equals(date.trim())) { return ""; }
        String[] sdate = date.split("/");
        String annee = "1900", mois = "01", jour = "01";

        if (sdate.length == 3 && sdate[2] != null && sdate[2].length() == 4 && sdate[1] != null
                && sdate[1].length() == 2 && sdate[0] != null && sdate[0].length() == 2) {
            annee = sdate[2];
            mois = sdate[1];
            jour = sdate[0];
        }
        return annee + mois + jour + "000000";
    }

    /**
     * Prend une date en format String en un format d'entr�e iFormat donn�
     * pour une zone de date donn�e par iLocale
     * ex : Locale iLocale=new Locale("en","US")
     * et la convertie en une chaine en un format de sortie oFormat
     * pour une zone de date donn�e par oLocale
     * (cf tableau des param�tre d'un format date ci dessous)
     *
     * @param sDate String
     * @param iFormat String
     * @param oFormat String
     * @return String
     */

    public static String convert(String sdate, String iFormat, String oFormat, Locale iLocale, Locale oLocale) {
        if (sdate == null || "".equals(sdate)) { return ""; }
        SimpleDateFormat sdfi = new SimpleDateFormat(iFormat, iLocale);
        SimpleDateFormat sdfo = new SimpleDateFormat(oFormat, oLocale);
        //conversion de la chaine en date
        Date date = null;
        try {
            date = sdfi.parse(sdate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        //return de la date en chaine dans format de sortie
        if (date != null) { return sdfo.format(date); } else { return ""; }
    }
    /*
    Letter 	Date or Time Component 	Presentation 	Examples
     G 	Era designator 	Text 	AD
     y 	Year 	Year 	1996; 96
     M 	Month in year 	Month 	July; Jul; 07
     w 	Week in year 	Number 	27
     W 	Week in month 	Number 	2
     D 	Day in year 	Number 	189
     d 	Day in month 	Number 	10
     F 	Day of week in month 	Number 	2
     E 	Day in week 	Text 	Tuesday; Tue
     a 	Am/pm marker 	Text 	PM
     H 	Hour in day (0-23) 	Number 	0
     k 	Hour in day (1-24) 	Number 	24
     K 	Hour in am/pm (0-11) 	Number 	0
     h 	Hour in am/pm (1-12) 	Number 	12
     m 	Minute in hour 	Number 	30
     s 	Second in minute 	Number 	55
     S 	Millisecond 	Number 	978
     z 	Time zone 	General time zone 	Pacific Standard Time; PST; GMT-08:00
     Z 	Time zone 	RFC 822 time zone 	-0800
     */

    /**
     * Prend une date en format String en un format sFormat donn�
     * (cf tableau des param�tre d'un format date ci dessus)
     * DB format : yyyyMMddHHmmss
     * lui ajoute un certain nombre de jour, mois, ann�e
     * et renvois la chaine modifi� dans le m�me format d'entr�e
     *
     * @param sDate String
     * @param sFormat String
     * @param dayToAdd int
     * @param monthToAdd int
     * @param yearToAdd int
     * @return String
     */
    public static String add(String sDate, String sFormat, int dayToAdd, int monthToAdd, int yearToAdd) {
        GregorianCalendar calendar = new java.util.GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat(sFormat);
        Date inputDate = null;
        try {
            inputDate = sdf.parse(sDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
            return "parse error";
        }

        calendar.setTime(inputDate);
        calendar.add(Calendar.DATE, dayToAdd);
        calendar.add(Calendar.MONTH, monthToAdd);
        calendar.add(Calendar.YEAR, yearToAdd);

        return sdf.format(calendar.getTime());
    }

    /**
     * Prend une date en format String en un format sFormat donn�
     * (cf tableau des param�tre d'un format date ci dessus)
     * DB format : yyyyMMddHHmmss
     * lui ajoute un certain nombre d'un composant choisi
     * et renvois la chaine modifi� dans le m�me format d'entr�e
     *
     * @param sDate String
     * @param sFormat String
     * @param nb int
     * @param component char
     * @return String
     */
    public static String add(String sDate, String sFormat, int nb, char component) {
        GregorianCalendar calendar = new java.util.GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat(sFormat);
        Date inputDate = null;
        try {
            inputDate = sdf.parse(sDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
            return "parse error";
        }

        calendar.setTime(inputDate);

        int inc = 0;
        switch (component) {
            case 'y':
                inc = Calendar.YEAR;
                break;
            case 'M':
                inc = Calendar.MONTH;
                break;
            case 'w':
                inc = Calendar.WEEK_OF_YEAR;
                break;
            case 'W':
                inc = Calendar.WEEK_OF_MONTH;
                break;
            case 'D':
                inc = Calendar.DAY_OF_YEAR;
                break;
            case 'd':
                inc = Calendar.DAY_OF_MONTH;
                break;
            case 'F':
                inc = Calendar.DAY_OF_WEEK_IN_MONTH;
                break;
            case 'E':
                inc = Calendar.DAY_OF_WEEK;
                break;
            case 'H':
                inc = Calendar.HOUR_OF_DAY;
                break;
            case 'm':
                inc = Calendar.MINUTE;
                break;
            case 's':
                inc = Calendar.SECOND;
                break;
            case 'S':
                inc = Calendar.MILLISECOND;
                break;
        }

        calendar.add(inc, nb);

        return sdf.format(calendar.getTime());
    }

    /**
     * Prend une date en format String en un format sFormat donn�
     * (cf tableau des param�tre d'un format date ci dessus)
     * DB format : yyyyMMddHHmmss
     * lui ajoute un certain nombre d'un composant choisi
     * et renvois une date en milliseconde
     *
     * @param sDate String
     * @param sFormat String
     * @param nb int
     * @param component char
     * @return String
     */
    public static long giveMillis(String sDate, String sFormat) {
        GregorianCalendar calendar = new java.util.GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat(sFormat);
        Date inputDate = null;
        try {
            inputDate = sdf.parse(sDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
            return 0;
        }

        calendar.setTime(inputDate);
        return calendar.getTimeInMillis();
    }

    /**
     * Prend une date en milliseconde
     * lui ajoute un certain nombre d'un composant choisi
     * et renvois un nombre modifi� dans le m�me format d'entr�e
     *
     * @param sDate String
     * @param sFormat String
     * @param nb int
     * @param component char
     * @return String
     */
    public static long add(long beginTime, int nb, char component) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(beginTime);

        int inc = 0;
        switch (component) {
            case 'y':
                inc = Calendar.YEAR;
                break;
            case 'M':
                inc = Calendar.MONTH;
                break;
            case 'w':
                inc = Calendar.WEEK_OF_YEAR;
                break;
            case 'W':
                inc = Calendar.WEEK_OF_MONTH;
                break;
            case 'D':
                inc = Calendar.DAY_OF_YEAR;
                break;
            case 'd':
                inc = Calendar.DAY_OF_MONTH;
                break;
            case 'F':
                inc = Calendar.DAY_OF_WEEK_IN_MONTH;
                break;
            case 'E':
                inc = Calendar.DAY_OF_WEEK;
                break;
            case 'H':
                inc = Calendar.HOUR_OF_DAY;
                break;
            case 'm':
                inc = Calendar.MINUTE;
                break;
            case 's':
                inc = Calendar.SECOND;
                break;
            case 'S':
                inc = Calendar.MILLISECOND;
                break;
        }

        calendar.add(inc, nb);

        return calendar.getTimeInMillis();
    }

    public static Date add(Date beginTime, int nb, char component) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(beginTime);
        calendar.setTimeInMillis(add(calendar.getTimeInMillis(), nb, component));
        return calendar.getTime();
    }

    /**
     * Prend une date d'entr�e en format String en un format sFormat donn�
     * et retourne les p*N composants de dates pr�c�dents pr�c�dent ou suivant
     * DANS LE FORMAT D'ENTREE (1 seul format en signature)
     * exemple : 6 dernier mois, 7 prochains jours, 12 derni�res heures...
     *
     * @param sDate String date d'entr�e
     * @param sFormat String format de la date en entr�e et en sortie
     * @param before int nombre de p�riode avant
     * @param before int nombre de p�riode apr�s
     * @param component char nature du composant de date : ann�e, mois, semaine, jour, heure, minute, seconde, ms
     * @param pas int : intervalle entre deux date g�n�r�es
     * @return HashMap : rang + dates
     */

    public static HashMap<Integer, String> aroundDate(String sDate, String sFormat, int before, int after,
            char component, int pas) {
        HashMap<Integer, String> result = new HashMap<Integer, String>();
        GregorianCalendar calendar = new java.util.GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat(sFormat);
        Date inputDate = null;
        try {
            inputDate = sdf.parse(sDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
            return null;
        }
        int inc = 0;
        switch (component) {
            case 'y':
                inc = Calendar.YEAR;
                break;
            case 'M':
                inc = Calendar.MONTH;
                break;
            case 'w':
                inc = Calendar.WEEK_OF_YEAR;
                break;
            case 'W':
                inc = Calendar.WEEK_OF_MONTH;
                break;
            case 'D':
                inc = Calendar.DAY_OF_YEAR;
                break;
            case 'd':
                inc = Calendar.DAY_OF_MONTH;
                break;
            case 'F':
                inc = Calendar.DAY_OF_WEEK_IN_MONTH;
                break;
            case 'E':
                inc = Calendar.DAY_OF_WEEK;
                break;
            case 'H':
                inc = Calendar.HOUR_OF_DAY;
                break;
            case 'm':
                inc = Calendar.MINUTE;
                break;
            case 's':
                inc = Calendar.SECOND;
                break;
            case 'S':
                inc = Calendar.MILLISECOND;
                break;
        }
        int rang = 0;
        calendar.setTime(inputDate);
        calendar.add(inc, -before * pas);
        for (int i = 0; i < before + after; i++) {
            calendar.add(inc, pas);
            result.put(rang++, sdf.format(calendar.getTime()));
        }
        return result;
    }

    /**
     * Prend une date d'entr�e en format String en un format sFormat donn�
     * et retourne les p*N composants de dates pr�c�dents pr�c�dent ou suivant
     * DANS LE FORMAT SPECIFIE EN ENTREE (2 formats en signature)
     * exemple : 6 dernier mois, 7 prochains jours, 12 derni�res heures...
     *
     * @param sDate String date d'entr�e
     * @param sFormat String format de date d'entr�e et de sortie
     * @param sFormated String format de sortie
     * @param before int nombre de p�riode avant
     * @param before int nombre de p�riode apr�s
     * @param component char nature du composant de date : ann�e, mois, semaine, jour, heure, minute, seconde, ms
     * @param pas int : intervalle entre deux date g�n�r�es
     * @return HashMap : rang + dates
     */

    public static HashMap<Integer, String> aroundDate(String sDate, String sFormat, String sFormated, int before,
            int after, char component, int pas) {
        HashMap<Integer, String> result = new HashMap<Integer, String>();
        GregorianCalendar calendar = new java.util.GregorianCalendar();
        SimpleDateFormat sdfi = new SimpleDateFormat(sFormat);
        SimpleDateFormat sdfo = new SimpleDateFormat(sFormated);
        Date inputDate = null;
        try {
            inputDate = sdfi.parse(sDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
            return null;
        }
        int inc = 0;
        switch (component) {
            case 'y':
                inc = Calendar.YEAR;
                break;
            case 'M':
                inc = Calendar.MONTH;
                break;
            case 'w':
                inc = Calendar.WEEK_OF_YEAR;
                break;
            case 'W':
                inc = Calendar.WEEK_OF_MONTH;
                break;
            case 'D':
                inc = Calendar.DAY_OF_YEAR;
                break;
            case 'd':
                inc = Calendar.DAY_OF_MONTH;
                break;
            case 'F':
                inc = Calendar.DAY_OF_WEEK_IN_MONTH;
                break;
            case 'E':
                inc = Calendar.DAY_OF_WEEK;
                break;
            case 'H':
                inc = Calendar.HOUR_OF_DAY;
                break;
            case 'm':
                inc = Calendar.MINUTE;
                break;
            case 's':
                inc = Calendar.SECOND;
                break;
            case 'S':
                inc = Calendar.MILLISECOND;
                break;
        }
        int rang = 0;
        calendar.setTime(inputDate);
        calendar.add(inc, -before * pas);
        for (int i = 0; i < before + after; i++) {
            calendar.add(inc, pas);
            result.put(rang++, sdfo.format(calendar.getTime()));
        }
        return result;
    }

    /**
     * Transforms a date in the 'DD/MM/YYYY' format in the database format:
     * YYYYMMDD: no hours, minutes ou seconds specified.
     *
     * @param date String
     * @return String
     */
    public static String formatStdtoDBDateStrict(String date) {
        return date.substring(6, 10) +
                date.substring(3, 5) +
                date.substring(0, 2);
    }

    /**
     * Returns the date and time of the given calendar to the format of the SQL
     * Server database (to fit in a 'datetime' column).
     *
     * @param calendar Calendar
     * @return String
     */
    public static String convertCalendarToSQLServerDate(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

        return dateFormat.format(calendar.getTime());
    }

    /**
     * intervalle : donne le nombre de component (ann�e, mois, jour, heure...)
     * entre deux dates donn�es sous forme de string
     *
     * @param sDate String
     * @param before int
     * @param after int
     * @param component char
     * @return int
     */
    public static float intervalle(String sBeginDate, String sFormatBeg, String sEndDate, String sFormatEnd,
            char component) {
        GregorianCalendar beginCalendar = new GregorianCalendar();
        GregorianCalendar endCalendar = new GregorianCalendar();
        SimpleDateFormat sdfBeg = new SimpleDateFormat(sFormatBeg);
        SimpleDateFormat sdfEnd = new SimpleDateFormat(sFormatEnd);
        Date beginDate = null, endDate = null;
        try {
            beginDate = sdfBeg.parse(sBeginDate);
            endDate = sdfEnd.parse(sEndDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
            return 0;
        }
        float inc = 0, an_jj = 365, jj_hh = 24, an_mm = 12, mm_jj =
                an_jj / an_mm, se_jj = 7, hh_ss = 3600, mn_ss = 60, ss_ms = 1000;
        switch (component) {
            case 'y':
                inc = an_jj * jj_hh * hh_ss * ss_ms;
                break;
            case 'M':
                inc = mm_jj * jj_hh * hh_ss * ss_ms;
                break;
            case 'w':
                inc = se_jj * jj_hh * hh_ss * ss_ms;
                break;
            case 'W':
                inc = se_jj * jj_hh * hh_ss * ss_ms;
                break;
            case 'D':
                inc = jj_hh * hh_ss * ss_ms;
                break;
            case 'd':
                inc = jj_hh * hh_ss * ss_ms;
                break;
            case 'H':
                inc = hh_ss * ss_ms;
                break;
            case 'm':
                inc = mn_ss * ss_ms;
                break;
            case 's':
                inc = ss_ms;
                break;
            case 'S':
                inc = 1;
                break;
        }

        beginCalendar.setTime(beginDate);
        endCalendar.setTime(endDate);
        float intervalleInMillis = endCalendar.getTimeInMillis() - beginCalendar.getTimeInMillis();
        float intervalle = intervalleInMillis / inc;
        return intervalle;
    }

    public static float intervalle(Date beginDate, Date endDate, char component) {
        GregorianCalendar beginCalendar = new GregorianCalendar();
        GregorianCalendar endCalendar = new GregorianCalendar();

        float inc = 0, an_jj = 365, jj_hh = 24, an_mm = 12, mm_jj =
                an_jj / an_mm, se_jj = 7, hh_ss = 3600, mn_ss = 60, ss_ms = 1000;
        switch (component) {
            case 'y':
                inc = an_jj * jj_hh * hh_ss * ss_ms;
                break;
            case 'M':
                inc = mm_jj * jj_hh * hh_ss * ss_ms;
                break;
            case 'w':
                inc = se_jj * jj_hh * hh_ss * ss_ms;
                break;
            case 'W':
                inc = se_jj * jj_hh * hh_ss * ss_ms;
                break;
            case 'D':
                inc = jj_hh * hh_ss * ss_ms;
                break;
            case 'd':
                inc = jj_hh * hh_ss * ss_ms;
                break;
            case 'H':
                inc = hh_ss * ss_ms;
                break;
            case 'm':
                inc = mn_ss * ss_ms;
                break;
            case 's':
                inc = ss_ms;
                break;
            case 'S':
                inc = 1;
                break;
        }

        beginCalendar.setTime(beginDate);
        endCalendar.setTime(endDate);
        float intervalleInMillis = endCalendar.getTimeInMillis() - beginCalendar.getTimeInMillis();
        float intervalle = intervalleInMillis / inc;
        return intervalle;
    }

    public static int intervalleInt(String sBeginDate, String sFormatBeg, String sEndDate, String sFormatEnd,
            char component) {
        return Integer.parseInt(
                new DecimalFormat("0").format(intervalle(sBeginDate, sFormatBeg, sEndDate, sFormatEnd, component)));
    }

    public static Integer intervalleInt(Date beginDate, Date endDate, char component) {
        return new Integer(Integer.parseInt(new DecimalFormat("0").format(intervalle(beginDate, endDate, component))));
    }

    /**
     * convert
     *
     * @param date Date
     * @param string String
     * @return String
     */
    public static String convert(Date date, String sFormat) {
        if (date == null || sFormat == null || "".equals(sFormat)) { return ""; }

        return new SimpleDateFormat(sFormat).format(date);
    }

    public static Date convert(String sdate, String iFormat) {
        if (sdate == null || "".equals(sdate) || "null".equals(sdate)) { return null; }
        SimpleDateFormat sdfi = new SimpleDateFormat(iFormat);

        //conversion de la chaine en date
        Date date = null;
        try {
            date = sdfi.parse(sdate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return date;

    }

    /**
     * Prend une date en format String en un format d'entr�e iFormat donn�
     * et la convertie en une chaine en un format de sortie oFormat
     * (cf tableau des param�tre d'un format date ci dessous)
     *
     * @param sDate String
     * @param iFormat String
     * @param oFormat String
     * @return String
     */

    public static String convert(String sdate, String iFormat, String oFormat) {
        if (sdate == null || "".equals(sdate)) { return ""; }
        SimpleDateFormat sdfi = new SimpleDateFormat(iFormat);
        SimpleDateFormat sdfo = new SimpleDateFormat(oFormat);
        //conversion de la chaine en date
        Date date = null;
        try {
            date = sdfi.parse(sdate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        //return de la date en chaine dans format de sortie
        if (date != null) { return sdfo.format(date); } else { return ""; }
    }

    /**
     * convert
     * cherche la date � partir d'un tableau de format
     * renvoie la date trouv�e au format oFormat
     * sinon une chaine ifNoDate
     *
     * @param sDate String
     * @param iFormat String[]
     * @return String
     */
    public static String convert(String sDate, String[] iFormat, String oFormat, String ifNoDate) {
        if (sDate == null) { return ifNoDate; }
        DateFormat sdf;
        boolean isDate;
        for (int i = 0; i < iFormat.length; i++) {
            sdf = new SimpleDateFormat(iFormat[i]);
            Date date = null;
            isDate = true;
            try {
                date = sdf.parse(sDate);
            } catch (ParseException ex) {
                isDate = false;
            }
            if (isDate) {
                return new SimpleDateFormat(oFormat).format(date);
            }
        }
        return ifNoDate;
    }

    /**
     * getCritDate
     *
     * @param text String
     * @return String
     */
    public static String getCritDate(String text) {
        //1 on recherche s'il y a des op�rateur
        String[] opi = { ">", "<" };
        String[] sfx = { "000000", "235959" };
        String op = "=", sx = "000000";
        for (int i = 0; i < opi.length; i++) {
            if (text.startsWith(opi[i])) {
                op = opi[i] + "=";
                sx = sfx[i];
                text = text.substring(1);
                break;
            }
        }
        //2 on recherche si c'est une date
        String[] sf = { "dd/MM/yyyy" };
        String out = convert(text, sf, "yyyyMMdd", "99999999");
        if (!"99999999".equals(out) && "=".equals(op)) {
            return " BETWEEN '" + out + "000000' AND '" + out + "235959'";
        } else { return op + "'" + out + sx + "'"; }

    }

    public static Date getFmtDate(String text, String oFormat) {
        //formatsb support�s
        String[] sf = { "yyyyMMddHHmmSS", "yyyyMMdd", "dd/MM/yyyy", "dd.MM.yyyy", "dd-MM-yyyy" };
        String out = convert(text, sf, oFormat, "not_a_date");
        if (!"not_a_date".equals(out)) {
            return convert(out, oFormat);
        }
        return null;
    }

    /**
     * renvoi une s�rie de jour enyre deux dates
     *
     * @param beg
     * @param end
     * @return
     */
    public static ArrayList<Date> getSerieDay(Date beg, Date end) {
        ArrayList<Date> serie = new ArrayList<Date>();
        //initialisation
        Date d = beg;
        while (d.compareTo(end) <= 0) {
            serie.add(d);
            d = add(d, 1, 'd');
        }

        return serie;
    }

    public static TreeMap<Date, Date[]> getSerieMonth(Date beg, Date end) {
        HashMap<Date, Date[]> serie = new HashMap<Date, Date[]>();
        if (beg == null || end == null) { return new TreeMap<Date, Date[]>(serie); }

        GregorianCalendar cal = new GregorianCalendar();
        //initialisation
        Date be_ = beg, en_ = be_;
        boolean go = true, put;
        while (go) {
            cal.setTime(en_);
            put = false;
            //si on arrive � la fin on arrete
            if (end.compareTo(en_) == 0) {
                go = false;
                put = true;
                //si date=fin du mois ou fin de semaine
            } else if ((cal.get(Calendar.DAY_OF_MONTH) == cal.getActualMaximum(Calendar.DAY_OF_MONTH))) {
                put = true;
            }
            if (put) {
                //cl� = annee + mois
                serie.put(be_, new Date[] { be_, en_ });
                be_ = add(en_, 1, 'd');
                en_ = be_;
            } else {
                en_ = add(en_, 1, 'd');
            }
        }
        return new TreeMap<Date, Date[]>(serie);
    }

    /**
     * isBetween examine si une date est entre deux bornes dates
     *
     * @param date
     * @param dates
     * @return
     */

    public static boolean isBetween(Date date, Date[] dates) {
        if (date == null || dates == null || dates.length != 2) { return false; }
        return date.compareTo(dates[0]) >= 0 && date.compareTo(dates[1]) <= 0;
    }

    public static String convert(Calendar date, String sFormat) {
        if (date == null) { return ""; }
        return convert(date.getTime(), sFormat);
    }

    public static String longToString(Long date, String format) {
        if (date == null) { return null; }
        SimpleDateFormat df = new SimpleDateFormat(format);
        String newDate = df.format(date);
        return newDate;
    }

    public static String timeStampToString(Timestamp date, String format) {
        if (date == null) { return null; }
        DateFormat df = new SimpleDateFormat(format);
        String reportDate = df.format(date);
        return reportDate;
    }

    public static Date stringToDate(String date, String format) {
        if (date == null) { return null; }
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date newDate = null;
        try {
            //System.out.println(date);
            newDate = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }
}
