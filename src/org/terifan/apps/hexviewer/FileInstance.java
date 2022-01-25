package org.terifan.apps.hexviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;


class FileInstance extends JPanel
{
	private RandomAccessFile mFileStream;
	private File mFile;
	private JPanel mStatusBar;


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

			mStatusBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			mStatusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(172, 172, 172)));

			update();

			super.add(textPane, BorderLayout.CENTER);
			super.add(scrollBar, BorderLayout.EAST);
			super.add(mStatusBar, BorderLayout.SOUTH);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
	}


	public void update()
	{
		long created = 0;
		try
		{
			created = Files.readAttributes(mFile.toPath(), BasicFileAttributes.class).creationTime().toMillis();
		}
		catch (IOException e)
		{
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		mStatusBar.removeAll();
		mStatusBar.add(new JLabel("Length: "));
		mStatusBar.add(new JLabel("" + mFile.length()));
		mStatusBar.add(new JLabel("        "));
		mStatusBar.add(new JLabel("Created: "));
		mStatusBar.add(new JLabel(sdf.format(created)));
		mStatusBar.add(new JLabel("        "));
		mStatusBar.add(new JLabel("Modified: "));
		mStatusBar.add(new JLabel(sdf.format(mFile.lastModified())));
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
