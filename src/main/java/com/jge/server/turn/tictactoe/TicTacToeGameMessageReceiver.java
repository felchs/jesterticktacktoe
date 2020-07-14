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

import com.jge.server.client.MessageSender;
import com.jge.server.net.Channel;
import com.jge.server.space.Space;
import com.jge.server.space.game.turn.TurnBasedGame;
import com.jge.server.space.game.turn.card.CardGameMessageReceiver;

public class TicTacToeGameMessageReceiver extends CardGameMessageReceiver {
	public TicTacToeGameMessageReceiver(Space gameSpace) {
		super(gameSpace);
	}
	
	@Override
	public boolean isPlayMessage(byte protocol) {
		return super.isPlayMessage(protocol);
	}

	@Override
	public boolean receivedTurnMessage(Channel channel, MessageSender sender, byte event, TurnBasedGame game, ByteBuffer msg) {
		if (super.receivedTurnMessage(channel, sender, event, game, msg)) {
			return true;
		}
		
		TicTacToeGame ticTacToeGame = ((TicTacToeGame)game);
		
		if (TicTacToeProtocol.DO_PLAY.getId() == event) {
			byte row = msg.get();
			byte col = msg.get();
			ticTacToeGame.onDoPlay(row, col);
			return true;
		}
		
		return false;
	}
}
