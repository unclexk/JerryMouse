package xiaokang.jerrymouse.server;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import xiaokang.jerrymouse.container.Container;
import xiaokang.jerrymouse.http.HttpRequestMessage;
import xiaokang.jerrymouse.http.HttpResponseEncoder;
import xiaokang.jerrymouse.http.HttpResponseMessage;

public class Connector {

	private HttpRequestMessage requestMessage;
	private AsynchronousSocketChannel channel;

	public Connector(AsynchronousSocketChannel channel,
			HttpRequestMessage request) {
		this.channel = channel;
		this.requestMessage = request;
	}

	// 分发请求,生成响应对象,非法请求直接像客户端返回结果
	public void handleRequest() {
		HttpResponseMessage responseMessage;
		if (requestMessage.getRequestMethod() == HttpRequestMessage.NOT_SUPPORT_METHOD) {
			responseMessage = new HttpResponseMessage();
			responseMessage
					.setResponseCode(HttpResponseMessage.HTTP_STATUS_FORBIDDEN);
			responseMessage.appendBody("<html>"
					+ "<head><title>501 Not Implemented</title></head>"
					+ "<body bgcolor=\"white\">"
					+ "<center><h1>501 Not Implemented</h1></center>"
					+ "<hr><center>JerryMouse/1.0</center>" + "</body>"
					+ "</html>");
			doWrite(responseMessage);
		} else {
			// 请求方法支持
			try {
				final String url = requestMessage.getRequestUrl();
				final File file = new File(Server.webRoot + url);
				if (file.exists()) {
					if (file.isFile()) {
						// 多线程读取本地文件
						Server.ioPool.execute(new Thread() {
							@Override
							public void run() {
								HttpResponseMessage response = new HttpResponseMessage();
								String[] fileName = url.split("[.]");
								String contentType = null;
								if (fileName.length > 1) {
									contentType = Server.contentTypeMap.get("."
											+ fileName[fileName.length - 1]);
									if (contentType == null) {
										contentType = HttpResponseMessage.CONTENT_OCTET_STREAM;
										response.setContentDisposition("attachment; filename="
												+ file.getName());
									}
								} else {
									contentType = HttpResponseMessage.CONTENT_OCTET_STREAM;
								}
								response.setContentType(contentType);
								response.appendFileToBody(file);
								doWrite(response);
							}
						});
					} else {
						responseMessage = new HttpResponseMessage();
						responseMessage
								.setResponseCode(HttpResponseMessage.HTTP_STATUS_FORBIDDEN);
						responseMessage.appendBody("<html>"
								+ "<head><title>403 Forbidden</title></head>"
								+ "<body bgcolor=\"white\">"
								+ "<center><h1>403 Forbidden</h1></center>"
								+ "<hr><center>JerryMouse/1.0</center>"
								+ "</body>" + "</html>");
						doWrite(responseMessage);
					}
				} else {
					// 文件不存在，客户访问的有可能为servlet
					// 访问servlet的请求交由servlet容器处理，传入Connector的引用数据准备好即回调
					Container.getInstance().doRequest(this, requestMessage);
				}
			} catch (Exception e) {
				// 服务器出错
				e.printStackTrace();
				responseMessage = new HttpResponseMessage();
				responseMessage
						.setResponseCode(HttpResponseMessage.HTTP_STATUS_SERVER_ERROR);
				try {
					responseMessage.body.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				responseMessage
						.appendBody("<html>"
								+ "<head><title>500 Internal Server Error</title></head>"
								+ "<body bgcolor=\"white\">"
								+ "<center><h1>500 Internal Server Error</h1></center>"
								+ "<hr><center>JerryMouse/1.0</center>"
								+ "</body>" + "</html>");
				doWrite(responseMessage);
			}
		}
	}

	// 写数据
	private void doWrite(HttpResponseMessage response) {
		HttpResponseEncoder encoder = new HttpResponseEncoder();
		String responseHeader = encoder.encodeHeader(response);

		byte[] headers = (responseHeader).getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(headers.length
				+ response.getBodyLength());
		writeBuffer.put(headers);
		writeBuffer.put(response.getBody());
		writeBuffer.flip();
		channel.write(writeBuffer, writeBuffer,
				new CompletionHandler<Integer, ByteBuffer>() {
					@Override
					public void completed(Integer result, ByteBuffer buffer) {
						// 如果没有发送完成，继续发送
						if (buffer.hasRemaining()) {
							channel.write(buffer, buffer, this);
						} else {
							try {
								channel.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					@Override
					public void failed(Throwable exc, ByteBuffer attachment) {
						try {
							channel.close();
						} catch (IOException e) {
							// ingnore on close
						}
					}
				});
	}

	public void onResponse(HttpResponseMessage response) {
		// TODO Auto-generated method stub
		doWrite(response);
	}
}
