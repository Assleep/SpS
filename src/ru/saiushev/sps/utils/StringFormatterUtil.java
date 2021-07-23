package ru.saiushev.sps.utils;

import com.ibm.icu.text.Transliterator;
import java.util.regex.Pattern;

public class StringFormatterUtil {
    public static String formatString(String nonMachineWord, Boolean isRussian){
        Transliterator toLatin = Transliterator.getInstance("Cyrillic-Latin");
        String machineWord = nonMachineWord.toLowerCase().trim().replaceAll("[\\s\\n]+|[^a-zA-Zа-яА-Я\\d]+", " ");
        if(isRussian){
            return machineWord;
        } else{
            return toLatin.transliterate(machineWord);
        }
    }
}
