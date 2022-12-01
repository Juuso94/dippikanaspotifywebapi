export function addSongToQueue(id) {
  const queueApi = "addToQueue"
  const queryParam = encodeURI("?songURI=" + id)
  fetch(backendURI + queueApi + queryParam)
    .then(console.log("Song "+ id + " added to que"))
}

export function msTominutes(milliseconds) {
  var minutes = Math.floor(milliseconds / 60000);
  var seconds = ((milliseconds % 60000) / 1000).toFixed(0);
  return (
    seconds === 60 ?
    (minutes+1) + ":00" :
    minutes + ":" + (seconds < 10 ? "0" : "") + seconds
  );
}

export const backendURI = process.env.REACT_APP_backendURI