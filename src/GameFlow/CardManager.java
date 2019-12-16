package GameFlow;

import DevelopmentCards.Card;
import DevelopmentCards.Knight;
import Player.Player;

import java.util.ArrayList;
import java.util.Stack;

/**
 * controls the card related works by taking data from game
 * @author Cevat Aykan Sevinc
 * @version 08.12.2019
 */
public class CardManager
{
	// Methods
	/**
	 * Adds a development card taken out from the stack to the CURRENT player.
	 */
	public void addDevelopmentCard()
	{
		// Get the related data
		Game game = Game.getInstance();
		FlowManager flowManager = new FlowManager();
		Player current = flowManager.getCurrentPlayer();
		Stack<Card> devCards = game.getCardStack();
		TitleManager titleManager = new TitleManager();

		// Get the top of the card
		Card top = devCards.peek();
		devCards.pop();

		if( top instanceof Knight ){
			flowManager.getCurrentPlayer().incrementLargestArmy();
			titleManager.updateLargestArmy();
		}

		current.buyDevelopmentCard( Card.REQUIREMENTS_FOR_CARD , top);
	}

	/**
	 * Call this function at the beginning turn of CURRENT player to allow the player to play previously bought special cards.
	 */
	public void makeCardsPlayable()
	{
		// Get the related data
		Game game = Game.getInstance();
		FlowManager flowManager = new FlowManager();
		ArrayList<Card> cards = flowManager.getCurrentPlayer().getCards();

		// Iterate over each card to make it playable
		for ( Card tempCard : cards)
		{
			tempCard.makePlayable();
		}
	}


	/**
	 * after user picked a material this method takes all materials from other players and add to the current player
	 * @param selectedMaterial selected material to play monopoly
	 */
	public void playMonopoly( int selectedMaterial)
	{
		// Get necessary data to manipulate
		Game game = Game.getInstance();
		FlowManager flowManager = new FlowManager();
		ArrayList<Player> players = game.getPlayers();
		Player currentP = flowManager.getCurrentPlayer();

		for ( int i = 0; i < players.size(); i++) // Loop through every player
		{
			// If the looped player isn't the one stealing, start the steal loop.
			if ( players.get(i) != currentP )
			{
				// Steal the material until the player no longer has any.
				/*for ( int p = 0; p < players.get( p).getResources()[ selectedMaterial]; p++)
				{
					current.collectMaterial( selectedMaterial, 1);
					players.get( p).discardMaterial( selectedMaterial, 1);
				}*/
				// Current player collect the total amount of selected material while other player discards it
				currentP.collectMaterial( selectedMaterial, players.get( i).getResources()[ selectedMaterial] );
				players.get( i).discardMaterial( selectedMaterial, players.get( i).getResources()[ selectedMaterial] );
			}
		}
	}

	/**
	 * after user selected a material, this method gives two selected material to the current user
	 * @param selectedMaterial selected material to give
	 */
	public void playYearOfPlenty( int selectedMaterial)
	{
		// Get the related data
		FlowManager flowManager = new FlowManager();
		Player currentP = flowManager.getCurrentPlayer();

		currentP.collectMaterial( selectedMaterial, 1);
		currentP.collectMaterial( selectedMaterial, 1);
	}
}
