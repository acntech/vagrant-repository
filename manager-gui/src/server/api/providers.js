// Modules
const express = require('express');
// Variables
const router = express.Router();
const providers = require('../data/providers.json');

router.get('/:id', (req, res) => {
    const id = req.params.id;

    if (!id) {
        res.status(400).send();
    }

    const entity = providers.find(e => e.id === id);

    if (entity) {
        res.send(entity);
    } else {
        res.status(204).send();
    }
});

module.exports = router;