package core;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;

public class Main {
	public static int counter = 0;

	public static JFrame frame;
	public static JLabel lblStato = new JLabel("Stato: Ready");
	public static JLabel lblTime = new JLabel("");



	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Main() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 384, 203);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnTrain = new JButton("Train");
		btnTrain.setBounds(67, 37, 89, 23);
		frame.getContentPane().add(btnTrain);
		
		JButton btnTest = new JButton("Test");
		btnTest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JFileChooser fc = null;
				try {
					fc = new JFileChooser(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
					System.out.println(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());} 
				catch (URISyntaxException e3) {
					e3.printStackTrace();}
				long startTime = System.currentTimeMillis();
				lblStato.setText("Testing..");

				//fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); //Naviga solo tra le directory
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); //Naviga tra le directory e i files
				FileFilter filter = new FileNameExtensionFilter("txt", "dat", "xml");
				fc.setFileFilter(filter); //Applica il filtro
				
				if(fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION){
				    System.out.println("URL Directory: " + fc.getCurrentDirectory()); //Stampa l'URL della directory selezionata / del file
				    System.out.println("URL File: " + fc.getSelectedFile()); //Stampa l'URL della directory del file selezionato
		
				    //Stampa i nomi di tutti i files all'interno della stessa dir del file	
				    if (fc.getSelectedFile().isDirectory()){
				    	System.out.println("Avvio gestione più files: ");
				    	File dir= new File(fc.getSelectedFile().toString());
				    	try {
							Test.testing(fc.getSelectedFile().toString(), scanFolder(dir));
				    		} 
				    	catch (SQLException | FileNotFoundException e) {
							e.printStackTrace();}
					   
					    
					}//End if (fc.getSelectedFile().isDirectory())
				    else{
				    	System.out.println("Avvio gestione singolo file");
				    	
				    	System.out.println(fc.getSelectedFile().toString());	
				    	
				    }//End else
				    lblTime.setText("Tempo di Esecuzione: " + ((System.currentTimeMillis() - startTime)/1000) + " secondi");
				    lblStato.setText("Stato: Ready");
	        		JOptionPane.showMessageDialog(frame, "Testing Terminato!");
				}//End if(fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)	
			}
		});
		btnTest.setBounds(204, 37, 89, 23);
		frame.getContentPane().add(btnTest);
		
		
		lblStato.setBounds(10, 142, 148, 14);
		frame.getContentPane().add(lblStato);
		
		JLabel lblTime = new JLabel("");
		lblTime.setBounds(121, 142, 237, 14);
		frame.getContentPane().add(lblTime);
		
		JButton btnDemo = new JButton("Demo");
		btnDemo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser fc = null;
				try {
					fc = new JFileChooser(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
					System.out.println(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());} 
				catch (URISyntaxException e3) {
					e3.printStackTrace();}
				long startTime = System.currentTimeMillis();
				lblStato.setText("Testing..");

				//fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); //Naviga solo tra le directory
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); //Naviga tra le directory e i files
				FileFilter filter = new FileNameExtensionFilter("txt", "dat", "xml");
				fc.setFileFilter(filter); //Applica il filtro
				
				if(fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION){
				    System.out.println("URL Directory: " + fc.getCurrentDirectory()); //Stampa l'URL della directory selezionata / del file
				    System.out.println("URL File: " + fc.getSelectedFile()); //Stampa l'URL della directory del file selezionato
		
				    //Stampa i nomi di tutti i files all'interno della stessa dir del file	
				    if (fc.getSelectedFile().isDirectory()){
				    	System.out.println("Avvio gestione più files: ");
				    	File dir= new File(fc.getSelectedFile().toString());
				    	try {
							Demo.testing(fc.getSelectedFile().toString(), scanFolder(dir));
				    		} 
				    	catch (SQLException | FileNotFoundException e) {
							e.printStackTrace();}
					   
					    
					}//End if (fc.getSelectedFile().isDirectory())
				    else{
				    	System.out.println("Avvio gestione singolo file");
				    	
				    	System.out.println(fc.getSelectedFile().toString());	
				    	
				    }//End else
				    lblTime.setText("Tempo di Esecuzione: " + ((System.currentTimeMillis() - startTime)/1000) + " secondi");
				    lblStato.setText("Stato: Ready");
	        		JOptionPane.showMessageDialog(frame, "Demo Terminato!");
				}//End if(fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)	
				
			}
		});
		btnDemo.setBounds(136, 86, 89, 23);
		frame.getContentPane().add(btnDemo);
		
		
		btnTrain.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JFileChooser fc = null;
				try {
					fc = new JFileChooser(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
					System.out.println(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
				} catch (URISyntaxException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
				lblStato.setText("Training..");

				long startTime = System.currentTimeMillis();
				//fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); //Naviga solo tra le directory
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); //Naviga tra le directory e i files
				FileFilter filter = new FileNameExtensionFilter("txt", "dat", "xml");
				fc.setFileFilter(filter); //Applica il filtro
				
				if(fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION){
				    System.out.println("URL Directory: " + fc.getCurrentDirectory()); //Stampa l'URL della directory selezionata / del file
				    System.out.println("URL File: " + fc.getSelectedFile()); //Stampa l'URL della directory del file selezionato
		
				    //Stampa i nomi di tutti i files all'interno della stessa dir del file	
				    if (fc.getSelectedFile().isDirectory()){
				    	System.out.println("Avvio gestione più files: ");
				    	File dir= new File(fc.getSelectedFile().toString());
				    	try {
							Train.training(fc.getSelectedFile().toString(), scanFolder(dir));
				    		} 
				    	catch (SQLException | FileNotFoundException e) {
							e.printStackTrace();}
					   
					    
					}//End if (fc.getSelectedFile().isDirectory())
				    else{
				    	System.out.println("Avvio gestione singolo file");
				    	
				    	System.out.println(fc.getSelectedFile().toString());			
				    }//End else
				    lblTime.setText("Tempo di Esecuzione: " + ((System.currentTimeMillis() - startTime)/60000) + " minuti");
				}//End if(fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)	
			    lblStato.setText("Stato: Ready");
        		JOptionPane.showMessageDialog(frame, "Training Terminato!");
			}
		});
		
	}
	
	public static String[] scanFolder(File dir){
		File[] fList = dir.listFiles();
		String filesNames []= new String [fList.length]; 
		int i = 0;
	    for (File file : fList){ //Gira per tutti i files contenuti nella directory
	    	if (file.isDirectory()){ //Se il file è una directory, allora naviga anche nella sua sottodirectory
	    		//System.out.println("Directory: " + file.getName()); //Stampa il nome della cartella
	    		System.out.println("Directory " + file.getAbsoluteFile()); //Stampa il percorso completo di una cartella
	    		//File dire= new File(file.getAbsoluteFile().toString());
	    		scanFolder(new File(file.getAbsoluteFile().toString()));
	    		
	    	}
	    	else{ //Se il file è un singolo file classico
	    		
	    		if (file.getName().toLowerCase().endsWith("txt") || 
	    			file.getName().toLowerCase().endsWith("xml") || 
	    			file.getName().toLowerCase().endsWith("dat")){
		    		System.out.println("File: " + file.getName().toLowerCase());
		    		filesNames[i] = file.getName().toLowerCase();
		    		i++;
		    	}
	            
	    	}
	    	//System.out.println(file.getName());
        }
	    return filesNames;
	    
	}
}
