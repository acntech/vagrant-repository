// Modules
const express = require('express');
// Variables
const router = express.Router();
const providers = require('../data/providers.json');

router.get('/:id', (req, res) => {
    const id = req.params.id;

    if (!id) {
        const error = createError(404, 'Not Found', 'Provider ID not set', '/api/providers/:id');
        res.status(400).send(error);
        return;
    }

    const entity = providers.find(e => e.id === id);

    if (entity) {
        res.send(entity);
    } else {
        res.status(204).send();
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