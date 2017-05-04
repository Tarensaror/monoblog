function submitHandler(event) {
	var command = document.getElementById('command');
	var output = document.getElementById('output');

	output.value += '\n' + command.value;
	command.value = '';
	output.scrollTop = output.scrollHeight;

	event.preventDefault();
}


document.getElementById('command_form').addEventListener('submit', submitHandler);