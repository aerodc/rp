
import utils.TimeUtil;
import org.junit.Test;
import org.junit.Assert;
import java.util.*;


public class TestDate {

    @Test
    public void testDate() {

        String sDate = "2015-03-05";
        String format = "yyyy-MM-dd";

		Date date = TimeUtil.stringToDate(sDate, format);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		Assert.assertEquals(5, cal.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(2015, cal.get(Calendar.YEAR));
		Assert.assertEquals(3, cal.get(Calendar.MONTH)+1);
    }
}
