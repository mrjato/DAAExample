var peopleFormId = 'people-form';
var peopleListId = 'people-list';
var peopleFormQuery = '#' + peopleFormId;
var peopleListQuery = '#' + peopleListId;

function insertPeopleList(parent) {
	parent.append(
		'<table id="' + peopleListId + '">\
			<tr>\
				<th>Nombre</th>\
				<th>Apellido</th>\
				<th></th>\
				<th></th>\
			</tr>\
		</table>'
	);
}

function insertPeopleForm(parent) {
	parent.append(
		'<form id="' + peopleFormId + '">\
			<input name="id" type="hidden" value=""/>\
			<input name="name" type="text" value="" />\
			<input name="surname" type="text" value=""/>\
			<input id="btnSubmit" type="submit" value="Create"/>\
			<input id="btnClear" type="reset" value="Limpiar"/>\
		</form>'
	);
}

function createPersonRow(person) {
	return '<tr id="person-'+ person.id +'">\
		<td class="name">' + person.name + '</td>\
		<td class="surname">' + person.surname + '</td>\
		<td>\
			<a class="edit" href="#">Edit</a>\
		</td>\
		<td>\
			<a class="delete" href="#">Delete</a>\
		</td>\
	</tr>';
}

function formToPerson() {
	var form = $(peopleFormQuery);
	return {
		'id': form.find('input[name="id"]').val(),
		'name': form.find('input[name="name"]').val(),
		'surname': form.find('input[name="surname"]').val()
	};
}

function personToForm(person) {
	var form = $(peopleFormQuery);
	form.find('input[name="id"]').val(person.id);
	form.find('input[name="name"]').val(person.name);
	form.find('input[name="surname"]').val(person.surname);
}

function rowToPerson(id) {
	var row = $('#person-' + id);

	return {
		'id': id,
		'name': row.find('td.name').text(),
		'surname': row.find('td.surname').text()
	};
}

function isEditing() {
	return $(peopleFormQuery + ' input[name="id"]').val() != "";
}

function disableForm() {
	$(peopleFormQuery + ' input').prop('disabled', true);
}

function enableForm() {
	$(peopleFormQuery + ' input').prop('disabled', false);
}

function resetForm() {
	$(peopleFormQuery)[0].reset();
	$(peopleFormQuery + ' input[name="id"]').val('');
	$('#btnSubmit').val('Crear');
}

function showErrorMessage(jqxhr, textStatus, error) {
	alert(textStatus + ": " + error);
}

function addRowListeners(person) {
	$('#person-' + person.id + ' a.edit').click(function() {
		personToForm(rowToPerson(person.id));
		$('input#btnSubmit').val('Modificar');
	});
	
	$('#person-' + person.id + ' a.delete').click(function() {
		if (confirm('Está a punto de eliminar a una persona. ¿Está seguro de que desea continuar?')) {
			deletePerson(person.id,
				function() {
					$('tr#person-' + person.id).remove();
				},
				showErrorMessage
			);
		}
	});
}

function appendToTable(person) {
	$(peopleListQuery + ' > tbody:last')
		.append(createPersonRow(person));
	addRowListeners(person);
}

function initPeople() {
	// getScript permite importar otro script. En este caso, se importan las
	// funciones de acceso a datos.
	$.getScript('js/dao/people.js', function() {
		listPeople(function(people) {
			$.each(people, function(key, person) {
				appendToTable(person);
			});
		});
		
		// La acción por defecto de enviar formulario (submit) se sobreescribe
		// para que el envío sea a través de AJAX
		$(peopleFormQuery).submit(function(event) {
			var person = formToPerson();
			
			if (isEditing()) {
				modifyPerson(person,
					function(person) {
						$('#person-' + person.id + ' td.name').text(person.name);
						$('#person-' + person.id + ' td.surname').text(person.surname);
						resetForm();
					},
					showErrorMessage,
					enableForm
				);
			} else {
				addPerson(person,
					function(person) {
						appendToTable(person);
						resetForm();
					},
					showErrorMessage,
					enableForm
				);
			}
			
			return false;
		});
		
		$('#btnClear').click(resetForm);
	});
};
