import javax.swing.*;
import java.awt.*;

public class LevelComponent extends JComponent {
    private Level level;

    public LevelComponent(Level level) {
        this.level = level;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the LINEDEFS
        g.setColor(Color.RED);
        for (Level.Linedef linedef : level.getLinedefs()) {
            Level.Vertex start = linedef.getStartVertex();
            Level.Vertex end = linedef.getEndVertex();

            System.out.println("Vertex start: (" + start.getX() + ", " + start.getY() + ")");
            System.out.println("Vertex end: (" + end.getX() + ", " + end.getY() + ")");

            // Get the side def indices if the start and end vertexes
            int startSidedefIndex = linedef.getRightSidedef();
            int endSidedefIndex = linedef.getLeftSidedef();

            Level.Sidedef startSidedef = null;
            Level.Sidedef endSidedef = null;

            if (startSidedefIndex != 65535 && endSidedefIndex != 65535) {
                startSidedef = level.getSidedefs().get(startSidedefIndex);
                endSidedef = level.getSidedefs().get(endSidedefIndex);
                System.out.println("Start sidedef index: " + startSidedefIndex + ", End sidedef index: " + endSidedefIndex);
            } else {
                //System.out.println("Skipping linedef from vertex " + start + " to vertex " + end + " due to sidedef index 65535.");
                continue;
            }

            int startSectorIndex = startSidedef.getSectorIndex();
            int endSectorIndex = endSidedef.getSectorIndex();

            Level.Sector startSector = null;
            Level.Sector endSector = null;

            if (startSectorIndex != 65535 && endSectorIndex != 65535) {
                startSector = level.getSectors().get(startSectorIndex);
                endSector = level.getSectors().get(endSectorIndex);
                System.out.println("Start sector index: " + startSectorIndex + ", End sector index: " + endSectorIndex);
            } else {
               // System.out.println("Skipping linedef from vertex " + start + " to vertex " + end + " due to sector index 65535.");
                continue;
            }

            if (start.getY() < startSector.getCeilingHeight() && end.getY() < endSector.getCeilingHeight()) {
                // Scale the coordinates to fit within the component size
                double scaleX = (double) getWidth() / level.getMaxX();
                double scaleY = (double) getHeight() / level.getMaxY();
                int startX = (int) ((start.getX() / (double) level.getMaxX()) * getWidth());
                int startY = getHeight() - (int) ((start.getY() / (double) level.getMaxY()) * getHeight());
                int endX = (int) ((end.getX() / (double) level.getMaxX()) * getWidth());
                int endY = getHeight() - (int) ((end.getY() / (double) level.getMaxY()) * getHeight());

                // Draw the line
                g.drawLine(startX, startY, endX, endY);
                System.out.println("Drawing line from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ")");
            } else {
               // System.out.println("Skipping line from vertex " + start + " to vertex " + end + " due to ceiling height.");
                // Draw the VERTEXES
           /* g.setColor(Color.BLUE);
            for (Level.Vertex vertex : level.getVertexes()) {
                // Scale the coordinates to fit within the component size
                int x = (int) ((vertex.getX() / (double) level.getMaxX()) * getWidth());
                int y = getHeight() - (int) ((vertex.getY() / (double) level.getMaxY()) * getHeight());

                // Draw a small rectangle for the vertex
                g.fillRect(x - 2, y - 2, 4, 4);
            }*/

          /*  // Draw the THINGS
            g.setColor(Color.GREEN);
            for (Level.Thing thing : level.getThings()) {
                // Scale the coordinates to fit within the component size
                int x = (int) ((thing.getX() / (double) level.getMaxX()) * getWidth());
                int y = getHeight() - (int) ((thing.getY() / (double) level.getMaxY()) * getHeight());

                // Draw a circle for the thing
                g.fillOval(x - 5, y - 5, 10, 10);
            }*/
            }
        }
    }
}



   /* private boolean isValidSidedefIndex(int index) {
        return index >= 0 && index < level.getSidedefs().size();
    }

    private boolean isValidSectorIndex(int index) {
        return index >= 0 && index < level.getSectors().size();
    }

    private boolean isValidCoordinate(int startX, int startY, int endX, int endY) {
        int width = getWidth();
        int height = getHeight();

        return startX >= 0 && startX <= width &&
                startY >= 0 && startY <= height &&
                endX >= 0 && endX <= width &&
                endY >= 0 && endY <= height;
    }
}*/

