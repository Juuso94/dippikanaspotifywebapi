import React, { Component } from 'react';
import {backendURI} from "../HelperFunctions.js"

function AdminLogin() {
  return (
    <div className="AdminLogin">
      <SpotifyLogin />
    </div>
  );
}

class SpotifyLogin extends Component {
  constructor () {
    super ()
    this.state = {logged: false, passu: ""}
    fetch(backendURI + "checkToken")
    .then(response => {
      if (response.ok) {this.setState({logged: true, passu: ""})}
    })
  }

  fetchToken = () => {
    let queryParam = encodeURI("?kekW=" + this.state.passu)
    fetch(backendURI + "login" + queryParam)
      .then((response) => response.text())
      .then(response => {
        if(response === "vitun pelle kuole") {
          window.alert("Kuopion yliopistollinen sairaala")
        }
        else {
          window.location.assign(response)
        }
        
      })
  }
  logout = () => {
    fetch(backendURI + "logout").then(response => {
      if (response.ok)  {this.setState({logged: false, passu: ""})}
    })
  }

  handleChange = (event) => {
    this.setState({logged:false, passu: event.target.value});
  }

  render() {
    return (
      <div>
      <h1>Login to spotify</h1>
      { this.state.logged
        ? <button onClick={this.logout}> Logout </button>
        : <div>
        <input type="text" value={this.state.passu} onChange={this.handleChange} />
        <button onClick={this.fetchToken}> Login </button>
        </div>
          
          
      }
      </div>
    )
  }
}   

export default AdminLogin;