package huntingrl.world;

import com.badlogic.ashley.core.Entity;
import com.flowpowered.noise.Noise;
import com.flowpowered.noise.NoiseQuality;
import huntingrl.ecs.ComponentMappers;
import huntingrl.ecs.components.*;
import huntingrl.util.Math.Point;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class World {

    private final int elevationSeed;
    private final int treeSeed;
    private final int humiditySeed;
    private final int shrubSeed;
    private final int grassSeed;

    public static final short WATER_ELEVATION = 50;

    private static final double[] NOISE_FREQUENCIES = {0.15, 0.5, 1, 5, 10, 20, 50};
    private static final double[] NOISE_AMPLITUDES = {0.65, 0.2, 0.05, 0.01, 0.005, 0.002, 0.001}; //try not to let the sum of these exceed 1.0
    private static final double NOISE_REDISTRIBUTION_FACTOR = 2;
    private static final double MAX_ELEVATION = 255;
    private static final double WORLD_SCALE_MOD = 256;
    private static final long TREE_DISTANCE_AT_MAX_HUMIDITY = 2;
    private static final long TREE_DISTANCE_AT_MIN_HUMIDITY = 8;
    private static final double MIN_HUMIDITY_FOR_TREES_TO_APPEAR = 0.15;
    private static final double TREE_NOISE_FREQUENCY = 0.5;

    private static final long SHRUB_MIN_DISTANCE = 3;
    private static final double SHRUB_NOISE_FREQUENCY = 0.5;

    private static final long GRASS_MIN_DISTANCE = 1;
    private static final double GRASS_NOISE_FREQUENCY = 0.5;


    public World(int seed) {
        this.elevationSeed = seed;
        treeSeed = elevationSeed + 100;
        humiditySeed = treeSeed + 100;
        shrubSeed = humiditySeed + 100;
        grassSeed = shrubSeed + 100;
    }

    public WorldPoint pointAt(long x, long y, short scale) {
        return new WorldPoint(new Point(x + (scale / 2), y + (scale / 2)),
                (short)elevationAtCoords(x + (scale / 2), y + (scale / 2)),
                humidityAtCoords(x + (scale / 2), y + (scale / 2)));
    }

    private double elevationAtCoords(long x, long y){
        double elevationAtPoint = generateValueFromHarmonicNoiseAtPoint(x, y, elevationSeed);
        elevationAtPoint = Math.pow(elevationAtPoint * 2, NOISE_REDISTRIBUTION_FACTOR) / Math.pow(2, NOISE_REDISTRIBUTION_FACTOR);

        //convert 0.0-1.0 elevation into world elevation
        return elevationAtPoint * MAX_ELEVATION;
    }

    private double generateValueFromHarmonicNoiseAtPoint(long x, long y, int seed) {
        double valueAtPoint = 0;
        for(int i = 0; i < NOISE_FREQUENCIES.length; i++) {
            double foo = Noise.valueCoherentNoise3D(
                    x * NOISE_FREQUENCIES[i] / WORLD_SCALE_MOD, y * NOISE_FREQUENCIES[i] / WORLD_SCALE_MOD,
                    0, seed+i, NoiseQuality.STANDARD);
            valueAtPoint += NOISE_AMPLITUDES[i] * foo;
        }
        return valueAtPoint;
    }

    private double humidityAtCoords(long x, long y) {
        return generateValueFromHarmonicNoiseAtPoint(x, y, humiditySeed);
    }

    public List<Entity> entitiesInBounds(long minX, long minY, long maxX, long maxY) {
        List<Entity> entities = new ArrayList<Entity>();
        List<Entity> trees = treesInBounds(minX, minY, maxX, maxY);
        List<Entity> shrubs = shrubsInBounds(minX, minY, maxX, maxY);
        List<Entity> grass = grassInBounds(minX, minY, maxX, maxY);

        //prioritize trees > shrubs > grass.  Delete shrubs or grass that are on tree tiles, and grass that is on shrub tiles
        removeDuplicatePositionEntities(trees, shrubs);
        removeDuplicatePositionEntities(trees, grass);
        removeDuplicatePositionEntities(shrubs, grass);

        entities.addAll(trees);
        entities.addAll(shrubs);
        entities.addAll(grass);
        return entities;
    }

    private List<Entity> treesInBounds(long minX, long minY, long maxX, long maxY) {
        List<Entity> trees = new ArrayList<>();
        for(long xc = minX; xc < maxX; xc++) {
            for(long yc = minY; yc < maxY; yc++) {
                double humidityAtThisTile = humidityAtCoords(xc, yc);
                if (elevationAtCoords(xc, yc) > WATER_ELEVATION && humidityAtThisTile > MIN_HUMIDITY_FOR_TREES_TO_APPEAR) {
                    double bestVal = 0;
                    long treeDistance = TREE_DISTANCE_AT_MIN_HUMIDITY  - Math.round(humidityAtThisTile * Math.abs(TREE_DISTANCE_AT_MAX_HUMIDITY - TREE_DISTANCE_AT_MIN_HUMIDITY));
                    for (long xn = xc - treeDistance; xn < xc + treeDistance; xn++) {
                        for (long yn = yc - treeDistance; yn < yc + treeDistance; yn++) {
                            double e = generateValueFromSingleFrequencyNoise(xn, yn, treeSeed, TREE_NOISE_FREQUENCY);
                            if (e > bestVal) {
                                bestVal = e;
                            }
                        }
                    }
                    if(generateValueFromSingleFrequencyNoise(xc, yc, treeSeed, TREE_NOISE_FREQUENCY) == bestVal) {
                        trees.add(generateTree(new Point(xc, yc)));
                    }
                }
            }
        }
        return trees;
    }

    private List<Entity> shrubsInBounds(long minX, long minY, long maxX, long maxY) {
        List<Entity> shrubs = new ArrayList<>();
        for(long xc = minX; xc < maxX; xc++) {
            for(long yc = minY; yc < maxY; yc++) {
                if (elevationAtCoords(xc, yc) > WATER_ELEVATION) {
                    double bestVal = 0;
                    for (long xn = xc - SHRUB_MIN_DISTANCE; xn < xc + SHRUB_MIN_DISTANCE; xn++) {
                        for (long yn = yc - SHRUB_MIN_DISTANCE; yn < yc + SHRUB_MIN_DISTANCE; yn++) {
                            double e = generateValueFromSingleFrequencyNoise(xn, yn, shrubSeed, SHRUB_NOISE_FREQUENCY);
                            if (e > bestVal) {
                                bestVal = e;
                            }
                        }
                    }
                    if(generateValueFromSingleFrequencyNoise(xc, yc, shrubSeed, SHRUB_NOISE_FREQUENCY) == bestVal) {
                        shrubs.add(generateShrub(new Point(xc, yc)));
                    }
                }
            }
        }
        return shrubs;
    }

    private List<Entity> grassInBounds(long minX, long minY, long maxX, long maxY) {
        List<Entity> grass = new ArrayList<>();
        for(long xc = minX; xc < maxX; xc++) {
            for(long yc = minY; yc < maxY; yc++) {
                if (elevationAtCoords(xc, yc) > WATER_ELEVATION) {
                    double bestVal = 0;
                    for (long xn = xc - GRASS_MIN_DISTANCE; xn < xc + GRASS_MIN_DISTANCE; xn++) {
                        for (long yn = yc - GRASS_MIN_DISTANCE; yn < yc + GRASS_MIN_DISTANCE; yn++) {
                            double e = generateValueFromSingleFrequencyNoise(xn, yn, grassSeed, GRASS_NOISE_FREQUENCY);
                            if (e > bestVal) {
                                bestVal = e;
                            }
                        }
                    }
                    if(generateValueFromSingleFrequencyNoise(xc, yc, grassSeed, GRASS_NOISE_FREQUENCY) == bestVal) {
                        grass.add(generateGrass(new Point(xc, yc)));
                    }
                }
            }
        }
        return grass;
    }

    private double generateValueFromSingleFrequencyNoise(long x, long y, int seed, double frequency) {
        return Noise.gradientCoherentNoise3D(
                x * frequency, y * frequency,
                0, seed, NoiseQuality.STANDARD);
    }

    private Entity generateTree(Point point) {
        Entity entity = new Entity();
        entity.add(new PositionComponent(point.getX(), point.getY()));
        entity.add(new GraphicsComponent((char) 79, new Color(70, 45, 0), null));
        entity.add(new LocalOnlyComponent());
        entity.add(new BlocksMovementComponent());
        entity.add(new CastsShadowComponent());
        return entity;
    }

    private Entity generateShrub(Point point) {
        Entity entity = new Entity();
        entity.add(new PositionComponent(point.getX(), point.getY()));
        entity.add(new GraphicsComponent((char) 5,
                new Color(0, 60, 0), null));
        entity.add(new LocalOnlyComponent());
        return entity;
    }

    private Entity generateGrass(Point point) {
        Entity entity = new Entity();
        entity.add(new PositionComponent(point.getX(), point.getY()));
        entity.add(new GraphicsComponent((char) 34,
                new Color(0,
                        200,
                        0), null));
        entity.add(new LocalOnlyComponent());
        return entity;
    }

    private void removeDuplicatePositionEntities(List<Entity> primaryList, List<Entity> secondaryList) {
        List<Entity> entitiesToDeleteFromSecondaryList = new ArrayList<>();
        for(Entity secondaryEntity: secondaryList) {
            for(Entity primaryEntity: primaryList) {
                PositionComponent primaryPosition = ComponentMappers.positionMapper.get(primaryEntity);
                PositionComponent secondaryPosition = ComponentMappers.positionMapper.get(secondaryEntity);
                if(primaryPosition.equals(secondaryPosition)) {
                    entitiesToDeleteFromSecondaryList.add(secondaryEntity);
                }
            }
        }
        secondaryList.removeAll(entitiesToDeleteFromSecondaryList);
    }

}
