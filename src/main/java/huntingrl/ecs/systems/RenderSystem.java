package huntingrl.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import huntingrl.ecs.ComponentMappers;
import huntingrl.ecs.components.*;
import huntingrl.util.Math.MathUtils;
import huntingrl.view.RenderBuffer;
import huntingrl.view.panel.ViewFrame;
import huntingrl.world.World;
import huntingrl.world.WorldPoint;
import huntingrl.util.Math.Point;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class RenderSystem extends EntitySystem {

    private static final int SHADOW_OPACITY = 100;
    private static final Color MAX_NEGATIVE_DELTA_ELEVATION_COLOR = new Color(100, 255, 255);
    private static final Color MAX_POSITIVE_DELTA_ELEVATION_COLOR = new Color(0, 0, 0);
    private static final String COLOR_MAP_IMAGE_URL = "WorldColorMap.bmp";

    private ImmutableArray<Entity> renderableEntities;
    private ImmutableArray<Entity> shadowingEntities;
    private Entity playerEntity;
    private WorldChunkingSystem worldChunkingSystem;

    private RenderBuffer buffer;
    private int[][] worldColorMapPixels;

    public RenderSystem(RenderBuffer buffer) {
        this.buffer = buffer;
        loadWorldColorMap();
    }

    private void loadWorldColorMap() {
        BufferedImage colorMapImage;
        try {
            URL colorMapUrl = this.getClass().getClassLoader().getResource(COLOR_MAP_IMAGE_URL);
            colorMapImage = ImageIO.read(colorMapUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        worldColorMapPixels = new int[255][255];
        for(int i = 0; i < 255; i++) {
            for(int j = 0; j < 255; j++) {
                worldColorMapPixels[i][j] = colorMapImage.getRGB(i, j);
            }
        }
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        playerEntity = engine.getEntitiesFor(Family.all(PlayerComponent.class, PositionComponent.class).get()).first();
        renderableEntities = engine.getEntitiesFor(Family.all(PositionComponent.class, GraphicsComponent.class).get());
        shadowingEntities = engine.getEntitiesFor(Family.all(PositionComponent.class, CastsShadowComponent.class).get());
        worldChunkingSystem = engine.getSystem(WorldChunkingSystem.class);
    }

    public void renderInView(ViewFrame viewFrame) {
        renderWorldInView(viewFrame);
        renderEntitiesInView(viewFrame);
        if(viewFrame.isLocalFrame()) {
            applyFOVToRenderBuffer(viewFrame);
        }
    }
    
    private void renderWorldInView(ViewFrame viewFrame) {
        WorldPoint[][] worldPointsInFrame = worldChunkingSystem.retrieveWorldPointsInFrame(viewFrame);
        PositionComponent playerPosition = playerEntity.getComponent(PositionComponent.class);
        for(int i = 0; i < viewFrame.getPanelBounds().getWidth(); i++) {
            for(int j = 0; j < viewFrame.getPanelBounds().getHeight(); j++) {
                int elevationAtIJ = worldPointsInFrame[i][j].getElevation();
                boolean ijIsUnderwater = elevationAtIJ < World.WATER_ELEVATION;

                Color terrainColor = getBaseTerrainColor(worldPointsInFrame[i][j]);

                if(!ijIsUnderwater) {
                    int playerElevation = worldChunkingSystem.getWorldPointAt(playerPosition.getX(), playerPosition.getY(), viewFrame)
                            .getElevation();
                    if(viewFrame.getProperties().isTerrainColorModLocked()) {
                        playerElevation = World.WATER_ELEVATION;
                    }
                    int deltaElevation = elevationAtIJ - playerElevation;
                    if (deltaElevation > 0) {
                        terrainColor = modifyColorByDeltaElevation(terrainColor, MAX_POSITIVE_DELTA_ELEVATION_COLOR,
                                deltaElevation, viewFrame.getTerrainColorModPositiveSoftness());
                    } else if (deltaElevation < 0) {
                        terrainColor = modifyColorByDeltaElevation(terrainColor, MAX_NEGATIVE_DELTA_ELEVATION_COLOR,
                                Math.abs(deltaElevation), viewFrame.getTerrainColorModNegativeSoftness());
                    }
                }

                buffer.write((char) 0, viewFrame.getPanelBounds().getX() + i,
                        viewFrame.getPanelBounds().getY() + j,
                        Color.GRAY, terrainColor);
            }
        }
    }

    private Color getBaseTerrainColor(WorldPoint point) {
        int rgb = worldColorMapPixels[(short) (point.getHumidity() * 255)][point.getElevation()];
        return new Color(rgb);
    }

    private Color modifyColorByDeltaElevation(Color baseColor, Color goalColor, int deltaElevation, int softness) {
       return new Color(
            ((baseColor.getRed() * softness) + (Math.abs(deltaElevation) * goalColor.getRed()))
                    / (1 + Math.abs(deltaElevation) + softness),
            ((baseColor.getGreen() * softness) + (Math.abs(deltaElevation) * goalColor.getGreen()))
                    / (1 + Math.abs(deltaElevation) + softness),
            ((baseColor.getBlue() * softness) + (Math.abs(deltaElevation) * goalColor.getBlue()))
                    / (1 + Math.abs(deltaElevation) + softness));
    }

    private void renderEntitiesInView(ViewFrame viewFrame) {
        List<Entity> entities = Arrays.asList(renderableEntities.toArray());
        entities.sort(Comparator.comparingInt((Entity entity) -> ComponentMappers.graphicsMapper.get(entity).getZIndex()));
        for(Entity entity: entities) {
            renderEntity(viewFrame, entity);
        }
    }

    private void renderEntity(ViewFrame viewFrame, Entity entity) {
        PositionComponent position = ComponentMappers.positionMapper.get(entity);
        GraphicsComponent graphics = ComponentMappers.graphicsMapper.get(entity);

        if(!viewFrame.isLocalFrame() && ComponentMappers.localOnlyMapper.has(entity)) {
            return;
        }

        if (position.getX() >= viewFrame.getOffsetWorldX() &&
                position.getY() >= viewFrame.getOffsetWorldY() &&
                position.getX() < viewFrame.getOffsetWorldX() + (viewFrame.getPanelBounds().getWidth() * viewFrame.getTileSize()) &&
                position.getY() < viewFrame.getOffsetWorldY() + (viewFrame.getPanelBounds().getHeight() * viewFrame.getTileSize())) {
            //Entity is in frame, render it.  Make sure to convert properly between screen coords and world coords!!!
            if (graphics.getBgColor() == null) {
                buffer.write(graphics.getCharacter(),
                        viewFrame.getPanelBounds().getX() + (int) ((position.getX() - viewFrame.getOffsetWorldX()) / viewFrame.getTileSize()),
                        viewFrame.getPanelBounds().getY() + (int) ((position.getY() - viewFrame.getOffsetWorldY()) / viewFrame.getTileSize()),
                        graphics.getFgColor(), new Color(0f, 0f, 0f, 0f));
            } else {
                buffer.write(graphics.getCharacter(),
                        viewFrame.getPanelBounds().getX() + (int) ((position.getX() - viewFrame.getOffsetWorldX()) / viewFrame.getTileSize()),
                        viewFrame.getPanelBounds().getY() + (int) ((position.getY() - viewFrame.getOffsetWorldY()) / viewFrame.getTileSize()),
                        graphics.getFgColor(), graphics.getBgColor());
            }
        }
    }

    private void applyFOVToRenderBuffer(ViewFrame viewFrame) {
        PositionComponent playerPosition = ComponentMappers.positionMapper.get(playerEntity);

        List<Point> shadowedTiles = new ArrayList<>();
        List<Point> edgeTiles = new ArrayList<>();
        LongStream xRange1 = LongStream.range(viewFrame.getOffsetWorldX(), viewFrame.getOffsetWorldX() + viewFrame.getPanelBounds().getWidth());
        LongStream xRange2 = LongStream.range(viewFrame.getOffsetWorldX(), viewFrame.getOffsetWorldX() + viewFrame.getPanelBounds().getWidth());
        LongStream yRange1 = LongStream.range(viewFrame.getOffsetWorldY(), viewFrame.getOffsetWorldY() + viewFrame.getPanelBounds().getHeight());
        LongStream yRange2 = LongStream.range(viewFrame.getOffsetWorldY(), viewFrame.getOffsetWorldY() + viewFrame.getPanelBounds().getHeight());

        //collect all points on edge of view
        edgeTiles.addAll(xRange1.mapToObj(x -> new Point(x, viewFrame.getOffsetWorldY())).collect(Collectors.toList()));
        edgeTiles.addAll(xRange2.mapToObj(x -> new Point(x, viewFrame.getOffsetWorldY() + viewFrame.getPanelBounds().getHeight())).collect(Collectors.toList()));
        edgeTiles.addAll(yRange1.mapToObj(y -> new Point(viewFrame.getOffsetWorldX(), y)).collect(Collectors.toList()));
        edgeTiles.addAll(yRange2.mapToObj(y -> new Point(viewFrame.getOffsetWorldX() + viewFrame.getPanelBounds().getWidth(), y)).collect(Collectors.toList()));

        edgeTiles.forEach(edgePoint -> {
            Point[] lineFromPlayertoEdgePoint = MathUtils.bresenhamLine(
                    new Point(playerPosition.getX(), playerPosition.getY()), edgePoint);
            boolean castingShadow = false;
            for(Point linePoint : lineFromPlayertoEdgePoint) {
                if(castingShadow) {
                    shadowedTiles.add(linePoint);
                } else {
                    for (Entity shadowingEntity : shadowingEntities) {
                        PositionComponent shadowingPosition = ComponentMappers.positionMapper.get(shadowingEntity);
                        if (shadowingPosition.getX() == linePoint.getX() && shadowingPosition.getY() == linePoint.getY()) {
                            castingShadow = true;
                            break;
                        }
                    }
                }
            }
        });

        List<Point> distinctShadowedTiles = shadowedTiles.stream().distinct().collect(Collectors.toList());
        for(Point point: distinctShadowedTiles) {
            if (point.getX() >= viewFrame.getOffsetWorldX() &&
                    point.getY() >= viewFrame.getOffsetWorldY() &&
                    point.getX() < viewFrame.getOffsetWorldX() + (viewFrame.getPanelBounds().getWidth() * viewFrame.getTileSize()) &&
                    point.getY() < viewFrame.getOffsetWorldY() + (viewFrame.getPanelBounds().getHeight() * viewFrame.getTileSize())) {
                buffer.write((char) 0,
                        viewFrame.getPanelBounds().getX() + (int) ((point.getX() - viewFrame.getOffsetWorldX()) / viewFrame.getTileSize()),
                        viewFrame.getPanelBounds().getY() + (int) ((point.getY() - viewFrame.getOffsetWorldY()) / viewFrame.getTileSize()),
                        Color.RED, new Color(0, 0, 0, SHADOW_OPACITY));
            }
        }

        /*  LINE TEST

        for(Point point : pointsFromPlayerToZeroZero) {
            if (point.getX() >= viewFrame.getOffsetWorldX() &&
                    point.getY() >= viewFrame.getOffsetWorldY() &&
                    point.getX() < viewFrame.getOffsetWorldX() + (viewFrame.getPanelBounds().getWidth() * viewFrame.getTileSize()) &&
                    point.getY() < viewFrame.getOffsetWorldY() + (viewFrame.getPanelBounds().getHeight() * viewFrame.getTileSize())) {
                buffer.write((char) 37,
                        viewFrame.getPanelBounds().getX() + (int) ((point.getX() - viewFrame.getOffsetWorldX()) / viewFrame.getTileSize()),
                        viewFrame.getPanelBounds().getY() + (int) ((point.getY() - viewFrame.getOffsetWorldY()) / viewFrame.getTileSize()),
                        Color.RED, new Color(0, 0, 0, 0));
            }
        }
         */
    }
}


