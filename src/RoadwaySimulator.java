import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class RoadwaySimulator extends JFrame {
	
	String fileName;
	Document doc;
	RoadCell[][] Tiles= new RoadCell[9][9];
	ArrayList<Car> Cars= new ArrayList<Car>();
	GridPanel gridPanel;
	public RoadwaySimulator()
	{
		super("Roadway Simulator");
		setSize(900,700);
		setLocation(100,10);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		String [] columnNames= {"Car#", "X","Y"};
		DefaultTableModel tableModel = new DefaultTableModel(columnNames,0);
		JTable mapTable= new JTable(tableModel);
		JMenuBar jmb= new JMenuBar();
		JMenuItem openFile = new JMenuItem("Open File...");
		openFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)//Action Event listener for OpenFile menu item
			{
			
				final JFileChooser fc= new JFileChooser();
				File path=new File("C:/Users/Bradley/Documents/Brad/USC/CS 201/eclipse_projects/RoadwaySimulator");
				fc.setCurrentDirectory(path);
				int status = fc.showOpenDialog(RoadwaySimulator.this);
				if (status== JFileChooser.APPROVE_OPTION)
				{                                               //loading information from the selected file
					File selectedFile=fc.getSelectedFile();
					fileName=selectedFile.getName();
					System.out.println("Name is: " + fileName);
					
					
					//XML PARSING USING DOM
					DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();
					try {

						DocumentBuilder db = dbf.newDocumentBuilder();
						 doc  = db.parse(fileName);
						}
					catch(ParserConfigurationException pce)
					{
							pce.printStackTrace();
					}
					catch(SAXException se)
					{
							se.printStackTrace();
					}
					catch(IOException ioe) 
					{
							ioe.printStackTrace();
					}
					
					NodeList root = doc.getChildNodes();
					Node rwNode=getNode("roadway", root);
					System.out.println(rwNode.getChildNodes().getLength());
					Node gridNode=getNode("grid", rwNode.getChildNodes());
					Node carsNode=getNode("cars", rwNode.getChildNodes());
					System.out.println(gridNode.getNodeName());
					String rowletter;
					String column;
					String tileType;
					String degree;
					String ai, speed, color, carRow, carColumn;
					//iterating through all row nodes and obtaining tile information
					NodeList rowNodes= ((Element)gridNode).getElementsByTagName("row");
					System.out.println(rowNodes.getLength());
					
					for (int i=0; i< rowNodes.getLength(); i++)//going through all rows
					{
						Node dummy=rowNodes.item(i);
						Element row=(Element)dummy;
						
						rowletter=row.getAttribute("label");
						NodeList tileNodes = row.getElementsByTagName("tile");
						for (int j=0; j<tileNodes.getLength(); j++)
						{
							Node dummy2=tileNodes.item(j);
							Element tile=(Element)dummy2;
							column=tile.getAttribute("column");
							tileType=tile.getAttribute("type");
							degree=tile.getAttribute("degree");
							Tiles[i][j]=new RoadCell(rowletter,column,tileType, degree);
							
							
						}
					
						
					}
					NodeList carNodes= ((Element)carsNode).getElementsByTagName("car");
					
					for (int k=0; k<carNodes.getLength();k++)
					{
						Node dummy3=carNodes.item(k);
						Element car= (Element)dummy3;
						color=car.getAttribute("color");
						ai=car.getAttribute("ai");
						speed=car.getAttribute("speed");
						NodeList locationNodes=car.getElementsByTagName("location");
						for (int l=0; l<locationNodes.getLength(); l++)
						{
							Node dummy4 = locationNodes.item(l);
							Element location = (Element)dummy4;
							carRow=location.getAttribute("y");
							carColumn=location.getAttribute("x");
							Cars.add(new Car(color,ai,speed,carColumn,carRow,k+1));
							System.out.println(color + " " + ai + " " + speed + " " + carColumn + " " + carRow+ " " + k);
							
						}
					}
					gridPanel.repaint();
					CarThread[] threads=new CarThread[Cars.size()];
					
					for(int l=0;l<Cars.size(); l++)
					{
						threads[l]=new CarThread(Cars.get(l),gridPanel,Tiles,tableModel,mapTable);
					}
					for (int m=0; m<Cars.size(); m++)
					{
						threads[m].start();
					}
				}
					
				
				
				
				else if (status == JFileChooser.CANCEL_OPTION){
					
				}
				fc.showOpenDialog(fc);
			}
		});
		jmb.add(openFile);
		setJMenuBar(jmb);
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel drawPanel= new JPanel();
		Dimension drawDimension= new Dimension(600,600);
		drawPanel.setPreferredSize(drawDimension);
		drawPanel.setBackground(Color.WHITE);
		drawPanel.setLayout(new GridBagLayout());
		
		Dimension drawPanelSize=new Dimension(466,466);
		 gridPanel=new GridPanel(Tiles,Cars);
		gridPanel.setPreferredSize(drawPanelSize);
		gridPanel.setBackground(Color.WHITE);
		drawPanel.add(gridPanel);
		
		add(drawPanel);
		
		
		
		//setting up the side panel + table
		JPanel tablePanel= new JPanel();
		Dimension panelDimension = new Dimension();
		panelDimension.setSize(200,600);
		
		
		
		
		
	
		mapTable.setPreferredScrollableViewportSize(panelDimension);
		mapTable.setFillsViewportHeight(true);
		mapTable.setBackground(Color.LIGHT_GRAY);
		JScrollPane jsp = new JScrollPane(mapTable);
		tablePanel.add(jsp);
		add(tablePanel);
		
	
		
		
		
		setVisible(true);

		
		
	
		
	}
	
	
	public static void main(String [] args)
	{
		RoadwaySimulator rs= new RoadwaySimulator();
	}
	
	class GridPanel extends JPanel{
		public RoadCell[][] rcArray;
		public ArrayList<Car> carArray;
		public int counter=0;
		public GridPanel(RoadCell[][] rcArray, ArrayList<Car> carArray)
		{
			this.rcArray=rcArray;
			this.carArray=carArray;
		}
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			//x axis
			if (counter>0 && carArray.size()>0)//set to green
			{
				g.setColor(Color.green);
				g.fillRect(15,15,466,466);
			}
			g.setColor(Color.black);
			g.drawString("1", 35, 10);//shift entire graph by 15 pixels right and down to account for axis
			g.drawString("2", 85, 10);
			g.drawString("3", 135, 10);
			g.drawString("4", 185, 10);
			g.drawString("5", 235, 10);
			g.drawString("6", 285, 10);
			g.drawString("7", 335, 10);
			g.drawString("8", 385, 10);
			g.drawString("9", 435, 10);
			//yaxis
			g.drawString("A", 5,45);
			g.drawString("B", 5,95);
			g.drawString("C", 5,145);
			g.drawString("D", 5,195);
			g.drawString("E", 5,245);
			g.drawString("F", 5,295);
			g.drawString("G", 5,345);
			g.drawString("H", 5,395);
			g.drawString("I", 5,445);
			//drawing the grid
			for(int i=0; i<10; i++)
			{
			g.drawLine(15,(i*50)+15,465,(i*50)+15);//horizontal lines
			g.drawLine((i*50)+15,15,(i*50+15),465);//vertical lines
			}
			if (counter>0 && carArray.size()>0)//will be true when recalling the repaint function
			{
				//PAINTING THE  MAP
				for(int i=0; i<9; i++)//iterating through the rcArray that holds the tile
				{
					for (int j=0; j<9; j++)
					{	
						
						//filling in middle
						if(!rcArray[i][j].type.equals("blank"))
						{
							g.fillRect((j*50)+15+16,(i*50)+15+16,18,18);
						}
						//top square section
						if((rcArray[i][j].type.equals("i")&&(rcArray[i][j].degree.equals("0")||rcArray[i][j].degree.equals("180"))) || (rcArray[i][j].type.equals("l")&&(rcArray[i][j].degree.equals("0")||rcArray[i][j].degree.equals("90"))) || (rcArray[i][j].type.equals("t")&&(rcArray[i][j].degree.equals("90")||rcArray[i][j].degree.equals("180")||rcArray[i][j].degree.equals("270"))) || rcArray[i][j].type.equals("+"))
						{
							g.fillRect((j*50)+16+15,(i*50)+15,16,16);
						}
						//Bottom square section
						if((rcArray[i][j].type.equals("i")&&(rcArray[i][j].degree.equals("0")||rcArray[i][j].degree.equals("180"))) || (rcArray[i][j].type.equals("l")&&(rcArray[i][j].degree.equals("180")||rcArray[i][j].degree.equals("270"))) || (rcArray[i][j].type.equals("t")&&(rcArray[i][j].degree.equals("90")||rcArray[i][j].degree.equals("0")||rcArray[i][j].degree.equals("270"))) || rcArray[i][j].type.equals("+"))
						{
							g.fillRect((j*50)+16+15,(i*50)+34+15,16,16);
						}
						//Left Square Section
						if((rcArray[i][j].type.equals("i")&&(rcArray[i][j].degree.equals("90")||rcArray[i][j].degree.equals("270"))) || (rcArray[i][j].type.equals("l")&&(rcArray[i][j].degree.equals("180")||rcArray[i][j].degree.equals("90"))) || (rcArray[i][j].type.equals("t")&&(rcArray[i][j].degree.equals("0")||rcArray[i][j].degree.equals("180")||rcArray[i][j].degree.equals("270"))) || rcArray[i][j].type.equals("+"))
						{
							g.fillRect((j*50)+15,(i*50)+16+15,16,16);
						}
						//Right Square Section
						if((rcArray[i][j].type.equals("i")&&(rcArray[i][j].degree.equals("90")||rcArray[i][j].degree.equals("270"))) || (rcArray[i][j].type.equals("l")&&(rcArray[i][j].degree.equals("0")||rcArray[i][j].degree.equals("270"))) || (rcArray[i][j].type.equals("t")&&(rcArray[i][j].degree.equals("0")||rcArray[i][j].degree.equals("180")||rcArray[i][j].degree.equals("90"))) || rcArray[i][j].type.equals("+"))
						{
							g.fillRect((j*50)+15+34,(i*50)+16+15,16,16);
						}
					}
				}
				
			}
			counter++;
			
			//drawing cars
			for(int k=0;k<carArray.size(); k++)
			{
				if(carArray.get(k).visible==true)//checking if car is visible (for blinking states)
				{
					
					Color carColor;
					try {
					    Field field = Class.forName("java.awt.Color").getField(carArray.get(k).color);
					    carColor = (Color)field.get(null);
					} catch (Exception e) {
					    carColor = null; // Not defined
					}
					
					
					String carNumber=carArray.get(k).number;
					int carRow=Integer.parseInt(findRow(carArray.get(k).row));
					int carColumn=Integer.parseInt(carArray.get(k).column)-1;
					g.setColor(carColor);
					g.fillOval((carColumn*50)+15+16, (carRow*50)+15+16, 20, 20);
					g.setColor(Color.black);
					g.drawString(carNumber, (carColumn*50)+15+23, (carRow*50)+15+32);
				}
			}
			
		}
	}
	
	
