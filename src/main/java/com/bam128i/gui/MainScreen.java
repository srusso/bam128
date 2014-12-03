package com.bam128i.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import com.bam128i.parser.Parser;
import com.bam128i.parser.ParserTask;
import com.bam128i.utils.MyJLabel;
import com.bam128i.utils.TextFileFilter;

/**
 * 
 * @author Simone Russo
 *
 */
public class MainScreen extends JFrame implements MouseListener, ActionListener {
	private static final long serialVersionUID = 1L;
	private final String VERSION="0.3a";
	private final String APP_NAME="BAM128 Interpreter";
	private final String COMPILATION_TIME;
	
	private Parser parser;
	
	
	private Container center;
	
	private final JButton execute = new JButton("Start");
	private final JButton stop = new JButton("Stop");
	private final JButton showMatrix = new JButton("Show Matrix");
	private final JEditorPane editorPanel = new JEditorPane();
	private final JTextArea outputPanel = new JTextArea();
	private final JTextArea rowCol = new JTextArea(2, 10);
	private final JFileChooser fileSaveChooser, fileOpenChooser;
	
	private final int FILE_NEW=0, FILE_SAVED=1, FILE_MODIFIED=2;
	private int FILESTATE=FILE_NEW;
	private File openedFile;
	
	private WindowListener windowListener = new WindowListener(){

		public void windowActivated(WindowEvent arg0) {
			
		}

		public void windowClosed(WindowEvent arg0) {
			
		}

		public void windowClosing(WindowEvent e) {
			if(FILESTATE==FILE_MODIFIED){
				int x=JOptionPane.YES_OPTION;
	        	
				x=JOptionPane.showConfirmDialog(null, "Discard changes?", "Input", JOptionPane.YES_NO_OPTION);
	        	
	        	if(x==JOptionPane.NO_OPTION){
	        		int returnVal = fileSaveChooser.showOpenDialog(fileSaveChooser);

	                if (returnVal == JFileChooser.APPROVE_OPTION) {
	                	File file = fileSaveChooser.getSelectedFile();
	                	saveFile(file);
	                	dispose();
	                }
	        	} else if(x==JOptionPane.YES_OPTION){
	        		dispose();
	        	}
			} else dispose();
		}

		public void windowDeactivated(WindowEvent arg0) {
			
		}

		public void windowDeiconified(WindowEvent arg0) {
			
		}

		public void windowIconified(WindowEvent arg0) {
			
		}

		public void windowOpened(WindowEvent arg0) {
			
		}
		
	};
	
