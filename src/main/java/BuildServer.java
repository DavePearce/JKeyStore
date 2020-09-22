import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import jledger.core.Content;
import jledger.core.Content.Layout;
import jledger.util.AbstractLayouts;
import jledger.util.ArrayLayouts;
import static jledger.util.AbstractLayouts.*;
import jledger.util.NonSequentialLedger;
import jledger.util.PrimitiveLayouts;
import jledger.util.RecordLayouts;

import static java.nio.file.StandardWatchEventKinds.*;

public class BuildServer {
	private final NonSequentialLedger<Directory> ledger = new NonSequentialLedger<>(Directory.LAYOUT, 10);

	public BuildServer() {
		// Initialiser with an empty directory
		ledger.put(new Directory());
	}

	public void create(String name, byte[] contents) {
		Directory d = ledger.get(ledger.versions() - 1);
		// Write another one
		ledger.put(d.add(name.hashCode()));
	}

	public void update(String name, byte[] contents) {

	}

	public void remove(String name) {

	}

	@Override
	public String toString() {
		String r = "";
		for (int i = 0; i != ledger.versions(); ++i) {
			Directory d = ledger.get(i);
			r += d.toString();
		}
		return r;
	}

	private static final class Directory extends AbstractLayouts.Proxy<Directory> {
		public static final Content.Layout<Directory> LAYOUT = RecordLayouts.RECORD(Directory::new,
				ArrayLayouts.DYNAMIC_ARRAY(Entry.LAYOUT));

		public Directory() {
			super(LAYOUT);
		}

		public Directory(Content.Blob blob, int offset) {
			super(LAYOUT, blob, offset);
		}

		public int size() {
			return LAYOUT.read_i32(POSITION(0, 0), blob, offset);
		}

		public Directory add(int value) {
			Entry e = new Entry().set(value);
			Content.Blob nblob = LAYOUT.append(e, POSITION(0), blob, offset);
			return new Directory(nblob, offset);
		}

		public Entry get(int i) {
			return LAYOUT.readBytes(Entry.class, POSITION(0, i + 1), blob, offset);
		}

		@Override
		public String toString() {
			String r = "{";
			for (int i = 0; i != size(); ++i) {
				if (i != 0) {
					r += ",";
				}
				r += get(i);
			}
			return r + "}";
		}
	}

	private static final class Entry extends AbstractLayouts.Proxy<Entry> {
		public static final Content.Layout<Entry> LAYOUT = RecordLayouts.RECORD(Entry::new, PrimitiveLayouts.INT32);

		public Entry() {
			super(LAYOUT);
		}

		public Entry(Content.Blob blob, int offset) {
			super(LAYOUT, blob, offset);
		}

		public Entry set(int value) {
			return new Entry(LAYOUT.write_i32(value, POSITION(0), blob, value), offset);
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		FileSystem fs = FileSystems.getDefault();
		WatchService watcher = fs.newWatchService();
		Path p = fs.getPath(".");
		WatchKey key = p.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		BuildServer server = new BuildServer();
		while (key.isValid()) {
			System.out.println("STATE: " + server);
			// retrieve key
			key = watcher.take();
			// process events
			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();
				// Handle overflows
				if (kind == OVERFLOW) {
					continue;
				} else {
					Path path = (Path) event.context();
					if (path.toString().matches("[a-zA-Z0-9]+.txt")) {
						if (kind == ENTRY_CREATE) {
							System.out.println("CREATE: " + path);
							server.create(path.toString(), null);
						} else if (kind == ENTRY_DELETE) {
							System.out.println("DELETE: " + path);
						} else {
							System.out.println("UPDATE: " + path);
						}
					}
				}
			}
			key.reset();
		}
	}
}
