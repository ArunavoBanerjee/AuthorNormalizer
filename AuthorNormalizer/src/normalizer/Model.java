package normalizer;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.opencsv.CSVReader;

public class Model {

	HashMap<String, ArrayList<String>> startsWithTokensMap = new HashMap<String, ArrayList<String>>();
	HashMap<String, ArrayList<String>> endsWithTokensMap = new HashMap<String, ArrayList<String>>();
	HashMap<String, ArrayList<String>> containsTokensMap = new HashMap<String, ArrayList<String>>();
	Set<String> authRemove = new HashSet<String>();
	
	public Model(String configPath) throws Exception{

		CSVReader crauthswTokens = new CSVReader(
				new FileReader(configPath + "/startsWithTokens.csv"), '|','"');
		for (String[] row : crauthswTokens.readAll()) {		
			//System.out.println(row[0].toLowerCase() + row[1].trim());
			startsWithTokensMap.put(row[0].trim(),new ArrayList<String>(Arrays.asList(new String[] {row[1],row[2],row[3]})));
		}
		crauthswTokens.close();
		
		CSVReader crauthewTokens = new CSVReader(
				new FileReader(configPath + "/endsWithTokens.csv"), '|','"');
		for (String[] row : crauthewTokens.readAll()) {		
			//System.out.println(row[0].toLowerCase() + row[1].trim());
			endsWithTokensMap.put(row[0].trim(),new ArrayList<String>(Arrays.asList(new String[] {row[1],row[2],row[3]})));
		}
		crauthewTokens.close();
		
		CSVReader crauthcnTokens = new CSVReader(
				new FileReader(configPath + "/containsTokens.csv"), '|','"');
		for (String[] row : crauthcnTokens.readAll()) {		
			//System.out.println(row[0].toLowerCase() + row[1].trim());
			containsTokensMap.put(row[0].trim(),new ArrayList<String>(Arrays.asList(new String[] {row[1],row[2],row[3]})));
		}
		crauthcnTokens.close();
		
		CSVReader crremauthTokens = new CSVReader(
				new FileReader(configPath + "/authRemove.csv"), '|','"');
		for (String[] row : crremauthTokens.readAll()) {		
			//System.out.println(row[0].toLowerCase() + row[1].trim());
			//authRemove.add(row[0].trim());
		}
		crremauthTokens.close();
		
	}
	
