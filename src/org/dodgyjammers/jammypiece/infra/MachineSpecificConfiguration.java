package org.dodgyjammers.jammypiece.infra;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class giving access to machine-specific configuration.
 */
public class MachineSpecificConfiguration
{
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * Available configuration items.
   */
  public static enum CfgItem
  {
    /**
     * The MIDI output device to use (name + description).
     */
    MIDI_OUT_DEVICE,

    /**
     * The MIDI input device to use (name + description).
     */
    MIDI_IN_DEVICE,

    /**
     * The (string representation of the) key signature.
     */
    KEY_SIGNATURE,

    /**
     * The (string representation of the) time signature.
     */
    TIME_SIGNATURE,

    /**
     * The tempo (in beats per minute).
     */
    TEMPO,

    /**
     * Clicker configuration.
     */
    CLICKER_ENABLED,
    CLICKER_CHANNEL,
    CLICKER_STRESSED_NOTE,
    CLICKER_UNSTRESSED_NOTE,

    /**
     * General channel configuration.
     */
    MAX_SOLO_CHANNEL,
    BASS_CHANNEL,
    CHORD_CHANNEL,
    EXTRA_MELODY_CHANNEL,
    DRUM_CHANNEL,
  }

  private static final Properties MACHINE_PROPERTIES = new Properties();
  static
  {
    // Computer is identified by the COMPUTERNAME environment variable (Windows) or HOSTNAME (Linux).
    String lComputerName = System.getenv("COMPUTERNAME");
    if (lComputerName == null)
    {
      lComputerName = System.getenv("HOSTNAME");
    }

    if (lComputerName != null)
    {
      try (InputStream lPropStream = new FileInputStream("data/cfg/" + lComputerName + ".properties"))
      {
        MACHINE_PROPERTIES.load(lPropStream);
      }
      catch (IOException lEx)
      {
        LOGGER.warn("Missing/invalid machine-specific configuration for " + lComputerName);
      }
    }
    else
    {
      LOGGER.warn("Failed to identify computer name - no environment variable COMPUTERNAME or HOSTNAME");
    }
  }

  /**
   * @return the specified String configuration value, or the default if not configured.
   *
   * @param xiKey
   * @param xiDefault
   */
  public static String getCfgVal(CfgItem xiKey, String xiDefault)
  {
    return (MACHINE_PROPERTIES.getProperty(xiKey.toString(), xiDefault));
  }

  /**
   * @return the specified integer configuration value, or the default if not configured.
   *
   * @param xiKey
   * @param xiDefault
   */
  public static int getCfgVal(CfgItem xiKey, int xiDefault)
  {
    return Integer.parseInt(getCfgVal(xiKey, "" + xiDefault));
  }

  /**
   * @return the specified boolean configuration value, or the default if not configured.
   *
   * @param xiKey
   * @param xiDefault
   */
  public static boolean getCfgVal(CfgItem xiKey, boolean xiDefault)
  {
    return Boolean.parseBoolean(getCfgVal(xiKey, xiDefault ? "true" : "false"));
  }

  /**
   * Log all machine-specific configuration.
   */
  public static void logConfig()
  {
    LOGGER.info("Running with machine-specific properties:");
    for (Entry<Object, Object> e : MACHINE_PROPERTIES.entrySet())
    {
      LOGGER.info("\t" + e.getKey() + " = " + e.getValue());
    }
  }
}
