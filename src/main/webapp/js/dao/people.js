function listPeople(done, fail, always) {
	done = typeof done !== 'undefined' ? done : function() {};
	fail = typeof fail !== 'undefined' ? fail : function() {};
	always = typeof always !== 'undefined' ? always : function() {};
	
	$.ajax({
		url: 'rest/people',
		type: 'GET'
	})
	.done(done)
	.fail(fail)
	.always(always);
}

function addPerson(person, done, fail, always) {
	done = typeof done !== 'undefined' ? done : function() {};
	fail = typeof fail !== 'undefined' ? fail : function() {};
	always = typeof always !== 'undefined' ? always : function() {};
	
	$.ajax({
		url: 'rest/people',
		type: 'POST',
		data: person
	})
	.done(done)
	.fail(fail)
	.always(always);
}

function modifyPerson(person, done, fail, always) {
	done = typeof done !== 'undefined' ? done : function() {};
	fail = typeof fail !== 'undefined' ? fail : function() {};
	always = typeof always !== 'undefined' ? always : function() {};
	
	$.ajax({
		url: 'rest/people/' + person.id,
		type: 'PUT',
		data: person
	})
	.done(done)
	.fail(fail)
	.always(always);
}

function deletePerson(id, done, fail, always) {
	done = typeof done !== 'undefined' ? done : function() {};
	fail = typeof fail !== 'undefined' ? fail : function() {};
	always = typeof always !== 'undefined' ? always : function() {};
	
	$.ajax({
		url: 'rest/people/' + id,
		type: 'DELETE',
	})
	.done(done)
	.fail(fail)
	.always(always);
}