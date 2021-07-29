package ru.saiushev.sps.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.concurrent.Task;

public class AnswerTask extends Task<Map<String, Integer>> {
    private Map<String, Integer> map = new HashMap<String, Integer>();
    private String word;
    private String path;
    private String extension;
    private boolean isSearched = false;

    public AnswerTask(String path, String extension, String word){
        this.path = path;
        this.extension = extension;
        this.word = word;
    }
    @Override
    protected Map<String, Integer> call() throws Exception {
        map.clear();
        isSearched = false;
        searchOnLocalDrive();
        return map;
    }
    private void searchOnLocalDrive() throws Exception{
        if(!isSearched){
            List<File> filesList = new ArrayList<File>();

            if(Files.exists(Paths.get(this.path)) && Files.isReadable(Paths.get(this.path))) {
                if(new File(this.path).listFiles().length != 0) {
                    getDirList(this.path, this.extension, filesList);
                }else{
                    throw new Exception("Directory is empty!");
                }
            }else{
                throw new Exception("Directory does not exist!");
            }

            if(filesList.size() == 1){
                setList(filesList);
            }else if(filesList.size() % 3 == 0){
                multithreading(filesList, 0);
            }else{
                if((filesList.size()+1) % 3 == 0){
                    multithreading(filesList,1);
                }else{
                    multithreading(filesList,-1);
                }
            }
            isSearched = true;
        }
    }
    private void extensionDef(File f){
        switch(f.getName().substring(f.getName().lastIndexOf(".") + 1)){
            case "pptx":
                map.put(f.getPath(), DocumentParser.parsePPTX(f, word));
                break;
            case "docx":
                map.put(f.getPath(), DocumentParser.parseDOCX(f, word));
                break;
            case "txt":
                map.put(f.getPath(), DocumentParser.parseTXT(f, word));
                break;
            case "xlsx":
                map.put(f.getPath(), DocumentParser.parseXLSX(f, word));
                break;
            default:
                break;
        }
    }
    private void getDirList(String dir, String extension, List<File> filesList){
        String[] dirList = new File(dir).list();

        if(!isCancelled()){
            assert dirList != null;
            for(String s : dirList){
                String path = dir + File.separator + s;
                File f1 = new File(path);
                if(f1.isFile()){
                    if(extension.equals("All files")){
                        if(f1.getName().endsWith("pptx") | f1.getName().endsWith("docx") | f1.getName().endsWith("txt") | f1.getName().endsWith("xlsx")){
                            filesList.add(f1);
                        }
                    }else{
                        if(f1.getName().endsWith(extension)){
                            filesList.add(f1);
                        }
                    }
                }else{
                    getDirList(path, extension, filesList);
                }
            }
        }
    }
    private void setList(List<File> fl){
        for(File f : fl){
            try{
                extensionDef(f);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    private void multithreading(List<File> filesList, Integer i){
        Thread t1 =  new Thread(() -> setList(filesList.subList(0, (filesList.size()+i)/3)));
        t1.start();
        Thread t2 =  new Thread(() -> setList(filesList.subList((filesList.size()+i)/3, ((filesList.size()+i)/3)*2)));
        t2.start();
        Thread t3 =  new Thread(() -> setList(filesList.subList(((filesList.size()+i)/3)*2, filesList.size())));
        t3.start();
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
