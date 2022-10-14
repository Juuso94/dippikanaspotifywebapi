import '../App.css';
import React, { Component } from 'react';
import {backendURI} from "../HelperFunctions.js"

class PlaybackController extends Component {
  constructor() {
    super();
    this.playbackRef = React.createRef()
    this.state = {
      song: {
        name: null,
        artists: [],
        id: null,
        is_playing : false,
        time_left: null
      },
      disabled: false
    }
    
  }

  componentDidMount() {
    this.updateCurrentlyPlaying()
    this.songCountdown()
  }

  updateCurrentlyPlaying = () => {

    fetch(backendURI + "currentlyPlaying")
    .then(response => {
      if(response.status === 204) {
        this.setState( {
          song: {
            name: null,
            artists: [],
            id: null,
            is_playing : false,
            time_left: null
          },
          disabled: false
        })
      }
      else {
        response.json()
        .then(newSong => {
          let time = Number(newSong.item.duration_ms) - Number(newSong.progress_ms)
          this.setState({
            song: {
              name: newSong.item.name,
              artists: newSong.item.artists,
              id: newSong.item.id,
              is_playing: newSong.is_playing,
              time_left: time
            },
            disabled: false
          })
        }) 
      }
    })
      
  }

  startPlayback = ()  => {
    this.setState({
      disabled: true
    })

    fetch(backendURI + "startPlayback")
    .then(
      setTimeout(this.updateCurrentlyPlaying, 1000)
    )
  }

  songCountdown = () => {
    setInterval(() => {

      let currentState = this.state

      if(currentState.song.time_left !== null) {
        console.log(this.state)
        let time = currentState.song.time_left - 1000

        currentState.song.time_left = time

        if(time <= 500) {
          this.updateCurrentlyPlaying()
        }
        else {
          this.setState(currentState)
        }
      }
    }, 1000)
  }
  
  stopPlayback = ()  => {
    this.setState({
      disabled: true
    })

    fetch(backendURI + "stopPlayback")
    .then(
      setTimeout(this.updateCurrentlyPlaying, 1000)
    )
  }

  StartPlayBackButton = (props) => {
    return (
      <button onClick={props.click} disabled = {this.state.disabled}> Start Player </button>
    );
  }

  StopPlayBackButton = (props) => {
    return (
      <button onClick={props.click} disabled = {this.state.disabled}> {props.text} </button>
    );
  }

  getState = () => {
    return this.state.is_active
  }

  render() {
    let button;
    let playbackState = this.state.song.is_playing
    if(playbackState) {
      let props = {
        click: this.stopPlayback,
        text: "Stop Player"
      }
      button = <this.StopPlayBackButton {... props} />
    }
    else {
      button = <this.StartPlayBackButton click = {this.startPlayback} />
    }
    let artistNames = this.state.song.artists.map(artist => artist.name)
    return (
      <div className='Player'>
        Currently playing: {artistNames.toString()}: {this.state.song.name}
        {button}
      </div>
    )
  }
}

export default PlaybackController;
