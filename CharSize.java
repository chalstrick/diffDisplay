package com.sap.clap.test;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Display;

import com.sap.clap.util.DisposeUtil;

@SuppressWarnings("nls")
public class CharSize {

  private final Display display;
  private final Font font;

  private final int charWidth;
  private final int charHeight;

  public CharSize(final Display display) {
    this(display, null);
  }

  public CharSize(final Display display, final Font font) {
    this.display = display;
    this.font = font;

    final TextLayout textLayout = createTextLayout(display, font, "X");
    charWidth = textLayout.getBounds().width;
    charHeight = textLayout.getBounds().height;
    DisposeUtil.dispose(textLayout);
  }

  private TextLayout createTextLayout(final Display display, final Font font, final String text) {
    final TextLayout textLayout = new TextLayout(display);
    if (font != null) {
      textLayout.setFont(font);
    }
    textLayout.setText(text);
    return textLayout;
  }

  public int getWidth() {
    return charWidth;
  }

  public int getHeight() {
    return charHeight;
  }

  public int getWidth(final String text) {
    final TextLayout textLayout = createTextLayout(display, font, text);
    final int textWidth = textLayout.getBounds().width;
    DisposeUtil.dispose(textLayout);
    return textWidth;
  }
}
