package Project;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import Project.QuadTree.Node;

public class Draw {
	
	static QuadTree rootQ = null;
	static KDTree rootKD = null;
	static RTree rootR = null;
	
	int numToShow;
	String dataStructure;
	
    public Draw() throws IOException
    {	   	   	   	
    	
    	JFrame frame = new JFrame("Nearest State & County");
        frame.setLayout(new FlowLayout());
        frame.setSize(800, 780);
    	
        BufferedImage img = ImageIO.read(new File("white-states_50.png"));
        ImageIcon icon = new ImageIcon(img);        
        
        ArrayList<Point> points = new ArrayList<Point>(); 
        
        JPanel panelA = new JPanel(){
            @Override
            public void paintComponent(Graphics g) {
               super.paintComponent(g);
               Graphics2D g2 = (Graphics2D) g;
               
               g2.setColor(Color.RED);
               
               g.drawImage(icon.getImage(), 0, 0, null);
               for (Point point : points) {
                   g2.fillOval(point.x - 5, point.y - 5, 8, 8);
               }
            };
        };                
        
        JPanel panelB = new JPanel();
        
        JButton button = new JButton("GetResults");
        JTextField longitude = new JTextField(8);
        JTextField lattitude = new JTextField(8);
        JLabel xLabel = new JLabel("longtitude");
        JLabel yLabel = new JLabel("lattitude");
        JTextArea textArea = new JTextArea(15, 70);
        JRadioButton quad   = new JRadioButton("Quad", true);
        JRadioButton r    = new JRadioButton("R");
        JRadioButton kd = new JRadioButton("Kd");
        JLabel nLabel = new JLabel("Num of results");
        
        dataStructure = "q";
        
        textArea.setEditable(false);
        
        String[] numOfPoints = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        JComboBox<String> numList = new JComboBox<String>(numOfPoints);
        numList.setSelectedIndex(9);
        numToShow = 10;
        
        
        panelB.add(yLabel);
        panelB.add(lattitude);
        panelB.add(xLabel);
        panelB.add(longitude); 
        panelB.add(quad);
        panelB.add(r);
        panelB.add(kd);
        panelB.add(button); 
        panelB.add(nLabel);
        panelB.add(numList);
        panelB.add(textArea);
        
        ;
        
        ButtonGroup bgroup = new ButtonGroup();
        bgroup.add(quad);
        bgroup.add(r);
        bgroup.add(kd);
        
        panelA.addMouseListener(new MouseAdapter() {
       	 @Override
            public void mousePressed(MouseEvent e) {
       		 	points.clear();
                
                double[] coords = transformCoords(e.getX(), e.getY());
                longitude.setText(Double.toString(coords[0]));
                lattitude.setText(Double.toString(coords[1]));
                
                int[] result = reverseCoords(coords[0], coords[1]);
                points.add(new Point(result[0], result[1]));
                panelA.repaint();
            }
        });
        panelA.setLayout(null);  
        
        button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Coord coord = new Coord(Double.parseDouble(longitude.getText()), Double.parseDouble(lattitude.getText()));
					Node[] nearestNodes = new Node[10];
					boolean executed = false;
					if (dataStructure == "q") {
						if (rootQ != null) {
							rootQ.search(coord, nearestNodes);
							executed = true;
							textArea.setText("QuadTree: \n");
						}
						else {
							textArea.setText("Not Built In Yet");
						}
						
					}
					else if (dataStructure == "k"){
						if (rootKD != null) {
							nearestNodes = rootKD.nearest(rootKD.root,coord);
							executed = true;
							textArea.setText("KD Tree: \n");
						}
						else {
							textArea.setText("Not Built In Yet");
						}
					}
					else {
						if (rootR != null) {
							rootR.search(coord, nearestNodes);
							executed = true;
							textArea.setText("R Tree: \n");
						}
						else {
							textArea.setText("Not Built In Yet");
						}

					}
					
					if (executed) {
						String[] result = Test.getVotesResult(nearestNodes);
						textArea.append("The point is in " + result[1] + ", " + result[0] + "\n");
				    	textArea.append("Nearest States and County: \n");			
						
						for (int i = 0; i < numToShow; i++) {
							textArea.append(nearestNodes[i].toString() + " Distance: " + String.format("%.3fkm\n", coord.distanceFrom(nearestNodes[i].point)));
						}
					}
					
				}
				catch (Exception ex) {
					textArea.setText("Please Enter Valid Nums For Latitude and Longitude");
				}
				
			}
        	
        });
        
        longitude.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				points.clear();
				try {
					int[] result = reverseCoords(Double.parseDouble(longitude.getText()), Double.parseDouble(lattitude.getText()));
					points.add(new Point(result[0], result[1]));
	                panelA.repaint();
				}
				catch(Exception e) {
					if (!longitude.getText().equals("")) {
						System.out.println("Please enter number");
					}
					panelA.repaint();
				}
                
				
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				points.clear();
				try {
					int[] result = reverseCoords(Double.parseDouble(longitude.getText()), Double.parseDouble(lattitude.getText()));
					points.add(new Point(result[0], result[1]));
	                panelA.repaint();
				}
				catch(Exception e) {
					if (!longitude.getText().equals("")) {
						System.out.println("Please enter number");
					}
					panelA.repaint();
				}
				
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				points.clear();
				try {					
					int[] result = reverseCoords(Double.parseDouble(longitude.getText()), Double.parseDouble(lattitude.getText()));
					points.add(new Point(result[0], result[1]));
	                panelA.repaint();
				}
				catch(Exception e) {
					if (!longitude.getText().equals("")) {
						System.out.println("Please enter number");
					}					
					panelA.repaint();
				}
				
			}
        	
        });
        
        lattitude.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				points.clear();
				try {
					int[] result = reverseCoords(Double.parseDouble(longitude.getText()), Double.parseDouble(lattitude.getText()));
					points.add(new Point(result[0], result[1]));
	                panelA.repaint();
				}
				catch(Exception e) {
					if (!lattitude.getText().equals("")) {
						System.out.println("Please enter number");
					}	
					panelA.repaint();
				}
				
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				points.clear();
				try {
					int[] result = reverseCoords(Double.parseDouble(longitude.getText()), Double.parseDouble(lattitude.getText()));
					points.add(new Point(result[0], result[1]));
	                panelA.repaint();
				}
				catch(Exception e) {
					if (!lattitude.getText().equals("")) {
						System.out.println("Please enter number");
					}	
					panelA.repaint();
				}
				
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				points.clear();
				try {
					int[] result = reverseCoords(Double.parseDouble(longitude.getText()), Double.parseDouble(lattitude.getText()));
					points.add(new Point(result[0], result[1]));
	                panelA.repaint();
				}
				catch(Exception e) {
					if (!lattitude.getText().equals("")) {
						System.out.println("Please enter number");
					}	
					panelA.repaint();
				}
				
			}
        	
        });
        
        quad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dataStructure = "q";
				
			}
        	
        });
        
        r.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dataStructure = "r";
				
			}
        	
        });
        
        kd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dataStructure = "k";
				
			}
        	
        });
        
        numList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<String> cb = (JComboBox) e.getSource();
				numToShow = Integer.parseInt((String)cb.getSelectedItem());
		        
				
			}
        	
        });
        
        Container cp = frame.getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(panelA, BorderLayout.CENTER);
        cp.add(panelB, BorderLayout.SOUTH);
        
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public double[] transformCoords(int x, int y) {
    	double longtitude = (double) x * 7 / 88 - 127.675;
    	double latitude = (double) - y * 4 / 70 + 50.7143;
    	longtitude = Math.round(longtitude * 10000000.0) / 10000000.0;
    	latitude = Math.round(latitude * 10000000.0) / 10000000.0;
    	double[] result = {longtitude, latitude};
    	return result;
    }
    
    public int[] reverseCoords(double longtitude, double latitude) {
    	double x = (longtitude + 127.675) * 88 / 7;
    	double y = (50.7143 - latitude) * 70 / 4;
    	int[] result = {(int) Math.round(x), (int) Math.round(y)};
    	return result;
    }
    
    public static void main(String[] args) throws IOException {
    	boolean buildDT = true;
		String filename = "NationalFile_StateProvinceDecimalLatLong.txt";
		try {
			if (buildDT) {
				
				ArrayList<Node> nodes = new ArrayList<Node>();
				double[] graphBounds; 
				graphBounds = Test.readGraph(filename, nodes);
				
				rootQ = Test.buildQuadTree(nodes, graphBounds);
				rootKD = Test.buildKDTree(nodes);
				// rootR = Test.buildRTree(nodes); // Seems to has over stack problem
			}			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
    	Draw dr = new Draw();
    }
}
