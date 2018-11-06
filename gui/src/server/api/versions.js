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
        return;
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
    const providerType = req.query.providerType;

    if (!id) {
        const error = createError(404, 'Not Found', 'Version ID not set', '/api/versions/:id/providers');
        res.status(404).send(error);
        return;
    }

    if (providerType) {
        const entities = providers.filter(e => e.version.id == id && e.providerType === providerType);
        res.send(entities);
    } else {
        const entities = providers.filter(e => e.version.id == id);
        res.send(entities);
    }
});

router.post('/:id/providers', (req, res) => {
    const { params, body } = req;
    const { id } = params;

    if (!id) {
        const error = createError(404, 'Not Found', 'Version ID not set', '/api/versions/:id/providers');
        res.status(404).send(error);
        return;
    }

    if (!body || !body.providerType) {
        const error = createError(400, 'Bad Request', 'Request body is malformed', '/api/versions/:id/providers');
        res.status(400).send(error);
        return;
    }

    const entity = versions.find(e => e.id == id);

    if (!entity) {
        const error = createError(400, 'Bad Request', `Version with ID ${id} not found`, '/api/versions/:id/providers');
        res.status(400).send(error);
        return;
    }

    const entities = providers.filter(e => e.providerType === body.providerType && e.version.id == id);

    if (entities && entities.length > 0) {
        const error = createError(
            409,
            'Conflict',
            `Provider with type ${body.providerType} already exists for version ${entity.name}`,
            '/api/versions/:id/providers');
        res.status(409).send(error);
        return;
    }

    const providerId = providers.length + 1;
    const provider = { ...body, id: providerId, version: entity };
    providers.push(provider);

    res.send(provider);
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