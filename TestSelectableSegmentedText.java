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
import com.sap.clap.common.text.Segment;
import com.sap.clap.common.text.selection.SelectableSegment;
import com.sap.clap.common.text.selection.SelectableSegmentedText;
import com.sap.clap.common.text.selection.SelectableTextLayout;
import com.sap.clap.common.text.selection.SelectionDirection;
import com.sap.clap.common.text.selection.SelectionRange;

public class TestSelectableSegmentedText {

  private Display display;
  private Shell shell;
  
  final int charWidth = 6;
  final int lineHeight = 13;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell(display);
  }
  
  @Test
  public void testGetLineCount() {    
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      checkLineCount(text, 0);
      
      Segment segment1 = text.addSegment();
      // empty segment has already 1 line
      checkLineCount(text, 1);    
      
      segment1.append("first line");
      checkLineCount(text, 1);    
      
      segment1.append(" more text for first line");
      checkLineCount(text, 1);
      
      segment1.appendln();
      checkLineCount(text, 2);
      
      segment1.appendln("second line");
      checkLineCount(text, 3);
      
      Segment segment2 = text.addSegment();
      // empty segment has already 1 line
      checkLineCount(text, 4);    
      
      segment1.append("first line in second segment");
      checkLineCount(text, 4);
      
      segment2.appendln();
      checkLineCount(text, 5);
      
      text.addSegment();
      // empty segment has already 1 line
      checkLineCount(text, 6);    
      
      // append another line to first segment
      segment1.appendln();
      checkLineCount(text, 7);
    } finally {
      text.dispose();
    }
  }
  
  private void checkLineCount(SelectableSegmentedText text, int expectedLineCount) {
    int lineCount = text.getLineCount();
    assertTrue("Expected [" + expectedLineCount + "] lines for [" + text.getText() + "]; lineCount = [" + lineCount + "]", lineCount == expectedLineCount);
  }
  
  @Test
  public void testGetLineIndex() {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      Segment segment1 = text.addSegment();
      
      segment1.append("0123");
      checkLineIndex(text, 0, 0);
      checkLineIndex(text, 1, 0);
      checkLineIndex(text, 2, 0);
      checkLineIndex(text, 3, 0);
      
      segment1.append("45");
      checkLineIndex(text, 4, 0);
      checkLineIndex(text, 5, 0);      
      
      segment1.appendln();
      // line break
      checkLineIndex(text, 6, 0);
      checkLineIndex(text, 7, 0);
      
      segment1.append("89");
      checkLineIndex(text, 8, 1);
      checkLineIndex(text, 9, 1);
      
      Segment segment2 = text.addSegment();
      // segment break
      checkLineIndex(text, 10, 1);
      checkLineIndex(text, 11, 1);
      
      segment2.append("01");
      checkLineIndex(text, 12, 2);
      checkLineIndex(text, 13, 2);
      
      text.addSegment();
      // segment break
      checkLineIndex(text, 14, 2);
      checkLineIndex(text, 15, 2);
    } finally {
      text.dispose();    
    }
  }  
  
  private void checkLineIndex(SelectableSegmentedText text, int offset, int expectedLineIndex) {    
    int lineIndex = text.getLineIndex(offset);
    assertTrue("Expected [" + expectedLineIndex + "] as line index for [" + text.getText() + "] at offset [" + offset + "]; lineIndex = [" + lineIndex + "]", lineIndex == expectedLineIndex);
  }
  
  @Test
  public void testGetLineMetrics() {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      Segment segment1 = text.addSegment();
      
      FontData[] fontData10 = shell.getFont().getFontData();
      for (FontData fd : fontData10) {        
        fd.setHeight(10);
      }      
      Font f10 = new Font(Display.getDefault(), fontData10);      
      segment1.appendln("line with height = 10", f10);
      checkLineHeight(text, 0, 16); 
      
      FontData[] fontData20 = shell.getFont().getFontData();
      for (FontData fd : fontData20) {        
        fd.setHeight(20);
      }
      Font f20 = new Font(Display.getDefault(), fontData20);
      segment1.append("line with height = 20", f20);
      checkLineHeight(text, 1, 33); 
      
      // check if font metrics for first line are still returned correctly
      checkLineHeight(text, 0, 16); 
      
      Segment segment2 = text.addSegment();
      segment2.appendln("line with height = 10", f10);
      checkLineHeight(text, 2, 16);
      
      Segment segment3 = text.addSegment();
      segment3.appendln("line with height = 20", f20);
      checkLineHeight(text, 4, 33); 
    } finally {
      text.dispose();    
    }
  }
  
  private void checkLineHeight(SelectableSegmentedText text, int lineIndex, int expectedLineHeight) {    
    int lineHeight = text.getLineMetrics(lineIndex).getHeight();
    assertTrue("Expected line height [" + expectedLineHeight + "] for text [" + text .getText() + "] at line index [" + lineIndex + "]; line height = [" + lineHeight + "]", lineHeight == expectedLineHeight);
  }
  
  @Test
  public void testGetLocation() {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      Segment segment1 = text.addSegment();
      
      segment1.append("0123");
      checkLocation(text, 0, 0, 0);      
      checkLocation(text, 1, 6, 0);
      checkLocation(text, 2, 12, 0);
      checkLocation(text, 3, 18, 0);  
      segment1.appendln("45");
      checkLocation(text, 4, 24, 0);
      checkLocation(text, 5, 30, 0);
      
      // line break
      checkLocation(text, 6, 36, 0);      
      checkLocation(text, 7, 36, 0);
      
      segment1.append("89");
      checkLocation(text, 8, 0, 13);      
      checkLocation(text, 9, 6, 13);
      
      Segment segment2 = text.addSegment();
      // segment break
      checkLocation(text, 10, 12, 13);
      checkLocation(text, 11, 12, 13);
      
      segment2.append("01");
      checkLocation(text, 12, 0, 26);
      checkLocation(text, 13, 6, 26);      
    } finally {
      text.dispose();    
    }
  }
  
  private void checkLocation(SelectableSegmentedText text, int offset, int expectedX, int expectedY) {    
    Point location = text.getLocation(offset, false);
    Point expectedLocation = new Point(expectedX, expectedY);
    assertTrue("Expected location [" + expectedLocation + "] for text [" + text .getText() + "] at offset [" + offset + "]; location = [" + location + "]", expectedLocation.equals(location));
  }
  
  @Test
  public void testGetOffset() {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      Segment segment0 = text.addSegment();
      segment0.appendln("012345").append("89");
      Segment segment1 = text.addSegment();
      segment1.appendln("012345").appendln("89").append("234");
      Segment segment2 = text.addSegment();
      segment2.appendln("012345").append("89");
      
      assertEquals(0, text.getOffset(new Point(0, 0)));
      assertEquals(0, text.getOffset(new Point(charWidth - 1, lineHeight - 1)));
      assertEquals(1, text.getOffset(new Point(1 * charWidth, lineHeight - 1)));
      assertEquals(5, text.getOffset(new Point(5 * charWidth, lineHeight - 1)));
      assertEquals(8, text.getOffset(new Point(0, lineHeight)));
      assertEquals(9, text.getOffset(new Point(1 * charWidth, lineHeight + lineHeight - 1)));
      assertEquals(10, text.getOffset(new Point(1000, lineHeight + lineHeight - 1)));
      assertEquals(12, text.getOffset(new Point(0, 2 * lineHeight)));
      assertEquals(17, text.getOffset(new Point(5 * charWidth, 2 * lineHeight)));
      assertEquals(20, text.getOffset(new Point(0, 3 * lineHeight)));
      assertEquals(20, text.getOffset(new Point(-1, 3 * lineHeight)));
      assertEquals(22, text.getOffset(new Point(1000, 3 * lineHeight)));
      assertEquals(24, text.getOffset(new Point(0, 4 * lineHeight)));
      assertEquals(30, text.getOffset(new Point(1 * charWidth, 5 * lineHeight)));
      assertEquals(38, text.getOffset(new Point(1 * charWidth, 6 * lineHeight)));

      assertEquals(0, text.getOffset(new Point(-1, -1)));
      assertEquals(39, text.getOffset(new Point(1000, 1000)));
    } finally {
      text.dispose();    
    }
  }
  
  @Test
  public void testGetNextOffset() {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      Segment segment0 = text.addSegment();
      segment0.appendln("this is the first line").append("this is the second line");
      Segment segment1 = text.addSegment();
      segment1.appendln("this is the third line").append("this is the 4th line");
      
      assertEquals(4, text.getNextOffset(1, SWT.MOVEMENT_WORD_END));
      assertEquals(7, text.getNextOffset(4, SWT.MOVEMENT_WORD_END));
      assertEquals(11, text.getNextOffset(10, SWT.MOVEMENT_WORD_END));
      assertEquals(22, text.getNextOffset(22, SWT.MOVEMENT_WORD_END));
      assertEquals(28, text.getNextOffset(23, SWT.MOVEMENT_WORD_END));
      assertEquals(35, text.getNextOffset(33, SWT.MOVEMENT_WORD_END));
      assertEquals(47, text.getNextOffset(44, SWT.MOVEMENT_WORD_END));
      assertEquals(47, text.getNextOffset(47, SWT.MOVEMENT_WORD_END));
      assertEquals(53, text.getNextOffset(48, SWT.MOVEMENT_WORD_END));
      assertEquals(53, text.getNextOffset(49, SWT.MOVEMENT_WORD_END));
      assertEquals(71, text.getNextOffset(68, SWT.MOVEMENT_WORD_END));
      assertEquals(93, text.getNextOffset(90, SWT.MOVEMENT_WORD_END));
      assertEquals(93, text.getNextOffset(93, SWT.MOVEMENT_WORD_END));
      
      assertEquals(5, text.getNextOffset(2, SWT.MOVEMENT_WORD));
      assertEquals(8, text.getNextOffset(5, SWT.MOVEMENT_WORD));
      assertEquals(18, text.getNextOffset(13, SWT.MOVEMENT_WORD));
      assertEquals(22, text.getNextOffset(20, SWT.MOVEMENT_WORD));
      assertEquals(24, text.getNextOffset(23, SWT.MOVEMENT_WORD));
      assertEquals(47, text.getNextOffset(44, SWT.MOVEMENT_WORD));
      assertEquals(49, text.getNextOffset(48, SWT.MOVEMENT_WORD));
      assertEquals(54, text.getNextOffset(49, SWT.MOVEMENT_WORD));
      assertEquals(93, text.getNextOffset(90, SWT.MOVEMENT_WORD));
      
      assertEquals(5, text.getNextOffset(2, SWT.MOVEMENT_WORD_START));
      assertEquals(8, text.getNextOffset(5, SWT.MOVEMENT_WORD_START));
      assertEquals(18, text.getNextOffset(13, SWT.MOVEMENT_WORD_START));
      assertEquals(22, text.getNextOffset(20, SWT.MOVEMENT_WORD_START));
      assertEquals(24, text.getNextOffset(23, SWT.MOVEMENT_WORD_START));
      assertEquals(47, text.getNextOffset(44, SWT.MOVEMENT_WORD_START));
      assertEquals(49, text.getNextOffset(48, SWT.MOVEMENT_WORD_START));
      assertEquals(54, text.getNextOffset(49, SWT.MOVEMENT_WORD_START));
      assertEquals(93, text.getNextOffset(90, SWT.MOVEMENT_WORD_START));
      
      assertEquals(1, text.getNextOffset(0, SWT.MOVEMENT_CHAR));
      assertEquals(5, text.getNextOffset(4, SWT.MOVEMENT_CHAR));
      assertEquals(23, text.getNextOffset(22, SWT.MOVEMENT_CHAR));
      assertEquals(24, text.getNextOffset(23, SWT.MOVEMENT_CHAR));
      assertEquals(48, text.getNextOffset(47, SWT.MOVEMENT_CHAR));
      assertEquals(49, text.getNextOffset(48, SWT.MOVEMENT_CHAR));
      assertEquals(50, text.getNextOffset(49, SWT.MOVEMENT_CHAR));
      assertEquals(93, text.getNextOffset(92, SWT.MOVEMENT_CHAR));
      assertEquals(93, text.getNextOffset(93, SWT.MOVEMENT_CHAR));
      
      assertEquals(1, text.getNextOffset(0, SWT.MOVEMENT_CLUSTER));
      assertEquals(5, text.getNextOffset(4, SWT.MOVEMENT_CLUSTER));
      assertEquals(22, text.getNextOffset(22, SWT.MOVEMENT_CLUSTER));
      assertEquals(24, text.getNextOffset(23, SWT.MOVEMENT_CLUSTER));
      assertEquals(47, text.getNextOffset(47, SWT.MOVEMENT_CLUSTER));
      assertEquals(49, text.getNextOffset(48, SWT.MOVEMENT_CLUSTER));
      assertEquals(50, text.getNextOffset(49, SWT.MOVEMENT_CLUSTER));
      assertEquals(93, text.getNextOffset(92, SWT.MOVEMENT_CLUSTER));
      assertEquals(93, text.getNextOffset(93, SWT.MOVEMENT_CLUSTER));
      
      try {
        text.getNextOffset(-1, SWT.MOVEMENT_CLUSTER);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$
      }
      
      try {
        text.getNextOffset(94, SWT.MOVEMENT_CLUSTER);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$
      }
    } finally {
      text.dispose();    
    }
  }
  
  @Test
  public void testGetPreviousOffset() {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      Segment segment0 = text.addSegment();
      segment0.appendln("this is the first line").append("this is the second line");
      Segment segment1 = text.addSegment();
      segment1.appendln("this is the third line").append("this is the 4th line");
      
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
      assertEquals(43, text.getPreviousOffset(47, SWT.MOVEMENT_WORD_START));
      assertEquals(43, text.getPreviousOffset(48, SWT.MOVEMENT_WORD_START));
      assertEquals(47, text.getPreviousOffset(49, SWT.MOVEMENT_WORD_START));
      assertEquals(49, text.getPreviousOffset(50, SWT.MOVEMENT_WORD_START));
      assertEquals(54, text.getPreviousOffset(55, SWT.MOVEMENT_WORD_START));
      assertEquals(67, text.getPreviousOffset(71, SWT.MOVEMENT_WORD_START));
      assertEquals(71, text.getPreviousOffset(72, SWT.MOVEMENT_WORD_START));
      assertEquals(71, text.getPreviousOffset(73, SWT.MOVEMENT_WORD_START));
      assertEquals(89, text.getPreviousOffset(93, SWT.MOVEMENT_WORD_START));
      
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
      assertEquals(43, text.getPreviousOffset(48, SWT.MOVEMENT_WORD));
      assertEquals(47, text.getPreviousOffset(49, SWT.MOVEMENT_WORD));
      assertEquals(49, text.getPreviousOffset(50, SWT.MOVEMENT_WORD));
      assertEquals(54, text.getPreviousOffset(55, SWT.MOVEMENT_WORD));
      assertEquals(67, text.getPreviousOffset(71, SWT.MOVEMENT_WORD));
      assertEquals(71, text.getPreviousOffset(72, SWT.MOVEMENT_WORD));
      assertEquals(71, text.getPreviousOffset(73, SWT.MOVEMENT_WORD));
      assertEquals(89, text.getPreviousOffset(93, SWT.MOVEMENT_WORD));
      
      assertEquals(0, text.getPreviousOffset(0, SWT.MOVEMENT_WORD_END));
      assertEquals(0, text.getPreviousOffset(3, SWT.MOVEMENT_WORD_END));
      assertEquals(4, text.getPreviousOffset(6, SWT.MOVEMENT_WORD_END));
      assertEquals(17, text.getPreviousOffset(22, SWT.MOVEMENT_WORD_END));
      assertEquals(22, text.getPreviousOffset(23, SWT.MOVEMENT_WORD_END));
      assertEquals(22, text.getPreviousOffset(24, SWT.MOVEMENT_WORD_END));
      assertEquals(22, text.getPreviousOffset(25, SWT.MOVEMENT_WORD_END));
      assertEquals(28, text.getPreviousOffset(29, SWT.MOVEMENT_WORD_END));
      assertEquals(42, text.getPreviousOffset(47, SWT.MOVEMENT_WORD_END));
      assertEquals(42, text.getPreviousOffset(48, SWT.MOVEMENT_WORD_END));
      assertEquals(47, text.getPreviousOffset(49, SWT.MOVEMENT_WORD_END));
      assertEquals(47, text.getPreviousOffset(50, SWT.MOVEMENT_WORD_END));
      assertEquals(88, text.getPreviousOffset(93, SWT.MOVEMENT_WORD_END));
      
      assertEquals(0, text.getPreviousOffset(0, SWT.MOVEMENT_CHAR));
      assertEquals(0, text.getPreviousOffset(1, SWT.MOVEMENT_CHAR));
      assertEquals(4, text.getPreviousOffset(5, SWT.MOVEMENT_CHAR));
      assertEquals(21, text.getPreviousOffset(22, SWT.MOVEMENT_CHAR));
      assertEquals(22, text.getPreviousOffset(23, SWT.MOVEMENT_CHAR));
      assertEquals(23, text.getPreviousOffset(24, SWT.MOVEMENT_CHAR));
      assertEquals(46, text.getPreviousOffset(47, SWT.MOVEMENT_CHAR));
      assertEquals(47, text.getPreviousOffset(48, SWT.MOVEMENT_CHAR));
      assertEquals(48, text.getPreviousOffset(49, SWT.MOVEMENT_CHAR));      
      assertEquals(49, text.getPreviousOffset(50, SWT.MOVEMENT_CHAR));
      assertEquals(71, text.getPreviousOffset(72, SWT.MOVEMENT_CHAR));
      assertEquals(92, text.getPreviousOffset(93, SWT.MOVEMENT_CHAR));
      
      assertEquals(0, text.getPreviousOffset(0, SWT.MOVEMENT_CLUSTER));
      assertEquals(0, text.getPreviousOffset(1, SWT.MOVEMENT_CLUSTER));
      assertEquals(4, text.getPreviousOffset(5, SWT.MOVEMENT_CLUSTER));
      assertEquals(21, text.getPreviousOffset(22, SWT.MOVEMENT_CLUSTER));
      assertEquals(22, text.getPreviousOffset(23, SWT.MOVEMENT_CLUSTER));
      assertEquals(22, text.getPreviousOffset(24, SWT.MOVEMENT_CLUSTER));
      assertEquals(24, text.getPreviousOffset(25, SWT.MOVEMENT_CLUSTER));
      assertEquals(46, text.getPreviousOffset(47, SWT.MOVEMENT_CLUSTER));
      assertEquals(47, text.getPreviousOffset(48, SWT.MOVEMENT_CLUSTER));
      assertEquals(47, text.getPreviousOffset(49, SWT.MOVEMENT_CLUSTER));
      assertEquals(49, text.getPreviousOffset(50, SWT.MOVEMENT_CLUSTER));
      assertEquals(92, text.getPreviousOffset(93, SWT.MOVEMENT_CLUSTER));
      
      try {
        text.getPreviousOffset(-1, SWT.MOVEMENT_CLUSTER);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$
      }
      
      try {
        text.getPreviousOffset(94, SWT.MOVEMENT_CLUSTER);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$
      }
    } finally {
      text.dispose();    
    }
  }
  
  @Test
  public void testSelection() {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
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
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
      
      SelectableSegment segment0 = (SelectableSegment)text.addSegment();
      segment0.appendln("this is the first line").append("this is the second line");
      SelectableSegment segment1 = (SelectableSegment)text.addSegment();
      segment1.appendln("this is the third line").append("this is the 4th line");
      
      text.setSelectionRange(new SelectionRange(0, 1, SelectionDirection.LEFT_TO_RIGHT));
      assertTrue(text.hasSelection());
      assertEquals("t", text.getSelectedText());
      assertTrue(segment0.hasSelection());
      assertEquals("t", segment0.getSelectedText());
      assertFalse(segment1.hasSelection());
      assertEquals(new SelectionRange(0, 1, SelectionDirection.LEFT_TO_RIGHT), text.getSelectionRange());
      
      text.setSelectionRange(new SelectionRange(5, 10, SelectionDirection.RIGHT_TO_LEFT));
      assertTrue(text.hasSelection());
      assertEquals("is th", text.getSelectedText());
      assertTrue(segment0.hasSelection());
      assertEquals("is th", segment0.getSelectedText());
      assertFalse(segment1.hasSelection());
      assertEquals(new SelectionRange(5, 10, SelectionDirection.RIGHT_TO_LEFT), text.getSelectionRange());
      
      text.setSelectionRange(new SelectionRange(0, 10, SelectionDirection.RIGHT_TO_LEFT));
      assertTrue(text.hasSelection());
      assertEquals("this is th", text.getSelectedText());
      assertTrue(segment0.hasSelection());
      assertEquals("this is th", segment0.getSelectedText());
      assertFalse(segment1.hasSelection());
      assertEquals(new SelectionRange(0, 10, SelectionDirection.RIGHT_TO_LEFT), text.getSelectionRange());
      
      try {
        text.setSelectionRange(new SelectionRange(0, 100, SelectionDirection.RIGHT_TO_LEFT));
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
      
      text.clearSelection();
      assertFalse(text.hasSelection());
      for (Segment segment : text.getSegments()) {
        assertFalse(((SelectableSegment)segment).hasSelection());
      }
      assertNull(text.getSelectionRange());
      assertNull(text.getSelectedText());
      
      text.setSelectionRange(new SelectionRange(0, 10, SelectionDirection.RIGHT_TO_LEFT));
      assertTrue(text.hasSelection());
      text.setSelectionRange(null);
      assertFalse(text.hasSelection());
      for (Segment segment : text.getSegments()) {
        assertFalse(((SelectableSegment)segment).hasSelection());
      }
      assertNull(text.getSelectionRange());
      assertNull(text.getSelectedText());
      
      text.setSelectionRange(new SelectionRange(54, 60, SelectionDirection.RIGHT_TO_LEFT));
      assertTrue(text.hasSelection());
      assertEquals("is the", text.getSelectedText());
      assertFalse(segment0.hasSelection());
      assertTrue(segment1.hasSelection());
      assertEquals("is the", segment1.getSelectedText());
      assertEquals(new SelectionRange(54, 60, SelectionDirection.RIGHT_TO_LEFT), text.getSelectionRange());
      
      text.setSelectionRange(new SelectionRange(21, 25, SelectionDirection.RIGHT_TO_LEFT));
      assertTrue(text.hasSelection());
      assertEquals("e\r\nt", text.getSelectedText());
      assertTrue(segment0.hasSelection());
      assertEquals("e\r\nt", segment0.getSelectedText());
      assertFalse(segment1.hasSelection());
      assertEquals(new SelectionRange(21, 25, SelectionDirection.RIGHT_TO_LEFT), text.getSelectionRange());
      
      text.setSelectionRange(new SelectionRange(46, 50, SelectionDirection.RIGHT_TO_LEFT));
      assertTrue(text.hasSelection());
      assertEquals("e\r\nt", text.getSelectedText());
      assertTrue(segment0.hasSelection());
      assertEquals("e\r\n", segment0.getSelectedText());
      assertTrue(segment1.hasSelection());
      assertEquals("t", segment1.getSelectedText());
      assertEquals(new SelectionRange(46, 50, SelectionDirection.RIGHT_TO_LEFT), text.getSelectionRange());
    } finally {
      text.dispose();    
    }
  }
  
  @Test
  @Ignore
  public void testSegmentSelection() {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {      
      // TODO test what happens with the selection of SelectableSegmentedText when a new selection is set on one of the segments
      fail("TODO");
    } finally {
      text.dispose();    
    }
  }

  @Test
  public void testCursorPosition() {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      assertEquals(0, text.getCaretPosition());
      
      SelectableSegment segment0 = (SelectableSegment)text.addSegment();
      segment0.appendln("this is the first line").append("this is the second line");
      SelectableSegment segment1 = (SelectableSegment)text.addSegment();
      segment1.appendln("this is the third line").append("this is the 4th line");
      
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
      
      text.setCaretPosition(text.getText().length());
      assertEquals(text.getText().length(), text.getCaretPosition());
      
      text.setSelectionRange(new SelectionRange(46, 50, SelectionDirection.LEFT_TO_RIGHT));
      assertEquals(50, text.getCaretPosition());
      text.setSelectionRange(new SelectionRange(46, 50, SelectionDirection.RIGHT_TO_LEFT));
      assertEquals(46, text.getCaretPosition());
      
      try {
        text.setCaretPosition(-1);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
      
      try {
        text.setCaretPosition(text.getText().length() + 1);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
    } finally {
      text.dispose();    
    }
  }
  
  @Test
  @Ignore
  public void testGetSelectionBounds() {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      assertNull(text.getSelectionBounds());
      
      SelectableSegment segment0 = (SelectableSegment)text.addSegment();
      segment0.appendln("this is the first line", FontUtil.syntaxFont()).append("this is the second line", FontUtil.syntaxFont());
      SelectableSegment segment1 = (SelectableSegment)text.addSegment();
      segment1.appendln("this is the third line", FontUtil.syntaxFont()).append("this is the 4th line", FontUtil.syntaxFont());
      
      assertNull(text.getSelectionBounds());
      
      int charWidth = 8;
      
      text.setSelectionRange(new SelectionRange(0, 1, SelectionDirection.LEFT_TO_RIGHT));
      assertEquals(new Rectangle(0, 0, charWidth, lineHeight), text.getSelectionBounds());
      
      text.setSelectionRange(new SelectionRange(2, 6, SelectionDirection.RIGHT_TO_LEFT));
      assertEquals(new Rectangle(2 * charWidth, 0, 4 * charWidth, lineHeight), text.getSelectionBounds());
      
      text.setSelectionRange(new SelectionRange(0, text.getText().length(), SelectionDirection.LEFT_TO_RIGHT));
      assertEquals(text.getBounds(), text.getSelectionBounds());
      
      text.setSelectionRange(new SelectionRange(46, 50, SelectionDirection.LEFT_TO_RIGHT));
      assertEquals(new Rectangle(0, lineHeight, 23 * charWidth, 2 * lineHeight), text.getSelectionBounds());      
    } finally {
      text.dispose();    
    }
  }
  
  @Test
  public void testSelectionChangedListener() {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      SelectableSegment segment0 = (SelectableSegment)text.addSegment();
      segment0.appendln("this is the first line", FontUtil.syntaxFont()).append("this is the second line", FontUtil.syntaxFont());
      SelectableSegment segment1 = (SelectableSegment)text.addSegment();
      segment1.appendln("this is the third line", FontUtil.syntaxFont()).append("this is the 4th line", FontUtil.syntaxFont());
      
      int charWidth = 8;
      
      VerifyingSelectionChangedListener<SelectableTextLayout> selectionChangedListener = new VerifyingSelectionChangedListener<SelectableTextLayout>(text, new Rectangle(0, 0, charWidth, lineHeight), null);
      text.addSelectionChangedListener(selectionChangedListener);
      VerifyingSelectionChangedListener<SelectableTextLayout> segment0SelectionChangedListener = new VerifyingSelectionChangedListener<SelectableTextLayout>(segment0, new Rectangle(0, 0, charWidth, lineHeight), null);
      segment0.addSelectionChangedListener(segment0SelectionChangedListener);
      VerifyingSelectionChangedListener<SelectableTextLayout> segment1SelectionChangedListener = new VerifyingSelectionChangedListener<SelectableTextLayout>();
      segment1.addSelectionChangedListener(segment1SelectionChangedListener);
      text.setSelectionRange(new SelectionRange(0, 1, SelectionDirection.LEFT_TO_RIGHT));
      selectionChangedListener.verify();
      segment0SelectionChangedListener.verify();
      segment1SelectionChangedListener.verify();
      
      text.setSelectionRange(new SelectionRange(0, 1, SelectionDirection.LEFT_TO_RIGHT));
      selectionChangedListener.verify();
      segment0SelectionChangedListener.verify();
      segment1SelectionChangedListener.verify();
      
      VerifyingSelectionChangedListener<SelectableTextLayout> selectionChangedListener2 = new VerifyingSelectionChangedListener<SelectableTextLayout>(text, new Rectangle(0, 0, 6 * charWidth, lineHeight), new Rectangle(0, 0, charWidth, lineHeight));
      text.addSelectionChangedListener(selectionChangedListener2);
      text.removeSelectionChangedListener(selectionChangedListener);
      VerifyingSelectionChangedListener<SelectableTextLayout> segment0SelectionChangedListener2 = new VerifyingSelectionChangedListener<SelectableTextLayout>(segment0, new Rectangle(0, 0, 6 * charWidth, lineHeight), new Rectangle(0, 0, charWidth, lineHeight));
      segment0.addSelectionChangedListener(segment0SelectionChangedListener2);
      segment0.removeSelectionChangedListener(segment0SelectionChangedListener);
      text.setSelectionRange(new SelectionRange(0, 6, SelectionDirection.LEFT_TO_RIGHT));      
      selectionChangedListener2.verify();
      segment1SelectionChangedListener.verify();
      segment0SelectionChangedListener2.verify();
      selectionChangedListener.verify();
      segment0SelectionChangedListener.verify();
      
      text.removeSelectionChangedListener(selectionChangedListener2);
      segment0.removeSelectionChangedListener(segment0SelectionChangedListener2);
      VerifyingSelectionChangedListener<SelectableTextLayout> selectionChangedListener3 = new VerifyingSelectionChangedListener<SelectableTextLayout>(text, null, new Rectangle(0, 0, 6 * charWidth, lineHeight));
      text.addSelectionChangedListener(selectionChangedListener3);
      VerifyingSelectionChangedListener<SelectableTextLayout> segment0SelectionChangedListener3 = new VerifyingSelectionChangedListener<SelectableTextLayout>(segment0, null, new Rectangle(0, 0, 6 * charWidth, lineHeight));
      segment0.addSelectionChangedListener(segment0SelectionChangedListener3);
      text.clearSelection();
      text.clearSelection();
      selectionChangedListener3.verify();
      segment0SelectionChangedListener3.verify();
      text.removeSelectionChangedListener(selectionChangedListener3);
      segment0.removeSelectionChangedListener(segment0SelectionChangedListener3);
      segment1.removeSelectionChangedListener(segment0SelectionChangedListener);
      
      text.removeSelectionChangedListener(selectionChangedListener3);
      segment0.removeSelectionChangedListener(segment0SelectionChangedListener3);
      segment1.removeSelectionChangedListener(segment1SelectionChangedListener);
      VerifyingSelectionChangedListener<SelectableTextLayout> selectionChangedListener4 = new VerifyingSelectionChangedListener<SelectableTextLayout>(text, new Rectangle(0, 2 * lineHeight, 6 * charWidth, lineHeight), null);
      text.addSelectionChangedListener(selectionChangedListener4);
      VerifyingSelectionChangedListener<SelectableTextLayout> segment0SelectionChangedListener4 = new VerifyingSelectionChangedListener<SelectableTextLayout>();
      segment0.addSelectionChangedListener(segment0SelectionChangedListener4);
      VerifyingSelectionChangedListener<SelectableTextLayout> segment1SelectionChangedListener2 = new VerifyingSelectionChangedListener<SelectableTextLayout>(segment1, new Rectangle(0, 0, 6 * charWidth, lineHeight), null);
      segment1.addSelectionChangedListener(segment1SelectionChangedListener2);
      text.setSelectionRange(new SelectionRange(49, 55, SelectionDirection.LEFT_TO_RIGHT));      
      selectionChangedListener4.verify();
      segment0SelectionChangedListener4.verify();
      segment1SelectionChangedListener2.verify();
      
      text.removeSelectionChangedListener(selectionChangedListener4);
      segment0.removeSelectionChangedListener(segment0SelectionChangedListener4);
      segment1.removeSelectionChangedListener(segment1SelectionChangedListener2);
      VerifyingSelectionChangedListener<SelectableTextLayout> selectionChangedListener5 = new VerifyingSelectionChangedListener<SelectableTextLayout>(text, new Rectangle(0, lineHeight, 23 * charWidth, 2 * lineHeight), new Rectangle(0, 2 * lineHeight, 6 * charWidth, lineHeight));
      text.addSelectionChangedListener(selectionChangedListener5);
      VerifyingSelectionChangedListener<SelectableTextLayout> segment0SelectionChangedListener5 = new VerifyingSelectionChangedListener<SelectableTextLayout>(segment0, new Rectangle(22 * charWidth, lineHeight, charWidth, lineHeight), null);
      segment0.addSelectionChangedListener(segment0SelectionChangedListener5);
      VerifyingSelectionChangedListener<SelectableTextLayout> segment1SelectionChangedListener3 = new VerifyingSelectionChangedListener<SelectableTextLayout>(segment1, new Rectangle(0, 0, 7 * charWidth, lineHeight), new Rectangle(0, 0, 6 * charWidth, lineHeight));
      segment1.addSelectionChangedListener(segment1SelectionChangedListener3);
      text.setSelectionRange(new SelectionRange(46, 56, SelectionDirection.LEFT_TO_RIGHT));      
      selectionChangedListener5.verify();
      segment0SelectionChangedListener5.verify();
      segment1SelectionChangedListener3.verify();
    } finally {
      text.dispose();    
    }
  }
  
  @Test
  @Ignore
  public void testGetLineIndexRelativeToSegmentedText() throws Throwable {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      Segment segment0 = text.addSegment();
      segment0.appendln("012345").appendln("012345").append("012345");      
      
      assertEquals(0, getLineIndexRelativeToSegmentedText(text, segment0, 0));
      assertEquals(1, getLineIndexRelativeToSegmentedText(text, segment0, 1));
      assertEquals(2, getLineIndexRelativeToSegmentedText(text, segment0, 2));
      
      try {
        getLineIndexRelativeToSegmentedText(text, segment0, 3);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
      
      Segment segment1 = text.addSegment();
      segment1.appendln("012345").appendln("012345").append("012345");
      assertEquals(3, getLineIndexRelativeToSegmentedText(text, segment0, 3));
      assertEquals(2, getLineIndexRelativeToSegmentedText(text, segment1, -1));
      assertEquals(3, getLineIndexRelativeToSegmentedText(text, segment1, 0));
      assertEquals(4, getLineIndexRelativeToSegmentedText(text, segment1, 1));
      assertEquals(5, getLineIndexRelativeToSegmentedText(text, segment1, 2));
      
      try {
        getLineIndexRelativeToSegmentedText(text, segment1, 3);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
      
      Segment segment2 = text.addSegment();
      segment2.appendln("012345").appendln("012345").append("012345");
      assertEquals(6, getLineIndexRelativeToSegmentedText(text, segment2, 0));
      assertEquals(7, getLineIndexRelativeToSegmentedText(text, segment2, 1));
      assertEquals(8, getLineIndexRelativeToSegmentedText(text, segment2, 2));
    } finally {
      text.dispose();    
    }
  }
  
  @SuppressWarnings("unchecked")
  private int getLineIndexRelativeToSegmentedText(SelectableSegmentedText text, Segment segment, int lineIndex) throws Throwable {
    return (Integer)TestUtil.invokeMethod(text, "getLineIndexRelativeToSegmentedText", new Class[] {Segment.class, int.class}, new Object[] {segment, lineIndex});
  }
  
  @Test
  @Ignore
  public void testGetLineIndexForSegment() throws Throwable {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      Segment segment0 = text.addSegment();
      segment0.appendln("012345").appendln("012345").append("012345");
      Segment segment1 = text.addSegment();
      segment1.appendln("012345").appendln("012345").append("012345");
      Segment segment2 = text.addSegment();
      segment2.appendln("012345").appendln("012345").append("012345");
      
      assertEquals(0, getLineIndex(text, segment0));
      assertEquals(3, getLineIndex(text, segment1));
      assertEquals(6, getLineIndex(text, segment2));
    } finally {
      text.dispose();    
    }
  }
  
  @SuppressWarnings("unchecked")
  private int getLineIndex(SelectableSegmentedText text, Segment segment) throws Throwable {
    return (Integer)TestUtil.invokeMethod(text, "getLineIndex", new Class[] {Segment.class}, new Object[] {segment}); 
  }
  
  @Test
  @Ignore
  public void testGetOffsetRelativeToSegment() throws Throwable {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      Segment segment0 = text.addSegment();
      segment0.appendln("012345").append("89");
      Segment segment1 = text.addSegment();
      segment1.appendln("012345").append("89");
      Segment segment2 = text.addSegment();
      segment2.appendln("012345").append("89");
      
      assertEquals(0, getOffsetRelativeToSegment(text, segment0, 0));
      assertEquals(9, getOffsetRelativeToSegment(text, segment0, 9));
      assertEquals(10, getOffsetRelativeToSegment(text, segment0, 10));
      assertEquals(11, getOffsetRelativeToSegment(text, segment0, 11));
      assertEquals(12, getOffsetRelativeToSegment(text, segment0, 12));
      
      assertEquals(0, getOffsetRelativeToSegment(text, segment1, 12));
      assertEquals(9, getOffsetRelativeToSegment(text, segment1, 21));
      assertEquals(0, getOffsetRelativeToSegment(text, segment2, 24));
      assertEquals(9, getOffsetRelativeToSegment(text, segment2, 33));
      assertEquals(10, getOffsetRelativeToSegment(text, segment2, 34));
      
      try {
        getOffsetRelativeToSegment(text, segment0, 13);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
      
      try {
        getOffsetRelativeToSegment(text, segment1, 11);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
      
      try {
        getOffsetRelativeToSegment(text, segment2, 35);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
    } finally {
      text.dispose();    
    }
  }
  
  @SuppressWarnings("unchecked")
  private int getOffsetRelativeToSegment(SelectableSegmentedText text, Segment segment, int offset) throws Throwable {
    return (Integer)TestUtil.invokeMethod(text, "getOffsetRelativeToSegment", new Class[] {Segment.class, int.class}, new Object[] {segment, offset}); 
  }
  
  @Test
  @Ignore
  public void testGetOffsetForSegment() throws Throwable {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      Segment segment0 = text.addSegment();
      segment0.appendln("012345").append("89");
      Segment segment1 = text.addSegment();
      segment1.appendln("012345").append("89");
      Segment segment2 = text.addSegment();
      segment2.appendln("012345").append("89");
      
      assertEquals(0, getOffset(text, segment0));
      assertEquals(12, getOffset(text, segment1));
      assertEquals(24, getOffset(text, segment2));
    } finally {
      text.dispose();    
    }
  }
  
  @SuppressWarnings("unchecked")
  private int getOffset(SelectableSegmentedText text, Segment segment) throws Throwable {
    return (Integer)TestUtil.invokeMethod(text, "getOffset", new Class[] {Segment.class}, new Object[] {segment}); 
  }
  
  @Test
  @Ignore
  public void testFindSegmentByOffset() throws Throwable {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      Segment segment0 = text.addSegment();
      segment0.appendln("012345").append("89");
      Segment segment1 = text.addSegment();
      segment1.appendln("012345").append("89");
      Segment segment2 = text.addSegment();
      segment2.appendln("012345").append("89");
      
      assertEquals(segment0, findSegmentByOffset(text, 0));
      assertEquals(segment0, findSegmentByOffset(text, 9));
      assertEquals(segment0, findSegmentByOffset(text, 10));
      assertEquals(segment0, findSegmentByOffset(text, 11));
      assertEquals(segment1, findSegmentByOffset(text, 12));
      assertEquals(segment1, findSegmentByOffset(text, 23));
      assertEquals(segment2, findSegmentByOffset(text, 24));
      assertEquals(segment2, findSegmentByOffset(text, 33));
      assertEquals(segment2, findSegmentByOffset(text, 34));
      
      try {
        findSegmentByOffset(text, -1);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
      
      try {
        findSegmentByOffset(text, 35);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
    } finally {
      text.dispose();    
    }
  }
  
  @SuppressWarnings("unchecked")
  private Segment findSegmentByOffset(SelectableSegmentedText text, int offset) throws Throwable {
    return (Segment)TestUtil.invokeMethod(text, "findSegmentByOffset", new Class[] {int.class}, new Object[] {offset}); 
  }
  
  @Test
  @Ignore
  public void testGetLineIndexRelativeToSegment() throws Throwable {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      Segment segment0 = text.addSegment();
      segment0.appendln("012345").append("89");
      Segment segment1 = text.addSegment();
      segment1.appendln("012345").appendln("89").appendln("234");
      Segment segment2 = text.addSegment();
      segment2.appendln("012345").append("89");
      
      assertEquals(0, getLineIndexRelativeToSegment(text, segment0, 0));
      assertEquals(1, getLineIndexRelativeToSegment(text, segment0, 1));
      assertEquals(0, getLineIndexRelativeToSegment(text, segment1, 2));
      assertEquals(1, getLineIndexRelativeToSegment(text, segment1, 3));
      assertEquals(2, getLineIndexRelativeToSegment(text, segment1, 4));
      assertEquals(3, getLineIndexRelativeToSegment(text, segment1, 5));
      assertEquals(0, getLineIndexRelativeToSegment(text, segment2, 6));
      assertEquals(1, getLineIndexRelativeToSegment(text, segment2, 7));
      
      try {
        getLineIndexRelativeToSegment(text, segment0, -1);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
      
      try {
        getLineIndexRelativeToSegment(text, segment0, 2);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
      
      try {
        getLineIndexRelativeToSegment(text, segment1, 1);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
      
      try {
        getLineIndexRelativeToSegment(text, segment1, 6);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
      
      try {
        getLineIndexRelativeToSegment(text, segment2, 5);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
      
      try {
        getLineIndexRelativeToSegment(text, segment2, 8);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
    } finally {
      text.dispose();    
    }
  }
  
  @SuppressWarnings("unchecked")
  private int getLineIndexRelativeToSegment(SelectableSegmentedText text, Segment segment, int lineIndex) throws Throwable {
    return (Integer)TestUtil.invokeMethod(text, "getLineIndexRelativeToSegment", new Class[] {Segment.class, int.class}, new Object[] {segment, lineIndex}); 
  }
  
  @Test
  @Ignore
  public void testFindSegmentByLineIndex() throws Throwable {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      Segment segment0 = text.addSegment();
      segment0.appendln("012345").append("89");
      Segment segment1 = text.addSegment();
      segment1.appendln("012345").appendln("89").appendln("234");
      Segment segment2 = text.addSegment();
      segment2.appendln("012345").append("89");
            
      assertEquals(segment0, findSegmentByLineIndex(text, 0));
      assertEquals(segment0, findSegmentByLineIndex(text, 1));
      assertEquals(segment1, findSegmentByLineIndex(text, 2));
      assertEquals(segment1, findSegmentByLineIndex(text, 3));
      assertEquals(segment1, findSegmentByLineIndex(text, 4));
      assertEquals(segment1, findSegmentByLineIndex(text, 5));
      assertEquals(segment2, findSegmentByLineIndex(text, 6));
      assertEquals(segment2, findSegmentByLineIndex(text, 7));
      
      try {
        findSegmentByLineIndex(text, -1);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
      
      try {
        findSegmentByLineIndex(text, 8);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
    } finally {
      text.dispose();    
    }
  }
  
  @SuppressWarnings("unchecked")
  private Segment findSegmentByLineIndex(SelectableSegmentedText text, int lineIndex) throws Throwable {
    return (Segment)TestUtil.invokeMethod(text, "findSegmentByLineIndex", new Class[] {int.class}, new Object[] {lineIndex}); 
  }
  
  @Test
  @Ignore
  public void testGetOffsetRelativeToSegmentedText() throws Throwable {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      Segment segment0 = text.addSegment();
      segment0.appendln("012345").append("89");
      Segment segment1 = text.addSegment();
      segment1.appendln("012345").appendln("89").append("234");
      Segment segment2 = text.addSegment();
      segment2.appendln("012345").append("89");
      
      assertEquals(0, getOffsetRelativeToSegmentedText(text, segment0, 0));
      assertEquals(1, getOffsetRelativeToSegmentedText(text, segment0, 1));
      assertEquals(9, getOffsetRelativeToSegmentedText(text, segment0, 9));
      assertEquals(10, getOffsetRelativeToSegmentedText(text, segment0, 10));
      assertEquals(11, getOffsetRelativeToSegmentedText(text, segment0, 11));
      assertEquals(12, getOffsetRelativeToSegmentedText(text, segment0, 12));
      assertEquals(11, getOffsetRelativeToSegmentedText(text, segment1, -1));
      assertEquals(12, getOffsetRelativeToSegmentedText(text, segment1, 0));
      assertEquals(26, getOffsetRelativeToSegmentedText(text, segment1, 14));
      assertEquals(27, getOffsetRelativeToSegmentedText(text, segment1, 15));
      assertEquals(28, getOffsetRelativeToSegmentedText(text, segment1, 16));
      assertEquals(29, getOffsetRelativeToSegmentedText(text, segment2, 0));
      assertEquals(38, getOffsetRelativeToSegmentedText(text, segment2, 9));
      assertEquals(39, getOffsetRelativeToSegmentedText(text, segment2, 10));
      
      try {
        getOffsetRelativeToSegmentedText(text, segment0, -1);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
      
      try {
        getOffsetRelativeToSegmentedText(text, segment2, 11);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
    } finally {
      text.dispose();    
    }
  }
  
  @SuppressWarnings("unchecked")
  private int getOffsetRelativeToSegmentedText(SelectableSegmentedText text, Segment segment, int offset) throws Throwable {
    return (Integer)TestUtil.invokeMethod(text, "getOffsetRelativeToSegmentedText", new Class[] {Segment.class, int.class}, new Object[] {segment, offset}); 
  }
  
  @Test
  @Ignore
  public void testFindSegment() throws Throwable {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {      
      assertEquals(null, findSegment(text, new Point(10, 100)));
      
      Segment segment0 = text.addSegment();
      segment0.appendln("012345").append("89");
      Segment segment1 = text.addSegment();
      segment1.appendln("012345").appendln("89").append("234");
      Segment segment2 = text.addSegment();
      segment2.appendln("012345").append("89");     
      
      assertEquals(segment0, findSegment(text, new Point(0, -10)));
      assertEquals(segment0, findSegment(text, new Point(0, 0)));
      assertEquals(segment0, findSegment(text, new Point(100000, 0)));
      assertEquals(segment0, findSegment(text, new Point(-10, 0)));
      assertEquals(segment0, findSegment(text, new Point(0, 2 * lineHeight - 1)));
      assertEquals(segment0, findSegment(text, new Point(6 * charWidth - 1, 2 * lineHeight - 1)));
      assertEquals(segment1, findSegment(text, new Point(0, 2 * lineHeight)));
      assertEquals(segment1, findSegment(text, new Point(0, 5 * lineHeight - 1)));
      assertEquals(segment2, findSegment(text, new Point(0, 5 * lineHeight)));
      assertEquals(segment2, findSegment(text, new Point(0, 7 * lineHeight - 1)));
      assertEquals(segment2, findSegment(text, new Point(0, 1000000000)));
    } finally {
      text.dispose();    
    }
  }
  
  @SuppressWarnings("unchecked")
  private Segment findSegment(SelectableSegmentedText text, Point point) throws Throwable {
    return (Segment)TestUtil.invokeMethod(text, "findSegment", new Class[] {Point.class}, new Object[] {point}); 
  }
  
  @Test
  @Ignore
  public void testIsSegementHere() throws Throwable {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      Segment segment0 = text.addSegment();
      segment0.appendln("012345").append("89");
      Segment segment1 = text.addSegment();
      segment1.appendln("012345").appendln("89").append("234");
      Segment segment2 = text.addSegment();
      segment2.appendln("012345").append("89");
      
      assertEquals(1, isSegmentHere(text, segment0, new Point(0, -1)));
      assertEquals(0, isSegmentHere(text, segment0, new Point(0, 0)));
      assertEquals(0, isSegmentHere(text, segment0, new Point(100000, 0)));
      assertEquals(0, isSegmentHere(text, segment0, new Point(-10, 0)));
      assertEquals(0, isSegmentHere(text, segment0, new Point(0, 2 * lineHeight - 1)));
      assertEquals(0, isSegmentHere(text, segment0, new Point(6 * charWidth - 1, 2 * lineHeight - 1)));
      assertEquals(-1, isSegmentHere(text, segment0, new Point(0, 2 * lineHeight)));
      assertEquals(1, isSegmentHere(text, segment1, new Point(0, 2 * lineHeight - 1)));
      assertEquals(0, isSegmentHere(text, segment1, new Point(0, 2 * lineHeight)));
      assertEquals(0, isSegmentHere(text, segment1, new Point(0, 5 * lineHeight - 1)));
      assertEquals(-1, isSegmentHere(text, segment1, new Point(0, 5 * lineHeight)));
      assertEquals(1, isSegmentHere(text, segment2, new Point(0, 5 * lineHeight - 1)));
      assertEquals(0, isSegmentHere(text, segment2, new Point(0, 5 * lineHeight)));
      assertEquals(0, isSegmentHere(text, segment2, new Point(0, 7 * lineHeight - 1)));
      assertEquals(-1, isSegmentHere(text, segment2, new Point(0, 7 * lineHeight)));
    } finally {
      text.dispose();    
    }
  }
  
  @SuppressWarnings("unchecked")
  private int isSegmentHere(SelectableSegmentedText text, Segment segment, Point point) throws Throwable {
    return (Integer)TestUtil.invokeMethod(text, "isSegmentHere", new Class[] {Segment.class, Point.class}, new Object[] {segment, point}); 
  }
  
  @Test
  public void testGetSegmentsAffectedBySelectionChange() throws Throwable {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      Segment segment0 = text.addSegment();
      segment0.appendln("012345").append("89");
      Segment segment1 = text.addSegment();
      segment1.appendln("012345").appendln("89").append("234");
      Segment segment2 = text.addSegment();
      segment2.appendln("012345").append("89");
      
      // no old selection
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment0}, getSegmentsAffectedBySelectionChange(text, null, new SelectionRange(0, 1, null)));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment0}, getSegmentsAffectedBySelectionChange(text, null, new SelectionRange(0, 12, null)));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment0}, getSegmentsAffectedBySelectionChange(text, null, new SelectionRange(11, 12, null)));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment1}, getSegmentsAffectedBySelectionChange(text, null, new SelectionRange(12, 13, null)));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment1}, getSegmentsAffectedBySelectionChange(text, null, new SelectionRange(12, 29, null)));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment2}, getSegmentsAffectedBySelectionChange(text, null, new SelectionRange(29, 39, null)));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment2}, getSegmentsAffectedBySelectionChange(text, null, new SelectionRange(38, 39, null)));
      
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment0, segment1}, getSegmentsAffectedBySelectionChange(text, null, new SelectionRange(11, 13, null)));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment1, segment2}, getSegmentsAffectedBySelectionChange(text, null, new SelectionRange(28, 30, null)));
      
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment0, segment1, segment2}, getSegmentsAffectedBySelectionChange(text, null, new SelectionRange(11, 30, null)));
      
      // no new selection
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment0}, getSegmentsAffectedBySelectionChange(text, new SelectionRange(0, 1, null), null));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment0}, getSegmentsAffectedBySelectionChange(text, new SelectionRange(0, 12, null), null));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment0}, getSegmentsAffectedBySelectionChange(text, new SelectionRange(11, 12, null), null));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment1}, getSegmentsAffectedBySelectionChange(text, new SelectionRange(12, 13, null), null));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment1}, getSegmentsAffectedBySelectionChange(text, new SelectionRange(12, 29, null), null));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment2}, getSegmentsAffectedBySelectionChange(text, new SelectionRange(29, 39, null), null));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment2}, getSegmentsAffectedBySelectionChange(text, new SelectionRange(38, 39, null), null));
      
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment0, segment1}, getSegmentsAffectedBySelectionChange(text, new SelectionRange(11, 13, null), null));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment1, segment2}, getSegmentsAffectedBySelectionChange(text, new SelectionRange(28, 30, null), null));
      
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment0, segment1, segment2}, getSegmentsAffectedBySelectionChange(text, new SelectionRange(11, 30, null), null));
      
      // old + new selection with no intersection
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment0}, getSegmentsAffectedBySelectionChange(text, new SelectionRange(0, 6, null), new SelectionRange(6, 8, null)));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment0, segment1}, getSegmentsAffectedBySelectionChange(text, new SelectionRange(0, 6, null), new SelectionRange(13, 16, null)));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment0, segment2}, getSegmentsAffectedBySelectionChange(text, new SelectionRange(0, 6, null), new SelectionRange(32, 34, null)));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment0, segment1, segment2}, getSegmentsAffectedBySelectionChange(text, new SelectionRange(11, 30, null), new SelectionRange(6, 8, null)));
      
      // old + new selection with intersection
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment0, segment2}, getSegmentsAffectedBySelectionChange(text, new SelectionRange(11, 30, null), new SelectionRange(6, 33, null)));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment0, segment2}, getSegmentsAffectedBySelectionChange(text, new SelectionRange(6, 33, null), new SelectionRange(11, 30, null)));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment0}, getSegmentsAffectedBySelectionChange(text, new SelectionRange(6, 10, null), new SelectionRange(2, 12, null)));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment0}, getSegmentsAffectedBySelectionChange(text, new SelectionRange(2, 12, null), new SelectionRange(6, 10, null)));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment0, segment1}, getSegmentsAffectedBySelectionChange(text, new SelectionRange(1, 10, null), new SelectionRange(2, 16, null)));
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[] {segment0, segment1}, getSegmentsAffectedBySelectionChange(text, new SelectionRange(2, 16, null), new SelectionRange(1, 10, null)));
      
      // old + new selection are equal
      TestUtil.assertArrayEqualsIgnoreOrder(new Segment[0], getSegmentsAffectedBySelectionChange(text, new SelectionRange(11, 30, null), new SelectionRange(11, 30, null)));
    } finally {
      text.dispose();        
    }
  }
  
  @SuppressWarnings("unchecked")
  private Segment[] getSegmentsAffectedBySelectionChange(SelectableSegmentedText text, SelectionRange oldSelection, SelectionRange newSelection) throws Throwable {
    return (Segment[])TestUtil.invokeMethod(text, "getSegmentsAffectedBySelectionChange", new Class[] {SelectionRange.class, SelectionRange.class}, new Object[] {oldSelection, newSelection}); 
  }
  
  @Test
  public void testGetLineRange() {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      Segment segment0 = text.addSegment();
      segment0.appendln("0123456789").appendln("0123456789");
      Segment segment1 = text.addSegment();
      segment1.append("0123456789");
      Segment segment2 = text.addSegment();
      segment2.append("0123456789");
      
      assertEquals(new OffsetRange(0, 12), text.getLineRange(0));
      assertEquals(new OffsetRange(12, 24), text.getLineRange(1));
      assertEquals(new OffsetRange(24, 26), text.getLineRange(2));
      assertEquals(new OffsetRange(26, 38), text.getLineRange(3));
      assertEquals(new OffsetRange(38, 48), text.getLineRange(4));
    } finally {
      text.dispose();        
    }
  }
  
  @Test
  public void testGetLine() {
    SelectableSegmentedText text = new SelectableSegmentedText(shell);
    try {
      Segment segment0 = text.addSegment();
      segment0.appendln("0123456789").appendln("abcdefghij");
      Segment segment1 = text.addSegment();
      segment1.append("0123456789");
      
      assertEquals("0123456789\r\n", text.getLine(0));
      assertEquals("abcdefghij\r\n", text.getLine(1));
      assertEquals("\r\n", text.getLine(2));
      assertEquals("0123456789", text.getLine(3));
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
