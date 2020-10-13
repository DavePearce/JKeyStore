import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.layouts.Array;
import jledger.layouts.Pair;

import static jledger.layouts.Primitive.BYTE_ARRAY;
import jledger.util.ByteBlob;
import jledger.util.SequentialLedger;

import static java.nio.file.StandardWatchEventKinds.*;

public class BuildServer {
	private final SequentialLedger<Directory> ledger = new SequentialLedger<>(new Directory(), 10);

	public void create(String name, byte[] contents) {
		Directory d = ledger.get(ledger.versions() - 1);
		// Write another one
		ledger.put(d.add(new Entry(name.getBytes(), contents)));
	}

	public void addAll(Entry[] entries) {
		Directory d = ledger.last();
		// Write another one
		ledger.put(d.addAll(entries));
	}

	public void update(String name, byte[] contents) {
		Directory d = ledger.get(ledger.versions() - 1);
		ledger.put(d.replace(name, contents));
		System.out.println("VERSIONS: " + ledger.versions() + ", SIZE: " + ledger.last().getBlob().size() + " bytes");
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

	private static final class Directory extends Array.Proxy<Entry, Directory> {
		public static final Array.Layout<Entry, Directory> LAYOUT = Array.LAYOUT(Entry.LAYOUT, Directory::new);

		public Directory() {
			super(LAYOUT, LAYOUT.initialise(ByteBlob.EMPTY, 0), 0);
		}

		public Directory(Content.Blob blob, int offset) {
			super(LAYOUT, blob, offset);
		}

		public Directory add(Entry e) {
			Content.Blob blob = append(e);
			return new Directory(blob, offset);
		}

		public Directory addAll(Entry[] es) {
			Content.Blob blob = appendAll(es);
			return new Directory(blob, offset);
		}

		public Directory replace(String name, byte[] contents) {
			for (int i = 0; i != length(); ++i) {
				Entry ith = get(i);
				String n = new String(ith.getFirst());
				if (n.equals(name)) {
					// Match
					// FIXME: replacing whole entry!
					Content.Blob b = set(i, new Entry(name.getBytes(), contents));
					//
					return new Directory(b, offset);
				}
			}
			// Nothing found
			return this;
		}

		@Override
		public String toString() {
			String r = "{";
			for (int i = 0; i != length(); ++i) {
				if (i != 0) {
					r += ",";
				}
				r += get(i);
			}
			return r + "}";
		}
	}

	private static final class Entry extends Pair.Proxy<byte[], byte[], Entry> {
		public static final Pair.Layout<byte[], byte[], Entry> LAYOUT = Pair.LAYOUT(BYTE_ARRAY, BYTE_ARRAY, Entry::new);

		public Entry(byte[] first, byte[] second) {
			this(LAYOUT.initialise(ByteBlob.EMPTY, 0, first, second), 0);
		}

		public Entry(Blob blob, int offset) {
			super(LAYOUT, blob, offset);
		}

		@Override
		public String toString() {
			String f = new String(getFirst());
			return "\"" + f + "\":" + new String(getSecond()).replace("\n", "\\n");
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		BuildServer server = new BuildServer();
		//
		FileSystem fs = FileSystems.getDefault();
		WatchService watcher = fs.newWatchService();
		Path p = fs.getPath(".");
		// Initialise ledger with existing files
		ArrayList<Entry> pairs = new ArrayList<>();
		for (String f : p.toFile().list()) {
			if (f.matches("[a-zA-Z0-9]+.txt")) {
				byte[] contents = Files.readAllBytes(fs.getPath(f));
				pairs.add(new Entry(f.getBytes(), contents));
			}
		}
		//
		server.addAll(pairs.toArray(new Entry[pairs.size()]));
		//
		System.out.println(server);
		// Watch for remaining changes
		WatchKey key = p.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		while (key.isValid()) {
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
							byte[] contents = Files.readAllBytes(path);
							server.create(path.toString(), contents);
						} else if (kind == ENTRY_DELETE) {
							server.remove(path.toString());
						} else {
							byte[] contents = Files.readAllBytes(path);
							server.update(path.toString(), contents);
						}
						System.out.println(server);
					}
				}
			}
			key.reset();
		}
	}
}
