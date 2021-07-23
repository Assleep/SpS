package test;

import org.junit.Assert;
import org.junit.Test;
import ru.saiushev.sps.utils.Parser;

import java.io.File;

public class ParserUtilTest {
    private static File docx;
    private static File pptx;
    private static File xlsx;
    private static File txt;

    public ParserUtilTest(){
        docx = new File("");
        pptx = new File("");
        xlsx = new File("");
        txt = new File("");
    }

    @Test
    public void testParseDOCX() {
        Assert.assertEquals(10,(long) Parser.parseDOCX(docx, "docx"));
    }
    @Test
    public void testParsePPTX() {
        Assert.assertEquals(3,(long) Parser.parsePPTX(pptx, "pptx"));
    }
    @Test
    public void testParseXSLX() {
        Assert.assertEquals(8,(long) Parser.parseXLSX(xlsx, "xlsx"));
    }
    @Test
    public void testParseTXT() {
        Assert.assertEquals(1,(long) Parser.parseTXT(txt, "txt"));
    }
}