	public MainScreen(){
		DateFormat format=DateFormat.getDateInstance();
		COMPILATION_TIME = format.format(new Date());
		
		parser = new Parser(this, outputPanel);
		
		setSize(600, 600);
		setTitle(APP_NAME);
        
        //Create a file chooser
        fileSaveChooser = new JFileChooser();
        fileOpenChooser = new JFileChooser();
        
        fileSaveChooser.setApproveButtonText("Save");
        fileSaveChooser.setMultiSelectionEnabled(false);
        
        fileOpenChooser.setFileFilter(new TextFileFilter());
        fileOpenChooser.setMultiSelectionEnabled(false);

		
		FlowLayout f=new FlowLayout();
		f.setAlignment(FlowLayout.CENTER);
		
		editorPanel.setSize(400, 400);
		editorPanel.setAutoscrolls(true);

		KeyListener kl = new KeyListener(){
			public void keyPressed(KeyEvent e) {
				
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
				if(FILESTATE==FILE_MODIFIED)
					return;
				
				FILESTATE = FILE_MODIFIED;
				setTitle(getTitle()+" *");
			}
			
		};
		editorPanel.addKeyListener(kl);
		editorPanel.addCaretListener(new CaretListener(){

			public void caretUpdate(CaretEvent e) {
				int pos = e.getDot();
		        int row = 1, column=0;
				int lastNewline=-1;
				String text = editorPanel.getText().replaceAll("\r", "");
				
				for(int i=0;i<pos;i++){
					if(text.charAt(i)==10){
						row++;
						lastNewline=i;
					}
				}
				
				column=pos-lastNewline;
				
				rowCol.setText("Col: " + column + "\nRow: " + row);
			}
			
		});
		
		center = new Container();
		center.setLayout(new BorderLayout());
		center.add(editorPanel, BorderLayout.CENTER);
		
		Container east = new Container();
		showMatrix.setAlignmentX(Component.CENTER_ALIGNMENT);
		execute.setAlignmentX(Component.CENTER_ALIGNMENT);
		stop.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JPanel jp = new JPanel();
		JPanel jp2 = new JPanel();jp2.setLayout(new FlowLayout());
		rowCol.setText("Col:\nRow:");
		rowCol.setEditable(false);
		rowCol.setFocusable(false);
		rowCol.setAutoscrolls(false);
		rowCol.setBackground(jp2.getBackground());
		rowCol.setDragEnabled(false);
		jp2.add(rowCol);
		
		BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
		BoxLayout bl2 = new BoxLayout(east, BoxLayout.Y_AXIS);east.setLayout(bl2);
		jp.setLayout(bl);
		TitledBorder bor = BorderFactory.createTitledBorder("Commands");bor.setTitlePosition(TitledBorder.TOP);
		jp.setBorder(bor);
		jp.add(execute);
		jp.add(stop);
		jp.add(showMatrix);
		east.add(jp);
		east.add(jp2);
		
		execute.setName("easdhkj");
		stop.setName("asdasduuifd");
		showMatrix.setName("asdoijausdhwh");
		rowCol.setName("awioudos");
		execute.addMouseListener(this);
		stop.addMouseListener(this);
		showMatrix.addMouseListener(this);	
		
		outputPanel.setEditable(false);
		
		outputPanel.setMargin(new Insets(20, 20, 20, 20));
		outputPanel.setBackground(new Color(0,0,0,255));
		outputPanel.setForeground(new Color(255,255,255,255));
		
		JScrollPane editorScroll=new JScrollPane(editorPanel);
		editorScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		JScrollPane outputScroll=new JScrollPane(outputPanel);
		
		Container south = new Container();
		south.setLayout(new BorderLayout());
		south.add(outputScroll);
		south.setMinimumSize(new Dimension(0, 200));
		
		add(editorScroll, BorderLayout.CENTER);
		add(east, BorderLayout.EAST);
		add(south, BorderLayout.SOUTH);
		
		setJMenuBar(buildMenuBar());
		
		addWindowListener(windowListener);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(getRootPane());
		setVisible(true);
		
	}
	
