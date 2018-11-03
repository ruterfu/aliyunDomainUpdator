/**
 * 
 */
package com.ruterfu;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Ruter
 *
 */
public class AliyunDomainURLSignature {
	private String accessKey;
	private String secret;
	private String format;
	private String httpMethod;

	private final String ENCODING = "UTF-8";
    private final String VERSION = "2015-01-09";
    private final String SIGNATURE_VERSION = "1.0";
    private final String SIGNATURE_METHOD = "HMAC-SHA1";
    private final String REGION_ID = "cn-hangzhou";

    private final String ALGORITHM = "HmacSHA1";
	public AliyunDomainURLSignature(String accessKey, String secret) {
		this.accessKey = accessKey;
		this.secret = secret;
		this.format = "JSON";
		this.httpMethod = "GET";
	}
	/**
	 * IP解析增加简化
	 * @param domainName 需要修改的域名
	 * @param name 主机记录, 例如pi.ruterfu.com就填写pi
	 * @param ip 记录值, 这个IP解析到什么地方, 填写IP地址
	 * @return URL地址
	 */
	public String setNewDomainRecord(String domainName, String name, String ip) {
		return setNewDomainRecord(domainName, name, "A", ip);
	}

	/**
	 * IP解析增加
	 * @param domainName 需要修改的域名
	 * @param rr 主机记录, 例如pi.ruterfu.com就填写pi
	 * @param type 记录类型, IP记录为A记录, 详见 https://help.aliyun.com/document_detail/29805.html?spm=a2c4g.11186623.2.6.Pg0Uh8
	 * @param value 记录值, 这个IP解析到什么地方, 填写IP地址
	 * @return URL地址
	 */
	public String setNewDomainRecord(String domainName, String rr, String type, String value) {
		String timeStamp = formatIso8601Date(new Date());
		String sigNounce = UUID.randomUUID().toString();
		String action = "AddDomainRecord";

		Map<String, String> parameters = new HashMap<String, String>();
        // 加入请求参数
        parameters.put("Action", action);
        parameters.put("DomainName", domainName);
        parameters.put("Version", VERSION);
        parameters.put("AccessKeyId", accessKey);
        parameters.put("Timestamp", timeStamp);
        parameters.put("SignatureMethod", SIGNATURE_METHOD);
        parameters.put("SignatureVersion", SIGNATURE_VERSION);
        parameters.put("SignatureNonce", sigNounce);
		parameters.put("RegionId", REGION_ID);
        parameters.put("Format", format);
        parameters.put("RR", rr);
        parameters.put("Type", type);
        parameters.put("Value", value);

        String sign = getGenerate(parameters);

		return Constant.ALIYUN_DOMAIN_API + "/?Format=" + format +
                "&AccessKeyId=" + accessKey +
                "&Action=" + action +
                "&SignatureMethod=" + SIGNATURE_METHOD +
                "&DomainName=" + domainName +
                "&SignatureNonce=" + sigNounce +
                "&SignatureVersion=" + SIGNATURE_VERSION +
                "&Version=" + VERSION +
                "&Timestamp=" + Tools.url(timeStamp) +
                "&RR=" + rr +
                "&Type=" + type +
                "&RegionId=" + REGION_ID +
                "&Value=" + value +
                "&Signature=" + Tools.url(sign);
	}

	/**
	 * IP更新解析简化
	 * @param recordId 需要修改
	 * @param name 主机记录, 例如pi.ruterfu.com就填写pi
	 * @param ip 记录值, 这个IP解析到什么地方, 填写IP地址
	 * @return URL地址
	 */
	public String updateDomainRecord(String recordId, String name, String ip) {
		return updateDomainRecord(recordId, name, "A", ip);
	}
	/**
	 * 更新解析接口
	 * @param recordId 解析记录ID(新增会返回ID, 或查询)
	 * @param rr 主机记录，如果要解析@.exmaple.com，主机记录要填写"@”，而不是空
	 * @param type 记录解析类型
	 * @param value 记录值
	 * @return URL地址
	 */
	public String updateDomainRecord(String recordId, String rr, String type, String value) {
		String timeStamp = formatIso8601Date(new Date());
		String sigNounce = UUID.randomUUID().toString();
		String action = "UpdateDomainRecord";
		
		Map<String, String> parameters = new HashMap<String, String>();
		// 加入请求参数
		parameters.put("Action", action);
		parameters.put("RR", rr);
		parameters.put("Version", VERSION);
        parameters.put("AccessKeyId", accessKey);
        parameters.put("Timestamp", timeStamp);
        parameters.put("SignatureMethod", SIGNATURE_METHOD);
        parameters.put("SignatureVersion", SIGNATURE_VERSION);
		parameters.put("SignatureNonce", sigNounce);
		parameters.put("RegionId", REGION_ID);
		parameters.put("Format", format);
		parameters.put("RecordId", recordId);
		parameters.put("Type", type);
		parameters.put("Value", value);
		
		String sign = getGenerate(parameters);

		return Constant.ALIYUN_DOMAIN_API + "/?Format=" + format +
				"&AccessKeyId=" + accessKey +
				"&Action=" + action +
				"&SignatureMethod=" + SIGNATURE_METHOD +
				"&RR=" + rr +
				"&SignatureNonce=" + sigNounce +
				"&SignatureVersion=" + SIGNATURE_VERSION +
				"&Version=" + VERSION +
				"&Timestamp=" + Tools.url(timeStamp) +
				"&RecordId=" + recordId +
				"&Type=" + type +
				"&Value=" + value +
				"&RegionId=" + REGION_ID +
				"&Signature=" + Tools.url(sign);
	}

