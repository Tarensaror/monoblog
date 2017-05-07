var Status = Object.freeze(
	{
		INIT: 'INIT',
		NAME: 'NAME',
		PASS: 'PASS',
		AUTH: 'AUTH',
		BLCK: 'BLCK',
		READY: 'READY'
	}
);

var state = Status.NAME;
var name = null;

function write_output(message) {
	var output = document.getElementById('output');
	
	output.value += '\n' + message;
	output.scrollTop = output.scrollHeight;
}

function send_input() {
	var command = document.getElementById('command');
	
	switch(state) {
	case Status.NAME:
		name = command.value;
		state = Status.PASS;
		command.type = 'password';
		break;
	case Status.PASS:
		connect(name, command.value);
		command.value = '';
		command.type = 'text';
		state = Status.BLCK;
		break;
	case Status.BLCK:
		write_output(command.value);
		break;
	case Status.READY:
		write_output(command.value);
		send_request(command.value);
		break;
	}
	
	command.value = '';
	
	alert('status is ' + state);
	
}

function submitHandler(event) {
	send_input();
	event.preventDefault();
}

document.getElementById('command_form').addEventListener('submit', submitHandler);







var client;

function connect() {
    var socket = new SockJS('/ws');

    client = Stomp.over(socket);
    client.connect({}, function(frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        state = Status.READY;
        alert('set state to auth');
        client.subscribe('/user/queue/replies', function(reply) {
        alert('received reply');
        	state = Status.READY
            write_output(JSON.parse(reply.body).content);
        });
    });
}

function send_request(command) {
alert('sending request');
	state = Status.BLCK;
    client.send('/app/command', {}, JSON.stringify({'name': command}));
alert('request sent');
}
