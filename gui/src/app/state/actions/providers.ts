import axios from 'axios';

import {
    CreateProvider,
    Provider,
    CreateProviderActionType,
    CreateProviderErrorAction,
    CreateProviderLoadingAction,
    CreateProviderSuccessAction,
    GetProviderActionType,
    GetProviderErrorAction,
    GetProviderLoadingAction,
    GetProviderSuccessAction,
    FindProvidersActionType,
    FindProvidersErrorAction,
    FindProvidersLoadingAction,
    FindProvidersSuccessAction,
    UpdateProviderActionType,
    UpdateProviderErrorAction,
    UpdateProviderLoadingAction,
    UpdateProviderSuccessAction,
} from '../../models';
import { showError, showSuccess } from '../actions';

const getProviderLoading = (loading: boolean): GetProviderLoadingAction => ({ type: GetProviderActionType.LOADING, loading });
const getProviderSuccess = (payload: Provider): GetProviderSuccessAction => ({ type: GetProviderActionType.SUCCESS, payload });
const getProviderError = (error: any): GetProviderErrorAction => ({ type: GetProviderActionType.ERROR, error });

const findProvidersLoading = (loading: boolean): FindProvidersLoadingAction => ({ type: FindProvidersActionType.LOADING, loading });
const findProvidersSuccess = (payload: Provider[]): FindProvidersSuccessAction => ({ type: FindProvidersActionType.SUCCESS, payload });
const findProvidersError = (error: any): FindProvidersErrorAction => ({ type: FindProvidersActionType.ERROR, error });

const createProviderLoading = (loading: boolean): CreateProviderLoadingAction => ({ type: CreateProviderActionType.LOADING, loading });
const createProviderSuccess = (payload: Provider): CreateProviderSuccessAction => ({ type: CreateProviderActionType.SUCCESS, payload });
const createProviderError = (error: any): CreateProviderErrorAction => ({ type: CreateProviderActionType.ERROR, error });

const updateProviderLoading = (loading: boolean): UpdateProviderLoadingAction => ({ type: UpdateProviderActionType.LOADING, loading });
const updateProviderSuccess = (payload: Provider): UpdateProviderSuccessAction => ({ type: UpdateProviderActionType.SUCCESS, payload });
const updateProviderError = (error: any): UpdateProviderErrorAction => ({ type: UpdateProviderActionType.ERROR, error });

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

export function createVersionProvider(versionId: number, provider: CreateProvider) {
    return (dispatch) => {
        dispatch(createProviderLoading(true));
        const url = `${versionsRootPath}/${versionId}/providers`;
        return axios.post(url, provider)
            .then((response) => {
                dispatch(showSuccess('Provider created successfully'));
                return dispatch(createProviderSuccess(response.data));
            })
            .catch((error) => {
                const { message } = error.response.data;
                dispatch(showError('Error creating provider', message));
                return dispatch(createProviderError(error));
            });
    };
}

export function updateVersionProvider(providerId: number, file: any) {
    return (dispatch) => {
        dispatch(updateProviderLoading(true));
        const url = `${providersRootPath}/${providerId}`;
        const formData = new FormData();
        formData.append('file', file);
        const config = {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        };
        return axios.post(url, formData, config)
            .then((response) => {
                dispatch(showSuccess('Provider updated successfully'));
                console.log(response);
                return dispatch(updateProviderSuccess(response.data));
            })
            .catch((error) => {
                const { message } = error.response.data;
                dispatch(showError('Error updating provider', message));
                return dispatch(updateProviderError(error));
            });
    };
}