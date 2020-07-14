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

import com.jge.server.space.game.GameScore;
import com.jge.server.space.game.MatchScore;
import com.jge.server.space.game.turn.GameTurn;
import com.jge.server.space.game.turn.ScreenPosition;
import com.jge.server.space.game.turn.ScreenPositionInterface;
import com.jge.server.space.game.turn.TurnBasedGame;
import com.jge.server.space.game.turn.TurnNode;
import com.jge.server.space.game.turn.TurnNodeInfo;
import com.jge.server.utils.DGSLogger;

public class TicTacToeTurn extends GameTurn {	
	public transient static final int MAX_MATCH_SCORE = 2;
	public transient static final int PLAYERS_TO_PLAY = 2;

	public TicTacToeTurn(TurnBasedGame turnBasedGame) {
		super(turnBasedGame);
	}
	
	
	@Override
	protected int getNumPlayerToPlayByTurn() {
		return 2;
	}

	@Override
	protected void setupNewTurn(TurnNodeInfo info, TurnBasedGame turnBasedGame) {
		super.setupNewTurn(info, turnBasedGame);
		
		DGSLogger.log("TicTacToeTurn.setupNewTurn...");
		TicTacToeTurn newNode = (TicTacToeTurn)createNodeInstance();
		MatchScore ticTacToeMatchScore = (MatchScore)matchScore;
		newNode.setMatchScore(ticTacToeMatchScore);
		newNode.setTurnPlay(this.getTurnPlay());
		
		turnBasedGame.setupNewNode(newNode);
	}

	

	@Override
	public GameScore createScoreNode() {
		TicTacToeGame ticTacGame = (TicTacToeGame)turnBasedGame;
		GameScore gameScore = ticTacGame.getGameScore();

		DGSLogger.log("TicTacToeTurn.createScoreNode, max turn points: " + MAX_MATCH_SCORE);
		MatchScore matchScore = new MatchScore(MAX_MATCH_SCORE);
		matchScore.setGameScore(gameScore);
		matchScore.addToScore(ticTacGame.getVERT_TEAM(), new Float(0));
		matchScore.addToScore(ticTacGame.getHORIZ_TEAM(), new Float(0));
		
		return matchScore;
	}
	
	@Override
	protected void resetPlayersToPlay() {
		setPlayersToPlay(PLAYERS_TO_PLAY);
	}

	
	@Override
	protected boolean hasTurnFinished() {
		boolean hasTurnFinished = playersToPlay <= 0;
		DGSLogger.log("TicTacToeTurn.hasTurnFinished(), hasTurnFinished: " + hasTurnFinished + " playerstoPlay: " + playersToPlay);
		return hasTurnFinished;
	}
	
	@Override
	protected void updateOnTurnFinish() {
		super.updateOnTurnFinish();
		DGSLogger.log("TicTacToeTurn.updateOnTurnFinish()");
	}
	
	@Override
	public TurnNode createNodeInstance() {
		return new TicTacToeTurn(turnBasedGame);
	}

	@Override
	public void updateScore() {

		// FIXME: todo the logic of the tictactoe here!
		ScreenPositionInterface winnerScreenPos = ScreenPosition.BOTTOM;
		
	
		TicTacToeGame ticTacToeGame = (TicTacToeGame)turnBasedGame;
		String winnerTeamName = ticTacToeGame.getTeamNameWithScreenPos(winnerScreenPos);
		
		MatchScore matchScore = (MatchScore)super.matchScore;

		DGSLogger.log("TicTacToeTurn.updateScore, team name: " + winnerTeamName + " " + ((TicTacToeGame)turnBasedGame).getVERT_TEAM() + " " + ((TicTacToeGame)turnBasedGame).getId());
		
		
		// update the match results if has winner
		if (matchScore.hasWinner()) {
			GameScore gameScore = matchScore.getGameScore();
			short points = 1;
			gameScore.addToScore(winnerTeamName, points);
		}

		// send score
		turnBasedGame.sendScore(matchScore);
	}

	protected boolean isGameFinishedByScoreInfo() {
		return matchScore.getGameScore().hasWinner();
	}

	@Override
	protected TurnNodeInfo getTurnNodeInfo() {
		TurnNodeInfo info = new TurnNodeInfo();
		
		boolean hasMatchWinner = matchScore.hasWinner();
		boolean hasGameFinished = matchScore.getGameScore().hasWinner();
		
		info.setNewTurn(!hasMatchWinner && !hasGameFinished);
		info.setNewMatch(hasMatchWinner && !hasGameFinished);
		info.setHasGameFinished(hasGameFinished);
		
		DGSLogger.log("TicTacToeTurn.getTurnNodeInfo, HAS MATCH WINNER: " + hasMatchWinner + " IS NEW TURN: " + info.isNewTurn() +  " GAME FINISHED: " + hasGameFinished);

		return info;
	}

	@Override
	protected boolean hasTurnFinishedByEvent() {
		return false;
	}


	@Override
	protected void updateBeforeTurnStart() {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void updateAfterTurnStart() {
		// TODO Auto-generated method stub
		
	}


	public byte[] getTableCopy() {
		// FIXME: to copy the current table
		return null;
	}


}