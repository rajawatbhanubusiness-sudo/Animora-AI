
package com.animora.ai

data class AnimationStyleConfig(
    val id: String,
    val displayName: String,
    val prompt: String,
    val negativePrompt: String
)

object AnimationStyleConfigs {

    private const val COMMON_NEGATIVE =
        "blurry, low resolution, distorted face, deformed body, " +
        "extra fingers, missing fingers, extra limbs, bad anatomy, " +
        "uneven eyes, duplicate person, unwanted objects, artifacts, " +
        "watermark, text, logo, identity change"

    val all: List<AnimationStyleConfig> = listOf(

        AnimationStyleConfig(
            "anime",
            "Anime",
            "Transform the input into high-quality cinematic anime. " +
            "Use expressive detailed eyes, clean line art, beautifully " +
            "shaded hair, rich colors, detailed backgrounds and " +
            "professional animated-film lighting. Preserve the original " +
            "person's identity, face, hairstyle, pose and clothing.",
            COMMON_NEGATIVE
        ),

        AnimationStyleConfig(
            "3d_cartoon",
            "3D Cartoon",
            "Create a premium 3D animated movie appearance. Use detailed " +
            "3D characters, realistic materials, expressive faces, " +
            "soft cinematic lighting, realistic shadows, depth and " +
            "beautifully rendered environments. Preserve identity and pose.",
            COMMON_NEGATIVE
        ),

        AnimationStyleConfig(
            "2d_cartoon",
            "2D Cartoon",
            "Transform into polished professional 2D animation. Use " +
            "smooth outlines, appealing character design, expressive " +
            "features, carefully shaded colors and detailed animated " +
            "backgrounds. Preserve the original person's identity.",
            COMMON_NEGATIVE
        ),

        AnimationStyleConfig(
            "watercolor",
            "Watercolor",
            "Create an elegant watercolor animation illustration. Use " +
            "delicate pigment washes, natural color blending, visible " +
            "paper texture, soft edges, artistic brush details and " +
            "beautiful atmospheric lighting. Preserve facial identity.",
            COMMON_NEGATIVE
        ),

        AnimationStyleConfig(
            "clay_animation",
            "Clay Animation",
            "Transform the subject into a handcrafted clay animation " +
            "character. Use realistic modeling-clay textures, subtle " +
            "fingerprint details, sculpted features, soft studio lighting " +
            "and a charming miniature environment. Preserve identity.",
            COMMON_NEGATIVE
        ),

        AnimationStyleConfig(
            "comic_book",
            "Comic Book",
            "Create a professional graphic novel illustration. Use " +
            "bold ink outlines, dramatic shadows, detailed halftone " +
            "textures, dynamic colors and cinematic comic-book lighting. " +
            "Preserve the original person's recognizable face.",
            COMMON_NEGATIVE
        ),

        AnimationStyleConfig(
            "pixel_art",
            "Pixel Art",
            "Convert the subject into carefully crafted high-quality " +
            "pixel art. Use a consistent pixel grid, deliberate pixel " +
            "shading, rich limited color palettes, detailed pixel textures " +
            "and a polished retro-game aesthetic. Preserve recognizable features.",
            COMMON_NEGATIVE
        ),

        AnimationStyleConfig(
            "realistic_animation",
            "Realistic Animation",
            "Create a highly detailed realistic animated-film character. " +
            "Use natural skin textures, realistic hair, lifelike eyes, " +
            "accurate anatomy, subtle facial details and cinematic " +
            "lighting. Preserve the person's exact recognizable identity.",
            COMMON_NEGATIVE
        ),

        AnimationStyleConfig(
            "fantasy_animation",
            "Fantasy Animation",
            "Transform the scene into an imaginative fantasy animation. " +
            "Use magical atmosphere, beautiful environmental details, " +
            "cinematic light rays, rich colors, intricate textures and " +
            "subtle magical visual elements. Preserve the subject's identity.",
            COMMON_NEGATIVE
        ),

        AnimationStyleConfig(
            "vintage_cartoon",
            "Vintage Cartoon",
            "Create a classic vintage cartoon appearance. Use hand-drawn " +
            "animation aesthetics, expressive shapes, warm retro colors, " +
            "subtle film grain, textured backgrounds and traditional " +
            "cel-animation shading. Preserve recognizable identity.",
            COMMON_NEGATIVE
        ),

        AnimationStyleConfig(
            "paper_cutout",
            "Paper Cutout",
            "Transform the subject into layered paper-cutout animation. " +
            "Use handcrafted paper textures, precise cut edges, layered " +
            "depth, soft cast shadows and beautiful colorful composition. " +
            "Preserve the original person's recognizable appearance.",
            COMMON_NEGATIVE
        ),

        AnimationStyleConfig(
            "cyberpunk",
            "Cyberpunk",
            "Create a premium futuristic cyberpunk animation. Use neon " +
            "lighting, futuristic architecture, reflective surfaces, " +
            "atmospheric haze, detailed technology, dramatic cinematic " +
            "colors and high-detail character rendering. Preserve identity.",
            COMMON_NEGATIVE
        )
    )

    fun findByName(name: String): AnimationStyleConfig? {
        return all.firstOrNull {
            name.contains(it.displayName, ignoreCase = true) ||
            name.equals(it.id, ignoreCase = true)
        }
    }

    fun findById(id: String): AnimationStyleConfig? {
        return all.firstOrNull {
            it.id.equals(id, ignoreCase = true)
        }
    }
}
