package normalizer;

import java.util.ArrayList;

public class Parse {
	
	
	Model auth_curation;
	Shift auth_shift;
	
	
	
	public Parse(String configPath) throws Exception {
		// TODO Auto-generated constructor stub		
		
		auth_curation = new Model(configPath);
		auth_shift = new Shift(configPath+"/authshift.csv");
		
	}
	
	ArrayList<String[]> getResult(String inputValue) {
		
		String[] shiftresult = auth_shift.getAuthvalue(inputValue);
		System.out.println(shiftresult[0] + ":" + shiftresult[1]);
		return auth_curation.execute(shiftresult[0], shiftresult[1]);
		
	}
}
