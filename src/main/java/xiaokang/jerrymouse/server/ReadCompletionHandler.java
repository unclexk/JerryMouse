package xiaokang.jerrymouse.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import xiaokang.jerrymouse.http.HttpRequestDecoder;
import xiaokang.jerrymouse.http.HttpRequestMessage;

public class ReadCompletionHandler implements
		CompletionHandler<Integer, ByteBuffer> {

	private AsynchronousSocketChannel channel;

	public ReadCompletionHandler(AsynchronousSocketChannel channel) {
		if (this.channel == null) {
			this.channel = channel;
		}
	}

	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		attachment.flip();
		byte[] data = new byte[attachment.remaining()];
		attachment.get(data);
		try {
			// System.out.println(new String(data, "UTF-8"));
			HttpRequestDecoder decoder = new HttpRequestDecoder();
			HttpRequestMessage request = decoder.decodeRequest(data);
			Connector connector = new Connector(channel, request);
			connector.handleRequest();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				this.channel.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		try {
			this.channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
