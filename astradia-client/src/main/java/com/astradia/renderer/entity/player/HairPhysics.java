package com.astradia.renderer.entity.player;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class HairPhysics {

        // Estado actual
        private float currentPitch = 0f;
        private float currentYaw = 0f;

        private float velocityPitch = 0f;
        private float velocityYaw = 0f;

        // Velocidad suavizada del jugador
        private float smoothVelocityX = 0f;
        private float smoothVelocityY = 0f;
        private float smoothVelocityZ = 0f;

        // Viento
        private float windPhase = 0f;
        private float windStrength = 0f;

        // Física base
        private static final float SPRING = 0.18f;
        private static final float DAMPING = 0.82f;

        // Movimiento (valores calibrados a Minecraft real)
        private static final float VELOCITY_SMOOTHING = 0.7f;
        private static final float FORWARD_PITCH_FACTOR = 140f;
        private static final float VERTICAL_PITCH_FACTOR = 90f;
        private static final float SIDEWAYS_YAW_FACTOR = 120f;

        // Viento
        private static final float WIND_BASE_STRENGTH = 0.25f;
        private static final float WIND_GUST_STRENGTH = 0.35f;
        private static final float WIND_SPEED = 0.05f;

        // Límites
        private static final float MAX_PITCH = 45f;
        private static final float MAX_YAW = 35f;

        public void update(float headPitch, float headYaw, Vec3 velocity, float deltaTime) {

            // ----------------------------------------------------
            // 1. Suavizar velocity (CRÍTICO para multiplayer)
            // ----------------------------------------------------

            smoothVelocityX = smoothVelocityX * VELOCITY_SMOOTHING + (float) velocity.x * (1f - VELOCITY_SMOOTHING);
            smoothVelocityY = smoothVelocityY * VELOCITY_SMOOTHING + (float) velocity.y * (1f - VELOCITY_SMOOTHING);
            smoothVelocityZ = smoothVelocityZ * VELOCITY_SMOOTHING + (float) velocity.z * (1f - VELOCITY_SMOOTHING);

            // ----------------------------------------------------
            // 2. Velocidad relativa a la cabeza (FIX REAL)
            // ----------------------------------------------------

            float yawRad = (float) Math.toRadians(headYaw);

            // Forward/backward con signo correcto
            float forwardSpeed =
                    smoothVelocityX * (float) -Math.sin(yawRad)
                            + smoothVelocityZ * (float)  Math.cos(yawRad);

            // Movimiento lateral real
            float sidewaysSpeed =
                    smoothVelocityX * (float) Math.cos(yawRad)
                            + smoothVelocityZ * (float) Math.sin(yawRad);

            // ----------------------------------------------------
            // 3. Actualizar viento (suave y estable)
            // ----------------------------------------------------

            updateWind(deltaTime);

            // ----------------------------------------------------
            // 4. Targets físicos
            // ----------------------------------------------------

            float targetPitch = 0f;
            float targetYaw = 0f;

            // Inercia por movimiento forward/backward
            targetPitch -= forwardSpeed * FORWARD_PITCH_FACTOR;

            // Inercia vertical (saltar / caer)
            targetPitch += smoothVelocityY * VERTICAL_PITCH_FACTOR;

            // Influencia leve de la rotación de cabeza
            targetPitch += -headPitch * 0.35f;

            // Movimiento lateral → yaw
            targetYaw -= sidewaysSpeed * SIDEWAYS_YAW_FACTOR;

            // Viento
            targetPitch += Math.sin(windPhase) * windStrength * 6f;
            targetYaw   += Math.cos(windPhase * 1.2f) * windStrength * 8f;

            // ----------------------------------------------------
            // 5. Spring physics (estable y suave)
            // ----------------------------------------------------

            float diffPitch = targetPitch - currentPitch;
            velocityPitch += diffPitch * SPRING * deltaTime;
            velocityPitch *= DAMPING;
            currentPitch += velocityPitch * deltaTime;

            float diffYaw = targetYaw - currentYaw;
            velocityYaw += diffYaw * SPRING * deltaTime;
            velocityYaw *= DAMPING;
            currentYaw += velocityYaw * deltaTime;

            // ----------------------------------------------------
            // 6. Clamp final (evita spasms)
            // ----------------------------------------------------

            currentPitch = clamp(currentPitch, -MAX_PITCH, MAX_PITCH);
            currentYaw   = clamp(currentYaw,   -MAX_YAW,   MAX_YAW);
        }

        private void updateWind(float deltaTime) {
            windPhase += WIND_SPEED * deltaTime;

            float gust = (float) Math.sin(windPhase * 0.6f) * WIND_GUST_STRENGTH;
            windStrength = WIND_BASE_STRENGTH + gust;
        }

        private float clamp(float v, float min, float max) {
            return Math.max(min, Math.min(max, v));
        }

        public float getPitchRotation() {
            return currentPitch;
        }

        public float getYawRotation() {
            return currentYaw;
        }

        public void reset() {
            currentPitch = 0f;
            currentYaw = 0f;
            velocityPitch = 0f;
            velocityYaw = 0f;
            smoothVelocityX = 0f;
            smoothVelocityY = 0f;
            smoothVelocityZ = 0f;
        }

    /**
     * Obtiene solo la velocidad hacia adelante/atrás relativa
     * Positivo = hacia adelante, Negativo = hacia atrás
     */
    public static float getForwardSpeed(LivingEntity entity) {
        Vec3 currentPos = new Vec3(entity.getX(), entity.getY(), entity.getZ());
        Vec3 lastPos = new Vec3(entity.xo, entity.yo, entity.zo);
        Vec3 velocity = currentPos.subtract(lastPos);

        float bodyYawRad = entity.yBodyRot * 0.017453292F;
        double sinYaw = Math.sin(bodyYawRad);
        double cosYaw = Math.cos(bodyYawRad);

        return (float)(velocity.x * sinYaw + velocity.z * cosYaw);
    }

    /**
     * Obtiene solo la velocidad lateral relativa
     * Positivo = hacia la derecha, Negativo = hacia la izquierda
     */
    public static float getStrafeSpeed(LivingEntity entity) {
        Vec3 currentPos = new Vec3(entity.getX(), entity.getY(), entity.getZ());
        Vec3 lastPos = new Vec3(entity.xo, entity.yo, entity.zo);
        Vec3 velocity = currentPos.subtract(lastPos);

        float bodyYawRad = entity.yBodyRot * 0.017453292F;
        double sinYaw = Math.sin(bodyYawRad);
        double cosYaw = Math.cos(bodyYawRad);

        return (float)(velocity.x * cosYaw - velocity.z * sinYaw);
    }
}