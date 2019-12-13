package SceneManagement;

/**
 * Pixel processor takes the
 */
public class PixelProcessor
{
	private static int hexIndex = -1;
	private static int tileIndex = -1;

	/**
	 * Processes the mouse click event for the x coordinate of game board
	 * @param x is the x coordinate given by the mouse event
	 * @return an integer index, the processed result corresponding the x index for the game board
	 */
	public static int processX( double x)
	{
		// Threshold for eliminating 0 index bug
		if (  x < 35 )
		{
			return -1;
		}
		else
		{
			x = x - 45; // Omit threshold

			hexIndex = 0;
			while ( (int) (x / 170) > 0 ) // Find the right index of the hexagon.
			{
				++hexIndex;
				x = x - 120; // Discard the first hexagon, shift every hexagon to left
			}

			// Find the right index of the tile in the hexagon
			// PLEASE DONT JUDGE ME IT IS 01.33 AM AND I AM TIRED! i will update it with a while loop i am aware pls.
			if ( (int) (x / 10) == 0 )
			{
				tileIndex = 0;
			}
			else
			{
				x = x - 10;
				if ( (int) (x / 20) == 0 )
				{
					tileIndex = 1;
				}
				else
				{
					x = x - 20;
					if ( (int) (x / 30) == 0 )
					{
						tileIndex = 2;
					}
					else
					{
						x = x - 30;
						if ( (int) (x / 40) == 0 )
						{
							tileIndex = 3;
						}
						else
						{
							x = x - 40;
							if ( (int) (x / 30) == 0 )
							{
								tileIndex = 4;
							}
							else
							{
								x = x - 30;
								if ( (int) (x / 20) == 0 )
								{
									tileIndex = 5;
								}
								else
								{
									tileIndex = 6;
								}
							}
						}
					}
				}
			}
			int realTileIndex = 0;
			int tempHexIndex = hexIndex;
			while ( tempHexIndex > 0)
			{
				tempHexIndex--;
				realTileIndex += 4;
			}

			return realTileIndex + tileIndex;
		}
	}

	/**
	 * Processes the mouse click event for the y coordinate of game board
	 * @param y is the y coordinate given by the mouse event
	 * @return an integer index, the processed result corresponding the y index for the game board
	 */
	public static int processY( double y)
	{
		if ( y < 35)
		{
			return -1;
		}
		// How beautiful everything is when each tile has the same height ;/
		y -= 35;
		return (int) y / 30;
	}

	/**
	 * Gets the real value of x pixel coordinate for displaying structures to the players.
	 * @return the corresponding x pixel value on the screen regarding parameters hexIndex and tileIndex
	 */
	public static int getXToDisplay()
	{
		// Process the hexIndex and tileIndex to access the x pixel on the screen for display.
		int x = hexIndex * 120;
		if ( tileIndex == 0 )
		{
			x += 40;
		}
		else if ( tileIndex == 1 )
		{
			x += 60;
		}
		else if ( tileIndex == 2 )
		{
			x += 80;
		}
		else if ( tileIndex == 3 )
		{
			x += 110;
		}
		else if ( tileIndex == 4 )
		{
			x += 150;
		}
		else if ( tileIndex == 5)

		{
			x += 180;
		}
		else
		{
			x += 200;
		}

		return x;
	}

	/**
	 * Gets the real value of y pixel coordinate for displaying structures to the players.
	 * @param y is the int row index on the Tile[][]
	 * @return int - the corresponding starting pixel of the row: y
	 */
	public static int getYToDisplay( int y)
	{
		return y * 30 + 35;
	}
}
