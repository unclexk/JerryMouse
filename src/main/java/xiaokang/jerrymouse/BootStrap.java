package xiaokang.jerrymouse;

import java.io.File;
import java.util.HashMap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import xiaokang.jerrymouse.container.Container;
import xiaokang.jerrymouse.server.Server;

/**
 * 
 * 服务器启动程序，选择启动代理服务器或节点服务器
 * 
 * @version 1.0 2015-04-10
 * @author xiaokang
 */
public class BootStrap {

	private static HashMap<String, String> configs = new HashMap<String, String>();

	public static void main(String[] args) throws DocumentException {
		initConfigs();
		// 服务器端口号
		int port = Server.DEFULT_PORT;
		if (configs.get("port") != null) {
			port = Integer.valueOf(configs.get("port"));
		}
		// I/O线程数量
		int ioPoolSize = Server.DEFULT_MAX_IO_THREAD_POOL_SIZE;
		if (configs.get("io_pool_size") != null) {
			ioPoolSize = Integer.valueOf(configs.get("io_pool_size"));
		}
		// 服务器资源根目录
		String webRoot = Server.DEFULT_WEB_ROOT;
		if (configs.get("web_root") != null) {
			webRoot = configs.get("web_root");
		}
		// servelet线程池大小
		int spoolSize = Container.DEFULT_MAX_SERVLRT_THREAD_POOL_SIZE;
		if (configs.get("servelet_pool_size") != null) {
			spoolSize = Integer.valueOf(configs.get("servelet_pool_size"));
		}

		// 新建服务器实例
		Server server = new Server(port, webRoot, ioPoolSize);
		// 初始化servelet容器
		new Container(spoolSize, webRoot);
		// 拉起服务器监听线程
		new Thread(server, "JerryMouse").start();

	}

	private static void initConfigs() throws DocumentException {
		File config = new File("config.xml");
		if (!config.exists()) {
			System.out.println("未找到配置文件！将启用默认配置");
		} else {
			SAXReader reader = new SAXReader();
			Document document = reader.read(config);
			Element root = document.getRootElement();

			Element port = root.element("port");
			Element web_root = root.element("web_root");
			Element ioPoolSize = root.element("io_pool_size");
			Element serveletPoolSize = root.element("servelet_pool_size");

			configs.put("port", port.getText());
			configs.put("web_root", web_root.getText());
			configs.put("ioPoolSize", ioPoolSize.getText());
			configs.put("serveletPoolSize", serveletPoolSize.getText());
		}
	}
}
