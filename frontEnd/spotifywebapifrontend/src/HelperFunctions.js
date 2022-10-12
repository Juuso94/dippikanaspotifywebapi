export function addSongToQueue(id) {
  const queueApi = "/addToQueue"
  const queryParam = encodeURI("?songURI=" + id)
  fetch(queueApi + queryParam)
    .then(console.log("Song "+ id + " added to que"))
}

let timer;

export function setTimer(timedFunction, timer) {
  if (timer !== null) {
    clearTimeout(timer)
  }
  timer = setTimeout(timedFunction, timer)
}