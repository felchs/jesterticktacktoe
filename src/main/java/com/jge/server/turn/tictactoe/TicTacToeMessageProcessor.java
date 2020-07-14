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

import com.jge.server.space.game.turn.TurnMessageAdapter;
import com.jge.server.space.game.turn.TurnMessageProcessor;
import com.jge.server.utils.DGSLogger;

public class TicTacToeMessageProcessor extends TurnMessageProcessor {

	public TicTacToeMessageProcessor(TurnMessageAdapter ticTacToeMessageOutInterface) {
		super(ticTacToeMessageOutInterface);
	}

	public TurnMessageAdapter getTicTacToeOut() {
		return (TurnMessageAdapter)spaceOutMessageInterface;
	}
	
	@Override
	public boolean callFunction(byte event, ByteBuffer message) {
		if (super.callFunction(event, message)) {
			return true;
		}
		
		DGSLogger.log("TicTacToeMessageProcessor.callFunction, event: " + event);
		
		if (TicTacToeProtocol.DO_PLAY.getId() == event) {
			byte row = message.get();
			byte col = message.get();
			// FIXME:
			//getTicTacToeOut().show11HandsAccepted(answer);
			return true;
		}
		
		return false;
	}
}
