package test;

import org.junit.Assert;
import org.junit.Test;
import ru.saiushev.sps.utils.DocumentParser;

import java.io.File;

public class DocumentParserTest {
    private static File docx;
    private static File pptx;
    private static File xlsx;
    private static File txt;

    public DocumentParserTest(){
        docx = new File("");
        pptx = new File("");
        xlsx = new File("");
        txt = new File("");
    }

    @Test
    public void testParseDOCX() {
        Assert.assertEquals(10,(long) DocumentParser.parseDOCX(docx, "docx"));
    }
    @Test
    public void testParsePPTX() {
        Assert.assertEquals(3,(long) DocumentParser.parsePPTX(pptx, "pptx"));
    }
    @Test
    public void testParseXSLX() {
        Assert.assertEquals(8,(long) DocumentParser.parseXLSX(xlsx, "xlsx"));
    }
    @Test
    public void testParseTXT() {
        Assert.assertEquals(1,(long) DocumentParser.parseTXT(txt, "txt"));
    }
}
