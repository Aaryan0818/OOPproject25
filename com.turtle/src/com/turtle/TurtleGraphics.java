package com.turtle; 

import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import uk.ac.leedsbeckett.oop.LBUGraphics;

public class TurtleGraphics extends LBUGraphics {

    private List<String> commandHistory = new ArrayList<>(); //  Store all commands
    private boolean isDirty = false; // Flag to track unsaved changes
    private boolean isUserCommand = false;
   
    public static void main(String[] args) {
        TurtleGraphics app = new TurtleGraphics();
        System.out.println("Please enter a command:");
        Scanner scan = new Scanner(System.in);
        boolean state = true;
        while (state) {
            String input = scan.nextLine();
            if (input.equalsIgnoreCase("about")) {
                app.about();
            } else if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting program..");
                state = false;
                break;
            } else {
                System.out.println(input + " : Command not found!");
            }
        }
    }

    public TurtleGraphics() {
        // Set up the main frame
        JFrame mainFrame = new JFrame("Turtle Graphics Application");
        this.setPreferredSize(new Dimension(800, 600));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.add(this);
        mainFrame.pack();
        mainFrame.setVisible(true);

        // Add menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu1 = new JMenu("File");
        JMenu fileMenu2 = new JMenu("Help");
        JMenu fileMenu3 = new JMenu("Window");

        //File
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem loadItem = new JMenuItem("Load");
        JMenuItem saveCommandsItem = new JMenuItem("Save Commands");
        JMenuItem loadCommandsItem = new JMenuItem("Load Commands");
        
        //Help
        JMenuItem infoItem = new JMenuItem("info");
        JMenuItem commandsItem = new JMenuItem("commands");
        
        //Window
        JMenuItem editItem = new JMenuItem("edit");
        
        saveItem.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Image");
            int choice = chooser.showSaveDialog(mainFrame);
            if (choice == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                saveImage(file.getAbsolutePath());
            }
        });

        saveCommandsItem.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Commands as Text");
            int choice = chooser.showSaveDialog(mainFrame);
            if (choice == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                saveCommandsToFile(file.getAbsolutePath().replaceAll("\\.txt$", ""));
            }
        });

        loadCommandsItem.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Load Commands");
            int choice = chooser.showOpenDialog(mainFrame);
            if (choice == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                loadCommandsFromFile(file.getAbsolutePath());
            }
        });

        loadItem.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Load Image");
            int choice = chooser.showOpenDialog(mainFrame);
            if (choice == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                loadImage(file.getAbsolutePath());
            }
        });
        infoItem.addActionListener(e -> {
            String message = "<html><div style='text-align: center;'>"
                    + "<h2>TURTLE GRAPHICS APPLICATION</h2>"
                    + "<p>Version 1.0</p>"
                    + "<p>Created by: Aaryan Shah</p>"  // Replace with actual name
                    + "<p>Contact: saaryan23@tbc.edu.np</p>"  // Replace with actual email
                    + "<p>© 2024 All rights reserved</p>"
                    + "</div></html>";
            
            JOptionPane.showMessageDialog(mainFrame, 
                                         message, 
                                         "Application Info", 
                                         JOptionPane.INFORMATION_MESSAGE);
        });
        
        commandsItem.addActionListener(e -> showCommandsHelpDialog());

        fileMenu1.add(saveItem);
        fileMenu1.add(loadItem);
        fileMenu1.add(saveCommandsItem);
        fileMenu1.add(loadCommandsItem);
        fileMenu2.add(infoItem);
        fileMenu2.add(commandsItem);
        fileMenu3.add(editItem);
        menuBar.add(fileMenu1);
        menuBar.add(fileMenu2);
        menuBar.add(fileMenu3);
        mainFrame.setJMenuBar(menuBar);

        // Initialize turtle (but don't call about yet)
        reset(); // Reset turtle to default position
        setPenColour(Color.red);
        setStroke(2);
        setPenState(true);
        displayMessage("Welcome to Turtle Graphics! Type 'help' for commands.");
    }
    private boolean isPositionValid(int x, int y) {
    	return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }
    // Check if a movement distance is reasonable
    private boolean isDistanceValid(int distance) {
       return Math.abs(distance) <= 1000; // Arbitrary large but reasonable limit
   }
    
   // Check if an angle is valid
  private boolean isAngleValid(int angle) {
      return angle >= -360 && angle <= 360; // Allow full circle rotations
  }
  
  private void showCommandsHelpDialog() {
      StringBuilder helpText = new StringBuilder();
      helpText.append("<html><div style='width: 500px; padding: 10px;'>");
      helpText.append("<h1 style='text-align: center; color: #2c3e50;'>Turtle Graphics Commands</h1>");
      
      // Movement Section
      helpText.append("<h2 style='color: #2980b9;'>Movement</h2>");
      helpText.append("<ul>");
      helpText.append("<li><b>forward &lt;n&gt;</b> or <b>fd &lt;n&gt;</b> - Move forward n pixels</li>");
      helpText.append("<li><b>backward &lt;n&gt;</b> or <b>bk &lt;n&gt;</b> - Move backward n pixels</li>");
      helpText.append("<li><b>right &lt;n&gt;</b> or <b>rt &lt;n&gt;</b> - Turn right n degrees</li>");
      helpText.append("<li><b>left &lt;n&gt;</b> or <b>lt &lt;n&gt;</b> - Turn left n degrees</li>");
      helpText.append("</ul>");
      
      // Drawing Control Section
      helpText.append("<h2 style='color: #2980b9;'>Drawing Control</h2>");
      helpText.append("<ul>");
      helpText.append("<li><b>penup</b> or <b>pu</b> - Lift pen (stop drawing)</li>");
      helpText.append("<li><b>pendown</b> or <b>pd</b> - Lower pen (start drawing)</li>");
      helpText.append("<li><b>setpencolor &lt;color&gt;</b> - Set pen color (e.g., red, #RRGGBB)</li>");
      helpText.append("<li><b>clear</b> - Clear the canvas</li>");
      helpText.append("<li><b>reset</b> - Reset turtle position and direction</li>");
      helpText.append("</ul>");
      
      // Shapes Section
      helpText.append("<h2 style='color: #2980b9;'>Shapes</h2>");
      helpText.append("<ul>");
      helpText.append("<li><b>circle &lt;radius&gt;</b> - Draw a circle with specified radius</li>");
      helpText.append("<li><b>square &lt;size&gt;</b> - Draw a square with specified side length</li>");
      helpText.append("<li><b>polygon &lt;sides&gt; &lt;size&gt;</b> - Draw a regular polygon</li>");
      helpText.append("<li><b>triangle &lt;side1&gt; &lt;side2&gt; &lt;side3&gt;</b> - Draw a triangle with specified sides</li>");
      helpText.append("<li><b>spiral</b> - Draw a colorful spiral pattern</li>");
      helpText.append("<li><b>about</b> - Draw creater of this application's name</li>");
      helpText.append("</ul>");
      
      // File Operations Section
      helpText.append("<h2 style='color: #2980b9;'>File Operations</h2>");
      helpText.append("<ul>");
      helpText.append("<li><b>save &lt;filename&gt;</b> - Save drawing as PNG image</li>");
      helpText.append("<li><b>load &lt;filename&gt;</b> - Load drawing from PNG image</li>");
      helpText.append("<li><b>savecommands &lt;filename&gt;</b> - Save command history to text file</li>");
      helpText.append("<li><b>loadcommands &lt;filename&gt;</b> - Load and execute commands from text file</li>");
      helpText.append("</ul>");
      
      // Settings Section
      helpText.append("<h2 style='color: #2980b9;'>Settings</h2>");
      helpText.append("<ul>");
      helpText.append("<li><b>setspeed &lt;1-10&gt;</b> - Set turtle animation speed</li>");
      helpText.append("<li><b>penwidth &lt;width&gt;</b> - Set pen stroke width in pixels</li>");
      helpText.append("<li><b>pen &lt;red&gt; &lt;green&gt; &lt;blue&gt;</b> - Set pen color using RGB values (0-255)</li>");
      helpText.append("</ul>");
      
      // Bounds and Limits Section
      helpText.append("<h2 style='color: #2980b9;'>Bounds and Limits</h2>");
      helpText.append("<ul>");
      helpText.append("<li>Movement distance limited to 1000 pixels</li>");
      helpText.append("<li>Angles limited to -360 to 360 degrees</li>");
      helpText.append("<li>Shape sizes limited to 500 pixels</li>");
      helpText.append("<li>Turtle cannot move off screen</li>");
      helpText.append("</ul>");
      
      helpText.append("</div></html>");

      JLabel label = new JLabel(helpText.toString());
      JScrollPane scrollPane = new JScrollPane(label);
      scrollPane.setPreferredSize(new Dimension(650, 500));
      JOptionPane.showMessageDialog(null, scrollPane, "Commands Help", JOptionPane.INFORMATION_MESSAGE);
  }

    @Override
    public void processCommand(String command) {
        commandHistory.add(command);
        System.out.println( command); 
        try {
            String[] parts = command.trim().split("\\s+");
            if (parts.length == 0 || parts[0].isEmpty()) return;

            String cmd = parts[0].toLowerCase();

            switch (cmd) {
                case "about":
                    isUserCommand = true; // Set flag before calling about()
                    about();
                    break;
                case "move":
                case "mv":
                    if (parts.length == 2) {
                        try {
                            int distance = Integer.parseInt(parts[1]);
                            if (!isDistanceValid(distance)) {
                                displayMessage("Distance too large (max 1000 pixels)");
                                break;
                            }
                            
                            // NEW: Get current position first
                            int currentX = getxPos();
                            int currentY = getyPos();
                            
                            // Calculate new position
                            int newX = currentX + (int)(distance * Math.sin(Math.toRadians(getDirection())));
                            int newY = currentY - (int)(distance * Math.cos(Math.toRadians(getDirection())));
                            
                            // NEW: Show position in error message
                            if (!isPositionValid(newX, newY)) {
                                displayMessage(String.format("Cannot move to [%d, %d] - off screen", newX, newY));
                                break;
                            }
                            forward(distance);
                            isDirty = true;
                        } catch (NumberFormatException e) {
                            displayMessage("Invalid distance - must be integer");
                        }
                    } else {
                    displayMessage("Syntax: move <distance>");
                }
                break;

                case "reverse":
                case "rv":
                    if (parts.length == 2) {
                        forward(-Integer.parseInt(parts[1]));
                        isDirty = true;
                    } else {
                        displayMessage("Syntax: reverse <distance>");
                    }
                    break;

                case "right":
                case "rt":
                	if (parts.length == 2) {
                        try {
                            int angle = Integer.parseInt(parts[1]);
                            if (!isAngleValid(angle)) {
                                displayMessage("Angle must be between -360 and 360 degrees");
                                break;
                            }
                            right(angle);
                        } catch (NumberFormatException e) {
                            displayMessage("Invalid angle - must be integer");
                        }
                    } else {
                        right(90);
                    }
                    break;

                case "left":
                case "lt":
                    if (parts.length == 2) {
                        left(Integer.parseInt(parts[1]));
                    } else {
                        left(90);
                    }
                    break;

                case "penup":
                case "pu":
                    setPenState(false);
                    displayMessage("Pen lifted");
                    break;

                case "pendown":
                case "pd":
                    setPenState(true);
                    displayMessage("Pen lowered");
                    break;

                case "setpencolor":
                    if (parts.length == 2) {
                        setPenColor(parts[1]);
                    } else {
                        displayMessage("Syntax: setpencolor <color>");
                    }
                    break;

                case "clear":
                    if (isDirty) {
                        int result = JOptionPane.showConfirmDialog(null,
                            "You have unsaved changes. Do you really want to clear the canvas?",
                            "Unsaved Changes",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                        if (result != JOptionPane.YES_OPTION) {
                            displayMessage("Clear cancelled");
                            break;
                        }
                    }
                    clear();
                    isDirty = false;
                    displayMessage("Canvas cleared");
                    break;


                case "reset":
                    reset();
                    displayMessage("Turtle reset");
                    break;
                    
                case "triangle":
                	if(parts.length ==2) {
                		int side = Integer.parseInt(parts[1]);
                		triangle(side, side, side);
                		
                	}else if (parts.length ==4) {
                	int side1 = Integer.parseInt(parts[1]);
                	int side2 = Integer.parseInt(parts[2]);
                	int side3 = Integer.parseInt(parts[3]);
                	triangle(side1, side2, side3);
                	} else {
                		displayMessage("Invalid command! 'triangle' command expects 1 or 3 parameters.");
                	}
                	break;

                case "circle":
                	if (parts.length == 2) {
                        try {
                            int radius = Integer.parseInt(parts[1]);
                            if (radius <= 0) {
                                displayMessage("Radius must be positive");
                                break;
                            }
                            if (radius > 500) {
                                displayMessage("Radius too large (max 500 pixels)");
                                break;
                            }
                            circle(radius);
                            isDirty = true;
                        } catch (NumberFormatException e) {
                            displayMessage("Invalid radius - must be integer");
                        }
                    } else {
                        displayMessage("Syntax: circle <radius>");
                    }
                    break;

                case "square":
                	if (parts.length == 2) {
                        try {
                            int size = Integer.parseInt(parts[1]);
                            if (size <= 0) {
                                displayMessage("Size must be positive");
                                break;
                            }
                            if (size > 500) {
                                displayMessage("Size too large (max 500 pixels)");
                                break;
                            }
                            drawSquare(size);
                            isDirty = true;
                        } catch (NumberFormatException e) {
                            displayMessage("Invalid size - must be integer");
                        }
                    } else {
                        displayMessage("Syntax: square <size>");
                    }
                    break;

                case "polygon":
                    if (parts.length == 3) {
                        drawPolygon(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                        isDirty = true;
                    } else {
                        displayMessage("Syntax: polygon <sides> <size>");
                    }
                    break;

                case "save":
                    if (parts.length == 2) {
                        saveImage(parts[1]);
                    } else {
                        displayMessage("Syntax: save <filename>");
                    }
                    break;

                case "load":
                    if (parts.length == 2) {
                        loadImage(parts[1]);
                    } else {
                        displayMessage("Syntax: load <filename>");
                    }
                    break;

                case "setspeed":
                    if (parts.length == 2) {
                        setTurtleSpeed(Integer.parseInt(parts[1]));
                        displayMessage("Speed set to " + parts[1]);
                    } else {
                        displayMessage("Syntax: setspeed <1-10>");
                    }
                    break;
                    
                case "savecommands":
                    if (parts.length == 2) {
                        saveCommandsToFile(parts[1]);
                    } else {
                        displayMessage("Syntax: savecommands <filename>");
                    }
                    break;
                    
                 case "pen":
                    if (parts.length == 4) {
                        setPenRGB(parts[1], parts[2], parts[3]);
                    } else {
                        displayMessage("Syntax: pen <red> <green> <blue> (values 0-255)");
                    }
                    break;
                    
                 case "penwidth":
                	 if(parts.length==2) {
                		 int width = Integer.parseInt(parts[1]);
                		 if (width>=0) {
                			 setStroke(width);
                			 displayMessage("pen Width is set to "+ width + "px'");
                		 } else {
                			 displayMessage("Invalid input! pen width cannot be negative.");
                		 }
                		 } else {
                			 displayMessage("Invalid command: 'penwidth' command expects 1 parameter.");		 
                			 
                	}
                	 break;
                 case "spiral":
                	 spiral();
                	 displayMessage("Spiral Created");
                	 break;
                	 		 
               default:
                    displayMessage("Unknown command: " + cmd);
            }
        } catch (NumberFormatException e) {
            displayMessage("Please enter a valid number");
        } catch (ArrayIndexOutOfBoundsException e) {
            displayMessage("Missing parameter for command");
        } catch (Exception e) {
            displayMessage("Error: " + e.getMessage());
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Reset position validation when window size changes
        resetPositionIfNeeded();
    }

    private void resetPositionIfNeeded() {
        int x = getxPos();
        int y = getyPos();
        if (!isPositionValid(x, y)) {
            reset();
            displayMessage("Window resized - turtle reset to center");
        }
    }
    private void setPenRGB(String redStr, String greenStr, String blueStr) {
        try {
            int red = Integer.parseInt(redStr);
            int green = Integer.parseInt(greenStr);
            int blue = Integer.parseInt(blueStr);
            
            // Validate each component is between 0-255
            if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
                throw new IllegalArgumentException("RGB values must be between 0-255");
            }
            
            Color color = new Color(red, green, blue);
            setPenColour(color);
            displayMessage("Pen color set to RGB(" + red + ", " + green + ", " + blue + ")");
        } catch (NumberFormatException e) {
            displayMessage("Invalid RGB values - must be numbers between 0-255");
        } catch (IllegalArgumentException e) {
            displayMessage(e.getMessage());
        }
    }

    private void setPenColor(String colorStr) {
        try {
            Color color;
            if (colorStr.startsWith("#") && colorStr.length() == 7) {
                color = Color.decode(colorStr);
            } else {
                switch(colorStr.toLowerCase()) {
                    case "red": color = Color.RED; break;
                    case "green": color = Color.GREEN; break;
                    case "blue": color = Color.BLUE; break;
                    case "black": color = Color.BLACK; break;
                    case "white": color = Color.WHITE; break;
                    case "yellow": color = Color.YELLOW; break;
                    case "cyan": color = Color.CYAN; break;
                    case "magenta": color = Color.MAGENTA; break;
                    default: throw new IllegalArgumentException("Unknown color");
                }
            }
            setPenColour(color);
            displayMessage("Pen color set to " + colorStr);
        } catch (Exception e) {
            displayMessage("Invalid color: " + colorStr + ". Try: red, green, blue, etc. or #RRGGBB");
        }
    }

    private void drawSquare(int side) {
    	reset();
    	setStroke(8);
    	setPenState(true);
        for (int i = 0; i < 4; i++) {
            forward(side);
            right(90);
        }
        setPenState(false);
        for (int i = 0; i < 4; i++) {
            forward(side);
            right(90);
    }
        reset();
        }
    
    public void triangle(int side1, int side2, int side3) {
    	reset();
    	setStroke(2);// pen width set to 2
    	
    	// Calculate the cosine of angles B and C using the law of Cosines
    	double cosB = (side1 * side1 + side3 * side3 - side2 * side2)/(2.0 * side1 * side3);
    	double cosC = (side1 * side1 + side2 * side2 - side3 * side3)/(2.0 * side1 * side2);
    	
    	//convert the cosins values to angles in degress
    	double angleB = Math.toDegrees(Math.acos(cosB));
    	double angleC = Math.toDegrees(Math.acos(cosC));
    	
    	/*setPenState(false);
    	forward(150);
    	left(90);*/
    	
    	//equilateral triangle
    	right(90);
    	setPenState(true);
    	forward(side3);
    	right((int) Math.round(180-angleB));
    	forward(side1);
    	right((int) Math.round(180-angleC));
    	forward(side2);
    	setPenState(false);
    	reset();
    }
  	
    private void drawPolygon(int sides, int size) {
        if (sides < 3) {
            displayMessage("Polygon must have at least 3 sides");
            return;
        }
        setPenState(true);
        int angle = 360 / sides;
        for (int i = 0; i < sides; i++) {
            forward(size);
            right(angle);
        }
    }
    
    private void saveImage(String filename) {
        try {
            BufferedImage image = getBufferedImage();
            ImageIO.write(image, "PNG", new File(filename + ".png"));
            isDirty = false; // Reset flag after successful save
            displayMessage("Drawing saved as " + filename + ".png");
        } catch (Exception e) {
            displayMessage("Error saving: " + e.getMessage());
        }
    }

    
    private void saveCommandsToFile(String filename) {
        try (FileWriter writer = new FileWriter(filename + ".txt")) {
            for (String cmd : commandHistory) {
                writer.write(cmd + "\n");
            }
            displayMessage("Commands saved to " + filename + ".txt");
        } catch (IOException e) {
            displayMessage("Failed to save commands: " + e.getMessage());
        }
    }

    private void loadImage(String filename) {
        try {
            BufferedImage image = ImageIO.read(new File(filename));
            setBufferedImage(image);
            displayMessage("Drawing loaded from " + filename);
        } catch (Exception e) {
            displayMessage("Error loading: " + e.getMessage());
        }
    }

    private void loadCommandsFromFile(String filename) {
        try (Scanner scanner = new Scanner(new FileReader(filename))) {
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine().trim();
                if (!command.isEmpty()) {
                    processCommand(command);
                }
            }
            displayMessage("Commands loaded and executed from " + filename);
        } catch (IOException e) {
            displayMessage("Error loading commands: " + e.getMessage());
        }
    }
  
    @Override
    public void about() {
        if (isUserCommand) {
            // User typed "about" → Draw AARYAN only
            ImageAaryan();
        } else {
            // App startup → Show default OOP animation
            super.about();
        }
        isUserCommand = false; // Reset for next call
    }
    
    public void ImageAaryan() {
    	reset();
    	
    	setTurtleSpeed(1); // Max speed
        setStroke(8);
    	reset(); // Reset position and angle
        setPenState(false);
        setPenColour(Color.CYAN);
        setStroke(3);
        //letter A
     
        right(90); 
        forward(250);
        right(90);
        
        setPenState(true);
        forward(100);
        right(90);
        forward(50);
        right(90);
        forward(100);
        forward(-50);
        right(90);
        forward(50);
        setPenState(false);
        
        //letter A
        setPenState(false);
        left(90);
        forward(50);
        left(90);
        forward(60);
        left(90);
        
        setPenState(true);
        setPenColour(Color.BLUE);
        forward(100);
        right(90);
        forward(50);
        right(90);
        forward(100);
        forward(-50);
        right(90);
        forward(50);
        setPenState(false);
        
        //letter R
        setPenState(false);
        left(90);
        forward(50);
        left(90);
        forward(60);
        left(90);
        
        setPenState(true);
        setPenColour(Color.RED);
        forward(100);
        right(90);
        forward(25);
        right(20);
        forward(10);
        right(20);
        forward(10);
        right(20);
        forward(10);
        right(20);
        forward(10);
        right(20);
        forward(10);
        right(20);
        forward(10);
        right(20);
        forward(10);
        right(20);
        forward(10);
        right(20);
        forward(10);
        right(20);
        forward(10);
        left(160);
        forward(82);
        setPenState(false); 
        
        //letter Y
        setPenState(false);
        left(40);
        forward(20);
        left(90); 
        
        setPenState(true);
        setPenColour(Color.GREEN);
        forward(50);
        left(30);
        forward(55);
        forward(-55);
        right(60);
        forward(55);
        setPenState(false);
        
        
        //letter A
        setPenState(false);
        right(150);
        forward(100);
        left(90);
        forward(10);
        left(90);
        
        
        setPenState(true);
        setPenColour(Color.YELLOW);
        forward(100);
        right(90);
        forward(50);
        right(90);
        forward(100);
        forward(-50);
        right(90);
        forward(50);
        setPenState(false);
        
        //letter N
        setPenState(false);
        left(90);
        forward(50);
        left(90);
        forward(60);
        left(90);
        
        setPenState(true);
        setPenColour(Color.WHITE);
        forward(100);
        right(150);
        forward(115);
        left(150);
        forward(100);
        reset();
        setPenState(false);
        displayMessage("Turtle Graphics Application created by Aaryan Shah");
       
        }
    public void spiral() {
    	reset();
    	setPenState(true);
    	setTurtleSpeed(1);
    	
    	int length = 5;
    	int angle = 91;
    	int steps = 120;
    	int colorChange = 5;
    	
        for (int i = 0; i < steps; i++) {
            // Create a rainbow color effect
            int red = (int)(Math.sin(0.1 * i + 0) * 127 + 128);
            int green = (int)(Math.sin(0.1 * i + 2) * 127 + 128);
            int blue = (int)(Math.sin(0.1 * i + 4) * 127 + 128);
            
            setPenColour(new Color(red, green, blue));
            forward(length);
            right(angle);
            
            length += 1; // Increase length slightly each step
        }
        reset();
        }
}
