var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var util = require('util');
var net = require('net');

var port;
var serverAdress;
var client = new net.Socket();

app.get('/', function(req, res){
  res.sendFile(__dirname + '/index.html');
});


http.listen(3000, function(){
  console.log('listening on *:3000');
});



io.on('connection', function(socket){

    socket.on('serverConnectionRequest', function(r_address, r_port) {
        if (r_address != undefined && r_port != undefined) {
            port = r_port;
            serverAdress = r_address;

            initSocket();
            io.emit('connectionRequestAnswer', "ok", 0);
        }
        else {
            io.emit('connectionRequestAnswer', "ko", 1);
        }
    });
});

function initSocket() {

}



// client.connect(42666, '127.0.0.1', function() {
// 	console.log('Connected');
// 	client.write('Hello, server! Love, Client.');
// });
//
// client.on('data', function(data) {
// 	console.log('Received: ' + data);
// 	client.destroy(); // kill client after server's response
// });
//
// client.on('close', function() {
// 	console.log('Connection closed');
// });
