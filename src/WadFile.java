import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WadFile {
    private WadHeader header;
    private List<WadDirectoryEntry> directory;
    // We'll just store the lump data as raw bytes for now
    private List<byte[]> lumps;

    // add constructors, getters, setters, etc...

    public byte[] getLumpData(WadDirectoryEntry entry) {
        int index = directory.indexOf(entry);
        if (index != -1) {
            return lumps.get(index);
        } else {
            return null;
        }
    }

    public List<WadDirectoryEntry> getDirectory() {
        return directory;
    }

    public WadFile(String filename) throws IOException {
        loadWadFile(filename);




    }

    private void loadWadFile(String filename) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filename, "r");

        // Read the header
        header = new WadHeader();
        header.setType(readString(file, 4));
        header.setNumLumps(readInt(file));
        header.setDirectoryOffset(readInt(file));

        // Read the directory
        directory = new ArrayList<>();
        file.seek(header.getDirectoryOffset());
        for (int i = 0; i < header.getNumLumps(); i++) {
            WadDirectoryEntry entry = new WadDirectoryEntry();
            entry.setOffset(readInt(file));
            entry.setSize(readInt(file));
            entry.setName(readString(file, 8));
            directory.add(entry);
        }



        // Read the lump data
        lumps = new ArrayList<>();
        for (WadDirectoryEntry entry : directory) {
            byte[] data = new byte[entry.getSize()];
            file.seek(entry.getOffset());
            file.readFully(data);
            lumps.add(data);
        }

        file.close();
    }

    private String readString(RandomAccessFile file, int length) throws IOException {
        byte[] bytes = new byte[length];
        file.read(bytes);
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }

    private int readInt(RandomAccessFile file) throws IOException {
        return Integer.reverseBytes(file.readInt());
    }

    // add getters for header, directory, and lumps...
}

class WadHeader {
    private String type;
    private int numLumps;
    private int directoryOffset;

    // Constructor
    public WadHeader() {}

    // Getters
    public String getType() {
        return type;
    }

    public int getNumLumps() {
        return numLumps;
    }

    public int getDirectoryOffset() {
        return directoryOffset;
    }

    // Setters
    public void setType(String type) {
        this.type = type;
    }

    public void setNumLumps(int numLumps) {
        this.numLumps = numLumps;
    }

    public void setDirectoryOffset(int directoryOffset) {
        this.directoryOffset = directoryOffset;
    }
}

class WadDirectoryEntry {
    private int offset;
    private int size;
    private String name;

    // Constructor
    public WadDirectoryEntry() {}

    // Getters
    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    // Setters
    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setName(String name) {
        this.name = name;
    }
}

