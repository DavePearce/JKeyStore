package jledger.core;

public interface Key extends Iterable<String>, Comparable<Key> {

	/**
	 * Get the number of components that make up this ID.
	 * @return
	 */
	public int size();

	/**
	 * Return the component at a given index.
	 * @param index
	 * @return
	 */
	public String get(int index);

	/**
	 * A convenience function that gets the last component of this path.
	 *
	 * @return
	 */
	public String last();

	/**
	 * Get the parent of this path.
	 *
	 * @return
	 */
	public Key parent();

	/**
	 * Get a sub Key from this Key, which consists of those components between
	 * start and end (exclusive).
	 *
	 * @param start
	 *            --- starting component index
	 * @param start
	 *            --- one past last component index
	 * @return
	 */
	public Key subpath(int start, int end);

	/**
	 * Append a component onto the end of this Key.
	 *
	 * @param component
	 *            --- to be appended
	 * @return
	 */
	public Key append(String component);

	/**
	 * Append all components from an Key onto the end of this Key.
	 *
	 * @param Key
	 * @return
	 */
	public Key append(Key id);
}
