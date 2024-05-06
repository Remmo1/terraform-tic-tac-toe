import React, { useState, useEffect } from "react";
import "./App.css";
import "./components/GameLogic"
import GameLogic from "./components/GameLogic";

const App = () => {
  return (
    <div className="App">
      <GameLogic />
    </div>
  );
};

export default App;
