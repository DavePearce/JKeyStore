import java.util.HashMap;

import jledger.util.ByteArrayLedger;
import jledger.util.Pair;

public class SimpleLanguageServer implements LanguageServer {
	private HashMap<String,Workspace> workspaces = new HashMap<>();

	@Override
	public Workspace open(String name) {
		if(workspaces.containsKey(name)) {
			return workspaces.get(name);
		} else {
			Workspace workspace = new Workspace();
			workspaces.put(name, workspace);
			return workspace;
		}
	}

	private class Workspace implements LanguageServer.Workspace {
		private final ByteArrayLedger ledger;
		private final ByteArrayLedger.Key root;

		public Workspace() {
			this.ledger = new ByteArrayLedger(10);
			// Construct root key
			root = ledger.add("/");
			// Write initial set of meta-data
		}

		@Override
		public Project[] list() {
			// Extract meta-data
			ByteArrayLedger.Data data = ledger.get(root);
			// Somehow turn this into a list of projects?

		}

		@Override
		public Project create(String name) {
			ByteArrayLedger.Key key = ledger.lookup(name);
			// Check whether project of same name already exists
			if(key != null) {
				// Yes, it already exists!
				throw new IllegalArgumentException("Project " + name + " already exists!");
			} else {
				// Create key for project
				key = ledger.add(name);
				// Write initial set of meta-deta
				ledger.add(new Pair<>(key,value));
				//
				return new Project(this,key);
			}
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub

		}

		@Override
		public void flush() {
			// TODO Auto-generated method stub

		}

	}

	private class Project implements LanguageServer.Project {
		private final Workspace workspace;
		private final ByteArrayLedger.Key key;

		public Project(Workspace workspace, ByteArrayLedger.Key key) {
			this.workspace = workspace;
			this.key = key;
		}

		@Override
		public File[] list() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public File create(String name) {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
