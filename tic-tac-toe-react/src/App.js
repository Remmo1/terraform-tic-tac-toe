import React, { useEffect } from 'react'
import { BrowserRouter,Routes, Route,Navigate  } from 'react-router-dom'
import Home from './components/Home';
import Signup from './components/Signup';
import Login from './components/Login';
import GameLogic from './game/GameLogic';

import './App.css';
import userpool from './userpool';

function App() {

  useEffect(()=>{
    let user=userpool.getCurrentUser();
      if(user){
        <Navigate to='/game' replace />
      }
  },[]);

  return (
    <BrowserRouter>
      <Routes>
        <Route path='/' element={<Home />}/>
        <Route path='/signup' element={<Signup />}/>
        <Route path='/login' element={<Login />}/>
        <Route path='/game' element={<GameLogic />}/>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
