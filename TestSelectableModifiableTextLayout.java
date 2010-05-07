package com.sap.clap.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sap.clap.application.FontUtil;
import com.sap.clap.common.text.OffsetRange;
import com.sap.clap.common.text.selection.SelectableModifiableTextLayout;
import com.sap.clap.common.text.selection.SelectableTextLayout;
import com.sap.clap.common.text.selection.SelectionDirection;
import com.sap.clap.common.text.selection.SelectionRange;

public class TestSelectableModifiableTextLayout {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell(display);
  }

  @Test
  public void testGetLineCount() {
    final SelectableModifiableTextLayout text = new SelectableModifiableTextLayout(display, null);
    try {
      // empty text has already one line
      assertEquals(1, text.getLineCount());

      text.setText("myTestText");
      assertEquals(1, text.getLineCount());

      text.appendln();
      text.append("another");
      assertEquals(2, text.getLineCount());

      text.append("line");
      assertEquals(2, text.getLineCount());

      text.insertln(1);
      assertEquals(3, text.getLineCount());

      text.deleteln(1);
      assertEquals(2, text.getLineCount());
    } finally {
      text.dispose();
    }
  }

  @Test
  public void testGetLineIndex() {
    final SelectableModifiableTextLayout text = new SelectableModifiableTextLayout(display, null);
    try {
      text.setText("01");
      assertEquals(0, text.getLineIndex(0));
      assertEquals(0, text.getLineIndex(1));

      text.appendln();
      assertEquals(0, text.getLineIndex(2));
      assertEquals(0, text.getLineIndex(3));

      text.appendln("45");
      assertEquals(1, text.getLineIndex(4));
      assertEquals(1, text.getLineIndex(5));
      assertEquals(1, text.getLineIndex(6));
      assertEquals(1, text.getLineIndex(7));

      text.append("89");
      assertEquals(2, text.getLineIndex(8));
      assertEquals(2, text.getLineIndex(9));

      try {
        text.getLineIndex(100);
        fail("expected IllegalArgumentException");
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
    } finally {
      text.dispose();
    }
  }

  @Test
  @Ignore
  public void testGetLineMetrics() {
    final SelectableModifiableTextLayout text = new SelectableModifiableTextLayout(display, null);
    try {
      text.setText("01");
      checkLineHeight(text, 0, 13);
      text.appendln();

      final FontData[] fontData10 = shell.getFont().getFontData();
      for (final FontData fd : fontData10) {
        fd.setHeight(10);
      }
      final Font f10 = new Font(Display.getDefault(), fontData10);
      try {
        text.appendln("45", f10);
        checkLineHeight(text, 1, 16);

        text.append("8", f10);
        checkLineHeight(text, 2, 16);
      } finally {
        f10.dispose();
      }

      final FontData[] fontData20 = shell.getFont().getFontData();
      for (final FontData fd : fontData20) {
        fd.setHeight(20);
      }
      final Font f20 = new Font(Display.getDefault(), fontData20);
      try {
        text.append("9", f20);
        checkLineHeight(text, 2, 33);
      } finally {
        f20.dispose();
      }
    } finally {
      text.dispose();
    }
  }

  private void checkLineHeight(final SelectableTextLayout text, final int lineIndex, final int expectedLineHeight) {
    final int lineHeight = text.getLineMetrics(lineIndex).getHeight();
    assertTrue("Expected line height [" + expectedLineHeight + "] for text [" + text.getText() + "] at line index [" + lineIndex
        + "]; line height = [" + lineHeight + "]", lineHeight == expectedLineHeight);
  }

  @Test
  @Ignore
  public void testGetLocation() {
    final SelectableModifiableTextLayout text = new SelectableModifiableTextLayout(display, null);
    try {
      text.setText("01", FontUtil.syntaxFont());
      checkLocation(text, 0, 0, 0, false);
      checkLocation(text, 0, 8, 0, true);
      checkLocation(text, 1, 8, 0, false);
      checkLocation(text, 1, 16, 0, true);

      text.appendln();
      checkLocation(text, 2, 16, 0, false);
      checkLocation(text, 2, 16, 0, true);
      checkLocation(text, 3, 0, 13, false);
      checkLocation(text, 3, 0, 13, true);

      text.append("45", FontUtil.syntaxFont());
      checkLocation(text, 4, 0, 13, false);
      checkLocation(text, 4, 8, 13, true);
      checkLocation(text, 5, 8, 13, false);
      checkLocation(text, 5, 16, 13, true);

      text.append("6", FontUtil.syntaxFont());
      checkLocation(text, 6, 16, 13, false);
      checkLocation(text, 6, 24, 13, true);

      text.appendln();
      checkLocation(text, 7, 24, 13, false);
      checkLocation(text, 7, 24, 13, true);
      checkLocation(text, 8, 0, 26, false);
      checkLocation(text, 8, 0, 26, true);

      text.append("9", FontUtil.syntaxFont());
      checkLocation(text, 9, 0, 26, false);
      checkLocation(text, 9, 8, 26, true);
    } finally {
      text.dispose();
    }
  }

  private void checkLocation(final SelectableTextLayout text, final int offset, final int expectedX, final int expectedY,
      final boolean trailing) {
    final Point location = text.getLocation(offset, trailing);
    final Point expectedLocation = new Point(expectedX, expectedY);
    assertTrue("Expected location [" + expectedLocation + "] for text [" + text.getText() + "] at offset [" + offset + "]; trailing = ["
        + trailing + "]; location = [" + location + "]", expectedLocation.equals(location));
  }

  @Test
  @Ignore
  public void testGetOffset() {
    final SelectableModifiableTextLayout text = new SelectableModifiableTextLayout(display, null);
    try {
      assertEquals(0, text.getOffset(new Point(100, 100)));

      text.setText("01", FontUtil.syntaxFont());
      text.appendln();
      text.append("45", FontUtil.syntaxFont());
      assertEquals(0, text.getOffset(new Point(0, 0)));
      assertEquals(0, text.getOffset(new Point(7, 0)));
      assertEquals(0, text.getOffset(new Point(0, 12)));
      assertEquals(0, text.getOffset(new Point(7, 12)));
      assertEquals(1, text.getOffset(new Point(8, 0)));
      assertEquals(1, text.getOffset(new Point(15, 0)));
      assertEquals(1, text.getOffset(new Point(8, 12)));
      assertEquals(1, text.getOffset(new Point(15, 12)));

      assertEquals(2, text.getOffset(new Point(100, 0)));
      assertEquals(2, text.getOffset(new Point(100, 12)));

      assertEquals(4, text.getOffset(new Point(0, 13)));
      assertEquals(4, text.getOffset(new Point(7, 13)));
      assertEquals(4, text.getOffset(new Point(0, 25)));
      assertEquals(4, text.getOffset(new Point(7, 25)));
      assertEquals(5, text.getOffset(new Point(8, 13)));
      assertEquals(5, text.getOffset(new Point(15, 13)));
      assertEquals(5, text.getOffset(new Point(8, 25)));
      assertEquals(5, text.getOffset(new Point(15, 25)));

      assertEquals(6, text.getOffset(new Point(100, 13)));
      assertEquals(6, text.getOffset(new Point(100, 25)));
      assertEquals(4, text.getOffset(new Point(-1, 13)));

      assertEquals(4, text.getOffset(new Point(0, 100)));
      assertEquals(4, text.getOffset(new Point(7, 100)));
      assertEquals(5, text.getOffset(new Point(8, 100)));
      assertEquals(5, text.getOffset(new Point(15, 100)));

      assertEquals(0, text.getOffset(new Point(-1, -1)));
      assertEquals(6, text.getOffset(new Point(100, 100)));
    } finally {
      text.dispose();
    }
  }

  @Test
  public void testGetNextOffset() {
    final SelectableModifiableTextLayout text = new SelectableModifiableTextLayout(display, null);
    try {
      text.setText("this is the first line\r\nthis is the second line");
      assertEquals(4, text.getNextOffset(1, SWT.MOVEMENT_WORD_END));
      assertEquals(7, text.getNextOffset(4, SWT.MOVEMENT_WORD_END));
      assertEquals(11, text.getNextOffset(8, SWT.MOVEMENT_WORD_END));
      assertEquals(22, text.getNextOffset(20, SWT.MOVEMENT_WORD_END));
      assertEquals(28, text.getNextOffset(23, SWT.MOVEMENT_WORD_END));
      assertEquals(35, text.getNextOffset(33, SWT.MOVEMENT_WORD_END));
      assertEquals(47, text.getNextOffset(44, SWT.MOVEMENT_WORD_END));

      assertEquals(5, text.getNextOffset(2, SWT.MOVEMENT_WORD));
      assertEquals(18, text.getNextOffset(13, SWT.MOVEMENT_WORD));
      assertEquals(22, text.getNextOffset(20, SWT.MOVEMENT_WORD));
      assertEquals(47, text.getNextOffset(44, SWT.MOVEMENT_WORD));

      assertEquals(5, text.getNextOffset(2, SWT.MOVEMENT_WORD_START));
      assertEquals(18, text.getNextOffset(13, SWT.MOVEMENT_WORD_START));
      assertEquals(22, text.getNextOffset(20, SWT.MOVEMENT_WORD_START));
      assertEquals(47, text.getNextOffset(44, SWT.MOVEMENT_WORD_START));

      assertEquals(3, text.getNextOffset(2, SWT.MOVEMENT_CHAR));
      assertEquals(5, text.getNextOffset(4, SWT.MOVEMENT_CHAR));
      assertEquals(23, text.getNextOffset(22, SWT.MOVEMENT_CHAR));
      assertEquals(24, text.getNextOffset(23, SWT.MOVEMENT_CHAR));
      assertEquals(47, text.getNextOffset(46, SWT.MOVEMENT_CHAR));
      assertEquals(47, text.getNextOffset(47, SWT.MOVEMENT_CHAR));

      assertEquals(3, text.getNextOffset(2, SWT.MOVEMENT_CLUSTER));
      assertEquals(5, text.getNextOffset(4, SWT.MOVEMENT_CLUSTER));
      assertEquals(22, text.getNextOffset(22, SWT.MOVEMENT_CLUSTER));
      assertEquals(24, text.getNextOffset(23, SWT.MOVEMENT_CLUSTER));
      assertEquals(47, text.getNextOffset(46, SWT.MOVEMENT_CLUSTER));
      assertEquals(47, text.getNextOffset(47, SWT.MOVEMENT_CLUSTER));

      try {
        text.getNextOffset(-1, SWT.MOVEMENT_CLUSTER);
        fail("expected IllegalArgumentException");
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$
      }

      try {
        text.getNextOffset(48, SWT.MOVEMENT_CLUSTER);
        fail("expected IllegalArgumentException");
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$
      }
    } finally {
      text.dispose();
    }
  }

  @Test
  public void testGetPreviousOffset() {
    final SelectableModifiableTextLayout text = new SelectableModifiableTextLayout(display, null);
    try {
      text.setText("this is the first line\r\nthis is the second line");
      assertEquals(0, text.getPreviousOffset(0, SWT.MOVEMENT_WORD_START));
      assertEquals(0, text.getPreviousOffset(2, SWT.MOVEMENT_WORD_START));
      assertEquals(0, text.getPreviousOffset(4, SWT.MOVEMENT_WORD_START));
      assertEquals(0, text.getPreviousOffset(5, SWT.MOVEMENT_WORD_START));
      assertEquals(5, text.getPreviousOffset(6, SWT.MOVEMENT_WORD_START));
      assertEquals(8, text.getPreviousOffset(10, SWT.MOVEMENT_WORD_START));
      assertEquals(18, text.getPreviousOffset(22, SWT.MOVEMENT_WORD_START));
      assertEquals(22, text.getPreviousOffset(23, SWT.MOVEMENT_WORD_START));
      assertEquals(22, text.getPreviousOffset(24, SWT.MOVEMENT_WORD_START));
      assertEquals(24, text.getPreviousOffset(25, SWT.MOVEMENT_WORD_START));

      assertEquals(0, text.getPreviousOffset(0, SWT.MOVEMENT_WORD));
      assertEquals(0, text.getPreviousOffset(2, SWT.MOVEMENT_WORD));
      assertEquals(0, text.getPreviousOffset(4, SWT.MOVEMENT_WORD));
      assertEquals(0, text.getPreviousOffset(5, SWT.MOVEMENT_WORD));
      assertEquals(5, text.getPreviousOffset(6, SWT.MOVEMENT_WORD));
      assertEquals(8, text.getPreviousOffset(10, SWT.MOVEMENT_WORD));
      assertEquals(18, text.getPreviousOffset(22, SWT.MOVEMENT_WORD));
      assertEquals(22, text.getPreviousOffset(23, SWT.MOVEMENT_WORD));
      assertEquals(22, text.getPreviousOffset(24, SWT.MOVEMENT_WORD));
      assertEquals(24, text.getPreviousOffset(25, SWT.MOVEMENT_WORD));

      assertEquals(0, text.getPreviousOffset(0, SWT.MOVEMENT_WORD_END));
      assertEquals(0, text.getPreviousOffset(3, SWT.MOVEMENT_WORD_END));
      assertEquals(4, text.getPreviousOffset(6, SWT.MOVEMENT_WORD_END));
      assertEquals(17, text.getPreviousOffset(22, SWT.MOVEMENT_WORD_END));
      assertEquals(22, text.getPreviousOffset(23, SWT.MOVEMENT_WORD_END));
      assertEquals(22, text.getPreviousOffset(25, SWT.MOVEMENT_WORD_END));
      assertEquals(28, text.getPreviousOffset(29, SWT.MOVEMENT_WORD_END));

      assertEquals(0, text.getPreviousOffset(0, SWT.MOVEMENT_CHAR));
      assertEquals(0, text.getPreviousOffset(1, SWT.MOVEMENT_CHAR));
      assertEquals(4, text.getPreviousOffset(5, SWT.MOVEMENT_CHAR));
      assertEquals(22, text.getPreviousOffset(23, SWT.MOVEMENT_CHAR));
      assertEquals(23, text.getPreviousOffset(24, SWT.MOVEMENT_CHAR));

      assertEquals(0, text.getPreviousOffset(0, SWT.MOVEMENT_CLUSTER));
      assertEquals(0, text.getPreviousOffset(1, SWT.MOVEMENT_CLUSTER));
      assertEquals(4, text.getPreviousOffset(5, SWT.MOVEMENT_CLUSTER));
      assertEquals(21, text.getPreviousOffset(22, SWT.MOVEMENT_CLUSTER));
      assertEquals(22, text.getPreviousOffset(23, SWT.MOVEMENT_CLUSTER));
      assertEquals(22, text.getPreviousOffset(24, SWT.MOVEMENT_CLUSTER));
      assertEquals(24, text.getPreviousOffset(25, SWT.MOVEMENT_CLUSTER));
    } finally {
      text.dispose();
    }
  }

  @Test
  public void testSelection() {
    final SelectableModifiableTextLayout text = new SelectableModifiableTextLayout(display, null);
    try {
      assertFalse(text.hasSelection());
      assertNull(text.getSelectionRange());
      assertNull(text.getSelectedText());

      text.clearSelection();
      assertFalse(text.hasSelection());
      assertNull(text.getSelectionRange());
      assertNull(text.getSelectedText());

      try {
        // selection should fail since there is no text that could be selected
        text.setSelectionRange(new SelectionRange(0, 1, SelectionDirection.LEFT_TO_RIGHT));
        fail("expected IllegalArgumentException");
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }

      text.append("0123456789");
      text.setSelectionRange(new SelectionRange(0, 1, SelectionDirection.LEFT_TO_RIGHT));
      assertTrue(text.hasSelection());
      assertEquals("0", text.getSelectedText());
      assertEquals(new SelectionRange(0, 1, SelectionDirection.LEFT_TO_RIGHT), text.getSelectionRange());

      text.setSelectionRange(new SelectionRange(5, 10, SelectionDirection.RIGHT_TO_LEFT));
      assertTrue(text.hasSelection());
      assertEquals("56789", text.getSelectedText());
      assertEquals(new SelectionRange(5, 10, SelectionDirection.RIGHT_TO_LEFT), text.getSelectionRange());

      text.setSelectionRange(new SelectionRange(0, 10, SelectionDirection.RIGHT_TO_LEFT));
      assertTrue(text.hasSelection());
      assertEquals("0123456789", text.getSelectedText());
      assertEquals(new SelectionRange(0, 10, SelectionDirection.RIGHT_TO_LEFT), text.getSelectionRange());

      try {
        text.setSelectionRange(new SelectionRange(0, 11, SelectionDirection.RIGHT_TO_LEFT));
        fail("expected IllegalArgumentException");
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }

      text.clearSelection();
      assertFalse(text.hasSelection());
      assertNull(text.getSelectionRange());
      assertNull(text.getSelectedText());

      text.setSelectionRange(new SelectionRange(0, 10, SelectionDirection.RIGHT_TO_LEFT));
      assertTrue(text.hasSelection());
      text.setSelectionRange(null);
      assertFalse(text.hasSelection());
      assertNull(text.getSelectionRange());
      assertNull(text.getSelectedText());
    } finally {
      text.dispose();
    }
  }

  @Test
  public void testCursorPosition() {
    final SelectableModifiableTextLayout text = new SelectableModifiableTextLayout(display, null);
    try {
      assertEquals(0, text.getCaretPosition());

      text.append("0123456789");
      assertEquals(0, text.getCaretPosition());

      text.setSelectionRange(new SelectionRange(2, 8, SelectionDirection.LEFT_TO_RIGHT));
      assertEquals(8, text.getCaretPosition());
      assertTrue(text.hasSelection());

      text.setCaretPosition(5);
      assertEquals(5, text.getCaretPosition());
      assertFalse(text.hasSelection());

      text.setSelectionRange(new SelectionRange(2, 8, SelectionDirection.RIGHT_TO_LEFT));
      assertEquals(2, text.getCaretPosition());

      text.clearSelection();
      assertEquals(2, text.getCaretPosition());

      text.setCaretPosition(5);
      assertEquals(5, text.getCaretPosition());

      text.setSelectionRange(new SelectionRange(0, 10, SelectionDirection.LEFT_TO_RIGHT));
      assertEquals(10, text.getCaretPosition());

      text.setCaretPosition(text.getText().length());
      assertEquals(text.getText().length(), text.getCaretPosition());

      try {
        text.setCaretPosition(-1);
        fail("expected IllegalArgumentException");
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }

      try {
        text.setCaretPosition(text.getText().length() + 1);
        fail("expected IllegalArgumentException");
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
    } finally {
      text.dispose();
    }
  }

  @Test
  public void testGetSelectionBounds() {
    final SelectableModifiableTextLayout text = new SelectableModifiableTextLayout(display, null);
    try {
      assertNull(text.getSelectionBounds());

      text.appendln("01234567");
      text.appendln("01234567");
      text.appendln("01234567");

      assertNull(text.getSelectionBounds());

      text.setSelectionRange(new SelectionRange(0, 1, SelectionDirection.LEFT_TO_RIGHT));
      assertEquals(new Rectangle(0, 0, 6, 13), text.getSelectionBounds());

      text.setSelectionRange(new SelectionRange(16, 22, SelectionDirection.RIGHT_TO_LEFT));
      assertEquals(new Rectangle(0, 13, 8 * 6, 2 * 13), text.getSelectionBounds());

      text.setSelectionRange(new SelectionRange(0, text.getText().length(), SelectionDirection.LEFT_TO_RIGHT));
      assertEquals(new Rectangle(0, 0, 8 * 6, 3 * 13), text.getSelectionBounds());
      // why in this case with Eclipse 3.5 text.getBounds() is not equal to text.getSelectionBounds()??
      // assertEquals(text.getBounds(), text.getSelectionBounds());
    } finally {
      text.dispose();
    }
  }

  @Test
  public void testSelectionChangedListener() {
    final SelectableModifiableTextLayout text = new SelectableModifiableTextLayout(display, null);
    try {
      text.append("0123456789");
      final VerifyingSelectionChangedListener<SelectableTextLayout> selectionChangedListener =
        new VerifyingSelectionChangedListener<SelectableTextLayout>(text, new Rectangle(0, 0, 6, 13), null);
      text.addSelectionChangedListener(selectionChangedListener);
      text.setSelectionRange(new SelectionRange(0, 1, SelectionDirection.LEFT_TO_RIGHT));
      selectionChangedListener.verify();

      text.setSelectionRange(new SelectionRange(0, 1, SelectionDirection.LEFT_TO_RIGHT));
      selectionChangedListener.verify();

      final VerifyingSelectionChangedListener<SelectableTextLayout> selectionChangedListener2 =
        new VerifyingSelectionChangedListener<SelectableTextLayout>(text, new Rectangle(0, 0, 6 * 6, 13), new Rectangle(0, 0, 6, 13));
      text.addSelectionChangedListener(selectionChangedListener2);
      text.removeSelectionChangedListener(selectionChangedListener);
      text.setSelectionRange(new SelectionRange(0, 6, SelectionDirection.LEFT_TO_RIGHT));
      selectionChangedListener2.verify();
      selectionChangedListener.verify();
      text.removeSelectionChangedListener(selectionChangedListener2);
      final VerifyingSelectionChangedListener<SelectableTextLayout> selectionChangedListener3 =
        new VerifyingSelectionChangedListener<SelectableTextLayout>(text, null, new Rectangle(0, 0, 6 * 6, 13));
      text.addSelectionChangedListener(selectionChangedListener3);
      text.clearSelection();
      text.clearSelection();
      selectionChangedListener3.verify();
    } finally {
      text.dispose();
    }
  }

  @Test
  public void testGetLineRange() {
    final SelectableModifiableTextLayout text = new SelectableModifiableTextLayout(display, null);
    try {
      text.appendln("0123456789");
      text.appendln("0123456789");
      text.appendln();
      text.append("0123456789");

      assertEquals(new OffsetRange(0, 12), text.getLineRange(0));
      assertEquals(new OffsetRange(12, 24), text.getLineRange(1));
      assertEquals(new OffsetRange(24, 26), text.getLineRange(2));
      assertEquals(new OffsetRange(26, 36), text.getLineRange(3));
    } finally {
      text.dispose();
    }
  }

  @Test
  @Ignore
  public void testGetLineBounds() {
    // TODO
    fail("TODO");
  }

  @After
  public void cleanUp() {
    shell.dispose();
    display.dispose();
  }
}