class CarThread extends Thread{
	private GridPanel gp;
	private RoadCell[][]cells;
	private Car car;
	double clock;
	double speed;
	double numBlinks;
	double seconds;
	DefaultTableModel dtm;
	JTable jt;
	public CarThread(Car car1, GridPanel gp,RoadCell[][] cells,DefaultTableModel dtm,JTable jt)
	{
		this.dtm=dtm;
		this.jt=jt;
		this.car=car1;
		this.gp=gp;
		this.cells=cells;
		//calculating blink frequency
		speed=Double.parseDouble(car.speed);
		numBlinks=speed*3.0;//number of blinks per sec
		seconds=(1/speed);//number of seconds a car spends in a square
		clock=seconds/6.0;//number of time spend in visible/invisible state
		Object [] carTable=new Object [] {car.number,car.column,car.row};
		dtm.addRow(carTable);
	}
	public void run()
	{
		int counter=2;
		boolean ai1First=true;
		String prevTile="nothing";
		ArrayList<RoadCell> tileMem = new ArrayList<RoadCell>();
		ArrayList<String> validDir= new ArrayList<String>();
		boolean goEast=true;
		boolean goRand=false;
		Random rand= new Random();
		int eastCell=0;
		int westCell=0;
		boolean foundE=false;
		boolean foundW=false;
		int leftCount=0;
		int topCount=0;
		int rightCount=0;
		int bottomCount=0;
		//DETERMINE EAST AND WEST MOST ROADS
		for(int i=8;i>=0;i--)//east cell
		{
			for(int j=0; j<9; j++)
			{
				if((!cells[j][i].type.equals("blank"))&&foundE==false)
				{
					eastCell=i;
					
					foundE=true;
				}
			}
		}
		for(int i=0;i<9;i++)//west cell
		{
			for(int j=0; j<9; j++)
			{
				
				if((!cells[j][i].type.equals("blank"))&&foundW==false)
				{
					westCell=i;
					
					foundW=true;
				}
			}
		}
		
		
		while (true)
		{
			//blinkng section
			if(counter==1||counter==3||counter==5)
			{
				car.visible=true;
			
					
			}
			else if (counter==2||counter==4||counter==6)
			{
				car.visible=false;
				
			}

		//A.I. code
			if(counter==1)//has blinked  6 times
			{
				
				int carColumn=Integer.parseInt(car.column);
				int carRow= Integer.parseInt(findRow(car.row));
				//CODE FOR AI 1
				if(car.ai.equals("1"))
					{
						if(ai1First==true)
						{
							//Choosing the -90 degree choice
							ai1First=false;
							if(cells[carRow][carColumn-1].isLeft==true)
							{
								
								car.column=Integer.toString(carColumn-1);
								prevTile="left";
							}
							else if(cells[carRow][carColumn-1].isTop==true)
							{
								car.row=reverseRow(Integer.toString(carRow-1));
								prevTile="top";
							}
							else if(cells[carRow][carColumn-1].isRight==true)
							{
								car.column=Integer.toString(carColumn+1);
								prevTile="right";
							}
							else
							{
								car.row=reverseRow(Integer.toString(carRow+1));
								prevTile="bottom";
							}
						}
						else//choosing the clockwise choice from previous decision
						{
							if(prevTile=="bottom")
							{
								if(cells[carRow][carColumn-1].isLeft==true)
								{
									
									car.column=Integer.toString(carColumn-1);
									prevTile="left";
								}
								
								else if(cells[carRow][carColumn-1].isRight==true)
								{
									car.column=Integer.toString(carColumn+1);
									prevTile="right";
								}
								else
									
								{
									car.row=reverseRow(Integer.toString(carRow+1));
									prevTile="bottom";
								}
							}
							else if(prevTile=="left")
							{
							
								if(cells[carRow][carColumn-1].isTop==true)
								{
									car.row=reverseRow(Integer.toString(carRow-1));
									prevTile="top";
								}
								
								
								else if(cells[carRow][carColumn-1].isBottom==true)
								{
									car.row=reverseRow(Integer.toString(carRow+1));
									prevTile="bottom";
								}
								else if(cells[carRow][carColumn-1].isLeft==true)
								{
									
									car.column=Integer.toString(carColumn-1);
									prevTile="left";
								}
							}
						
							else if(prevTile=="top")
						{
						
						
						
							 if(cells[carRow][carColumn-1].isRight==true)
							{
								 
								car.column=Integer.toString(carColumn+1);
								prevTile="right";
								
							}
							
							else if(cells[carRow][carColumn-1].isLeft==true)
							{
								//System.out.println("Hi there" + carRow + carColumn);
								car.column=Integer.toString(carColumn-1);
								prevTile="left";
							}
							else if(cells[carRow][carColumn-1].isTop==true)
								{
									car.row=reverseRow(Integer.toString(carRow-1));
									prevTile="top";
								}
						}
						
							else if(prevTile=="right")
				{
	
					 if(cells[carRow][carColumn-1].isBottom==true)
					{
						car.row=reverseRow(Integer.toString(carRow+1));
						prevTile="bottom";
					}
					
					else if(cells[carRow][carColumn-1].isTop==true)
						{
							car.row=reverseRow(Integer.toString(carRow-1));
							prevTile="top";
						}
					else if(cells[carRow][carColumn-1].isRight==true)
						{
							car.column=Integer.toString(carColumn+1);
							prevTile="right";
						}
				}
			}
		}
				else if (car.ai.equals("2"))
				{
					
					System.out.println(tileMem.size());
					int randNum;
					tileMem.add(cells[carRow][carColumn-1]);
					boolean repeat=true;
					boolean valid=false;
					while(repeat==true)
					{
						 
						
						
						//go through and check all directions, all valid ones are added to the pool (arraylist) of items to be randomely chosen from
							 if((carColumn-2)>=0&&(!tileMem.contains(cells[carRow][carColumn-2]))&&cells[carRow][carColumn-1].isLeft)//left, checking if left tile already been to
						{
							
								 validDir.add("left");
							
							valid=true;
						}
						 if((carRow-1)>=0&&!(tileMem.contains(cells[carRow-1][carColumn-1]))&&cells[carRow][carColumn-1].isTop)//top
						{
							
							 validDir.add("top");
							
							valid=true;
						}
						 if(carColumn<=8&&!(tileMem.contains(cells[carRow][carColumn]))&&cells[carRow][carColumn-1].isRight)//right
						{
							 
							 validDir.add("right");
							valid=true;
						}
						 if (carRow<8&&!(tileMem.contains(cells[carRow+1][carColumn-1]))&&cells[carRow][carColumn-1].isBottom)//bottom
						{
							 
							 validDir.add("bottom");
							
							valid=true;
						}
						if(valid==false)//not possible valid squares
						{
							tileMem.clear();//clear memory then repeat the loops of finding valid spaces
							System.out.println("clearing memory");
						}
						else
						{
							
							repeat=false;
						}
					}
					randNum=rand.nextInt(validDir.size());
					 System.out.println(car.color+randNum);
					String choice=validDir.get(randNum);
					
					if(choice.equals("left"))
					{
						
						car.column=Integer.toString(carColumn-1);
					}
					else if (choice.equals("top"))
					{
						car.row=reverseRow(Integer.toString(carRow-1));
					}
					else if (choice.equals("right"))
					{
						car.column=Integer.toString(carColumn+1);
					}
					else if (choice.equals("bottom"))
					{
						car.row=reverseRow(Integer.toString(carRow+1));
					}
					validDir.clear();
				}
				
				
				else if (car.ai.equals("3"))
				{
					if(carColumn-1==eastCell)
					{
						goEast=false;
					}
					else if (carColumn-1==westCell)
					{
						goEast=true;
					}
					if(!cells[carRow][carColumn-1].isRight&&goEast==true)
					{
						goRand=true;
					}
					if(!cells[carRow][carColumn-1].isLeft&&goEast==false)
					{
						goRand=true;
					}
					//first try to go east
					if(goEast==true&&cells[carRow][carColumn-1].isRight)//if going in east phase and can move to the east
					{
						car.column=Integer.toString(carColumn+1);
						
					}
					//try to move west
					else if(goEast==false&&cells[carRow][carColumn-1].isLeft)//west phase and can move to west	
					{
						car.column=Integer.toString(carColumn-1);
						
					}
					else if (goRand==true)//move random direction
					{
						int randNumber;
						 if((carColumn-2)>=0&&cells[carRow][carColumn-1].isLeft)//left, checking if left tile is valid
							{
								
									 validDir.add("left");
								
								
							}
							 if((carRow-1)>=0&&cells[carRow][carColumn-1].isTop)//top
							{
								
								 validDir.add("top");
								
								
							}
							 if(carColumn<=8&&cells[carRow][carColumn-1].isRight)//right
							{
								 
								 validDir.add("right");
								
							}
							 if (carRow<8&&cells[carRow][carColumn-1].isBottom)//bottom
							{
								
								 validDir.add("bottom");
								
								
							}
							
						
						randNumber=rand.nextInt(validDir.size());
						 System.out.println(car.color+randNumber);
						String choice=validDir.get(randNumber);
						
						if(choice.equals("left"))
						{
							
							car.column=Integer.toString(carColumn-1);
						}
						else if (choice.equals("top"))
						{
							car.row=reverseRow(Integer.toString(carRow-1));
						}
						else if (choice.equals("right"))
						{
							car.column=Integer.toString(carColumn+1);
						}
						else if (choice.equals("bottom"))
						{
							car.row=reverseRow(Integer.toString(carRow+1));
						}
						validDir.clear();
					}
					
				}
				else if (car.ai.equals("4"))
				{
					int randNumber;
					int [] direction=new int[4];
					int counter1=0;
					boolean foundMin=false;
					direction[0]=leftCount;
					direction[1]=rightCount;
					direction[2]=topCount;
					direction[3]=bottomCount;
					Arrays.sort(direction);//sorting all of the counts for each direction
					System.out.println("leftcount is " + leftCount + " rightcount is " + rightCount + " topcount is " + topCount + " bottom count is " + bottomCount);
					for(int i=0;i<4;i++)
					{
						System.out.println("array sorted is" + direction[i]);
					}
					while(foundMin==false)//as long as don't have a valid choice for the directions a certain amount of count
					{
					 if((carColumn-2)>=0&&cells[carRow][carColumn-1].isLeft&&leftCount==direction[counter1])//left, checking if left tile is valid
						{
							
								 validDir.add("left");
								 foundMin=true;
							
							
						}
						 if((carRow-1)>=0&&cells[carRow][carColumn-1].isTop&&topCount==direction[counter1])//top
						{
							
							 validDir.add("top");
							 foundMin=true;
							
						}
						 if(carColumn<=8&&cells[carRow][carColumn-1].isRight&&rightCount==direction[counter1])//right
						{
							
							 validDir.add("right");
							 foundMin=true;
						}
						 if (carRow<8&&cells[carRow][carColumn-1].isBottom&&bottomCount==direction[counter1])//bottom
						{
							
							 validDir.add("bottom");
							 foundMin=true;
							
						}
						 if(foundMin!=true)//if there are no valid directions for the give count number, go to next smallest count
						 {
							 counter1++;
						 }
						
					}
					randNumber=rand.nextInt(validDir.size());
				
					String choice=validDir.get(randNumber);
					
					if(choice.equals("left"))
					{
						System.out.println("moved left");
						car.column=Integer.toString(carColumn-1);
						leftCount++;
					}
					else if (choice.equals("top"))
					{
						System.out.println("moved top");
						car.row=reverseRow(Integer.toString(carRow-1));
						topCount++;
					}
					else if (choice.equals("right"))
					{
						System.out.println("moved right");
						car.column=Integer.toString(carColumn+1);
						rightCount++;
					}
					else if (choice.equals("bottom"))
					{
						System.out.println("moved bottom");
						car.row=reverseRow(Integer.toString(carRow+1));
						bottomCount++;
					}
					validDir.clear();
				
				
				}
				
			}
			
			counter++;
			
			if(counter==7)
			{
				counter=1;
			}
			try {
				 sleep((long)(clock*1000));
				 } catch (InterruptedException ie) {
				 System.out.println("interrupted");
				 return;
				 }
				dtm.setValueAt(car.row,Integer.parseInt(car.number)-1,2);
				dtm.setValueAt(car.column,Integer.parseInt(car.number)-1,1);
			
			gp.repaint();
		}
	}
	
	
}
	
