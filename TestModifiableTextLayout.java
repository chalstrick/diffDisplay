package com.sap.clap.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sap.clap.common.text.ModifiableTextLayout;

public class TestModifiableTextLayout {

  private Display display;

  @Before
  public void setUp() {
    display = new Display();
  }

  @Test
  public void testSetText() {
    final ModifiableTextLayout text = new ModifiableTextLayout(display);
    try {
      assertEquals("", text.getText());
      text.setText("myTestText");
      assertEquals("myTestText", text.getText());
      text.setText("anotherTestText");
      assertEquals("anotherTestText", text.getText());
    } finally {
      text.dispose();
    }
  }

  @Test
  public void testClear() {
    final ModifiableTextLayout text = new ModifiableTextLayout(display);
    try {
      text.setText("myTestText");
      assertEquals("myTestText", text.getText());
      text.clear();
      assertEquals("", text.getText());
    } finally {
      text.dispose();
    }
  }

  @Test
  public void testAppend() {
    final ModifiableTextLayout text = new ModifiableTextLayout(display);
    try {
      text.append("my");
      assertEquals("my", text.getText());
      text.append("Test");
      assertEquals("myTest", text.getText());
      text.append("Text");
      assertEquals("myTestText", text.getText());
    } finally {
      text.dispose();
    }
  }

  @Test
  public void testAppendln() {
    final ModifiableTextLayout text = new ModifiableTextLayout(display);
    try {
      text.appendln("my");
      assertEquals("my\r\n", text.getText());
      text.appendln("Test");
      assertEquals("my\r\nTest\r\n", text.getText());
      text.appendln("Text");
      assertEquals("my\r\nTest\r\nText\r\n", text.getText());
    } finally {
      text.dispose();
    }
  }

  @Test
  public void testInsert() {
    final ModifiableTextLayout text = new ModifiableTextLayout(display);
    try {
      // 1. insert in middle of first line
      text.setText("myText");
      assertEquals("myText", text.getText());
      text.insert(0, 2, "Test");
      assertEquals("myTestText", text.getText());

      // 2. insert at beginning of first line
      text.setText("TestText");
      assertEquals("TestText", text.getText());
      text.insert(0, 0, "my");
      assertEquals("myTestText", text.getText());

      // 3. insert at end of first line
      text.setText("myTest");
      assertEquals("myTest", text.getText());
      text.insert(0, 6, "Text");
      assertEquals("myTestText", text.getText());

      // 4. insert at negative char position -> text should be inserted in the beginning
      text.setText("TestText");
      assertEquals("TestText", text.getText());
      text.insert(0, -1, "my");
      assertEquals("myTestText", text.getText());

      // 5. insert at non-existing positive char position -> text should be appended
      text.setText("myTest");
      assertEquals("myTest", text.getText());
      text.insert(0, 100, "Text");
      assertEquals("myTestText", text.getText());

      // 6. insert in middle of second line
      text.setText("first line\r\nmyText");
      assertEquals("first line\r\nmyText", text.getText());
      text.insert(1, 2, "Test");
      assertEquals("first line\r\nmyTestText", text.getText());

      // 7. insert at beginning of second line
      text.setText("first line\r\nTestText");
      assertEquals("first line\r\nTestText", text.getText());
      text.insert(1, 0, "my");
      assertEquals("first line\r\nmyTestText", text.getText());

      // 8. insert at end of second line
      text.setText("first line\r\nmyTest");
      assertEquals("first line\r\nmyTest", text.getText());
      text.insert(1, 6, "Text");
      assertEquals("first line\r\nmyTestText", text.getText());

      // 9. insert into previous line
      text.setText("myText\r\nsecond line");
      assertEquals("myText\r\nsecond line", text.getText());
      text.insert(1, -6, "Test");
      assertEquals("myTestText\r\nsecond line", text.getText());

      // 10. insert into next line
      text.setText("first line\r\nmyText");
      assertEquals("first line\r\nmyText", text.getText());
      text.insert(0, 14, "Test");
      assertEquals("first line\r\nmyTestText", text.getText());
    } finally {
      text.dispose();
    }
  }

