import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.MatteBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
public class MainPanel extends JPanel {
    /* properties */
    JFrame gameOverframe;
    List<JPanel> miniPanels;
    String accidantOnWhatWall;
    Queue<Integer> inputDirect = new LinkedList<>();
    Timer
        movement,
        foodBlinking;
    int
        snakeLength,
        snakeTailLength,
        foodCurrentIndex,
        lastIndexOfSnakeBody,
        nextIndexOfSnakeHead,
        snakeHeadDistanceFromWalls,
        indexOfCurrentSnakeHeadPositon;
    List<Integer>
        cornerCells = new ArrayList<>(),
        snakeBodyIndexs = new ArrayList<>(),
        indexesOfTopWallCells = new ArrayList<>(),
        indexesOfLeftWallCells = new ArrayList<>(),
        indexesOfRightWallCells = new ArrayList<>(),
        indexesOfBottomWallCells = new ArrayList<>(),
        prohibitedCellsForSnakeHead = new ArrayList<>();
    boolean
        gameOver,
        next_up_direct,
        accidentOnWall,
        next_down_direct,
        next_left_direct,
        next_right_direct,
        increaseSnakeLength;
    static final int
        H_GAP = 1,
        V_GAP = 1,
        ROWS = 35,
        COLUMNS = 0;

    /* Constructor */
    public MainPanel() {
        this.callPreparatoryMethods();
        this.movement.start();
        this.foodBlinking.start();
    }

