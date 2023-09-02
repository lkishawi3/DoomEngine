import java.util.ArrayList;
import java.util.List;

public class Level {
    private List<Thing> things;
    private List<Linedef> linedefs;
    private List<Sidedef> sidedefs;
    private List<Vertex> vertexes;
    private List<Sector> sectors;
    private int maxX;
    private int maxY;
    private int[][] grid;
    // other data structures...

    public Level(byte[] thingsData) {
        this.things = parseThings(thingsData);
    }

    public Level(WadFile wadFile, String name) {
        this.things = parseThings(wadFile.getLumpData("THINGS"));
        vertexes = parseVertexes(wadFile.getLumpData("VERTEXES")); // Moved up
        linedefs = parseLinedefs(wadFile.getLumpData("LINEDEFS")); // Moved down
        sidedefs = parseSidedefs(wadFile.getLumpData("SIDEDEFS"));
        sectors = parseSectors(wadFile.getLumpData("SECTORS"));
        this.things = new ArrayList<>();
        System.out.println("Level things: " + things);
        System.out.println("Number of linedefs: " + linedefs.size());
        System.out.println("Number of sidedefs: " + sidedefs.size());
        System.out.println("Number of sectors: " + sectors.size());
        System.out.println("Number of vertexes: " + vertexes.size());
        calculateMaxCoordinates();
    }

    private List<Thing> parseThings(byte[] data) {
        List<Thing> things = new ArrayList<>();
        for (int i = 0; i + 9 < data.length; i += 10) {
            int x = Byte.toUnsignedInt(data[i]) | (Byte.toUnsignedInt(data[i + 1]) << 8);
            int y = Byte.toUnsignedInt(data[i + 2]) | (Byte.toUnsignedInt(data[i + 3]) << 8);
            int angle = Byte.toUnsignedInt(data[i + 4]) | (Byte.toUnsignedInt(data[i + 5]) << 8);
            int type = Byte.toUnsignedInt(data[i + 6]) | (Byte.toUnsignedInt(data[i + 7]) << 8);
            int flags = Byte.toUnsignedInt(data[i + 8]) | (Byte.toUnsignedInt(data[i + 9]) << 8);
            things.add(new Thing(x, y, angle, type, flags));
        }
        return things;
    }


    private List<Linedef> parseLinedefs(byte[] data) {
        List<Linedef> linedefs = new ArrayList<>();
        for (int i = 0; i < data.length; i += 14) {
            int startVertexIndex = Byte.toUnsignedInt(data[i]) | (Byte.toUnsignedInt(data[i + 1]) << 8);
            int endVertexIndex = Byte.toUnsignedInt(data[i + 2]) | (Byte.toUnsignedInt(data[i + 3]) << 8);
            int flags = Byte.toUnsignedInt(data[i + 4]) | (Byte.toUnsignedInt(data[i + 5]) << 8);
            int lineType = Byte.toUnsignedInt(data[i + 6]) | (Byte.toUnsignedInt(data[i + 7]) << 8);
            int sectorTag = Byte.toUnsignedInt(data[i + 8]) | (Byte.toUnsignedInt(data[i + 9]) << 8);
            int rightSidedef = Byte.toUnsignedInt(data[i + 10]) | (Byte.toUnsignedInt(data[i + 11]) << 8);
            int leftSidedef = Byte.toUnsignedInt(data[i + 12]) | (Byte.toUnsignedInt(data[i + 13]) << 8);
            Vertex startVertex = vertexes.get(startVertexIndex);
            Vertex endVertex = vertexes.get(endVertexIndex);
            linedefs.add(new Linedef(startVertex, endVertex, flags, lineType, sectorTag, rightSidedef, leftSidedef));
        }
        return linedefs;
    }

    private List<Sidedef> parseSidedefs(byte[] data) {
        List<Sidedef> sidedefs = new ArrayList<>();
        for (int i = 0; i < data.length; i += 30) {
            int xOffset = Byte.toUnsignedInt(data[i]) | (Byte.toUnsignedInt(data[i + 1]) << 8);
            int yOffset = Byte.toUnsignedInt(data[i + 2]) | (Byte.toUnsignedInt(data[i + 3]) << 8);
            String upperTexture = new String(data, i + 4, 8).trim();
            String lowerTexture = new String(data, i + 12, 8).trim();
            String middleTexture = new String(data, i + 20, 8).trim();
            int sector = Byte.toUnsignedInt(data[i + 28]) | (Byte.toUnsignedInt(data[i + 29]) << 8);
            sidedefs.add(new Sidedef(xOffset, yOffset, upperTexture, lowerTexture, middleTexture, sector));
        }
        return sidedefs;
    }

    private List<Vertex> parseVertexes(byte[] data) {
        List<Vertex> vertexes = new ArrayList<>();
        for (int i = 0; i < data.length; i += 4) {
            int x = Byte.toUnsignedInt(data[i]) | (Byte.toUnsignedInt(data[i + 1]) << 8) / 1000;
            int y = Byte.toUnsignedInt(data[i + 2]) | (Byte.toUnsignedInt(data[i + 3]) << 8) / 1000;
            vertexes.add(new Vertex(x, y));
        }
        return vertexes;
    }

