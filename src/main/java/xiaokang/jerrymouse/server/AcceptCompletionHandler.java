package xiaokang.jerrymouse.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements
		CompletionHandler<AsynchronousSocketChannel, Server> {

	@Override
	public void completed(AsynchronousSocketChannel result,
			Server attachment) {
		attachment.asynchronousServerSocketChannel.accept(attachment, this);
		// 足够大的缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		result.read(buffer, buffer, new ReadCompletionHandler(result));
	}

	@Override
	public void failed(Throwable exc, Server attachment) {
		exc.printStackTrace();
		attachment.latch.countDown();
	}
}
