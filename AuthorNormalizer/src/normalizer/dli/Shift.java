package normalizer.dli;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shift {

    static LinkedHashMap<String, String[]> tokensMap = new LinkedHashMap<String, String[]>();
    Pattern pattern;
    Matcher matcher;

    public Shift(String configPath) throws Exception{
    	HashMap<String, String[]> unsortedMap = new HashMap<String, String[]>();
        CSVReader cr = new CSVReader(new FileReader(new File(configPath + File.separatorChar + "authshift.csv")));
        for (String[] row : cr.readAll())
        	unsortedMap.put(row[0], new String[] {row[1],row[2]});
        tokensMap = sort(unsortedMap);
        unsortedMap.clear();
        cr.close();
    }

    public String[] getAuthvalue(String textContent) {
    	String inputValue = textContent.trim();
        String[] response = new String[3];
        boolean shift = false, person = false;
        Set<String> mapField = new HashSet<String>();
        for (Map.Entry<String, String[]> tokens : tokensMap.entrySet()) {
        	String modtoken = tokens.getKey().replaceAll("([\\W&&\\S])", "\\\\$1");
        	String process = tokens.getValue()[1];
        	//System.out.println(modtoken);
//        	if (inputValue.matches("(?i).*(?<=[\\s,\\(\\[:_\\.\\|]|^)" + modtoken + "(?=[,\\s\\]\\):_\\.\\|]|$).*")) {
//                shift = true;
//                mapField += tokens.getValue()+";";
//                //inputValue = inputValue.replaceAll("(?i)(?<=[\\s,\\(\\[:]|^)" + modtoken + "(?=[,\\s\\]\\):]|$)", " ");
//                inputValue = inputValue.replaceAll("(?i)(^|[\\s,\\(\\[:_\\.\\|]+)" + modtoken + "([,\\s\\]\\):_\\.\\|]+|$)", " ");
//                System.out.println("matchblock: " + mapField + inputValue+modtoken);
//            }
        	pattern = Pattern.compile("(?i).*((?:\\W+|^)" + modtoken + "(?:\\W+|$)).*");
        	matcher = pattern.matcher(inputValue);
        	if (matcher.find()) {
                shift = true;
                mapField.add(tokens.getValue()[0]);
                if(process.equalsIgnoreCase("removeToken")) {
            	String tokenSegment = matcher.group(1);
            	String updateSegment = tokenSegment.replaceAll("(?i)" + modtoken, "");
            	//System.out.println(tokenSegment + "--" + updateSegment);
                inputValue = inputValue.replace(tokenSegment, updateSegment);
                person = true;
                } else
                	inputValue = textContent.trim();
                //System.out.println("matchblock: " + mapField + inputValue+ ":" + modtoken);
            }
        }
        if (!shift) {
            response[0] = "default";
            response[1] = inputValue;
            response[2] = "person";
        } else{
        	String fieldString = "";
        	for(String eachField : mapField)
        		fieldString += eachField + ";";
        	response[0] = fieldString.replaceAll(";$", "");
        	response[1] = inputValue;
        	if(person)
            	response[2] = "person";
            else
            	response[2] = "other";
        }
        return response;
    }
    
    LinkedHashMap<String, String[]> sort(HashMap<String, String[]> in) {
		//System.out.println(in);
		LinkedHashMap<String, String[]> out = new LinkedHashMap<String, String[]>();
		List<Entry<String, String[]>> unsortedList = new ArrayList<Entry<String, String[]>>(in.entrySet());
        
        Collections.sort(unsortedList,new Comparator<Entry<String, String[]>>()
                {
                    @Override
                    public int compare(Entry<String, String[]> e1, Entry<String, String[]> e2)
                    {
                    	int e1_length = e1.getKey().length();
                    	int e2_length = e2.getKey().length();
                    	if(e1_length>e2_length)
                    		return -1;
                    	else
                    		return 1;
                    }
                }
                );
        
        for(Entry<String, String[]> entry:unsortedList){
        	out.put(entry.getKey(),entry.getValue());
        }
		//System.out.println(out);
        return out;
	}
}
