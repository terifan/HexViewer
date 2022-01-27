package org.terifan.apps.hexviewer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.io.RandomAccessFile;
import javax.swing.JPanel;
import javax.swing.JScrollBar;


class HexTextPaneImpl extends JPanel
{
	private final static long serialVersionUID = 1L;

	private RandomAccessFile mFile;
	private JScrollBar mScrollBar;

	private int mCharsPerLine;
	private int mLineHeight;
	private int mCharWidth;
	private int mRowNumberWidth;
	private int mHexGroupSpacing;
	private int mHexSymbolWidth;
	private Font mFont = new Font("courier new", Font.PLAIN, 12);
	private int mDescent;


	public HexTextPaneImpl(RandomAccessFile aFile, JScrollBar aScrollBar)
	{
		mFile = aFile;
		mScrollBar = aScrollBar;

		FontRenderContext frc = new FontRenderContext(null, true, true);
		LineMetrics lm = mFont.getLineMetrics("Aj", frc);
		int cw = (int)mFont.getStringBounds("E", frc).getWidth() + 1;

		mLineHeight = (int)lm.getHeight() + 4;
		mDescent = (int)lm.getDescent();
		mHexSymbolWidth = 2 * cw + cw / 2;
		mHexGroupSpacing = cw;
		mCharWidth = cw;

		mRowNumberWidth = 100;

		addComponentListener(new ComponentAdapter()
		{
			@Override
			public synchronized void componentResized(ComponentEvent aEvent)
			{
				int n = 8 * Math.max(1, ((getWidth() - mRowNumberWidth) / (8 * (mCharWidth + mHexSymbolWidth) + mHexGroupSpacing)));

				if (n != mCharsPerLine)
				{
					mCharsPerLine = n;

					long len;
					try
					{
						len = mFile.length();
					}
					catch (Exception e)
					{
						return;
					}

					mScrollBar.setMaximum(1 + (int)Math.ceil(len / (double)mCharsPerLine));

					invalidate();
					repaint();
				}

				mScrollBar.setVisibleAmount((int)Math.ceil(getHeight() / (double)mLineHeight));
				mScrollBar.invalidate();
				mScrollBar.validate();
			}
		});
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		int w = getWidth();

		Graphics2D g = (Graphics2D)aGraphics;
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, getHeight());
		g.setFont(mFont);
		g.setColor(Color.BLACK);

		long row = mScrollBar.getValue();

		try
		{
			mFile.seek(row * mCharsPerLine);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}

		int y = mLineHeight - mDescent;
		byte[] buf = new byte[mCharsPerLine];

		for (int i = 0; i < 1 + getHeight() / mLineHeight; i++)
		{
			int len = -1;
			try
			{
				len = mFile.read(buf);
			}
			catch (Exception e)
			{
			}

			if (len <= 0)
			{
				break;
			}

			String rt = Long.toString(row * mCharsPerLine);
			int x = mRowNumberWidth - mCharWidth - (int)g.getFontMetrics().getStringBounds(rt, g).getWidth();
			g.drawString(rt, x, y);

			x = mRowNumberWidth;
			for (int j = 0; j < len; j++)
			{
				g.drawString(String.format("%02X", buf[j]), x, y);
				if ((j % 8) == 7)
				{
					x += mHexGroupSpacing;
				}
				x += mHexSymbolWidth;
			}

			x = mRowNumberWidth + mCharsPerLine * mHexSymbolWidth + mHexGroupSpacing * (mCharsPerLine / 8);
			for (int j = 0; j < len; j++)
			{
				char c = (char)buf[j];
				if (c < 32 || c > 127)
				{
					g.drawString(".", x, y);
				}
				else
				{
					g.drawString(Character.toString(c), x, y);
				}
				x += mCharWidth;
			}

			y += mLineHeight;
			row++;
		}
	}
}
