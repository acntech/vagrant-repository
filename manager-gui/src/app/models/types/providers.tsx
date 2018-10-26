import { ChecksumType, GetProviderActionType, FindProvidersActionType, ProviderType } from '../constants';
import { Error } from './globals';
import { Version } from '../';

export interface Provider {
    id: number;
    providerType: ProviderType;
    size: number;
    checksumType: ChecksumType;
    checksum: string;
    version: Version;
}

export interface ProviderState {
    loading: boolean;
    providers: Provider[];
    error?: any;
}

export const initialProviderState: ProviderState = {
    loading: false,
    providers: []
};

export interface GetProviderLoadingAction {
    type: GetProviderActionType.LOADING,
    loading: boolean
}

export interface GetProviderSuccessAction {
    type: GetProviderActionType.SUCCESS,
    payload: Provider
}

export interface GetProviderErrorAction {
    type: GetProviderActionType.ERROR,
    error: Error
}

export interface FindProvidersLoadingAction {
    type: FindProvidersActionType.LOADING,
    loading: boolean
}

export interface FindProvidersSuccessAction {
    type: FindProvidersActionType.SUCCESS,
    payload: Provider[]
}

export interface FindProvidersErrorAction {
    type: FindProvidersActionType.ERROR,
    error: Error
}

export type GetProviderAction = GetProviderLoadingAction | GetProviderSuccessAction | GetProviderErrorAction;
export type FindProvidersAction = FindProvidersLoadingAction | FindProvidersSuccessAction | FindProvidersErrorAction;

export type ProviderAction = GetProviderAction | FindProvidersAction;