  @Test
  public void testInsertln() {
    final ModifiableTextLayout text = new ModifiableTextLayout(display);
    try {
      // 1. insert line the middle
      text.setText("my\r\nText");
      assertEquals("my\r\nText", text.getText());
      text.insertln(1, "Test");
      assertEquals("my\r\nTest\r\nText", text.getText());

      // 2. insert line in the beginning
      text.setText("Test\r\nText");
      assertEquals("Test\r\nText", text.getText());
      text.insertln(0, "my");
      assertEquals("my\r\nTest\r\nText", text.getText());

      // 3. insert line at the end
      text.setText("my\r\nTest");
      assertEquals("my\r\nTest", text.getText());
      text.insertln(2, "Text");
      assertEquals("my\r\nTest\r\nText", text.getText());

      // 4. insert at negative line -> new line should be inserted in the beginning
      text.setText("Test\r\nText");
      assertEquals("Test\r\nText", text.getText());
      text.insertln(-1, "my");
      assertEquals("my\r\nTest\r\nText", text.getText());

      // 5. insert at non-existing line -> new line should be appended
      text.setText("my\r\nTest");
      assertEquals("my\r\nTest", text.getText());
      text.insertln(100, "Text");
      assertEquals("my\r\nTest\r\nText", text.getText());

      // 6. insert empty line the middle
      text.setText("my\r\n\r\nTest\r\nText");
      assertEquals("my\r\n\r\nTest\r\nText", text.getText());
      text.insertln(3);
      assertEquals("my\r\n\r\nTest\r\n\r\nText", text.getText());

      // 7. insert empty line in the beginning
      text.setText("myTestText");
      assertEquals("myTestText", text.getText());
      text.insertln(0);
      assertEquals("\r\nmyTestText", text.getText());

      // 8. insert empty line at the end
      text.setText("myTestText");
      assertEquals("myTestText", text.getText());
      text.insertln(1);
      assertEquals("myTestText\r\n", text.getText());
    } finally {
      text.dispose();
    }
  }

  @Test
  public void testDelete() {
    final ModifiableTextLayout text = new ModifiableTextLayout(display);
    try {
      // 1. delete in middle of first line
      text.setText("myTestText");
      assertEquals("myTestText", text.getText());
      text.delete(0, 2, 4);
      assertEquals("myText", text.getText());

      // 2. delete in the beginning of first line
      text.setText("myTestText");
      assertEquals("myTestText", text.getText());
      text.delete(0, 0, 2);
      assertEquals("TestText", text.getText());

      // 2. delete at the end of first line
      text.setText("myTestText");
      assertEquals("myTestText", text.getText());
      text.delete(0, 6, 4);
      assertEquals("myTest", text.getText());

      // 4. delete from negative char position
      // a)
      text.setText("myTestText");
      assertEquals("myTestText", text.getText());
      text.delete(0, -2, 1);
      assertEquals("myTestText", text.getText());
      // b)
      text.setText("myTestText");
      assertEquals("myTestText", text.getText());
      text.delete(0, -2, 4);
      assertEquals("TestText", text.getText());

      // 5. delete from non-existing positive char position
      text.setText("myTestText");
      assertEquals("myTestText", text.getText());
      text.delete(0, 100, 10);
      assertEquals("myTestText", text.getText());

      // 6. delete in middle of second line
      text.setText("first line\r\nmyTestText");
      assertEquals("first line\r\nmyTestText", text.getText());
      text.delete(1, 2, 4);
      assertEquals("first line\r\nmyText", text.getText());

      // 7. delete at beginning of second line
      text.setText("first line\r\nmyTestText");
      assertEquals("first line\r\nmyTestText", text.getText());
      text.delete(1, 0, 2);
      assertEquals("first line\r\nTestText", text.getText());

      // 8. delete at end of second line
      text.setText("first line\r\nmyTestText");
      assertEquals("first line\r\nmyTestText", text.getText());
      text.delete(1, 6, 4);
      assertEquals("first line\r\nmyTest", text.getText());

      // 9. delete from previous line
      text.setText("myTestText\r\nsecond line");
      assertEquals("myTestText\r\nsecond line", text.getText());
      text.delete(1, -6, 4);
      assertEquals("myTest\r\nsecond line", text.getText());

      // 10. insert into next line
      text.setText("first line\r\nmyTestText");
      assertEquals("first line\r\nmyTestText", text.getText());
      text.delete(0, 14, 4);
      assertEquals("first line\r\nmyText", text.getText());
    } finally {
      text.dispose();
    }
  }

