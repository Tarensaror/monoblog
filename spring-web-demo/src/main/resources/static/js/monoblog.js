var Status = Object.freeze(
	{
		READY: 'READY',
		BLOCKED: 'BLOCKED'
	}
);

var state = Status.BLOCKED;

function write_output(message) {
	var output = document.getElementById('output');
	
	output.value += '\n' + message;
	output.scrollTop = output.scrollHeight;
}

function send_input() {
	var command = document.getElementById('command');
	
	switch(state) {
	case Status.READY:
		write_output(command.value);
		send_request(command.value);
		break;
	case Status.BLOCKED:
		write_output(command.value);
		break;
	}
	
	command.value = '';	
}

function submitHandler(event) {
	send_input();
	event.preventDefault();
}

document.getElementById('command_form').addEventListener('submit', submitHandler);







var client, token;

function connect() {
	write_output('MAINFRAME CONNECTING.');

    var socket = new SockJS('/ws');

    client = Stomp.over(socket);
    client.connect({}, function(frame) {
        setConnected(true);
        state = Status.READY;
        write_output('MAINFRAME CONNECTED.');
        console.log('Connected: ' + frame);
        client.subscribe('/user/queue/replies', function(reply) {
        	state = Status.READY
            write_output(JSON.parse(reply.body).message);
        });
    });
}


connect();




function parse_command_line(command_line) {
	command_line = command_line.trim();
	var command = command_line.substr(0, command_line.indexOf(' '));
	var arguments = command_line.substr(command_line.indexOf(' ') + 1);
	
	if (command == '') command = arguments;
	if (command == '') return {};
	
	return {'command' : command, 'arguments' : arguments};
}

function default_command_handler(command) {
	var tmp = parse_command_line(command);
    client.send('/app/command', {}, JSON.stringify({'command' : tmp.command, 'arguments' : tmp.arguments, 'token' : tmp.token}));
}



var request_handler = default_command_handler;

function send_request(command) {
	state = Status.BLOCKED;
	request_handler(command);
}
