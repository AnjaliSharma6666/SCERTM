package SCEXRun.SCEXRun;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SCMaven {

	public static void main(String[] args) throws Exception {
		
		String[] input = args[0].split("::");
		String[] executionNodes = input[0].split(",");
		
		for (String node : executionNodes)  
        { 
			SCMaven.SCRunExecution(Integer.parseInt(node), input[1], input[2]);
        } 
	
	}

	private static void SCRunExecution(int SC_ExecutionNodeId, String SC_Host, String SC_Token) throws Exception {

		try {
			URL url = new URL(SC_Host + "/Services1.0/execution/executionplanruns?nodeId=" + SC_ExecutionNodeId);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "application/json;charset=UTF-8");
			conn.setRequestProperty("SC-SESSION-ID", SC_Token);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP Error code : " + conn.getResponseCode());
			}

			else {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));

				StringBuilder response = new StringBuilder();
				String responseLine = null;

				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				String JsonString = response.toString();
				Map<String, String> map = new HashMap<String, String>();

				String[] JStr = JsonString.split("}],");
				String Data = JStr[1];
				String[] KeyVal = Data.replaceAll(",", ":").split(":");

				for (int i = 0; i < KeyVal.length; i = i + 2) {
					map.put(KeyVal[i], KeyVal[i + 1]);
				}

				System.out.println("Started run with  " +  map.get("\"executionPlanRunId\"") + " for execution plan is "+ map.get("\"executionPlanName\"") + " with Run Status " + map.get("\"status\"") + " on Silk Central Project ID " + map.get("\"projectId\"") );
				Thread.sleep(20000);
			}
			conn.disconnect();

		} catch (Exception e) {
			System.out.println("Exception:- " + e);
		}
	}

}
