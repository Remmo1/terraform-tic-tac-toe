import React, { useState, useEffect } from "react";
import "./GameLogic.css";
import Square from "./Square";
import { io } from "socket.io-client";
import { useNavigate } from 'react-router-dom';
import userpool from '../userpool';
import { Button, useThemeProps } from '@mui/material'
import { logout } from '../services/authenticate';
import { CognitoRefreshToken } from "amazon-cognito-identity-js";

const renderFrom = [
  [1, 2, 3],
  [4, 5, 6],
  [7, 8, 9],
];

const backendLink = process.env.REACT_APP_BACKEND_LINK;
const backendPort = 8080;

const GameLogic = () => {
  const Navigate = useNavigate();
  const [gameState, setGameState] = useState(renderFrom);
  const [currentPlayer, setCurrentPlayer] = useState("circle");
  const [finishedState, setFinishetState] = useState(false);
  const [finishedArrayState, setFinishedArrayState] = useState([]);
  const [playOnline, setPlayOnline] = useState(false);
  const [socket, setSocket] = useState(null);
  const [playerName, setPlayerName] = useState("");
  const [opponentName, setOpponentName] = useState(null);
  const [playingAs, setPlayingAs] = useState(null);

  
  useEffect(()=>{
    var cognitoUser = userpool.getCurrentUser();
    if(!cognitoUser) {
      Navigate('/login');
    }

    var cognitoUser = userpool.getCurrentUser();
    
    var refreshToken = new CognitoRefreshToken({ RefreshToken: localStorage.getItem('refresh')})
  
    cognitoUser.getSession(function(err, session) {
      localStorage.setItem('token', session.accessToken.jwtToken);
        if (err) {                
          res.send(err);
        }
        else {
          if (!session.isValid()) {
            /* Session Refresh */
            cognitoUser.refreshSession(refreshToken, (err, session) => {
              if (err) { //throw err;
                  console.log('In the err' + err);
              }
              else {
                  localStorage.setItem('token', session.accessToken.jwtToken);
              }
            });   
          }
        }
      });
  },[]);

  const handleLogoout=()=>{
    logout();
  };

  const handleResults = () => {
    Navigate('/result');
  }

  const checkWinner = () => {
    // row dynamic
    for (let row = 0; row < gameState.length; row++) {
      if (
        gameState[row][0] === gameState[row][1] &&
        gameState[row][1] === gameState[row][2]
      ) {
        setFinishedArrayState([row * 3 + 0, row * 3 + 1, row * 3 + 2]);
        return gameState[row][0];
      }
    }

    // column dynamic
    for (let col = 0; col < gameState.length; col++) {
      if (
        gameState[0][col] === gameState[1][col] &&
        gameState[1][col] === gameState[2][col]
      ) {
        setFinishedArrayState([0 * 3 + col, 1 * 3 + col, 2 * 3 + col]);
        return gameState[0][col];
      }
    }

    if (
      gameState[0][0] === gameState[1][1] &&
      gameState[1][1] === gameState[2][2]
    ) {
      return gameState[0][0];
    }

    if (
      gameState[0][2] === gameState[1][1] &&
      gameState[1][1] === gameState[2][0]
    ) {
      return gameState[0][2];
    }

    const isDrawMatch = gameState.flat().every((e) => {
      if (e === "circle" || e === "cross") return true;
    });

    if (isDrawMatch) return "draw";

    return null;
  };

  useEffect(() => {
    const winner = checkWinner();
    if (winner) {
      setFinishetState(winner);
      socket.emit("endGame", {
        result: winner
      })
    }
  }, [gameState]);

  const takePlayerName = async () => {
    let user = userpool.getCurrentUser();
    let result = user.getUsername();
    return result;
  };

  socket?.on("opponentLeftMatch", () => {
    setFinishetState("opponentLeftMatch");
  });

  socket?.on("playerMoveFromServer", (data) => {
    const id = data.state.id;
    setGameState((prevState) => {
      let newState = [...prevState];
      const rowIndex = Math.floor(id / 3);
      const colIndex = id % 3;
      newState[rowIndex][colIndex] = data.state.sign;
      return newState;
    });
    setCurrentPlayer(data.state.sign === "circle" ? "cross" : "circle");
  });

  socket?.on("connect", function () {
    setPlayOnline(true);
  });

  socket?.on("OpponentNotFound", function () {
    setOpponentName(false);
  });

  socket?.on("OpponentFound", function (data) {
    console.log('Found');
    console.log(data);
    setPlayingAs(data.playingAs);
    setOpponentName(data.opponentName);
  });


  async function playOnlineClick() {
    const username = await takePlayerName();
    setPlayerName(username);

    const backendAddress = backendLink || window.location.protocol + '//' + window.location.hostname + ':' + backendPort;
    console.log(`Backend address: ${backendAddress}`);

    let accessToken = localStorage.getItem('token');
    console.log(accessToken);
    const newSocket = io(backendAddress, {
      autoConnect: true,
      extraHeaders: {
        "token": accessToken
      }
    });

    newSocket?.emit("auth", {
      token: accessToken,
    })

    newSocket?.emit("request_to_play", {
      playerName: username,
    });

    setSocket(newSocket);
  }

  if (!playOnline) {
    return (
      <div className="main-div">
        <button onClick={playOnlineClick} className="playOnline">
          Play Online
        </button>
        <div className='Dashboard'>
          <Button
            style={{margin:"10px"}}
            variant='contained'
            onClick={handleResults}
          >
            Results
          </Button>
          <Button
            style={{margin:"10px"}}
            variant='contained'
            onClick={handleLogoout}
          >
            Logout
          </Button>
        </div>
      </div>
    );
  }

  if (playOnline && !opponentName) {
    return (
      <div className='main-div'>
        <p className="waiting">Waiting for opponent</p>
        <div className='Dashboard'>
          <Button
            style={{margin:"10px"}}
            variant='contained'
            onClick={handleResults}
          >
            Results
          </Button>
          <Button
            style={{margin:"10px"}}
            variant='contained'
            onClick={handleLogoout}
          >
            Logout
          </Button>
        </div>
    </div>
    );
  }

  return (
    <div className="main-div">
      <div className="move-detection">
        <div
          className={`left ${
            currentPlayer === playingAs ? "current-move-" + currentPlayer : ""
          }`}
        >
          {playerName}
        </div>
        <div
          className={`right ${
            currentPlayer !== playingAs ? "current-move-" + currentPlayer : ""
          }`}
        >
          {opponentName}
        </div>
      </div>
      <div>
        <h1 className="game-heading water-background">Tic Tac Toe</h1>
        <div className="square-wrapper">
          {gameState.map((arr, rowIndex) =>
            arr.map((e, colIndex) => {
              return (
                <Square
                  socket={socket}
                  playingAs={playingAs}
                  gameState={gameState}
                  finishedArrayState={finishedArrayState}
                  finishedState={finishedState}
                  currentPlayer={currentPlayer}
                  setCurrentPlayer={setCurrentPlayer}
                  setGameState={setGameState}
                  id={rowIndex * 3 + colIndex}
                  key={rowIndex * 3 + colIndex}
                  currentElement={e}
                />
              );
            })
          )}
        </div>
        {finishedState &&
          finishedState !== "opponentLeftMatch" &&
          finishedState !== "draw" && (
            <h3 className="finished-state">
              {finishedState === playingAs ? "You " : finishedState} won the
              game
            </h3>
          )}
        {finishedState &&
          finishedState !== "opponentLeftMatch" &&
          finishedState === "draw" && (
            <h3 className="finished-state">It's a Draw</h3>
          )}
      </div>
      {!finishedState && opponentName && (
        <h2>You are playing against {opponentName}</h2>
      )}
      {!finishedState && opponentName && (
        <h2>You are playing as {playingAs}</h2>
      )}
      {finishedState && finishedState === "opponentLeftMatch" && (
        <h2>You won the match, Opponent has left</h2>
      )}
      <div className='Dashboard'>
          <Button
            style={{margin:"10px"}}
            variant='contained'
            onClick={handleResults}
          >
            Results
          </Button>
          <Button
            style={{margin:"10px"}}
            variant='contained'
            onClick={handleLogoout}
          >
            Logout
          </Button>
        </div>
    </div>
  );
};

export default GameLogic;