  @Test
  public void testDeleteln() {
    final ModifiableTextLayout text = new ModifiableTextLayout(display);
    try {
      // 1. delete line the middle
      text.setText("my\r\nTest\r\nText");
      assertEquals("my\r\nTest\r\nText", text.getText());
      text.deleteln(1);
      assertEquals("my\r\nText", text.getText());

      // 2. delete line in the beginning
      text.setText("my\r\nTest\r\nText");
      assertEquals("my\r\nTest\r\nText", text.getText());
      text.deleteln(0);
      assertEquals("Test\r\nText", text.getText());

      // 3. delete line at the end
      text.setText("my\r\nTest\r\nText");
      assertEquals("my\r\nTest\r\nText", text.getText());
      text.deleteln(2);
      assertEquals("my\r\nTest", text.getText());

      // 4. delete line at negative line
      text.setText("my\r\nTest\r\nText");
      assertEquals("my\r\nTest\r\nText", text.getText());
      text.deleteln(-1);
      assertEquals("my\r\nTest\r\nText", text.getText());

      // 5. delete line at non-existing line
      text.setText("my\r\nTest\r\nText");
      assertEquals("my\r\nTest\r\nText", text.getText());
      text.deleteln(100);
      assertEquals("my\r\nTest\r\nText", text.getText());
    } finally {
      text.dispose();
    }
  }

  @Test
  public void testSetMaxLines() {
    final ModifiableTextLayout text = new ModifiableTextLayout(display);
    try {
      // limit to 2 lines
      text.setMaxLines(2);

      // 1.
      text.setText("my\r\nTest\r\nText");
      assertEquals("Test\r\nText", text.getText());
      assertEquals(2, text.getLineCount());

      // 2.
      text.clear();
      text.appendln("my");
      text.appendln("Test");
      text.append("Text");
      assertEquals("Test\r\nText", text.getText());
      assertEquals(2, text.getLineCount());

      // 3.
      text.clear();
      text.appendln("my");
      text.append("Text");
      text.insertln(1, "Test");
      assertEquals("Test\r\nText", text.getText());
      assertEquals(2, text.getLineCount());

      // back to unlimited number of lines
      text.setMaxLines(-1);

      // 1.
      text.setText("my\r\nTest\r\nText");
      assertEquals("my\r\nTest\r\nText", text.getText());
      assertEquals(3, text.getLineCount());

      // 2.
      text.clear();
      text.appendln("my");
      text.appendln("Test");
      text.append("Text");
      assertEquals("my\r\nTest\r\nText", text.getText());
      assertEquals(3, text.getLineCount());

      // 3.
      text.clear();
      text.appendln("my");
      text.append("Text");
      text.insertln(1, "Test");
      assertEquals("my\r\nTest\r\nText", text.getText());
      assertEquals(3, text.getLineCount());

      // limit to 2 lines when already more lines exist -> text should be shrinked
      text.setMaxLines(2);
      assertEquals("Test\r\nText", text.getText());
      assertEquals(2, text.getLineCount());
    } finally {
      text.dispose();
    }
  }

  @Test
  public void testGetLineCount() {
    final ModifiableTextLayout text = new ModifiableTextLayout(display);
    try {
      // empty text has already one line
      assertEquals(1, text.getLineCount());

      text.setText("my\r\nTest\r\nTe");
      assertEquals(3, text.getLineCount());

      text.append("xt");
      assertEquals(3, text.getLineCount());

      text.appendln("another line");
      assertEquals(4, text.getLineCount());

      text.insertln(0, "a very first line");
      assertEquals(5, text.getLineCount());

      text.deleteln(0);
      assertEquals(4, text.getLineCount());
    } finally {
      text.dispose();
    }
  }

