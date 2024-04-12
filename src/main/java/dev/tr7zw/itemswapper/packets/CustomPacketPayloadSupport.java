package dev.tr7zw.itemswapper.packets;

// spotless:off 
//#if MC >= 12005
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
//#endif
//spotless:on

//spotless:off 
//#if MC >= 12005
public interface CustomPacketPayloadSupport extends CustomPacketPayload {
  //#else
   //$$ public interface CustomPacketPayloadSupport {
  //#endif
  //spotless:on

    // spotless:off 
  //#if MC >= 12005
    
    public ResourceLocation id();

    public default Type<? extends CustomPacketPayload> type() {
        return new Type<CustomPacketPayload>(id());
    }

    public void write(FriendlyByteBuf paramFriendlyByteBuf);

  //#endif
  //spotless:on

}