    private List<Sector> parseSectors(byte[] data) {
        List<Sector> sectors = new ArrayList<>();
        for (int i = 0; i < data.length; i += 26) {
            int floorHeight = Byte.toUnsignedInt(data[i]) | (Byte.toUnsignedInt(data[i + 1]) << 8);
            int ceilingHeight = Byte.toUnsignedInt(data[i + 2]) | (Byte.toUnsignedInt(data[i + 3]) << 8);
            String floorTexture = new String(data, i + 4, 8).trim();
            String ceilingTexture = new String(data, i + 12, 8).trim();
            int lightLevel = Byte.toUnsignedInt(data[i + 20]) | (Byte.toUnsignedInt(data[i + 21]) << 8);
            int type = Byte.toUnsignedInt(data[i + 22]) | (Byte.toUnsignedInt(data[i + 23]) << 8);
            int tag = Byte.toUnsignedInt(data[i + 24]) | (Byte.toUnsignedInt(data[i + 25]) << 8);
            sectors.add(new Sector(floorHeight, ceilingHeight, floorTexture, ceilingTexture, lightLevel, type, tag));
        }
        return sectors;
    }

    public List<Thing> getThings() {
        return things;
    }

    public void generateGrid(int padding) {
        // Create the grid with some padding
        int maxX = getMaxX() + padding;
        int maxY = getMaxY() + padding;
        this.grid = new int[maxX][maxY];

        //Initialize the grid with zeros
        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < maxY; y++) {
                grid[x][y] = 0;
            }
        }

        markLinedefs();
    }

    private void markLinedefs() {
        for(Linedef linedef : getLinedefs()) {
            int x0 = linedef.getStartVertex().getX();
            int y0 = linedef.getStartVertex().getY();
            int x1 = linedef.getEndVertex().getX();
            int y1 = linedef.getEndVertex().getY();

            int dx = Math.abs(x1 - x0);
            int dy = Math.abs(y1 - y0);

            int sx = (x0 < x1) ? 1 : -1;
            int sy = (y0 < y1) ? 1 : -1;

            int err = dx - dy;

            while (true) {
                // Mark the current cell as a linedef
                grid[x0][y0] = 1;

                if (x0 == x1 && y0 == y1) break;

                int e2 = 2 * err;
                if (e2 > -dy) {
                    err -= dy;
                    x0 += sx;
                }
                if (e2 < dx) {
                    err += dx;
                    y0 += sy;
                }
            }
        }
    }

    public int[][] getGrid() {
        return this.grid;
    }

    public List<Vertex> getVertexes() {
        return vertexes;
    }

    public List<Linedef> getLinedefs() {
        return linedefs;
    }

    public List<Sector> getSectors() {
        return sectors;
    }

    public List<Sidedef> getSidedefs() {
        return sidedefs;
    }

    public static class Thing {
        private int x, y, angle, type, flags;

        public Thing(int x, int y, int angle, int type, int flags) {
            this.x = x;
            this.y = y;
            this.angle = angle;
            this.type = type;
            this.flags = flags;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }




    public static class Linedef {
        private Vertex startVertex;
        private Vertex endVertex;
        private int flags;
        private int lineType;
        private int sectorTag;
        private int rightSidedef;
        private int leftSidedef;

        public Linedef(Vertex startVertex, Vertex endVertex, int flags,  int lineType, int sectorTag, int rightSidedef, int leftSidedef) {
            this.startVertex = startVertex;
            this.endVertex = endVertex;
            this.flags = flags;
            this.lineType = lineType;
            this.sectorTag = sectorTag;
            this.rightSidedef = rightSidedef;
            this.leftSidedef = leftSidedef;
        }

        public Vertex getStartVertex() {
            return startVertex;
        }

        public Vertex getEndVertex() {
            return endVertex;
        }

        public int getRightSidedef() {
            return rightSidedef;
        }
        public int getLeftSidedef() {
            return leftSidedef;
        }
    }


    public static class Vertex {
        private int x;
        private int y;

        public Vertex(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public static class Sidedef {
        private int xOffset;
        private int yOffset;
        private String upperTextures;
        private String lowerTextures;
        private String middleTextures;
        private int sector;
        public int sectorIndex;

        public int getSectorIndex() {
            return sectorIndex;
        }

        public Sidedef(int xOffset, int yOffset, String upperTextures, String lowerTextures, String middleTextures, int sector) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.upperTextures = upperTextures;
            this.lowerTextures = lowerTextures;
            this.middleTextures = middleTextures;
            this.sector = sector;
            this.sectorIndex = sector;


        }
    }



    public static class Sector {
        private int floorHeight;
        private int ceilingHeight;
        private String floorTexture;
        private String ceilingTextures;
        private int lightLevel;
        private int type;
        private int tag;

        public Sector(int floorHeight, int ceilingHeight, String floorTexture, String ceilingTextures, int lightLevel, int type, int tag) {
            this.floorHeight = floorHeight;
            this.ceilingHeight = ceilingHeight;
            this.floorTexture = floorTexture;
            this.ceilingTextures = ceilingTextures;
            this.lightLevel = lightLevel;
            this.type = type;
            this.tag = tag;
        }

        public int getFloorHeight() {
            return floorHeight;
        }

        public int getCeilingHeight() {
            return ceilingHeight;
        }
    }

    private void calculateMaxCoordinates() {
        maxX = Integer.MIN_VALUE;
        maxY = Integer.MIN_VALUE;

        for (Vertex vertex : vertexes) {
            if (vertex.getX() > maxX) {
                maxX = vertex.getY();
            }
            if (vertex.getY() > maxY) {
                maxY = vertex.getY();
            }
        }
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }


}