	ArrayList<String[]> execute (String mapField, String textContent) {
		
		String inputValue = textContent.trim();
		inputValue = inputValue.replaceAll("\\s+", " ").trim();
		
		ArrayList<String[]> response = new ArrayList<>();
		
		if (inputValue.matches("[\\(\\[].+[\\)\\]]")) {
			response.add(new String [] {"ndl.sourceMeta.additionalInfo:DegreeType",inputValue});
			return response;
		}
			
		while (inputValue.matches(".*(\\[|\\().*(\\]|\\)).*"))
			inputValue = inputValue.replaceAll("(\\[[^\\[]*?\\])|((\\([^\\(]*?\\)))", "");
		//System.out.println(inputValue);
		inputValue = inputValue.replaceAll("^,|\\[.*|\\(.*|.*\\]|.*\\)","");

			//TechReport/Article Implementation.
			/*if (inputValue.matches("(?i)(?:^|\\w+\\W+)\\bdepartment\\b(?!\\Wof).*")) {
			response.add(new String[] { "dc.publisher.department", inputValue });
			return response;
		} else if (inputValue.matches("(?i).+\\bdepartment of\\b.+")) {
			inputValue = inputValue.replaceAll("(?i)(.*)\\bdepartment of\\b.*", "$1").trim();
			String department = inputValue.replaceAll("(?i).*\\b(department of\\b.*)", "$1").trim();
			response.add(new String[] { "dc.publisher.department", department });
		} else if (inputValue.matches("(?i)(?:^|\\w+\\W+)\\b(Instituut|Instituto|Institutes?|Institut|Institutions?|Institu)\\b.*")) {
			response.add(new String[] { "dc.publisher.institution", inputValue });
			return response;
		} else if (inputValue.matches("(?i).*\\b(centre|college|Foundation|Organization)\\b.*")) {
			response.add(new String[] { "dc.publisher.institution", inputValue });
			return response;
		} else if (inputValue.matches("(?i).*\\bsoftware\\b.*")) {
			response.add(new String[] { "dc.subject", inputValue });
			return response;
		} else if (inputValue.matches("(?i).*\\btelecom\\b.*")) {
			response.add(new String[] { "dc.contributor.other@organization", inputValue });
			return response;
		}*/
			//**********IMPORTANT******** IMPLEMENTATION TO BE CHANGED.*******************
			//Inproceedings implementation. Need to check for differences. Unnormalized implementation.
			
			//Implementation of startswith block
		
		for (String remAuthtokens : authRemove) {
			remAuthtokens = remAuthtokens.replaceAll("([\\W&&\\S])", "\\\\$1");
			//System.out.println(remAuthtokens + ":" + inputValue);
			if (inputValue.matches("(?i).*(^|\\s)" + remAuthtokens + "(\\s|$).*")) {
				return null;
			}
		}
		

		
			String updAuth = inputValue;
				boolean startMatch = false;
				for (Map.Entry<String, ArrayList<String>> tokenAction : startsWithTokensMap.entrySet()) {
					String token = tokenAction.getKey();
					if(tokenAction.getValue().get(0).equalsIgnoreCase("str"))
						token = token.replaceAll("([\\W&&\\S])", "\\\\$1");
					if (updAuth.matches("(?i)^" + token + "(\\s.*|$)")) {
						if (tokenAction.getValue().get(1).equals("move")) {
							mapField = tokenAction.getValue().get(2);
							response.add(new String[] { mapField, inputValue });
							return response;
						} else if (tokenAction.getValue().get(1).equals("removeToken")) {
							// startMatch = true;
							if(!tokenAction.getValue().get(2).isEmpty())
								mapField = tokenAction.getValue().get(2);
							updAuth = updAuth.replaceAll("(?i)(?:^|\\s)" + token + "(\\s|$)", " ").trim();
						} else if (tokenAction.getValue().get(1).equals("remove"))
							return response;
					}
				}
				// String value to pass through full curation cycle instead direct return.
				/*
				 * if(startMatch) { response.add(new String[] { mapField, updAuth }); return
				 * response; }
				 */

				boolean endMatch = false;
				for (Map.Entry<String, ArrayList<String>> tokenAction : endsWithTokensMap.entrySet()) {
					String token = tokenAction.getKey();
					if(tokenAction.getValue().get(0).equalsIgnoreCase("str"))
						token = token.replaceAll("([\\W&&\\S])", "\\\\$1");
					if (updAuth.matches("(?i)(^|.*\\s)" + token + "$")) {
						if (tokenAction.getValue().get(1).equals("move")) {
							mapField = tokenAction.getValue().get(2);
							response.add(new String[] { mapField, inputValue });
							return response;
						} else if (tokenAction.getValue().get(1).equals("removeToken")) {
							// endMatch = true;
							if(!tokenAction.getValue().get(2).isEmpty())
								mapField = tokenAction.getValue().get(2);
							updAuth = updAuth.replaceAll("(?i)(^|\\s)" + token + "(?:\\s|$)", " ").trim();
						} else if (tokenAction.getValue().get(1).equals("remove"))
							return response;
					}
				}
				// String value to pass through full curation cycle instead direct return.
				/*
				 * if(endMatch) { response.add(new String[] { mapField, updAuth }); return
				 * response; }
				 */

				boolean containsMatch = false;
				for (Map.Entry<String, ArrayList<String>> tokenAction : containsTokensMap.entrySet()) {
					String token = tokenAction.getKey();
					if(tokenAction.getValue().get(0).equalsIgnoreCase("str")) {
						token = token.replaceAll("([\\W&&\\S])", "\\\\$1");
						token = "(?:^|\\s|,)" + token + "(?:\\s|$|,)";
					}
					 //System.out.println(inputValue + " : " + token);
					if (updAuth.matches("(?i).*" + token + ".*")) {
						if (tokenAction.getValue().get(1).equals("move")) {
							mapField = tokenAction.getValue().get(2);
							response.add(new String[] { mapField, inputValue });
							return response;
						} else if (tokenAction.getValue().get(1).equals("removeToken")) {
							// containsMatch = true;
							if(!tokenAction.getValue().get(2).isEmpty())
								mapField = tokenAction.getValue().get(2);
							updAuth = updAuth.replaceAll("(?i)"+token, " ");
						} else if (tokenAction.getValue().get(1).equals("remove"))
							return response;
					}
				}
			//String value to pass through full curation cycle instead direct return.
			/*
			if(containsMatch) {
				response.add(new String[] { mapField, updAuth });
				return response;
			}*/

//			if(updAuth.matches("[\\w\\.]+\\sDepartment|Faculty$")) {
//				response.add(new String[] { "dc.publisher.department", inputValue });
//				return response;
//			}
			
//			if(updAuth.matches("(?:[\\w\\.]+\\s){2,}Department|Faculty$")) {
//				updAuth = updAuth.replaceAll("Department|Faculty", "").trim();
				//**********Implementation with alteration in logic. To be tested. ********//
				//response.add(new String[] { "dc.contributor.author", updAuth });
				//return response;
			//}
			//Code part of Normalization Excercise. Code commented for temporary execution. 
			/*
			if (inputValue.matches("(?i).+\\bdepartment of\\b.+")) {
				inputValue = inputValue.replaceAll("(?i)(.*)\\bdepartment of\\b.*", "$1").trim();
				String department = inputValue.replaceAll("(?i).*\\b(department of\\b.*)", "$1").trim();
				response.add(new String[] { "dc.publisher.department", department });
			} else if (inputValue.matches("(?i)(?:^|\\w+\\W+)\\b(Instituut|Instituto|Institutes?|Institut|Institutions?|Institu)\\b.*")) {
				response.add(new String[] { "dc.publisher.institution", inputValue });
				return response;
			}
			for (String tokens : removeTokens_author)
				inputValue = inputValue.replace(tokens, "").trim();
				*/
			ArrayList<String> authList = curate_author_OSTI(updAuth);
			//System.out.println(authList.size());
			if(!authList.isEmpty())
				for (String auth : authList)
					response.add(new String[] { mapField, auth });
			//else
				//response.add(new String[] { mapField, inputValue });
			
			return response;
		
	}
	