  @Test
  public void testSetWidth() {
    final ModifiableTextLayout text = new ModifiableTextLayout(display);
    try {
      text.setText("0123456789012345678901234567890123456789");
      assertEquals(240, text.getBounds().width);
      text.setWidth(100);
      assertTrue(text.getBounds().width <= 100);
    } finally {
      text.dispose();
    }
  }

  @Test
  public void testGetBounds() {
    final int charWidth = 6;
    final int lineHeight = 13;

    final ModifiableTextLayout text = new ModifiableTextLayout(display);
    try {
      // empty text has already one line
      assertBounds(0, 0, 0, 1 * lineHeight, text.getBounds());
      text.setText("123");
      assertBounds(0, 0, 3 * charWidth, 1 * lineHeight, text.getBounds());
      assertEquals(text.getBounds(), text.getBounds(0, 3));
      assertBounds(0, 0, 1 * charWidth, 1 * lineHeight, text.getBounds(0, 0));
      assertBounds(0, 0, 2 * charWidth, 1 * lineHeight, text.getBounds(0, 1));
      assertBounds(1 * charWidth, 0, 1 * charWidth, 1 * lineHeight, text.getBounds(1, 1));
      assertBounds(1 * charWidth, 0, 2 * charWidth, 1 * lineHeight, text.getBounds(1, 2));

      text.appendln();
      text.append("56");
      assertBounds(0, 0, 3 * charWidth, 2 * lineHeight, text.getBounds());

      text.append("78");
      assertBounds(0, 0, 4 * charWidth, 2 * lineHeight, text.getBounds());
      assertEquals(text.getBounds(), text.getBounds(0, text.getText().length() - 1));

      text.appendln();
      text.append("12");
      assertBounds(0, 1 * lineHeight, 4 * charWidth, 2 * lineHeight, text.getBounds(7, 11));
    } finally {
      text.dispose();
    }
  }

  private void assertBounds(final int expectedX, final int expectedY, final int expectedWidth, final int expectedHeight,
      final Rectangle actualBounds) {
    final Rectangle expectedBounds = new Rectangle(expectedX, expectedY, expectedWidth, expectedHeight);
    assertTrue("bounds: " + actualBounds + "; expected bounds: " + expectedBounds, actualBounds.equals(expectedBounds));
  }

  @Test
  @Ignore
  public void testSetFont() {
    // TODO
    fail("TODO");
  }

  @Test
  public void testGetEndOfLineOffset() throws Throwable {
    final ModifiableTextLayout text = new ModifiableTextLayout(display);
    try {
      assertEquals(0, getEndOfLineOffset(text, 0));

      text.appendln("0123456789");
      text.appendln("0123456789");
      text.appendln();
      text.append("0123456789");

      assertEquals(12, getEndOfLineOffset(text, 0));
      assertEquals(24, getEndOfLineOffset(text, 1));
      assertEquals(26, getEndOfLineOffset(text, 2));
      assertEquals(36, getEndOfLineOffset(text, 3));
    } finally {
      text.dispose();
    }
  }

  @SuppressWarnings("unchecked")
  private int getEndOfLineOffset(final ModifiableTextLayout text, final int lineIndex) throws Throwable {
    return (Integer) TestUtil.invokeMethod(text, "getEndOfLineOffset", new Class[] { int.class }, new Object[] { lineIndex });
  }

  @Test
  public void testGetLine() {
    final ModifiableTextLayout text = new ModifiableTextLayout(display);
    try {
      text.appendln("0123456789");
      text.appendln("abcdefghij");
      text.appendln();
      text.append("0123456789");

      assertEquals("0123456789\r\n", text.getLine(0));
      assertEquals("abcdefghij\r\n", text.getLine(1));
      assertEquals("\r\n", text.getLine(2));
      assertEquals("0123456789", text.getLine(3));
    } finally {
      text.dispose();
    }
  }

  @After
  public void cleanUp() {
    display.dispose();
  }
}
