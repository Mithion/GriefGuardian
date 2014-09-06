package com.mithion.griefguardian.util;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderUtils {
	public static void renderBoundingBox(AxisAlignedBB boundingBox, int boxColor, int outlineColor){
		Tessellator tessellator = Tessellator.instance;
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
		tessellator.startDrawingQuads();

        if (outlineColor != -1)
        {
            tessellator.setColorRGBA_I(boxColor, alpha);
        }

        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        tessellator.addVertex(boundingBox.maxX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        tessellator.addVertex(boundingBox.maxX + XOffset, boundingBox.minY + YOffset, boundingBox.maxZ + ZOffset);
        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.maxZ + ZOffset);
        
        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        tessellator.addVertex(boundingBox.maxX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        tessellator.addVertex(boundingBox.maxX + XOffset, boundingBox.maxY - YOffset, boundingBox.maxZ + ZOffset);
        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.maxZ + ZOffset);
        
        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.maxZ + ZOffset);
        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.maxZ + ZOffset);
        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        
        tessellator.addVertex(boundingBox.maxX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        tessellator.addVertex(boundingBox.maxX + XOffset, boundingBox.minY + YOffset, boundingBox.maxZ + ZOffset);
        tessellator.addVertex(boundingBox.maxX + XOffset, boundingBox.maxY - YOffset, boundingBox.maxZ + ZOffset);
        tessellator.addVertex(boundingBox.maxX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        
        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        tessellator.addVertex(boundingBox.maxX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        tessellator.addVertex(boundingBox.maxX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        
        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.maxZ + ZOffset);
        tessellator.addVertex(boundingBox.maxX + XOffset, boundingBox.minY + YOffset, boundingBox.maxZ + ZOffset);
        tessellator.addVertex(boundingBox.maxX + XOffset, boundingBox.maxY - YOffset, boundingBox.maxZ + ZOffset);
        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.maxZ + ZOffset);
        
        tessellator.draw();
		        
        GL11.glDisable(GL11.GL_BLEND);
        
        GL11.glPopAttrib();
        
        //outline		
        tessellator.startDrawing(3);

        if (outlineColor != -1)
        {
            tessellator.setColorOpaque_I(outlineColor);
        }

        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        tessellator.addVertex(boundingBox.maxX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        tessellator.addVertex(boundingBox.maxX + XOffset, boundingBox.minY + YOffset, boundingBox.maxZ + ZOffset);
        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.maxZ + ZOffset);
        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        tessellator.draw();
        tessellator.startDrawing(3);

        if (outlineColor != -1)
        {
            tessellator.setColorOpaque_I(outlineColor);
        }

        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        tessellator.addVertex(boundingBox.maxX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        tessellator.addVertex(boundingBox.maxX + XOffset, boundingBox.maxY - YOffset, boundingBox.maxZ + ZOffset);
        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.maxZ + ZOffset);
        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        tessellator.draw();
        tessellator.startDrawing(1);

        if (outlineColor != -1)
        {
            tessellator.setColorOpaque_I(outlineColor);
        }

        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        tessellator.addVertex(boundingBox.maxX + XOffset, boundingBox.minY + YOffset, boundingBox.minZ + ZOffset);
        tessellator.addVertex(boundingBox.maxX + XOffset, boundingBox.maxY - YOffset, boundingBox.minZ + ZOffset);
        tessellator.addVertex(boundingBox.maxX + XOffset, boundingBox.minY + YOffset, boundingBox.maxZ + ZOffset);
        tessellator.addVertex(boundingBox.maxX + XOffset, boundingBox.maxY - YOffset, boundingBox.maxZ + ZOffset);
        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.minY + YOffset, boundingBox.maxZ + ZOffset);
        tessellator.addVertex(boundingBox.minX + XOffset, boundingBox.maxY - YOffset, boundingBox.maxZ + ZOffset);
        tessellator.draw();
        
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
        //GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
	}
}
