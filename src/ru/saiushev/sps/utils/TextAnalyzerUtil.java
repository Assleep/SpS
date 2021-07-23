package ru.saiushev.sps.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.regex.Pattern;

public class TextAnalyzerUtil {
    public static Integer getNumOfEntries(String string, @NotNull String word){
        int counter = 0;
        String suffixOrPostfix = word.toLowerCase().matches("\\d+") ? "(\\.*)" : "(\\w*)";
        String[] stringArray = StringFormatterUtil.formatString(string, word.matches("[а-яА-Я]+")).split(" ");
        if(word.split(" ").length == 1){
            for(String s : stringArray){
                if(Pattern.compile(suffixOrPostfix+word.toLowerCase()+suffixOrPostfix).matcher(s).find()) counter++;
            }
        }else{
            String[] wordArray = word.trim().replaceAll("[\\s\\n]+"," ").split(" ");
            for(int i = 0; i < stringArray.length; i++){
                int k = 0;
                if(Pattern.compile(wordArray[k].toLowerCase()).matcher(stringArray[i]).find()){
                    for(String sb : Arrays.copyOfRange(stringArray, i+1, i+wordArray.length)){
                        k++;
                        if(Pattern.compile(wordArray[k].toLowerCase()).matcher(sb).find()){
                            if(k == wordArray.length-1) counter++;
                        }else{
                            break;
                        }
                    }
                }
            }
        }
        return counter;
    }

}
