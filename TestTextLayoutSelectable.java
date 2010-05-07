package com.sap.clap.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sap.clap.application.FontUtil;
import com.sap.clap.common.text.PointRange;
import com.sap.clap.common.text.Segment;
import com.sap.clap.common.text.selection.SelectableSegmentedText;
import com.sap.clap.common.text.selection.SelectionDirection;
import com.sap.clap.common.text.selection.SelectionRange;
import com.sap.clap.common.text.selection.TextLayoutSelectable;

public class TestTextLayoutSelectable {

  private Display display;
  private Shell shell;
  private SelectableSegmentedText text;
  private TextLayoutSelectable textLayoutSelectable;

  final int charWidth = 8;
  final int lineHeight = 13;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell(display);
    text = new SelectableSegmentedText(shell);
    text.setFont(FontUtil.syntaxFont());
    final Segment segment0 = text.addSegment();
    segment0.appendln("012345").append("89");
    final Segment segment1 = text.addSegment();
    segment1.appendln("012345").appendln("89").append("234");
    final Segment segment2 = text.addSegment();
    segment2.appendln("0 23 5").append("89 (test) end");
    textLayoutSelectable = new TextLayoutSelectable(text, null);
  }

  @Test
  public void testSetCursorPosition() {
    assertEquals(new Point(0, 0), textLayoutSelectable.getCaretPosition());
    assertEquals(0, text.getCaretPosition());

    textLayoutSelectable.setCaretLocation(new Point(charWidth, 0));
    assertEquals(new Point(charWidth, 0), textLayoutSelectable.getCaretPosition());
    assertEquals(1, text.getCaretPosition());

    textLayoutSelectable.setCaretLocation(new Point(charWidth + charWidth / 2, 0));
    assertEquals(new Point(charWidth, 0), textLayoutSelectable.getCaretPosition());
    assertEquals(1, text.getCaretPosition());

    textLayoutSelectable.setCaretLocation(new Point(0, lineHeight));
    assertEquals(new Point(0, lineHeight), textLayoutSelectable.getCaretPosition());
    assertEquals(8, text.getCaretPosition());

    textLayoutSelectable.setCaretLocation(new Point(0, lineHeight + lineHeight / 2));
    assertEquals(new Point(0, lineHeight), textLayoutSelectable.getCaretPosition());
    assertEquals(8, text.getCaretPosition());
  }

  @Test
  public void testDeselect() {
    text.setSelectionRange(new SelectionRange(5, 10, SelectionDirection.LEFT_TO_RIGHT));
    assertEquals(10, text.getCaretPosition());
    assertEquals(true, textLayoutSelectable.hasSelection());
    textLayoutSelectable.deselect();
    assertEquals(false, textLayoutSelectable.hasSelection());
    assertEquals(false, text.hasSelection());
    assertEquals(10, text.getCaretPosition());
  }

  @Test
  public void testSelectAll() {
    assertNull(textLayoutSelectable.getSelectedText());
    textLayoutSelectable.selectAll();
    assertEquals(text.getText(), textLayoutSelectable.getSelectedText());
    assertEquals(text.getText().length(), text.getCaretPosition());
  }

  @Test
  public void testSelectFromTo() {
    assertEquals(0, text.getCaretPosition());
    textLayoutSelectable.select(new Point(0, 0), new Point(0, 0));
    assertEquals(true, textLayoutSelectable.hasSelection());
    assertEquals(true, text.hasSelection());
    assertEquals("0", textLayoutSelectable.getSelectedText());
    assertEquals(SelectionDirection.LEFT_TO_RIGHT, text.getSelectionRange().getDirection());
    assertEquals(1, text.getCaretPosition());

    textLayoutSelectable.deselect();
    textLayoutSelectable.select(new Point(0, 0), new Point(charWidth / 2, 0));
    assertEquals(true, textLayoutSelectable.hasSelection());
    assertEquals(true, text.hasSelection());
    assertEquals("0", textLayoutSelectable.getSelectedText());
    assertEquals(SelectionDirection.LEFT_TO_RIGHT, text.getSelectionRange().getDirection());
    assertEquals(1, text.getCaretPosition());

    textLayoutSelectable.deselect();
    textLayoutSelectable.select(new Point(charWidth / 2, 0), new Point(0, 0));
    assertEquals(true, textLayoutSelectable.hasSelection());
    assertEquals(true, text.hasSelection());
    assertEquals("0", textLayoutSelectable.getSelectedText());
    assertEquals(SelectionDirection.RIGHT_TO_LEFT, text.getSelectionRange().getDirection());
    assertEquals(0, text.getCaretPosition());

    textLayoutSelectable.deselect();
    textLayoutSelectable.select(new Point(0, 0), new Point(2 * charWidth, 0));
    assertEquals("012", textLayoutSelectable.getSelectedText());
    assertEquals(SelectionDirection.LEFT_TO_RIGHT, text.getSelectionRange().getDirection());
    assertEquals(3, text.getCaretPosition());

    textLayoutSelectable.deselect();
    textLayoutSelectable.select(new Point(0, lineHeight), new Point(1 * charWidth, lineHeight));
    assertEquals("89", textLayoutSelectable.getSelectedText());
    assertEquals(SelectionDirection.LEFT_TO_RIGHT, text.getSelectionRange().getDirection());
    assertEquals(10, text.getCaretPosition());

    textLayoutSelectable.deselect();
    textLayoutSelectable.select(new Point(1 * charWidth, 2 * lineHeight), new Point(0, 2 * lineHeight));
    assertEquals("01", textLayoutSelectable.getSelectedText());
    assertEquals(SelectionDirection.RIGHT_TO_LEFT, text.getSelectionRange().getDirection());
    assertEquals(12, text.getCaretPosition());

    textLayoutSelectable.deselect();
    textLayoutSelectable.select(new Point(-1, -1), new Point(10000, 10000));
    assertEquals(text.getText(), textLayoutSelectable.getSelectedText());
    assertEquals(SelectionDirection.LEFT_TO_RIGHT, text.getSelectionRange().getDirection());
    assertEquals(text.getText().length(), text.getCaretPosition());
  }

  @Test
  public void testSelectLine() {
    textLayoutSelectable.selectLine(new Point(0, 0));
    assertEquals(text.getLine(0), textLayoutSelectable.getSelectedText());

    textLayoutSelectable.selectLine(new Point(0, lineHeight));
    assertEquals(text.getLine(1), textLayoutSelectable.getSelectedText());

    textLayoutSelectable.selectLine(new Point(0, 2 * lineHeight));
    assertEquals(text.getLine(2), textLayoutSelectable.getSelectedText());
  }

  @Test
  public void testSelectWord() {
    textLayoutSelectable.selectWord(new Point(0, 5 * lineHeight));
    assertEquals("0", textLayoutSelectable.getSelectedText());

    textLayoutSelectable.selectWord(new Point(charWidth, 5 * lineHeight));
    // not sure what to expect here
    assertEquals("0 23", textLayoutSelectable.getSelectedText());
    // IDE behavior:
    // assertEquals(" 23", textLayoutSelectable.getSelectedText());
    // Firefox and Internet-Explorer behavior:
    // assertEquals("0 ", textLayoutSelectable.getSelectedText());

    textLayoutSelectable.selectWord(new Point(2 * charWidth, 5 * lineHeight));
    // IDE behavior:
    assertEquals("23", textLayoutSelectable.getSelectedText());
    // Firefox and Internet-Explorer behavior:
    // assertEquals("23 ", textLayoutSelectable.getSelectedText());

    textLayoutSelectable.selectWord(new Point(3 * charWidth, 5 * lineHeight));
    assertEquals("23", textLayoutSelectable.getSelectedText());
    // Firefox and Internet-Explorer behavior:
    // assertEquals("23 ", textLayoutSelectable.getSelectedText());

    textLayoutSelectable.selectWord(new Point(4 * charWidth, 5 * lineHeight));
    // not sure what to expect here
    assertEquals("23 5", textLayoutSelectable.getSelectedText());
    // IDE behavior:
    // assertEquals(" 5", textLayoutSelectable.getSelectedText());
    // Firefox and Internet-Explorer behavior:
    // assertEquals("23 ", textLayoutSelectable.getSelectedText());

    textLayoutSelectable.selectWord(new Point(5 * charWidth, 5 * lineHeight));
    assertEquals("5", textLayoutSelectable.getSelectedText());

    textLayoutSelectable.selectWord(new Point(5 * charWidth, 5 * lineHeight));
    // not sure what to expect here
    // Firefox behavior:
    assertEquals("5", textLayoutSelectable.getSelectedText());
    // Internet-Explorer and IDE behavior
    // assertEquals("\r\n89", textLayoutSelectable.getSelectedText());

    textLayoutSelectable.selectWord(new Point(-1, 5 * lineHeight));
    // not sure what to expect here
    assertEquals("0", textLayoutSelectable.getSelectedText());

    textLayoutSelectable.selectWord(new Point(3 * charWidth, 6 * lineHeight));
    // not sure what to expect here
    assertEquals("(test", textLayoutSelectable.getSelectedText());

    textLayoutSelectable.selectWord(new Point(4 * charWidth, 6 * lineHeight));
    assertEquals("test", textLayoutSelectable.getSelectedText());
    textLayoutSelectable.selectWord(new Point(5 * charWidth, 6 * lineHeight));
    assertEquals("test", textLayoutSelectable.getSelectedText());
    textLayoutSelectable.selectWord(new Point(6 * charWidth, 6 * lineHeight));
    assertEquals("test", textLayoutSelectable.getSelectedText());
    textLayoutSelectable.selectWord(new Point(7 * charWidth, 6 * lineHeight));
    assertEquals("test", textLayoutSelectable.getSelectedText());

    textLayoutSelectable.selectWord(new Point(8 * charWidth, 6 * lineHeight));
    // not sure what to expect here
    assertEquals(") end", textLayoutSelectable.getSelectedText());
  }

  @Test
  public void testMoveSelectionBorderRight() {
    // selection LEFT_TO_RIGHT
    for (int i = 1; i <= text.getText().length(); i++) {
      textLayoutSelectable.moveSelectionBorder(SWT.RIGHT);
      assertEquals("i=" + i, text.getText().substring(0, i), textLayoutSelectable.getSelectedText());
      assertEquals("i=" + i, i, text.getCaretPosition());
    }
    textLayoutSelectable.moveSelectionBorder(SWT.RIGHT);
    assertEquals(text.getText(), textLayoutSelectable.getSelectedText());
    assertEquals(text.getText().length(), text.getCaretPosition());

    // selection RIGHT_TO_LEFT
    text.setSelectionRange(new SelectionRange(0, text.getText().length(), SelectionDirection.RIGHT_TO_LEFT));
    for (int i = 1; i < text.getText().length(); i++) {
      textLayoutSelectable.moveSelectionBorder(SWT.RIGHT);
      assertEquals("i=" + i, text.getText().substring(i), textLayoutSelectable.getSelectedText());
      assertEquals("i=" + i, i, text.getCaretPosition());
    }
    textLayoutSelectable.moveSelectionBorder(SWT.RIGHT);
    assertNull(textLayoutSelectable.getSelectedText());
    assertEquals(text.getText().length(), text.getCaretPosition());

    // test switch of selection direction
    text.setSelectionRange(new SelectionRange(4, 5, SelectionDirection.RIGHT_TO_LEFT));
    assertEquals(4, text.getCaretPosition());
    assertEquals("4", textLayoutSelectable.getSelectedText());
    assertEquals(SelectionDirection.RIGHT_TO_LEFT, text.getSelectionRange().getDirection());
    textLayoutSelectable.moveSelectionBorder(SWT.RIGHT);
    assertEquals(5, text.getCaretPosition());
    assertEquals(null, textLayoutSelectable.getSelectedText());
    assertEquals(null, text.getSelectionRange());
    textLayoutSelectable.moveSelectionBorder(SWT.RIGHT);
    assertEquals(6, text.getCaretPosition());
    assertEquals("5", textLayoutSelectable.getSelectedText());
    assertEquals(SelectionDirection.LEFT_TO_RIGHT, text.getSelectionRange().getDirection());

    // set cursor and start selection
    text.setSelectionRange(new SelectionRange(3, 5, SelectionDirection.LEFT_TO_RIGHT));
    assertEquals(5, text.getCaretPosition());
    text.setCaretPosition(13);
    assertEquals(13, text.getCaretPosition());
    assertEquals(null, textLayoutSelectable.getSelectedText());
    textLayoutSelectable.moveSelectionBorder(SWT.RIGHT);
    assertEquals(14, text.getCaretPosition());
    assertEquals("1", textLayoutSelectable.getSelectedText());
  }

  @Test
  public void testMoveSelectionBorderLeft() {
    // selection LEFT_TO_RIGHT
    text.setSelectionRange(new SelectionRange(0, text.getText().length(), SelectionDirection.LEFT_TO_RIGHT));
    for (int i = text.getText().length() - 1; i >= 1; i--) {
      textLayoutSelectable.moveSelectionBorder(SWT.LEFT);
      assertEquals("i=" + i, text.getText().substring(0, i), textLayoutSelectable.getSelectedText());
      assertEquals("i=" + i, i, text.getCaretPosition());
    }
    textLayoutSelectable.moveSelectionBorder(SWT.LEFT);
    assertNull(textLayoutSelectable.getSelectedText());
    assertEquals(0, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.LEFT);
    assertNull(textLayoutSelectable.getSelectedText());
    assertEquals(0, text.getCaretPosition());

    // selection RIGHT_TO_LEFT
    text.setCaretPosition(text.getText().length());
    for (int i = text.getText().length() - 1; i >= 1; i--) {
      textLayoutSelectable.moveSelectionBorder(SWT.LEFT);
      assertEquals("i=" + i, text.getText().substring(i), textLayoutSelectable.getSelectedText());
      assertEquals("i=" + i, i, text.getCaretPosition());
    }
    textLayoutSelectable.moveSelectionBorder(SWT.LEFT);
    assertEquals(text.getText(), textLayoutSelectable.getSelectedText());
    assertEquals(0, text.getCaretPosition());

    // test switch of selection direction
    text.setSelectionRange(new SelectionRange(4, 5, SelectionDirection.LEFT_TO_RIGHT));
    assertEquals(5, text.getCaretPosition());
    assertEquals("4", textLayoutSelectable.getSelectedText());
    assertEquals(SelectionDirection.LEFT_TO_RIGHT, text.getSelectionRange().getDirection());
    textLayoutSelectable.moveSelectionBorder(SWT.LEFT);
    assertEquals(4, text.getCaretPosition());
    assertEquals(null, textLayoutSelectable.getSelectedText());
    assertEquals(null, text.getSelectionRange());
    textLayoutSelectable.moveSelectionBorder(SWT.LEFT);
    assertEquals(3, text.getCaretPosition());
    assertEquals("3", textLayoutSelectable.getSelectedText());
    assertEquals(SelectionDirection.RIGHT_TO_LEFT, text.getSelectionRange().getDirection());

    // set cursor and start selection
    text.setSelectionRange(new SelectionRange(3, 5, SelectionDirection.RIGHT_TO_LEFT));
    assertEquals(3, text.getCaretPosition());
    text.setCaretPosition(13);
    assertEquals(13, text.getCaretPosition());
    assertNull(textLayoutSelectable.getSelectedText());
    textLayoutSelectable.moveSelectionBorder(SWT.LEFT);
    assertEquals(12, text.getCaretPosition());
    assertEquals("0", textLayoutSelectable.getSelectedText());
  }

  @Test
  public void testMoveSelectionBorderDown() {
    // selection LEFT_TO_RIGHT
    text.setSelectionRange(new SelectionRange(0, 1, SelectionDirection.LEFT_TO_RIGHT));
    assertEquals("0", textLayoutSelectable.getSelectedText());
    assertEquals(1, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.DOWN);
    assertEquals("012345\r\n8", textLayoutSelectable.getSelectedText());
    assertEquals(9, text.getCaretPosition());

    text.setSelectionRange(new SelectionRange(0, 2, SelectionDirection.LEFT_TO_RIGHT));
    assertEquals("01", textLayoutSelectable.getSelectedText());
    assertEquals(2, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.DOWN);
    assertEquals("012345\r\n89", textLayoutSelectable.getSelectedText());
    assertEquals(10, text.getCaretPosition());

    text.setSelectionRange(new SelectionRange(4, 6, SelectionDirection.LEFT_TO_RIGHT));
    assertEquals("45", textLayoutSelectable.getSelectedText());
    assertEquals(6, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.DOWN);
    assertEquals("45\r\n89", textLayoutSelectable.getSelectedText());
    assertEquals(10, text.getCaretPosition());

    text.setSelectionRange(new SelectionRange(47, 48, SelectionDirection.LEFT_TO_RIGHT));
    assertEquals("e", textLayoutSelectable.getSelectedText());
    assertEquals(48, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.DOWN);
    assertEquals("end", textLayoutSelectable.getSelectedText());
    assertEquals(50, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.DOWN);
    assertEquals("end", textLayoutSelectable.getSelectedText());
    assertEquals(50, text.getCaretPosition());

    // selection RIGHT_TO_LEFT
    text.setSelectionRange(new SelectionRange(13, 22, SelectionDirection.RIGHT_TO_LEFT));
    assertEquals("12345\r\n89", textLayoutSelectable.getSelectedText());
    assertEquals(13, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.DOWN);
    assertEquals("9", textLayoutSelectable.getSelectedText());
    assertEquals(21, text.getCaretPosition());
    assertEquals(SelectionDirection.RIGHT_TO_LEFT, text.getSelectionRange().getDirection());
    textLayoutSelectable.moveSelectionBorder(SWT.DOWN);
    assertEquals("\r\n2", textLayoutSelectable.getSelectedText());
    assertEquals(25, text.getCaretPosition());
    assertEquals(SelectionDirection.LEFT_TO_RIGHT, text.getSelectionRange().getDirection());

    // test if after down no selection
    text.setSelectionRange(new SelectionRange(1, 9, SelectionDirection.RIGHT_TO_LEFT));
    assertEquals("12345\r\n8", textLayoutSelectable.getSelectedText());
    assertEquals(1, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.DOWN);
    assertNull(textLayoutSelectable.getSelectedText());
    assertEquals(9, text.getCaretPosition());

    text.setSelectionRange(new SelectionRange(2, 10, SelectionDirection.RIGHT_TO_LEFT));
    assertEquals("2345\r\n89", textLayoutSelectable.getSelectedText());
    assertEquals(2, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.DOWN);
    assertNull(textLayoutSelectable.getSelectedText());
    assertEquals(10, text.getCaretPosition());

    // test without selection
    text.setCaretPosition(0);
    textLayoutSelectable.moveSelectionBorder(SWT.DOWN);
    assertEquals(8, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.DOWN);
    assertEquals(12, text.getCaretPosition());

    text.setCaretPosition(1);
    textLayoutSelectable.moveSelectionBorder(SWT.DOWN);
    assertEquals(9, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.DOWN);
    assertEquals(13, text.getCaretPosition());

    text.setCaretPosition(2);
    textLayoutSelectable.moveSelectionBorder(SWT.DOWN);
    assertEquals(10, text.getCaretPosition());

    text.setCaretPosition(3);
    textLayoutSelectable.moveSelectionBorder(SWT.DOWN);
    assertEquals(10, text.getCaretPosition());

    text.setCaretPosition(4);
    textLayoutSelectable.moveSelectionBorder(SWT.DOWN);
    assertEquals(10, text.getCaretPosition());

    text.setCaretPosition(49);
    textLayoutSelectable.moveSelectionBorder(SWT.DOWN);
    assertEquals(50, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.DOWN);
    assertEquals(50, text.getCaretPosition());
  }

  @Test
  public void testMoveSelectionBorderUp() {
    // selection LEFT_TO_RIGHT
    text.setSelectionRange(new SelectionRange(13, 22, SelectionDirection.LEFT_TO_RIGHT));
    assertEquals("12345\r\n89", textLayoutSelectable.getSelectedText());
    assertEquals(22, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.UP);
    assertEquals("1", textLayoutSelectable.getSelectedText());
    assertEquals(14, text.getCaretPosition());
    assertEquals(SelectionDirection.LEFT_TO_RIGHT, text.getSelectionRange().getDirection());
    textLayoutSelectable.moveSelectionBorder(SWT.UP);
    assertEquals("\r\n0", textLayoutSelectable.getSelectedText());
    assertEquals(10, text.getCaretPosition());
    assertEquals(SelectionDirection.RIGHT_TO_LEFT, text.getSelectionRange().getDirection());

    // selection RIGHT_TO_LEFT
    text.setSelectionRange(new SelectionRange(8, 9, SelectionDirection.RIGHT_TO_LEFT));
    assertEquals("8", textLayoutSelectable.getSelectedText());
    assertEquals(8, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.UP);
    assertEquals("012345\r\n8", textLayoutSelectable.getSelectedText());
    assertEquals(0, text.getCaretPosition());
    assertEquals(SelectionDirection.RIGHT_TO_LEFT, text.getSelectionRange().getDirection());

    text.setSelectionRange(new SelectionRange(8, 10, SelectionDirection.RIGHT_TO_LEFT));
    assertEquals("89", textLayoutSelectable.getSelectedText());
    assertEquals(8, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.UP);
    assertEquals("012345\r\n89", textLayoutSelectable.getSelectedText());
    assertEquals(0, text.getCaretPosition());

    text.setSelectionRange(new SelectionRange(2, 3, SelectionDirection.RIGHT_TO_LEFT));
    assertEquals("2", textLayoutSelectable.getSelectedText());
    assertEquals(2, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.UP);
    assertEquals("012", textLayoutSelectable.getSelectedText());
    assertEquals(0, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.UP);
    assertEquals("012", textLayoutSelectable.getSelectedText());
    assertEquals(0, text.getCaretPosition());

    // test if after up no selection
    text.setSelectionRange(new SelectionRange(1, 9, SelectionDirection.LEFT_TO_RIGHT));
    assertEquals("12345\r\n8", textLayoutSelectable.getSelectedText());
    assertEquals(9, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.UP);
    assertNull(textLayoutSelectable.getSelectedText());
    assertEquals(1, text.getCaretPosition());

    text.setSelectionRange(new SelectionRange(2, 10, SelectionDirection.LEFT_TO_RIGHT));
    assertEquals("2345\r\n89", textLayoutSelectable.getSelectedText());
    assertEquals(10, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.UP);
    assertNull(textLayoutSelectable.getSelectedText());
    assertEquals(2, text.getCaretPosition());

    // test without selection
    text.setCaretPosition(12);
    textLayoutSelectable.moveSelectionBorder(SWT.UP);
    assertEquals(8, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.UP);
    assertEquals(0, text.getCaretPosition());

    text.setCaretPosition(13);
    textLayoutSelectable.moveSelectionBorder(SWT.UP);
    assertEquals(9, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.UP);
    assertEquals(1, text.getCaretPosition());

    text.setCaretPosition(14);
    textLayoutSelectable.moveSelectionBorder(SWT.UP);
    assertEquals(10, text.getCaretPosition());

    text.setCaretPosition(15);
    textLayoutSelectable.moveSelectionBorder(SWT.UP);
    assertEquals(10, text.getCaretPosition());

    text.setCaretPosition(16);
    textLayoutSelectable.moveSelectionBorder(SWT.UP);
    assertEquals(10, text.getCaretPosition());

    text.setCaretPosition(1);
    textLayoutSelectable.moveSelectionBorder(SWT.UP);
    assertEquals(0, text.getCaretPosition());
    textLayoutSelectable.moveSelectionBorder(SWT.UP);
    assertEquals(0, text.getCaretPosition());
  }

  @Test
  @Ignore
  public void testSelectionChangeListener() {
    // TODO
    fail("TODO");
  }

  @Test
  @Ignore
  public void testGetSelectionBounds() {
    // TODO
    fail("TODO");
  }

  @Test
  @Ignore("unfinished")
  public void testGetSelection() {
    text.setSelectionRange(new SelectionRange(0, 1, SelectionDirection.LEFT_TO_RIGHT));
    final PointRange selection = textLayoutSelectable.getSelection();
    assertEquals(new Point(0, 0), selection.getFrom());
    assertEquals(new Point(charWidth - 1, lineHeight - 1), selection.getTo());
    assertEquals(0, text.getOffset(selection.getFrom()));
    assertEquals(0, text.getOffset(selection.getTo()));

    // TODO
    fail("TODO");
  }

  @After
  public void cleanUp() {
    text.dispose();
    shell.dispose();
    display.dispose();
  }
}
