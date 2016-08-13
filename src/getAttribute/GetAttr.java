package getAttribute;

import java.io.*;
import java.util.*;





public class GetAttr {
	private static HashMap<Integer, Double> temp_sl = new HashMap<>();
	private static HashMap<Integer, Double> temp_ll = new HashMap<>();
	
	public static HashMap<Integer, String> FS_oil(int att_index, ArrayList<ArrayList<String>> records) {
		 HashMap<Integer, String> result = new HashMap<>(); 	    
	     int col = att_index; 
	     int rise_number = 0;
	     int down_number = 0;
	     for (int i = 1; i < records.size(); i++ ) {       
	            if (i == 1) {	                
	                continue;
	            }
	            
	            if (Double.parseDouble(records.get(i).get(col))- Double.parseDouble(records.get(i-1).get(col)) > 0 ) {
	    	    	result.put(i, "R");     
	    	    	rise_number++;
	    	    } else {
	    	    	result.put(i, "D");  
	    	    	down_number++;
	    	    }	
        } 
	    for (int i = 1; i < records.size(); i++) {
	    	if (result.get(i) == null) {
	    		if (rise_number > down_number) {
	    			result.put(i, "R");	    			
	    		} else {
	    			result.put(i, "D");	    
	    		}
	    	}
	    }
	     
	    return result;		
	}
	
	//得到Sequence
	public static ArrayList<String> getsequence(ArrayList<String> sequence) {
		ArrayList<String> result = new ArrayList<>();	
		for (int i = 0; i < sequence.size(); i++) {
			result.add(sequence.get(i));
		}
		return result;
	}
	
	//得到Rule的前項
	public static ArrayList<ArrayList<String>> get_prefix(ArrayList<ArrayList<String>> rule) {
		ArrayList<ArrayList<String>> result = new ArrayList<>();	
		for (int i = 0; i < rule.size()-1; i++) {
			ArrayList<String> temp = new ArrayList<>();
			temp = getsequence(rule.get(i));
			result.add(temp);
		}
	    return result;
	}
	
