package org.dodgyjammers.jammypiece.musickb;

import java.util.LinkedList;
import java.util.List;

/**
 * Map of "nice" chord progressions.
 */
public class ChordMap
{
  public static class DetailedChord
  {
    /**
     * The chord.
     */
    public final Chord mChord;

    /**
     * The number of chords in the shortest path home (exclusive of this one, inclusive of home).
     */
    public final int mDistanceFromHome;

    /**
     * The chords that are reachable from here.
     *
     * This list must not be mutated by users!
     */
    public final List<DetailedChord> mNeighbours;

    /**
     * Create a detailed chord.
     *
     * @param xiChord - the chord.
     * @param xiDistanceFromHome - the distance from home.
     */
    public DetailedChord(Chord xiChord, int xiDistanceFromHome)
    {
      mChord = xiChord;
      mDistanceFromHome = xiDistanceFromHome;
      mNeighbours = new LinkedList<>();
    }

    public void addNeighbour(DetailedChord xiNext)
    {
      mNeighbours.add(xiNext);
    }
  }

  private static final DetailedChord CHORD_I   = new DetailedChord(Chord.CHORD_I,   0);
  private static final DetailedChord CHORD_ii  = new DetailedChord(Chord.CHORD_ii,  2);
  private static final DetailedChord CHORD_iii = new DetailedChord(Chord.CHORD_iii, 2);
  private static final DetailedChord CHORD_IV  = new DetailedChord(Chord.CHORD_IV,  1);
  private static final DetailedChord CHORD_V   = new DetailedChord(Chord.CHORD_V,   1);
  private static final DetailedChord CHORD_vi  = new DetailedChord(Chord.CHORD_vi,  2);

  static
  {
    CHORD_I.addNeighbour(CHORD_ii);
    CHORD_I.addNeighbour(CHORD_iii);
    CHORD_I.addNeighbour(CHORD_IV);
    CHORD_I.addNeighbour(CHORD_V);
    CHORD_I.addNeighbour(CHORD_vi);

    CHORD_ii.addNeighbour(CHORD_iii);
    CHORD_ii.addNeighbour(CHORD_V);

    CHORD_iii.addNeighbour(CHORD_IV);
    CHORD_iii.addNeighbour(CHORD_vi);

    CHORD_IV.addNeighbour(CHORD_I);
    CHORD_IV.addNeighbour(CHORD_ii);
    CHORD_IV.addNeighbour(CHORD_V);

    CHORD_V.addNeighbour(CHORD_I);
    CHORD_V.addNeighbour(CHORD_iii);
    CHORD_V.addNeighbour(CHORD_vi);

    CHORD_vi.addNeighbour(CHORD_ii);
    CHORD_vi.addNeighbour(CHORD_IV);
  }

  /**
   * @return the root chord.
   */
  public DetailedChord getRoot()
  {
    return CHORD_I;
  }
}
