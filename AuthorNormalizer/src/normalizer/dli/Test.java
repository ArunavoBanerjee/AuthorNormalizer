package normalizer.dli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.opencsv.CSVReader;

public class Test {
	static PrintStream pr = null;
	static HashMap<String,HashMap<String, ArrayList<String[]>>> fieldtranslationList = new HashMap<String, HashMap<String, ArrayList<String[]>>>();
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String configPath = "C:\\Users\\user\\Desktop\\KGP\\NDL\\data\\DLI\\";
		CSVReader crFT = new CSVReader(new FileReader(configPath + "/authMap.csv"), '|','"');
		for (String[] row : crFT.readAll()) {
			String keyField = row[0].trim();
			String keyvalue = row[1].toLowerCase().trim();
			//System.out.println(keyvalue);
			if(fieldtranslationList.containsKey(keyField)) {
				if(fieldtranslationList.get(keyField).containsKey(keyvalue)) {
					fieldtranslationList.get(keyField).get(keyvalue).add(new String[] { row[2].trim(), row[3].trim() });
				} else {
					fieldtranslationList.get(keyField).put(keyvalue, 
							new ArrayList<String[]>() {{ add(new String[] { row[2].trim(), row[3].trim()}); }});
				}
				} else {
					HashMap<String, ArrayList<String[]>> valueMap = new HashMap<String,ArrayList<String[]>>();
					valueMap.put(keyvalue, new ArrayList<String[]>() {{ add(new String[] { row[2].trim(), row[3].trim()}); }} );
					fieldtranslationList.put(keyField, valueMap);
				}
			}
		crFT.close();
		
		checkbyfile("C:\\Users\\user\\Desktop\\KGP\\NDL\\data\\DLI\\authorDistinct.csv");
		//checkbyfile("C:\\Users\\user\\Desktop\\KGP\\NDL\\data\\DLI\\author_test_in.csv");
		
		//checkbyname("Sheikul Islam\\");
	}
static void checkbyfile(String filepath) throws Exception {
	Parse parseAuthors = new Parse("C:\\Users\\user\\Desktop\\KGP\\NDL\\data\\DLI\\run.properties\\author\\config");
	File testf = new File(filepath);
	CSVReader cr = new CSVReader(new FileReader(testf),',','"',1);
	String sample_input = "", handleId = "";
	Integer count = 0;
	//pr = new PrintStream("C:\\Users\\user\\Desktop\\KGP\\NDL\\data\\DLI\\author_test_out.csv");
	pr = new PrintStream("C:\\Users\\user\\Desktop\\KGP\\NDL\\data\\DLI\\DLI_allauthors_out.csv");
	if(pr != null)
		pr.println("\"Id\",\"sourceData\",\"mapField\",\"targetData\"");
	for (String[] row : cr.readAll()) {
		System.out.println(count++ +" : " + sample_input);
		//handleId = row[0];
		handleId = count.toString();
		sample_input = row[0];
		ArrayList<String[]> result = new ArrayList<String[]>();
			ArrayList<String[]> mapReturnValueList = null;
			if ((mapReturnValueList = staticFieldTranslate("dc.contributor.author", sample_input)) != null) {
				for (String[] mapReturnValue : mapReturnValueList) {
					if (!mapReturnValue[0].equalsIgnoreCase("remove")) {
						for (String mapSplitValue : mapReturnValue[1].split(";")) {
							result.add(new String[] { mapReturnValue[0], mapSplitValue.trim() });
						}
					} else
						result.add(new String[] { "remove", "remove" });
				}
		for(String[] eachresult : result)		
			if(pr == null)
				System.out.println("\""+ handleId + "\",\"" + sample_input + "\",\"" + eachresult[0] + "\",\"" + eachresult[1]+"\"");
			else
				pr.println("\""+ handleId + "\",\"" + sample_input + "\",\"" + eachresult[0] + "\",\"" + eachresult[1]+"\"");
		}
		else {
		result = parseAuthors.getResult(sample_input);
		if(!result.isEmpty())
		for(String[] eachresult : result) {
			if(pr == null)
				System.out.println("\""+ handleId + "\",\"" + sample_input + "\",\"" + eachresult[0] + "\",\"" + eachresult[1]+"\"");
			else
				pr.println("\""+ handleId + "\",\"" + sample_input + "\",\"" + eachresult[0] + "\",\"" + eachresult[1]+"\"");
		}
		else
			if(pr == null)
				System.out.println("\""+ handleId + "\",\"" + sample_input + "\",\"NULL\",\"NULL\"");
			else
				pr.println("\""+ handleId + "\",\"" + sample_input + "\",\"NULL\",\"NULL\"");
	}
	}
	cr.close();
	if(pr != null)
		pr.close();
}
static void checkbyname(String name) throws Exception {
	Parse parseAuthors = new Parse("C:\\Users\\user\\Desktop\\KGP\\NDL\\data\\DLI\\run.properties\\author\\config");
	String input = name;
	System.out.println("Input Data : " + input);
	ArrayList<String[]> result = parseAuthors.getResult(input);
	if(!result.isEmpty())
		for(String[] eachresult : result)
			System.out.println(eachresult[0] + " -- " + eachresult[1]);
	else
		System.out.println("NULL");
}

static ArrayList<String[]> staticFieldTranslate(String input_fieldName, String input_fieldValue) {
	ArrayList<String[]> result = null;
	input_fieldValue = input_fieldValue.toLowerCase();
	if(fieldtranslationList.containsKey(input_fieldName)) {
	HashMap<String, ArrayList<String[]>> valueMap = fieldtranslationList.get(input_fieldName);
	if (valueMap.containsKey(input_fieldValue)) {
			result = valueMap.get(input_fieldValue);
		}
}
	return result;
}
}
