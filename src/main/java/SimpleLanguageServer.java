import java.util.HashMap;

import jledger.core.Ledger;
import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.core.Content.Layout;
import jledger.util.ByteBlob;
import jledger.util.ContentLayouts;

import static jledger.util.ContentLayouts.*;
import jledger.util.NonSequentialLedger;
import jledger.util.Pair;

public class SimpleLanguageServer implements LanguageServer {
	private HashMap<String,Environment> environments = new HashMap<>();

	@Override
	public Workspace open(String name) {
		if(environments.containsKey(name)) {
			return environments.get(name).get();
		} else {
			Environment workspace = new Environment();
			environments.put(name, workspace);
			return workspace.get();
		}
	}

	private class Environment {
		private final Ledger<Workspace> ledger;

		public Environment() {
			this.ledger = new NonSequentialLedger<>(Workspace.LAYOUT,10);
			this.ledger.put(EMPTY_WORKSPACE);
		}

		public Workspace get() {
			return ledger.get(ledger.versions() - 1);
		}
	}

	private static final Workspace EMPTY_WORKSPACE = new Workspace();

	private static class Workspace implements LanguageServer.Workspace, Content.Proxy {
		private static final Content.ConstructorLayout<Workspace> LAYOUT = ContentLayouts.CONSTRUCTOR(Workspace::new,
				STATIC_ARRAY(2, Project.LAYOUT));

		private final Content.Blob blob;

		public Workspace() {
			// Initialise me!
			this.blob = LAYOUT.initialise(ByteBlob.EMPTY, 0);
		}

		private Workspace(Content.Blob blob, int offset) {
			if (offset != 0) {
				throw new IllegalArgumentException();
			}
			this.blob = blob;
		}

		@Override
		public Blob getBlob() {
			return blob;
		}

		@Override
		public Layout getLayout() {
			return LAYOUT;
		}

		@Override
		public Project[] list() {
			Project[] items = new Project[2];
			//items[0] = ??;
			return items;
		}

		@Override
		public Project create(String name) {
			throw new IllegalArgumentException("implement me!");
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

	private static class Project implements LanguageServer.Project, Content.Proxy {
		private static final Content.ConstructorLayout<Project> LAYOUT = ContentLayouts.CONSTRUCTOR(Project::new,
				STATIC(INT32));

		public Project(Content.Blob blob, int offset) {

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

		@Override
		public Blob getBlob() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Layout getLayout() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
