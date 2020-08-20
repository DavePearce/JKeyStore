import java.util.HashMap;

import jledger.core.Ledger;
import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.core.Content.Layout;
import jledger.util.ByteBlob;
import static jledger.util.Layouts.*;
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
			this.ledger = new NonSequentialLedger<>(blob -> this.construct(blob),10);
			this.ledger.put(EMPTY_WORKSPACE);
		}

		public Workspace get() {
			return ledger.get(ledger.versions() - 1);
		}

		private Workspace construct(Content.Blob blob) {
			return new Workspace(blob);
		}
	}

	private static final Workspace EMPTY_WORKSPACE = new Workspace();

	private static class Workspace implements LanguageServer.Workspace, Content.Proxy {
		private static final Content.Layout LAYOUT = STATIC(INT32);

		private final Content.Blob blob;

		public Workspace() {
			// Initialise me!
			this.blob = LAYOUT.write_i32(0, POSITION(0), ByteBlob.EMPTY, 0);
		}

		private Workspace(Content.Blob blob) {
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
			throw new IllegalArgumentException("implement me!");
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
}
