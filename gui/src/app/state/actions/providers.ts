import axios from 'axios';

import { globalConfig } from '../../core/config';
import {
    Provider,
    ModifyProvider,
    CreateProviderActionType,
    CreateProviderErrorAction,
    CreateProviderLoadingAction,
    CreateProviderSuccessAction,
    DeleteProviderActionType,
    DeleteProviderErrorAction,
    DeleteProviderLoadingAction,
    DeleteProviderSuccessAction,
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
import { showError, showSuccess } from '.';

const { timeout: successTimeout } = globalConfig.notifications.success;
const { timeout: errorTimeout } = globalConfig.notifications.error;

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

const deleteProviderLoading = (loading: boolean): DeleteProviderLoadingAction => ({ type: DeleteProviderActionType.LOADING, loading });
const deleteProviderSuccess = (providerId: number): DeleteProviderSuccessAction => ({ type: DeleteProviderActionType.SUCCESS, providerId });
const deleteProviderError = (error: any): DeleteProviderErrorAction => ({ type: DeleteProviderActionType.ERROR, error });

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

export function createVersionProvider(versionId: number, provider: ModifyProvider) {
    return (dispatch) => {
        dispatch(createProviderLoading(true));
        const url = `${versionsRootPath}/${versionId}/providers`;
        return axios.post(url, provider)
            .then((response) => {
                const notification = { title: 'Provider created successfully', timeout: successTimeout };
                dispatch(showSuccess(notification));
                return dispatch(createProviderSuccess(response.data));
            })
            .catch((error) => {
                const { message } = error.response.data;
                const notification = { title: 'Error creating provider', content: message, timeout: errorTimeout };
                dispatch(showError(notification));
                return dispatch(createProviderError(error));
            });
    };
}

export function updateProvider(providerId: number, file: any) {
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
                const notification = { title: 'Provider updated successfully', timeout: successTimeout };
                dispatch(showSuccess(notification));
                console.log(response);
                return dispatch(updateProviderSuccess(response.data));
            })
            .catch((error) => {
                const { message } = error.response.data;
                const notification = { title: 'Error updating provider', content: message, timeout: errorTimeout };
                dispatch(showError(notification));
                return dispatch(updateProviderError(error));
            });
    };
}

export function deleteProvider(providerId: number) {
    return (dispatch) => {
        dispatch(deleteProviderLoading(true));
        const url = `${providersRootPath}/${providerId}`;
        return axios.delete(url)
            .then(() => {
                const notification = { title: 'Provider deleted successfully', timeout: successTimeout };
                dispatch(showSuccess(notification));
                return dispatch(deleteProviderSuccess(providerId));
            })
            .catch((error) => {
                const { message } = error.response.data;
                const notification = { title: 'Error deleting provider', content: message, timeout: errorTimeout };
                dispatch(showError(notification));
                return dispatch(deleteProviderError(error));
            });
    };
}
