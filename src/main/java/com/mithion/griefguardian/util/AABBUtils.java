package com.mithion.griefguardian.util;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class AABBUtils {
	public static boolean AABBIsWithinRange(AxisAlignedBB bb, Vec3 point, int distance){
		Vec3 a = Vec3.createVectorHelper(bb.minX, bb.minY, bb.minZ);
		Vec3 b = Vec3.createVectorHelper(bb.maxX, bb.minY, bb.minZ);
		Vec3 c = Vec3.createVectorHelper(bb.maxX, bb.minY, bb.maxZ);
		Vec3 d = Vec3.createVectorHelper(bb.minX, bb.minY, bb.maxZ);
		
		Vec3 e = Vec3.createVectorHelper(bb.minX, bb.maxY, bb.minZ);
		Vec3 f = Vec3.createVectorHelper(bb.maxX, bb.maxY, bb.minZ);
		Vec3 g = Vec3.createVectorHelper(bb.maxX, bb.maxY, bb.maxZ);
		Vec3 h = Vec3.createVectorHelper(bb.minX, bb.maxY, bb.maxZ);
		
		if (a.distanceTo(point) < distance)
			return true;
		if (b.distanceTo(point) < distance)
			return true;
		if (c.distanceTo(point) < distance)
			return true;
		if (d.distanceTo(point) < distance)
			return true;
		if (e.distanceTo(point) < distance)
			return true;
		if (f.distanceTo(point) < distance)
			return true;
		if (g.distanceTo(point) < distance)
			return true;
		if (h.distanceTo(point) < distance)
			return true;
		
		return false;
	}
}
