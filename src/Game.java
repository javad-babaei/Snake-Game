import javax.print.DocFlavor;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Game extends JFrame {
    MainPanel mainPanel;

    public Game() {
        this.gameConfig();
        this.mainPanel = new MainPanel();
        this.setContentPane( this.mainPanel );
        this.revalidate();
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Game.this.changeDirect( e.getKeyCode() );
            }
        });
    }
    public void gameConfig() {
        this.pack();
        this.setLayout( new GridLayout() );
        this.setVisible(true);
        this.setResizable(true);
        this.setSize(400, 400);
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
    public void changeDirect( int pressedKeyCode ) {
        this.mainPanel.inputDirect.add( Integer.valueOf( pressedKeyCode ) );
    }
}
