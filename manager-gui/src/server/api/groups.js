// Modules
const express = require('express');
// Variables
const router = express.Router();
const groups = require('../data/groups.json');
const boxes = require('../data/boxes.json');

router.get('/', (req, res) => {
    const { query } = req;
    const { name } = query;

    if (name) {
        const entities = groups.filter(e => e.name === name);
        res.send(entities);
    } else {
        res.send(groups);
    }
});

router.get('/:id', (req, res) => {
    const { params } = req;
    const { id } = params;

    if (!id) {
        res.status(404).send();
    }

    const entity = groups.find(e => e.id == id);

    if (entity) {
        res.send(entity);
    } else {
        res.status(204).send();
    }
});

router.post('/', (req, res) => {
    const { body } = req;

    if (!body || !body.name) {
        res.status(400).send();
    }

    const entities = groups.filter(e => e.name === body.name);

    if (entities && entities.length > 0) {
        res.status(409).send();
    }

    const groupId = groups.length + 1;
    const group = { ...body, id: groupId };
    groups.push(group);
    res.send(group);
});

router.get('/:id/boxes', (req, res) => {
    const { params, query } = req;
    const { id } = params;
    const { name } = query;

    if (!id) {
        res.status(404).send();
    }

    if (name) {
        const entities = boxes.filter(e => e.group.id == id && e.name === name);
        res.send(entities);
    } else {
        const entities = boxes.filter(e => e.group.id == id);
        res.send(entities);
    }
});

router.post('/:id/boxes', (req, res) => {
    const { params, body } = req;
    const { id } = params;

    if (!id) {
        const error = createError(404, 'Not Found', 'Group ID not set', '/api/:id/boxes');
        res.status(404).send(error);
        return;
    }

    if (!body || !body.name) {
        const error = createError(400, 'Bad Request', 'Request body malformed', '/api/:id/boxes');
        res.status(400).send(error);
        return;
    }

    const entity = groups.find(e => e.id == id);

    if (!entity) {
        const error = createError(400, 'Bad Request', `Group with ID ${id} not found`, '/api/:id/boxes');
        res.status(400).send(error);
        return;
    }

    const entities = boxes.filter(e => e.name === body.name);

    if (entities && entities.length > 0) {
        const error = createError(
            409,
            'Conflict',
            'Box with name body.name already exists for group',
            '/api/:id/boxes');
        res.status(409).send();
        return;
    }

    const boxId = groups.length + 1;
    const group = { ...body, id: boxId, group: entity };
    groups.push(group);

    res.send(group);
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