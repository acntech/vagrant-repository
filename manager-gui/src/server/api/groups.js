// Modules
const express = require('express');
// Variables
const router = express.Router();
const groups = require('../data/groups.json');
const boxes = require('../data/boxes.json');

router.get('/', (req, res) => {
    const name = req.query.name;
    if (name) {
        const entities = groups.filter(e => e.name === name);
        res.send(entities);
    } else {
        res.send(groups);
    }
});

router.get('/:id', (req, res) => {
    const id = req.params.id;
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

router.get('/:id/boxes', (req, res) => {
    const id = req.params.id;
    const name = req.query.name;

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