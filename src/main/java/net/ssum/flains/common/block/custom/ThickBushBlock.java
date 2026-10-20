package net.ssum.flains.common.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ThickBushBlock extends BushBlock {
    private static final MapCodec<ThickBushBlock> CODEC = simpleCodec(ThickBushBlock::new);
    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 11.5, 11.5, 11.0);

    public ThickBushBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity && entity.getType() != EntityType.FOX && entity.getType() != EntityType.BEE) {
            Vec3 motion = entity.getDeltaMovement();
            entity.setDeltaMovement(motion.x * 0.85d, motion.y, motion.z * 0.85d);
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canBeReplaced(BlockState state, Fluid fluid) {
        return true;
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }
}
