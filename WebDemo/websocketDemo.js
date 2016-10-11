//Establish the WebSocket connection and set up event handlers
//var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");
var webSocket = new WebSocket("ws://localhost:4568/echo/");

webSocket.onmessage = function (msg) { updateChat(msg); };
webSocket.onclose = function () {  };

//Send message if "Send" is clicked
document.getElementById("send").addEventListener("click", function () {
    sendMessage(document.getElementById("message").value);
});

//Send message if enter is pressed in the input field
document.getElementById("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) { sendMessage(e.target.value); }
});

//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
    if (message !== "") {
        webSocket.send(message);
        document.getElementById("message").value = "";
    }
}

//Update the chat-panel, and the list of connected users
function updateChat(msg) {
    var data = JSON.parse(msg.data);
    //insert("chat", data.userMessage);
    document.getElementById("userlist").insertAdjacentHTML("afterbegin", "<li> Message was: " + data.userMessage + "</li>");
}
