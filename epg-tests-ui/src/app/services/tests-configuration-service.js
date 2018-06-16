import fetch from 'isomorphic-fetch';

function testsConfigurationService () {
	return ({
        getFileUploadTime,
        uploadXMLFile,
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

    function getFileUploadTime (obj, cb) {
        fetch(rest_url_prefix+'/uploaded-file-time?'+
        this.serializeObjectToQs(obj), {
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

    function uploadXMLFile (data, cb) {
        fetch(rest_url_prefix+'/upload-xml-file', {
			method: 'POST',
            //mode: 'no-cors',
            body: data
		})
        .then((response) => {
			return response.json();
		})
        .then((json) => {
            if (!json.error) {
                cb(null, json);
            } else {
                cb(json, null);
            }
        })
        .catch((err) => {
            console.log(err);
            cb(err, null);			
        });
    }
}

export default testsConfigurationService;