	public static HashMap<Integer, ArrayList<Integer>> sequential_feture(ArrayList<ArrayList<String>> records, final ArrayList<ArrayList<ArrayList<String>>> sequences, HashMap<Integer, ArrayList<ArrayList<String>>> SDB_for_testing, HashMap<Integer, ArrayList<ArrayList<String>>> SDB_for_training) {
		//刪除Conflict rules				
		/*
		//對規則做排序:1.confidence 2.support 3.length
		Collections.sort(rule_set_before_top_k, new Comparator<ArrayList<ArrayList<String>>>() {
			@Override
			public int compare(ArrayList<ArrayList<String>> rule1, ArrayList<ArrayList<String>> rule2) {
				double conf_1 = rules.get(rule1).get(1);
				double conf_2 = rules.get(rule2).get(1);
				if (conf_1 > conf_2) {
					return -1;
				} else if (conf_1 == conf_2) {
					double sup_1 = rules.get(rule1).get(0);
					double sup_2 = rules.get(rule2).get(0);
					if (sup_1 > sup_2) {
						return -1;
					} else if (sup_1 == sup_2) {
						int len_1 = rules.get(rule1).size()-1;
						int len_2 = rules.get(rule2).size()-1;
						return len_1 - len_2;
					} else {
						return 1;
					}
				} else {
					return 1;
				}
			}	
		});		
		//for (ArrayList<ArrayList<String>> rule : rule_set_before_top_k) {			
		//	System.out.println("Conf: "+ rules.get(rule).get(1));		
		//}
		int count = 0;
		ArrayList<ArrayList<ArrayList<String>>> rule_set = new ArrayList<>();
		for (ArrayList<ArrayList<String>> rule : rule_set_before_top_k) {
			if (count < top_k) {
//				System.out.println(rules.get(rule).get(1));
		        rule_set.add(rule);	
		        count++;
			}
		}*/
		
		
		
		
		
		/*
		for (int i = 0 ; i < rule_set.size(); i++) {
    	    boolean same = false;
    	    for (int j = i+1; j < rule_set.size(); j++) {
    	        ArrayList<ArrayList<String>> temp1 = new ArrayList<>();
    		    for (int k1 = 0; k1 < rule_set.get(i).size()-1; k1++) {
    		        temp1.add(rule_set.get(i).get(k1));
    	        }    
    	        String str1 = rule_set.get(i).get(rule_set.get(i).size()-1).get(0);
    	        ArrayList<ArrayList<String>> temp2 = new ArrayList<>();
    		    for (int k1 = 0; k1 < rule_set.get(j).size()-1; k1++) {
    		        temp2.add(rule_set.get(j).get(k1));
    	        }
    	        String str2 = rule_set.get(j).get(rule_set.get(j).size()-1).get(0);
    	        if ((temp1.equals(temp2)) && (!str1.equals(str2))) {
    	        	//PRINT DUPLICATES
    	        	//int index_1 = rules_all_index.get(rule_set.get(i));  
    	        	//int index_2 = rules_all_index.get(rule_set.get(j));
    	        	//osw.write(index_1 + "    " + index_2 + "\r\n");
    	        	//osw.write(rules.get(rule_set.get(i)).get(1) + "    " + rules.get(rule_set.get(j)).get(1) + "\r\n");
    		        same = true;
    		        rule_set.remove(j--);		    		        	
    		        break;
    	        } 
    	    }    
    	    if (same) {
    	        //System.out.println(i);
    	    	
    	    		rule_set.remove(i--);	 
    	    	
       
    	    }
    	}	*/
		
		
		
		
		
		HashMap<Integer, ArrayList<Integer>> result = new HashMap<>();    	
		int i = 0;		
//		System.out.println("SDB_for_training size: " + SDB_for_training.size());
//		System.out.println("SDB_for_testing size: " + SDB_for_testing.size());
		for (i = 1; i <= SDB_for_training.size(); i++) {
			ArrayList<Integer> match = new ArrayList<>();
			//欲檢查的sequence
			ArrayList<ArrayList<String>> sequence = SDB_for_training.get(i);
		    for (ArrayList<ArrayList<String>> pattern : sequences) {		    		
		    	//System.out.println(rule);
		    	//System.out.println(prefix_of_rule);
		    	//看每個Sequence是否包含了Rule's prefix
		    	int size = 0;
                int current = 0;
                for (int i_1 = 0; i_1 < sequence.size(); i_1++) {                	
                    for (int j = current; j < pattern.size(); j++) {                                         
                        if (sequence.get(i_1).containsAll(pattern.get(j))) {    
                            current = j;
                            current++;
                            size++;
                        }   
                        break;
                    }                                                            
           
                }        
                //有包含Rule's prefix
                if (size == pattern.size()) {
                    match.add(1);            	                      	
                } else {
                	match.add(0);
                }
		    }
		    result.put(i, match);
		}
		int training_data = (int)((records.size()- 1)*0.8);
        for (int j = 1; j <= SDB_for_testing.size(); j++) {
        	ArrayList<Integer> match = new ArrayList<>();
			//欲檢查的sequence
			ArrayList<ArrayList<String>> sequence = SDB_for_testing.get(j);
		    for (ArrayList<ArrayList<String>> pattern : sequences) {
		    	//System.out.println("s:  " + sequence);
		    	//System.out.println("r:  " + prefix_of_rule);
		    	//看每個Sequence是否包含了Rule's prefix
		    	int size = 0;
                int current = 0;
                for (int i_1 = 0; i_1 < sequence.size(); i_1++) {                	
                    for (int j_1 = current; j_1 < pattern.size(); j_1++) {                                         
                        if (sequence.get(i_1).containsAll(pattern.get(j_1))) {    
                            current = j_1;
                            current++;
                            size++;
                        }   
                        break;
                    }                                                            
           
                }        
                //有包含Rule's prefix
                if (size == pattern.size()) {
                	//System.out.println("Yes");
                    match.add(1);            	                      	
                } else {
                	match.add(0);
                }	
		    }
		    result.put(training_data + j, match);
		}
//        System.out.println("feature size: " +result.size());
		return result;
	}
	public static HashMap<Integer, Double> BIAS_N(int length, int att_index, ArrayList<ArrayList<String>> records) {
    	HashMap<Integer, Double> result = new HashMap<>();
    	int col = att_index;   
    	for (int i = 1; i < records.size(); i++) {
    		double bias;
    	    if (i <= length-1) {
    	    	
    	    } else {
    	    	double sum_t = 0;
    	    	if (i - length + 1 >= 1) {
    	    		for (int j = i; j >= i-length+1; j--) {                
                        sum_t = sum_t + Double.parseDouble(records.get(j).get(col));
                    } 	    	    		
    	    	}
    	    	sum_t = sum_t / (double)length;
    	    	bias = (Double.parseDouble(records.get(i).get(att_index)) - sum_t)/(double) sum_t;
    	    	result.put(i, bias);  	    	
    	    }    		
    	}
    	
    	double average =  0.0;
    	for (int i = 1; i <= result.size(); i++) {
    		if (result.get(i) != null) {
    			average += result.get(i);
    		}
    	}
    	
    	average /= (double) result.size();
    	for (int i = 1; i < result.size(); i++) {
    		if (result.get(i) == null) {
    			result.put(i, average);
    		}
    	}

    	return result;
    }	
	//length是要選定的週期
	public static HashMap<Integer, String> Move_Average(int length, String att, int att_index, ArrayList<ArrayList<String>> records) {
        HashMap<Integer, String> result = new HashMap<>();    
        //The column of Target
        int col = att_index;                                                                                                                            
        for (int i = 1; i < records.size(); i++ ) {       
            if (i <= length) {
                result.put(i, "MA"+ length+ "_"+ col  + "_1");     
                continue;
            }
            
            double sum_t = 0;
            double sum_t_1 = 0;
            if (i -length + 1 >= 1) {         
                for (int p_1 = i; p_1 >= i-length+1; p_1--) {                
                    sum_t = sum_t + Double.parseDouble(records.get(p_1).get(col));
                } 
                     
                int j = i - 1;
                if (j - length + 1 >=1) {
                    
                    for (int p_2 = j; p_2 >= j-length+1; p_2--) {
                       
                        sum_t_1 = sum_t_1 + Double.parseDouble(records.get(p_2).get(col));
                    }
                }
            }          
            
            //Rise or Down
            double MA = sum_t/length - sum_t_1/length;     
            if (MA >= 0) {
                //System.out.println("i: " + i + " " + MA);
                result.put(i, "MA"+ length+ "_"+ col  + "_1");    
            } else {
                //System.out.println("i: " + i + " " + MA);
                result.put(i, "MA"+ length+ "_"+ col  + "_0"); 
            }              
        }            
        return result;
    }    	
	//擷取Numerical BIAS
	public static void featureExtraction_N(String output_filename, ArrayList<ArrayList<String>> records, HashMap<Integer, String> feature_target, int period_for_MA_BIAS) {		
    	ArrayList<ArrayList<String>> result = new ArrayList<>();
    	HashMap<Integer, Double> BIAS_N_4 = BIAS_N(period_for_MA_BIAS, 4,records);
    	HashMap<Integer, Double> BIAS_N_3= BIAS_N(period_for_MA_BIAS, 3,records);
    	HashMap<Integer, Double> BIAS_N_2 = BIAS_N(period_for_MA_BIAS, 2,records);
    	HashMap<Integer, Double> BIAS_N_1 = BIAS_N(period_for_MA_BIAS, 1,records);    	
    	HashMap<Integer, String> FT_but = feature(4, records);
    	
    	HashMap<Integer, String> MA_4 = Move_Average(period_for_MA_BIAS, records.get(0).get(4), 4, records);
    	HashMap<Integer, String> MA_3 = Move_Average(period_for_MA_BIAS, records.get(0).get(3), 3, records);
    	HashMap<Integer, String> MA_2 = Move_Average(period_for_MA_BIAS, records.get(0).get(2), 2, records);
    	HashMap<Integer, String> MA_1 = Move_Average(period_for_MA_BIAS, records.get(0).get(1), 1, records);
    	
		int training_data_size = (int) ((records.size()-1)*0.8);

		for (int i = 0; i < records.size(); i++) {		
			ArrayList<String> temp = new ArrayList<>();
			//Add time
			temp.add(records.get(i).get(0));
			if(i == 0) {
				
				temp.add("BIAS_N_1");
				temp.add("BIAS_N_2");
				temp.add("BIAS_N_3");
				temp.add("BIAS_N_4");	
				temp.add("MA_1");
				temp.add("MA_2");
				temp.add("MA_3");
				temp.add("MA_4");	
//				temp.add("Target");
			} else {
				
				temp.add(String.valueOf(BIAS_N_1.get(i)));
				temp.add(String.valueOf(BIAS_N_2.get(i)));
				temp.add(String.valueOf(BIAS_N_3.get(i)));
				temp.add(String.valueOf(BIAS_N_4.get(i)));
				temp.add(MA_1.get(i));
				temp.add(MA_2.get(i));
				temp.add(MA_3.get(i));
				temp.add(MA_4.get(i));
//				temp.add(feature_target.get(i));
			}	
			//temp.add(records.get(i).get(records.get(i).size()-1));	
			result.add(temp);
		}		
		try {
		writeCSV("", output_filename,result);
		} catch (IOException e) {
			System.out.println("[ERROR] I/O Exception.");
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public static HashMap<Integer, String> feature(int att_index, ArrayList<ArrayList<String>> records) {
		 HashMap<Integer, String> result = new HashMap<>(); 	    
	     int col = att_index; 
	     int rise_number = 0;
	     int down_number = 0;
	     for (int i = 1; i < records.size(); i++ ) {       
	            if (i == 1) {	                
	                continue;
	            }
	            
	            if (Double.parseDouble(records.get(i).get(col))- Double.parseDouble(records.get(i-1).get(col)) > 0 ) {
	    	    	result.put(i, "R");     
	    	    	rise_number++;
	    	    } else {
	    	    	result.put(i, "D");  
	    	    	down_number++;
	    	    }	
       } 
	    for (int i = 1; i < records.size(); i++) {
	    	if (result.get(i) == null) {
	    		if (rise_number > down_number) {
	    			result.put(i, "R");	    			
	    		} else {
	    			result.put(i, "D");	    
	    		}
	    	}
	    }
	     
	    return result;		
	}
	public static HashMap<Integer, String> feature_categories(int att_index, ArrayList<ArrayList<String>> records, int rise, int down) {
		 HashMap<Integer, String> result = new HashMap<>(); 	    
	     int col = att_index; 
	     for (int i = 1; i < records.size(); i++ ) {       
	            if (i == 1) {
	                result.put(i, Integer.toString(rise));     
	                continue;
	            }
	            
	            if (Double.parseDouble(records.get(i).get(col))- Double.parseDouble(records.get(i-1).get(col)) > 0 ) {
	    	    	result.put(i, Integer.toString(rise));     
	    	    } else {
	    	    	result.put(i, Integer.toString(down));  
	    	    }	
        }       	        	
	    return result;		
	}
	
	
	
	public static HashMap<Integer, Double> feature2_weka(int att_index, ArrayList<ArrayList<String>> records) {
		 HashMap<Integer, Double> result = new HashMap<>(); 	    
	     int col = att_index; 
	     for (int i = 1; i < records.size(); i++ ) {       
	            if (i == 1) {
	            	
	            } else {	            
	                double min = Double.parseDouble(records.get(i).get(col))- Double.parseDouble(records.get(i-1).get(col));
	                result.put(i, min);
	            }
         }    
	     
	     double average =  0.0;
	    	for (int i = 1; i < result.size(); i++) {
	    		if (result.get(i) != null) {
	    			average += result.get(i);
	    		}
	    	}
	    	
	    	average /= (double) result.size();
	    	for (int i = 1; i < result.size(); i++) {
	    		if (result.get(i)== null) {
	    			result.put(i, average);
	    		}
	    	}
	     return result;		
	}
	
	public static HashMap<Integer, String> feature2(int att_index, ArrayList<ArrayList<String>> records) {
		 HashMap<Integer, String> result = new HashMap<>(); 	    
	     int col = att_index; 
	     for (int i = 1; i < records.size(); i++ ) {       
	            if (i == 1) {
	            	result.put(i, records.get(0).get(col) + "_R");      
	                continue;
	            }
	            
	            if (Double.parseDouble(records.get(i).get(col))- Double.parseDouble(records.get(i-1).get(col)) >= 0 ) {
	    	    	result.put(i,  records.get(0).get(col) + "_R");     
	    	    } else {
	    	    	result.put(i,  records.get(0).get(col) + "_D");  
	    	    }	
       }       	        	
	    return result;		
	}
	
	
	public static HashMap<Integer, String> match_source_target(HashMap<Integer, String> s, HashMap<Integer, String> t, int sou, int tar) {
		HashMap<Integer, String> result = new HashMap<>(); 
	    for (int i = 1;i <= t.size(); i++) {
	        if (s.get(i) == t.get(i)) {
	        	result.put(i, "Same" + "_" + sou + "_" + tar);
	        } else {
	        	result.put(i, "Diff" + "_" + sou + "_" + tar);
	        }
	    }	    
	    return result;
	}
	
	
	public static HashMap<Integer, String> match_source_target_categories(HashMap<Integer, String> s, HashMap<Integer, String> t, int rise, int down) {
		HashMap<Integer, String> result = new HashMap<>(); 
	    for (int i = 1;i <= t.size(); i++) {
	        if (s.get(i) == t.get(i)) {
	        	result.put(i, Integer.toString(rise));
	        } else {
	        	result.put(i, Integer.toString(down));
	        }
	    }	    
	    return result;
	}
	
	public static HashMap<Integer, String> match_source_target_technical(HashMap<Integer, String> s, HashMap<Integer, String> t, int sou, int tar, String str) {
		HashMap<Integer, String> result = new HashMap<>(); 
	    for (int i = 1;i <= t.size(); i++) {
	    	char s1 = s.get(i).charAt(s.get(i).length()-1);
	    	char t1 = t.get(i).charAt(t.get(i).length()-1);
	        if (s1 == t1) {
	        	result.put(i, "Same" + "_" + sou + "_" + tar + "_" + str);
	        } else {
	        	result.put(i, "Diff" + "_" + sou + "_" + tar + "_" + str);
	        }
	    }	    
	    return result;
	}
	
	
	public static HashMap<Integer, String> Move_Average_same(int length1, int length2, String att, int att_index, ArrayList<ArrayList<String>> records) {
		HashMap<Integer, String> result = new HashMap<>();
		int col = att_index;   
		for (int i = 1; i < records.size(); i++ ) {       
	           if (i <= length1) {
	               result.put(i, "MAa"+ att.charAt(0) + length1 + "_1");     
	               continue;
	           }
	           double sum_t1 = 0;
	           double sum_t2= 0;
	           if (i - length1 + 1 >= 1) { 
	        	   for (int p_1 = i; p_1 >= i-length1+1; p_1--) {                
	                    sum_t1 = sum_t1+ Double.parseDouble(records.get(p_1).get(col));
	        	   }
	           }
	           if (i - length2 + 1 >= 1) { 
	        	   for (int p_1 = i; p_1 >= i-length2+1; p_1--) {                
	                    sum_t2 = sum_t2 + Double.parseDouble(records.get(p_1).get(col));
	        	   }
	           }
	           double MA = sum_t1/length1 - sum_t2/length2;
	           if (MA >= 0) {	                
	                result.put(i, "MAa" + att.charAt(0) + length1 + "_1");    
	           } else {	                
	                result.put(i, "MAa" + att.charAt(0) + length1 + "_0"); 
	           }       	           
		}
		return result;
	}
	
	//有差值Moving Average
	public static HashMap<Integer, Double> Move_Average_Diff_Numeric(int period, int att_index, ArrayList<ArrayList<String>> records) {
        HashMap<Integer, Double> result = new HashMap<>();    
        //The column of Target
        int col = att_index;                                                                                                                            
        for (int i = 1; i < records.size(); i++ ) {       
            if (i < period) {
              
            } else {
            
                double sum_t = 0;
                double sum_t_1 = 0;
                if (i - period + 1 >= 1) {         
                    for (int p_1 = i; p_1 >= i-period+1; p_1--) {                
                        sum_t = sum_t + Double.parseDouble(records.get(p_1).get(col));
                    } 
                     
                    int j = i - 1;
                    if (j - period + 1 >=1) {
                    
                        for (int p_2 = j; p_2 >= j-period+1; p_2--) {
                       
                            sum_t_1 = sum_t_1 + Double.parseDouble(records.get(p_2).get(col));
                        }
                    }
                }          
            
                double MA = sum_t/period - sum_t_1/period;     
                result.put(i, MA);
            
            }
        }  
        
        double average =  0.0;
    	for (int i = 1; i < result.size(); i++) {
    		if (result.get(i) != null) {
    			average += result.get(i);
    		}
    	}
    	
    	average /= (double) result.size();
    	for (int i = 1; i < records.size(); i++) {
    		if (result.get(i) == null) {
    			result.put(i, average);
    		}
    	}
        return result;
    }    
	
	public static HashMap<Integer, Double> Move_Average_Numeric(int period, int att_index, ArrayList<ArrayList<String>> records) {
        HashMap<Integer, Double> result = new HashMap<>();                                                                                                                               
        for (int i = 1; i < records.size(); i++ ) {       
            if (i < period) {
               
            } else {
                double sum_t = 0;
                if (i - period + 1 >= 1) {         
                    for (int p_1 = i; p_1 >= i-period+1; p_1--) {                
                        sum_t = sum_t + Double.parseDouble(records.get(p_1).get(att_index));
                    } 
                     
                
                }          
                double MA = sum_t/period;     
                result.put(i, MA);
            }
        }  
        
        double average =  0.0;
    	for (int i = 1; i < result.size(); i++) {
    		if (result.get(i) != null) {
    			average += result.get(i);
    		}
    	}
//    	System.out.println(average);
    	average /= (double) result.size();
    	for (int i = 1; i < records.size(); i++) {
    		if (result.get(i) == null) {
    			result.put(i, average);
    		}
    	}
        return result;
    }    
	
    
	 
    public static HashMap<Integer, String> BIAS(int length, int att_index, double threshold, ArrayList<ArrayList<String>> records) {
    	HashMap<Integer, String> result = new HashMap<>();
    	int col = att_index;   
    	int rise_number = 0;
    	int down_number = 0;
    	for (int i = 1; i < records.size(); i++) {
    		double bias;
    	    if (i <= length-1) {
    	    	//result.put(i, "BIAS_" + records.get(0).get(att_index).charAt(0) + "_" + length + "_" + threshold + "_1");
    	    } else {
    	    	double sum_t = 0;
    	    	if (i - length + 1 >= 1) {
    	    		for (int j = i; j >= i-length+1; j--) {                
                        sum_t = sum_t + Double.parseDouble(records.get(j).get(col));
                    } 	    	    		
    	    	}
    	    	sum_t = sum_t / (double)length;
    	    	bias = (Double.parseDouble(records.get(i).get(att_index)) - sum_t)/(double) sum_t;
    	    	if (bias >= threshold) {
    	    		result.put(i, "BIAS_" + records.get(0).get(att_index).charAt(0) + "_" + length + "_" + threshold + "_1");	
    	    		 rise_number++;
    	    	} else {
    	    		result.put(i, "BIAS_" + records.get(0).get(att_index).charAt(0) + "_" + length + "_" + threshold + "_0");	
    	    		down_number++;
    	    	}    	    	
    	    }
    		
    	}
    	for (int i = 1; i < records.size(); i++) {
    		if (result.get(i) == null) {
    			if (rise_number > down_number) {
    				result.put(i, "BIAS_" + records.get(0).get(att_index).charAt(0) + "_" + length + "_" + threshold + "_1");
    			} else {
    				result.put(i, "BIAS_" + records.get(0).get(att_index).charAt(0) + "_" + length + "_" + threshold + "_0");
    			}
    		}
    	}
    	
    	
    	return result;
    }
    
    public static HashMap<Integer, String> BIAS_categories(int length, int att_index, double threshold, ArrayList<ArrayList<String>> records, int rise, int down) {
    	HashMap<Integer, String> result = new HashMap<>();
    	int col = att_index;   
    	int rise_number = 0;
    	int down_number = 0;
    	for (int i = 1; i < records.size(); i++) {
    		double bias;
    	    if (i <= length-1) {
    	    	//result.put(i, "BIAS_" + records.get(0).get(att_index).charAt(0) + "_" + length + "_" + threshold + "_1");
    	    } else {
    	    	double sum_t = 0;
    	    	if (i - length + 1 >= 1) {
    	    		for (int j = i; j >= i-length+1; j--) {                
                        sum_t = sum_t + Double.parseDouble(records.get(j).get(col));
                    } 	    	    		
    	    	}
    	    	sum_t = sum_t / (double)length;
    	    	bias = (Double.parseDouble(records.get(i).get(att_index)) - sum_t)/(double) sum_t;
    	    	if (bias >= threshold) {
    	    		result.put(i, Integer.toString(i));	
    	    		 rise_number++;
    	    	} else {
    	    		result.put(i, "BIAS_" + records.get(0).get(att_index).charAt(0) + "_" + length + "_" + threshold + "_0");	
    	    		down_number++;
    	    	}    	    	
    	    }
    		
    	}
    	for (int i = 1; i < records.size(); i++) {
    		if (result.get(i) == null) {
    			if (rise_number > down_number) {
    				result.put(i, Integer.toString(rise));
    			} else {
    				result.put(i, Integer.toString(down));
    			}
    		}
    	}
    	
    	
    	return result;
    }
    
    public static HashMap<Integer, Double> BIAS_Numeric(int period, int att_index, ArrayList<ArrayList<String>> records) {
    	HashMap<Integer, Double> result = new HashMap<>();
    	
    	for (int i = 1; i < records.size(); i++) {
    		double bias;
    	    if (i <= period-1) {
    	    	
    	    } else {
    	    	double sum_t = 0;
    	    	if (i - period + 1 >= 1) {
    	    		for (int j = i; j >= i-period+1; j--) {                
                        sum_t = sum_t + Double.parseDouble(records.get(j).get(att_index));
                    } 	    	    		
    	    	}
    	    	sum_t = sum_t / (double) period;
    	    	bias = (Double.parseDouble(records.get(i).get(att_index)) - sum_t)/(double) sum_t;
    	    	result.put(i, bias);  	    	
    	    }
    		
    	}
    	
    	double average =  0.0;
    	for (int i = 1; i < result.size(); i++) {
    		if (result.get(i) != null) {
    			average += result.get(i);
    		}
    	}
    	
    	average /= (double) result.size();
    	for (int i = 1; i < records.size(); i++) {
    		if (result.get(i) == null) {
    			result.put(i, average);
    		}
    	}
    	return result;
    }
    
    public static HashMap<Integer, Double> origianl_relative(int att_index, ArrayList<ArrayList<String>> records) {
    	HashMap<Integer, Double> result = new HashMap<>();
    	double average = 0;
    	for (int i = 1; i < records.size(); i++) {
    	    //Empty
    		if (i == 1)	{
    	    	
    	    } else {
    	    	double pre = Double.parseDouble(records.get(i-1).get(att_index));
    	    	double now = Double.parseDouble(records.get(i).get(att_index));
    	    	
    	    	double relative = (now - pre)/ (double) pre;
    	    	result.put(i, relative);	    	
    	    	average += relative;
    	    }
    		
    	}
    	
    	average /= result.keySet().size();
    	for (int i = 1; i < records.size(); i++) {
    	    if (result.get(i) == null) {
    	    	result.put(i, average);
    	    }    		
    	}
    	return result;
    }
    
    public static HashMap<Integer, Double> MA_relative(HashMap<Integer, Double> MA) {
    	HashMap<Integer, Double> result = new HashMap<>();
    	double average = 0;
    	for (int i = 1; i <= MA.size(); i++) {
    	    //Empty
    		if (i == 1)	{
    	    	
    	    } else {
    	    	double pre = MA.get(i-1);
    	    	double now = MA.get(i);
    	    	
    	    	double relative = (now - pre)/ (double) pre;
    	    	result.put(i, relative);	    	
    	    	average += relative;
    	    }
    		
    	}
    	
    	average /= result.keySet().size();
    	for (int i = 1; i <= MA.size(); i++) {
    	    if (result.get(i) == null) {
    	    	result.put(i, average);
    	    }    		
    	}
    	return result;
    }
    
    
    
    
    //weka
    public static void featureExtraction_weka(String output_filename, ArrayList<ArrayList<String>> records, HashMap<Integer, String> feature_target, int period) {		
    	
		ArrayList<ArrayList<String>> result = new ArrayList<>();
		
		for (int i = 0; i < records.size(); i++) {		
			ArrayList<String> temp = new ArrayList<>();
			//Add time
			temp.add(records.get(i).get(0));
			if(i == 0) {

               temp.add("Target");
			} else {
	
				temp.add(feature_target.get(i));		
			}	
			//temp.add(records.get(i).get(records.get(i).size()-1));	
			result.add(temp);
		}		
		try {
		writeCSV("", output_filename,result);
		} catch (IOException e) {
			System.out.println("[ERROR] I/O Exception.");
			e.printStackTrace();
		}
	}
	
    public static HashMap<Integer, String> featureExtraction_target(ArrayList<ArrayList<String>> records) {
    	HashMap<Integer, String> result = new HashMap<>();
    	
    	int index_of_target_att = records.get(0).size()-1;
    	for (int i = 1; i < records.size(); i++) {
    	    if (i==1) {
    	    	result.put(i, "100"); 
    	    	continue;
    	    }
//    	    System.out.println(i);
//    	    System.out.println(Double.parseDouble(records.get(i).get(index_of_target_att)));
    	   
    	    if (Double.parseDouble(records.get(i).get(index_of_target_att))- Double.parseDouble(records.get(i-1).get(index_of_target_att)) >= 0 ) {
    	    	result.put(i, "100");     
    	    } else {
    	    	result.put(i, "200");  
    	    }	
    	}    	  
    	return result;  
    	
    }
    
    public static  HashMap<Integer, String> featureExtraction_target_user_defined(ArrayList<ArrayList<String>> records) throws FileNotFoundException {
    	
    	
    	HashMap<Integer, String> result = new HashMap<>();
    	int training_data_size = (int)((records.size() - 1)*0.8);
    	int index_of_target_att = records.get(0).size()-1;
    	int Rise1_in_training = 0, Rise2_in_training = 0, Down1_in_training = 0, Down2_in_training = 0;
    	double threshold = 0.025;
    	
    	for (int i = 1; i <= training_data_size; i++) {    	
    		if (i == 1) {
    			Down1_in_training++;
    			continue;
    		}
    		double price_now = Double.parseDouble(records.get(i).get(index_of_target_att));
    	    double price_pre = Double.parseDouble(records.get(i-1).get(index_of_target_att));
    	    double price_per_level = (price_now-price_pre) / (double) price_pre;    	
    	    if (price_per_level > 0) {
    	    	if (price_per_level > threshold) {
    	    		result.put(i, "Rise_2");
    	    		Rise2_in_training++;   	
    	    	} else {
    	    		result.put(i, "Rise_1");
    	    		Rise1_in_training++;
    	    	}
    	    } else {
    	    	if (price_per_level == 0) {
    	    		result.put(i, "Down_1");
    	    		Down1_in_training++;	
    	    	} else {
    	    		if (Math.abs(price_per_level) > threshold) {	
    	    			result.put(i, "Down_2");
    	    			Down2_in_training++;  	
    	    		} else {
    	    			result.put(i, "Down_1");
    	    			Down1_in_training++;
    	    		}
    	    	}
    	    }
    	
    	}
    	System.out.println("Rise1_in_training: " + Rise1_in_training);
    	System.out.println("Rise2_in_training: " + Rise2_in_training);
    	System.out.println("Down1_in_training: " + Down1_in_training);
    	System.out.println("Down2_in_training: " + Down2_in_training);
    	System.out.println();
    	int Rise1_in_testing = 0, Rise2_in_testing = 0, Down1_in_testing = 0, Down2_in_testing = 0;
    	for (int i = training_data_size+1; i < records.size(); i++) {
    		double price_now = Double.parseDouble(records.get(i).get(index_of_target_att));
    	    double price_pre = Double.parseDouble(records.get(i-1).get(index_of_target_att));
    	    double price_per_level = (price_now-price_pre) / (double) price_pre;    	
    	    if (price_per_level > 0) {
    	    	if (price_per_level > threshold) {
    	    		result.put(i, "Rise_2");
    	    		Rise2_in_testing++;   	
    	    	} else {
    	    		result.put(i, "Rise_1");
    	    		Rise1_in_testing++;
    	    	}
    	    } else {
    	    	if (price_per_level == 0) {
    	    		result.put(i, "Down_1");
    	    		Down1_in_testing++;	
    	    	} else {
    	    		if (Math.abs(price_per_level) > threshold) {	
    	    			result.put(i, "Down_2");
    	    			Down2_in_testing++;  	
    	    		} else {
    	    			result.put(i, "Down_1");
    	    			Down1_in_testing++;
    	    		}
    	    	}
    	    }
    	
    	}
    	System.out.println("Rise1_in_testing: " + Rise1_in_testing);
    	System.out.println("Rise2_in_testing: " + Rise2_in_testing);
    	System.out.println("Down1_in_testing: " + Down1_in_testing);
    	System.out.println("Down2_in_testing: " + Down2_in_testing);
    	
    	
    	/*
    	int debug = 0;
    	if (debug == 1) {
    	ArrayList<ArrayList<String>> result_train_1 = new ArrayList<>();
    	ArrayList<ArrayList<String>> result_train_2 = new ArrayList<>();
    	int training_data_size = (int)((records.size() - 1)*0.8);
    	int index_of_target_att = records.get(0).size()-1;
    	int r = 0, r_h = 0, d = 0, d_h = 0;
    	int five_per_rise_large = 0;
	    int five_per_rise_small = 0;
	    int five_per_fall_large = 0;
	    int five_per_fall_small = 0;
        int train_size = (int)((records.size()*0.8)-1);
        
        
        ArrayList<String> temp_train_2 = new ArrayList<>();
        temp_train_2.add("Feature");
    	result_train_2.add(temp_train_2);
    	for (int i = 0; i <= train_size; i++) {
    		if (i==0) {  
    			ArrayList<String> temp = new ArrayList<>();
    	    	temp.add("Feature");
    	    	result_train_1.add(temp);
    	    	continue;
    	    }
    	    if (i==1) {  
    	    	ArrayList<String> temp = new ArrayList<>();
    	    	temp.add(String.valueOf(0));
    	    	result_train_1.add(temp);
    	    	continue;
    	    }
    	    
    	    double price_now = Double.parseDouble(records.get(i).get(index_of_target_att));
    	    double price_pre = Double.parseDouble(records.get(i-1).get(index_of_target_att));
    	    double price_per = price_now-price_pre;
    	    
    	    if (price_per > 0) {
    	    	ArrayList<String> temp = new ArrayList<>();
    	    	temp.add(String.valueOf(price_per));
    	    	result_train_1.add(temp);
    	    } else {
    	    	ArrayList<String> temp = new ArrayList<>();
    	    	temp.add(String.valueOf(price_per));
    	    	result_train_2.add(temp);
    	    }
   
    	}   
    	
    	try {
    		writeCSV("", "result_train_1.csv",result_train_1);
    	} catch (IOException e) {
    		System.out.println("[ERROR] I/O Exception.");
    		e.printStackTrace();
    	}
    	try {
    		writeCSV("", "result_train_2.csv",result_train_2);
    	} catch (IOException e) {
    		System.out.println("[ERROR] I/O Exception.");
    		e.printStackTrace();
    	}
    	ArrayList<ArrayList<String>> result_test_1 = new ArrayList<>();
    	ArrayList<ArrayList<String>> result_test_2 = new ArrayList<>();
    	ArrayList<String> temp1 = new ArrayList<>();
    	temp1.add("Feature");
    	result_test_1.add(temp1);
    	
    	ArrayList<String> temp2 = new ArrayList<>();
    	temp2.add("Feature");
    	result_test_2.add(temp2);
  
    	for (int i = train_size + 1; i < records.size(); i++) {
    		
    	    if (i==train_size + 1) {  
    	    	ArrayList<String> temp = new ArrayList<>();
    	    	temp.add(String.valueOf(0));
    	    	result_test_1.add(temp);
    	    	continue;
    	    }
    	    
    	    double price_now = Double.parseDouble(records.get(i).get(index_of_target_att));
    	    double price_pre = Double.parseDouble(records.get(i-1).get(index_of_target_att));
    	    double price_per = price_now-price_pre;
    	    
    	    if (price_per > 0) {
    	    	ArrayList<String> temp = new ArrayList<>();
    	    	temp.add(String.valueOf(price_per));
    	    	result_test_1.add(temp);
    	    } else {
    	    	ArrayList<String> temp = new ArrayList<>();
    	    	temp.add(String.valueOf(price_per));
    	    	result_test_2.add(temp);
    	    }
   
    	}   
    	
    	try {
    		writeCSV("", "result_test_1.csv",result_test_1);
    	} catch (IOException e) {
    		System.out.println("[ERROR] I/O Exception.");
    		e.printStackTrace();
    	}
    	try {
    		writeCSV("", "result_test_2.csv",result_test_2);
    	} catch (IOException e) {
    		System.out.println("[ERROR] I/O Exception.");
    		e.printStackTrace();
    	}
    	
    	} else {
    		
    		int index_of_target_att = records.get(0).size()-1;
    		int train_size = (int)((records.size()*0.8)-1);
    		
    		//對於訓練資料的索引
    		int result_train1_for_training_index = 1;
    		int result_train2_for_training_index = 1;
    		for (int i = 0; i <= train_size; i++) {
        		if (i==0) {  
        			result.put(0, "Target");
        	    	continue;
        	    }
        	    if (i==1) {  
        	    	result.put(i, "Down_1");
        	    	continue;
        	    }
        	    
        	    double price_now = Double.parseDouble(records.get(i).get(index_of_target_att));
        	    double price_pre = Double.parseDouble(records.get(i-1).get(index_of_target_att));
        	    double price_per = price_now-price_pre;
        	    
        	    
        	    ArrayList<ArrayList<String>> result_train1_for_training = readCSV("result_train1_for_training.csv");
        	    ArrayList<ArrayList<String>> result_train2_for_training = readCSV("result_train2_for_training.csv");

        	    if (price_per > 0) {
        	    	if (result_train1_for_training.get(result_train1_for_training_index).get(0) == "Feature_1") {
        	    		result.put(i, "Rise_1");	

        	    	} else {
        	    		result.put(i, "Rise_2");	
        	    	
        	    	}
        	    	result_train1_for_training_index++;
        	    	
        	    } else {        	    	
        	    	if (result_train2_for_training.get(result_train2_for_training_index).get(0).equals("Feature_1")) {
        	    		result.put(i, "Down_1");	
        	    	} else {
        	    		result.put(i, "Down_2");	
        	    	}
        	    	result_train2_for_training_index++;   	
        	    }      
        	}   
    		//對於測試資料的索引
    		int result_test1_for_testing_index = 1;
    		int result_test2_for_testing_index = 1;
    		
    		for (int i = train_size + 1; i < records.size(); i++) {
        		
        	    if (i==train_size + 1) {  
        	    	result.put(i, "Down_1");
        	    	continue;
        	    }
        	    
        	    double price_now = Double.parseDouble(records.get(i).get(index_of_target_att));
        	    double price_pre = Double.parseDouble(records.get(i-1).get(index_of_target_att));
        	    double price_per = price_now-price_pre;

        	    ArrayList<ArrayList<String>> result_test1_for_testing = readCSV("result_test1_for_testing.csv");
        	    ArrayList<ArrayList<String>> result_test2_for_testing = readCSV("result_test2_for_testing.csv");
        	    
        	    if (price_per > 0) {
        	    	if (result_test1_for_testing.get(result_test1_for_testing_index).get(0).equals("Feature_1")) {
        	    		result.put(i, "Rise_1");	
        	    	} else {
        	    		result.put(i, "Rise_2");	
        	    	}
        	    	result_test1_for_testing_index++;
        	    } else {
        	    	if (result_test2_for_testing.get(result_test2_for_testing_index).get(0).equals("Feature_1")) {
        	    		result.put(i, "Down_1");	
        	    	} else {
        	    		result.put(i, "Down_2");	
        	    	}
        	    	result_test2_for_testing_index++;
        	    }
       
        	}          	    		
    		
    		
    	}

    	*/
		return result;
    	
    	
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
    public static HashMap<Integer, String> MACD(int tl, int sl, int ll, String att, ArrayList<ArrayList<String>> records) {
    	HashMap<Integer, String> result = new HashMap<>(); 
    	for (int i = 1; i < records.size(); i++) {
    	    double MACD = DIF(i, sl, ll, records) - DEM(i, sl, ll, tl, records);        	
    		if (MACD < 0) {
    			result.put(i, "MACD_" + att.charAt(0) + sl + ll+"_0");
    		} else {
    			result.put(i, "MACD_" + att.charAt(0) + sl + ll+"_1");			
    		}
    	}
    	return result;
    } 
    
    public static HashMap<Integer, Double> MACD_weka(int tl, int sl, int ll, String att, ArrayList<ArrayList<String>> records) {
    	HashMap<Integer, Double> result = new HashMap<>(); 
    	for (int i = 1; i < records.size(); i++) {
    	    double MACD = DIF(i, sl, ll, records) - DEM(i, sl, ll, tl, records);        	
    		result.put(i, MACD);
    	}
    	return result;
    } 
     
    public static double EMA(int t, int l, ArrayList<ArrayList<String>> records, String s) {
    	if (t == 0) {  
    		return 0.0;
    	}
    	int col = 2;
    	double alpha = 2/(double)(l+1);
    	double p = Double.parseDouble(records.get(t).get(col));
        if (s.equals("sl")) {
        	temp_sl.put(0, 0.0);
            temp_sl.put(t, temp_sl.get(t-1) + alpha*(p - temp_sl.get(t-1)));
            return temp_sl.get(t);
        } else {  	
        	temp_ll.put(0, 0.0); 
            temp_ll.put(t, temp_ll.get(t-1) + alpha*(p - temp_ll.get(t-1)));
            return temp_ll.get(t);
        }  
    	
    }
    
    public static double DIF(int t, int sl, int ll, ArrayList<ArrayList<String>> records) {
        return EMA(t, sl, records, "sl") - EMA(t, ll, records, "ll"); 	
    }
    
    public static double DEM(int t, int sl, int ll, int tl, ArrayList<ArrayList<String>> records) {
        return 	(DIF(t, sl, ll, records) + DIF(t-1, sl, ll, records))/(double) tl;
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
