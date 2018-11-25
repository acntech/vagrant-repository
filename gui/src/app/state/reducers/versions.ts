import {
    Version,
    VersionState,
    VersionAction,
    CreateVersionAction,
    CreateVersionActionType,
    GetVersionAction,
    GetVersionActionType,
    FindVersionsAction,
    FindVersionsActionType,
    EntityType,
    ActionType
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

const create = (state: VersionState = initialVersionState, action: CreateVersionAction): VersionState => {
    switch (action.type) {
        case CreateVersionActionType.LOADING: {
            const { versions } = state;
            const { loading } = action;
            return { ...initialVersionState, versions: versions, loading: loading };
        }

        case CreateVersionActionType.SUCCESS: {
            let { versions } = state;
            const { payload } = action;
            let modified;

            if (payload) {
                versions = replaceOrAppend(versions, payload);
                modified = { id: payload.id, entityType: EntityType.VERSION, actionType: ActionType.CREATE };
            }

            return { ...initialVersionState, versions: versions, modified: modified };
        }

        case CreateVersionActionType.ERROR: {
            const { versions } = state;
            const { data } = action.error.response;
            const error = { ...data, entityType: EntityType.VERSION, actionType: ActionType.CREATE };
            return { ...initialVersionState, versions: versions, error: error };
        }

        default: {
            return state;
        }
    }
};

const get = (state: VersionState = initialVersionState, action: GetVersionAction): VersionState => {
    switch (action.type) {
        case GetVersionActionType.LOADING: {
            const { versions } = state;
            const { loading } = action;
            return { ...initialVersionState, versions: versions, loading: loading };
        }

        case GetVersionActionType.SUCCESS: {
            let { versions } = state;
            const { payload } = action;

            if (payload) {
                versions = replaceOrAppend(versions, payload);
            }

            return { ...initialVersionState, versions: versions };
        }

        case GetVersionActionType.ERROR: {
            const { versions } = state;
            const { data } = action.error.response;
            const error = { ...data, entityType: EntityType.VERSION, actionType: ActionType.GET };
            return { ...initialVersionState, versions: versions, error: error };
        }

        default: {
            return state;
        }
    }
};

function find(state: VersionState = initialVersionState, action: FindVersionsAction): VersionState {
    switch (action.type) {
        case FindVersionsActionType.LOADING: {
            const { versions } = state;
            const { loading } = action;
            return { ...initialVersionState, versions: versions, loading: loading };
        }

        case FindVersionsActionType.SUCCESS: {
            let { versions } = state;
            const { payload } = action;
            if (payload) {
                payload.forEach(version => {
                    versions = replaceOrAppend(versions, version);
                });
            }
            return { ...initialVersionState, versions: versions };
        }

        case FindVersionsActionType.ERROR: {
            const { versions } = state;
            const { data } = action.error.response;
            const error = { ...data, entityType: EntityType.VERSION, actionType: ActionType.FIND };
            return { ...initialVersionState, versions: versions, error: error };
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
};