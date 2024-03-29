package absolutelyaya.goop.particles;

import absolutelyaya.goop.client.GoopClient;
import absolutelyaya.goop.client.GoopConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class SurfaceAlignedParticle extends SpriteBillboardParticle
{
	static final GoopConfig config;
	
	private final List<Boolean> faceShouldRender = new ArrayList<>();
	private final List<Vec3d> verts = new ArrayList<>();
	private final List<Vec2f> uvs = new ArrayList<>();
	private final List<Float> maxDeform = new ArrayList<>();
	protected final SpriteProvider spriteProvider;
	protected final Vec3d dir;
	
	protected float deformation;
	float targetSize;
	boolean isFancy, deforms;
	
	protected SurfaceAlignedParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider,
									 Vec3d color, float scale, Vec3d dir, boolean deforms)
	{
		super(world, x, y, z);
		this.targetSize = scale;
		this.scale = this.random.nextFloat() * scale * 0.5f + scale * 0.25f;
		this.spriteProvider = spriteProvider;
		sprite = spriteProvider.getSprite(random);
		gravityStrength = 0;
		angle = config.randomRot ? random.nextFloat() * 360 : 0;
		setColor((float)color.getX(), (float)color.getY(), (float)color.getZ());
		
		isFancy = config.fancyGoop;
		this.deforms = deforms;
		
		this.dir = new Vec3d((float)Math.round(dir.x), (float)Math.round(dir.y), (float)Math.round(dir.z));
		boolean b = dir.x != 0;
		if(dir.y != 0)
		{
			if(b)
				markDead();
			b = true;
		}
		if(dir.z != 0 && b)
			markDead();
		
		if(dead)
		{
			this.scale = 0;
			return;
		}
		
		Vec3d modDir = new Vec3d(dir.getX() == 0 ? 1 : 0, dir.getY() == 0 ? 1 : 0, dir.getZ() == 0 ? 1 : 0);
		float subdivisions = isFancy ? Math.max(targetSize, 1) : 1;
		for(int vy = 0; vy <= subdivisions; vy++) //vertexY
		{
			for (int vx = 0; vx <= subdivisions; vx++) //vertexX
			{
				Vec3d vert;
				if(dir.y != 0)
					vert = new Vec3d(modDir.getX() * vx / subdivisions, modDir.getY(), modDir.getZ() * vy / subdivisions);
				else
					vert = new Vec3d(modDir.getX() * vx / subdivisions, modDir.getY() * vy / subdivisions, modDir.getZ() * vx / subdivisions);
				vert = vert.add(dir.multiply(random.nextFloat() / 50)); //fight Z-Fighting
				verts.add(vert);
				faceShouldRender.add(true);
				uvs.add(new Vec2f(MathHelper.lerp(vx / subdivisions, getMinU(), getMaxU()), MathHelper.lerp(vy / subdivisions, getMinV(), getMaxV())));
				if(dir.y == 0)
					maxDeform.add(random.nextFloat());
				else
					maxDeform.add(random.nextBoolean() ? random.nextFloat() * 0.25f * targetSize : 0);
			}
		}
	}
	
	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta)
	{
		boolean debug = config.goopDebug && !MinecraftClient.getInstance().isPaused();
		if(verts.size() == 0)
			return;
		
		Vec3d camPos = camera.getPos();
		float f = (float)(MathHelper.lerp(tickDelta, this.prevPosX, this.x) - camPos.getX());
		float g = (float)(MathHelper.lerp(tickDelta, this.prevPosY, this.y) - camPos.getY());
		float h = (float)(MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - camPos.getZ());
		
		Vec3d dir = this.dir;
		
		List<Vec3d> verts = new ArrayList<>();
		Vec3d modDir = new Vec3d(dir.getX() == 0 ? 1 : 0, dir.getY() == 0 ? 1 : 0, dir.getZ() == 0 ? 1 : 0);
		this.verts.forEach(i ->
		{
			i = i.subtract(new Vec3d((float)(modDir.getX() * 0.5), (float)(modDir.getY() * 0.5), (float)(modDir.getZ() * 0.5)));
			verts.add(i);
		});
		
		AtomicInteger atomicInt = new AtomicInteger();
		for (int i = 0; i < verts.size(); i++)
		{
			Vec3d v = verts.get(i);
			//random rotation
			v = v.rotateX((float)(dir.getX() * angle));
			v = v.rotateY((float)(dir.getY() * angle));
			v = v.rotateZ((float)(dir.getZ() * angle));
			//deformation
			if(deforms && !(this.dir.getY() > 0) && isFancy)
				v = v.subtract(new Vec3d(0, deformation * maxDeform.get(atomicInt.get()), 0));
			v = v.multiply(scale);
			verts.set(i, v.add(f, g, h));
			atomicInt.getAndIncrement();
		}
		
		float targetSize = isFancy ? Math.max(this.targetSize, 1) : 1;
		
		for (int y = 1, vi = 0; y < (int)targetSize + 1; y++, vi++)
		{
			for (int x = 1; x < (int)targetSize + 1; x++, vi++)
			{
				Vec3d[] faceVerts = new Vec3d[] {verts.get(vi), verts.get((int)(vi + targetSize + 1)),
						verts.get((int)(vi + targetSize + 2)), verts.get(vi + 1)};
				
				boolean render = !isFancy || faceShouldRender.get(vi);
				
				if(isFancy && dir.getY() > 0 && faceShouldRender.get(vi))
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
						faceShouldRender.set(vi, false); //so faces don't reappear after being removed
					
					if(debug)
					{
						//face normal, emitted from center
						world.addParticle(ParticleTypes.FLAME,
								camPos.x + faceCenter.getX(), camPos.y + faceCenter.getY() + 0.1, camPos.z + faceCenter.getZ(),
								0, 0.05, 0);
					}
					
					if(config.wrapToEdges && this.targetSize >= 2)
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
					int brightness = getBrightness(tickDelta);
					//face
					vertexConsumer.vertex(faceVerts[0].getX(), faceVerts[0].getY(), faceVerts[0].getZ()).texture(uvs.get(vi).x, uvs.get(vi).y).color(this.red, this.green, this.blue, this.alpha).light(brightness).next();
					vertexConsumer.vertex(faceVerts[1].getX(), faceVerts[1].getY(), faceVerts[1].getZ()).texture(uvs.get((int)(vi + targetSize + 1)).x, uvs.get((int)(vi + targetSize + 1)).y).color(this.red, this.green, this.blue, this.alpha).light(brightness).next();
					vertexConsumer.vertex(faceVerts[2].getX(), faceVerts[2].getY(), faceVerts[2].getZ()).texture(uvs.get((int)(vi + targetSize + 2)).x, uvs.get((int)(vi + targetSize + 2)).y).color(this.red, this.green, this.blue, this.alpha).light(brightness).next();
					vertexConsumer.vertex(faceVerts[3].getX(), faceVerts[3].getY(), faceVerts[3].getZ()).texture(uvs.get(vi + 1).x, uvs.get(vi + 1).y).color(this.red, this.green, this.blue, this.alpha).light(brightness).next();
					//backface
					vertexConsumer.vertex(faceVerts[3].getX(), faceVerts[3].getY(), faceVerts[3].getZ()).texture(uvs.get(vi + 1).x, uvs.get(vi + 1).y).color(this.red, this.green, this.blue, this.alpha).light(brightness).next();
					vertexConsumer.vertex(faceVerts[2].getX(), faceVerts[2].getY(), faceVerts[2].getZ()).texture(uvs.get((int)(vi + targetSize + 2)).x, uvs.get((int)(vi + targetSize + 2)).y).color(this.red, this.green, this.blue, this.alpha).light(brightness).next();
					vertexConsumer.vertex(faceVerts[1].getX(), faceVerts[1].getY(), faceVerts[1].getZ()).texture(uvs.get((int)(vi + targetSize + 1)).x, uvs.get((int)(vi + targetSize + 1)).y).color(this.red, this.green, this.blue, this.alpha).light(brightness).next();
					vertexConsumer.vertex(faceVerts[0].getX(), faceVerts[0].getY(), faceVerts[0].getZ()).texture(uvs.get(vi).x, uvs.get(vi).y).color(this.red, this.green, this.blue, this.alpha).light(brightness).next();
				}
				verts.set(vi, faceVerts[0]);
				verts.set((int)(vi + targetSize + 1), faceVerts[1]);
				verts.set((int)(vi + targetSize + 2), faceVerts[2]);
				verts.set(vi + 1, faceVerts[3]);
			}
		}
		
		if(debug)
		{
			//goop Vertices, colored based on UVs
			for (int i = 0; i < verts.size(); i++)
			{
				Vec3d vertex = verts.get(i);
				Vec2f uv = uvs.get(i);
				world.addParticle(new DustParticleEffect(new Vector3f(uv.x, uv.y, 0f), 0.5f),
						camPos.x + vertex.getX(), camPos.y + vertex.getY() + 0.1, camPos.z + vertex.getZ(),
						0, 0.05, 0);
			}
			//goop Center
			world.addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 1f), 1f), this.x, this.y, this.z,
					0, 0.25, 0);
		}
	}
	
	boolean isValidPos(Vec3d pos)
	{
		BlockPos blockPos = BlockPos.ofFloored(pos);
		VoxelShape shape = world.getBlockState(blockPos).getCollisionShape(world, blockPos);
		if(!shape.isEmpty() && shape.getBoundingBox().offset(blockPos).contains(pos))
			return false;
		Vec3d attachedPos = new Vec3d(pos.x - dir.getX() * 0.065f, pos.y - dir.getY() * 0.065f, pos.z - dir.getZ() * 0.065f);
		BlockPos attachedBlockPos = BlockPos.ofFloored(attachedPos);
		VoxelShape attachedShape = world.getBlockState(attachedBlockPos).getCollisionShape(world, attachedBlockPos);
		return !attachedShape.isEmpty() && attachedShape.getBoundingBox().offset(attachedBlockPos).contains(attachedPos);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if(!isValidPos(new Vec3d(x, y, z)))
			markDead();
		if(deforms)
			deformation = (float)age / maxAge;
	}
	
	private Vec3d moveToBlockEdge(Vec3d vert)
	{
		Vec3d dir = vert.subtract(x, y, z).normalize().multiply(0.33);
		return new Vec3d(Math.round(vert.getX() - dir.x), vert.getY(), Math.round(vert.getZ() - dir.z));
	}
	
	@Override
	protected int getBrightness(float tint)
	{
		BlockPos blockPos = BlockPos.ofFloored(this.x, this.y, this.z);
		return WorldRenderer.getLightmapCoordinates(this.world, blockPos);
	}
	
	static {
		config = GoopClient.getConfig();
	}
}
