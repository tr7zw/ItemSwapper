package dev.tr7zw.itemswapper.manager;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mojang.blaze3d.platform.NativeImage;

import dev.tr7zw.itemswapper.accessor.SpriteContentsAccess;
import dev.tr7zw.itemswapper.util.ColorUtil;
import dev.tr7zw.itemswapper.util.ColorUtil.UnpackedColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockTextureManager {

    private final Map<Block, UnpackedColor[]> colorMap = new HashMap<>();
    private final Set<String> bannedKeywords = new HashSet<>(Arrays.asList("_slab", "_stairs", "_fence", "_gate",
            "_carpet", "_wall", "piston", "cake", "repeater", "comparator", "waxed_", "infested_"));

    public void init() {
        colorMap.clear();
        BuiltInRegistries.BLOCK.entrySet().forEach(e -> {
//            System.out.println(e.getKey() + " " + e.getValue());
            BlockState state = e.getValue().getStateDefinition().any();
            TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRenderer().getBlockModel(state)
                    .getParticleIcon();
//            System.out.println(sprite);
            if (state.canOcclude() && !state.hasBlockEntity()) {
                for (String key : bannedKeywords) {
                    if (e.getKey().location().toString().contains(key)) {
                        return;
                    }
                }
                NativeImage img = ((SpriteContentsAccess) sprite.contents()).getOriginalImage();
                // colorMap.put(e.getValue(),
                // ColorUtil.primaryColorDetection(img.getPixelsRGBA(), 0.3f));
//                UnpackedColor[] colors = ColorUtil.primaryColorDetection(img.getPixelsRGBA(), 0.3f);
//                UnpackedColor c2 = ColorUtil.calculateAverageColor(Arrays.stream(img.getPixelsRGBA()).mapToObj(UnpackedColor::new).toList());
//                System.out.println("Fist Pixel: " + new UnpackedColor(img.getPixelsRGBA()[0]) + " org: " + c2 + " new: " + Arrays.toString(colors));
            }
        });
    }

    public UnpackedColor[] getColor(Block block) {
        if (colorMap.isEmpty()) {
            init();
        }
        return colorMap.get(block);
    }

    // Method to sort blocks by the closeness of their average colors to a target
    // block's average color
    public List<Block> getBlocksByAverageColor(UnpackedColor[] targetAverageColor) {
        if (colorMap.isEmpty()) {
            init();
        }

        // Create a list to store blocks along with their color distances from the
        // target block
        List<Map.Entry<Block, Double>> blockDistances = new ArrayList<>();

        // Calculate the distance between the average color of each block and the target
        // block's average color
        for (Block block : colorMap.keySet()) {
            UnpackedColor[] blockColor = colorMap.get(block);
            double dist = Double.MAX_VALUE;
            for (UnpackedColor target : targetAverageColor) {
                for (UnpackedColor check : blockColor) {
                    dist = Math.min(dist, ColorUtil.colorDistance(target, check));
                }
            }
            blockDistances.add(new AbstractMap.SimpleEntry<>(block, dist));
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

}
