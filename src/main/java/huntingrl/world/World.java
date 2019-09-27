package huntingrl.world;

import com.flowpowered.noise.Noise;
import com.flowpowered.noise.NoiseQuality;
import lombok.Getter;

@Getter
public class World {

    private final int seed;

    private static final double[] NOISE_FREQUENCIES = {8, 16, 32, 64, 128};
    private static final double[] NOISE_AMPLITUDES = {0.5, 0.25, 0.125, 0.0625, 0.03125}; //try not to let the sum of these exceed 1.0
    private static final double NOISE_REDISTRIBUTION_FACTOR = 2.0;
    private static final double MAX_ELEVATION = 255;

    public World(int seed) {
        this.seed = seed;
    }

    public WorldPoint pointAt(double x, double y) {
        return new WorldPoint((short)elevationAtCoords(x, y));
    }

    private double elevationAtCoords(double x, double y){
        double elevationAtPoint = 0;
        for(int i = 0; i < NOISE_FREQUENCIES.length; i++) {
            elevationAtPoint += NOISE_AMPLITUDES[i] * Noise.gradientCoherentNoise3D(
                    x * NOISE_FREQUENCIES[i], y * NOISE_FREQUENCIES[i], 0, seed, NoiseQuality.FAST);
        }
        elevationAtPoint = Math.pow(elevationAtPoint, NOISE_REDISTRIBUTION_FACTOR);

        //convert 0.0-1.0 elevation into world elevation
        return elevationAtPoint * 255;
    }

    public boolean pointOutOfBounds(int x, int y) {
        return false;
    }
}
