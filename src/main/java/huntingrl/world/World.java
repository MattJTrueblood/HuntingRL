package huntingrl.world;

import com.flowpowered.noise.Noise;
import com.flowpowered.noise.NoiseQuality;
import lombok.Getter;

@Getter
public class World {

    private int seed;
    private static final int WORLD_HEIGHT = 100;
    private static final int WORLD_WIDTH = 100;

    private static final double NOISE_FREQUENCY = 100000.0;

    private WorldPoint[][] points = new WorldPoint[100][100];

    public World(int seed) {
        this.seed = seed;
        generatePoints();
    }

    private void generatePoints() {
        for(int i = 0; i < WORLD_WIDTH; i++) {
            for(int j = 0; j < WORLD_HEIGHT; j++) {
                double elevationAtPoint = Noise.gradientCoherentNoise3D(i * NOISE_FREQUENCY, j * NOISE_FREQUENCY, 0, seed, NoiseQuality.FAST);
                short elevationBetweenZeroAndTen = (short) (elevationAtPoint * 10);
                points[i][j] = new WorldPoint(elevationBetweenZeroAndTen);
                //System.out.println(points[i][j].getElevation());
            }
        }
        System.out.println("done building world");
    }

    public WorldPoint pointAt(int x, int y) {
        return points[x][y];
    }

    public boolean pointOutOfBounds(int x, int y) {
        return x < 0 || y < 0 || x >= WORLD_WIDTH || y >= WORLD_HEIGHT;
    }
}
