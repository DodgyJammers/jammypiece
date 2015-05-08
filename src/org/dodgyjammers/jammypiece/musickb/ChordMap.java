package org.dodgyjammers.jammypiece.musickb;

import java.util.LinkedList;
import java.util.List;

import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration;
import org.dodgyjammers.jammypiece.infra.MachineSpecificConfiguration.CfgItem;

/**
 * Map of "nice" chord progressions.
 */
public class ChordMap
{
  /**
   * A group of closely related chords.
   */
  public static class ChordGroup
  {
    /**
     * The primary chord in the group.
     */
    public final Chord mPrimaryChord;

    /**
     * The chord variations.  Typically more complex chords that can be added for interest.
     *
     * Users MUST NOT mutate this list!
     */
    public final List<Chord> mVariations;

    /**
     * The number of chord groups in the shortest path home (exclusive of this one, inclusive of home).
     */
    public final int mShortestPathHome;

    /**
     * The longest path home (without visiting a group more than once or hyper-jumping).
     */
    // public final int mLongestPathHome;

    /**
     * The chord groups that are reachable from here.
     *
     * Users MUST NOT mutate this list!
     */
    public final List<ChordGroup> mNeighbours;

    /**
     * Create a detailed chord.
     *
     * @param xiChord - the chord.
     * @param xiDistanceFromHome - the distance from home.
     */
    public ChordGroup(Chord xiChord, int xiDistanceFromHome)
    {
      mPrimaryChord = xiChord;
      mVariations = new LinkedList<>();
      mShortestPathHome = xiDistanceFromHome;
      mNeighbours = new LinkedList<>();
    }

    public void addVariation(Chord xiVariation)
    {
      mVariations.add(xiVariation);
    }

    public void addNeighbour(ChordGroup xiNext)
    {
      mNeighbours.add(xiNext);
    }
  }

  private static final ChordGroup CHORD_I   = new ChordGroup(Chord.CHORD_I,   0);
  private static final ChordGroup CHORD_ii  = new ChordGroup(Chord.CHORD_ii,  2);
  private static final ChordGroup CHORD_iii = new ChordGroup(Chord.CHORD_iii, 2);
  private static final ChordGroup CHORD_IV  = new ChordGroup(Chord.CHORD_IV,  1);
  private static final ChordGroup CHORD_V   = new ChordGroup(Chord.CHORD_V,   1);
  private static final ChordGroup CHORD_vi  = new ChordGroup(Chord.CHORD_vi,  2);

  static
  {
    // Progressions in the simple map

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

    if (MachineSpecificConfiguration.getCfgVal(CfgItem.COMPLEX_CHORDS, false))
    {
      // Add variations to the existing groups.
      CHORD_I.addVariation(new Chord(0, Chord.Variation.M2));
      CHORD_I.addVariation(new Chord(0, Chord.Variation.M6));
      CHORD_I.addVariation(new Chord(0, Chord.Variation.M7));
      CHORD_I.addVariation(new Chord(0, Chord.Variation.M9));
      CHORD_I.addVariation(new Chord(0, Chord.Variation.SUS));

      CHORD_ii.addVariation(new Chord(2, Chord.Variation.m7));
      CHORD_ii.addVariation(new Chord(2, Chord.Variation.m9));

      CHORD_iii.addVariation(new Chord(4, Chord.Variation.m7));

      CHORD_IV.addVariation(new Chord(5, Chord.Variation.M6));
      CHORD_IV.addVariation(new Chord(5, Chord.Variation.M7));

      CHORD_V.addVariation(new Chord(7, Chord.Variation.M7));
      CHORD_V.addVariation(new Chord(7, Chord.Variation.M9));
      CHORD_V.addVariation(new Chord(7, Chord.Variation.M11));
      CHORD_V.addVariation(new Chord(7, Chord.Variation.M13));
      CHORD_V.addVariation(new Chord(7, Chord.Variation.SUS));
    }
  }

  /**
   * @return the root chord group.
   */
  public ChordGroup getRoot()
  {
    return CHORD_I;
  }
}
