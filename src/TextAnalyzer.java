import com.ibm.icu.text.Transliterator;
import java.util.Arrays;
import java.util.regex.Pattern;

public class TextAnalyzer {
    private static String formatString(String nonMachineWord, Boolean isRussian){
        Transliterator toLatin = Transliterator.getInstance("Cyrillic-Latin");
        String machineWord = nonMachineWord.toLowerCase().trim().replaceAll("[\\s\\n]+", " ").replaceAll("[^a-zA-Zа-яА-Я\\s\\n\\d]", "");
        if(isRussian){
            return machineWord;
        } else{
            return toLatin.transliterate(machineWord);
        }
    }
    public static Integer getNumOfEntries(String string, String word){
        int counter = 0;
        if(word.split(" ").length == 1){
            String[] stringArray = TextAnalyzer.formatString(string, word.matches("[а-яА-Я]+")).split(" ");
            for(String s : stringArray){
                if(word.toLowerCase().matches("[а-яА-Я]+")){
                    if(word.length() <= 3) {
                        if(Pattern.compile("(\\w*)"+word.toLowerCase()+"(\\w*)").matcher(s).find()) counter++;
                    } else{
                        if(Pattern.compile((word.substring(0, word.length()-1).toLowerCase())+"(\\w*)").matcher(s).find()) counter++;
                    }
                }else if(word.toLowerCase().matches("\\d+")){
                    if(Pattern.compile("(\\.*)"+word.toLowerCase()+"(\\.*)").matcher(s).find()) counter++;
                }else{
                    if(s.matches(word.toLowerCase())) counter++;
                }
            }
        } else{
            String[] wordArray = word.split(" ");
            String[] stringArray = TextAnalyzer.formatString(string, false).split(" ");
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
