import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.concurrent.Task;

public class AnswerWorker extends Task<Map<String, Integer>> {
    public AnswerWorker( String path, String extension, String word){
        this.path = path;
        this.extension = extension;
        this.word = word;
    }
    @Override
    protected Map<String, Integer> call() throws Exception {
        map.clear();
        isSearched = false;
        if(new File(GUI.DIR_TO_SAVE).list().length != 0){
            for(File f : new File(GUI.DIR_TO_SAVE).listFiles()){
                f.delete();
            }
        }
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
            if(filesList.size()%3 == 0){
                multithreading(filesList, 0);
            }else if(filesList.size() == 1){
                setList(filesList, this.word);
            }else{
                if(filesList.size()+1 %3 == 0){
                    multithreading(filesList, 1);
                }else{
                    multithreading(filesList, -1);
                }
            }
            isSearched = true;
        }
    }
    private void extensionDef(File f, String word){
        String ext = new String(f.getName().substring(f.getName().lastIndexOf(".")+1));
        switch (ext) {
            case "pptx":
                map.put(f.getPath(), Parser.parsePPTX(f, word));
                break;
            case "docx":
                map.put(f.getPath(), Parser.parseDOCX(f, word));
                break;
            case "txt":
                map.put(f.getPath(), Parser.parseTXT(f, word));
                break;
            case "xlsx":
                map.put(f.getPath(), Parser.parseXLSX(f, word));
                break;
            default:
                break;
        }
    }
    private void getDirList(String dir, String extension, List<File> filesList){
        File f = new File(dir);
        String[] dirList = f.list();
        if (!isCancelled()) {
            if (extension.equals("All files")) {
                for (String s : dirList) {
                    String path = dir + File.separator + s;
                    File f1 = new File(path);
                    if (f1.isFile()) {
                        if (f1.getName().endsWith("pptx") | f1.getName().endsWith("docx") | f1.getName().endsWith("txt") | f1.getName().endsWith("xlsx")) {
                            filesList.add(f1);
                        }
                    } else {
                        getDirList(path, extension, filesList);
                    }
                }
            } else {
                for (String s : dirList) {
                    String path = dir + File.separator + s;
                    File f1 = new File(path);
                    if (f1.isFile()) {
                        if (f1.getName().endsWith(extension)) {
                            filesList.add(f1);
                        }
                    } else {
                        getDirList(path, extension, filesList);
                    }
                }
            }
        }
    }
    private void setList(List<File> fl, String word){
        for(File f : fl){
            try{
                extensionDef(f, word);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    private void downloadUsingNIO(String urlStr, String file) throws Exception {
        if(Files.exists(Paths.get(GUI.DIR_TO_SAVE))){
            URL url = new URL(urlStr);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(GUI.DIR_TO_SAVE + file);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
        }else {
            throw new Exception("Set a download directory in settings");
        }
    }
    private void multithreading(List<File> filesList, Integer i){
        Thread t1 =  new Thread(() -> {
            setList(filesList.subList(0, (filesList.size()+i)/3), this.word);
        });
        t1.start();
        Thread t2 =  new Thread(() -> {
            setList(filesList.subList((filesList.size()+i)/3, ((filesList.size()+i)/3)*2), this.word);
        });
        t2.start();
        Thread t3 =  new Thread(() -> {
            setList(filesList.subList(((filesList.size()+i)/3)*2, filesList.size()), this.word);
        });
        t3.start();
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private Map<String, Integer> map = new HashMap<String, Integer>();
    private String word = "";
    private String path = "";
    private String extension = "";
    private boolean isSearched = false;
}
