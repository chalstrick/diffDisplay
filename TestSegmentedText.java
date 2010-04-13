package com.sap.clap.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sap.clap.common.text.Segment;
import com.sap.clap.common.text.SegmentedText;

public class TestSegmentedText {

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
  public void testGetText() {    
    SegmentedText segmentedText = new SegmentedText(shell);
    try {
      assertEquals("", segmentedText.getText());
      Segment segment1 = segmentedText.addSegment();
      segment1.append("my");
      assertEquals("my", segmentedText.getText());
      segment1.append("Test");
      assertEquals("myTest", segmentedText.getText());
      segment1.appendln("Text");
      assertEquals("myTestText\r\n", segmentedText.getText());
      segment1.append("second line");
      assertEquals("myTestText\r\nsecond line", segmentedText.getText());
      
      Segment segment2 = segmentedText.addSegment();
      segment2.append("mySecondSegment");
      assertEquals("myTestText\r\nsecond line\r\nmySecondSegment", segmentedText.getText());
      
      segment1.appendln();
      segment1.append("third line");
      assertEquals("myTestText\r\nsecond line\r\nthird line\r\nmySecondSegment", segmentedText.getText());
      
      Segment segment3 = segmentedText.addSegment();
      segment3.append("myThirdSegment");
      assertEquals("myTestText\r\nsecond line\r\nthird line\r\nmySecondSegment\r\nmyThirdSegment", segmentedText.getText());
    } finally {
      segmentedText.dispose();
    }
  }
  
  @Test
  public void testAddGetDeleteSegments() {
    SegmentedText segmentedText = new SegmentedText(shell);
    try {
      assertEquals(0, segmentedText.getSegments().length);
      Segment segment0 = segmentedText.addSegment();
      assertEquals(1, segmentedText.getSegments().length);
      assertEquals(segment0, segmentedText.getSegments()[0]);
      Segment segment1 = segmentedText.addSegment();
      assertEquals(2, segmentedText.getSegments().length);   
      assertEquals(segment0, segmentedText.getSegments()[0]);
      assertEquals(segment1, segmentedText.getSegments()[1]);
      
      segmentedText.deleteSegment(0);
      assertEquals(1, segmentedText.getSegments().length);
      assertEquals(segment1, segmentedText.getSegments()[0]);
      
      Segment segment2 = segmentedText.addSegment();
      assertEquals(2, segmentedText.getSegments().length);   
      assertEquals(segment1, segmentedText.getSegments()[0]);
      assertEquals(segment2, segmentedText.getSegments()[1]);
      
      assertEquals(segment1, segmentedText.getSegment(0));
      assertEquals(segment2, segmentedText.getSegment(1));
      
      try {
        segmentedText.getSegment(2);
        fail("expected IndexOutOfBoundsException");
      } catch (IndexOutOfBoundsException e) {
        // $JL_EXC$ expected exception
      }
      
      try {
        segmentedText.getSegment(-1);
        fail("expected IndexOutOfBoundsException");
      } catch (IndexOutOfBoundsException e) {
        // $JL_EXC$ expected exception
      }
      
      try {
        segmentedText.deleteSegment(2);
        fail("expected IndexOutOfBoundsException");
      } catch (IndexOutOfBoundsException e) {
        // $JL_EXC$ expected exception
      }
      
      try {
        segmentedText.deleteSegment(-1);
        fail("expected IndexOutOfBoundsException");
      } catch (IndexOutOfBoundsException e) {
        // $JL_EXC$ expected exception
      }      
    } finally {
      segmentedText.dispose();
    }
  }
  
  @Test
  public void testHasSegments() {
    SegmentedText segmentedText = new SegmentedText(shell);
    try {
      assertFalse(segmentedText.hasSegments());
     
      segmentedText.addSegment();
      assertTrue(segmentedText.hasSegments());
      
      segmentedText.addSegment();
      assertTrue(segmentedText.hasSegments());      
      
      segmentedText.deleteSegment(0);
      assertTrue(segmentedText.hasSegments());
      
      segmentedText.deleteSegment(0);
      assertFalse(segmentedText.hasSegments());
      
      segmentedText.addSegment();
      assertTrue(segmentedText.hasSegments());
    } finally {
      segmentedText.dispose();
    }
  }
  
  @Test
  public void testGetLastSegment() {
    SegmentedText segmentedText = new SegmentedText(shell);
    try {
      assertNull(segmentedText.getLastSegment());
      
      Segment segment0 = segmentedText.addSegment();
      assertEquals(segment0, segmentedText.getLastSegment());
      
      Segment segment1 = segmentedText.addSegment();
      assertEquals(segment1, segmentedText.getLastSegment());
      
      segmentedText.deleteSegment(1);
      assertEquals(segment0, segmentedText.getLastSegment());
      
      segmentedText.deleteSegment(0);
      assertNull(segmentedText.getLastSegment());
    } finally {
      segmentedText.dispose();
    }
  }
  
