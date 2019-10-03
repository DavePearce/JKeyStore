package jkeystore.core;

/**
 * A generic mechanism for identifying one or more keys.
 *
 * @author David J. Pearce
 *
 */
public interface Query {
	/**
	 * Check whether a given entry is matched by this filter.
	 *
	 * @param id --- id to test.
	 * @return --- true if it matches, otherwise false.
	 */
	public boolean matches(Key id);

	/**
	 * Check whether a given subkey is matched by this filter. A matching subpath
	 * does not necessarily identify an exact match; rather, it may be an enclosing
	 * folder.
	 *
	 * @param id
	 * @return
	 */
	public boolean matchesSubpath(Key id);
}
