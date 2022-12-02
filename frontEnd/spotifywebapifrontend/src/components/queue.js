import '../App.css';
import React, { Component } from 'react';
import {backendURI, msTominutes} from "../HelperFunctions.js"

class Queue extends Component {
  constructor() {
    super()
    this.state = {
      songs: [],
    }
  }
  queueApi = "queue"
  latestAddedSong

  componentDidMount() {
    this.fetchQueue()
  }

  fetchQueue = () => {

    fetch(backendURI + this.queueApi)
      .then(response =>{
        if(response.status == 200) {
          response.json()
          .then(jsonRes => {
            this.latestAddedSong = jsonRes.songID
            this.setState({
              songs: jsonRes.queue.map(song => song),
            })
          });
        }
        else {
          response.text()
          .then(error => {
            console.log(error)
            this.setState({songs: []})
          })
        }
        console.log(this.state)
      })
  }

  render() {
    let index = this.state.songs.findIndex(song => this.latestAddedSong === song.id)
    let addedSongs
    let defaultSongs

    if(index >= 10) {
      addedSongs = this.state.songs.slice(0, 10).map( song => {
        let props = {songInfo: song}
        return ListItem(props)
      })

      defaultSongs = []
    }
    else if(index === -1) {
      addedSongs = []
      defaultSongs = this.state.songs.slice(0, 11).map( song => {
        let props = {songInfo: song}
        return ListItem(props)
      })
    }

    else {
      addedSongs = this.state.songs.slice(0, index + 1).map( song => {
        let props = {songInfo: song}
        return ListItem(props)
      })

      defaultSongs = this.state.songs.slice(index + 1, 10).map( song => {
        let props = {songInfo: song}
        return ListItem(props)
      })
    }


    return (
      <div className="QueueList">
        <h1 style={{color: 'white'}}>Upcoming songs</h1>
        <h3 style={{color: 'white'}}>Added to queue</h3>
      <ul>
        {addedSongs}
      </ul>
      <h3 style={{color: 'white'}}>Default queue</h3>
      <ul>
        {defaultSongs}
      </ul>
      </div>
    )

  }
}

function ListItem(props) {
  let artistNames = props.songInfo.artists.map(artist => artist.name)
  return (
    <li key={props.songInfo.id}>{} {artistNames.toString()}: {props.songInfo.name} {msTominutes(props.songInfo.duration_ms)}</li>
  );
}

export default Queue
