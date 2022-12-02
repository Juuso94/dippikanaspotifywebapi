import '../App.css';
import React, { Component } from 'react';
import {backendURI, msTominutes} from "../HelperFunctions.js"

class Queue extends Component {
  constructor() {
    super()
    this.state = {
      songs: []
    }
  }
  queueApi = "queue"

  componentDidMount() {
    this.fetchQueue()
  }

  fetchQueue = () => {

    fetch(backendURI + this.queueApi)
      .then(response =>{
        if(response.status == 200) {
          response.json()
          .then(jsonRes => {
            console.log(jsonRes.queue)
            this.setState({
              songs: jsonRes.queue.map(song => song)
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
      })
  }

  render() {
    let songs = this.state.songs.map( song => {
      let props = {songInfo: song}
      return ListItem(props)
    })
    return (
      <div className="QueueList">
        <h1 style={{color: 'white'}}>Jono</h1>
      <ul>
          {songs.slice(0, 10)}
        </ul>
      </div>
    )

  }
}

function ListItem(props) {
  let artistNames = props.songInfo.artists.map(artist => artist.name)
  return (
    <li>{} {artistNames.toString()}: {props.songInfo.name} {msTominutes(props.songInfo.duration_ms)}</li>
  );
}

export default Queue
