package ui_app;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.border.LineBorder;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 550;
    private static final int HEIGHT = 550;

    private JPanel panel1;
    private JScrollPane scrollPane1;
    private JScrollPane scrollPane2;
    private JButton openButton1;
    private JButton openButton2;
    private ImagePanel imgLabel1;
    private ImagePanel imgLabel2;
    private JComboBox chooseChanneBox;
    private JComboBox chooseFormulaBox;
    private JSlider slider;
    private JLabel lblAnswer;
    private JLabel lblFormula;
    private ChartPanel chartPanel;
    private JPanel JchartPanel;
    private JButton clearButton;
    private JButton compareButton;
    
    private BufferedImage buf_image1 = null;
    private BufferedImage buf_image2 = null;

    private BufferedImage buf_image1_temp = null;
    private BufferedImage buf_image2_temp = null;
    private double zoom = 1;
    String[] rgbcolors = {
            "all colors",
            "red",
            "green",
            "blue",
            "grey"
    };
    String[] formulas = {
            "NCC",
            "SSD",
            "NSSD",
            "standart"
    };
    //Controller controller = new Controller();
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
        super("");
        Toolkit kit = Toolkit.getDefaultToolkit();
        setSize(558, kit.getScreenSize().height - 70);
        setLocation((kit.getScreenSize().width - WIDTH) / 2, 5);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createGUIelements();
    }

    public void createGUIelements() {
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
                        slider.setValue((int) (zoom / 0.02));
                        resizeImage();
                    }
            }
        });
        panel1.setLayout(null);

        openButton1 = new JButton("Open image 1");
        openButton1.setBounds(59, 11, 160, 23);
        panel1.add(openButton1);
        openButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	openImage(imgLabel1, 1);
            }
        });

        openButton2 = new JButton("Open image 2");
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
        imgLabel1 = new ImagePanel(1);
        scrollPane1.setViewportView(imgLabel1);

        scrollPane2 = new JScrollPane();
        scrollPane2.setBounds(270, 40, 255, 255);
        panel1.add(scrollPane2);
        imgLabel2 = new ImagePanel(2);
        scrollPane2.setViewportView(imgLabel2);

        chooseChanneBox = new JComboBox(rgbcolors);
        chooseChanneBox.setEnabled(false);
        chooseChanneBox.setBounds(402, 306, 109, 20);
        panel1.add(chooseChanneBox);
        chooseChanneBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    int index = chooseChanneBox.getSelectedIndex();
                    buf_image1_temp = RGBchannels.getRGB(index, buf_image1);
                    buf_image2_temp = RGBchannels.getRGB(index, buf_image2);
                    imgLabel1.setIcon(Controller.makeNewSize(buf_image1_temp, zoom));
                    imgLabel2.setIcon(Controller.makeNewSize(buf_image2_temp, zoom));
            }
        });

        slider = new JSlider();
        slider.setEnabled(false);
        slider.setBounds(171, 306, 200, 31);
        slider.setValue(50);
        slider.setMinorTickSpacing(5);
        slider.setMajorTickSpacing(10);
        slider.setSnapToTicks(true);
        slider.setPaintTicks(true);
        panel1.add(slider);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                zoom = 0.02 * slider.getValue();
                zoom = Math.max(zoom, 0.1);
                resizeImage();
            }
        });

        compareButton = new JButton("Compare!");
        compareButton.setEnabled(false);
        compareButton.setBounds(10, 306, 89, 23);
        panel1.add(compareButton);
        compareButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	BufferedImage buf = buf_image2_temp.getSubimage(imgLabel1.getCoords(0), imgLabel1.getCoords(1),
                        imgLabel1.getCoords(2), imgLabel1.getCoords(3));
				if(buf_image1 != null && buf_image2 != null && buf != null)
            	{
            		int formula_index = chooseFormulaBox.getSelectedIndex();  
            		BufferedImage temp2 = Controller.deepCopy(buf_image2_temp);
                    
                    /*if (buf != null) {
                        File outputfile = new File("croppedImage.jpg");
                        try {
                            ImageIO.write(buf, "jpg", outputfile);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }*/
                    double[][] arr = templateMatch.compare(buf_image2_temp, buf, formula_index);

            		int w = buf_image2_temp.getWidth()-buf.getWidth()+1;
            		refreshChart(arr, w, templateMatch.getMatchLoc().y);         	
            		lblAnswer.setText("<html>" + "Original at  ["+ imgLabel1.getCoords(0) + ", " + imgLabel1.getCoords(1) + "]"+  ", " + 
            				"Offset:  [" + (imgLabel1.getCoords(0)- templateMatch.getMatchLoc().x) +  ", " +
                            (imgLabel1.getCoords(1)- templateMatch.getMatchLoc().y) + "]" + "<br>" +
                            "Best match at [" + templateMatch.getMatchLoc().x +
            				", " + templateMatch.getMatchLoc().y + "] = " 
                            + (Math.rint(templateMatch.getCorrFunc()*1e3)/1e3) + "</html>");

            		imgLabel1.setIcon(Controller.makeNewSize(buf_image1_temp, zoom));
            		imgLabel2.setIcon(Controller.makeNewSize(buf_image2_temp, zoom));
            		buf_image2_temp = Controller.deepCopy(temp2);
            	}
				else 
					JOptionPane.showMessageDialog(panel1, "Open images and select region of interest", "Error", JOptionPane.ERROR_MESSAGE);
				//buf_image1_temp = TemplateMatching.run(buf_image1_temp, buf_image2_temp);
				//new SwingWrapper(getChart()).displayChart();
            }          

        });

        clearButton = new JButton("Clear");
        clearButton.setEnabled(false);
        clearButton.setBounds(104, 306, 64, 23);
        panel1.add(clearButton);
        clearButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {    
        		if(buf_image1 != null && buf_image2 != null){
        			imgLabel1.removeRect();
        			//imgLabel1.setIcon(Controller.makeNewSize(buf_image1_temp, zoom));
        			imgLabel2.setIcon(Controller.makeNewSize(buf_image2_temp, zoom));        			
        		}
        		else
        			JOptionPane.showMessageDialog(panel1, "First open both images", "Error", JOptionPane.ERROR_MESSAGE);
        	}
        });
        
        JchartPanel = new JPanel();
        JchartPanel.setBounds(10, 375, 522, 278);
        JchartPanel.setBorder(LineBorder.createGrayLineBorder());
        panel1.add(JchartPanel);

        lblAnswer = new JLabel();
        lblAnswer.setVerticalAlignment(SwingConstants.TOP);
        lblAnswer.setHorizontalAlignment(SwingConstants.LEFT);
        lblAnswer.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblAnswer.setText("Answer will be here");
        lblAnswer.setBounds(312, 337, 220, 33);
        panel1.add(lblAnswer);
        lblAnswer.setBorder(LineBorder.createGrayLineBorder());

        lblFormula = new JLabel("Choose a formula");/////???????????????????????
        lblFormula.setBounds(10, 344, 117, 20);
        panel1.add(lblFormula);

        chooseFormulaBox = new JComboBox(formulas);
        chooseFormulaBox.setEnabled(false);
        chooseFormulaBox.setBounds(137, 344, 82, 20);
        panel1.add(chooseFormulaBox);

        //imgPanel2.addMouseListener(new PopClickListener());
    }
    class PopUpDemo extends JPopupMenu {
        JMenuItem anItem;
        public PopUpDemo(){
            anItem = new JMenuItem("get image back");
            add(anItem);
            /*anItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    imgPanel2.setImage(buf_image2);
                }
            });*/
        }
    }
    class PopClickListener extends MouseAdapter {
        public void mousePressed(MouseEvent e){
            if (e.isPopupTrigger())
                doPop(e);
        }

        public void mouseReleased(MouseEvent e){
            if (e.isPopupTrigger())
                doPop(e);
        }

        private void doPop(MouseEvent e){
            PopUpDemo menu = new PopUpDemo();
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

	public void resizeImage() {
		if (buf_image1_temp != null) {
			imgLabel1.setIcon(Controller.makeNewSize(buf_image1_temp, zoom));
		}
		if (buf_image2_temp != null) {
			imgLabel2.setIcon(Controller.makeNewSize(buf_image2_temp, zoom));
		}
	}
	public ImageIcon createImageIcon(String ImagePath, int numberOfImage) {
        ImageIcon newImg = null;
		int zoomscale = 1;
		if (numberOfImage == 1) {
			try {
				buf_image1 = ImageIO.read(new File(ImagePath));
				buf_image1_temp = Controller.deepCopy(buf_image1);
				//newImg = new ImageIcon(Controller.resizeImagetoLabel(buf_image1,imgLabel1));
				newImg = Controller.makeNewSize(buf_image1, zoomscale);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				buf_image2 = ImageIO.read(new File(ImagePath));
				buf_image2_temp = Controller.deepCopy(buf_image2);
				//newImg = new ImageIcon(Controller.resizeImagetoLabel(buf_image2,imgLabel2));
				newImg = Controller.makeNewSize(buf_image2, zoomscale);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return newImg;
	}

	public void openImage(ImagePanel imgLabel, int numberOfImage) {
		Dimension dim = new Dimension();
		JFileChooser file = new JFileChooser();
		file.setCurrentDirectory(new File("."));
		//filter the files
		//FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "PNG", "jpg", "jpeg");
		//file.addChoosableFileFilter(filter);
		int result = file.showOpenDialog(null);
		//if the user click on save in Jfilechooser
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = file.getSelectedFile();
			String path = selectedFile.getAbsolutePath();
			if (Controller.checkSelectedFile(selectedFile)) {
				imgLabel.setIcon(createImageIcon(path, numberOfImage));
			    chooseChanneBox.setEnabled(true);
			    chooseFormulaBox.setEnabled(true);
			    slider.setEnabled(true);
			    clearButton.setEnabled(true);
			    compareButton.setEnabled(true);
			} else
				JOptionPane.showMessageDialog(panel1, "Could not open file", "Error", JOptionPane.ERROR_MESSAGE);
		}
		//if the user click on save in Jfilechooser
		else if (result == JFileChooser.CANCEL_OPTION) {
			System.out.println("No File Select");
		}
	}

	public JFreeChart createChart(double[][] arr, int w, int bestY) {
		//String chartTitle = "Correlation function";
		// based on the dataset we create the chart
		JFreeChart myChart = ChartFactory.createBarChart(null, "", "R(x)", createDataset(arr, w, bestY), PlotOrientation.VERTICAL, true, false, false);
		return myChart;
	}

	public void refreshChart(double[][] arr, int w, int bestY) {
		JchartPanel.removeAll();
		JchartPanel.revalidate(); // This removes the old chart
		JFreeChart aChart = createChart(arr, w, bestY);
		aChart.removeLegend();
		chartPanel = new ChartPanel(aChart);
		chartPanel.setMouseWheelEnabled(true);
		JchartPanel.setLayout(new BorderLayout());
		JchartPanel.add(chartPanel);
		JchartPanel.repaint();
	}

	private CategoryDataset createDataset(double[][] arr, int w, int bestY) {
		// row keys...
		String corr = "";
		// column keys...
		String column_key = "";
		// create the dataset...
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (int i = 0; i < w; i++) {
			corr = "R(" + String.valueOf(bestY);
			column_key = String.valueOf(i);
			dataset.addValue(arr[i][bestY], corr, column_key);
		}
		return dataset;
	}
}
