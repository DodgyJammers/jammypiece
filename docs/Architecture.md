Here's a brief description of the blocks on the architecture diagram.

- MIDI in - get MIDI events from OS and add to MIDI event bus (central horizontal line).
- Mic. In + f0 estimation - almost certainly won't do.  This is listening to a melody from a microphone, working out which note was played (f0 estimation was my final year project at uni.) and putting that as MIDI events onto the bus.
- Input selection - not required whilst we've only got MIDI in.
- Key detection + Tempo detection - the idea here being that we could avoid having to make this configurable (if time).  The system would listen for a few bars first in order to detect the key + tempo and then join in.  I think this shouldn't be too hard.
- Time signature detection - unlike key + tempo, I think this is a really tricky task.  It probably requires significant understanding of music structure.  I suspect we'll just use configuration for this.
- Metronome - I imagine this taking input from tempo and time sig. detection and then producing major + minor beat events to clock the rest of the system (so that, for example, there's a trigger for an underlying chord change, even during a sustained note).
- Melody adjustment - a place to fix up melody events, potentially changing the note played and its timing.  Not necessary initially.
- Continuation - I'd like the soloist to be able to stop playing and have the system continue the piece!  Clearly won't happen (but perhaps if we're aiming for the most ambitious hack...).
- Chord selection - the heart of the system.  Takes data about what chord progressions typically sound good together (potentially filtered by some sort of style setting - I imagine that jazz progressions look a lot different from 17th century choral works) and then selects a suitable chord to go with the played melody note.  Would be responsible for imposing structure.
- Harmony production - takes the note played + chord selected to produce the harmony note(s).  Options here include bass line only, arpeggiation, following in 3rds/6ths, etc..  Hopefully relatively straightforward.
- + - combine melody + harmonization (& optionally a metronome click) into the final MIDI output stream.
- MIDI out - consume events from the MIDI bus and supply to the OS (either for built-in OS synth or to an external MIDI out for external synth).
