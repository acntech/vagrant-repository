import {
    Version,
    VersionState,
    VersionAction,
    CreateVersionAction,
    CreateVersionActionType,
    GetVersionAction,
    GetVersionActionType,
    FindVersionsAction,
    FindVersionsActionType
} from '../../models';
import { initialVersionState } from '../store/initial-state';

export function reducer(state: VersionState = initialVersionState, action: VersionAction): VersionState {
    switch (action.type) {
        case CreateVersionActionType.LOADING:
        case CreateVersionActionType.SUCCESS:
        case CreateVersionActionType.ERROR:
            return create(state, action);
        case GetVersionActionType.LOADING:
        case GetVersionActionType.SUCCESS:
        case GetVersionActionType.ERROR:
            return get(state, action);
        case FindVersionsActionType.LOADING:
        case FindVersionsActionType.SUCCESS:
        case FindVersionsActionType.ERROR:
            return find(state, action);
        default:
            return state;
    }
}

export const create = (state: VersionState = initialVersionState, action: CreateVersionAction): VersionState => {
    switch (action.type) {
        case CreateVersionActionType.LOADING: {
            const { loading } = action;
            return { ...state, loading: loading, modified: undefined };
        }

        case CreateVersionActionType.SUCCESS: {
            const { payload } = action;
            let { versions } = state;
            let modified;

            if (payload) {
                versions = replaceOrAppend(versions, payload);
                modified = { id: payload.id };
            }

            return { ...initialVersionState, versions: versions, modified: modified };
        }

        case CreateVersionActionType.ERROR: {
            const { error } = action;
            return { ...initialVersionState, error: error, modified: undefined };
        }

        default: {
            return state;
        }
    }
}

export const get = (state: VersionState = initialVersionState, action: GetVersionAction): VersionState => {
    switch (action.type) {
        case GetVersionActionType.LOADING: {
            const { loading } = action;
            return { ...state, loading: loading, modified: undefined };
        }

        case GetVersionActionType.SUCCESS: {
            const { payload } = action;
            let { versions } = state;

            if (payload) {
                versions = replaceOrAppend(versions, payload);
            }

            return { ...initialVersionState, versions: versions, modified: undefined };
        }

        case GetVersionActionType.ERROR: {
            const { error } = action;
            return { ...initialVersionState, error: error, modified: undefined };
        }

        default: {
            return state;
        }
    }
}

export function find(state: VersionState = initialVersionState, action: FindVersionsAction): VersionState {
    switch (action.type) {
        case FindVersionsActionType.LOADING: {
            const { loading } = action;
            return { ...state, loading: loading, modified: undefined };
        }

        case FindVersionsActionType.SUCCESS: {
            const { payload } = action;
            let { versions } = state;
            if (payload) {
                payload.forEach(box => {
                    let index = versions.indexOf(box);
                    if (~index) {
                        versions[index] = box;
                    } else {
                        versions = versions.concat(payload);
                    }
                });
            }
            return { ...initialVersionState, versions: payload, modified: undefined };
        }

        case FindVersionsActionType.ERROR: {
            const { error } = action;
            return { ...initialVersionState, error: error, modified: undefined };
        }

        default: {
            return state;
        }
    }
}

const replaceOrAppend = (versions: Version[], version: Version) => {
    const index = versions.map(box => box.id).indexOf(version.id);

    if (~index) {
        versions[index] = version;
    } else {
        versions = versions.concat(version);
    }

    return versions;
}