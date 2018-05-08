package abg35;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;

public class LpSolver {
	public static String solve(String input){
		String result = "Could not solve";
		try {
			Socket s=new Socket("afsconnect1.njit.edu", 9090);
			DataInputStream dis=new DataInputStream(s.getInputStream());
			DataOutputStream dos=new DataOutputStream(s.getOutputStream());
			JSONObject json=new JSONObject();
			json.put("rcode", input);
			dos.writeUTF(json.toJSONString());
			System.out.println(input);
			result = dis.readUTF();
			result = result.split("\"")[3];
			System.out.println(result);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
}
