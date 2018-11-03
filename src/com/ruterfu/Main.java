/**
 * 
 */
package com.ruterfu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.Map;

/**
 * @author Ruter
 *
 */
public class Main {

	public static void main(String[] args) throws Exception {
		Map<String, String> arguments = Tools.parseArguments(args);
		if(arguments == null) {
			Tools.printSystemOut("arguments like java -jar --domain=aa.test.com --access=ABCDEFG --secret=SECRET");
			return;
		}
		String accessKey = arguments.getOrDefault("access", null);
		String secret = arguments.getOrDefault("secret", null);
		String topDomain = arguments.getOrDefault("topDomain", null);
		String rrValue = arguments.getOrDefault("rr", null);

		if(!Tools.isNotNull(accessKey, secret, topDomain, rrValue)) {
			Tools.printSystemOut("arguments like java -jar --domain=aa.test.com --access=ABCDEFG --secret=SECRET");
			return;
		}
		// 先查询IP
		String httpIpResponse = Tools.httpGet(Constant.IP_QUERY_API, true);
		String ip = getIP(httpIpResponse);
		if(ip == null) {
			Tools.printSystemOut("Cannot get IP address, ip address is null");
			return;
		}
		String content;

		// 获得文件中IP信息
		File file = new File(Constant.TEMP_FOLDER, "IP-QUERY-COMPARABLE-RUTERFU.dat");
		Tools.printSystemOut("Read file " + file.getAbsolutePath());
		if(file.exists()) {
			String fileContent = readFile(file);
			// 如果IP一样, 跳过
			if(!Tools.isNull(fileContent) && fileContent.equals(ip)) {
				Tools.printSystemOut("Local Domain IP is " + fileContent + ", current IP " + ip + ", need not to change");
				return;
			}
		}
		// 继续查询
		AliyunDomainURLSignature aliyunDomainURLSignature = new AliyunDomainURLSignature(accessKey, secret);
		String connectUrl = aliyunDomainURLSignature.getAliyunDomainList(topDomain, rrValue);
		content = Tools.httpGet(connectUrl, false);
		if(content == null) {
			Tools.printSystemOut("On Aliyun Query Domain, Cannot connect " + connectUrl);
			return;
		}
		JSONObject obj = new JSONObject(content);
		if(obj.has("Message")) {
			Tools.printSystemOut("Aliyun Response : " + obj.getString("Message"));
			return;
		}
		int recordCount = obj.getInt("TotalCount");
		if(recordCount == 0) {
			Tools.printSystemOut("Aliyun Response : " + "Remote not found domain " + rrValue + "." + topDomain);
			return;
		}
		JSONArray record = obj.getJSONObject("DomainRecords").getJSONArray("Record");
		String recordId = record.length()  > 0 ? record.getJSONObject(0).getString("RecordId") : null;
		String ipAddress = record.length()  > 0 ? record.getJSONObject(0).getString("Value") : null;
		if(ip.equals(ipAddress)) {
			Tools.printSystemOut("Aliyun Domain IP is " + ipAddress + ", current IP " + ip + ", need not to change");// 写入文件
			if(!file.exists()) {
				writeToFile(file, ip);
			}
		} else {
			// 执行修改
			connectUrl = aliyunDomainURLSignature.updateDomainRecord(recordId, "home", ip);
			content = Tools.httpGet(connectUrl, false);
			if(content == null) {
				Tools.printSystemOut("On Aliyun Update Domain, Cannot connect " + connectUrl);
				return;
			}
			Tools.printSystemOut("Aliyun Domain IP change to " + ipAddress);// 写入文件
			if(!file.exists()) {
				writeToFile(file, ip);
			}
			// 写入文件
			writeToFile(file, ip);
		}
	}

	private static String getIP(String httpIpResponse) {
		String[] splits = Tools.split(httpIpResponse, " ");
		Integer index = null;
		int currentPoint = 0;
		for(String d :splits) {
			if(d.contains(":")) {
				index = currentPoint;
				break;
			}
			currentPoint++;
		}
		return index == null ? null : splits[index + 1].trim();
	}

	private static String readFile(File file) {
		BufferedReader br = null;
		FileReader fr = null;
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			StringBuilder sb = new StringBuilder();
			String tmp;
			while((tmp = br.readLine()) != null) {
				sb.append(tmp);
			}
			return sb.toString().trim();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try { if(br != null){ br.close(); }  } catch (Exception ignored) { }
			try { if(fr != null){ fr.close(); }  } catch (Exception ignored) { }
		}
	}
	private static void writeToFile(File file, String ip) {
		FileWriter fr = null;
		try {
			fr = new FileWriter(file,false);
			fr.write(ip);
			fr.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try { if(fr != null){ fr.close(); }  } catch (Exception ignored) { }
		}
	}

}
