package com.mithion.griefguardian.util;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderUtils {
	public static void renderBoundingBox(AxisAlignedBB boundingBox, int boxColor, int outlineColor){
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		int alpha = 60;
		double XOffset = 0.5;
		double YOffset = 0.5;
		double ZOffset = 0.5;
		
		boundingBox = boundingBox.contract(0.01, 0.01, 0.01);
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_LIGHTING_BIT);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
		//GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		
		//box

        if (outlineColor != -1)
        {
            worldrenderer.func_178974_a(boxColor, alpha);
        }

        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        worldrenderer.addVertex(boundingBox.maxX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        worldrenderer.addVertex(boundingBox.maxX + XOffset, boundingBox.minY + YOffset, boundingBox.maxZ + ZOffset);
        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.maxZ + ZOffset);
        
        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        worldrenderer.addVertex(boundingBox.maxX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        worldrenderer.addVertex(boundingBox.maxX + XOffset, boundingBox.maxY - YOffset, boundingBox.maxZ + ZOffset);
        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.maxZ + ZOffset);
        
        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.maxZ + ZOffset);
        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.maxZ + ZOffset);
        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        
        worldrenderer.addVertex(boundingBox.maxX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        worldrenderer.addVertex(boundingBox.maxX + XOffset, boundingBox.minY + YOffset, boundingBox.maxZ + ZOffset);
        worldrenderer.addVertex(boundingBox.maxX + XOffset, boundingBox.maxY - YOffset, boundingBox.maxZ + ZOffset);
        worldrenderer.addVertex(boundingBox.maxX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        
        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        worldrenderer.addVertex(boundingBox.maxX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        worldrenderer.addVertex(boundingBox.maxX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        
        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.maxZ + ZOffset);
        worldrenderer.addVertex(boundingBox.maxX + XOffset, boundingBox.minY + YOffset, boundingBox.maxZ + ZOffset);
        worldrenderer.addVertex(boundingBox.maxX + XOffset, boundingBox.maxY - YOffset, boundingBox.maxZ + ZOffset);
        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.maxZ + ZOffset);
        
        tessellator.draw();
		        
        GL11.glDisable(GL11.GL_BLEND);
        
        GL11.glPopAttrib();
        
        //outline		
        worldrenderer.startDrawing(3);

        if (outlineColor != -1)
        {
            worldrenderer.func_178991_c(outlineColor);
        }

        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        worldrenderer.addVertex(boundingBox.maxX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        worldrenderer.addVertex(boundingBox.maxX + XOffset, boundingBox.minY + YOffset, boundingBox.maxZ + ZOffset);
        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.maxZ + ZOffset);
        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        tessellator.draw();
        worldrenderer.startDrawing(3);

        if (outlineColor != -1)
        {
            worldrenderer.func_178991_c(outlineColor);
        }

        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        worldrenderer.addVertex(boundingBox.maxX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        worldrenderer.addVertex(boundingBox.maxX + XOffset, boundingBox.maxY - YOffset, boundingBox.maxZ + ZOffset);
        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.maxZ + ZOffset);
        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        tessellator.draw();
        worldrenderer.startDrawing(1);

        if (outlineColor != -1)
        {
            worldrenderer.func_178991_c(outlineColor);
        }

        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        worldrenderer.addVertex(boundingBox.maxX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        worldrenderer.addVertex(boundingBox.maxX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        worldrenderer.addVertex(boundingBox.maxX + XOffset, boundingBox.minY + YOffset, boundingBox.maxZ + ZOffset);
        worldrenderer.addVertex(boundingBox.maxX + XOffset, boundingBox.maxY - YOffset, boundingBox.maxZ + ZOffset);
        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.maxZ + ZOffset);
        worldrenderer.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.maxZ + ZOffset);
        tessellator.draw();
        
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
        //GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
	}
}
