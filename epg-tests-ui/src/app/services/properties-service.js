import fetch from 'isomorphic-fetch';

function propertiesService () {
	return ({
		setPropertiesFile,
        getAllProperties,
        getProperty,
        saveProperty,
        uploadTemplate,
        getTemplate,
        saveTemplate,
        serializeObjectToQs
	});

    function serializeObjectToQs (obj) {
        let a = [];
        let qs = '';
        for(let p in obj) {
            if (obj.hasOwnProperty(p)) {
                a.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
            }
        }
        qs = a.join("&");
        return qs;
    };

	function setPropertiesFile (data, cb) {
		fetch(rest_url_prefix+'/set-properties-file', {
			method: 'POST',
            mode: 'no-cors',
            body: data
		})
        .then((response) => {
			return response.blob();
		})
        .then((json) => {
            cb(null, json);
        })
        .catch((err) => {
            console.log(err);
            cb(err, null);			
        });
	};

    function saveProperty(obj, cb) {
        fetch(rest_url_prefix+'/set-property', {
			method: 'POST',
            mode: 'no-cors',
            body: JSON.stringify(obj)
		})
        .then((response) => {
			return response.blob();
		})
        .then((json) => {
            cb(null, json);
        })
        .catch((err) => {
            console.log(err);
            cb(err, null);			
        });
    };

    function getProperty(obj, cb) {
        fetch(rest_url_prefix+'/get-property?'
            +serializeObjectToQs(obj), {
			method: 'GET'
		})
        .then((response) => {
			return response.json();
		})
        .then((json) => {
            cb(null, json);
        })
        .catch((err) => {
            console.log(err);
            cb(err, null);			
        });
    };

    function getAllProperties(cb) {
        fetch(rest_url_prefix+'/get-all-properties',{
			method: 'GET'
		})
        .then((response) => {
			return response.json();
		})
        .then((json) => {
            cb(null, json);
        })
        .catch((err) => {
            console.log(err);
            cb(err, null);			
        });
    };

    function uploadTemplate(data, cb) {
        fetch(rest_url_prefix + '/upload-template', {
			method: 'POST',
            mode: 'no-cors',
            body: data
		})
        .then((response) => {
			return response.blob();
		})
        .then((json) => {
            cb(null, json);
        })
        .catch((err) => {
            console.log(err);
            cb(err, null);			
        });
    };

    function getTemplate(obj, cb) {
        fetch(rest_url_prefix+'/template?'
            +serializeObjectToQs(obj), {
			method: 'GET'
		})
        .then((response) => {
			return response.json();
		})
        .then((json) => {
            cb(null, json);
        })
        .catch((err) => {
            console.log(err);
            cb(err, null);			
        });
    };

    function saveTemplate(data, cb) {
        fetch(rest_url_prefix+'/save-template', {
			method: 'POST',
            mode: 'no-cors',
            body: data
		})
        .then((response) => {
			return response.blob();
		})
        .then((json) => {
            cb(null, json);
        })
        .catch((err) => {
            console.log(err);
            cb(err, null);			
        });
    };
}

export default propertiesService;