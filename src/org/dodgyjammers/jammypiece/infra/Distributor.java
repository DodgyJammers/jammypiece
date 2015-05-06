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
  
  protected void registerConsumerAndUpdate(Consumer<T> xiConsumer, T xiItem)
  {
    mConsumers.add(xiConsumer);
    safelyTell(xiConsumer, xiItem);
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
      safelyTell(lConsumer, xiItem);
    }
  }
  
  private void safelyTell(Consumer<T> xiConsumer, T xiItem)
  {
    try
    {
      xiConsumer.consume(xiItem);
    }
    catch (Exception e)
    {
      // Log the exception as an error, but the show must go on. Swallow it
      // and continue.
      LOGGER.error("Consumer " + xiConsumer + " threw while processing " + xiItem, e);
    }
  }

  @Override
  public void start()
  {
    // Default implementation does nothing.
  }
}
