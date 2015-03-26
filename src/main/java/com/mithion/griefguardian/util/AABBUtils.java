package com.mithion.griefguardian.util;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class AABBUtils {
	public static boolean AABBIsWithinRange(AxisAlignedBB bb, Vec3 point, int distance){
		Vec3 a = new Vec3(bb.minX, bb.minY, bb.minZ);
		Vec3 b = new Vec3(bb.maxX, bb.minY, bb.minZ);
		Vec3 c = new Vec3(bb.maxX, bb.minY, bb.maxZ);
		Vec3 d = new Vec3(bb.minX, bb.minY, bb.maxZ);
		
		Vec3 e = new Vec3(bb.minX, bb.maxY, bb.minZ);
		Vec3 f = new Vec3(bb.maxX, bb.maxY, bb.minZ);
		Vec3 g = new Vec3(bb.maxX, bb.maxY, bb.maxZ);
		Vec3 h = new Vec3(bb.minX, bb.maxY, bb.maxZ);
		
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
