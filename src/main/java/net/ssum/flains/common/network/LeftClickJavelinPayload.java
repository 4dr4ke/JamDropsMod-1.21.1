package net.ssum.flains.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.ssum.flains.Flains;

public class LeftClickJavelinPayload implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<LeftClickJavelinPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "left_click_javelin"));

    public static final LeftClickJavelinPayload INSTANCE = new LeftClickJavelinPayload();

    public static final StreamCodec<FriendlyByteBuf, LeftClickJavelinPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
