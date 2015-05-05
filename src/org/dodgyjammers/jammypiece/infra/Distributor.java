package org.dodgyjammers.jammypiece.infra;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A helper class for all producers.
 *
 * @param <T> - the type of item produced.
 */
public abstract class Distributor<T> implements Producer<T>
{
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * A list of all registered consumers.
   */
  protected final List<Consumer<T>> mConsumers = new LinkedList<>();

  @Override
  public void registerConsumer(Consumer<T> xiConsumer)
  {
    mConsumers.add(xiConsumer);
  }

  /**
   * Distribute an item to all registered consumers.
   *
   * @param xiItem - the item to distribute.
   */
  protected void distribute(T xiItem)
  {
    LOGGER.trace("Distributing " + xiItem);
    for (Consumer<T> lConsumer : mConsumers)
    {
      lConsumer.consume(xiItem);
    }
  }
}
