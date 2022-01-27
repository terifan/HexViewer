package org.terifan.apps.hexviewer;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class HexViewer
{
	private JFrame mFrame;
	private JTabbedPane mTabbedPane;


	public HexViewer()
	{
		mFrame = new JFrame();
		mFrame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent aEvent)
			{
				if (mTabbedPane != null)
				{
					for (int i = 0; i < mTabbedPane.getTabCount(); i++)
					{
						try
						{
							HexTextPane tab = (HexTextPane)mTabbedPane.getTabComponentAt(i);
							tab.close();
						}
						catch (Exception e)
						{
						}
					}
				}
			}
		});

		JLabel label = new JLabel("Drag and drop a file...");
		label.setFont(new Font("arial", Font.PLAIN, 12));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(label);
		addDropListener(panel);

		mFrame.add(panel);
		mFrame.setSize(1024, 768);
		mFrame.setLocationRelativeTo(null);
		mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mFrame.setVisible(true);
	}


	private void openTab(File aFile)
	{
		if (mTabbedPane == null)
		{
			mTabbedPane = new JTabbedPane();
			mTabbedPane.addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent aEvent)
				{
					HexTextPane tab = (HexTextPane)mTabbedPane.getSelectedComponent();
					mFrame.setTitle(tab.getFile().getAbsolutePath());
					tab.update();
				}
			});

			mFrame.getContentPane().removeAll();

			mFrame.add(mTabbedPane);
		}

		HexTextPane panel = new HexTextPane(aFile);
		mTabbedPane.addTab(aFile.getName(), panel);
		addDropListener(panel);

		mTabbedPane.invalidate();
		mTabbedPane.validate();
		mTabbedPane.repaint();

		mTabbedPane.setSelectedComponent(panel);
	}


	private void addDropListener(JPanel aPanel)
	{
		aPanel.setDropTarget(new DropTarget(aPanel, new DropTargetAdapter()
		{
			@Override
			public void drop(DropTargetDropEvent aEvent)
			{
				try
				{
					if (aEvent.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
					{
						aEvent.acceptDrop(DnDConstants.ACTION_COPY);

						for (File file : (List<File>)aEvent.getTransferable().getTransferData(DataFlavor.javaFileListFlavor))
						{
							new Thread()
							{
								@Override
								public void run()
								{
									openTab(file);
								}
							}.start();
						}
					}
				}
				catch (Exception | Error e)
				{
					e.printStackTrace(System.out);
				}
			}
		}));
	}


	public static void main(String ... args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			HexViewer app = new HexViewer();

			if (args.length > 0)
			{
				app.openTab(new File(args[0]));
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}
