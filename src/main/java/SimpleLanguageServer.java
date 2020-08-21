import java.util.HashMap;

import jledger.core.Ledger;
import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.core.Content.Layout;
import jledger.util.ByteBlob;
import jledger.util.AbstractLayouts;

import static jledger.util.AbstractLayouts.*;
import static jledger.util.PrimitiveLayouts.*;
import static jledger.util.ArrayLayouts.*;
import jledger.util.NonSequentialLedger;
import jledger.util.Pair;
import jledger.util.RecordLayouts;

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
		private static final Content.ConstructorLayout<Project[]> PROJECTS = DYNAMIC_ARRAY(Project.LAYOUT,
				new Project[0]);
		private static final Content.ConstructorLayout<Workspace> LAYOUT = AbstractLayouts.CONSTRUCTOR(Workspace::new,
				PROJECTS);

		private final Content.Blob blob;
		private final int offset;

		public Workspace() {
			// Initialise me!
			this.blob = LAYOUT.initialise(ByteBlob.EMPTY, 0);
			this.offset = 0;
		}

		private Workspace(Content.Blob blob, int offset) {
			if (offset != 0) {
				throw new IllegalArgumentException();
			}
			this.blob = blob;
			this.offset = offset;
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
			return PROJECTS.construct(blob, offset);
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
		private static final Content.ConstructorLayout<Project> LAYOUT = AbstractLayouts.CONSTRUCTOR(Project::new,
				RecordLayouts.RECORD(INT32));

		public Project(Content.Blob blob, int offset) {

		}

		@Override
		public File[] list() {
			return new File[0];
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
