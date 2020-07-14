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

import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.jge.server.ServerMessage;
import com.jge.server.client.Client;
import com.jge.server.net.AppContext;
import com.jge.server.net.Channel;
import com.jge.server.net.ChannelManager;
import com.jge.server.net.Delivery;
import com.jge.server.net.session.ClientSession;
import com.jge.server.space.Space;
import com.jge.server.space.SpaceMessageReceiver;
import com.jge.server.space.SpaceState;
import com.jge.server.space.game.GameProtocol;
import com.jge.server.space.game.GameScore;
import com.jge.server.space.game.GameTeam;
import com.jge.server.space.game.MatchScore;
import com.jge.server.space.game.Player;
import com.jge.server.space.game.RobotInterface;
import com.jge.server.space.game.achievements.Achievements;
import com.jge.server.space.game.lobby.LobbySpace;
import com.jge.server.space.game.lobby.LobbySubPlace;
import com.jge.server.space.game.ranking.Ranking;
import com.jge.server.space.game.turn.ScreenPosition;
import com.jge.server.space.game.turn.ScreenPositionInterface;
import com.jge.server.space.game.turn.TurnBasedGame;
import com.jge.server.space.game.turn.TurnGameProtocol;
import com.jge.server.space.game.turn.TurnGameTeam;
import com.jge.server.space.game.turn.TurnNode;
import com.jge.server.space.game.turn.TurnNodeInfo;
import com.jge.server.space.game.turn.TurnPlayer;
import com.jge.server.space.game.turn.TurnRobotInterface;
import com.jge.server.utils.DGSLogger;

public class TicTacToeGame extends TurnBasedGame implements LobbySubPlace {
	private static transient final int AUTOMATIC_PLAY_TIME = 0;//30 * 1000;
	
	private boolean isMiniTicTacToe;
	
	private static transient final int MAX_GAME_POINTS = 3;
	
	private String VERT_TEAM;
	private String HORIZ_TEAM;
	
	private byte placeId;
	private byte subPlaceId;
	
	public TicTacToeGame(int id, byte gameEnum, boolean isMini) {
		super(id, gameEnum, 2, 2, AUTOMATIC_PLAY_TIME);
		
		this.isMiniTicTacToe = isMini;
	}

	public boolean isMiniTicTacToe() {
		return isMiniTicTacToe;
	}
	
	public String getVERT_TEAM() {
		return VERT_TEAM;
	}
	
	public String getHORIZ_TEAM() {
		return HORIZ_TEAM;
	}
	
	public int getMAX_GAME_POINTS() {
		return MAX_GAME_POINTS;
	}
	
	protected String getChatChannelName() {
		return getName() + "ChChat";
	}
	
	public Channel getChatChannel() {
		return AppContext.getChannelManager().getChannel(getChatChannelName());
	}

	public String getOpponentTeamName(Player player) {
		String playerTeamName = player.getTeamName();
		int sz = teamNames.size();
		for (int i = 0; i < sz; i++) {
			String teamName = teamNames.get(i);
			if (!teamName.equals(playerTeamName)) {
				return teamName;
			}
		}

		return null;
	}

	@Override
	public void createAutomaticPlayers() {
		if (!hasAutomaticPlay()) {
			return;
		}
		
		int automaticPlayerNamesSz = automaticPlayerNames.size();
		DGSLogger.log("TicTacToeGame.createAutomaticPlayers(), sz: " + automaticPlayerNamesSz);
		
		if (automaticPlayerNamesSz == 0) {
			TicTacToeAutomaticPlayer ticTacToeAutomaticPlayer = new TicTacToeAutomaticPlayer(PLAYER_PREFIX, (byte)0, this);
			addAutomaticPlayer(ticTacToeAutomaticPlayer);
		}
	}

	@Override
	public void createScreenPositions() {
		// horizontal positions
		ScreenPosition top = ScreenPosition.TOP;
		screenPositions.add(top);

		ScreenPosition bottom = ScreenPosition.BOTTOM;
		screenPositions.add(bottom);
	}
	
	public String getTeamNameWithScreenPos(ScreenPositionInterface screenPos) {
		byte value = screenPos.getPositionIdx();
		if (value == ScreenPosition.RIGHT.ordinal() || value == ScreenPosition.LEFT.ordinal()) {
			return getHORIZ_TEAM();
		} else if (value == ScreenPosition.TOP.ordinal() || value == ScreenPosition.BOTTOM.ordinal()) {
			return getVERT_TEAM();
		}
		throw new RuntimeException("Team not found: " + screenPos.toString());
	}

