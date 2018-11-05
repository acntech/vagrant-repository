import {
    Provider,
    ProviderState,
    ProviderAction,
    CreateProviderAction,
    CreateProviderActionType,
    GetProviderAction,
    GetProviderActionType,
    FindProvidersAction,
    FindProvidersActionType,
    UpdateProviderAction,
    UpdateProviderActionType,
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
        case UpdateProviderActionType.LOADING:
        case UpdateProviderActionType.SUCCESS:
        case UpdateProviderActionType.ERROR:
            return update(state, action);
        default:
            return state;
    }
}

export const create = (state: ProviderState = initialProviderState, action: CreateProviderAction): ProviderState => {
    switch (action.type) {
        case CreateProviderActionType.LOADING: {
            const { loading } = action;
            const { providers } = state;
            return { ...initialProviderState, loading: loading, providers: providers };
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
            const { providers } = state;
            return { ...initialProviderState, error: error, providers: providers };
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
            const { providers } = state;
            return { ...initialProviderState, loading: loading, providers: providers };
        }

        case GetProviderActionType.SUCCESS: {
            const { payload } = action;
            let { providers } = state;

            if (payload) {
                providers = replaceOrAppend(providers, payload);
            }

            return { ...initialProviderState, providers: providers };
        }

        case GetProviderActionType.ERROR: {
            const { error } = action;
            const { providers } = state;
            return { ...initialProviderState, error: error, providers: providers };
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
            const { providers } = state;
            return { ...initialProviderState, loading: loading, providers: providers };
        }

        case FindProvidersActionType.SUCCESS: {
            const { payload } = action;
            let { providers } = state;
            if (payload) {
                payload.forEach(provider => {
                    providers = replaceOrAppend(providers, provider);
                });
            }
            return { ...initialProviderState, providers: providers };
        }

        case FindProvidersActionType.ERROR: {
            const { error } = action;
            const { providers } = state;
            return { ...initialProviderState, error: error, providers: providers };
        }

        default: {
            return state;
        }
    }
}

export const update = (state: ProviderState = initialProviderState, action: UpdateProviderAction): ProviderState => {
    switch (action.type) {
        case UpdateProviderActionType.LOADING: {
            const { loading } = action;
            const { providers } = state;
            return { ...initialProviderState, loading: loading, providers: providers };
        }

        case UpdateProviderActionType.SUCCESS: {
            const { payload } = action;
            let { providers } = state;
            let modified;

            if (payload) {
                providers = replaceOrAppend(providers, payload);
                modified = { id: payload.id };
            }

            return { ...initialProviderState, providers: providers, modified: modified };
        }

        case UpdateProviderActionType.ERROR: {
            const { error } = action;
            const { providers } = state;
            return { ...initialProviderState, error: error, providers: providers };
        }

        default: {
            return state;
        }
    }
}

const replaceOrAppend = (providers: Provider[], provider: Provider) => {
    const index = providers.map(p => p.id).indexOf(provider.id);

    if (~index) {
        providers[index] = provider;
    } else {
        providers = providers.concat(provider);
    }

    return providers;
}