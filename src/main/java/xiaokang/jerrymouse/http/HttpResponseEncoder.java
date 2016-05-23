package xiaokang.jerrymouse.http;

import java.util.Map.Entry;

/**
 * HTTP响应编码
 */
public class HttpResponseEncoder {

	private StringBuilder builder;
	private static final byte[] CRLF = new byte[] { 0x0D, 0x0A };

	public String encodeHeader(HttpResponseMessage message) {
		// output all headers except the content length
		builder = new StringBuilder();
		// HTTP响应起始行
		builder.append("HTTP/1.1 ");
		builder.append(String.valueOf(message.getResponseCode()));

		// 返回码
		switch (message.getResponseCode()) {
		case HttpResponseMessage.HTTP_STATUS_SUCCESS:
			builder.append(" OK");
			break;
		case HttpResponseMessage.HTTP_MOVED_TEMPORARILY:
			builder.append(" Moved Temporarily");
			break;
		case HttpResponseMessage.HTTP_STATUS_NOT_FOUND:
			builder.append(" Not Found");
			break;
		case HttpResponseMessage.HTTP_STATUS_SERVER_ERROR:
			builder.append(" Internal Server Error");
			break;
		case HttpResponseMessage.HTTP_STATUS_NOT_IMPLEMENTED:
			builder.append(" Not Implemented");
			break;
		case HttpResponseMessage.HTTP_STATUS_FORBIDDEN:
			builder.append(" Forbidden");
			break;
		}
		builder.append(new String(CRLF));

		// HTTP响应首部
		for (Entry<String, String> entry : message.getHeaders().entrySet()) {
			builder.append(entry.getKey());
			builder.append(": ");
			builder.append(entry.getValue());
			builder.append(new String(CRLF));
		}
		// 响应体长度
		builder.append("Content-Length: ");
		builder.append(String.valueOf(message.getBodyLength()));
		builder.append(new String(CRLF));
		builder.append(new String(CRLF));
		return builder.toString();
	}
}