	@Override
	public void updatePlayersTeams() {
		CopyOnWriteArrayList<String> playerNames = getPlayerNames();
		if (playerNames == null) {
			throw new RuntimeException();
		}
		
		for (String playerName : playerNames) 
		{
			updatePlayerTeam(playerName);
		}
	}

	public void updatePlayerTeam(String playerName) {
		TurnPlayer player = (TurnPlayer)getPlayerWithPlayerName(playerName);
		ScreenPositionInterface pos = player.getScreenPosition();
		String teamName = null;

		if (pos == ScreenPosition.TOP || pos == ScreenPosition.BOTTOM)
		{
			teamName = VERT_TEAM;
		}
		else
		{
			teamName = HORIZ_TEAM;
		}
		
		player.setTeamName(teamName);
		player.setTeamHandle(0);
		getTeam(teamName).addGamePlayer(player.getPlayerName());
	}
	
	@Override
	public void createTeams() {
		VERT_TEAM = this.getName() + ":TEAM:" + "vert_team";
		HORIZ_TEAM = this.getName() + ":TEAM:" + "horiz_team";

		TurnGameTeam turnGameTeamH = new TurnGameTeam(0, HORIZ_TEAM);
		TurnGameTeam turnGameTeamV = new TurnGameTeam(1, VERT_TEAM);
		
		addTeam(turnGameTeamH);
		addTeam(turnGameTeamV);
	}
	
	@Override
	protected Player createRobot(byte robotIndex, Object initInfo) {
		ScreenPosition screenPos = (ScreenPosition)initInfo;
		return new TicTacToeRobotPlayer(PLAYER_PREFIX, robotIndex, screenPos, this);
	}
	
	@Override
	protected Player createRobotWithExistingPlayer(Player player, Object initInfo) {
		TurnPlayer turnPlayer = (TurnPlayer)player;
		ScreenPositionInterface screenPos = turnPlayer.getScreenPosition();
		byte robotIndex = (Byte)initInfo;
		TicTacToeRobotPlayer ticTacToeRobotPlayer = new TicTacToeRobotPlayer(PLAYER_PREFIX, robotIndex, screenPos, this);
		ticTacToeRobotPlayer.setTeamName(player.getTeamName());

		return ticTacToeRobotPlayer;
	}

	public void joinChannels(ClientSession clientSession) {
		ChannelManager cm = AppContext.getChannelManager();
		cm.getChannel(getChatChannelName()).join(clientSession);
	}
	
	public void createChannels() {
		ChannelManager cm = AppContext.getChannelManager();
		cm.createChannel(getChatChannelName(), null, Delivery.RELIABLE);
	}
	
	@Override
	public TurnNode createTurnNode() {
		TicTacToeTurn ticTacToeTurn = new TicTacToeTurn(this);
		return ticTacToeTurn;
	}
	
