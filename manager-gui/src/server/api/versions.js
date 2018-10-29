// Modules
const express = require('express');
// Variables
const router = express.Router();
const versions = require('../data/versions.json');
const providers = require('../data/providers.json');

router.get('/:id', (req, res) => {
    const id = req.params.id;

    if (!id) {
        const error = createError(404, 'Not Found', 'Version ID not set', '/api/versions/:id');
        res.status(404).send(error);
    }

    const entity = versions.find(e => e.id == id);

    if (entity) {
        res.send(entity);
    } else {
        res.status(204).send();
    }
});

router.get('/:id/providers', (req, res) => {
    const id = req.params.id;
    const name = req.query.name;

    if (!id) {
        const error = createError(404, 'Not Found', 'Version ID not set', '/api/versions/:id/providers');
        res.status(404).send(error);
    }

    if (name) {
        const entities = providers.filter(e => e.version.id == id && e.name === name);
        res.send(entities);
    } else {
        const entities = providers.filter(e => e.version.id == id);
        res.send(entities);
    }
});

const createError = (status, error, message, path) => {
    return {
        timestamp: (new Date).toUTCString(),
        status: status,
        error: error,
        message: message,
        path: path
    };
}

module.exports = router;