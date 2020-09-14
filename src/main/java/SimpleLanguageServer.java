import java.util.Arrays;
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
import static jledger.util.RecordLayouts.*;
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
		private static final Content.ConstructorLayout<Workspace> LAYOUT = RECORD(Workspace::new,
				PROJECTS);
		private static final Content.Position PROJECTS_FIELD = POSITION(0);
		//
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
		public int getOffset() {
			return 0;
		}

		@Override
		public Blob getBlob() {
			return blob;
		}

		@Override
		public Layout getLayout() {
			return LAYOUT;
		}

		public Workspace add(Project project) {
			throw new IllegalArgumentException("implement me!");
		}

		@Override
		public Project[] list() {
			// FIXME: this is broken
			return PROJECTS.read(blob, offset);
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
		private static final Content.ConstructorLayout<Project> LAYOUT = RECORD(Project::new,
				RecordLayouts.RECORD(INT32));

		private final int offset;
		private final Content.Blob blob;

		public Project(Content.Blob blob, int offset) {
			this.offset = offset;
			this.blob = blob;
		}

		@Override
		public int getOffset() {
			return offset;
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
		public File[] list() {
			return new File[0];
		}

		@Override
		public File create(String name) {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
