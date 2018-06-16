import fetch from 'isomorphic-fetch';

function alertsService () {
	return ({
        getAlerts,
        getCompletedByTestCase,
        getLastRun,
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

    function getAlerts(cb) {
        fetch(rest_url_prefix+'/alerts', {
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

    function getCompletedByTestCase(id, cb) {
        fetch(rest_url_prefix+'/completed-by-tc?id='+encodeURIComponent(id), {
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

    function getLastRun(cb) {
        fetch(rest_url_prefix+'/last-run', {
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

export default alertsService;