package net.ssum.flains.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.ssum.flains.Flains;

public record GudannaAttackPayload() implements CustomPacketPayload {
    public static final Type<GudannaAttackPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, "gudanna_attack"));

    public static final StreamCodec<ByteBuf, GudannaAttackPayload> CODEC =
            StreamCodec.unit(new GudannaAttackPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}