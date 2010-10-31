package com.idega.slide.jcr;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.EventListenerIterator;
import javax.jcr.version.Version;
import javax.jcr.version.VersionIterator;

public class IteratorHelper<T> implements NodeIterator, EventListenerIterator, PropertyIterator, VersionIterator {

	private int position;
	private List<T> items;

	public IteratorHelper() {
		super();
	}

	public IteratorHelper(List<T> items) {
		this();

		this.items = items;
	}

	@Override
	public boolean hasNext() {
		return position < items.size();
	}

	@Override
	public Object next() {
		try {
			return items.get(position);
		} finally {
			position++;
		}
	}

	@Override
	public void remove() {
		if (position < getSize()) {
			items.remove(position);
		}
	}

	@Override
	public void skip(long skipNum) {
		// TODO Auto-generated method stub
	}

	@Override
	public long getSize() {
		return items.size();
	}

	@Override
	public long getPosition() {
		return position;
	}

	@Override
	public EventListener nextEventListener() {
		return (EventListener) next();
	}

	@Override
	public Node nextNode() {
		return (Node) next();
	}

	@Override
	public Property nextProperty() {
		return (Property) next();
	}

	@Override
	public Version nextVersion() {
		return (Version) next();
	}

}