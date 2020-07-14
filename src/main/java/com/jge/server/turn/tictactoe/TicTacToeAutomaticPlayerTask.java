/*
 * Jester Game Engine is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation and
 * distributed hereunder to you.
 *
 * Jester Game Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author: orochimaster
 * @email: orochimaster@yahoo.com.br
 */
package com.jge.server.turn.tictactoe;

import com.jge.server.client.ClientMessage;
import com.jge.server.net.Task;
import com.jge.server.space.game.turn.card.CardGameProtocol;
import com.jge.server.utils.DGSLogger;

public class TicTacToeAutomaticPlayerTask implements Task {

	private static boolean notcontains(byte[] cards, byte card) {
		for (int i = 0; i < cards.length; i++) {
			if (cards[i] == card) {
				return false;
			}
		}
		return true;
	}
	
	private static byte[] getRandomPlay(final byte[] tableCards) {
		// FIXME: TODO A RANDOM CHOICE
		return new byte [] { 0, 0 };
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	private final long automaticPlayTime;
	
	private final TicTacToeGame ticTacToeGame;

	private final byte automaticPlayIndex;
	
	private final long key;
	
	public TicTacToeAutomaticPlayerTask(byte automaticPlayIndex, long key, long automaticPlayTime, TicTacToeGame ticTacToeGame) {
		this.automaticPlayIndex = automaticPlayIndex;
		this.key = key;
		this.automaticPlayTime = automaticPlayTime;
		this.ticTacToeGame = ticTacToeGame;
	}
	
	public long getAutomaticPlayTime() {
		return automaticPlayTime;
	}
	
	public void run() {
		boolean active = ticTacToeGame.isAutomaticPlayInvalidated(automaticPlayIndex, key);
		
		DGSLogger.log("TicTacToeAutomaticPlayer.run(), active: " + active);
		
		if (!active) {
			ticTacToeGame.removeAutomaticPlayer(automaticPlayIndex, key);
			return;
		}
		
		final TicTacToeTurn ticTacToeTurn = (TicTacToeTurn) ticTacToeGame.getCurrentTurnNode();
		final int spaceID = ticTacToeGame.getId();
		
		final int screenPos = ticTacToeGame.getCurrentScreenPos().getPositionIdx();
		byte[] table = ticTacToeTurn.getTableCopy();
		

		final byte[] randomPlay = getRandomPlay(table);
		byte row = randomPlay[0];
		byte col = randomPlay[1];
		ClientMessage clientMessage = new ClientMessage(spaceID, TicTacToeProtocol.DO_PLAY);
		clientMessage.put(row);
		clientMessage.put(col);
		clientMessage.send();
	}
}
