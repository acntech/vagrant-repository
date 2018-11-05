// Modules
const express = require('express');
// Variables
const router = express.Router();
const boxes = require('../data/boxes.json');
const versions = require('../data/versions.json');

router.get('/:id', (req, res) => {
    const id = req.params.id;

    if (!id) {
        const error = createError(404, 'Not Found', 'Box ID not set', '/api/boxes/:id');
        res.status(404).send(error);
        return;
    }

    const entity = boxes.find(e => e.id == id);

    if (entity) {
        res.send(entity);
    } else {
        res.status(204).send();
    }
});

router.get('/:id/versions', (req, res) => {
    const id = req.params.id;
    const name = req.query.name;

    if (!id) {
        const error = createError(404, 'Not Found', 'Box ID not set', '/api/boxes/:id/versions');
        res.status(404).send(error);
        return;
    }

    if (name) {
        const entities = versions.filter(e => e.box.id == id && e.name === name);
        res.send(entities);
    } else {
        const entities = versions.filter(e => e.box.id == id);
        res.send(entities);
    }
});

router.post('/:id/versions', (req, res) => {
    const { params, body } = req;
    const { id } = params;

    if (!id) {
        const error = createError(404, 'Not Found', 'Box ID not set', '/api/boxes/:id/versions');
        res.status(404).send(error);
        return;
    }

    if (!body || !body.name) {
        const error = createError(400, 'Bad Request', 'Request body is malformed', '/api/boxes/:id/versions');
        res.status(400).send(error);
        return;
    }

    const entity = boxes.find(e => e.id == id);

    if (!entity) {
        const error = createError(400, 'Bad Request', `Box with ID ${id} not found`, '/api/boxes/:id/versions');
        res.status(400).send(error);
        return;
    }

    const entities = versions.filter(e => e.name === body.name && e.box.id == id);

    if (entities && entities.length > 0) {
        const error = createError(
            409,
            'Conflict',
            `Version with name ${body.name} already exists for box ${entity.name}`,
            '/api/boxes/:id/versions');
        res.status(409).send(error);
        return;
    }

    const versionId = versions.length + 1;
    const version = { ...body, id: versionId, box: entity };
    versions.push(version);

    res.send(version);
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