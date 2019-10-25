package normalizer.dli;

import java.util.ArrayList;

public class Parse {
	
	
	Model auth_curation;
	Shift auth_shift;
	Cleanse cleanse;
	
	
	
	public Parse(String configPath) throws Exception {
		// TODO Auto-generated constructor stub		
		
		auth_curation = new Model(configPath,false);
		auth_shift = new Shift(configPath);
		cleanse = new Cleanse(configPath);
		
	}
	
	ArrayList<String[]> getResult(String inputValue) {
		ArrayList<String[]> response = new ArrayList<String[]>();
		inputValue = cleanse.cleanParts(inputValue);
		//System.out.println(inputValue);
		inputValue = inputValue.replaceAll("_", " ");
		String[] inputSplit = inputValue.split("(?i)\\|");
		for (String splitValue : inputSplit) {
		String[] shiftresult = auth_shift.getAuthvalue(splitValue);
		//System.out.println(shiftresult[0] + ":" + shiftresult[1] + ":" + shiftresult[2]);
		if(shiftresult[2].equalsIgnoreCase("other"))
			response.add(new String[] {shiftresult[0], shiftresult[1]});
		else
			response.addAll(auth_curation.execute(shiftresult[0], shiftresult[1]));
		}
		return response;
	}
}
