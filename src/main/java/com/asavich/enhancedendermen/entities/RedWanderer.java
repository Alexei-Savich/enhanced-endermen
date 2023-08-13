package com.asavich.enhancedendermen.entities;

import com.asavich.enhancedendermen.init.EntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class RedWanderer extends EnderMan {

    public RedWanderer(EntityType<RedWanderer> type, Level level) {
        super(type, level);
    }

    public RedWanderer(Level level, double x, double y, double z) {
        this(EntityInit.RED_WANDERER.get(), level);
        setPos(x, y, z);
    }

    public RedWanderer(Level level, BlockPos position) {
        this(level, position.getX(), position.getY(), position.getZ());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RedWandererFreezeWhenLookedAt(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(10, new RedWandererLeaveBlockGoal(this));
        this.goalSelector.addGoal(11, new RedWandererTakeBlockGoal(this));
        this.targetSelector.addGoal(1, new RedWandererLookForPlayerGoal(this, this::isAngryAt));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Endermite.class, true, false));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    public static AttributeSupplier.Builder createRedWandererAttributes() {
        return EnderMan.createAttributes();
    }

    public static boolean canSpawn(EntityType<RedWanderer> entityType, ServerLevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos pos, RandomSource randomSource) {
        return Monster.checkMonsterSpawnRules(entityType, levelAccessor, spawnType, pos, randomSource);
    }

    boolean isLookingAtMe(Player player) {
        Vec3 playerViewVector = player.getViewVector(1.0F).normalize();
        Vec3 vec3 = new Vec3(this.getX() - player.getX(), this.getEyeY() - player.getEyeY(), this.getZ() - player.getZ());
        double length = vec3.length();
        vec3 = vec3.normalize();
        double d1 = playerViewVector.dot(vec3);
        return d1 > 1.0D - 0.025D / length ? player.hasLineOfSight(this) : false;
    }

    boolean teleportTowards(Entity entity) {
        Vec3 vec3 = new Vec3(this.getX() - entity.getX(), this.getY(0.5D) - entity.getEyeY(), this.getZ() - entity.getZ());
        vec3 = vec3.normalize();
        double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3.x * 16.0D;
        double d2 = this.getY() + (double) (this.random.nextInt(16) - 8) - vec3.y * 16.0D;
        double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3.z * 16.0D;
        return this.teleport(d1, d2, d3);
    }

    private boolean teleport(double x, double y, double z) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(x, y, z);

        while(blockpos$mutableblockpos.getY() > this.level().getMinBuildHeight() && !this.level().getBlockState(blockpos$mutableblockpos).blocksMotion()) {
            blockpos$mutableblockpos.move(Direction.DOWN);
        }

        BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos);
        boolean flag = blockstate.blocksMotion();
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
        if (flag && !flag1) {
            net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(this, x, y, z);
            if (event.isCanceled()) return false;
            Vec3 vec3 = this.position();
            boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
            if (flag2) {
                this.level().gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(this));
                if (!this.isSilent()) {
                    this.level().playSound((Player) null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
                    this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }

            return flag2;
        } else {
            return false;
        }
    }

    static class RedWandererFreezeWhenLookedAt extends Goal {
        private final RedWanderer redWanderer;
        @Nullable
        private LivingEntity target;

        public RedWandererFreezeWhenLookedAt(RedWanderer redWanderer) {
            this.redWanderer = redWanderer;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        public boolean canUse() {
            this.target = this.redWanderer.getTarget();
            if (!(this.target instanceof Player)) {
                return false;
            } else {
                double d0 = this.target.distanceToSqr(this.redWanderer);
                return d0 > 256.0D ? false : this.redWanderer.isLookingAtMe((Player) this.target);
            }
        }

        public void start() {
            this.redWanderer.getNavigation().stop();
        }

        public void tick() {
            this.redWanderer.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
        }
    }

    static class RedWandererLeaveBlockGoal extends Goal {
        private final RedWanderer redWanderer;

        public RedWandererLeaveBlockGoal(RedWanderer p_32556_) {
            this.redWanderer = p_32556_;
        }

        public boolean canUse() {
            if (this.redWanderer.getCarriedBlock() == null) {
                return false;
            } else if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.redWanderer.level(), this.redWanderer)) {
                return false;
            } else {
                return this.redWanderer.getRandom().nextInt(reducedTickDelay(2000)) == 0;
            }
        }

        public void tick() {
            RandomSource randomsource = this.redWanderer.getRandom();
            Level level = this.redWanderer.level();
            int i = Mth.floor(this.redWanderer.getX() - 1.0D + randomsource.nextDouble() * 2.0D);
            int j = Mth.floor(this.redWanderer.getY() + randomsource.nextDouble() * 2.0D);
            int k = Mth.floor(this.redWanderer.getZ() - 1.0D + randomsource.nextDouble() * 2.0D);
            BlockPos blockpos = new BlockPos(i, j, k);
            BlockState blockstate = level.getBlockState(blockpos);
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate1 = level.getBlockState(blockpos1);
            BlockState blockstate2 = this.redWanderer.getCarriedBlock();
            if (blockstate2 != null) {
                blockstate2 = Block.updateFromNeighbourShapes(blockstate2, this.redWanderer.level(), blockpos);
                if (this.canPlaceBlock(level, blockpos, blockstate2, blockstate, blockstate1, blockpos1) && !net.minecraftforge.event.ForgeEventFactory.onBlockPlace(redWanderer, net.minecraftforge.common.util.BlockSnapshot.create(level.dimension(), level, blockpos1), net.minecraft.core.Direction.UP)) {
                    level.setBlock(blockpos, blockstate2, 3);
                    level.gameEvent(GameEvent.BLOCK_PLACE, blockpos, GameEvent.Context.of(this.redWanderer, blockstate2));
                    this.redWanderer.setCarriedBlock((BlockState) null);
                }

            }
        }

        private boolean canPlaceBlock(Level p_32559_, BlockPos p_32560_, BlockState p_32561_, BlockState p_32562_, BlockState p_32563_, BlockPos p_32564_) {
            return p_32562_.isAir() && !p_32563_.isAir() && !p_32563_.is(Blocks.BEDROCK) && !p_32563_.is(net.minecraftforge.common.Tags.Blocks.ENDERMAN_PLACE_ON_BLACKLIST) && p_32563_.isCollisionShapeFullBlock(p_32559_, p_32564_) && p_32561_.canSurvive(p_32559_, p_32560_) && p_32559_.getEntities(this.redWanderer, AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(p_32560_))).isEmpty();
        }
    }

    static class RedWandererLookForPlayerGoal extends NearestAttackableTargetGoal<Player> {
        private final RedWanderer redWanderer;
        @Nullable
        private Player pendingTarget;
        private int aggroTime;
        private int teleportTime;
        private final TargetingConditions startAggroTargetConditions;
        private final TargetingConditions continueAggroTargetConditions = TargetingConditions.forCombat().ignoreLineOfSight();

        public RedWandererLookForPlayerGoal(RedWanderer p_32573_, @Nullable Predicate<LivingEntity> p_32574_) {
            super(p_32573_, Player.class, 10, false, false, p_32574_);
            this.redWanderer = p_32573_;
            this.startAggroTargetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector((p_32578_) -> {
                return p_32573_.isLookingAtMe((Player) p_32578_);
            });
        }

        public boolean canUse() {
            this.pendingTarget = this.redWanderer.level().getNearestPlayer(this.startAggroTargetConditions, this.redWanderer);
            return this.pendingTarget != null;
        }

        public void start() {
            this.aggroTime = this.adjustedTickDelay(5);
            this.teleportTime = 0;
            this.redWanderer.setBeingStaredAt();
        }

        public void stop() {
            this.pendingTarget = null;
            super.stop();
        }

        public boolean canContinueToUse() {
            if (this.pendingTarget != null) {
                if (!this.redWanderer.isLookingAtMe(this.pendingTarget)) {
                    return false;
                } else {
                    this.redWanderer.lookAt(this.pendingTarget, 10.0F, 10.0F);
                    return true;
                }
            } else {
                return this.target != null && this.continueAggroTargetConditions.test(this.redWanderer, this.target) ? true : super.canContinueToUse();
            }
        }

        public void tick() {
            if (this.redWanderer.getTarget() == null) {
                super.setTarget((LivingEntity) null);
            }

            if (this.pendingTarget != null) {
                if (--this.aggroTime <= 0) {
                    this.target = this.pendingTarget;
                    this.pendingTarget = null;
                    super.start();
                }
            } else {
                if (this.target != null && !this.redWanderer.isPassenger()) {
                    if (this.redWanderer.isLookingAtMe((Player) this.target)) {
                        if (this.target.distanceToSqr(this.redWanderer) < 16.0D) {
                            this.redWanderer.teleport();
                        }

                        this.teleportTime = 0;
                    } else if (this.target.distanceToSqr(this.redWanderer) > 256.0D && this.teleportTime++ >= this.adjustedTickDelay(30) && this.redWanderer.teleportTowards(this.target)) {
                        this.teleportTime = 0;
                    }
                }

                super.tick();
            }

        }
    }

    static class RedWandererTakeBlockGoal extends Goal {
        private final RedWanderer redWanderer;

        public RedWandererTakeBlockGoal(RedWanderer redWanderer) {
            this.redWanderer = redWanderer;
        }

        public boolean canUse() {
            if (this.redWanderer.getCarriedBlock() != null) {
                return false;
            } else if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.redWanderer.level(), this.redWanderer)) {
                return false;
            } else {
                return this.redWanderer.getRandom().nextInt(reducedTickDelay(20)) == 0;
            }
        }

        public void tick() {
            RandomSource randomsource = this.redWanderer.getRandom();
            Level level = this.redWanderer.level();
            int i = Mth.floor(this.redWanderer.getX() - 2.0D + randomsource.nextDouble() * 4.0D);
            int j = Mth.floor(this.redWanderer.getY() + randomsource.nextDouble() * 3.0D);
            int k = Mth.floor(this.redWanderer.getZ() - 2.0D + randomsource.nextDouble() * 4.0D);
            BlockPos blockpos = new BlockPos(i, j, k);
            BlockState blockstate = level.getBlockState(blockpos);
            Vec3 vec3 = new Vec3((double) this.redWanderer.getBlockX() + 0.5D, (double) j + 0.5D, (double) this.redWanderer.getBlockZ() + 0.5D);
            Vec3 vec31 = new Vec3((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D);
            BlockHitResult blockhitresult = level.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this.redWanderer));
            boolean flag = blockhitresult.getBlockPos().equals(blockpos);
            if (flag) {
                level.removeBlock(blockpos, false);
                level.gameEvent(GameEvent.BLOCK_DESTROY, blockpos, GameEvent.Context.of(this.redWanderer, blockstate));
                this.redWanderer.setCarriedBlock(blockstate.getBlock().defaultBlockState());
            }

        }
    }

}
