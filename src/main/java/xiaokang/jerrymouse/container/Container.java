package xiaokang.jerrymouse.container;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import xiaokang.jerrymouse.http.HttpRequestMessage;
import xiaokang.jerrymouse.http.HttpResponseMessage;
import xiaokang.jerrymouse.http.Request;
import xiaokang.jerrymouse.http.Response;
import xiaokang.jerrymouse.server.Connector;

public class Container {

	private static Container mInstance;
	public static final int DEFULT_MAX_SERVLRT_THREAD_POOL_SIZE = 20;
	public static ExecutorService servletThreadPool;

	// servlet映射
	private static HashMap<String, String> servletMapping = new HashMap<String, String>();

	public static Container getInstance() {
		return mInstance;
	}

	public Container(int maxServletThreadPoolSize, String webRoot) {
		// TODO Auto-generated constructor stub
		Container.servletThreadPool = Executors
				.newFixedThreadPool(maxServletThreadPoolSize);
		try {
			traversalWebRoot(webRoot);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("web.xml文件解析错误");
		}
		mInstance = this;
	}

	private void traversalWebRoot(String filePath) throws Exception {
		File dir = new File(filePath);
		if (!dir.exists()) {
			System.out.println("根目录不存在");
			return;
		}
		File[] files = dir.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				traversalWebRoot(files[i].getAbsolutePath());
			} else {
				String strFileName = files[i].getAbsolutePath();
				if (strFileName.endsWith("web.xml")) {
					analyzeXml(files[i]);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void analyzeXml(File file) throws Exception {

		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		Element root = document.getRootElement();
		List<Element> servlets = root.elements("servlet");
		List<Element> servletsMapping = root.elements("servlet-mapping");
		for (Element element : servlets) {
			ServletFactory.addServlet(
					element.element("servlet-name").getText(),
					element.element("servlet-class").getText(),
					file.getParent());
		}
		for (Element element : servletsMapping) {
			servletMapping.put(element.element("url-pattern").getText(),
					element.element("servlet-name").getText());
		}
	}

	public void doRequest(final Connector connector,
			final HttpRequestMessage request) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		Container.servletThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpResponseMessage responseMessage = new HttpResponseMessage();
				try {
					String servletName = servletMapping.get(request
							.getRequestUrl());
					if (servletName == null) {
						responseMessage
								.setResponseCode(HttpResponseMessage.HTTP_STATUS_NOT_FOUND);
						responseMessage.appendBody("<html>"
								+ "<head><title>404 Not Found</title></head>"
								+ "<body bgcolor=\"white\">"
								+ "<center><h1>404 Not Found</h1></center>"
								+ "<hr><center>JerryMouse/1.0</center>"
								+ "</body>" + "</html>");
					} else {
						HttpServlet servlet = ServletFactory
								.getInstance(servletName);
						servlet.service(new Request(request), new Response(
								responseMessage));
					}
					responseMessage.bodyData = responseMessage.getBody();
					connector.onResponse(responseMessage);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

}