	@Override
	public void updateStatisticsOnGameFinish() {
		DGSLogger.log("TicTacToeGame.updateRanking(), gameState: " + getSpaceState());
		
		// if the game isn't started just return and don't update ranking
		if (getSpaceState() == SpaceState.NOT_STARTED) {
			return;
		}
		
		TicTacToeTurn ticTacToeTurn = (TicTacToeTurn)currentTurnNode;
		GameScore score = ticTacToeTurn.getMatchScore();
		
		SortedMap<String, Float> pointsMap = score.getPointsMap();
		Iterator<String> it = pointsMap.keySet().iterator();
		String t1Name = it.next();
		String t2Name = it.next();
		Float t1Value = pointsMap.get(t1Name);
		Float t2Value = pointsMap.get(t2Name);
		
		String winnerTeamName = null;
		String loserTeamName = null;
		
		if (t1Value > t2Value) {
			winnerTeamName = t1Name;
			loserTeamName = t2Name;
		} 
		else if (t2Value > t1Value) {
			winnerTeamName = t2Name;
			loserTeamName = t1Name;
		}

		DGSLogger.log("TicTacToeGame.updateRanking(), winnerTeam: " + winnerTeamName + " loserTeam: " + loserTeamName);

		float tiePoints = 0; // by now don't set tie points for TicTacToe game
		if (winnerTeamName != null)
		{
			DGSLogger.log("TicTacToeGame.updateWinner()");
			
			GameTeam winnerTeam = getTeam(winnerTeamName);
			GameTeam loserTeam = getTeam(loserTeamName);
			
			if (hasRanking()) {
				ranking.updateWinner(winnerTeam, loserTeam, tiePoints);
			}
			
			CopyOnWriteArrayList<String> winnerPlayerNames = winnerTeam.getGamePlayerNames();
			for (String playerName : winnerPlayerNames) {
				Player player = getPlayerWithPlayerName(playerName);
				if (!player.isRobot() && !player.isAutomaticPlayer()) {
					Client client = player.getClient();
					Achievements achievements = client.getAchievements(getGameEnum());
					achievements.addWin(1);
				}
			}
			
			CopyOnWriteArrayList<String> losePlayerNames = loserTeam.getGamePlayerNames();
			for (String playerName : losePlayerNames) {
				Player player = getPlayerWithPlayerName(playerName);
				if (!player.isRobot() && !player.isAutomaticPlayer()) {
					Client client = player.getClient();
					Achievements achievements = client.getAchievements(getGameEnum());
					achievements.addLose(1);
				}
			}
		}
		else
		{
			GameTeam t1 = getTeam(t1Name);
			GameTeam t2 = getTeam(t2Name);
			
			DGSLogger.log("TicTacToeGame.updateDraw()");
			if (hasRanking()) {
				ranking.updateDraw(t1, t2, tiePoints);
			}
			
			CopyOnWriteArrayList<String> playerNames = t1.getGamePlayerNames();
			for (String playerName : playerNames) {
				Player player = getPlayerWithPlayerName(playerName);
				if (!player.isRobot() && !player.isAutomaticPlayer()) {
					Client client = player.getClient();
					Achievements achievements = client.getAchievements(getGameEnum());
					achievements.addDraw(1);
				}
			}
			
			playerNames = t2.getGamePlayerNames();
			for (String playerName : playerNames) {
				Player player = getPlayerWithPlayerName(playerName);
				if (!player.isRobot() && !player.isAutomaticPlayer()) {
					Client client = player.getClient();
					Achievements achievements = client.getAchievements(getGameEnum());
					achievements.addDraw(1);
				}
			}
		}
	}
	
	@Override
	protected void updateRankingOnExit(Player player, boolean disconnected) {
		boolean hasRanking = hasRanking();
		DGSLogger.log("TicTacToeGame.updateRankingOnExit(), hasRanking: " + hasRanking);
		if (hasRanking) {
			Ranking ranking_ = ranking;
			ranking_.updateRankingOnExit(player, disconnected);
		}
	}
	
	@Override
	protected void createGameScore() {
		int MAX_GAME_POINTS = getMAX_GAME_POINTS();
		GameScore gameScore = new GameScore(MAX_GAME_POINTS);
		gameScore.addToScore(getVERT_TEAM(), 0f);
		gameScore.addToScore(getHORIZ_TEAM(), 0f);
		this.gameScore = gameScore;
	}
	
	@Override
	protected Achievements createAchievements() {
		byte gameEnum = getGameEnum();
		return new TicTacToechievements(gameEnum);		
	}
	
	@Override
	public SpaceMessageReceiver createMessageReceiver() {
		return new TicTacToeGameMessageReceiver(this);
	}

	@Override
	protected void reconnectClient(Client client) {
	}
	
	// Parent abstract notification impl --------------------------------------

	@Override
	protected void notifyParentRobotEnter(RobotInterface robot) {
		DGSLogger.log("TicTacToeGame.notifyParentRobotEnter(), robotIndex: " + robot.getRobotIndex());
		Space parentSpace = getParentSpace();
		LobbySpace lobby = (LobbySpace)parentSpace;
		
		byte robotIndex = ((RobotInterface)robot).getRobotIndex();
		short connectedId = (short) (robotIndex * -1);
		byte placeId_ = getPlaceId();
		
		// subplace must be set and it's based on 
		// player's chair choose which in turn is the screen position
		TurnRobotInterface turnRobot = (TurnRobotInterface)robot;
		byte robotScreenPos = turnRobot.getRobotScreenPos();
		byte subPlaceId_ = robotScreenPos;
		setSubPlaceId(subPlaceId_);
		DGSLogger.log("TicTacToeGame.notifyParentRobotEnter(), PlaceId: " + placeId_ + " SubPlaceId: " + subPlaceId_);
		lobby.setPlaceBusy(placeId_, subPlaceId_, false, connectedId);
	}
	
