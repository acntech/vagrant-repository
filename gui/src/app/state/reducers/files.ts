import {
    File,
    FileState,
    FindFilesAction,
    FindFilesActionType,
    EntityType,
    ActionType,
} from '../../models';
import { initialFileState } from '../store/initial-state';

export function reducer(state: FileState = initialFileState, action: FindFilesAction): FileState {
    switch (action.type) {
        case FindFilesActionType.LOADING:
        case FindFilesActionType.SUCCESS:
        case FindFilesActionType.ERROR:
            return findFiles(state, action);
        default:
            return state;
    }
}

const findFiles = (state: FileState = initialFileState, action: FindFilesAction): FileState => {
    switch (action.type) {
        case FindFilesActionType.LOADING: {
            const { files } = state;
            const { loading } = action;
            return { ...initialFileState, files: files, loading: loading };
        }

        case FindFilesActionType.SUCCESS: {
            let { files } = state;
            const { payload } = action;

            if (payload) {
                payload.forEach(file => {
                    files = replaceOrAppend(files, file);
                });
            }

            return { ...initialFileState, files: files };
        }

        case FindFilesActionType.ERROR: {
            const { files } = state;
            const { data } = action.error.response;
            const error = { ...data, entityType: EntityType.PROVIDER, actionType: ActionType.FIND };
            return { ...initialFileState, files: files, error: error };
        }

        default: {
            return state;
        }
    }
};

const replaceOrAppend = (files: File[], file: File) => {
    const index = files.map(f => f.providerId).indexOf(file.providerId);

    if (~index) {
        files[index] = file;
    } else {
        files = files.concat(file);
    }

    return files;
};