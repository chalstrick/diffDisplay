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
import com.sap.clap.util.DisposeUtil;

@SuppressWarnings("nls")
public class TestSegmentedText {

  private Display display;
  private Shell shell;
  private CharSize charSize;

  final int caretWidth = 1;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell(display);
    charSize = new CharSize(display);
  }

  @After
  public void cleanUp() {
    DisposeUtil.dispose(shell);
    DisposeUtil.dispose(display);
  }

  @Test
  public void testGetText() {
    final SegmentedText segmentedText = new SegmentedText(shell);
    try {
      assertEquals("", segmentedText.getText());
      final Segment segment1 = segmentedText.addSegment();
      segment1.append("my");
      assertEquals("my", segmentedText.getText());
      segment1.append("Test");
      assertEquals("myTest", segmentedText.getText());
      segment1.appendln("Text");
      assertEquals("myTestText\r\n", segmentedText.getText());
      segment1.append("second line");
      assertEquals("myTestText\r\nsecond line", segmentedText.getText());

      final Segment segment2 = segmentedText.addSegment();
      segment2.append("mySecondSegment");
      assertEquals("myTestText\r\nsecond line\r\nmySecondSegment", segmentedText.getText());

      segment1.appendln();
      segment1.append("third line");
      assertEquals("myTestText\r\nsecond line\r\nthird line\r\nmySecondSegment", segmentedText.getText());

      final Segment segment3 = segmentedText.addSegment();
      segment3.append("myThirdSegment");
      assertEquals("myTestText\r\nsecond line\r\nthird line\r\nmySecondSegment\r\nmyThirdSegment", segmentedText.getText());
    } finally {
      segmentedText.dispose();
    }
  }

  @Test
  public void testAddGetDeleteSegments() {
    final SegmentedText segmentedText = new SegmentedText(shell);
    try {
      assertEquals(0, segmentedText.getSegments().length);
      final Segment segment0 = segmentedText.addSegment();
      assertEquals(1, segmentedText.getSegments().length);
      assertEquals(segment0, segmentedText.getSegments()[0]);
      final Segment segment1 = segmentedText.addSegment();
      assertEquals(2, segmentedText.getSegments().length);
      assertEquals(segment0, segmentedText.getSegments()[0]);
      assertEquals(segment1, segmentedText.getSegments()[1]);

      segmentedText.deleteSegment(0);
      assertEquals(1, segmentedText.getSegments().length);
      assertEquals(segment1, segmentedText.getSegments()[0]);

      final Segment segment2 = segmentedText.addSegment();
      assertEquals(2, segmentedText.getSegments().length);
      assertEquals(segment1, segmentedText.getSegments()[0]);
      assertEquals(segment2, segmentedText.getSegments()[1]);

      assertEquals(segment1, segmentedText.getSegment(0));
      assertEquals(segment2, segmentedText.getSegment(1));

      try {
        segmentedText.getSegment(2);
        fail("expected IndexOutOfBoundsException");
      } catch (final IndexOutOfBoundsException e) {
        // $JL_EXC$ expected exception
      }

      try {
        segmentedText.getSegment(-1);
        fail("expected IndexOutOfBoundsException");
      } catch (final IndexOutOfBoundsException e) {
        // $JL_EXC$ expected exception
      }

      try {
        segmentedText.deleteSegment(2);
        fail("expected IndexOutOfBoundsException");
      } catch (final IndexOutOfBoundsException e) {
        // $JL_EXC$ expected exception
      }

      try {
        segmentedText.deleteSegment(-1);
        fail("expected IndexOutOfBoundsException");
      } catch (final IndexOutOfBoundsException e) {
        // $JL_EXC$ expected exception
      }
    } finally {
      segmentedText.dispose();
    }
  }

  @Test
  public void testHasSegments() {
    final SegmentedText segmentedText = new SegmentedText(shell);
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
    final SegmentedText segmentedText = new SegmentedText(shell);
    try {
      assertNull(segmentedText.getLastSegment());

      final Segment segment0 = segmentedText.addSegment();
      assertEquals(segment0, segmentedText.getLastSegment());

      final Segment segment1 = segmentedText.addSegment();
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
    final SegmentedText segmentedText = new SegmentedText(shell);
    try {
      assertSize(0, 0, segmentedText.computeSize(SWT.DEFAULT, SWT.DEFAULT));

      final Segment segment0 = segmentedText.addSegment();
      assertSize(0, charSize.getHeight(), segmentedText.computeSize(SWT.DEFAULT, SWT.DEFAULT));

      segment0.append("123");
      assertSize(3 * charSize.getWidth() + caretWidth, charSize.getHeight(), segmentedText.computeSize(SWT.DEFAULT, SWT.DEFAULT));

      final Segment segment1 = segmentedText.addSegment();
      segment1.append("12345");
      assertSize(5 * charSize.getWidth() + caretWidth, 2 * charSize.getHeight(), segmentedText.computeSize(SWT.DEFAULT, SWT.DEFAULT));

      final Segment segment2 = segmentedText.addSegment();
      segment2.append("1234");
      assertSize(5 * charSize.getWidth() + caretWidth, 3 * charSize.getHeight(), segmentedText.computeSize(SWT.DEFAULT, SWT.DEFAULT));

      segmentedText.deleteSegment(1);
      assertSize(4 * charSize.getWidth() + caretWidth, 2 * charSize.getHeight(), segmentedText.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    } finally {
      segmentedText.dispose();
    }
  }

  @Test
  public void testGetSegmentArea() throws Throwable {
    final SegmentedText segmentedText = new SegmentedText(shell);
    try {
      final Segment segment0 = segmentedText.addSegment();
      segment0.appendln("01234").append("78");
      final Segment segment1 = segmentedText.addSegment();
      segment1.appendln("0123").append("67");

      assertEquals(new Rectangle(0, 0, 5 * charSize.getWidth(), 2 * charSize.getHeight()), getSegmentArea(segmentedText, segment0));
      assertEquals(new Rectangle(0, 2 * charSize.getHeight(), 4 * charSize.getWidth(), 2 * charSize.getHeight()), getSegmentArea(
        segmentedText, segment1));
    } finally {
      segmentedText.dispose();
    }
  }

  @SuppressWarnings("unchecked")
  private Rectangle getSegmentArea(final SegmentedText text, final Segment segment) throws Throwable {
    return (Rectangle) TestUtil.invokeMethod(text, "getSegmentArea", new Class[] { Segment.class }, new Object[] { segment });
  }

  @Test
  public void testGetPointRelativeToSegmentLocation() throws Throwable {
    final SegmentedText segmentedText = new SegmentedText(shell);
    try {
      final Segment segment0 = segmentedText.addSegment();
      segment0.appendln("01234").append("78");
      final Segment segment1 = segmentedText.addSegment();
      segment1.appendln("abcd").append("gh");

      assertEquals(new Point(-5, -5), getPointRelativeToSegmentLocation(segmentedText, segment0, new Point(-5, -5)));
      assertEquals(new Point(0, 0), getPointRelativeToSegmentLocation(segmentedText, segment0, new Point(0, 0)));
      assertEquals(new Point(3 * charSize.getWidth(), charSize.getHeight()), getPointRelativeToSegmentLocation(segmentedText, segment0,
        new Point(3 * charSize.getWidth(), charSize.getHeight())));
      assertEquals(new Point(3 * charSize.getWidth(), 3 * charSize.getHeight()), getPointRelativeToSegmentLocation(segmentedText, segment0,
        new Point(3 * charSize.getWidth(), 3 * charSize.getHeight())));
      assertEquals(new Point(1000, 1000), getPointRelativeToSegmentLocation(segmentedText, segment0, new Point(1000, 1000)));

      assertEquals(new Point(-5, -5 - 2 * charSize.getHeight()), getPointRelativeToSegmentLocation(segmentedText, segment1, new Point(-5,
          -5)));
      assertEquals(new Point(0, 0 - 2 * charSize.getHeight()), getPointRelativeToSegmentLocation(segmentedText, segment1, new Point(0, 0)));
      assertEquals(new Point(3 * charSize.getWidth(), charSize.getHeight() - 2 * charSize.getHeight()), getPointRelativeToSegmentLocation(
        segmentedText, segment1, new Point(3 * charSize.getWidth(), charSize.getHeight())));
      assertEquals(new Point(3 * charSize.getWidth(), 3 * charSize.getHeight() - 2 * charSize.getHeight()),
        getPointRelativeToSegmentLocation(segmentedText, segment1, new Point(3 * charSize.getWidth(), 3 * charSize.getHeight())));
      assertEquals(new Point(1000, 1000 - 2 * charSize.getHeight()), getPointRelativeToSegmentLocation(segmentedText, segment1, new Point(
          1000, 1000)));
    } finally {
      segmentedText.dispose();
    }
  }

  @SuppressWarnings("unchecked")
  private Point getPointRelativeToSegmentLocation(final SegmentedText text, final Segment segment,
      final Point pointRelativeToSegmentedTextLocation) throws Throwable {
    return (Point) TestUtil.invokeMethod(text, "getPointRelativeToSegmentLocation", new Class[] { Segment.class, Point.class },
      new Object[] { segment, pointRelativeToSegmentedTextLocation });
  }

  private void assertSize(final int expectedWidth, final int expectedHeight, final Point actualSize) {
    final Point expectedSize = new Point(expectedWidth, expectedHeight);
    assertTrue("size: " + actualSize + "; expected size: " + expectedSize, actualSize.equals(expectedSize));
  }

  @Test
  public void testGetRectangleRelativeToSegmentedTextLocation() throws Throwable {
    final SegmentedText text = new SegmentedText(shell);
    try {
      final Segment segment0 = text.addSegment();
      segment0.appendln("012345").append("89");
      final Segment segment1 = text.addSegment();
      segment1.appendln("012345").appendln("89").append("234");
      final Segment segment2 = text.addSegment();
      segment2.appendln("012345").append("89");

      assertEquals(new Rectangle(0, 0, 6 * charSize.getWidth() - 1, 2 * charSize.getHeight() - 1),
        getRectangleRelativeToSegmentedTextLocation(text, segment0, new Rectangle(0, 0, 6 * charSize.getWidth() - 1,
            2 * charSize.getHeight() - 1)));
      assertEquals(new Rectangle(0, 2 * charSize.getHeight(), 6 * charSize.getWidth() - 1, 3 * charSize.getHeight() - 1),
        getRectangleRelativeToSegmentedTextLocation(text, segment1, new Rectangle(0, 0, 6 * charSize.getWidth() - 1,
            3 * charSize.getHeight() - 1)));
      assertEquals(new Rectangle(0, 5 * charSize.getHeight(), 6 * charSize.getWidth() - 1, 2 * charSize.getHeight() - 1),
        getRectangleRelativeToSegmentedTextLocation(text, segment2, new Rectangle(0, 0, 6 * charSize.getWidth() - 1,
            2 * charSize.getHeight() - 1)));

      assertEquals(new Rectangle(0, 5 * charSize.getHeight(), 6 * charSize.getWidth() - 1, 2 * charSize.getHeight()),
        getRectangleRelativeToSegmentedTextLocation(text, segment2, new Rectangle(0, 0, 6 * charSize.getWidth() - 1,
            2 * charSize.getHeight())));

      assertEquals(new Rectangle(10, -5, 6 * charSize.getWidth() - 1 - 10, 2 * charSize.getHeight() - 1 + 5),
        getRectangleRelativeToSegmentedTextLocation(text, segment0, new Rectangle(10, -5, 6 * charSize.getWidth() - 1 - 10,
            2 * charSize.getHeight() - 1 + 5)));

      assertEquals(new Rectangle(0, 2 * charSize.getHeight(), charSize.getWidth() - 1, charSize.getHeight() - 1),
        getRectangleRelativeToSegmentedTextLocation(text, segment1, new Rectangle(0, 0, charSize.getWidth() - 1, charSize.getHeight() - 1)));
    } finally {
      text.dispose();
    }
  }

  @SuppressWarnings("unchecked")
  private Rectangle getRectangleRelativeToSegmentedTextLocation(final SegmentedText text, final Segment segment,
      final Rectangle rectangleRelativeToSegmentLocation) throws Throwable {
    return (Rectangle) TestUtil.invokeMethod(text, "getRectangleRelativeToSegmentedTextLocation", new Class[] { Segment.class,
        Rectangle.class }, new Object[] { segment, rectangleRelativeToSegmentLocation });
  }

  @Test
  public void testGetPointRelativeToSegmentedTextLocation() throws Throwable {
    final SegmentedText text = new SegmentedText(shell);
    try {
      final Segment segment0 = text.addSegment();
      segment0.append("012345");
      final Point point = new Point(10, 3);
      assertEquals(point, getPointRelativeToSegmentedTextLocation(text, segment0, point));

      final Segment segment1 = text.addSegment();
      segment1.append("012345");
      Point expectedPoint = new Point(point.x, point.y + segment0.getBounds().height);
      assertEquals(expectedPoint, getPointRelativeToSegmentedTextLocation(text, segment1, point));

      final Segment segment2 = text.addSegment();
      segment2.append("012345");
      expectedPoint = new Point(point.x, point.y + segment0.getBounds().height + segment1.getBounds().height);
      assertEquals(expectedPoint, getPointRelativeToSegmentedTextLocation(text, segment2, point));
    } finally {
      text.dispose();
    }
  }

  @SuppressWarnings("unchecked")
  private Point getPointRelativeToSegmentedTextLocation(final SegmentedText text, final Segment segment,
      final Point pointRelativeToSegmentLocation) throws Throwable {
    return (Point) TestUtil.invokeMethod(text, "getPointRelativeToSegmentedTextLocation", new Class[] { Segment.class, Point.class },
      new Object[] { segment, pointRelativeToSegmentLocation });
  }

  @Test
  public void testGetBounds() {
    final SegmentedText text = new SegmentedText(shell);
    try {
      assertEquals(new Rectangle(0, 0, 0, 0), text.getBounds());

      final Segment segment0 = text.addSegment();
      segment0.append("012345");
      assertEquals(new Rectangle(0, 0, 6 * charSize.getWidth() + caretWidth, charSize.getHeight()), text.getBounds());

      final Segment segment1 = text.addSegment();
      segment1.append("012345678");
      assertEquals(new Rectangle(0, 0, 9 * charSize.getWidth() + caretWidth, 2 * charSize.getHeight()), text.getBounds());

      final Segment segment2 = text.addSegment();
      segment2.appendln("0123").append("0");
      assertEquals(new Rectangle(0, 0, 9 * charSize.getWidth() + caretWidth, 4 * charSize.getHeight()), text.getBounds());
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

  @Test
  public void testGetLineIndexRelativeToSegmentedText() throws Throwable {
    final SegmentedText text = new SegmentedText(shell);
    try {
      final Segment segment0 = text.addSegment();
      segment0.appendln("012345").appendln("012345").append("012345");

      assertEquals(0, getLineIndexRelativeToSegmentedText(text, segment0, 0));
      assertEquals(1, getLineIndexRelativeToSegmentedText(text, segment0, 1));
      assertEquals(2, getLineIndexRelativeToSegmentedText(text, segment0, 2));

      try {
        getLineIndexRelativeToSegmentedText(text, segment0, 3);
        fail("expected IllegalArgumentException");
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }

      final Segment segment1 = text.addSegment();
      segment1.appendln("012345").appendln("012345").append("012345");
      assertEquals(3, getLineIndexRelativeToSegmentedText(text, segment0, 3));
      assertEquals(2, getLineIndexRelativeToSegmentedText(text, segment1, -1));
      assertEquals(3, getLineIndexRelativeToSegmentedText(text, segment1, 0));
      assertEquals(4, getLineIndexRelativeToSegmentedText(text, segment1, 1));
      assertEquals(5, getLineIndexRelativeToSegmentedText(text, segment1, 2));

      try {
        getLineIndexRelativeToSegmentedText(text, segment1, 3);
        fail("expected IllegalArgumentException");
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }

      final Segment segment2 = text.addSegment();
      segment2.appendln("012345").appendln("012345").append("012345");
      assertEquals(6, getLineIndexRelativeToSegmentedText(text, segment2, 0));
      assertEquals(7, getLineIndexRelativeToSegmentedText(text, segment2, 1));
      assertEquals(8, getLineIndexRelativeToSegmentedText(text, segment2, 2));
    } finally {
      text.dispose();
    }
  }

  @SuppressWarnings("unchecked")
  private int getLineIndexRelativeToSegmentedText(final SegmentedText text, final Segment segment, final int lineIndex) throws Throwable {
    return (Integer) TestUtil.invokeMethod(text, "getLineIndexRelativeToSegmentedText", new Class[] { Segment.class, int.class },
      new Object[] { segment, lineIndex });
  }

  @Test
  public void testGetLineIndexForSegment() throws Throwable {
    final SegmentedText text = new SegmentedText(shell);
    try {
      final Segment segment0 = text.addSegment();
      segment0.appendln("012345").appendln("012345").append("012345");
      final Segment segment1 = text.addSegment();
      segment1.appendln("012345").appendln("012345").append("012345");
      final Segment segment2 = text.addSegment();
      segment2.appendln("012345").appendln("012345").append("012345");

      assertEquals(0, getLineIndex(text, segment0));
      assertEquals(3, getLineIndex(text, segment1));
      assertEquals(6, getLineIndex(text, segment2));
    } finally {
      text.dispose();
    }
  }

  @SuppressWarnings("unchecked")
  private int getLineIndex(final SegmentedText text, final Segment segment) throws Throwable {
    return (Integer) TestUtil.invokeMethod(text, "getLineIndex", new Class[] { Segment.class }, new Object[] { segment });
  }

  @Test
  public void testGetOffsetRelativeToSegment() throws Throwable {
    final SegmentedText text = new SegmentedText(shell);
    try {
      final Segment segment0 = text.addSegment();
      segment0.appendln("012345").append("89");
      final Segment segment1 = text.addSegment();
      segment1.appendln("012345").append("89");
      final Segment segment2 = text.addSegment();
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
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }

      try {
        getOffsetRelativeToSegment(text, segment1, 11);
        fail("expected IllegalArgumentException");
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }

      try {
        getOffsetRelativeToSegment(text, segment2, 35);
        fail("expected IllegalArgumentException");
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
    } finally {
      text.dispose();
    }
  }

  @SuppressWarnings("unchecked")
  private int getOffsetRelativeToSegment(final SegmentedText text, final Segment segment, final int offset) throws Throwable {
    return (Integer) TestUtil.invokeMethod(text, "getOffsetRelativeToSegment", new Class[] { Segment.class, int.class }, new Object[] {
        segment, offset });
  }

  @Test
  public void testGetOffsetForSegment() throws Throwable {
    final SegmentedText text = new SegmentedText(shell);
    try {
      final Segment segment0 = text.addSegment();
      segment0.appendln("012345").append("89");
      final Segment segment1 = text.addSegment();
      segment1.appendln("012345").append("89");
      final Segment segment2 = text.addSegment();
      segment2.appendln("012345").append("89");

      assertEquals(0, getOffset(text, segment0));
      assertEquals(12, getOffset(text, segment1));
      assertEquals(24, getOffset(text, segment2));
    } finally {
      text.dispose();
    }
  }

  @SuppressWarnings("unchecked")
  private int getOffset(final SegmentedText text, final Segment segment) throws Throwable {
    return (Integer) TestUtil.invokeMethod(text, "getOffset", new Class[] { Segment.class }, new Object[] { segment });
  }

  @Test
  public void testFindSegmentByOffset() throws Throwable {
    final SegmentedText text = new SegmentedText(shell);
    try {
      final Segment segment0 = text.addSegment();
      segment0.appendln("012345").append("89");
      final Segment segment1 = text.addSegment();
      segment1.appendln("012345").append("89");
      final Segment segment2 = text.addSegment();
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
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }

      try {
        findSegmentByOffset(text, 35);
        fail("expected IllegalArgumentException");
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
    } finally {
      text.dispose();
    }
  }

  @SuppressWarnings("unchecked")
  private Segment findSegmentByOffset(final SegmentedText text, final int offset) throws Throwable {
    return (Segment) TestUtil.invokeMethod(text, "findSegmentByOffset", new Class[] { int.class }, new Object[] { offset });
  }

  @Test
  public void testGetLineIndexRelativeToSegment() throws Throwable {
    final SegmentedText text = new SegmentedText(shell);
    try {
      final Segment segment0 = text.addSegment();
      segment0.appendln("012345").append("89");
      final Segment segment1 = text.addSegment();
      segment1.appendln("012345").appendln("89").appendln("234");
      final Segment segment2 = text.addSegment();
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
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }

      try {
        getLineIndexRelativeToSegment(text, segment0, 2);
        fail("expected IllegalArgumentException");
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }

      try {
        getLineIndexRelativeToSegment(text, segment1, 1);
        fail("expected IllegalArgumentException");
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }

      try {
        getLineIndexRelativeToSegment(text, segment1, 6);
        fail("expected IllegalArgumentException");
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }

      try {
        getLineIndexRelativeToSegment(text, segment2, 5);
        fail("expected IllegalArgumentException");
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }

      try {
        getLineIndexRelativeToSegment(text, segment2, 8);
        fail("expected IllegalArgumentException");
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
    } finally {
      text.dispose();
    }
  }

  @SuppressWarnings("unchecked")
  private int getLineIndexRelativeToSegment(final SegmentedText text, final Segment segment, final int lineIndex) throws Throwable {
    return (Integer) TestUtil.invokeMethod(text, "getLineIndexRelativeToSegment", new Class[] { Segment.class, int.class }, new Object[] {
        segment, lineIndex });
  }

  @Test
  public void testFindSegmentByLineIndex() throws Throwable {
    final SegmentedText text = new SegmentedText(shell);
    try {
      final Segment segment0 = text.addSegment();
      segment0.appendln("012345").append("89");
      final Segment segment1 = text.addSegment();
      segment1.appendln("012345").appendln("89").appendln("234");
      final Segment segment2 = text.addSegment();
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
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }

      try {
        findSegmentByLineIndex(text, 8);
        fail("expected IllegalArgumentException");
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
    } finally {
      text.dispose();
    }
  }

  @SuppressWarnings("unchecked")
  private Segment findSegmentByLineIndex(final SegmentedText text, final int lineIndex) throws Throwable {
    return (Segment) TestUtil.invokeMethod(text, "findSegmentByLineIndex", new Class[] { int.class }, new Object[] { lineIndex });
  }

  @Test
  public void testGetOffsetRelativeToSegmentedText() throws Throwable {
    final SegmentedText text = new SegmentedText(shell);
    try {
      final Segment segment0 = text.addSegment();
      segment0.appendln("012345").append("89");
      final Segment segment1 = text.addSegment();
      segment1.appendln("012345").appendln("89").append("234");
      final Segment segment2 = text.addSegment();
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
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }

      try {
        getOffsetRelativeToSegmentedText(text, segment2, 11);
        fail("expected IllegalArgumentException");
      } catch (final IllegalArgumentException e) {
        // $JL-EXC$ expected exception
      }
    } finally {
      text.dispose();
    }
  }

  @SuppressWarnings("unchecked")
  private int getOffsetRelativeToSegmentedText(final SegmentedText text, final Segment segment, final int offset) throws Throwable {
    return (Integer) TestUtil.invokeMethod(text, "getOffsetRelativeToSegmentedText", new Class[] { Segment.class, int.class },
      new Object[] { segment, offset });
  }

  @Test
  public void testFindSegment() throws Throwable {
    final SegmentedText text = new SegmentedText(shell);
    try {
      assertEquals(null, findSegment(text, new Point(10, 100)));

      final Segment segment0 = text.addSegment();
      segment0.appendln("012345").append("89");
      final Segment segment1 = text.addSegment();
      segment1.appendln("012345").appendln("89").append("234");
      final Segment segment2 = text.addSegment();
      segment2.appendln("012345").append("89");

      assertEquals(segment0, findSegment(text, new Point(0, -10)));
      assertEquals(segment0, findSegment(text, new Point(0, 0)));
      assertEquals(segment0, findSegment(text, new Point(100000, 0)));
      assertEquals(segment0, findSegment(text, new Point(-10, 0)));
      assertEquals(segment0, findSegment(text, new Point(0, 2 * charSize.getHeight() - 1)));
      assertEquals(segment0, findSegment(text, new Point(6 * charSize.getWidth() - 1, 2 * charSize.getHeight() - 1)));
      assertEquals(segment1, findSegment(text, new Point(0, 2 * charSize.getHeight())));
      assertEquals(segment1, findSegment(text, new Point(0, 5 * charSize.getHeight() - 1)));
      assertEquals(segment2, findSegment(text, new Point(0, 5 * charSize.getHeight())));
      assertEquals(segment2, findSegment(text, new Point(0, 7 * charSize.getHeight() - 1)));
      assertEquals(segment2, findSegment(text, new Point(0, 1000000000)));
    } finally {
      text.dispose();
    }
  }

  @SuppressWarnings("unchecked")
  private Segment findSegment(final SegmentedText text, final Point point) throws Throwable {
    return (Segment) TestUtil.invokeMethod(text, "findSegment", new Class[] { Point.class }, new Object[] { point });
  }

  @Test
  public void testIsSegementHere() throws Throwable {
    final SegmentedText text = new SegmentedText(shell);
    try {
      final Segment segment0 = text.addSegment();
      segment0.appendln("012345").append("89");
      final Segment segment1 = text.addSegment();
      segment1.appendln("012345").appendln("89").append("234");
      final Segment segment2 = text.addSegment();
      segment2.appendln("012345").append("89");

      assertEquals(1, isSegmentHere(text, segment0, new Point(0, -1)));
      assertEquals(0, isSegmentHere(text, segment0, new Point(0, 0)));
      assertEquals(0, isSegmentHere(text, segment0, new Point(100000, 0)));
      assertEquals(0, isSegmentHere(text, segment0, new Point(-10, 0)));
      assertEquals(0, isSegmentHere(text, segment0, new Point(0, 2 * charSize.getHeight() - 1)));
      assertEquals(0, isSegmentHere(text, segment0, new Point(6 * charSize.getWidth() - 1, 2 * charSize.getHeight() - 1)));
      assertEquals(-1, isSegmentHere(text, segment0, new Point(0, 2 * charSize.getHeight())));
      assertEquals(1, isSegmentHere(text, segment1, new Point(0, 2 * charSize.getHeight() - 1)));
      assertEquals(0, isSegmentHere(text, segment1, new Point(0, 2 * charSize.getHeight())));
      assertEquals(0, isSegmentHere(text, segment1, new Point(0, 5 * charSize.getHeight() - 1)));
      assertEquals(-1, isSegmentHere(text, segment1, new Point(0, 5 * charSize.getHeight())));
      assertEquals(1, isSegmentHere(text, segment2, new Point(0, 5 * charSize.getHeight() - 1)));
      assertEquals(0, isSegmentHere(text, segment2, new Point(0, 5 * charSize.getHeight())));
      assertEquals(0, isSegmentHere(text, segment2, new Point(0, 7 * charSize.getHeight() - 1)));
      assertEquals(-1, isSegmentHere(text, segment2, new Point(0, 7 * charSize.getHeight())));
    } finally {
      text.dispose();
    }
  }

  @SuppressWarnings("unchecked")
  private int isSegmentHere(final SegmentedText text, final Segment segment, final Point point) throws Throwable {
    return (Integer) TestUtil.invokeMethod(text, "isSegmentHere", new Class[] { Segment.class, Point.class },
      new Object[] { segment, point });
  }
}
