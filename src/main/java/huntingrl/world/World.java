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

    public World(int seed) {
        this.elevationSeed = seed;
        treeSeed = elevationSeed + 1;
    }

    public WorldPoint pointAt(double x, double y) {
        return new WorldPoint((short)elevationAtCoords(x, y));
    }

    private double elevationAtCoords(double x, double y){
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

}
