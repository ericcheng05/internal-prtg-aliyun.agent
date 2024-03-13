import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import util.SignatureUtil;
import util.StringUtil;
import util.UrlUtil;

/**
 * @version 2.5.1
 * @author Eric
 * 
 */
public class prtg_aliyun
{
	private final static boolean debug = false;
	
	private final static String defaultSignatureVersion = "1.0";
	private final static String defaultSignatureType = "HMAC-SHA1";
	private final static String apiFortmat = "XML";
	private final static String apiVersion = "2017-03-01";
	private final static String apiDomain = "metrics.aliyuncs.com";
	
/*
	protected HttpHeaders buildHttpHeaders(String sessionId)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authentication", sessionId);
		return headers;
	}
*/
	/**
	 * @param args Alibaba Cloud / Aliyun Parameter required for Processing
	 */
	public static void main(String[] args)
	{
		Map<String, String> parameters = new HashMap<String, String>();
		// String action = "DescribeInstances";
		String action = "QueryMetricLast";
		String result = null;
		
		Request request = null;
		
		if (args.length != 7 && args.length != 9 && args.length != 11 && args.length != 13)
		{
			result = "<prtg><error>1</error><text>Not Enough Parameters or Incorrect Arguments Format</text></prtg>";			
			System.out.println(result);
			return;
		}
		else
		{
			request = new Request (args);			
			
			if (request.getProduct() == null)
			{
				result = "<prtg><error>1</error><text>Incorrect Project</text></prtg>";			
				System.out.println(result);
				return;
			}
		}
					
		parameters.put("RegionId", request.getRegionId());
		parameters.put("Action", action);
		parameters.put("Project", request.getProject());
		parameters.put("Metric", request.getMetric());
		// parameters.put("StartTime", "2017-08-11 16:00:00");
		parameters.put("Period", request.getPeriod());
		parameters.put("Length", "1000");
		// Note the dimension format!
		parameters.put("Dimensions", request.getDimensions());
		parameters.put("AccessKeyId", request.getAccessKeyId());
		parameters.put("Format", apiFortmat);
		parameters.put("SignatureMethod", defaultSignatureType);
		parameters.put("SignatureNonce", UUID.randomUUID().toString());
		parameters.put("SignatureVersion", defaultSignatureVersion);
		parameters.put("Version", apiVersion);
		parameters.put("Timestamp", StringUtil.formatISO8601Date(new Date()));
		
		String url = "http://" + apiDomain;
		if (!url.endsWith("/"))
		{
			url += "/";
		}
		url += "?";
		url += UrlUtil.generateQueryString(parameters, true);
		
		String signature = null;
		try
		{
			signature = SignatureUtil.generate("GET", parameters, request.getAccessKeySecret());
			url += "&Signature=" + signature;
		}
		catch (Exception e)
		{
			result = "<prtg><error>1</error><text>Cannot Generate Signature</text></prtg>";			
			System.out.println(result);
			return;
		}
		
		if (debug)
		{
			System.out.println(url);
		}	
		
		// HttpMethod http1 = new PostMethod();
		HttpMethod httpMethod = new GetMethod(url);
		HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(6000); // Set the request timeout time to 6 seconds.
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(30000); // Set the read timeout time.
		
		byte[] returnBytes = null;
		
		try
		{
			int statusCode = httpClient.executeMethod(httpMethod);
			
			switch (statusCode)
	        {
				case 200:
					break;
	        	case 400:
	        		result = "<prtg><error>1</error><text>Error Code: " + statusCode + " Bad Request (Parameter Error)</text></prtg>";
	        		System.out.println(result);
	        		return;	        		
	        	case 403:
	        		result = "<prtg><error>1</error><text>Error Code: " + statusCode + " Forbidden (AccessKey Pair Not Match)</text></prtg>";
	        		System.out.println(result);
	        		return;
	        	case 500:
	        		result = "<prtg><error>1</error><text>Error Code: " + statusCode + " Server Error (Cannot Connect to " + apiDomain + ")</text></prtg>";
	        		System.out.println(result);
	        		return;
	        	default:
	        		result = "<prtg><error>1</error><text>Other Unknown Error: " + statusCode + "</text></prtg>";
	        		System.out.println(result);
	        		return;
	        }
			
			returnBytes = httpMethod.getResponseBody();
		}
		catch (HttpException e)
		{
			result = "<prtg><error>1</error><text>HTTP Error: " + e.getMessage() + "</text></prtg>";
    		System.out.println(result);
    		return;
		}
		catch (IOException e)
		{
			result = "<prtg><error>1</error><text>IO Error: " + e.getMessage() + "</text></prtg>";
    		System.out.println(result);
    		return;
		}
		
		try
		{
			result = new String(returnBytes, "UTF-8");
			
			if (debug)
			{
				System.out.println(result);
			}
		}
		catch (UnsupportedEncodingException e)
		{
			result = "<prtg><error>1</error><text>Encoding Error: Cannot Parse HTTP Body using UTF-8</text></prtg>";
    		System.out.println(result);
    		return;
		}
		
		Document document = null;
		NodeList nodeList = null;
		Node firstNode = null;
		String result_timestamp = null, result_average = null, result_maximum = null, result_minimum = null;
		StringUtil.StatisticType type = StringUtil.parseStatistic(result);
		
		if (type == null)
		{
			result = "<prtg><error>1</error><text>Parsing Error: Cannot Parse Datapoints</text></prtg>";
    		System.out.println(result);
    		return;
		}
		
		try
		{
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(result.getBytes()));
			document.getDocumentElement().normalize();
			nodeList = document.getElementsByTagName("Datapoints");
			firstNode = nodeList.item(0);
			
			switch(type)
			{
				case AverageMaxMin:
					if (firstNode.getNodeType() == Node.ELEMENT_NODE)
					{
						Element eElement = (Element) firstNode;
						
						if (eElement.getElementsByTagName("Average").item(0).getTextContent() != null)
						{
							result_average = eElement.getElementsByTagName("Average").item(0).getTextContent();				
						}
						else
						{
							result = "<prtg><error>1</error><text>Data Error: Average Data Not Found in HTTP Body</text></prtg>";
				    		System.out.println(result);
				    		return;
						}			
						if (eElement.getElementsByTagName("Maximum").item(0).getTextContent() != null)
						{
							result_maximum = eElement.getElementsByTagName("Maximum").item(0).getTextContent();
						}
						else
						{
							result = "<prtg><error>1</error><text>Data Error: Maximum Data Not Found in HTTP Body</text></prtg>";
				    		System.out.println(result);
				    		return;
						}
						if (eElement.getElementsByTagName("Minimum").item(0).getTextContent() != null)
						{
							result_minimum = eElement.getElementsByTagName("Minimum").item(0).getTextContent();
						}
						else
						{
							result = "<prtg><error>1</error><text>Data Error: Minimum Data Not Found in HTTP Body</text></prtg>";
				    		System.out.println(result);
				    		return;
						}
						
						if (eElement.getElementsByTagName("timestamp").item(0).getTextContent() != null)
						{
							result_timestamp = eElement.getElementsByTagName("timestamp").item(0).getTextContent();
						}
						else
						{
							result = "<prtg><error>1</error><text>Data Error: Timestamp Data Not Found in HTTP Body</text></prtg>";
				    		System.out.println(result);
				    		return;
						}
					}
					else
					{
						result = "<prtg><error>1</error><text>Data Error: No Data in HTTP Body</text></prtg>";
			    		System.out.println(result);
			    		return;
					}
					break;
				case Average:
					if (firstNode.getNodeType() == Node.ELEMENT_NODE)
					{
						Element eElement = (Element) firstNode;
						
						if (eElement.getElementsByTagName("Average").item(0).getTextContent() != null)
						{
							result_average = eElement.getElementsByTagName("Average").item(0).getTextContent();				
						}
						else
						{
							result = "<prtg><error>1</error><text>Data Error: Average Data Not Found in HTTP Body</text></prtg>";
				    		System.out.println(result);
				    		return;
						}			
						
						if (eElement.getElementsByTagName("timestamp").item(0).getTextContent() != null)
						{
							result_timestamp = eElement.getElementsByTagName("timestamp").item(0).getTextContent();
						}
						else
						{
							result = "<prtg><error>1</error><text>Data Error: Timestamp Data Not Found in HTTP Body</text></prtg>";
				    		System.out.println(result);
				    		return;
						}
					}
					else
					{
						result = "<prtg><error>1</error><text>Data Error: No Data in HTTP Body</text></prtg>";
			    		System.out.println(result);
			    		return;
					}
					break;
				case Value_Upper:
					if (firstNode.getNodeType() == Node.ELEMENT_NODE)
					{
						Element eElement = (Element) firstNode;
						
						if (eElement.getElementsByTagName("Value").item(0).getTextContent() != null)
						{
							result_average = eElement.getElementsByTagName("Value").item(0).getTextContent();				
						}
						else
						{
							result = "<prtg><error>1</error><text>Data Error: Value Data Not Found in HTTP Body</text></prtg>";
				    		System.out.println(result);
				    		return;
						}
						if (eElement.getElementsByTagName("timestamp").item(0).getTextContent() != null)
						{
							result_timestamp = eElement.getElementsByTagName("timestamp").item(0).getTextContent();
						}
						else
						{
							result = "<prtg><error>1</error><text>Data Error: Timestamp Data Not Found in HTTP Body</text></prtg>";
				    		System.out.println(result);
				    		return;
						}
					}
					else
					{
						result = "<prtg><error>1</error><text>Data Error: No Data in HTTP Body</text></prtg>";
			    		System.out.println(result);
			    		return;
					}
					break;
				case Value_Lower:
					if (firstNode.getNodeType() == Node.ELEMENT_NODE)
					{
						Element eElement = (Element) firstNode;
						
						if (eElement.getElementsByTagName("value").item(0).getTextContent() != null)
						{
							result_average = eElement.getElementsByTagName("value").item(0).getTextContent();				
						}
						else
						{
							result = "<prtg><error>1</error><text>Data Error: Value Data Not Found in HTTP Body</text></prtg>";
				    		System.out.println(result);
				    		return;
						}
						if (eElement.getElementsByTagName("timestamp").item(0).getTextContent() != null)
						{
							result_timestamp = eElement.getElementsByTagName("timestamp").item(0).getTextContent();
						}
						else
						{
							result = "<prtg><error>1</error><text>Data Error: Timestamp Data Not Found in HTTP Body</text></prtg>";
				    		System.out.println(result);
				    		return;
						}
					}
					else
					{
						result = "<prtg><error>1</error><text>Data Error: No Data in HTTP Body</text></prtg>";
			    		System.out.println(result);
			    		return;
					}
					break;
				default:
					break;
			}
		}
		catch (SAXException e)
		{
			result = "<prtg><error>1</error><text>SAX Error: " + e.getMessage() + "</text></prtg>";
    		System.out.println(result);
    		return;
		}
		catch (ParserConfigurationException e)
		{
			result = "<prtg><error>1</error><text>Parser Error: " + e.getMessage() + "</text></prtg>";
    		System.out.println(result);
    		return;
		}
		catch (IOException e)
		{
			result = "<prtg><error>1</error><text>IO Error: " + e.getMessage() + "</text></prtg>";
    		System.out.println(result);
    		return;
		}
		
		Date time = new Date(Long.parseLong(result_timestamp, 10));
		result = "<prtg><text>Aliyun Sampling Timestamp: " + StringUtil.formatISO8601Date(time) + "</text>";
		
		switch(type)
		{
			case AverageMaxMin:
				result = result
						+ "<result><channel>" + args[2] + " Average</channel><value>" + result_average + "</value><float>1</float></result>"
						+ "<result><channel>" + args[2] + " Maximum</channel><value>" + result_maximum + "</value><float>1</float></result>"
						+ "<result><channel>" + args[2] + " Minimum</channel><value>" + result_minimum + "</value><float>1</float></result>"
						+ "</prtg>"; 
				break;
			case Average: case Value_Upper: case Value_Lower:
				result = result
						+ "<result><channel>" + args[2] + " Value</channel><value>" + result_average + "</value><float>1</float></result>"
						+ "</prtg>"; 
				break;
			default:
				break;
		}
		
		System.out.println(result);
	}
}