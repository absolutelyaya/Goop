package absolutelyaya.goop.client.particle;

import absolutelyaya.goop.particle.BaseGoopData;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class SurfaceAlignedParticle extends SpriteBillboardParticle
{
	protected final SpriteProvider spriteProvider;
	final BaseGoopData data;
	final Direction up;
	final List<Vertex> vertices;
	protected float deformation;
	
	protected SurfaceAlignedParticle(ClientWorld clientWorld, Vec3d pos, SpriteProvider spriteProvider, BaseGoopData data, Direction up)
	{
		super(clientWorld, pos.x, pos.y, pos.z);
		this.maxAge = /*config.permanent ? Integer.MAX_VALUE : */200 + random.nextInt(100);
		this.spriteProvider = spriteProvider;
		this.data = data;
		this.up = up;
		sprite = spriteProvider.getSprite(random);
		gravityStrength = 0;
		angle = random.nextFloat() * 360; //TODO: add config for random rotation
		float[] c = new Color(data.color(), true).getColorComponents(null);
		if(c.length >= 3)
			setColor(c[0], c[1], c[2]);
		if(c.length >= 4)
			alpha = c[3];
		
		ImmutableList.Builder<Vertex> builder = new ImmutableList.Builder<>();
		
		float subdivisions = isFancy() ? Math.max(data.scale(), 1) : 1;
		for(int vy = 0; vy <= subdivisions; vy++) //vertexY
		{
			for (int vx = 0; vx <= subdivisions; vx++) //vertexX
			{
				Vec3d vert;
				if(up.getAxis().isVertical())
					vert = new Vec3d(vx / subdivisions - 0.5f, 0f, vy / subdivisions - 0.5f);
				else
					vert = new Vec3d(Math.abs(up.getOffsetZ()) * vx / subdivisions - 0.5f, vy / subdivisions - 0.5f, Math.abs(up.getOffsetX()) * vx / subdivisions - 0.5f);
				vert = vert.add(up.getDoubleVector().multiply(random.nextFloat() / 50f)); //fight Z-Fighting
				Vec2f uv = new Vec2f(MathHelper.lerp(vx / subdivisions, getMinU(), getMaxU()), MathHelper.lerp(vy / subdivisions, getMinV(), getMaxV()));
				
				float maxDeform;
				if(up.getAxis().isVertical())
					maxDeform = random.nextFloat() * 0.5f;
				else
					maxDeform = random.nextBoolean() ? random.nextFloat() * 0.25f * data.scale() : 0;
				builder.add(new Vertex(vert, true, uv, maxDeform));
			}
		}
		vertices = builder.build();
	}
	
	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float delta)
	{
		boolean debug = /*config.goopDebug &&*/ !MinecraftClient.getInstance().isPaused();
		if(vertices.isEmpty())
			return;
		boolean fancy = isFancy();
		
		Vec3d camPos = camera.getPos();
		float dx = (float)(MathHelper.lerp(delta, this.lastX, this.x) - camPos.getX());
		float dy = (float)(MathHelper.lerp(delta, this.lastY, this.y) - camPos.getY());
		float dz = (float)(MathHelper.lerp(delta, this.lastZ, this.z) - camPos.getZ());
		
		List<Vec3d> verts = new ArrayList<>();
		this.vertices.forEach(i ->
			verts.add(i.pos.subtract(new Vec3d(up.getOffsetZ() * 0.5f, up.getAxis().isVertical() ? 0f : 0.5f, up.getOffsetX() * 0.5f))));
		
		for (int i = 0; i < verts.size(); i++)
		{
			Vec3d v = verts.get(i);
			//random rotation
			v = v.rotateX(up.getOffsetZ() * angle);
			v = v.rotateY(up.getOffsetY() * angle);
			v = v.rotateZ(up.getOffsetZ() * angle);
			//deformation
			if(data.deforms() && !(up.equals(Direction.DOWN)) && isFancy())
				v = v.subtract(new Vec3d(0, deformation * vertices.get(i).maxDeform, 0));
			v = v.multiply(scale);
			verts.set(i, v.add(dx, dy, dz));
		}
		
		float targetSize = isFancy() ? Math.max(data.scale(), 1) : 1;
		
		for (int y = 1, vi = 0; y < (int)targetSize + 1; y++, vi++)
		{
			for (int x = 1; x < (int)targetSize + 1; x++, vi++)
			{
				Vec3d[] faceVerts = new Vec3d[] {
						verts.get(vi),
						verts.get((int)(vi + targetSize + 1)),
						verts.get((int)(vi + targetSize + 2)),
						verts.get(vi + 1)};
				
				boolean render = !fancy || vertices.get(vi).visible;
				
				if(fancy && up.equals(Direction.DOWN) && vertices.get(vi).visible)
				{
					//calculate face center
					Vec3d faceCenter = faceVerts[0];
					faceCenter = faceCenter.add(faceVerts[1]);
					faceCenter = faceCenter.add(faceVerts[2]);
					faceCenter = faceCenter.add(faceVerts[3]);
					faceCenter = faceCenter.multiply(0.25f);
					//check if position of this face is attached to a valid surface
					Vec3d v = camPos.add(faceCenter);
					render = isValidPos(v);
					if(!render)
						vertices.get(vi).visible = false; //so faces don't reappear after being removed
					
					if(debug)
					{
						//face normal, emitted from center
						world.addParticleClient(ParticleTypes.FLAME,
								camPos.x + faceCenter.getX(), camPos.y + faceCenter.getY() + 0.1, camPos.z + faceCenter.getZ(),
								0, 0.05, 0);
					}
					
					//if(config.wrapToEdges && this.targetSize >= 2)
					//{
					//	for (int i = 0; i < faceVerts.length; i++)
					//	{
					//		Vec3d mv = faceVerts[i];
					//		mv = mv.add(camPos);
					//		if(!isValidPos(mv))
					//			mv = moveToBlockEdge(mv);
					//		faceVerts[i] = mv.subtract(camPos);
					//	}
					//}
				}
				
				if(render)
				{
					int brightness = getBrightness(delta);
					//face
					vertexConsumer.vertex((float)faceVerts[0].getX(), (float)faceVerts[0].getY(), (float)faceVerts[0].getZ()).
							texture(vertices.get(vi).uv.x, vertices.get(vi).uv.y)
							.color(this.red, this.green, this.blue, this.alpha).light(brightness);
					vertexConsumer.vertex((float)faceVerts[1].getX(), (float)faceVerts[1].getY(), (float)faceVerts[1].getZ()).
							texture(vertices.get((int)(vi + targetSize + 1)).uv.x, vertices.get((int)(vi + targetSize + 1)).uv.y)
							.color(this.red, this.green, this.blue, this.alpha).light(brightness);
					vertexConsumer.vertex((float)faceVerts[2].getX(), (float)faceVerts[2].getY(), (float)faceVerts[2].getZ()).
							texture(vertices.get((int)(vi + targetSize + 2)).uv.x, vertices.get((int)(vi + targetSize + 2)).uv.y)
							.color(this.red, this.green, this.blue, this.alpha).light(brightness);
					vertexConsumer.vertex((float)faceVerts[3].getX(), (float)faceVerts[3].getY(), (float)faceVerts[3].getZ()).
							texture(vertices.get(vi + 1).uv.x, vertices.get(vi + 1).uv.y)
							.color(this.red, this.green, this.blue, this.alpha).light(brightness);
					//backface
					vertexConsumer.vertex((float)faceVerts[3].getX(), (float)faceVerts[3].getY(), (float)faceVerts[3].getZ()).
							texture(vertices.get(vi + 1).uv.x, vertices.get(vi + 1).uv.y)
							.color(this.red, this.green, this.blue, this.alpha).light(brightness);
					vertexConsumer.vertex((float)faceVerts[2].getX(), (float)faceVerts[2].getY(), (float)faceVerts[2].getZ()).
							texture(vertices.get((int)(vi + targetSize + 2)).uv.x, vertices.get((int)(vi + targetSize + 2)).uv.y)
							.color(this.red, this.green, this.blue, this.alpha).light(brightness);
					vertexConsumer.vertex((float)faceVerts[1].getX(), (float)faceVerts[1].getY(), (float)faceVerts[1].getZ()).
							texture(vertices.get((int)(vi + targetSize + 1)).uv.x, vertices.get((int)(vi + targetSize + 1)).uv.y)
							.color(this.red, this.green, this.blue, this.alpha).light(brightness);
					vertexConsumer.vertex((float)faceVerts[0].getX(), (float)faceVerts[0].getY(), (float)faceVerts[0].getZ()).
							texture(vertices.get(vi).uv.x, vertices.get(vi).uv.y)
							.color(this.red, this.green, this.blue, this.alpha).light(brightness);
				}
				//vertices.get(vi).pos = faceVerts[0];
				//vertices.get((int)(vi + targetSize + 1)).pos = faceVerts[1];
				//vertices.get((int)(vi + targetSize + 2)).pos = faceVerts[2];
				//vertices.get(vi + 1).pos = faceVerts[3];
			}
		}
		
		if(debug)
		{
			//goop Vertices, colored based on UVs
			for (int i = 0; i < verts.size(); i++)
			{
				Vec3d vertex = verts.get(i);
				Vec2f uv = vertices.get(i).uv;
				world.addParticleClient(new DustParticleEffect(new Color(uv.x, uv.y, 0f).getRGB(), 0.5f),
						camPos.x + vertex.getX(), camPos.y + vertex.getY() + 0.1, camPos.z + vertex.getZ(), 0, 0.05, 0);
			}
			//goop Center
			world.addParticleClient(new DustParticleEffect(0xffffff, 1f), this.x, this.y, this.z, 0, 0.25, 0);
		}
	}
	
	boolean isFancy()
	{
		return false && MinecraftClient.getInstance().gameRenderer.getCamera().getPos().squaredDistanceTo(x, y, z) < 32; //TODO: config && ...
	}
	
	boolean isValidPos(Vec3d pos)
	{
		BlockPos blockPos = BlockPos.ofFloored(pos);
		VoxelShape shape = world.getBlockState(blockPos).getCollisionShape(world, blockPos);
		if(!shape.isEmpty() && shape.getBoundingBox().offset(blockPos).contains(pos))
			return false;
		Vec3d attachedPos = pos.subtract(up.getDoubleVector().multiply(0.065f));
		BlockPos attachedBlockPos = BlockPos.ofFloored(attachedPos);
		VoxelShape attachedShape = world.getBlockState(attachedBlockPos).getCollisionShape(world, attachedBlockPos);
		return !attachedShape.isEmpty() && attachedShape.getBoundingBox().offset(attachedBlockPos).contains(attachedPos);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		//if(!isValidPos(new Vec3d(x, y, z)))
		//	markDead();
		if(data.deforms())
			deformation = (float)age / maxAge;
	}
	
	//private Vec3d moveToBlockEdge(Vec3d vert)
	//{
	//	Vec3d dir = vert.subtract(x, y, z).normalize().multiply(0.33);
	//	return new Vec3d(Math.round(vert.getX() - dir.x), vert.getY(), Math.round(vert.getZ() - dir.z));
	//}
	
	@Override
	protected int getBrightness(float tint)
	{
		BlockPos blockPos = BlockPos.ofFloored(this.x, this.y, this.z);
		return WorldRenderer.getLightmapCoordinates(this.world, blockPos);
	}
	
	static class Vertex
	{
		Vec3d pos;
		boolean visible;
		Vec2f uv;
		float maxDeform;
		
		public Vertex(Vec3d pos, boolean visible, Vec2f uv, float maxDeform)
		{
			this.pos = pos;
			this.visible = visible;
			this.uv = uv;
			this.maxDeform = maxDeform;
		}
	}
	
	
}
