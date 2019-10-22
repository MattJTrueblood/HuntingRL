package huntingrl.util.Math;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

public class MathUtils {

    /*
     * https://rosettacode.org/wiki/Bitmap/Bresenham%27s_line_algorithm
     */
    public static Point[] bresenhamLine(Point start, Point end) {
        // delta of exact value and rounded value of the dependent variable
        long d = 0;

        long dx = Math.abs(end.getX() - start.getX());
        long dy = Math.abs(end.getY() - start.getY());

        long dx2 = 2 * dx; // slope scaling factors to
        long dy2 = 2 * dy; // avoid floating point

        int ix = start.getX() < end.getX() ? 1 : -1; // increment direction
        int iy = start.getY() < end.getY() ? 1 : -1;

        long x = start.getX();
        long y = start.getY();

        List<Point> points = new ArrayList<>();
        if (dx >= dy) {
            while (true) {
                points.add(new Point(x, y));
                if (x == end.getX())
                    break;
                x += ix;
                d += dy2;
                if (d > dx) {
                    y += iy;
                    d -= dx2;
                }
            }
        } else {
            while (true) {
                points.add(new Point(x, y));
                if (y == end.getY())
                    break;
                y += iy;
                d += dx2;
                if (d > dy) {
                    x += ix;
                    d -= dy2;
                }
            }
        }
        return points.toArray(new Point[0]);
    }
}
