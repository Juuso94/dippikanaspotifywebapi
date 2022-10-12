import './App.css';
import PlaybackController from "./components/playbackController.js"
import SearchField from './components/searchComponent';
import React, { Component } from 'react';

function App() {
  return (
    <div className='FrontPage'>
      <PlaybackController />
      <SearchField />
    </div>
    
  );
}

export default App;
