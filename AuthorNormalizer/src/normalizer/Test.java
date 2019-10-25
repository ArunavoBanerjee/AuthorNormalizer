package normalizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;

import com.opencsv.CSVReader;

public class Test {
	static PrintStream pr = null;
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//checkbyfile("C:\\Users\\user\\Desktop\\KGP\\NDL\\data\\OSTI/test_input_all.csv");
		checkbyname("Ryutov,, D.D.]");
	}
static void checkbyfile(String filepath) throws Exception {
	Parse parseAuthors = new Parse("C:\\Users\\user\\Desktop\\KGP\\NDL\\data\\OSTI");
	File testf = new File(filepath);
	CSVReader cr = new CSVReader(new FileReader(testf),',','"',1);
	String sample_input = "", handleId = "";
	int count = 0;
	pr = new PrintStream("C:\\Users\\user\\Desktop\\KGP\\NDL\\data\\OSTI/OSTI_TR_all_authors_2.csv");
	pr.println("\"handleId\",\"sourceData\",\"mapField\",\"targetData\"");
	for (String[] row : cr.readAll()) {
		handleId = row[0];
		sample_input = row[1];
		//System.out.println(sample_input);
		System.out.println(count++);
		ArrayList<String[]> result = parseAuthors.getResult(sample_input);
		if(!result.isEmpty())
		for(String[] eachresult : result) {
			pr.println("\""+ handleId + "\",\"" + sample_input + "\",\"" + eachresult[0] + "\",\"" + eachresult[1]+"\"");
		}
		else
			pr.println("\""+ handleId + "\",\"" + sample_input + "\",\"NULL\",\"NULL\"");
	}
	cr.close();
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
}
