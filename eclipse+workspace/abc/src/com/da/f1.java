package com.da;

import java.io.IOException;
import java.io.PrintWriter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.json.JSONException;
import org.json.JSONObject;



/**
 * Servlet implementation class f1
 */
public class f1 extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	private String identifiedDatatype = ""; 

	public static final String []DATE_FORMAT_ARRAY = {"dd-MMM-yy", "yyyy.MM.dd G 'at' HH:mm:ss z", "EEE, MMM d, ''yy", "h:mm a", "hh 'o''clock' a, zzzz",
		"K:mm a, z", "yyyyy.MMMMM.dd GGG hh:mm aaa", "EEE, d MMM yyyy HH:mm:ss Z", "dd.MM.yy",
		"yyyy.MM.dd G 'at' hh:mm:ss z", "EEE d MMM yy", "yyyy-mm-dd", "yyyy/mm/dd", "yyyy-MM-dd HH:mm:ss,z", 
		"EEE, dd MMM yyyy HH:mm:ss z", "yyyy-MM-dd'T'HH:mm:ssz"};
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public f1() 
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		// TODO Auto-generated method stub

		//String data= "{id: \"0\",value: \"100\"}";
		PrintWriter responseWriter= response.getWriter();
		JSONObject reqjobj,respjsonobj=new JSONObject();
		
		String data = request.getParameter("data");
		try
		{
if(data == null)
{
	respjsonobj.append("INPUT", data);
	respjsonobj.append("SUCCESS", "FALSE");
	responseWriter.append(respjsonobj.toString());
	return;
}
		}
		catch(Exception ex)
		{
			responseWriter.append("UnhandledException");
			responseWriter.close();
			return;
		}

		
		//String rowidkey ="id", rowidvalue , rowvaluekey="value", rowvalue; 

		

		try
		{
			reqjobj = new JSONObject(data);

			

			//rowidvalue = jobj.get(rowidkey).toString();
			//rowvalue= jobj.get(rowvaluekey).toString();

			boolean result =	tryParseDataTypes(reqjobj);

			respjsonobj.append("INPUT", reqjobj);
			respjsonobj.append("SUCCESS", result);
			respjsonobj.append("IDENTIFIED_DATATYPE", identifiedDatatype);

			
			

		}
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			responseWriter.write(e.getMessage());
			
			try {
				respjsonobj.append("INPUT", data);
				respjsonobj.append("SUCCESS", "FALSE");
				responseWriter.append(respjsonobj.toString());
				responseWriter.close();
				return;
			} catch (JSONException ex) {
				responseWriter.append("UnhandledException");
				responseWriter.close();
				return;
			}
		}
		
		responseWriter.append(respjsonobj.toString());
		

		responseWriter.close();
		return;

	}


	private boolean tryParseDataTypes(JSONObject jObj) throws JSONException { 

		// Place checks higher in if-else statement to give higher priority to that type and Modify the enum dataType sequence
		identifiedDatatype = "";
		String currentId = jObj.getString("id");
		String str = jObj.getString("value");
		try
		{
			if(Boolean.parseBoolean(str) || str.compareToIgnoreCase("false") == 0) 
			{
				identifiedDatatype = Boolean.class.toString();
				return true;
			}
		}
		catch(Exception e){}
		// Date Parsing with DATE_FORMAT_ARRAY
		try
		{
			Date dateValue = null ;
			int isDateValid = 0, count = 0;
			while(isDateValid  == 0 && count < DATE_FORMAT_ARRAY.length) 
			{
				try 
				{
					SimpleDateFormat tm = new SimpleDateFormat(DATE_FORMAT_ARRAY[count++]);
					tm.setLenient(false);
					dateValue = tm.parse(str);
					isDateValid = 1;
					identifiedDatatype = Date.class.toString();
				}
				catch(Exception e){}
			}
			if (isDateValid==0) throw new Exception();
			
			identifiedDatatype = Date.class.toString();
			return true;
		}
		catch(Exception e){}

		// Integer Parsing
		try
		{
			int intValue = Integer.parseInt(str);

			identifiedDatatype = int.class.toString();
			return true;
		}
		catch(Exception e){} 

		// Double Parsing
		try
		{

			double doubleValue = Double.parseDouble(str);
			identifiedDatatype = double.class.toString();
			return true;
		}
		catch(Exception e){}

		// Parsing Days of the week
		try
		{
			List<String> day = new  ArrayList<String>();
			day.add("SUNDAY");day.add("MONDAY");day.add("TUESDAY");day.add("WEDNESDAY");day.add("THURSDAY");day.add("FRIDAY");day.add("SATURDAY");
			if(day.contains(str.toUpperCase())) 
			{
				identifiedDatatype = "DaysOfWeek";
				return true;
			}
		}
		catch(Exception e){}

		// Everything else :- a String or an Empty String
		try
		{
			if (str.length()!=0) 
			{
				identifiedDatatype = String.class.toString();
				return true;
				
			}
			else 
			{
				identifiedDatatype = "NullString";
				return true;	
			}

		}
		catch(Exception e){}
		identifiedDatatype = "Unidentified";
		return true;
	}


/**
 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
 */
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
{
	// TODO Auto-generated method stub
}

}
