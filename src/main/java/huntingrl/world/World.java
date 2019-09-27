package huntingrl.world;

import com.flowpowered.noise.Noise;
import com.flowpowered.noise.NoiseQuality;
import lombok.Getter;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

@Getter
public class World {

    private int seed;
    private static final int WORLD_HEIGHT = 100;
    private static final int WORLD_WIDTH = 100;

    private static final double[] NOISE_FREQUENCIES = {8, 16, 32, 64, 128};
    private static final double[] NOISE_AMPLITUDES = {0.5, 0.25, 0.125, 0.0625, 0.03125}; //try not to let the sum of these exceed 1.0
    private static final double NOISE_REDISTRIBUTION_FACTOR = 2.0;
    private static final double MAX_ELEVATION = 255;

    private WorldPoint[][] points = new WorldPoint[100][100];

    public World(int seed) {
        this.seed = seed;
        generatePoints();
    }

    private void generatePoints() {
        for(int i = 0; i < WORLD_WIDTH; i++) {
            for(int j = 0; j < WORLD_HEIGHT; j++) {
                //Calculate elevation between 0.0 and 1.0 using noise function and cool math
                double normalizedX = ((double)i) / ((double)WORLD_WIDTH);
                double normalizedY = ((double)j) / ((double)WORLD_HEIGHT);

                double scaledElevation = elevationAtCoords(normalizedX, normalizedY);

                points[i][j] = new WorldPoint((short) scaledElevation);
            }
        }
        System.out.println("done building world");
    }

    private double elevationAtCoords(double x, double y){
        double elevationAtPoint = 0;
        for(int i = 0; i < NOISE_FREQUENCIES.length; i++) {
            elevationAtPoint += NOISE_AMPLITUDES[i] * Noise.gradientCoherentNoise3D(
                    x * NOISE_FREQUENCIES[i], y * NOISE_FREQUENCIES[i], 0, seed, NoiseQuality.BEST);
        }
        elevationAtPoint = Math.pow(elevationAtPoint, NOISE_REDISTRIBUTION_FACTOR);

        //convert 0.0-1.0 elevation into world elevation
        return elevationAtPoint * 255;
    }

    public WorldPoint pointAt(int x, int y) {
        return points[x][y];
    }

    public boolean pointOutOfBounds(int x, int y) {
        return x < 0 || y < 0 || x >= WORLD_WIDTH || y >= WORLD_HEIGHT;
    }
}
