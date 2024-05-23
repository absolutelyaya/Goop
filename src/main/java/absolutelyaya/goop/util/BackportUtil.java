package absolutelyaya.goop.util;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.random.Random;

public class BackportUtil
{
	public static BlockPos ofFloored(double x, double y, double z)
	{
		return new BlockPos(Math.floor(x), Math.floor(y), Math.floor(z));
	}
	
	public static BlockPos ofFloored(Vec3d pos)
	{
		return new BlockPos(Math.floor(pos.x), Math.floor(pos.y), Math.floor(pos.z));
	}
	
	public static Vec3f vec3dTo3f(Vec3d input)
	{
		return new Vec3f((float)input.x, (float)input.y, (float)input.z);
	}
	
	public static Vec3f readVec3fFromByteBuf(PacketByteBuf buf)
	{
		return new Vec3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
	}
	
	public static void writeVec3fToByteBuf(PacketByteBuf buf, Vec3f vec)
	{
		buf.writeFloat(vec.getX());
		buf.writeFloat(vec.getY());
		buf.writeFloat(vec.getZ());
	}
	
	public static void writeVec3fToByteBuf(PacketByteBuf buf, Vec3d vec)
	{
		buf.writeFloat((float)vec.x);
		buf.writeFloat((float)vec.y);
		buf.writeFloat((float)vec.z);
	}
	
	public static Vec3d addRandomVector(Vec3d vec, Random random, float randomness)
	{
		return vec.add(random.nextFloat() * randomness, random.nextFloat() * randomness, random.nextFloat() * randomness);
	}
}
