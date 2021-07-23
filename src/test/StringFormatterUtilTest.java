package test;

import org.junit.Assert;
import org.junit.Test;
import ru.saiushev.sps.utils.StringFormatterUtil;

public class StringFormatterUtilTest {
    @Test
    public void testFormatString() {
        Assert.assertEquals("", StringFormatterUtil.formatString("", false));
        Assert.assertEquals("123 456 abc d", StringFormatterUtil.formatString("123      456.abc?d", false));
    }

}
