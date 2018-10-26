import axios from 'axios';

import {
    Provider,
    GetProviderActionType,
    GetProviderErrorAction,
    GetProviderLoadingAction,
    GetProviderSuccessAction,
    FindProvidersActionType,
    FindProvidersErrorAction,
    FindProvidersLoadingAction,
    FindProvidersSuccessAction,
} from '../../models';

const getProviderLoading = (loading: boolean): GetProviderLoadingAction => ({ type: GetProviderActionType.LOADING, loading });
const getProviderSuccess = (payload: Provider): GetProviderSuccessAction => ({ type: GetProviderActionType.SUCCESS, payload });
const getProviderError = (error: any): GetProviderErrorAction => ({ type: GetProviderActionType.ERROR, error });

const findProvidersLoading = (loading: boolean): FindProvidersLoadingAction => ({ type: FindProvidersActionType.LOADING, loading });
const findProvidersSuccess = (payload: Provider[]): FindProvidersSuccessAction => ({ type: FindProvidersActionType.SUCCESS, payload });
const findProvidersError = (error: any): FindProvidersErrorAction => ({ type: FindProvidersActionType.ERROR, error });

const providersRootPath = '/api/providers';
const versionsRootPath = '/api/versions';

export function getProvider(providerId: number) {
    return (dispatch) => {
        dispatch(getProviderLoading(true));
        const url = `${providersRootPath}/${providerId}`;
        return axios.get(url)
            .then((response) => {
                return dispatch(getProviderSuccess(response.data));
            })
            .catch((error) => {
                return dispatch(getProviderError(error));
            });
    };
}

export function findProviders(providerName?: string) {
    return (dispatch) => {
        dispatch(findProvidersLoading(true));
        const url = name ? `${providersRootPath}?name=${providerName}` : providersRootPath;
        return axios.get(url)
            .then((response) => {
                return dispatch(findProvidersSuccess(response.data));
            })
            .catch((error) => {
                return dispatch(findProvidersError(error));
            });
    };
}

export function findVersionProviders(versionId: number) {
    return (dispatch) => {
        dispatch(findProvidersLoading(true));
        const url = `${versionsRootPath}/${versionId}/providers`;
        return axios.get(url)
            .then((response) => {
                return dispatch(findProvidersSuccess(response.data));
            })
            .catch((error) => {
                return dispatch(findProvidersError(error));
            });
    };
}