import '../App.css';
import React, { Component } from 'react';
import {msTominutes, backendURI} from "../HelperFunctions.js"

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

  handleSearch = (queryParam) => {

    const searchQuery = encodeURI("?q=" + queryParam)

    this.setState({
      searchValue: queryParam
    })

    if( queryParam !== "") {
      fetch(backendURI + this.searchApi + searchQuery)
        .then(response =>{
          if(response.status == 200) {
            response.json()
            .then(jsonRes => {
              console.log(jsonRes.tracks.items)
              this.setState({
                songs: jsonRes.tracks.items.map(song => song)
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
    else {
      this.setState({
        songs: [],
        searchValue: queryParam
      })
    }
  }

  addSongToQueue =(uri, id) => {
    const queueApi = "addToQueue"
    const queryParam = encodeURI("?songURI=" + uri + "&songID=" + id)
    fetch(backendURI + queueApi + queryParam)
      .then(response => {
        if(response.status === 400) {
          response.text()
            .then(error => console.log(error))
        }
      })
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
    return (
      <div className="SearchBar">
        <h1 style={{color: 'white'}}>Search and add songs to queue</h1>
        <input type = "text" value={this.state.searchValue} id = "queryParam" name = "queryParam" placeholder='Search for artists or songs'
        onChange={value => this.handleSearch(value.target.value)}>
        </input>
        <div className="ButtonGroup">
          {songButtons.splice(0,15)}
        </div>
      </div>
    )

  }
}

function SongButton(props) {
  let artistNames = props.songInfo.artists.map(artist => artist.name)
  return (
    <button onClick={() => {if (window.confirm("Haluatko lisätä biisin " + props.songInfo.name + " jonoon"))
      props.onClick(props.songInfo.uri, props.songInfo.id)}}>
      {artistNames.toString()}: {props.songInfo.name} {msTominutes(props.songInfo.duration_ms)}</button>
  );
}

export default SearchField
