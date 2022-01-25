package org.terifan.apps.hexviewer;

import java.awt.BorderLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.io.RandomAccessFile;
import javax.swing.JPanel;
import javax.swing.JScrollBar;


class FileInstance extends JPanel
{
	private RandomAccessFile mFileStream;
	private File mFile;


	public FileInstance(HexViewer aViewer, File aFile)
	{
		super.setLayout(new BorderLayout());

		try
		{
			mFile = aFile;
			mFileStream = new RandomAccessFile(mFile, "r");

			JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);

			HexTextPane textPane = new HexTextPane(mFileStream, scrollBar);

			scrollBar.addAdjustmentListener(new AdjustmentListener()
			{
				@Override
				public void adjustmentValueChanged(AdjustmentEvent aEvent)
				{
					textPane.repaint();
				}
			});

			super.add(textPane, BorderLayout.CENTER);
			super.add(scrollBar, BorderLayout.EAST);

			super.invalidate();
			super.validate();
			super.repaint();
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
	}


	public File getFile()
	{
		return mFile;
	}


	public void close()
	{
		try
		{
			mFileStream.close();
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
}