	public JMenuBar buildMenuBar() {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;

        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription("File menu");
        menuBar.add(menu);
        
        menuItem = new JMenuItem("New", KeyEvent.VK_O);
        menuItem.getAccessibleContext().setAccessibleDescription("New File");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        menuItem.setName("newmenu");
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Open File", KeyEvent.VK_O);
        menuItem.getAccessibleContext().setAccessibleDescription("Open File");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        menuItem.setName("openfile");
        menu.add(menuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Save", KeyEvent.VK_S);
        menuItem.getAccessibleContext().setAccessibleDescription("Save File");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        menuItem.setName("savefile");
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Save As..");
        menuItem.getAccessibleContext().setAccessibleDescription("Save File As..");
        menuItem.addActionListener(this);
        menuItem.setName("savefileas");
        menu.add(menuItem);

        //Build help menu
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        menu.getAccessibleContext().setAccessibleDescription("Help Menu");
        menuBar.add(menu);
        
        menuItem = new JMenuItem("BAM Guide", KeyEvent.VK_G);
        menuItem.getAccessibleContext().setAccessibleDescription("About");
        menuItem.addActionListener(this);
        menuItem.setName("bamguide");
        menu.add(menuItem);
        
        menuItem = new JMenuItem("About", KeyEvent.VK_T);
        menuItem.getAccessibleContext().setAccessibleDescription("About");
        menuItem.addActionListener(this);
        menuItem.setName("about");
        menu.add(menuItem);

        return menuBar;
    }
	
	public static void main(String[]args){
		
		new MainScreen();
		
	}

	public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        String name=source.getName();
        
        if(name.equals("newmenu")){
        	int x=JOptionPane.YES_OPTION;
        	if(FILESTATE==FILE_MODIFIED){
        		x=JOptionPane.showConfirmDialog(null, "Discard changes?", "Input", JOptionPane.YES_NO_OPTION);
        	}
        	if(x==JOptionPane.YES_OPTION){
        		FILESTATE=FILE_NEW;
        		setTitle(APP_NAME);
        		openedFile=null;
        		editorPanel.setText("");
        	}
        }
        
        else if(name.equals("about")){
        	JOptionPane.showMessageDialog(null, "Author: Simone Russo\nEmail: simone.russo89@gmail.com\nVersion: "+VERSION +"\nCompiled: " + COMPILATION_TIME, "About", JOptionPane.INFORMATION_MESSAGE);
        } 
        
        else if(name.equals("openfile")){
        	int x=JOptionPane.YES_OPTION;
        	if(FILESTATE==FILE_MODIFIED){
        		x=JOptionPane.showConfirmDialog(null, "Discard changes?", "Input", JOptionPane.YES_NO_OPTION);
        	}
        	if(x==JOptionPane.YES_OPTION){
        		int returnVal = fileOpenChooser.showOpenDialog(this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                	File file = fileOpenChooser.getSelectedFile();
                	loadFile(file);
                }
        	}
        }
        
        else if(name.equals("savefile")){
        	if(openedFile!=null)
        		saveFile(openedFile);
        	else {
        		int returnVal = fileSaveChooser.showOpenDialog(this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                	File file = fileSaveChooser.getSelectedFile();
                	saveFile(file);
                }
        	}
        } 
        
        else if(name.equals("savefileas")){
        	int returnVal = fileSaveChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
            	File file = fileSaveChooser.getSelectedFile();
            	saveFile(file);
            }
        } 
        
