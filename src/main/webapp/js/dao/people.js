function listPeople(done, fail, always) {
	done = typeof done !== 'undefined' ? done : function() {};
	fail = typeof fail !== 'undefined' ? fail : function() {};
	always = typeof always !== 'undefined' ? always : function() {};
	
	$.getJSON('rest/people/list')
		.done(done)
		.fail(fail)
		.always(always);
}

function addPerson(person, done, fail, always) {
	done = typeof done !== 'undefined' ? done : function() {};
	fail = typeof fail !== 'undefined' ? fail : function() {};
	always = typeof always !== 'undefined' ? always : function() {};
	
	$.getJSON('rest/people/add', person)
		.done(done)
		.fail(fail)
		.always(always);
}

function modifyPerson(person, done, fail, always) {
	done = typeof done !== 'undefined' ? done : function() {};
	fail = typeof fail !== 'undefined' ? fail : function() {};
	always = typeof always !== 'undefined' ? always : function() {};
	
	$.getJSON('rest/people/modify', person)
		.done(done)
		.fail(fail)
		.always(always);
}

function deletePerson(id, done, fail, always) {
	done = typeof done !== 'undefined' ? done : function() {};
	fail = typeof fail !== 'undefined' ? fail : function() {};
	always = typeof always !== 'undefined' ? always : function() {};
	
	$.getJSON('rest/people/delete', { 'id': id })
		.done(done)
		.fail(fail)
		.always(always);
}