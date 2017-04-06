var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var util = require('util');
var net = require('net');

var args = process.argv.slice(2);
var port = 3000;

if (args == '') {
    console.log('No urgument, using default port.');
} else {
    port = parseInt(args);
    if (isNaN(port)) {
        port = 3000;
        console.log("Can't parse port, switching to default port.");
    }
}

app.get('/', function(req, res){
  res.sendFile(__dirname + '/index.html');
});


http.listen(port, function(){
  console.log('listening on *:' + port);
});



io.on('connection', function(socket){
    var port;
    var serverAdress;
    var username;
    var server = new net.Socket();
    var isServerConnected = false;
    var connected = false;

    socket.on('send', function(data) {
        console.log("writing : " + data);
        try {
            server.write(data);
        } catch (e) {
            console.log(e);
            socket.emit('serverConnectionError');
        }
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
                connected = true;
                socket.emit('connectionRequestAnswer', "ok", 0);
            }).on("error", function(e) {
                if (connected) {
                    socket.emit('serverConnectionError');
                } else {
                    console.log("Can't connect to server.");
                    socket.emit('connectionRequestAnswer', "Impossible de joindre le serveur de Scrabble.", 1);
                }
            });

            server.on('data', function(data) {
                console.log("Received : " + data);
                socket.emit("receive", data.toString('utf8'));
            });

            server.on('close', function() {
                console.log('Server connection closed.');
                socket.emit('serverConnectionError');
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
