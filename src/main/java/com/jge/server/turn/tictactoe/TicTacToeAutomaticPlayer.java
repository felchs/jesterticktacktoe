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
import java.util.Hashtable;
import java.util.Set;

import com.jge.server.net.AppContext;
import com.jge.server.space.game.AutomaticPlayerInterface;
import com.jge.server.space.game.GameSpace;
import com.jge.server.space.game.turn.TurnPlayer;
import com.jge.server.utils.DGSLogger;

public class TicTacToeAutomaticPlayer extends TurnPlayer implements AutomaticPlayerInterface {
	private static final transient String AUTOMATIC_PLAYER_PREFIX = ":AUTO_PLAYER";
	
	private final byte automaticPlayerIndex;
	
	private Hashtable<Long, Boolean> automaticPlays = new Hashtable<Long, Boolean>();
	
	private GameSpace gameSpace;
	
	public TicTacToeAutomaticPlayer(String PLAYER_PREFIX, byte automaticPlayerIndex, GameSpace gameSpace) {
		super(null, PLAYER_PREFIX + AUTOMATIC_PLAYER_PREFIX + automaticPlayerIndex, null);
		
		this.automaticPlayerIndex = automaticPlayerIndex;
		this.gameSpace = gameSpace;
	}
	
	public String getName() {
		return getPlayerName();
	}
	
	public boolean isAutomaticPlayActive(long key) {
		return automaticPlays.get(key);
	}
	
	public boolean removeAutomaticPlay(long key) {
		return automaticPlays.remove(key);
	}
	
	public byte getAutomaticPlayerIndex() {
		return automaticPlayerIndex;
	}
	
	public void invalidateCurrentAutomaticPlays() {
		DGSLogger.log("Invalidate automaticPlays()");
		Set<Long> keys = automaticPlays.keySet();
		for (Long key : keys) {
			automaticPlays.put(key, false);
		}
	}

	@Override
	public boolean isAutomaticPlayer() {
		return true;
	}

	public void scheduleAutomaticPlay(long automaticPlayTime) {
		DGSLogger.log("TicTacToeAutomaticPlayer.scheduleAutomaticPlay, automaticPlayTime: " + automaticPlayTime);
		
		TicTacToeGame game = (TicTacToeGame)gameSpace;
		long automaticPlayKey = System.currentTimeMillis();
		automaticPlays.put(automaticPlayKey, true);
		TicTacToeAutomaticPlayerTask automaticPlayTask = new TicTacToeAutomaticPlayerTask(automaticPlayerIndex, automaticPlayKey, automaticPlayTime, game);
		AppContext.getTaskManager().scheduleTask(automaticPlayTask, automaticPlayTask.getAutomaticPlayTime());
	}

	public void receiveMessage(ByteBuffer message) { }
}
