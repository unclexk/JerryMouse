package xiaokang.jerrymouse.http;

import java.util.HashMap;
import java.util.regex.PatternSyntaxException;

public class HttpRequestDecoder {

	public HttpRequestMessage decodeRequest(byte[] data) throws Exception {
		HttpRequestMessage request = new HttpRequestMessage();
		// 解析http请求
		int startLineEndPos = 0;
		int headerEndPos = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i] == 0x0D && data[i + 1] == 0x0A) {
				if (startLineEndPos == 0) {
					startLineEndPos = i;
				}
				if (data[i + 2] == 0x0D && data[i + 3] == 0x0A) {
					headerEndPos = i;
					break;
				}
			}
		}
		byte[] startLineData = new byte[startLineEndPos];
		byte[] headersData = new byte[headerEndPos - startLineEndPos];
		byte[] bodyData = new byte[data.length - headerEndPos - 4];
		for (int i = 0; i < data.length; i++) {
			if (i < startLineEndPos) {
				startLineData[i] = data[i];
			} else if (i < headerEndPos) {
				headersData[i - startLineEndPos] = data[i + 2];
			} else {
				if (bodyData.length == 0) {
					break;
				}
				if (i > headerEndPos + 3) {
					bodyData[i - headerEndPos - 4] = data[i];
				}
			}

		}

		String startLine = new String(startLineData, "UTF-8");
		String headersString = new String(headersData, "UTF-8");

		// 解析请求体
		request.setBody(bodyData);

		// 判断请求方法
		if (startLine.startsWith("GET")) {
			request.setRequestMethod(HttpRequestMessage.HTTP_GET);
		} else if (startLine.startsWith("POST")) {
			request.setRequestMethod(HttpRequestMessage.HTTP_POST);
		} else {
			request.setRequestMethod(HttpRequestMessage.NOT_SUPPORT_METHOD);
		}

		try {
			// 提取请求url
			String[] startLineUrl = startLine.split(" ");
			if (startLineUrl.length > 2) {
				String urlArray[] = startLineUrl[1].split("[?]");
				String url = urlArray[0];
				request.setRequestUrl(url);
				// 提取get请求参数
				if (urlArray.length > 1
						&& request.getRequestMethod() == HttpRequestMessage.HTTP_GET) {
					decodeFormData(urlArray[1], request);
				} else if (request.getRequestMethod() == HttpRequestMessage.HTTP_POST) {
					decodeFormData(new String(bodyData, "UTF-8"), request);
				}
			}
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
		}

		// 解析首部
		String[] headers = headersString.split("\n");
		HashMap<String, String> headersMap = new HashMap<String, String>();
		for (int i = 0; i < headers.length; i++) {
			String[] temp = headers[i].split(": ");
			headersMap.put(temp[0], temp[1]);
		}
		request.setHeaders(headersMap);

		return request;
	}

	// 解析请求数据
	private void decodeFormData(String paramString, HttpRequestMessage request) {
		String params[] = paramString.split("[&]");
		HashMap<String, String> paramter = new HashMap<String, String>();
		for (int i = 0; i < params.length; i++) {
			String[] temp = params[i].split("=");
			paramter.put(temp[0], temp[1]);
		}
		if (request.getRequestMethod() == HttpRequestMessage.HTTP_GET) {
			request.setGETParameter(paramter);
		} else if (request.getRequestMethod() == HttpRequestMessage.HTTP_POST) {
			request.setPOSTParameter(paramter);
		}
	}
}
