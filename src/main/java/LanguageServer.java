import jledger.core.Content;

public interface LanguageServer {
	/**
	 * Open a given workspace using this language server. This essentially ensures
	 * the workspace is available for use (e.g. is read into memory, etc).
	 *
	 * @param name
	 * @return
	 */
	public Workspace open(String name);

	/**
	 * Represents a given workspace within the language server.
	 *
	 * @author David J. Pearce
	 *
	 */
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
		 * Close the workspace, whilst ensuring all changes are persisted.
		 */
		public void close();
		/**
		 * Ensure any changes are persisted to non-volatile storage.
		 */
		public void flush();
	}

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
		public File[] list();
		/**
		 * Create a new (empty) file within this project.
		 *
		 * @param name
		 */
		public File create(String name);
	}

	public interface File {
		/**
		 * Delete this file.
		 */
		public void delete();
		/**
		 * Overwrite the entire file with the contents of a given byte array.
		 *
		 * @param contents
		 */
		public void write(byte[] contents);
		/**
		 * Apply a partial write to the file.
		 *
		 * @param diff
		 */
		public void write(Content.Diff diff);
		/**
		 * Read the entire file into a byte array.
		 *
		 * @return
		 */
		public byte[] read();
		/**
		 * Read a chunk from the file into a byte array.
		 *
		 * @param offset
		 * @param length
		 * @return
		 */
		public byte[] read(int offset, int length);
		/**
		 * Read a chunk from the file into a given byte array.
		 *
		 * @param srcOffset
		 * @param dest
		 * @param dstOffset
		 * @param length
		 */
		public void read(int srcOffset, byte[] dest, int dstOffset, int length);
	}
}
