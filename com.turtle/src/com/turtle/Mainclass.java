package com.turtle;

import javax.swing.JFrame;

public class Mainclass {
    public static void main(String[] args) {
        // Create and set up the main frame
        JFrame mainFrame = new JFrame("Turtle Graphics Application");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create the turtle graphics application
        TurtleGraphics turtlegraphics = new TurtleGraphics();
        
        // Add the turtle to the frame
        mainFrame.add(turtlegraphics);
        
        // Configure and show the frame`
        mainFrame.pack();
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null); // Center the window
        mainFrame.setVisible(true);
        
        
        
        // Focus the turtle component for immediate input
        turtlegraphics.requestFocusInWindow();
    }
}
