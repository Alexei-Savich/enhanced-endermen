package com.asavich.enhancedendermen.entities;

import com.asavich.enhancedendermen.init.BlockInit;
import com.asavich.enhancedendermen.init.EntityInit;
import com.mojang.logging.LogUtils;
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
import net.minecraft.world.entity.item.PrimedTnt;
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
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.asavich.enhancedendermen.util.LogicUtils.getAllBlocksInSquareRadius;

public class RedWanderer extends EnderMan {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final RandomSource random = RandomSource.create();
    private Entity carriedEntity = null;

    public RedWanderer(EntityType<RedWanderer> type, Level level) {
        super(type, level);
        if (random.nextInt(100) > 97) {
            this.setCarriedBlock(Blocks.TNT.defaultBlockState());
        }
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
        this.goalSelector.addGoal(1, new RedWandererTeleportHostileMobTowardsPlayer(this));
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
        return d1 > 1.0D - 0.025D / length && player.hasLineOfSight(this);
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

        while (blockpos$mutableblockpos.getY() > this.level().getMinBuildHeight() && !this.level().getBlockState(blockpos$mutableblockpos).blocksMotion()) {
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
                    this.level().playSound(null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
                    this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }

            return flag2;
        } else {
            return false;
        }
    }

    static class RedWandererTeleportHostileMobTowardsPlayer extends Goal {
        private final RedWanderer redWanderer;
        @Nullable
        private LivingEntity target;
        private List<Monster> mobs = new ArrayList<>();
        private final TargetingConditions mobsTargeting = TargetingConditions.forNonCombat().range(64.0D).ignoreLineOfSight();
        private Monster currMob;

        public RedWandererTeleportHostileMobTowardsPlayer(RedWanderer redWanderer) {
            this.redWanderer = redWanderer;
        }

        public boolean canUse() {
            this.target = this.redWanderer.getTarget();
            return target != null;
        }

        public void start() {
            mobs = getMobs();
            LOGGER.debug("Located {} mobs: {}", mobs.size(), mobs);
        }

        public void tick() {
            if (mobs.isEmpty()) {
                mobs = getMobs();
            }
            if (redWanderer.carriedEntity != null) {
                boolean couldTpToTarget = redWanderer.teleportTowards(target);
                if(couldTpToTarget){
                    LOGGER.debug("Teleported with {} to TARGET", redWanderer.carriedEntity);
                    redWanderer.carriedEntity.teleportTo(target.getX(), target.getY(), target.getZ());
                } else {
                    redWanderer.carriedEntity = null;
                    currMob = null;
                }
            }
            if (currMob == null) {
                currMob = mobs.get(0);
                mobs.remove(0);
                LOGGER.debug("Current mob is {}", currMob);
            }
            if (redWanderer.distanceToSqr(currMob) > 4) {
                boolean successTp = redWanderer.teleport(currMob.getX(), currMob.getY(), currMob.getZ());
                if (successTp) {
                    LOGGER.debug("Teleported to take {}", currMob);
                    redWanderer.carriedEntity = currMob;
                } else {
                    currMob = null;
                }
            }
        }

        private List<Monster> getMobs() {
            return this.redWanderer.level()
                    .getNearbyEntities(Monster.class, mobsTargeting, this.redWanderer, this.redWanderer.getBoundingBox().inflate(128d, 64d, 128d))
                    .stream().filter(monster -> !monster.getClass().equals(RedWanderer.class)).collect(Collectors.toList());
        }
    }

    static class RedWandererLeaveBlockGoal extends Goal {
        private final RedWanderer redWanderer;

        public RedWandererLeaveBlockGoal(RedWanderer redWanderer) {
            this.redWanderer = redWanderer;
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
            BlockPos targetPos = new BlockPos(i, j, k);
            BlockState targetBlock = level.getBlockState(targetPos);
            BlockPos belowTargetPos = targetPos.below();
            BlockState belowTarget = level.getBlockState(belowTargetPos);
            BlockState carried = this.redWanderer.getCarriedBlock();
            if (carried != null) {
                if (getAllBlocksInSquareRadius(targetPos, 20, level).contains(BlockInit.REPELLER)) {
                    LOGGER.debug("Blocked placing a block because of REPELLER at pos: {}", targetPos);
                    return;
                }
                carried = Block.updateFromNeighbourShapes(carried, this.redWanderer.level(), targetPos);
                if (this.canPlaceBlock(level, targetPos, carried, targetBlock, belowTarget, belowTargetPos) && !net.minecraftforge.event.ForgeEventFactory.onBlockPlace(redWanderer, net.minecraftforge.common.util.BlockSnapshot.create(level.dimension(), level, belowTargetPos), net.minecraft.core.Direction.UP)) {
                    if (carried.equals(Blocks.TNT.defaultBlockState())) {
                        PrimedTnt summonedTnt = new PrimedTnt(level, targetPos.getX(), targetPos.getY(), targetPos.getZ(), this.redWanderer);
                        level.addFreshEntity(summonedTnt);
                        boolean teleportSuccess = this.redWanderer.teleport();
                        int counter = 0;
                        LOGGER.debug("Attempt #{} to teleport after placing a TNT: {}", counter + 1, teleportSuccess);
                        while (!teleportSuccess && counter < 5) {
                            teleportSuccess = this.redWanderer.teleport();
                            counter++;
                            LOGGER.debug("Attempt #{} to teleport after placing a TNT: {}", counter + 1, teleportSuccess);
                        }
                    } else {
                        level.setBlock(targetPos, carried, 3);
                        level.gameEvent(GameEvent.BLOCK_PLACE, targetPos, GameEvent.Context.of(this.redWanderer, carried));
                    }
                    this.redWanderer.setCarriedBlock(null);
                }

            }
        }

        private boolean canPlaceBlock(Level level, BlockPos targetPos, BlockState carriedBlock, BlockState targetBlock, BlockState belowTargetBlock, BlockPos belowTargetPos) {
            return targetBlock.isAir() && !belowTargetBlock.isAir() && !belowTargetBlock.is(net.minecraftforge.common.Tags.Blocks.ENDERMAN_PLACE_ON_BLACKLIST) && belowTargetBlock.isCollisionShapeFullBlock(level, belowTargetPos) && carriedBlock.canSurvive(level, targetPos) && level.getEntities(this.redWanderer, AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(targetPos))).isEmpty();
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

        public RedWandererLookForPlayerGoal(RedWanderer redWanderer, @Nullable Predicate<LivingEntity> target) {
            super(redWanderer, Player.class, 10, false, false, target);
            this.redWanderer = redWanderer;
            this.startAggroTargetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector((player) -> redWanderer.isLookingAtMe((Player) player));
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
                return this.target != null && this.continueAggroTargetConditions.test(this.redWanderer, this.target) || super.canContinueToUse();
            }
        }

        public void tick() {
            if (this.redWanderer.getTarget() == null) {
                super.setTarget(null);
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
                if (getAllBlocksInSquareRadius(blockpos, 20, level).contains(BlockInit.REPELLER)) {
                    LOGGER.debug("Blocked taking a block because of REPELLER at pos: {}", blockpos);
                    return;
                }
                level.removeBlock(blockpos, false);
                level.gameEvent(GameEvent.BLOCK_DESTROY, blockpos, GameEvent.Context.of(this.redWanderer, blockstate));
                this.redWanderer.setCarriedBlock(blockstate.getBlock().defaultBlockState());
            }

        }
    }

}
