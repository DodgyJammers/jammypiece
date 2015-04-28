package org.dogyjammers.jammypiece;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dogyjammers.jammypiece.components.DummyMidiSource;
import org.dogyjammers.jammypiece.components.MidiOut;

/**
 * Entry point for jammypiece.
 */
public class jammypiece
{
  private static final Logger LOGGER = LogManager.getLogger();

  public static void main(String[] args)
  {
    // Create all the components.
    try
    {
      new MidiOut(new DummyMidiSource());
      LOGGER.info("jammypiece started");
    }
    catch (Exception lEx)
    {
      LOGGER.error("jammypiece failed to start");
      lEx.printStackTrace();
    }

    // Spin until interrupted.
    try
    {
      while (true)
      {
        Thread.sleep(30000);
      }
    }
    catch (InterruptedException lEx)
    {
      LOGGER.info("Main thread interrupted - exiting");
    }
  }
}
