package ui_app;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ui_app.RGBchannels;
import ui_app.Controller;
import ui_app.TemplateMatching;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	// Размеры окна приложения в виде констант
    private static final int WIDTH = 550;
    private static final int HEIGHT = 550;

	private JPanel panel1;
	private	JScrollPane scrollPane1;
	private	JScrollPane scrollPane2;
    private JButton openButton1;
    private JButton openButton2;
    private JLabel imgLabel1;
    private JLabel imgLabel2;
	private JComboBox chooseChannelСomboBox;
	private JComboBox chooseFormulaBox;
	private JSlider slider;
	private JLabel lblAnswer;
	private JLabel lblFormula;
	
    private BufferedImage buf_image1 = null;
    private BufferedImage buf_image2 = null;
    
    private BufferedImage buf_image1_temp = null;
    private BufferedImage buf_image2_temp = null;
   
    private double zoom = 1;
    String[] rgbcolors = {
    		"все цвета",
    	    "красный",
    	    "зеленый",
    	    "синий",
    	    "серый"
    	};
    String[] formulas = {
    		"NCC",
    		"SSD",
    		"NSSD"
    	};
    Controller controller = new Controller();
    TemplateMatching templateMatch = new TemplateMatching();
    
    public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public MainFrame() {
		super("уруру");		
        Toolkit kit = Toolkit.getDefaultToolkit();
        setSize(558, kit.getScreenSize().height);
        // Отцентрировать окно приложения на экране
       // setLocation((kit.getScreenSize().width - WIDTH)/2, (kit.getScreenSize().height - HEIGHT)/2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createGUIelements();
	}
	public void createGUIelements(){
		panel1 = new JPanel();
		setContentPane(panel1);
		
		panel1.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                double temp = zoom - (notches * 0.1);
                // minimum zoom factor is 1.0
                temp = Math.max(temp, 0.1);
                if (temp != zoom) {
                    zoom = temp;
                    slider.setValue((int)(zoom/0.02));
                    resizeImage();
                }                
            }
        });
		panel1.setLayout(null);
		
		openButton1 = new JButton("Выбрать оригинал");
		openButton1.setBounds(59, 11, 160, 23);
		panel1.add(openButton1);
		openButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openImage(imgLabel1, 1);
            }
        });
		
		openButton2 = new JButton("Выбрать образец");
		openButton2.setBounds(317, 11, 160, 23);
		panel1.add(openButton2);
		openButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openImage(imgLabel2, 2);         	
            }
        }); 
		
		scrollPane1 = new JScrollPane();
		scrollPane1.setBounds(10, 40, 255, 255);
		panel1.add(scrollPane1);
		
		imgLabel1 = new JLabel();
		scrollPane1.setViewportView(imgLabel1);
		
		scrollPane2 = new JScrollPane();
		scrollPane2.setBounds(270, 40, 255, 255);
		panel1.add(scrollPane2);
		
		imgLabel2 = new JLabel();
		scrollPane2.setViewportView(imgLabel2);
		
		chooseChannelСomboBox = new JComboBox(rgbcolors);
		chooseChannelСomboBox.setBounds(402, 306, 109, 20);
		panel1.add(chooseChannelСomboBox);
		chooseChannelСomboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	int index = chooseChannelСomboBox.getSelectedIndex();
            	buf_image1_temp = RGBchannels.getRGB(index, buf_image1);
            	buf_image2_temp = RGBchannels.getRGB(index, buf_image2);
                imgLabel1.setIcon(Controller.makeNewSize(buf_image1_temp, zoom));
                imgLabel2.setIcon(Controller.makeNewSize(buf_image2_temp, zoom));
            }
        });
		
		slider = new JSlider();
		slider.setBounds(171, 306, 200, 31);
		slider.setValue(50);
		slider.setMinorTickSpacing(5);
		slider.setMajorTickSpacing(10);
		slider.setSnapToTicks(true);
		slider.setPaintTicks(true);
		panel1.add(slider);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event){
				zoom = 0.02 * slider.getValue();
				zoom = Math.max(zoom, 0.1);
                resizeImage();
			}
		});
		
		JButton compareButton = new JButton("Сравнить!");
		compareButton.setBounds(10, 306, 120, 23);
		panel1.add(compareButton);
		compareButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(buf_image1 != null && buf_image2 != null)
            	{
            		int formula_index = chooseFormulaBox.getSelectedIndex();
            		buf_image1_temp = Controller.deepCopy(buf_image1);
            		buf_image2_temp = Controller.deepCopy(buf_image2);
            		double[][] arr = templateMatch.compare(buf_image1_temp, buf_image2_temp, formula_index);

            		int w = buf_image1_temp.getWidth()-buf_image2_temp.getWidth()+1;            	
            		addGraphic(arr, w, templateMatch.getMatchLoc().y);         	    
            		lblAnswer.setText("<html>" + "R (" + templateMatch.getMatchLoc().x + 
            				", " + templateMatch.getMatchLoc().y +
            				") = " + Math.rint(templateMatch.getCorrFunc() * 1e4)/1e4 + "<br></html>");

            		imgLabel1.setIcon(Controller.makeNewSize(buf_image1_temp, zoom));
            	}
            	//buf_image1_temp = TemplateMatching.run(buf_image1_temp, buf_image2_temp);
            	//new SwingWrapper(getChart()).displayChart();
            }
        });		

		lblAnswer = new JLabel();
		lblAnswer.setVerticalAlignment(SwingConstants.TOP);
		lblAnswer.setHorizontalAlignment(SwingConstants.LEFT);
		lblAnswer.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblAnswer.setText("Answer will be here");
		lblAnswer.setBounds(402, 337, 120, 33);
		panel1.add(lblAnswer);
		lblAnswer.setBorder(LineBorder.createGrayLineBorder());
		
		lblFormula = new JLabel("Выберите формулу:");
		lblFormula.setBounds(10, 340, 109, 23);
		panel1.add(lblFormula);
		
		chooseFormulaBox = new JComboBox(formulas);
		chooseFormulaBox.setBounds(128, 341, 109, 20);
		panel1.add(chooseFormulaBox);
	}	
	public void resizeImage() {
        //System.out.println(zoom);
        if(buf_image1_temp != null) {
        	imgLabel1.setIcon(Controller.makeNewSize(buf_image1_temp, zoom));
        }
        if(buf_image2_temp != null) {
        	imgLabel2.setIcon(Controller.makeNewSize(buf_image2_temp, zoom));
        }
	}
    public ImageIcon createImageIcon(String ImagePath, int numberOfImage) {
        ImageIcon newImg = null;
        int zoomscale = 1;
    	if(numberOfImage == 1){
	    	try {
				buf_image1 = ImageIO.read(new File(ImagePath));
				buf_image1_temp  = Controller.deepCopy(buf_image1);
				//newImg = new ImageIcon(Controller.resizeImagetoLabel(buf_image1,imgLabel1));
				newImg = Controller.makeNewSize(buf_image1, zoomscale);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		try {
				buf_image2 = ImageIO.read(new File(ImagePath));
				buf_image2_temp  = Controller.deepCopy(buf_image2);
				//newImg = new ImageIcon(Controller.resizeImagetoLabel(buf_image2,imgLabel2));
				newImg = Controller.makeNewSize(buf_image2, zoomscale);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	return newImg;
    }
    public void openImage(JLabel imgLabel, int numberOfImage){
        JFileChooser file = new JFileChooser();
        file.setCurrentDirectory(new File(System.getProperty("user.home")));
        //filter the files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg","gif","png");
        file.addChoosableFileFilter(filter);
        int result = file.showOpenDialog(null);
        //if the user click on save in Jfilechooser
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = file.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            if(Controller.checkSelectedFile(selectedFile)) {
                    imgLabel.setIcon(createImageIcon(path, numberOfImage));                    
            } else
                JOptionPane.showMessageDialog(panel1, "Could not open file", "Error", JOptionPane.ERROR_MESSAGE);
        }
        //if the user click on save in Jfilechooser
        else if (result == JFileChooser.CANCEL_OPTION) {
            System.out.println("No File Select");
        }
    }
    public void addGraphic(double[][] arr, int w, int bestY){
    	String chartTitle = "График зависимости корреляционной функции";		
    	// based on the dataset we create the chart
    	JFreeChart myChart = ChartFactory.createBarChart(chartTitle, "", "R(x)", createDataset(arr, w, bestY),PlotOrientation.VERTICAL, false, true, false);
    	ChartPanel chartPanel = new ChartPanel(myChart);
    	chartPanel.setBounds(10, 375, 522, 278);
    	chartPanel.setVisible(true);
    	chartPanel.setMouseWheelEnabled(true);
    	panel1.add(chartPanel);
    }
    private CategoryDataset createDataset(double[][] arr,int w, int bestY) {
		// row keys...
		String corr = "";
		// column keys...
		String column_key = "";
		// create the dataset...
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	
		for(int i = 0;i < w;i++){
			corr = "R("+String.valueOf(bestY);
			column_key = String.valueOf(i);
			dataset.addValue(arr[i][bestY], corr, column_key);
		}
		return dataset;     
	}
}