package com.astradia.render.player;

public class HairPhysics {
    // Estado de cada segmento de cabello
    private float currentPitch = 0f;  // Rotación actual
    private float targetPitch = 0f;   // Rotación objetivo (gravedad)
    private float velocity = 0f;      // Velocidad para efecto de inercia

    // Configuración
    private static final float GRAVITY_STRENGTH = 0.15f;  // Qué tan fuerte "cae"
    private static final float DAMPING = 0.85f;           // Reduce oscilación
    private static final float MAX_SWING = 45f;           // Límite de balanceo

    /**
     * Actualiza la física del cabello cada frame
     * @param headPitch Pitch de la cabeza del jugador (-90 a 90)
     * @param deltaTime Delta time del frame (generalmente 1.0f)
     */
    public void update(float headPitch, float deltaTime) {

        // Calcular rotación objetivo basada en gravedad
        // Cuando miras arriba (pitch negativo), las coletas deben caer hacia atrás
        // Cuando miras abajo (pitch positivo), las coletas caen hacia adelante
        targetPitch = -headPitch; // Factor para efecto natural

        // Aplicar física simple con velocidad
        float diff = targetPitch - currentPitch;
        velocity += diff * GRAVITY_STRENGTH * deltaTime;
        velocity *= DAMPING; // Amortiguación

        currentPitch += velocity * deltaTime;

        // Limitar el balanceo máximo
        currentPitch = Math.max(-180, Math.min(180, currentPitch));
    }

    /**
     * Obtiene la rotación a aplicar al modelo
     */
    public float getRotation() {
        return currentPitch;
    }
}