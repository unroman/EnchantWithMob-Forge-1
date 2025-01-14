package baguchi.enchantwithmob.entity;

import baguchi.enchantwithmob.api.IEnchantCap;
import baguchi.enchantwithmob.registry.MobEnchants;
import baguchi.enchantwithmob.registry.ModSounds;
import baguchi.enchantwithmob.utils.MobEnchantUtils;
import baguchi.enchantwithmob.utils.MobEnchantmentData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class Enchanter extends SpellcasterIllager {
    private LivingEntity enchantTarget;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState castingAnimationState = new AnimationState();

    public int attackAnimationTick;
    public final int attackAnimationLength = 20;
    public final int attackAnimationActionPoint = 10;

    public int castingAnimationTick;
    public final int castingAnimationLength = 72;

    public Enchanter(EntityType<? extends Enchanter> type, Level p_i48551_2_) {
        super(type, p_i48551_2_);
        this.xpReward = 12;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new Enchanter.CastingSpellGoal());
        this.goalSelector.addGoal(1, new AttackGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Creaking.class, 8.0F, 1.2, 1.35));
        this.goalSelector.addGoal(3, new AvoidTargetEntityGoal<>(this, Mob.class, 6.5F, 0.8D, 1.05D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.8D, 1.15D));
        this.goalSelector.addGoal(4, new Enchanter.SpellGoal());
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }


    @Override
    public void baseTick() {
        super.baseTick();
        if (this.level().isClientSide) {
            if (this.attackAnimationTick < this.attackAnimationLength) {
                this.attackAnimationTick++;
            }

            if (this.attackAnimationTick >= this.attackAnimationLength) {
                this.attackAnimationState.stop();
            }

            if (this.castingAnimationTick < this.castingAnimationLength) {
                this.castingAnimationTick++;
            }

            if (this.castingAnimationTick >= this.castingAnimationLength) {
                this.castingAnimationState.stop();
            }
        }
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4) {
            this.attackAnimationState.start(this.tickCount);
            this.idleAnimationState.stop();
            this.castingAnimationState.stop();
            this.attackAnimationTick = 0;
        } else if (p_21375_ == 61) {
            this.castingAnimationState.start(this.tickCount);
            this.idleAnimationState.stop();
            this.attackAnimationState.stop();
            this.castingAnimationTick = 0;
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }
    public static AttributeSupplier.Builder createAttributeMap() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, (double) 0.3F).add(Attributes.MAX_HEALTH, 24.0D).add(Attributes.FOLLOW_RANGE, 24.0D).add(Attributes.ATTACK_DAMAGE, 2.0F);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            this.setupAnimationStates();
        }
    }

    private void setupAnimationStates() {
        if (this.attackAnimationState.isStarted() || this.castingAnimationState.isStarted() || this.hurtTime > 0 || this.walkAnimation.isMoving()) {
            this.idleAnimationState.stop();
        } else {
            this.idleAnimationState.startIfStopped(this.tickCount);
        }
    }

    private void setEnchantTarget(@Nullable LivingEntity enchantTargetIn) {
        this.enchantTarget = enchantTargetIn;
    }

    @Nullable
    public LivingEntity getEnchantTarget() {
        return enchantTarget;
    }

    @Override
    public boolean canBeLeader() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.ENCHANTER_IDLE.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ENCHANTER_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.ENCHANTER_HURT.get();
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return ModSounds.ENCHANTER_SPELL.get();
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.ENCHANTER_IDLE.get();
    }

    @Override
    public void applyRaidBuffs(ServerLevel serverLevel, int p_213660_1_, boolean p_213660_2_) {
        Raid raid = this.getCurrentRaid();
        boolean flag = this.random.nextFloat() <= raid.getEnchantOdds() + 0.1F;
        if (flag) {
            if (this instanceof IEnchantCap cap) {
                MobEnchantUtils.addEnchantmentToEntity(this, cap, new MobEnchantmentData(MobEnchants.PROTECTION.get(), 2));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isCastingSpell()) {
            return AbstractIllager.IllagerArmPose.SPELLCASTING;
        } else {
            return AbstractIllager.IllagerArmPose.CROSSED;
        }
    }

    class CastingSpellGoal extends SpellcasterIllager.SpellcasterCastingSpellGoal {
        private CastingSpellGoal() {
            super();
        }

        @Override
        public void tick() {
            if (Enchanter.this.isCastingSpell() && Enchanter.this.getEnchantTarget() != null) {
                Enchanter.this.getLookControl().setLookAt(Enchanter.this.getEnchantTarget(), (float) Enchanter.this.getMaxHeadYRot(), (float) Enchanter.this.getMaxHeadXRot());
            } else if (Enchanter.this.isCastingSpell() && Enchanter.this.getTarget() != null) {
                Enchanter.this.getLookControl().setLookAt(Enchanter.this.getTarget(), (float) Enchanter.this.getMaxHeadYRot(), (float) Enchanter.this.getMaxHeadXRot());
            }
        }
    }


    public class SpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
        private final Predicate<LivingEntity> fillter = (entity) -> {
            return !(entity instanceof Enchanter) && entity instanceof IEnchantCap enchantCap && !enchantCap.getEnchantCap().hasEnchant();
        };

        private final Predicate<LivingEntity> enchanted_fillter = (entity) -> {
            return !(entity instanceof Enchanter) && entity instanceof IEnchantCap enchantCap && enchantCap.getEnchantCap().hasEnchant();
        };

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            if (Enchanter.this.getTarget() == null) {
                return false;
            } else if (Enchanter.this.isCastingSpell()) {
                return false;
            } else if (Enchanter.this.tickCount < this.nextAttackTickCount) {
                return false;
            } else {
                List<LivingEntity> list = Enchanter.this.level().getEntitiesOfClass(LivingEntity.class, Enchanter.this.getBoundingBox().expandTowards(16.0D, 8.0D, 16.0D), this.fillter);
                if (list.isEmpty()) {
                    return false;
                } else {
                    List<LivingEntity> enchanted_list = Enchanter.this.level().getEntitiesOfClass(LivingEntity.class, Enchanter.this.getBoundingBox().expandTowards(16.0D, 8.0D, 16.0D), this.enchanted_fillter);

                    //set enchant limit
                    if (enchanted_list.size() < 5) {
                        LivingEntity target = list.get(Enchanter.this.random.nextInt(list.size()));
                        if (target != Enchanter.this.getTarget() && target != Enchanter.this && target.isAlliedTo(Enchanter.this) && Enchanter.this.isAlliedTo(target) && (target.getTeam() == Enchanter.this.getTeam() || target.getType().is(EntityTypeTags.ILLAGER_FRIENDS) && target.getTeam() == null)) {
                            Enchanter.this.setEnchantTarget(target);
                            Enchanter.this.level().broadcastEntityEvent(Enchanter.this, (byte) 61);
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean canContinueToUse() {
            return Enchanter.this.getEnchantTarget() != null && Enchanter.this.getEnchantTarget() != Enchanter.this.getTarget() && this.attackWarmupDelay > 0;
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void stop() {
            super.stop();
            Enchanter.this.setEnchantTarget(null);
        }

        protected void performSpellCasting() {
            LivingEntity entity = Enchanter.this.getEnchantTarget();
            if (entity != null && entity.isAlive()) {
                if (entity instanceof IEnchantCap cap) {
                    float difficulty = entity.level().getCurrentDifficultyAt(entity.blockPosition()).getEffectiveDifficulty();
                    MobEnchantUtils.addUnstableRandomEnchantmentToEntity(entity, Enchanter.this, cap, entity.getRandom(), (int) (5 + difficulty * 2), false, false);
                }
            }
        }

        protected int getCastWarmupTime() {
            return 40;
        }

        protected int getCastingTime() {
            return 60;
        }

        protected int getCastingInterval() {
            return 200;
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.BOOK_PAGE_TURN;
        }

        protected SpellcasterIllager.IllagerSpell getSpell() {
            return SpellcasterIllager.IllagerSpell.WOLOLO;
        }
    }

    class AvoidTargetEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
        private final Enchanter enchanter;

        public AvoidTargetEntityGoal(Enchanter enchanterIn, Class<T> entityClassToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
            super(enchanterIn, entityClassToAvoidIn, avoidDistanceIn, farSpeedIn, nearSpeedIn);
            this.enchanter = enchanterIn;
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            if (super.canUse() && this.toAvoid == this.enchanter.getTarget()) {
                return this.enchanter.getTarget() != null;
            } else {
                return false;
            }
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            Enchanter.this.setTarget((LivingEntity) null);
            super.start();
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            Enchanter.this.setTarget((LivingEntity) null);
            super.tick();
        }
    }

    static class AttackGoal extends Goal {
        private final Enchanter enchanter;
        private int tick;

        AttackGoal(Enchanter enchanter) {
            this.enchanter = enchanter;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.enchanter.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else {
                return this.canPerformAttack(livingentity);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.tick < 20;
        }

        @Override
        public void start() {
            super.start();
            this.enchanter.level().broadcastEntityEvent(this.enchanter, (byte) 4);
        }

        @Override
        public void stop() {
            super.stop();
            this.tick = 0;
        }

        @Override
        public void tick() {
            super.tick();
            LivingEntity livingentity = this.enchanter.getTarget();

            if (livingentity != null && livingentity.isAlive() && livingentity.level() instanceof ServerLevel serverLevel) {
                this.enchanter.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                if (this.canPerformAttack(livingentity)) {
                    if (this.tick == this.enchanter.attackAnimationActionPoint) {
                        this.enchanter.swing(InteractionHand.MAIN_HAND);
                        this.enchanter.doHurtTarget(serverLevel, livingentity);
                        this.enchanter.playSound(ModSounds.ENCHANTER_ATTACK.get());
                    }
                    this.enchanter.getNavigation().stop();
                }
            }
            ++this.tick;
        }

        protected boolean canPerformAttack(LivingEntity p_301299_) {
            return this.enchanter.isWithinMeleeAttackRange(p_301299_) && this.enchanter.getSensing().hasLineOfSight(p_301299_);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }


}
