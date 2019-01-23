
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const express = require('express');
const cors = require('cors');

const app = express();

app.use(cors({origin : true}));
admin.initializeApp(functions.config().firebase);

//16-12-18
//firestore cloud functions update may break the app
//fix
const firestore = admin.firestore();
const settings = { timestampsInSnapshots: true };
firestore.settings(settings);

exports.importWordsToFirestore = functions.https.onRequest((request, response) => {
		const db = admin.firestore();
		//words collection ref
		const wcRef = db.collection('words');
		var task;
		for(var i = 0; i < request.body.length; i++) {
			task = wcRef.doc().set(request.body[i]);
		}
		task.then(() => {
			response.status(200).send('done!');
		}).catch(err => {
			console.log(err);
			response.status(500).send(err);
		});
		/*return wcRef.doc().set({
			id : 0,
			word : 'genesis'
		}).then(() => {
			console.log('done!');
			response.status(200).send('done!');
		}).catch(err => {
			console.log(err);
			response.status(500).send(err);
		});*/
});

exports.updateIndexDaily = functions.https.onRequest((request, response) => {
	const db = admin.firestore();
	//app_info collection ref
	const appInfoRef = db.collection('app_info');
	//words collection ref
	const wcRef = db.collection('words');
	//word_pointer document ref
	const wordPointerRef = appInfoRef.doc('word_pointer');
	//get the pointer value
	var val = 0, word;
	wordPointerRef.get().then(doc => {
		if(doc.exists) {
			val = doc.data().value;
			val++; //updating the pointer value by 1
		}
		//get the word at the pointer value
		wcRef.where('id','==',val).get().then(querySnapshot => {
			querySnapshot.forEach(doc => {
				//only one doc must[should] be present
				word = doc.data().word;
			});
			wordPointerRef.set({
				value : val,
				word : word
			}).then(() => {
				console.log('done!');
				response.status(200).send('done!');
			}).catch(err => {
				console.log(err);
				response.status(500).send(err);
			});
		}).catch(err => {
			console.log(err);
			response.status(500).send(err);
		});
	}).catch(err => {
		console.log(err);
		response.status(500).send(err);
	});

});









