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

import java.nio.ByteBuffer;

import com.jge.server.client.ClientMessage;
import com.jge.server.space.game.GameSpace;
import com.jge.server.space.game.turn.ScreenPositionInterface;
import com.jge.server.space.game.turn.TurnMessageAdapter;
import com.jge.server.space.game.turn.TurnNode;
import com.jge.server.space.game.turn.TurnPlayer;
import com.jge.server.space.game.turn.TurnRobotInterface;
import com.jge.server.utils.DGSLogger;

public class TicTacToeRobotPlayer extends TurnPlayer implements TurnRobotInterface {
	public static boolean notcontains(byte[] cards, byte card) {
		for (int i = 0; i < cards.length; i++) {
			if (cards[i] == card) {
				return false;
			}
		}
		return true;
	}
	
	public static int count = 0;
	
	///////////////////////////////////////////////////////////////////////////
	
	private byte robotIndex;
	
	private GameSpace gameSpace;
	
	private boolean played;

	public TicTacToeRobotPlayer(String PLAYER_PREFIX, byte robotIndex, ScreenPositionInterface screenPos, TicTacToeGame ticTacToeGame) {
		super(null, PLAYER_PREFIX + GameSpace.ROBOT_PREFIX + robotIndex, screenPos);

		this.robotIndex = robotIndex;
		this.gameSpace = ticTacToeGame;
	}
	
	public byte getRobotScreenPos() {
		return (byte) getScreenPosition().getPositionIdx();
	}
	
	@Override
	public short getPlayerHandle() {
		return robotIndex;
	}

	public byte getRobotIndex() {
		return robotIndex;
	}
	
	@Override
	public boolean isRobot() {
		return true;
	}
	
	public void receiveMessage(ByteBuffer message) {
		final TicTacToeGame ticTacToeGame = (TicTacToeGame)gameSpace;
		final TurnNode currentTurnNodeRef = ticTacToeGame.getCurrentTurnNode();
		if (currentTurnNodeRef == null) {
			return;
		}
		
		final TicTacToeRobotPlayer myself = this;

		TicTacToeMessageProcessor ticTacToeMessageOutProcessor = new TicTacToeMessageProcessor(new TurnMessageAdapter() {
			
			@Override
			public void turnFinished(ByteBuffer message) {
				super.turnFinished(message);
				
				myself.played = false;
			}

			@Override
			public void notifyCurrentPlayerRobot(byte robotIndex) {
				//
				// DO ALL THE ROBOT LOGIC HERE!
				//
				
				int spaceId = ticTacToeGame.getId();
			
				ClientMessage clientMessage = new ClientMessage(spaceId, TicTacToeProtocol.DO_PLAY);
				byte row = (byte) (1);
				byte col = (byte) (1);
				clientMessage.put(row);
				clientMessage.put(col);
				clientMessage.send();
			}
		});

		// call a function
		message.get(); // byte session
		int intSpaceId = message.getInt(); // spaceId
		DGSLogger.log("TicTacToeRobotPlayer.received message, spaceId: " + intSpaceId);
		byte event = message.get();
		DGSLogger.log("TicTacToeRobotPlayer.received message, event: " + event);
		ticTacToeMessageOutProcessor.callFunction(event, message);
	}
}