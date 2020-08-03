import jledger.util.ByteArrayDiff;

public interface Workspace {
	/**
	 * List all available projects within the workspace.
	 *
	 * @return
	 */
	public Project[] list();

	/**
	 * Create a new project within the workspace.
	 * @param name
	 * @return
	 */
	public Project create(String name);

	/**
	 * Delete a project within the workspace.
	 *
	 * @param name
	 */
	public void delete(String name);

	/**
	 * Represents a given project within the workspace.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Project {
		/**
		 * List all files contained within the project.
		 *
		 * @return
		 */
		public String[] list();
		/**
		 * Create a new (empty) file within this project.
		 *
		 * @param name
		 */
		public void create(String name);
		/**
		 * Write a new diff to the file.
		 *
		 * @param name
		 */
		public void write(String name, String contents);
		/**
		 * Read contents of the file from the project.
		 *
		 * @param name
		 * @return
		 */
		public String read(String name);
	}
}
