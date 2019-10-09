package huntingrl.world;

import com.flowpowered.noise.Noise;
import com.flowpowered.noise.NoiseQuality;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class World {

    private final int elevationSeed;
    private final int treeSeed;

    private static final double[] NOISE_FREQUENCIES = {8, 16, 32, 64, 128};
    private static final double[] NOISE_AMPLITUDES = {0.5, 0.25, 0.125, 0.0625, 0.03125}; //try not to let the sum of these exceed 1.0
    private static final double NOISE_REDISTRIBUTION_FACTOR = 2.0;
    private static final double MAX_ELEVATION = 255;
    private static final double WORLD_SCALE = 16000;
    private static final long TREE_NOISE_R = 3;
    private static final long TREE_NOISE_FREQUENCY = 128;

    public World(int seed) {
        this.elevationSeed = seed;
        treeSeed = elevationSeed + 1;
    }

    public WorldPoint pointAt(long x, long y) {
        return new WorldPoint(new WorldCoord(x, y), (short)elevationAtCoords(x, y));
    }

    private double elevationAtCoords(long x, long y){
        double elevationAtPoint = 0;
        for(int i = 0; i < NOISE_FREQUENCIES.length; i++) {
            elevationAtPoint += NOISE_AMPLITUDES[i] * Noise.gradientCoherentNoise3D(
                    x * NOISE_FREQUENCIES[i] / WORLD_SCALE, y * NOISE_FREQUENCIES[i] / WORLD_SCALE,
                    0, elevationSeed, NoiseQuality.FAST);
        }
        elevationAtPoint = Math.pow(elevationAtPoint, NOISE_REDISTRIBUTION_FACTOR);

        //convert 0.0-1.0 elevation into world elevation
        return elevationAtPoint * 255;
    }

    public List<WorldCoord> treeLocationsInBounds(long minX, long minY, long maxX, long maxY) {
        List<WorldCoord> treeLocations = new ArrayList<>();
        for(long yc = minY; yc < maxY; yc++) {
            for(long xc = minX; xc < maxX; xc++) {
                double max = 0;
                for(long yn = yc - TREE_NOISE_R; yn <= yc + TREE_NOISE_R; yn++) {
                    for(long xn = xc - TREE_NOISE_R; xn <= xc + TREE_NOISE_R; xn++) {
                        double e = Noise.gradientCoherentNoise3D(
                                xn * TREE_NOISE_FREQUENCY / WORLD_SCALE, yn * TREE_NOISE_FREQUENCY / WORLD_SCALE,
                                0, treeSeed, NoiseQuality.FAST);
                        if(e > max) {
                            max = e;
                        }
                    }
                }
                if(Noise.gradientCoherentNoise3D(
                        xc * TREE_NOISE_FREQUENCY / WORLD_SCALE, yc * TREE_NOISE_FREQUENCY / WORLD_SCALE,
                        0, treeSeed, NoiseQuality.FAST) == max) {
                    treeLocations.add(new WorldCoord(xc, yc));
                }
            }
        }
        return treeLocations;
    }

}
