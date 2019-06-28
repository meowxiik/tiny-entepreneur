package com.mewu.plazastar.utils;

public class PrecisePoint {

    public float X;
    public float Y;

    public PrecisePoint(int x, int y) {
        this.X = x;
        this.Y = y;
    }

    public PrecisePoint(float x, float y) {
        this.X = x;
        this.Y = y;
    }


    @Override
    public int hashCode() {
        return (int) (X * Y);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PrecisePoint))
            return false;

        PrecisePoint p2 = (PrecisePoint) o;

        return this.X == p2.X && this.Y == p2.Y;
    }
}
