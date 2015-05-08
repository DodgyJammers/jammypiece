package org.dodgyjammers.jammypiece.infra;

/**
 * Merge multiple distributors into a single one
 */
public class Merger<T> extends Distributor<T> implements Consumer<T>
{
  @Override
  public void consume(T xiItem) throws Exception {
    distribute(xiItem);
  }
  
  public void add(Producer<T> xiProducer) {
    xiProducer.registerConsumer(this);
  }
}
