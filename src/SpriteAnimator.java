import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.io.IOException;
import java.util.List;

public class SpriteAnimator extends JPanel {

    private Map<String, List<BufferedImage>> spriteSequences;
    private List<String> sequenceNames;
    private String currentSequenceName;
    private int currentSpriteIndex;
    private Timer timer;
    private JButton nextSequenceButton;
    private int maxWidth;
    private int maxHeight;

    public SpriteAnimator(Map<String, List<BufferedImage>> spriteSequences, int animationDelay) {
        this.spriteSequences = spriteSequences;
        this.sequenceNames = new ArrayList<>(spriteSequences.keySet());
        this.currentSequenceName = this.sequenceNames.get(0);
        this.currentSpriteIndex = 0;

        // Calculate the maximum sprite dimensions
        this.maxWidth = 0;
        this.maxHeight = 0;
        for (List<BufferedImage> sequence : this.spriteSequences.values()) {
            for (BufferedImage sprite : sequence) {
                this.maxWidth = Math.max(this.maxWidth, sprite.getWidth());
                this.maxHeight = Math.max(this.maxHeight, sprite.getHeight());
            }
        }

        this.timer = new Timer(animationDelay, e -> {
            currentSpriteIndex = (currentSpriteIndex + 1) % spriteSequences.get(currentSequenceName).size();
            repaint();
        });
        timer.start();

        nextSequenceButton = new JButton("Next Sequence");
        nextSequenceButton.addActionListener(e -> {
            int currentIndex = sequenceNames.indexOf(currentSequenceName);
            currentSequenceName = sequenceNames.get((currentIndex + 1) % sequenceNames.size());
            currentSpriteIndex = 0;
            repaint();
        });
        this.add(nextSequenceButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage currentSprite = spriteSequences.get(currentSequenceName).get(currentSpriteIndex);

        int scale = 9;
        // Scale the sprite
        int scaledWidth = currentSprite.getWidth() * scale;
        int scaledHeight = currentSprite.getHeight() * scale;

        int x = (getWidth() - currentSprite.getWidth()) / 4 + 40;
        int y = (getHeight() - currentSprite.getHeight()) / 4 - 50;

        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(currentSprite, x, y, scaledWidth, scaledHeight, null);
    }

    @Override
    public Dimension getPreferredSize() {
        int width = spriteSequences.get(currentSequenceName).get(currentSpriteIndex).getWidth();
        return new Dimension(width, maxHeight);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                WadFile wadFile = new WadFile("src/DOOM1.WAD");
                Map<String, byte[]> spriteDataMap = new HashMap<>();
                byte[] paletteData = null;
                Map<String, String> lumpToSequence = new HashMap<>();
                lumpToSequence.put("TROOA1", "sequence1");
                lumpToSequence.put("TROOB1", "sequence1");
                lumpToSequence.put("TROOC1", "sequence1");
                lumpToSequence.put("TROOD1", "sequence1");
                lumpToSequence.put("TROOE1", "sequence2");
                lumpToSequence.put("TROOF1", "sequence2");
                lumpToSequence.put("TROOG1", "sequence2");
                lumpToSequence.put("TROOA2A8", "sequence3");
                lumpToSequence.put("TROOB2B8", "sequence3");
                lumpToSequence.put("TROOC2C8", "sequence3");
                lumpToSequence.put("TROOD2D8", "sequence3");
                lumpToSequence.put("TROOE2E8", "sequence4");
                lumpToSequence.put("TROOF2F8", "sequence4");
                lumpToSequence.put("TROOG2G8", "sequence4");
                lumpToSequence.put("TROOA4A6", "sequence5");
                lumpToSequence.put("TROOB4B6", "sequence5");
                lumpToSequence.put("TROOC4C6", "sequence5");
                lumpToSequence.put("TROOD4D6", "sequence5");
                lumpToSequence.put("TROOE4E6", "sequence6");
                lumpToSequence.put("TROOF4F6", "sequence6");
                lumpToSequence.put("TROOG4G6", "sequence6");
                lumpToSequence.put("TROOA5", "sequence7");
                lumpToSequence.put("TROOB5", "sequence7");
                lumpToSequence.put("TROOC5", "sequence7");
                lumpToSequence.put("TROOD5", "sequence7");
                lumpToSequence.put("TROOE5", "sequence8");
                lumpToSequence.put("TROOF5", "sequence8");
                lumpToSequence.put("TROOG5", "sequence8");

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
                    Map<String, List<BufferedImage>> spriteSequences = new HashMap<>();

                    // Iterate over each sprite data
                    for (Map.Entry<String, byte[]> spriteEntry : spriteDataMap.entrySet()) {
                        String spriteName = spriteEntry.getKey();
                        byte[] spriteData = spriteEntry.getValue();

                        // Parse the sprite header and decode the sprite data
                        int width = Byte.toUnsignedInt(spriteData[0]) | (Byte.toUnsignedInt(spriteData[1]) << 8);
                        int height = Byte.toUnsignedInt(spriteData[2]) | (Byte.toUnsignedInt(spriteData[3]) << 8);
                        int[] spritePixels = new int[width * height];

                        // Decode each column
                        for (int column = 0; column < width; column++) {
                            int columnOffset = Byte.toUnsignedInt(spriteData[8 + column * 4]) | (Byte.toUnsignedInt(spriteData[9 + column * 4]) << 8) | (Byte.toUnsignedInt(spriteData[10 + column * 4]) << 16) | (Byte.toUnsignedInt(spriteData[11 + column * 4]) << 24);
                            int offset = columnOffset;
                            while (Byte.toUnsignedInt(spriteData[offset]) != 255) {
                                int topdelta = Byte.toUnsignedInt(spriteData[offset++]);
                                int length = Byte.toUnsignedInt(spriteData[offset++]);
                                offset++;
                                for (int i = 0; i < length; i++) {
                                    spritePixels[column + (topdelta + i) * width] = Byte.toUnsignedInt(spriteData[offset++]);
                                }
                                offset++;
                            }
                        }

                        int maxHeight = spriteSequences.values().stream()
                                .flatMap(List::stream)
                                .mapToInt(BufferedImage::getHeight)
                                .max()
                                .orElse(0);

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

                        String sequenceName = lumpToSequence.get(spriteName); // e.g. "TROO"
                        if (sequenceName != null) {
                            if (!spriteSequences.containsKey(sequenceName)) {
                                spriteSequences.put(sequenceName, new ArrayList<>());
                            }
                            spriteSequences.get(sequenceName).add(image);
                        }
                    }

                    // Create the sprite animator and start the animation
                    SpriteAnimator spriteAnimator = new SpriteAnimator(spriteSequences, 200);

                    // Create the frame to display the sprite animator
                    JFrame frame = new JFrame("Doom Sprite Animator");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.getContentPane().add(spriteAnimator);
                    frame.setPreferredSize(new Dimension(800, 800));
                    frame.pack();
                    frame.setLocationRelativeTo(null);


                    frame.setVisible(true);


                } else {
                    System.out.println("Could not find TROOA1 lump.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}

