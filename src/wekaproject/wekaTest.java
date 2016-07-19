package wekaproject;
import java.io.*;
import java.util.*;

import ca.pfv.spmf.algorithms.sequentialpatterns.BIDE_and_prefixspan_with_strings.AlgoPrefixSpan_with_Strings;
import ca.pfv.spmf.input.sequence_database_list_strings.SequenceDatabase;
import dataPreprocessing.SAXTransformation;
import dataPreprocessing.SAXTransformation_Testing;
import getAttribute.GetAttr;
import ruleGeneration.RuleEvaluation;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;

import weka.classifiers.functions.LibSVM;
import weka.classifiers.trees.J48;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;


import transferToSDB.T2SDB;
import weka.core.converters.ArffSaver;
public class wekaTest {
	static HashSet<List<String>> powerSet = new HashSet<List<String>>();
	
	public static BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;
 
		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}
 
		return inputReader;
	}
 
	public static Evaluation classify(Classifier model,
			Instances trainingSet, Instances testingSet) throws Exception {
		Evaluation evaluation = new Evaluation(trainingSet);
 
		model.buildClassifier(trainingSet);
		evaluation.evaluateModel(model, testingSet);
 
		return evaluation;
	}
 
	public static double calculateAccuracy(FastVector predictions) {
		double correct = 0;
 
		for (int i = 0; i < predictions.size(); i++) {
			NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
			if (np.predicted() == np.actual()) {
				correct++;
			}
		}
 
		return 100 * correct / predictions.size();
	}
 
	public static void run(int period, int minsup, String preprocessing_path, String output_path, int class_two) throws Exception {

		BufferedReader datafile = readDataFile(preprocessing_path + "weka_training_" + period + "_" + minsup +".arff");
 
		Instances data = new Instances(datafile);
		//System.out.println(data.numAttributes() - 1);
		data.setClassIndex(data.numAttributes() - 1);
		
		//Divide train and test
		int trainSize = (int) Math.round(data.numInstances() * 0.8);
		int testSize = data.numInstances() - trainSize;
		Instances train = new Instances(data, 0, trainSize);
		//System.out.println(train);
		Instances test = new Instances(data, trainSize, testSize);
		//Set class index to the last attribute
		test.setClassIndex(test.numAttributes()-1);		
		// Use a set of classifiers
		Classifier[] models = { 
				new J48(), // a decision tree			
				new LibSVM(),
		};
		
		
		
		// Run for each model
		for (int j = 0; j < models.length; j++) {

			
			//SVM MODULE, SET KERNEL
            if (j == 1) {
            	/** Criteria:         
    	         *           Predict R      Predict D
    	         * Real R |True Positive |False Negative|           
    	         *        -------------------------------
    	         * Real D |False Positive|True Negative |
    	         * 
    	         */		 
            	//針對兩類
    			int True_Positive  = 0;
    	        int True_Negative  = 0;
    	        
    	        int False_Positive = 0;
    	        int False_Negative = 0;     
    	        
    	        //針對四類
    			int True_Rise1  = 0;
    			int True_Rise2  = 0;
    			int True_Down1  = 0;
    			int True_Down2  = 0;
    	      
    	        int False_Rise1 = 0;
    	        int False_Rise2 = 0;     
    	        int False_Down1 = 0;
    	        int False_Down2 = 0;   
    	        
    			int avaliable_count = 0;
    			
            	try {        	
            		
            		//Linear kernel
            	    String options = ( "-K 0" );
            	    String[] optionsArray = options.split( " " );
            	    models[j].setOptions(optionsArray);    
            	    
    		        //Build classifier
            	    models[j].buildClassifier(train);
		            
            	    //Classify each test case
            	    for (int i = 2; i < test.numInstances(); i++) {
            	    	
            	    	//Get class double value for current instance
            	    	double actualClass = test.instance(i).classValue();
            	    	
            	    	//Get class string value using the class index using the class's int value
            	    	String actual = test.classAttribute().value((int) actualClass);
            	    	
            	    	//Get Instance object of current instance
            	    	Instance newInst = test.instance(i);
            	    	
            	    	//Check if test case is available
            	    	boolean none_zero = false;
            	    	for (int k= 0; k < newInst.numValues(); k++) {
            	            int att = (int)newInst.value(k);
            	            if (att == 1) {
            	            	none_zero = true;
            	            	break;            	            	
            	            }
            	    	}
            	    	
            	    	if (none_zero == false) {
            	    		continue;
            	    	} else {
            	    	    avaliable_count++;
            	    	}

            	    	//Call classifyInstance, which returns a double value for class
            	    	double predSVM = models[j].classifyInstance(newInst);
            	    	
            	    	//Using this value to get string value of the predicted class
            	    	String predString = test.classAttribute().value((int) predSVM);   
            	    	
            	    	//Evaluate Criteria
            	    	if (class_two == 1) {
	            	        if (predString.equals("Rise")) {
	            	    		if (actual.equals("Rise")) {
	            	    			True_Positive++;
	            	    		} else {
	            	    			False_Positive++;
	            	    		}            	    		
	            	    	} else if (predString.equals("Down")) {
	            	    		if (actual.equals("Down")) {
	            	    			True_Negative++;
	            	    		} else {
	            	    			False_Negative++;
	            	    		}            	    		
	            	    	}      
            	    	} else {//針對四類
            	    		if (predString.equals("Rise_1")) {
            	    			if (actual.equals("Rise_1")) {
            	    				True_Rise1++;
	            	    		} else {
	            	    			False_Rise1++;
	            	    		}	           	    		
	            	    	} else if (predString.equals("Rise_2")) {
	            	    		if (actual.equals("Rise_2")) {
            	    				True_Rise2++;
	            	    		} else {
	            	    			False_Rise2++;
	            	    		}	          	    		
	            	    	} else if (predString.equals("Down_1")) {
	            	    		if (actual.equals("Down_1")) {
            	    				True_Down1++;
	            	    		} else {
	            	    			False_Down1++;
	            	    		}	
	                        } else if (predString.equals("Down_2")) {
	                        	if (actual.equals("Down_2")) {
            	    				True_Down2++;
	            	    		} else {
	            	    			False_Down2++;
	            	    		}	
	                        }         
            	    	}
            	    	System.out.println(actual + ", " + predString);
            	    }
            	    
            	    //Evaluate Criteria
            	    int test_size = (test.numInstances()-2);
                    int size = True_Negative +  True_Positive + False_Positive + False_Negative;
            	    //Rise
            	    double precision_rise = 0;
                    if (True_Positive == 0 ) {        	
                    	precision_rise = 0;
                    } else {
                    	precision_rise =  True_Positive / (double)(True_Positive + False_Positive);                      
                    }       
            	    
                    double recall_rise =  True_Positive / (double) (True_Positive + False_Negative);
                    
                    //Down                   
                    double precision_down = 0;
                    if (True_Negative == 0 ) {        	
                    	precision_down = 0;
                    } else {
                    	precision_down =  True_Negative / (double)(True_Negative +  False_Negative);                       
                    }     
                  
                    double acc =  (True_Positive + True_Negative)/ (double)(size);
                    double recall_down =  True_Negative / (double) (True_Negative + False_Positive);                    
                    double macro_precision = ( precision_rise + precision_down) / (double) 2;
                    double macro_recall = ( recall_rise + recall_down) / (double) 2;
                    double macro_f_measure = 2*(macro_precision*macro_recall)/ (macro_precision+macro_recall);
                    double applicability = avaliable_count / (double) (test_size);
                	File fout = new File(output_path + "svm_liner_"+ period + "_" + minsup +".arff");                	
             	    FileOutputStream fos = new FileOutputStream(fout);
                    OutputStreamWriter osw = new OutputStreamWriter(fos);            	

            		osw.write("=== Confusion Matrix ===\r\n");
            		osw.write("          a      b\r\n");
            		osw.write("a=Rise   " + True_Positive + "\t" + False_Negative + "\r\n");
           		    osw.write("b=Down   " + False_Positive + "\t" + True_Negative+ "\r\n");		
            		osw.write("precision_rise: " + precision_rise+ "\r\n");
            		osw.write("recall_rise: " + recall_rise + "\r\n");
            		osw.write("precision_down: " + precision_down+ "\r\n");
           		    osw.write("recall_down: " + recall_down + "\r\n");
                    osw.write("macro_precision: " + macro_precision+ "\r\n");
                    osw.write("macro_recall: " + macro_recall+ "\r\n");
                    osw.write("macro_f_measure: " + macro_f_measure+ "\r\n");
            		osw.write("acc: "               + acc+ "\r\n");
            		osw.write("applicability: "               +applicability+ "\r\n");
            		osw.write("\r\n");
            		osw.write("\r\n");
    		        osw.close();
    		        
            	}catch (IOException e) {
    	        	System.out.println("[ERROR] I/O Exception.");
    	            e.printStackTrace();  	
    	        }   

            } else {
                try {       
                	/** Criteria:         
        	         *           Predict R      Predict D
        	         * Real R |True Positive |False Negative|           
        	         *        -------------------------------
        	         * Real D |False Positive|True Negative |
        	         * 
        	         */		 
                	//針對兩類
        			int True_Positive  = 0;
        	        int True_Negative  = 0;
        	        
        	        int False_Positive = 0;
        	        int False_Negative = 0;     
        	        
        	        //針對四類
        			int True_Rise1  = 0;
        			int True_Rise2  = 0;
        			int True_Down1  = 0;
        			int True_Down2  = 0;
        	      
        	        int False_Rise1 = 0;
        	        int False_Rise2 = 0;     
        	        int False_Down1 = 0;
        	        int False_Down2 = 0;   
        			
        			int avaliable_count = 0;
        			
                	//Build classifier
            	    models[j].buildClassifier(train);
            	                	    
                	//Classify each test case
            	    for (int i = 2; i < test.numInstances(); i++) {
            	    	
            	    	
            	    	
            	    	
            	    	//Get class double value for current instance
            	    	double actualClass = test.instance(i).classValue();
            	    	
            	    	//Get class string value using the class index using the class's int value
            	    	String actual = test.classAttribute().value((int) actualClass);
            	    	
            	    	//Get Instance object of current instance
            	    	Instance newInst = test.instance(i);
            	    	//Check if test case is available
            	    	boolean none_zero = false;
            	    	for (int k= 0; k < newInst.numValues(); k++) {
            	            int att = (int)newInst.value(k);
            	            if (att == 1) {
            	            	none_zero = true;
            	            	break;            	            	
            	            }
            	    	}
            	    	
            	    	if (none_zero == false) {
            	    		continue;
            	    	} else {
            	    	    avaliable_count++;
            	    	}

            	    	//Call classifyInstance, which returns a double value for class
            	    	double predDT = models[j].classifyInstance(newInst);
            	    	
            	    	//Using this value to get string value of the predicted class
            	    	String predString = test.classAttribute().value((int) predDT);   
            	    	
            	    	//Evaluate Criteria
            	    	if (class_two == 1) {
	            	    	if (predString.equals("Rise")) {
	            	    		if (actual.equals("Rise")) {
	            	    			True_Positive++;
	            	    		} else {
	            	    			False_Positive++;
	            	    		}            	    		
	            	    	} else if (predString.equals("Down")) {
	            	    		if (actual.equals("Down")) {
	            	    			True_Negative++;
	            	    		} else {
	            	    			False_Negative++;
	            	    		}            	    		
	            	    	}  
            	    	} else {
            	    		if (predString.equals("Rise_1")) {
            	    			if (actual.equals("Rise_1")) {
            	    				True_Rise1++;
	            	    		} else {
	            	    			False_Rise1++;
	            	    		}	           	    		
	            	    	} else if (predString.equals("Rise_2")) {
	            	    		if (actual.equals("Rise_2")) {
            	    				True_Rise2++;
	            	    		} else {
	            	    			False_Rise2++;
	            	    		}	          	    		
	            	    	} else if (predString.equals("Down_1")) {
	            	    		if (actual.equals("Down_1")) {
            	    				True_Down1++;
	            	    		} else {
	            	    			False_Down1++;
	            	    		}	
	                        } else if (predString.equals("Down_2")) {
	                        	if (actual.equals("Down_2")) {
            	    				True_Down2++;
	            	    		} else {
	            	    			False_Down2++;
	            	    		}	
	                        }                     	    		
            	    	}
            	    	System.out.println(actual + ", " + predString);
            	    }
            	    //Evaluate Criteria
            	    //Rise
            	    double precision_rise = 0;
                    if (True_Positive == 0 ) {        	
                    	precision_rise = 0;
                    } else {
                    	precision_rise =  True_Positive / (double)(True_Positive + False_Positive);                      
                    }       
            	    
                    double recall_rise =  True_Positive / (double)(True_Positive + False_Negative);
                    
                    //Down                   
                    double precision_down = 0;
                    if (True_Negative == 0 ) {        	
                    	precision_down = 0;
                    } else {
                    	precision_down =  True_Negative / (double)(True_Negative +  False_Negative);                       
                    }     
                    int size = True_Negative +  True_Positive + False_Positive + False_Negative;
                    double acc =  (True_Positive + True_Negative)/ (double)(size);
                    double recall_down =  True_Negative / (double)(True_Negative + False_Positive);                    
                    double macro_precision = ( precision_rise + precision_down) / (double) 2;
                    double macro_recall = ( recall_rise + recall_down) / (double) 2;
                    double macro_f_measure = 2*(macro_precision*macro_recall)/ (macro_precision+macro_recall);
                    double applicability = avaliable_count / (double) (test.numInstances()-2);
                	File fout = new File(output_path + "DT_"+ period + "_" + minsup +".arff");                	
             	    FileOutputStream fos = new FileOutputStream(fout);
                    OutputStreamWriter osw = new OutputStreamWriter(fos);            	

            		osw.write("=== Confusion Matrix ===\r\n");
            		osw.write("          a      b\r\n");
            		osw.write("a=Rise   " + True_Positive + "\t" + False_Negative + "\r\n");
           		    osw.write("b=Down   " + False_Positive + "\t" + True_Negative+ "\r\n");		
            		osw.write("precision_rise: " + precision_rise+ "\r\n");
            		osw.write("recall_rise: " + recall_rise + "\r\n");
            		osw.write("precision_down: " + precision_down+ "\r\n");
           		    osw.write("recall_down: " + recall_down + "\r\n");
                    osw.write("macro_precision: " + macro_precision+ "\r\n");
                    osw.write("macro_recall: " + macro_recall+ "\r\n");
                    osw.write("macro_f_measure: " + macro_f_measure+ "\r\n");
            		osw.write("acc: "               + acc+ "\r\n");
            		osw.write("applicability: "               +applicability+ "\r\n");
            		osw.write("\r\n");
            		osw.write("\r\n");
    		        osw.close();
    		        
                }catch (IOException e) {
    	        	System.out.println("[ERROR] I/O Exception.");
    	            e.printStackTrace();  	
    	        }   		           
		    
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
	
	
	 public static ArrayList<ArrayList<String>> read_text_weka(String filename) throws FileNotFoundException {
	     ArrayList<ArrayList<String>> result = new ArrayList<>();    	
	     Scanner sc = new Scanner(new File(filename));
	     int i = 1;
	     while(sc.hasNextLine()){
		     String[] tokens = sc.nextLine().split(", ");  
			 ArrayList<String> temp = new ArrayList<>();  
			 if (i == 1) {		    
			     for (String s : tokens) {
			         temp.add(s);
			     }   
			     i--;
			     result.add(temp); 
			 } else {
			     for (String s : tokens) {
			         temp.add(s);
			     }   
			     result.add(temp); 
			 }
		  }
		  return result;			
	}
	 
	public static void main(String[] args) throws Exception {		
		/**參數設定**/		
		int N = 5;
		int Original_Level = 0;
		int Original_Relative = 0;
		int Original_Data = 0;
		int MA_Relative = 0;
		int MA_N = 0;
        int MA_Diff = 0;
		int user_defined_class = 1;
        int minsup = 0;
        int class_two = 0;
        if (args.length < 4) {
		    System.out.println("Please input: (1) data_path  (2) preprocessing_path  (3) output_path  (4) periods"); 	
		}
        
		String data_path = args[0];
		String preprocessing_path = args[1];
		String output_path = args[2];
		//選MA BIAS的週期
		int period = Integer.parseInt(args[3]); 
				
		/**擷取類別**/    
		String path = data_path;    	    
	    ArrayList<ArrayList<String>> records = readCSV(path);
	    HashMap<Integer, String> feature_target = new HashMap<>();	    
	    if (user_defined_class == 1) {
	    	feature_target = GetAttr.featureExtraction_target_user_defined(records);
//	    	for (Integer index : feature_target.keySet()) {
//	    		System.out.println(index + ", " + feature_target.get(index));
//	    	}
	    } else {
	    	feature_target = GetAttr.featureExtraction_target(records);
	    }  
//	    SAXTransformation.start("target1.txt");
//		SAXTransformation_Testing.start("breakpoint_target_1.txt");

//		SAXTransformation.start("target2.txt");
//		SAXTransformation_Testing.start("breakpoint_target_2.txt");
	    int debug = 0;
		if (debug == 0) {
	    //先取BIAS與MA
	    String output = "transformed_petro_subset1_feature.csv";
		GetAttr.featureExtraction_N(output, records, feature_target, period);	
		
		/**SAX for BIAS**/
		SAXTransformation.start("SAXTransformation_config_petro_subset1_2010.txt");
		SAXTransformation_Testing.start("petro_subset1_breakpoints_2010.txt");
		System.out.println("Done for SAX!");
		
		//轉成Sequence
		String path_after_discrete = "transformed_petro_subset1_feature_for_sax_training.csv";
		T2SDB t = new T2SDB();
		int SDB_Training_Size = t.translate_training_sliding_window(N, path_after_discrete,  feature_target, "SDB(Training).txt");
		System.out.println("Done for Sequence(Training)!");
		String path_of_testing_file = "transformed_petro_subset1_feature_for_sax_testing.csv";
        int SDB_Testing_Size = t.translate_testing_sliding_window(N, path_of_testing_file, "SDB(Testing).txt");		
        System.out.println("Done for Sequence(Testing)!");
       
			
        for (minsup = 152; minsup <= 152; minsup++) {
	    /**Sequential Pattern Mining**/
        sequential_pattern_mining(minsup);
        
	    /**讀取Sequence**/
	    ArrayList<ArrayList<ArrayList<String>>> sequences = ReadSDB_for_sequence("sequential_patterns.txt");
//	    for (ArrayList<ArrayList<String>> sequence : sequences) {
//	    	System.out.println(sequence);
//	    }
	   

	    /**產生Sequential Feature*/	    
	    HashMap<Integer, ArrayList<Integer>> SF = GetAttr.sequential_feture(records, sequences, ReadSDB_for_testing("SDB(Testing).txt"), Read_Training_Data("SDB(Training).txt"));	    
//	    System.out.println("Done for Rule!");	
	    //for (int index : SF.keySet()) {
	    //	System.out.println(index);
	    //}
	    
	    
		String output_fe_weka = preprocessing_path + "weka_"  + period + "_" + minsup +".csv" ;				
    	GetAttr.featureExtraction_weka(output_fe_weka , records, feature_target, period);  
    	//System.out.println(para_list);
    	/**Translate To SDB**/
    	/**1.Training Data**/
    	    
        T2SDB t2sdb = new T2SDB();   
    	String input = output_fe_weka;
    	String output_txt =  preprocessing_path+"weka_training_" + period + "_" + minsup +".txt";
    	t2sdb.translate_training_sliding_window_weka_including_level_new(N, input, feature_target, output_txt, Original_Level, records, records.get(0).size()-1, SF);
    	
    	    
    	try {
    	    String input_of_read_txt = output_txt;
            ArrayList<ArrayList<String>> txt_training = read_text_weka(input_of_read_txt);  
            try {
                String output_of_txt = preprocessing_path + "weka_training_" + period + "_" + minsup +".csv";
    		    writeCSV("", output_of_txt, txt_training);
    		} catch (IOException e) {
   			    System.out.println("[ERROR] I/O Exception.");
    			e.printStackTrace();
   		    }  
        } catch (FileNotFoundException e) {
               
        }
    	    
    	    
    	/**load CSV**/
    	CSVLoader loader = new CSVLoader();
    	String input_for_csvloader = preprocessing_path + "weka_training_" + period + "_" + minsup +".csv";
    	loader.setSource(new File(input_for_csvloader));
    	Instances data1 = loader.getDataSet();
    	    
    	/**save ARFF**/
    	ArffSaver saver = new ArffSaver();
    	saver.setInstances(data1);
    	String output_arff = preprocessing_path + "weka_training_" + period + "_" + minsup +".arff";
    	saver.setFile(new File(output_arff));
    	//saver.setDestination(new File(args[1]));
    	saver.writeBatch();    	    
    	run(period, minsup, preprocessing_path, output_path, class_two);
            
		}
		
        }
	}
	
		
	
	
	private static void sequential_feture(HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> readRules,
			HashMap<Integer, ArrayList<ArrayList<String>>> readSDB_for_testing,
			HashMap<Integer, ArrayList<ArrayList<String>>> read_Training_Data) {
		// TODO Auto-generated method stub
		
	}

	//讀取SDB(Training).txt
	static HashMap<Integer, ArrayList<ArrayList<String>>> Read_Training_Data(String filename) throws FileNotFoundException{
	    HashMap<Integer, ArrayList<ArrayList<String>>> result = new HashMap<>();
	    int index = 1;        
	    Scanner sc = new Scanner(new File(filename));        
	    while(sc.hasNextLine()) {        
	        ArrayList<ArrayList<String>> itemsets = new ArrayList<>();
	        String[] tokens = sc.nextLine().split(" -1 -2");
	        String[] tokens_next = tokens[0].split(" -1 ");
	        for (String s : tokens_next) {
	            ArrayList<String> itemset = new ArrayList<>();
	            String[] tokens_next_next = s.split(" ");
	            for (String ss : tokens_next_next) {
	                itemset.add(ss);
	            }
	            itemsets.add(itemset);
	        }
	        result.put(index, itemsets);
	        index = index + 1;
	    }            
	    /*
	    //debug
	    for (Integer i : result.keySet()) {
		    System.out.println(i + " " + result.get(i));
		    
		}*/
	    //System.out.println(result.size());
	    sc.close();
	    return result;	        
	}
	
	
	//讀取SDB(Testing).txt
	static HashMap<Integer, ArrayList<ArrayList<String>>> ReadSDB_for_testing(String filename) throws FileNotFoundException{
        HashMap<Integer, ArrayList<ArrayList<String>>> result = new HashMap<>();
        int index = 1;        
        Scanner sc = new Scanner(new File(filename));        
        while(sc.hasNextLine()) {        
            ArrayList<ArrayList<String>> itemsets = new ArrayList<>();
         
            String[] tokens = sc.nextLine().split(" -1 -2");
            String[] tokens_next = tokens[0].split(" -1 ");
            for (String s : tokens_next) {
                ArrayList<String> itemset = new ArrayList<>();
                String[] tokens_next_next = s.split(" ");
                for (String ss : tokens_next_next) {
                    itemset.add(ss);
                }
                itemsets.add(itemset);
            }
            result.put(index, itemsets);
            index = index + 1;
        }            
        /*
        //debug
        for (Integer i : result.keySet()) {
	        System.out.println(i + " " + result.get(i));
	    
	    }*/
        //System.out.println(result.size());
        sc.close();
        return result;
        
    }
	
	//讀取Sequence
    static ArrayList<ArrayList<ArrayList<String>>> ReadSDB_for_sequence(String filename) throws FileNotFoundException{
	    ArrayList<ArrayList<ArrayList<String>>> result = new ArrayList<>();     
	    Scanner sc = new Scanner(new File(filename));        
	    while(sc.hasNextLine()) {        
	        ArrayList<ArrayList<String>> itemsets = new ArrayList<>();
	        String[] tokens = sc.nextLine().split(" -1  #SUP: ");
	        String[] tokens_next = tokens[0].split(" -1 ");
	        for (String s : tokens_next) {
	            ArrayList<String> itemset = new ArrayList<>();
	            String[] tokens_next_next = s.split(" ");
	            for (String ss : tokens_next_next) {
	                itemset.add(ss);
	            }
	            itemsets.add(itemset);
	        }
	      //  if (itemsets.size()==1) {

	      //  } else {
	        	
	            result.add(itemsets);
	       // }
	     }      
	     /*
	     int inter = 0;
	     //看交錯情形
	     int inter1 = 0, inter2 = 0, inter3 = 0, inter4 = 0;
	     for (ArrayList<ArrayList<String>> sequence : result) {
	    	 int max = 0;
	    	 ArrayList<String> first_itemset = sequence.get(0);	    	 
	    	 for (String first_item : first_itemset) {
	    		 int cur = 0;
	    		 for (int i = 1; i < sequence.size(); i++) {
	    			 ArrayList<String> next_itemset = sequence.get(i);
	    			 for (int j = 0; j < next_itemset.size(); j++) {
	    			     String next_item = next_itemset.get(j);
	    			     //判斷是否交錯
	    			     //System.out.println(first_item.subSequence(0, 2) + " " +next_item.subSequence(0, 2));
	    			     if (first_item.subSequence(0, 2).equals(next_item.subSequence(0, 2))) {
	    			    	 //有交錯
	    			    	 if (first_item.charAt(first_item.length()-3) != next_item.charAt(next_item.length()-3)) {
	    			    	     cur++;
	    			    	     if (cur > max) {
	    			    	    	 max = cur;
	    			    	     }
	    			    		 break;
	    			    	     
	    			    	 }
	    			     //有交錯
	    			     } else {
	    			    	 cur++;
	    			    	 if (cur > max) {
    			    	    	 max = cur;
    			    	     }
	    			    	 break;
	    			     }
	    			 }
	    		 }
	    	 }
	    	 max++;
	    	 System.out.println(sequence+ "  " + max);
	    	 //計算交錯個數
	    	 
	    	 if (max == 1) {
    			 inter1++;
    		 }
	    	 
	    	 if ((max) >=2 ) {
	    		 
	    		 if ( max== 2) {
	    			 inter2++;	 
	    		 } else if ( max > 2) {
	    			 inter3 ++;
	    		 } 
	    		 inter++;
	    	 }
	    	
	    	 
	     }*/
	     /*
	     System.out.println("Interaction: " + (inter/(double) result.size()) );
	     System.out.println("1: " + (inter1/(double) result.size()) );
	     System.out.println("2: " + (inter2/(double)  inter) );
	     System.out.println("3: " + (inter3/(double)  inter) );
	     sc.close();
	     */
	     System.out.println("After reducing: " + result.size());
	     return result;
	        
	}	
		
	//Read rule file
    static HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> readRules(String filename) throws FileNotFoundException{
	        
		HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> result = new HashMap<>();
				
		Scanner sc = new Scanner(new File(filename));
		while(sc.hasNextLine()){
		
		    ArrayList<ArrayList<String>> itemsets = new ArrayList<>();
		    ArrayList<Double> list = new ArrayList<>();
			String[] tokens = sc.nextLine().split("\t:\t");
			//For sup, confidence
			String[] number = tokens[1].split(",\t");
			for (String s : number) {
			    double n = Double.parseDouble(s);
			    list.add(n);
			}
			
			//For items
			String[] tokens_next = tokens[0].split(" -> ");
			String[] tokens_next_next = tokens_next[0].split(" -1 ");
			
			//tokens_next[1] : Rise/Down
			ArrayList<String> itemset_next = new ArrayList<>();
			itemset_next.add(tokens_next[1]);
			
			for(String s : tokens_next_next) {
			    String[] tokens_next_next_next =  s.split(" ");
			    ArrayList<String> itemset = new ArrayList<>();   
			    for(String ss : tokens_next_next_next) {
			        itemset.add(ss);    			    
			    }
			    itemsets.add(itemset);
            }
			itemsets.add(itemset_next);		
			result.put(itemsets, list);
			
		}
		/*
		//debug
		for (ArrayList<ArrayList<String>> key : result.keySet()) {
		    System.out.println(key + " " + result.get(key));
		
		}*/
		
		sc.close();
		return result;	
    }	
    
    public static void sequential_pattern_mining(int minsup) throws IOException {
    	SequenceDatabase sequenceDatabase = new SequenceDatabase();
 	    sequenceDatabase.loadFile("SDB(Training).txt");	    
 	    AlgoPrefixSpan_with_Strings algo = new AlgoPrefixSpan_with_Strings(); 
 	    algo.runAlgorithm(sequenceDatabase, "sequential_patterns.txt", minsup);
 	    algo.printStatistics(sequenceDatabase.size());
 	    System.out.println("Done for Mining!");	    	
    }
	
}
