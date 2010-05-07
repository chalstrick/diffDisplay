package com.sap.clap.test;

import static org.junit.Assert.assertEquals;

import org.eclipse.swt.graphics.Rectangle;

import com.sap.clap.common.text.selection.SelectionChangedListener;

public class VerifyingSelectionChangedListener<T> implements SelectionChangedListener<T> {

  private final T expectedSelectable;
  private final Rectangle expectedNewSelectionBounds;
  private final Rectangle expectedOldSelectionBounds;
  private final int expectedCallCount;

  private int callCount = 0;

  public VerifyingSelectionChangedListener(final T expectedSelectable, final Rectangle expectedNewSelectionBounds,
      final Rectangle expectedOldSelectionBounds) {
    this.expectedSelectable = expectedSelectable;
    this.expectedNewSelectionBounds = expectedNewSelectionBounds;
    this.expectedOldSelectionBounds = expectedOldSelectionBounds;
    this.expectedCallCount = 1;
  }

  public VerifyingSelectionChangedListener() {
    this.expectedSelectable = null;
    this.expectedNewSelectionBounds = null;
    this.expectedOldSelectionBounds = null;
    this.expectedCallCount = 0;
  }

  public void selectionChanged(final T selectable, final Rectangle newSelectionBounds, final Rectangle oldSelectionBounds) {
    assertEquals(expectedSelectable, selectable);
    assertEquals(expectedNewSelectionBounds, newSelectionBounds);
    assertEquals(expectedOldSelectionBounds, oldSelectionBounds);
    callCount++;
  }

  public void verify() {
    assertEquals(expectedCallCount, callCount);
  }
}
