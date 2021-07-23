package test;

import org.junit.Assert;
import org.junit.Test;
import ru.saiushev.sps.utils.TextAnalyzerUtil;

public class TextAnalyzerUtilUtilTest {
    @Test
    public void testGetNumOfEntries(){
        Assert.assertEquals( 2, (long) TextAnalyzerUtil.getNumOfEntries("Simple example text for example", "example"));
        Assert.assertEquals( 2, (long) TextAnalyzerUtil.getNumOfEntries("Simple exam with another text for example", "exam"));
        Assert.assertEquals( 2, (long) TextAnalyzerUtil.getNumOfEntries("Simple 12 exam with another text for example 2", "2"));
        Assert.assertEquals( 1, (long) TextAnalyzerUtil.getNumOfEntries("Simple 12 exam with another text for example 2", "exam with another text"));
        Assert.assertEquals( 2, (long) TextAnalyzerUtil.getNumOfEntries("Simple 12 exam with another text for example 2 and another exam with   another text", "exam      with   another\n text"));
    }
}
