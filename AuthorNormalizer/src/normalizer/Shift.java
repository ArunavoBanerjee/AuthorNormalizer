package normalizer;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Shift {

    static HashMap<String, String> tokensMap = new HashMap<String, String>();

    public Shift(String paths) throws Exception{
        CSVReader cr = new CSVReader(new FileReader(new File(paths)));
        for (String[] row : cr.readAll())
            tokensMap.put(row[0], row[1]);
        cr.close();
    }

    /*public void AuthShift(String path) throws Exception {

        CSVReader cr = new CSVReader(new FileReader(new File(path)));
        for (String[] row : cr.readAll())
            tokensMap.put(row[0], row[1]);
        cr.close();
    }*/

    public String[] getAuthvalue(String inputValue) {
        String[] response = new String[2];
        boolean shift = false;
        String mapField = "";
        for (Map.Entry<String, String> tokens : tokensMap.entrySet()) {
        	String modtoken = tokens.getKey().replaceAll("([\\W&&\\S])", "\\\\$1");
        	//System.out.println(modtoken);
        	if (inputValue.matches("(?i).*(?<=[\\s,\\(\\[:]|^)" + modtoken + "(?=[,\\s\\]\\):]|$).*")) {
                shift = true;
                mapField += tokens.getValue()+";";
                inputValue = inputValue.replaceAll("(?i)(?<=[\\s,\\(\\[:]|^)" + modtoken + "(?=[,\\s\\]\\):]|$)", " ");
                //System.out.println(mapField + inputValue+modtoken);
            }
        }
        if (!shift) {
            response[0] = "dc.contributor.author";
            response[1] = inputValue;
        } else {
        	response[0] = mapField.replaceAll(";$", "");
            response[1] = inputValue;
        }
        return response;
    }
}
