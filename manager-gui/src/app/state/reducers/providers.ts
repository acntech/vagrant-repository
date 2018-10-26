import {
    ProviderState,
    ProviderAction,
    FindProvidersAction,
    FindProvidersActionType,
    initialProviderState
} from '../../models';

export function reducer(state: ProviderState = initialProviderState, action: ProviderAction): ProviderState {
    switch (action.type) {
        case FindProvidersActionType.LOADING:
        case FindProvidersActionType.SUCCESS:
        case FindProvidersActionType.ERROR:
            return find(state, action);
        default:
            return state;
    }
}

export function find(state: ProviderState = initialProviderState, action: FindProvidersAction): ProviderState {
    switch (action.type) {
        case FindProvidersActionType.LOADING: {
            const { loading } = action;
            return { ...state, loading: loading };
        }

        case FindProvidersActionType.SUCCESS: {
            const { payload } = action;
            let { providers } = state;
            if (payload) {
                payload.forEach(box => {
                    let index = providers.indexOf(box);
                    if (~index) {
                        providers[index] = box;
                    } else {
                        providers = providers.concat(payload);
                    }
                });
            }
            return { ...initialProviderState, providers: payload };
        }

        case FindProvidersActionType.ERROR: {
            const { error } = action;
            return { ...initialProviderState, error: error };
        }

        default: {
            return state;
        }
    }
}