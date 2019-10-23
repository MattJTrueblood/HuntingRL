package huntingrl.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import huntingrl.ecs.ComponentMappers;
import huntingrl.ecs.components.PositionComponent;
import huntingrl.ecs.components.WorldComponent;
import huntingrl.view.panel.ViewFrame;
import huntingrl.world.World;
import huntingrl.world.WorldPoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.swing.text.View;
import java.util.*;
import java.util.stream.Collectors;

public class WorldChunkingSystem extends EntitySystem {

    private static final int CHUNK_SIZE_IN_TILES = 16;
    private static final int TIME_TO_UNLOADED_CHUNK_DESPAWN = 100;

    private Map<ViewFrame, Map<ChunkCoords, Chunk>> chunkMapsForFrames = new HashMap<>();

    private World world;
    private Engine engine;

    public void addedToEngine(Engine engine) {
        this.engine = engine;
        world = engine.getEntitiesFor(Family.all(WorldComponent.class).get()).first().getComponent(WorldComponent.class).getWorld();
    }

    /**
     * The main function for loading the world in a view frame.  Each frame has a separate chunk map.  If you're adding
     * and removing frames at all ADD A FUNCTION TO ALLOW YOU TO UNREGISTER FRAMES.  Otherwise, you're fine.
     * @param frame
     * @return
     */
    public WorldPoint[][] retrieveWorldPointsInFrame(ViewFrame frame) {
        Map<ChunkCoords, Chunk> chunkMap = getChunkMapForFrame(frame);
        WorldPoint[][] worldPoints = loadWorldPointsInFrameFromMap(frame, chunkMap);
        unloadStaleChunks(chunkMap, frame);
        return worldPoints;
    }

    private Map<ChunkCoords, Chunk> getChunkMapForFrame(ViewFrame frame) {
        if(!chunkMapsForFrames.containsKey(frame)) {
            chunkMapsForFrames.put(frame, new HashMap<>());
        }
        return chunkMapsForFrames.get(frame);
    }

    private WorldPoint[][] loadWorldPointsInFrameFromMap(ViewFrame frame, Map<ChunkCoords, Chunk> chunkMap) {
        int chunkSizeInWorldCoords = CHUNK_SIZE_IN_TILES * frame.getTileSize();
        WorldPoint[][] worldPointsInFrame = new WorldPoint[frame.getPanelBounds().getWidth()][frame.getPanelBounds().getHeight()];
        for(int i = 0; i < frame.getPanelBounds().getWidth(); i++) {
            for(int j = 0; j < frame.getPanelBounds().getHeight(); j++) {
                ChunkCoords ijChunkCoords = getChunkCoordsForIJInViewFrame(i, j, frame, chunkSizeInWorldCoords);
                Chunk chunkForIJ = findOrGenerateChunk(chunkMap, ijChunkCoords, frame);
                worldPointsInFrame[i][j] = getWorldPointInChunkFromWorldCoords(
                        frame.getOffsetWorldX() + (i * frame.getTileSize()),
                        frame.getOffsetWorldY() + (j * frame.getTileSize()),
                        chunkForIJ, frame);
            }
        }
        return worldPointsInFrame;
    }

    private ChunkCoords getChunkCoordsForIJInViewFrame(int i, int j, ViewFrame frame, int chunkSizeInWorldCoords) {
        long worldX = frame.getOffsetWorldX() + (i * frame.getTileSize());
        long worldY = frame.getOffsetWorldY() + (j * frame.getTileSize());
        return getChunkCoordsForChunkContainingWorldCoords(worldX, worldY, chunkSizeInWorldCoords);
    }

    private ChunkCoords getChunkCoordsForChunkContainingWorldCoords(long worldX, long worldY, int chunkSizeInWorldCoords) {
        return new ChunkCoords(
                roundWorldCoordToNearestChunk(worldX, chunkSizeInWorldCoords),
                roundWorldCoordToNearestChunk(worldY, chunkSizeInWorldCoords));
    }

    private long roundWorldCoordToNearestChunk(long value, int chunkSize) {
        return (long) Math.floor((double) value / chunkSize) * chunkSize;
    }

