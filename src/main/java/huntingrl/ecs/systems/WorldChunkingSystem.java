package huntingrl.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
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

public class WorldChunkingSystem extends EntitySystem {

    private static final int CHUNK_SIZE_IN_TILES = 16;
    private static final int TIME_TO_UNLOADED_CHUNK_DESPAWN = 100;

    private Map<ViewFrame, Map<ChunkCoords, Chunk>> chunkMapsForFrames = new HashMap<>();

    private World world;

    public void addedToEngine(Engine engine) {
        world = engine.getEntitiesFor(Family.all(WorldComponent.class).get()).first().getComponent(WorldComponent.class).getWorld();
    }

    public WorldPoint[][] retrieveWorldPointsInFrame(ViewFrame frame) {
        Map<ChunkCoords, Chunk> chunkMap = getChunkMapForFrame(frame);
        WorldPoint[][] worldPoints = loadWorldPointsInFrameFromMap(frame, chunkMap);
        unloadStaleChunks(chunkMap);
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
            chunksForThisViewFrame.put(chunkCoords, chunkForIJ);
        } else {
            chunkForIJ = chunksForThisViewFrame.get(chunkCoords);
        }
        return chunkForIJ;
    }

    private WorldPoint  getWorldPointInChunkFromWorldCoords(long worldX, long worldY, Chunk chunk, ViewFrame frame) {
        int xIndexInsideChunk = ((int) (worldX - chunk.getCoords().worldX)) / frame.getTileSize();
        int yIndexInsideChunk = ((int) (worldY - chunk.getCoords().worldY)) / frame.getTileSize();
        return chunk.getPoints()[xIndexInsideChunk][yIndexInsideChunk];
    }

    private void unloadStaleChunks(Map<ChunkCoords, Chunk> chunkMap) {
        List<ChunkCoords> keysToRemove = new ArrayList<>();
        for(Map.Entry<ChunkCoords, Chunk> chunkEntry : chunkMap.entrySet()) {
            chunkEntry.getValue().decrementDespawnTime();
            if(chunkEntry.getValue().shouldDespawn()) {
                keysToRemove.add(chunkEntry.getKey());
            }
        }
        for(ChunkCoords keyToRemove : keysToRemove) {
            chunkMap.remove(keyToRemove);
        }
    }

    @Getter
    private class Chunk {
        short tileSize;
        ChunkCoords coords;
        private WorldPoint[][] points;
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
                    points[i][j] = world.pointAt(coords.worldX + (i * tileSize), coords.worldY + (j * tileSize));
                }
            }
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
