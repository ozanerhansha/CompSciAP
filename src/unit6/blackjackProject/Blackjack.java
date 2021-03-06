package unit6.blackjackProject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import acm.graphics.GImage;
import acm.graphics.GTurtle;
import acm.program.GraphicsProgram;

/**
 * A Blackjack game using the ACM Library.
 * <br><br>
 * AP Computer Science<br>
 * 1/28/16<br>
 * Dr. Jones
 * @author Ozaner Hansha
 */
@SuppressWarnings("serial")
public class Blackjack extends GraphicsProgram implements BlackjackView {
	
	/**
	 * Initial window size.
	 */
	private static final Dimension INITIAL_SIZE = new Dimension(700,300);
	
	/**
	 * Coordinates of where the card stacks start.
	 */
	private static final double DECK_X = INITIAL_SIZE.getWidth() * .04,
								PLAYER_X = INITIAL_SIZE.getWidth() * .55,
								DEALER_X = INITIAL_SIZE.getWidth() * .2,
								CARD_Y = INITIAL_SIZE.getHeight() * .1;
	
	/**
	 * How much to offset new cards added to dealer/player's hand.
	 */
	private static final double CARD_OFFSET = BlackjackGCard.cardWidth() * .25;
	
	/**
	 * The Y Coordinate of the name tags.
	 */
	private static final double NAME_Y = INITIAL_SIZE.getHeight() * .01;
	
	/**
	 * The font of the {@link #nameTags}.
	 */
	private static final Font NAME_FONT = new Font("Comic Sans MS", Font.PLAIN, 19);
		
	/**
	 * How long, in seconds, a turn is.
	 */
	private static final int TURN_TIME = 10;
	
	/**
	 * The turtle.
	 */
	private GTurtle turtle = new GTurtle();
	
	/**
	 * Initial Coordinates of turtle.
	 */
	private static final double TURTLE_X = INITIAL_SIZE.getWidth() * .09,
								TURTLE_Y = INITIAL_SIZE.getWidth() * .11;
	
	/**
	 * A pointer to the {@link BlackjackModel} corresponding with
	 * this instance of {@link Backjack} for callbacks.
	 */
	private BlackjackModel bm;
	
	/**
	 * This {@link JLabel} displays any messages sent by {@link #bm}.
	 */
	private JLabel notifications = new JLabel();
	
	/**
	 * This {@link JLabel} displays the scores of the games.
	 * @see #updateScoreboard()
	 */
	private JLabel scoreboard = new JLabel();
	
	/**
	 * How much time is remaining this turn.
	 */
	private JLabel timerLabel = new JLabel();
	
	/**
	 * Keeps track of how long the player has to make their move.
	 */
	private Timer timer = new Timer();
	
	/**
	 * Name tags for the deck, dealer, and the player.
	 */
	private JLabel[] nameTags = {new JLabel("Deck"),
								new JLabel("Dealer"),
								new JLabel("Player")};
	
	/**
	 * The 3 turn buttons.
	 */
	private JButton[] buttons = {new JButton("New Round"),
								new JButton("Hit"),
								new JButton("Stay")};

	/**
	 * An image of a facedown card, representing the deck.
	 */
	private GImage deck = BlackjackGCard.getBackImage();
	
	/**
	 * Entry point when running Blackjack as an application.
	 * @param args
	 */
	public static void main(String[] args)
	{
		(new Blackjack()).start();
	}

	/** 
	 * Create the Model (with this for callbacks).
	 * Set up the GUI.
	 */
	@Override
	public void init()
	{
		bm = new BlackjackModel(this);

		setSize(INITIAL_SIZE);
		setBackground(Color.LIGHT_GRAY);
		
		notifications.setText("Welcome to BlackJack. Press New Game to start a new round.");
		notifications.setIcon(new ImageIcon(Blackjack.class.getResource("/unit6/blackjackProject/bjLogo.png")));
		notifications.setFont(NAME_FONT);
		timerLabel.setFont(NAME_FONT);
		scoreboard.setFont(NAME_FONT);
		
		//GUI
		add(notifications, NORTH);
		
		for(JButton b: buttons)
		{
			add(b, SOUTH);
		}
		buttons[1].setEnabled(false);
		buttons[2].setEnabled(false);
		
		add(deck, DECK_X, CARD_Y);
		
		for(JLabel j: nameTags)
			j.setFont(NAME_FONT);
		add(nameTags[0], DECK_X, NAME_Y);
		add(nameTags[1], DEALER_X, NAME_Y);
		add(nameTags[2], PLAYER_X, NAME_Y);
		
		add(timerLabel, NORTH);
		add(scoreboard, SOUTH);
		add(turtle, TURTLE_X, TURTLE_Y);
		startTurtleAI();
		updateScoreboard(0,0,0); //Starts scoreboard at 0
		addActionListeners();
	}