	protected void notifyParentPlayerExit(Player player) {
		DGSLogger.log("TicTacToeoGame.notifyParentPlayerExit()");
		TurnPlayer turnPlayer = (TurnPlayer)player;
		
		Space parentSpace = getParentSpace();
		LobbySpace lobby = (LobbySpace)parentSpace;
		
		short connectedID = 0;
		if (turnPlayer.isRobot()) {
			// note: * -1, the robot id is negative on client side
			byte robotIndex = ((RobotInterface)turnPlayer).getRobotIndex();
			connectedID = (short) (robotIndex * -1);
		} else {
			connectedID = turnPlayer.getClientId();
		}

		byte placeID_ = getPlaceId();
		byte screenPos = (byte) ((TurnPlayer)player).getScreenPosition().getPositionIdx();
		byte subPlaceID_ = screenPos;		
		lobby.onPlayerExitGame(placeID_, subPlaceID_, connectedID);
	}
	
	protected void notifyParentGameStarted() {
		// sign the game start to lobby (if exists)
		Space parentSpace = getParentSpace();
		if (parentSpace != null) {
			LobbySpace lobby = (LobbySpace)parentSpace;
			lobby.onGameStart(this);
		}
	}
	
	// LobbySubPlace Interface impl -------------------------------------------
	
	public void setPlaceID(byte placeId) {
		this.placeId = placeId;
	}

	public byte getPlaceId() {
		return placeId;
	}

	public void setSubPlaceId(byte subPlaceId) {
		this.subPlaceId = subPlaceId;
	}

	public byte getSubPlaceId() {
		return subPlaceId;
	}

	// Input Events ------------------------------------------------------------
	
	@Override
	protected boolean getAutomaticRestartState() {
		return false;
	}
	

	public void onDoPlay(byte row, byte col) {
		//
		// DO THE GAME LOGIC HERE
		//
		
	}	

	// Output Events ----------------------------------------------------------
	
	@Override
	public void sendGameResults() {
		ServerMessage serverMessage = new ServerMessage(getId(), GameProtocol.GAME_RESULTS);
		SortedMap<String, Float> pointsMap = getGameScore().getPointsMap();
		
		Set<String> teams = pointsMap.keySet();
		for (String teamName : teams) {
			short point = (short)pointsMap.get(teamName).intValue();
			serverMessage.put(point);
		}
		DGSLogger.log("TicTacToeGame.sendGameResults id: " + id);
		serverMessage.sendToPlayers(this);
	}

	public void sendScore(GameScore score) {
		MatchScore turnScore = (MatchScore)score;
		
		ServerMessage serverMessage = new ServerMessage(getId(), GameProtocol.UPDATE_SCORE);
		
		SortedMap<String, Float> pointsMap = turnScore.getPointsMap();
		Set<String> keys = pointsMap.keySet();
		for (String key : keys) {
			short value = pointsMap.get(key).shortValue();
			serverMessage.put(value);
		}

		// game points
		GameScore gameScore = turnScore.getGameScore();
		SortedMap<String, Float> gamePoints = gameScore.getPointsMap();
		Set<String> gamePointsKeys = gamePoints.keySet();
		for (String key : gamePointsKeys) {
			short value = gamePoints.get(key).shortValue();
			serverMessage.put(value);
			DGSLogger.log("TicTacToeGame.sendScore gp: " + key + " v: " + value);
		}

		DGSLogger.log("TicTacToeGame.sendScore id: " + id);
		serverMessage.sendToPlayers(this);
	}
		
	@Override
	protected void sendAdditionalInitInfo(ServerMessage serverMessage) {
	}
	
	@Override
	public void sendTurnStarted(TurnNodeInfo info) {
		ServerMessage serverMessage = new ServerMessage(getId(), TurnGameProtocol.TURN_STARTED);
		boolean isNewMatch = info.isNewMatch();
		serverMessage.put(isNewMatch);
		DGSLogger.log("TicTacToeGame.sendTurnStarted id: " + id + ", " + isNewMatch);
		serverMessage.sendToPlayers(this);
	}

}