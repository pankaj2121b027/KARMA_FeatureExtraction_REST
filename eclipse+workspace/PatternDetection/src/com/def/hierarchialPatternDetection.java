package com.def;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sun.reflect.generics.tree.ReturnType;

import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;





class PatternClass
{
	String Pattern=null;
	//String CountPattern = null;
	int frequency=0;
	String sample=null;
	boolean removed= false; // used only for finding max freq pattern
	String depth="";
}
class clsReturnValue
{
	String Depth;
	int returnValue;

	public clsReturnValue(String pDepth,int preturnValue)
	{
		Depth = pDepth;
		returnValue = preturnValue;
	}

	public static  void SortOnReturnValue(Vector<clsReturnValue> arrReturnValues) //sorts on frequency b of depth a
	{
		String tempDepth ;
		int tempreturnValue;

		for(int i =0;i< (arrReturnValues.size()-1);i++)
		{
			for(int j=i;j<i;j++)
			{
				if(arrReturnValues.get(j).returnValue > arrReturnValues.get(j+1).returnValue)
				{
					tempDepth = arrReturnValues.get(j).Depth;
					tempreturnValue = arrReturnValues.get(j).returnValue;

					arrReturnValues.get(j).returnValue = arrReturnValues.get(j+1).returnValue;
					arrReturnValues.get(j).Depth = arrReturnValues.get(j+1).Depth;

					arrReturnValues.get(j+1).returnValue = tempreturnValue;
					arrReturnValues.get(j+1).Depth = tempDepth;
				}
			}

		}

	}
}


/**
 * Servlet implementation class hierarchialPatternDetection
 */
public class hierarchialPatternDetection extends HttpServlet {

	private final static String SingleCharacterReplacement = "c";
	private final static String SingleDigitReplacement = "d";
	private final static String CharacterGroupReplacement = "C";
	private final static String DigitGroupReplacement = "D";
	private final static String spaceReplacement = "s";
	private final static String specialCharReplacement = "X";
	private final static String EscapingCharacter ="%"; //do not use '\' here
	private final static String[] jsonFields = {"Pattern","frequency","depth","sample","removed"};

	private static boolean returnAllPatterns = false;
	private static boolean returnJson = false;
	private static boolean inputIsJson = false; 
	private static int SamplingThreshold =1000;
	private static int SamplingPercentage =10;

	//private static int maxDepthReached=0;	
	private static final long serialVersionUID = 1L;
	//HashMap<String, PatternClass> gobjPatternMap;
	//private static Vector<HashMap<String, PatternClass>> gobjDepthPatternMap;
	private static HashMap<String, HashMap<String, PatternClass>> gobjDepthPatternMap;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public hierarchialPatternDetection() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */

	private static String startWork(String req)
	{
		StringBuilder resultResponse= new StringBuilder();
		try
		{


			InitializeResources();




			PatternClass[] result;


			IncrementDepth();

			resultResponse.append(ProcessData(GetValuesfromURLParameter(req)));



			/*for(String k: gobjDepthPatternMap.keySet())
			{
				 for(String pattern: gobjDepthPatternMap.get(k).keySet())
				 {
					 PatternClass pObj = gobjDepthPatternMap.get(k).get(pattern);
					 resultResponse.append(pObj.Pattern + " : " + pObj.frequency + " : " + pObj.depth +"<br>");
				 }

			}*/


		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}	

		return resultResponse.toString();
	}

