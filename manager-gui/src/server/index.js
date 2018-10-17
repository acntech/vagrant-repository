// Modules
const express = require('express');
const bodyParser = require('body-parser');

// Variables
const server = express();
const isProduction = process.env.NODE_ENV === 'production';
const address = '0.0.0.0';
const port = 8080;

if (isProduction) {
    server.use(express.static('dist'));
}

server.use(bodyParser.json());
server.use(bodyParser.urlencoded({ extended: true }));

// Configure backend
// Default route
server.use((req, res) => {
    res.status(404).send();
});


server.listen(port, address, () => {
    console.log(`Listening on ${address}:${port}!`);
});