  @Test
  public void testComputeSize() {
    SegmentedText segmentedText = new SegmentedText(shell);
    try {
      assertSize(0, 0, segmentedText.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      
      Segment segment0 = segmentedText.addSegment();
      assertSize(0, lineHeight, segmentedText.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      
      segment0.append("123");
      assertSize(3 * charWidth, lineHeight, segmentedText.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      
      Segment segment1 = segmentedText.addSegment();
      segment1.append("12345");
      assertSize(5 * charWidth, 2 * lineHeight, segmentedText.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      
      Segment segment2 = segmentedText.addSegment();
      segment2.append("1234");
      assertSize(5 * charWidth, 3 * lineHeight, segmentedText.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      
      segmentedText.deleteSegment(1);
      assertSize(4 * charWidth, 2 * lineHeight, segmentedText.computeSize(SWT.DEFAULT, SWT.DEFAULT));      
    } finally {
      segmentedText.dispose();
    }
  }
  
  @Test
  public void testGetSegmentArea() throws Throwable {
    SegmentedText segmentedText = new SegmentedText(shell);
    try {
      Segment segment0 = segmentedText.addSegment();
      segment0.appendln("01234").append("78");
      Segment segment1 = segmentedText.addSegment();
      segment1.appendln("0123").append("67");
      
      assertEquals(new Rectangle(0, 0, 5 * charWidth, 2 * lineHeight), getSegmentArea(segmentedText, segment0));
      assertEquals(new Rectangle(0, 2 * lineHeight, 4 * charWidth, 2 * lineHeight), getSegmentArea(segmentedText, segment1));
    } finally {
      segmentedText.dispose();
    }
  }
  
  @SuppressWarnings("unchecked")
  private Rectangle getSegmentArea(SegmentedText text, Segment segment) throws Throwable {
    return (Rectangle)TestUtil.invokeMethod(text, "getSegmentArea", new Class[] {Segment.class}, new Object[] {segment}); 
  }
  
  @Test
  public void testGetPointRelativeToSegmentLocation() throws Throwable {
    SegmentedText segmentedText = new SegmentedText(shell);
    try {  
      Segment segment0 = segmentedText.addSegment();
      segment0.appendln("01234").append("78");
      Segment segment1 = segmentedText.addSegment();
      segment1.appendln("abcd").append("gh");
      
      assertEquals(new Point(-5, -5), getPointRelativeToSegmentLocation(segmentedText, segment0, new Point(-5, -5)));
      assertEquals(new Point(0, 0), getPointRelativeToSegmentLocation(segmentedText, segment0, new Point(0, 0)));
      assertEquals(new Point(3 * charWidth, lineHeight), getPointRelativeToSegmentLocation(segmentedText, segment0, new Point(3 * charWidth, lineHeight)));
      assertEquals(new Point(3 * charWidth, 3 * lineHeight), getPointRelativeToSegmentLocation(segmentedText, segment0, new Point(3 * charWidth, 3 * lineHeight)));
      assertEquals(new Point(1000, 1000), getPointRelativeToSegmentLocation(segmentedText, segment0, new Point(1000, 1000)));
      
      assertEquals(new Point(-5, -5 - 2 * lineHeight), getPointRelativeToSegmentLocation(segmentedText, segment1, new Point(-5, -5)));
      assertEquals(new Point(0, 0 - 2 * lineHeight), getPointRelativeToSegmentLocation(segmentedText, segment1, new Point(0, 0)));
      assertEquals(new Point(3 * charWidth, lineHeight - 2 * lineHeight), getPointRelativeToSegmentLocation(segmentedText, segment1, new Point(3 * charWidth, lineHeight)));
      assertEquals(new Point(3 * charWidth, 3 * lineHeight - 2 * lineHeight), getPointRelativeToSegmentLocation(segmentedText, segment1, new Point(3 * charWidth, 3 * lineHeight)));
      assertEquals(new Point(1000, 1000 - 2 * lineHeight), getPointRelativeToSegmentLocation(segmentedText, segment1, new Point(1000, 1000)));
    } finally {
      segmentedText.dispose();
    }
  }
  
  @SuppressWarnings("unchecked")
  private Point getPointRelativeToSegmentLocation(SegmentedText text, Segment segment, Point pointRelativeToSegmentedTextLocation) throws Throwable {
    return (Point)TestUtil.invokeMethod(text, "getPointRelativeToSegmentLocation", new Class[] {Segment.class, Point.class}, new Object[] {segment, pointRelativeToSegmentedTextLocation}); 
  }
  
  private void assertSize(int expectedWidth, int expectedHeight, Point actualSize) {
    Point expectedSize = new Point(expectedWidth, expectedHeight);
    assertTrue("size: " + actualSize + "; expected size: " + expectedSize, actualSize.equals(expectedSize));
  }
  
  @Test
  public void testGetRectangleRelativeToSegmentedTextLocation() throws Throwable {
    SegmentedText text = new SegmentedText(shell);
    try {
      Segment segment0 = text.addSegment();
      segment0.appendln("012345").append("89");
      Segment segment1 = text.addSegment();
      segment1.appendln("012345").appendln("89").append("234");
      Segment segment2 = text.addSegment();
      segment2.appendln("012345").append("89");
      
      assertEquals(new Rectangle(0, 0, 6 * charWidth - 1, 2 * lineHeight - 1), getRectangleRelativeToSegmentedTextLocation(text, segment0, new Rectangle(0, 0, 6 * charWidth - 1, 2 * lineHeight - 1)));
      assertEquals(new Rectangle(0, 2 * lineHeight, 6 * charWidth - 1, 3 * lineHeight - 1), getRectangleRelativeToSegmentedTextLocation(text, segment1, new Rectangle(0, 0, 6 * charWidth - 1, 3 * lineHeight - 1)));
      assertEquals(new Rectangle(0, 5 * lineHeight, 6 * charWidth - 1, 2 * lineHeight - 1), getRectangleRelativeToSegmentedTextLocation(text, segment2, new Rectangle(0, 0, 6 * charWidth - 1, 2 * lineHeight - 1)));
      
      assertEquals(new Rectangle(0, 5 * lineHeight, 6 * charWidth - 1, 2 * lineHeight), getRectangleRelativeToSegmentedTextLocation(text, segment2, new Rectangle(0, 0, 6 * charWidth - 1, 2 * lineHeight)));
      
      assertEquals(new Rectangle(10, -5, 6 * charWidth - 1 - 10, 2 * lineHeight - 1 + 5), getRectangleRelativeToSegmentedTextLocation(text, segment0, new Rectangle(10, -5, 6 * charWidth - 1 - 10, 2 * lineHeight - 1 + 5)));
      
      assertEquals(new Rectangle(0, 2 * lineHeight, charWidth - 1, lineHeight - 1), getRectangleRelativeToSegmentedTextLocation(text, segment1, new Rectangle(0, 0, charWidth - 1, lineHeight - 1)));
    } finally {
      text.dispose();    
    }
  }
  
  @SuppressWarnings("unchecked")
  private Rectangle getRectangleRelativeToSegmentedTextLocation(SegmentedText text, Segment segment, Rectangle rectangleRelativeToSegmentLocation) throws Throwable {
    return (Rectangle)TestUtil.invokeMethod(text, "getRectangleRelativeToSegmentedTextLocation", new Class[] {Segment.class, Rectangle.class}, new Object[] {segment, rectangleRelativeToSegmentLocation}); 
  }
  
  @Test
  public void testGetPointRelativeToSegmentedTextLocation() throws Throwable {
    SegmentedText text = new SegmentedText(shell);
    try {
      Segment segment0 = text.addSegment();
      segment0.append("012345");
      Point point = new Point(10, 3);
      assertEquals(point, getPointRelativeToSegmentedTextLocation(text, segment0, point));
      
      Segment segment1 = text.addSegment();
      segment1.append("012345");
      Point expectedPoint = new Point(point.x, point.y + segment0.getBounds().height); 
      assertEquals(expectedPoint, getPointRelativeToSegmentedTextLocation(text, segment1, point));
      
      Segment segment2 = text.addSegment();
      segment2.append("012345");
      expectedPoint = new Point(point.x, point.y + segment0.getBounds().height + segment1.getBounds().height); 
      assertEquals(expectedPoint, getPointRelativeToSegmentedTextLocation(text, segment2, point));
    } finally {
      text.dispose();    
    }
  }
  
  @SuppressWarnings("unchecked")
  private Point getPointRelativeToSegmentedTextLocation(SegmentedText text, Segment segment, Point pointRelativeToSegmentLocation) throws Throwable {
    return (Point)TestUtil.invokeMethod(text, "getPointRelativeToSegmentedTextLocation", new Class[] {Segment.class, Point.class}, new Object[] {segment, pointRelativeToSegmentLocation});
  }
  
  @Test
  public void testGetBounds() {
    SegmentedText text = new SegmentedText(shell);
    try {
      assertEquals(new Rectangle(0, 0, 0, 0), text.getBounds());
      
      Segment segment0 = text.addSegment();
      segment0.append("012345");
      assertEquals(new Rectangle(0, 0, 6 * charWidth, lineHeight), text.getBounds());
      
      Segment segment1 = text.addSegment();
      segment1.append("012345678");
      assertEquals(new Rectangle(0, 0, 9 * charWidth, 2 * lineHeight), text.getBounds());
      
      Segment segment2 = text.addSegment();
      segment2.appendln("0123").append("0");
      assertEquals(new Rectangle(0, 0, 9 * charWidth, 4 * lineHeight), text.getBounds());
    } finally {
      text.dispose();        
    }
  }
  
  @Test
  @Ignore
  public void testSetFont() {
    // TODO
    fail("TODO");
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
