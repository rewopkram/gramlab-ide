/*
 * Unitex
 *
 * Copyright (C) 2001-2010 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 *
 */

package fr.umlv.unitex.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.text.PlainDocument;

import fr.umlv.unitex.Config;
import fr.umlv.unitex.MyCursors;
import fr.umlv.unitex.editor.EditionTextArea;
import fr.umlv.unitex.editor.FileEditionMenu;
import fr.umlv.unitex.editor.FileManager;
import fr.umlv.unitex.editor.ui.FindDialog;

/*
 * This class is used to display the text
 *  
 */
public class FileEditionTextFrame extends JInternalFrame {

	/** Area where the text is stored and can be edited */
	EditionTextArea text;
	FileManager fileManager;
	File file;
    
	Action saveAction = new AbstractAction("", MyCursors.saveIcon) {
		public void actionPerformed(ActionEvent e) {
			saveFile(file);
		}
	};
	Action saveAsAction = new AbstractAction("") {
		public void actionPerformed(ActionEvent e) {
			saveFile(null);
		}
	};
	Action cutAction = new AbstractAction("", MyCursors.cutIcon) {
		public void actionPerformed(ActionEvent e) {
			text.cut();
		}
	};
	Action copyAction = new AbstractAction("", MyCursors.copyIcon) {
		public void actionPerformed(ActionEvent e) {
			text.copy();
		}
	};
 Action pasteAction = new AbstractAction("", MyCursors.pasteIcon) {
        public void actionPerformed(ActionEvent e) {
            text.paste();
        }
    };
Action findAction = new AbstractAction("", MyCursors.findIcon) {
        public void actionPerformed(ActionEvent e) {
            FindDialog findDialog = new FindDialog(FileEditionTextFrame.this);
            findDialog.setVisible(true);
        }
};
    
	FileEditionTextFrame(File file) {
		super("", true, true, true, true);
		text = new EditionTextArea();
		this.file=file;
		if (file==null) {
			this.setTitle("New File");
		} else {
			this.setTitle(file.getAbsolutePath());
		}
		init();
	}


