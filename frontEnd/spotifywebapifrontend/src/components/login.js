import React, { useState, useEffect, Component } from 'react';

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
    this.state = {logged: false}
    console.log(this.state.logged)
    fetch("/checkToken")
    .then(response => {
      console.log(response)
      if (response.ok) {this.setState({logged: true})}
    })
    console.log(this.state.logged)
  }

  async fetchToken () {
    await fetch("/login")
      .then((response) => response.text())
      .then(response => {
        window.location.assign(response)
      })
  }

  render() {
    return (
      <ul>
      <h1>Login to spotify</h1>
      { this.state.logged
        ? <h2>You are already logged in</h2>
        : <button onClick={this.fetchToken}> Login </button>
      }
      </ul>
    )
  }
}   

export default AdminLogin;