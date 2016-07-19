package dataPreprocessing;
/********************************************************************************

 * 數值欄位離散化 (SAXTransformation)												*

 * 輸入：欲處理的csv格式資料表、json設定檔。											*

 * 處理：將輸入之資料表中指定數值欄位以SAX方法進行離散化並轉換為符合指定符號個數的類別欄位。	*

 * 輸出：於指定路徑下輸出處理後的資料表檔案。											*

 * 設定檔參數：																	*

 *	- path：輸入之csv格式資料表的檔案路徑。											*

 *	- output：輸出之資料表的檔案路徑。												*

 *	- test_setting：指定輸出的處理設定檔(測試/預測用、json格式)檔案路徑。				*

 *	- attrs：一json物件陣列，包含欲進行處理的欄位及其設定值。							*

 *		- attr_name：欲進行處理的欄位名稱。											*

 *		- num_category：轉換後的符號個數。											*

 ********************************************************************************/

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

public class SAXTransformation {
	static int MAXNUM_CATEGORY = 10;	//if the value is greater than 10, the initialization of BREAKPOINT_TABLE in main function should be extended.
	static double[][] BREAKPOINT_TABLE = new double[MAXNUM_CATEGORY+1][];
	static String INVALID_SYMBOL = "-";
	
	static class AttributeSetting{
		int numCategory = 2;
		
		public AttributeSetting(){}
		public AttributeSetting(int numCategory){
			this.numCategory = numCategory;
		}
	}
	
	public static void start(String jsconfig) {
		mainflow(jsconfig);
	}
	
