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

package org.eclipse.core.databinding.property.set;

import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.AbstractObservableSet;
import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.core.databinding.property.IProperty;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.IPropertyObservable;

/**
 * @since 1.2
 * 
 */
class SimpleSetPropertyObservableSet extends AbstractObservableSet implements
		IPropertyObservable {
	private Object source;
	private SimpleSetProperty property;

	private volatile boolean updating = false;

	private volatile int modCount = 0;

	private INativePropertyListener listener;

	private Set cachedSet;

	/**
	 * @param realm
	 * @param source
	 * @param property
	 */
	public SimpleSetPropertyObservableSet(Realm realm, Object source,
			SimpleSetProperty property) {
		super(realm);
		this.source = source;
		this.property = property;
	}

	protected void firstListenerAdded() {
		if (!isDisposed()) {
			cachedSet = property.getSet(source);

			if (listener == null) {
				listener = property
						.adaptListener(new ISetPropertyChangeListener() {
							public void handleSetPropertyChange(
									final SetPropertyChangeEvent event) {
								modCount++;
								if (!isDisposed() && !updating) {
									getRealm().exec(new Runnable() {
										public void run() {
											Set oldSet = cachedSet;
											Set newSet = cachedSet = property
													.getSet(source);
											SetDiff diff = event.diff;
											if (diff == null) {
												diff = Diffs.computeSetDiff(
														oldSet, newSet);
											}
											fireSetChange(diff);
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

		cachedSet = null;
	}

	protected Set getWrappedSet() {
		return property.getSet(source);
	}

	public Object getElementType() {
		return property.getElementType();
	}

	// Queries

	protected int doGetSize() {
		return property.size(source);
	}

	// Single change operations

	public boolean add(Object o) {
		checkRealm();

		boolean changed;

		boolean wasUpdating = updating;
		updating = true;
		try {
			changed = property.add(source, o);
			modCount++;
		} finally {
			updating = wasUpdating;
		}

		cachedSet = property.getSet(source);

		if (changed)
			fireSetChange(Diffs.createSetDiff(Collections.singleton(o),
					Collections.EMPTY_SET));

		return changed;
	}

	public Iterator iterator() {
		getterCalled();
		return new Iterator() {
			int expectedModCount = modCount;
			Iterator delegate = new HashSet(property.getSet(source)).iterator();
			Object last = null;

			public boolean hasNext() {
				getterCalled();
				checkForComodification();
				return delegate.hasNext();
			}

			public Object next() {
				getterCalled();
				checkForComodification();
				last = delegate.next();
				return last;
			}

			public void remove() {
				checkRealm();
				checkForComodification();

				delegate.remove(); // stay in sync

				boolean wasUpdating = updating;
				updating = true;
				try {
					property.remove(source, last);
					modCount++;
				} finally {
					updating = wasUpdating;
				}

				cachedSet = property.getSet(source);

				fireSetChange(Diffs.createSetDiff(Collections.EMPTY_SET,
						Collections.singleton(last)));

				last = null;
				expectedModCount = modCount;
			}

			private void checkForComodification() {
				if (expectedModCount != modCount)
					throw new ConcurrentModificationException();
			}
		};
	}

	public boolean remove(Object o) {
		getterCalled();

		boolean changed;

		boolean wasUpdating = updating;
		updating = true;
		try {
			changed = property.remove(source, o);
			modCount++;
		} finally {
			updating = wasUpdating;
		}

		cachedSet = property.getSet(source);
		fireSetChange(Diffs.createSetDiff(Collections.EMPTY_SET, Collections
				.singleton(o)));

		return changed;
	}

	// Bulk change operations

	public boolean addAll(Collection c) {
		getterCalled();

		if (c.isEmpty())
			return false;

		Set additions = new HashSet(c);
		additions.removeAll(property.getSet(source));
		if (additions.isEmpty())
			return false;

		boolean wasUpdating = updating;
		updating = true;
		try {
			property.addAll(source, c);
			modCount++;
		} finally {
			updating = wasUpdating;
		}

		cachedSet = property.getSet(source);
		fireSetChange(Diffs.createSetDiff(additions, Collections.EMPTY_SET));

		return true;
	}

	public boolean removeAll(Collection c) {
		getterCalled();

		if (property.isEmpty(source) || c.isEmpty())
			return false;

		Set removals = new HashSet(c);
		removals.retainAll(property.getSet(source));
		if (removals.isEmpty())
			return false;

		boolean wasUpdating = updating;
		updating = true;
		try {
			property.removeAll(source, c);
			modCount++;
		} finally {
			updating = wasUpdating;
		}

		cachedSet = property.getSet(source);
		fireSetChange(Diffs.createSetDiff(Collections.EMPTY_SET, removals));

		return true;
	}

	public boolean retainAll(Collection c) {
		getterCalled();

		if (property.isEmpty(source))
			return false;

		if (c.isEmpty()) {
			clear();
			return true;
		}

		Set removals = new HashSet(property.getSet(source));
		removals.removeAll(c);
		if (removals.isEmpty())
			return false;

		boolean wasUpdating = updating;
		updating = true;
		try {
			property.retainAll(source, c);
			modCount++;
		} finally {
			updating = wasUpdating;
		}

		cachedSet = property.getSet(source);
		fireSetChange(Diffs.createSetDiff(Collections.EMPTY_SET, removals));

		return true;
	}

	public void clear() {
		getterCalled();

		if (property.isEmpty(source))
			return;

		Set removals = new HashSet(property.getSet(source));

		boolean wasUpdating = updating;
		updating = true;
		try {
			property.clear(source);
			modCount++;
		} finally {
			updating = wasUpdating;
		}

		cachedSet = property.getSet(source);
		fireSetChange(Diffs.createSetDiff(Collections.EMPTY_SET, removals));
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
