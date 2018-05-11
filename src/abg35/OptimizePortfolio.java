package abg35;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Servlet implementation class OptimizePortfolio
 */
@WebServlet("/OptimizePortfolio")
public class OptimizePortfolio extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Entered!");
		JSONParser parser = new JSONParser();
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		JSONObject resp = new JSONObject();
		String line1 = "", line2 = "", line3 = "", line4 = "", line5 = "max: ", line6 = "int", line7 = "";
		
		try {
			JSONObject json = (JSONObject) parser.parse(getBody(request));
			double totalInvestment = Double.parseDouble(String.valueOf(json.get("investment")));
			//System.out.println("Investment: " + totalInvestment);
			JSONArray jsonArr=new JSONArray(String.valueOf(json.get("stocks")));
			for(int i = 0; i < jsonArr.length(); i++)
			{
				//System.out.println("Looping...");
				org.json.JSONObject jsonObj = jsonArr.getJSONObject(i);
				String symbol = jsonObj.getString("symbol");
				//System.out.println("symbol: " + symbol);
				String type = jsonObj.getString("type");
				//System.out.println("type: " + type);
				double currentValue = Double.parseDouble(jsonObj.getString("currentValue"));
				//System.out.println("currentValue: " + currentValue);
				float expectedReturn = Float.parseFloat(jsonObj.getString("expectedReturn")); 	
				//System.out.println("expectedReturn: " + expectedReturn);
				float beta = Float.parseFloat(jsonObj.getString("beta")); 	
				
				line1 += symbol + ">=1;\r\n";
				line2 += "+" + currentValue + symbol + " ";
				if(type.equals("DOW30")) {
					line3 += "+" + (0.32 * currentValue) + symbol + " ";
					line4 += "+" + (0.28 * currentValue) + symbol + " ";
				}else {
					line3 += "-" + (0.68 * currentValue) + symbol + " ";
					line4 += "-" + (0.72 * currentValue) + symbol + " ";
				}
				line5 += "+" + (currentValue * expectedReturn / 100) + symbol + " ";
				line6 += " " + symbol;
				if(beta - 1.2 > 0) {
					line7 += "+" + (beta - 1.2) + symbol + " ";
				}else {
					line7 += (beta - 1.2) + symbol + " ";
				}
				
			}
			String data =
					line5 + ";\r\n" +
					line1 +
					line7 + "<= 0;\r\n" +
					line2 + "<=" + totalInvestment + ";\r\n" +
					line2 + ">=" + (totalInvestment - totalInvestment * 10 / 100) + ";\r\n" +
					line3 + ">=0" + ";\r\n" +
					line4 + "<=0" + ";\r\n" +
					line6 + ";";
			//System.out.println(data);
	    	String result = LpSolver.solve(data);
			resp.put("resp", result);
			out.print(resp);
			out.flush();
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getBody(HttpServletRequest request)
	{
		StringBuilder buffer = new StringBuilder();
	    try {
			BufferedReader reader = request.getReader();
			String line;
			while ((line = reader.readLine()) != null) {
			    buffer.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    String data = buffer.toString();
		return data;
	}
}