	private static List  GetValuesfromURLParameter(String parameter)
	{

		
		BufferedReader br = null;
		StringBuilder sCurrentLine=new StringBuilder();
		try {

			String s=null;

			br = new BufferedReader(new FileReader("G:\\Pankaj\\MS material\\Directed_Reserach\\szekely test files\\output\\subject.txt"));

			while ((s =br.readLine()) != null) {
				sCurrentLine.append(s + "\r");	
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			parameter = sCurrentLine.toString();
		}

		 


		List returnList=new ArrayList<String>();
		try
		{

			int jump=1;
			if(inputIsJson)
			{
				JSONArray rowArr =new JSONArray(parameter);
				JSONObject jobj;

				if(SamplingThreshold <= rowArr.length())
				{
					jump = rowArr.length() / SamplingPercentage;
				}
				for(int i=0;i< rowArr.length();i+=jump)
				{
					jobj = new JSONObject(rowArr.get(i).toString());

					returnList.add(jobj.get("v").toString());
				}

			}
			else
			{


				String values[]= parameter.split("\r");


				if(SamplingThreshold <= values.length)
				{
					jump = values.length / SamplingPercentage;
				}

				for(int i=0;i<values.length-1; i+=jump)
				{
					returnList.add(values[i]);
				}
			}

		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}

		return returnList;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		PrintWriter responseWriter= response.getWriter();


		responseWriter.append("<html>");
		responseWriter.append(startWork(request.getParameter("d")));
		responseWriter.append("</br>");
		responseWriter.append("</br>");
		responseWriter.append("</html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		PrintWriter responseWriter= response.getWriter();
		responseWriter.append("<html>");

		String input= request.getParameter("d");

		responseWriter.append(startWork(input));


		responseWriter.append("</br>");
		responseWriter.append("</br>");
		responseWriter.append("</html>");
	}


	private static int TokenizeOnSpaces(List rowList, String depth)
	{

		Pattern replace;
		Matcher matcher2;
		try
		{
			/*JSONObject jobj ;
			String row;

			for(int i =0; i< rowArr.length();i++)
			{
				jobj = new JSONObject(rowArr.get(i).toString());
				row=jobj.get("v").toString();

				String result = new String( row.getBytes("UTF-8") , "UTF-8");
			 */
			for(int i=0;i <  rowList.size(); i++)
			{

				String result = new String( rowList.get(i).toString().getBytes("UTF-8") , "UTF-8");



				result = ApplyRegexforSingleReplacement( result,"[a-zA-Z]",SingleCharacterReplacement);
				result = ApplyRegexforSingleReplacement( result,"[0-9]",SingleDigitReplacement);
				result = ApplyRegexforSingleReplacement( result,"\\s",spaceReplacement);

				result = ApplyRegexforSingleReplacement( result,"\\W",specialCharReplacement);

				/*
original
				replace = Pattern.compile("[a-zA-Z]");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(SingleCharacterReplacement);

				replace = Pattern.compile("[0-9]");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(SingleDigitReplacement);

				replace = Pattern.compile("\\s");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(spaceReplacement);

				replace = Pattern.compile("\\W");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(specialCharReplacement);
				 */
				result = AppendOccurrranceCounts(result, SingleCharacterReplacement, SingleCharacterReplacement);
				result = AppendOccurrranceCounts(result, SingleDigitReplacement, SingleDigitReplacement);
				result = AppendOccurrranceCounts(result, spaceReplacement, spaceReplacement);
				result = AppendOccurrranceCounts(result, specialCharReplacement, specialCharReplacement);

				addToPatternSet(result, rowList.get(i).toString(),depth);
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		return	getPatternsCountAtDepth(depth);
	}

	private static int TokenizeOnSpaceOnly(List rowList, String depth)
	{

		try
		{

			for(int i=0;i <  rowList.size(); i++)
			{

				String result = new String( rowList.get(i).toString().getBytes("UTF-8") , "UTF-8");

				//result = ReplaceSpecialChars(result);
				result = ApplyRegexforSingleReplacement( result,"[a-zA-Z0-9]",CharacterGroupReplacement);
				//result = ApplyRegexforSingleReplacement( result,"[0-9]",CharacterGroupReplacement);
				result = ApplyRegexforSingleReplacement( result,"\\s",spaceReplacement);

				result = ApplyRegexforSingleReplacement( result,"\\W",CharacterGroupReplacement);



				result = AppendOccurrranceCounts(result, CharacterGroupReplacement, CharacterGroupReplacement);
				result = AppendOccurrranceCounts(result, CharacterGroupReplacement, CharacterGroupReplacement);
				result = AppendOccurrranceCounts(result, spaceReplacement, spaceReplacement);
				result = AppendOccurrranceCounts(result, CharacterGroupReplacement, CharacterGroupReplacement);


				addToPatternSet(result, rowList.get(i).toString(),depth);
			}




		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		return	getPatternsCountAtDepth(depth);

	}


	private static int TokenizeOnSpaceOnlyCoarse(List rowList, String depth)
	{

		try
		{

			for(int i=0;i <  rowList.size(); i++)
			{

				String result = new String( rowList.get(i).toString().getBytes("UTF-8") , "UTF-8");

				//result = ReplaceSpecialChars(result);
				result = ApplyRegexforSingleReplacement( result,"[a-zA-Z0-9]+",CharacterGroupReplacement);
				//result = ApplyRegexforSingleReplacement( result,"[0-9]+",CharacterGroupReplacement);
				result = ApplyRegexforSingleReplacement( result,"\\s",spaceReplacement);

				result = ApplyRegexforSingleReplacement( result,"[\\W]+",CharacterGroupReplacement);



				result = AppendOccurrranceCounts(result, CharacterGroupReplacement, CharacterGroupReplacement);
				result = AppendOccurrranceCounts(result, CharacterGroupReplacement, CharacterGroupReplacement);
				result = AppendOccurrranceCounts(result, spaceReplacement, spaceReplacement);
				result = AppendOccurrranceCounts(result, CharacterGroupReplacement, CharacterGroupReplacement);


				addToPatternSet(result, rowList.get(i).toString(),depth);
			}




		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		return	getPatternsCountAtDepth(depth);

	}

	private static int TokenizeOnSpacesCoarse(List rowList, String depth)
	{




		Pattern replace;
		Matcher matcher2;
		try
		{

			/*			for(int i =0; i< rowArr.length();i++)
			{
				jobj = new JSONObject(rowArr.get(i).toString());
				row=jobj.get("v").toString();

				String result = new String( row.getBytes("UTF-8") , "UTF-8");
			 */
			for(int i=0;i <  rowList.size(); i++)
			{

				String result = new String( rowList.get(i).toString().getBytes("UTF-8") , "UTF-8");

				//result = ReplaceSpecialChars(result);
				result = ApplyRegexforSingleReplacement( result,"[a-zA-Z]+",CharacterGroupReplacement);
				result = ApplyRegexforSingleReplacement( result,"[0-9]+",DigitGroupReplacement);
				result = ApplyRegexforSingleReplacement( result,"\\s",spaceReplacement);

				result = ApplyRegexforSingleReplacement( result,"[\\W]+",specialCharReplacement);

				/* original

				replace = Pattern.compile("[a-zA-Z]+");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(CharacterGroupReplacement);

				replace = Pattern.compile("[0-9]+");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(DigitGroupReplacement);

				replace = Pattern.compile("\\s");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(spaceReplacement);

				replace = Pattern.compile("\\W");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(specialCharReplacement);
				 */

				result = AppendOccurrranceCounts(result, CharacterGroupReplacement, CharacterGroupReplacement);
				result = AppendOccurrranceCounts(result, DigitGroupReplacement, DigitGroupReplacement);
				result = AppendOccurrranceCounts(result, spaceReplacement, spaceReplacement);
				result = AppendOccurrranceCounts(result, specialCharReplacement, specialCharReplacement);


				addToPatternSet(result, rowList.get(i).toString(),depth);
			}




		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		return	getPatternsCountAtDepth(depth);
	}
	private static String ProcessData(List data) throws JSONException
	{
		StringBuilder returnString =new StringBuilder();
		int totalPatternsFound =0;
		int totals=0;
		try
		{
			JSONArray jArr=null;

			//JSONObject jObj; 
			if(returnJson)
			{
				jArr = new JSONArray();
				//returnString.append("[");
			}
			else
			{
				returnString.append("<html>");
			}

			Vector<clsReturnValue> r= ProcessDepth1(data);
			clsReturnValue.SortOnReturnValue(r);
			PatternClass pobj;


			for(int index=0;index<r.size();index++)
			{
				for(String pattern: gobjDepthPatternMap.get(r.get(index).Depth).keySet())
				{

					pobj = gobjDepthPatternMap.get(r.get(index).Depth).get(pattern);
					totalPatternsFound++;
					totals+= pobj.frequency;
					if(returnJson)
					{
						//returnString.append("{a:\"" + pobj.Pattern + "\",b:\"" + pobj.frequency + "\",c:\"" + pobj.depth + "\"}");
						JSONObject jObj ;//= new JSONObject(pobj, jsonFields);
						//jObj = new JSONObject("{a:\"" + pobj.Pattern + "\",b:\"" + pobj.frequency + "\",c:\"" + pobj.depth + "\"}");
						jObj = new JSONObject();
						jObj.put("p",pobj.Pattern);
						jObj.put("f",pobj.frequency);

						jArr.put(jObj);

					}
					else
					{
						returnString.append(pobj.Pattern + ":&nbsp&nbsp" + pobj.frequency + ":&nbsp&nbsp" + pobj.depth + "<br></br>");	
					}

					//jArr.put(new JSONObject(pobj,jsonFields ));


				}

			}
			if(returnJson)
			{
				returnString.append(jArr.toString());
			}
			else
			{
				returnString.append("</html>");
			}

		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());

		}
		totalPatternsFound =totalPatternsFound;
		totals= totals;
		return returnString.toString();

	}


	private static  Vector<clsReturnValue> ProcessDepth1(List rowList) throws Exception
	{
		JSONObject jobj ;
		String row;

		int inputLength = rowList.size();



		/*int PatternCountDepth_1= TokenizeOnSpacesCoarse(rowArr, "1");
		int PatternCountDepth_1_1= TokenizeOnSpaces(rowArr, "1.1");
		int PatternCountDepth_1_2= TokenizeOnSpacePreserveSymbolsCoarse(rowArr, "1.2");
		int PatternCountDepth_1_3= TokenizeOnSpacePreserveSymbolsCoarse(rowArr, "1.3");
		 */



		int PatternCountDepth_1_4_2 = TokenizeOnSpacePreserveAlphabets(rowList, "142");
		int PatternCountDepth_1_4_1 = TokenizeOnSpacePreserveAlphabetsCoarse(rowList, "141");

		int PatternCountDepth_1_3_2 = TokenizeOnSpacePreserveDigits(rowList, "132");
		int PatternCountDepth_1_3_1 = TokenizeOnSpacePreserveDigitsCoarse(rowList, "131");

		int PatternCountDepth_1_2_2 =TokenizeOnSpacePreserveSymbols(rowList, "122");
		int PatternCountDepth_1_2_1 = TokenizeOnSpacePreserveSymbolsCoarse(rowList, "121");


		int PatternCountDepth_1_1_2 =TokenizeOnSpaces(rowList, "112");
		int PatternCountDepth_1_1_1= TokenizeOnSpacesCoarse(rowList, "111");

		int PatternCountDepth_1_0_2 =TokenizeOnSpaceOnly(rowList, "102");
		int PatternCountDepth_1_0_1= TokenizeOnSpaceOnlyCoarse(rowList, "101");


		int TenPercentLength = inputLength/10;
		if(TenPercentLength == 0)
			TenPercentLength = 1;
		int TwentyPercentLength = 2 * TenPercentLength;

		if(inputLength <= 10)
		{
			TenPercentLength = 1;
			TwentyPercentLength = 2 * TenPercentLength;
		}







		Vector<clsReturnValue> temp = new Vector<>();
		if(PatternCountDepth_1_1_1 <= TwentyPercentLength && PatternCountDepth_1_1_1 >=TenPercentLength)
		{
			temp.add(new clsReturnValue("111", PatternCountDepth_1_1_1));
		}
		if(PatternCountDepth_1_1_2 <= TwentyPercentLength && PatternCountDepth_1_1_2 >=TenPercentLength)
		{
			temp.add(new clsReturnValue("112", PatternCountDepth_1_1_2));
		}
		if(PatternCountDepth_1_2_1 <= TwentyPercentLength && PatternCountDepth_1_2_1 >=TenPercentLength)
		{
			temp.add(new clsReturnValue("121", PatternCountDepth_1_2_1));
		}
		if(PatternCountDepth_1_2_2 <= TwentyPercentLength && PatternCountDepth_1_2_2 >=TenPercentLength)
		{
			temp.add(new clsReturnValue("122", PatternCountDepth_1_2_2));
		}
		if(PatternCountDepth_1_3_1 <= TwentyPercentLength && PatternCountDepth_1_3_1 >=TenPercentLength)
		{
			temp.add(new clsReturnValue("131", PatternCountDepth_1_3_1));
		}
		if(PatternCountDepth_1_3_2 <= TwentyPercentLength && PatternCountDepth_1_3_2 >=TenPercentLength)
		{
			temp.add(new clsReturnValue("132", PatternCountDepth_1_3_2));
		}
		if(PatternCountDepth_1_4_1 <= TwentyPercentLength && PatternCountDepth_1_4_1 >=TenPercentLength)
		{
			temp.add(new clsReturnValue("141", PatternCountDepth_1_4_1));
		}
		if(PatternCountDepth_1_4_2 <= TwentyPercentLength && PatternCountDepth_1_4_2 >=TenPercentLength)
		{
			temp.add(new clsReturnValue("142", PatternCountDepth_1_4_2));
		}
		if(PatternCountDepth_1_0_1 <= TwentyPercentLength && PatternCountDepth_1_0_1 >=TenPercentLength)
		{
			temp.add(new clsReturnValue("101", PatternCountDepth_1_0_1));
		}
		if(PatternCountDepth_1_0_2 <= TwentyPercentLength && PatternCountDepth_1_0_2 >=TenPercentLength)
		{
			temp.add(new clsReturnValue("102", PatternCountDepth_1_0_2));
		}

		HashMap<String, Integer> DepthCountMap = new HashMap<>();
		DepthCountMap.put("101", PatternCountDepth_1_0_1);
		DepthCountMap.put("102", PatternCountDepth_1_0_2);
		DepthCountMap.put("111", PatternCountDepth_1_1_1);
		DepthCountMap.put("112", PatternCountDepth_1_1_2);
		DepthCountMap.put("121", PatternCountDepth_1_2_1);
		DepthCountMap.put("122", PatternCountDepth_1_2_2);
		DepthCountMap.put("131", PatternCountDepth_1_3_1);
		DepthCountMap.put("132", PatternCountDepth_1_3_2);
		DepthCountMap.put("141", PatternCountDepth_1_4_1);
		DepthCountMap.put("142", PatternCountDepth_1_4_2);


		if(returnAllPatterns)
		{
			Vector<clsReturnValue> AllPatterns = new Vector();
			for(String Depth : DepthCountMap.keySet())
			{
				AllPatterns.add(new clsReturnValue(Depth,DepthCountMap.get(Depth)));
			}

			return AllPatterns;
		}


		int min = 9999999;
		int max = 0;
		int c=0;
		String maxDepth="";
		for(int i=0;i< temp.size();i++)
		{

			c=0;
			//HashMap<String, PatternClass> h = gobjDepthPatternMap.get(temp.get(i).Depth);
			//c= h.keySet().size();



			if(Integer.parseInt( temp.get(i).Depth) < min)
			{
				min =Integer.parseInt( temp.get(i).Depth);
				//minDepth = temp.get(i).Depth;
			}
			if(Integer.parseInt( temp.get(i).Depth) > max)
			{
				max =Integer.parseInt( temp.get(i).Depth);


			}

		}


		clsReturnValue retValIfLoops = null;

		if(PatternCountDepth_1_0_1 <= TenPercentLength)
		{


			if(PatternCountDepth_1_0_2 <= TenPercentLength)
			{


				if(PatternCountDepth_1_1_1 <= TenPercentLength)
				{
					if(PatternCountDepth_1_1_2 <= TenPercentLength)
					{
						if(PatternCountDepth_1_2_1 <= TenPercentLength)
						{
							if(PatternCountDepth_1_2_2 <= TenPercentLength)
							{
								if(PatternCountDepth_1_3_1 <= TenPercentLength)
								{
									if(PatternCountDepth_1_3_2 <= TenPercentLength)
									{
										if(PatternCountDepth_1_4_1 <= TenPercentLength)
										{
											retValIfLoops= new clsReturnValue("142", PatternCountDepth_1_4_2);
										}
										else
										{
											if((PatternCountDepth_1_4_1 <= PatternCountDepth_1_4_2) && (PatternCountDepth_1_4_2 <= TwentyPercentLength) )
											{
												retValIfLoops=  new clsReturnValue("142", PatternCountDepth_1_4_2);
											}
											else
											{
												retValIfLoops=  new clsReturnValue("141", PatternCountDepth_1_4_1);
											}
										}
									}
									else
									{
										if((PatternCountDepth_1_3_2 <= PatternCountDepth_1_4_1) && (PatternCountDepth_1_4_1 <= TwentyPercentLength) )
										{
											retValIfLoops=  new clsReturnValue("141", PatternCountDepth_1_4_1);
										}
										else
										{
											retValIfLoops=  new clsReturnValue("132", PatternCountDepth_1_3_2);
										}
									}

								}
								else
								{
									if((PatternCountDepth_1_3_1 <= PatternCountDepth_1_3_2) && (PatternCountDepth_1_3_2 <= TwentyPercentLength) )
									{
										retValIfLoops=  new clsReturnValue("132", PatternCountDepth_1_3_2);
									}
									else
									{
										retValIfLoops=  new clsReturnValue("131", PatternCountDepth_1_3_1);
									}
								}


							}
							else
							{
								if((PatternCountDepth_1_2_2 <= PatternCountDepth_1_3_1) && (PatternCountDepth_1_3_1 <= TwentyPercentLength) )
								{
									retValIfLoops=  new clsReturnValue("131", PatternCountDepth_1_3_1);
								}
								else
								{
									retValIfLoops=  new clsReturnValue("122", PatternCountDepth_1_2_2);
								}
							}

						}
						else
						{
							if((PatternCountDepth_1_2_1 <= PatternCountDepth_1_2_2) && (PatternCountDepth_1_2_2 <= TwentyPercentLength) )
							{
								retValIfLoops=  new clsReturnValue("122", PatternCountDepth_1_2_2);
							}
							else
							{
								retValIfLoops=  new clsReturnValue("121", PatternCountDepth_1_2_1);
							}

						}
					}
					else
					{
						if((PatternCountDepth_1_1_2 <= PatternCountDepth_1_2_1) && (PatternCountDepth_1_2_1 <= TwentyPercentLength) )
						{
							retValIfLoops=  new clsReturnValue("121", PatternCountDepth_1_2_1);
						}
						else
						{
							retValIfLoops=  new clsReturnValue("112", PatternCountDepth_1_1_2);
						}


					}
				}
				else
				{
					if((PatternCountDepth_1_1_1 <= PatternCountDepth_1_1_2) && (PatternCountDepth_1_1_2 <= TwentyPercentLength) )
					{
						retValIfLoops=  new clsReturnValue("112", PatternCountDepth_1_1_2);
					}
					else
					{
						retValIfLoops=  new clsReturnValue("111", PatternCountDepth_1_1_1);
					}
				}
			}
			else
			{
				if((PatternCountDepth_1_0_2 <= PatternCountDepth_1_1_1) && (PatternCountDepth_1_1_1 <= TwentyPercentLength) )
				{
					retValIfLoops=  new clsReturnValue("111", PatternCountDepth_1_1_1);
				}
				else
				{
					retValIfLoops=  new clsReturnValue("102", PatternCountDepth_1_0_2);
				}
			}

		}
		else
		{
			if((PatternCountDepth_1_0_1 <= PatternCountDepth_1_0_2) && (PatternCountDepth_1_0_2 <= TwentyPercentLength) )
			{
				retValIfLoops=  new clsReturnValue("102", PatternCountDepth_1_0_2);
			}
			else
			{
				retValIfLoops=  new clsReturnValue("101", PatternCountDepth_1_0_1);
			}
		}

		/*if(PatternCountDepth_1_1_1 < 5)
		{
			if(PatternCountDepth_1_1_2 < 5)
			{
				int[] intArray ;
				intArray = new int[]{PatternCountDepth_1_2_1,PatternCountDepth_1_2_2,PatternCountDepth_1_3_1,PatternCountDepth_1_3_2,PatternCountDepth_1_4_1,PatternCountDepth_1_4_2};
				int minIndex = findMin(intArray);
				switch(minIndex)
				{
				case 0:
					return new clsReturnValue("1_2_1", PatternCountDepth_1_2_1);
				case 1:	
					return new clsReturnValue("1_2_2", PatternCountDepth_1_2_2);
				case 2:
					return new clsReturnValue("1_3_1", PatternCountDepth_1_3_1);
				case 3:
					return new clsReturnValue("1_3_2", PatternCountDepth_1_3_2);
				case 4:
					return new clsReturnValue("1_4_1", PatternCountDepth_1_4_1);
				case 5:
					return new clsReturnValue("1_4_1", PatternCountDepth_1_4_2);
				}
			}
			else
			{
				if(PatternCountDepth_1_1_2 > (inputLength/5))
				{
					if(PatternCountDepth_1_1_2 > PatternCountDepth_1_1_1) //find min
					{
						return new clsReturnValue("1_1_2", PatternCountDepth_1_1_2);		
					}
					else
					{
						return new clsReturnValue("1_1_1", PatternCountDepth_1_1_1);
					}
				}
				else
				{
					return new clsReturnValue("1_1_2", PatternCountDepth_1_1_2);
				}

			}

		}
		else
		{
			return new clsReturnValue("1_1_1", PatternCountDepth_1_1_1);

		}
		 */
		//return new clsReturnValue("-1_1_1", -1);
		clsReturnValue tempRet= null;
		if(max == 0)
		{
			int innermax = 0;
			String innermaxPattern ="0";
			tempRet = retValIfLoops;
			//find 2nd highest
			for (String s : DepthCountMap.keySet())
			{
				if(DepthCountMap.get(s) <= TwentyPercentLength )
				{
					if(Integer.parseInt(innermaxPattern) < Integer.parseInt(s))
					{
						innermax = DepthCountMap.get(s);
						innermaxPattern = s;
					}
				}
			}

			if(innermaxPattern !="0" && innermax !=0)
			{
				tempRet = new clsReturnValue(innermaxPattern, innermax);	 
			}


		}
		else
		{
			tempRet = new clsReturnValue(""+max, getPatternsCountAtDepth(""+max));
		}
		//return retValIfLoops;
		Vector<clsReturnValue> retValArr = new Vector<>(1);
		retValArr.add(tempRet);

		//return tempRet;

		return retValArr;
	}


	private static int findMin(int[] arr)
	{
		int min;
		int minIndex=0;
		min = 99999999;

		for (int i=0;i < arr.length;i++)
		{
			if(min > arr[i]){
				min = arr[i];
				minIndex = i;
			}

		}
		return minIndex;
	}
	/*private static  int ProcessDepth1(JSONArray rowArr) throws Exception
	{
		JSONObject jobj ;
		String row;


		for(int i =0; i< rowArr.length();i++)
		{
			jobj = new JSONObject(rowArr.get(i).toString());
			row=jobj.get("v").toString();
			TokenizeOnSpacesCoarse(row, "1");
		}

		int PatternCountDepth_1= getPatternsCountAtDepth(1);

		if(PatternCountDepth_1 < 5)
		{
			for(int i =0; i< rowArr.length();i++)
			{
				jobj = new JSONObject(rowArr.get(i).toString());
				row=jobj.get("v").toString();
				TokenizeOnSpaces(row, "1.1");
			}

			int PatternCountDepth_1_1 = getPatternsCountAtDepth(1.1);
			if(PatternCountDepth_1_1 >= PatternCountDepth_1 && PatternCountDepth_1_1 < 5)
			{
				for(int i =0; i< rowArr.length();i++)
				{
					jobj = new JSONObject(rowArr.get(i).toString());
					row=jobj.get("v").toString();
					TokenizeOnSpacePreserveSymbolsCoarse(row, "1.2");
				}
				int PatternCountDepth_1_2 = getPatternsCountAtDepth(1.2); remaining
			}
			if(PatternCountDepth_1_1  >= PatternCountDepth_1 && PatternCountDepth_1_1 >= 5)
			{
				remaining
			}
			if(PatternCountDepth_1_1 < PatternCountDepth_1)
			{
				remaining
			}
		}
		else
		{
			return PatternCountDepth_1;
		}

		return -1;
	}*/
	private PatternClass[] getNextFrequesntPattern(String depth)
	{
		PatternClass[] sortedPatterns = null;
		try
		{
			//sortedPatterns=new PatternClass gobjDepthPatternMap.get(depth).size();		
			sortedPatterns=new PatternClass[20];
			int maxFreq=0, index=0;
			String maxPattern="";

			int freq;
			boolean b;
			for(int i = 0; i < gobjDepthPatternMap.get(depth).size();i++)
			{
				for(String key : gobjDepthPatternMap.get(depth).keySet())
				{
					freq = gobjDepthPatternMap.get(depth).get(key).frequency ; 
					b = gobjDepthPatternMap.get(depth).get(key).removed;
					if(maxFreq <= freq && b == false)
					{
						maxFreq = gobjDepthPatternMap.get(depth).get(key).frequency;
						maxPattern = gobjDepthPatternMap.get(depth).get(key).Pattern;

					}


				}
				sortedPatterns[index] = gobjDepthPatternMap.get(depth).get(maxPattern);
				gobjDepthPatternMap.get(depth).get(maxPattern).removed=true;
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

	private static void addToPatternSet(String pattern,String sample , String depth)
	{

		try
		{



			PatternClass pc1;
			if(gobjDepthPatternMap.containsKey(depth))
			{
				if(gobjDepthPatternMap.get(depth).containsKey(pattern))
				{

					pc1=  gobjDepthPatternMap.get(depth).get(pattern);
					pc1.frequency++;
				}
				else
				{
					pc1=new PatternClass();
					pc1.Pattern = pattern;
					pc1.frequency =1;
					pc1.sample= sample;
					//pc1.depth = Integer.parseInt(depth);
					pc1.depth = depth;
					gobjDepthPatternMap.get(depth).put(pattern, pc1);
				}
			}
			else
			{
				gobjDepthPatternMap.put(depth, new HashMap<String,PatternClass>());

				if(gobjDepthPatternMap.get(depth).containsKey(pattern))
				{

					pc1=  gobjDepthPatternMap.get(depth).get(pattern);
					pc1.frequency++;
				}
				else
				{
					pc1=new PatternClass();
					pc1.Pattern = pattern;
					pc1.frequency =1;
					pc1.sample= sample;
					//pc1.depth = Integer.parseInt(depth);
					pc1.depth = depth;
					gobjDepthPatternMap.get(depth).put(pattern, pc1);
				}
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}

	private static void IncrementDepth()
	{
		try
		{
			//maxDepthReached++;

			//gobjDepthPatternMap.add(new HashMap<String,PatternClass>());
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}

	}

	private static void InitializeResources()
	{
		//maxDepthReached =0;
		//gobjDepthPatternMap =new Vector<>(25, 1);
		gobjDepthPatternMap = new HashMap<>();
		/*for(int i=0;i<=15;i++)
		gobjDepthPatternMap.add(new HashMap<String,PatternClass>());
		 */
	}


	private static int TokenizeOnSpacePreserveSymbols(List rowList, String depth)
	{

		Pattern replace;
		Matcher matcher2;
		try
		{
			/*JSONObject jobj ;
			String row;

			for(int i =0; i< rowArr.length();i++)
			{
				jobj = new JSONObject(rowArr.get(i).toString());
				row=jobj.get("v").toString();


				String result = new String( row.getBytes("UTF-8") , "UTF-8");
			 */
			for(int i=0;i <  rowList.size(); i++)
			{

				String result = new String( rowList.get(i).toString().getBytes("UTF-8") , "UTF-8");


				result = ApplyRegexforSingleReplacement( result,"[a-zA-Z]",SingleCharacterReplacement);
				result = ApplyRegexforSingleReplacement( result,"[0-9]",SingleDigitReplacement);
				result = ApplyRegexforSingleReplacement( result,"\\s",spaceReplacement);

				/* original
				replace = Pattern.compile("[a-zA-Z]");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(SingleCharacterReplacement);

				replace = Pattern.compile("[0-9]");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(SingleDigitReplacement);

				replace = Pattern.compile("\\s");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(spaceReplacement);
				 */

				result = AppendOccurrranceCounts(result, SingleCharacterReplacement, SingleCharacterReplacement);
				result = AppendOccurrranceCounts(result, SingleDigitReplacement, SingleDigitReplacement);
				result = AppendOccurrranceCounts(result, spaceReplacement, spaceReplacement);

				addToPatternSet(result, rowList.get(i).toString(),depth);
			}

		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		return	getPatternsCountAtDepth(depth);

	}


	private static int TokenizeOnSpacePreserveSymbolsCoarse(List rowList, String depth)
	{


		Pattern replace;
		Matcher matcher2;
		try
		{
			/*	JSONObject jobj ;
			String row;

			for(int i =0; i< rowArr.length();i++)
			{
				jobj = new JSONObject(rowArr.get(i).toString());
				row=jobj.get("v").toString();

				String result = new String( row.getBytes("UTF-8") , "UTF-8");
			 */
			for(int i=0;i <  rowList.size(); i++)
			{

				String result = new String( rowList.get(i).toString().getBytes("UTF-8") , "UTF-8");


				result = ApplyRegexforSingleReplacement( result,"[a-zA-Z]+",CharacterGroupReplacement);
				result = ApplyRegexforSingleReplacement( result,"[0-9]+",DigitGroupReplacement);
				result = ApplyRegexforSingleReplacement( result,"\\s",spaceReplacement);



				/* original
				replace = Pattern.compile("[a-zA-Z]+");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(CharacterGroupReplacement);

				replace = Pattern.compile("[0-9]+");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(DigitGroupReplacement);

				replace = Pattern.compile("\\s");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(spaceReplacement);

				 */	

				result = AppendOccurrranceCounts(result, CharacterGroupReplacement, CharacterGroupReplacement);
				result = AppendOccurrranceCounts(result, DigitGroupReplacement, DigitGroupReplacement);
				result = AppendOccurrranceCounts(result, spaceReplacement, spaceReplacement);


				addToPatternSet(result, rowList.get(i).toString(),depth);
			}

		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}

		return	getPatternsCountAtDepth(depth);
	}

	private static int TokenizeOnSpacePreserveAlphabetsCoarse(List rowList, String depth)
	{


		Pattern replace;
		Matcher matcher2;
		try
		{
			/*JSONObject jobj ;
			String row;

			for(int i =0; i< rowArr.length();i++)
			{
				jobj = new JSONObject(rowArr.get(i).toString());
				row=jobj.get("v").toString();

				String result = new String( row.getBytes("UTF-8") , "UTF-8");*/
			//String row;

			/*for(int i =0; i< rowArr.length();i++)
			{
				jobj = new JSONObject(rowArr.get(i).toString());
				row=jobj.get("v").toString();
			 */
			for(int i=0;i <  rowList.size(); i++)
			{

				String result = new String( rowList.get(i).toString().getBytes("UTF-8") , "UTF-8");
				//row = result;

				/*replace = Pattern.compile("[a-zA-Z]+");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(characterReplacement);*/

				result = ApplyRegexforSingleReplacement( result,"[0-9]+",DigitGroupReplacement);
				result = ApplyRegexforSingleReplacement( result,"\\s",spaceReplacement);

				/*	replace = Pattern.compile("[0-9]+"); original
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(DigitGroupReplacement);

				replace = Pattern.compile("\\s");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(spaceReplacement);
				 */

				result = AppendOccurrranceCounts(result, DigitGroupReplacement, DigitGroupReplacement);
				result = AppendOccurrranceCounts(result, spaceReplacement, spaceReplacement);

				addToPatternSet(result, rowList.get(i).toString(),depth);
			}

		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}

		return	getPatternsCountAtDepth(depth);
	}


	private static int TokenizeOnSpacePreserveAlphabets(List rowList, String depth)
	{


		Pattern replace;
		Matcher matcher2;
		try
		{

			//String row;

			/*for(int i =0; i< rowArr.length();i++)
			{
				jobj = new JSONObject(rowArr.get(i).toString());
				row=jobj.get("v").toString();
			 */
			for(int i=0;i <  rowList.size(); i++)
			{

				String result = new String( rowList.get(i).toString().getBytes("UTF-8") , "UTF-8");
				//row = result;
				/*replace = Pattern.compile("[a-zA-Z]+");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(characterReplacement);*/


				result = ApplyRegexforSingleReplacement( result,"[0-9]",SingleDigitReplacement);
				result = ApplyRegexforSingleReplacement( result,"\\s",spaceReplacement);

				/*		

				replace = Pattern.compile("[0-9]");
				matcher2 = replace.matcher(result);


				int startIndex =0, endIndex=0 , initial=0;
				 while (matcher2.find()) {
				        startIndex = matcher2.start();
				        endIndex= matcher2.end();



				         String initail = result.substring(initial,startIndex);
				         String last = result.substring(endIndex);


				         result=  initail +  escapeMyReplacements( SingleDigitReplacement)
				        		 + last; 

				    }



				//result =  matcher2.replaceAll(SingleDigitReplacement);

				replace = Pattern.compile("\\s");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(spaceReplacement);
				 */



				/*
				original
				replace = Pattern.compile("[0-9]");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(SingleDigitReplacement);

				replace = Pattern.compile("\\s");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(spaceReplacement);


				 */



				result= AppendOccurrranceCounts(result, SingleDigitReplacement,SingleDigitReplacement);
				result = AppendOccurrranceCounts(result,spaceReplacement,spaceReplacement);

				addToPatternSet(result, rowList.get(i).toString(),depth);
			}

		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}

		return	getPatternsCountAtDepth(depth);
	}


	private static int TokenizeOnSpacePreserveDigitsCoarse(List rowList, String depth)
	{


		Pattern replace;
		Matcher matcher2;
		try
		{
			/*JSONObject jobj ;
			String row;

			for(int i =0; i< rowArr.length();i++)
			{
				jobj = new JSONObject(rowArr.get(i).toString());
				row=jobj.get("v").toString();

				String result = new String( row.getBytes("UTF-8") , "UTF-8");*/
			for(int i=0;i <  rowList.size(); i++)
			{

				String result = new String( rowList.get(i).toString().getBytes("UTF-8") , "UTF-8");

				result = ApplyRegexforSingleReplacement( result,"[a-zA-Z]+",CharacterGroupReplacement);
				result = ApplyRegexforSingleReplacement( result,"\\s",spaceReplacement);

				/* original
				replace = Pattern.compile("[a-zA-Z]+");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(CharacterGroupReplacement);

				replace = Pattern.compile("\\s");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(spaceReplacement);

				 */
				result = AppendOccurrranceCounts(result, CharacterGroupReplacement, CharacterGroupReplacement);
				result = AppendOccurrranceCounts(result, spaceReplacement, spaceReplacement);

				addToPatternSet(result, rowList.get(i).toString(),depth);
			}

		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}

		return	getPatternsCountAtDepth(depth);
	}

	private static int TokenizeOnSpacePreserveDigits(List rowList, String depth)
	{


		Pattern replace;
		Matcher matcher2;
		try
		{
			/*JSONObject jobj ;
			String row;

			for(int i =0; i< rowArr.length();i++)
			{
				jobj = new JSONObject(rowArr.get(i).toString());
				row=jobj.get("v").toString();

				String result = new String( row.getBytes("UTF-8") , "UTF-8");*/
			for(int i=0;i <  rowList.size(); i++)
			{

				String result = new String( rowList.get(i).toString().getBytes("UTF-8") , "UTF-8");

				result = ApplyRegexforSingleReplacement( result,"[a-zA-Z]",SingleCharacterReplacement);
				result = ApplyRegexforSingleReplacement( result,"\\s",spaceReplacement);

				/* original	
			replace = Pattern.compile("[a-zA-Z]");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(SingleCharacterReplacement);

				replace = Pattern.compile("\\s");
				matcher2 = replace.matcher(result);
				result =  matcher2.replaceAll(spaceReplacement);

				 */	
				result = AppendOccurrranceCounts(result, SingleCharacterReplacement, SingleCharacterReplacement);
				result = AppendOccurrranceCounts(result, spaceReplacement, spaceReplacement);


				addToPatternSet(result, rowList.get(i).toString(),depth);
			}

		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}

		return	getPatternsCountAtDepth(depth);
	}


	private static int getPatternsCountAtDepth(String depth)
	{
		int count=0;
		try
		{

			for(String patternKey : gobjDepthPatternMap.get(depth).keySet())
			{
				//count +=gobjDepthPatternMap.get(depth).get(patternKey).frequency;
				count++;

			}

		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}


		return count;



	}


	private static String escapeMyReplacements(String input)
	{
		return EscapingCharacter + input;
	}

	private static String AppendOccurrranceCounts(String value , String match , String replaceWith)
	{
		try
		{
			return value;
			/*
	 char matchChar = match.toCharArray()[0]; 
	Pattern replace = null;
	Matcher matcher2 = null;


	 int i=0,count=0;
	  String res ="";
	  boolean found = false;
	  while(i <= value.length()-1)
	  {
		  found = false;
		while(value.charAt(i)==matchChar)
		{
			found = true;
			count++;
			i++;
			value = value.replaceFirst(""+matchChar, "R"); //replace each "match" character with R

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
			 */

		}
		catch(Exception ex)
		{
			return "";
		}
	}

	private static String getStringofGivenLength(String symbol, int length)
	{
		StringBuilder result =new StringBuilder(length);
		for(int i =0; i < length; i++)
		{
			result.append(symbol);
		}
		return result.toString();
	}

	/*This method should be called
	 * before replacing characters/digits/spaces
	 */
	private static String ReplaceSpecialChars(String inputString)
	{

		StringBuilder result =new StringBuilder(inputString.length());

		for(int i =0;i < inputString.length()-1;i++)
		{
			if("" + inputString.charAt(i)== specialCharReplacement)
			{
				result.append(specialCharReplacement + specialCharReplacement);
			}
			else
			{
				result.append(inputString.charAt(i));
			}

		}
		return result.toString();
	}

	private static String ApplyRegexforSingleReplacement(String inputString , String regex , String replacement)
	{
		Pattern replace = null;
		Matcher matcher2 = null;



		replace = Pattern.compile(regex);
		matcher2 = replace.matcher(inputString);
		return matcher2.replaceAll(replacement);

		/*
String returnValue = "";
	String data = inputString;
try
{
	replace = Pattern.compile(regex);
	matcher2 = replace.matcher(data);


	int startIndex =0, endIndex=0 , initial=0;
	 while (matcher2.find()) {
	        startIndex = matcher2.start();
	        endIndex= matcher2.end();
	        returnValue += data.substring(initial,startIndex) +  escapeMyReplacements( replacement);
	        data=   data.substring(endIndex); 



	         replace = Pattern.compile(regex);
	     	matcher2 = replace.matcher(data);

	    }
}
catch(Exception ex)
{
	System.out.println("ApplyRegexforSingleReplacement :" + ex.getMessage());
	}

return returnValue=="" ? inputString : returnValue + data;
		 */
	}


	private static void findFacotrs()
	{
		int N =8;

		Double x = Math.floor(Math.sqrt(N));

		while(N%x != 0)
		{
			x++;
		}
		System.out.println(x + " * " + N/x);

	}

}
