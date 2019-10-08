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

import java.util.*;

public class WorldChunkingSystem extends EntitySystem {

    private static final int CHUNK_SIZE_IN_TILES = 8;
    private static final int TIME_TO_UNLOADED_CHUNK_DESPAWN = 100;

    private Map<ViewFrame, Map<ChunkCoords, Chunk>> chunkMap = new HashMap<>();

    private World world;

    public void addedToEngine(Engine engine) {
        world = engine.getEntitiesFor(Family.all(WorldComponent.class).get()).first().getComponent(WorldComponent.class).getWorld();
    }

    public WorldPoint[][] retrieveWorldPointsInFrame(ViewFrame frame) {
        //create chunk map if you haven't used this viewframe before.
        if(!chunkMap.containsKey(frame)) {
            chunkMap.put(frame, new HashMap<>());
        }
        Map<ChunkCoords, Chunk> chunksForThisViewFrame = chunkMap.get(frame);

        WorldPoint[][] worldPointsInFrame = new WorldPoint[frame.getPanelBounds().getWidth()][frame.getPanelBounds().getHeight()];
        int chunkSizeInWorldCoords = CHUNK_SIZE_IN_TILES * frame.getTileSize();

        for(int i = 0; i < frame.getPanelBounds().getWidth(); i++) {
            for(int j = 0; j < frame.getPanelBounds().getHeight(); j++) {

                //convert ij to chunk coords.
                long worldX = frame.getOffsetWorldX() + (i * frame.getTileSize());
                long worldY = frame.getOffsetWorldY() + (j * frame.getTileSize());
                ChunkCoords ijChunkCoords = new ChunkCoords(
                        roundWorldCoordToNearestChunk(worldX, chunkSizeInWorldCoords),
                        roundWorldCoordToNearestChunk(worldY, chunkSizeInWorldCoords));

                //check chunk coords in chunk map.  retrieve chunk if in map, otherwise generate new chunk using world
                Chunk chunkForIJ;
                if(!chunksForThisViewFrame.containsKey(ijChunkCoords)) {
                    System.out.println("GENERATING UNLOADED CHUNK AT " + ijChunkCoords.worldX + ", " + ijChunkCoords.worldY +
                            ", TILESIZE=" + frame.getTileSize());
                    chunkForIJ = new Chunk(ijChunkCoords, world, frame.getTileSize());
                    chunksForThisViewFrame.put(ijChunkCoords, chunkForIJ);
                } else {
                    System.out.println("we already have a chunk at " + ijChunkCoords.worldX + ", " + ijChunkCoords.worldY +
                            ", tilesize=" + frame.getTileSize());
                   chunkForIJ = chunksForThisViewFrame.get(ijChunkCoords);
                }

                //get ij point from chunk, store in worldPointsInFrame
                int xIndexInsideChunk = ((int) (worldX - ijChunkCoords.worldX)) / frame.getTileSize();
                int yIndexInsideChunk = ((int) (worldY - ijChunkCoords.worldY)) / frame.getTileSize();
                worldPointsInFrame[i][j] = chunkForIJ.getPoints()[xIndexInsideChunk][yIndexInsideChunk];
            }
        }

        //decrement all chunk despawn counters, despawn any chunks that haven't been loaded recently
        for(Map.Entry<ChunkCoords, Chunk> chunkEntry : chunksForThisViewFrame.entrySet()) {
            chunkEntry.getValue().decrementDespawnTime();
            if(chunkEntry.getValue().shouldDespawn()) {
                chunksForThisViewFrame.remove(chunkEntry.getKey());
            }
        }

        return worldPointsInFrame;
    }

    //this returns in world coords
    private long roundWorldCoordToNearestChunk(long value, int chunkSize) {
        return (value / chunkSize) * chunkSize;
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
    private class ChunkCoords {
        private long worldX;
        private long worldY;
    }
}
