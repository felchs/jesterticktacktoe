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

import com.jge.server.Protocol;
import com.jge.server.utils.ByteUtils;

public enum TicTacToeProtocol implements Protocol<Byte> {
	DO_PLAY((byte)110);
	
	private byte id;

	private TicTacToeProtocol(byte id) {
		this.id = id;
	}

	public Byte getId() {
		return id;
	}
	
	public byte[] getIdAsBytes() {
		return ByteUtils.getBytes(id);
	}

	public String getName() {
		return "TicTacToeProtocol." + this.toString();
	}

	public static boolean contains(byte value) {
		TicTacToeProtocol[] values = TicTacToeProtocol.values();
		for (TicTacToeProtocol ticTacToeProtocol : values) {
			if (ticTacToeProtocol.getId() == value) {
				return true;
			}
		}
		return false;

	}
}
