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
            const { versions } = state;
            return { ...initialVersionState, loading: loading, versions: versions };
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
            const { versions } = state;
            return { ...initialVersionState, error: error, versions: versions };
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
            const { versions } = state;
            return { ...initialVersionState, loading: loading, versions: versions };
        }

        case GetVersionActionType.SUCCESS: {
            const { payload } = action;
            let { versions } = state;

            if (payload) {
                versions = replaceOrAppend(versions, payload);
            }

            return { ...initialVersionState, versions: versions };
        }

        case GetVersionActionType.ERROR: {
            const { error } = action;
            const { versions } = state;
            return { ...initialVersionState, error: error, versions: versions };
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
            const { versions } = state;
            return { ...initialVersionState, loading: loading, versions: versions };
        }

        case FindVersionsActionType.SUCCESS: {
            const { payload } = action;
            let { versions } = state;
            if (payload) {
                payload.forEach(version => {
                    versions = replaceOrAppend(versions, version);
                });
            }
            return { ...initialVersionState, versions: versions };
        }

        case FindVersionsActionType.ERROR: {
            const { error } = action;
            const { versions } = state;
            return { ...initialVersionState, error: error, versions: versions };
        }

        default: {
            return state;
        }
    }
}

const replaceOrAppend = (versions: Version[], version: Version) => {
    const index = versions.map(version => version.id).indexOf(version.id);

    if (~index) {
        versions[index] = version;
    } else {
        versions = versions.concat(version);
    }

    return versions;
}