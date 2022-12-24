package com.rekindled.embers;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rekindled.embers.api.block.IDial;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.fml.LogicalSide;

@OnlyIn(Dist.CLIENT)
public class EmbersClientEvents {

	public static final IGuiOverlay DIAL_OVERLAY = EmbersClientEvents::renderDialOverlay;
	public static int ticks = 0;

	public static void onClientTick(ClientTickEvent event) {
		if (event.side == LogicalSide.CLIENT && event.phase == Phase.START) {
			Minecraft mc = Minecraft.getInstance();
			ticks++;
			//if(!mc.isPaused())
			//ClientProxy.particleRenderer.updateParticles();

			if (mc.hitResult instanceof BlockHitResult result) {
				Level world = mc.level;
				if (result != null && world != null) {
					if (result.getType() == BlockHitResult.Type.BLOCK) {
						BlockState state = world.getBlockState(result.getBlockPos());
						if (state.getBlock() instanceof IDial) {
							((IDial) state.getBlock()).updateBEData(world, state, result.getBlockPos());
						}
					}
				}
			}
		}
	}

	public static void renderDialOverlay(ForgeGui gui, PoseStack poseStack, float partialTicks, int width, int height) {
		Minecraft mc = gui.getMinecraft();
		if (mc.options.hideGui)
			return;

		if (mc.hitResult instanceof BlockHitResult result) {
			ClientLevel world = mc.level;
			if (result != null) {
				if (result.getType() == BlockHitResult.Type.BLOCK) {
					BlockPos pos = result.getBlockPos();
					BlockState state = world.getBlockState(pos);
					List<String> text = new ArrayList<String>();

					if (state.getBlock() instanceof IDial) {
						text.addAll(((IDial) state.getBlock()).getDisplayInfo(world, result.getBlockPos(), state));
					} else {
						return;
					}

					poseStack.pushPose();
					for (int i = 0; i < text.size(); i++) {
						mc.font.drawShadow(poseStack, text.get(i), width/ 2 - mc.font.width(text.get(i)) / 2, height / 2 + 40 + 11 * i, 0xFFFFFF);
						mc.font.draw(poseStack, text.get(i), width/ 2 - mc.font.width(text.get(i)) / 2, height / 2 + 40 + 11 * i, 0xFFFFFF);
					}
					poseStack.popPose();
				}
			}
		}
	}
}