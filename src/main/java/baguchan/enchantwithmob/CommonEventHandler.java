package baguchan.enchantwithmob;

import baguchan.enchantwithmob.api.IEnchantCap;
import baguchan.enchantwithmob.api.IEnchantVisual;
import baguchan.enchantwithmob.capability.MobEnchantHandler;
import baguchan.enchantwithmob.item.mobenchant.ItemMobEnchantments;
import baguchan.enchantwithmob.message.MobEnchantedMessage;
import baguchan.enchantwithmob.mobenchant.MobEnchant;
import baguchan.enchantwithmob.registry.MobEnchants;
import baguchan.enchantwithmob.registry.ModItems;
import baguchan.enchantwithmob.utils.MobEnchantCombatRules;
import baguchan.enchantwithmob.utils.MobEnchantUtils;
import baguchan.enchantwithmob.utils.MobEnchantmentData;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static net.minecraft.world.inventory.AnvilMenu.calculateIncreasedRepairCost;

@EventBusSubscriber(modid = EnchantWithMob.MODID)
public class CommonEventHandler {

    /*
     * add Enchant Visual
     */
    @SubscribeEvent
    public static void onTraceableEntitySpawn(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof IEnchantVisual enchantVisual && event.getEntity() instanceof TraceableEntity traceableEntity) {
            if (traceableEntity.getOwner() instanceof IEnchantCap enchantCap) {
                enchantVisual.setEnchantVisual(enchantCap.getEnchantCap().hasEnchant());
            }
        }
    }

    /*
     * this event handle the Ender dragon mob enchant
     */
    @SubscribeEvent
    public static void onEnderDragonSpawn(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof IEnchantCap cap && event.getEntity() instanceof EnderDragon livingEntity) {
            LevelAccessor world = event.getLevel();
            if (!world.isClientSide()) {
                if (!cap.getEnchantCap().hasEnchant()) {
                    if (isSpawnAlwayEnchantableAncientEntity(livingEntity)) {
                        int i = 0;
                        float difficultScale = world.getCurrentDifficultyAt(livingEntity.blockPosition()).getEffectiveDifficulty() - 0.2F;
                        switch (world.getDifficulty()) {
                            case EASY:
                                i = (int) Mth.clamp((5 + world.getRandom().nextInt(10)) * difficultScale, 1, 30);

                                MobEnchantUtils.addRandomEnchantmentToEntity(livingEntity, cap, world.getRandom(), i, true, false, true);
                                break;
                            case NORMAL:
                                i = (int) Mth.clamp((5 + world.getRandom().nextInt(15)) * difficultScale, 1, 60);

                                MobEnchantUtils.addRandomEnchantmentToEntity(livingEntity, cap, world.getRandom(), i, true, false, true);
                                break;
                            case HARD:
                                i = (int) Mth.clamp((5 + world.getRandom().nextInt(20)) * difficultScale, 1, 100);

                                MobEnchantUtils.addRandomEnchantmentToEntity(livingEntity, cap, world.getRandom(), i, true, false, true);
                                break;
                        }
                        livingEntity.setHealth(livingEntity.getMaxHealth());
                    }

                    // On add MobEnchant Alway Enchantable Mob
                    if (isSpawnAlwayEnchantableEntity(livingEntity)) {
                        int i = 0;
                        float difficultScale = world.getCurrentDifficultyAt(livingEntity.blockPosition()).getEffectiveDifficulty() - 0.2F;
                        switch (world.getDifficulty()) {
                            case EASY:
                                i = (int) Mth.clamp((5 + world.getRandom().nextInt(5)) * difficultScale, 1, 20);

                                MobEnchantUtils.addRandomEnchantmentToEntity(livingEntity, cap, world.getRandom(), i, true, false, false);
                                break;
                            case NORMAL:
                                i = (int) Mth.clamp((5 + world.getRandom().nextInt(5)) * difficultScale, 1, 40);

                                MobEnchantUtils.addRandomEnchantmentToEntity(livingEntity, cap, world.getRandom(), i, true, false, false);
                                break;
                            case HARD:
                                i = (int) Mth.clamp((5 + world.getRandom().nextInt(10)) * difficultScale, 1, 50);

                                MobEnchantUtils.addRandomEnchantmentToEntity(livingEntity, cap, world.getRandom(), i, true, false, false);
                                break;
                        }

                        livingEntity.setHealth(livingEntity.getMaxHealth());
                    }
                }
            }
        }
    }

    /*
     * handle the Normal Entity Mob Enchant
     */
    @SubscribeEvent
    public static void onSpawnEntity(MobSpawnEvent.FinalizeSpawn event) {
        if (event.getEntity() instanceof IEnchantCap cap) {
            LevelAccessor world = event.getLevel();
            if (!world.isClientSide()) {
                LivingEntity livingEntity = event.getEntity();

                if (isSpawnAlwayEnchantableAncientEntity(livingEntity)) {
                    int i = 0;
                    float difficultScale = world.getCurrentDifficultyAt(livingEntity.blockPosition()).getEffectiveDifficulty() - 0.2F;
                    switch (world.getDifficulty()) {
                        case EASY:
                            i = (int) Mth.clamp((5 + world.getRandom().nextInt(10)) * difficultScale, 1, 30);

                            MobEnchantUtils.addRandomEnchantmentToEntity(livingEntity, cap, world.getRandom(), i, true, false, true);
                            break;
                        case NORMAL:
                            i = (int) Mth.clamp((5 + world.getRandom().nextInt(15)) * difficultScale, 1, 60);

                            MobEnchantUtils.addRandomEnchantmentToEntity(livingEntity, cap, world.getRandom(), i, true, false, true);
                            break;
                        case HARD:
                            i = (int) Mth.clamp((5 + world.getRandom().nextInt(20)) * difficultScale, 1, 100);

                            MobEnchantUtils.addRandomEnchantmentToEntity(livingEntity, cap, world.getRandom(), i, true, false, true);
                            break;
                    }

                    livingEntity.setHealth(livingEntity.getMaxHealth());
                }

                // On add MobEnchant Alway Enchantable Mob
                if (isSpawnAlwayEnchantableEntity(livingEntity)) {
                    int i = 0;
                    float difficultScale = world.getCurrentDifficultyAt(livingEntity.blockPosition()).getEffectiveDifficulty() - 0.2F;
                    switch (world.getDifficulty()) {
                        case EASY:
                            i = (int) Mth.clamp((5 + world.getRandom().nextInt(5)) * difficultScale, 1, 20);

                            MobEnchantUtils.addRandomEnchantmentToEntity(livingEntity, cap, world.getRandom(), i, true, false, false);
                            break;
                        case NORMAL:
                            i = (int) Mth.clamp((5 + world.getRandom().nextInt(5)) * difficultScale, 1, 40);

                            MobEnchantUtils.addRandomEnchantmentToEntity(livingEntity, cap, world.getRandom(), i, true, false, false);
                            break;
                        case HARD:
                            i = (int) Mth.clamp((5 + world.getRandom().nextInt(10)) * difficultScale, 1, 50);

                            MobEnchantUtils.addRandomEnchantmentToEntity(livingEntity, cap, world.getRandom(), i, true, false, false);
                            break;
                    }

                    livingEntity.setHealth(livingEntity.getMaxHealth());
                }


                if (EnchantConfig.COMMON.naturalSpawnEnchantedMob.get() && isSpawnEnchantableEntity(event.getEntity())) {

                    if (!(livingEntity instanceof Animal) && !(livingEntity instanceof WaterAnimal) || EnchantConfig.COMMON.spawnEnchantedAnimal.get()) {
                        if (event.getSpawnType() != MobSpawnType.BREEDING && event.getSpawnType() != MobSpawnType.CONVERSION && event.getSpawnType() != MobSpawnType.STRUCTURE && event.getSpawnType() != MobSpawnType.MOB_SUMMONED) {
                            if (world.getRandom().nextFloat() < (EnchantConfig.COMMON.difficultyBasePercent.get() * world.getDifficulty().getId()) + world.getCurrentDifficultyAt(livingEntity.blockPosition()).getEffectiveDifficulty() * EnchantConfig.COMMON.effectiveBasePercent.get()) {
                                if (!world.isClientSide()) {
                                    int i = 0;
                                    float difficultScale = world.getCurrentDifficultyAt(livingEntity.blockPosition()).getEffectiveDifficulty() - 0.2F;
                                    switch (world.getDifficulty()) {
                                        case EASY:
                                            i = (int) Mth.clamp((5 + world.getRandom().nextInt(5)) * difficultScale, 1, 20);

                                            MobEnchantUtils.addRandomEnchantmentToEntity(livingEntity, cap, world.getRandom(), i, true, true, false);
                                            break;
                                        case NORMAL:
                                            i = (int) Mth.clamp((5 + world.getRandom().nextInt(5)) * difficultScale, 1, 40);

                                            MobEnchantUtils.addRandomEnchantmentToEntity(livingEntity, cap, world.getRandom(), i, true, true, false);
                                            break;
                                        case HARD:
                                            i = (int) Mth.clamp((5 + world.getRandom().nextInt(10)) * difficultScale, 1, 50);

                                            MobEnchantUtils.addRandomEnchantmentToEntity(livingEntity, cap, world.getRandom(), i, true, true, false);
                                            break;
                                    }

                                    livingEntity.setHealth(livingEntity.getMaxHealth());
                                }
                            }
                        }

                        if (event.getSpawnType() == MobSpawnType.TRIAL_SPAWNER) {
                            if (world.getRandom().nextFloat() < 1.0) {
                                MobEnchantUtils.addEnchantmentToEntity(livingEntity, cap, new MobEnchantmentData(MobEnchants.WIND.get(), 1));
                            }
                        }

                    }
                }
            }
        }
    }

    private static boolean isSpawnAlwayEnchantableEntity(Entity entity) {
        return !(entity instanceof Player) && !(entity instanceof ArmorStand) && !(entity instanceof Boat) && !(entity instanceof Minecart) && EnchantConfig.COMMON.ALWAY_ENCHANTABLE_MOBS.get().contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
    }

    private static boolean isSpawnAlwayEnchantableAncientEntity(Entity entity) {
        return !(entity instanceof Player) && !(entity instanceof ArmorStand) && !(entity instanceof Boat) && !(entity instanceof Minecart) && EnchantConfig.COMMON.ALWAY_ENCHANTABLE_ANCIENT_MOBS.get().contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
    }

    private static boolean isSpawnEnchantableEntity(Entity entity) {
        return !(entity instanceof Player) && !(entity instanceof ArmorStand) && !(entity instanceof Boat) && !(entity instanceof Minecart) && !EnchantConfig.COMMON.ENCHANT_ON_SPAWN_EXCLUSION_MOBS.get().contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
    }

    @SubscribeEvent
    public static void onUpdateEnchanted(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();

        if (entity instanceof IEnchantCap cap && entity instanceof LivingEntity livingEntity) {
            for (MobEnchantHandler enchantHandler : cap.getEnchantCap().getMobEnchants()) {
                enchantHandler.getMobEnchant().tick(livingEntity, enchantHandler.getEnchantLevel());
            }

        }
        ;
    }


    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        LivingEntity livingEntity = event.getEntity();

        if (event.getSource().getEntity() instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) event.getSource().getEntity();

            if (attacker instanceof IEnchantCap cap) {
                if (cap.getEnchantCap().hasEnchant()) {
                    int mobEnchantLevel = MobEnchantUtils.getMobEnchantLevelFromHandler(cap.getEnchantCap().getMobEnchants(), MobEnchants.STRONG.get());
                    int mobEnchantSize = cap.getEnchantCap().getMobEnchants().size();

                    //make snowman stronger
                    if (!livingEntity.isDamageSourceBlocked(event.getSource()) && event.getAmount() == 0) {
                        event.setAmount(MobEnchantCombatRules.getDamageAddition(1, mobEnchantLevel, mobEnchantSize));
                    } else if (event.getAmount() > 0) {
                        event.setAmount(MobEnchantCombatRules.getDamageAddition(event.getAmount(), mobEnchantLevel, mobEnchantSize));
                    }
                }

                if (cap.getEnchantCap().hasEnchant() && MobEnchantUtils.findMobEnchantFromHandler(cap.getEnchantCap().getMobEnchants(), MobEnchants.POISON.get())) {
                    int i = MobEnchantUtils.getMobEnchantLevelFromHandler(cap.getEnchantCap().getMobEnchants(), MobEnchants.POISON.get());

                    if (event.getAmount() > 0) {
                        if (attacker.getRandom().nextFloat() < i * 0.125F) {
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * i, 0), attacker);
                        }
                    }
                }


            }
        }

        if (livingEntity instanceof IEnchantCap cap) {
            if (!event.getSource().is(DamageTypeTags.BYPASSES_EFFECTS) && cap.getEnchantCap().hasEnchant()) {
                int mobEnchantLevel = MobEnchantUtils.getMobEnchantLevelFromHandler(cap.getEnchantCap().getMobEnchants(), MobEnchants.PROTECTION.get());
                int mobEnchantSize = cap.getEnchantCap().getMobEnchants().size();

                event.setAmount(MobEnchantCombatRules.getDamageReduction(event.getAmount(), mobEnchantLevel, mobEnchantSize));
            }
            if (event.getSource().getDirectEntity() != null) {
               if (cap.getEnchantCap().hasEnchant()) {
                   int i = MobEnchantUtils.getMobEnchantLevelFromHandler(cap.getEnchantCap().getMobEnchants(), MobEnchants.THORN.get());

                   if (event.getSource().getDirectEntity() instanceof LivingEntity && !event.getSource().is(DamageTypeTags.IS_PROJECTILE) && !event.getSource().is(DamageTypes.THORNS) && livingEntity.getRandom().nextFloat() < i * 0.1F) {
                       LivingEntity attacker = (LivingEntity) event.getSource().getDirectEntity();

                       attacker.hurt(livingEntity.damageSources().thorns(livingEntity), MobEnchantCombatRules.getThornDamage(event.getAmount(), cap.getEnchantCap()));
                   }
               }
            }
        }
    }




    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.EntityInteract event) {
        ItemStack stack = event.getItemStack();
        Entity entityTarget = event.getTarget();

        if (!(entityTarget instanceof Player)) {
            if (stack.getItem() == ModItems.MOB_ENCHANT_BOOK.get() && !event.getEntity().getCooldowns().isOnCooldown(stack.getItem())) {
                if (entityTarget instanceof LivingEntity) {
                    LivingEntity target = (LivingEntity) entityTarget;
                    if (MobEnchantUtils.hasMobEnchant(stack)) {

                        if (target instanceof IEnchantCap cap) {
                            boolean flag = MobEnchantUtils.addItemMobEnchantToEntity(stack, target, event.getEntity(), cap);

                            if (flag) {
                                event.getEntity().playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0F, 1.0F);

                                stack.hurtAndBreak(1, event.getEntity(), LivingEntity.getSlotForHand(event.getHand()));

                                event.getEntity().getCooldowns().addCooldown(stack.getItem(), 60);

                                event.setCancellationResult(InteractionResult.SUCCESS);
                                event.setCanceled(true);
                            } else {
                                event.getEntity().displayClientMessage(Component.translatable("enchantwithmob.cannot.enchant"), true);
                                event.getEntity().getCooldowns().addCooldown(stack.getItem(), 20);
                                event.setCancellationResult(InteractionResult.FAIL);
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            }

            if (stack.getItem() == ModItems.MOB_UNENCHANT_BOOK.get() && !event.getEntity().getCooldowns().isOnCooldown(stack.getItem())) {
                if (entityTarget instanceof LivingEntity) {
                    LivingEntity target = (LivingEntity) entityTarget;

                    if (target instanceof IEnchantCap cap) {
                        MobEnchantUtils.removeMobEnchantToEntity(target, cap);
                        event.getEntity().playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0F, 1.0F);

                        stack.hurtAndBreak(1, event.getEntity(), LivingEntity.getSlotForHand(event.getHand()));

                        event.getEntity().getCooldowns().addCooldown(stack.getItem(), 80);

                        event.setCancellationResult(InteractionResult.SUCCESS);
                        event.setCanceled(true);
                    }
                }
            }
        }
        }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack stack3 = event.getLeft();
        int i = 0;
        long j = 0L;
        int k = 0;
        if (!stack3.isEmpty() && MobEnchantUtils.canStoreEnchantments(stack3)) {
            ItemStack stack1 = stack3.copy();
            ItemStack stack2 = event.getRight();
            ItemMobEnchantments.Mutable itemenchantments$mutable = new ItemMobEnchantments.Mutable(MobEnchantUtils.getEnchantmentsForCrafting(stack1));
            j += (long) stack3.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0)).intValue()
                    + (long) stack2.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0)).intValue();
            boolean flag = false;
            if (!stack2.isEmpty()) {
                flag = stack2.has(DataComponents.STORED_ENCHANTMENTS);
                if (stack1.isDamageableItem() && stack1.getItem().isValidRepairItem(stack3, stack2)) {
                    int l2 = Math.min(stack1.getDamageValue(), stack1.getMaxDamage() / 4);
                    if (l2 <= 0) {
                        event.setOutput(ItemStack.EMPTY);
                        event.setCost(0);
                        return;
                    }

                    int j3;
                    for (j3 = 0; l2 > 0 && j3 < stack2.getCount(); j3++) {
                        int k3 = stack1.getDamageValue() - l2;
                        stack1.setDamageValue(k3);
                        i++;
                        l2 = Math.min(stack1.getDamageValue(), stack1.getMaxDamage() / 4);
                    }

                    event.setMaterialCost(j3);
                } else {
                    if (!flag && (!stack1.is(stack2.getItem()) || !stack1.isDamageableItem())) {
                        event.setOutput(ItemStack.EMPTY);
                        event.setCost(0);
                        return;
                    }

                    if (stack1.isDamageableItem() && !flag) {
                        int l = stack3.getMaxDamage() - stack3.getDamageValue();
                        int i1 = stack2.getMaxDamage() - stack2.getDamageValue();
                        int j1 = i1 + stack1.getMaxDamage() * 12 / 100;
                        int k1 = l + j1;
                        int l1 = stack1.getMaxDamage() - k1;
                        if (l1 < 0) {
                            l1 = 0;
                        }

                        if (l1 < stack1.getDamageValue()) {
                            stack1.setDamageValue(l1);
                            i += 2;
                        }
                    }

                    ItemMobEnchantments itemenchantments = MobEnchantUtils.getEnchantmentsForCrafting(stack2);
                    boolean flag2 = false;
                    boolean flag3 = false;

                    for (Object2IntMap.Entry<Holder<MobEnchant>> entry : itemenchantments.entrySet()) {
                        Holder<MobEnchant> holder = entry.getKey();
                        MobEnchant enchantment = holder.value();
                        int i2 = itemenchantments$mutable.getLevel(enchantment);
                        int j2 = entry.getIntValue();
                        j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                        boolean flag1 = true;
                        if (event.getPlayer().getAbilities().instabuild || stack3.is(ModItems.MOB_ENCHANT_BOOK.get()) || stack3.is(ModItems.ENCHANTERS_BOOK.get())) {
                            flag1 = true;
                        }

                        for (Holder<MobEnchant> holder1 : itemenchantments$mutable.keySet()) {
                            if (!holder1.equals(holder) && !enchantment.isCompatibleWith(holder1.value())) {
                                flag1 = false;
                                i++;
                            }
                        }

                        if (!flag1) {
                            flag3 = true;
                        } else {
                            flag2 = true;
                            if (j2 > enchantment.getMaxLevel()) {
                                j2 = enchantment.getMaxLevel();
                            }

                            itemenchantments$mutable.set(enchantment, j2);
                            int l3 = enchantment.getAnvilCost();
                            if (flag) {
                                l3 = Math.max(1, l3 / 2);
                            }

                            i += l3 * j2;
                            if (stack3.getCount() > 1) {
                                i = 40;
                            }
                        }
                    }

                    if (flag3 && !flag2) {
                        event.setOutput(ItemStack.EMPTY);
                        event.setCost(0);
                        return;
                    }
                }
            }

            int k2 = (int) Mth.clamp(j + (long) i, 0L, 2147483647L);
            event.setCost(k2);
            if (i <= 0) {
                stack1 = ItemStack.EMPTY;
            }

            if (k == i && k > 0 && event.getCost() >= 40) {
                event.setCost(39);
            }

            if (event.getCost() >= 40 && !event.getPlayer().getAbilities().instabuild) {
                stack1 = ItemStack.EMPTY;
            }

            if (!stack1.isEmpty()) {
                int i3 = stack1.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0));
                if (i3 < stack2.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0))) {
                    i3 = stack2.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0));
                }

                if (k != i || k == 0) {
                    i3 = calculateIncreasedRepairCost(i3);
                }

                stack1.set(DataComponents.REPAIR_COST, i3);
                MobEnchantUtils.setEnchantments(stack1, itemenchantments$mutable.toImmutable());
            }

            event.setOutput(stack1);
        }
    }

    @SubscribeEvent
    public static void onExpDropped(LivingExperienceDropEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof IEnchantCap cap) {
            if (cap.getEnchantCap().hasEnchant()) {
                if (cap.getEnchantCap().isAncient()) {
                    event.setDroppedExperience(event.getDroppedExperience() + MobEnchantUtils.getExperienceFromMob(cap) * 5);
                } else {
                    event.setDroppedExperience(event.getDroppedExperience() + MobEnchantUtils.getExperienceFromMob(cap));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            if (player instanceof IEnchantCap cap) {
                for (int i = 0; i < cap.getEnchantCap().getMobEnchants().size(); i++) {
                    PacketDistributor.sendToPlayer(serverPlayer, new MobEnchantedMessage(player, cap.getEnchantCap().getMobEnchants().get(i)));

                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player playerEntity = event.getEntity();
        if (playerEntity instanceof IEnchantCap cap) {
            if (!playerEntity.level().isClientSide()) {
                for (int i = 0; i < cap.getEnchantCap().getMobEnchants().size(); i++) {
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(playerEntity, new MobEnchantedMessage(playerEntity, cap.getEnchantCap().getMobEnchants().get(i)));
                }
            }
        }
        ;
    }


    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();
        if (!event.isWasDeath()) {
            ((IEnchantCap) oldPlayer).getEnchantCap().getMobEnchants().forEach(mobEnchantHandler -> {
                ((IEnchantCap) newPlayer).getEnchantCap().addMobEnchant(newPlayer, mobEnchantHandler.getMobEnchant(), mobEnchantHandler.getEnchantLevel());
            });
        }
    }
}
