package org.dogyjammers.jammypiece.infra;

/**
 * Interface for all components which consume data produced by another component.
 *
 * @param <T> - the type of data consumed.
 */
public interface Consumer<T>
{
  /**
   * Consume an item.
   *
   * @param xiItem - the item.
   */
  public void consume(T xiItem);
}
