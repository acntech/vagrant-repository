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
    EntityType,
    ActionType,
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
            const { providers } = state;
            const { loading } = action;
            return { ...initialProviderState, providers: providers, loading: loading };
        }

        case CreateProviderActionType.SUCCESS: {
            let { providers } = state;
            const { payload } = action;
            let modified;

            if (payload) {
                providers = replaceOrAppend(providers, payload);
                modified = { id: payload.id, entityType: EntityType.PROVIDER, actionType: ActionType.CREATE };
            }

            return { ...initialProviderState, providers: providers, modified: modified };
        }

        case CreateProviderActionType.ERROR: {
            const { providers } = state;
            const { data } = action.error.response;
            const error = { ...data, entityType: EntityType.PROVIDER, actionType: ActionType.CREATE };
            return { ...initialProviderState, providers: providers, error: error };
        }

        default: {
            return state;
        }
    }
}

export const get = (state: ProviderState = initialProviderState, action: GetProviderAction): ProviderState => {
    switch (action.type) {
        case GetProviderActionType.LOADING: {
            const { providers } = state;
            const { loading } = action;
            return { ...initialProviderState, providers: providers, loading: loading };
        }

        case GetProviderActionType.SUCCESS: {
            let { providers } = state;
            const { payload } = action;

            if (payload) {
                providers = replaceOrAppend(providers, payload);
            }

            return { ...initialProviderState, providers: providers };
        }

        case GetProviderActionType.ERROR: {
            const { providers } = state;
            const { data } = action.error.response;
            const error = { ...data, entityType: EntityType.PROVIDER, actionType: ActionType.GET };
            return { ...initialProviderState, providers: providers, error: error };
        }

        default: {
            return state;
        }
    }
}

export function find(state: ProviderState = initialProviderState, action: FindProvidersAction): ProviderState {
    switch (action.type) {
        case FindProvidersActionType.LOADING: {
            const { providers } = state;
            const { loading } = action;
            return { ...initialProviderState, providers: providers, loading: loading };
        }

        case FindProvidersActionType.SUCCESS: {
            let { providers } = state;
            const { payload } = action;

            if (payload) {
                payload.forEach(provider => {
                    providers = replaceOrAppend(providers, provider);
                });
            }

            return { ...initialProviderState, providers: providers };
        }

        case FindProvidersActionType.ERROR: {
            const { providers } = state;
            const { data } = action.error.response;
            const error = { ...data, entityType: EntityType.PROVIDER, actionType: ActionType.GET };
            return { ...initialProviderState, providers: providers, error: error };
        }

        default: {
            return state;
        }
    }
}

export const update = (state: ProviderState = initialProviderState, action: UpdateProviderAction): ProviderState => {
    switch (action.type) {
        case UpdateProviderActionType.LOADING: {
            const { providers } = state;
            const { loading } = action;
            return { ...initialProviderState, providers: providers, loading: loading };
        }

        case UpdateProviderActionType.SUCCESS: {
            let { providers } = state;
            const { payload } = action;
            let modified;

            if (payload) {
                providers = replaceOrAppend(providers, payload);
                modified = { id: payload.id, entityType: EntityType.PROVIDER, actionType: ActionType.UPDATE };
            }

            return { ...initialProviderState, providers: providers, modified: modified };
        }

        case UpdateProviderActionType.ERROR: {
            const { providers } = state;
            const { data } = action.error.response;
            const error = { ...data, entityType: EntityType.PROVIDER, actionType: ActionType.UPDATE };
            return { ...initialProviderState, providers: providers, error: error };
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