package GameFlow;

import DevelopmentCards.Card;
import Player.Player;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
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
		Player current = game.getCurrentPlayer();
		Stack<Card> devCards = game.getCardStack();

		// Get the top of the card
		Card top = devCards.peek();
		devCards.pop();

		current.buyDevelopmentCard( Card.REQUIREMENTS_FOR_CARD , top);
	}

	/**
	 * Call this function at the beginning turn of CURRENT player to allow the player to play previously bought special cards.
	 */
	public void makeCardsPlayable()
	{
		Game game = Game.getInstance();
		ArrayList<Card> cards = game.getCurrentPlayer().getCards();

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
		ArrayList<Player> players = game.getPlayers();
		Player currentP = game.getCurrentPlayer();

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
		Player currentP = Game.getInstance().getCurrentPlayer();

		currentP.collectMaterial( selectedMaterial, 1);
		currentP.collectMaterial( selectedMaterial, 1);
	}

	/**
	 * Plays a development card specified from the current player. The effect of the card will take place depending on
	 * the type. must will be loaded. actions will take place after input is taken at other specified functions
	 * @param card is the development card that will be played with its effect.
	 */
	public void playDevelopmentCard(Card card)
	{
        /*if ( card.getType() == DevelopmentCards.Card.CardType.KNIGHT)
        {
            flowManager.addMust(3); // inside tile
            flowManager.addMust(8); // get neighbor
            getCurrentPlayer().incrementLargestArmy(); // Add 1 army point to the player.
            this.updateLargestArmy();
        }
        else if ( card.getType() == DevelopmentCards.Card.CardType.MONOPOLY)
        {
            flowManager.addMust(4); // monopoly
        }
        else if ( card.getType() == DevelopmentCards.Card.CardType.ROADBUILDING)
        {
            getCurrentPlayer().addResource(GameBoard.StructureTile.REQUIREMENTS_FOR_ROAD);
            getCurrentPlayer().addResource(GameBoard.StructureTile.REQUIREMENTS_FOR_ROAD);

            flowManager.addMust(0); //road
            flowManager.addMust(0); //road
        }
        else if ( card.getType() == DevelopmentCards.Card.CardType.VICTORYPOINT)
        {
            getCurrentPlayer().increaseScore(1); // Add 1 score to the player.
        }
        else if ( card.getType() == DevelopmentCards.Card.CardType.YEAROFPLENTY)
        {
            flowManager.addMust(5); // year of plenty
        }*/
	}
}
