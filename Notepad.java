import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.undo.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultEditorKit;
class Notepad implements ActionListener
{
	JTextArea jt;
	JScrollPane sc;
	
	JFrame jf;
	JMenuBar menubar;
	JMenu menu[];
	JMenuItem filemenuitem[],editmenuitem[],viewmenuitem[],toolsmenuitem[];
	JFileChooser jfc;
	FileReader fread;
	FileWriter fwrite;
	BufferedWriter bw;
	BufferedReader br;
	String s,filetype;
	UndoManager manager;
	Notepad()
	{
		jt=new JTextArea();
		sc=new JScrollPane(jt);
		jf=new JFrame("Notepad");
		jt.setLineWrap(true);
		jt.setWrapStyleWord(true);
		sc.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jf.add(sc);
		menubar=new JMenuBar();
		menu=new JMenu[5];
		jfc=new JFileChooser();
		manager=new UndoManager();
		jt.getDocument().addUndoableEditListener(new UndoableEditListener() {
          public void undoableEditHappened(UndoableEditEvent e) 
          {
            manager.addEdit(e.getEdit());
          }

        });
		String menuListing[]={"File","Edit","View","Tools","Help"};
		int shortcut[]={KeyEvent.VK_F,KeyEvent.VK_E,KeyEvent.VK_I,KeyEvent.VK_T,KeyEvent.VK_H};
		for(int i=0;i<menu.length;i++)
		{
			menu[i]=new JMenu(menuListing[i]);
			menu[i].setMnemonic(shortcut[i]);
			menu[i].addActionListener(this);
			menubar.add(menu[i]);
		}

		filemenuitem=new JMenuItem[5];
		String filemenuitemListing[]={"New","Open","Save","Save_as","Exit"};
		int filemenuitemShortcut[]={KeyEvent.VK_N,KeyEvent.VK_O,KeyEvent.VK_S,KeyEvent.VK_A,KeyEvent.VK_E};
		for(int i=0;i<filemenuitem.length;i++)
		{
			filemenuitem[i]=new JMenuItem(filemenuitemListing[i]);
			filemenuitem[i].setMnemonic(filemenuitemShortcut[i]);
			filemenuitem[i].addActionListener(this);
			filemenuitem[i].setAccelerator(KeyStroke.getKeyStroke(filemenuitemShortcut[i],ActionEvent.CTRL_MASK));
			menu[0].add(filemenuitem[i]);
		}

		editmenuitem=new JMenuItem[7];
		String editmenuitemListing[]={"Undo","Redo","Select All","Delete","Copy","Paste","Cut"};
		int editmenuitemShortcut[]={KeyEvent.VK_Z,KeyEvent.VK_R,KeyEvent.VK_A,KeyEvent.VK_D,KeyEvent.VK_C,KeyEvent.VK_V,KeyEvent.VK_X};

		
		for(int i=0;i<=3;i++)
		{
			editmenuitem[i]=new JMenuItem(editmenuitemListing[i]);
			
			editmenuitem[i].setMnemonic(editmenuitemShortcut[i]);
			editmenuitem[i].addActionListener(this);
			editmenuitem[i].setAccelerator(KeyStroke.getKeyStroke(editmenuitemShortcut[i],ActionEvent.CTRL_MASK));
			menu[1].add(editmenuitem[i]);
		}
		editmenuitem[4]=new JMenuItem(new DefaultEditorKit.CopyAction());
		//editmenuitem[4]=new JMenuItem(editmenuitemListing[4]);
		editmenuitem[5]=new JMenuItem(new DefaultEditorKit.PasteAction());
		//editmenuitem[5]=new JMenuItem(editmenuitemListing[5]);
		editmenuitem[4].setMnemonic(editmenuitemShortcut[4]);
		editmenuitem[5].setMnemonic(editmenuitemShortcut[5]);
		editmenuitem[6]=new JMenuItem(new DefaultEditorKit.CutAction());
		//editmenuitem[6]=new JMenuItem(editmenuitemListing[6]);
		editmenuitem[6].setMnemonic(editmenuitemShortcut[6]);
		menu[1].add(editmenuitem[4]);
		menu[1].add(editmenuitem[5]);
		menu[1].add(editmenuitem[6]);
		viewmenuitem=new JMenuItem[2];
		String viewmenuitemListing[]={"Statusbar","FullScreen"};
		int viewmenuitemShortcut[]={KeyEvent.VK_S,KeyEvent.VK_F,KeyEvent.VK_F};
		for(int i=0;i<viewmenuitem.length;i++)
		{
			viewmenuitem[i]=new JMenuItem(viewmenuitemListing[i]);
			viewmenuitem[i].setMnemonic(viewmenuitemShortcut[i]);
			viewmenuitem[i].addActionListener(this);
			viewmenuitem[i].setAccelerator(KeyStroke.getKeyStroke(viewmenuitemShortcut[i],ActionEvent.CTRL_MASK));
			menu[2].add(viewmenuitem[i]);
		}

		JMenu fontitem=new JMenu("Font");
		fontitem.addActionListener(this);
		menu[2].add(fontitem);
		ButtonGroup bg=new ButtonGroup();
		JRadioButtonMenuItem fontMenu[]=new JRadioButtonMenuItem[3];
		String fontMenuListing[]={"italic","Bold","sans-serif"};
		for(int i=0;i<fontMenu.length;i++)
		{
			fontMenu[i]=new JRadioButtonMenuItem(fontMenuListing[i]);
			bg.add(fontMenu[i]);
			fontitem.add(fontMenu[i]);
			fontMenu[i].addActionListener(this);
		}


		toolsmenuitem=new JMenuItem[1];
		String toolsmenuitemListing[]={"Document Statistics"};
		int toolsmenuitemShortcut[]={KeyEvent.VK_D};
		for(int i=0;i<toolsmenuitem.length;i++)
		{
			toolsmenuitem[i]=new JMenuItem(toolsmenuitemListing[i]);
			toolsmenuitem[i].setMnemonic(toolsmenuitemShortcut[i]);
			toolsmenuitem[i].addActionListener(this);
			toolsmenuitem[i].setAccelerator(KeyStroke.getKeyStroke(toolsmenuitemShortcut[i],ActionEvent.CTRL_MASK));
			menu[3].add(toolsmenuitem[i]);
		}
		jf.setJMenuBar(menubar);
		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jf.setSize(800,800);
		jf.setVisible(true);
	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("New"))
		{
			new Notepad();
		}

