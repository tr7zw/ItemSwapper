package dev.tr7zw.itemswapper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.blaze3d.platform.NativeImage;

import dev.tr7zw.itemswapper.accessor.SpriteContentsAccess;
import net.minecraft.client.renderer.texture.SpriteContents;

@Mixin(SpriteContents.class)
public class SpriteContentsMixin implements SpriteContentsAccess {

    @Shadow
    private NativeImage originalImage;
    
    @Override
    public NativeImage getOriginalImage() {
        return originalImage;
    }

}