public String reverseRow(String letter)
{
	if(letter.equals("0"))
	{
		return("A");
	}
	else if(letter.equals("1"))
	{
		return("B");
	}
	else if(letter.equals("2"))
	{
		return("C");
	}
	else if(letter.equals("3"))
	{
		return("D");
	}
	else if(letter.equals("4"))
	{
		return("E");
	}
	else if(letter.equals("5"))
	{
		return("F");
	}
	else if(letter.equals("6"))
	{
		return("G");
	}
	else if(letter.equals("7"))
	{
		return("H");
	}
	else 
	{
		return("I");
	}
}
public String findRow(String letter)
{
	if(letter.equals("A"))
	{
		return("0");
	}
	else if(letter.equals("B"))
	{
		return("1");
	}
	else if(letter.equals("C"))
	{
		return("2");
	}
	else if(letter.equals("D"))
	{
		return("3");
	}
	else if(letter.equals("E"))
	{
		return("4");
	}
	else if(letter.equals("F"))
	{
		return("5");
	}
	else if(letter.equals("G"))
	{
		return("6");
	}
	else if(letter.equals("H"))
	{
		return("7");
	}
	else 
	{
		return("8");
	}
}
	
	public void sort(int[] array)
	{
		
	}
	
	
	
	
	protected Node getNode(String tagName, NodeList nodes) {
	    for ( int x = 0; x < nodes.getLength(); x++ ) {
	        Node node = nodes.item(x);
	        if (node.getNodeName().equalsIgnoreCase(tagName)) {
	            return node;
	        }
	    }
	 
	    return null;
	}
	
	
	
}