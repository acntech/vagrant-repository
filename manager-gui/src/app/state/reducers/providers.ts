import {
    Provider,
    ProviderState,
    ProviderAction,
    CreateProviderAction,
    CreateProviderActionType,
    GetProviderAction,
    GetProviderActionType,
    FindProvidersAction,
    FindProvidersActionType
} from '../../models';
import { initialProviderState } from '../store/initial-state';

export function reducer(state: ProviderState = initialProviderState, action: ProviderAction): ProviderState {
    switch (action.type) {
        case CreateProviderActionType.LOADING:
        case CreateProviderActionType.SUCCESS:
        case CreateProviderActionType.ERROR:
            return create(state, action);
        case GetProviderActionType.LOADING:
        case GetProviderActionType.SUCCESS:
        case GetProviderActionType.ERROR:
            return get(state, action);
        case FindProvidersActionType.LOADING:
        case FindProvidersActionType.SUCCESS:
        case FindProvidersActionType.ERROR:
            return find(state, action);
        default:
            return state;
    }
}

export const create = (state: ProviderState = initialProviderState, action: CreateProviderAction): ProviderState => {
    switch (action.type) {
        case CreateProviderActionType.LOADING: {
            const { loading } = action;
            return { ...state, loading: loading, modified: undefined };
        }

        case CreateProviderActionType.SUCCESS: {
            const { payload } = action;
            let { providers } = state;
            let modified;

            if (payload) {
                providers = replaceOrAppend(providers, payload);
                modified = { id: payload.id };
            }

            return { ...initialProviderState, providers: providers, modified: modified };
        }

        case CreateProviderActionType.ERROR: {
            const { error } = action;
            return { ...initialProviderState, error: error, modified: undefined };
        }

        default: {
            return state;
        }
    }
}

export const get = (state: ProviderState = initialProviderState, action: GetProviderAction): ProviderState => {
    switch (action.type) {
        case GetProviderActionType.LOADING: {
            const { loading } = action;
            return { ...state, loading: loading, modified: undefined };
        }

        case GetProviderActionType.SUCCESS: {
            const { payload } = action;
            let { providers } = state;

            if (payload) {
                providers = replaceOrAppend(providers, payload);
            }

            return { ...initialProviderState, providers: providers, modified: undefined };
        }

        case GetProviderActionType.ERROR: {
            const { error } = action;
            return { ...initialProviderState, error: error, modified: undefined };
        }

        default: {
            return state;
        }
    }
}

export function find(state: ProviderState = initialProviderState, action: FindProvidersAction): ProviderState {
    switch (action.type) {
        case FindProvidersActionType.LOADING: {
            const { loading } = action;
            return { ...state, loading: loading, modified: undefined };
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
            return { ...initialProviderState, providers: payload, modified: undefined };
        }

        case FindProvidersActionType.ERROR: {
            const { error } = action;
            return { ...initialProviderState, error: error, modified: undefined };
        }

        default: {
            return state;
        }
    }
}

const replaceOrAppend = (providers: Provider[], provider: Provider) => {
    const index = providers.map(box => box.id).indexOf(provider.id);

    if (~index) {
        providers[index] = provider;
    } else {
        providers = providers.concat(provider);
    }

    return providers;
}