	static void mainflow(String jsconfig) {
		//System.out.println("==========SAXTransformation(Training)=============");
		//Initialize BREAKPOINT_TABLE (Make each category has same probability in normal distribution)
		initialize();
		
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
			test_setting_path = jsobj.get("test_setting").toString();
			JSONArray js_attrs = (JSONArray) jsobj.get("attrs");
			for(int i=0;i<js_attrs.size();i++){
				String attr_name = ((JSONObject)js_attrs.get(i)).get("attr_name").toString();
				int num_category = Integer.parseInt(((JSONObject)js_attrs.get(i)).get("num_category").toString());
				attrSettings.put(attr_name, new AttributeSetting(num_category));
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
			
			//System.out.print("Gathering information...");
			//2. Get average of each attribute (whose flag = 1)
			double[] Avgs = new double[Num_Attrs];	//Now it is a set of summation, not average.
			int[] numValid = new int[Num_Attrs];
			for(int r=1;r<records.size();r++){
				ArrayList<String> curRecord = records.get(r);
				for(int c=0;c<Num_Attrs;c++){
					if(!attrSettings.containsKey(attrs.get(c))) continue;
					String curValue = curRecord.get(c);
					try {
						double val = Double.parseDouble(curValue);
						Avgs[c] += val;
						numValid[c]++;
					} catch(NumberFormatException e) {
						//The value is not a valid double
						//Do nothing
					}
				}
			}
			for(int c=0;c<Num_Attrs;c++){
				if(!attrSettings.containsKey(attrs.get(c))) continue;
				Avgs[c] /= numValid[c];				//Now it becomes a set of average
			}
			
			//3. Get variance of each attribute (whose flag = 1)
			double[] Vars = new double[Num_Attrs];	//Now it is a set of squared distance sum
			for(int r=1;r<records.size();r++){
				ArrayList<String> curRecord = records.get(r);
				for(int c=0;c<Num_Attrs;c++){
					if(!attrSettings.containsKey(attrs.get(c))) continue;
					String curValue = curRecord.get(c);
					try {
						double val = Double.parseDouble(curValue);
						Vars[c] += Math.pow(val - Avgs[c], 2);
					} catch(NumberFormatException e) {
						//The value is not a valid double
						//Do nothing
					}
				}
			}
			for(int c=0;c<Num_Attrs;c++){
				if(!attrSettings.containsKey(attrs.get(c))) continue;
				Vars[c] = Math.pow(Vars[c]/numValid[c], 0.5);				//Now it becomes a set of variance
			}
			
			//4. Set breakpoint_value for each attribute (whose flag = 1) by their Avg, Var, and numCategory 
			double[][] breakpoint_values = new double[Num_Attrs][];
			for(int c=0;c<Num_Attrs;c++){
				if(!attrSettings.containsKey(attrs.get(c))) continue;
				int numCategory = attrSettings.get(attrs.get(c)).numCategory;
				breakpoint_values[c] = new double[numCategory-1];
				for(int i=0;i<breakpoint_values[c].length;i++)
					breakpoint_values[c][i] = Avgs[c] + BREAKPOINT_TABLE[numCategory][i] * Vars[c];
			}
			writeTestingSettings(test_setting_path, attrs, attrSettings, breakpoint_values);
			//System.out.println("done.");
			//System.out.print("Transforming...");
			
			//5. Transform each attribute (whose flag = 1) into category attribute by SAX
			//為了目標屬性 有做修改
			int traing_data = (int)((records.size()-1)*0.8);
			for(int r=1;r<=traing_data;r++){
//			for(int r=1;r< records.size();r++){
				ArrayList<String> curRecord = records.get(r);
				for(int c=0;c<Num_Attrs;c++){
					if(!attrSettings.containsKey(attrs.get(c))) continue;
					String curValue = curRecord.get(c);
					try {
						double val = Double.parseDouble(curValue);
						for(int i=0;i<breakpoint_values[c].length;i++){
							if(val <= breakpoint_values[c][i]){
								curRecord.set(c, records.get(0).get(c) + "_" + (i+1));
								//curRecord.set(c, "C" + "_" + (i+1));
								break;
							}
						}
						if(val > breakpoint_values[c][breakpoint_values[c].length-1]) {
							curRecord.set(c, records.get(0).get(c) + "_" + (breakpoint_values[c].length+1));
							//curRecord.set(c, "C" + "_" + (breakpoint_values[c].length+1));
						}   
					} catch(NumberFormatException e) {
						//The value is not a valid double
						curRecord.set(c, INVALID_SYMBOL);
					}
				}
			}
			
			//System.out.println("done.");
			//System.out.print("Writing to output file (" + output_filename + ") ...");
			
			//6. Write records into output file
			writeCSV("", output_filename, records);
			//System.out.println("done.");
		} catch (FileNotFoundException e) {
			System.out.println("[ERROR] File Not Found Exception.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[ERROR] I/O Exception.");
			e.printStackTrace();
		}
	}
	
