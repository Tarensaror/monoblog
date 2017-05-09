var Status = Object.freeze(
	{
		READY: 'READY',
		BLOCKED: 'BLOCKED'
	}
);

var state = Status.BLOCKED;

function write_output(message, newline = true) {
	var output = document.getElementById('output');
	
	output.value += message + (newline ? '\n' : '');
	output.scrollTop = output.scrollHeight;
}

function send_input() {
	var command_element = document.getElementById('command');
	var command = command_element.value;
	command_element.value = '';
	
	switch(state) {
	case Status.READY:
		if (!send_input.hide) write_output(command);
		send_request(command);
		break;
	case Status.BLOCKED:
		if (!send_input.hide) write_output(command);
		break;
	}
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
        state = Status.READY;
        write_output('MAINFRAME CONNECTED.');

        client.subscribe('/user/queue/replies', function(reply) {
        	state = Status.READY
        	var data = JSON.parse(reply.body);
            write_output(data.message);
            
            if (data.success && data.command == 'login') token = data.userdata;
        });
    });
}


connect();






function login_process(input) {
	if (typeof login_process.state == 'undefined') login_process.state = 'INIT';
	
	switch(login_process.state) {
	case 'INIT':
		write_output('Name: ', false);
		login_process.state = 'NAME';
		request_handler = login_process;
		break;
	case 'NAME':
		write_output('Password: ', false);
		login_process.state = 'PASSWORD';
		login_procecss.name = input;
		document.getElementById('command').type = 'password';
		send_input.hide = true;
		break;
	case 'PASSWORD':
		login_process.state = 'INIT';
		document.getElementById('command').type = 'text';
		send_input.hide = false;
		request_handler = default_command_handler;
		remote_process({'command' : 'login', 'arguments' : login_process.name + ' ' + input});
		login_process.name = null;
		break;
	}
}

function register_process(input) {
	if (typeof register_process.state == 'undefined') register_process.state = 'INIT';
	
	switch(register_process.state) {
	case 'INIT':
		write_output('Name: ', false);
		register_process.state = 'NAME';
		request_handler = register_process;
		break;
	case 'NAME':
		write_output('Password: ', false);
		register_process.state = 'PASSWORD';
		register_process.name = input;
		document.getElementById('command').type = 'password';
		send_input.hide = true;
		break;
	case 'PASSWORD':
		write_output('Repeat password: ', false);
		register_process.state = 'PASSWORD_REPEAT';
		register_process.password = input;
		break;
	case 'PASSWORD_REPEAT':
		register_process.state = 'INIT';
		document.getElementById('command').type = 'text';
		send_input.hide = false;
		request_handler = default_command_handler;
		remote_process({'command' : 'register', 'arguments' : register_process.name + ' ' + register_process.password + ' ' + input});
		register_process.name = register_process.password = null;
		break;
	}
}

function remote_process(command) {
	state = Status.BLOCKED;
	command.token = token;
	client.send('/app/command', {}, JSON.stringify(command));
}

function parse_command_line(command_line) {
	command_line = command_line.trim();
	var space_index = command_line.indexOf(' ');
	
	var command = command_line.substr(0, space_index);
	var arguments = command_line.substr(space_index + 1).trimLeft();
	
	if (space_index == -1) arguments = '';
	if (command == '') return {};
	
	return {'command' : command, 'arguments' : arguments};
}

function launch_process(command) {
	switch(command.command) {
	case 'login': login_process(); break;
	case 'register': register_process(); break;
	default: remote_process(command);
	}
}

function default_command_handler(command) {
	var tmp = parse_command_line(command);
	launch_process(tmp);
}






var request_handler = default_command_handler;

function send_request(command) {
	request_handler(command);
}
