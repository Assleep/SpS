import com.ibm.icu.text.Transliterator;

import java.util.regex.Pattern;

public class StringEditor {
    private static String formatString(String nonMachineWord, Boolean isRussian){
        Transliterator toLatin = Transliterator.getInstance("Cyrillic-Latin");
        String machineWord = nonMachineWord.toLowerCase().trim().replaceAll("[\\s\\n]+", " ").replaceAll("[^a-zA-Zа-яА-Я\\s\\n\\d]", "");
        if(isRussian){
            return machineWord;
        } else{
            return toLatin.transliterate(machineWord);
        }
    }
    public static Integer getNumOfEntryes(String string, String word){
        int counter = 0;
        String[] stringArray = StringEditor.formatString(string, word.matches("[а-яА-Я]+")).split(" ");
        for(String s : stringArray){
            if(word.toLowerCase().matches("[а-яА-Я]+")){
                if(word.length() <= 3) {
                    if(Pattern.compile("(\\w*)"+word.toLowerCase()+"(\\w*)").matcher(s).find()) counter++;
                } else{
                    if(Pattern.compile(word.substring(0, word.length()-1).toLowerCase()+"(\\w*)").matcher(s).find()) counter++;
                }
            }else if(word.toLowerCase().matches("\\d+")){
                if(Pattern.compile("(\\.*)"+word.toLowerCase()+"(\\.*)").matcher(s).find()) counter++;
            }else{
                if(s.matches(word.toLowerCase())) counter++;
            }
        }
        return counter;
    }
}
