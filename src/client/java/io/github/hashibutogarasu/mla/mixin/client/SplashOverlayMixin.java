package io.github.hashibutogarasu.mla.mixin.client;

import io.github.hashibutogarasu.mla.MojangLogoAnimation;
import io.github.hashibutogarasu.mla.sounds.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin {
    @Unique
    private static boolean firstLoad = true;

    @Shadow @Final private ResourceReload reload;

    @Shadow private float progress;

    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private boolean reloading;

    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger(SplashOverlayMixin.class);

    @Unique
    private boolean animationStarting = false;

    @Unique
    private boolean animationEnded = false;

    @Unique
    private int animProgress = 0;

    @Unique
    private final Identifier tex = new Identifier("textures/gui/title/mojangstudios.png");

    @Unique
    private boolean playing = false;

    @Unique
    private final PositionedSoundInstance SOUND = PositionedSoundInstance.master(ModSounds.MOJANG_SOUND_EVENT, 1.0f, 1.0f);

    @Redirect(method = "render",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;LOGO:Lnet/minecraft/util/Identifier;"))
    private Identifier logo() {
        return tex;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceReload;getProgress()F"))
    private float getProgress(ResourceReload instance) {
        return this.reload.getProgress();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(Lnet/minecraft/client/render/RenderLayer;IIIII)V"), cancellable = true)
    private void fill(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {

    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIFFIIII)V", ordinal = 0))
    private void drawTexture0(DrawContext context, Identifier texture, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        double d = Math.min((double)context.getScaledWindowWidth() * 0.75, context.getScaledWindowHeight()) * 0.25;
        double e = d * 4.0;
        int r = (int)(e);

        if(progress > 0){
            if(!animationStarting && firstLoad){
                this.reload.whenComplete().thenAccept(object -> {
                    client.getSoundManager().play(SOUND);
                    getAnimationThread().start();
                });
                animationStarting = true;
            }
        }

        this.playing = client.getSoundManager().isPlaying(SOUND);
        context.drawTexture(getMojang(this.animProgress), x, y, r, (int) d, u, v, regionWidth, regionHeight + 60, textureWidth, textureHeight);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIFFIIII)V", ordinal = 1))
    private void drawTexture1(DrawContext context, Identifier texture, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {

    }

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;reloading:Z", opcode = Opcodes.GETFIELD, ordinal = 2))
    private boolean isReloading(SplashOverlay instance) {
        return (firstLoad && !animationEnded) || reloading;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init(Lnet/minecraft/client/MinecraftClient;II)V"), method = "render")
    private void init(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        firstLoad = false;
    }

    @Unique
    private Identifier getMojang(int index){
        return firstLoad ? new Identifier("mla", "textures/gui/title/mojang/mojang" + index + ".png") : new Identifier("mla", "textures/gui/title/mojang/mojang38.png");
    }

    @Unique
    private @NotNull Thread getAnimationThread() {
        var animthread = new Thread(()->{
            this.animProgress = 0;
            for(int i = 0; i < 38; i++){
                animProgress++;
                try {
                    Thread.sleep(70);
                } catch (InterruptedException ignored) {

                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {

            }
            animationEnded = true;
        });

        animthread.setName("animthread");
        return animthread;
    }
}
