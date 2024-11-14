package dev.tr7zw.itemswapper.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorUtil {

    private static final float COLOR_NORMAL = (float) Math.sqrt(255d * 255d * 3d);

    public enum ColorFormat {
        RGBA, ABGR
    }

    public record UnpackedColor(int a, int r, int g, int b) {
        public static UnpackedColor parse(int color, ColorFormat format) {
            return switch (format) {
            case RGBA:
                yield new UnpackedColor((color >>> 24), (color & 0xFF), (color >> 8 & 0xFF), (color >> 16 & 0xFF));
            case ABGR:
                // ABGR: Alpha in bits 24-31, Blue in bits 16-23, Green in bits 8-15, Red in bits 0-7
                yield new UnpackedColor((color >>> 24), (color >> 16 & 0xFF), (color >> 8 & 0xFF), (color & 0xFF));
            };
        }

        public UnpackedColor(Color color) {
            this(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
        }

        public int toInt() {
            return (a << 24) | (r) | (g << 8) | (b << 16);
        }
    }

    // Method to calculate the Euclidean distance between two colors, ignoring fully
    // transparent pixels
    public static double colorDistance(UnpackedColor c1, UnpackedColor c2) {
        // If both colors are fully transparent, return 0 (no distance)
        if (c1.a == 0 && c2.a == 0) {
            return 0;
        }

        double deltaR = c2.r - c1.r;
        double deltaG = c2.g - c1.g;
        double deltaB = c2.b - c1.b;

        return Math.sqrt(deltaR * deltaR + deltaG * deltaG + deltaB * deltaB) / COLOR_NORMAL;
    }

    public static UnpackedColor calculateAverageColor(UnpackedColor[] colors) {
        int totalRed = 0;
        int totalGreen = 0;
        int totalBlue = 0;
        int totalAlpha = 0;

        // Sum up the RGBA components
        for (UnpackedColor color : colors) {
            totalRed += color.r;
            totalGreen += color.g;
            totalBlue += color.b;
            totalAlpha += color.a;
        }

        return new UnpackedColor(totalAlpha / colors.length, totalRed / colors.length, totalGreen / colors.length,
                totalBlue / colors.length);
    }

    public static UnpackedColor calculateAverageColor(List<UnpackedColor> colors) {
        int totalRed = 0;
        int totalGreen = 0;
        int totalBlue = 0;
        int totalAlpha = 0;

        // Sum up the RGBA components
        for (UnpackedColor color : colors) {
            totalRed += color.r;
            totalGreen += color.g;
            totalBlue += color.b;
            totalAlpha += color.a;
        }

        return new UnpackedColor(totalAlpha / colors.size(), totalRed / colors.size(), totalGreen / colors.size(),
                totalBlue / colors.size());
    }

    // Method to create a Tetrad 4 color palette with a 30° offset from a given
    // color
    public static List<UnpackedColor> createTetradPalette(UnpackedColor color) {
        List<UnpackedColor> palette = new ArrayList<>();

        // Convert the given color to HSV
        float[] hsv = new float[3];
        java.awt.Color.RGBtoHSB(color.r, color.g, color.b, hsv);

        // Calculate the offsets
        float offset1 = hsv[0];
        if (hsv[1] < 0.1 || hsv[2] < 0.2) {
            offset1 = hsv[2];
        }
        float offset2 = (offset1 + 30f / 360f) % 1f;
        float offset3 = (offset1 + 0.5f) % 1f;
        float offset4 = (offset2 + 0.5f) % 1f;

        Color color1;
        Color color2;
        Color color3;
        Color color4;

        if (hsv[1] < 0.1 || hsv[2] < 0.2) {
            // Create the colors for the palette based on the brightness offsets
            color1 = Color.getHSBColor(hsv[0], hsv[1], offset1);
            color2 = Color.getHSBColor(hsv[0], hsv[1], offset2);
            color3 = Color.getHSBColor(hsv[0], hsv[1], offset3);
            color4 = Color.getHSBColor(hsv[0], hsv[1], offset4);
        } else {
            // Create the colors for the palette based on the hue offsets
            color1 = Color.getHSBColor(offset1, hsv[1], hsv[2]);
            color2 = Color.getHSBColor(offset2, hsv[1], hsv[2]);
            color3 = Color.getHSBColor(offset3, hsv[1], hsv[2]);
            color4 = Color.getHSBColor(offset4, hsv[1], hsv[2]);
        }

        // Add the colors to the palette
        palette.add(new UnpackedColor(color1));
        palette.add(new UnpackedColor(color2));
        palette.add(new UnpackedColor(color3));
        palette.add(new UnpackedColor(color4));

        return palette;
    }

    public static UnpackedColor[] primaryColorDetection(int[] colors, ColorFormat format, float closenessThreshold) {
        if (colors == null || colors.length == 0) {
            return new UnpackedColor[0];
        }

        // Map to store colors grouped by their closeness
        Map<UnpackedColor, List<UnpackedColor>> colorGroups = new HashMap<>();

        // Perform blob detection
        for (int mojangColor : colors) {
            UnpackedColor color = UnpackedColor.parse(mojangColor, format);
            boolean foundGroup = false;
            for (UnpackedColor groupColor : colorGroups.keySet()) {
                if (colorDistance(color, groupColor) < closenessThreshold) {
                    colorGroups.get(groupColor).add(color);
                    foundGroup = true;
                    break;
                }
            }
            if (!foundGroup) {
                List<UnpackedColor> newGroup = new ArrayList<>();
                newGroup.add(color);
                colorGroups.put(color, newGroup);
            }
        }

        List<List<UnpackedColor>> blobs = new ArrayList<>(colorGroups.values());
        blobs.sort((a, b) -> Integer.compare(b.size(), a.size()));
        return new UnpackedColor[] { calculateAverageColor(blobs.get(0)) };
    }

    public static UnpackedColor[] blobDetection(int[] colors, ColorFormat format, float closenessThreshold) {
        if (colors == null || colors.length == 0) {
            return new UnpackedColor[0];
        }

        // Map to store colors grouped by their closeness
        Map<UnpackedColor, List<UnpackedColor>> colorGroups = new HashMap<>();

        // Perform blob detection
        for (int mojangColor : colors) {
            UnpackedColor color = UnpackedColor.parse(mojangColor, format);
            boolean foundGroup = false;
            for (UnpackedColor groupColor : colorGroups.keySet()) {
                if (colorDistance(color, groupColor) < closenessThreshold) {
                    colorGroups.get(groupColor).add(color);
                    foundGroup = true;
                    break;
                }
            }
            if (!foundGroup) {
                List<UnpackedColor> newGroup = new ArrayList<>();
                newGroup.add(color);
                colorGroups.put(color, newGroup);
            }
        }

        // Convert color groups to array
        UnpackedColor[] result = new UnpackedColor[colorGroups.size()];
        int index = 0;
        for (List<UnpackedColor> group : colorGroups.values()) {
            result[index] = calculateAverageColor(group);
            index++;
        }

        return result;
    }

    public static UnpackedColor[] calculateColorClumps(int[] colors, ColorFormat format, float closenessTreshold) {
        // colorset, (color, colortotal)
        List<UnpackedColor> colorArrays = new ArrayList<UnpackedColor>();
        for (int color : colors) {
            UnpackedColor pixel = UnpackedColor.parse(color, format);
            boolean colorExists = false;

            for (int i = 0; i < colorArrays.size(); i++) {
                UnpackedColor current = colorArrays.get(i);
                if (colorDistance(pixel, current) < closenessTreshold) {
                    colorExists = true;
                    current = new UnpackedColor((current.a + pixel.a) >> 1, (current.r + pixel.r) >> 1,
                            (current.g + pixel.g) >> 1, (current.b + pixel.b) >> 1);
                    colorArrays.set(i, current);
                    break;
                }
            }
            // new clump
            if (!colorExists) {
                colorArrays.add(UnpackedColor.parse(color, format));
            }
        }
        return colorArrays.toArray(new UnpackedColor[0]);
    }

}
