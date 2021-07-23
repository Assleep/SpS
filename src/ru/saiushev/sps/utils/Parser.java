package ru.saiushev.sps.utils;

import java.io.*;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.sl.extractor.SlideShowExtractor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xslf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFComment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class Parser {
    public static Integer parseDOCX(File file, String word){
        try{
            XWPFDocument docx = new XWPFDocument(new FileInputStream(file));
            XWPFWordExtractor wordExtractor = new XWPFWordExtractor(docx);
            StringBuilder allTextInDocument = new StringBuilder();
            allTextInDocument.append(wordExtractor.getText()+" ");
            for(XWPFComment c : docx.getComments()){
                allTextInDocument.append(c.getText()+" ");
            }
            allTextInDocument.append(wordExtractor.getMetadataTextExtractor().getText()+" ");
            allTextInDocument.append(docx.getProperties().getExtendedProperties().getNotes());
            return TextAnalyzerUtil.getNumOfEntries(allTextInDocument.toString(), word);
        }catch(IOException e){
            e.printStackTrace();
        }
        return 0;
    }
    public static Integer parseXLSX(File file, String word){
        try{
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));
            StringBuilder allTextInXLSX = new StringBuilder();
            for(int i = 0; i < wb.getNumberOfSheets(); i++){
                Sheet sheet = wb.getSheetAt(i);
                allTextInXLSX.append(sheet.getSheetName());
                Iterator<Row> it = sheet.iterator();
                while(it.hasNext()){
                    Row row = it.next();
                    Iterator<Cell> cells = row.cellIterator();
                    while(cells.hasNext()){
                        Cell cell = cells.next();
                        CellType cellType = cell.getCellType();
                        switch(cellType){
                            case NUMERIC:
                                allTextInXLSX.append(cell.getNumericCellValue()+" ");
                                break;
                            case STRING:
                                allTextInXLSX.append(cell.getStringCellValue()+" ");
                                break;
                            case FORMULA:
                                allTextInXLSX.append(cell.getCellFormula()+" ");
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            return TextAnalyzerUtil.getNumOfEntries(allTextInXLSX.toString(), word);
        }catch(IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static Integer parseTXT(File file, String word){
        try {
            StringBuilder allTextInTxt = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader((new FileInputStream(file))));
            reader.lines().forEach(x-> allTextInTxt.append(x));
            reader.close();
            return TextAnalyzerUtil.getNumOfEntries(allTextInTxt.toString(), word);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static Integer parsePPTX(File file, String word) {
        try {
            XMLSlideShow pptx = new XMLSlideShow(new FileInputStream(file));
            SlideShowExtractor<XSLFShape, XSLFTextParagraph> slideShowExtractor = new SlideShowExtractor<XSLFShape, XSLFTextParagraph>(pptx);
            StringBuilder allTextInSlide = new StringBuilder();

            slideShowExtractor.setCommentsByDefault(true);
            slideShowExtractor.setMasterByDefault(true);
            slideShowExtractor.setNotesByDefault(true);
            slideShowExtractor.setSlidesByDefault(true);

            for (XSLFSlide slide : pptx.getSlides()) {
                for (POIXMLDocumentPart part : slide.getRelations()) {
                    if (part.getPackagePart().getPartName().getName().startsWith("/ppt/diagrams/data")) {
                        XmlCursor cursor = XmlObject.Factory.parse(part.getPackagePart().getInputStream()).newCursor();
                        while (cursor.hasNextToken()) {
                            if (cursor.isText()) {
                                allTextInSlide.append(cursor.getTextValue() + "\r\n");
                            }
                            cursor.toNextToken();
                        }
                    }
                }
            }

            String allTextContentInSlideShow = slideShowExtractor.getText();
            POITextExtractor textExtractor = slideShowExtractor.getMetadataTextExtractor();
            String metaData = textExtractor.getText();

            allTextInSlide.append(metaData+" ");
            allTextInSlide.append(allTextContentInSlideShow);
            return TextAnalyzerUtil.getNumOfEntries(allTextInSlide.toString(), word);
        } catch (IOException | XmlException e) {
            e.printStackTrace();
        }
        return 0;
    }
    // In early development
    public static Integer parsePPT(File file, String word){
        try {
            XMLSlideShow ppt = new XMLSlideShow(new FileInputStream(file));
            SlideShowExtractor<XSLFShape, XSLFTextParagraph> slideShowExtractor = new SlideShowExtractor<XSLFShape, XSLFTextParagraph>(ppt);
            StringBuilder allTextInSlide = new StringBuilder("");

            slideShowExtractor.setCommentsByDefault(true);
            slideShowExtractor.setMasterByDefault(true);
            slideShowExtractor.setNotesByDefault(true);
            slideShowExtractor.setSlidesByDefault(true);

            StringBuilder sb = new StringBuilder();
            for (XSLFSlide slide : ppt.getSlides()) {
                for (POIXMLDocumentPart part : slide.getRelations()) {
                    if (part.getPackagePart().getPartName().getName().startsWith("/ppt/diagrams/data")) {
                        XmlCursor cursor = XmlObject.Factory.parse(part.getPackagePart().getInputStream()).newCursor();
                        while (cursor.hasNextToken()) {
                            if (cursor.isText()) {
                                sb.append(cursor.getTextValue() + "\r\n");
                            }
                            cursor.toNextToken();
                        }
                    }
                }
            }

            String allTextContentInSlideShow = slideShowExtractor.getText();
            POITextExtractor textExtractor = slideShowExtractor.getMetadataTextExtractor();
            String metaData = textExtractor.getText();

            String allTextContentInDiagrams = sb.toString();
            allTextInSlide.append(allTextContentInDiagrams+" ");
            allTextInSlide.append(metaData+" ");
            allTextInSlide.append(allTextContentInSlideShow);
            return TextAnalyzerUtil.getNumOfEntries(allTextInSlide.toString(), word);
        } catch (IOException | XmlException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static Integer parseXLS(File file, String word){
        try {
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
            StringBuilder allTextInXLS = new StringBuilder();

            for(int i = 0; i < wb.getNumberOfSheets(); i++){
                Sheet sheet = wb.getSheetAt(i);
                allTextInXLS.append(sheet.getSheetName());
                Iterator<Row> it = sheet.iterator();
                while(it.hasNext()){
                    Row row = it.next();
                    Iterator<Cell> cells = row.cellIterator();
                    while(cells.hasNext()){
                        Cell cell = cells.next();
                        CellType cellType = cell.getCellType();
                        switch(cellType){
                            case NUMERIC:
                                allTextInXLS.append(cell.getNumericCellValue()+" ");
                                break;
                            case STRING:
                                allTextInXLS.append(cell.getStringCellValue()+" ");
                                break;
                            case FORMULA:
                                allTextInXLS.append(cell.getCellFormula()+" ");
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            return TextAnalyzerUtil.getNumOfEntries(allTextInXLS.toString(), word);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

