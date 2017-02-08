var PeopleView = (function() {
	var dao;
	
	// Referencia a this que permite acceder a las funciones públicas desde las funciones de jQuery.
	var self;
	
	var formId = 'people-form';
	var listId = 'people-list';
	var formQuery = '#' + formId;
	var listQuery = '#' + listId;
	
	function PeopleView(peopleDao, formContainerId, listContainerId) {
		dao = peopleDao;
		self = this;
		
		insertPeopleForm($('#' + formContainerId));
		insertPeopleList($('#' + listContainerId));
		
		this.init = function() {
			dao.listPeople(function(people) {
				$.each(people, function(key, person) {
					appendToTable(person);
				});
			});
			
			// La acción por defecto de enviar formulario (submit) se sobreescribe
			// para que el envío sea a través de AJAX
			$(formQuery).submit(function(event) {
				var person = self.getPersonInForm();
				
				if (self.isEditing()) {
					dao.modifyPerson(person,
						function(person) {
							$('#person-' + person.id + ' td.name').text(person.name);
							$('#person-' + person.id + ' td.surname').text(person.surname);
							self.resetForm();
						},
						showErrorMessage,
						self.enableForm
					);
				} else {
					dao.addPerson(person,
						function(person) {
							appendToTable(person);
							self.resetForm();
						},
						showErrorMessage,
						self.enableForm
					);
				}
				
				return false;
			});
			
			$('#btnClear').click(this.resetForm);
		};

		this.getPersonInForm = function() {
			var form = $(formQuery);
			return {
				'id': form.find('input[name="id"]').val(),
				'name': form.find('input[name="name"]').val(),
				'surname': form.find('input[name="surname"]').val()
			};
		};

		this.getPersonInRow = function(id) {
			var row = $('#person-' + id);

			if (row !== undefined) {
				return {
					'id': id,
					'name': row.find('td.name').text(),
					'surname': row.find('td.surname').text()
				};
			} else {
				return undefined;
			}
		};
		
		this.editPerson = function(id) {
			var row = $('#person-' + id);

			console.log(row);
			if (row !== undefined) {
				var form = $(formQuery);
				console.log(form);
				console.log(row.find('td.name').text());
				console.log(row.find('td.surname').text());
				
				form.find('input[name="id"]').val(id);
				form.find('input[name="name"]').val(row.find('td.name').text());
				form.find('input[name="surname"]').val(row.find('td.surname').text());
			}
		}

		this.isEditing = function() {
			return $(formQuery + ' input[name="id"]').val() != "";
		};

		this.disableForm = function() {
			$(formQuery + ' input').prop('disabled', true);
		};

		this.enableForm = function() {
			$(formQuery + ' input').prop('disabled', false);
		};

		this.resetForm = function() {
			$(formQuery)[0].reset();
			$(formQuery + ' input[name="id"]').val('');
			$('#btnSubmit').val('Crear');
		};
	}
	
	var insertPeopleList = function(parent) {
		parent.append(
			'<table id="' + listId + '">\
				<tr>\
					<th>Nombre</th>\
					<th>Apellido</th>\
					<th></th>\
					<th></th>\
				</tr>\
			</table>'
		);
	}

	var insertPeopleForm = function(parent) {
		parent.append(
			'<form id="' + formId + '">\
				<input name="id" type="hidden" value=""/>\
				<input name="name" type="text" value="" />\
				<input name="surname" type="text" value=""/>\
				<input id="btnSubmit" type="submit" value="Create"/>\
				<input id="btnClear" type="reset" value="Limpiar"/>\
			</form>'
		);
	}

	var createPersonRow = function(person) {
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

	var showErrorMessage = function(jqxhr, textStatus, error) {
		alert(textStatus + ": " + error);
	}

	var addRowListeners = function(person) {
		$('#person-' + person.id + ' a.edit').click(function() {
			self.editPerson(person.id);
			$('input#btnSubmit').val('Modificar');
		});
		
		$('#person-' + person.id + ' a.delete').click(function() {
			if (confirm('Está a punto de eliminar a una persona. ¿Está seguro de que desea continuar?')) {
				dao.deletePerson(person.id,
					function() {
						$('tr#person-' + person.id).remove();
					},
					showErrorMessage
				);
			}
		});
	}

	var appendToTable = function(person) {
		$(listQuery + ' > tbody:last')
			.append(createPersonRow(person));
		addRowListeners(person);
	}
	
	return PeopleView;
})();
