package dataPreprocessing;
/********************************************************************************************

 * ´ú¸Õ/¹w´ú¸ê®Æ¥Î¼Æ­ÈÄæ¦ìÂ÷´²¤Æ (SAXTransformation_Testing)										*

 * ¿é¤J¡G±ý³B²zªºcsv®æ¦¡¸ê®Æªí¡B¨â­Ójson ³]©wÀÉ (¸ô®|¡B³B²z)¡C										*

 * ³B²z¡G±N¿é¤J¤§¸ê®Æªí¤¤«ü©w¼Æ­ÈÄæ¦ì¥H»P°V½m¸ê®Æ¬Û¦PªºÂ÷´²¤Æ¤èªkÂà´«¬°²Å¦X«ü©w²Å¸¹­Ó¼ÆªºÃþ§OÄæ¦ì¡C			*

 * ¿é¥X¡G©ó«ü©w¸ô®|¤U¿é¥X³B²z«áªº¸ê®ÆªíÀÉ®×¡C 														*

 * ³]©wÀÉ(¸ô®|)°Ñ¼Æ¡G																			*

 *	- path¡G¿é¤J¤§csv®æ¦¡¸ê®ÆªíªºÀÉ®×¸ô®|¡C														*

 *	- output¡G¿é¥X¤§¸ê®ÆªíªºÀÉ®×¸ô®|¡C															*

 *	- test_setting¡G«ü©w¿é¤Jªº³B²z³]©wÀÉÀÉ®×¸ô®|¡C¦¹ÀÉ®×¥Ñ¼Æ­ÈÄæ¦ìÂ÷´²¤Æ (SAXTransformation)²£¥Í¡C		*

 * ³]©wÀÉ(³B²z)°Ñ¼Æ¡G																			*

 *	- attrs¡G¤@jsonª«¥ó°}¦C¡A¥]§t±ý¶i¦æ³B²zªºÄæ¦ì¤Î¨ä³]©w­È¡C										*

 *ƒÞ	- attr_name¡G±ý¶i¦æ³B²zªºÄæ¦ì¦WºÙ¡C														*

 *ƒÞ	- breakpoints¡G¤@º¥¼W§Ç¯BÂI¼Æ°}¦C¡A¥]§tÂ÷´²¤Æ¤ÀÂ_ÂI³]©w­È¡C									*

 ********************************************************************************************/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SAXTransformation_Testing {
	static int MAXNUM_CATEGORY = 10;	//if the value is greater than 10, the initialization of BREAKPOINT_TABLE in main function should be extended.
	static double[][] BREAKPOINT_TABLE = new double[MAXNUM_CATEGORY+1][];
	static String INVALID_SYMBOL = "-";
	
	static class AttributeSetting{
		double[] breakpoints;
		public AttributeSetting(){}
		public AttributeSetting(double[] breakpoints){
			this.breakpoints = breakpoints;
		}
	}
	
	public static void start(String jsconfig) {
		mainflow(jsconfig);
	}
	
	static void mainflow(String jsconfig) {
		//System.out.println("==========SAXTransformation(Testing)=============");
		//0. Load configurations
		JSONParser parser = new JSONParser();
		String path = "",
			   test_setting_path = "",
			   output_filename = "";
		HashMap<String, AttributeSetting> attrSettings = new HashMap<>();
		try {
			JSONObject jsobj = (JSONObject) parser.parse(new FileReader(jsconfig));
			path = (String) jsobj.get("path");
			output_filename = jsobj.get("output").toString();
			//output_filename = "petro_subset1_2010_rate_after_sax_testing.csv";
			test_setting_path = jsobj.get("test_setting").toString();
		    
			
			jsobj = (JSONObject) parser.parse(new FileReader(test_setting_path));
			JSONArray js_attrs = (JSONArray) jsobj.get("attrs");
			for(int i=0;i<js_attrs.size();i++){
				String attr_name = ((JSONObject)js_attrs.get(i)).get("attr_name").toString();
				JSONArray js_breakpoints = (JSONArray) ((JSONObject)js_attrs.get(i)).get("breakpoints");
				double[] breakpoints = new double[js_breakpoints.size()];
				for(int j=0;j<js_breakpoints.size();j++){
					breakpoints[j] = Double.parseDouble(js_breakpoints.get(j).toString());
				}
				attrSettings.put(attr_name, new AttributeSetting(breakpoints));
			}
		} catch (Exception e) {
			System.out.println("[ERROR] Failed at JSON parsing.");
			e.printStackTrace();
			System.exit(0);
		}
		
		try {
			//1. Read the csv file
			//System.out.print("Reading \"" + path + "\"...");
			ArrayList<ArrayList<String>> records = readCSV(path);
			//System.out.println("done.\nFind " + records.size() + " records.");
			
			int Num_Attrs = records.get(0).size();
			ArrayList<String> attrs = records.get(0);
			
			//Training data size 
			int training_data =  (int)((records.size() - 1)*0.8);
			
			
			
			//System.out.print("Transforming...");
			//2. Transform each attribute (whose flag = 1) into category attribute by SAX
			//¬°¤F¥Ø¼ÐÄÝ©Ê ©Ò¥H¦³°µ­×§ï
			for(int r=training_data+1;r<records.size();r++){
//			for(int r=1;r<records.size();r++){
				ArrayList<String> curRecord = records.get(r);
				for(int c=0;c<Num_Attrs;c++){
					if(!attrSettings.containsKey(attrs.get(c))) continue;
					double[] breakpoints = attrSettings.get(attrs.get(c)).breakpoints;
					String curValue = curRecord.get(c);
					try {
						double val = Double.parseDouble(curValue);
						for(int i=0;i<breakpoints.length;i++){
							if(val <= breakpoints[i]){
								curRecord.set(c, records.get(0).get(c) + "_" + (i+1));
								//curRecord.set(c, "C" + "_" + (i+1));
								break;
							}
						}
						if(val > breakpoints[breakpoints.length-1])
							curRecord.set(c, records.get(0).get(c)+ "_" + (breakpoints.length+1));
							//curRecord.set(c, "C" + "_" + (breakpoints.length+1));
					} catch(NumberFormatException e) {
						//The value is not a valid double
						curRecord.set(c, INVALID_SYMBOL);
					}
				}
			}
			
			//System.out.println("done.");
			//System.out.print("Writing to output file (" + output_filename + ") ...");
			
			//3. Write records into output file
			writeCSV("", output_filename, records);
			//System.out.println("done.");
		} catch (FileNotFoundException e) {
			System.out.println("[ERROR] File Not Found Exception.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[ERROR] I/O Exception.");
			e.printStackTrace();
		}
		//System.out.println("===================================================\n");
	}
	
	static ArrayList<ArrayList<String>> readCSV(String fullpath) throws FileNotFoundException{
		ArrayList<ArrayList<String>> records = new ArrayList<>();
		File inputFile = new File(fullpath);
		Scanner scl = new Scanner(inputFile);
		while(scl.hasNextLine()){
			ArrayList<String> newRecord = new ArrayList<>();
			String[] tokens = scl.nextLine().split(",");
			for(String token : tokens){
				newRecord.add(token);
			}
			records.add(newRecord);
		}
		scl.close();
		
		return records;
	}
	
	static void writeCSV(String path, String filename, ArrayList<ArrayList<String>> records) throws IOException{
		FileWriter outputFW = new FileWriter(path + filename);
		for(int i=0;i<records.size();i++){
			ArrayList<String> record = records.get(i);
			StringBuilder recordSB = new StringBuilder();
			for(String col : record) recordSB.append(col).append(',');
			recordSB.deleteCharAt(recordSB.length()-1);
			outputFW.write(recordSB.toString());
			if(i < records.size()-1) outputFW.write("\r\n");
		}
		outputFW.close();
	}
}
