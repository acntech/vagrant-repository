// Modules
const fs = require('fs');
const express = require('express');
const crypto = require('crypto')
const multer = require('multer');
// Variables
const router = express.Router();
const providers = require('../data/providers.json');
const rootDir = '/tmp/vagrant-repository-manager';
const uploadDir = `${rootDir}/uploads`;

const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        const dirs = [rootDir, uploadDir];
        dirs.forEach(dir => {
            if (!fs.existsSync(dir)) {
                fs.mkdirSync(dir);
            }
        });
        cb(null, uploadDir);
    },
    filename: function (req, file, cb) {
        cb(null, 'default.box');
    }
})

const upload = multer({ storage: storage });

router.get('/:id', (req, res) => {
    const id = req.params.id;

    if (!id) {
        const error = createError(404, 'Not Found', 'Provider ID not set', '/api/providers/:id');
        res.status(400).send(error);
        return;
    }

    const entity = providers.find(e => e.id == id);

    if (entity) {
        res.send(entity);
    } else {
        res.status(204).send();
    }
});

router.post('/:id', upload.single('file'), (req, res) => {
    const { params, file } = req;
    const { id } = params;

    if (!id) {
        const error = createError(404, 'Not Found', 'Provider ID not set', '/api/providers/:id');
        res.status(404).send(error);
        return;
    }

    if (!file) {
        const error = createError(400, 'Bad Request', 'Request file not set', '/api/providers/:id');
        res.status(400).send(error);
        return;
    }

    const entity = providers.find(e => e.id == id);

    if (!entity) {
        const error = createError(400, 'Bad Request', `Provider with ID ${id} not found`, '/api/providers/:id');
        res.status(400).send(error);
        return;
    }

    const index = providers.indexOf(entity);
    if (~index) {
        const { size, path } = file;
        const data = fs.readFileSync(path, { encoding: 'utf8' });
        const checksum = crypto
            .createHash('sha1')
            .update(data, 'utf8')
            .digest('hex');
        const provider = { ...entity, size: size, checksumType: 'SHA1', checksum: checksum };
        providers[index] = provider;
        res.send(provider);
    } else {
        const error = createError(400, 'Server Error', `Error occured while saving file for provider with ID ${id}`, '/api/providers/:id');
        res.status(500).send(error);
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