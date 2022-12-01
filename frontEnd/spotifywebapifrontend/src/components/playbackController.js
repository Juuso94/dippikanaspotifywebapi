import '../App.css';
import React, { Component } from 'react';
import {backendURI, msTominutes} from "../HelperFunctions.js"

class PlaybackController extends Component {
  constructor() {
    super();
    this.state = {
      song: {
        name: null,
        artists: [],
        id: null,
        is_playing : false,
        duration_ms: null,
        progress_ms: null,
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
            duration_ms: null,
            progress_ms: null,
          },
          disabled: false
        })
      }
      else if (response.status === 400) {
        this.setState({
          song: {
            name: null,
            artists: [],
            id: null,
            is_playing : false,
            duration_ms: null,
            progress_ms: null,
          },
          disabled: false
        })
      }
      else {
        response.json()
        .then(newSong => {

          this.setState({
            song: {
              name: newSong.item.name,
              artists: newSong.item.artists,
              id: newSong.item.id,
              is_playing: newSong.is_playing,
              duration_ms: newSong.item.duration_ms,
              progress_ms: newSong.progress_ms,
            },
            disabled: false
          })
        })
      }
    })

  }

  songCountdown = () => {
    setInterval(() => {

      let currentState = this.state

      if(currentState.song.is_playing) {
        currentState.song.progress_ms += 1000
        let time = currentState.song.duration_ms - currentState.song.progress_ms

        if(time <= 500) {
          this.updateCurrentlyPlaying()
        }
        else {
          this.setState(currentState)
        }
      }
    }, 1000)
  }

  startPlayback = ()  => {
    this.setState({
      disabled: true
    })

    fetch(backendURI + "startPlayback")
    .then(
      setTimeout(this.updateCurrentlyPlaying, 1500)
    )
  }

  stopPlayback = ()  => {
    this.setState({
      disabled: true
    })

    fetch(backendURI + "stopPlayback")
    .then(
      setTimeout(this.updateCurrentlyPlaying, 1500)
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
        Currently playing {artistNames.toString()}: {this.state.song.name} <br />
        {msTominutes(this.state.song.progress_ms)}/{msTominutes(this.state.song.duration_ms)}
        {button}
      </div>
    )
  }
}

export default PlaybackController;