    private Chunk findOrGenerateChunk(Map<ChunkCoords, Chunk> chunksForThisViewFrame, ChunkCoords chunkCoords, ViewFrame frame) {
        Chunk chunkForIJ;
        if(!chunksForThisViewFrame.containsKey(chunkCoords)) {
            chunkForIJ = new Chunk(chunkCoords, world, frame.getTileSize());
            if(frame.isLocalFrame()) {
                chunkForIJ.loadEntities(world);
                loadAllEntitiesInChunk(chunkForIJ);
            }
            chunksForThisViewFrame.put(chunkCoords, chunkForIJ);
        } else {
            chunkForIJ = chunksForThisViewFrame.get(chunkCoords);
        }
        return chunkForIJ;
    }

    private WorldPoint getWorldPointInChunkFromWorldCoords(long worldX, long worldY, Chunk chunk, ViewFrame frame) {
        int xIndexInsideChunk = ((int) (worldX - chunk.getCoords().worldX)) / frame.getTileSize();
        int yIndexInsideChunk = ((int) (worldY - chunk.getCoords().worldY)) / frame.getTileSize();
        return chunk.getPoints()[xIndexInsideChunk][yIndexInsideChunk];
    }

    private void unloadStaleChunks(Map<ChunkCoords, Chunk> chunkMap, ViewFrame frame) {
        List<ChunkCoords> keysToRemove = new ArrayList<>();
        for(Map.Entry<ChunkCoords, Chunk> chunkEntry : chunkMap.entrySet()) {
            chunkEntry.getValue().decrementDespawnTime();
            if(chunkEntry.getValue().shouldDespawn()) {
                if(frame.isLocalFrame()) {
                    unloadAllEntitiesInChunk(chunkEntry.getValue());
                }
                keysToRemove.add(chunkEntry.getKey());
            }
        }
        for(ChunkCoords keyToRemove : keysToRemove) {
            chunkMap.remove(keyToRemove);
        }
    }

    private void loadAllEntitiesInChunk(Chunk chunk) {
        chunk.getEntities().forEach((Entity entity) -> {
            this.engine.addEntity(entity);
        });
    }

    private void unloadAllEntitiesInChunk(Chunk chunk) {
        chunk.getEntities().forEach((Entity entity) -> {
            this.engine.removeEntity(entity);
        });
    }

    /**
     * Gets a single world point from world point x/y coords.  This is expensive, if you are getting multiple points from
     * the same frame or chunk you can easily reuse those objects (see loadWorldPointsInFrameFromMap as an example)
     * @param x
     * @param y
     * @param frame
     * @return
     */
    public WorldPoint getWorldPointAt(long x, long y, ViewFrame frame) {
        int chunkSizeInWorldCoords = CHUNK_SIZE_IN_TILES * frame.getTileSize();
        ChunkCoords ijChunkCoords = getChunkCoordsForChunkContainingWorldCoords(x, y, chunkSizeInWorldCoords);
        Chunk chunk = findOrGenerateChunk(chunkMapsForFrames.get(frame), ijChunkCoords, frame);
        return getWorldPointInChunkFromWorldCoords(x, y, chunk, frame);
    }

    @Getter
    private class Chunk {
        short tileSize;
        ChunkCoords coords;
        private WorldPoint[][] points;
        private List<Entity> entities;
        private int timeToDespawn;

        public Chunk(ChunkCoords coords, World world, short tileSize) {
            this.coords = coords;
            this.tileSize = tileSize;
            loadPoints(world);
            resetDespawnTime();
        }

        private void loadPoints(World world) {
            points = new WorldPoint[CHUNK_SIZE_IN_TILES][CHUNK_SIZE_IN_TILES];
            for(int i = 0; i < CHUNK_SIZE_IN_TILES; i++) {
                for(int j = 0; j < CHUNK_SIZE_IN_TILES; j++) {
                    points[i][j] = world.pointAt(coords.worldX + (i * tileSize), coords.worldY + (j * tileSize), tileSize);
                }
            }
        }

        public void loadEntities(World world) {
            entities = world.entitiesInBounds(coords.worldX, coords.worldY,
                    coords.worldX + (tileSize * CHUNK_SIZE_IN_TILES),
                    coords.worldY + (tileSize * CHUNK_SIZE_IN_TILES));
        }

        private void decrementDespawnTime() {
            timeToDespawn--;
        }

        private void resetDespawnTime() {
            timeToDespawn = TIME_TO_UNLOADED_CHUNK_DESPAWN;
        }

        private boolean shouldDespawn() {
            return timeToDespawn <= 0;
        }
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    private class ChunkCoords {
        private long worldX;
        private long worldY;
    }
}
