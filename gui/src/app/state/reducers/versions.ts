import {
    Version,
    VersionState,
    VersionAction,
    CreateVersionAction,
    CreateVersionActionType,
    DeleteVersionAction,
    DeleteVersionActionType,
    GetVersionAction,
    GetVersionActionType,
    FindVersionsAction,
    FindVersionsActionType,
    EntityType,
    ActionType
} from '../../models';
import {initialVersionState} from '../store/initial-state';

export function reducer(state: VersionState = initialVersionState, action: VersionAction): VersionState {
    switch (action.type) {
        case CreateVersionActionType.LOADING:
        case CreateVersionActionType.SUCCESS:
        case CreateVersionActionType.ERROR:
            return createVersion(state, action);
        case DeleteVersionActionType.LOADING:
        case DeleteVersionActionType.SUCCESS:
        case DeleteVersionActionType.ERROR:
            return deleteVersion(state, action);
        case FindVersionsActionType.LOADING:
        case FindVersionsActionType.SUCCESS:
        case FindVersionsActionType.ERROR:
            return findVersions(state, action);
        case GetVersionActionType.LOADING:
        case GetVersionActionType.SUCCESS:
        case GetVersionActionType.ERROR:
            return getVersion(state, action);
        default:
            return state;
    }
}

const createVersion = (state: VersionState = initialVersionState, action: CreateVersionAction): VersionState => {
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

const deleteVersion = (state: VersionState = initialVersionState, action: DeleteVersionAction): VersionState => {
    switch (action.type) {
        case DeleteVersionActionType.LOADING: {
            const { versions } = state;
            const { loading } = action;
            return { ...initialVersionState, versions: versions, loading: loading };
        }

        case DeleteVersionActionType.SUCCESS: {
            let { versions } = state;
            const { versionId } = action;
            let modified = { id: versionId, entityType: EntityType.VERSION, actionType: ActionType.DELETE };
            versions = versions.filter(version => version.id != versionId);

            return { ...initialVersionState, versions: versions, modified: modified };
        }

        case DeleteVersionActionType.ERROR: {
            const { versions } = state;
            const { data } = action.error.response;
            const error = { ...data, entityType: EntityType.VERSION, actionType: ActionType.DELETE };
            return { ...initialVersionState, versions: versions, error: error };
        }

        default: {
            return state;
        }
    }
};

const findVersions = (state: VersionState = initialVersionState, action: FindVersionsAction): VersionState => {
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
};

const getVersion = (state: VersionState = initialVersionState, action: GetVersionAction): VersionState => {
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

const replaceOrAppend = (versions: Version[], version: Version) => {
    const index = versions.map(version => version.id).indexOf(version.id);

    if (~index) {
        versions[index] = version;
    } else {
        versions = versions.concat(version);
    }

    return versions;
};