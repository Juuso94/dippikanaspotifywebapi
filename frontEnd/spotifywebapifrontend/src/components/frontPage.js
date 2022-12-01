import { Component } from 'react';
import '../App.css';
import PlaybackController from "./playbackController.js"
import SearchField from './searchComponent';


class FrontPage extends Component {
  constructor () {
    super()
    this.state = {
      componentId: 1
    }
    }

    render() {
      let component

      if(this.state.componentId == 1) {
        component = SearchField
      }

      return (
        <div className='FrontPage'>
          <div className='Navbar'>
            <button>haku</button>
            <button>jono</button>
          </div>
          { this.state.componentId === 1 ?
            <SearchField />
            : this.state.componentId === 2 ?
            <div>homopaskaa</div>
            : null
          }

          <PlaybackController />
        </div>
      )
    }
}

export default FrontPage;