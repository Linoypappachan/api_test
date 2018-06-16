import fetch from 'isomorphic-fetch';

function testDetailsService () {
	return ({
        getDetails,
        getTestCasesMap,
        serializeObjectToQs
	});

    function serializeObjectToQs (obj) {
        let a = [];
        let qs = '';
        for(let p in obj) {
        if (obj.hasOwnProperty(p)) {
            a.push(p + "=" + obj[p]);
        }
        }
        qs = a.join("&");
        return qs;
    };

    function getDetails(obj, cb) {
        fetch(rest_url_prefix+'/test_details?'
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

    function getTestCasesMap(cb) {
        fetch(rest_url_prefix+'/testcases-map', {
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
}

export default testDetailsService;