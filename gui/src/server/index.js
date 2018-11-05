// Modules
const express = require('express');
const bodyParser = require('body-parser');

const groups = require('./api/groups');
const boxes = require('./api/boxes');
const versions = require('./api/versions');
const providers = require('./api/providers');

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
server.use('/api/groups', groups);
server.use('/api/boxes', boxes);
server.use('/api/versions', versions);
server.use('/api/providers', providers);
// Default route
server.use((req, res) => {
    res.status(404).send();
});

server.listen(port, address, () => {
    console.log(`Listening on ${address}:${port}!`);
});