        else if(name.equals("bamguide")){
        	showGuide();
        }
    }
	
	private void saveFile(File file){
		if(file==null)
			return;
		
		FileOutputStream fos;

	    try {
	    	if(!file.getName().endsWith(".txt") && !file.getName().endsWith(".bam")){
				file = new File(file.getCanonicalPath()+".bam");
			}
	    	
	    	fos = new FileOutputStream(file);
	    	String code = editorPanel.getText();
	     	int size = code.length();
	      
	        for(int i=0;i<size;i++){
	        	fos.write(code.charAt(i));
	     	}
	      
	     	fos.close();
	     	
	     	openedFile=file;
		    setTitle(APP_NAME + " - " + openedFile.getName());
		    FILESTATE=FILE_SAVED;
	      
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	    
	}
	
	/**
	 * Reads from a file and fills the editorPanel.
	 * @param filename
	 */
	private void loadFile(File file){
		if(file==null)
			return;
		
		try {
			FileInputStream fis = new FileInputStream(file);
			
			editorPanel.read(fis, null);
				
			fis.close();
			
			openedFile=file;
		    setTitle(APP_NAME + " - " + openedFile.getName());
		    FILESTATE=FILE_SAVED;
			
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this, "File \"" + file.getName() + "\" not found.", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error reading the file.", "Error" , JOptionPane.ERROR_MESSAGE);
		}	
	}
	
	private void showGuide(){
		JFrame f=new JFrame("Guide");
    	Container c=new Container();
    	BorderLayout flow=new BorderLayout();
    	c.setLayout(flow);
    	
    	Font font = new Font("Verdana", Font.BOLD, 12);
    	JTextArea ta = new JTextArea("Info");
    	ta.setFont(font);
    	ClassLoader cl = ClassLoader.getSystemClassLoader();
    	try {
    		InputStream is=cl.getResourceAsStream("instr.txt");
    		ta.setText("");
			int ch=is.read();
			while(ch!=-1){
				ta.append(""+(char)ch);
				ch=is.read();
			}
			is.close();
		} catch (FileNotFoundException e1) {
			ta.setText("Couldn't load instruction file.");
		} catch (IOException e1) {
			ta.setText("Couldn't load instruction file.");
		}
    	ta.setEditable(false);
    	ta.setBackground(new Color(220, 220, 220));
    	
    	JScrollPane outputScroll=new JScrollPane(ta);
    	
    	try {
    		Image image = ImageIO.read(cl.getResourceAsStream("bam128instr.jpg"));
    		JLabel label = new JLabel(new ImageIcon(image));
    		c.add(label, BorderLayout.CENTER);
        } catch (IOException ex) {
    	   
        }
    	c.add(outputScroll, BorderLayout.EAST);
    	f.add(c);
    	f.pack();
    	f.setVisible(true);
    	f.setResizable(true);
    	f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	f.setLocationRelativeTo(getRootPane());
	}
	
	private final MouseListener matrixPopup = new MouseListener(){
		public void mouseClicked(MouseEvent e) {
			
		}

		public void mouseEntered(MouseEvent e) {
			MyJLabel label = (MyJLabel)e.getComponent();

			
			frame.setTitle("Matrix [" + label.row + ", " + label.column + "]");

		}

		public void mouseExited(MouseEvent e) {

		}

		public void mousePressed(MouseEvent e) {
			
		}

		public void mouseReleased(MouseEvent e) {
			
		}
		
	};
	
	Container matrixGridContainer;
	JFrame frame;
	Container centermat = new Container();
	JTextArea fromRow = new JTextArea(1, 4);
	JTextArea toRow = new JTextArea(1, 4);
	JTextArea fromCol = new JTextArea(1, 4);
	JTextArea toCol = new JTextArea(1, 4);
	
	private void showMatrix(){
		matrixGridContainer=new Container();
		frame=new JFrame("Matrix");
		matrixGridContainer.setName("lol");
		
		
		//TODO guarda il todo di sotto
		fromRow.setText("0");
		toRow.setText("3");
		fromCol.setText("0");
		toCol.setText("10");
		
		BorderLayout borLayout=new BorderLayout();
    	
    	Container north = new Container();
    	north.setLayout(new FlowLayout());
    	matrixGridContainer.setLayout(borLayout);

    	JButton showNew = new JButton("Show");
    	
    	Container another1 = new Container();
    	another1.setLayout(new BoxLayout(another1, BoxLayout.Y_AXIS));
    	Container another2 = new Container();
    	another2.setLayout(new BoxLayout(another2, BoxLayout.Y_AXIS));
    	
    	another1.add(new JLabel("From Row:"));
    	another1.add(fromRow);
    	another1.add(new JLabel("To Row:"));
    	another1.add(toRow);
    	
    	another2.add(new JLabel("From Column:"));
    	another2.add(fromCol);
    	another2.add(new JLabel("To Column:"));
    	another2.add(toCol);
    	
    	north.add(another1);
    	north.add(another2);
    	north.add(showNew);
    	
    	
    	JLabel empty = new JLabel("Select rows and colums to show");
    	empty.setAlignmentY(Component.CENTER_ALIGNMENT);
    	empty.setAlignmentX(Component.CENTER_ALIGNMENT);
    	centermat = new Container();
    	centermat.setLayout(new FlowLayout());
    	centermat.add(empty);
    	
    	
    	JScrollPane outputScroll=new JScrollPane(centermat);
    	
    	showNew.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				try{
					int startRow=0, endRow=0, startCol=0, endCol=0;
					
					startRow = Integer.parseInt(fromRow.getText().replaceAll(" ", ""));
					endRow   = Integer.parseInt(toRow.getText().replaceAll(" ", ""));
					startCol = Integer.parseInt(fromCol.getText().replaceAll(" ", ""));
					endCol   = Integer.parseInt(toCol.getText().replaceAll(" ", ""));
					
					
					if(startRow<0 || startCol <0 || startCol>127 || startRow>127 ||
							startRow>endRow || startCol>=endCol || endRow<0 || endCol <0 || endCol>127 || endRow>127){
						JOptionPane.showMessageDialog(centermat, "Insert numbers in the interval [0, 127]. And remember that start value must be < end value!", "Input Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					addMatrix(startRow, endRow, startCol, endCol);
					centermat.validate();
				} catch(NumberFormatException e){
					JOptionPane.showMessageDialog(centermat, "Numbers only!", "Input Error", JOptionPane.ERROR_MESSAGE);
				}
			}
    		
    	});
    	
    	matrixGridContainer.add(north, BorderLayout.NORTH);
    	matrixGridContainer.add(outputScroll, BorderLayout.CENTER);
    	
    	frame.add(matrixGridContainer);
    	frame.setSize(600, 600);
    	frame.setVisible(true);
    	frame.setResizable(true);
    	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	frame.addWindowListener(new WindowListener(){

			public void windowActivated(WindowEvent arg0) {
				
			}

			public void windowClosed(WindowEvent arg0) {
				centermat.removeAll();
			}
			public void windowClosing(WindowEvent arg0) {
				
			}

			public void windowDeactivated(WindowEvent arg0) {
				
			}

			public void windowDeiconified(WindowEvent arg0) {
				
			}

			public void windowIconified(WindowEvent arg0) {
				
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
				
			}
    		
    	});
    	frame.setLocationRelativeTo(getRootPane());
    	
    	
    	//TODO temporaneo, probabilmente da eliminare, MA ANCHE NO
    	addMatrix(0, 3, 0, 10);
		centermat.validate();
	}
	
	private void addMatrix(int startRow, int endRow, int startCol, int endCol){
		centermat.removeAll();
		int [][]matrix = parser.getMatrix();
		final Border listPanelBorder=BorderFactory.createLoweredBevelBorder();
    	MyJLabel label;
    	
    	GridLayout gridLayout=new GridLayout(endRow-startRow+1, endCol-startCol+1);
    	centermat.setLayout(gridLayout);
    	
    	if(centermat.getComponentCount()==0){
    		for(int i=startRow;i<=endRow;i++)
    			for(int j=startCol;j<=endCol;j++){
    				label = new MyJLabel(""+matrix[i][j], i, j);
    				label.setBorder(listPanelBorder);
    				label.addMouseListener(matrixPopup);
    				centermat.add(label);
    			}
    	
    	} else {
    		for(int i=startRow;i<endRow;i++)
    			for(int j=startCol;j<endCol;j++){
    				label = (MyJLabel) centermat.getComponent(i*matrix.length+j);
    				label.setText(""+matrix[i][j]);
    			}
    	}
	}
	
	private Thread parserThread;
	private ParserTask parserTask;

	public void mouseClicked(MouseEvent e) {
		Component comp = e.getComponent();
		
		if(comp.getName()==null)
			return;
		
		if(comp.getName().equals(execute.getName())){
			if(parserTask!=null){
				parserThread.interrupt();
				parserTask.stopParsing();
			}
			
			parserTask   = new ParserTask(parser, editorPanel.getText().toCharArray());
			parserThread = new Thread(parserTask);
			parserThread.start();
		} else if(comp.getName().equals(stop.getName())){
			if(parserTask!=null){
				parserThread.interrupt();
				parserTask.stopParsing();
			}
		} else if(comp.getName().equals(this.showMatrix.getName())){
			showMatrix();
		}
		
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		
	}

	public void mouseReleased(MouseEvent e) {
		
	}
}