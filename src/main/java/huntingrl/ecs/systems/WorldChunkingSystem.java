package huntingrl.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import huntingrl.ecs.components.WorldComponent;
import huntingrl.view.panel.ViewFrame;
import huntingrl.world.World;
import huntingrl.world.WorldPoint;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.*;

public class WorldChunkingSystem extends EntitySystem {

    private static final int CHUNK_SIZE_IN_TILES = 8;
    private static final int TIME_TO_UNLOADED_CHUNK_DESPAWN = 100;

    private Map<ViewFrame, Map<ChunkCoords, WorldChunk>> chunkMap = new HashMap<>();
    private World world;

    public void addedToEngine(Engine engine) {
        world = engine.getEntitiesFor(Family.all(WorldComponent.class).get()).first().getComponent(WorldComponent.class).getWorld();
    }

    public WorldPoint[][] retrieveWorldPointsInFrame(ViewFrame frame) {
        //create chunk map if you haven't used this viewframe before.
        if(!chunkMap.containsKey(frame)) {
            chunkMap.put(frame, new HashMap<>());
            return retrieveWorldPointsInFrame(frame);
        }

        //get chunk coords you need to get to cover entire frame
        ChunkCoords[][] chunkCoordsInFrame = chunkCoordsInFrame(frame);

        //get chunks from map, or generate if haven't visited before.
        Map<ChunkCoords, WorldChunk> chunks = chunkMap.get(frame);
        for(int i = 0; i < chunkCoordsInFrame.length; i++) {
            for(int j = 0; j < chunkCoordsInFrame[0].length; j++) {
                ChunkCoords coords = chunkCoordsInFrame[i][j];
                if(!chunks.containsKey(coords)) {
                    System.out.println("GENERATING UNLOADED CHUNK AT " + coords.x + ", " + coords.y + ", TILESIZE=" + frame.getTileSize());
                    chunks.put(coords, new WorldChunk(coords, world, frame.getTileSize()));
                } else {
                    System.out.println("we already have a chunk at " + coords.x + ", " + coords.y + ", TILESIZE=" + frame.getTileSize());
                    chunks.get(coords).resetDespawnTime();
                }
            }
        }

        //using the chunks in the chunk map, get the worldpoint at each xy tile position.
        WorldPoint[][] worldPointsInFrame = new WorldPoint[frame.getPanelBounds().getWidth()][frame.getPanelBounds().getHeight()];
        for(int i = 0; i < frame.getPanelBounds().getWidth(); i++) {
            for(int j = 0; j < frame.getPanelBounds().getHeight(); j++) {
                double worldI = frame.getOffsetWorldX() + (i * frame.getTileSize());
                double worldJ = frame.getOffsetWorldY() + (j * frame.getTileSize());
                ChunkCoords chunkCoordsContainingIJ = new ChunkCoords(roundToChunk(worldI), roundToChunk(worldJ));
                int iInChunk = (int) (worldI - chunkCoordsContainingIJ.getX());
                int jInChunk = (int) (worldJ - chunkCoordsContainingIJ.getY());
                worldPointsInFrame[i][j] = chunks.get(chunkCoordsContainingIJ).getPoints()[iInChunk][jInChunk];
            }
        }

        //decrement all chunk despawn counters, despawn any chunks that haven't been loaded recently
        for(Map.Entry<ChunkCoords, WorldChunk> chunkEntry : chunks.entrySet()) {
            chunkEntry.getValue().decrementDespawnTime();
            if(chunkEntry.getValue().shouldDespawn()) {
                chunks.remove(chunkEntry.getKey());
            }
        }

        return worldPointsInFrame;
    }

    private ChunkCoords[][] chunkCoordsInFrame(ViewFrame frame) {
        int widthInChunks = ((int) Math.floor(((double) frame.getPanelBounds().getWidth()) / CHUNK_SIZE_IN_TILES)) + 1;
        int heightInChunks = ((int) Math.floor(((double)frame.getPanelBounds().getHeight()) / CHUNK_SIZE_IN_TILES)) + 1;
        ChunkCoords[][] chunkCoords = new ChunkCoords[widthInChunks][heightInChunks];
        for(int i = 0; i < widthInChunks; i++) {
            for(int j = 0; j < heightInChunks; j++) {
                chunkCoords[i][j] = new ChunkCoords(roundToChunk(frame.getOffsetWorldX() + (i * CHUNK_SIZE_IN_TILES * frame.getTileSize())),
                        roundToChunk(frame.getOffsetWorldY() + (j * CHUNK_SIZE_IN_TILES * frame.getTileSize())));
            }
        }
        return chunkCoords;
    }

    private int roundToChunk(double val) {
        return ((int) Math.floor(val / CHUNK_SIZE_IN_TILES)) * CHUNK_SIZE_IN_TILES;
    }

    @Getter
    private class WorldChunk {
        double tileSize;
        ChunkCoords coords;
        private WorldPoint[][] points;
        private int timeToDespawn;

        public WorldChunk(ChunkCoords coords, World world, double tileSize) {
            this.coords = coords;
            loadPoints(world);
            resetDespawnTime();
        }

        private void loadPoints(World world) {
            points = new WorldPoint[CHUNK_SIZE_IN_TILES][CHUNK_SIZE_IN_TILES];
            for(int i = 0; i < CHUNK_SIZE_IN_TILES; i++) {
                for(int j = 0; j < CHUNK_SIZE_IN_TILES; j++) {
                    points[i][j] = world.pointAt(coords.x + (i * tileSize), coords.y + (j * tileSize));
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
        private int x;
        private int y;
    }
}
