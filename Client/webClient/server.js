var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var util = require('util');
var net = require('net');

app.get('/', function(req, res){
  res.sendFile(__dirname + '/index.html');
});


http.listen(3000, function(){
  console.log('listening on *:3000');
});



io.on('connection', function(socket){
    var port;
    var serverAdress;
    var username;
    var server = new net.Socket();
    var isServerConnected = false;

    socket.on('send', function(data) {
        console.log("writing : " + data);
        server.write(data);
    });

    socket.on('serverConnectionRequest', function(r_address, r_port, r_username) {
        if (r_address != undefined && r_port != undefined && r_username != undefined) {
            if (r_port < 0 || r_port > 65536) {
                socket.emit('connectionRequestAnswer', "Numéro de port incorrect.", 1);
                return;
            }


            port = r_port;
            serverAdress = r_address;
            username = r_username;



            server = net.createConnection(port, serverAdress).on("connect", function(e) {
                console.log("Server connection succesful");

                socket.emit('connectionRequestAnswer', "ok", 0);
            }).on("error", function(e) {
                console.log("Can't connect to server.");
                socket.emit('connectionRequestAnswer', "Impossible de joindre le serveur de Scrabble.", 1);
            });

            server.on('data', function(data) {
                console.log("Received : " + data);
                socket.emit("receive", data.toString('utf8'));
            });



        } else {
            socket.emit('connectionRequestAnswer', 'Too few args.', 1);
        }
    });

    socket.on('disconnect', function() {
        console.log('Disconnection.');
        server.destroy();
    });
});


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
