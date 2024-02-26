package dev.tr7zw.itemswapper.manager;

import java.awt.Color;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.mojang.blaze3d.platform.NativeImage;

import dev.tr7zw.itemswapper.accessor.SpriteContentsAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockTextureManager {

    private final Map<Block, Integer> colorMap = new HashMap<>();
    private final Set<String> bannedKeywords = new HashSet<String>(Arrays.asList("_slab", "_stairs", "_fence", "_gate",
            "_carpet", "_wall", "piston", "cake", "repeater", "comparator", "waxed_"));

    public void init() {
        colorMap.clear();
        BuiltInRegistries.BLOCK.entrySet().forEach(e -> {
            System.out.println(e.getKey() + " " + e.getValue());
            BlockState state = e.getValue().getStateDefinition().any();
            TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRenderer().getBlockModel(state)
                    .getParticleIcon();
            System.out.println(sprite);
            if (state.canOcclude() && !state.hasBlockEntity() && state.isSolid()) {
                for (String key : bannedKeywords) {
                    if (e.getKey().location().toString().contains(key)) {
                        return;
                    }
                }
                NativeImage img = ((SpriteContentsAccess) sprite.contents()).getOriginalImage();
                colorMap.put(e.getValue(), calculateAverageColor(img.getPixelsRGBA()));
                int[] colors = blobDetection(img.getPixelsRGBA(), 50f);
                int c2 = calculateAverageColor(img.getPixelsRGBA());
                System.out.println("org: " + ((c2 >> 16) & 0xFF) + ";" + ((c2 >> 8) & 0xFF) +";" + (c2 & 0xFF) + " " + String.join(", ", Arrays.stream(colors).mapToObj(c1 -> ((c1 >> 16) & 0xFF) + ";" + ((c1 >> 8) & 0xFF) +";" + (c1 & 0xFF)).collect(Collectors.toList())));
            }
        });
    }

    public Integer getColor(Block block) {
        if (colorMap.isEmpty()) {
            init();
        }
        return colorMap.get(block);
    }

    // Method to sort blocks by the closeness of their average colors to a target block's average color
    public List<Block> getBlocksByAverageColor(int targetAverageColor) {
        if (colorMap.isEmpty()) {
            init();
        }

        // Create a list to store blocks along with their color distances from the target block
        List<Map.Entry<Block, Double>> blockDistances = new ArrayList<>();

        // Calculate the distance between the average color of each block and the target block's average color
        for (Block block : colorMap.keySet()) {
            int blockAverageColor = colorMap.get(block);
            double distance = colorDistance(targetAverageColor, blockAverageColor);
            blockDistances.add(new AbstractMap.SimpleEntry<>(block, distance));
        }

        // Sort the blocks based on their color distances from the target block
        blockDistances.sort(Comparator.comparingDouble(Map.Entry::getValue));

        // Extract the sorted blocks from the sorted list of block-distance pairs
        List<Block> sortedBlocks = new ArrayList<>();
        for (Map.Entry<Block, Double> entry : blockDistances) {
            sortedBlocks.add(entry.getKey());
        }

        return sortedBlocks;
    }

    // Method to calculate the average color from an array of integers in RGBA format
    private static int calculateAverageColor(int[] colors) {
        int totalRed = 0;
        int totalGreen = 0;
        int totalBlue = 0;
        int totalAlpha = 0;

        // Sum up the RGBA components
        for (int color : colors) {
            totalRed += (color >> 16) & 0xFF;
            totalGreen += (color >> 8) & 0xFF;
            totalBlue += (color) & 0xFF;
            totalAlpha += (color >> 24) & 0xFF;
        }

        // Calculate the average RGBA components
        int averageRed = totalRed / colors.length;
        int averageGreen = totalGreen / colors.length;
        int averageBlue = totalBlue / colors.length;
        int averageAlpha = totalAlpha / colors.length;

        // Combine the average RGBA components into a single integer
        int averageColor = (averageAlpha << 24) | (averageRed << 16) | (averageGreen << 8) | averageBlue;

        return averageColor;
    }
    
    private static int calculateAverageColor(List<Integer> colors) {
        int totalRed = 0;
        int totalGreen = 0;
        int totalBlue = 0;
        int totalAlpha = 0;

        // Sum up the RGBA components
        for (int color : colors) {
            totalRed += (color >> 16) & 0xFF;
            totalGreen += (color >> 8) & 0xFF;
            totalBlue += (color) & 0xFF;
            totalAlpha += (color >> 24) & 0xFF;
        }

        // Calculate the average RGBA components
        int averageRed = totalRed / colors.size();
        int averageGreen = totalGreen / colors.size();
        int averageBlue = totalBlue / colors.size();
        int averageAlpha = totalAlpha / colors.size();

        // Combine the average RGBA components into a single integer
        int averageColor = (averageAlpha << 24) | (averageRed << 16) | (averageGreen << 8) | averageBlue;

        return averageColor;
    }

    // Method to calculate the Euclidean distance between two colors, ignoring fully transparent pixels
    private static double colorDistance(int c1, int c2) {
        int a1 = (c1 >> 24) & 0xFF;
        int a2 = (c2 >> 24) & 0xFF;

        // If both colors are fully transparent, return 0 (no distance)
        if (a1 == 0 && a2 == 0) {
            return 0;
        }

        int r1 = (c1 >> 16) & 0xFF;
        int g1 = (c1 >> 8) & 0xFF;
        int b1 = c1 & 0xFF;

        int r2 = (c2 >> 16) & 0xFF;
        int g2 = (c2 >> 8) & 0xFF;
        int b2 = c2 & 0xFF;

        double deltaR = r2 - r1;
        double deltaG = g2 - g1;
        double deltaB = b2 - b1;

        return Math.sqrt(deltaR * deltaR + deltaG * deltaG + deltaB * deltaB);
    }
    
    // Method to perform blob detection on an array of RGBA colors
    public static int[] blobDetection(int[] colors, float closenessThreshold) {
        if (colors == null || colors.length == 0) {
            return new int[0];
        }

        // Map to store colors grouped by their closeness
        Map<Integer, List<Integer>> colorGroups = new HashMap<>();

        // Perform blob detection
        for (int color : colors) {
            boolean foundGroup = false;
            for (int groupColor : colorGroups.keySet()) {
                if (colorDistance(color, groupColor) < closenessThreshold) {
                    colorGroups.get(groupColor).add(color);
                    foundGroup = true;
                    break;
                }
            }
            if (!foundGroup) {
                List<Integer> newGroup = new ArrayList<>();
                newGroup.add(color);
                colorGroups.put(color, newGroup);
            }
        }

        // Convert color groups to array
        int[] result = new int[colorGroups.size()];
        int index = 0;
        for (List<Integer> group : colorGroups.values()) {
            result[index] = calculateAverageColor(group);
            index++;
        }

        return result;
    }

    // Method to check if two colors are close enough based on the closeness threshold
    private static boolean isCloseEnough(int color1, int color2, float threshold) {
        // Implement your logic for comparing colors based on closeness threshold
        // For simplicity, let's just compare the absolute difference between the colors
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int colorDifference = Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
        return colorDifference <= threshold;
    }

    // Method to calculate the Euclidean distance between two colors in HSV space
    private static double colorDistanceHSV(int c1, int c2) {
        // Convert the RGB colors to HSV
        float[] hsv1 = new float[3];
        Color.RGBtoHSB((c1 >> 16) & 0xFF, (c1 >> 8) & 0xFF, c1 & 0xFF, hsv1);

        float[] hsv2 = new float[3];
        Color.RGBtoHSB((c2 >> 16) & 0xFF, (c2 >> 8) & 0xFF, c2 & 0xFF, hsv2);

        // Calculate the differences in hue, saturation, and value
        double deltaH = Math.abs(hsv2[0] - hsv1[0]);
        double deltaS = Math.abs(hsv2[1] - hsv1[1]);
        double deltaV = Math.abs(hsv2[2] - hsv1[2]);

        // Take into account hue wrapping around (360° is equivalent to 0°)
        if (deltaH > 0.5) {
            deltaH = 1 - deltaH;
        }

        // Calculate the Euclidean distance in HSV space
        return Math.sqrt(deltaH * deltaH + deltaS * deltaS + deltaV * deltaV);
    }

    // Method to create a Tetrad 4 color palette with a 30° offset from a given color
    public static List<Integer> createTetradPalette(int color) {
        List<Integer> palette = new ArrayList<>();

        // Convert the given color to HSV
        float[] hsv = new float[3];
        Color.RGBtoHSB((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, hsv);

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
        palette.add(color1.getRGB());
        palette.add(color2.getRGB());
        palette.add(color3.getRGB());
        palette.add(color4.getRGB());

        return palette;
    }

}