	ArrayList<String> curate_author_OSTI(String inputValue) {
		ArrayList<String> validauthorList = new ArrayList<String>();
		inputValue = inputValue.replaceAll(";,",",").trim();
		String[] auth_splitList;
		if (inputValue.matches("(?i).*\\sand\\s.*"))
			auth_splitList = inputValue.split("(?i),|(\\band(?!\\sothers)\\b)");
		else
			auth_splitList = inputValue.split("(\\s|^)and(\\s|$)");
		for (String auth : auth_splitList) {
			//auth = auth.replaceAll("^,|\\[.*|\\(.*","");
			boolean jr_sr_flag = false;
			String suffix = "";
			if(auth.matches(".*(\\s|^|,)(Jr\\.?|Sr\\.?)(\\s|$|,).*")) {
				jr_sr_flag = true;
				suffix = auth.replaceAll(".*(?<=\\s|^|,)(Jr\\.?|Sr\\.?)(?=(,|\\s|$)).*", "$1");
				auth = auth.replaceAll("(?<=\\s|^|,)(Jr[\\.]?|Sr[\\.]?)(?=\\s|$|,)", "");
				auth = auth.replaceAll("(\\s+,(?!\\s*$))", ",");
			}
			auth = auth.replaceAll(",+", ",").replaceAll("\\s+", " ").trim();
			if (!auth.isEmpty()) {
				auth = auth.replaceAll("^-|-$|\\>|\\|", "").trim();
				auth = auth.replaceAll("(?i)(^'.*)\\s+([a-z])\\.?\\s?$", "$2$1").replaceAll(",\\s*\\.?$",
						"").trim();
				auth = auth.replaceAll("\\.(?!\\s|$)", ". ").trim();
				auth = auth.replaceAll("(?i)(?<=\\s|^)([A-Z](?!\\.))(?=\\s|$)", "$1.").trim();
				auth = auth.replaceAll("\\.\\s*,", ".").replaceAll(",\\s*\\.?$", "").trim();
				auth = auth.replaceAll("(?i)(^'.*)\\s+([a-z])\\.?\\s?$", "$2$1").replaceAll(",$", "").trim();
				if (!auth.contains(",")) {
					if (!auth.matches("(?i).*\\b([a-z]\\.?|\\W+)$"))
						auth = auth.replaceAll("(.*)\\s(.*)", "$2, $1");
					else if (auth.matches("(?i).*?\\b[a-z]{1,2}\\.?\\b.*"))
						auth = auth.replaceAll("(?i)(.*?)\\s(\\b[a-z]{1,2}\\.?\\b.*)","$1, $2");
					else if (auth.matches("(?i)(?<!^[a-z]\\.).*\\s+[a-z]\\.?"))
						auth = auth.replaceAll("(.*)\\s(.*)", "$1, $2");
					if(auth.matches("(?i)^Kumari?\\s.+,.*"))
						auth = auth.replaceAll("(?i)^(Kumari?)(\\s.+,.*)","$2 $1");
					auth = auth.replaceAll("[=#*]+", "").replaceAll("\\s+\\.", "").trim();
				}
				if(jr_sr_flag)
					auth += " ("+suffix+")";
				if(auth.length()>3)
					validauthorList.add(auth.replaceAll("\\s+", " ").trim());
			}
		}
		return validauthorList;
	}

}


