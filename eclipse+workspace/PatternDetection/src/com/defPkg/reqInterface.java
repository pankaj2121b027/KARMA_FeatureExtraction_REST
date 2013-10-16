package com.defPkg;



import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

class PatternClass
{
String Pattern=null;
//String CountPattern = null;
int frequency=0;
String sample=null;
boolean removed= false; // used only for finding max freq pattern
}


/**
 * Servlet implementation class reqInterface
 */
public class reqInterface extends HttpServlet {
	private static final long serialVersionUID = 1L;
private java.util.List<String> DATE_FORMAT_LIST;
private java.util.List<String> DAYS_OF_WEEK;
private java.util.List<String> MONTHS;
private final String WEEKDAY = "WEEKDAY";
private final String MONTH = "MONTH";
private final String strBOOLEAN = "BOOLEAN";
private final String strNUMBER = "NUMBER";

	
      HashMap<String, PatternClass> gobjPatternMap;
      final String  strTRUE = "true";      
      final String  strFALSE = "false";
      private void InitializeResources()
      {
    	  String []DATE_FORMAT_ARRAY = {"dd-dd-dddd","dd-www-dd", "dddd.dd.ddww'ww'dd:dd:ddw", "www,wwwd,''dd", "dd:ddww", "dd'w''wwwww' wwwwwwwwww",
    			"d:ddww,www", "ddddd.wwwww.ddwwdd:ddww", "www,dwwwdddddd:dd:-dddd", "dd.ww.dd",
    			"dddd.dd.ddww'ww'dd:dd:ddww", "wwwdwwwdd", "dddd-dd-dd", "dddd/dd/dd", "dddd-dd-dddd:dd:dd,ww", 
    			"www,ddwwwdddddd:dd:ddww", "dddd-dd-dd'w'dd:dd:ddw"};
    		
    	 DATE_FORMAT_LIST = new ArrayList<>(DATE_FORMAT_ARRAY.length);
    	 for(String format : DATE_FORMAT_ARRAY)
    	 {
    		 DATE_FORMAT_LIST.add(format);
    	 }
    	 
    	 String []alldays= {"MONDAY", "TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY", "SUNDAY"};
    	 DAYS_OF_WEEK = new ArrayList<>(alldays.length);
    	 for(String day : alldays)
    	 {
    		 DAYS_OF_WEEK.add(day);
    	 }
    	 
    	 String[] allMonths={"JANUARY","FEBRUARY","MARCH","APRIL","MAY","JUNE","JULY","AUGUST","SEPTEBER","OCTOBER","NOVEMBER","DECEMBER"};
	 MONTHS = new ArrayList<>();
  	for(String month : allMonths)
	 {
  		MONTHS.add(month);
	 }
      }
      
      
    /**
     * @see HttpServlet#HttpServlet()
     */
    public reqInterface() {
        super();
        // TODO Auto-generated constructor stub
        InitializeResources();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		gobjPatternMap = new HashMap<>();
		PrintWriter responseWriter= response.getWriter();
		
		ProcessData(request.getParameter("d"));
		
		PatternClass[] result =getNextFrequesntPattern();
		
		responseWriter.append("<html>");
		 for ( PatternClass pObj: result)
		 {
			 responseWriter.append(pObj.Pattern + " : " + pObj.frequency + "<br>");
		 }
		 responseWriter.append("</html>");
		/*for (String key: gobjPatternMap.keySet()) 
		{
		    responseWriter.append(gobjPatternMap.get(key).Pattern + " : " + gobjPatternMap.get(key).frequency + "<p>");
		}*/
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	private String ProcessData(String data)
	{
		try
		{
			boolean processedNumberPattern = false;
			gobjPatternMap = new HashMap<>();
			
			JSONArray rowArr = new JSONArray(data);
			JSONObject jobj ;
			String row;
			
			for(int i =0; i< rowArr.length();i++)
			{
				jobj = new JSONObject(rowArr.get(i).toString());
				row=jobj.get("v").toString();
				try
				{
					Float.parseFloat(row);
					
					findNumberPattern(row);
					addToPatternSet(strNUMBER,row);
					processedNumberPattern = true;
				}
				catch(Exception ex)
				{}
				
				if(!processedNumberPattern)
					{
						FindStringPattern(row);
					}
				FindDatePattern(row);
				FindDayPattern(row);
				FindMonthPattern(row);
			}
			
			return "";
			
		}
		catch(JSONException jex)
		{
			return "JSON EXCEPTION";
		}
		catch(Exception ex)
		{
			//throw ex;
			return "";
		}
		
	}
	
	private void FindDayPattern(String row)
	{
		try
		{
			if(DAYS_OF_WEEK.contains(row.toUpperCase()))
			{
				addToPatternSet(row.toUpperCase(), row);
				addToPatternSet(WEEKDAY, row);
			}
		}
		catch(Exception ex)
		{
			
		}
	}
	
	private void FindMonthPattern(String row)
	{
		try
		{
			if(MONTHS.contains(row.toUpperCase()))
			{
				addToPatternSet(row.toUpperCase(), row);
				addToPatternSet(MONTH, row);
			}
		}
		catch(Exception ex)
		{
			
		}
	}
	
	private String FindDatePattern(String row)
	{
		try
		{
			String value = row;
			
			
			
			/*replace alphabets with w*/
			Pattern replace = Pattern.compile("[a-zA-Z]");
		    Matcher matcher2 = replace.matcher(value);
		     value =  matcher2.replaceAll("w");
		     
		     /*replace numbers with d*/
		 	replace = Pattern.compile("[0-9]");
		    matcher2 = replace.matcher(value);
		     value =  matcher2.replaceAll("d");
		     
		     if(DATE_FORMAT_LIST.contains(value))
		     {
		    		 addToPatternSet(value, row);

		     }
		     
		     return "";
		}
		catch(Exception ex)
		{
			return "";
		}
	}
	
	private String FindStringPattern(String row)
	{
		try
		{
			if(row.equals(""))
			{
				addToPatternSet("NULL", row);
				return "";
			}
			 
			if(DAYS_OF_WEEK.contains(row.toUpperCase()))//if its day, do not proceed. It will be checked by other function
			{
				return "";
			}
			if(MONTHS.contains(row.toUpperCase()))//if its month, do not proceed. It will be checked by other function
			{
				return "";
			}
			
			
			//String value ="zf34 vd4f&4%55$.55f5%5喜";
			String value = row;
			
		
			value=  value.replace(" ", "");
			
			/*chaeck boolean pattern*/
			identifyBoolean(value);
			
			
			Pattern replace;
			Matcher matcher2 ;
			/*find patterns like  abc123 & abc345    and  abc123 & hdg123 */
			{
				
				String replacedNums, replacedAlpha;
				replace = Pattern.compile("[a-zA-Z]");
			    matcher2 = replace.matcher(value);
			    replacedAlpha =  matcher2.replaceAll("w");
				if(replacedAlpha.contains("w"))
				{
					addToPatternSet(replacedAlpha, row);
				}
			    
			    replace = Pattern.compile("[0-9]");
			    matcher2 = replace.matcher(value);
			    replacedNums =  matcher2.replaceAll("d");
			    if(replacedNums.contains("d"))
				{
					addToPatternSet(replacedNums, row);
				}
			
			    
			    /*finds pattern like abc123344 &  abc3453453634563456*/
			    replace = Pattern.compile("d+");
			    matcher2 = replace.matcher(replacedNums);
			   String replacedAlld =  matcher2.replaceAll("d");
			   if(replacedAlld.contains("d"))
				{
					addToPatternSet(replacedAlld, row);
				}
			   
			   /*finds pattern like abcsdfsdf123 &  ac123*/
			    replace = Pattern.compile("\\w+");
			    matcher2 = replace.matcher(replacedAlpha);
			   String replacedAllw =  matcher2.replaceAll("w");
			   if(replacedAllw.contains("d"))
				{
					addToPatternSet(replacedAllw, row);
				}
			   
			}
			
			
			
			
			
			
			
			
			/*replace alphabets with w*/
			replace = Pattern.compile("[a-zA-Z]");
		    matcher2 = replace.matcher(value);
		     value =  matcher2.replaceAll("w");
		     
		     /*replace numbers with d*/
		 	replace = Pattern.compile("[0-9]");
		    matcher2 = replace.matcher(value);
		     value =  matcher2.replaceAll("d");
		     
			
		     if(DATE_FORMAT_LIST.contains(value)) //if its date, do not proceed. It will be checked by other function
		     {
		    	 return "";
		     }
		  
		     String res;
		     
		  // store pattern b4 counting freq of characters/numbers
		     //addToPatternSet(value, row);
			
			  
			res= countOccurrrances(value, 'w', 'w');   
			// store pattern after counting freq of characters  
			addToPatternSet(res, row);
			
			String res1= countOccurrrances(res, 'd', 'd');
			// store pattern b4 counting freq of numbers
			if(!res.equals(res1)) // to avoid adding same pattern to hashset e.g. dfg
			{
				addToPatternSet(res1, row);	
			}
			
			
			
			//detected patterns here
			
			  return res;
			  
		}
		catch(Exception ex)
		{
		//	throw ex;
			return "";
		}
	
	}
	

	private String countOccurrrances(String value , char match , char replaceWith)
	{
		try
		{
			
		
		Pattern replace = null;
		Matcher matcher2 = null;
	
		
		 int i=0,count=0;
		  String res ="";
		  boolean found = false;
		  while(i <= value.length()-1)
		  {
			  found = false;
			while(value.charAt(i)==match)
			{
				found = true;
				count++;
				i++;
				value = value.replaceFirst(""+match, "R"); //replace each "match" character with R
				
				if(i == value.length())
					break;
			}
			if(found)
			{
				replace = Pattern.compile("R+");
			    matcher2 = replace.matcher(value);
			    //res +=  matcher2.replaceAll("w("+count + ")");
			    res+= replaceWith +  "(" + count + ")";
			}
			else
			{
			res+= value.charAt(i);
		     count=0;
		     i++;
			}
		  }
		  
		
		
		return res;
		}
		catch(Exception ex)
		{
			return "";
		}
	}
	
	
	private String findNumberPattern(String row)
	{
		String patternStr="", value;
		Pattern replace;
		Matcher matcher2;
		
		/*replace numbers with d*/
	 	replace = Pattern.compile("[0-9]");
	    matcher2 = replace.matcher(row);
	     value =  matcher2.replaceAll("d");
	     countOccurrrances(value, 'd', 'd');
	     addToPatternSet(value, row);
		
		/*if(row.contains(".")) //float
		{
			float f =Float.parseFloat(row);
			if(f<0)
			{
				patternStr +="-d.d"; //simple -ve pattern
				addToPatternSet(patternStr, row);

			}
			else
			{
				patternStr +="d.d"; //simple +ve pattern
				addToPatternSet(patternStr, row);
			}
			
		}*/
		
		
		return "";
	}
	
/*	private String findNumberPattern(String row)
	{
		try
		{
		//String value ="12.2135(123)-234喜";
		String value = row;
	PatternClass pc1;	
		
		
		value=  value.replace(" ", "");
		
		//replace numbers with d  
	     Pattern	replace = Pattern.compile("[0-9]");
	     Matcher  matcher2 = replace.matcher(value);
	     value =  matcher2.replaceAll("d");
	
	     //store pattern b4 counting number of digits
	     addToPatternSet(value,row);
	     
	     
	     String res= countOccurrrances(value, 'd', 'd');
	     //store pattern after counting number of digits
	     addToPatternSet(res,row);
	    
	     
		return res;
		}
		catch(Exception ex)
		{
			return "";
		}
	}*/
	
	private void addToPatternSet(String pattern,String sample)
	{
		PatternClass pc1;
		 if(gobjPatternMap.containsKey(pattern))
		  {
			  
			pc1=  gobjPatternMap.get(pattern);
			 pc1.frequency++;
		  }
		  else
		  {
			  pc1=new PatternClass();
			  pc1.Pattern = pattern;
			  pc1.frequency =1;
			  pc1.sample= sample;
			   gobjPatternMap.put(pattern, pc1);
		  }
	}
	
	private void identifyBoolean(String row)
	{
		if(row.equalsIgnoreCase("true"))
		{
			addToPatternSet(strTRUE, row);
			addToPatternSet(strBOOLEAN, row);
		}
		if(row.equalsIgnoreCase("false"))
		{
			addToPatternSet(strFALSE, row);
			addToPatternSet(strBOOLEAN, row);
		}
	}
	
	
	private PatternClass[] getNextFrequesntPattern()
	{
		PatternClass[] sortedPatterns = null;
		try
		{
sortedPatterns=new PatternClass[gobjPatternMap.size()];		
int maxFreq=0, index=0;
String maxPattern="";


for(int i = 0; i < gobjPatternMap.size();i++)
{
	for(String key : gobjPatternMap.keySet())
	{
		if(maxFreq <= gobjPatternMap.get(key).frequency && gobjPatternMap.get(key).removed == false)
		{
			maxFreq = gobjPatternMap.get(key).frequency;
			maxPattern = gobjPatternMap.get(key).Pattern;
			
		}
		 

		}
	sortedPatterns[index] = gobjPatternMap.get(maxPattern);
	gobjPatternMap.get(maxPattern).removed=true;
	 index++;
	 maxFreq = 0;
}
		}
		catch(Exception ex)
		{
			return sortedPatterns;
		}
		return sortedPatterns;
	}
}
