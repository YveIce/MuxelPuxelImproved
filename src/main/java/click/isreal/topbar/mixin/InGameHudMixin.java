package click.isreal.topbar.mixin;

/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2022 YveIce
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

import click.isreal.topbar.Topbar;
import click.isreal.topbar.client.TopbarClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;


@Environment( EnvType.CLIENT )
@Mixin( InGameHud.class )
public abstract class InGameHudMixin extends DrawableHelper
{

    @Shadow
    private int scaledWidth;
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private int scaledHeight;

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject( method = "renderStatusEffectOverlay", at = @At( "HEAD" ), cancellable = true )
    public void renderStatusEffectOverlay( MatrixStack matrices, final CallbackInfo ci )
    {
        Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
        if ( !collection.isEmpty() )
        {
            RenderSystem.enableBlend();
            int i = 0;
            int j = 0;
            StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
            List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());
            this.client.getTextureManager().bindTexture(HandledScreen.BACKGROUND_TEXTURE);
            Iterator var7 = Ordering.natural().reverse().sortedCopy(collection).iterator();

            while ( var7.hasNext() )
            {
                StatusEffectInstance statusEffectInstance = (StatusEffectInstance) var7.next();
                StatusEffect statusEffect = statusEffectInstance.getEffectType();
                if ( statusEffectInstance.shouldShowIcon() )
                {
                    int k = this.scaledWidth;
                    int l = 0;
                    if ( this.client.isDemo() )
                    {
                        l += 15;
                    }

                    if ( TopbarClient.getInstance().isMixelPixel() )
                    {
                        l += 10;
                    }

                    if ( statusEffect.isBeneficial() )
                    {
                        ++i;
                        k -= (Topbar.getInstance().getEffectIconSize() + 2) * i;
                    }
                    else
                    {
                        ++j;
                        k -= (Topbar.getInstance().getEffectIconSize() + 2) * j;
                        l += (Topbar.getInstance().getEffectIconSize() + 2);
                    }

                    RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
                    float f = 1.0F;
                    if ( statusEffectInstance.isAmbient() )
                    {
                        //this.drawTexture(matrices, k, l, 165, 166, 14, 14, 14, 14);
                        fill(matrices, k, l, k + (Topbar.getInstance().getEffectIconSize() + 2), l + (Topbar.getInstance().getEffectIconSize() + 2), Topbar.getInstance().getEffectColorPositive());
                    }
                    else
                    {
                        fill(matrices, k, l, k + (Topbar.getInstance().getEffectIconSize() + 2), l + (Topbar.getInstance().getEffectIconSize() + 2), Topbar.getInstance().getEffectColorNegative());
                        if ( statusEffectInstance.getDuration() <= 200 )
                        {
                            int m = 10 - statusEffectInstance.getDuration() / 20;
                            f = MathHelper.clamp((float) statusEffectInstance.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + MathHelper.cos((float) statusEffectInstance.getDuration() * 3.1415927F / 5.0F) * MathHelper.clamp((float) m / 10.0F * 0.25F, 0.0F, 0.25F);
                        }
                    }

                    Sprite sprite = statusEffectSpriteManager.getSprite(statusEffect);
                    float finalF = f;
                    int finalK = k;
                    int finalL = l;
                    list.add(() -> {
                        this.client.getTextureManager().bindTexture(sprite.getAtlasId());
                        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, finalF);
                        drawSprite(matrices, finalK + 1, finalL + 1, this.getZOffset(), Topbar.getInstance().getEffectIconSize(), Topbar.getInstance().getEffectIconSize(), sprite);
                    });
                }
            }

            list.forEach(Runnable::run);
        }
        if ( ci.isCancellable() )
        {
            ci.cancel();
        }
    }

    @Inject( method = {"renderScoreboardSidebar"}, at = {@At( "HEAD" )}, cancellable = true )
    private void renderScoreboardSidebar( MatrixStack matrices, ScoreboardObjective objective, final CallbackInfo callbackInfo )
    {

        if ( TopbarClient.getInstance().isMixelPixel() && TopbarClient.getInstance().world != TopbarClient.mpWorld.OTHER )
        {
      /* custom scale
      RenderSystem.pushMatrix();
      RenderSystem.translatef(2.0F, 8.0F, 0.0F);
      RenderSystem.scaled(2.0D,2.0D, 1.0D);

       */

            int offsetLeft = 2;
            int offsetRight = 2;
            String fps = TopbarClient.getInstance().getFPS();
            String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());

            fill(matrices, 0, 0, this.scaledWidth, 10, Topbar.getInstance().getColorBackground());
            this.getTextRenderer().getClass();

            if ( Topbar.getInstance().isFpsShow() )
            {
                offsetLeft += this.getTextRenderer().getWidth(Formatting.strip(fps + TopbarClient.getInstance().strSplitter));
            }

            if ( Topbar.getInstance().isTimeShow() )
            {
                offsetRight += this.getTextRenderer().getWidth(" | 00:00:00");
            }

            this.getTextRenderer().draw(matrices, TopbarClient.getInstance().strTopLeft, offsetLeft, 1, 0xfff0f0f0);
            this.getTextRenderer().draw(matrices, TopbarClient.getInstance().strTopRight, this.scaledWidth - this.getTextRenderer().getWidth(Formatting.strip(TopbarClient.getInstance().strTopRight)) - offsetRight, 1, 0xfff0f0f0);
            if ( Topbar.getInstance().isFpsShow() )
            {
                this.getTextRenderer().draw(matrices, fps + TopbarClient.getInstance().strSplitter, 2, 1, Topbar.getInstance().getFpsColor());
            }
            if ( Topbar.getInstance().isTimeShow() )
            {
                this.getTextRenderer().draw(matrices, TopbarClient.getInstance().strSplitter + Formatting.RESET + time, this.scaledWidth - offsetRight, 1, Topbar.getInstance().getTimeColor());
            }

            this.getTextRenderer().draw(matrices, TopbarClient.getInstance().strPlotName, this.scaledWidth - this.getTextRenderer().getWidth(Formatting.strip(TopbarClient.getInstance().strPlotName)) - 2, this.scaledHeight - 19, 0xfff0f0f0);

            this.getTextRenderer().draw(matrices, TopbarClient.getInstance().strPlotOwner, this.scaledWidth - this.getTextRenderer().getWidth(Formatting.strip(TopbarClient.getInstance().strPlotOwner)) - 2, this.scaledHeight - 10, 0xfff0f0f0);


            //this.getFontRenderer().draw(matrices, TopbarClient.getInstance().DEBUGTEXT, 2, 20, Topbar.getInstance().getTimeColor());

            // custom scale
            //RenderSystem.popMatrix();

            callbackInfo.cancel();
        }

    }
}




