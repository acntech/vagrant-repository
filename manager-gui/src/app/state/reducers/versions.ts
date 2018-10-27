import {
    VersionState,
    VersionAction,
    FindVersionsAction,
    FindVersionsActionType
} from '../../models';
import { initialVersionState } from '../store/initial-state';

export function reducer(state: VersionState = initialVersionState, action: VersionAction): VersionState {
    switch (action.type) {
        case FindVersionsActionType.LOADING:
        case FindVersionsActionType.SUCCESS:
        case FindVersionsActionType.ERROR:
            return find(state, action);
        default:
            return state;
    }
}

export function find(state: VersionState = initialVersionState, action: FindVersionsAction): VersionState {
    switch (action.type) {
        case FindVersionsActionType.LOADING: {
            const { loading } = action;
            return { ...state, loading: loading };
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
            return { ...initialVersionState, versions: payload };
        }

        case FindVersionsActionType.ERROR: {
            const { error } = action;
            return { ...initialVersionState, error: error };
        }

        default: {
            return state;
        }
    }
}