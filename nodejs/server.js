var express = require('express');

/* Settings */
var server_port = process.env.OPENSHIFT_NODEJS_PORT || 8080
var server_ip_address = process.env.OPENSHIFT_NODEJS_IP || '127.0.0.1'

var app = express();
app.use(express.json());
app.use(express.urlencoded());

/* Authentication */
var LocalStrategy   = require('passport-local').Strategy;
var cookieParser = require('cookie-parser');
app.use(cookieParser());

// Configuring Passport
var passport = require('passport');
var expressSession = require('express-session');

app.use(expressSession({secret: 'mySecretKey'}));
app.use(passport.initialize());
app.use(passport.session());

passport.use('login', new LocalStrategy({ passReqToCallback : true },
	function(req, username, password, done) {   
	    var user = {
	        "id": 1,
	        "username": username,
	        "password": password
	    };
		return done(null, user);
	})
);

passport.serializeUser(function(user, done) {
    done(null, user.username);
});

passport.deserializeUser(function(username, done) {    
    var user = {
        "id": 1,
        "username": username,
        "password": ""
    };
    done(null, user);
});

var isAuthenticated = function (req, res, next) {
	if (req.isAuthenticated()) {
		return next();
	}
	res.redirect('/isNotAuthenticated');
}

/* Start server */
app.listen(server_port, server_ip_address, function(){
  console.log("Listening on " + server_ip_address + ", server_port " + server_port)
});

/* Routing */

//login
app.post('/products/login', passport.authenticate('login'), function(req, res){
	res.status(200);
	res.json(message);
});

//login failure
app.get('/isNotAuthenticated', function(req, res){
	res.status(403);
	res.json(not_auth_message);
});

//logout
app.get('/products/signout', function(req, res) {
	req.logout();
	res.status(204);
	res.send("Wylogowano");
});

app.get('/', function (req, res) {
  res.send('Please contact: olszewskimar.pg@gmail.com!');
});

//get products
app.get('/products', isAuthenticated, function (req, res) {
  res.json(products);
});

//create product
app.post('/products', isAuthenticated, function (req, res) {

	if(req.body.name == undefined || req.body.name == "") {
  		res.send(406);
  	} else if(alreadyExist(req.body.name)) {
  		res.send(409);
  	} else {

  		var lon = req.body.lon;
  		var lat = req.body.lat;

		if(lon && lat) {

			lon = lon.replace("[", "").replace("]", ""); 
			lat = lat.replace("[", "").replace("]", ""); 

			var newProduct = new Product(req.body.name, 0, lon, lat);
		} else {
			var newProduct = new ProductWithoutCoordinates(req.body.name);
		}

  		products.push( newProduct );
  		res.status(201);
  		res.json(newProduct);
	}
});

//update product
app.put('/products/:id', isAuthenticated, function (req, res) {
	
	var id = req.params.id;

	if(findById(id) == undefined || req.body.amount == undefined || parseInt(req.body.amount) < 0) {
		res.send(404);
	}

	update(id, req.body.amount);
	res.send(204);
});

//delete
app.delete('/products/:id', isAuthenticated, function (req, res) {
	
	var id = req.params.id;

	if(findById(id) == undefined) {
		res.send(404); 
	}

  	remove(id);
	res.send(204);
});

//synchronization
app.post('/products/synchronize', function (req, res) {

	console.log(req.body.created);
	console.log(req.body.deleted);
	console.log(req.body.changed);

	// tworzenie nowych produktów
	JSON.parse(req.body.created).forEach(function(entry) {
		if(!alreadyExist(entry.name)) {

			var amount = 0;
			if(entry.delta > 0 ) {
				amount = entry.delta;
			}

			var lon = 0.0;
			var lat = 0.0;
			if(entry.lon) {
				lon = entry.lon;
			}
			if(entry.lat) {
				lat = entry.lat;
			}

			products.push( 
				new Product(entry.name, amount, lon, lat) 
			);
		};
	});
	
	// // //usunięcie produktów
	JSON.parse(req.body.deleted).forEach(function(entry) {
		remove(entry.id);
	});

	// //synchronizacja ilości produktów
	JSON.parse(req.body.changed).forEach(function(entry) {
		var toUpdate = findById(entry.id);
		if(toUpdate != undefined) {
			toUpdate.amount = toUpdate.amount + entry.delta;
			if(toUpdate.amount < 0) {
				toUpdate.amount = 0;
			}
		}
	});

	res.status(200);
    res.json(products);
});

/* Bussiness logic */

function SampleProduct(id, name, amount, lon, lat) {
  	this.id = id;
  	this.name = name;
  	this.amount = amount;
  	this.lon = lon;
  	this.lat = lat;
}

function ProductWithoutCoordinates(name) {
	if(products == undefined || products.length == 0) {
		this.id = 0;
	} else {
  		this.id = products[products.length-1].id+1;
	}
  	this.name = name;
  	this.amount = 0;
  	this.lon = 0.0;
  	this.lat = 0.0;
}

// function Product(name, lon, lat) {
// 	if(products == undefined || products.length == 0) {
// 		this.id = 0;
// 	} else {
//   		this.id = products[products.length-1].id+1;
// 	}
//   	this.name = name;
//   	this.amount = 0;
//   	this.lon = lon;
//   	this.lat = lat;
// }

function Product(name, amount, lon, lat) {
	if(products == undefined || products.length == 0) {
		this.id = 0;
	} else {
  		this.id = products[products.length-1].id+1;
	}
  	this.name = name;
  	this.amount = amount;
  	this.lon = lon;
  	this.lat = lat;
}

var products = [
	new SampleProduct(1, "Kapusta", 12, 54.32970854, 18.61510361), 
	new SampleProduct(2, "Kiełbasa", 18, 54.32970825, 18.61510360), 
	new SampleProduct(3, "Ananas", 6, 54.32920855, 18.61110301)
	]

var GUIDs = []

function alreadyExist(name){
	var exist = false;
	products.forEach(function(entry) {
    	if(name == entry.name) {
    		exist = true;
			return;
    	}
	});
	return exist;
}

function findById(id) {
	var product;
	products.forEach(function(entry) {
    	if(id == entry.id) {
    		product = entry;
			return;
    	}
	});
	return product;
}

function update(id, amount) {
	findById(id).amount = amount;
}

function remove(id) {
	for(var i = products.length - 1; i >= 0; i--) {
	    if(products[i].id == id) {
	       products.splice(i, 1);
	    }
	}
}

var message = {
	"message": "Success!"
};

var not_auth_message = {
	"message": "Dostęp zabroniony!"
};