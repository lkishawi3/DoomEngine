import java.io.IOException;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;


public class Main {
    public static void main(String[] args) {
        try {
            WadFile wadFile = new WadFile("src/DOOM1.WAD");
            byte[] spriteData = null;
            byte[] paletteData = null;
            for (WadDirectoryEntry entry : wadFile.getDirectory()) {
                System.out.println(entry.getName());
                if (entry.getName().equals("SARGA1")) {
                    spriteData = wadFile.getLumpData(entry);
                } else if (entry.getName().equals("PLAYPAL")) {
                    paletteData = wadFile.getLumpData(entry);
                }
            }
            if (spriteData != null && paletteData != null) {
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

                // Window
                JFrame frame = new JFrame("Doom Sprite");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JLabel label = new JLabel(new ImageIcon(image));
                frame.getContentPane().add(label, BorderLayout.CENTER);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                System.out.println("Sprite Width: " + width);
                System.out.println("Sprite Height: " + height);
                System.out.println("Sprite Left Offset: " + leftOffset);
                System.out.println("Sprite Top Offset: " + topOffset);
                System.out.println("spritePixels: " + spriteData.length);


            } else {
                System.out.println("Could not find TROOA1 lump.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}