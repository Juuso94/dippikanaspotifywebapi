import { Component } from 'react';
import '../App.css';
import PlaybackController from "./playbackController.js"
import SearchField from './searchComponent';
import Queue from './queue';


class FrontPage extends Component {
  constructor () {
    super()
    this.state = {
      componentId: 1
    }
  }

  switchComponent = (id) => {
    this.setState({
      componentId: id
    })
  }


  render() {

    return (
      <div className='FrontPage'>
        <div className='Navbar'>
          <button onClick={() => this.switchComponent(1)}>Search</button>
          <button onClick={() => this.switchComponent(2)}>Queue</button>
        </div>
        { this.state.componentId === 1 ?
          <SearchField />
          : this.state.componentId === 2 ?
          <Queue />
          : null
        }

        <PlaybackController />
      </div>
    )
  }
}

export default FrontPage;