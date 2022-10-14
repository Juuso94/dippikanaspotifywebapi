import '../App.css';
import React, { Component } from 'react';
import {addSongToQueue, backendURI} from "../HelperFunctions.js"

class SearchField extends Component {
  constructor() {
    super()
    this.playbackRef = React.createRef()
    this.state = {
      songs: [],
      searchValue: ""
    }
  }
  searchApi = "search"

  componentDidUpdate() {
    console.log(this.state)
  }

  handleSearch = (queryParam) => {

    const searchQuery = encodeURI("?q=" + queryParam)

    this.setState({
      searchValue: queryParam
    })

    if( queryParam !== "") {
      fetch(backendURI + this.searchApi + searchQuery)
        .then(response => response.json())
        .then(jsonRes => {
          console.log(jsonRes.tracks.items)
          this.setState({
            songs: jsonRes.tracks.items.map(song => song)
          })
      });
    }
    else {
      this.setState({
        songs: [],
        searchValue: queryParam
      })
    } 
  }

  addSongToQueue =(id) => {
    const queueApi = "addToQueue"
    const queryParam = encodeURI("?songURI=" + id)
    fetch(backendURI + queueApi + queryParam)
    this.setState({
      songs: [],
      searchValue: ""
    })
  }

  render() {
    let songButtons = this.state.songs.map( song => {
      let props = {songInfo: song, onClick: this.addSongToQueue}
      return SongButton(props)
    })
    console.log(songButtons)
    return (
      <div className="SearchBar">
        <input type = "text" value={this.state.searchValue} id = "queryParam" name = "queryParam" placeholder='Search for artists or songs'
        onChange={value => this.handleSearch(value.target.value)}>
        </input>
        <div className="ButtonGroup">
          {songButtons}
        </div>
      </div>
    )
    
  }
}

function SongButton(props) {
  let artistNames = props.songInfo.artists.map(artist => artist.name)
  return (
    <button onClick={() => {if (window.confirm("Haluatko lisätä biisin " + props.songInfo.name + " jonoon")) props.onClick(props.songInfo.uri)}} >
      {artistNames.toString()}: {props.songInfo.name} </button>
  );
}

export default SearchField
