package edu.colorado.phet.statesofmatter;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PBounds;
import junit.framework.TestCase;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class PiccoloTestingUtils {
    private static final double SMALL_SIZE = 1.0 / 3.0;
    private static final double MEDIUM_SIZE = SMALL_SIZE * 2;
    private static final double LARGE_SIZE = SMALL_SIZE * 3;

    public static void testBoundsAreFullyVisible(PNode node, PCanvas canvas) {
        verifyCameraTransformIsIdentity(canvas);

        PBounds visibleBounds = getVisibleBounds(canvas);

        PBounds nodeBounds = node.getGlobalFullBounds();

        TestCase.assertTrue("The bounds of the node " + node.getClass() + ": "+nodeBounds+" do not fully fall within the visible portion of the canvas.", contains(visibleBounds, nodeBounds));
    }

    public static boolean contains(Rectangle2D container,Rectangle2D child){
        return child.isEmpty()?container.contains(child.getX(),child.getY()) :container.contains(child);
    }

    public static boolean intersects(Rectangle2D r1,Rectangle2D r2){
        if (r2.isEmpty()) {
            return r1.contains(r2.getX(), r2.getY());
        }
        else if (r1.isEmpty()) {
            return r2.contains(r1.getX(), r1.getY());
        }
        else {
            return r1.intersects(r2);
        }
    }

    public static void testBoundsAreVisible(PNode node, PCanvas canvas) {
        verifyCameraTransformIsIdentity(canvas);

        PBounds visibleBounds = getVisibleBounds(canvas);

        PBounds nodeBounds = node.getGlobalFullBounds();

        boolean visibleBoundsIntersectsNodeBounds = intersects(visibleBounds, nodeBounds);

        TestCase.assertTrue("The bounds of the node " + node + " do not fall within the visible portion of the canvas.", visibleBoundsIntersectsNodeBounds);
    }

    public static void testBoundsAreNonZero(PNode node) {
        TestCase.assertFalse("The bounds of the node " + node + " are zero.", node.getGlobalFullBounds().isEmpty());
    }

    public static void testIsRoughlyCentered(PNode node, PCanvas canvas) {
        verifyCameraTransformIsIdentity(canvas);

        PBounds visibleBounds = getVisibleBounds(canvas);

        double maxDistance = 1.0 / 5.0 * Math.max(visibleBounds.getWidth(), visibleBounds.getHeight());

        PBounds nodeBounds = node.getGlobalFullBounds();

        Point2D canvasCenter = visibleBounds.getCenter2D();
        Point2D nodeCenter = nodeBounds.getCenter2D();

        double distance = canvasCenter.distance(nodeCenter);

        TestCase.assertTrue("The "+node.getClass() +" node with global full bounds " + nodeBounds + " does not lie near the center of the visible portion of the canvas.", distance < maxDistance);
    }

    public static void testIsSmallSized(PNode node, PCanvas canvas) {
        assertSizeIsCorrect(node, canvas, 0, SMALL_SIZE);
    }

    private static void assertSizeIsCorrect(PNode node, PCanvas canvas, double lowerBound, double upperBound) {
        TestCase.assertTrue("node global full bounds=" + node.getGlobalFullBounds() + " canvas bounds=" + getVisibleBounds(canvas) + ", lower bound=" + lowerBound + ", upperBound=" + upperBound,
                            !isSmallerThan(node, canvas, lowerBound)
                            && isSmallerThan(node, canvas, upperBound));
    }


    public static void testIsMediumSized(PNode node, PCanvas canvas) {
        assertSizeIsCorrect(node, canvas, SMALL_SIZE, MEDIUM_SIZE);
    }

    public static void testIsLargeSized(PNode node, PCanvas canvas) {
        assertSizeIsCorrect(node, canvas, MEDIUM_SIZE, LARGE_SIZE);
    }

    public static boolean isSmallerThan(PNode node, PCanvas canvas, double maxSizeRatio) {
        double limitingSize = Math.min(canvas.getWidth(), canvas.getHeight());
        double nodeDimension = limitingSize == canvas.getWidth() ? node.getGlobalFullBounds().getWidth() : node.getGlobalFullBounds().getHeight();
        double fraction = nodeDimension / limitingSize;
        return fraction < maxSizeRatio;
    }

    private static PBounds getVisibleBounds(PCanvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        return new PBounds(0, 0, width, height);
    }

    private static void verifyCameraTransformIsIdentity(PCanvas canvas) {
        if (!canvas.getCamera().getTransform().equals(new PAffineTransform())) {
            throw new IllegalArgumentException("Cannot deal with camera transform: " + canvas.getCamera().getTransform());
        }
    }

    public static void testIsVisible(PNode node, PCanvas canvas) {
        PNode originalNode = node;

        if (node.getGlobalFullBounds().isEmpty()) {
            TestCase.fail("The node " + originalNode.getClass() + " is not visible because it has zero global full bounds.");
        }

        testBoundsAreVisible(node, canvas);

        while (node != null) {
            if (node == canvas.getLayer()) {
                return;
            }

            node = node.getParent();
        }

        TestCase.fail("The node " + originalNode.getClass() + " is not visible on the canvas " + canvas + ".");
    }
}
