package absolutelyaya.goop.client.particle;

import absolutelyaya.goop.client.GoopClient;
import absolutelyaya.goop.client.config.GoopClientConfig;
import absolutelyaya.goop.data.FinalGoopData;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class SurfaceAlignedParticle extends SpriteBillboardParticle
{
	protected final SpriteProvider spriteProvider;
	final FinalGoopData data;
	final Direction up;
	final List<Vertex> vertices;
	protected float deformation;
	
	protected SurfaceAlignedParticle(ClientWorld clientWorld, Vec3d pos, SpriteProvider spriteProvider, FinalGoopData data, Direction up)
	{
		super(clientWorld, pos.x, pos.y, pos.z);
		this.maxAge = GoopClientConfig.INSTANCE.permanent.getValue() ? Integer.MAX_VALUE : 200 + random.nextInt(100);
		this.spriteProvider = spriteProvider;
		this.data = data;
		this.up = up;
		sprite = spriteProvider.getSprite(random);
		gravityStrength = 0;
		if(GoopClientConfig.INSTANCE.puddleRot.getValue())
			angle = random.nextFloat() * 360;
		
		int ci = GoopClient.getColorOrCensor(data);
		Color c = new Color(ci, (ci >> 24 & 0xff) > 0);
		setColor(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
		alpha = c.getAlpha() / 255f;
		
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
				{
					float x = up.getOffsetX(), z = up.getOffsetZ();
					vert = new Vec3d(z * vx / subdivisions, vy / subdivisions, x * vx / subdivisions);
				}
				vert = vert.add(up.getDoubleVector().multiply(random.nextFloat() / 50f)); //fight Z-Fighting
				Vec2f uv = new Vec2f(MathHelper.lerp(vx / subdivisions, getMinU(), getMaxU()), MathHelper.lerp(vy / subdivisions, getMinV(), getMaxV()));
				
				float maxDeform;
				if(up.getAxis().isVertical())
					maxDeform = (float)Math.pow(random.nextFloat(), 4f) * 0.65f;
				else
					maxDeform = random.nextBoolean() ? random.nextFloat() * 0.2f * data.scale() : 0;
				builder.add(new Vertex(vert, true, uv, maxDeform));
			}
		}
		vertices = builder.build();
	}
	
	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float delta)
	{
		boolean debug = GoopClientConfig.INSTANCE.debug.getValue() && !MinecraftClient.getInstance().isPaused();
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
			v = v.rotateX(up.getOffsetX() * angle);
			v = v.rotateY(up.getOffsetY() * angle);
			v = v.rotateZ(up.getOffsetZ() * angle);
			v = v.multiply(scale);
			verts.set(i, v.add(dx, dy, dz));
		}
		
		float targetSize = isFancy() ? Math.max(data.scale(), 1) : 1;
		
		for (int y = 1, vi = 0; y < (int)targetSize + 1; y++, vi++)
		{
			for (int x = 1; x < (int)targetSize + 1; x++, vi++)
			{
				if(vi + targetSize + 2 >= verts.size())
					continue;
				Vec3d[] faceVerts = new Vec3d[] {
						verts.get(vi),
						verts.get((int)(vi + targetSize + 1)),
						verts.get((int)(vi + targetSize + 2)),
						verts.get(vi + 1)};
				
				boolean render = !fancy || vertices.get(vi).visible;
				
				if(fancy && vertices.get(vi).visible && isProcessFancy())
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
					{
						vertices.get(vi).visible = false; //so faces don't reappear after being removed
						continue;
					}
					
					//deformation
					if(data.deform() && !(up.equals(Direction.UP)))
					{
						faceVerts[0] = faceVerts[0].subtract(new Vec3d(0, deformation * vertices.get(vi).maxDeform, 0));
						faceVerts[1] = faceVerts[1].subtract(new Vec3d(0, deformation * vertices.get((int)(vi + targetSize + 1)).maxDeform, 0));
						faceVerts[2] = faceVerts[2].subtract(new Vec3d(0, deformation * vertices.get((int)(vi + targetSize + 2)).maxDeform, 0));
						faceVerts[3] = faceVerts[3].subtract(new Vec3d(0, deformation * vertices.get(vi + 1).maxDeform, 0));
					}
					
					if(debug && age % 10 == 0)
					{
						//face normal, emitted from center
						Vec3d vel = up.getDoubleVector().multiply(0.05f);
						world.addParticleClient(ParticleTypes.FLAME,
								camPos.x + faceCenter.getX(), camPos.y + faceCenter.getY() + 0.1, camPos.z + faceCenter.getZ(),
								vel.x, vel.y, vel.z);
					}
					
					if(GoopClientConfig.INSTANCE.wrapToEdges.getValue() && data.scale() >= 2)
					{
						for (int i = 0; i < faceVerts.length; i++)
						{
							Vec3d mv = faceVerts[i];
							mv = mv.add(camPos);
							if(!isValidPos(mv))
								mv = moveToBlockEdge(mv);
							faceVerts[i] = mv.subtract(camPos);
						}
					}
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
					
					if(debug && age % 9 == 0)
					{
						//goop Vertices, colored based on UVs
						for (Vec3d vertex : faceVerts)
						{
							world.addParticleClient(new DustParticleEffect(new Color(x / targetSize, y / targetSize, 0f).getRGB(), 0.5f),
									camPos.x + vertex.getX(), camPos.y + vertex.getY() + 0.1, camPos.z + vertex.getZ(), 0, 0.05, 0);
						}
					}
				}
			}
		}
		
		if(debug && age % 3 == 0)
		{
			//goop Center
			world.addParticleClient(new DustParticleEffect(0xffffff, 1f), this.x, this.y, this.z, 0, 0.25, 0);
		}
	}
	
	boolean isFancy()
	{
		if(!GoopClientConfig.INSTANCE.fancy.getValue())
			return false;
		return MinecraftClient.getInstance().gameRenderer.getCamera().getPos().squaredDistanceTo(x, y, z) < 64 * 64;
	}
	
	boolean isProcessFancy()
	{
		return isFancy() && MinecraftClient.getInstance().gameRenderer.getCamera().getPos().squaredDistanceTo(x, y, z) < 32 * 32;
	}
	
	boolean isValidPos(Vec3d pos)
	{
		HitResult hit = world.raycast(new RaycastContext(pos, pos.offset(up, 0.15f),
				RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, ShapeContext.absent()));
		if(!hit.getType().equals(HitResult.Type.MISS))
			return false;
		hit = world.raycast(new RaycastContext(pos, pos.offset(up.getOpposite(), 0.15f),
				RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, ShapeContext.absent()));
		return !hit.getType().equals(HitResult.Type.MISS);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if(!isValidPos(new Vec3d(x, y, z)))
			markDead();
		if(data.deform())
			deformation = (float)age / maxAge;
	}
	
	private Vec3d moveToBlockEdge(Vec3d vert)
	{
		for (Direction.Axis axis : Direction.Axis.VALUES)
		{
			if(axis.equals(up.getAxis()))
				continue;
			double floored = Math.floor(vert.getComponentAlongAxis(axis));
			if(isValidPos(vert.withAxis(axis, floored)))
			{
				vert = vert.withAxis(axis, floored);
				continue;
			}
			double ceiled = Math.ceil(vert.getComponentAlongAxis(axis));
			if(isValidPos(vert.withAxis(axis, ceiled)))
				vert = vert.withAxis(axis, ceiled);
		}
		if(!isValidPos(vert))
		{
			Vec3d tmp = new Vec3d(Math.floor(vert.x), Math.floor(vert.y), Math.floor(vert.z));
			tmp = tmp.withAxis(up.getAxis(), vert.getComponentAlongAxis(up.getAxis()));
			if(isValidPos(tmp))
				return tmp;
			tmp = new Vec3d(Math.ceil(vert.x), Math.ceil(vert.y), Math.ceil(vert.z));
			tmp = tmp.withAxis(up.getAxis(), vert.getComponentAlongAxis(up.getAxis()));
			if(isValidPos(tmp))
				return tmp;
		}
		return vert;
	}
	
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
