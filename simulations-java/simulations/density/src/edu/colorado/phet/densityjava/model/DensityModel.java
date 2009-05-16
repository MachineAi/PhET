package edu.colorado.phet.densityjava.model;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class DensityModel {
    private SwimmingPool swimmingPool = new SwimmingPool();

    private Sphere sphere = new Sphere();
    private Scale scale = new Scale();
    private double waterVolume = swimmingPool.getVolume() * 0.8;
    private ArrayList<Block> blocks = new ArrayList<Block>();
    private Water water = new Water(swimmingPool, waterVolume, new WaterHeightMapper() {
        public double getWaterHeight(double waterVolume) {
            double proposedHeight = waterVolume / swimmingPool.getWidth() / swimmingPool.getDepth();
            for (int i = 0; i < 10; i++) {//use an iterative algorithm since I can't find any nice analytical solution
                proposedHeight = getEffectiveVolume(proposedHeight) / swimmingPool.getWidth() / swimmingPool.getDepth();
            }
            return proposedHeight;  //To change body of implemented methods use File | Settings | File Templates.
        }

        private double getEffectiveVolume(double proposedHeight) {
            double subVol = 0.0;
            for (Block block : blocks) subVol += block.getSubmergedVolume(proposedHeight);
            return waterVolume + subVol;
        }
    });

    public static interface Listener {
        void blockAdded(Block block);
    }

    private ArrayList<Listener> listeners = new ArrayList<Listener>();

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void stepInTime(double simulationTimeChange) {
        //update the blocks and the forces on the blocks
        for (Block block : blocks) {
            block.stepInTime(simulationTimeChange);
        }
    }

    public void setFourBlocksSameMass() {
        clearBlocks();
        addBlock(new Block("Block 1", 1, water, -1, swimmingPool.getMaxY() + 5));
        addBlock(new Block("Block 2", 1, water, 1, swimmingPool.getMaxY() + 5));
        addBlock(new Block("Block 3", 2, water, 4, swimmingPool.getMaxY() + 5));
        addBlock(new Block("Block 4", 2, water, 7, swimmingPool.getMaxY() + 5));
    }

    public void setFourBlocksSameVolume() {
        clearBlocks();
        addBlock(new Block("Block 1", 1, water, -1, swimmingPool.getMaxY() + 5));
        addBlock(new Block("Block 2", 1, water, 1, swimmingPool.getMaxY() + 5));
        addBlock(new Block("Block 3", 2, water, 4, swimmingPool.getMaxY() + 5));
        addBlock(new Block("Block 4", 2, water, 7, swimmingPool.getMaxY() + 5));
    }

    private void addBlock(Block block) {
        blocks.add(block);
        block.addListener(listener);
        notifyBlockAdded(block);
    }

    private void notifyBlockAdded(Block block) {
        for (Listener listener : listeners) {
            listener.blockAdded(block);
        }
    }

    private void clearBlocks() {
        while (blocks.size() > 0) {
            removeBlock(blocks.get(0));
        }
    }

    private void removeBlock(Block block) {
        block.removeListener(listener);
        block.notifyRemoving();
        blocks.remove(block);
    }

    public int getBlockCount() {
        return blocks.size();
    }

    public Block getBlock(int i) {
        return blocks.get(i);
    }

    public static interface WaterHeightMapper {
        double getWaterHeight(double waterVolume);
    }

    final RectangularObject.Listener listener = new RectangularObject.Adapter() {
        public void modelChanged() {
            updateWaterHeight();
        }
    };

    public DensityModel() {
        setFourBlocksSameVolume();
        //as blocks go underwater, water level should rise
        water.updateWaterHeight();
    }

    private void updateWaterHeight() {
        water.updateWaterHeight();
    }

    public SwimmingPool getSwimmingPool() {
        return swimmingPool;
    }

    public Water getWater() {
        return water;
    }

    public Sphere getSphere() {
        return sphere;
    }

    public Scale getScale() {
        return scale;
    }

    public static class Block extends RectangularObject {
        private double mass = 5;
        private double velocity = 0;
        private Water water;
        private boolean dragging = false;

        Block(String name, double dim, Water water, double x, double y) {
            super(name, x, y, dim, dim, dim, new Color(123, 81, 237));
            this.water = water;
        }

        public void stepInTime(double simulationTimeChange) {
            if (!dragging) {
                double force = getGravityForce() + getBuoyancyForce() + getNormalForce();
                double accel = force / mass;
                velocity += accel * simulationTimeChange;
                setPosition2D(getX(), getY() + velocity * simulationTimeChange);
                if (getY() <= getFloorY()) {
                    setPosition2D(getX(), getFloorY());
                    velocity = 0;
                }
            }
        }

        //the bottom of the pool if over the pool, otherwise the ground level
        //todo: generalize for block stacking
        private double getFloorY() {
            if (water.getWidthRange().contains(getWidthRange()))
                return water.getBottomY();
            else
                return water.getSwimmingPoolSurfaceY();
        }

        private double getNormalForce() {
            if (getY() <= getFloorY() && getGravityForce() + getBuoyancyForce() < 0) {
                return -getGravityForce() - getBuoyancyForce();
            } else return 0;
        }


        public void setVelocity(double v) {
            this.velocity = v;
            notifyListeners();
        }

        //According to Archimedes' principle,
        // "Any object, wholly or partly immersed in a fluid,
        // is buoyed up by a force equal to the weight of the fluid displaced by the object."
        private double getBuoyancyForce() {
            double volumeDisplaced = getSubmergedVolume(water.getHeight());
            double massDisplaced = water.getDensity() * volumeDisplaced / 500;
            double weightDisplaced = massDisplaced * 9.8;
            return weightDisplaced;
        }

        private double getGravityForce() {
            return -9.8 * mass;
        }

        public void setDragging(boolean b) {
            dragging = b;
        }

    }

    static class Sphere {

    }

    static class Scale {

    }

    public static class RectangularObject {
        private String name;
        private double x;
        private double y;
        private double width;
        private double height;
        private double depth;
        private Color faceColor;

        RectangularObject(String name, double x, double y, double width, double height, double depth, Color faceColor) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.depth = depth;
            this.faceColor = faceColor;
        }

        //todo: generalize
        public double getIntersectingVolume(RectangularObject object) {
            double intersectHeight = getOverlapLengths(object.getY(), object.getY() + object.getHeight(),
                    this.getY(), this.getY() + this.getHeight());
            RectangularObject intersection = new RectangularObject("intersection", 0, 0,
                    Math.min(object.getWidth(), this.getWidth()), intersectHeight, Math.min(object.getDepth(), this.getDepth()), Color.black);
            final double volume = intersection.getVolume();
            return volume;
        }

        public Range getWidthRange() {
            return new Range(x, x + width);
        }

        public double getSubmergedVolume(double proposedHeight) {
            return getWidth() * getDepth() * getHeightSubmerged(proposedHeight);
        }

        private double getHeightSubmerged(double waterLevelY) {
            if (waterLevelY < y) return 0;
            else if (waterLevelY > y + height) return height;
            else return waterLevelY - y;
        }

        public double getMaxY() {
            return y + height;
        }

        public void removeListener(Listener listener) {
            listeners.remove(listener);
        }

        static class Range {
            private double min;
            private double max;

            Range(double min, double max) {
                this.min = min;
                this.max = max;
            }

            public double getOverlap(Range b) {
                if (this.contains(b)) {
                    return b.range();
                } else if (b.contains(this)) {
                    return this.range();
                } else if (this.contains(b.min)) {
                    return new Range(b.min, this.max).range();
                } else if (this.contains(b.max)) {
                    return new Range(this.min, b.max).range();
                } else if (b.contains(this.min)) {
                    return new Range(this.min, b.max).range();
                } else if (b.contains(this.max)) {
                    return new Range(b.min, this.max).range();
                } else {
                    return 0;
                }
            }

            private double range() {
                return max - min;
            }

            private boolean contains(Range b) {
                return contains(b.min) && contains(b.max);
            }

            private boolean contains(double x) {
                return x >= min && x <= max;
            }
        }

        private double getOverlapLengths(double a1, double a2, double b1, double b2) {
            Range a = new Range(a1, a2);
            Range b = new Range(b1, b2);
            return a.getOverlap(b);
        }

        public void translate(double dx, double dy) {
            translate(new Point2D.Double(dx, dy));
        }

        public void setHeight(double height) {
            this.height = height;
            notifyListeners();
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getVolume() {
            return width * height * depth;
        }

        public Shape getFrontFace() {
            return new Rectangle2D.Double(x, y, width, height);
        }

        public Color getFaceColor() {
            return faceColor;
        }

        public Shape getTopFace() {
            Point2D startPoint = new Point2D.Double(x, y + height);
            final DoubleGeneralPath path = new DoubleGeneralPath(startPoint);
            path.lineToRelative(width, 0);
            double viewAngle = Math.PI / 4;
            path.lineToRelative(depth * Math.cos(viewAngle), depth * Math.sin(viewAngle));
            path.lineToRelative(-width, 0);
            path.lineTo(startPoint);
            path.closePath();
            return path.getGeneralPath();
        }

        public Shape getRightFace() {
            Point2D startPoint = new Point2D.Double(x + width, y + height);
            final DoubleGeneralPath path = new DoubleGeneralPath(startPoint);
//            path.lineToRelative(width, 0);
            double viewAngle = Math.PI / 4;
            path.lineToRelative(depth * Math.cos(viewAngle), depth * Math.sin(viewAngle));
            path.lineToRelative(0, -height);
            path.lineToRelative(-depth * Math.cos(viewAngle), -depth * Math.sin(viewAngle));
            path.lineTo(startPoint);
            path.closePath();
            return path.getGeneralPath();
        }


        public Paint getTopFaceColor() {
            return new Color(faceColor.brighter().getRed(), faceColor.brighter().getGreen(), faceColor.brighter().getBlue(), faceColor.getAlpha());
        }

        public Paint getRightFaceColor() {
            return new Color(faceColor.darker().getRed(), faceColor.darker().getGreen(), faceColor.darker().getBlue(), faceColor.getAlpha());
        }

        public void translate(Point2D point2D) {
            x += point2D.getX();
            y += point2D.getY();
            notifyListeners();
        }

        public Point2D getPoint2D() {
            return new Point2D.Double(x, y);
        }

        private ArrayList<Listener> listeners = new ArrayList<Listener>();

        public double getCenterX() {
            return x + width / 2;
        }

        public double getCenterY() {
            return y + height / 2;
        }

        public double getCenterZ() {
            return 0 - depth / 2;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }

        public double getDepth() {
            return depth;
        }

        public String getName() {
            return name;
        }

        public void setPosition2D(double x, double y) {
            this.x = x;
            this.y = y;
            notifyListeners();
        }

        public static interface Listener {

            void modelChanged();

            void blockRemoving();
        }

        public static class Adapter implements Listener {

            public void modelChanged() {
            }

            public void blockRemoving() {
            }
        }

        public void addListener(Listener listener) {
            listeners.add(listener);
        }

        protected void notifyListeners() {
            for (Listener listener : listeners) {
                listener.modelChanged();
            }
        }


        public void notifyRemoving() {
            for (Listener listener : listeners) {
                listener.blockRemoving();
            }
        }
    }

    public static class SwimmingPool extends RectangularObject {
        public SwimmingPool() {
            super("Pool", 0, 0, 10, 5, 5, new Color(255, 255, 255));
        }

    }

    public static class Water extends RectangularObject {
        private SwimmingPool container;
        private double waterVolume;
        private WaterHeightMapper waterHeightMapper;

        public Water(SwimmingPool container, double waterVolume, WaterHeightMapper waterHeightMapper) {
            super("Water", container.getX(), container.getY(), container.getWidth(), 4, container.getDepth(), new Color(144, 207, 206, 128));
            this.container = container;
            this.waterVolume = waterVolume;
            this.waterHeightMapper = waterHeightMapper;
            updateWaterHeight();
        }

        private void updateWaterHeight() {
            setHeight(waterHeightMapper.getWaterHeight(waterVolume));
        }

        public double getWaterVolume() {
            return waterVolume;
        }

        public double getDistanceToTopOfPool() {
            return container.getHeight() - getHeight();
        }

        public double getDensity() {
            return 1000;
        }

        public double getBottomY() {
            return container.getY();
        }

        public double getSwimmingPoolSurfaceY() {
            return container.getY() + container.getHeight();
        }

    }
}
