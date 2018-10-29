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
        const error = createError(404, 'Not Found', 'Group ID not set', '/api/groups/:id');
        res.status(404).send(error);
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
        const error = createError(400, 'Bad Request', 'Request body is malformed', '/api/groups');
        res.status(400).send(error);
    }

    const entities = groups.filter(e => e.name === body.name);

    if (entities && entities.length > 0) {
        const error = createError(
            409,
            'Conflict',
            `Group with name ${body.name} already exists for group`,
            '/api/groups');
        res.status(409).send(error);
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
        const error = createError(404, 'Not Found', 'Group ID not set', '/api/groups/:id/boxes');
        res.status(404).send(error);
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
        const error = createError(404, 'Not Found', 'Group ID not set', '/api/groups/:id/boxes');
        res.status(404).send(error);
    }

    if (!body || !body.name) {
        const error = createError(400, 'Bad Request', 'Request body is malformed', '/api/groups/:id/boxes');
        res.status(400).send(error);
    }

    const entity = groups.find(e => e.id == id);

    if (!entity) {
        const error = createError(400, 'Bad Request', `Group with ID ${id} not found`, '/api/groups/:id/boxes');
        res.status(400).send(error);
    }

    const entities = boxes.filter(e => e.name === body.name);

    if (entities && entities.length > 0) {
        const error = createError(
            409,
            'Conflict',
            `Box with name ${body.name} already exists for group`,
            '/api/groups/:id/boxes');
        res.status(409).send(error);
    }

    const boxId = boxes.length + 1;
    const box = { ...body, id: boxId, group: entity };
    boxes.push(box);

    res.send(boxes);
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