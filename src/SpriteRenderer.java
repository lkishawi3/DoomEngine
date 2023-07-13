import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;


public class SpriteRenderer {
    public static void main(String[] args) {
        try {
            WadFile wadFile = new WadFile("src/DOOM1.WAD");
            Map<String, byte[]> spriteDataMap = new LinkedHashMap<>();
            byte[] paletteData = null;

            for (WadDirectoryEntry entry : wadFile.getDirectory()) {
                System.out.println(entry.getName());
                if (entry.getName().equals("PLAYPAL")) {
                    paletteData = wadFile.getLumpData(entry);
                } else if (entry.getName().startsWith("TROO")) {
                    byte[] spriteData = wadFile.getLumpData(entry);
                    if (spriteData != null) {
                        spriteDataMap.put(entry.getName(), spriteData);
                    }
                }
            }
            if (!spriteDataMap.isEmpty() && paletteData != null) {
                // Create a panel to hold the sprites
                int horizontalGap = 0;
                int verticalGap = 0;
                JPanel spritePanel = new JPanel(new GridLayout(0, 5, horizontalGap, verticalGap));

                //Iterate over each sprite data
                for(Map.Entry<String, byte[]> spriteEntry : spriteDataMap.entrySet()) {
                    String spriteName = spriteEntry.getKey();
                    byte[] spriteData = spriteEntry.getValue();

                    // Parse the sprite header
                    int width = Byte.toUnsignedInt(spriteData[0]) | (Byte.toUnsignedInt(spriteData[1]) << 8);
                    int height = Byte.toUnsignedInt(spriteData[2]) | (Byte.toUnsignedInt(spriteData[3]) << 8);
                    int leftOffset = Byte.toUnsignedInt(spriteData[4]) | (Byte.toUnsignedInt(spriteData[5]) << 8);
                    int topOffset = Byte.toUnsignedInt(spriteData[6]) | (Byte.toUnsignedInt(spriteData[7]) << 8);

                    // Image Buffer for sprite
                    int[] spritePixels = new int[width * height];

                    // Decode each column
                    for (int column = 0; column < width; column++) {
                        int columnOffset = Byte.toUnsignedInt(spriteData[8 + column * 4]) | (Byte.toUnsignedInt(spriteData[9 + column * 4]) << 8) | (Byte.toUnsignedInt(spriteData[10 + column * 4]) << 16) | (Byte.toUnsignedInt(spriteData[11 + column * 4]) << 24);
                        int offset = columnOffset;
                        while (Byte.toUnsignedInt(spriteData[offset]) != 255) {
                            int topdelta = Byte.toUnsignedInt(spriteData[offset++]);
                            int length = Byte.toUnsignedInt(spriteData[offset++]);
                            offset++; // Skip the padding byte
                            for (int i = 0; i < length; i++) {
                                spritePixels[column + (topdelta + i) * width] = Byte.toUnsignedInt(spriteData[offset++]);
                            }
                            offset++; // Skip the end of post byte
                        }
                    }


                    // Convert palette indices to RGB
                    for (int i = 0; i < spritePixels.length; i++) {
                        int paletteIndex = spritePixels[i];
                        int red = Byte.toUnsignedInt(paletteData[paletteIndex * 3]);
                        int green = Byte.toUnsignedInt(paletteData[paletteIndex * 3 + 1]);
                        int blue = Byte.toUnsignedInt(paletteData[paletteIndex * 3 + 2]);
                        spritePixels[i] = (red << 16) | (green << 8) | blue;
                    }

                    // Create an image from the sprite data
                    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    image.setRGB(0, 0, width, height, spritePixels, 0, width);


                    //Create a label with the sprite image and add it to the panel
                    JLabel spriteLabel = new JLabel(spriteName);
                    spriteLabel.setIcon(new ImageIcon(image));
                    spritePanel.add(spriteLabel);
                }


                // Scroll pane to hold the sprite panel
                JScrollPane scrollPane = new JScrollPane(spritePanel);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scrollPane.getViewport().setPreferredSize(spritePanel.getPreferredSize());
                // Window
                JFrame frame = new JFrame("Doom Sprites");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(scrollPane);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
/*
                System.out.println("Sprite Width: " + width);
                System.out.println("Sprite Height: " + height);
                System.out.println("Sprite Left Offset: " + leftOffset);
                System.out.println("Sprite Top Offset: " + topOffset);
                System.out.println("spritePixels: " + spriteData.length);
*/

            } else {
                System.out.println("Could not find TROOA1 lump.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}