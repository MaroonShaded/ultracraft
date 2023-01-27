/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *   http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ultreon.craft.world;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.ultreon.craft.block.Block;
import com.ultreon.craft.block.Blocks;

import java.util.Map;

public class World implements RenderableProvider {
	public static final int CHUNK_SIZE = 16;
	public static final int CHUNK_HEIGHT = 16;

	public final Chunk[] chunkArray;
	public final Mesh[] meshArray;
	public final Material[] materialArray;
	public final boolean[] dirty;
	public final int[] numVertices;
	public float[] vertices;
	public final int chunksX;
	public final int chunksY;
	public final int chunksZ;
	public final int voxelsX;
	public final int voxelsY;
	public final int voxelsZ;
	public int renderedChunks;
	public int numChunks;

	private Map<ChunkPos, Chunk> chunks;
	private Map<ChunkPos, Mesh> meshes;
	private Map<ChunkPos, Material> materials;

	public World(Texture texture, int chunksX, int chunksY, int chunksZ) {
		this.chunkArray = new Chunk[chunksX * chunksY * chunksZ];
		this.chunksX = chunksX;
		this.chunksY = chunksY;
		this.chunksZ = chunksZ;
		this.numChunks = chunksX * chunksY * chunksZ;
		this.voxelsX = chunksX * CHUNK_SIZE;
		this.voxelsY = chunksY * CHUNK_HEIGHT;
		this.voxelsZ = chunksZ * CHUNK_SIZE;
		int i = 0;
		for (int y = 0; y < chunksY; y++) {
			for (int z = 0; z < chunksZ; z++) {
				for (int x = 0; x < chunksX; x++) {
					Chunk chunk = new Chunk(CHUNK_SIZE, CHUNK_HEIGHT, CHUNK_SIZE);
					chunk.offset.set(x * CHUNK_SIZE, y * CHUNK_HEIGHT, z * CHUNK_SIZE);
					chunkArray[i++] = chunk;
				}
			}
		}
		int len = CHUNK_SIZE * CHUNK_HEIGHT * CHUNK_SIZE * 6 * 6 / 3;
		short[] indices = new short[len];
		short j = 0;
		for (i = 0; i < len; i += 6, j += 4) {
			indices[i] = j;
			indices[i + 1] = (short)(j + 1);
			indices[i + 2] = (short)(j + 2);
			indices[i + 3] = (short)(j + 2);
			indices[i + 4] = (short)(j + 3);
			indices[i + 5] = j;
		}
		this.meshArray = new Mesh[chunksX * chunksY * chunksZ];
		for (i = 0; i < meshArray.length; i++) {
			meshArray[i] = new Mesh(true, CHUNK_SIZE * CHUNK_HEIGHT * CHUNK_SIZE * 6 * 4,
				CHUNK_SIZE * CHUNK_HEIGHT * CHUNK_SIZE * 36 / 3, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
			meshArray[i].setIndices(indices);
		}
		this.dirty = new boolean[chunksX * chunksY * chunksZ];
		for (i = 0; i < dirty.length; i++)
			dirty[i] = true;

		this.numVertices = new int[chunksX * chunksY * chunksZ];
		for (i = 0; i < numVertices.length; i++)
			numVertices[i] = 0;

		this.vertices = new float[Chunk.VERTEX_SIZE * 6 * CHUNK_SIZE * CHUNK_HEIGHT * CHUNK_SIZE];
		this.materialArray = new Material[chunksX * chunksY * chunksZ];
		for (i = 0; i < materialArray.length; i++) {
			materialArray[i] = new Material(new TextureAttribute(TextureAttribute.Diffuse, texture));
		}
	}

	public void set(float x, float y, float z, Block voxel) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int chunkX = ix / CHUNK_SIZE;
		if (chunkX < 0 || chunkX >= chunksX) return;
		int chunkY = iy / CHUNK_HEIGHT;
		if (chunkY < 0 || chunkY >= chunksY) return;
		int chunkZ = iz / CHUNK_SIZE;
		if (chunkZ < 0 || chunkZ >= chunksZ) return;
		chunkArray[chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ].set(ix % CHUNK_SIZE, iy % CHUNK_HEIGHT, iz % CHUNK_SIZE, voxel);
	}

	public Block get(float x, float y, float z) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int chunkX = ix / CHUNK_SIZE;
		if (chunkX < 0 || chunkX >= chunksX) return Blocks.AIR.get();
		int chunkY = iy / CHUNK_HEIGHT;
		if (chunkY < 0 || chunkY >= chunksY) return Blocks.AIR.get();
		int chunkZ = iz / CHUNK_SIZE;
		if (chunkZ < 0 || chunkZ >= chunksZ) return Blocks.AIR.get();
		
		return chunkArray[chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ].get(ix % CHUNK_SIZE, iy % CHUNK_HEIGHT,iz % CHUNK_SIZE);
	}

	public float getHighest(float x, float z) {
		int ix = (int)x;
		int iz = (int)z;
		if (ix < 0 || ix >= voxelsX) return 0;
		if (iz < 0 || iz >= voxelsZ) return 0;
		// FIXME optimize
		for (int y = voxelsY - 1; y > 0; y--) {
			if (get(ix, y, iz) != Blocks.AIR.get()) return y + 1;
		}
		return 0;
	}

	public void setColumn(float x, float y, float z, Block voxel) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		if (ix < 0 || ix >= voxelsX) return;
		if (iy < 0 || iy >= voxelsY) return;
		if (iz < 0 || iz >= voxelsZ) return;
		// FIXME optimize
		for (; iy > 0; iy--) {
			set(ix, iy, iz, voxel);
		}
	}

	public void setCube(float x, float y, float z, float width, float height, float depth, Block voxel) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int iwidth = (int)width;
		int iheight = (int)height;
		int idepth = (int)depth;
		int startX = Math.max(ix, 0);
		int endX = Math.min(voxelsX, ix + iwidth);
		int startY = Math.max(iy, 0);
		int endY = Math.min(voxelsY, iy + iheight);
		int startZ = Math.max(iz, 0);
		int endZ = Math.min(voxelsZ, iz + idepth);
		// FIXME optimize
		for (iy = startY; iy < endY; iy++) {
			for (iz = startZ; iz < endZ; iz++) {
				for (ix = startX; ix < endX; ix++) {
					set(ix, iy, iz, voxel);
				}
			}
		}
	}

	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		renderedChunks = 0;
		for (int i = 0; i < chunkArray.length; i++) {
			Chunk chunk = chunkArray[i];
			Mesh mesh = meshArray[i];
			if (dirty[i]) {
				int numVerts = chunk.calculateVertices(vertices);
				numVertices[i] = numVerts / 4 * 6;
				mesh.setVertices(vertices, 0, numVerts * Chunk.VERTEX_SIZE);
				dirty[i] = false;
			}
			if (numVertices[i] == 0) continue;
			Renderable renderable = pool.obtain();
			renderable.material = materialArray[i];
			renderable.meshPart.mesh = mesh;
			renderable.meshPart.offset = 0;
			renderable.meshPart.size = numVertices[i];
			renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
			renderables.add(renderable);
			renderedChunks++;
		}
	}
}