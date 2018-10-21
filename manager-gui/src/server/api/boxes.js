// Modules
const express = require('express');
// Variables
const router = express.Router();
const boxes = require('../data/boxes.json');
const versions = require('../data/versions.json');

router.get('/:id', (req, res) => {
    const id = req.params.id;
    if (!id) {
        res.status(404).send();
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
        res.status(404).send();
    }

    if (name) {
        const entities = versions.filter(e => e.box.id == id && e.name === name);
        res.send(entities);
    } else {
        const entities = versions.filter(e => e.box.id == id);
        res.send(entities);
    }
});

module.exports = router;