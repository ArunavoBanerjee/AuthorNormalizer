package normalizer.dli;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.opencsv.CSVReader;

public class Cleanse {
	
	LinkedHashMap<String, String> containsTokensMap = new LinkedHashMap<String, String>();
	Set<String> authRemove = new HashSet<String>();

	
	public Cleanse(String configPath) throws Exception {
		
		HashMap<String, String> unsortedMap = new HashMap<String, String>();
		/*CSVReader crauthswTokens = new CSVReader(
				new FileReader(configPath + "/startsWithTokens.csv"), '|','"');
		for (String[] row : crauthswTokens.readAll()) {		
			//System.out.println(row[0].toLowerCase() + row[1].trim());
			unsortedMap.put(row[0].trim(),new ArrayList<String>(Arrays.asList(new String[] {row[1],row[2],row[3]})));
		}
		startsWithTokensMap = sort(unsortedMap);
		unsortedMap.clear();
		crauthswTokens.close();
		
		CSVReader crauthewTokens = new CSVReader(
				new FileReader(configPath + "/endsWithTokens.csv"), '|','"');
		for (String[] row : crauthewTokens.readAll()) {		
			//System.out.println(row[0].toLowerCase() + row[1].trim());
			unsortedMap.put(row[0].trim(),new ArrayList<String>(Arrays.asList(new String[] {row[1],row[2],row[3]})));
		}
		endsWithTokensMap = sort(unsortedMap);
		unsortedMap.clear();
		crauthewTokens.close();
		*/

		CSVReader crauthcnTokens = new CSVReader(
				new FileReader(configPath + "/containsTokens.csv"));
		for (String[] row : crauthcnTokens.readAll()) {
			unsortedMap.put(row[0].trim(),row[1]);
		}
		containsTokensMap = sort(unsortedMap);
		unsortedMap.clear();
		crauthcnTokens.close();
	/*	
		CSVReader crremauthTokens = new CSVReader(
				new FileReader(configPath + "/authRemove.csv"), '|','"');
		for (String[] row : crremauthTokens.readAll()) {		
			//System.out.println(row[0].toLowerCase() + row[1].trim());
			//authRemove.add(row[0].trim());
		}
		crremauthTokens.close();
*/
		
	}

	String cleanParts(String inputValue) {

		for (String remAuthtokens : authRemove) {
			remAuthtokens = remAuthtokens.replaceAll("([\\W&&\\S])", "\\\\$1");
			//System.out.println(remAuthtokens + ":" + inputValue);
			if (inputValue.matches("(?i).*(^|\\s)" + remAuthtokens + "(\\s|$).*")) {
				return "";
			}
		}
		
			String updAuth = inputValue;
			
//				boolean startMatch = false;
//				for (Map.Entry<String, ArrayList<String>> tokenAction : startsWithTokensMap.entrySet()) {
//					String token = tokenAction.getKey();
//					if(tokenAction.getValue().get(0).equalsIgnoreCase("str"))
//						token = token.replaceAll("([\\W&&\\S])", "\\\\$1");
//					if (updAuth.matches("(?i)^" + token + "(\\s.*|$)")) {
//						if (tokenAction.getValue().get(1).equals("removeToken")) {
//							// startMatch = true;
//							if(!tokenAction.getValue().get(2).isEmpty())
//								mapField = tokenAction.getValue().get(2);
//							updAuth = updAuth.replaceAll("(?i)(?:^|\\s)" + token + "(\\s|$)", " ").trim();
//						} else if (tokenAction.getValue().get(1).equals("remove"))
//							return response;
//					}
//				}
				// String value to pass through full curation cycle instead direct return.
				/*
				 * if(startMatch) { response.add(new String[] { mapField, updAuth }); return
				 * response; }
				 */

//				boolean endMatch = false;
//				for (Map.Entry<String, ArrayList<String>> tokenAction : endsWithTokensMap.entrySet()) {
//					String token = tokenAction.getKey();
//					if(tokenAction.getValue().get(0).equalsIgnoreCase("str"))
//						token = token.replaceAll("([\\W&&\\S])", "\\\\$1");
//					if (updAuth.matches("(?i)(^|.*\\s)" + token + "$")) {
//						if (tokenAction.getValue().get(1).equals("move")) {
//							mapField = tokenAction.getValue().get(2);
//							response.add(new String[] { mapField, inputValue });
//							return response;
//						} else if (tokenAction.getValue().get(1).equals("removeToken")) {
//							// endMatch = true;
//							if(!tokenAction.getValue().get(2).isEmpty())
//								mapField = tokenAction.getValue().get(2);
//							updAuth = updAuth.replaceAll("(?i)(^|\\s)" + token + "(?:\\s|$)", " ").trim();
//						} else if (tokenAction.getValue().get(1).equals("remove"))
//							return response;
//					}
//				}
				// String value to pass through full curation cycle instead direct return.
				/*
				 * if(endMatch) { response.add(new String[] { mapField, updAuth }); return
				 * response; }
				 */
				for (Map.Entry<String, String> tokenAction : containsTokensMap.entrySet()) {
					String token = tokenAction.getKey();
					//System.out.println(token);
					if(tokenAction.getValue().equalsIgnoreCase("str")) {
						token = token.replaceAll("([\\W&&\\S])", "\\\\$1");
						token = "(?:^|[\\W_])" + token + "(?:[\\W_]|$)";
					}
					 //System.out.println(inputValue + " : " + token);
					if (updAuth.matches("(?i).*" + token + ".*")) {
						 //System.out.println(inputValue + " : " + token);
							updAuth = updAuth.replaceAll("(?i)"+token, " ");
					}
				}
				return updAuth;
	}
	
	LinkedHashMap<String, String> sort(HashMap<String, String> in) {
		//System.out.println(in);
		LinkedHashMap<String, String> out = new LinkedHashMap<String, String>();
		List<Entry<String, String>> unsortedList = new ArrayList<Entry<String, String>>(in.entrySet());
        
        Collections.sort(unsortedList,new Comparator<Entry<String, String>>()
                {
                    @Override
                    public int compare(Entry<String, String> e1, Entry<String, String> e2)
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
        
        for(Entry<String, String> entry:unsortedList){
        	out.put(entry.getKey(),entry.getValue());
        }
		//System.out.println(out);
        return out;
	}
	
}
