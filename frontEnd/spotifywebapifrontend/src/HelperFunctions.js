export function addSongToQueue(id) {
  const queueApi = "addToQueue"
  const queryParam = encodeURI("?songURI=" + id)
  fetch(backendURI + queueApi + queryParam)
    .then(console.log("Song "+ id + " added to que"))
}

export const backendURI = process.env.REACT_APP_backendURI