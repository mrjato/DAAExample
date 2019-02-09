var PeopleDAO = (function() {
    var resourcePath = "rest/people/";
    var requestByAjax = function(data, done, fail, always) {
	done = typeof done !== 'undefined' ? done : function() {};
	fail = typeof fail !== 'undefined' ? fail : function() {};
	always = typeof always !== 'undefined' ? always : function() {};

	let authToken = localStorage.getItem('authorization-token');
	if (authToken !== null) {
	    data.beforeSend = function(xhr) {
		xhr.setRequestHeader('Authorization', 'Basic ' + authToken);
	    };
	}

	$.ajax(data).done(done).fail(fail).always(always);
    };

    function PeopleDAO() {
	this.listPeople = function(done, fail, always) {
	    requestByAjax({
		url : resourcePath,
		type : 'GET'
	    }, done, fail, always);
	};

	this.addPerson = function(person, done, fail, always) {
	    requestByAjax({
		url : resourcePath,
		type : 'POST',
		data : person
	    }, done, fail, always);
	};

	this.modifyPerson = function(person, done, fail, always) {
	    requestByAjax({
		url : resourcePath + person.id,
		type : 'PUT',
		data : person
	    }, done, fail, always);
	};

	this.deletePerson = function(id, done, fail, always) {
	    requestByAjax({
		url : resourcePath + id,
		type : 'DELETE',
	    }, done, fail, always);
	};
    }

    return PeopleDAO;
})();