	private void init() {
		fileManager = new FileManager();
		// main panel
		JPanel top = new JPanel();
		top.setOpaque(true);
		top.setLayout(new BorderLayout());
		top.setBorder(new EmptyBorder(2, 2, 2, 2));

		// vertical scrooling initialisaton
		JScrollPane scroll = new JScrollPane(text);
		scroll
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		// texte panel initialisation
		JPanel middle = new JPanel();
		middle.setOpaque(true);
		middle.setLayout(new BorderLayout());
		middle.setBorder(BorderFactory.createLoweredBevelBorder());
		middle.add(scroll);

		// jmenuBar initialisation
		setJMenuBar(initMenuBar());
		top.add(middle, BorderLayout.CENTER);

		// toolbar initialisation
		JToolBar toolBar = initToolBar();
		top.add(toolBar, BorderLayout.NORTH);

		setContentPane(top);
		pack();
		setBounds(100, 100, 800, 600);

		setVisible(true);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);        
		addInternalFrameListener(new InternalFrameAdapter() {
			
		    
	        public void internalFrameClosing(InternalFrameEvent e) {
	            if (text.isModified()) {
	                Object[] options_on_exit = { "Save", "Don't save" };
	                Object[] normal_options = { "Save", "Don't save", "Cancel" };
	                int n;
	                if (UnitexFrame.closing) {
	                    n = JOptionPane
	                            .showOptionDialog(
	                                    FileEditionTextFrame.this,
	                                    "Text has been modified. Do you want to save it ?",
	                                    "", JOptionPane.YES_NO_CANCEL_OPTION,
	                                    JOptionPane.QUESTION_MESSAGE, null,
	                                    options_on_exit, options_on_exit[0]);
	                } else {
	                    n = JOptionPane
	                            .showOptionDialog(
	                                    FileEditionTextFrame.this,
	                                    "Text has been modified. Do you want to save it ?",
	                                    "", JOptionPane.YES_NO_CANCEL_OPTION,
	                                    JOptionPane.QUESTION_MESSAGE, null,
	                                    normal_options, normal_options[0]);
	                }
	                if (n == JOptionPane.CLOSED_OPTION)
	                    return;
	                if (n == 0) {
	                    saveFile(file);
	                    FileEditionTextFrame.this.setVisible(false);
                        FileEditionTextFrame.this.dispose();
                        return;
	                }
	                if (n != 2) {
	                    FileEditionTextFrame.this.setVisible(false);
	                    FileEditionTextFrame.this.dispose();
	                    return;
	                }
	                FileEditionTextFrame.this.setVisible(true);
	                try {
	                    FileEditionTextFrame.this.setSelected(true);
	                    FileEditionTextFrame.this.setIcon(false);
	                } catch (java.beans.PropertyVetoException e2) {
	                    e2.printStackTrace();
	                }
	                return;
	            }
	            FileEditionTextFrame.this.setVisible(false);
	            UnitexFrame.removeInternalFrame(FileEditionTextFrame.this);
	        }
		    
		});
	}

	
	
	private JMenuBar initMenuBar() {
		JMenuBar jb = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem newFrame = new JMenuItem("New");
		newFrame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fileManager.newFile();
			}
		});
		JMenuItem open = new JMenuItem("Open...");
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FileEditionMenu.openFile();
			}
		});
		JMenuItem close = new JMenuItem("Close");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFile(file);
			}
		});
		JMenuItem saveAs = new JMenuItem("Save As...");
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFile(null);
			}
		});
		fileMenu.add(newFrame);
		fileMenu.add(open);
		fileMenu.add(save);
		fileMenu.add(saveAs);
		fileMenu.add(close);
		JMenu edit = new JMenu("Edit");
		JMenuItem cut = new JMenuItem(cutAction);
		cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				InputEvent.CTRL_MASK));
		JMenuItem copy = new JMenuItem(copyAction);
		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				InputEvent.CTRL_MASK));
		edit.addSeparator();
		JMenuItem paste = new JMenuItem(pasteAction);
		paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
				InputEvent.CTRL_MASK));
		JMenuItem find = new JMenuItem(findAction);
		find.setMnemonic('f');
		find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
				InputEvent.CTRL_MASK));
		edit.add(cut);
		edit.add(copy);
		edit.add(paste);
		edit.addSeparator();
		edit.add(find);
		jb.add(fileMenu);
		jb.add(edit);
		return jb;
	}
	/**
	 * Initialisation of the tool bar
	 */
	private JToolBar initToolBar() {
		JToolBar myToolBar = new JToolBar("file Edition tool bar");
		myToolBar.setMargin(new Insets(0, 0, 0, 0));
		JButton save = new JButton(saveAction);
		initToolBarIcone(save);
		JButton copy = new JButton(copyAction);
		initToolBarIcone(copy);
		JButton cut = new JButton(cutAction);
		initToolBarIcone(cut);
		JButton paste = new JButton(pasteAction);
		initToolBarIcone(paste);
		JButton find = new JButton(findAction);
		initToolBarIcone(find);
		// tooltips
		save.setToolTipText("Save text");
		copy.setToolTipText("Copy");
		cut.setToolTipText("Cut");
		paste.setToolTipText("Paste");
		find.setToolTipText("Find");
		myToolBar.add(save);
		myToolBar.add(copy);
		myToolBar.add(cut);
		myToolBar.add(paste);
		myToolBar.add(find);
		text.setFont(Config.getCurrentTextFont());
		text.setLineWrap(true);
		return myToolBar;
	}

	private void initToolBarIcone(JButton button) {
		button.setMaximumSize(new Dimension(36, 36));
		button.setMinimumSize(new Dimension(36, 36));
		button.setPreferredSize(new Dimension(36, 36));
	}

	/**
	 * Save a file
	 */
	public void saveFile(File f) {
		if (f == null) {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(Config.getCurrentCorpusDir());
			chooser.setMultiSelectionEnabled(false);
			chooser.setDialogType(JFileChooser.SAVE_DIALOG);

			int returnVal = chooser.showSaveDialog(this);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				// we return if the user has clicked on CANCEL
				return;
			}
			f = chooser.getSelectedFile();
		}
		this.file=f;
		setTitle(f.getAbsolutePath());
		fileManager.save(f.getAbsolutePath());
	}

	public void closeText() {
		fileManager.killTimer();
		setVisible(false);
		try {
			setIcon(false);
		} catch (java.beans.PropertyVetoException e2) {
			e2.printStackTrace();
		}
		text.setDocument(new PlainDocument());
		System.gc();
	}

	/**
	 * Returns the text.
	 * 
	 * @return MyTextArea
	 */
	public EditionTextArea getText() {
		return text;
	}

	File getFile() {
		return file;
	}
}