	/**
	 * Starts the turtle ai on a new thread.
	 */
	public void startTurtleAI()
	{
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){
		public void run()
		{
		//"A.I" controlled greg army
		switch(new Random().nextInt(5))
		{
		case 0:	turtle.left(12);			break;
		case 1:	turtle.right(12);		break;
		case 2:	turtle.forward(2);		break;
		case 4:	turtle.forward(-2);		break;
		}}}, 25, 5);
	}
	
	/**
	 * Starts the turn timer.
	 * (Can't put timer in model because interface is not editable.)
	 */
	public void startTimer()
	{
		timer.cancel();
		timer = new Timer();
		timer.schedule(new TimerTask()
		{
			int time = TURN_TIME;
			@Override
			public void run()
			{
				if(time >= 0)
				{
					timerLabel.setText("Time Left: " + time + " Seconds.");
					time--;
				}
				else if(time == -1)
				{
					bm.stay(); //Will stay automatically if nothing is done.
					cancel();
					timer = new Timer();
				}
			}
		},0,1000);
	}
	
	/**
	 * Starts a new round.
	 */
	public void newRound()
	{
		if(!bm.isGameInProgress())
		{
			//Removes all cards from canvas.
			for(int x = getGCanvas().getElementCount()-1; x > 0; x--)
			{
				if(getGCanvas().getElement(x) instanceof BlackjackGCard)
				{
					getGCanvas().remove(getGCanvas().getElement(x));
				}
			}
			
			bm.newRound();
			notifications.setText("Starting a New Round. (Hit or Stay?)");
			buttons[0].setText("Give Up");
			buttons[1].setEnabled(true);
			buttons[2].setEnabled(true);
			startTimer();
		}
		else
		{
			bm.quitGame();
			timerLabel.setText("");
			timer.cancel();
		}
	}
	
	/**
	 * Done after a round is over.
	 */
	public void endRound(int wins, int losses, int ties)
	{
		//Turn all cards face up.
		for(int x = 0; x < getGCanvas().getElementCount(); x++)
		{
			if(getGCanvas().getElement(x) instanceof BlackjackGCard)
			{
				((BlackjackGCard)getGCanvas().getElement(x)).turnFaceUp();
			}
		}
		updateScoreboard(wins, losses, ties);
		buttons[0].setText("New Round");
		buttons[1].setEnabled(false);
		buttons[2].setEnabled(false);
	}
	
	/**
	 * Updates the scoreboard with the current wins, losses, and ties.
	 */
	public void updateScoreboard(int wins, int losses, int ties) {
		scoreboard.setText(String.format("%d Wins - %d Losses - %d Ties", wins, losses, ties));
	}
	
	/** 
	 * Handler for button actions.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand() == "Hit")
		{
			bm.hit();
			startTimer();
		}
		else if(e.getActionCommand() == "Stay")
		{
			bm.stay();
			timer.cancel();
			timerLabel.setText("");
		}
		else //Must be reset, only 3 buttons.
			newRound();
	}

	/**
	 * This is a helper method for capitalizing strings.
	 * @param string - String to capitalize.
	 */
	public static String capitalize(String string)
	{
		return string.charAt(0) + string.toLowerCase().substring(1);
	}
	
	/*************  Notifications  ***************/

	/**
	 * Place the card dealt to the player on the canvas.
	 * The card size is rescaled to be consistent with the current deck image size.
	 * @param card  the card to be added
	 */
	@Override
	public void cardDealtToPlayerNotification(BlackjackGCard card) {
		notifications.setText(String.format("Dealer dealt a %s of %s.",
				capitalize(card.getRank().name()), capitalize(card.getSuit().name())));
		add(card, PLAYER_X + (bm.getPlayerCardNumber() -1) * CARD_OFFSET, CARD_Y);
	}

	/**
	 * Place the card dealt to the dealer on the canvas.
	 * The card size is rescaled to be consistent with the current deck image size.
	 * @param card  the card to be added
	 */
	@Override
	public void cardDealtToDealerNotification(BlackjackGCard card) {
		notifications.setText("Dealer dealt a card to himself");
		add(card, DEALER_X + (bm.getDealerCardNumber() -1) * CARD_OFFSET, CARD_Y);
	}

	/**
	 * End of game result notification. Dealer and player both bust.
	 */
	@Override
	public void bothBustNotification(int wins, int losses, int ties) {
		notifications.setText("You and the Dealer go bust. It's a Tie!");
		endRound(wins, losses, ties);
	}

	/**
	 * End of game result notification. Dealer wins, player busts.
	 */
	@Override
	public void youBustDealerWinsNotification(int wins, int losses, int ties) {
		notifications.setText("You went bust. You lose!");
		endRound(wins, losses, ties);
	}

	/**
	 * End of game result notification. Dealer busts, player wins.
	 */
	@Override
	public void dealerBustYouWinNotification(int wins, int losses, int ties) {
		notifications.setText("The Dealer went bust. You win!");
		endRound(wins, losses, ties);
	}

	/**
	 * End of game result notification. Player beats dealer.
	 */
	@Override
	public void youBeatDealerNotification(int wins, int losses, int ties) {
		notifications.setText("You beat the Dealer, You win!");
		endRound(wins, losses, ties);
	}

	/**
	 * End of game result notification. Dealer beats player.
	 */
	@Override
	public void dealerBeatsYouNotification(int wins, int losses, int ties) {
		notifications.setText("Dealer beat you, You Lose!");
		endRound(wins, losses, ties);
	}

	/**
	 * End of game result notification. Dealer and player tie.
	 */
	@Override
	public void bothTieNotification(int wins, int losses, int ties) {
		notifications.setText("You and the Dealer tie!");
		endRound(wins, losses, ties);
	}

	/**
	 * End of game result notification. Player quits game and loses.
	 */
	@Override
	public void quitGameNotification(int wins, int losses, int ties) {
		notifications.setText("You quit, You lose!");
		endRound(wins, losses, ties);
	}
}