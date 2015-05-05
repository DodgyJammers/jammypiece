package org.dodgyjammers.jammypiece.infra;

/**
 * Interface for all components which produce some form of data that other components might be interested in.
 *
 * @param <T> - type of items produced.
 */
public interface Producer<T>
{
  /**
   * Register a consumer.
   *
   * @param xiConsumer - the consumer.
   */
  public void registerConsumer(Consumer<T> xiConsumer);
}
