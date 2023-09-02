import javax.swing.JFrame;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;


public class DoomViewer {
    public static void main(String[] args) {
        // Create a WadFile instance
        WadFile wadFile;
        try {
            wadFile = new WadFile("src/Doom1.WAD");
            System.out.println("WadFile created successfully!");
            System.out.println("WadFile Header: " + wadFile.getHeader());
        } catch (IOException e) {
            System.out.println("Failed to create WadFile");
            e.printStackTrace();
            return;
        }

        /*// Print out all lump names
        for (WadDirectoryEntry entry : wadFile.getDirectory()) {
            System.out.println("Lump name: " + entry.toString());
        }*/


        // Create a Level instance
        Level level;
        try {
            level = new Level(wadFile, "E1M1");
            level.generateGrid(10);
            System.out.println("Level created successfully!");
            System.out.println("Level things: " + level.getThings());
        } catch (Exception e) {
            System.out.println("Failed to create Level");
            e.printStackTrace();
            return;
        }

        // Create a JFrame
        JFrame frame = new JFrame("Doom Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a LevelComponent and add it to the JFrame
        LevelComponent levelComponent = new LevelComponent(level);
        frame.getContentPane().add(levelComponent);

        // Set the size of the JFrame
        frame.setSize(800, 600);

        // Make the JFrame visible
        frame.setVisible(true);

        String workingDir = System.getProperty("user.dir");
        System.out.println("Current working directory: " + workingDir);
    }
}
