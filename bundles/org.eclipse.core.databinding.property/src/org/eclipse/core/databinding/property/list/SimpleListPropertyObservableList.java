/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 ******************************************************************************/

package org.eclipse.core.databinding.property.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.ObservableTracker;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.AbstractObservableList;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.databinding.property.IProperty;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.IPropertyObservable;

/**
 * @since 1.2
 * 
 */
class SimpleListPropertyObservableList extends AbstractObservableList implements
		IPropertyObservable {
	private Object source;
	private SimpleListProperty property;

	private volatile boolean updating = false;

	private volatile int modCount = 0;

	private INativePropertyListener listener;

	private List cachedList;

	/**
	 * @param realm
	 * @param source
	 * @param property
	 */
	public SimpleListPropertyObservableList(Realm realm, Object source,
			SimpleListProperty property) {
		super(realm);
		this.source = source;
		this.property = property;
	}

	protected void firstListenerAdded() {
		if (!isDisposed()) {
			cachedList = property.getList(source);

			if (listener == null) {
				listener = property
						.adaptListener(new IListPropertyChangeListener() {
							public void handleListPropertyChange(
									final ListPropertyChangeEvent event) {
								modCount++;
								if (!isDisposed() && !updating) {
									getRealm().exec(new Runnable() {
										public void run() {
											List oldList = cachedList;
											List newList = cachedList = property
													.getList(source);
											ListDiff diff = event.diff;
											if (diff == null) {
												diff = Diffs.computeListDiff(
														oldList, newList);
											}
											fireListChange(diff);
										}
									});
								}
							}
						});
			}
			property.addListener(source, listener);
		}
	}

	protected void lastListenerRemoved() {
		if (listener != null) {
			property.removeListener(source, listener);
		}

		cachedList = null;
	}

	private void getterCalled() {
		ObservableTracker.getterCalled(this);
	}

	public Object getElementType() {
		return property.getElementType();
	}

	// Queries

	protected int doGetSize() {
		return property.size(source);
	}

	public boolean contains(Object o) {
		getterCalled();
		return property.contains(source, o);
	}

	public boolean containsAll(Collection c) {
		getterCalled();
		return property.containsAll(source, c);
	}

	public Object get(int index) {
		getterCalled();
		return property.get(source, index);
	}

	public int indexOf(Object o) {
		getterCalled();
		return property.indexOf(source, o);
	}

	public boolean isEmpty() {
		getterCalled();
		return property.isEmpty(source);
	}

	public int lastIndexOf(Object o) {
		getterCalled();
		return property.lastIndexOf(source, o);
	}

	public Object[] toArray() {
		getterCalled();
		return property.toArray(source);
	}

	public Object[] toArray(Object[] a) {
		getterCalled();
		return property.toArray(source, a);
	}

	// Single change operations

	public boolean add(Object o) {
		checkRealm();
		add(property.size(source), o);
		return true;
	}

	public void add(int index, Object o) {
		checkRealm();
		boolean wasUpdating = updating;
		updating = true;
		try {
			property.add(source, index, o);
			modCount++;
		} finally {
			updating = wasUpdating;
		}

		cachedList = property.getList(source);
		fireListChange(Diffs.createListDiff(Diffs.createListDiffEntry(index,
				true, o)));
	}

	public Iterator iterator() {
		getterCalled();
		return new Iterator() {
			int expectedModCount = modCount;
			ListIterator delegate = new ArrayList(property.getList(source))
					.listIterator();

			Object lastElement = null;
			int lastIndex = -1;

			public boolean hasNext() {
				getterCalled();
				checkForComodification();
				return delegate.hasNext();
			}

			public Object next() {
				getterCalled();
				checkForComodification();
				Object next = lastElement = delegate.next();
				lastIndex = delegate.previousIndex();
				return next;
			}

			public void remove() {
				checkRealm();
				checkForComodification();
				if (lastIndex == -1)
					throw new IllegalStateException();

				delegate.remove(); // stay in sync

				boolean wasUpdating = updating;
				updating = true;
				try {
					property.remove(source, lastIndex);
					modCount++;
				} finally {
					updating = wasUpdating;
				}

				cachedList = property.getList(source);
				fireListChange(Diffs.createListDiff(Diffs.createListDiffEntry(
						lastIndex, false, lastElement)));

				lastElement = null;
				lastIndex = -1;

				expectedModCount = modCount;
			}

			private void checkForComodification() {
				if (expectedModCount != modCount)
					throw new ConcurrentModificationException();
			}
		};
	}

	public Object move(int oldIndex, int newIndex) {
		checkRealm();

		int size = property.size(source);
		if (oldIndex < 0 || oldIndex >= size || newIndex < 0
				|| newIndex >= size)
			throw new IndexOutOfBoundsException();
		if (oldIndex == newIndex)
			return property.get(source, oldIndex);

		Object element;

		boolean wasUpdating = updating;
		updating = true;
		try {
			element = property.move(source, oldIndex, newIndex);
			modCount++;
		} finally {
			updating = wasUpdating;
		}

		cachedList = property.getList(source);
		fireListChange(Diffs.createListDiff(Diffs.createListDiffEntry(oldIndex,
				false, element), Diffs.createListDiffEntry(newIndex, true,
				element)));

		return element;
	}

	public boolean remove(Object o) {
		checkRealm();

		int index = property.indexOf(source, o);
		if (index == -1)
			return false;

		remove(index);

		return true;
	}

	public ListIterator listIterator() {
		return listIterator(0);
	}

	public ListIterator listIterator(final int index) {
		getterCalled();
		return new ListIterator() {
			int expectedModCount = modCount;
			ListIterator delegate = new ArrayList(property.getList(source))
					.listIterator(index);

			Object lastElement = null;
			int lastIndex = -1;

			public boolean hasNext() {
				getterCalled();
				checkForComodification();
				return delegate.hasNext();
			}

			public int nextIndex() {
				getterCalled();
				checkForComodification();
				return delegate.nextIndex();
			}

			public Object next() {
				getterCalled();
				checkForComodification();
				lastElement = delegate.next();
				lastIndex = delegate.previousIndex();
				return lastElement;
			}

			public boolean hasPrevious() {
				getterCalled();
				checkForComodification();
				return delegate.hasPrevious();
			}

			public int previousIndex() {
				getterCalled();
				checkForComodification();
				return delegate.previousIndex();
			}

			public Object previous() {
				getterCalled();
				checkForComodification();
				lastElement = delegate.previous();
				lastIndex = delegate.nextIndex();
				return lastElement;
			}

			public void add(Object o) {
				checkRealm();
				checkForComodification();
				int index = delegate.nextIndex();

				delegate.add(o); // keep in sync

				boolean wasUpdating = updating;
				updating = true;
				try {
					property.add(source, index, o);
					modCount++;
				} finally {
					updating = wasUpdating;
				}

				cachedList = property.getList(source);
				fireListChange(Diffs.createListDiff(Diffs.createListDiffEntry(
						index, true, o)));

				lastElement = null;
				lastIndex = -1;
				expectedModCount = modCount;
			}

			public void set(Object o) {
				checkRealm();
				checkForComodification();

				delegate.set(o);

				boolean wasUpdating = updating;
				updating = true;
				try {
					property.set(source, lastIndex, o);
					modCount++;
				} finally {
					updating = wasUpdating;
				}

				cachedList = property.getList(source);
				fireListChange(Diffs.createListDiff(Diffs.createListDiffEntry(
						lastIndex, false, lastElement), Diffs
						.createListDiffEntry(lastIndex, true, o)));

				lastElement = o;

				expectedModCount = modCount;
			}

			public void remove() {
				checkRealm();
				checkForComodification();
				if (lastIndex == -1)
					throw new IllegalStateException();

				delegate.remove(); // keep in sync

				boolean wasUpdating = updating;
				updating = true;
				try {
					property.remove(source, lastIndex);
					modCount++;
				} finally {
					updating = wasUpdating;
				}

				cachedList = property.getList(source);
				fireListChange(Diffs.createListDiff(Diffs.createListDiffEntry(
						lastIndex, false, lastElement)));

				lastElement = null;
				lastIndex = -1;
				expectedModCount = modCount;
			}

			private void checkForComodification() {
				if (expectedModCount != modCount)
					throw new ConcurrentModificationException();
			}
		};
	}

	public Object remove(int index) {
		checkRealm();

		Object element;

		boolean wasUpdating = updating;
		updating = true;
		try {
			element = property.remove(source, index);
			modCount++;
		} finally {
			updating = wasUpdating;
		}

		cachedList = property.getList(source);
		fireListChange(Diffs.createListDiff(Diffs.createListDiffEntry(index,
				false, element)));

		return element;
	}

	public Object set(int index, Object o) {
		checkRealm();

		Object oldElement;

		boolean wasUpdating = updating;
		updating = true;
		try {
			oldElement = property.set(source, index, o);
			modCount++;
		} finally {
			updating = wasUpdating;
		}

		cachedList = property.getList(source);
		fireListChange(Diffs.createListDiff(Diffs.createListDiffEntry(index,
				false, oldElement), Diffs.createListDiffEntry(index, true, o)));

		return oldElement;
	}

	public List subList(int fromIndex, int toIndex) {
		getterCalled();
		return Collections.unmodifiableList(property.getList(source).subList(
				fromIndex, toIndex));
	}

	// Bulk change operations

	public boolean addAll(Collection c) {
		checkRealm();

		return addAll(property.size(source), c);
	}

	public boolean addAll(int index, Collection c) {
		checkRealm();

		if (c.isEmpty())
			return false;

		ListDiffEntry[] entries = new ListDiffEntry[c.size()];
		int offsetIndex = 0;
		for (Iterator it = c.iterator(); it.hasNext();) {
			Object element = it.next();
			entries[offsetIndex] = Diffs.createListDiffEntry(index
					+ offsetIndex, true, element);
			offsetIndex++;
		}

		boolean changed;

		boolean wasUpdating = updating;
		updating = true;
		try {
			changed = property.addAll(source, index, c);
			modCount++;
		} finally {
			updating = wasUpdating;
		}

		cachedList = property.getList(source);
		if (changed)
			fireListChange(Diffs.createListDiff(entries));

		return changed;
	}

	public boolean removeAll(Collection c) {
		checkRealm();

		if (property.isEmpty(source) || c.isEmpty())
			return false;

		boolean changed;

		List entries = new ArrayList();

		boolean wasUpdating = updating;
		updating = true;
		try {
			List list = new ArrayList(property.getList(source));
			for (ListIterator it = list.listIterator(); it.hasNext();) {
				Object element = it.next();
				int index = it.previousIndex();
				if (c.contains(element)) {
					it.remove();
					entries.add(Diffs
							.createListDiffEntry(index, false, element));
				}
			}
			changed = property.removeAll(source, c);
			modCount++;
		} finally {
			updating = wasUpdating;
		}

		cachedList = property.getList(source);

		if (changed)
			fireListChange(Diffs.createListDiff((ListDiffEntry[]) entries
					.toArray(new ListDiffEntry[entries.size()])));

		return changed;
	}

	public boolean retainAll(Collection c) {
		checkRealm();

		if (property.isEmpty(source))
			return false;

		if (c.isEmpty()) {
			clear();
			return true;
		}

		boolean changed;

		List entries = new ArrayList();

		boolean wasUpdating = updating;
		updating = true;
		try {
			List list = new ArrayList(property.getList(source));
			for (ListIterator it = list.listIterator(); it.hasNext();) {
				Object element = it.next();
				int index = it.previousIndex();
				if (!c.contains(element)) {
					it.remove();
					entries.add(Diffs
							.createListDiffEntry(index, false, element));
				}
			}
			changed = property.retainAll(source, c);
			modCount++;
		} finally {
			updating = wasUpdating;
		}

		cachedList = property.getList(source);

		if (changed)
			fireListChange(Diffs.createListDiff((ListDiffEntry[]) entries
					.toArray(new ListDiffEntry[entries.size()])));

		return changed;
	}

	public void clear() {
		checkRealm();

		if (property.isEmpty(source))
			return;

		List entries = new ArrayList();
		for (Iterator it = property.getList(source).iterator(); it.hasNext();) {
			// always report 0 as the remove index
			entries.add(Diffs.createListDiffEntry(0, false, it.next()));
		}

		boolean wasUpdating = updating;
		updating = true;
		try {
			property.clear(source);
			modCount++;
		} finally {
			updating = wasUpdating;
		}

		cachedList = property.getList(source);
		fireListChange(Diffs.createListDiff((ListDiffEntry[]) entries
				.toArray(new ListDiffEntry[entries.size()])));
	}

	public boolean equals(Object o) {
		getterCalled();
		return property.equals(source, o);
	}

	public int hashCode() {
		getterCalled();
		return property.hashCode(source);
	}

	public Object getObserved() {
		return source;
	}

	public IProperty getProperty() {
		return property;
	}

	public synchronized void dispose() {
		if (!isDisposed()) {
			if (listener != null)
				property.removeListener(source, listener);
			property = null;
			source = null;
			listener = null;
		}
		super.dispose();
	}
}
