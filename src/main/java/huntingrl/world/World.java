package huntingrl.world;

import com.badlogic.ashley.core.Entity;
import com.flowpowered.noise.Noise;
import com.flowpowered.noise.NoiseQuality;
import huntingrl.ecs.components.BlocksMovementComponent;
import huntingrl.ecs.components.GraphicsComponent;
import huntingrl.ecs.components.LocalOnlyComponent;
import huntingrl.ecs.components.PositionComponent;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class World {

    private final int elevationSeed;
    private final int treeSeed;

    public static final short WATER_ELEVATION = 50;

    private static final double[] NOISE_FREQUENCIES = {0.15, 0.5, 1, 5, 10, 20, 50};
    private static final double[] NOISE_AMPLITUDES = {0.65, 0.2, 0.05, 0.01, 0.005, 0.002, 0.001}; //try not to let the sum of these exceed 1.0
    private static final double NOISE_REDISTRIBUTION_FACTOR = 2;
    private static final double MAX_ELEVATION = 255;
    private static final double WORLD_SCALE_MOD = 256;
    private static final long TREE_MIN_DISTANCE = 4;
    private static final double TREE_NOISE_FREQUENCY = 0.5;

    public World(int seed) {
        this.elevationSeed = seed;
        treeSeed = elevationSeed + 1;
    }

    public WorldPoint pointAt(long x, long y, short scale) {
        return new WorldPoint(new WorldCoord(x + (scale / 2), y + (scale / 2)), (short)elevationAtCoords(x + (scale / 2), y + (scale / 2)));
    }

    private double elevationAtCoords(long x, long y){
        double elevationAtPoint = 0;
        for(int i = 0; i < NOISE_FREQUENCIES.length; i++) {
            elevationAtPoint += NOISE_AMPLITUDES[i] * Noise.gradientCoherentNoise3D(
                    x * NOISE_FREQUENCIES[i] / WORLD_SCALE_MOD, y * NOISE_FREQUENCIES[i] / WORLD_SCALE_MOD,
                    0, elevationSeed, NoiseQuality.STANDARD);
        }
        elevationAtPoint = Math.pow(elevationAtPoint * 2, NOISE_REDISTRIBUTION_FACTOR) / Math.pow(2, NOISE_REDISTRIBUTION_FACTOR);

        //convert 0.0-1.0 elevation into world elevation
        return elevationAtPoint * MAX_ELEVATION;
    }

    public List<Entity> entitiesInBounds(long minX, long minY, long maxX, long maxY) {
        return treesInBounds(minX, minY, maxX, maxY);
    }

    private List<Entity> treesInBounds(long minX, long minY, long maxX, long maxY) {
        List<Entity> trees = new ArrayList<>();
        for(long xc = minX; xc < maxX; xc++) {
            for(long yc = minY; yc < maxY; yc++) {
                if (elevationAtCoords(xc, yc) > WATER_ELEVATION) {
                    double bestVal = 0;
                    for (long xn = xc - TREE_MIN_DISTANCE; xn < xc + TREE_MIN_DISTANCE; xn++) {
                        for (long yn = yc - TREE_MIN_DISTANCE; yn < yc + TREE_MIN_DISTANCE; yn++) {
                            double e = treeNoiseValueAt(xn, yn);
                            if (e > bestVal) {
                                bestVal = e;
                            }
                        }
                    }
                    if(treeNoiseValueAt(xc, yc) == bestVal) {
                        trees.add(generateTree(new WorldCoord(xc, yc)));
                    }
                }
            }
        }
        return trees;
    }

    private double treeNoiseValueAt(long x, long y) {
        return Noise.gradientCoherentNoise3D(
                x * TREE_NOISE_FREQUENCY, y * TREE_NOISE_FREQUENCY,
                0, treeSeed, NoiseQuality.STANDARD);
    }

    private Entity generateTree(WorldCoord point) {
        Entity entity = new Entity();
        entity.add(new PositionComponent(point.x, point.y));
        entity.add(new GraphicsComponent((char) 5, new Color(0, 10, 0), null));
        entity.add(new LocalOnlyComponent());
        entity.add(new BlocksMovementComponent());
        return entity;
    }

}
