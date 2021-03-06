/**
Marvin Project <2007-2013>
http://www.marvinproject.org

License information:
http://marvinproject.sourceforge.net/en/license.html

Discussion group:
https://groups.google.com/forum/#!forum/marvin-project
*/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.TitledBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import marvin.gui.MarvinFilterWindow;
import marvin.gui.MarvinImagePanel;
import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;
import marvin.performance.MarvinPerformanceMeter;
import marvin.plugin.MarvinImagePlugin;
import marvin.util.MarvinErrorHandler;
import marvin.util.MarvinFileChooser;
import marvin.util.MarvinPluginLoader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Marvin Image Editor Application.
 * @version 1.0 29/01/09 
 * @author Fabio Andrijaukas
 * @author Gabriel Ambr�sio Archanjo
 * @author Danilo Rosetto Mu�oz
 */
public class MarvinEditor extends JFrame {
	// Constants
	private final static String ICONS_PLUGINS_PATH = "./res/icons/plugins/";
	private final static String ICONS_APPLICATION_PATH = "./res/icons/application/";
	
	private final static String INITIAL_IMAGE = "./res/images/tucano.jpg";
	private final static String PATH_FILTERS = "./marvin/plugins/image";
	private final static String PATH_ANALYSES = "./lib/plugins/analyses";
	
	private final static String MARVIN_EDITOR_VERSION = "1.3.1";
	
	// GUI's components
	private JPanel panelMain;
	private TitledBorder titlep;
	private ImageIcon imageIcon;
	//private JLabel lblImage;
	
	private MarvinImagePanel 	imagePanel;
	private JScrollPane			imageScrollPane;

	JMenuBar menuBar;
	
	// Menu
	JMenu		jmnFile,
				jmnHelp,
				jmnEdit,
				jmnFilters,
				jmnAnalyses,
				jmnHistory;
				
	// MenuItens
	JMenuItem	jmiNew,
				jmiOpen,
				jmiSave,
				jmiSaveAs,
				jmiExit,
				jmiShowHistory,
				jmiReset,
				jmiJavadoc,
				jmiAbout;

	// Access to the files and filters
	private File imgway;

	// Reflection receiving class
    private Class<?> myClass;
	
	private MarvinImage image, originalImage;				// Control and image manipulating class
	private String currentFilePath;
	private MarvinPerformanceMeter performanceMeter;		// Marvin plug-in progress	
	private MarvinEditor marvin;									// Pointer to itself

	private NewFileDialog	newFileDialog;
	private MenuHandler menuHandler;

