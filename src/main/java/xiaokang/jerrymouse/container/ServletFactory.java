package xiaokang.jerrymouse.container;

import java.io.File;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;

public class ServletFactory {

	private static HashMap<String, HttpServlet> servlets = new HashMap<String, HttpServlet>();

	public static HttpServlet getInstance(String servletName) {
		return servlets.get(servletName);
	}

	public static void addServlet(String servletName, String className,
			String parentPath) throws Exception {
		File classes = new File(parentPath + "/classes");
		if (!classes.exists()) {
			System.out.println("classes目录不存在");
			return;
		}
		JerryClassLoader classLoader = new JerryClassLoader();
		StringBuilder sb = new StringBuilder();
		String[] classinfo = className.split("[.]");
		for (String temp : classinfo) {
			sb.append("/" + temp);
		}
		Class<?> clazz = classLoader.getClassByFile(classes + sb.toString()
				+ ".class");
		classLoader.loadClass(HttpServlet.class.getName());
		HttpServlet servlet = (HttpServlet) clazz.newInstance();
		servlets.put(servletName, servlet);
	}
}
