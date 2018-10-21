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

    const { name } = body;

    const entities = groups.filter(e => e.name === name);

    if (entities && entities.length > 0) {
        res.status(409).send();
    } else {
        const id = groups.length + 1;
        const group = { ...body, id: id };
        groups.push(group);
        res.send(group);
    }
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

module.exports = router;