	/**
	 * Constructs Marvin main class with arguments
	 * 
	 * @param a_args Arguments
	 * <p>
	 * NOTE: Marvin supports 2 arguments at initialization: 
	 * {@code windowBounds(x, y, width, height)} and {@code windowState(state)}.
	 * <br>
	 * These arguments are useful when it's working with 2 screens. 
	 * Assume second screen is set like "LEFT" and it has 1024x768 resolution. 
	 * It's possible opening Marvin directly to the second screen: 
	 * <br><br>
	 * <strong>java -cp ".";"./bin" kernel.Marvin windowBounds(-1024,0,800,600) windowState(max)</strong>
	 * <br><br>
	 * This way could help in working source code and IDE with the main screen and testing Marvin in other one.
	 */
	public MarvinEditor(String a_args[]) {
		super("MARVIN");
		loadMenuBar(); 		// Menu bars
		
		panelMain = new JPanel();
		titlep = BorderFactory.createTitledBorder("Image");
		panelMain.setBorder(titlep);
		panelMain.setSize(600, 600);
		panelMain.setLayout(new BorderLayout());
		add(panelMain);

		imagePanel = new MarvinImagePanel();
		imagePanel.enableHistory();		
		imagePanel.setImage(image);
		imageScrollPane = new JScrollPane(imagePanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		panelMain.add(imageScrollPane);
		
		// loads the File Chooser class.
		new JFileChooser();
		
		//performanceMeter = new MarvinPerformanceMeter();
		//add(performanceMeter.getPanel(), BorderLayout.SOUTH);

		newImage(500,500);
		treatArgs(a_args);
		marvin = this;
		
		setSize(800,600); 	// Default size
		setVisible(true);
	}

	private void loadPluginMenu(String a_tag, JMenuItem a_menuItem, ActionListener a_listener){
		try{
			
			File file = new File("./conf/conf.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName(a_tag);
			nodeLst = ((Element)nodeLst.item(0)).getElementsByTagName("item");
			NodeList l_listEntries;
			JMenu l_currentMenu;
			JMenuItem l_currentMenuItem;
			String l_icon;
			
			for(int s = 0; s < nodeLst.getLength(); s++){				
			    Node fstNode = nodeLst.item(s);
			    
			    if (fstNode.getNodeType() == Node.ELEMENT_NODE){
			    	Element fstElmnt = (Element) fstNode;
			    	
			    	l_currentMenu = new JMenu(fstElmnt.getAttribute("name"));
			    	l_currentMenu.add(new JSeparator(JSeparator.VERTICAL));
			    	a_menuItem.add(l_currentMenu);
			    	
			    	l_listEntries = fstElmnt.getElementsByTagName("entry");
			    	
			    	for(int w=0; w<l_listEntries.getLength(); w++){
			    		Node l_node = l_listEntries.item(w);
			    		Element l_element = (Element)l_node;
				    	
			    		l_currentMenuItem = new JMenuItem(l_element.getAttribute("name"));
			    		l_currentMenuItem.addActionListener(a_listener);
			    		l_currentMenuItem.setActionCommand(l_element.getAttribute("package"));
			    		l_currentMenu.add(l_currentMenuItem);
			    		
			    		
			    		l_icon = l_element.getAttribute("icon");
			    		if(!l_icon.equals("")){
			    			l_currentMenuItem.setIcon(new ImageIcon(ICONS_PLUGINS_PATH+l_icon));
			    			l_icon = "";
			    		}
			    	 }
			    }
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}	
	/**
	 * Gets the current image.
	 * @return current {@code MarvinImage}
	 * 
	 * @see MarvinImage
	 */
	public MarvinImage getCurrentImage(){
		return image;
	}
	
	/**
	 * Gets the {@code MarvinPerformanceMeter}
	 * @return the {@code MarvinPerformanceMeter} instance
	 * 
	 * @see MarvinPerformanceMeter
	 */
	public MarvinPerformanceMeter getPerformanceMeter(){
		return performanceMeter;
	}
	
	/**
	 * Treat the arguments
	 * @param a_args Arguments
	 */
	private void treatArgs(String a_args[]){
		for(int i=0; i<a_args.length; i++){
			// Window Position, format: windowBounds(x,y,width,height)
			if(a_args[i].contains("windowBounds")){
				int	l_x=-1,
						l_y=-1,
						l_width=-1,
						l_height=-1;

				int l_pos;

				try{
					l_pos = a_args[i].indexOf(',');
					l_x = Integer.parseInt(a_args[i].substring(a_args[i].lastIndexOf('(')+1, l_pos));
					l_pos = a_args[i].indexOf(',', l_pos+1);
					l_width = Integer.parseInt(a_args[i].substring(l_pos+1, a_args[i].indexOf(',', l_pos+1)));
					l_pos = a_args[i].indexOf(',', l_pos+1);
					l_height = Integer.parseInt(a_args[i].substring(l_pos+1, a_args[i].lastIndexOf(')')));
				}
				catch(Exception e){
					System.out.println("[ERROR]:invalid parameter:"+a_args[i]);
					e.printStackTrace();
				}

				if(l_x != -1){
					setBounds(l_x,l_y,l_width,l_height);
				}
			}
			
			// Window state, format: windowState(max) || ...
			if(a_args[i].contains("windowState")){
				if(a_args[i].substring(a_args[i].indexOf('(')+1, a_args[i].indexOf(')')).equals("max")){
					setExtendedState(JFrame.MAXIMIZED_BOTH);
				}
			}
		}
	}

	/**
	 * Creates the menubar
	 */
	private void loadMenuBar() {
		menuBar = new JMenuBar();
		menuHandler = new MenuHandler();

		jmnFile = new JMenu("File");
		jmnFile.setMnemonic('F');
		menuBar.add(jmnFile);

		jmnEdit = new JMenu("Edit");
		jmnEdit.setMnemonic('E');
		menuBar.add(jmnEdit);
		
		// Load filter
		jmnFilters = new JMenu("Filters");
		jmnFilters.setMnemonic('f');
		
		jmnAnalyses = new JMenu("Analyse");
		jmnAnalyses.setMnemonic('n');

		loadPluginMenu("filters", jmnFilters, new FilterMenuItemHandler());
		loadPluginMenu("analyses", jmnAnalyses, new FilterMenuItemHandler());
		
		menuBar.add(jmnFilters);
		menuBar.add(jmnAnalyses);
		
		jmnHistory = new JMenu("History");
		jmnHistory.setMnemonic('H');

		jmiShowHistory = new JMenuItem("Show history");
		jmiShowHistory.setIcon(new ImageIcon(ICONS_APPLICATION_PATH+"history.png"));
		jmiShowHistory.setMnemonic('S');
		jmiShowHistory.addActionListener(menuHandler);
		jmnHistory.add(jmiShowHistory);

		
		// Menu Item New
		jmiNew = new JMenuItem("New");
		jmiNew.setIcon(new ImageIcon(ICONS_APPLICATION_PATH+"newFile.png"));
		jmiNew.setMnemonic('N');
		jmiNew.addActionListener(menuHandler);
		jmnFile.add(jmiNew);
		
		// Menu Item Open
		jmiOpen = new JMenuItem("Open");
		jmiOpen.setIcon(new ImageIcon(ICONS_APPLICATION_PATH+"open.png"));
		jmiOpen.setMnemonic('O');
		jmiOpen.addActionListener(menuHandler);
		jmnFile.add(jmiOpen);
		
		// Menu Item Save
		jmiSave = new JMenuItem("Save");
		jmiSave.setIcon(new ImageIcon(ICONS_APPLICATION_PATH+"save.png"));
		jmiSave.setMnemonic('S');
		jmiSave.addActionListener(menuHandler);
		jmnFile.add(jmiSave);
		
		// Menu Item Save As
		jmiSaveAs = new JMenuItem("Save As...");
		jmiSaveAs.setIcon(new ImageIcon(ICONS_APPLICATION_PATH+"saveas.png"));
		jmiSaveAs.setMnemonic('a');
		jmiSaveAs.addActionListener(menuHandler);
		jmnFile.add(jmiSaveAs);
		
		// Menu Item Close
		jmiExit= new JMenuItem("Exit");
		jmiExit.setIcon(new ImageIcon(ICONS_APPLICATION_PATH+"exit.png"));
		jmiExit.setMnemonic('E');
		jmiExit.addActionListener(menuHandler);
		jmnFile.add(jmiExit);

		// Menu Item Reset
		jmiReset = new JMenuItem("Reset");
		jmiReset.setIcon(new ImageIcon(ICONS_APPLICATION_PATH+"reset.png"));
		jmiReset.setMnemonic('R');
		jmiReset.addActionListener(menuHandler);
		
		// Menu Item Edit
		jmnEdit.add(jmiReset);

		// Menu Item Help
		jmnHelp = new JMenu("Help");
		jmnHelp.setMnemonic('H');

		// Menu Item About
		jmiAbout = new JMenuItem("About");
		jmiAbout.setIcon(new ImageIcon(ICONS_APPLICATION_PATH+"about.png"));
		jmiAbout.setMnemonic('A');
		jmiAbout.addActionListener(menuHandler);
		
		menuBar.add(jmnHistory);
		jmnHelp.add(jmiAbout);
		menuBar.add(jmnHelp);
		
		/* TODO JAVADOC IS NOT FINISHED YET
		 * 	jmnHelp.add(jmiJavadoc);
		*/
		
		add(menuBar, BorderLayout.NORTH);
	}
	
	private void updateImagePanel(){
		// TODO: Find a better way to do this
		panelMain.remove(imageScrollPane);
		imageScrollPane = new JScrollPane(imagePanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		panelMain.add(imageScrollPane);
		validate();
	}
	
	
	/**
	 * Open images from the file system to apply the filters
	 * @param f File path
	 */
	private void loadImage(String f) {
		// Clear the list

		// Open the file dialog
		boolean imgOk = false;
		while (imgOk != true) {

			if (f == null) {
				try {
					f = MarvinFileChooser.select(this, true, MarvinFileChooser.OPEN_DIALOG, MarvinFileChooser.AllSupportedImages);
				} catch (Exception e) {
					MarvinErrorHandler.handleDialog(MarvinErrorHandler.TYPE.ERROR_FILE_CHOOSE, e);
					return;
				}
			}

			if(f == null) return;

			currentFilePath = f;
			
			// Load to an ImageIcon to show the image in a label
			imageIcon = new ImageIcon(f);

			originalImage = MarvinImageIO.loadImage(f);
			image = originalImage.clone();
			
			// History
			imagePanel.getHistory().clear();
			imagePanel.getHistory().addEntry("Initial Image", (MarvinImage) image.clone(), null);			
			imagePanel.setImage(image);
			
			updateImagePanel();
			
			
			
			
			if(image != null){
				imgOk = true;
			}
			else{
				imgOk = false;
			}
		}
	}

	public void newImage(int width, int height){
		currentFilePath = null;
		
		originalImage = new MarvinImage(width, height);
		originalImage.fillRect(0, 0, width, height, Color.white);
		image = originalImage.clone();
		
		// History
		imagePanel.getHistory().clear();
		imagePanel.getHistory().addEntry("Initial Image", (MarvinImage) image.clone(), null);			
		imagePanel.setImage(image);
		
		updateImagePanel();
	}
	
	private void saveImage(){
		image = imagePanel.getImage();
		MarvinImageIO.saveImage(image, currentFilePath);
		System.out.println("save in:"+currentFilePath);
	}
	
	private void saveAsImage(){
		try {
			
			String arq;
			
			if(currentFilePath != null){
				String directory = currentFilePath.substring(0, currentFilePath.lastIndexOf("\\"));
				arq = MarvinFileChooser.select(directory, marvin, false, MarvinFileChooser.SAVE_DIALOG, MarvinFileChooser.AllSupportedImages);
			}
			else{
				arq = MarvinFileChooser.select(marvin, false, MarvinFileChooser.SAVE_DIALOG, MarvinFileChooser.AllSupportedImages);
			}
			
			
			
			if(arq != null){
				
				image = imagePanel.getImage();
				MarvinImageIO.saveImage(image, arq);

				/*
				JOptionPane.showMessageDialog(null, "File "
						+ arq + " saved successfully",
						"Marvin", JOptionPane.INFORMATION_MESSAGE);
				*/
			}
		} catch (Exception e3) {
			MarvinErrorHandler.handleDialog(MarvinErrorHandler.TYPE.ERROR_FILE_SAVE, e3);
		}
	}
	
	/**
	 * Receive filter menu action events
	 * @version 1.0 02/13/08 
	 */
	private class FilterMenuItemHandler implements ActionListener {
		public void actionPerformed(ActionEvent e){
			String l_path = ((JMenuItem)e.getSource()).getActionCommand();
			l_path = l_path.substring(l_path.lastIndexOf("\\")+1);
			
			// Load Plugin
			MarvinImagePlugin l_plugin = MarvinPluginLoader.loadImagePlugin(l_path);
			l_plugin.setImagePanel(imagePanel);			
			MarvinFilterWindow filterWindow = new MarvinFilterWindow("Plugin", 400, 340, imagePanel, l_plugin);
			filterWindow.setVisible(true);
		}
	}

	/**
	 * Receive menu action events
	 */
	private class MenuHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){
			
			if(e.getSource() == jmiNew){
				newFileDialog = new NewFileDialog(marvin, "New File");
			}
			// Menu Item Open
			else if(e.getSource() == jmiOpen){
				loadImage(null);
				titlep.setTitle("Image - " + image.getWidth() + " x "+ image.getHeight());
			}
			
			// Menu Item Save as
			else if(e.getSource() == jmiSave){
				
				if(currentFilePath != null){
					saveImage();
				}
				else{
					saveAsImage();
				}
			}
			// Menu Item Save
			else if(e.getSource() == jmiSaveAs){
				saveAsImage();
			}
			
			// Menu Item Close
			else if (e.getSource() == jmiExit){
				System.exit(0);
			}
			
			// Menu Item Reset
			else if(e.getSource() == jmiReset){
				//performanceMeter.reset();
				imagePanel.getHistory().clear();
				try {
					image = originalImage.clone();
					image.update();
					imagePanel.setImage(image);
					imagePanel.getHistory().addEntry("Initial Image", image, null);
					//imageIcon.setImage((Image) image.getImage());
					repaint();
				} catch (Exception e3) {
					MarvinErrorHandler.handleDialog(MarvinErrorHandler.TYPE.IMAGE_RELOAD, e3);
				}
			}			
			// Menu Item History
			else if(e.getSource() == jmiShowHistory){
				//pluginHistory.showThumbnailHistory();
				imagePanel.getHistory().showThumbnailHistory();
			}
			// Menu Item About
			else if(e.getSource() == jmiAbout){
				//new AboutDialog(marvin);
				JOptionPane.showMessageDialog(null, 
						"    Marvin Editor "+MARVIN_EDITOR_VERSION+"\n"+
						"www.marvinproject.org",
						"Marvin", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
	
	/**
	 * The main method
	 * @param args Arguments
	 * 
	 * @see MarvinEditor arguments
	 */
	public static void main(String[] args) {
		MarvinEditor l_editor = new MarvinEditor(args);	
		
		// Defines the default close operation.
		l_editor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