    /* Methods */
    public void callPreparatoryMethods() {
        this.initializeFields();
        this.mainPanelConfig();
        this.createAndAddMiniPanelsToMainPanel();
        this.fillWallArrayLists();
        this.getProhibitedCellsForSnakeHead();
        this.initializeGame();
    }
    public void initializeFields() {
        this.snakeLength = 20;
        this.foodCurrentIndex = -1;
        this.accidantOnWhatWall = "";
        this.nextIndexOfSnakeHead = -1;
        this.lastIndexOfSnakeBody = -1;
        this.snakeHeadDistanceFromWalls = 5;
        this.indexOfCurrentSnakeHeadPositon = -1;
        this.snakeTailLength = this.snakeLength - 1;

        this.gameOver = false;
        this.accidentOnWall = false;
        this.next_up_direct = false;
        this.next_down_direct = false;
        this.next_left_direct = false;
        this.next_right_direct = false;
        this.increaseSnakeLength = false;

        this.movement = movementTimer();
        this.foodBlinking = foodBlinkingTimer();
    }
    public void mainPanelConfig() {
        this.setVisible( true );
        this.setBackground( Color.WHITE );
        this.setLayout( new GridLayout( ROWS, COLUMNS, H_GAP,V_GAP ) );
    }
    public void createAndAddMiniPanelsToMainPanel() {
        for ( int i = 0; i<1225; i++ ) {
            JPanel panel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
            panel.setVisible( false );
            panel.setBackground( Color.BLACK );

            JLabel label = new JLabel( Integer.toString( i ) );
            label.setLayout( new FlowLayout( FlowLayout.CENTER ) );
            label.setVisible( false );
            /* Uncomment 3 followed line for test environment */
//            panel.setVisible( true );
//            panel.setBackground( Color.GRAY );
//            label.setVisible( true );

            panel.add(label);
            this.add(panel);
            this.revalidate();
        }
    }
    public void fillWallArrayLists() {
        for ( int i = 1; i < 34; i++ ) {
            this.indexesOfTopWallCells.add( Integer.valueOf( i ) );
            this.indexesOfBottomWallCells.add( Integer.valueOf( i + 1190 ) );
        }

        for ( int i = 35; i < 1156; i += 35 ) {
            this.indexesOfLeftWallCells.add( Integer.valueOf( i ) );
            this.indexesOfRightWallCells.add( Integer.valueOf( i + 34) );
        }

        this.cornerCells.add( Integer.valueOf( 0 ) );
        this.cornerCells.add( Integer.valueOf( 34 ) );
        this.cornerCells.add( Integer.valueOf( 1190 ) );
        this.cornerCells.add( Integer.valueOf( 1224 ) );

    }
    public Timer movementTimer() {
        return
                new Timer(100, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        directProcessing();
                        if ( gameOver ) {
                            gameOverHandler();
                        }
                        refreshSnakeBodyIndexes();
                        getFoodCurrentIndex();
                        moveSnake();
                        repaint();
                    }
                });
    }
    public Timer foodBlinkingTimer() {
        return
                new Timer(99, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (foodCurrentIndex != -1) {
                            if ( getComponent( foodCurrentIndex ).isVisible() )
                                getComponent( foodCurrentIndex ).setVisible( false );
                            else
                                getComponent( foodCurrentIndex ).setVisible( true );
                        }
                    }
                });
    }
    public void gameOverHandler() {
        this.movement.stop();
        this.foodBlinking.stop();
        this.gameOverPanel();
    }
    public void gameOverPanel() {
        this.gameOverframe = new JFrame();
        this.gameOverframe.setVisible(true);
        this.gameOverframe.setResizable(false);
        this.gameOverframe.setSize(200, 200);
        this.gameOverframe.setLocationRelativeTo( this );
        this.gameOverframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.gameOverframe.setContentPane( new GameOverPanel( new ReGameManager() ) );
    }
    public void reGame() {
        this.gameOverframe.setVisible(false);
        for ( Component panel : this.getComponents() ) {
            panel.setVisible( false );
            panel.setBackground(Color.BLACK);
        }
        this.initializeFields();
        this.inputDirect.clear();
        this.movement.start();
        this.foodBlinking.start();
        this.initializeGame();
    }
    public class ReGameManager {
        public void reGame() {
            MainPanel.this.reGame();
        }
        public int getSnakeLength() {
            return snakeBodyIndexs.size();
        }
    }
    public void initializeGame() {
        this.indexOfCurrentSnakeHeadPositon = this.getAppropriateHeadIndex();
        this.snakeBodyIndexs.clear();
        for ( Integer index : getSnakeTail() ) {
            this.snakeBodyIndexs.add( index );
        }
        this.gameOver = false;
        this.getFoodCurrentIndex();
    }
    public int getAppropriateHeadIndex() {
        int tempHeadIndex;
        whileLoop:
        while ( true ) {
            tempHeadIndex = ThreadLocalRandom.current().nextInt( 1225 );
            for ( Integer index : this.prohibitedCellsForSnakeHead ) {
                if ( index.intValue() == tempHeadIndex ) {
                    continue whileLoop;
                }
            }
            return tempHeadIndex;
        }
    }
    public void getProhibitedCellsForSnakeHead() {
        int rowsCount = ROWS;
        int columnsCount = this.getComponents().length / ROWS;
        for ( int i = 0; i < this.snakeHeadDistanceFromWalls; i++ ) {
            for ( int f = i * columnsCount; f < ( i * columnsCount ) + columnsCount; f++ ) {
                this.prohibitedCellsForSnakeHead.add( Integer.valueOf( f ) );
            }
            for ( int k = ( rowsCount - this.snakeHeadDistanceFromWalls + i ) * columnsCount; k < ( ( rowsCount - this.snakeHeadDistanceFromWalls + i) * columnsCount ) + columnsCount; k++ ) {
                this.prohibitedCellsForSnakeHead.add( Integer.valueOf( k ) );
            }
            for ( int p = i; p < ( columnsCount * rowsCount ); p += 35 ) {
                this.prohibitedCellsForSnakeHead.add( Integer.valueOf( p ) );
            }
            for ( int o = ( columnsCount - this.snakeHeadDistanceFromWalls + i ); o < ( columnsCount * ( rowsCount - 1 ) ) + rowsCount; o += 35 ) {
                this.prohibitedCellsForSnakeHead.add( Integer.valueOf( o ) );
            }
        }
    }
    public List<Integer> getSnakeTail() {
        List<Integer> snakeBody = new ArrayList<>();
        snakeBody.add( 0, Integer.valueOf( this.indexOfCurrentSnakeHeadPositon ) );
        List<String> directs = new ArrayList( Arrays.asList("down", "up", "left", "right") );
        Collections.shuffle(directs);
        directs.remove(2);
        int i = 1;
        whileLoop:
        while ( i < this.snakeLength ) {
            List<Integer> allNextIndexes = new ArrayList();
            Collections.shuffle(directs);
            boolean flag = true;
            forLoop:
            for ( String direct : directs ) {
                int nextIndex = getNextIndex( snakeBody.get( i - 1 ).intValue(), direct );
                if (
                        nextIndex != -1
                                &&
                        !this.integerListContainsValue( Integer.valueOf( nextIndex ), snakeBody )
                )
                {
                    snakeBody.add( i, nextIndex );
                    flag = false;
                    break forLoop;
                }
            }
            if (flag) {
                i = 1;
                snakeBody.clear();
                snakeBody.add( 0, Integer.valueOf( this.indexOfCurrentSnakeHeadPositon ) );
                continue whileLoop;
            }
            i++;
        }
        return snakeBody;
    }
    public List<String> getShuffledDirectsList() {
        List<String> directs = new ArrayList( Arrays.asList("down", "up", "left", "right") );
        Collections.shuffle(directs);
        return directs;
    }
    public boolean integerListContainsValue ( Integer needle, List<Integer> haystack ) {
        for ( Integer element : haystack ) {
            if ( element.intValue() == needle.intValue() ) {
                return true;
            }
        }
        return false;
    }
    public void refreshSnakeBodyIndexes() {
        if ( this.next_up_direct ) {
            this.updateSnakeBodyIndexesToDirect( "up" );
        } else if ( this.next_down_direct ) {
            this.updateSnakeBodyIndexesToDirect( "down" );
        } else if ( this.next_left_direct ) {
            this.updateSnakeBodyIndexesToDirect( "left" );
        } else if ( this.next_right_direct ) {
            this.updateSnakeBodyIndexesToDirect( "right" );
        }
    }
    public void updateSnakeBodyIndexesToDirect( String direct ) {
        List<Integer> tempNewSnakeBodyIndexes = new ArrayList<>();
        int currentIndexOfSnakeHead = this.snakeBodyIndexs.get(0).intValue();
        for ( int i = 0; i < this.snakeBodyIndexs.size(); i++ ) {
            if (i == 0) {
                if ( this.getNextIndex( currentIndexOfSnakeHead, direct ) != -1 ) {
                    this.nextIndexOfSnakeHead = this.getNextIndex( currentIndexOfSnakeHead, direct );
                    tempNewSnakeBodyIndexes.add( 0, Integer.valueOf( this.nextIndexOfSnakeHead ) );
                } else {
                    return;
                }
            } else {
                tempNewSnakeBodyIndexes.add( i, this.snakeBodyIndexs.get( i - 1 ) );
            }
        }
        this.checkFooding();
        if ( this.increaseSnakeLength ) {
            tempNewSnakeBodyIndexes.add( Integer.valueOf( this.lastIndexOfSnakeBody ) );
            this.increaseSnakeLength = false;
        }
        this.snakeBodyIndexs.clear();
        for ( int newIndex : tempNewSnakeBodyIndexes ) {
            this.snakeBodyIndexs.add( newIndex );
        }
        this.lastIndexOfSnakeBody = this.snakeBodyIndexs.get(this.snakeBodyIndexs.size() - 1).intValue();
    }
    public void checkFooding() {
        if ( this.nextIndexOfSnakeHead == this.foodCurrentIndex ) {
            this.increaseSnakeLength = true;
        }
    }
    public void getFoodCurrentIndex() {
        int newFoodIndex;
        whileLoop:
        while ( true ) {
            newFoodIndex = ThreadLocalRandom.current().nextInt(1225);
            for ( Integer index : this.snakeBodyIndexs ) {
                if (index.intValue() == newFoodIndex) {
                    continue whileLoop;
                }
            }
            break whileLoop;
        }
        if ( this.foodCurrentIndex == -1 ) {
            this.getComponent( newFoodIndex ).setVisible( true );
            this.getComponent( newFoodIndex ).setBackground( Color.BLUE );
            this.revalidate();
            this.foodCurrentIndex = newFoodIndex;
        } else if (this.nextIndexOfSnakeHead == this.foodCurrentIndex) {
            this.getComponent( this.foodCurrentIndex ).setVisible( false );
            this.getComponent( this.foodCurrentIndex ).setBackground( Color.BLACK );
            this.revalidate();

            this.getComponent( newFoodIndex ).setVisible( true );
            this.getComponent( newFoodIndex ).setBackground( Color.BLUE );
            this.revalidate();
            this.foodCurrentIndex = newFoodIndex;
        }
    }
    public int getNextIndex( int currentIndex, String direct ) {
        int nextIndex = -1;
        boolean allowedMovement = this.nextMovementValidation( currentIndex, direct );
        if ( allowedMovement ) {
            switch ( direct ) {
                case "up"   :
                    nextIndex = currentIndex - 35; break;
                case "down" :
                    nextIndex = currentIndex + 35; break;
                case "left" :
                    nextIndex = currentIndex - 1;  break;
                case "right":
                    nextIndex = currentIndex + 1;  break;
            }
        }
        if ( this.snakeBodyIndexs.contains( Integer.valueOf( nextIndex ) ) ) {
            this.gameOver = true;
            this.accidantOnWhatWall = direct;
            nextIndex = -1;
        }
        return nextIndex;
    }
    public boolean nextMovementValidation( int currentIndex, String direct ) {
        boolean nextMovementIsValid = true;
         if ( this.indexesOfTopWallCells.contains( Integer.valueOf( currentIndex ) ) ) {
            if (direct.equalsIgnoreCase("up")) {
                nextMovementIsValid = false;
                this.accidentOnWall = true;
                this.gameOver = true;
                this.accidantOnWhatWall = direct;
            }
        } else if (  this.indexesOfBottomWallCells.contains( Integer.valueOf( currentIndex ) ) ) {
            if (direct.equalsIgnoreCase("down")) {
                nextMovementIsValid = false;
                this.accidentOnWall = true;
                this.gameOver = true;
                this.accidantOnWhatWall = direct;
            }
        } else if (  this.indexesOfLeftWallCells.contains( Integer.valueOf( currentIndex ) ) ) {
            if (direct.equalsIgnoreCase("left")) {
                nextMovementIsValid = false;
                this.accidentOnWall = true;
                this.gameOver = true;
                this.accidantOnWhatWall = direct;
            }
        } else if (  this.indexesOfRightWallCells.contains( Integer.valueOf( currentIndex ) ) ) {
            if (direct.equalsIgnoreCase("right")) {
                nextMovementIsValid = false;
                this.accidentOnWall = true;
                this.gameOver = true;
                this.accidantOnWhatWall = direct;
            }
        } else if (  cornerCells.contains( Integer.valueOf( currentIndex ) ) ) {
            if ( currentIndex == 0 ) {
                if ( direct.equalsIgnoreCase("up") || direct.equalsIgnoreCase("left") ) {
                    nextMovementIsValid = false;
                    this.accidentOnWall = true;
                    this.gameOver = true;
                    this.accidantOnWhatWall = direct;
                }
            } else if ( currentIndex == 34 ) {
                if ( direct.equalsIgnoreCase("up") || direct.equalsIgnoreCase("right") ) {
                    nextMovementIsValid = false;
                    this.accidentOnWall = true;
                    this.gameOver = true;
                    this.accidantOnWhatWall = direct;
                }
            } else if ( currentIndex == 1190 ) {
                if ( direct.equalsIgnoreCase("down") || direct.equalsIgnoreCase("left") ) {
                    nextMovementIsValid = false;
                    this.accidentOnWall = true;
                    this.gameOver = true;
                    this.accidantOnWhatWall = direct;
                }
            } else if ( currentIndex == 1224 ) {
                if ( direct.equalsIgnoreCase("down") || direct.equalsIgnoreCase("right") ) {
                    nextMovementIsValid = false;
                    this.accidentOnWall = true;
                    this.gameOver = true;
                    this.accidantOnWhatWall = direct;
                }
            }
        }
        return nextMovementIsValid;
    }
    public void moveSnake() {
        for ( Component panel : this.getComponents() ) {
            if ( !panel.equals( this.getComponent( this.foodCurrentIndex ) ) ) {
                panel.setVisible( false );
                panel.setBackground( Color.BLACK );
            }
        }
        for ( int i = 0; i < this.snakeBodyIndexs.size(); i++ ) {
            this.getComponent( this.snakeBodyIndexs.get(i).intValue() ).setVisible( true );
            if ( i == 0 ) {
                this.getComponent( this.snakeBodyIndexs.get(i).intValue() ).setBackground(Color.RED);
            }
//            if ( this.snakeBodyIndexs.size() > 1 && i == this.snakeBodyIndexs.size() - 1 ) {
//                this.getComponent( this.snakeBodyIndexs.get(i).intValue() ).setBackground(Color.ORANGE);
//            }
        }
        this.indexOfCurrentSnakeHeadPositon = this.snakeBodyIndexs.get(0);
    }
    public void directProcessing() {
        if ( !this.inputDirect.isEmpty() ) {
            this.changeDirect( this.inputDirect.poll().intValue() );
        }
    }
    public void changeDirect(int pressedKeyCode) {
        if ( pressedKeyCode == KeyEvent.VK_UP && this.directValidation( "up" ) ) {
            this.next_up_direct    = true;
            this.next_down_direct  = false;
            this.next_left_direct  = false;
            this.next_right_direct = false;
        } else if ( pressedKeyCode == KeyEvent.VK_LEFT && this.directValidation( "left" ) ) {
            this.next_up_direct    = false;
            this.next_down_direct  = false;
            this.next_left_direct  = true;
            this.next_right_direct = false;
        } else if ( pressedKeyCode == KeyEvent.VK_DOWN && this.directValidation( "down" ) ) {
            this.next_up_direct    = false;
            this.next_down_direct  = true;
            this.next_left_direct  = false;
            this.next_right_direct = false;
        } else if ( pressedKeyCode == KeyEvent.VK_RIGHT && this.directValidation( "right" ) ) {
            this.next_up_direct    = false;
            this.next_down_direct  = false;
            this.next_left_direct  = false;
            this.next_right_direct = true;
        }
    }
    public boolean directValidation( String nextDirect ) {
        if ( this.next_right_direct && nextDirect.equals(  "left" ) ) return false;
        if ( this.next_left_direct  && nextDirect.equals( "right" ) ) return false;
        if ( this.next_up_direct    && nextDirect.equals(  "down" ) ) return false;
        if ( this.next_down_direct  && nextDirect.equals(   "up"  ) ) return false;
        return true;
    }
}