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
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import com.opencsv.CSVReader;

public class Model {

	Set<String> validTitles = new HashSet<String>();
	
	public Model(String configPath) throws Exception{
		
		CSVReader crvalidTitles = new CSVReader(
				new FileReader(configPath + "/validTitles.csv"), '\n');
		for (String[] row : crvalidTitles.readAll()) {		
			validTitles.add(row[0].toLowerCase().trim());
		}
		crvalidTitles.close();
	}
	
	ArrayList<String[]> execute (String mapField, String textContent) {
		
		ArrayList<String[]> response = new ArrayList<>();
		
		for(String eachText : textContent.split("(\\s+|^)and(\\s+|$)")) {
			
		String inputValue = eachText.trim();

		inputValue = inputValue.replaceAll("\\s+", " ").trim();
		
		/*if (inputValue.matches("[\\(\\[].+[\\)\\]]")) {
			response.add(new String [] {"ndl.sourceMeta.additionalInfo:DegreeType",inputValue});
			return response;
		}*/
			//System.out.println(inputValue);
		while (inputValue.matches(".*(\\[|\\()\\W*(\\]|\\)).*"))
			inputValue = inputValue.replaceAll("(\\[[^\\[]*?\\])|((\\([^\\(]*?\\)))", "");
		//System.out.println(inputValue);
		//inputValue = inputValue.replaceAll("^,|\\[.*|\\(.*|.*\\]|.*\\)","");
		inputValue = inputValue.replaceAll("[\\[\\]\\(\\)]+","");
        inputValue = inputValue.replaceAll("(\\W)(?!\\s+)", "$1 ");
		inputValue = inputValue.replaceAll("(\\s+|^)\\W+(\\s+|$)"," ");
		//System.out.println(inputValue);

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
			ArrayList<String> authList = curate_author_OSTI(inputValue);
			//System.out.println(authList.size());
			if(!authList.isEmpty())
				for (String auth : authList)
					response.add(new String[] { mapField, auth });
			//else
				//response.add(new String[] { mapField, inputValue });
	}
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
				String[] authParts = auth.split("\\s");
				if(validTitles.contains(authParts[0].toLowerCase()))
					auth = auth.replaceFirst("\\s", ", ");
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
				if(auth.length()>3) {
				auth = auth.replaceAll("\\s+", " ");
				auth = WordUtils.capitalizeFully(auth);
					validauthorList.add(auth.trim());
				}
			}
		}
		return validauthorList;
	}

}