		if(e.getActionCommand().equals("Open"))
		{
			int x=jfc.showOpenDialog(null);
			if(x==JFileChooser.APPROVE_OPTION)
			{
				File f=jfc.getSelectedFile();
				s=f.getAbsolutePath();
				Path p=Paths.get(s);
					try
					{	
						fread=new FileReader(s);
						br=new BufferedReader(fread);
						jt.read(br,true);
						br.close();
					}
					catch(Exception e1)
					{

					}
			}

			if(x==JFileChooser.CANCEL_OPTION)
			{
				System.out.println("cancel");
			}
		}


		if(e.getActionCommand().equals("Save"))
		{
			File f=jfc.getSelectedFile();
			s=f.getAbsolutePath();
			System.out.println(s);
			
				try
				{
					fwrite=new FileWriter(s);
					bw=new BufferedWriter(fwrite);
					jt.write(bw);	
				}
				catch(Exception e3){}
		}

		if(e.getActionCommand().equals("Save_as"))
		{
			int x=jfc.showSaveDialog(null);
			if(x==JFileChooser.APPROVE_OPTION)
			{
				try
				{
					File f3=jfc.getCurrentDirectory();
					String filename=jfc.getSelectedFile().getName();
					s=f3.getAbsolutePath();
					File directory=new File(s);
					File f1=new File(directory,filename);
					if(f1.exists())
					{
						int ans=JOptionPane.showConfirmDialog(null,"Replace Existing file??");
						if(ans==JOptionPane.NO_OPTION)
							return;	
					}
					fwrite=new FileWriter(f1);
					bw=new BufferedWriter(fwrite);
					jt.write(bw);
				}
				catch(Exception e3){}
				
			}

		}

		if(e.getActionCommand().equals("Exit"))
		{
			System.exit(0);
		}

		if(e.getActionCommand().equals("Undo"))
		{
			try
			{
				manager.undo();
			}
			catch(Exception e4){}
		}

		if(e.getActionCommand().equals("Redo"))
		{
			try
			{
				manager.redo();
			}
			catch(Exception e5){}
		}
		if(e.getActionCommand().equals("FullScreen"))
		{
			jf.setSize(1450,950);
		}
		if(e.getActionCommand().equals("Select All"))
		{
			jt.selectAll();
		}

		if(e.getActionCommand().equals("Delete"))
		{
			jt.setText("");
		}

		if(e.getActionCommand().equals("italic"));
		{
			jt.setFont(new Font("Times New Roman",Font.ITALIC,12));
		}
		if(e.getActionCommand().equals("Bold"));
		{
			jt.setFont(new Font("Times New Roman",Font.BOLD,18));
		}
	}

	public static void main(String[] args) 
	{
		new Notepad();
	}

}
