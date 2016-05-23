package xiaokang.jerrymouse.http;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP请求
 * 
 * @author xiaokang
 * 
 */
public class HttpRequestMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3596565304085296975L;
	/**
	 * HTTP请求的主要属性及内容
	 */

	public static final int HTTP_GET = 0;
	public static final int HTTP_POST = 1;
	public static final int NOT_SUPPORT_METHOD = -1;

	// HTTP请求方法
	private int requestMethod = HTTP_GET;
	// 请求url
	private String requestUrl = "";
	// HTTP请求首部
	private Map<String, String> headers = null;
	// GET请求参数
	private Map<String, String> GETParameter = new HashMap<String, String>();
	// GET请求参数
	private Map<String, String> POSTParameter = new HashMap<String, String>();
	// HTTP请求体
	private byte[] body;

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	/**
	 * 获取HTTP请求的请求体
	 */
	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public int getRequestMethod() {
		return requestMethod;
	}

	public String getStringRequestMethod() {
		switch (requestMethod) {
		case 0:
			return "GET";
		case 1:
			return "POST";
		case 2:
			return "HEAD";
		default:
			return "GET";
		}
	}

	public void setRequestMethod(int requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public Map<String, String> getGETParameter() {
		return GETParameter;
	}

	public void setGETParameter(Map<String, String> gETParameter) {
		GETParameter = gETParameter;
	}

	public Map<String, String> getPOSTParameter() {
		return POSTParameter;
	}

	public void setPOSTParameter(Map<String, String> pOSTParameter) {
		POSTParameter = pOSTParameter;
	}

}
