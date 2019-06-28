package com.mewu.plazastar.utils;

public class Point {

    public final int X;
    public final int Y;

    public Point(int x, int y) {
        this.X = x;
        this.Y = y;
    }

    public Point(Point original, int dx, int dy){
        this.X = original.X + dx;
        this.Y = original.Y + dy;
    }

    @Override
    public int hashCode() {
        return X ^ Y;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point))
            return false;

        Point p2 = (Point) o;

        return this.X == p2.X && this.Y == p2.Y;
    }
}
