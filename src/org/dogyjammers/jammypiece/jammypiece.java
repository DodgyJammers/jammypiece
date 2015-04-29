package org.dogyjammers.jammypiece;

import java.util.Collections;
import java.util.List;

import javax.sound.midi.MidiEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dogyjammers.jammypiece.components.ChordSelector;
import org.dogyjammers.jammypiece.components.DummyMidiSource;
import org.dogyjammers.jammypiece.components.InputSelector;
import org.dogyjammers.jammypiece.components.KeyDetector;
import org.dogyjammers.jammypiece.components.MelodyAdjuster;
import org.dogyjammers.jammypiece.components.Metronome;
import org.dogyjammers.jammypiece.components.MidiOut;
import org.dogyjammers.jammypiece.events.ChordChange;
import org.dogyjammers.jammypiece.events.KeyChange;
import org.dogyjammers.jammypiece.events.Tick;
import org.dogyjammers.jammypiece.infra.Producer;

/**
 * Entry point for jammypiece.
 */
public class jammypiece
{
  private static final Logger LOGGER = LogManager.getLogger();

  public static void main(String[] args)
  {
    try
    {
      // Create all the components.
      DummyMidiSource lSource = new DummyMidiSource();
      List<Producer<MidiEvent>> lSources = Collections.singletonList((Producer<MidiEvent>)lSource);
      Producer<MidiEvent> lInputSelector = new InputSelector(lSources);
      Producer<KeyChange> lKeyDetector = new KeyDetector(lInputSelector);
      Producer<Tick> lMetronome = new Metronome();
      Producer<ChordChange> lChordSelector = new ChordSelector(lInputSelector, lKeyDetector, lMetronome);
      Producer<MidiEvent> lAdjuster = new MelodyAdjuster(lInputSelector, lChordSelector, lMetronome);
      new MidiOut(lAdjuster);

      // Start the source.
      lSource.start();
      LOGGER.info("jammypiece started");

      // Spin until interrupted.
      while (true)
      {
        Thread.sleep(30000);
      }
    }
    catch (InterruptedException lEx)
    {
      LOGGER.info("Main thread interrupted - exiting");
    }
    catch (Exception lEx)
    {
      LOGGER.error("jammypiece failed to start", lEx);
    }
  }
}
