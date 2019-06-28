package com.mewu.plazastar.utils;

public class Pair<L,R> {

  public final L P1;
  public final R P2;

  public Pair(L P1, R P2) {
    this.P1 = P1;
    this.P2 = P2;
  }

  @Override
  public int hashCode() { return P1.hashCode() ^ P2.hashCode(); }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Pair))
      return false;

    Pair pairo = (Pair) o;

    return this.P1.equals(pairo.P1) && this.P2.equals(pairo.P2);
  }

}