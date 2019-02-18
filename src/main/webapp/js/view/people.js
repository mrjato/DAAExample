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
			},
			function() {
			    	alert('No has sido posible acceder al listado de personas.');
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

			if (row !== undefined) {
				var form = $(formQuery);
				
				form.find('input[name="id"]').val(id);
				form.find('input[name="name"]').val(row.find('td.name').text());
				form.find('input[name="surname"]').val(row.find('td.surname').text());
				
				$('input#btnSubmit').val('Modificar');
			}
		};
		
		this.deletePerson = function(id) {
			if (confirm('Está a punto de eliminar a una persona. ¿Está seguro de que desea continuar?')) {
				dao.deletePerson(id,
					function() {
						$('tr#person-' + id).remove();
					},
					showErrorMessage
				);
			}
		};

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
	};
	
	var insertPeopleList = function(parent) {
		parent.append(
			'<table id="' + listId + '" class="table">\
				<thead>\
					<tr class="row">\
						<th class="col-sm-4">Nombre</th>\
						<th class="col-sm-5">Apellido</th>\
						<th class="col-sm-3">&nbsp;</th>\
					</tr>\
				</thead>\
				<tbody>\
				</tbody>\
			</table>'
		);
	};

	var insertPeopleForm = function(parent) {
		parent.append(
			'<form id="' + formId + '" class="mb-5 mb-10">\
				<input name="id" type="hidden" value=""/>\
				<div class="row">\
					<div class="col-sm-4">\
						<input name="name" type="text" value="" placeholder="Nombre" class="form-control" required/>\
					</div>\
					<div class="col-sm-5">\
						<input name="surname" type="text" value="" placeholder="Apellido" class="form-control" required/>\
					</div>\
					<div class="col-sm-3">\
						<input id="btnSubmit" type="submit" value="Crear" class="btn btn-primary" />\
						<input id="btnClear" type="reset" value="Limpiar" class="btn" />\
					</div>\
				</div>\
			</form>'
		);
	};

	var createPersonRow = function(person) {
		return '<tr id="person-'+ person.id +'" class="row">\
			<td class="name col-sm-4">' + person.name + '</td>\
			<td class="surname col-sm-5">' + person.surname + '</td>\
			<td class="col-sm-3">\
				<a class="edit btn btn-primary" href="#">Editar</a>\
				<a class="delete btn btn-warning" href="#">Eliminar</a>\
			</td>\
		</tr>';
	};

	var showErrorMessage = function(jqxhr, textStatus, error) {
		alert(textStatus + ": " + error);
	};

	var addRowListeners = function(person) {
		$('#person-' + person.id + ' a.edit').click(function() {
			self.editPerson(person.id);
		});
		
		$('#person-' + person.id + ' a.delete').click(function() {
			self.deletePerson(person.id);
		});
	};

	var appendToTable = function(person) {
		$(listQuery + ' > tbody:last')
			.append(createPersonRow(person));
		addRowListeners(person);
	};
	
	return PeopleView;
})();
