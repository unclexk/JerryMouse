package xiaokang.jerrymouse.container;

import java.util.Enumeration;
import java.util.Iterator;

public class JerryEnumeration<E> implements Enumeration<E> {

	private Iterator<E> iterator;

	public JerryEnumeration(Iterator<E> iterator) {
		this.iterator = iterator;
	}

	@Override
	public boolean hasMoreElements() {
		// TODO Auto-generated method stub
		return iterator.hasNext();
	}

	@Override
	public E nextElement() {
		// TODO Auto-generated method stub
		return iterator.next();
	}

}
