# Vento Deluxe

A modular, data-driven visual framework for Minecraft Fabric — built to empower server developers with full control over player appearance, cosmetic logic, and animation, all through JSON.

## Overview

**Vento Deluxe** is a custom visual system commissioned for a private Minecraft server currently in development. Its core purpose is to provide server developers with a flexible and extensible way to define cosmetics, player transformations, and visual behaviors — entirely through data files, textures, and models, without writing a single line of Java code.

The system supports a wide range of cosmetic types, from simple accessories to full-body replacements, and includes advanced features like dynamic texture layering, animation control, slot-based rendering logic, and player-reactive visuals. It is under active development, with new features added regularly in response to evolving server needs and creative direction.

## Development Context

- Designed for internal use by server developers and content creators.
- Built to support rapid iteration and visual experimentation via JSON and resource packs.
- Includes customization of player proportions per body part (arms, legs, torso, etc.).
- Uses GeckoLib on the client side for model and animation handling.
- Replaces the vanilla player model with a custom one to support advanced visuals.
- Some features are deferred or partially implemented based on staff priorities and server launch timeline.

## Some Features

- **Variant-based architecture**: Each cosmetic can define multiple variants with unique visual and behavioral configurations.
- **Component-driven design**: Modular components handle textures, models, animations, particles, sounds, and more.
- **Slot behaviors**: Custom logic per slot (e.g. `arms:replace`, `legs:merge`) allows for advanced rendering and animation overrides.
- **Dynamic tokens**: Use context-aware placeholders like `@player_skin`, `@is_slim`, or `@item_texture` to adapt cosmetics to the player state.
- **Conditional expressions**: Support for ternary logic in JSON fields (e.g. `@is_slim ? slim.png : normal.png`) for adaptive rendering.
- **Global rules**: Enforce constraints across variants (e.g. only one tintable variant per cosmetic).
- **Client-server separation**: Clean modular boundaries between common logic, client-side rendering, and server-side validation.
- **Body customization**: Includes a system for modifying player proportions per body part.

## 📦 JSON Example

```json
{
  "id": "astradia:hook_arm",
  "name": "Hook Arm",
  "slotId": "arms:replace",
  "behavior": "astradia:arm_replace",
  "variants": [
    {
      "id": "default",
      "displayName": "Standard",
      "components": [
        {
          "type": "model",
          "path": "astradia:models/robot_arm_with_hook"
        },
        {
          "type": "layeredTexture",
          "layers": [
            {
              "path": "@player_skin",
              "targets": ["player_arm_segment"]
            },
            {
              "path": "astradia:textures/hook.png",
              "targets": ["hook"]
            }
          ]
        }
      ]
    }
  ]
}
