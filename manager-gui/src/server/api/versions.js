// Modules
const express = require('express');
// Variables
const router = express.Router();
const versions = require('../data/versions.json');
const providers = require('../data/providers.json');

router.get('/:id', (req, res) => {
    const id = req.params.id;

    if (!id) {
        res.status(404).send();
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
        res.status(404).send();
    }

    if (name) {
        const entities = providers.filter(e => e.version.id == id && e.name === name);
        res.send(entities);
    } else {
        const entities = providers.filter(e => e.version.id == id);
        res.send(entities);
    }
});

module.exports = router;