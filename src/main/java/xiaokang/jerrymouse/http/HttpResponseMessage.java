package xiaokang.jerrymouse.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HttpResponseMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5017144959318148616L;
	// HTTP返回码
	public static final int HTTP_STATUS_SUCCESS = 200;
	public static final int HTTP_MOVED_TEMPORARILY = 302;
	public static final int HTTP_STATUS_FORBIDDEN = 403;
	public static final int HTTP_STATUS_NOT_FOUND = 404;
	public static final int HTTP_STATUS_SERVER_ERROR = 500;
	public static final int HTTP_STATUS_NOT_IMPLEMENTED = 501;

	// Content-Type
	public static final String CONTENT_OCTET_STREAM = "application/octet-stream";
	public static final String CONTENT_TEXT_HTML = "text/html";

	private final Map<String, String> headers = new HashMap<String, String>();

	// HTTP响应体输出流
	public transient ByteArrayOutputStream body = new ByteArrayOutputStream();
	public byte[] bodyData;

	private int responseCode = HTTP_STATUS_SUCCESS;

	public HttpResponseMessage() {
		headers.put("Server", "JerryMouse 1.0");
		headers.put("Cache-Control", "private");
		headers.put("Content-Type", CONTENT_TEXT_HTML);
		headers.put("Connection", "keep-alive");
		headers.put("Keep-Alive", "120");
		headers.put("Date", new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
				.format(new Date()));
		headers.put("Last-Modified", new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
				.format(new Date()));
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setContentType(String contentType) {
		headers.put("Content-Type", contentType);
	}

	public void setContentDisposition(String contentDisposition) {
		headers.put("Content-Disposition", contentDisposition);
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public int getResponseCode() {
		return this.responseCode;
	}

	public void appendBody(byte[] b) {
		try {
			body.write(b);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void appendFileToBody(File file) {
		try {
			FileInputStream in = new FileInputStream(file);
			body = new ByteArrayOutputStream((int) file.length());
			byte[] b = new byte[(int) file.length()];
			int n;
			while ((n = in.read(b)) != -1) {
				body.write(b, 0, n);
			}
			in.close();
		} catch (IOException e) {
			// log.error("helper:get bytes from file process error!");
			e.printStackTrace();
		}
	}

	public void appendBody(String s) {
		try {
			body.write(s.getBytes());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public byte[] getBody() {
		if (body == null) {
			return bodyData;
		} else {
			return body.toByteArray();
		}
	}

	public int getBodyLength() {
		if (body == null) {
			return bodyData.length;
		} else {
			return body.size();
		}
	}

}
