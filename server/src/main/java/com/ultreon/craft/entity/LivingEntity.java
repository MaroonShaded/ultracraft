package com.ultreon.craft.entity;

import com.ultreon.craft.block.Blocks;
import com.ultreon.craft.entity.damagesource.DamageSource;
import com.ultreon.craft.events.EntityEvents;
import com.ultreon.craft.server.util.Utils;
import com.ultreon.craft.world.ChunkPos;
import com.ultreon.craft.world.SoundEvent;
import com.ultreon.craft.world.World;
import com.ultreon.data.types.MapType;
import com.ultreon.libs.commons.v0.Mth;
import com.ultreon.libs.events.v1.ValueEventResult;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class LivingEntity extends Entity {
    protected float health;
    private float maxHeath = 20;
    protected boolean isDead = false;
    protected int damageImmunity = 0;

    public float jumpVel = 0.52F;
    public boolean jumping = false;
    public boolean invincible = false;
    protected float oldHealth;

    public LivingEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public float getHealth() {
        return this.health;
    }

    public void setHealth(float health) {
        this.health = Mth.clamp(health, 0, this.maxHeath);
    }

    public float getMaxHeath() {
        return this.maxHeath;
    }

    public void setMaxHeath(float maxHeath) {
        this.maxHeath = maxHeath;
    }

    public float getJumpVel() {
        return this.jumpVel;
    }

    public void setJumpVel(float jumpVel) {
        this.jumpVel = jumpVel;
    }

    public boolean isInvincible() {
        return this.invincible;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    @Override
    public void tick() {
        if (this.isDead) return;

        if (this.jumping && this.onGround) {
            this.jump();
        }

        if (this.damageImmunity > 0) {
            this.damageImmunity--;
        }

        if (this.isInVoid()) {
            this.hurtFromVoid();
        }

        if (this.health <= 0) {
            this.health = 0;

            if (!this.isDead) {
                if (!EntityEvents.DEATH.factory().onEntityDeath(this).isCanceled()) {
                    this.isDead = true;
                    this.onDeath();
                }
            }
        }

        super.tick();
    }

    protected void hurtFromVoid() {
        this.hurt(5, DamageSource.VOID);
    }

    public void jump() {
        if (this.isInWater()) return;
        this.velocityY = this.jumpVel;
    }

    @Override
    protected void hitGround() {
        if (!this.noGravity && !this.isInWater()) {
            int damage = (int) (this.fallDistance - 2.2f);
            if (damage > 0) {
                this.hurt(damage, DamageSource.FALLING);
            }
        }
    }

    public final void hurt(float damage, DamageSource source) {
        if (this.isDead || this.health <= 0 || this.invincible || this.damageImmunity > 0) return;

        ValueEventResult<Float> result = EntityEvents.DAMAGE.factory().onEntityDamage(this, source, damage);
        Float value = result.getValue();
        if (value != null) damage = value;

        if (this.onHurt(damage, source)) return;

        SoundEvent hurtSound = this.getHurtSound();
        if (hurtSound != null) {
            this.world.playSound(hurtSound, this.x, this.y, this.z);
        }

        damage = Math.max(damage, 0);

        this.oldHealth = this.health;
        this.health = Math.max(this.health - damage, 0);
        this.damageImmunity = 10;

        if (this.health <= 0) {
            this.health = 0;

            if (!EntityEvents.DEATH.factory().onEntityDeath(this).isCanceled()) {
                this.isDead = true;
                this.onDeath();
            }
        }
    }

    /**
     * Handles {@link #hurt(float, DamageSource)} for subclasses.
     *
     * @param damage the damage dealt.
     * @param source the source of the damage/
     * @return true to cancel the damage.
     */
    @ApiStatus.OverrideOnly
    public boolean onHurt(float damage, DamageSource source) {
        return false;
    }

    @Nullable
    public SoundEvent getHurtSound() {
        return null;
    }

    public void onDeath() {

    }

    @Override
    public void load(MapType data) {
        super.load(data);

        this.health = data.getFloat("health", this.health);
        this.maxHeath = data.getFloat("maxHealth", this.maxHeath);
        this.damageImmunity = data.getInt("damageImmunity", this.damageImmunity);
        this.isDead = data.getBoolean("isDead", this.isDead);
        this.jumpVel = data.getFloat("jumpVelocity", this.jumpVel);
        this.jumping = data.getBoolean("jumping", this.jumping);
        this.invincible = data.getBoolean("invincible", this.invincible);
    }

    @Override
    public MapType save(MapType data) {
        data = super.save(data);

        data.putFloat("health", this.health);
        data.putFloat("maxHealth", this.maxHeath);
        data.putInt("damageImmunity", this.damageImmunity);
        data.putBoolean("isDead", this.isDead);
        data.putFloat("jumpVelocity", this.jumpVel);
        data.putBoolean("jumping", this.jumping);
        data.putBoolean("jumping", this.invincible);

        return data;
    }

    public boolean isDead() {
        return this.isDead;
    }

    public boolean isInWater() {
        return this.world.get(this.blockPosition()) == Blocks.WATER;
    }

    public ChunkPos getChunkPos() {
        return Utils.toChunkPos(this.blockPosition());
    }
}
