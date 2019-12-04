import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
public class GameOverPanel extends JPanel {
    JPanel[] panels;
    JButton[] buttons;
    JLabel[] labels;
    MainPanel.ReGameManager reGameManager;

    public GameOverPanel(MainPanel.ReGameManager reGameManager) {
        this.reGameManager = reGameManager;
        this.initializeFields();
        this.createGameOverPanel();
        this.configs();
        this.addComponentsToMainPanel();
    }
    public void initializeFields() {
        this.labels  = new JLabel [ 2 ];
        this.panels  = new JPanel [ 4 ];
        this.buttons = new JButton[ 2 ];
    }
    public void addComponentsToMainPanel() {
        this.panels[ 0 ].add( this.labels [ 0 ] );
        this.panels[ 1 ].add( this.labels [ 1 ] );
        this.panels[ 2 ].add( this.buttons[ 0 ] );
        this.panels[ 3 ].add( this.buttons[ 1 ] );
        for ( JPanel panel : panels ) {
            this.add(panel);
            this.revalidate();
        }
    }
    public void createGameOverPanel() {
        this.createPanels();
        this.createButtons();
        this.createLables();
    }
    public void configs() {
        this.mainPanelConfig();
        this.nestedPanelConfig();
        this.labelConfig();
        this.buttonsConfig();
    }
    public void createButtons() {
        for (int i = 0; i < 2; i++) {
            JButton button = new JButton();
            buttons[ i ] = button;
        }
    }
    public void createLables() {
        for (int i = 0; i < 2; i++) {
            JLabel lable = new JLabel();
            labels[ i ] = lable;
        }
    }
    public void createPanels() {
        for (int i = 0; i < 4; i++) {
            JPanel panel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
            this.panels[ i ] = panel;
        }
    }
    public void mainPanelConfig() {
        this.setVisible(true);
        this.setBackground( Color.BLACK );
        this.setLayout( new GridLayout( 4, 0 ) );
    }
    public void buttonsConfig() {
        for (int i = 0; i < 2; i++) {
            this.buttons[ i ].setVisible(true);
            this.buttons[ i ].setForeground( Color.RED );
            if ( i == 0 ) {
                this.buttons[ i ].setText("RESTART GAME");
                this.buttons[ i ].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        reGameManager.reGame();
                    }
                });
                this.buttons[ i ].addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        super.keyPressed(e);
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            reGameManager.reGame();
                        }
                    }
                });
            } else {
                this.buttons[ i ].setText("EXIT");
                this.buttons[ i ].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });
            }
        }
    }
    public void labelConfig() {
        for (int i = 0; i < 2; i++) {
            if (i == 0) this.labels[ i ].setText("GAME OVER");
            else this.labels[ i ].setText( "Snake Length: " + reGameManager.getSnakeLength() );
            this.labels[ i ].setVisible(true);
            this.labels[ i ].setHorizontalAlignment(JLabel.CENTER);
            this.labels[ i ].setForeground( Color.WHITE );
        }
    }
    public void nestedPanelConfig() {
        for (int i = 0; i < 4; i++) {
            this.panels[ i ].setVisible(true);
            this.panels[ i ].setBackground( Color.BLACK);
            this.panels[ i ].setLayout( new GridLayout( 1, 0 ) );
        }
    }

}