	public String getAliyunDomainList(String domainName, String rrKeyword) {

		String timeStamp = formatIso8601Date(new Date());
		String sigNounce = UUID.randomUUID().toString();
		String action = "DescribeDomainRecords";
		
		Map<String, String> parameters = new HashMap<String, String>();
        // 加入请求参数
        parameters.put("Action", action);
        parameters.put("DomainName", domainName);
        parameters.put("Version", VERSION);
        parameters.put("AccessKeyId", accessKey);
        parameters.put("Timestamp", timeStamp);
        parameters.put("RRKeyWord", rrKeyword);
        parameters.put("SignatureMethod", SIGNATURE_METHOD);
        parameters.put("SignatureVersion", SIGNATURE_VERSION);
		parameters.put("RegionId", REGION_ID);
        parameters.put("SignatureNonce", sigNounce);
        parameters.put("Format", format);
        String sign = getGenerate(parameters);

		return Constant.ALIYUN_DOMAIN_API + "/?" +
                "SignatureVersion=" + SIGNATURE_VERSION +
                "&Action=" + action +
                "&RRKeyWord=" + rrKeyword +
                "&Format=" + format +
                "&SignatureNonce=" + sigNounce +
                "&Version=" + VERSION +
                "&DomainName=" + domainName +
                "&AccessKeyId=" + accessKey +
                "&Signature=" + Tools.url(sign) +
                "&SignatureMethod=" + SIGNATURE_METHOD +
                "&RegionId=" + REGION_ID +
                "&Timestamp=" + Tools.url(timeStamp);
	}
	private String getGenerate(Map<String, String> parameters) {
		
        try {
        	// 对参数进行排序，注意严格区分大小写
            String[] sortedKeys = parameters.keySet().toArray(new String[]{});
            Arrays.sort(sortedKeys);
            final String SEPARATOR = "&";
            // 生成stringToSign字符串
            StringBuilder stringToSign = new StringBuilder();
            stringToSign.append(httpMethod).append(SEPARATOR);
            stringToSign.append(percentEncode("/")).append(SEPARATOR);
            StringBuilder canonicalizedQueryString = new StringBuilder();
            for(String key : sortedKeys) {
                // 这里注意对key和value进行编码
                canonicalizedQueryString.append("&")
                .append(percentEncode(key)).append("=")
                .append(percentEncode(parameters.get(key)));
            }
            // 这里注意对canonicalizedQueryString进行编码
            stringToSign.append(percentEncode(canonicalizedQueryString.toString().substring(1)));
			return signString(stringToSign.toString(), secret + "&");
        }catch (Exception e) {
			e.printStackTrace();
		}
        return null;
	}
    private String formatIso8601Date(Date date) {
		String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
		SimpleDateFormat df = new SimpleDateFormat(ISO8601_DATE_FORMAT);
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return df.format(date);
    }
    private String percentEncode(String value)
            throws UnsupportedEncodingException{
        return value != null ?
                URLEncoder.encode(value, ENCODING).replace("+", "%20")
                .replace("*", "%2A").replace("%7E", "~")
                : null;
    }
	public String signString(String source, String accessSecret)
			throws InvalidKeyException, IllegalStateException {
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(new SecretKeySpec(
					accessSecret.getBytes(ENCODING),"HmacSHA1"));
			byte[] signData = mac.doFinal(source.getBytes(ENCODING));
			return Base64Helper.encode(signData);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("HMAC-SHA1 not supported.");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 not supported.");
		}

	}
}