	static void initialize(){
		for(int i=2;i<MAXNUM_CATEGORY+1;i++){
			switch(i){
			case 2:
				BREAKPOINT_TABLE[i] = new double[] {0};
				break;
			case 3:
				BREAKPOINT_TABLE[i] = new double[] {-0.43,0.43};
				break;
			case 4:
				BREAKPOINT_TABLE[i] = new double[] {-0.67, 0, 0.67};
				break;
			case 5:
				BREAKPOINT_TABLE[i] = new double[] {-0.84, -0.25, 0.25, 0.84};
				break;
			case 6:
				BREAKPOINT_TABLE[i] = new double[] {-0.97, -0.43, 0, 0.43, 0.97};
				break;
			case 7:
				BREAKPOINT_TABLE[i] = new double[] {-1.07, -0.57, -0.18, 0.18, 0.57, 1.07};
				break;
			case 8:
				BREAKPOINT_TABLE[i] = new double[] {-1.15, -0.67, -0.32, 0, 0.32, 0.67, 1.15};
				break;
			case 9:
				BREAKPOINT_TABLE[i] = new double[] {-1.22, -0.76, -0.43, -0.14, 0.14, 0.43, 0.76, 1.22};
				break;
			case 10:
				BREAKPOINT_TABLE[i] = new double[] {-1.28, -0.84, -0.52, -0.25, 0, 0.25, 0.52, 0.84, 1.28};
				break;
			default:
				System.out.println("[ERROR] Breakpoint Table should be extended!");
			}
		}
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
	

	static void writeTestingSettings(String filename, ArrayList<String> attrs, HashMap<String, AttributeSetting> attrSettings, double[][] breakpoint_values){
		int debug = 0;
		if (debug == 0) {
		//The output file for testing settings is in json format
		File outputfile = new File(filename);
		if(!outputfile.getParentFile().exists()) outputfile.getParentFile().mkdirs();
		try {
			FileWriter fw = new FileWriter(outputfile);
			fw.write("{\r\n\t\"path\"\t\t:\t\"transformed_petro_subset1_feature.csv\",");
			fw.write("\r\n\t\"output\"\t:\t\"transformed_petro_subset1_feature_for_sax_testing.csv\",");
			fw.write("\r\n\t\"test_setting\"\t:\t\"petro_subset1_breakpoints_2010.txt\",");
			fw.write("\r\n\t\"attrs\"\t:\r\n\t[");
			boolean first = true;
			for(int i=0;i<attrs.size();i++){
				if(!attrSettings.containsKey(attrs.get(i))) continue;
				if(!first) fw.write(",");
				else first = false;
				fw.write("\r\n\t\t{\r\n\t\t\t\"attr_name\"\t:\t\"" + attrs.get(i) + "\","
						+ "\r\n\t\t\t\"breakpoints\"\t:\r\n\t\t\t[");
				boolean first_breakpoint = true;
				for(int j=0;j<attrSettings.get(attrs.get(i)).numCategory-1;j++){
					if(!first_breakpoint) fw.write(",");
					else first_breakpoint = false;
					fw.write("\r\n\t\t\t\t" + breakpoint_values[i][j]);
				}
				fw.write("\r\n\t\t\t]\r\n\t\t}");
			}
			fw.write("\r\n\t]\r\n}");
			fw.flush();
			fw.close();
		} catch (IOException e) {
			System.out.println("[Error] I/O Exception.");
			e.printStackTrace();
		}
		} else {
			//The output file for testing settings is in json format
			File outputfile = new File(filename);
			if(!outputfile.getParentFile().exists()) outputfile.getParentFile().mkdirs();
			try {
				FileWriter fw = new FileWriter(outputfile);
				fw.write("{\r\n\t\"path\"\t\t:\t\"result_test_2.csv\",");
				fw.write("\r\n\t\"output\"\t:\t\"result_train2_for_testing.csv\",");
				fw.write("\r\n\t\"test_setting\"\t:\t\"breakpoint_target_2.txt\",");
				fw.write("\r\n\t\"attrs\"\t:\r\n\t[");
				boolean first = true;
				for(int i=0;i<attrs.size();i++){
					if(!attrSettings.containsKey(attrs.get(i))) continue;
					if(!first) fw.write(",");
					else first = false;
					fw.write("\r\n\t\t{\r\n\t\t\t\"attr_name\"\t:\t\"" + attrs.get(i) + "\","
							+ "\r\n\t\t\t\"breakpoints\"\t:\r\n\t\t\t[");
					boolean first_breakpoint = true;
					for(int j=0;j<attrSettings.get(attrs.get(i)).numCategory-1;j++){
						if(!first_breakpoint) fw.write(",");
						else first_breakpoint = false;
						fw.write("\r\n\t\t\t\t" + breakpoint_values[i][j]);
					}
					fw.write("\r\n\t\t\t]\r\n\t\t}");
				}
				fw.write("\r\n\t]\r\n}");
				fw.flush();
				fw.close();
			} catch (IOException e) {
				System.out.println("[Error] I/O Exception.");
				e.printStackTrace();
			}
			
			
		}
	}
}
