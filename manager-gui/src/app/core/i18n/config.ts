import { translation } from './en/common';

export const i18nConfig = {
    "interpolation": {
        "escapeValue": false // not needed for react
    },
    "lng": "en",
    "resources": {
        "en": {
            "translation": translation
        }
    }
};